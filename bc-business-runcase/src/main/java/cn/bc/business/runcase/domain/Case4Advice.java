/**
 * 
 */
package cn.bc.business.runcase.domain;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 投诉与建议
 * 
 * @author dragon
 */
@Entity
@Table(name = "BS_CASE_ADVICE")
public class Case4Advice extends CaseBase4AdviceAndPraise {
	private static final long serialVersionUID = 1L;
	public static final String ATTACH_TYPE = Case4Advice.class.getSimpleName();
	/** 投诉还是建议：投诉 */
	public static final int ADVICE_TYPE_COMPLAIN = 0;
	/** 投诉还是建议：建议 */
	public static final int ADVICE_TYPE_SUGGEST = 1;

	private int adviceType;// 标识是投诉还是建议:见 ADVICE_TYPE_XXX 常数的定义
	private String receiveCode;// 受理号
	private String duty;// 责任
	private String extent;// 程度
	private Calendar deliverDate;// 结案书交回日期
	private boolean invalid;// 是否无效

	@Column(name = "ADVICE_TYPE")
	public int getAdviceType() {
		return adviceType;
	}

	public void setAdviceType(int adviceType) {
		this.adviceType = adviceType;
	}

	@Column(name = "RECEIVE_CODE")
	public String getReceiveCode() {
		return receiveCode;
	}

	public void setReceiveCode(String receiveCode) {
		this.receiveCode = receiveCode;
	}

	public String getDuty() {
		return duty;
	}

	public void setDuty(String duty) {
		this.duty = duty;
	}

	public String getExtent() {
		return extent;
	}

	public void setExtent(String extent) {
		this.extent = extent;
	}

	@Column(name = "DELIVER_DATE")
	public Calendar getDeliverDate() {
		return deliverDate;
	}

	public void setDeliverDate(Calendar deliverDate) {
		this.deliverDate = deliverDate;
	}

	@Column(name = "IS_INVALID")
	public boolean isInvalid() {
		return invalid;
	}

	public void setInvalid(boolean invalid) {
		this.invalid = invalid;
	}

}