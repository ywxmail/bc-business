/**
 * 
 */
package cn.bc.business.spider.domain;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import cn.bc.sync.domain.SyncBase;

/**
 * 从金盾网抓取的交通违法信息
 * 
 * @author dragon
 */
@Entity
@Table(name = "BS_SYNC_JINDUN_JTWF")
public class JinDunJTWF extends SyncBase {
	private static final long serialVersionUID = 1L;
	/** UUID的前缀，实际的uid使用KEY_UID + "-" + id */
	public static final String KEY_UID = SyncBase.class.getSimpleName();

	// "处理状态"使用基类的status字段记录；"违法序号"使用基类的syncCode字段记录，作为同类信息的唯一标识

	// 请求发出的参数
	private String carType;// 号牌种类
	private String carTypeDesc;// 号牌种类的中文描述
	private String carPlateType;// 车牌归属，如“粤A”
	private String carPlateNo;// 车牌号码，如“C4X74”
	private String engineNo;// 发动机号

	// 抓取到的参数
	private String decisionNo;// 决定书编号
	private String decisionType;// 决定书类别
	private Calendar happenDate;// 违法时间
	private String address;// 违法地点
	private String source;// 违法来源
	private String driverName;// 当事人
	private Float jeom;// 违法记分数
	private Float penalty;// 罚款金额
	private Float overduePayment;// 滞纳金
	private String traffic;// 交通方式
	private String breakType;// 违法行为

	@Column(name = "HAPPEN_DATE")
	public Calendar getHappenDate() {
		return happenDate;
	}

	public void setHappenDate(Calendar happenDate) {
		this.happenDate = happenDate;
	}
	
	@Column(name = "CAR_TYPE")
	public String getCarType() {
		return carType;
	}
	
	public void setCarType(String carType) {
		this.carType = carType;
	}

	@Column(name = "CAR_TYPE_DESC")
	public String getCarTypeDesc() {
		return carTypeDesc;
	}

	public void setCarTypeDesc(String carTypeDesc) {
		this.carTypeDesc = carTypeDesc;
	}

	@Column(name = "CAR_PLATE_TYPE")
	public String getCarPlateType() {
		return carPlateType;
	}
	
	public void setCarPlateType(String carPlateType) {
		this.carPlateType = carPlateType;
	}
	
	@Column(name = "CAR_PLATE_NO")
	public String getCarPlateNo() {
		return carPlateNo;
	}

	public void setCarPlateNo(String carPlateNo) {
		this.carPlateNo = carPlateNo;
	}

	@Transient
	public String getCarPlate() {
		return this.carPlateType + "." + this.carPlateNo;
	}

	@Column(name = "ENGINE_NO")
	public String getEngineNo() {
		return engineNo;
	}
	
	public void setEngineNo(String engineNo) {
		this.engineNo = engineNo;
	}

	@Column(name = "DRIVER_NAME")
	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public Float getJeom() {
		return jeom;
	}

	public void setJeom(Float jeom) {
		this.jeom = jeom;
	}

	@Column(name = "DECISION_NO")
	public String getDecisionNo() {
		return decisionNo;
	}

	public void setDecisionNo(String decisionNo) {
		this.decisionNo = decisionNo;
	}

	@Column(name = "DECISION_TYPE")
	public String getDecisionType() {
		return decisionType;
	}

	public void setDecisionType(String decisionType) {
		this.decisionType = decisionType;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Float getPenalty() {
		return penalty;
	}

	public void setPenalty(Float penalty) {
		this.penalty = penalty;
	}

	@Column(name = "OVERDUE_PAYMENT")
	public Float getOverduePayment() {
		return overduePayment;
	}

	public void setOverduePayment(Float overduePayment) {
		this.overduePayment = overduePayment;
	}

	public String getTraffic() {
		return traffic;
	}

	public void setTraffic(String traffic) {
		this.traffic = traffic;
	}

	@Column(name = "BREAK_TYPE")
	public String getBreakType() {
		return breakType;
	}

	public void setBreakType(String breakType) {
		this.breakType = breakType;
	}
}