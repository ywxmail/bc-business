/**
 * 
 */
package cn.bc.business.contract.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import cn.bc.business.carman.domain.CarMan;

/**
 * 司机劳动合同
 * 
 * @author dragon
 */
@Entity
@Table(name = "BS_CONTRACT_LABOUR")
@Inheritance(strategy = InheritanceType.JOINED)
public class Contract4Labour extends Contract4Car {
	private static final long serialVersionUID = 1L;
	public static final String KEY_UID = Contract4Labour.class.getSimpleName();
	private CarMan driver;// 司机
	private String certNo;// 资格证号
	private boolean additionProtocol;// 补充协议:0-无,1-有
	private String preIndustryName;// 前身行业名称
	private boolean preIndustryType;// 前身工种行业:0-非特殊,1-特殊
	private boolean hiringProcedure;// 招用工手续:0-未办,1-已办
	private boolean dole;// 下岗失业补贴:0-已发,1-未发
	private boolean filing;// 是否已备案
	
	public Contract4Labour() {
		this.setCode("劳动合同");
	}

	public boolean isFiling() {
		return filing;
	}

	public void setFiling(boolean filing) {
		this.filing = filing;
	}

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "DRIVER_ID", referencedColumnName = "ID")
	public CarMan getDriver() {
		return driver;
	}

	public void setDriver(CarMan driver) {
		this.driver = driver;
	}

	@Column(name = "CERT_NO")
	public String getCertNo() {
		return certNo;
	}

	public void setCertNo(String certNo) {
		this.certNo = certNo;
	}

	@Column(name = "ADDITION_PROTOCOL")
	public boolean isAdditionProtocol() {
		return additionProtocol;
	}

	public void setAdditionProtocol(boolean additionProtocol) {
		this.additionProtocol = additionProtocol;
	}

	@Column(name = "PRE_INDUSTRY_NAME")
	public String getPreIndustryName() {
		return preIndustryName;
	}

	public void setPreIndustryName(String preIndustryName) {
		this.preIndustryName = preIndustryName;
	}

	@Column(name = "PRE_INDUSTRY_TYPE")
	public boolean isPreIndustryType() {
		return preIndustryType;
	}

	public void setPreIndustryType(boolean preIndustryType) {
		this.preIndustryType = preIndustryType;
	}

	@Column(name = "HIRING_PROCEDURE")
	public boolean isHiringProcedure() {
		return hiringProcedure;
	}

	public void setHiringProcedure(boolean hiringProcedure) {
		this.hiringProcedure = hiringProcedure;
	}

	public boolean isDole() {
		return dole;
	}

	public void setDole(boolean dole) {
		this.dole = dole;
	}
}