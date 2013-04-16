/**
 * 
 */
package cn.bc.business.carPrepare.domain;

import java.util.Calendar;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import cn.bc.identity.domain.RichFileEntityImpl;

/**
 * 出车准备
 * 
 * @author zxr
 */
@Entity
@Table(name = "BS_CAR_PREPARE")
@Inheritance(strategy = InheritanceType.JOINED)
public class CarPrepare extends RichFileEntityImpl {
	private static final long serialVersionUID = 1L;
	public static final String KEY_CODE = "carPrepare.code";
	public static final String KEY_UID = CarPrepare.class.getSimpleName();

	/** 状态：待更新 */
	public static final int STATUS_STAYUPDATED = 0;
	/** 状态：更新中 */
	public static final int STATUS_INTHEUPDATE = 1;
	/** 状态：已完成 */
	public static final int STATUS_COMPLETED = 2;

	private String code;// 经营权号
	private Calendar planDate;// 预计交车日
	private String C1PlateType;// 旧车车牌归属 : 如粤A
	private String C1PlateNo;// 旧车车牌号 : 如471G7
	private String C1Company;// 旧车所属公司
	private Integer C1Motorcade;// 旧车所属车队
	private String C1BsType;// 旧车合同性质
	private String C1Scrapto;// 旧车残值归属
	private Calendar C1RegisterDate;// 旧车登记日期
	private Calendar C1ContractEndDate;// 旧车经济合同到期日
	private Calendar C1CommeriaEndDate;// 旧车商业险到期日
	private Calendar C1GreenslipEndDate;// 旧车强制险到期日
	private String C2Indicator;// 新车指标编号
	private String C2PlateType;// 新车车牌归属
	private String C2PlateNo;// 新车车牌号
	private Long C2CarId;// 新车ID
	private String C2BsType;// 新车营运性质
	private String C2CarActiveType;// 新车出车性质
	private Long C2ManageNo;// 新车管理号
	private String C2Vin;// 新车车架号
	private String C2CarCode;// 新车自编号
	private String C2Company;// 新车所属公司
	private Long C2Branch;// 新车所属分公司
	private Long C2Motorcade;// 新车所属车队
	private Long C2Driver1Id;// 新车迁入司机1Id
	private String C2Driver1;// 新车迁入司机1
	private String C2CertFWZG4Driver1;// 新车迁入司机1的服务资格证
	private String C2Nature4Driver1;// 新车迁入司机1的驾驶员性质
	private Long C2Driver2Id;// 新车迁入司机1Id
	private String C2Driver2;// 新车迁入司机1
	private String C2CertFWZG4Driver2;// 新车迁入司机2的服务资格证
	private String C2Nature4Driver2;// 新车迁入司机2的驾驶员性质

	private Set<CarPrepareItem> carPrepareItem;// 车辆更新项目

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Column(name = "PLAN_DATE")
	public Calendar getPlanDate() {
		return planDate;
	}

	public void setPlanDate(Calendar planDate) {
		this.planDate = planDate;
	}

	@Column(name = "C1_PLATE_TYPE")
	public String getC1PlateType() {
		return C1PlateType;
	}

	public void setC1PlateType(String c1PlateType) {
		C1PlateType = c1PlateType;
	}

	@Column(name = "C1_PLATE_NO")
	public String getC1PlateNo() {
		return C1PlateNo;
	}

	public void setC1PlateNo(String c1PlateNo) {
		C1PlateNo = c1PlateNo;
	}

	@Column(name = "C1_COMPANY")
	public String getC1Company() {
		return C1Company;
	}

	public void setC1Company(String c1Company) {
		C1Company = c1Company;
	}

	@Column(name = "C1_MOTORCADE")
	public Integer getC1Motorcade() {
		return C1Motorcade;
	}

	public void setC1Motorcade(Integer c1Motorcade) {
		C1Motorcade = c1Motorcade;
	}

	@Column(name = "C1_BS_TYPE")
	public String getC1BsType() {
		return C1BsType;
	}

	public void setC1BsType(String c1BsType) {
		C1BsType = c1BsType;
	}

	@Column(name = "C1_SCRAPTO")
	public String getC1Scrapto() {
		return C1Scrapto;
	}

	public void setC1Scrapto(String c1Scrapto) {
		C1Scrapto = c1Scrapto;
	}

	@Column(name = "C1_REGISTER_DATE")
	public Calendar getC1RegisterDate() {
		return C1RegisterDate;
	}

	public void setC1RegisterDate(Calendar c1RegisterDate) {
		C1RegisterDate = c1RegisterDate;
	}

	@Column(name = "C1_CONTRACT_END_DATE")
	public Calendar getC1ContractEndDate() {
		return C1ContractEndDate;
	}

	public void setC1ContractEndDate(Calendar c1ContractEndDate) {
		C1ContractEndDate = c1ContractEndDate;
	}

	@Column(name = "C1_COMMERIAL_END_DATE")
	public Calendar getC1CommeriaEndDate() {
		return C1CommeriaEndDate;
	}

	public void setC1CommeriaEndDate(Calendar c1CommeriaEndDate) {
		C1CommeriaEndDate = c1CommeriaEndDate;
	}

	@Column(name = "C1_GREENSLIP_END_DATE")
	public Calendar getC1GreenslipEndDate() {
		return C1GreenslipEndDate;
	}

