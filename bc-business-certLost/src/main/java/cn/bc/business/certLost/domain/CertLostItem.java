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
	private boolean isReplace;// 是否补办
	private Calendar replaceDate;// 遗失日期
	private String reason;// 补办原因
	private String certNo;// 证件号码
	private String NewCertNo;// 新证件号码
	private boolean isRemains;// 是否有残骸
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

	@Column(name = "IS_REPLACE")
	public boolean isReplace() {
		return isReplace;
	}

	public void setReplace(boolean isReplace) {
		this.isReplace = isReplace;
	}

	@Column(name = "REPLACE_DATE")
	public Calendar getReplaceDate() {
		return replaceDate;
	}

	public void setReplaceDate(Calendar replaceDate) {
		this.replaceDate = replaceDate;
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
		return NewCertNo;
	}

	public void setNewCertNo(String newCertNo) {
		NewCertNo = newCertNo;
	}

	@Column(name = "IS_REMAINS")
	public boolean isRemains() {
		return isRemains;
	}

	public void setRemains(boolean isRemains) {
		this.isRemains = isRemains;
	}

	@Column(name = "DESC_")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}