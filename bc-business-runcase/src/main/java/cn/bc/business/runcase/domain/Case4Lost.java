/**
 * 
 */
package cn.bc.business.runcase.domain;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 投诉与建议
 * 
 * @author dragon
 */
@Entity
@Table(name = "BS_CASE_LOST")
public class Case4Lost extends CaseBase {
	private static final long serialVersionUID = 1L;
	private static Log logger = LogFactory.getLog(Case4Lost.class);
	public static final String ATTACH_TYPE = Case4Lost.class.getSimpleName();
	public static final String KEY_CODE = "runcase.code";
		
	/** 性别:未定义 */
	public static final int SEX_NONE = 0;
	/** 性别:男 */
	public static final int SEX_MAN = 1;
	/** 性别:女 */
	public static final int SEX_WOMAN = 2;
	
	private Calendar receiveDate; //接报失时间
	private String ownerName; //报失人
	private String ownerTel; //报失人电话
	private String ownerUnit; //报失人单位
	private String path;// 乘车路线
	private Integer passengerCount;// 乘车人数
	private String driverFeature;// 司机特征
	private String ticket;// 车票号码
	private float machinePrice;// 计费器显示价格
	private float charge;// 实际收费
	private String desc2;// 备注2
	private float money;// 估算价值
	private String items;// 报失物品
	private Integer sitePostion; //遗失位置
	private Integer level; //级别
	private boolean took; //是否领取 true:为已领,false:未领
	private Calendar tookDate;//领取时间
	private String takerName; //领取人
	private Integer takerAge; //领取人年龄
	private String takerUnit; //领取人单位
	private String takerTel; //领取人电话
	private String takerIdentity; //领取人证件
	private Integer result; //失物去向
	private Calendar retrunDate;//交还日期
	private Calendar replyDate;//回复日期
	private Integer handleResult; //处理结果
	private Long transactorId;// 经办人ID(对应ActorHistory的ID)
	private String transactorName;// 经办人姓名

	@Column(name = "RECEIVE_DATE")
	public Calendar getReceiveDate() {
		return receiveDate;
	}

	public void setReceiveDate(Calendar receiveDate) {
		this.receiveDate = receiveDate;
	}

	@Column(name = "OWNER_NAME")
	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	@Column(name = "OWNER_TEL")
	public String getOwnerTel() {
		return ownerTel;
	}

	public void setOwnerTel(String ownerTel) {
		this.ownerTel = ownerTel;
	}

	@Column(name = "OWNER_UNIT")
	public String getOwnerUnit() {
		return ownerUnit;
	}

	public void setOwnerUnit(String ownerUnit) {
		this.ownerUnit = ownerUnit;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Column(name = "PASSENGER_COUNT")
	public Integer getPassengerCount() {
		return passengerCount;
	}

	public void setPassengerCount(Integer passengerCount) {
		this.passengerCount = passengerCount;
	}

	@Column(name = "DRIVER_FEATURE")
	public String getDriverFeature() {
		return driverFeature;
	}

	public void setDriverFeature(String driverFeature) {
		this.driverFeature = driverFeature;
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

	public String getDesc2() {
		return desc2;
	}

	public void setDesc2(String desc2) {
		this.desc2 = desc2;
	}

	public float getMoney() {
		return money;
	}

	public void setMoney(float money) {
		this.money = money;
	}

	public String getItems() {
		return items;
	}

	public void setItems(String items) {
		this.items = items;
	}

	@Column(name = "SITE_POSTION")
	public Integer getSitePostion() {
		return sitePostion;
	}

	public void setSitePostion(Integer sitePostion) {
		this.sitePostion = sitePostion;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	@Column(name = "IS_TOOK")
	public boolean isTook() {
		return took;
	}

	public void setTook(boolean took) {
		this.took = took;
	}

	@Column(name = "TOOK_DATE")
	public Calendar getTookDate() {
		return tookDate;
	}

	public void setTookDate(Calendar tookDate) {
		this.tookDate = tookDate;
	}

	@Column(name = "TAKER_NAME")
	public String getTakerName() {
		return takerName;
	}

	public void setTakerName(String takerName) {
		this.takerName = takerName;
	}
	
	@Column(name = "TAKER_AGE")
	public Integer getTakerAge() {
		return takerAge;
	}

	public void setTakerAge(Integer takerAge) {
		this.takerAge = takerAge;
	}

	@Column(name = "TAKER_UNIT")
	public String getTakerUnit() {
		return takerUnit;
	}

	public void setTakerUnit(String takerUnit) {
		this.takerUnit = takerUnit;
	}

	@Column(name = "TAKER_TEL")
	public String getTakerTel() {
		return takerTel;
	}

	public void setTakerTel(String takerTel) {
		this.takerTel = takerTel;
	}

	@Column(name = "TAKER_IDENTITY")
	public String getTakerIdentity() {
		return takerIdentity;
	}

	public void setTakerIdentity(String takerIdentity) {
		this.takerIdentity = takerIdentity;
	}
	
	@Column(name = "result_")
	public Integer getResult() {
		return result;
	}

	public void setResult(Integer result) {
		this.result = result;
	}

	@Column(name = "RETRUN_DATE")
	public Calendar getRetrunDate() {
		return retrunDate;
	}

	public void setRetrunDate(Calendar retrunDate) {
		this.retrunDate = retrunDate;
	}

	@Column(name = "REPLY_DATE")
	public Calendar getReplyDate() {
		return replyDate;
	}

	public void setReplyDate(Calendar replyDate) {
		this.replyDate = replyDate;
	}

	@Column(name = "HANDLE_RESULT")
	public Integer getHandleResult() {
		return handleResult;
	}

	public void setHandleResult(Integer handleResult) {
		this.handleResult = handleResult;
	}

	@Column(name = "TRANSACTOR_ID")
	public Long getTransactorId() {
		return transactorId;
	}

	public void setTransactorId(Long transactorId) {
		this.transactorId = transactorId;
	}

	@Column(name = "TRANSACTOR_NAME")
	public String getTransactorName() {
		return transactorName;
	}

	public void setTransactorName(String transactorName) {
		this.transactorName = transactorName;
	}

	//####   有关性别设置代码开始     #####
	
	/**
	 * @return 报失人性别(0-未设置,1-男,2-女)
	 */
	@Column(name = "OWNER_SEX")
	public Integer getOwnerSex() {
		Integer i = getInteger("ownerSex");
		if (i == null)
			return 1;
		else
			return i.intValue();
	}

	public void setOwnerSex(Integer sex) {
		set("ownerSex", new Integer(sex));
	}
	
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
	
	/**
	 * @return 领取人性别(0-未设置,1-男,2-女)
	 */
	@Column(name = "TAKER_SEX")
	public Integer getTakerSex() {
		Integer i = getInteger("takerSex");
		if (i == null)
			return 1;
		else
			return i.intValue();
	}

	public void setTakerSex(Integer sex) {
		set("takerSex", new Integer(sex));
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

	//####   有关性别设置代码结束    #####
	
	
}