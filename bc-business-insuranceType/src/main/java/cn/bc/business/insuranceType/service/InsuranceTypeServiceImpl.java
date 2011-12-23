package cn.bc.business.insuranceType.service;

import cn.bc.business.insuranceType.dao.InsuranceTypeDao;
import cn.bc.business.insuranceType.domain.InsuranceType;
import cn.bc.core.service.DefaultCrudService;

/**
 * 车辆保单Service的实现
 * 
 * @author zxr
 * 
 */
public class InsuranceTypeServiceImpl extends DefaultCrudService<InsuranceType>
		implements InsuranceTypeService {
	private InsuranceTypeDao insuranceTypeDao;

	public InsuranceTypeDao getInsuranceTypeDao() {
		return insuranceTypeDao;
	}

	public void setInsuranceTypeDao(InsuranceTypeDao insuranceTypeDao) {
		this.insuranceTypeDao = insuranceTypeDao;
		this.setCrudDao(insuranceTypeDao);
	}

}
