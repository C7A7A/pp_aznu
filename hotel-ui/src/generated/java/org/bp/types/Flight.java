
package org.bp.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Flight complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Flight"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="from" type="{http://www.bp.org/types}Point"/&gt;
 *         &lt;element name="to" type="{http://www.bp.org/types}Point"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Flight", propOrder = {
    "from",
    "to"
})
public class Flight {

    @XmlElement(required = true)
    protected Point from;
    @XmlElement(required = true)
    protected Point to;

    /**
     * Gets the value of the from property.
     * 
     * @return
     *     possible object is
     *     {@link Point }
     *     
     */
    public Point getFrom() {
        return from;
    }

    /**
     * Sets the value of the from property.
     * 
     * @param value
     *     allowed object is
     *     {@link Point }
     *     
     */
    public void setFrom(Point value) {
        this.from = value;
    }

    /**
     * Gets the value of the to property.
     * 
     * @return
     *     possible object is
     *     {@link Point }
     *     
     */
    public Point getTo() {
        return to;
    }

    /**
     * Sets the value of the to property.
     * 
     * @param value
     *     allowed object is
     *     {@link Point }
     *     
     */
    public void setTo(Point value) {
        this.to = value;
    }

}
