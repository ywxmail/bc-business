/**
 * 
 */
package cn.bc.business.policy.domain;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
	public static final String KEY_CODE = "blacklist.code";

	private Car car;// 投保车号
	private Calendar registerDate;// 初登日期
	private Long assured;// 投保人
	private String commerialNo;// 商业险号
	private Long commerialCompany;// 商业保险公司
	private Calendar commerialStartDate;// 商业险开始日期
	private Calendar commerialEndDate;// 商业险结束日期
	private boolean ownrisk;// 是否自保
	private boolean greenslip;// 是否购买了强制险
	private boolean greenslipSameDate;// 强制险是否与商业险同期
	private String greenslipNo;// 强险单号
	private Long greenslipCompany;// 强险保险公司
	private Calendar greenslipStartDate;// 强制险开始日期
	private Calendar greenslipEndDate;// 强制险结束日期
	private String greenslipSource;// 强保人来源
	private String liabilityNo;// 责任险单号
	private Float amount;// 合计

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

	public Long getAssured() {
		return assured;
	}

	public void setAssured(Long assured) {
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
	public Long getCommerialCompany() {
		return commerialCompany;
	}

	public void setCommerialCompany(Long commerialCompany) {
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

	public boolean isGreenslipSameDate() {
		return greenslipSameDate;
	}

	@Column(name = "GREENSLIP_SAME_DATE")
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
	public Long getGreenslipCompany() {
		return greenslipCompany;
	}

	public void setGreenslipCompany(Long greenslipCompany) {
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

}