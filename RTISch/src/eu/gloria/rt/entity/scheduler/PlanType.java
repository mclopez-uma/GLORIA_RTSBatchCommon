
package eu.gloria.rt.entity.scheduler;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para planType.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <p>
 * <pre>
 * &lt;simpleType name="planType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="DARK"/>
 *     &lt;enumeration value="FLAT"/>
 *     &lt;enumeration value="OBSERVATION"/>
 *     &lt;enumeration value="BIAS"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "planType")
@XmlEnum
public enum PlanType {

    DARK,
    FLAT,
    OBSERVATION,
    BIAS;

    public String value() {
        return name();
    }

    public static PlanType fromValue(String v) {
        return valueOf(v);
    }

}
