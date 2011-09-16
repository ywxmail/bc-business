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
	public static final String ATTACH_TYPE = Case4Accident.class
			.getSimpleName();

	private Calendar receiveDate;// 受理时间
	private Long receiverId;// 经办人ID(对应ActorHistory的ID)
	private String receiverName;// 经办人姓名
	private String receiveCode;// 受理编号
	private String department;// 处理部门

	private Long chargerId;// 负责人ID(对应ActorHistory的ID)
	private String chargerName;// 负责人姓名

	private String duty;// 责任
	private String sort;// 性质
	private String weather;// 天气
	private String driverArea;// 司机区域
	private String driverType;// 司机类型：车主等
	private String driverClasses;// 驾驶状态：如正班等
	private String insuranceCompany;// 保险公司
	private String insuranceInfo;// 相关保单信息
	private boolean close;// 是否结案
	private boolean deliver;// 是否送保
	private boolean claim;// 是否保险公司赔付
	private boolean pay;// 是否司机受款

	private String casualties;// 伤亡情况
	private String carHurt;// 车损情况
	private String thirdParty;// 第三者情况
	private boolean rob;// 是否抢劫：1-是
	private String hurtCount;// 受伤人数
	private String deadCount;// 死亡人数
	private String actualLoss;// 实际损失
	private boolean innerFix;// 是否修理厂内修
	private String fixCost;// 修理厂内修金额
	private String costDetail;// 损失明细

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
	public String getDriverArea() {
		return driverArea;
	}

	public void setDriverArea(String driverArea) {
		this.driverArea = driverArea;
	}

	@Column(name = "DRIVER_TYPE")
	public String getDriverType() {
		return driverType;
	}

	public void setDriverType(String driverType) {
		this.driverType = driverType;
	}

	@Column(name = "DRIVER_CLASSES")
	public String getDriverClasses() {
		return driverClasses;
	}

	public void setDriverClasses(String driverClasses) {
		this.driverClasses = driverClasses;
	}

	@Column(name = "INSURANCE_COMPANY")
	public String getInsuranceCompany() {
		return insuranceCompany;
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

	@Column(name = "IS_CLOSE")
	public boolean isClose() {
		return close;
	}

	public void setClose(boolean close) {
		this.close = close;
	}

	@Column(name = "IS_DELIVER")
	public boolean isDeliver() {
		return deliver;
	}

	public void setDeliver(boolean deliver) {
		this.deliver = deliver;
	}

	@Column(name = "IS_CLAIM")
	public boolean isClaim() {
		return claim;
	}

	public void setClaim(boolean claim) {
		this.claim = claim;
	}

	@Column(name = "IS_PAY")
	public boolean isPay() {
		return pay;
	}

	public void setPay(boolean pay) {
		this.pay = pay;
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
	public String getHurtCount() {
		return hurtCount;
	}

	public void setHurtCount(String hurtCount) {
		this.hurtCount = hurtCount;
	}

	@Column(name = "DEAD_COUNT")
	public String getDeadCount() {
		return deadCount;
	}

	public void setDeadCount(String deadCount) {
		this.deadCount = deadCount;
	}

	@Column(name = "ACTUAL_LOSS")
	public String getActualLoss() {
		return actualLoss;
	}

	public void setActualLoss(String actualLoss) {
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
	public String getFixCost() {
		return fixCost;
	}

	public void setFixCost(String fixCost) {
		this.fixCost = fixCost;
	}

	@Column(name = "COST_DETAIL")
	public String getCostDetail() {
		return costDetail;
	}

	public void setCostDetail(String costDetail) {
		this.costDetail = costDetail;
	}
}