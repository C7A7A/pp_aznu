package org.bp.hotel;

import java.math.BigDecimal;

import org.bp.BookRoomRequest;
import org.bp.CancelBookingRequest;
import org.bp.types.BookingInfo;
import org.bp.types.Fault;
import org.springframework.stereotype.Service;

@Service
public class HotelBookingEndpoint implements HotelBooking {

	@Override
	public BookingInfo bookRoom(BookRoomRequest payload) throws HotelFaultMsg {
		if (payload != null && payload.getHotel() != null && "China".equals(payload.getHotel().getCountry())) {
			Fault hotelFault = new Fault();
			hotelFault.setCode(10);
			hotelFault.setText("China is temporarily not operated destination");
			
			HotelFaultMsg fault = new HotelFaultMsg("Room is not available", hotelFault);
			throw fault;
		}
		
		BookingInfo response = new BookingInfo();
		response.setId(1);
		response.setCost(new BigDecimal(999));
		
		return response;
	}

	@Override
	public BookingInfo cancelBooking(CancelBookingRequest payload) throws HotelFaultMsg {
		// TODO Auto-generated method stub
		return null;
	}

}
