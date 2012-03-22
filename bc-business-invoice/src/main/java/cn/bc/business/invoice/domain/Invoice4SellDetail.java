/**
 * 
 */
package cn.bc.business.invoice.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import cn.bc.core.EntityImpl;

/**
 * 票务销售明细
 * 
 * @author wis
 */
@Entity
@Table(name = "BS_INVOICE_SELL_DETAIL")
public class Invoice4SellDetail extends EntityImpl {
	private static final long serialVersionUID = 1L;
	
	private Invoice4Sell invoice4Sell;
	private Long buyId;//对应的采购单号id;
	private int count;//销售数量
	private Float price;//销售单价
	private String startNo;//开始号
	private String endNo;//结束号
	
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "SELL_ID", referencedColumnName = "ID")
	public Invoice4Sell getInvoice4Sell() {
		return invoice4Sell;
	}

	public void setInvoice4Sell(Invoice4Sell invoice4Sell) {
		this.invoice4Sell = invoice4Sell;
	}

	@Column(name = "BUY_ID")
	public Long getBuyId() {
		return buyId;
	}

	public void setBuyId(Long buyId) {
		this.buyId = buyId;
	}

	@Column(name = "COUNT_")
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}
	
	@Column(name = "START_NO")
	public String getStartNo() {
		return startNo;
	}

	public void setStartNo(String startNo) {
		this.startNo = startNo;
	}

	@Column(name = "END_NO")
	public String getEndNo() {
		return endNo;
	}

	public void setEndNo(String endNo) {
		this.endNo = endNo;
	}
	
}