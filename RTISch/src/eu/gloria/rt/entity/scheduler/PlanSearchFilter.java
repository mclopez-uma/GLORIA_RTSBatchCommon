
package eu.gloria.rt.entity.scheduler;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Clase Java para planSearchFilter complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="planSearchFilter">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="user" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="execPredictedDateInteval" type="{http://gloria.eu/rt/entity/scheduler}dateInterval" minOccurs="0"/>
 *         &lt;element name="execBeginDateInteval" type="{http://gloria.eu/rt/entity/scheduler}dateInterval" minOccurs="0"/>
 *         &lt;element name="execEndDateInteval" type="{http://gloria.eu/rt/entity/scheduler}dateInterval" minOccurs="0"/>
 *         &lt;element name="observationSession" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="states" type="{http://gloria.eu/rt/entity/scheduler}planState" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="types" type="{http://gloria.eu/rt/entity/scheduler}planType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "planSearchFilter", propOrder = {
    "user",
    "execPredictedDateInteval",
    "execBeginDateInteval",
    "execEndDateInteval",
    "observationSession",
    "states",
    "types"
})
public class PlanSearchFilter {

    protected String user;
    protected DateInterval execPredictedDateInteval;
    protected DateInterval execBeginDateInteval;
    protected DateInterval execEndDateInteval;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar observationSession;
    protected List<PlanState> states;
    protected List<PlanType> types;

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
     * Obtiene el valor de la propiedad execPredictedDateInteval.
     * 
     * @return
     *     possible object is
     *     {@link DateInterval }
     *     
     */
    public DateInterval getExecPredictedDateInteval() {
        return execPredictedDateInteval;
    }

    /**
     * Define el valor de la propiedad execPredictedDateInteval.
     * 
     * @param value
     *     allowed object is
     *     {@link DateInterval }
     *     
     */
    public void setExecPredictedDateInteval(DateInterval value) {
        this.execPredictedDateInteval = value;
    }

    /**
     * Obtiene el valor de la propiedad execBeginDateInteval.
     * 
     * @return
     *     possible object is
     *     {@link DateInterval }
     *     
     */
    public DateInterval getExecBeginDateInteval() {
        return execBeginDateInteval;
    }

    /**
     * Define el valor de la propiedad execBeginDateInteval.
     * 
     * @param value
     *     allowed object is
     *     {@link DateInterval }
     *     
     */
    public void setExecBeginDateInteval(DateInterval value) {
        this.execBeginDateInteval = value;
    }

    /**
     * Obtiene el valor de la propiedad execEndDateInteval.
     * 
     * @return
     *     possible object is
     *     {@link DateInterval }
     *     
     */
    public DateInterval getExecEndDateInteval() {
        return execEndDateInteval;
    }

    /**
     * Define el valor de la propiedad execEndDateInteval.
     * 
     * @param value
     *     allowed object is
     *     {@link DateInterval }
     *     
     */
    public void setExecEndDateInteval(DateInterval value) {
        this.execEndDateInteval = value;
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
     * Gets the value of the states property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the states property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStates().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PlanState }
     * 
     * 
     */
    public List<PlanState> getStates() {
        if (states == null) {
            states = new ArrayList<PlanState>();
        }
        return this.states;
    }

    /**
     * Gets the value of the types property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the types property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTypes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PlanType }
     * 
     * 
     */
    public List<PlanType> getTypes() {
        if (types == null) {
            types = new ArrayList<PlanType>();
        }
        return this.types;
    }

}
