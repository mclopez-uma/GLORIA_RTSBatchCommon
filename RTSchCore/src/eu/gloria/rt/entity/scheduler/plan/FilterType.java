//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.11.07 at 01:25:33 PM CET 
//


package eu.gloria.rt.entity.scheduler.plan;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for filterType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="filterType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="OPEN"/>
 *     &lt;enumeration value="CLOSED"/>
 *     &lt;enumeration value="BESSEL_I"/>
 *     &lt;enumeration value="BESSEL_R"/>
 *     &lt;enumeration value="BESSEL_V"/>
 *     &lt;enumeration value="BESSEL_B"/>
 *     &lt;enumeration value="BESSEL_U"/>
 *     &lt;enumeration value="SLOAN_U"/>
 *     &lt;enumeration value="SLOAN_G"/>
 *     &lt;enumeration value="SLOAN_R"/>
 *     &lt;enumeration value="SLOAN_I"/>
 *     &lt;enumeration value="SLOAN_Z"/>
 *     &lt;enumeration value="Z_FILTER"/>
 *     &lt;enumeration value="Y_FILTER"/>
 *     &lt;enumeration value="CCD_RESPONSE"/>
 *     &lt;enumeration value="GRISM"/>
 *     &lt;enumeration value="SLIT_25UM"/>
 *     &lt;enumeration value="SLIT_50UM"/>
 *     &lt;enumeration value="SLIT_75UM"/>
 *     &lt;enumeration value="SLIT_100UM"/>
 *     &lt;enumeration value="UKIRT_H"/>
 *     &lt;enumeration value="UKIRT_J"/>
 *     &lt;enumeration value="UKIRT_K"/>
 *     &lt;enumeration value="UKIRT_Z"/>
 *     &lt;enumeration value="UKIRT_Y"/>
 *     &lt;enumeration value="JOHNSON_B"/>
 *     &lt;enumeration value="JOHNSON_R"/>
 *     &lt;enumeration value="JOHNSON_I"/>
 *     &lt;enumeration value="JOHNSON_J"/>
 *     &lt;enumeration value="JOHNSON_K"/>
 *     &lt;enumeration value="JOHNSON_L"/>
 *     &lt;enumeration value="JOHNSON_V"/>
 *     &lt;enumeration value="H_ALPHA"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "filterType")
@XmlEnum
public enum FilterType {

    OPEN("OPEN"),
    CLOSED("CLOSED"),
    BESSEL_I("BESSEL_I"),
    BESSEL_R("BESSEL_R"),
    BESSEL_V("BESSEL_V"),
    BESSEL_B("BESSEL_B"),
    BESSEL_U("BESSEL_U"),
    SLOAN_U("SLOAN_U"),
    SLOAN_G("SLOAN_G"),
    SLOAN_R("SLOAN_R"),
    SLOAN_I("SLOAN_I"),
    SLOAN_Z("SLOAN_Z"),
    Z_FILTER("Z_FILTER"),
    Y_FILTER("Y_FILTER"),
    CCD_RESPONSE("CCD_RESPONSE"),
    GRISM("GRISM"),
    @XmlEnumValue("SLIT_25UM")
    SLIT_25_UM("SLIT_25UM"),
    @XmlEnumValue("SLIT_50UM")
    SLIT_50_UM("SLIT_50UM"),
    @XmlEnumValue("SLIT_75UM")
    SLIT_75_UM("SLIT_75UM"),
    @XmlEnumValue("SLIT_100UM")
    SLIT_100_UM("SLIT_100UM"),
    UKIRT_H("UKIRT_H"),
    UKIRT_J("UKIRT_J"),
    UKIRT_K("UKIRT_K"),
    UKIRT_Z("UKIRT_Z"),
    UKIRT_Y("UKIRT_Y"),
    JOHNSON_B("JOHNSON_B"),
    JOHNSON_R("JOHNSON_R"),
    JOHNSON_I("JOHNSON_I"),
    JOHNSON_J("JOHNSON_J"),
    JOHNSON_K("JOHNSON_K"),
    JOHNSON_L("JOHNSON_L"),
    JOHNSON_V("JOHNSON_V"),
    H_ALPHA("H_ALPHA");
    private final String value;

    FilterType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static FilterType fromValue(String v) {
        for (FilterType c: FilterType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
