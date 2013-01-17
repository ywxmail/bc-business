/**
 * 
 */
package cn.bc.business.carman.domain;

import java.util.Calendar;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import cn.bc.identity.domain.RichFileEntityImpl;

/**
 * 司机人意险
 * 
 * @author dragon
 */
@Entity
@Table(name = "BS_CARMAN_RISK")
@Inheritance(strategy = InheritanceType.JOINED)
public class CarManRisk extends RichFileEntityImpl {
	private static final long serialVersionUID = 1L;
	public static final String KEY_UID = CarManRisk.class.getSimpleName();

	/** 购买类型：未知 */
	public static final int BUY_TYPE_NONE = 0;
	/** 购买类型：公司 */
	public static final int BUY_TYPE_COMPANY = 1;
	/** 购买类型：自买 */
	public static final int BUY_TYPE_SELF = 2;

	/** 状态：草稿 */
	public static final int STATUS_DRAFT = -1;
	/** 状态：正常 */
	public static final int STATUS_ENABLED = 0;
	/** 状态：禁用 */
	public static final int STATUS_DISABLED = 1;

	private String code;// 保单号
	private String holder;// 投保人
	private int buyType;// 购买类型：详见BUY_TYPE_XXX常数的定义
	private String company;// 保险公司
	private Calendar startDate;// 开始日期
	private Calendar endDate;// 结束日期
	private Set<CarManRiskItem> items;// 购买的险种
	private Set<CarMan> insurants;// 被投保人

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getHolder() {
		return holder;
	}

	public void setHolder(String holder) {
		this.holder = holder;
	}

	@Column(name = "BUY_TYPE")
	public int getBuyType() {
		return buyType;
	}

	public void setBuyType(int buyType) {
		this.buyType = buyType;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
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

	@OneToMany(mappedBy = "parent", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy(value = "orderNo asc")
	public Set<CarManRiskItem> getItems() {
		return items;
	}

	public void setItems(Set<CarManRiskItem> items) {
		this.items = items;
	}

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "BS_CARMAN_RISK_INSURANT", joinColumns = @JoinColumn(name = "RISK_ID", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "MAN_ID", referencedColumnName = "ID"))
	@OrderBy("orderNo asc")
	public Set<CarMan> getInsurants() {
		return insurants;
	}

	public void setInsurants(Set<CarMan> insurants) {
		this.insurants = insurants;
	}
}