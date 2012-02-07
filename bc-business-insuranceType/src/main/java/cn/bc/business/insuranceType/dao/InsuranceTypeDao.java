package cn.bc.business.insuranceType.dao;

import java.util.List;
import java.util.Map;

import cn.bc.business.insuranceType.domain.InsuranceType;
import cn.bc.core.dao.CrudDao;

public interface InsuranceTypeDao extends CrudDao<InsuranceType> {
	/**
	 * 获取险种表中所有的模板
	 * 
	 * @return InsuranceType ID,名称
	 */
	public List<Map<String,String>> findEnabled4Option();
}
