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
import cn.bc.core.RichEntityImpl;

/**
 * 司机责任人
 * 
 * @author dragon
 */
@Entity
@Table(name = "BS_CARMAN")
public class CarMan extends RichEntityImpl {
	private static final long serialVersionUID = 1L;
	/**类别：司机*/
	public static final int TYPE_DRIVER = 0;
	/**类别：责任人*/
	public static final int TYPE_CHARGER = 1;
	/**类别：司机和责任人*/
	public static final int TYPE_DRIVER_AND_CHARGER = 2;

	private int type;// 类别
	private String code;// 编号
	private String orderNo;// 排序号
	private String name;// 姓名
	private int sex;// 性别
	private Calendar workDate;// 入职日期
	
	private Set<Cert> certs;//拥有的证件

	@OneToMany(fetch=FetchType.LAZY)
    @JoinTable(name="BS_CARMAN_CERT",
        joinColumns=
            @JoinColumn(name="MAN_ID", referencedColumnName="ID"),
        inverseJoinColumns=
            @JoinColumn(name="CERT_ID", referencedColumnName="ID")
        )
    @OrderBy("code asc")
	public Set<Cert> getCerts() {
		return certs;
	}

	public void setCerts(Set<Cert> certs) {
		this.certs = certs;
	}

	@Column(name = "TYPE_")
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Column(name = "ORDER_")
	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
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

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	@Column(name = "WORK_DATE")
	public Calendar getWorkDate() {
		return workDate;
	}

	public void setWorkDate(Calendar workDate) {
		this.workDate = workDate;
	}
}