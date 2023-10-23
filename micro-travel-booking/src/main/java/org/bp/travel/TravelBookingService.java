package org.bp.travel;

import static org.apache.camel.model.rest.RestParamType.body;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.bp.travel.model.BookTravelRequest;
import org.bp.travel.model.BookingInfo;
import org.bp.travel.model.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.bp.travel.exceptions.FlightException;
import org.bp.travel.exceptions.HotelException;
import org.bp.travel.model.ExceptionResponse;
import org.bp.travel.state.ProcessingEvent;
import org.bp.travel.state.ProcessingState;
import org.bp.travel.state.StateService;

@Component
public class TravelBookingService extends RouteBuilder {
	
	@Autowired
	BookingIdentifierService bookingIdentifierService;
	
	@Autowired
	PaymentService paymentService;
	
	@Autowired
	StateService flightStateService;
	
	@Autowired
	StateService hotelStateService;

	@Override
	public void configure() throws Exception {
		bookHotelExceptionHandlers();
		bookFlightExceptionHandlers();
		gateway();
		hotel();
		flight();
		payment();
	}
	
	private void payment() {
		from("kafka:BookingInfoTopic?brokers=localhost:9092").routeId("paymentBookingInfo")
			.log("fired paymentBookingInfo")
			.unmarshal().json(JsonLibrary.Jackson, BookingInfo.class)
			.process((exchange) -> {
				String bookingTravelId = exchange.getMessage().getHeader("bookingTravelId", String.class);
				boolean isReady = paymentService.addBookingInfo(
					bookingTravelId,
					exchange.getMessage().getBody(BookingInfo.class),
					exchange.getMessage().getHeader("serviceType", String.class)
				);
				exchange.getMessage().setHeader("isReady", isReady);
			})
			.choice()
			.when(header("isReady").isEqualTo(true)).to("direct:finalizePayment")
			.endChoice();
		
		from("kafka:TravelReqTopic?brokers=localhost:9092").routeId("paymentTravelReq")
			.log("fired paymentTravelReq")
			.unmarshal().json(JsonLibrary.Jackson, BookTravelRequest.class)
			.process((exchange) -> {
				String bookingTravelId = exchange.getMessage().getHeader("bookingTravelId", String.class);
				boolean isReady= paymentService.addBookTravelRequest(
					bookingTravelId,
					exchange.getMessage().getBody(BookTravelRequest.class)
				);
				exchange.getMessage().setHeader("isReady", isReady);
			})
			.choice()
			.when(header("isReady").isEqualTo(true)).to("direct:finalizePayment")
			.endChoice();
		
		from("direct:finalizePayment").routeId("finalizePayment")
			.log("fired finalizePayment")
			.process((exchange) -> {
				String bookingTravelId = exchange.getMessage().getHeader("bookingTravelId", String.class);
				PaymentService.PaymentData paymentData = paymentService.getPaymentData(bookingTravelId);
				
				BigDecimal hotelCost = paymentData.hotelBookingInfo.getCost();
				BigDecimal flightCost = paymentData.flightBookingInfo.getCost();
				BigDecimal totalCost = hotelCost.add(flightCost);
				
				BookingInfo travelBookingInfo = new BookingInfo();
				travelBookingInfo.setId(bookingTravelId);
				travelBookingInfo.setCost(totalCost);
				
				exchange.getMessage().setBody(travelBookingInfo);
			})
			.to("direct:notification");
		
		from("direct:notification").routeId("notification")
			.log("fired notification")
			.to("stream:out");
	}
	
