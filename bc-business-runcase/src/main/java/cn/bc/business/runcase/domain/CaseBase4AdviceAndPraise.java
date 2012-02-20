/**
 * 
 */
package cn.bc.business.runcase.domain;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 投诉、建议与表扬
 * 
 * @author dragon
 */
@MappedSuperclass
public class CaseBase4AdviceAndPraise extends CaseBase {
	private static final long serialVersionUID = 1L;
	private static Log logger = LogFactory.getLog(CaseBase4AdviceAndPraise.class);
	public static final String ATTACH_TYPE = CaseBase4AdviceAndPraise.class
			.getSimpleName();
	
	/** 性别:未定义 */
	public static final int SEX_NONE = 0;
	/** 性别:男 */
	public static final int SEX_MAN = 1;
	/** 性别:女 */
	public static final int SEX_WOMAN = 2;
	
	private Calendar receiveDate;// 接诉时间
	private String subject2;// 投诉建议项目小类(大类使用基类的subject)
	private String detail;// 投诉内容
	private String carColor;// 车色
	//private int driverSex;// 司机性别:参考 ActorDetail 类 SEX_XXX 常数的定义
	private String driverFeature;// 司机特征
	private String advisorName;// 提诉人姓名
	//private int advisorSex;// 提诉人性别
	private Integer advisorAge;// 提诉人年龄
	private String advisorPhone;// 提诉人电话
	private String advisorCert;// 提诉人证件号

	private Calendar noticeDate;// 通知时间
	private String result;// 处理结果

	private String ticket;// 车票号码
	private float machinePrice;// 计费器显示价格
	private float charge;// 实际收费
	private Calendar ridingStartTime;// 乘车起始时间
	private Calendar ridingEndTime;// 乘车结束时间
	private String pathFrom;// 乘车路线(从)
	private String pathTo;// 乘车路线(到)
	private String path;// 乘车路线
	private Integer passengerManCount;// 乘车人数(男)
	private Integer passengerWomanCount;// 乘车人数(女)
	private Integer passengerChildCount;// 乘车人数(童)
	private Integer passengerCount;// 乘车人数

	@Column(name = "RECEIVE_DATE")
	public Calendar getReceiveDate() {
		return receiveDate;
	}

	public void setReceiveDate(Calendar receiveDate) {
		this.receiveDate = receiveDate;
	}


	public String getSubject2() {
		return subject2;
	}

	public void setSubject2(String item2) {
		this.subject2 = item2;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	@Column(name = "CAR_COLOR")
	public String getCarColor() {
		return carColor;
	}

	public void setCarColor(String carColor) {
		this.carColor = carColor;
	}

//	@Column(name = "DRIVER_SEX")
//	public int getDriverSex() {
//		return driverSex;
//	}
//
//	public void setDriverSex(int driverSex) {
//		this.driverSex = driverSex;
//	}
	
	/**
	 * @return 司机性别(0-未设置,1-男,2-女)
	 */
	@Column(name = "DRIVER_SEX")
	public Integer getDriverSex() {
		Integer i = getInteger("driverSex");
		if (i == null)
			return 1;
		else
			return i.intValue();
	}

	public void setDriverSex(Integer sex) {
		set("driverSex", new Integer(sex));
	}
	
	@Transient
	private Map<String, Object> attrs;

	/**
	 * @param key
	 *            键
	 * @return 指定键的属性值
	 */
	@Transient
	public Object get(String key) {
		return (attrs != null && attrs.containsKey(key)) ? attrs.get(key)
				: null;
	}

	public void set(String key, Object value) {
		if (logger.isDebugEnabled())
			logger.debug("key=" + key + ";value=" + value + ";valueType="
					+ (value != null ? value.getClass() : "?"));
		if (key == null)
			throw new RuntimeException("key can't to be null");
		if (attrs == null)
			attrs = new HashMap<String, Object>();
		attrs.put(key, value);
	}

	@Transient
	public Integer getInteger(String key) {
		return (Integer) get(key);
	}

	@Column(name = "DRIVER_FEATURE")
	public String getDriverFeature() {
		return driverFeature;
	}

	public void setDriverFeature(String driverFeature) {
		this.driverFeature = driverFeature;
	}

	@Column(name = "ADVISOR_NAME")
	public String getAdvisorName() {
		return advisorName;
	}

	public void setAdvisorName(String advisorName) {
		this.advisorName = advisorName;
	}

//	@Column(name = "ADVISOR_SEX")
//	public int getAdvisorSex() {
//		return advisorSex;
//	}
//
//	public void setAdvisorSex(int advisorSex) {
//		this.advisorSex = advisorSex;
//	}
	
	/**
	 * @return 	提诉人性别(0-未设置,1-男,2-女)
	 */
	@Column(name = "ADVISOR_SEX")
	public Integer getAdvisorSex() {
		Integer i = getInteger("advisorSex");
		if (i == null)
			return 1;
		else
			return i.intValue();
	}

	public void setAdvisorSex(Integer sex) {
		set("advisorSex", new Integer(sex));
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

	@Column(name = "NOTICE_DATE")
	public Calendar getNoticeDate() {
		return noticeDate;
	}

	public void setNoticeDate(Calendar noticeDate) {
		this.noticeDate = noticeDate;
	}

	@Column(name = "RESULT_")
	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	@Column(name = "MACHINE_PRICE")
	public float getMachinePrice() {
		return machinePrice;
	}

	public void setMachinePrice(float machinePrice) {
		this.machinePrice = machinePrice;
	}

	public float getCharge() {
		return charge;
	}

	public void setCharge(float charge) {
		this.charge = charge;
	}

	@Column(name = "RIDING_TIME_START")
	public Calendar getRidingStartTime() {
		return ridingStartTime;
	}

	public void setRidingStartTime(Calendar ridingStartTime) {
		this.ridingStartTime = ridingStartTime;
	}

	@Column(name = "RIDING_TIME_END")
	public Calendar getRidingEndTime() {
		return ridingEndTime;
	}

	public void setRidingEndTime(Calendar ridingEndTime) {
		this.ridingEndTime = ridingEndTime;
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

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Column(name = "PASSENGER_COUNT_MAN")
	public Integer getPassengerManCount() {
		return passengerManCount;
	}

	public void setPassengerManCount(Integer passengerManCount) {
		this.passengerManCount = passengerManCount;
	}

	@Column(name = "PASSENGER_COUNT_WOMAN")
	public Integer getPassengerWomanCount() {
		return passengerWomanCount;
	}

	public void setPassengerWomanCount(Integer passengerWomanCount) {
		this.passengerWomanCount = passengerWomanCount;
	}

	@Column(name = "PASSENGER_COUNT_CHILD")
	public Integer getPassengerChildCount() {
		return passengerChildCount;
	}

	public void setPassengerChildCount(Integer passengerChildCount) {
		this.passengerChildCount = passengerChildCount;
	}

	@Column(name = "PASSENGER_COUNT")
	public Integer getPassengerCount() {
		return passengerCount;
	}

	public void setPassengerCount(Integer passengerCount) {
		this.passengerCount = passengerCount;
	}
}