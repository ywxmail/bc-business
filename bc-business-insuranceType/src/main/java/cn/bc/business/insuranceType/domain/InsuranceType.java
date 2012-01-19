package cn.bc.business.insuranceType.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import cn.bc.identity.domain.FileEntityImpl;

/**
 * 车保险种
 * 
 * @author dragon
 */
@Entity
@Table(name = "BS_INSURANCE_TYPE")
public class InsuranceType extends FileEntityImpl {
	private static final long serialVersionUID = 1L;

	private String Name;// 险种名称
	private Float coverage;// 保额
	private String description;// 备注
	private int status;// 状态

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public Float getCoverage() {
		return coverage;
	}

	public void setCoverage(Float coverage) {
		this.coverage = coverage;
	}

	@Column(name = "DESC_")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "STATUS_")
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}