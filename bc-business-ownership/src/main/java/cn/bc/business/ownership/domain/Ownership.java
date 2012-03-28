package cn.bc.business.ownership.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

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
	private Long id;//车辆Id
	private String nature;//经营权性质
	private String situation;//经营权情况
	private String owner;//车辆产权
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	

}