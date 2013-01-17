package cn.bc.business.carman.web.struts2;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.car.service.CarService;
import cn.bc.business.carman.domain.CarManRisk;
import cn.bc.business.motorcade.service.MotorcadeService;
import cn.bc.core.util.DateUtils;
import cn.bc.docs.web.struts2.ImportDataAction;

import com.google.gson.JsonObject;

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
	private MotorcadeService motorcadeService;

	@Autowired
	public void setCarService(CarService carService) {
		this.carService = carService;
	}

	@Autowired
	public void setMotorcadeService(MotorcadeService motorcadeService) {
		this.motorcadeService = motorcadeService;
	}

	@Override
	protected void importData(List<Map<String, Object>> data, JsonObject json,
			String fileType) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Map<String, Object> map;
		CarManRisk e;
		int updateCount = 0;
		int exceptionCount = 0;
		String driverName, driverIdentity, buyType;
		for (int i = 0; i < data.size(); i++) {
			try {
				map = data.get(i);
				e = new CarManRisk();
				e.setCompany((String) map.get("保司"));
				e.setCode((String) map.get("保险单号"));
				e.setStartDate((Calendar) map.get("人意险起始日"));
				e.setEndDate((Calendar) map.get("人意险到期日"));

				// 查找司机信息
				driverName = (String) map.get("姓名");
				driverIdentity = (String) map.get("身份证号");

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
				exceptionCount++;
			}
		}
		String msg;
		if (exceptionCount > 0) {
			if (updateCount > 0) {
				msg = "成功导入" + (data.size() - updateCount - exceptionCount)
						+ "条新数据，更新" + updateCount + "条现有数据，" + exceptionCount
						+ "条数据存在异常没有导入！";
			} else {
				msg = "成功导入" + (data.size() - exceptionCount) + "条新数据，"
						+ exceptionCount + "条数据存在异常没有导入！";
			}
		} else {
			if (updateCount > 0) {
				msg = "成功导入" + (data.size() - updateCount) + "条新数据，更新"
						+ updateCount + "条现有数据！";
			} else {
				msg = "成功导入" + data.size() + "条新数据！";
			}
		}
		json.addProperty("msg", msg);
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