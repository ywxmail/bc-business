/**
 * 
 */
package cn.bc.business.contract.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 合同与车辆的关联关系
 * 
 * @author dragon
 */
@Entity
@Table(name = "BS_CAR_CONTRACT")
public class ContractCarRelation implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long contractId;// 合同id
	private Long carId;// 车辆id

	public ContractCarRelation() {
	}

	public ContractCarRelation(Long contractId, Long carId) {
		this.contractId = contractId;
		this.carId = carId;
	}

	@Id
	@Column(name = "CONTRACT_ID")
	public Long getContractId() {
		return contractId;
	}

	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}

	@Id
	@Column(name = "CAR_ID")
	public Long getCarId() {
		return carId;
	}

	public void setCarId(Long carId) {
		this.carId = carId;
	}

	@Override
	public String toString() {
		return "{contractId:" + contractId + ",carId:" + carId + "}";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return super.equals(obj);

		ContractCarRelation other = (ContractCarRelation) obj;
		if (this.contractId != null
				&& this.contractId.equals(other.getContractId())
				&& this.carId != null && this.carId.equals(other.getCarId()))
			return true;
		else
			return super.equals(obj);
	}

	public boolean equals(Long contractId, Long carId) {
		return this.contractId != null && this.contractId.equals(contractId)
				&& this.carId != null && this.carId.equals(carId);
	}
}