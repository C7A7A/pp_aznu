package org.bp.hotelui;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;

import org.bp.BookRoomRequest;
import org.bp.hotel.HotelBooking;
import org.bp.hotel.HotelBookingEndpointService;
import org.bp.hotel.HotelFaultMsg;
import org.bp.types.BookingInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class HotelBookingController {
	private static final QName SERVICE_NAME = new QName("http://hotel.bp.org/", "HotelBookingEndpointService");

	@GetMapping("/bookRoom")
	public String bookRoomForm(Model model) {
		model.addAttribute("bookRoomRequest", new BookRoomRequest());
		return "bookRoom";
	}
	
	@PostMapping("/bookRoom")
	public String bookRoom(@ModelAttribute BookRoomRequest brf, Model model) {
		URL wsdlURL = HotelBookingEndpointService.WSDL_LOCATION;
		
		HotelBookingEndpointService ss = new HotelBookingEndpointService(wsdlURL, SERVICE_NAME);
        HotelBooking port = ss.getHotelBookingEndpointPort();

        {
	        System.out.println("Invoking bookRoom...");
	        org.bp.BookRoomRequest _bookRoom_payload = brf;
	        try {
	            org.bp.types.BookingInfo _bookRoom__return = port.bookRoom(_bookRoom_payload);
	            System.out.println("bookRoom.result=" + _bookRoom__return);
	            
	            model.addAttribute("bookingInfo", _bookRoom__return);
	            return "result";
	
	        } catch (HotelFaultMsg e) {
	            System.out.println("Expected exception: HotelFaultMsg has occurred.");
	            System.out.println(e.toString());
	            model.addAttribute("hotelFaultMsg", e);
	            return "fault";
	        }
        }
	}

}
