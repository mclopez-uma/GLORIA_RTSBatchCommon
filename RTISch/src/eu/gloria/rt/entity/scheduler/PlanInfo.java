
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
 *         &lt;element name="type" type="{http://gloria.eu/rt/entity/scheduler}planType"/>
 *         &lt;element name="user" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="comment" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="observationSession" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="execDateIni" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="execDateEnd" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="execDeadline" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="receivedDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="advertDeadline" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="advertDateIni" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="advertDateEnd" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="offeredDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="offerDeadline" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="scheduleDateIni" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="scheduleDateEnd" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="predAstr" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
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
    "type",
    "user",
    "description",
    "comment",
    "observationSession",
    "execDateIni",
    "execDateEnd",
    "execDeadline",
    "receivedDate",
    "advertDeadline",
    "advertDateIni",
    "advertDateEnd",
    "offeredDate",
    "offerDeadline",
    "scheduleDateIni",
    "scheduleDateEnd",
    "predAstr"
})
public class PlanInfo {

    @XmlElement(required = true)
    protected String uuid;
    @XmlElement(required = true)
    protected PlanStateInfo stateInfo;
    @XmlElement(required = true)
    protected PlanType type;
    protected String user;
    protected String description;
    protected String comment;
	@XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar observationSession;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar execDateIni;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar execDateEnd;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar execDeadline;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar receivedDate;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar advertDeadline;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar advertDateIni;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar advertDateEnd;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar offeredDate;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar offerDeadline;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar scheduleDateIni;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar scheduleDateEnd;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar predAstr;

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
     * Obtiene el valor de la propiedad type.
     * 
     * @return
     *     possible object is
     *     {@link PlanType }
     *     
     */
    public PlanType getType() {
        return type;
    }

    /**
     * Define el valor de la propiedad type.
     * 
     * @param value
     *     allowed object is
     *     {@link PlanType }
     *     
     */
    public void setType(PlanType value) {
        this.type = value;
    }

    /**
     * Obtiene el valor de la propiedad user.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUser() {
        return user;
    }

    /**
     * Define el valor de la propiedad user.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUser(String value) {
        this.user = value;
    }

    /**
     * Obtiene el valor de la propiedad description.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Define el valor de la propiedad description.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Obtiene el valor de la propiedad comment.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComment() {
        return comment;
    }

	/**
     * Define el valor de la propiedad comment.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComment(String value) {
        this.comment = value;
    }

	/**
     * Obtiene el valor de la propiedad observationSession.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getObservationSession() {
        return observationSession;
    }

    /**
     * Define el valor de la propiedad observationSession.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setObservationSession(XMLGregorianCalendar value) {
        this.observationSession = value;
    }

    /**
     * Obtiene el valor de la propiedad execDateIni.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getExecDateIni() {
        return execDateIni;
    }

    /**
     * Define el valor de la propiedad execDateIni.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setExecDateIni(XMLGregorianCalendar value) {
        this.execDateIni = value;
    }

    /**
     * Obtiene el valor de la propiedad execDateEnd.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getExecDateEnd() {
        return execDateEnd;
    }

    /**
     * Define el valor de la propiedad execDateEnd.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setExecDateEnd(XMLGregorianCalendar value) {
        this.execDateEnd = value;
    }

    /**
     * Obtiene el valor de la propiedad execDeadline.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getExecDeadline() {
        return execDeadline;
    }

    /**
     * Define el valor de la propiedad execDeadline.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setExecDeadline(XMLGregorianCalendar value) {
        this.execDeadline = value;
    }

    /**
     * Obtiene el valor de la propiedad receivedDate.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getReceivedDate() {
        return receivedDate;
    }

    /**
     * Define el valor de la propiedad receivedDate.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setReceivedDate(XMLGregorianCalendar value) {
        this.receivedDate = value;
    }

    /**
     * Obtiene el valor de la propiedad advertDeadline.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getAdvertDeadline() {
        return advertDeadline;
    }

    /**
     * Define el valor de la propiedad advertDeadline.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setAdvertDeadline(XMLGregorianCalendar value) {
        this.advertDeadline = value;
    }

    /**
     * Obtiene el valor de la propiedad advertDateIni.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getAdvertDateIni() {
        return advertDateIni;
    }

    /**
     * Define el valor de la propiedad advertDateIni.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setAdvertDateIni(XMLGregorianCalendar value) {
        this.advertDateIni = value;
    }

    /**
     * Obtiene el valor de la propiedad advertDateEnd.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getAdvertDateEnd() {
        return advertDateEnd;
    }

    /**
     * Define el valor de la propiedad advertDateEnd.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setAdvertDateEnd(XMLGregorianCalendar value) {
        this.advertDateEnd = value;
    }

    /**
     * Obtiene el valor de la propiedad offeredDate.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getOfferedDate() {
        return offeredDate;
    }

    /**
     * Define el valor de la propiedad offeredDate.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setOfferedDate(XMLGregorianCalendar value) {
        this.offeredDate = value;
    }

    /**
     * Obtiene el valor de la propiedad offerDeadline.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getOfferDeadline() {
        return offerDeadline;
    }

    /**
     * Define el valor de la propiedad offerDeadline.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setOfferDeadline(XMLGregorianCalendar value) {
        this.offerDeadline = value;
    }

    /**
     * Obtiene el valor de la propiedad scheduleDateIni.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getScheduleDateIni() {
        return scheduleDateIni;
    }

    /**
     * Define el valor de la propiedad scheduleDateIni.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setScheduleDateIni(XMLGregorianCalendar value) {
        this.scheduleDateIni = value;
    }

    /**
     * Obtiene el valor de la propiedad scheduleDateEnd.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getScheduleDateEnd() {
        return scheduleDateEnd;
    }

    /**
     * Define el valor de la propiedad scheduleDateEnd.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setScheduleDateEnd(XMLGregorianCalendar value) {
        this.scheduleDateEnd = value;
    }

    /**
     * Obtiene el valor de la propiedad predAstr.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getPredAstr() {
        return predAstr;
    }

    /**
     * Define el valor de la propiedad predAstr.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setPredAstr(XMLGregorianCalendar value) {
        this.predAstr = value;
    }

}
