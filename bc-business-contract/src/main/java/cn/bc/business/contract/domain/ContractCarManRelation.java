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
 * 合同与司机、责任人的关联关系
 * 
 * @author dragon
 */
@Entity
@Table(name = "BS_CARMAN_CONTRACT")
public class ContractCarManRelation implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long contractId;// 合同id
	private Long carManId;// 司机、责任人id

	public ContractCarManRelation() {
	}

	public ContractCarManRelation(Long contractId, Long carManId) {
		this.contractId = contractId;
		this.carManId = carManId;
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
	@Column(name = "MAN_ID")
	public Long getCarManId() {
		return carManId;
	}

	public void setCarManId(Long carManId) {
		this.carManId = carManId;
	}

	@Override
	public String toString() {
		return "{contractId:" + contractId + ",carManId:" + carManId + "}";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return super.equals(obj);

		ContractCarManRelation other = (ContractCarManRelation) obj;
		if (this.contractId != null
				&& this.contractId.equals(other.getContractId())
				&& this.carManId != null
				&& this.carManId.equals(other.getCarManId()))
			return true;
		else
			return super.equals(obj);
	}

	public boolean equals(Long contractId, Long carManId) {
		return this.contractId != null && this.contractId.equals(contractId)
				&& this.carManId != null && this.carManId.equals(carManId);
	}
}