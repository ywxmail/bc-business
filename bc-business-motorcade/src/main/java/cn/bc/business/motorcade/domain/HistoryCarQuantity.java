package cn.bc.business.motorcade.domain;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import cn.bc.core.EntityImpl;

/**
 * 车辆
 * 
 * @author dragon
 */
@Entity
@Table(name = "BS_HISTORY_CAR_QUANTITY")
public class HistoryCarQuantity extends EntityImpl {
	private static final long serialVersionUID = 1L;

	private String year;//年份
	private String month;//月份
	private String carquantity;//车辆数
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getCarquantity() {
		return carquantity;
	}
	public void setCarquantity(String carquantity) {
		this.carquantity = carquantity;
	}
	
}