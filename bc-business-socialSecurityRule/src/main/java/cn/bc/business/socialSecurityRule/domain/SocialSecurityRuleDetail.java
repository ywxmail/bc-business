package cn.bc.business.socialSecurityRule.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import cn.bc.core.EntityImpl;
/**
 * 社保规则保险明细
 * 
 * @author admin
 *
 */

@Entity
@Table(name = "BS_SOCIALSECURITYRULE_DETAIL")
public class SocialSecurityRuleDetail extends EntityImpl {
	private static final long serialVersionUID = 1L;

	private SocialSecurityRule socialSecurityRule;//社保收费规则
	private String name;//名称
	private Float unitRate;//单位缴率
	private Float personalRate;//个人缴率
	private Float baseNumber;//基数
	private String desc;//描述
	
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "PID", referencedColumnName = "ID")
	public SocialSecurityRule getSocialSecurityRule() {
		return socialSecurityRule;
	}
	public void setSocialSecurityRule(SocialSecurityRule socialSecurityRule) {
		this.socialSecurityRule = socialSecurityRule;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Column(name = "UNIT_RATE")
	public Float getUnitRate() {
		return unitRate;
	}
	public void setUnitRate(Float unitRate) {
		this.unitRate = unitRate;
	}
	
	@Column(name = "PERSONAL_RATE")
	public Float getPersonalRate() {
		return personalRate;
	}
	public void setPersonalRate(Float personalRate) {
		this.personalRate = personalRate;
	}
	
	@Column(name = "BASE_NUMBER")
	public Float getBaseNumber() {
		return baseNumber;
	}
	public void setBaseNumber(Float baseNumber) {
		this.baseNumber = baseNumber;
	}
	
	@Column(name = "DESC_")
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	
	
	
}
