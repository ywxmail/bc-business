package cn.bc.business.carman.web.struts2;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.car.service.CarService;
import cn.bc.business.carman.domain.CarManRisk;
import cn.bc.business.carman.service.CarManRiskService;
import cn.bc.core.util.DateUtils;
import cn.bc.docs.web.struts2.ImportDataAction;

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
	public CarService carService;
	private CarManRiskService carManRiskService;

	@Autowired
	public void setCarManRiskService(CarManRiskService carManRiskService) {
		this.carManRiskService = carManRiskService;
	}

	@Autowired
	public void setCarService(CarService carService) {
		this.carService = carService;
	}

	@Override
	protected void importData(List<Map<String, Object>> data, JSONObject json,
			String fileType) throws JSONException {
		Map<String, Object> map;
		CarManRisk e;
		int updateCount = 0;
		String driverName, driverIdentity, buyType;
		List<Map<String, Object>> error = new ArrayList<Map<String, Object>>();
		Map<String, Object> driverInfo;
		for (int i = 0; i < data.size(); i++) {
			map = data.get(i);
			if (logger.isDebugEnabled()) {
				logger.debug("map:" + map);
			}
			driverName = (String) map.get("姓名");
			driverIdentity = (String) map.get("身份证号");
			try {
				e = new CarManRisk();
				e.setCompany((String) map.get("保司"));
				e.setCode((String) map.get("保险单号"));
				e.setStartDate((Calendar) map.get("人意险起始日"));
				e.setEndDate((Calendar) map.get("人意险到期日"));

				// 查找司机信息
				driverInfo = this.carManRiskService
						.getCarManInfo(driverIdentity);// id,name
				if (driverInfo == null) {
					map.put("index", i);
					map.put("msg", "找不到司机：" + driverName + "(" + driverIdentity
							+ ")");
					error.add(map);
					continue;
				} else if (!driverName.equals(driverInfo.get("name"))) {
					map.put("index", i);
					map.put("msg", "司机姓名与身份证不相符：" + driverName + "("
							+ driverIdentity + ")");
					error.add(map);
					continue;
				}
				driverInfo.get("id");

				// 确定购买方式
				buyType = (String) map.get("是否跟公司购买");
				if ("是".equals(buyType))
					e.setBuyType(CarManRisk.BUY_TYPE_COMPANY);
				else if ("否".equals(buyType))
					e.setBuyType(CarManRisk.BUY_TYPE_SELF);
				else
					e.setBuyType(CarManRisk.BUY_TYPE_NONE);

				// 保存
				// this.hcjtzService.save(e);
			} catch (Exception e1) {
				logger.warn(e1.getMessage(), e1);
				map.put("index", i);
				map.put("msg", "未知异常：" + driverName + "(" + driverIdentity
						+ ") - error=" + e1.getMessage());
				error.add(map);
				continue;
			}
		}
		String msg;
		if (!error.isEmpty()) {
			if (updateCount > 0) {
				msg = "成功导入" + (data.size() - updateCount - error.size())
						+ "条新数据，更新" + updateCount + "条现有数据，" + error.size()
						+ "条数据存在异常没有导入！";
			} else {
				msg = "成功导入" + (data.size() - error.size()) + "条新数据，"
						+ error.size() + "条数据存在异常没有导入！";
			}
		} else {
			if (updateCount > 0) {
				msg = "成功导入" + (data.size() - updateCount) + "条新数据，更新"
						+ updateCount + "条现有数据！";
			} else {
				msg = "成功导入" + data.size() + "条新数据！";
			}
		}

		// 简短的处理结果描述信息
		json.put("msg", msg);

		// 记录详细的异常处理信息
		if (!error.isEmpty()) {
			JSONArray ejs = new JSONArray();
			JSONObject ej;
			for (Map<String, Object> m : error) {
				ej = new JSONObject();
				for (Entry<String, Object> entry : m.entrySet()) {
					ej.put((String) entry.getKey(), entry.getValue());
				}
				ejs.put(ej);
			}

			json.put("detail", ejs);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("errorInfo:" + error);
			logger.debug("msg:" + msg);
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
}