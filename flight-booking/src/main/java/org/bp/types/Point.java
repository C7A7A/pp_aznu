
package org.bp.types;

import javax.xml.datatype.XMLGregorianCalendar;

public class Point {


    protected String airport;
    protected XMLGregorianCalendar date;
	public String getAirport() {
		return airport;
	}
	public void setAirport(String airport) {
		this.airport = airport;
	}
	public XMLGregorianCalendar getDate() {
		return date;
	}
	public void setDate(XMLGregorianCalendar date) {
		this.date = date;
	}


}
