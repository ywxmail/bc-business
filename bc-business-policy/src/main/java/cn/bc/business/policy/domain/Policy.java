/**
 * 
 */
package cn.bc.business.policy.domain;

import java.util.Calendar;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import cn.bc.business.car.domain.Car;
import cn.bc.identity.domain.RichFileEntityImpl;

/**
 * 车辆保单
 * 
 * @author dragon
 */
@Entity
@Table(name = "BS_CAR_POLICY ")
@Inheritance(strategy = InheritanceType.JOINED)
public class Policy extends RichFileEntityImpl {
	private static final long serialVersionUID = 1L;
	public static final String KEY_CODE = "policy.code";
	public static final String KEY_UID = Policy.class.getSimpleName();

	/** 操作类型 新建 */
	public static final int OPTYPE_CREATE = 1;
	/** 操作类型 维护 */
	public static final int OPTYPE_EDIT = 2;
	/** 操作类型 续保 */
	public static final int OPTYPE_RENEWAL = 3;
	/** 操作类型 停保 */
	public static final int OPTYPE_SURRENDERS = 4;

	/** 主版本号默认值 */
	public static final int MAJOR_DEFALUT = 1;
	/** 次版本号默认值 */
	public static final int MINOR_DEFALUT = 0;
	/** 主体当前版本 */
	public static final int MAIN_NOW = 0;
	/** 主体历史版本 */
	public static final int MAIN_HISTORY = 1;
	/** 布尔值：是 */
	public static final int BOOLEAN_YES = 1;
	/** 布尔值：否 */
	public static final int BOOLEAN_NO = 0;

	private Car car;// 投保车号
	private Calendar registerDate;// 初登日期
	private String assured;// 投保人
	private String commerialNo;// 商业险号
	private String commerialCompany;// 商业保险公司
	private Calendar commerialStartDate;// 商业险开始日期
	private Calendar commerialEndDate;// 商业险结束日期
	private boolean ownrisk;// 是否自保
	private boolean greenslip;// 是否购买了强制险
	private boolean greenslipSameDate;// 强制险是否与商业险同期
	private String greenslipNo;// 强险单号
	private String greenslipCompany;// 强险保险公司
	private Calendar greenslipStartDate;// 强制险开始日期
	private Calendar greenslipEndDate;// 强制险结束日期
	private String greenslipSource;// 强保人来源
	private String liabilityNo;// 责任险单号
	private Float amount;// 合计
	private Calendar stopDate;// 停保日期
	private Integer verMajor;// 主版本号
	private int main; // 主体： 0-当前版本,1-历史版本
	private String patchNo;// 批号
	private int opType; // 操作类型：1-新建,2-维护,3-续保,4-停保
	private Long Pid;// 父级ID
	private Set<BuyPlant> buyPlants;// 购买保单险种

	@ManyToOne(fetch = FetchType.EAGER, optional = true)
	@JoinColumn(name = "CAR_ID", referencedColumnName = "ID")
	public Car getCar() {
		return car;
	}

	public void setCar(Car car) {
		this.car = car;
	}

	@Column(name = "REGISTER_DATE")
	public Calendar getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(Calendar registerDate) {
		this.registerDate = registerDate;
	}

	public String getAssured() {
		return assured;
	}

	public void setAssured(String assured) {
		this.assured = assured;
	}

	@Column(name = "COMMERIAL_NO")
	public String getCommerialNo() {
		return commerialNo;
	}

	public void setCommerialNo(String commerialNo) {
		this.commerialNo = commerialNo;
	}

	@Column(name = "COMMERIAL_COMPANY")
	public String getCommerialCompany() {
		return commerialCompany;
	}

	public void setCommerialCompany(String commerialCompany) {
		this.commerialCompany = commerialCompany;
	}

	@Column(name = "COMMERIAL_START_DATE")
	public Calendar getCommerialStartDate() {
		return commerialStartDate;
	}

	public void setCommerialStartDate(Calendar commerialStartDate) {
		this.commerialStartDate = commerialStartDate;
	}

	@Column(name = "COMMERIAL_END_DATE")
	public Calendar getCommerialEndDate() {
		return commerialEndDate;
	}

	public void setCommerialEndDate(Calendar commerialEndDate) {
		this.commerialEndDate = commerialEndDate;
	}

	public boolean isOwnrisk() {
		return ownrisk;
	}

	public void setOwnrisk(boolean ownrisk) {
		this.ownrisk = ownrisk;
	}

	public boolean isGreenslip() {
		return greenslip;
	}

	public void setGreenslip(boolean greenslip) {
		this.greenslip = greenslip;
	}

	@Column(name = "GREENSLIP_SAME_DATE")
	public boolean isGreenslipSameDate() {
		return greenslipSameDate;
	}

	public void setGreenslipSameDate(boolean greenslipSameDate) {
		this.greenslipSameDate = greenslipSameDate;
	}

	@Column(name = "GREENSLIP_NO")
	public String getGreenslipNo() {
		return greenslipNo;
	}

	public void setGreenslipNo(String greenslipNo) {
		this.greenslipNo = greenslipNo;
	}

	@Column(name = "GREENSLIP_COMPANY")
	public String getGreenslipCompany() {
		return greenslipCompany;
	}

	public void setGreenslipCompany(String greenslipCompany) {
		this.greenslipCompany = greenslipCompany;
	}

	@Column(name = "GREENSLIP_START_DATE")
	public Calendar getGreenslipStartDate() {
		return greenslipStartDate;
	}

	public void setGreenslipStartDate(Calendar greenslipStartDate) {
		this.greenslipStartDate = greenslipStartDate;
	}

	@Column(name = "GREENSLIP_END_DATE")
	public Calendar getGreenslipEndDate() {
		return greenslipEndDate;
	}

	public void setGreenslipEndDate(Calendar greenslipEndDate) {
		this.greenslipEndDate = greenslipEndDate;
	}

	@Column(name = "GREENSLIP_SOURCE")
	public String getGreenslipSource() {
		return greenslipSource;
	}

	public void setGreenslipSource(String greenslipSource) {
		this.greenslipSource = greenslipSource;
	}

	@Column(name = "LIABILITY_NO")
	public String getLiabilityNo() {
		return liabilityNo;
	}

	public void setLiabilityNo(String liabilityNo) {
		this.liabilityNo = liabilityNo;
	}

	public Float getAmount() {
		return amount;
	}

	public void setAmount(Float amount) {
		this.amount = amount;
	}

	@Column(name = "VER_MAJOR")
	public Integer getVerMajor() {
		return verMajor;
	}

	public void setVerMajor(Integer verMajor) {
		this.verMajor = verMajor;
	}

	public int getMain() {
		return main;
	}

	public void setMain(int main) {
		this.main = main;
	}

	@Column(name = "PATCH_NO")
	public String getPatchNo() {
		return patchNo;
	}

	public void setPatchNo(String patchNo) {
		this.patchNo = patchNo;
	}

	@Column(name = "OP_TYPE")
	public int getOpType() {
		return opType;
	}

	public void setOpType(int opType) {
		this.opType = opType;
	}

	public Long getPid() {
		return Pid;
	}

	public void setPid(Long pid) {
		Pid = pid;
	}

	@OneToMany(mappedBy = "policy", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy(value = "orderNo asc")
	public Set<BuyPlant> getBuyPlants() {
		return buyPlants;
	}

	public void setBuyPlants(Set<BuyPlant> buyPlants) {
		this.buyPlants = buyPlants;
	}

	public Calendar getStopDate() {
		return stopDate;
	}

	public void setStopDate(Calendar stopDate) {
		this.stopDate = stopDate;
	}

}