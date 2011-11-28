package cn.bc.business.sync.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.business.sync.domain.Sync4InfractTraffic;
import cn.bc.business.ws.service.WSMiddle;
import cn.bc.core.service.DefaultCrudService;
import cn.bc.identity.domain.ActorHistory;
import cn.bc.option.service.OptionService;
import cn.bc.sync.domain.SyncBase;
import cn.bc.sync.service.SyncBaseService;
import cn.bc.web.ws.dotnet.DataSet;
import cn.bc.web.ws.dotnet.Row;

/**
 * 交委接口的交通违法信息同步服务的默认实现
 * 
 * @author rongjih
 * 
 */
public class Sync4InfractTrafficServiceImpl extends
		DefaultCrudService<Sync4InfractTraffic> implements
		Sync4InfractTrafficService {
	protected final Log logger = LogFactory
			.getLog(Sync4InfractTrafficServiceImpl.class);
	private OptionService optionService;
	private WSMiddle wsMiddle;// 交委接口
	private SyncBaseService syncBaseService;

	@Autowired
	public void setOptionService(OptionService optionService) {
		this.optionService = optionService;
	}

	@Autowired
	public void setWsMiddle(WSMiddle wsMiddle) {
		this.wsMiddle = wsMiddle;
	}

	@Autowired
	public void setSyncBaseService(SyncBaseService syncBaseService) {
		this.syncBaseService = syncBaseService;
	}

	public int doSync(ActorHistory syncer, Calendar fromDate, Calendar toDate,
			StringBuffer strMsg) {
		// 交委接口的宝城企业ID
		String jiaoWei_qyid_baocheng = this.optionService.getItemValue("sync",
				"jiaowei.ws.qyid.baocheng");

		// 交委接口的url
		String jiaoWei_ws_uri = this.optionService.getItemValue("sync",
				"jiaowei.ws.soapUrl");
		String jiaoWei_ws_method = "SearchPublicTransport";

		// 从交委接口获取数据
		DataSet dataSet = wsMiddle.findBreachOfTraffic(jiaoWei_qyid_baocheng,
				fromDate, toDate, strMsg);

		// 发生异常就直接退出
		if (strMsg.length() > 0)
			return 0;

		// 循环每一条数据作处理
		Sync4InfractTraffic domain;
		Calendar now = Calendar.getInstance();
		ActorHistory author = syncer;
		String syncType = Sync4InfractTraffic.class.getSimpleName();
		String syncFrom = jiaoWei_ws_uri + "#" + jiaoWei_ws_method;
		List<SyncBase> toSaveDomains = new ArrayList<SyncBase>();
		if (dataSet.getRows() != null) {
			for (Row row : dataSet.getRows()) {
				domain = rowToDomain(row, author, now, syncFrom, syncType);
				if (!this.syncBaseService.hadSync(syncType,
						domain.getSyncCode())) {// 没有同步过的记录
					toSaveDomains.add(domain);
				} else {// 已存在同步记录
					if (logger.isInfoEnabled())
						logger.info("记录已存在，忽略：syncType=" + syncType
								+ ",syncCode=" + domain.getSyncCode());
				}
			}
			if (!toSaveDomains.isEmpty())
				this.syncBaseService.save(toSaveDomains);
		}

		return toSaveDomains.size();
	}

	/**
	 * 根据同步数据生成相应的Domain
	 * 
	 * @param row
	 * @param author
	 * @param createDate
	 * @param syncFrom
	 * @param syncType
	 * @return
	 */
	private Sync4InfractTraffic rowToDomain(Row row, ActorHistory author,
			Calendar createDate, String syncFrom, String syncType) {
		Sync4InfractTraffic domain = new Sync4InfractTraffic();
		domain.setAuthor(author);
		domain.setStatus(Sync4InfractTraffic.STATUS_NEW);
		domain.setSyncDate(createDate);
		domain.setSyncType(syncType);
		domain.setSyncFrom(syncFrom);

		domain.setSyncCode(row.getCellStringValue("违章顺序号"));
		domain.setCarPlate(row.getCellStringValue("车牌号码"));
		domain.setDriverName(row.getCellStringValue("当事司机姓名"));
		domain.setDriverCert(row.getCellStringValue("服务资格证"));
		domain.setHappenDate(row.getCellCalendarValue("违章时间"));
		domain.setContent(row.getCellStringValue("违章内容"));
		domain.setCompanyName(row.getCellStringValue("公司名称"));
		domain.setJeom(Float.parseFloat(row.getCellStringValue("本次扣分")));

		return domain;
	}
}
