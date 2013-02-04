/**
 * 
 */
package cn.bc.business.runcase.domain;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import cn.bc.identity.domain.RichFileEntityImpl;

/**
 * 有关车辆、司机相关事件的基类
 * 
 * @author dragon
 */
@Entity
@Table(name = "BS_CASE_BASE")
@Inheritance(strategy = InheritanceType.JOINED)
public class CaseBase extends RichFileEntityImpl {
	private static final long serialVersionUID = 1L;
	public static final String ATTACH_TYPE = CaseBase.class.getSimpleName();

	/** 事件状态：在案 */
	public static final int STATUS_ACTIVE = 0;
	/** 事件状态：已结案 */
	public static final int STATUS_CLOSED = 1;
	/** 事件状态：处理中 */
	public static final int STATUS_HANDLING = 2;

	/** 事件类型：表扬 */
	public static final int TYPE_PRAISE = 1;
	/** 事件类型：客管投诉 */
	public static final int TYPE_COMPLAIN = 2;
	/** 事件类型：营运违章 */
	public static final int TYPE_INFRACT_BUSINESS = 3;
	/** 事件类型：交通违法 */
	public static final int TYPE_INFRACT_TRAFFIC = 4;
	/** 事件类型：事故 */
	public static final int TYPE_ACCIDENT = 5;
	/** 事件类型：公司投诉 */
	public static final int TYPE_COMPANY_COMPLAIN = 6;
	/** 事件类型：报失 */
	public static final int TYPE_LOST = 7;

	/** 事件来源：自建 */
	public static final int SOURCE_SYS = 0;
	/** 事件来源：接口 */
	public static final int SOURCE_SYNC = 1;
	/** 事件来源：生成 */
	public static final int SOURCE_GENERATION = 2;

	private String subject;// 标题：事件的简要描述
	private int type;// 事件类型：参考常数TYPE_XXXX的定义
	private int source = SOURCE_SYS;// 来源：参考常数SOURCE_xxxx的定义
	private String from; // 信息来源(用户填写)
	private String caseNo;// 案号
	private String code;// 自编号
	private String address;// 事发地点
	private Calendar happenDate;// 事发时间
	private Calendar closeDate;// 结案日期
	private Long closerId;// 结案人ID(ActorHistory的ID)
	private String closerName;// 结案人姓名
	private String description;// 备注、详细内容

	private String company;// 所属公司:宝城、广发
	private Long motorcadeId;// 车队ID
	private String motorcadeName;// 车队名称
	private Long carId;// 车辆ID
	private String carPlate;// 车牌号码
	private Long driverId;// 司机ID
	private String driverName;// 司机名称
	private String driverCert;// 司机服务资格证

	private String syncUid;// 旧数据同步的UID
	private Long syncId;// 数据同步ID

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	@Column(name = "CASE_NO")
	public String getCaseNo() {
		return caseNo;
	}

	public void setCaseNo(String caseNo) {
		this.caseNo = caseNo;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	@Column(name = "TYPE_")
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getSource() {
		return source;
	}

	public void setSource(int source) {
		this.source = source;
	}

	@Column(name = "FROM_")
	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Column(name = "HAPPEN_DATE")
	public Calendar getHappenDate() {
		return happenDate;
	}

	public void setHappenDate(Calendar happenDate) {
		this.happenDate = happenDate;
	}

	@Column(name = "CLOSE_DATE")
	public Calendar getCloseDate() {
		return closeDate;
	}

	public void setCloseDate(Calendar closeDate) {
		this.closeDate = closeDate;
	}

	@Column(name = "CLOSER_ID")
	public Long getCloserId() {
		return closerId;
	}

	public void setCloserId(Long closerId) {
		this.closerId = closerId;
	}

	@Column(name = "CLOSER_NAME")
	public String getCloserName() {
		return closerName;
	}

	public void setCloserName(String closerName) {
		this.closerName = closerName;
	}

	@Column(name = "DESC_")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	@Column(name = "DRIVER_CERT")
	public String getDriverCert() {
		return driverCert;
	}

	public void setDriverCert(String driverCert) {
		this.driverCert = driverCert;
	}

	@Column(name = "SYNC_ID")
	public Long getSyncId() {
		return syncId;
	}

	public void setSyncId(Long syncId) {
		this.syncId = syncId;
	}

	@Column(name = "SYNC_UID")
	public String getSyncUid() {
		return syncUid;
	}

	public void setSyncUid(String syncUid) {
		this.syncUid = syncUid;
	}

}