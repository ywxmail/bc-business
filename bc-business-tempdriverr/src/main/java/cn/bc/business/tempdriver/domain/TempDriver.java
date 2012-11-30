package cn.bc.business.tempdriver.domain;

import java.util.Calendar;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import cn.bc.identity.domain.RichFileEntityImpl;

/**
 * 司机招聘信息
 * 
 * @author dragon
 */
@Entity
@Table(name = "BS_TEMP_DRIVER")
public class TempDriver extends RichFileEntityImpl {
	private static final long serialVersionUID = 1L;

	public static final String KEY_UID = "tempDriver.uid";
	
	/** 聘用状态：待聘 */
	public static final int STATUS_RESERVE = 0;
	/** 聘用状态：审批中 */
	public static final int STATUS_CHECK = 1;
	/** 聘用状态：聘用 */
	public static final int STATUS_PASS = 2;
	/** 聘用状态：弃用 */
	public static final int STATUS_GIVEUP = 3;
	/** 聘用状态：删除 */
	public static final int STATUS_DELETE = 4;
	
	/** 性别：男 */
	public static final int SEX_MAN = 1;
	/** 性别：女 */
	public static final int SEX_WOMAN = 2;

	/** 区域：本市 */
	public static final int REGION_BEN_SHI = 1;
	/** 区域：本省 */
	public static final int REGION_BEN_SHENG = 2;
	/** 区域：外省 */
	public static final int REGION_WAI_SHENG = 3;
	/** 区域：空 */
	public static final int REGION_ = 0;

	private Set<TempDriverWorkFlow> tempDriverWorkFlowList;// 参加的面试流程记录集合
	private String name;// 姓名
	private Calendar birthdate;// 生日
	private int sex;// 性别
	private int region;// 区域
	private String origin;// 籍贯
	private String address;// 地址
	private String newAddress;// 最新地址
	private String phone;
	private String certIdentity;// 身份证
	private String certFWZG;// 服务资格证
	private String certCYZG;// 从业资格证
	private String education;// 学历
	private String nation;// 民族
	private String listWorkExperience;// 工作经历信息集合
	private String listFamily;// 家庭成员信息集合
	private String marry;// 婚姻状况
	private String desc;// 备注
	private String credit;// 信誉档案
	private Calendar certDrivingFirstDate;//本市驾驶证初领日期
	private Integer cyStartYear;//本市出租车从业初始年份

	@OneToMany(mappedBy = "tempDriver", fetch = FetchType.LAZY)
	@OrderBy(value =("startTime DESC"))
	public Set<TempDriverWorkFlow> getTempDriverWorkFlowList() {
		return tempDriverWorkFlowList;
	}

	public void setTempDriverWorkFlowList(
			Set<TempDriverWorkFlow> tempDriverWorkFlowList) {
		this.tempDriverWorkFlowList = tempDriverWorkFlowList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Column(name = "NEW_ADDR")
	public String getNewAddress() {
		return newAddress;
	}

	public void setNewAddress(String newAddress) {
		this.newAddress = newAddress;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Column(name = "CERT_IDENTITY")
	public String getCertIdentity() {
		return certIdentity;
	}

	public void setCertIdentity(String certIdentity) {
		this.certIdentity = certIdentity;
	}

	@Column(name = "CERT_FWZG")
	public String getCertFWZG() {
		return certFWZG;
	}

	public void setCertFWZG(String certFWZG) {
		this.certFWZG = certFWZG;
	}

	@Column(name = "CERT_CYZG")
	public String getCertCYZG() {
		return certCYZG;
	}

	public void setCertCYZG(String certCYZG) {
		this.certCYZG = certCYZG;
	}

	public String getEducation() {
		return education;
	}

	public void setEducation(String education) {
		this.education = education;
	}

	public String getNation() {
		return nation;
	}

	public void setNation(String nation) {
		this.nation = nation;
	}

	@Column(name = "LIST_WORK_EXPERIENCE")
	public String getListWorkExperience() {
		return listWorkExperience;
	}

	public void setListWorkExperience(String listWorkExperience) {
		this.listWorkExperience = listWorkExperience;
	}

	@Column(name = "LIST_FAMILY")
	public String getListFamily() {
		return listFamily;
	}

	public void setListFamily(String listFamily) {
		this.listFamily = listFamily;
	}

	public String getMarry() {
		return marry;
	}

	public void setMarry(String marry) {
		this.marry = marry;
	}

	@Column(name = "DESC_")
	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
	@Column(name = "REGION_")
	public int getRegion() {
		return region;
	}

	public void setRegion(int region) {
		this.region = region;
	}

	public Calendar getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(Calendar birthdate) {
		this.birthdate = birthdate;
	}

	@Column(name = "CREDIT_")
	public String getCredit() {
		return credit;
	}

	public void setCredit(String credit) {
		this.credit = credit;
	}

	@Column(name = "CERT_DRIVING_FIRST_DATE")
	public Calendar getCertDrivingFirstDate() {
		return certDrivingFirstDate;
	}

	public void setCertDrivingFirstDate(Calendar certDrivingFirstDate) {
		this.certDrivingFirstDate = certDrivingFirstDate;
	}

	@Column(name = "CY_START_YEAR")
	public Integer getCyStartYear() {
		return cyStartYear;
	}

	public void setCyStartYear(Integer cyStartYear) {
		this.cyStartYear = cyStartYear;
	}
	
	
}