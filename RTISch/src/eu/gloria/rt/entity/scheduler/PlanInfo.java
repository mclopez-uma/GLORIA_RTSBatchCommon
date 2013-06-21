
package eu.gloria.rt.entity.scheduler;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Clase Java para planInfo complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="planInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="uuid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="stateInfo" type="{http://gloria.eu/rt/entity/scheduler}planStateInfo"/>
 *         &lt;element name="queuedDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="execbeginDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="execEndDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "planInfo", propOrder = {
    "uuid",
    "stateInfo",
    "queuedDate",
    "execbeginDate",
    "execEndDate"
})
public class PlanInfo {

    @XmlElement(required = true)
    protected String uuid;
    @XmlElement(required = true)
    protected PlanStateInfo stateInfo;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar queuedDate;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar execbeginDate;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar execEndDate;

    /**
     * Obtiene el valor de la propiedad uuid.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Define el valor de la propiedad uuid.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUuid(String value) {
        this.uuid = value;
    }

    /**
     * Obtiene el valor de la propiedad stateInfo.
     * 
     * @return
     *     possible object is
     *     {@link PlanStateInfo }
     *     
     */
    public PlanStateInfo getStateInfo() {
        return stateInfo;
    }

    /**
     * Define el valor de la propiedad stateInfo.
     * 
     * @param value
     *     allowed object is
     *     {@link PlanStateInfo }
     *     
     */
    public void setStateInfo(PlanStateInfo value) {
        this.stateInfo = value;
    }

    /**
     * Obtiene el valor de la propiedad queuedDate.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getQueuedDate() {
        return queuedDate;
    }

    /**
     * Define el valor de la propiedad queuedDate.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setQueuedDate(XMLGregorianCalendar value) {
        this.queuedDate = value;
    }

    /**
     * Obtiene el valor de la propiedad execbeginDate.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getExecbeginDate() {
        return execbeginDate;
    }

    /**
     * Define el valor de la propiedad execbeginDate.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setExecbeginDate(XMLGregorianCalendar value) {
        this.execbeginDate = value;
    }

    /**
     * Obtiene el valor de la propiedad execEndDate.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getExecEndDate() {
        return execEndDate;
    }

    /**
     * Define el valor de la propiedad execEndDate.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setExecEndDate(XMLGregorianCalendar value) {
        this.execEndDate = value;
    }

}
