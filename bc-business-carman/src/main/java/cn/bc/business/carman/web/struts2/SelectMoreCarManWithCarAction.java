/**
 * 
 */
package cn.bc.business.carman.web.struts2;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.carman.domain.CarMan;
import cn.bc.business.carman.service.CarManService;
import cn.bc.business.web.struts2.FileEntityAction;

/**
 * 根据车辆ID选择多个司机信息
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class SelectMoreCarManWithCarAction extends FileEntityAction<Long, CarMan> {
	public List<CarMan> carMans;
	private CarManService carManService;
	public Long carId;

	@Autowired
	public void setCarManService(CarManService carManService) {
		this.carManService = carManService;
		this.setCrudService(carManService);
	}

	public String selectCarMans() throws Exception {
		carMans = this.carManService.selectAllCarManByCarId(new Long(carId));
		return SUCCESS;
	}
}
