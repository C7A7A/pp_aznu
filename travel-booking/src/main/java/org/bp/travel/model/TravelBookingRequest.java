package org.bp.travel.model;

public class TravelBookingRequest {
	private org.bp.flight.Person person;
	private org.bp.Hotel hotel;
	private org.bp.flight.Flight flight;
	private org.bp.payment.PaymentCard paymentCard;
	
	public org.bp.flight.Person getPerson() {
		return person;
	}
	public void setPerson(org.bp.flight.Person person) {
		this.person = person;
	}
	public org.bp.Hotel getHotel() {
		return hotel;
	}
	public void setHotel(org.bp.Hotel hotel) {
		this.hotel = hotel;
	}
	public org.bp.flight.Flight getFlight() {
		return flight;
	}
	public void setFlight(org.bp.flight.Flight flight) {
		this.flight = flight;
	}
	public org.bp.payment.PaymentCard getPaymentCard() {
		return paymentCard;
	}
	public void setPaymentCard(org.bp.payment.PaymentCard paymentCard) {
		this.paymentCard = paymentCard;
	}
	

}
