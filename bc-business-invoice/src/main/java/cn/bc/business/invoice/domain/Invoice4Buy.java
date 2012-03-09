/**
 * 
 */
package cn.bc.business.invoice.domain;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import cn.bc.identity.domain.ActorHistory;
import cn.bc.identity.domain.FileEntityImpl;

/**
 * 票务采购单
 * 
 * @author wis
 */
@Entity
@Table(name = "BS_INVOICE_BUY")
public class Invoice4Buy extends FileEntityImpl {
	private static final long serialVersionUID = 1L;
	public static final String KEY_UID = "Invoice4Buy.uid";
	public static final String KEY_CODE = Invoice4Buy.class.getSimpleName();

	/** 票务状态：正常 */
	public static final int STATUS_NORMAL = 0;
	/** 票务状态：作废 */
	public static final int STATUS_INVALID = 1;
	
	/** 发票类型：打印票 */
	public static final int TYPE_PRINT = 1;
	/** 发票类型：手撕票 */
	public static final int TYPE_TORE = 2;
	
	/** 单位：卷*/
	public static final int UNIT_JUAN = 2;
	/** 单位：本 */
	public static final int UNIT_BEN = 1;
	
	private int status;//状态
	private String company;//所属公司
	private String code;//发票代码
	private int type;// 发票类型：1-打印票,2-手撕票
	private String startNo;//开始号
	private String endNo;//编码号
	private int count;//采购数量
	private int unit;// 单位:1-卷,2-本
	private Float buyPrice; //单价
	private Float sellPrice; //售价
	private ActorHistory buyerId;// 采购人
	private Calendar buyDate; //采购日期
	private String desc;//备注
	
	@Column(name = "STATUS_")
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	@Column(name = "TYPE_")
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
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
	@Column(name = "COUNT_")
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	@Column(name = "UNIT_")
	public int getUnit() {
		return unit;
	}
	public void setUnit(int unit) {
		this.unit = unit;
	}
	@Column(name = "BUY_PRICE")
	public Float getBuyPrice() {
		return buyPrice;
	}
	public void setBuyPrice(Float buyPrice) {
		this.buyPrice = buyPrice;
	}
	@Column(name = "SELL_PRICE")
	public Float getSellPrice() {
		return sellPrice;
	}
	public void setSellPrice(Float sellPrice) {
		this.sellPrice = sellPrice;
	}
	@ManyToOne(fetch = FetchType.EAGER, optional = true)
	@JoinColumn(name = "BUYER_ID", referencedColumnName = "ID")
	public ActorHistory getBuyerId() {
		return buyerId;
	}
	public void setBuyerId(ActorHistory buyerId) {
		this.buyerId = buyerId;
	}
	@Column(name = "BUY_DATE")
	public Calendar getBuyDate() {
		return buyDate;
	}
	public void setBuyDate(Calendar buyDate) {
		this.buyDate = buyDate;
	}
	@Column(name = "DESC_")
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
}