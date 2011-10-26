package cn.bc.business.motorcade.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import cn.bc.identity.domain.Actor;
import cn.bc.identity.domain.RichFileEntityImpl;

/**
 * 车辆
 * 
 * @author dragon
 */
@Entity
@Table(name = "BS_MOTORCADE")
public class Motorcade extends RichFileEntityImpl {
	private static final long serialVersionUID = 1L;
	public  static final String KEY_UID = "motorcade.uid";
	/** 类别：分队 */
	public static final int TYPE_TEAM = 0;
	/** 类别：分组 */
	public static final int TYPE_GROUP = 1;

	private Actor unit;// 所属单位
	private Long principalId;// 负责人ID
	private String principalName;// 负责人姓名
	private int type;// 类别：参考常数TYPE_XXX的定义
	private String code;// 编码
	private Motorcade parent;// 所属父类
	private String name;// 简称
	private String color;// 颜色
	private String address;// 地址
	private String phone;// 电话
	private String fax;// 传真
	private String paymentDate;// 缴费日
	private String description;// 备注

	@ManyToOne(fetch = FetchType.EAGER, optional = true)
	@JoinColumn(name = "PID", referencedColumnName = "ID")
	public Motorcade getParent() {
		return parent;
	}

	public void setParent(Motorcade parent) {
		this.parent = parent;
	}

	@Column(name = "TYPE_")
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

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

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "UNIT_ID", referencedColumnName = "ID")
	public Actor getUnit() {
		return unit;
	}

	public void setUnit(Actor unit) {
		this.unit = unit;
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

	@Column(name = "PRINCIPAL_ID")
	public Long getPrincipalId() {
		return principalId;
	}

	public void setPrincipalId(Long principalId) {
		this.principalId = principalId;
	}

	@Column(name = "PRINCIPAL_NAME")
	public String getPrincipalName() {
		return principalName;
	}

	public void setPrincipalName(String principalName) {
		this.principalName = principalName;
	}

	@Column(name = "PAYMENT_DATE")
	public String getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(String paymentDate) {
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