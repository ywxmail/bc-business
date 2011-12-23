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
	public static final String KEY_CODE = "runcase.code";
	private String praiseType;// 表扬类型
	private float rewardMoney;// 获奖金额

	@Column(name = "PRAISE_TYPE")
	public String getPraiseType() {
		return praiseType;
	}

	public void setPraiseType(String praiseType) {
		this.praiseType = praiseType;
	}

	@Column(name = "REWARD_MONEY")
	public float getRewardMoney() {
		return rewardMoney;
	}

	public void setRewardMoney(float rewardMoney) {
		this.rewardMoney = rewardMoney;
	}

}