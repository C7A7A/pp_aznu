package org.bp.flight;



import javax.jws.WebService;

import org.bp.types.BookingInfo;
import org.bp.types.Fault;
import org.bp.types.Flight;
import org.bp.types.FlightTicket;
import org.springframework.stereotype.Service;

@WebService
@Service
public class FlightService {
	
	public BookingInfo bookFligth(FlightTicket fligthTicket) throws Fault{
		if (fligthTicket!=null) {
			Flight fligth = fligthTicket.getFlight();
			if (fligth!=null && fligth.getFrom() !=null && fligth.getFrom().getAirport()!=null
					&& fligth.getTo()!=null && fligth.getFrom().getAirport().equals(fligth.getTo().getAirport())
					) {
				Fault fault = new Fault();
				fault.setCode(5);
				fault.setText("Start and destination airport can not be the same");
				throw fault;
			}
		}
		BookingInfo bookingInfo = new BookingInfo();
		bookingInfo.setId(1);
		bookingInfo.setCost(new java.math.BigDecimal(345));
		return bookingInfo;
		
	}
	
	public BookingInfo cancelBooking(int bookingId)  throws Fault {
		return null;
	}

}
