package cn.bc.business.motorcade.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import cn.bc.identity.domain.FileEntityImpl;

/**
 * 车辆
 * 
 * @author dragon
 */
@Entity
@Table(name = "BS_MOTORCADE_CARQUANTITY")
public class HistoryCarQuantity extends FileEntityImpl {
	private static final long serialVersionUID = 1L;

	private int year;// 年份
	private int month;// 月份
	private int day;// 日份
	private int quantity;// 车辆数
	private Motorcade motorcade;// 所属车队

	@Column(name = "YEAR_")
	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	@Column(name = "MONTH_")
	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}
	
	@Column(name = "DAY_")
	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	@Column(name = "QUANTITY")
	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "MOTORCADE_ID", referencedColumnName = "ID")
	public Motorcade getMotorcade() {
		return motorcade;
	}

	public void setMotorcade(Motorcade motorcade) {
		this.motorcade = motorcade;
	}
}