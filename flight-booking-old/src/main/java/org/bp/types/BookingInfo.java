
package org.bp.types;

import java.math.BigDecimal;

public class BookingInfo {

    protected int id;
    protected BigDecimal cost;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public BigDecimal getCost() {
		return cost;
	}
	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}


}
