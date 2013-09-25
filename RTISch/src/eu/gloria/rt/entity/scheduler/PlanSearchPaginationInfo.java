
package eu.gloria.rt.entity.scheduler;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para planSearchPaginationInfo complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="planSearchPaginationInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="pageNumber" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="pageSize" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="pageCount" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "planSearchPaginationInfo", propOrder = {
    "pageNumber",
    "pageSize",
    "pageCount"
})
public class PlanSearchPaginationInfo {

    protected int pageNumber;
    protected int pageSize;
    protected int pageCount;

    /**
     * Obtiene el valor de la propiedad pageNumber.
     * 
     */
    public int getPageNumber() {
        return pageNumber;
    }

    /**
     * Define el valor de la propiedad pageNumber.
     * 
     */
    public void setPageNumber(int value) {
        this.pageNumber = value;
    }

    /**
     * Obtiene el valor de la propiedad pageSize.
     * 
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * Define el valor de la propiedad pageSize.
     * 
     */
    public void setPageSize(int value) {
        this.pageSize = value;
    }

    /**
     * Obtiene el valor de la propiedad pageCount.
     * 
     */
    public int getPageCount() {
        return pageCount;
    }

    /**
     * Define el valor de la propiedad pageCount.
     * 
     */
    public void setPageCount(int value) {
        this.pageCount = value;
    }

}
