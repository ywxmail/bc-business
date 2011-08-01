/**
 * 
 */
package cn.bc.business.cert.domain;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import cn.bc.identity.domain.RichFileEntityImpl;

/**
 * 证件基类（继承关系使用Join Strategy：每个子类对应一张表，但此表中不包含基类的属性,仅仅是此子类的扩展属性,共享基类的属性）
 * 
 * @author dragon
 */
@Entity
@Table(name = "BS_CERT")
@Inheritance(strategy = InheritanceType.JOINED)
public class Cert extends RichFileEntityImpl {
	private static final long serialVersionUID = 1L;
	public static final String ATTACH_TYPE = Cert.class.getSimpleName();

	private String certCode;// 证件号
	private String certName;// 证件简称
	private String certFullName;// 证件全称
	private String licencer;// 发证机关
	private Calendar issueDate;// 发证日期
	private Calendar startDate;// 生效日期
	private Calendar endDate;// 到期日期

	private String ext_str1;// 扩展域
	private String ext_str2;// 扩展域
	private String ext_str3;// 扩展域
	private Integer ext_num1;// 扩展域
	private Integer ext_num2;// 扩展域
	private Integer ext_num3;// 扩展域

	@Column(name = "CERT_CODE")
	public String getCertCode() {
		return certCode;
	}

	public void setCertCode(String certCode) {
		this.certCode = certCode;
	}

	@Column(name = "CERT_NAME")
	public String getCertName() {
		return certName;
	}

	public void setCertName(String certName) {
		this.certName = certName;
	}

	@Column(name = "CERT_FULL_NAME")
	public String getCertFullName() {
		return certFullName;
	}

	public void setCertFullName(String fullName) {
		this.certFullName = fullName;
	}

	public String getLicencer() {
		return licencer;
	}

	public void setLicencer(String licencer) {
		this.licencer = licencer;
	}

	@Column(name = "ISSUE_DATE")
	public Calendar getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(Calendar issueDate) {
		this.issueDate = issueDate;
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

	public String getExt_str1() {
		return ext_str1;
	}

	public void setExt_str1(String ext_str1) {
		this.ext_str1 = ext_str1;
	}

	public String getExt_str2() {
		return ext_str2;
	}

	public void setExt_str2(String ext_str2) {
		this.ext_str2 = ext_str2;
	}

	public String getExt_str3() {
		return ext_str3;
	}

	public void setExt_str3(String ext_str3) {
		this.ext_str3 = ext_str3;
	}

	public Integer getExt_num1() {
		return ext_num1;
	}

	public void setExt_num1(Integer ext_num1) {
		this.ext_num1 = ext_num1;
	}

	public Integer getExt_num2() {
		return ext_num2;
	}

	public void setExt_num2(Integer ext_num2) {
		this.ext_num2 = ext_num2;
	}

	public Integer getExt_num3() {
		return ext_num3;
	}

	public void setExt_num3(Integer ext_num3) {
		this.ext_num3 = ext_num3;
	}
}