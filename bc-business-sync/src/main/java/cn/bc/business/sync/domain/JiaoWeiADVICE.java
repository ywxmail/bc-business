/**
 * 
 */
package cn.bc.business.sync.domain;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import cn.bc.sync.domain.SyncBase;

/**
 * 从交委WebService接口同步的交通违章信息
 * 
 * @author dragon
 */
@Entity
@Table(name = "BS_SYNC_JIAOWEI_ADVICE")
public class JiaoWeiADVICE extends SyncBase {
	private static final long serialVersionUID = 1L;
	/** UUID的前缀，实际的uid使用KEY_UID + "-" + id */
	public static final String KEY_UID = SyncBase.class.getSimpleName();
	
	/** 区分接口同步的类型*/
	public static final String KEY_TYPE = JiaoWeiADVICE.class.getSimpleName();

	// 违章顺序号使用基类的SyncId字段记录，作为同类信息的唯一标识

	private String cId;
	private String receiveCode;//电话受理号
	private String advisorName;//投诉人姓名
	private String pathFrom;//乘车路线(从)
	private String pathTo;//乘车路线(到)
	private Calendar ridingTimeStart;//乘车起始时间
	private Calendar ridingTimeEnd;//乘车结束时间
	private String advisorSex;//投诉人性别
	private Integer advisorAge;//投诉人年龄
	private String advisorPhone;//投诉人电话
	private String advisorCert;//投诉人证件号
	private String oldUnitName;//车属单位
	private String carPlate;//车牌号码
	private String driverCert;//资格证号
	private String driverChar;//司机特征
	private String content;//投诉内容
	private Calendar receiveDate;//受理时间
	private String result;//处理结果
	private String adviceType;//投诉或建议
	private String adviceBs;//投诉行业
	private String subject;//投诉项目大类
	private String subject2;//投诉项目小类
	private String machinePrice;//计费器显示价格
	private String ticket;//车票号码
	private String charge;//实际收费
	private String driverSex;//司机性别
	private String suggestBs;//建议行业
	private String buslines;//公交线路或站场
	private String busColor;//车辆颜色
	private String reply;//是否回复
	
	@Column(name = "C_ID")
	public String getcId() {
		return cId;
	}
	public void setcId(String cId) {
		this.cId = cId;
	}
	@Column(name = "RECEIVE_CODE")
	public String getReceiveCode() {
		return receiveCode;
	}
	public void setReceiveCode(String receiveCode) {
		this.receiveCode = receiveCode;
	}
	@Column(name = "ADVISOR_NAME")
	public String getAdvisorName() {
		return advisorName;
	}
	public void setAdvisorName(String advisorName) {
		this.advisorName = advisorName;
	}
	@Column(name = "PATH_FROM")
	public String getPathFrom() {
		return pathFrom;
	}
	public void setPathFrom(String pathFrom) {
		this.pathFrom = pathFrom;
	}
	@Column(name = "PATH_TO")
	public String getPathTo() {
		return pathTo;
	}
	public void setPathTo(String pathTo) {
		this.pathTo = pathTo;
	}
	@Column(name = "RIDING_TIME_START")
	public Calendar getRidingTimeStart() {
		return ridingTimeStart;
	}
	public void setRidingTimeStart(Calendar ridingTimeStart) {
		this.ridingTimeStart = ridingTimeStart;
	}
	@Column(name = "RIDING_TIME_END")
	public Calendar getRidingTimeEnd() {
		return ridingTimeEnd;
	}
	public void setRidingTimeEnd(Calendar ridingTimeEnd) {
		this.ridingTimeEnd = ridingTimeEnd;
	}
	@Column(name = "ADVISOR_SEX")
	public String getAdvisorSex() {
		return advisorSex;
	}
	public void setAdvisorSex(String advisorSex) {
		this.advisorSex = advisorSex;
	}
	@Column(name = "ADVISOR_AGE")
	public Integer getAdvisorAge() {
		return advisorAge;
	}
	public void setAdvisorAge(Integer advisorAge) {
		this.advisorAge = advisorAge;
	}
	@Column(name = "ADVISOR_PHONE")
	public String getAdvisorPhone() {
		return advisorPhone;
	}
	public void setAdvisorPhone(String advisorPhone) {
		this.advisorPhone = advisorPhone;
	}
	@Column(name = "ADVISOR_CERT")
	public String getAdvisorCert() {
		return advisorCert;
	}
	public void setAdvisorCert(String advisorCert) {
		this.advisorCert = advisorCert;
	}
	@Column(name = "OLD_UNIT_NAME")
	public String getOldUnitName() {
		return oldUnitName;
	}
	public void setOldUnitName(String oldUnitName) {
		this.oldUnitName = oldUnitName;
	}
	@Column(name = "CAR_PLATE")
	public String getCarPlate() {
		return carPlate;
	}
	public void setCarPlate(String carPlate) {
		this.carPlate = carPlate;
	}
	@Column(name = "DRIVER_ID")
	public String getDriverCert() {
		return driverCert;
	}
	public void setDriverCert(String driverCert) {
		this.driverCert = driverCert;
	}
	@Column(name = "DRIVER_CHAR")
	public String getDriverChar() {
		return driverChar;
	}
	public void setDriverChar(String driverChar) {
		this.driverChar = driverChar;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	@Column(name = "RECEIVE_DATE")
	public Calendar getReceiveDate() {
		return receiveDate;
	}
	public void setReceiveDate(Calendar receiveDate) {
		this.receiveDate = receiveDate;
	}
	@Column(name = "RESULT_")
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	@Column(name = "ADVICE_TYPE")
	public String getAdviceType() {
		return adviceType;
	}
	public void setAdviceType(String adviceType) {
		this.adviceType = adviceType;
	}
	@Column(name = "ADVICE_BS")
	public String getAdviceBs() {
		return adviceBs;
	}
	public void setAdviceBs(String adviceBs) {
		this.adviceBs = adviceBs;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	@Column(name = "SUBJECT2")
	public String getSubject2() {
		return subject2;
	}
	public void setSubject2(String subject2) {
		this.subject2 = subject2;
	}
	@Column(name = "MACHINE_PRICE")
	public String getMachinePrice() {
		return machinePrice;
	}
	public void setMachinePrice(String machinePrice) {
		this.machinePrice = machinePrice;
	}
	public String getTicket() {
		return ticket;
	}
	public void setTicket(String ticket) {
		this.ticket = ticket;
	}
	public String getCharge() {
		return charge;
	}
	public void setCharge(String charge) {
		this.charge = charge;
	}
	@Column(name = "DRIVER_SEX")
	public String getDriverSex() {
		return driverSex;
	}
	public void setDriverSex(String driverSex) {
		this.driverSex = driverSex;
	}
	@Column(name = "SUGGEST_BS")
	public String getSuggestBs() {
		return suggestBs;
	}
	public void setSuggestBs(String suggestBs) {
		this.suggestBs = suggestBs;
	}
	public String getBuslines() {
		return buslines;
	}
	public void setBuslines(String buslines) {
		this.buslines = buslines;
	}
	@Column(name = "BUS_COLOR")
	public String getBusColor() {
		return busColor;
	}
	public void setBusColor(String busColor) {
		this.busColor = busColor;
	}
	public String getReply() {
		return reply;
	}
	public void setReply(String reply) {
		this.reply = reply;
	}
	

 
}