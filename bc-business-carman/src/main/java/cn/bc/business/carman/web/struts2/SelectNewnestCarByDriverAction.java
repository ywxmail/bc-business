/**
 * 
 */
package cn.bc.business.carman.web.struts2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.carman.domain.CarByDriverHistory;
import cn.bc.business.carman.service.CarByDriverHistoryService;
import cn.bc.web.ui.json.Json;

import com.opensymphony.xwork2.ActionSupport;

/**
 * 根据司机查找回最新的迁移记录的营运车辆Action
 * 
 * @author zxr
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class SelectNewnestCarByDriverAction extends ActionSupport {

	private static final long serialVersionUID = 1L;
	public CarByDriverHistoryService carByDriverHistoryService;
	public Long carManId;

	@Autowired
	public void setCarByDriverHistoryService(
			CarByDriverHistoryService carByDriverHistoryService) {
		this.carByDriverHistoryService = carByDriverHistoryService;
	}

	public Json json;

	public String findNewnestCarByDriver() {
		CarByDriverHistory carByDriverHistory = this.carByDriverHistoryService
				.findNewestCarByDriverHistory(carManId);
		json = new Json();
		if (carByDriverHistory != null) {
			if (carByDriverHistory.getToCar() != null) {
				// 执行转车操作后执行公司到公司，注销未有去向，交回未注销取回最新的迁移记录
				json.put("fromCarId", carByDriverHistory.getToCar().getId());
				json.put("plate", carByDriverHistory.getToCar().getPlateType()
						+ "." + carByDriverHistory.getToCar().getPlateNo());
				json.put("fromMotorcadeId",
						carByDriverHistory.getToMotorcadeId());
				json.put("fromclasses", carByDriverHistory.getToClasses());
			} else {
				// 执行交回未注销操作后执行公司到公司，注销未有去向，交回未注销取回最新的迁移记录
				json.put("fromCarId", carByDriverHistory.getFromCar().getId());
				json.put("plate", carByDriverHistory.getFromCar()
						.getPlateType()
						+ "."
						+ carByDriverHistory.getFromCar().getPlateNo());
				json.put("fromMotorcadeId",
						carByDriverHistory.getFromMotorcadeId());
				json.put("fromclasses", carByDriverHistory.getFromClasses());
			}
		}
		return "json";
	}
}
