package cn.bc.business.charger.domain;

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
@Table(name = "BS_CHARGER")
public class Charger extends RichFileEntityImpl   {
	private static final long serialVersionUID = 1L;

	private String card;//身份证
	private String name;//姓名
	private String sex;//性别
	private String orderId;//排序号
	private String idAddress;//身份证地址
	private String TWIC;//资格证
	private String phone;//电话
	private String unit;//分支机构
	private String area;//区域
	private String temporaryAddress;//暂停地址
	private String nativePlace;//籍贯
	private Calendar brithdate;//出生日期
	private String description;// 备注
	public String getCard() {
		return card;
	}
	public void setCard(String card) {
		this.card = card;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getIdAddress() {
		return idAddress;
	}
	public void setIdAddress(String idAddress) {
		this.idAddress = idAddress;
	}
	
	public String getTWIC() {
		return TWIC;
	}
	public void setTWIC(String tWIC) {
		TWIC = tWIC;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getTemporaryAddress() {
		return temporaryAddress;
	}
	public void setTemporaryAddress(String temporaryAddress) {
		this.temporaryAddress = temporaryAddress;
	}
	public String getNativePlace() {
		return nativePlace;
	}
	public void setNativePlace(String nativePlace) {
		this.nativePlace = nativePlace;
	}
	
	public Calendar getBrithdate() {
		return brithdate;
	}
	public void setBrithdate(Calendar brithdate) {
		this.brithdate = brithdate;
	}
	@Column(name = "DESC_")
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
}