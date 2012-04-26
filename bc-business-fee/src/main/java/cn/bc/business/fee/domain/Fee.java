/**
 * 
 */
package cn.bc.business.fee.domain;

import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import cn.bc.identity.domain.RichFileEntityImpl;

/**
 * 承包费
 * 
 * 
 * @author wis
 */
@Entity
@Table(name = "BS_FEE")
public class Fee extends RichFileEntityImpl {
	private static final long serialVersionUID = 1L;
	public static final String ATTACH_TYPE = Fee.class.getSimpleName();
	
	private Integer feeYear;//收费年
	private Integer feeMonth;//收费月
	private Calendar feeDate;//收费日期
	private String collectionWay;//收款方式
	private float shouldSubtotal; //应收小计
	private float shouldUpkeep; //应收维修费
	private float shouldTotal; //应收合计
	private float adjustSubtotal; //调整小计
	private float adjustUpkeep; //调整维修费
	private float adjustTotal; //调整维修费
	private float realSubtotal; //实收小计
	private float realUpkeep; //实收维修费
	private float realTotal; //实收合计
	private float oweSubtotal; //欠费小计
	private float oweUpkeep; //欠费维修费
	private float oweTotal; //欠费合计
	private float saTotal1; //本期应收款合计
	private float saTotal2; //前期加本期应收合计
	
	private String company;// 所属公司:宝城、广发
	private Long motorcadeId;// 车队ID
	private String motorcadeName;// 车队名称
	private Long carId;// 车辆ID
	private String carPlate;// 车牌号码
	private Long payerId;// 缴费人ID
	private String payerName;// 缴费人姓名
	private String description;// 备注
	private Set<FeeDetail> feeDetail;// 承包费明细
	

	@Column(name = "FEE_YEAR")
	public Integer getFeeYear() {
		return feeYear;
	}

	public void setFeeYear(Integer feeYear) {
		this.feeYear = feeYear;
	}

	@Column(name = "FEE_MONTH")
	public Integer getFeeMonth() {
		return feeMonth;
	}

	public void setFeeMonth(Integer feeMonth) {
		this.feeMonth = feeMonth;
	}

	@Column(name = "FEE_DATE")
	public Calendar getFeeDate() {
		return feeDate;
	}

	public void setFeeDate(Calendar feeDate) {
		this.feeDate = feeDate;
	}
	
	@Column(name = "COLLECTION_WAY")
	public String getCollectionWay() {
		return collectionWay;
	}


	public void setCollectionWay(String collectionWay) {
		this.collectionWay = collectionWay;
	}

	@Column(name = "S_SUBTOTAL")
	public float getShouldSubtotal() {
		return shouldSubtotal;
	}

	public void setShouldSubtotal(float shouldSubtotal) {
		this.shouldSubtotal = shouldSubtotal;
	}

	@Column(name = "S_UPKEEP")
	public float getShouldUpkeep() {
		return shouldUpkeep;
	}

	public void setShouldUpkeep(float shouldUpkeep) {
		this.shouldUpkeep = shouldUpkeep;
	}
	
	@Column(name = "S_TOTAL")
	public float getShouldTotal() {
		return shouldTotal;
	}

	public void setShouldTotal(float shouldTotal) {
		this.shouldTotal = shouldTotal;
	}

	@Column(name = "A_SUBTOTAL")
	public float getAdjustSubtotal() {
		return adjustSubtotal;
	}

	public void setAdjustSubtotal(float adjustSubtotal) {
		this.adjustSubtotal = adjustSubtotal;
	}

	@Column(name = "A_UPKEEP")
	public float getAdjustUpkeep() {
		return adjustUpkeep;
	}

	public void setAdjustUpkeep(float adjustUpkeep) {
		this.adjustUpkeep = adjustUpkeep;
	}
	
	@Column(name = "A_TOTAL")
	public float getAdjustTotal() {
		return adjustTotal;
	}

	public void setAdjustTotal(float adjustTotal) {
		this.adjustTotal = adjustTotal;
	}


	@Column(name = "R_SUBTOTAL")
	public float getRealSubtotal() {
		return realSubtotal;
	}

	public void setRealSubtotal(float realSubtotal) {
		this.realSubtotal = realSubtotal;
	}

	@Column(name = "R_UPKEEP")
	public float getRealUpkeep() {
		return realUpkeep;
	}

	public void setRealUpkeep(float realUpkeep) {
		this.realUpkeep = realUpkeep;
	}

	@Column(name = "R_TOTAL")
	public float getRealTotal() {
		return realTotal;
	}

	public void setRealTotal(float realTotal) {
		this.realTotal = realTotal;
	}

	@Column(name = "O_SUBTOTAL")
	public float getOweSubtotal() {
		return oweSubtotal;
	}

	public void setOweSubtotal(float oweSubtotal) {
		this.oweSubtotal = oweSubtotal;
	}

	@Column(name = "O_UPKEEP")
	public float getOweUpkeep() {
		return oweUpkeep;
	}

	public void setOweUpkeep(float oweUpkeep) {
		this.oweUpkeep = oweUpkeep;
	}

	@Column(name = "O_TOTAL")
	public float getOweTotal() {
		return oweTotal;
	}

	public void setOweTotal(float oweTotal) {
		this.oweTotal = oweTotal;
	}
	
	@Column(name = "SA_TOTAL1")
	public float getSaTotal1() {
		return saTotal1;
	}

	public void setSaTotal1(float saTotal1) {
		this.saTotal1 = saTotal1;
	}

	@Column(name = "SA_TOTAL2")
	public float getSaTotal2() {
		return saTotal2;
	}

	public void setSaTotal2(float saTotal2) {
		this.saTotal2 = saTotal2;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
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

	@Column(name = "PAYER_ID")
	public Long getPayerId() {
		return payerId;
	}

	public void setPayerId(Long payerId) {
		this.payerId = payerId;
	}

	@Column(name = "PAYER_NAME")
	public String getPayerName() {
		return payerName;
	}

	public void setPayerName(String payerName) {
		this.payerName = payerName;
	}

	@Column(name = "DESC_")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Transient
	public Set<FeeDetail> getFeeRealDetail() {//本期实收明细
		Set<FeeDetail> feeDetail = this.feeDetail;
		Set<FeeDetail> feeRealDetail = new LinkedHashSet<FeeDetail>();
		if(null != feeDetail && feeDetail.size() > 0){
			for(FeeDetail obj : feeDetail){
				if(FeeDetail.TYPE_REAL == obj.getFeeType()){
					feeRealDetail.add(obj);
				}
			}
		}
		return feeRealDetail;
	}
	
	@Transient
	public Set<FeeDetail> getFeeOweDetail() {//本期欠费明细
		Set<FeeDetail> feeDetail = this.feeDetail;
		Set<FeeDetail> feeOweDetail = new LinkedHashSet<FeeDetail>();
		if(null != feeDetail && feeDetail.size() > 0){
			for(FeeDetail obj : feeDetail){
				if(FeeDetail.TYPE_OWE == obj.getFeeType()){
					feeOweDetail.add(obj);
				}
			}
		}
		return feeOweDetail;
	}
	
	@OneToMany(mappedBy = "fee", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	public Set<FeeDetail> getFeeDetail() {
		return feeDetail;
	}
	
	public void setFeeDetail(Set<FeeDetail> feeDetail) {
		this.feeDetail = feeDetail;
	}

}