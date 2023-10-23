package org.bp.travel.model;

import java.math.BigDecimal;

public class Utils {
	static public BookingInfo prepareBookingInfo(String bookingId, BigDecimal cost) {
		BookingInfo bookingInfo = new BookingInfo();
		bookingInfo.setId(bookingId);
		bookingInfo.setCost(cost);
		return bookingInfo;
	}

}
