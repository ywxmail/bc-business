/**
 * 
 */
package cn.bc.business.cert.domain;

import java.util.Calendar;

import javax.persistence.MappedSuperclass;

/**
 * 与人有关的证件信息
 * 
 * @author dragon
 */
@MappedSuperclass
public abstract class Cert4Man extends Cert {
	private static final long serialVersionUID = 1L;

	private String address;// 住址
	private String name;// 姓名
	private Integer sex;// 性别：参考ActorDetail类中SEX_*常数的定义
	private Calendar birthdate;// 出生日期
	private String nation;// 国籍、民族

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public Calendar getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(Calendar birthdate) {
		this.birthdate = birthdate;
	}

	public String getNation() {
		return nation;
	}

	public void setNation(String nation) {
		this.nation = nation;
	}
}