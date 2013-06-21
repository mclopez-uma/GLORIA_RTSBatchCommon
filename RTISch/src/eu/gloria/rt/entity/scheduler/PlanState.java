
package eu.gloria.rt.entity.scheduler;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para planState.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <p>
 * <pre>
 * &lt;simpleType name="planState">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="QUEUED"/>
 *     &lt;enumeration value="RUNNING"/>
 *     &lt;enumeration value="DONE"/>
 *     &lt;enumeration value="CANCEL"/>
 *     &lt;enumeration value="ERROR"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "planState")
@XmlEnum
public enum PlanState {

    QUEUED,
    RUNNING,
    DONE,
    CANCEL,
    ERROR;

    public String value() {
        return name();
    }

    public static PlanState fromValue(String v) {
        return valueOf(v);
    }

}
