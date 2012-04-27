package cn.bc.business.socialSecurityRule.domain;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import cn.bc.identity.domain.FileEntityImpl;
/**
 * 社保收费规则
 * 
 * @author admin
 *
 */

@Entity
@Table(name = "BS_SOCIALSECURITYRULE")
public class SocialSecurityRule extends FileEntityImpl {
	private static final long serialVersionUID = 1L;
	
	private Long areaId;//使用区域ID
	private String areaName;//使用区域名称
	private int startYear;//起始年
	private int startMonth;//起始月
	private String houseType;//户口类型：本地城镇、本地农村、外地城镇、外地农村
	
	private Set<SocialSecurityRuleDetail> socialSecurityRuleDetail;//社保收费规则保险明细

	@Column(name = "AREA_ID")
	public Long getAreaId() {
		return areaId;
	}

	public void setAreaId(Long areaId) {
		this.areaId = areaId;
	}

	@Column(name = "AREA_NAME")
	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	@Column(name = "START_YEAR")
	public int getStartYear() {
		return startYear;
	}

	public void setStartYear(int startYear) {
		this.startYear = startYear;
	}

	@Column(name = "START_MONTH")
	public int getStartMonth() {
		return startMonth;
	}

	public void setStartMonth(int startMonth) {
		this.startMonth = startMonth;
	}
	
	@Column(name = "HOUSE_TYPE")
	public String getHouseType() {
		return houseType;
	}

	public void setHouseType(String houseType) {
		this.houseType = houseType;
	}

	@OneToMany(mappedBy = "socialSecurityRule", fetch = FetchType.EAGER, 
			cascade = CascadeType.ALL, orphanRemoval = true)
	public Set<SocialSecurityRuleDetail> getSocialSecurityRuleDetail() {
		return socialSecurityRuleDetail;
	}

	public void setSocialSecurityRuleDetail(
			Set<SocialSecurityRuleDetail> socialSecurityRuleDetail) {
		this.socialSecurityRuleDetail = socialSecurityRuleDetail;
	}
	
	
	
	
}
