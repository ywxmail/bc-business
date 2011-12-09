/**
 * 
 */
package cn.bc.business.carman.domain;

import java.util.Calendar;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import cn.bc.business.cert.domain.Cert;
import cn.bc.business.contract.domain.Contract;
import cn.bc.identity.domain.RichFileEntityImpl;

/**
 * 司机责任人
 * 
 * @author dragon
 */
@Entity
@Table(name = "BS_CARMAN")
public class CarMan extends RichFileEntityImpl {
	private static final long serialVersionUID = 1L;
	public static final String KEY_UID = "carMan.uid";
	public static final String KEY_CODE = "carMan.code";
	/** 类别：司机 */
	public static final int TYPE_DRIVER = 0;
	/** 类别：责任人 */
	public static final int TYPE_CHARGER = 1;
	/** 类别：司机和责任人 */
	public static final int TYPE_DRIVER_AND_CHARGER = 2;

	private int type;// 类别
	private String orderNo;// 排序号
	private String name;// 姓名
	private int sex;// 性别：参考ActorDetail类中SEX_*常数的定义
	private String origin;// 籍贯
	private int region;// 区域
	private String houseType;// 户口性质

	private Calendar birthdate;// 出生日期
	private Calendar workDate;// 入职日期
	private String formerUnit;// 入职原单位
	private String address;// 身份证住址
	private String address1;// 暂住地址
	private String phone;// 电话1
	private String phone1;// 电话2
	private String description;// 备注
	private String level;// 等级

	// 历史遗留
	private String model;// 准驾车型
	private String cert4Indentity;// 身份证号
	private String cert4Driving;// 驾驶证号
	private Calendar cert4DrivingFirstDate;// 初次领驾驶证日期
	private Calendar cert4DrivingStartDate;// 驾驶证起效日期
	private Calendar cert4DrivingEndDate;// 驾驶证过期日期
	private String cert4DrivingArchive;// 驾驶证档案号
	private String cert4FWZG;// 服务资格证号
	private String cert4FWZGID;// 服务资格ID
	private String cert4CYZG;// 从业资格证号
	private int drivingStatus;// 驾驶状态
	private String oldUnitName;// 分支机构：用于历史数据的保存
	private String extZRR;// 责任人：用于历史数据的保存
	private boolean gz;// 驾驶证是否广州:0-否,1-是
	private String accessCerts;// 已考取证件：历史数据保存

	private Set<Cert> certs;// 拥有的证件
	private Set<Contract> contracts;// 合同

	@Column(name = "CERT_FWZG_ID")
	public String getCert4FWZGID() {
		return cert4FWZGID;
	}

	public void setCert4FWZGID(String cert4fwzgid) {
		cert4FWZGID = cert4fwzgid;
	}

