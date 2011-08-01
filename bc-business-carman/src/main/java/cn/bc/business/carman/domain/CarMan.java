/**
 * 
 */
package cn.bc.business.carman.domain;

import java.util.Calendar;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import cn.bc.business.cert.domain.Cert;
import cn.bc.identity.domain.RichFileEntityImpl;

/**
 * 司机责任人
 * 
 * @author dragon
 */
@Entity
@Table(name = "BS_CARMAN")
public class CarMan extends RichFileEntityImpl {
	private static final long serialVersionUID = 1L;
	/**类别：司机*/
	public static final Integer TYPE_DRIVER = 0;
	/**类别：责任人*/
	public static final Integer TYPE_CHARGER = 1;
	/**类别：司机和责任人*/
	public static final Integer TYPE_DRIVER_AND_CHARGER = 2;

	private Integer type;// 类别
	private String orderNo;// 排序号
	private String name;// 姓名
	private Integer sex;// 性别：参考ActorDetail类中SEX_*常数的定义
	private String origin;// 籍贯
	private String region;// 区域
	private String houseType;// 户口类型
	
	private Calendar birthdate;// 出生日期
	private Calendar workDate;// 入职日期
	private String formerUnit;// 入职原单位
	private String address;// 身份证住址
	private String address1;// 暂住地址
	private String phone;// 电话1
	private String phone1;// 电话2
	
	private Set<Cert> certs;//拥有的证件

	@OneToMany(fetch=FetchType.LAZY)
    @JoinTable(name="BS_CARMAN_CERT",
        joinColumns=
            @JoinColumn(name="MAN_ID", referencedColumnName="ID"),
        inverseJoinColumns=
            @JoinColumn(name="CERT_ID", referencedColumnName="ID")
        )
    @OrderBy("certCode asc")
	public Set<Cert> getCerts() {
		return certs;
	}

	public void setCerts(Set<Cert> certs) {
		this.certs = certs;
	}

	@Column(name = "ORDER_")
	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "WORK_DATE")
	public Calendar getWorkDate() {
		return workDate;
	}

	public void setWorkDate(Calendar workDate) {
		this.workDate = workDate;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	@Column(name = "REGION_")
	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	@Column(name = "HOUSE_TYPE")
	public String getHouseType() {
		return houseType;
	}

	public void setHouseType(String houseType) {
		this.houseType = houseType;
	}

	public Calendar getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(Calendar birthdate) {
		this.birthdate = birthdate;
	}

	@Column(name = "FORMER_UNIT")
	public String getFormerUnit() {
		return formerUnit;
	}

	public void setFormerUnit(String formerUnit) {
		this.formerUnit = formerUnit;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPhone1() {
		return phone1;
	}

	public void setPhone1(String phone1) {
		this.phone1 = phone1;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	@Column(name = "TYPE_")
	public Integer getType() {
		return type;
	}

	public Integer getSex() {
		return sex;
	}
}