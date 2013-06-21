
package eu.gloria.rt.entity.scheduler;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para planStateDetail.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <p>
 * <pre>
 * &lt;simpleType name="planStateDetail">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="NONE"/>
 *     &lt;enumeration value="WRONG_BEHAVIOUR"/>
 *     &lt;enumeration value="OUT_OF_TIME"/>
 *     &lt;enumeration value="BY_ADMINISTRATOR"/>
 *     &lt;enumeration value="BY_GLORIA"/>
 *     &lt;enumeration value="BY_RTS"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "planStateDetail")
@XmlEnum
public enum PlanStateDetail {

    NONE,
    WRONG_BEHAVIOUR,
    OUT_OF_TIME,
    BY_ADMINISTRATOR,
    BY_GLORIA,
    BY_RTS;

    public String value() {
        return name();
    }

    public static PlanStateDetail fromValue(String v) {
        return valueOf(v);
    }

}
