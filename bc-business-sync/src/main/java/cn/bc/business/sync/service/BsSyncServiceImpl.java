package cn.bc.business.sync.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import cn.bc.business.spider.Spider4JinDunJTWF;
import cn.bc.business.spider.domain.JinDunJTWF;
import cn.bc.business.sync.domain.JiaoWeiJTWF;
import cn.bc.business.ws.service.WSMiddle;
import cn.bc.core.cache.Cache;
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
public class BsSyncServiceImpl implements BsSyncService {
	protected final Log logger = LogFactory.getLog(BsSyncServiceImpl.class);
	private OptionService optionService;
	private WSMiddle wsMiddle;// 交委接口
	private Spider4JinDunJTWF spider4JinDunJTWF;// 金盾交通违法抓取器
	private SyncBaseService syncBaseService;
	private Cache jinDunCache;// 金盾网交通违法抓取的缓存

	@Autowired
	public void setJinDunCache(@Qualifier("jinDunCache") Cache jinDunCache) {
		this.jinDunCache = jinDunCache;
	}

	@Autowired
	public void setOptionService(OptionService optionService) {
		this.optionService = optionService;
	}

	@Autowired
	public void setWsMiddle(WSMiddle wsMiddle) {
		this.wsMiddle = wsMiddle;
	}

	@Autowired
	public void setSpider4JinDunJTWF(Spider4JinDunJTWF spider4JinDunJTWF) {
		this.spider4JinDunJTWF = spider4JinDunJTWF;
	}

	@Autowired
	public void setSyncBaseService(SyncBaseService syncBaseService) {
		this.syncBaseService = syncBaseService;
	}

	public int doSync4JiaoWeiJTWF(ActorHistory syncer, Calendar fromDate,
			Calendar toDate, StringBuffer strMsg) {
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
		JiaoWeiJTWF domain;
		Calendar now = Calendar.getInstance();
		ActorHistory author = syncer;
		String syncType = JiaoWeiJTWF.class.getSimpleName();
		String syncFrom = jiaoWei_ws_uri + "#" + jiaoWei_ws_method;
		List<SyncBase> toSaveDomains = new ArrayList<SyncBase>();
		if (dataSet.getRows() != null) {
			for (Row row : dataSet.getRows()) {
				domain = dataSetRow2JiaoWeiJTWF(row, author, now, syncFrom,
						syncType);
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
	 * 数据集行数据到交委交通违法信息的转换
	 * 
	 * @param row
	 * @param author
	 * @param createDate
	 * @param syncFrom
	 * @param syncType
	 * @return
	 */
	private JiaoWeiJTWF dataSetRow2JiaoWeiJTWF(Row row, ActorHistory author,
			Calendar createDate, String syncFrom, String syncType) {
		JiaoWeiJTWF domain = new JiaoWeiJTWF();
		domain.setAuthor(author);
		domain.setStatus(JiaoWeiJTWF.STATUS_NEW);
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

	public int doSync4JinDunJTWF(ActorHistory syncer,
			List<Map<String, Object>> cars, StringBuffer strMsg) {
		if (logger.isDebugEnabled()) {
			logger.debug("cars=" + cars);
		}

		boolean hasError = false;
		List<JinDunJTWF> all = new ArrayList<JinDunJTWF>();

		// 循环每台车执行抓取
		this.spider4JinDunJTWF.setSyncer(syncer);
		this.spider4JinDunJTWF.setCarType("02");
		int i = 0;
		List<String> carSpideHistory = this.jinDunCache.get("carSpideHistory");
		String carId;
		for (Map<String, Object> car : cars) {
			carId = car.get("id").toString();
			i++;
			// if(i > 10) break;
			if (carSpideHistory != null && carSpideHistory.contains(carId)) {
				logger.info("(" + i + "/" + cars.size() + ")" + "车辆'"
						+ car.get("plateType").toString()
						+ car.get("plateNo").toString() + "'存在抓取缓存，忽略频繁的抓取！");
				continue;
			} else {
				this.spider4JinDunJTWF.setCarPlateType(car.get("plateType")
						.toString());
				this.spider4JinDunJTWF.setCarPlateNo(car.get("plateNo")
						.toString());
				if (car.get("engineNo") != null) {
					logger.warn("(" + i + "/" + cars.size() + ")" + "开始抓取'"
							+ car.get("plateType").toString() + "."
							+ car.get("plateNo").toString() + "'的交通违法信息...");
					this.spider4JinDunJTWF.setEngineNo(car.get("engineNo")
							.toString());
				} else {
					logger.warn("(" + i + "/" + cars.size() + ")" + "车辆'"
							+ car.get("plateType").toString()
							+ car.get("plateNo").toString() + "'没有设置发动机号，忽略抓取！");
					continue;
				}

				try {
					all.addAll(this.spider4JinDunJTWF.excute());

					// 添加到缓存记录
					if (carSpideHistory == null)
						carSpideHistory = new ArrayList<String>();
					carSpideHistory.remove(carId);
					carSpideHistory.add(carId);
				} catch (Exception e) {
					// 抓取异常就停止
					logger.error(e.getMessage());
					hasError = true;
				}
			}
		}
		this.jinDunCache.put("carSpideHistory", carSpideHistory);

		if (all.isEmpty())
			return 0;

		// 获取新的记录
		List<SyncBase> news = new ArrayList<SyncBase>();
		List<SyncBase> olds = new ArrayList<SyncBase>();
		String syncType = JinDunJTWF.class.getSimpleName();
		for (JinDunJTWF jtwf : all) {
			if (!this.syncBaseService.hadSync(syncType, jtwf.getSyncCode())) {
				news.add(jtwf);
			} else {
				olds.add(jtwf);
			}
		}

		// 如果抓取没有异常，就将所有现有的其他未处理记录设置为已处理
		if (!hasError) {
			List<String> newSyncCodes = new ArrayList<String>();
			for (SyncBase syncBase : news) {
				newSyncCodes.add(syncBase.getSyncCode());
			}
			this.syncBaseService.updateNewStatus2Done4ExcludeCode(syncType,
					newSyncCodes);
		}

		// 将已经存在的旧记录的状态更新为未处理
		if (!olds.isEmpty()) {
			List<String> oldSyncCodes = new ArrayList<String>();
			for (SyncBase syncBase : olds) {
				oldSyncCodes.add(syncBase.getSyncCode());
			}
			this.syncBaseService.updateStatus2New(syncType, oldSyncCodes);
		}

		// 保存新增的记录
		if (!news.isEmpty())
			this.syncBaseService.save(news);

		return news.size();
	}
}
