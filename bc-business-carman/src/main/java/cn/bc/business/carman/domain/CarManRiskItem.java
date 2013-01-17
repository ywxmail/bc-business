package cn.bc.business.carman.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import cn.bc.core.EntityImpl;

/**
 * 司机人意险购买的险种
 * 
 * @author dragon
 */
@Entity
@Table(name = "BS_CARMAN_RISK_ITEM")
public class CarManRiskItem extends EntityImpl {
	private static final long serialVersionUID = 1L;
	private String name;// 险种名称
	private String coverage;// 保额
	private String premium;// 保费
	private String description;// 备注
	private int orderNo;// 排序号
	private CarManRisk parent;// 所属保单

	@Column(name = "ORDER_")
	public int getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(int orderNo) {
		this.orderNo = orderNo;
	}

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "PID", referencedColumnName = "ID")
	public CarManRisk getParent() {
		return parent;
	}

	public void setParent(CarManRisk parent) {
		this.parent = parent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCoverage() {
		return coverage;
	}

	public void setCoverage(String coverage) {
		this.coverage = coverage;
	}

	@Column(name = "DESC_")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPremium() {
		return premium;
	}

	public void setPremium(String premium) {
		this.premium = premium;
	}
}