	@Column(name = "DESC_")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@OneToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "BS_CARMAN_CERT", joinColumns = @JoinColumn(name = "MAN_ID", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "CERT_ID", referencedColumnName = "ID"))
	@OrderBy("certCode asc")
	public Set<Cert> getCerts() {
		return certs;
	}

	public void setCerts(Set<Cert> certs) {
		this.certs = certs;
	}

	@OneToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "BS_CARMAN_CONTRACT", joinColumns = @JoinColumn(name = "MAN_ID", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "CONTRACT_ID", referencedColumnName = "ID"))
	@OrderBy("fileDate desc")
	public Set<Contract> getContracts() {
		return contracts;
	}

	public void setContracts(Set<Contract> contracts) {
		this.contracts = contracts;
	}

	@Column(name = "ORDER_")
	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "WORK_DATE")
	public Calendar getWorkDate() {
		return workDate;
	}

	public void setWorkDate(Calendar workDate) {
		this.workDate = workDate;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	@Column(name = "REGION_")
	public int getRegion() {
		return region;
	}

	public void setRegion(int region) {
		this.region = region;
	}

	@Column(name = "HOUSE_TYPE")
	public String getHouseType() {
		return houseType;
	}

	public void setHouseType(String houseType) {
		this.houseType = houseType;
	}

	public Calendar getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(Calendar birthdate) {
		this.birthdate = birthdate;
	}

	@Column(name = "FORMER_UNIT")
	public String getFormerUnit() {
		return formerUnit;
	}

	public void setFormerUnit(String formerUnit) {
		this.formerUnit = formerUnit;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPhone1() {
		return phone1;
	}

	public void setPhone1(String phone1) {
		this.phone1 = phone1;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	@Column(name = "TYPE_")
	public int getType() {
		return type;
	}

	public int getSex() {
		return sex;
	}

	@Column(name = "MODEL_")
	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	@Column(name = "CERT_IDENTITY")
	public String getCert4Indentity() {
		return cert4Indentity;
	}

	public void setCert4Indentity(String cert4Indentity) {
		this.cert4Indentity = cert4Indentity;
	}

	@Column(name = "CERT_DRIVING")
	public String getCert4Driving() {
		return cert4Driving;
	}

	public void setCert4Driving(String cert4Driving) {
		this.cert4Driving = cert4Driving;
	}

	@Column(name = "CERT_DRIVING_FIRST_DATE")
	public Calendar getCert4DrivingFirstDate() {
		return cert4DrivingFirstDate;
	}

	public void setCert4DrivingFirstDate(Calendar cert4DrivingFirstDate) {
		this.cert4DrivingFirstDate = cert4DrivingFirstDate;
	}

	@Column(name = "CERT_DRIVING_START_DATE")
	public Calendar getCert4DrivingStartDate() {
		return cert4DrivingStartDate;
	}

	public void setCert4DrivingStartDate(Calendar cert4DrivingStartDate) {
		this.cert4DrivingStartDate = cert4DrivingStartDate;
	}

	@Column(name = "CERT_DRIVING_END_DATE")
	public Calendar getCert4DrivingEndDate() {
		return cert4DrivingEndDate;
	}

	public void setCert4DrivingEndDate(Calendar cert4DrivingEndDate) {
		this.cert4DrivingEndDate = cert4DrivingEndDate;
	}

	@Column(name = "CERT_DRIVING_ARCHIVE")
	public String getCert4DrivingArchive() {
		return cert4DrivingArchive;
	}

	public void setCert4DrivingArchive(String cert4DrivingArchive) {
		this.cert4DrivingArchive = cert4DrivingArchive;
	}

	@Column(name = "CERT_FWZG")
	public String getCert4FWZG() {
		return cert4FWZG;
	}

	public void setCert4FWZG(String cert4fwzg) {
		cert4FWZG = cert4fwzg;
	}

	@Column(name = "CERT_CYZG")
	public String getCert4CYZG() {
		return cert4CYZG;
	}

	public void setCert4CYZG(String cert4cyzg) {
		cert4CYZG = cert4cyzg;
	}

	@Column(name = "DRIVING_STATUS")
	public int getDrivingStatus() {
		return drivingStatus;
	}

	public void setDrivingStatus(int drivingStatus) {
		this.drivingStatus = drivingStatus;
	}

	@Column(name = "OLD_UNIT_NAME")
	public String getOldUnitName() {
		return oldUnitName;
	}

	public void setOldUnitName(String oldUnitName) {
		this.oldUnitName = oldUnitName;
	}

	@Column(name = "EXT_ZRR")
	public String getExtZRR() {
		return extZRR;
	}

	public void setExtZRR(String extZRR) {
		this.extZRR = extZRR;
	}

	public boolean isGz() {
		return gz;
	}

	public void setGz(boolean gz) {
		this.gz = gz;
	}

	@Column(name = "ACCESS_CERTS")
	public String getAccessCerts() {
		return accessCerts;
	}

	public void setAccessCerts(String accessCerts) {
		this.accessCerts = accessCerts;
	}

	@Column(name = "LEVEL_")
	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

}