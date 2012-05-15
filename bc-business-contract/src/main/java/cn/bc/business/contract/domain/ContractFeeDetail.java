package cn.bc.business.contract.domain;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import cn.bc.core.EntityImpl;
import cn.bc.core.util.DateUtils;

/**
 * 合同收费明细
 * 
 * @author zxr
 */
@Entity
@Table(name = "BS_CONTRACT_FEE_DETAIL")
public class ContractFeeDetail extends EntityImpl {
	private static final long serialVersionUID = 1L;

	/** 收费方式：每月 */
	public static final int PAY_TYPE_MONTH = 1;
	/** 收费方式：每季 */
	public static final int PAY_TYPE_SEASON = 2;
	/** 收费方式：每年 */
	public static final int PAY_TYPE_YEAR = 3;
	/** 收费方式:一次性 */
	public static final int PAY_TYPE_ALL = 4;

	private String name;// 项目
	private float price;// 金额
	private int count;// 数量
	private int payType;// 收费方式
	private Calendar startDate;// 开始时间
	private Calendar endDate;// 结束时间
	private String description;// 备注
	private int orderNo;// 排序号
	private Contract contract;// 合同ID

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	@Column(name = "COUNT_")
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@Column(name = "PAY_TYPE")
	public int getPayType() {
		return payType;
	}

	public void setPayType(int payType) {
		this.payType = payType;
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
		DateUtils.setToMaxTime(this.endDate);
	}

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "PID", referencedColumnName = "ID")
	public Contract getContract() {
		return contract;
	}

	public void setContract(Contract contract) {
		this.contract = contract;
	}

	@Column(name = "ORDER_")
	public int getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(int orderNo) {
		this.orderNo = orderNo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "DESC_")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}