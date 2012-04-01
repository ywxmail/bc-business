package cn.bc.business.ownership.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import cn.bc.business.car.domain.Car;
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
	private String nature;// 经营权性质
	private String situation;// 经营权情况
	private String owner;// 车辆产权
	private String description;// 备注
	private Car car;// 车辆

	@OneToOne(fetch = FetchType.EAGER, optional = true)
	@JoinColumn(name = "CAR_ID", referencedColumnName = "ID")
	public Car getCar() {
		return car;
	}

	public void setCar(Car car) {
		this.car = car;
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