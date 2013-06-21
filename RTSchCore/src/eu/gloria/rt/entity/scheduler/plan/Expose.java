//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.03.14 at 10:12:17 AM CET 
//


package eu.gloria.rt.entity.scheduler.plan;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for expose complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="expose">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="expositionTime" type="{http://gloria.eu/rt/entity/scheduler/plan}seconds"/>
 *         &lt;choice>
 *           &lt;element name="repeatDuration" type="{http://gloria.eu/rt/entity/scheduler/plan}seconds"/>
 *           &lt;element name="repeatCount" type="{http://gloria.eu/rt/entity/scheduler/plan}positiveInteger"/>
 *         &lt;/choice>
 *         &lt;element name="filter" type="{http://gloria.eu/rt/entity/scheduler/plan}filterType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "expose", propOrder = {
    "expositionTime",
    "repeatDuration",
    "repeatCount",
    "filter"
})
public class Expose {

    protected double expositionTime;
    protected Double repeatDuration;
    protected BigInteger repeatCount;
    @XmlElement(required = true)
    protected FilterType filter;

    /**
     * Gets the value of the expositionTime property.
     * 
     */
    public double getExpositionTime() {
        return expositionTime;
    }

    /**
     * Sets the value of the expositionTime property.
     * 
     */
    public void setExpositionTime(double value) {
        this.expositionTime = value;
    }

    /**
     * Gets the value of the repeatDuration property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getRepeatDuration() {
        return repeatDuration;
    }

    /**
     * Sets the value of the repeatDuration property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setRepeatDuration(Double value) {
        this.repeatDuration = value;
    }

    /**
     * Gets the value of the repeatCount property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getRepeatCount() {
        return repeatCount;
    }

    /**
     * Sets the value of the repeatCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setRepeatCount(BigInteger value) {
        this.repeatCount = value;
    }

    /**
     * Gets the value of the filter property.
     * 
     * @return
     *     possible object is
     *     {@link FilterType }
     *     
     */
    public FilterType getFilter() {
        return filter;
    }

    /**
     * Sets the value of the filter property.
     * 
     * @param value
     *     allowed object is
     *     {@link FilterType }
     *     
     */
    public void setFilter(FilterType value) {
        this.filter = value;
    }

}
