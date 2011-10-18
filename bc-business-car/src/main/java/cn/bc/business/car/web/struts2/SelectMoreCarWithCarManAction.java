/**
 * 
 */
package cn.bc.business.car.web.struts2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.car.domain.Car;
import cn.bc.business.car.service.CarService;
import cn.bc.business.web.struts2.FileEntityAction;

/**
 * 根据司机ID选择多辆车信息
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class SelectMoreCarWithCarManAction extends FileEntityAction<Long, Car> {
	public List<Car> cars;
	private CarService carService;
	public Long carManId;

	@Autowired
	public void setCarService(CarService carService) {
		this.carService = carService;
		this.setCrudService(carService);
	}

	public String selectCars() throws Exception {
		cars = this.carService.selectAllCarByCarManId(new Long(carManId));
		return SUCCESS;
	}
}
