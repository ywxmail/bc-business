/**
 * 
 */
package cn.bc.business.carman.web.struts2;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.car.service.CarService;
import cn.bc.business.carman.domain.CarByDriverHistory;
import cn.bc.business.carman.service.CarByDriverHistoryService;
import cn.bc.business.carman.service.CarByDriverService;
import cn.bc.business.carman.service.CarManService;
import cn.bc.business.web.struts2.FileEntityAction;

/**
 * 司机营运车辆Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class ShiftworkByDriverAction extends
		FileEntityAction<Long, CarByDriverHistory> {
	// private static Log logger = LogFactory.getLog(BulletinAction.class);
	private static final long serialVersionUID = 1L;
	public CarByDriverService carByDriverService;
	public CarByDriverHistoryService carByDriverHistoryService;
	public CarManService carManService;
	public CarService carService;
	public Map<Long, String> cars;
	public Long carManId;
	public int moveType;// 迁移类型
	public String shiftwork;// 顶班车辆

	@Autowired
	public void setCarManService(CarManService carManService) {
		this.carManService = carManService;
	}

	@Autowired
	public void CarService(CarService carService) {
		this.carService = carService;
	}

	@Autowired
	public void setCarByDriverHistoryService(
			CarByDriverHistoryService carByDriverHistoryService) {
		this.carByDriverHistoryService = carByDriverHistoryService;
		this.setCrudService(carByDriverHistoryService);
	}

	public void setSession(Map<String, Object> session) {
		this.session = session;
	}


	@Override
	protected void afterCreate(CarByDriverHistory entity) {
		super.afterCreate(entity);
		// 设置迁移类型为顶班
		this.getE().setMoveType(CarByDriverHistory.MOVETYPE_DINGBAN);
		// 顶班车辆
		cars = new HashMap<Long, String>();
		if (carManId != null) {
			this.getE().setDriver(this.carManService.load(carManId));
		}
	}


	public String save() throws Exception {
		// 创建CarByDriver列表
		// List<CarByDriver> toSaves = new ArrayList<CarByDriver>();
		this.beforeSave(this.getE());
		this.getE().setFromCar(null);

		// 将顶班车辆组装成字符串赋值给shiftwork字段：plate1,id1;plate2,id2;...
		String shiftwork = this.getE().getShiftwork();
		String[] shiftworks = shiftwork.split(";");
		Long[] carIds = new Long[shiftworks.length];
			for (int i = 0; i < shiftworks.length; i++) {
				carIds[i] = new Long(shiftworks[i].split(",")[1]);
				
			}
			this.carByDriverHistoryService.saveShiftwork(this.getE(), carIds);
		return "saveSuccess";
	}

}
