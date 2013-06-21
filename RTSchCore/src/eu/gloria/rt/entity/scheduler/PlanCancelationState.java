
package eu.gloria.rt.entity.scheduler;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para planCancelationState.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <p>
 * <pre>
 * &lt;simpleType name="planCancelationState">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="OK"/>
 *     &lt;enumeration value="INVALID_UUID"/>
 *     &lt;enumeration value="TOO_LATE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "planCancelationState")
@XmlEnum
public enum PlanCancelationState {

    OK,
    INVALID_UUID,
    TOO_LATE;

    public String value() {
        return name();
    }

    public static PlanCancelationState fromValue(String v) {
        return valueOf(v);
    }

}
