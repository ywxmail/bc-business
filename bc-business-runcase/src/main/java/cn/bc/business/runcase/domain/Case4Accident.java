/**
 * 
 */
package cn.bc.business.runcase.domain;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 事故理赔
 * 
 * @author dragon
 */
@Entity
@Table(name = "BS_CASE_ACCIDENT")
public class Case4Accident extends CaseBase {
	private static final long serialVersionUID = 1L;
	public final String ATTACH_TYPE = Case4Accident.class
			.getSimpleName();
	public static final String KEY_CODE = "runcase.code";
	
	private Calendar receiveDate;// 受理时间
	private Long receiverId;// 经办人ID(对应Actor的ID)
	private String receiverName;// 经办人姓名
	private String receiveCode;// 受理编号
	private String department;// 处理部门

	private Long chargerId;// 负责人ID(对应ActorHistory的ID)
	private String chargerName;// 负责人姓名

	private String duty;// 责任
	private String sort;// 性质
	private String weather;// 天气
	private int driverArea;// 司机区域
	private int driverType;// 司机类型：车主等
	private int driverClasses;// 驾驶状态：如正班等
	private String insuranceCompany;// 保险公司
	private String insuranceInfo;// 相关保单信息
	private boolean deliver;// 是否送保
	private Calendar deliverDate;// 送保日期
	private Float deliverMoney;// 送保金额
	private boolean claim;// 是否保险公司赔付
	private Calendar claimDate;// 保险公司赔付日期
	private Float claimMoney;// 保险公司赔付金额
	private String claimCode;// 保险公司赔付收据号
	private String claimNo1;// 商业险赔付号
	private String claimNo2;// 交强险赔付号
	private boolean pay;// 是否司机受款
	private Calendar payDate;// 司机受款日期
	private Float payMoney;// 司机受款金额
	private String payCode;// 司机受款收据号

	private String casualties;// 伤亡情况
	private String carHurt;// 车损情况
	private String thirdParty;// 第三者情况
	private boolean rob;// 是否抢劫：1-是
	private Long hurtCount;// 受伤人数
	private Long deadCount;// 死亡人数
	private Long actualLoss;// 实际损失
	private boolean innerFix;// 是否修理厂内修
	private Float fixCost;// 修理厂内修金额
	private String costDetail;// 损失明细
	
	private Float claimAmount;//出险金额
	private Float carWounding;//自车伤人金额
	private Float thirdWounding;//第三方伤人金额
	private Float medicalFee;//第三方医疗费用
	private String payDriver;//送保的受款司机
	private boolean deliverSecond;//是否有第二次的送保
	private boolean deliverTwo;//第二次送保里的是否送保
	private Calendar deliverDateTwo;//第二次送保里的送保日期
	private Float deliverMoneyTwo;//第二次送保里的送保金额
	
	private boolean claimTwo;//第二次送保里的是否保险公司赔付
	private Calendar claimDateTwo;//第二次送保里的保险公司赔付日期
	private Float claimMoneyTwo;//第二次送保里的保险公司赔付金额
	
	private boolean payTwo;//第二次送保里的是否司机受款
	private String payDriverTwo;//第二次送保里的受款司机
	private Calendar payDateTwo;//第二次送保里的司机受款日期
	private Float payMoneyTwo;//第二次送保里的司机受款金额

	private String origin;//司机籍贯
	private Float carmanCost;//拖车费用
	private Float thirdLoss;//第三者损失
	private Float thirdCost;//第三者拖车费用
	private Float agreementPayment;//协议赔付
	
	private Long payDriverId;//送保的受款司机ID
	private Long payDriverIdTwo;//第二次送保里的受款司机ID
	
	private String thirdLossInfo;//第三者损失情况
	
	@Column(name = "RECEIVE_DATE")
	public Calendar getReceiveDate() {
		return receiveDate;
	}

	public void setReceiveDate(Calendar receiveDate) {
		this.receiveDate = receiveDate;
	}

	@Column(name = "RECEIVER_ID")
	public Long getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(Long receiverId) {
		this.receiverId = receiverId;
	}

