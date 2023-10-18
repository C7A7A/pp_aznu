package org.bp.travel.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class Utils {
	
	public static org.bp.BookRoomRequest prepareHotelBookingRequest(TravelBookingRequest travelBookingRequest) {
		org.bp.BookRoomRequest bookRoomRequest =  new org.bp.BookRoomRequest();
		bookRoomRequest.setHotel(travelBookingRequest.getHotel());
		
		org.bp.Person person =  new org.bp.Person();
		if (travelBookingRequest.getPerson()!=null) {
			person.setFirstName(travelBookingRequest.getPerson().getFirstName());
			person.setLastName(travelBookingRequest.getPerson().getLastName());
		}
		
		bookRoomRequest.setPerson(person );
		
		return bookRoomRequest;				
	}
	
	public static org.bp.CancelBookingRequest prepareHotelCancelRequest(org.bp.BookingInfo bookingInfo) {
		org.bp.CancelBookingRequest cancelBookingRequest = new org.bp.CancelBookingRequest();
		cancelBookingRequest.setBookingId(bookingInfo.getId());
		return cancelBookingRequest;
	}	
	
	public static org.bp.flight.CancelBooking prepareFlightCancelRequest(org.bp.flight.BookFligthResponse bookFligthResponse) {
		org.bp.flight.CancelBooking cancelBookingRequest = new org.bp.flight.CancelBooking();
		cancelBookingRequest.setArg0(bookFligthResponse.getReturn().getId());
		return cancelBookingRequest;
	}	
	
	public static org.bp.flight.BookFligth prepareFlightBookingRequest(TravelBookingRequest travelBookingRequest) {
		org.bp.flight.BookFligth bookFligth=  new org.bp.flight.BookFligth();
		org.bp.flight.FlightTicket flightTicket =  new org.bp.flight.FlightTicket();
		flightTicket.setFlight(travelBookingRequest.getFlight());
		flightTicket.setPerson(travelBookingRequest.getPerson());
		
		bookFligth.setArg0(flightTicket);
		
		return bookFligth;
	}
	
	public static org.bp.payment.PaymentRequest preparePaymentRequest(TravelBookingRequest travelBookingRequest) {
		org.bp.payment.PaymentRequest paymentRequest = new org.bp.payment.PaymentRequest();
		paymentRequest.setPaymentCard(travelBookingRequest.getPaymentCard());
		org.bp.payment.Amount amount = new org.bp.payment.Amount();
		amount.setValue(new BigDecimal(0));
		amount.setCurrency("PLN");
		paymentRequest.setAmount(amount);
		return paymentRequest;
	}
	
	public static org.bp.payment.PaymentResponse createPaymentResponse() {
		org.bp.payment.PaymentResponse paymentResponse = new org.bp.payment.PaymentResponse();
		paymentResponse.setTransactionId(43434);
		paymentResponse.setTransactionDate(OffsetDateTime.now());
		return paymentResponse;
	}
}
