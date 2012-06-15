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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import cn.bc.business.motorcade.domain.Motorcade;
import cn.bc.identity.domain.ActorHistory;
import cn.bc.identity.domain.FileEntityImpl;

/**
 * 票务销售单
 * 
 * @author wis
 */
@Entity
@Table(name = "BS_INVOICE_SELL")
public class Invoice4Sell extends FileEntityImpl {
	private static final long serialVersionUID = 1L;
	public static final String KEY_UID = "Invoice4Sell.uid";
	public static final String KEY_CODE = Invoice4Sell.class.getSimpleName();
	
	/** 发票销售查看状态：销售 */
	public static final String READ_TYPE_SELL = "1";
	/** 发票销售查看状态：退票 */
	public static final String READ_TYPE_REFUND = "2";
	/** 发票销售查看状态：查询 */
	public static final String READ_TYPE_READ = "3";

	/** 票务状态：正常 */
	public static final int STATUS_NORMAL = 0;
	/** 票务状态：作废 */
	public static final int STATUS_INVALID = 1;
	
	/** 收款方式：现金 */
	public static final int PAY_TYPE_CASH = 0;
	/** 收款方式：银行卡 */
	public static final int PAY_TYPE_BANK_CARE = 1;
	
	/** 类型：实销 **/
	public static final int TYPE_SELL=1;
	
	/** 类型：退票**/
	public static final int TYPE_REFUND=2;

	private Set<Invoice4SellDetail> invoice4SellDetail;
	private Long buyerId;//购买人ID;
	private String buyerName;//购买人姓名;
	private Long carId;//车辆ID
	private String carPlate;//车牌号码
	private Motorcade motorcadeId;//车队ID
	private String company;//所属公司
	private Calendar sellDate; //销售日期
	private ActorHistory cashierId;// 收银员id
	private Integer payType; //收款方式
	private String bankCode; //银行流水号
	private String desc;//备注
	private int status;//状态
	private int type;//类型
	
	@OneToMany(mappedBy = "invoice4Sell", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	public Set<Invoice4SellDetail> getInvoice4SellDetail() {
		return invoice4SellDetail;
	}
	public void setInvoice4SellDetail(Set<Invoice4SellDetail> invoice4SellDetail) {
		this.invoice4SellDetail = invoice4SellDetail;
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
	@Column(name = "CAR_ID")
	public Long getCarId() {
		return carId;
	}
	public void setCarId(Long carId) {
		this.carId = carId;
	}
	@Column(name = "CAR_PLATE")
	public String getCarPlate() {
		return carPlate;
	}
	public void setCarPlate(String carPlate) {
		this.carPlate = carPlate;
	}
	@ManyToOne(fetch = FetchType.EAGER, optional = true)
	@JoinColumn(name = "MOTORCADE_ID", referencedColumnName = "ID")
	public Motorcade getMotorcadeId() {
		return motorcadeId;
	}
	public void setMotorcadeId(Motorcade motorcadeId) {
		this.motorcadeId = motorcadeId;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	@Column(name = "SELL_DATE")
	public Calendar getSellDate() {
		return sellDate;
	}
	public void setSellDate(Calendar sellDate) {
		this.sellDate = sellDate;
	}
	@ManyToOne(fetch = FetchType.EAGER, optional = true)
	@JoinColumn(name = "CASHIER_ID", referencedColumnName = "ID")
	public ActorHistory getCashierId() {
		return cashierId;
	}
	public void setCashierId(ActorHistory cashierId) {
		this.cashierId = cashierId;
	}
	@Column(name = "PAY_TYPE")
	public Integer getPayType() {
		return payType;
	}
	public void setPayType(Integer payType) {
		this.payType = payType;
	}
	@Column(name = "BANK_CODE")
	public String getBankCode() {
		return bankCode;
	}
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}
	@Column(name = "DESC_")
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}

	@Column(name = "STATUS_")
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	@Column(name = "TYPE_")
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	
}