	private void flight() {
		from("kafka:TravelReqTopic?brokers=localhost:9092").routeId("bookFlight")
			.log("fired bookFlight")
			.unmarshal().json(JsonLibrary.Jackson, BookTravelRequest.class)
			.process((exchange) -> {
				String bookingTravelId = exchange.getMessage().getHeader("bookingTravelId", String.class);
				ProcessingState previousState = flightStateService.sendEvent(bookingTravelId, ProcessingEvent.START);
				
				if (previousState!=ProcessingState.CANCELLED) {
					BookingInfo bi = new BookingInfo();
					bi.setId(bookingIdentifierService.getBookingIdentifier());
					
					BookTravelRequest btr= exchange.getMessage().getBody(BookTravelRequest.class);
					if (btr != null && btr.getFlight() != null && btr.getFlight().getFrom() != null && btr.getFlight().getFrom().getAirport() != null) {
						String from = btr.getFlight().getFrom().getAirport();
						if (from.equals("JFK")) {
							bi.setCost(new BigDecimal(700));
						}
						else if (from.equals("PEK")) {
							throw new FlightException("Not serviced airport: " + from);
						}
						else {
							bi.setCost(new BigDecimal(600));
						}
					}
					
					exchange.getMessage().setBody(bi);
					previousState = flightStateService.sendEvent(bookingTravelId, ProcessingEvent.FINISH);
				}
				
				exchange.getMessage().setHeader("previousState", previousState);
			})
			.marshal().json()
			.to("stream:out")
			.choice()
			.when(header("previousState").isEqualTo(ProcessingState.CANCELLED))
			.to("direct:bookFlightCompensationAction")
			.otherwise()
				.setHeader("serviceType", constant("flight"))
				.to("kafka:BookingInfoTopic?brokers=localhost:9092")
			.endChoice();
		
		from("kafka:TravelBookingFailTopic?brokers=localhost:9092").routeId("bookFlightCompensation")
			.log("fired bookFlightCompensation")
			.unmarshal().json(JsonLibrary.Jackson, ExceptionResponse.class)
			.choice()
			.when(header("serviceType").isNotEqualTo("flight"))
			.process((exchange) -> {
				String bookingTravelId = exchange.getMessage().getHeader("bookingTravelId", String.class);
				ProcessingState previousState = flightStateService.sendEvent(bookingTravelId, ProcessingEvent.CANCEL);
				exchange.getMessage().setHeader("previousState", previousState);
			})
			.choice()
			.when(header("previousState").isEqualTo(ProcessingState.FINISHED))
			.to("direct:bookFlightCompensationAction")
			.endChoice()
			.endChoice();
		
		from("direct:bookFlightCompensationAction").routeId("bookFlightCompensationAction")
			.log("fired bookFlightCompensationAction")
			.to("stream:out");
	}
	
	private void hotel() {
		from("kafka:TravelReqTopic?brokers=localhost:9092").routeId("bookHotel")
			.log("fired bookHotel")
			.unmarshal().json(JsonLibrary.Jackson, BookTravelRequest.class)
			.process((exchange) -> {
				String bookingTravelId = exchange.getMessage().getHeader("bookingTravelId", String.class);
				ProcessingState previousState = hotelStateService.sendEvent(bookingTravelId, ProcessingEvent.START);
				
				if (previousState!=ProcessingState.CANCELLED) {
					BookingInfo bi = new BookingInfo();
					bi.setId(bookingIdentifierService.getBookingIdentifier());
					
					BookTravelRequest btr = exchange.getMessage().getBody(BookTravelRequest.class);
					if (btr != null && btr.getHotel() != null && btr.getHotel().getCountry() != null) {
						String country = btr.getHotel().getCountry();
						
						if (country.equals("USA")) {
							bi.setCost(new BigDecimal(999));
						} else if (country.equals("China")) {
							throw new HotelException("Not serviced destination: " + country);
						} else {
							bi.setCost(new BigDecimal(888));
						}
					}
					
					exchange.getMessage().setBody(bi);
					previousState = hotelStateService.sendEvent(bookingTravelId, ProcessingEvent.FINISH);
				}
				
				exchange.getMessage().setHeader("previousState", previousState);
			})
			.marshal().json()
			.to("stream:out")
			.choice()
				.when(header("previousState").isEqualTo(ProcessingState.CANCELLED))
				.to("direct:bookHotelCompensationAction")
			.otherwise()
				.setHeader("serviceType", constant("hotel"))
				.to("kafka:BookingInfoTopic?brokers=localhost:9092")
			.endChoice();
		
		from("kafka:TravelBookingFailTopic?brokers=localhost:9092").routeId("bookHotelCompensation")
			.log("fired bookHotelCompensation")
			.unmarshal().json(JsonLibrary.Jackson, ExceptionResponse.class)
			.choice()
			.when(header("serviceType").isNotEqualTo("hotel"))
			.process((exchange) -> {
				String bookingTravelId = exchange.getMessage().getHeader("bookingTravelId", String.class);
				ProcessingState previousState = hotelStateService.sendEvent(bookingTravelId, ProcessingEvent.CANCEL);
				exchange.getMessage().setHeader("previousState", previousState);
			})
			.choice()
			.when(header("previousState").isEqualTo(ProcessingState.FINISHED))
			.to("direct:bookHotelCompensationAction")
			.endChoice()
			.endChoice();
		
		from("direct:bookHotelCompensationAction").routeId("bookHotelCompensationAction")
			.log("fired bookHotelCompensationAction")
			.to("stream:out");
	}

