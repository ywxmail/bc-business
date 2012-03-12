/**
 * 
 */
package cn.bc.business.invoice.web.struts2;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.business.OptionConstants;
import cn.bc.business.car.domain.Car;
import cn.bc.business.car.service.CarService;
import cn.bc.business.carman.domain.CarMan;
import cn.bc.business.carman.service.CarManService;
import cn.bc.business.invoice.domain.Invoice4Sell;
import cn.bc.business.invoice.service.Invoice4SellService;
import cn.bc.business.motorcade.service.MotorcadeService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.identity.web.SystemContext;
import cn.bc.option.domain.OptionItem;
import cn.bc.option.service.OptionService;
import cn.bc.web.ui.html.page.PageOption;

/**
 * 票务采购Action
 * 
 * @author wis
 * 
 */

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class Invoice4SellAction extends FileEntityAction<Long, Invoice4Sell> {
	// private static Log logger = LogFactory.getLog(CarAction.class);
	private static final long serialVersionUID = 1L;
	private Invoice4SellService invoice4SellService;
	private OptionService optionService;
	private MotorcadeService motorcadeService;
	private CarService carService;
	private CarManService carManService;
	
	public Long buyerId;
	public Long carId;
	
	public List<Map<String, String>> companyList; // 所属公司列表（宝城、广发）
	public List<Map<String, String>> motorcadeList; // 可选车队列表
	public List<Map<String, String>> typeList; // 发票类型列表（打印票、手撕票）
	public List<Map<String, String>> unitList; // 发票单位列表（卷、本）
	public List<Map<String, String>> payTypeList; // 付款方式列表（现金、银行卡）
	
	public boolean isMoreCar; // 是否存在多辆车
	public boolean isMoreCarMan; // 是否存在多个司机
	public boolean isNullCar; // 此司机没有车
	public boolean isNullCarMan; // 此车没有司机

	@Autowired
	public void setInvoice4BuyService(Invoice4SellService invoice4SellService) {
		this.setCrudService(invoice4SellService);
		this.invoice4SellService = invoice4SellService;
	}

	@Autowired
	public void setOptionService(OptionService optionService) {
		this.optionService = optionService;
	}
	
	@Autowired
	public void setMotorcadeService(MotorcadeService motorcadeService) {
		this.motorcadeService = motorcadeService;
	}

	@Autowired
	public void setCarService(CarService carService) {
		this.carService = carService;
	}

	@Autowired
	public void setCarManService(CarManService carManService) {
		this.carManService = carManService;
	}

	@Override
	public boolean isReadonly() {
		// 票务采购管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.invoice"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(800)
				.setMinWidth(250).setMinHeight(200);
	}

	@Override
	protected void afterCreate(Invoice4Sell entity) {
		super.afterCreate(entity);
		entity.setSellDate(Calendar.getInstance());
		//entity.setBuyPrice(7F);
		entity.setStatus(BCConstants.STATUS_ENABLED);
		entity.setPayType(Invoice4Sell.PAY_TYPE_CASH);
		
		if (carId != null && buyerId == null) {// 车辆页签中的新建
			Car car=this.carService.load(carId);
			entity.setCarId(this.carId);
			entity.setCarPlate(car.getPlate());
		}else if(carId == null && buyerId != null){// 司机页签中的新建
			CarMan carman=this.carManService.load(buyerId);
			entity.setBuyerId(this.buyerId);
			entity.setBuyerName(carman.getName());
		}
	}

	@Override
	protected void initForm(boolean editable) throws Exception {
		super.initForm(editable);
		
		// 加载可选车队列表
		this.motorcadeList = this.motorcadeService.findEnabled4Option();
		/*if (this.getE().getMotorcadeId() != null)
			OptionItem.insertIfNotExist(this.motorcadeList, this.getE()
					.getMotorcadeId().toString(), this.getE()
					.getMotorcadeName());
		*/
		// 批量加载可选项列表
		Map<String, List<Map<String, String>>> optionItems = this.optionService
				.findOptionItemByGroupKeys(new String[] {
						OptionConstants.CAR_COMPANY,
						OptionConstants.INVOICE_TYPE,
						OptionConstants.INVOICE_UNIT,
						OptionConstants.INVOICE_PAYTYPE
						});

		// 所属公司列表
		this.companyList =optionItems.get(OptionConstants.CAR_COMPANY);
		OptionItem.insertIfNotExist(companyList, null, getE().getCompany());
		//发票类型
		this.typeList=optionItems.get(OptionConstants.INVOICE_TYPE);
		//发票单位
		this.unitList=optionItems.get(OptionConstants.INVOICE_UNIT);
		//收款方式
		this.payTypeList=optionItems.get(OptionConstants.INVOICE_PAYTYPE);
	}
	
}