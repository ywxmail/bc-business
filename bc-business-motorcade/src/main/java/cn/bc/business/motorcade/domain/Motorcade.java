package cn.bc.business.motorcade.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import cn.bc.core.EntityImpl;
import cn.bc.identity.domain.RichFileEntity;
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

	/** 状态：已禁用 *//*
	public static final int STATUS_FORBIDDEN = 0;
	*//** 状态：启用中*//*
	public static final int STATUS_START = 1;
	*//** 状态：已删除*//*
	public static final int STATUS_DELETED = 2;*/
	
	
	private String code;//编码
	private String addrevation;//简称
	private String benelux;//全称
	private String company;//公司
	private String colour;//颜色
	private String address;//地址
	private String principal;//负责人
	private String phone;//电话
	private String fax;//传真
	private Date payment_date;//缴费日期
	private String description;// 备注
	private Date lastupdated_date;//最近更新
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getAddrevation() {
		return addrevation;
	}
	public void setAddrevation(String addrevation) {
		this.addrevation = addrevation;
	}
	public String getBenelux() {
		return benelux;
	}
	public void setBenelux(String benelux) {
		this.benelux = benelux;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getColour() {
		return colour;
	}
	public void setColour(String colour) {
		this.colour = colour;
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
	public Date getPayment_date() {
		return payment_date;
	}
	public void setPayment_date(Date payment_date) {
		this.payment_date = payment_date;
	}
	public Date getLastupdated_date() {
		return lastupdated_date;
	}
	public void setLastupdated_date(Date lastupdated_date) {
		this.lastupdated_date = lastupdated_date;
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