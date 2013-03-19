package cn.bc.business.industryrank.service;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.business.industryrank.dao.IROriginDao;
import cn.bc.business.industryrank.domain.IROrigin;
import cn.bc.core.service.DefaultCrudService;

/**
 * 行业排名原始数据 service 实现
 * 
 * @author dragon
 * 
 */
public class IROriginServiceImpl extends DefaultCrudService<IROrigin> implements
		IROriginService {
	private IROriginDao irOriginDao;

	@Autowired
	public void setIrOriginDao(IROriginDao irOriginDao) {
		this.irOriginDao = irOriginDao;
	}
}
