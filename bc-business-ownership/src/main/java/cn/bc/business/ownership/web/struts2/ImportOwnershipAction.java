package cn.bc.business.ownership.web.struts2;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.business.ownership.domain.Ownership;
import cn.bc.business.ownership.service.OwnershipService;
import cn.bc.docs.web.struts2.ImportDataAction;
import cn.bc.identity.web.SystemContext;
import cn.bc.identity.web.SystemContextHolder;

import com.google.gson.JsonObject;

/**
 * 导入经营权数据的Action
 * 
 * @author zxr
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class ImportOwnershipAction extends ImportDataAction {
	private static final long serialVersionUID = 1L;
	private final static Log logger = LogFactory
			.getLog(ImportOwnershipAction.class);
	public OwnershipService ownershipService;

	@Autowired
	public void setOwnershipService(OwnershipService ownershipService) {
		this.ownershipService = ownershipService;
	}

	@Override
	protected void importData(List<Map<String, Object>> data, JsonObject json,
			String fileType) {
		Map<String, Object> map;
		Ownership ownership;
		int existeNumber = 0;// 数据库中已存在相同经营权号的数量
		String existeOwnershipNumer = null;// 数据库中已存在的经营权号，多个时用逗号连接
		for (int i = 0; i < data.size(); i++) {
			map = new HashMap<String, Object>();
			ownership = new Ownership();
			map = data.get(i);
			// 判断数据库是否存在相同经营权号的数据，如果存在就不插入
			if (map.get("权证号") != null) {
				Ownership ownershipByNumber = this.ownershipService
						.getOwershipByNumber(map.get("权证号").toString());
				if (ownershipByNumber == null) {
					// 状态
					ownership.setStatus(BCConstants.STATUS_ENABLED);
					// 经营权证号
					if (map.get("权证号") != null) {
						ownership.setNumber(map.get("权证号").toString().trim());
					}
					// 经营权性质
					if (map.get("经营权性质") != null) {
						ownership.setNature(map.get("经营权性质").toString().trim());
					}
					// 经营权情况
					if (map.get("权证抵押情况") != null) {
						ownership.setSituation(map.get("权证抵押情况").toString()
								.trim());
					}
					// 经营权来源
					if (map.get("经营权来源") != null) {
						ownership.setSource(map.get("经营权来源").toString().trim());
					}
					// 车辆产权
					if (map.get("车辆产权") != null) {
						ownership.setOwner(map.get("车辆产权").toString().trim());
					}
					// 经营权权属
					if (map.get("经营权权属") != null) {
						ownership.setOwnership(map.get("经营权权属").toString()
								.trim());
					}
					// 设置创建人信息
					SystemContext context = (SystemContext) SystemContextHolder
							.get();
					Calendar now = Calendar.getInstance();
					now.set(Calendar.MILLISECOND, 0);
					ownership.setFileDate(now);
					ownership.setAuthor(context.getUserHistory());
					this.ownershipService.save(ownership);
				} else {
					// 已存在就不插入
					existeNumber++;
					if (existeNumber < 1 || existeNumber == 1) {
						existeOwnershipNumer = ownershipByNumber.getNumber();
					} else {
						existeOwnershipNumer = existeOwnershipNumer + ","
								+ ownershipByNumber.getNumber();
					}
				}
			}
		}
		json.addProperty("msg", (existeNumber == 0 ? "成功导入" + data.size()
				+ "条数据！" : "成功导入" + (data.size() - existeNumber) + "条数据！其中"
				+ existeNumber + "条数据没导入，其经营权号为：" + existeOwnershipNumer));
		logger.fatal("TODO: ImportOptionAction.importData");
		logger.fatal((existeNumber == 0 ? "成功导入" + data.size() + "条数据！"
				: "成功导入" + (data.size() - existeNumber) + "条数据！没导入的经营权号："
						+ existeOwnershipNumer));
	}

	@Override
	protected Object getCellValue(Cell cell, String columnName, String fileType) {
		// 特殊处理权证号：如：11107912转换后变为1.1107912E7
		if (columnName.equals("权证号")) {
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
			// try {
			// return cell.getStringCellValue();
			// } catch (Exception e) {
			// return String.valueOf((long) cell.getNumericCellValue());
			// }
		}
		return super.getCellValue(cell, columnName, fileType);
	}
}