	private void gateway() {
		restConfiguration()
			.component("servlet")
			.bindingMode(RestBindingMode.json)
			.dataFormatProperty("prettyPrint", "true")
			.enableCORS(true)
			.contextPath("/api")
			.apiContextPath("/api-doc")
			.apiProperty("api.title", "Micro Travel booking API")
			.apiProperty("api.version", "1.0.0");
		
		rest("/travel")
			.description("Micro Travel booking REST service")
			.consumes("application/json")
			.produces("application/json")
			.post("/booking").description("Book a travel").type(BookTravelRequest.class).outType(BookingInfo.class)
			.param().name("body").type(body).description("The travel to book").endParam()
			.responseMessage().code(200).message("Travel successfully booked").endResponseMessage()
			.to("direct:bookTravel");
		
		from("direct:bookTravel")
			.routeId("bookTravel")
			.log("bookTravel fired")
			.process((exchange) -> {
				exchange.getMessage().setHeader("bookingTravelId", bookingIdentifierService.getBookingIdentifier());
			})
			.to("direct:TravelBookRequest")
			.to("direct:bookRequester");
		
		from("direct:bookRequester")
			.routeId("bookRequester")
			.log("bookRequester fired")
			.process((exchange) -> {
				exchange.getMessage().setBody(
					Utils.prepareBookingInfo(
						exchange.getMessage().getHeader("bookingTravelId", String.class), 
						null
					)
				);
			});
		
		from("direct:TravelBookRequest")
			.routeId("TravelBookRequest")
			.log("brokerTopic fired")
			.marshal().json()
			.to("kafka:TravelReqTopic?brokers=localhost:9092");
	}
	
	private void bookFlightExceptionHandlers() {
		onException(FlightException.class)
		.process((exchange) -> {
			ExceptionResponse er = new ExceptionResponse();
			er.setTimestamp(OffsetDateTime.now());
			Exception cause = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
			er.setMessage(cause.getMessage());
			exchange.getMessage().setBody(er);
		})
	    .marshal().json()
		.to("stream:out")
		.setHeader("serviceType", constant("flight"))
		.to("kafka:TravelBookingFailTopic?brokers=localhost:9092")
		.handled(true);	
	}
	
	private void bookHotelExceptionHandlers() {
		onException(HotelException.class)
		.process((exchange) -> {
			ExceptionResponse er = new ExceptionResponse();
			er.setTimestamp(OffsetDateTime.now());
			Exception cause = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
			er.setMessage(cause.getMessage());
			exchange.getMessage().setBody(er);
		})
        .marshal().json()
		.to("stream:out")
		.setHeader("serviceType", constant("hotel"))
		.to("kafka:TravelBookingFailTopic?brokers=localhost:9092")
		.handled(true);	
	}	
}
