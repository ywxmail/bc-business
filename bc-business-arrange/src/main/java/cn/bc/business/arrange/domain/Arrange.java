package cn.bc.business.arrange.domain;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import cn.bc.identity.domain.Actor;
import cn.bc.identity.domain.RichFileEntityImpl;

/**
 * 安排
 * 
 * @author dragon
 */
@Entity
@Table(name = "BS_ARRANGE")
public class Arrange extends RichFileEntityImpl {
	private static final long serialVersionUID = 1L;
	public  static final String KEY_UID = "arrange.uid";
	
	/** 类别：司机安全学习 */
	public static final int TYPE_DRIVER = 0;
	/** 类别：车辆回场检 */
	public static final int TYPE_CAR = 1;
	/** 类别：会议安排 */
	public static final int TYPE_MEETING = 2;

	private Actor unit;// 所属单位
	private int type;// 类别：参考常数TYPE_XXX的定义
	private String subject;// 标题
	private String description;// 备注
	private Calendar startDate;// 开始时间
	private Calendar endDate;// 结束时间
	private int year;// 所属年份,4位数字
	private int month;// 所属月份，1～12
	
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "UNIT_ID", referencedColumnName = "ID")
	public Actor getUnit() {
		return unit;
	}
	
	public void setUnit(Actor unit) {
		this.unit = unit;
	}

	@Column(name = "TYPE_")
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
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

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	@Column(name = "DESC_")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}