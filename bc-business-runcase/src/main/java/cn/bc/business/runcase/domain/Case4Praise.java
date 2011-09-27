/**
 * 
 */
package cn.bc.business.runcase.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 表扬
 * 
 * @author dragon
 */
@Entity
@Table(name = "BS_CASE_PRAISE")
public class Case4Praise extends CaseBase4AdviceAndPraise {
	private static final long serialVersionUID = 1L;
	public static final String ATTACH_TYPE = Case4Praise.class.getSimpleName();
	private String praiseType;// 表扬类型
	private Float rewardMoney;// 获奖金额

	@Column(name = "PRAISE_TYPE")
	public String getPraiseType() {
		return praiseType;
	}

	public void setPraiseType(String praiseType) {
		this.praiseType = praiseType;
	}

	@Column(name = "REWARD_MONEY")
	public Float getRewardMoney() {
		return rewardMoney;
	}

	public void setRewardMoney(Float rewardMoney) {
		this.rewardMoney = rewardMoney;
	}
}