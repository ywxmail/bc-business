/**
 * 
 */
package cn.bc.business.invoice.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 票务库存
 * 
 * @author wis
 */
@Entity
@Table(name = "BS_INVOICE_STOCK")
public class Invoice4Stock implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final String KEY_UID = "Invoice4Stock.uid";
	public static final String KEY_CODE = Invoice4Stock.class.getSimpleName();

	private Long id;
	private int saleCount;//卖出数量
	private String saleCode;//卖出的编码范围:00001~40000;50001~60000
	private String remainderCode;//剩余的编码范围:40001~50000;60001~100000
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "SALE_COUNT")
	public int getSaleCount() {
		return saleCount;
	}

	public void setSaleCount(int saleCount) {
		this.saleCount = saleCount;
	}

	@Column(name = "SALE_CODE")
	public String getSaleCode() {
		return saleCode;
	}

	public void setSaleCode(String saleCode) {
		this.saleCode = saleCode;
	}

	@Column(name = "REMAINDER_CODE")
	public String getRemainderCode() {
		return remainderCode;
	}

	public void setRemainderCode(String remainderCode) {
		this.remainderCode = remainderCode;
	}
	
}