package org.bp.travel;


import java.util.HashMap;

import javax.annotation.PostConstruct;

import org.bp.travel.model.BookTravelRequest;
import org.bp.travel.model.BookingInfo;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
	private HashMap<String, PaymentData> payments;
	
	@PostConstruct
	void init() {
		payments=new HashMap<>();
	}
	
	public static class PaymentData {
		BookTravelRequest bookTravelRequest;
		BookingInfo hotelBookingInfo;
		BookingInfo flightBookingInfo;
		public boolean isReady() {
			return bookTravelRequest!=null && hotelBookingInfo!=null && flightBookingInfo!=null;
		}
	}
	
	public synchronized boolean addBookTravelRequest(String bookTravelId, BookTravelRequest bookTravelRequest) {
		PaymentData paymentData = getPaymentData(bookTravelId);
		paymentData.bookTravelRequest=bookTravelRequest;		
		return paymentData.isReady();
	}
	

	public synchronized boolean addBookingInfo(String bookTravelId, BookingInfo bookingInfo, String serviceType) {
		PaymentData paymentData = getPaymentData(bookTravelId);
		if (serviceType.equals("flight"))
			paymentData.flightBookingInfo=bookingInfo;
		else 
			paymentData.hotelBookingInfo=bookingInfo;		
		return paymentData.isReady();
	}	
	
	
	public synchronized PaymentData getPaymentData(String bookTravelId) {
		PaymentData paymentData = payments.get(bookTravelId);
		if (paymentData==null) {
			paymentData = new PaymentData();
			payments.put(bookTravelId, paymentData);
		}
		return paymentData;
	}

	


	

}
