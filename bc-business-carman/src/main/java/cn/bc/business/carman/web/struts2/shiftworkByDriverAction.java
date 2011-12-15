/**
 * 
 */
package cn.bc.business.carman.web.struts2;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.interceptor.RequestAware;
import org.apache.struts2.interceptor.SessionAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.Context;
import cn.bc.business.car.service.CarService;
import cn.bc.business.carman.domain.CarByDriver;
import cn.bc.business.carman.service.CarByDriverService;
import cn.bc.business.carman.service.CarManService;
import cn.bc.identity.domain.Actor;
import cn.bc.identity.domain.ActorHistory;
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
public class shiftworkByDriverAction extends ActionSupport implements
		SystemContext, Context, SessionAware, RequestAware {
	// private static Log logger = LogFactory.getLog(BulletinAction.class);
	private static final long serialVersionUID = 1L;
	public CarByDriverService carByDriverService;
	public CarManService carManService;
	public CarService carService;
	public Map<Long, String> cars;
	public Long classes;// 班次
	public String description;// 备注
	public Calendar fileDate;// 创建时间
	public ActorHistory author;// 创建人
	public Calendar modifiedDate;// 最后修改时间
	public ActorHistory modifier;// 最后修改人
	protected Map<String, Object> session;

	@Autowired
	public void setCarManService(CarManService carManService) {
		this.carManService = carManService;
	}

	@Autowired
	public void CarService(CarService carService) {
		this.carService = carService;
	}

	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public String create() throws Exception {
		SystemContext context = (SystemContext) this.session.get(Context.KEY);
		fileDate = Calendar.getInstance();
		author = context.getUserHistory();
		cars = new HashMap<Long, String>();
		return "create";
	}

	public String edit() throws Exception {

		return "edit";
	}

	public String ids;//
	public Long driverId;//
	public String carIds;//

	public String save() throws Exception {
		// 创建CarByDriver列表
		List<CarByDriver> toSaves = new ArrayList<CarByDriver>();

		this.carByDriverService.save(toSaves);
		return "saveSuccess";
	}

	public <T> T getAttr(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	public Context setAttr(String key, Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	public Actor getUser() {
		// TODO Auto-generated method stub
		return null;
	}

	public ActorHistory getUserHistory() {
		// TODO Auto-generated method stub
		return null;
	}

	public Actor getBelong() {
		// TODO Auto-generated method stub
		return null;
	}

	public Actor getUnit() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasAnyRole(String... roles) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasAnyGroup(String... groups) {
		// TODO Auto-generated method stub
		return false;
	}

	public void setRequest(Map<String, Object> request) {
		// TODO Auto-generated method stub

	}

}
