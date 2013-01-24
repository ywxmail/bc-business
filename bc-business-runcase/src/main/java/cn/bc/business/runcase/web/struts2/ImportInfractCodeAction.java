package cn.bc.business.runcase.web.struts2;

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

import cn.bc.business.runcase.domain.Case4InfractCode;
import cn.bc.business.runcase.service.Case4InfractCodeService;
import cn.bc.docs.web.struts2.ImportDataAction;
import cn.bc.identity.web.SystemContext;
import cn.bc.identity.web.SystemContextHolder;

/**
 * 导入经营权数据的Action
 * 
 * @author zxr
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class ImportInfractCodeAction extends ImportDataAction {
	private static final long serialVersionUID = 1L;
	private final static Log logger = LogFactory
			.getLog(ImportInfractCodeAction.class);
	private Case4InfractCodeService case4InfractCodeService;

	@Autowired
	public void setCase4InfractCodeService(
			Case4InfractCodeService case4InfractCodeService) {
		this.case4InfractCodeService = case4InfractCodeService;
	}

	@Override
	protected void importData(List<Map<String, Object>> data, JSONObject json,
			String fileType) throws JSONException {
		Map<String, Object> map;
		Case4InfractCode case4InfractCode;
		int existeCode4Count = 0;// 数据库中已存在相同违法代码的数量
		String existeInfractCode = null;// 数据库中已存在的违法代码，多个时用逗号连接
		for (int i = 0; i < data.size(); i++) {
			case4InfractCode = new Case4InfractCode();
			map = data.get(i);
			// 判断数据库是否存在相同违法代码的数据，如果存在就不插入
			if (map.get("违法代码") != null) {
				Case4InfractCode infractCodeByCode = this.case4InfractCodeService
						.getEntityByCode(map.get("违法代码").toString().trim());
				if (infractCodeByCode == null) {
					setCase4infractCodeInfo(map, case4InfractCode);
					// 设置创建人信息
					SystemContext context = (SystemContext) SystemContextHolder
							.get();
					Calendar now = Calendar.getInstance();
					now.set(Calendar.MILLISECOND, 0);
					case4InfractCode.setFileDate(now);
					case4InfractCode.setAuthor(context.getUserHistory());
					this.case4InfractCodeService.save(case4InfractCode);

				} else {
					// 如果不为空就更新经营权导入的信息
					setCase4infractCodeInfo(map, infractCodeByCode);

					// 设置修改人信息
					SystemContext context = (SystemContext) SystemContextHolder
							.get();
					Calendar now = Calendar.getInstance();
					now.set(Calendar.MILLISECOND, 0);
					infractCodeByCode.setModifiedDate(now);
					infractCodeByCode.setModifier(context.getUserHistory());
					this.case4InfractCodeService.save(infractCodeByCode);

					// 统计更新的经营权号
					existeCode4Count++;
					if (existeCode4Count < 1 || existeCode4Count == 1) {
						existeInfractCode = infractCodeByCode.getCode();
					} else {
						existeInfractCode = existeInfractCode + ","
								+ infractCodeByCode.getCode();
					}
				}
			}
		}
		json.put("msg", (existeCode4Count == 0 ? "成功导入" + data.size()
				+ "条数据！" : ((data.size() - existeCode4Count) == 0 ? "" : "成功导入"
				+ (data.size() - existeCode4Count) + "条数据！")
				+ "更新" + existeCode4Count + "条数据，其违法代码为：" + existeInfractCode));
		logger.fatal("TODO: ImportOptionAction.importData");
		logger.fatal((existeCode4Count == 0 ? "成功导入" + data.size() + "条数据！"
				: ((data.size() - existeCode4Count) == 0 ? "" : "成功导入"
						+ (data.size() - existeCode4Count) + "条数据！")
						+ "更新"
						+ existeCode4Count
						+ "条数据，其违法代码为："
						+ existeInfractCode));
	}

	private void setCase4infractCodeInfo(Map<String, Object> map,
			Case4InfractCode case4InfractCode) {
		// 违法代码
		if (map.get("违法代码") != null) {
			case4InfractCode.setCode(map.get("违法代码").toString().trim());
		}
		// 扣分
		if (map.get("扣分") != null) {
			case4InfractCode.setJeom(Float.valueOf(map.get("扣分").toString()));
		}
		// 罚款金额
		if (map.get("罚款金额") != null) {
			case4InfractCode.setPenalty(Float.valueOf(map.get("罚款金额")
					.toString()));
		}
		// 违法行为
		if (map.get("违法行为") != null) {
			case4InfractCode.setSubject(map.get("违法行为").toString().trim());
		}
		// 违法依据
		if (map.get("违法依据") != null) {
			case4InfractCode.setAccording(map.get("违法依据").toString().trim());
		}
	}

	@Override
	protected Object getCellValue(Cell cell, String columnName, String fileType) {
		if (columnName.equals("罚款金额")) {
			if (cell.getCellType() == Cell.CELL_TYPE_STRING) {// 字符串
				// 列名去空格
				// if (cell.getStringCellValue() != null) {
				// return cell.getStringCellValue().trim();
				// } else {
				// return cell.getStringCellValue();
				// }
				return (long) 0;
			} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {// 数字
				return String.valueOf((long) cell.getNumericCellValue());
			}
		}
		if (columnName.equals("违法代码")) {
			if (cell.getCellType() == Cell.CELL_TYPE_STRING) {// 字符串
				// 列名去空格
				if (cell.getStringCellValue() != null) {
					return cell.getStringCellValue().trim();
				} else {
					return cell.getStringCellValue();
				}
			} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {// 数字
				return String.valueOf((long) cell.getNumericCellValue());
			}
		}

		return super.getCellValue(cell, columnName, fileType);
	}
}
