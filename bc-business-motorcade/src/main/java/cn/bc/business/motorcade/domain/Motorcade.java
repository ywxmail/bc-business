package cn.bc.business.motorcade.domain;

import java.util.Calendar;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import cn.bc.identity.domain.RichFileEntityImpl;

/**
 * 车辆
 * 
 * @author dragon
 */
@Entity
@Table(name = "BS_MOTORCADE")
public class Motorcade extends RichFileEntityImpl   {
	private static final long serialVersionUID = 1L;
	
	private String code;//编码
	private String name;//简称
	private String fullName;//全称
	private String company;//公司
	private String color;//颜色
	private String address;//地址
	private String principal;//负责人
	private String phone;//电话
	private String fax;//传真
	private Calendar paymentDate;//缴费日期
	private String description;// 备注
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPrincipal() {
		return principal;
	}
	public void setPrincipal(String principal) {
		this.principal = principal;
	}
	@Column(name = "PAYMENT_DATE")
	public Calendar getPaymentDate() {
		return paymentDate;
	}
	public void setPaymentDate(Calendar paymentDate) {
		this.paymentDate = paymentDate;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getFax() {
		return fax;
	}
	public void setFax(String fax) {
		this.fax = fax;
	}
	@Column(name = "DESC_")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}