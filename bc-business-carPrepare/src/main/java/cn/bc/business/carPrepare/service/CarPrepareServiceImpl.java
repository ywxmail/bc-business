/**
 * 
 */
package cn.bc.business.carPrepare.service;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.business.carPrepare.dao.CarPrepareDao;
import cn.bc.business.carPrepare.domain.CarPrepare;
import cn.bc.core.service.DefaultCrudService;
import cn.bc.docs.service.AttachService;
import cn.bc.identity.service.IdGeneratorService;

/**
 * 出车准备Service的实现
 * 
 * @author zxr
 */
public class CarPrepareServiceImpl extends DefaultCrudService<CarPrepare>
		implements CarPrepareService {
	private CarPrepareDao carPrepareDao;
	private IdGeneratorService idGeneratorService;// 用于生成uid的服务
	private AttachService attachService;// 附件服务

	public CarPrepareDao getCarPrepareDao() {
		return carPrepareDao;
	}

	public void setCarPrepareDao(CarPrepareDao carPrepareDao) {
		this.carPrepareDao = carPrepareDao;
		this.setCrudDao(carPrepareDao);
	}

	@Autowired
	public void setIdGeneratorService(IdGeneratorService idGeneratorService) {
		this.idGeneratorService = idGeneratorService;
	}

	@Autowired
	public void setAttachService(AttachService attachService) {
		this.attachService = attachService;
	}

}