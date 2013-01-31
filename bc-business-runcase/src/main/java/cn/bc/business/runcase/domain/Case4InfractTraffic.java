/**
 * 
 */
package cn.bc.business.runcase.domain;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 交通违章
 * 
 * @author dragon
 */
@Entity
@Table(name = "BS_CASE_INFRACT_TRAFFIC")
public class Case4InfractTraffic extends CaseBase {
	private static final long serialVersionUID = 1L;
	public static final String KEY_CODE = "runcase.code";
	public static final String ATTACH_TYPE = Case4InfractTraffic.class
			.getSimpleName();

	private String charger;// 责任人

	private String duty;// 责任
	private String sort;// 性质
	private Float jeom;// 扣分
	private String comment;// 处理意见

	private boolean deliver;// 是否邮递
	private Calendar deliverDate;// 邮递时间
	private boolean sign;// 是否签领
	private Calendar signDate;// 签领时间
	private String infractCode;// 违法代码
	private Float penalty;// 罚款金额

	public String getCharger() {
		return charger;
	}

	public void setCharger(String charger) {
		this.charger = charger;
	}

	public String getDuty() {
		return duty;
	}

	public void setDuty(String duty) {
		this.duty = duty;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public Float getJeom() {
		return jeom;
	}

	public void setJeom(Float jeom) {
		this.jeom = jeom;
	}

	@Column(name = "COMMENT_")
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Column(name = "IS_DELIVER")
	public boolean isDeliver() {
		return deliver;
	}

	public void setDeliver(boolean deliver) {
		this.deliver = deliver;
	}

	@Column(name = "IS_SIGN")
	public boolean isSign() {
		return sign;
	}

	public void setSign(boolean sign) {
		this.sign = sign;
	}

	@Column(name = "DELIVER_DATE")
	public Calendar getDeliverDate() {
		return deliverDate;
	}

	public void setDeliverDate(Calendar deliverDate) {
		this.deliverDate = deliverDate;
	}

	@Column(name = "SIGN_DATE")
	public Calendar getSignDate() {
		return signDate;
	}

	public void setSignDate(Calendar signDate) {
		this.signDate = signDate;
	}

	@Column(name = "INFRACT_CODE")
	public String getInfractCode() {
		return infractCode;
	}

	public void setInfractCode(String infractCode) {
		this.infractCode = infractCode;
	}

	public Float getPenalty() {
		return penalty;
	}

	public void setPenalty(Float penalty) {
		this.penalty = penalty;
	}

}