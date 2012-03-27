package cn.bc.business.certLost.domain;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import cn.bc.core.EntityImpl;

/**
 * 遗失的证照
 * 
 * @author zxr
 */
@Entity
@Table(name = "BS_CERT_LOST_ITEM")
public class CertLostItem extends EntityImpl {
	private static final long serialVersionUID = 1L;
	private CertLost certLost;// 证照遗失管理
	private String certName;// 证照名称
	private boolean replace;// 是否补办
	private Calendar replaceDate;// 补办日期
	private String lostAddress;// 遗失地点
	private String alarmUnit;// 报警单位
	private String reason;// 补办原因
	private String certNo;// 证件号码
	private String newCertNo;// 新证件号码
	private boolean remains;// 是否有残骸
	private String description;// 备注

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "PID", referencedColumnName = "ID")
	public CertLost getCertLost() {
		return certLost;
	}

	public void setCertLost(CertLost certLost) {
		this.certLost = certLost;
	}

	@Column(name = "CERT_NAME")
	public String getCertName() {
		return certName;
	}

	public void setCertName(String certName) {
		this.certName = certName;
	}

	@Column(name = "REPLACE_DATE")
	public Calendar getReplaceDate() {
		return replaceDate;
	}

	public void setReplaceDate(Calendar replaceDate) {
		this.replaceDate = replaceDate;
	}

	@Column(name = "LOST_ADDRESS")
	public String getLostAddress() {
		return lostAddress;
	}

	public void setLostAddress(String lostAddress) {
		this.lostAddress = lostAddress;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	@Column(name = "CERT_NO")
	public String getCertNo() {
		return certNo;
	}

	public void setCertNo(String certNo) {
		this.certNo = certNo;
	}

	@Column(name = "NEW_CERT_NO")
	public String getNewCertNo() {
		return newCertNo;
	}

	@Column(name = "IS_REPLACE")
	public boolean isReplace() {
		return replace;
	}

	public void setReplace(boolean replace) {
		this.replace = replace;
	}

	@Column(name = "IS_REMAINS")
	public boolean isRemains() {
		return remains;
	}

	public void setRemains(boolean remains) {
		this.remains = remains;
	}

	public void setNewCertNo(String newCertNo) {
		this.newCertNo = newCertNo;
	}

	@Column(name = "DESC_")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAlarmUnit() {
		return alarmUnit;
	}

	public void setAlarmUnit(String alarmUnit) {
		this.alarmUnit = alarmUnit;
	}

}