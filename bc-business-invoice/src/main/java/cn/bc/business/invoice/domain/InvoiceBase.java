/**
 * 
 */
package cn.bc.business.invoice.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import cn.bc.identity.domain.RichFileEntityImpl;

/**
 * 票务基类
 * 
 * @author wis
 */
@Entity
@Table(name = "BS_INVOICE_BASE")
@Inheritance(strategy = InheritanceType.JOINED)
public class InvoiceBase extends RichFileEntityImpl {
	private static final long serialVersionUID = 1L;
	public static final String KEY_UID = "invoice.uid";
	public static final String KEY_CODE = InvoiceBase.class.getSimpleName();

	/** 发票类型：打印票 */
	public static final int TYPE_PRINT = 0;
	/** 发票类型：手撕票 */
	public static final int TYPE_TORE = 1;
	
	private int type;// 发票类型：1-打印票,2-手撕票
	private int count;// 数量
	private String invoiceCode;// 发票代码
	private String startCode;//发票开始编码
	private String endCode;//发票结束编码
	private String company;//所属公司
	private String desc;//备注
	
	@Column(name = "TYPE_")
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	@Column(name = "COUNT_")
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
	@Column(name = "INVOICE_CODE")
	public String getInvoiceCode() {
		return invoiceCode;
	}
	public void setInvoiceCode(String invoiceCode) {
		this.invoiceCode = invoiceCode;
	}
	
	@Column(name = "START_CODE")
	public String getStartCode() {
		return startCode;
	}
	public void setStartCode(String startCode) {
		this.startCode = startCode;
	}
	
	@Column(name = "END_CODE")
	public String getEndCode() {
		return endCode;
	}
	public void setEndCode(String endCode) {
		this.endCode = endCode;
	}
	
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	
	@Column(name = "DESC_")
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	
	
	
}