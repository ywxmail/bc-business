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

/**
 * 票务销售
 * 
 * @author wis
 */
@Entity
@Table(name = "BS_INVOICE_SALE")
public class Invoice4Sale extends InvoiceBase {
	private static final long serialVersionUID = 1L;
	public static final String KEY_UID = "Invoice4Sale.uid";
	public static final String KEY_CODE = Invoice4Sale.class.getSimpleName();

	/** 类别：司机 */
	public static final int DRIVER_TYPE_DRIVER = 0;
	/** 类别：责任人 */
	public static final int DRIVER_TYPE_CHARGER = 1;
	/** 类别：司机和责任人 */
	public static final int DRIVER_TYPE_DRIVER_AND_CHARGER = 2;
	/** 类别：非编 */
	public static final int DRIVER_TYPE_FEIBIAN = 3;

	private Invoice4Buy invoice4Buy; //采购单
	private Long sellerId;// 销售人id
	private String sellerName;// 销售人姓名
	private Calendar saleDate; //销售日期
	private Integer payType; //付款方式
	private String bankCode; //银行流水号
	private Long motorcadeId;//车队ID
	private String motorcadeName;//车队名称
	private Long carId;//车辆ID
	private String carPlate;//车牌号码
	private Long driverId;//司机ID
	private String driverName;//司机名称
	private int driverType;//司机类型:0-司机,1-责任人,2-司机和责任人,非编
	
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "BUY_ID", referencedColumnName = "ID")
	public Invoice4Buy getInvoice4Buy() {
		return invoice4Buy;
	}
	public void setInvoice4Buy(Invoice4Buy invoice4Buy) {
		this.invoice4Buy = invoice4Buy;
	}
	
	@Column(name = "SELLER_ID")
	public Long getSellerId() {
		return sellerId;
	}
	public void setSellerId(Long sellerId) {
		this.sellerId = sellerId;
	}
	
	@Column(name = "SELLER_NAME")
	public String getSellerName() {
		return sellerName;
	}
	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}
	
	@Column(name = "SALE_DATE")
	public Calendar getSaleDate() {
		return saleDate;
	}
	public void setSaleDate(Calendar saleDate) {
		this.saleDate = saleDate;
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
	
	@Column(name = "MOTORCADE_ID")
	public Long getMotorcadeId() {
		return motorcadeId;
	}
	public void setMotorcadeId(Long motorcadeId) {
		this.motorcadeId = motorcadeId;
	}
	
	@Column(name = "MOTORCADE_NAME")
	public String getMotorcadeName() {
		return motorcadeName;
	}
	public void setMotorcadeName(String motorcadeName) {
		this.motorcadeName = motorcadeName;
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
	
	@Column(name = "DRIVER_ID")
	public Long getDriverId() {
		return driverId;
	}
	public void setDriverId(Long driverId) {
		this.driverId = driverId;
	}
	
	@Column(name = "DRIVER_NAME")
	public String getDriverName() {
		return driverName;
	}
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	@Column(name = "DRIVER_TYPE")
	public int getDriverType() {
		return driverType;
	}
	public void setDriverType(int driverType) {
		this.driverType = driverType;
	}
	
}