package cn.bc.business.ws.web.struts2;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.ws.service.WSMiddle;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.Grid;
import cn.bc.web.ui.html.grid.GridData;
import cn.bc.web.ui.html.grid.GridHeader;
import cn.bc.web.ui.html.grid.IdColumn;
import cn.bc.web.ui.html.grid.TextColumn;
import cn.bc.web.ws.dotnet.Cell;
import cn.bc.web.ws.dotnet.DataSet;
import cn.bc.web.ws.dotnet.Row;

import com.opensymphony.xwork2.ActionSupport;

/**
 * 交委接口调用的测试Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller(value = "wSMiddleTestAction")
public class WSMiddleTestAction extends ActionSupport {
	// private static Log logger = LogFactory.getLog(BulletinAction.class);
	private static final long serialVersionUID = 1L;
	private WSMiddle wsMiddle;
	public String bodyXml;
	public String soapAction;
	public String msg;
	public String html;

	@Autowired
	public void setWsMiddle(WSMiddle wsMiddle) {
		this.wsMiddle = wsMiddle;
	}

	public String show() throws Exception {
		soapAction = "http://tempuri.org/GetMasterWZ";
		bodyXml = "<GetMasterWZ xmlns=\"http://tempuri.org/\">\r\n";
		bodyXml += "  <strMasterID>17E0FFF7-7816-46A5-83A7-23D5C9F762AB</strMasterID>\r\n";
		bodyXml += "  <dWeiZhangKSRQ>2011-01-01</dWeiZhangKSRQ>\r\n";
		bodyXml += "  <dWeiZhangJZRQ>2011-01-31</dWeiZhangJZRQ>\r\n";
		bodyXml += "  <strMsg></strMsg>\r\n";
		bodyXml += "</GetMasterWZ>";
		return SUCCESS;
	}

	public String getXml() throws Exception {
		StringBuffer strMsg = new StringBuffer();

		html = this.wsMiddle.findXml(bodyXml, soapAction, strMsg);
		msg = strMsg.toString();
		return "page";
	}

	public String getDataSet() throws Exception {
		StringBuffer strMsg = new StringBuffer();
		DataSet ds = this.wsMiddle.findDataSet(bodyXml, soapAction, strMsg);
		msg = strMsg.toString();

		// 根据构建一个grid
		html = buildDataSetGrid(ds).toString();

		return "page";
	}

	public String idKey = "c_id";

	protected Grid buildDataSetGrid(DataSet dataSet) {
		Grid grid = new Grid();

		// grid的列配置
		List<Column> columns = new ArrayList<Column>();
		grid.setColumns(columns);
		for (cn.bc.web.ws.dotnet.Column col4ds : dataSet.getColumns()) {
			if (idKey.equalsIgnoreCase(col4ds.getName())) {
				columns.add(new IdColumn().setId(col4ds.getName())
						.setLabel(col4ds.getName())
						.setValueExpression("['" + col4ds.getName() + "']")
						.setSortable(true));
			} else {
				TextColumn tc = new TextColumn(col4ds.getName(),
						col4ds.getName(), 100);
				tc.setValueExpression("['" + col4ds.getName() + "']")
						.setSortable(true).setUseTitleFromLabel(true);

				if ("dateTime".equals(col4ds.getType())) {
					tc.setValueFormater(new CalendarFormater(
							"yyyy-MM-dd HH:mm:ss"));
				}
				columns.add(tc);
			}
		}

		// grid列头部分
		GridHeader header = new GridHeader();
		header.setColumns(columns);
		grid.setGridHeader(header);

		// grid数据部分
		GridData data = new GridData();
		data.setData(this.toMaps(dataSet.getRows()));
		data.setColumns(columns);
		grid.setGridData(data);

		// 本地排序
		grid.setRemoteSort(false);

		// 单选
		grid.setSingleSelect(true);

		return grid;
	}

	private List<Map<String, Object>> toMaps(List<Row> rows) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map;
		for (Row row : rows) {
			map = new LinkedHashMap<String, Object>();
			for (Cell cell : row.getCells()) {
				map.put(cell.getKey(), cell.getValue());
			}
			list.add(map);
		}
		return list;
	}
}
