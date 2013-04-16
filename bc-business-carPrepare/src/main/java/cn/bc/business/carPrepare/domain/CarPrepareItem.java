package cn.bc.business.carPrepare.domain;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import cn.bc.core.EntityImpl;

/**
 * 车辆更新项目
 * 
 * @author zxr
 */
@Entity
@Table(name = "BS_CAR_PREPARE_ITEM")
public class CarPrepareItem extends EntityImpl {
	private static final long serialVersionUID = 1L;
	/** 状态：未完成 */
	public static final int STATUS_UNFINISHED = 0;
	/** 状态：已完成 */
	public static final int STATUS_FINISHED = 1;

	private String name;// 名称
	private int order;// 排序号
	private Calendar date;// 更新日期
	private int status;// 状态 : 0-未完成,1-已完成
	private String desc;// 备注
	private CarPrepare carPrepare;// 出车准备

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "ORDER_")
	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	@Column(name = "DATE_")
	public Calendar getDate() {
		return date;
	}

	public void setDate(Calendar date) {
		this.date = date;
	}

	@Column(name = "STATUS_")
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Column(name = "DESC_")
	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "PID", referencedColumnName = "ID")
	public CarPrepare getCarPrepare() {
		return carPrepare;
	}

	public void setCarPrepare(CarPrepare carPrepare) {
		this.carPrepare = carPrepare;
	}

}