package org.bp.payment.model;

import java.math.BigDecimal;

public class Amount {
	private BigDecimal value;
	private String currency;
	public BigDecimal getValue() {
		return value;
	}
	public void setValue(BigDecimal value) {
		this.value = value;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	

}