	@Column(name = "RECEIVER_NAME")
	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

	@Column(name = "RECEIVE_CODE")
	public String getReceiveCode() {
		return receiveCode;
	}

	public void setReceiveCode(String receiveCode) {
		this.receiveCode = receiveCode;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	@Column(name = "CHARGER_ID")
	public Long getChargerId() {
		return chargerId;
	}

	public void setChargerId(Long chargerId) {
		this.chargerId = chargerId;
	}

	@Column(name = "CHARGER_NAME")
	public String getChargerName() {
		return chargerName;
	}

	public void setChargerName(String chargerName) {
		this.chargerName = chargerName;
	}

	public String getDuty() {
		return duty;
	}

	public void setDuty(String duty) {
		this.duty = duty;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getWeather() {
		return weather;
	}

	public void setWeather(String weather) {
		this.weather = weather;
	}

	@Column(name = "DRIVER_AREA")
	public int getDriverArea() {
		return driverArea;
	}

	@Column(name = "DRIVER_TYPE")
	public int getDriverType() {
		return driverType;
	}

	public void setDriverType(int driverType) {
		this.driverType = driverType;
	}

	public void setDriverArea(int driverArea) {
		this.driverArea = driverArea;
	}

	@Column(name = "INSURANCE_COMPANY")
	public String getInsuranceCompany() {
		return insuranceCompany;
	}

	@Column(name = "DRIVER_CLASSES")
	public int getDriverClasses() {
		return driverClasses;
	}

	public void setDriverClasses(int driverClasses) {
		this.driverClasses = driverClasses;
	}

	public void setInsuranceCompany(String insuranceCompany) {
		this.insuranceCompany = insuranceCompany;
	}

	@Column(name = "INSURANCE_INFO")
	public String getInsuranceInfo() {
		return insuranceInfo;
	}

	public void setInsuranceInfo(String insuranceInfo) {
		this.insuranceInfo = insuranceInfo;
	}

	@Column(name = "IS_DELIVER")
	public boolean isDeliver() {
		return deliver;
	}

	public void setDeliver(boolean deliver) {
		this.deliver = deliver;
	}

	@Column(name = "DELIVER_DATE")
	public Calendar getDeliverDate() {
		return deliverDate;
	}

	public void setDeliverDate(Calendar deliverDate) {
		this.deliverDate = deliverDate;
	}

	@Column(name = "DELIVER_MONEY")
	public Float getDeliverMoney() {
		return deliverMoney;
	}

	public void setDeliverMoney(Float deliverMoney) {
		this.deliverMoney = deliverMoney;
	}

	@Column(name = "IS_CLAIM")
	public boolean isClaim() {
		return claim;
	}

	public void setClaim(boolean claim) {
		this.claim = claim;
	}

	@Column(name = "CLAIM_DATE")
	public Calendar getClaimDate() {
		return claimDate;
	}

	public void setClaimDate(Calendar claimDate) {
		this.claimDate = claimDate;
	}

	@Column(name = "CLAIM_MONEY")
	public Float getClaimMoney() {
		return claimMoney;
	}

	public void setClaimMoney(Float claimMoney) {
		this.claimMoney = claimMoney;
	}

	@Column(name = "CLAIM_CODE")
	public String getClaimCode() {
		return claimCode;
	}

	public void setClaimCode(String claimCode) {
		this.claimCode = claimCode;
	}

	@Column(name = "CLAIM_NO1")
	public String getClaimNo1() {
		return claimNo1;
	}

	public void setClaimNo1(String claimNo1) {
		this.claimNo1 = claimNo1;
	}

	@Column(name = "CLAIM_NO2")
	public String getClaimNo2() {
		return claimNo2;
	}

	public void setClaimNo2(String claimNo2) {
		this.claimNo2 = claimNo2;
	}

	@Column(name = "IS_PAY")
	public boolean isPay() {
		return pay;
	}

	public void setPay(boolean pay) {
		this.pay = pay;
	}

	@Column(name = "PAY_DATE")
	public Calendar getPayDate() {
		return payDate;
	}

	public void setPayDate(Calendar payDate) {
		this.payDate = payDate;
	}

	@Column(name = "PAY_MONEY")
	public Float getPayMoney() {
		return payMoney;
	}

	public void setPayMoney(Float payMoney) {
		this.payMoney = payMoney;
	}

	@Column(name = "PAY_CODE")
	public String getPayCode() {
		return payCode;
	}

	public void setPayCode(String payCode) {
		this.payCode = payCode;
	}

	@Column(name = "CASUALTIES")
	public String getCasualties() {
		return casualties;
	}

	public void setCasualties(String casualties) {
		this.casualties = casualties;
	}

	@Column(name = "CAR_HURT")
	public String getCarHurt() {
		return carHurt;
	}

	public void setCarHurt(String carHurt) {
		this.carHurt = carHurt;
	}

	@Column(name = "THIRD_PARTY")
	public String getThirdParty() {
		return thirdParty;
	}

	public void setThirdParty(String thirdParty) {
		this.thirdParty = thirdParty;
	}

	@Column(name = "IS_ROB")
	public boolean isRob() {
		return rob;
	}

	public void setRob(boolean rob) {
		this.rob = rob;
	}

	@Column(name = "HURT_COUNT")
	public Long getHurtCount() {
		return hurtCount;
	}

	public void setHurtCount(Long hurtCount) {
		this.hurtCount = hurtCount;
	}

	@Column(name = "DEAD_COUNT")
	public Long getDeadCount() {
		return deadCount;
	}

	public void setDeadCount(Long deadCount) {
		this.deadCount = deadCount;
	}

	@Column(name = "ACTUAL_LOSS")
	public Long getActualLoss() {
		return actualLoss;
	}

	public void setActualLoss(Long actualLoss) {
		this.actualLoss = actualLoss;
	}

	@Column(name = "IS_INNER_FIX")
	public boolean isInnerFix() {
		return innerFix;
	}

	public void setInnerFix(boolean innerFix) {
		this.innerFix = innerFix;
	}

	@Column(name = "FIX_COST")
	public Float getFixCost() {
		return fixCost;
	}

	public void setFixCost(Float fixCost) {
		this.fixCost = fixCost;
	}

	@Column(name = "COST_DETAIL")
	public String getCostDetail() {
		return costDetail;
	}

	public void setCostDetail(String costDetail) {
		this.costDetail = costDetail;
	}
	
	@Column(name="CLAIM_AMOUNT")
	public Float getClaimAmount() {
		return claimAmount;
	}

	public void setClaimAmount(Float claimAmount) {
		this.claimAmount = claimAmount;
	}
	@Column(name="CAR_WOUNDING")
	public Float getCarWounding() {
		return carWounding;
	}

	public void setCarWounding(Float carWounding) {
		this.carWounding = carWounding;
	}
	@Column(name="Medical_Fee")
	public Float getMedicalFee() {
		return medicalFee;
	}

	public void setMedicalFee(Float medicalFee) {
		this.medicalFee = medicalFee;
	}
	@Column(name="PAY_DRIVER")
	public String getPayDriver() {
		return payDriver;
	}

	public void setPayDriver(String payDriver) {
		this.payDriver = payDriver;
	}
	@Column(name="IS_DELIVER_SECOND")
	public boolean isDeliverSecond() {
		return deliverSecond;
	}

	public void setDeliverSecond(boolean deliverSecond) {
		this.deliverSecond = deliverSecond;
	}
	@Column(name="IS_DELIVER_TWO")
	public boolean isDeliverTwo() {
		return deliverTwo;
	}

	public void setDeliverTwo(boolean deliverTwo) {
		this.deliverTwo = deliverTwo;
	}
	@Column(name="DELIVER_DATE_TWO")
	public Calendar getDeliverDateTwo() {
		return deliverDateTwo;
	}

	public void setDeliverDateTwo(Calendar deliverDateTwo) {
		this.deliverDateTwo = deliverDateTwo;
	}
	@Column(name="DELIVER_MONEY_TWO")
	public Float getDeliverMoneyTwo() {
		return deliverMoneyTwo;
	}

	public void setDeliverMoneyTwo(Float deliverMoneyTwo) {
		this.deliverMoneyTwo = deliverMoneyTwo;
	}
	@Column(name="IS_CLAIM_TWO")
	public boolean isClaimTwo() {
		return claimTwo;
	}

	public void setClaimTwo(boolean claimTwo) {
		this.claimTwo = claimTwo;
	}
	@Column(name="CLAIM_DATE_TWO")
	public Calendar getClaimDateTwo() {
		return claimDateTwo;
	}

	public void setClaimDateTwo(Calendar claimDateTwo) {
		this.claimDateTwo = claimDateTwo;
	}
	@Column(name="CLAIM_MONEY_TWO")
	public Float getClaimMoneyTwo() {
		return claimMoneyTwo;
	}

	public void setClaimMoneyTwo(Float claimMoneyTwo) {
		this.claimMoneyTwo = claimMoneyTwo;
	}
	@Column(name="PAY_DRIVER_TWO")
	public String getPayDriverTwo() {
		return payDriverTwo;
	}

	public void setPayDriverTwo(String payDriverTwo) {
		this.payDriverTwo = payDriverTwo;
	}
	@Column(name="PAY_DATE_TWO")
	public Calendar getPayDateTwo() {
		return payDateTwo;
	}

	public void setPayDateTwo(Calendar payDateTwo) {
		this.payDateTwo = payDateTwo;
	}
	@Column(name="PAY_MONEY_TWO")
	public Float getPayMoneyTwo() {
		return payMoneyTwo;
	}

	public void setPayMoneyTwo(Float payMoneyTwo) {
		this.payMoneyTwo = payMoneyTwo;
	}

	@Column(name="IS_PAY_TWO")
	public boolean isPayTwo() {
		return payTwo;
	}

	public void setPayTwo(boolean payTwo) {
		this.payTwo = payTwo;
	}

	@Column(name="THIRD_WOUNDING")
	public Float getThirdWounding() {
		return thirdWounding;
	}

	public void setThirdWounding(Float thirdWounding) {
		this.thirdWounding = thirdWounding;
	}

	public String getOrigin() {
		return origin;
	}
	
	public void setOrigin(String origin) {
		this.origin = origin;
	}
	
	@Column(name="CARMAN_COST")
	public Float getCarmanCost() {
		return carmanCost;
	}

	public void setCarmanCost(Float carmanCost) {
		this.carmanCost = carmanCost;
	}
	
	@Column(name="THIRD_LOSS")
	public Float getThirdLoss() {
		return thirdLoss;
	}

	public void setThirdLoss(Float thirdLoss) {
		this.thirdLoss = thirdLoss;
	}
	
	@Column(name="THIRD_COST")
	public Float getThirdCost() {
		return thirdCost;
	}

	public void setThirdCost(Float thirdCost) {
		this.thirdCost = thirdCost;
	}

	@Column(name="AGREEMENT_PAYMENT")
	public Float getAgreementPayment() {
		return agreementPayment;
	}

	public void setAgreementPayment(Float agreementPayment) {
		this.agreementPayment = agreementPayment;
	}

	@Column(name="PAY_DRIVERID")
	public Long getPayDriverId() {
		return payDriverId;
	}

	public void setPayDriverId(Long payDriverId) {
		this.payDriverId = payDriverId;
	}
	@Column(name="PAY_DRIVERID_TWO")
	public Long getPayDriverIdTwo() {
		return payDriverIdTwo;
	}

	public void setPayDriverIdTwo(Long payDriverIdTwo) {
		this.payDriverIdTwo = payDriverIdTwo;
	}

	@Column(name="THIRD_LOSS_INFO")
	public String getThirdLossInfo() {
		return thirdLossInfo;
	}

	public void setThirdLossInfo(String thirdLossInfo) {
		this.thirdLossInfo = thirdLossInfo;
	}
	
	
	
}