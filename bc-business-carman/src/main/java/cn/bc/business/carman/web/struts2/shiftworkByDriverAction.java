/**
 * 
 */
package cn.bc.business.carman.web.struts2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.car.service.CarService;
import cn.bc.business.carman.domain.CarByDriver;
import cn.bc.business.carman.service.CarByDriverService;
import cn.bc.business.carman.service.CarManService;
import cn.bc.identity.web.SystemContext;

import com.opensymphony.xwork2.ActionSupport;

/**
 * 司机营运车辆Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class shiftworkByDriverAction extends
ActionSupport {
	// private static Log logger = LogFactory.getLog(BulletinAction.class);
	private static final long serialVersionUID = 1L;
	public CarByDriverService carByDriverService;
	public CarManService carManService;
	public CarService carService;
	public Map<Long, String> cars;
	public Long classes;//班次
	public String description;// 备注

	@Autowired
	public void setCarManService(CarManService carManService) {
		this.carManService = carManService;
	}

	@Autowired
	public void CarService(CarService carService) {
		this.carService = carService;
	}

	
	public String create() throws Exception {
		
		cars=new HashMap<Long, String>();
		return "create";
	}

	
	public String edit() throws Exception {
	
		
		return "edit";
	}

	public String ids ;//
	public Long driverId ;//
	public String carIds;//

	public String save() throws Exception {
		//创建CarByDriver列表
		List<CarByDriver> toSaves = new ArrayList<CarByDriver>();
		
		
		this.carByDriverService.save(toSaves);
		return "saveSuccess";
	}
}
