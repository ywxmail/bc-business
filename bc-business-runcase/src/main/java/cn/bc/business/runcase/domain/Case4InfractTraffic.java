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
	public static final String ATTACH_TYPE = Case4InfractTraffic.class
			.getSimpleName();

	private Long chargerId;// 责任人1ID(对应CarMan的ID)
	private String chargerName;// 责任人1姓名
	private Long chargerId2;// 责任人2ID(对应CarMan的ID)
	private String chargerName2;// 责任人2姓名

	private String duty;// 责任
	private String sort;// 性质
	private Float jeom;// 扣分
	private String comment;// 处理意见

	private boolean deliver;// 是否邮递
	private Calendar deliverDate;// 邮递时间
	private boolean sign;// 是否签领
	private Calendar signDate;// 签领时间

	@Column(name = "CHARGER1_ID")
	public Long getChargerId() {
		return chargerId;
	}

	public void setChargerId(Long chargerId) {
		this.chargerId = chargerId;
	}

	@Column(name = "CHARGER1_NAME")
	public String getChargerName() {
		return chargerName;
	}

	public void setChargerName(String chargerName) {
		this.chargerName = chargerName;
	}

	@Column(name = "CHARGER2_ID")
	public Long getChargerId2() {
		return chargerId2;
	}

	public void setChargerId2(Long chargerId2) {
		this.chargerId2 = chargerId2;
	}

	@Column(name = "CHARGER2_NAME")
	public String getChargerName2() {
		return chargerName2;
	}

	public void setChargerName2(String chargerName2) {
		this.chargerName2 = chargerName2;
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
}