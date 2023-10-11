
package org.bp.payment.model;



public class PaymentCard {

    protected String name;
    protected String validTo;
    protected String number;

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getValidTo() {
        return validTo;
    }

    public void setValidTo(String value) {
        this.validTo = value;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String value) {
        this.number = value;
    }

}
