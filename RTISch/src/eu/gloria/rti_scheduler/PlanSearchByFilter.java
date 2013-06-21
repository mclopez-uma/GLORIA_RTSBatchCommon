
package eu.gloria.rti_scheduler;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import eu.gloria.rt.entity.scheduler.PlanSearchFilter;


/**
 * <p>Clase Java para anonymous complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="filter" type="{http://gloria.eu/rt/entity/scheduler}planSearchFilter"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "filter"
})
@XmlRootElement(name = "planSearchByFilter")
public class PlanSearchByFilter {

    @XmlElement(required = true)
    protected PlanSearchFilter filter;

    /**
     * Obtiene el valor de la propiedad filter.
     * 
     * @return
     *     possible object is
     *     {@link PlanSearchFilter }
     *     
     */
    public PlanSearchFilter getFilter() {
        return filter;
    }

    /**
     * Define el valor de la propiedad filter.
     * 
     * @param value
     *     allowed object is
     *     {@link PlanSearchFilter }
     *     
     */
    public void setFilter(PlanSearchFilter value) {
        this.filter = value;
    }

}
