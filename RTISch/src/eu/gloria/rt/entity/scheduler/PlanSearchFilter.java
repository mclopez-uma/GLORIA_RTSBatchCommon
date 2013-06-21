
package eu.gloria.rt.entity.scheduler;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;element name="execPredictedDateInteval" type="{http://gloria.eu/rt/entity/scheduler}dateIterval" minOccurs="0"/>
 *         &lt;element name="execBeginDateInteval" type="{http://gloria.eu/rt/entity/scheduler}dateIterval" minOccurs="0"/>
 *         &lt;element name="execEndDateInteval" type="{http://gloria.eu/rt/entity/scheduler}dateIterval" minOccurs="0"/>
 *         &lt;element name="queuedDateInteval" type="{http://gloria.eu/rt/entity/scheduler}dateIterval" minOccurs="0"/>
 *         &lt;element name="states" type="{http://gloria.eu/rt/entity/scheduler}planState" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="stateDetails" type="{http://gloria.eu/rt/entity/scheduler}planStateDetail" maxOccurs="unbounded" minOccurs="0"/>
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
    "execPredictedDateInteval",
    "execBeginDateInteval",
    "execEndDateInteval",
    "queuedDateInteval",
    "states",
    "stateDetails"
})
public class PlanSearchFilter {

    protected DateIterval execPredictedDateInteval;
    protected DateIterval execBeginDateInteval;
    protected DateIterval execEndDateInteval;
    protected DateIterval queuedDateInteval;
    protected List<PlanState> states;
    protected List<PlanStateDetail> stateDetails;

    /**
     * Obtiene el valor de la propiedad execPredictedDateInteval.
     * 
     * @return
     *     possible object is
     *     {@link DateIterval }
     *     
     */
    public DateIterval getExecPredictedDateInteval() {
        return execPredictedDateInteval;
    }

    /**
     * Define el valor de la propiedad execPredictedDateInteval.
     * 
     * @param value
     *     allowed object is
     *     {@link DateIterval }
     *     
     */
    public void setExecPredictedDateInteval(DateIterval value) {
        this.execPredictedDateInteval = value;
    }

    /**
     * Obtiene el valor de la propiedad execBeginDateInteval.
     * 
     * @return
     *     possible object is
     *     {@link DateIterval }
     *     
     */
    public DateIterval getExecBeginDateInteval() {
        return execBeginDateInteval;
    }

    /**
     * Define el valor de la propiedad execBeginDateInteval.
     * 
     * @param value
     *     allowed object is
     *     {@link DateIterval }
     *     
     */
    public void setExecBeginDateInteval(DateIterval value) {
        this.execBeginDateInteval = value;
    }

    /**
     * Obtiene el valor de la propiedad execEndDateInteval.
     * 
     * @return
     *     possible object is
     *     {@link DateIterval }
     *     
     */
    public DateIterval getExecEndDateInteval() {
        return execEndDateInteval;
    }

    /**
     * Define el valor de la propiedad execEndDateInteval.
     * 
     * @param value
     *     allowed object is
     *     {@link DateIterval }
     *     
     */
    public void setExecEndDateInteval(DateIterval value) {
        this.execEndDateInteval = value;
    }

    /**
     * Obtiene el valor de la propiedad queuedDateInteval.
     * 
     * @return
     *     possible object is
     *     {@link DateIterval }
     *     
     */
    public DateIterval getQueuedDateInteval() {
        return queuedDateInteval;
    }

    /**
     * Define el valor de la propiedad queuedDateInteval.
     * 
     * @param value
     *     allowed object is
     *     {@link DateIterval }
     *     
     */
    public void setQueuedDateInteval(DateIterval value) {
        this.queuedDateInteval = value;
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
     * Gets the value of the stateDetails property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the stateDetails property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStateDetails().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PlanStateDetail }
     * 
     * 
     */
    public List<PlanStateDetail> getStateDetails() {
        if (stateDetails == null) {
            stateDetails = new ArrayList<PlanStateDetail>();
        }
        return this.stateDetails;
    }

}
