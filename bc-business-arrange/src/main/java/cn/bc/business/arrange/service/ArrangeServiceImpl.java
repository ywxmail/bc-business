package cn.bc.business.arrange.service;

import cn.bc.business.arrange.dao.ArrangeDao;
import cn.bc.business.arrange.domain.Arrange;
import cn.bc.core.service.DefaultCrudService;

/**
 * 安排Service的实现
 * 
 * @author dragon
 * 
 */
public class ArrangeServiceImpl extends DefaultCrudService<Arrange> implements
		ArrangeService {
	private ArrangeDao arrangeDao;

	public void setArrangeDao(ArrangeDao arrangeDao) {
		this.arrangeDao = arrangeDao;
		this.setCrudDao(arrangeDao);
	}
}
