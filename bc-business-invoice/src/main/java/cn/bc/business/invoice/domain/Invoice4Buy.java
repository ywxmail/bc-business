/**
 * 
 */
package cn.bc.business.invoice.domain;

import java.util.Calendar;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

/**
 * 票务采购
 * 
 * @author wis
 */
@Entity
@Table(name = "BS_INVOICE_BUY")
public class Invoice4Buy extends InvoiceBase {
	private static final long serialVersionUID = 1L;
	public static final String KEY_UID = "Invoice4Buy.uid";
	public static final String KEY_CODE = Invoice4Buy.class.getSimpleName();

	private Set<Invoice4Sale> invoice4Sales; //销售单集合
	private int unit;// 单位:1-卷,2-本
	private Long buyerId;// 采购人id
	private String buyerName;// 采购人姓名
	private Calendar buyDate; //采购日期
	private Float unitPrice; //单价
	private Float sellingPrice; //售价
	
	@OneToMany(mappedBy = "invoice4Buy", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE, orphanRemoval = true)
	@OrderBy(value = "invoiceCode asc")
	public Set<Invoice4Sale> getInvoice4Sales() {
		return invoice4Sales;
	}
	public void setInvoice4Sales(Set<Invoice4Sale> invoice4Sales) {
		this.invoice4Sales = invoice4Sales;
	}
	@Column(name = "UNIT_")
	public int getUnit() {
		return unit;
	}
	public void setUnit(int unit) {
		this.unit = unit;
	}
	@Column(name = "BUYER_ID")
	public Long getBuyerId() {
		return buyerId;
	}
	public void setBuyerId(Long buyerId) {
		this.buyerId = buyerId;
	}
	@Column(name = "BUYER_NAME")
	public String getBuyerName() {
		return buyerName;
	}
	public void setBuyerName(String buyerName) {
		this.buyerName = buyerName;
	}
	@Column(name = "BUY_DATE")
	public Calendar getBuyDate() {
		return buyDate;
	}
	public void setBuyDate(Calendar buyDate) {
		this.buyDate = buyDate;
	}
	@Column(name = "UNIT_PRICE")
	public Float getUnitPrice() {
		return unitPrice;
	}
	public void setUnitPrice(Float unitPrice) {
		this.unitPrice = unitPrice;
	}
	@Column(name = "SELLING_PRICE")
	public Float getSellingPrice() {
		return sellingPrice;
	}
	public void setSellingPrice(Float sellingPrice) {
		this.sellingPrice = sellingPrice;
	}
	
}