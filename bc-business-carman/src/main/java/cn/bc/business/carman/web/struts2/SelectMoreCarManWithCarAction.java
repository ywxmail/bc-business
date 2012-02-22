/**
 * 
 */
package cn.bc.business.carman.web.struts2;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
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
public class SelectMoreCarManWithCarAction extends
		FileEntityAction<Long, CarMan> {

	private static final long serialVersionUID = 1L;
	public List<CarMan> carMans;
	public HashMap<String,String> infoMap;
	
	private CarManService carManService;
	public Long carId;

	@Autowired
	public void setCarManService(CarManService carManService) {
		this.carManService = carManService;
		this.setCrudService(carManService);
	}

	public String selectCarMans() throws Exception {
		carMans = this.carManService.selectAllCarManByCarId(new Long(carId));
		infoMap = new HashMap<String, String>();
		if(carMans.size() > 0){
			for(CarMan man : carMans){
				infoMap.put(man.getName(), 
				isNullObject(man.getId())+isNullObject(man.getCert4FWZG())+isNullObject(man.getRegion())
				+isNullObject(man.getOrigin())+isNullObject(calendarToString(man.getBirthdate()))+isNullObject(calendarToString(man.getWorkDate()))
				+man.getStatus()
				);
			}
		}
		return SUCCESS;
	}
	
	public String isNullObject(Object obj) {
		if (null != obj && obj.toString().length() > 0) {
			return obj+",";
		} else {
			return "";
		}
	}
	
	public String calendarToString(Calendar obj){
		Calendar calendar = obj;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = df.format(calendar.getTime());
		return dateStr;
	}
}
