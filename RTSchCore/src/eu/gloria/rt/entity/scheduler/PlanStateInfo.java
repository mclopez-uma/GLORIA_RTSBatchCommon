
package eu.gloria.rt.entity.scheduler;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para planStateInfo complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="planStateInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="state" type="{http://gloria.eu/rt/entity/scheduler}planState"/>
 *         &lt;element name="stateDetail" type="{http://gloria.eu/rt/entity/scheduler}planStateDetail"/>
 *         &lt;element name="errorCode" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="errorDesc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "planStateInfo", propOrder = {
    "state",
    "stateDetail",
    "errorCode",
    "errorDesc"
})
public class PlanStateInfo {

    @XmlElement(required = true)
    protected PlanState state;
    @XmlElement(required = true)
    protected PlanStateDetail stateDetail;
    protected BigInteger errorCode;
    protected String errorDesc;

    /**
     * Obtiene el valor de la propiedad state.
     * 
     * @return
     *     possible object is
     *     {@link PlanState }
     *     
     */
    public PlanState getState() {
        return state;
    }

    /**
     * Define el valor de la propiedad state.
     * 
     * @param value
     *     allowed object is
     *     {@link PlanState }
     *     
     */
    public void setState(PlanState value) {
        this.state = value;
    }

    /**
     * Obtiene el valor de la propiedad stateDetail.
     * 
     * @return
     *     possible object is
     *     {@link PlanStateDetail }
     *     
     */
    public PlanStateDetail getStateDetail() {
        return stateDetail;
    }

    /**
     * Define el valor de la propiedad stateDetail.
     * 
     * @param value
     *     allowed object is
     *     {@link PlanStateDetail }
     *     
     */
    public void setStateDetail(PlanStateDetail value) {
        this.stateDetail = value;
    }

    /**
     * Obtiene el valor de la propiedad errorCode.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getErrorCode() {
        return errorCode;
    }

    /**
     * Define el valor de la propiedad errorCode.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setErrorCode(BigInteger value) {
        this.errorCode = value;
    }

    /**
     * Obtiene el valor de la propiedad errorDesc.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getErrorDesc() {
        return errorDesc;
    }

    /**
     * Define el valor de la propiedad errorDesc.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setErrorDesc(String value) {
        this.errorDesc = value;
    }

}
