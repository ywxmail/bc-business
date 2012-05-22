/**
 * 
 */
package cn.bc.business.fee.web.struts2;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.business.OptionConstants;
import cn.bc.business.car.domain.Car;
import cn.bc.business.car.service.CarService;
import cn.bc.business.fee.domain.Fee;
import cn.bc.business.fee.service.FeeService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.identity.web.SystemContext;
import cn.bc.option.service.OptionService;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;
import cn.bc.web.ui.json.JsonArray;

/**
 * 批量初始化承包费Action
 * 
 * @author wis
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class BatchInitAction extends FileEntityAction<Long, Fee> {
	private static final long serialVersionUID = 1L;
	public CarService carService;
	public String vehicles;// 批量修改经营权的车辆
	public List<Map<String, String>> feeMonths; //可选费用月份
	public Map<Long, String> carList = new HashMap<Long,String>();
	
	public FeeService feeService;
	private OptionService optionService;
	private Integer feeYear;
	private Integer feeMonth;

	public Integer getFeeYear() {
		return feeYear;
	}

	public void setFeeYear(Integer feeYear) {
		this.feeYear = feeYear;
	}

	public Integer getFeeMonth() {
		return feeMonth;
	}

	public void setFeeMonth(Integer feeMonth) {
		this.feeMonth = feeMonth;
	}

	@Autowired
	public void setOptionService(OptionService optionService) {
		this.optionService = optionService;
	}

	@Autowired
	public void CarService(CarService carService) {
		this.carService = carService;
	}

	@Autowired
	public void setFeeService(FeeService feeService) {
		this.feeService = feeService;
		this.setCrudService(feeService);
	}

	@Override
	public boolean isReadonly() {
		// 车辆经营权管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.fee.manage"),
				getText("key.role.bc.admin"));
	}

	public String save() throws Exception {
		Json json = new Json();
		Fee e = this.getE();
		List<Fee> list = new ArrayList<Fee>();
		Long excludeId = null;
		
		// 获取批量处理承包费车辆的Id
		String[] taxis = vehicles.split(";");
		Long[] carIds = new Long[taxis.length];
		for(int i = 0; i < taxis.length; i++){
			carIds[i] = new Long(taxis[i].split(",")[1]);
			//保存之前检测此车辆是否存在本年本月的承包费用
			excludeId = this.feeService.checkFeeIsExist(e.getId(), carIds[i],
					feeYear, feeMonth);
			if(null != excludeId){
				Fee tempView = this.feeService.load(excludeId);
				list.add(tempView);
			}
		}
		
		if(list != null && list.size() > 0){
			String msg = "";
			String showCarIds = "";
			for(Fee obj : list){
				msg += obj.getCarPlate()+",";
				showCarIds += obj.getCarId()+",";
			}
			json.put("success", false);
			json.put("carIds", showCarIds);
			json.put("msg", getText("以下车辆 [ "+msg+" ] 已存在 "+feeYear+" 年 "+feeMonth+" 月的承包费用"));
		} else {
			this.beforeSave(e);
			String[] carPlates = new String[taxis.length];
			for (int i = 0; i < taxis.length; i++) {
				carIds[i] = new Long(taxis[i].split(",")[1]);
				carPlates[i] = new String(taxis[i].split(",")[0]);
			}
			this.feeService.saveBatchInit(carIds,carPlates,feeYear,feeMonth);
			
			this.afterSave(e);
			
			json.put("success", true);
			json.put("msg", "初始化成功!");
		}
		this.json = json.toString();
		return "json";
	}

	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(380)
				.setMinWidth(320).setModal(true);

	}

	@Override
	protected void initForm(boolean editable) throws Exception {
		super.initForm(editable);
		// 批量加载可选项列表
		Map<String, List<Map<String, String>>> optionItems = this.optionService
				.findOptionItemByGroupKeys(new String[] {
						OptionConstants.FEE_MONTH });
		//设置当前年月
		feeYear = Integer.valueOf(DateUtils.format(new Date(), "yyyy").toString());
		feeMonth = Integer.valueOf(DateUtils.format(new Date(), "MM").toString());
		
		// 经营权性质
		feeMonths = optionItems.get(OptionConstants.FEE_MONTH);
		

	}	
	
	@Override
	protected void buildFormPageButtons(PageOption pageOption, boolean editable) {
		boolean readonly = this.isReadonly();
		if (editable && !readonly) {
			// 添加默认的保存按钮
			pageOption.addButton(new ButtonOption("确认", null,
					"bs.batchInitForm.saveAndClose"));
		}
	}
	
	public String selectAllCar() {
		List<Car> cars = this.carService.selectCarByStatus(BCConstants.STATUS_ENABLED);
		JsonArray jsons = new JsonArray();
		Json o;
		for (Car car : cars) {
			o = new Json();
			o.put("id", car.getId());
			o.put("plate", car.getPlate());
			jsons.add(o);
		}
		json = jsons.toString();
		return "json";
	}

}
