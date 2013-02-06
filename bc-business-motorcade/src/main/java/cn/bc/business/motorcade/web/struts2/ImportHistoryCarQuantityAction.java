package cn.bc.business.motorcade.web.struts2;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.motorcade.domain.HistoryCarQuantity;
import cn.bc.business.motorcade.domain.Motorcade;
import cn.bc.business.motorcade.service.HistoryCarQuantityService;
import cn.bc.business.motorcade.service.MotorcadeService;
import cn.bc.core.util.DateUtils;
import cn.bc.docs.web.struts2.ImportDataAction;
import cn.bc.identity.web.SystemContextHolder;

/**
 * 导入车队历史车辆数数据的Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class ImportHistoryCarQuantityAction extends ImportDataAction {
	private static final long serialVersionUID = 1L;
	private final static Log logger = LogFactory
			.getLog(ImportHistoryCarQuantityAction.class);
	public MotorcadeService motorcadeService;
	private HistoryCarQuantityService historyCarQuantityService;

	@Autowired
	public void setHistoryCarQuantityService(
			HistoryCarQuantityService historyCarQuantityService) {
		this.historyCarQuantityService = historyCarQuantityService;
	}

	@Autowired
	public void setMotorcadeService(MotorcadeService motorcadeService) {
		this.motorcadeService = motorcadeService;
	}

	@Override
	protected void importData(List<Map<String, Object>> data, JSONObject json,
			String fileType) throws JSONException {
		Map<String, Object> map;
		HistoryCarQuantity e;
		String motorcadeName;
		Calendar date;
		Motorcade motorcade;
		for (int i = 0; i < data.size(); i++) {
			map = data.get(i);
			if (logger.isDebugEnabled()) {
				logger.debug("map:" + map);
			}

			// 车队处理
			motorcadeName = (String) map.get("车队");
			if (motorcadeName == null || motorcadeName.isEmpty()) {
				addErrorItem(map, i, "车队不能为空");
				continue;
			}

			// 日期处理
			date = (Calendar) map.get("日期");
			if (date == null) {
				addErrorItem(map, i, "日期不能为空");
				continue;
			}

			try {
				// 车队
				motorcade = this.motorcadeService.loadByName(motorcadeName);
				if (motorcade == null) {
					addErrorItem(map, i, "系统不存在名为“" + motorcadeName + "”的车队");
					continue;
				}

				// 查找现有的
				e = this.historyCarQuantityService.loadByDate(
						motorcade.getId(), date);// 通过车队和日期查找
				if (e == null) {// 新建
					e = new HistoryCarQuantity();
					e.setAuthor(SystemContextHolder.get().getUserHistory());
					e.setFileDate(Calendar.getInstance());
					e.setModifier(e.getAuthor());
					e.setModifiedDate(e.getFileDate());
					// 车队
					e.setMotorcade(motorcade);
					// 日期
					e.setYear(date.get(Calendar.YEAR));
					e.setMonth(date.get(Calendar.MONTH) + 1);
					e.setDay(date.get(Calendar.DATE));
				} else {// 更新
					e.setModifier(SystemContextHolder.get().getUserHistory());
					e.setModifiedDate(Calendar.getInstance());
				}

				// 车辆数处理
				String quantity_ = (String) map.get("车辆数");
				if (quantity_ == null || quantity_.isEmpty()) {
					addErrorItem(map, i, "车辆数不能为空");
					continue;
				}
				try {
					int quantity = Integer.parseInt(quantity_);
					e.setQuantity(quantity);
					updateCount++;
				} catch (NumberFormatException ex) {
					addErrorItem(map, i, "无效的车辆数");
					continue;
				}

				// 保存
				this.historyCarQuantityService.save(e);
			} catch (Exception e1) {
				logger.warn(e1.getMessage(), e1);
				addErrorItem(map, i,
						"未知异常：" + motorcadeName + " - error=" + e1.getMessage());
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
		} else if (columnName.equals("日期")) {// 转换为 Calendar 类型
			Calendar c = getCellCalendar(cell);
			if (c != null)
				DateUtils.setToZeroTime(c);
			return c;
		} else if (columnName.equals("车辆数")) {// 转换为 String 类型
			if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {// 数字
				return String.valueOf((long) cell.getNumericCellValue());
			}
		}
		return super.getCellValue(cell, columnName, fileType);
	}

	@Override
	protected Object formatErrorItemValue(String key, Object value) {
		if (key.equals("日期")) {
			return DateUtils.formatCalendar2Day((Calendar) value);
		}
		return super.formatErrorItemValue(key, value);
	}
}