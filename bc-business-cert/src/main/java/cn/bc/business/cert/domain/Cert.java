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

import cn.bc.core.EntityImpl;

/**
 * 证件基类（继承关系使用Join Strategy：每个子类对应一张表，但此表中不包含基类的属性,仅仅是此子类的扩展属性,共享基类的属性）
 * 
 * @author dragon
 */
@Entity
@Table(name = "BS_CERT")
@Inheritance(strategy = InheritanceType.JOINED)
public class Cert extends EntityImpl {
	private static final long serialVersionUID = 1L;

	private String code;// 证件号
	private String name;// 证件简称
	private String fullName;// 证件全称
	private String licencer;// 发证机关
	private Calendar startDate;// 生效日期
	private Calendar endDate;// 到期日期

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "FULL_NAME")
	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getLicencer() {
		return licencer;
	}

	public void setLicencer(String licencer) {
		this.licencer = licencer;
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
}