package cn.bc.business.ownership.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import cn.bc.BCConstants;
import cn.bc.identity.domain.FileEntityImpl;

/**
 * 车辆经经营权
 * 
 * @author zxr
 */
@Entity
@Table(name = "BS_CAR_OWNERSHIP")
public class Ownership extends FileEntityImpl {
	private static final long serialVersionUID = 1L;
	private int status = BCConstants.STATUS_ENABLED;
	private String number;// 经营权证号
	private String nature;// 经营权性质
	private String situation;// 经营权情况
	private String source;// 经营权来源
	private String owner;// 车辆产权
	private String description;// 备注

	// private Car car;// 车辆

	// @OneToOne(fetch = FetchType.EAGER, optional = true)
	// @JoinColumn(name = "CAR_ID", referencedColumnName = "ID")
	// public Car getCar() {
	// return car;
	// }
	//
	// public void setCar(Car car) {
	// this.car = car;
	// }
	@Column(name = "STATUS_")
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Column(name = "NUMBER_")
	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getNature() {
		return nature;
	}

	public void setNature(String nature) {
		this.nature = nature;
	}

	public String getSituation() {
		return situation;
	}

	public void setSituation(String situation) {
		this.situation = situation;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	@Column(name = "OWNER_")
	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	@Column(name = "DESC_")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}