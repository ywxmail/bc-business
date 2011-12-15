package cn.bc.business.arrange.service;

import cn.bc.business.arrange.dao.SignRecordDao;
import cn.bc.business.arrange.domain.SignRecord;
import cn.bc.core.service.DefaultCrudService;

/**
 * 签到记录Service的实现
 * 
 * @author dragon
 * 
 */
public class SignRecordServiceImpl extends DefaultCrudService<SignRecord>
		implements SignRecordService {
	private SignRecordDao signRecordDao;

	public void setSignRecordDao(SignRecordDao signRecordDao) {
		this.signRecordDao = signRecordDao;
		this.setCrudDao(signRecordDao);
	}
}
