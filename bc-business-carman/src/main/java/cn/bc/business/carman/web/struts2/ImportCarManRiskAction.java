package cn.bc.business.carman.web.struts2;

import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.carman.domain.CarMan;
import cn.bc.business.carman.domain.CarManRisk;
import cn.bc.business.carman.service.CarManRiskService;
import cn.bc.core.util.DateUtils;
import cn.bc.docs.web.struts2.ImportDataAction;
import cn.bc.identity.service.IdGeneratorService;
import cn.bc.identity.web.SystemContextHolder;

/**
 * 司机人意险数据的Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class ImportCarManRiskAction extends ImportDataAction {
	private static final long serialVersionUID = 1L;
	private final static Log logger = LogFactory
			.getLog(ImportCarManRiskAction.class);
	public IdGeneratorService idGeneratorService;
	private CarManRiskService carManRiskService;

	@Autowired
	public void setCarManRiskService(CarManRiskService carManRiskService) {
		this.carManRiskService = carManRiskService;
	}

	@Autowired
	public void setIdGeneratorService(IdGeneratorService idGeneratorService) {
		this.idGeneratorService = idGeneratorService;
	}

	@Override
	protected void importData(List<Map<String, Object>> data, JSONObject json,
			String fileType) throws JSONException {
		Map<String, Object> map;
		CarManRisk e;
		String driverName, driverIdentity, buyType;
		Integer driverId;
		CarMan carMan;
		Map<String, Object> driverInfo;
		for (int i = 0; i < data.size(); i++) {
			map = data.get(i);
			if (logger.isDebugEnabled()) {
				logger.debug("map:" + map);
			}
			driverName = (String) map.get("姓名");
			if (driverName == null || driverName.isEmpty()) {
				addErrorItem(map, i, "姓名不能为空");
				continue;
			}
			driverIdentity = (String) map.get("身份证号");
			if (driverIdentity == null || driverIdentity.isEmpty()) {
				addErrorItem(map, i, "身份证不能为空");
				continue;
			}
			try {
				String code = (String) map.get("保险单号");
				String company = (String) map.get("保司");
				if (company == null || company.isEmpty()) {
					addErrorItem(map, i, "保司不能为空");
					continue;
				}
				Calendar startDate = (Calendar) map.get("人意险起始日");
				if (startDate == null) {
					addErrorItem(map, i, "人意险起始日不能为空");
					continue;
				}
				Calendar endDate = (Calendar) map.get("人意险到期日");
				if (endDate == null) {
					addErrorItem(map, i, "人意险到期日不能为空");
					continue;
				}

				// 查找现有的保单
				if (code != null && !code.isEmpty()) {// 通过保险单号查找保单
					e = this.carManRiskService.loadByCode(code);
				} else {// 通过保险公司、有效期限查找保单
					e = this.carManRiskService.loadByCompanyAndDate(company,
							startDate, endDate);
				}

				if (e == null) {// 新建
					e = new CarManRisk();
					e.setAuthor(SystemContextHolder.get().getUserHistory());
					e.setFileDate(Calendar.getInstance());
					e.setUid(this.idGeneratorService.next(CarManRisk.KEY_UID));
				} else {// 已存在
					code = e.getCode();
				}
				e.setCompany(company);
				e.setStartDate(startDate);
				e.setEndDate(endDate);
				if (map.containsKey("投保人"))
					e.setHolder((String) map.get("投保人"));

				// TODO 生成临时信息避免非空错误
				if (e.getHolder() == null || e.getHolder().isEmpty())
					e.setHolder("TODO");
				if (code == null || code.isEmpty()) {
					code = this.idGeneratorService.next("TODO.RISK");
				}
				e.setCode(code);

				// 查找司机信息
				driverInfo = this.carManRiskService
						.getCarManInfo(driverIdentity);// id,name
				if (driverInfo == null) {
					addErrorItem(map, i, "找不到身份证对应的司机");
					continue;
				} else if (!driverName.equals(driverInfo.get("name"))) {
					addErrorItem(map, i, "司机姓名与身份证不相符");
					continue;
				}
				driverId = (Integer) driverInfo.get("id");
				Set<CarMan> insurants = e.getInsurants();
				if (insurants == null) {
					insurants = new LinkedHashSet<CarMan>();
					e.setInsurants(insurants);
				}
				boolean contain = false;
				for (CarMan insurant : insurants) {
					if (insurant.getId().equals(driverId)) {
						contain = true;
						break;
					}
				}
				if (!contain) {// 新加入的司机
					carMan = new CarMan();
					carMan.setId(new Long(driverId));
					insurants.add(carMan);
				} else {
					updateCount++;
				}

				// 确定购买方式
				buyType = (String) map.get("是否跟公司购买");
				if (buyType == null || buyType.isEmpty()) {
					addErrorItem(map, i, "是否跟公司购买不能为空");
					continue;
				}
				if ("是".equals(buyType))
					e.setBuyType(CarManRisk.BUY_TYPE_COMPANY);
				else if ("否".equals(buyType))
					e.setBuyType(CarManRisk.BUY_TYPE_SELF);
				else
					e.setBuyType(CarManRisk.BUY_TYPE_NONE);

				// 保存
				this.carManRiskService.save(e);
			} catch (Exception e1) {
				logger.warn(e1.getMessage(), e1);
				addErrorItem(map, i, "未知异常：" + driverName + "("
						+ driverIdentity + ") - error=" + e1.getMessage());
				continue;
			}
		}
	}

	@Override
	protected Object getCellValue(Cell cell, String columnName, String fileType) {
		if (columnName.equals("序号")) {
			if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {// 数字
				return String.valueOf((long) cell.getNumericCellValue());
			}
		} else if (columnName.equals("人意险起始日")) {// 转换为 Calendar 类型
			Calendar c = getCellCalendar(cell);
			if (c != null)
				DateUtils.setToZeroTime(c);
			return c;
		} else if (columnName.equals("人意险到期日")) {// 转换为 Calendar 类型
			Calendar c = getCellCalendar(cell);
			if (c != null)
				DateUtils.setToMaxTime(c);
			return c;
		}
		return super.getCellValue(cell, columnName, fileType);
	}

	@Override
	protected Object formatErrorItemValue(String key, Object value) {
		if (key.equals("人意险起始日") || key.equals("人意险到期日")) {
			return DateUtils.formatCalendar2Day((Calendar) value);
		}
		return super.formatErrorItemValue(key, value);
	}
}