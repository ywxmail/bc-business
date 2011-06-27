/**
 * 
 */
package cn.bc.business.car.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

import cn.bc.identity.domain.FileEntity;

/**
 * 车辆
 * 
 * @author dragon
 */
@Entity
@Table(name = "BS_CAR")
public class Car extends FileEntity {
	private static final long serialVersionUID = 1L;

	private String description;// 备注

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}