/**
 * 
 */
package cn.bc.business.injury.domain;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import cn.bc.identity.domain.RichFileEntityImpl;

/**
 * 工伤类
 * 
 * @author wis
 */
@Entity
@Table(name = "BS_INDUSTRIAL_INJURY")
@Inheritance(strategy = InheritanceType.JOINED)
public class Injury extends RichFileEntityImpl {
	private static final long serialVersionUID = 1L;
	public static final String KEY_UID = Injury.class.getSimpleName();
	public static final String KEY_CODE = "injury.code";
	
	private String	code; // 工伤编号
	private Long	contractId; //合同ID
	private Calendar happenDate; //工伤时间
	private Calendar confirmDate; //认定时间
	private Calendar startDate;// 医疗开始
	private Calendar endDate;// 医疗结束
	private String	compensation;// 赔偿项目
	private Float	money;// 金额
	private boolean oAS;// 是否门诊:0-有,1-无
	private boolean inHospital;// 是否住院:0-有,1-无
	private boolean li;// 劳动鉴别:0-有,1-无
	private String description;// 概况描述
	
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public Long getContractId() {
		return contractId;
	}
	
	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}
	
	@Column(name = "HAPPEN_DATE")
	public Calendar getHappenDate() {
		return happenDate;
	}
	public void setHappenDate(Calendar happenDate) {
		this.happenDate = happenDate;
	}
	
	@Column(name = "CONFIRM_DATE")
	public Calendar getConfirmDate() {
		return confirmDate;
	}
	public void setConfirmDate(Calendar confirmDate) {
		this.confirmDate = confirmDate;
	}
	
	@Column(name = "START_DATE")
	public Calendar getStartDate() {
		return startDate;
	}
	public void setStartDate(Calendar startDate) {
		this.startDate = startDate;
	}
	
	@Column(name = "END_DATE")
	public Calendar getEndDate() {
		return endDate;
	}
	public void setEndDate(Calendar endDate) {
		this.endDate = endDate;
	}
	
	public String getCompensation() {
		return compensation;
	}
	public void setCompensation(String compensation) {
		this.compensation = compensation;
	}
	
	public Float getMoney() {
		return money;
	}
	public void setMoney(Float money) {
		this.money = money;
	}
	
	@Column(name = "IS_OAS")
	public boolean isoAS() {
		return oAS;
	}
	public void setoAS(boolean oAS) {
		this.oAS = oAS;
	}
	
	@Column(name = "IS_INHOSPITAL")
	public boolean isInHospital() {
		return inHospital;
	}
	public void setInHospital(boolean inHospital) {
		this.inHospital = inHospital;
	}
	
	@Column(name = "IS_LI")
	public boolean isLi() {
		return li;
	}
	public void setLi(boolean li) {
		this.li = li;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	
	
}