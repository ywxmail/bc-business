/**
 * 
 */
package cn.bc.business.invoice.web.struts2;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.business.OptionConstants;
import cn.bc.business.invoice.domain.Invoice4Buy;
import cn.bc.business.invoice.service.Invoice4BuyService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.identity.web.SystemContext;
import cn.bc.option.domain.OptionItem;
import cn.bc.option.service.OptionService;
import cn.bc.web.formater.NubmerFormater;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;

/**
 * 票务采购Action
 * 
 * @author wis
 * 
 */

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class Invoice4BuyAction extends FileEntityAction<Long, Invoice4Buy> {
	// private static Log logger = LogFactory.getLog(CarAction.class);
	private static final long serialVersionUID = 1L;
	private Invoice4BuyService invoice4BuyService;
	private OptionService optionService;

	public List<Map<String, String>> companyList; // 所属公司列表（宝城、广发）
	public List<Map<String, String>> typeList; // 发票类型列表（打印票、手撕票）
	public List<Map<String, String>> unitList; // 发票单位列表（卷、本）

	public String amount;// 合计
	public String balanceNumber;// 剩余号码段
	public String balanceCount;// 剩余数量
	public String unitName;

	@Autowired
	public void setInvoice4BuyService(Invoice4BuyService invoice4BuyService) {
		this.setCrudService(invoice4BuyService);
		this.invoice4BuyService = invoice4BuyService;
	}

	@Autowired
	public void setOptionService(OptionService optionService) {
		this.optionService = optionService;
	}

	@Override
	public boolean isReadonly() {
		// 票务管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		// 配置权限：发票管理员、发票采购管理员
		return !context.hasAnyRole(getText("key.role.bs.invoice"),
				getText("key.role.bs.invoice4buy"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(680)
				.setMinWidth(250).setMinHeight(200);
	}

	@Override
	protected void afterCreate(Invoice4Buy entity) {
		SystemContext context = this.getSystyemContext();
		super.afterCreate(entity);
		entity.setBuyDate(Calendar.getInstance());
		entity.setBuyPrice(7F);
		entity.setStatus(BCConstants.STATUS_ENABLED);
		// 每（卷/本）默认值为100
		entity.setEachCount(100);
		entity.setBuyerId(context.getUserHistory());
		this.unitName = getText("invoice.unit.juan");
		entity.setUnit(Invoice4Buy.UNIT_JUAN);
	}

	@Override
	protected void afterEdit(Invoice4Buy entity) {
		// 计算合计
		amount = new NubmerFormater("###,##0.00").format(entity.getCount()
				* entity.getBuyPrice());
		balanceCount = invoice4BuyService.findBalanceCountByInvoice4BuyId(
				entity.getId()).get(0);

		balanceNumber="";
		if (Integer.parseInt(balanceCount) > 0) {
			for (Map<String, String> bmap : invoice4BuyService
					.findBalanceNumber(entity.getId(), true)) {
				balanceNumber += "[" + bmap.get("sNo") + "~" + bmap.get("eNo")
						+ "] ";
			}
		}

		if (entity.getUnit() == Invoice4Buy.UNIT_BEN) {
			this.unitName = getText("invoice.unit.ben");
		} else {
			this.unitName = getText("invoice.unit.juan");
		}
		super.afterEdit(entity);
	}

	@Override
	protected void afterOpen(Invoice4Buy entity) {
		// 计算合计
		amount = new NubmerFormater("###,##0.00").format(entity.getCount()
				* entity.getBuyPrice());
		balanceCount = invoice4BuyService.findBalanceCountByInvoice4BuyId(
				entity.getId()).get(0);
		balanceNumber="";
		if (Integer.parseInt(balanceCount) > 0) {
			for (Map<String, String> bmap : invoice4BuyService
					.findBalanceNumber(entity.getId(), true)) {
				balanceNumber += "[" + bmap.get("sNo") + "~" + bmap.get("eNo")
						+ "] ";
			}
		}

		if (entity.getUnit() == Invoice4Buy.UNIT_BEN) {
			this.unitName = getText("invoice.unit.ben");
		} else {
			this.unitName = getText("invoice.unit.juan");
		}
		super.afterOpen(entity);

	}

	@Override
	public String save() throws Exception {
		Json json = new Json();
		Invoice4Buy e = this.getE();
		beforeSave(e);
		// 正常
		if (e.getStatus() == BCConstants.STATUS_ENABLED) {
			int sNo4Save = Integer.parseInt(e.getStartNo().trim());
			int eNo4Save = Integer.parseInt(e.getEndNo().trim());
			// 采购单列表
			List<Invoice4Buy> bList = null;
			// 检查相同发票代码的采购开始号和结束号是否出现在已保存的采购编码范围内
			if (e.isNew()) {
				bList = invoice4BuyService.selectInvoice4BuyByCode(e.getCode());
				if (bList != null && bList.size() > 0) {
					for (Invoice4Buy i4Buy : bList) {
						int sNo = Integer.parseInt(i4Buy.getStartNo().trim());
						int eNo = Integer.parseInt(i4Buy.getEndNo().trim());
						if (!(eNo4Save < sNo || eNo < sNo4Save)) {
							json.put("code", i4Buy.getCode());
							json.put("data_startNo", i4Buy.getStartNo());
							json.put("data_endNo", i4Buy.getEndNo());
							json.put("data_buyId", i4Buy.getId());
							json.put("success", false);
							json.put("isShowBuyInfo", true);
							json.put("msg", getText("invoice4Buy.tips1"));
							this.json = json.toString();
							return "json";
						}
					}
				}
			} else {
				bList = invoice4BuyService.selectInvoice4BuyByCode(e.getCode(),
						e.getId());
				if (bList != null && bList.size() > 0) {
					for (Invoice4Buy i4Buy : bList) {
						int sNo = Integer.parseInt(i4Buy.getStartNo().trim());
						int eNo = Integer.parseInt(i4Buy.getEndNo().trim());
						if (!(eNo4Save < sNo || eNo < sNo4Save)) {
							json.put("code", i4Buy.getCode());
							json.put("data_startNo", i4Buy.getStartNo());
							json.put("data_endNo", i4Buy.getEndNo());
							json.put("data_buyId", i4Buy.getId());
							json.put("success", false);
							json.put("isShowBuyInfo", true);
							json.put("msg", getText("invoice4Buy.tips2"));
							this.json = json.toString();
							return "json";
						}
					}
				}

				// 检查相同发票代码的采购开始号和结束号是否出现在已保存的采购编码范围内
				List<Map<String, String>> bnumber = this.invoice4BuyService
						.findBalanceNumber(e.getId(), true);
				if(bnumber!=null&&bnumber.size()>0){
					int firstENO = Integer.parseInt(bnumber.get(0).get("eNo"));
					int lastSNO = Integer.parseInt(bnumber.get(bnumber.size() - 1)
							.get("sNo"));
					if (!(sNo4Save < firstENO && eNo4Save > lastSNO)) {
						json.put("success", false);
						json.put("isShowBuyInfo", false);
						json.put("msg", getText("invoice4Buy.tips3"));
						this.json = json.toString();
						return "json";
					}
				}
			}
			// 作废
		} else {
			if (!e.isNew()) {
				if (this.invoice4BuyService.isExistSellAndRefund(e.getId())) {
					json.put("success", false);
					json.put("isShowBuyInfo", false);
					json.put("msg", getText("invoice4Buy.tips4"));
					this.json = json.toString();
					return "json";
				}
			}
		}

		this.getCrudService().save(e);
		this.afterSave(e);
		json.put("success", true);
		if (e.getStatus() == BCConstants.STATUS_ENABLED) {
			json.put("msg", getText("form.save.success"));
		} else
			json.put("msg", getText("invoice.save.success.invalid"));
		this.json = json.toString();
		return "json";
	}

	@Override
	protected void initForm(boolean editable) throws Exception {
		super.initForm(editable);
		// 批量加载可选项列表
		Map<String, List<Map<String, String>>> optionItems = this.optionService
				.findOptionItemByGroupKeys(new String[] { OptionConstants.CAR_COMPANY });

		// 所属公司列表
		this.companyList = optionItems.get(OptionConstants.CAR_COMPANY);
		OptionItem.insertIfNotExist(companyList, null, getE().getCompany());

		// 发票类型
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		list.add(this.getOptiomItems(String.valueOf(Invoice4Buy.TYPE_PRINT),
				getText("invoice.type.dayinpiao")));
		list.add(this.getOptiomItems(String.valueOf(Invoice4Buy.TYPE_TORE),
				getText("invoice.type.shousipiao")));
		this.typeList = list;

		// 发票单位
		list = new ArrayList<Map<String, String>>();
		list.add(this.getOptiomItems(String.valueOf(Invoice4Buy.UNIT_JUAN),
				getText("invoice.unit.juan")));
		list.add(this.getOptiomItems(String.valueOf(Invoice4Buy.UNIT_BEN),
				getText("invoice.unit.ben")));
		this.unitList = list;

	}

	/**
	 * 生成OptiomItem key、value值
	 */
	private Map<String, String> getOptiomItems(String key, String value) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("key", key);
		map.put("value", value);
		return map;
	}

	@Override
	protected void buildFormPageButtons(PageOption pageOption, boolean editable) {
		if (!this.isReadonly()) {
			if (editable) {// 可编辑时显示保存按钮
				pageOption.addButton(new ButtonOption(getText("label.save"),
						null, "bs.invoice4BuyForm.save")
						.setId("invoice4BuySave"));
				pageOption.addButton(new ButtonOption(
						getText("invoice.saveAndClose"), null,
						"bs.invoice4BuyForm.saveAndClose")
						.setId("invoice4BuySave"));
			} else {// open时
				if (this.getE().getStatus() == Invoice4Buy.STATUS_NORMAL) {
					// 维护
					pageOption.addButton(new ButtonOption(
							getText("invoice.optype.edit"), null,
							"bs.invoice4BuyForm.doMaintenance")
							.setId("invoice4BuyEdit"));
				}
			}
		}

	}

}