	public void setC1GreenslipEndDate(Calendar c1GreenslipEndDate) {
		C1GreenslipEndDate = c1GreenslipEndDate;
	}

	@Column(name = "C2_INDICATOR")
	public String getC2Indicator() {
		return C2Indicator;
	}

	public void setC2Indicator(String c2Indicator) {
		C2Indicator = c2Indicator;
	}

	@Column(name = "C2_PLATE_TYPE")
	public String getC2PlateType() {
		return C2PlateType;
	}

	public void setC2PlateType(String c2PlateType) {
		C2PlateType = c2PlateType;
	}

	@Column(name = "C2_PLATE_NO")
	public String getC2PlateNo() {
		return C2PlateNo;
	}

	public void setC2PlateNo(String c2PlateNo) {
		C2PlateNo = c2PlateNo;
	}

	@Column(name = "C2_COMPANY")
	public String getC2Company() {
		return C2Company;
	}

	public void setC2Company(String c2Company) {
		C2Company = c2Company;
	}

	@Column(name = "C2_BRANCH")
	public Long getC2Branch() {
		return C2Branch;
	}

	public void setC2Branch(Long c2Branch) {
		C2Branch = c2Branch;
	}

	@Column(name = "C2_MOTORCADE")
	public Long getC2Motorcade() {
		return C2Motorcade;
	}

	public void setC2Motorcade(Long c2Motorcade) {
		C2Motorcade = c2Motorcade;
	}

	@OneToMany(mappedBy = "carPrepare", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy(value = "order asc")
	public Set<CarPrepareItem> getCarPrepareItem() {
		return carPrepareItem;
	}

	public void setCarPrepareItem(Set<CarPrepareItem> carPrepareItem) {
		this.carPrepareItem = carPrepareItem;
	}

	@Column(name = "C2_CAR_ID")
	public Long getC2CarId() {
		return C2CarId;
	}

	public void setC2CarId(Long c2CarId) {
		C2CarId = c2CarId;
	}

	@Column(name = "C2_BS_TYPE")
	public String getC2BsType() {
		return C2BsType;
	}

	public void setC2BsType(String c2BsType) {
		C2BsType = c2BsType;
	}

	@Column(name = "C2_CARACTIVE_TYPE")
	public String getC2CarActiveType() {
		return C2CarActiveType;
	}

	public void setC2CarActiveType(String c2CarActiveType) {
		C2CarActiveType = c2CarActiveType;
	}

	@Column(name = "C2_MANAGE_NO")
	public Long getC2ManageNo() {
		return C2ManageNo;
	}

	public void setC2ManageNo(Long c2ManageNo) {
		C2ManageNo = c2ManageNo;
	}

	@Column(name = "C2_VIN")
	public String getC2Vin() {
		return C2Vin;
	}

	public void setC2Vin(String c2Vin) {
		C2Vin = c2Vin;
	}

	@Column(name = "C2_CAR_CODE")
	public String getC2CarCode() {
		return C2CarCode;
	}

	public void setC2CarCode(String c2CarCode) {
		C2CarCode = c2CarCode;
	}

	@Column(name = "C2_DRIVER1_ID")
	public Long getC2Driver1Id() {
		return C2Driver1Id;
	}

	public void setC2Driver1Id(Long c2Driver1Id) {
		C2Driver1Id = c2Driver1Id;
	}

	@Column(name = "C2_DRIVER1")
	public String getC2Driver1() {
		return C2Driver1;
	}

	public void setC2Driver1(String c2Driver1) {
		C2Driver1 = c2Driver1;
	}

	@Column(name = "C2_DRIVER2_ID")
	public Long getC2Driver2Id() {
		return C2Driver2Id;
	}

	public void setC2Driver2Id(Long c2Driver2Id) {
		C2Driver2Id = c2Driver2Id;
	}

	@Column(name = "C2_DRIVER2")
	public String getC2Driver2() {
		return C2Driver2;
	}

	public void setC2Driver2(String c2Driver2) {
		C2Driver2 = c2Driver2;
	}

	@Column(name = "C2_CERT_FWZG_DRIVER1")
	public String getC2CertFWZG4Driver1() {
		return C2CertFWZG4Driver1;
	}

	public void setC2CertFWZG4Driver1(String c2CertFWZG4Driver1) {
		C2CertFWZG4Driver1 = c2CertFWZG4Driver1;
	}

	@Column(name = "C2_NATURE_DRIVER1")
	public String getC2Nature4Driver1() {
		return C2Nature4Driver1;
	}

	public void setC2Nature4Driver1(String c2Nature4Driver1) {
		C2Nature4Driver1 = c2Nature4Driver1;
	}

	@Column(name = "C2_CERT_FWZG_DRIVER2")
	public String getC2CertFWZG4Driver2() {
		return C2CertFWZG4Driver2;
	}

	public void setC2CertFWZG4Driver2(String c2CertFWZG4Driver2) {
		C2CertFWZG4Driver2 = c2CertFWZG4Driver2;
	}

	@Column(name = "C2_NATURE_DRIVER2")
	public String getC2Nature4Driver2() {
		return C2Nature4Driver2;
	}

	public void setC2Nature4Driver2(String c2Nature4Driver2) {
		C2Nature4Driver2 = c2Nature4Driver2;
	}

}