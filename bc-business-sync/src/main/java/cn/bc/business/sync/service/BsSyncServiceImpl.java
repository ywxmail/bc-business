package cn.bc.business.sync.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import cn.bc.business.car.service.CarService;
import cn.bc.business.spider.Spider4JinDunJTWF;
import cn.bc.business.spider.domain.JinDunJTWF;
import cn.bc.business.sync.domain.JiaoWeiADVICE;
import cn.bc.business.sync.domain.JiaoWeiJTWF;
import cn.bc.business.sync.domain.JiaoWeiYYWZ;
import cn.bc.business.ws.service.WSMiddle;
import cn.bc.core.cache.Cache;
import cn.bc.core.util.DateUtils;
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
	private CarService carService;
	public Map<String, Object> carInfoMap;// 车辆信息map

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

	@Autowired
	public void setCarService(CarService carService) {
		this.carService = carService;
	}

	public int doSync4JiaoWeiJTWF(ActorHistory syncer, Calendar fromDate,
			Calendar toDate, StringBuffer strMsg) {
		// 交委接口的宝城企业ID
		String jiaoWei_qyid_baocheng = this.optionService.getItemValue("sync",
				"jiaowei.ws.qyid.baocheng");
		String jiaoWei_qyid_guangfa = this.optionService.getItemValue("sync",
				"jiaowei.ws.qyid.guangfa");

		int count = 0;
		StringBuffer msg = new StringBuffer();
		count += this.doSync4JiaoWeiJTWF(jiaoWei_qyid_baocheng, syncer,
				fromDate, toDate, msg);
		strMsg.append(strMsg);

		msg = new StringBuffer();
		count += this.doSync4JiaoWeiJTWF(jiaoWei_qyid_guangfa, syncer,
				fromDate, toDate, msg);
		strMsg.append(strMsg);
		return count;
	}

	private int doSync4JiaoWeiJTWF(String qyid, ActorHistory syncer,
			Calendar fromDate, Calendar toDate, StringBuffer strMsg) {
		Date startTime = new Date();
		// 交委接口的url
		String jiaoWei_ws_uri = this.optionService.getItemValue("sync",
				"jiaowei.ws.soapUrl");
		String jiaoWei_ws_method = "SearchPublicTransport";

		// 从交委接口获取数据
		DataSet dataSet = wsMiddle.findBreachOfTraffic(qyid, fromDate, toDate,
				strMsg);

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
		if (logger.isInfoEnabled())
			logger.info("从交委交通违法接口获取数据总耗时：" + DateUtils.getWasteTime(startTime)
					+ ",newCount=" + toSaveDomains.size());

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
		String plate = row.getCellStringValue("车牌号码");// 格式为粤A.XXXX
		int index = plate.indexOf(".");
		if (index != -1) {
			domain.setCarPlateType(plate.substring(0, index));
			domain.setCarPlateNo(plate.substring(index + 1));
		} else {
			domain.setCarPlateType(null);
			domain.setCarPlateNo(plate);
		}
		// 通过车牌号查找此车辆所属的分公司与车队
		carInfoMap = this.carService.findcarInfoByCarPlateNo2(domain
				.getCarPlateNo());
		if (carInfoMap != null && carInfoMap.get("unit_name") != null) {
			domain.setUnitName(carInfoMap.get("unit_name") + ""); // 设置分公司
		}
		if (carInfoMap != null && carInfoMap.get("motorcade_name") != null) {
			domain.setMotorcadeName(carInfoMap.get("motorcade_name") + ""); // 设置所属车队
		}
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
		Date startTime = new Date();
		if (logger.isDebugEnabled()) {
			logger.debug("cars=" + cars);
		}

		JinDunJTWF jtwf = null;

		int successCount = 0;

		// 循环每台车执行抓取
		this.spider4JinDunJTWF.setSyncer(syncer);
		this.spider4JinDunJTWF.setCarType("02");
		int i = 0;
		List<String> carSpideHistory = this.jinDunCache.get("carSpideHistory");
		String carId;
		int errorCount = 0;
		for (Map<String, Object> car : cars) {
			carId = car.get("id").toString();
			i++;
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
					jtwf = this.spider4JinDunJTWF.excute();
					// 添加到缓存记录
					if (carSpideHistory == null)
						carSpideHistory = new ArrayList<String>();
					carSpideHistory.remove(carId);
					carSpideHistory.add(carId);
				} catch (Exception e) {
					// 抓取异常就停止
					logger.warn(e.getMessage());
					errorCount++;
				}

				this.jinDunCache.put("carSpideHistory", carSpideHistory);

				if (jtwf == null)
					continue;

				String syncType = JinDunJTWF.class.getSimpleName();

				if (!this.syncBaseService.hadSync(syncType, jtwf.getSyncCode())) {
					// 保存新增的记录
					if (jtwf != null) {
						this.syncBaseService.save(jtwf);
						successCount++;
					}
				}

				if (logger.isInfoEnabled())
					logger.info("从金盾网抓取交通违法信息总耗时："
							+ DateUtils.getWasteTime(startTime) + ",newCount="
							+ successCount);

				if (errorCount > 0) {
					strMsg.append(errorCount);// 记录发生异常的数目
				}

			}
		}

		return successCount;
	}

	/*
	 * 批量抓取 public int doSync4JinDunJTWF(ActorHistory syncer, List<Map<String,
	 * Object>> cars, StringBuffer strMsg) { Date startTime = new Date(); if
	 * (logger.isDebugEnabled()) { logger.debug("cars=" + cars); }
	 * 
	 * boolean hasError = false; List<JinDunJTWF> all = new
	 * ArrayList<JinDunJTWF>();
	 * 
	 * // 循环每台车执行抓取 this.spider4JinDunJTWF.setSyncer(syncer);
	 * this.spider4JinDunJTWF.setCarType("02"); int i = 0; List<String>
	 * carSpideHistory = this.jinDunCache.get("carSpideHistory"); String carId;
	 * int errorCount = 0; for (Map<String, Object> car : cars) { carId =
	 * car.get("id").toString(); i++; // if(i > 10) break; if (carSpideHistory
	 * != null && carSpideHistory.contains(carId)) { logger.info("(" + i + "/" +
	 * cars.size() + ")" + "车辆'" + car.get("plateType").toString() +
	 * car.get("plateNo").toString() + "'存在抓取缓存，忽略频繁的抓取！"); continue; } else {
	 * this.spider4JinDunJTWF.setCarPlateType(car.get("plateType") .toString());
	 * this.spider4JinDunJTWF.setCarPlateNo(car.get("plateNo") .toString()); if
	 * (car.get("engineNo") != null) { logger.warn("(" + i + "/" + cars.size() +
	 * ")" + "开始抓取'" + car.get("plateType").toString() + "." +
	 * car.get("plateNo").toString() + "'的交通违法信息...");
	 * this.spider4JinDunJTWF.setEngineNo(car.get("engineNo") .toString()); }
	 * else { logger.warn("(" + i + "/" + cars.size() + ")" + "车辆'" +
	 * car.get("plateType").toString() + car.get("plateNo").toString() +
	 * "'没有设置发动机号，忽略抓取！"); continue; }
	 * 
	 * try { all.addAll(this.spider4JinDunJTWF.excute());
	 * 
	 * // 添加到缓存记录 if (carSpideHistory == null) carSpideHistory = new
	 * ArrayList<String>(); carSpideHistory.remove(carId);
	 * carSpideHistory.add(carId); } catch (Exception e) { // 抓取异常就停止
	 * logger.warn(e.getMessage()); hasError = true; errorCount ++; } } }
	 * this.jinDunCache.put("carSpideHistory", carSpideHistory);
	 * 
	 * if (all.isEmpty()) return 0;
	 * 
	 * // 获取新的记录 List<SyncBase> news = new ArrayList<SyncBase>(); List<SyncBase>
	 * olds = new ArrayList<SyncBase>(); String syncType =
	 * JinDunJTWF.class.getSimpleName(); for (JinDunJTWF jtwf : all) { if
	 * (!this.syncBaseService.hadSync(syncType, jtwf.getSyncCode())) {
	 * news.add(jtwf); } else { olds.add(jtwf); } }
	 * 
	 * // 如果抓取没有异常，就将所有现有的其他未处理记录设置为已处理 if (!hasError) { // List<String>
	 * newSyncCodes = new ArrayList<String>(); // for (SyncBase syncBase : news)
	 * { // newSyncCodes.add(syncBase.getSyncCode()); // } //
	 * this.syncBaseService.updateNewStatus2Done4ExcludeCode(syncType, //
	 * newSyncCodes); }
	 * 
	 * // 将已经存在的旧记录的状态更新为未处理 if (!olds.isEmpty()) { // List<String> oldSyncCodes
	 * = new ArrayList<String>(); // for (SyncBase syncBase : olds) { //
	 * oldSyncCodes.add(syncBase.getSyncCode()); // } // int u2 =
	 * this.syncBaseService.updateStatus2New(syncType, oldSyncCodes); // if(u2 >
	 * 0){ // logger.warn("将" + u2 + "条信息重新更新为未处理状态！"); // } }
	 * 
	 * // 保存新增的记录 if (!news.isEmpty()) this.syncBaseService.save(news); if
	 * (logger.isInfoEnabled()) logger.info("从金盾网抓取交通违法信息总耗时：" +
	 * DateUtils.getWasteTime(startTime) + ",newCount=" + news.size());
	 * 
	 * if(errorCount > 0){ strMsg.append(errorCount);//记录发生异常的数目 } return
	 * news.size(); }
	 */

	public int doSync4JiaoWeiYYWZ(ActorHistory syncer, Calendar fromDate,
			Calendar toDate, StringBuffer strMsg) {
		// 交委接口的宝城企业ID
		String jiaoWei_qyid_baocheng = this.optionService.getItemValue("sync",
				"jiaowei.ws.qyid.baocheng");
		String jiaoWei_qyid_guangfa = this.optionService.getItemValue("sync",
				"jiaowei.ws.qyid.guangfa");

		int count = 0;
		StringBuffer msg = new StringBuffer();
		count += this.doSync4JiaoWeiYYWZ(jiaoWei_qyid_baocheng, syncer,
				fromDate, toDate, msg);
		strMsg.append(strMsg);

		msg = new StringBuffer();
		count += this.doSync4JiaoWeiYYWZ(jiaoWei_qyid_guangfa, syncer,
				fromDate, toDate, msg);
		strMsg.append(strMsg);
		return count;
	}

	public int doSync4JiaoWeiYYWZ(String qyid, ActorHistory syncer,
			Calendar fromDate, Calendar toDate, StringBuffer strMsg) {
		Date startTime = new Date();
		// 交委接口的url
		String jiaoWei_ws_uri = this.optionService.getItemValue("sync",
				"jiaowei.ws.soapUrl");
		String jiaoWei_ws_method = "GetMasterWZ";

		// 从交委接口获取数据
		DataSet dataSet = wsMiddle.findBreachOfBusiness(qyid, fromDate, toDate,
				strMsg);

		// 发生异常就直接退出
		if (strMsg.length() > 0)
			return 0;

		// 循环每一条数据作处理
		JiaoWeiYYWZ domain;
		Calendar now = Calendar.getInstance();
		ActorHistory author = syncer;
		String syncType = JiaoWeiYYWZ.class.getSimpleName();
		String syncFrom = jiaoWei_ws_uri + "#" + jiaoWei_ws_method;
		List<SyncBase> toSaveDomains = new ArrayList<SyncBase>();
		if (dataSet.getRows() != null) {
			for (Row row : dataSet.getRows()) {
				domain = dataSetRow2JiaoWeiYYWZ(row, author, now, syncFrom,
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
		if (logger.isInfoEnabled())
			logger.info("从交委营运违法接口获取数据总耗时：" + DateUtils.getWasteTime(startTime)
					+ ",newCount=" + toSaveDomains.size());

		return toSaveDomains.size();
	}

	/**
	 * 数据集行数据到交委营运违法信息的转换
	 * 
	 * @param row
	 * @param author
	 * @param createDate
	 * @param syncFrom
	 * @param syncType
	 * @return
	 */
	private JiaoWeiYYWZ dataSetRow2JiaoWeiYYWZ(Row row, ActorHistory author,
			Calendar createDate, String syncFrom, String syncType) {
		JiaoWeiYYWZ domain = new JiaoWeiYYWZ();
		domain.setAuthor(author);
		domain.setStatus(JiaoWeiYYWZ.STATUS_NEW);
		domain.setSyncDate(createDate);
		domain.setSyncType(syncType);
		domain.setSyncFrom(syncFrom);

		domain.setcId(row.getCellStringValue("c_id"));
		domain.setSyncCode(row.getCellStringValue("案号"));
		domain.setWzStatus(row.getCellStringValue("状态"));
		domain.setConfiscateCertNo(row.getCellStringValue("扣件证号"));
		domain.setOperator(row.getCellStringValue("执法人"));
		domain.setOperateUnit(row.getCellStringValue("执法分队"));
		domain.setVideoOP(row.getCellStringValue("摄录员"));
		domain.setDriverName(row.getCellStringValue("当事人"));
		domain.setIdcardType(row.getCellStringValue("身份证明类型"));
		domain.setIdcardCode(row.getCellStringValue("身份证明编号"));
		domain.setCarPlate(row.getCellStringValue("违章主体"));

		String plate = domain.getCarPlate();// 格式为粤A.XXXX
		int index = plate.indexOf(".");
		if (index != -1) {
			plate = plate.substring(index + 1);
		}
		// 通过车牌号查找此车辆所属的分公司与车队
		carInfoMap = this.carService.findcarInfoByCarPlateNo2(plate);
		if (carInfoMap != null && carInfoMap.get("unit_name") != null) {
			domain.setUnitName(carInfoMap.get("unit_name") + ""); // 设置分公司
		}
		if (carInfoMap != null && carInfoMap.get("motorcade_name") != null) {
			domain.setMotorcadeName(carInfoMap.get("motorcade_name") + ""); // 设置所属车队
		}

		domain.setCompany(row.getCellStringValue("违章企业"));
		domain.setAddress(row.getCellStringValue("违章地段"));
		domain.setOwner(row.getCellStringValue("业户名称"));
		domain.setOwnerId(row.getCellStringValue("业户ID"));
		domain.setBsCertNo(row.getCellStringValue("经营许可证号"));
		domain.setContent(row.getCellStringValue("违章内容"));
		domain.setHappenDate(row.getCellCalendarValue("违章日期"));
		domain.setEvidenceUnit(row.getCellStringValue("保存单位"));
		domain.setDriverCert(row.getCellStringValue("资格证号"));
		domain.setCommitStatus(row.getCellStringValue("提交状态"));
		domain.setReceipt(row.getCellStringValue("罚没收据编号"));
		domain.setNotice(row.getCellStringValue("放行通知书编号"));
		domain.setBusinessCertNo(row.getCellStringValue("营运证号"));
		if (null != row.getCellStringValue("座位数")
				&& row.getCellStringValue("座位数").length() > 0) {
			domain.setSeating(Float.parseFloat(row.getCellStringValue("座位数")));
		}
		domain.setOweRecord(row.getCellStringValue("欠笔录"));
		domain.setOweSignature(row.getCellStringValue("欠签名"));
		domain.setLeaveTroops(row.getCellStringValue("留队部"));
		domain.setArea(row.getCellStringValue("所属区县"));
		domain.setCloseDate(row.getCellCalendarValue("结案日期"));
		domain.setDesc(row.getCellStringValue("备注"));
		domain.setClone(row.getCellStringValue("是否克隆车"));
		domain.setSubject(row.getCellStringValue("违章项目"));
		domain.setDetain(row.getCellStringValue("扣留物品"));
		domain.setPullUnit(row.getCellStringValue("拖车单位"));
		if (null != row.getCellStringValue("罚款")
				&& row.getCellStringValue("罚款").length() > 0) {
			domain.setPenalty(Float.parseFloat(row.getCellStringValue("罚款")));
		}

		return domain;
	}

	public int doSync4JiaoWeiADVICE(ActorHistory syncer, Calendar fromDate,
			Calendar toDate, StringBuffer strMsg) {
		// 交委接口的宝城企业ID
		String jiaoWei_qyid_baocheng = this.optionService.getItemValue("sync",
				"jiaowei.ws.qyid.baocheng");
		String jiaoWei_qyid_guangfa = this.optionService.getItemValue("sync",
				"jiaowei.ws.qyid.guangfa");

		int count = 0;
		StringBuffer msg = new StringBuffer();
		count += this.doSync4JiaoWeiADVICE(jiaoWei_qyid_baocheng, syncer,
				fromDate, toDate, msg);
		strMsg.append(strMsg);

		msg = new StringBuffer();
		count += this.doSync4JiaoWeiADVICE(jiaoWei_qyid_guangfa, syncer,
				fromDate, toDate, msg);
		strMsg.append(strMsg);
		return count;
	}

	/**
	 * 数据集行数据到交委投诉与建议信息的转换
	 * 
	 * @param row
	 * @param author
	 * @param createDate
	 * @param syncFrom
	 * @param syncType
	 */
	public int doSync4JiaoWeiADVICE(String qyid, ActorHistory syncer,
			Calendar fromDate, Calendar toDate, StringBuffer strMsg) {
		Date startTime = new Date();

		// 交委接口的url
		String jiaoWei_ws_uri = this.optionService.getItemValue("sync",
				"jiaowei.ws.soapUrl");
		String jiaoWei_ws_method = "GetAccuseByQYID";

		// 从交委接口获取数据
		DataSet dataSet = wsMiddle.findAccuseAndAdvice(qyid, fromDate, toDate,
				strMsg);

		// 发生异常就直接退出
		if (strMsg.length() > 0)
			return 0;

		// 循环每一条数据作处理
		JiaoWeiADVICE domain;
		Calendar now = Calendar.getInstance();
		ActorHistory author = syncer;
		String syncType = JiaoWeiADVICE.class.getSimpleName();
		String syncFrom = jiaoWei_ws_uri + "#" + jiaoWei_ws_method;
		List<SyncBase> toSaveDomains = new ArrayList<SyncBase>();
		if (dataSet.getRows() != null) {
			for (Row row : dataSet.getRows()) {
				domain = dataSetRow2JiaoWeiADVICE(row, author, now, syncFrom,
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
		if (logger.isInfoEnabled())
			logger.info("从交委投诉与建议接口获取数据总耗时："
					+ DateUtils.getWasteTime(startTime) + ",newCount="
					+ toSaveDomains.size());

		return toSaveDomains.size();
	}

	private JiaoWeiADVICE dataSetRow2JiaoWeiADVICE(Row row,
			ActorHistory author, Calendar createDate, String syncFrom,
			String syncType) {
		JiaoWeiADVICE domain = new JiaoWeiADVICE();
		domain.setAuthor(author);
		domain.setStatus(JiaoWeiADVICE.STATUS_NEW);
		domain.setSyncDate(createDate);
		domain.setSyncType(syncType);
		domain.setSyncFrom(syncFrom);
		// domain.setcId(row.getCellStringValue("c_id"));
		domain.setcId(row.getCellStringValue("c_id"));
		domain.setSyncCode(row.getCellStringValue("tis_handle_no"));
		domain.setReceiveCode(row.getCellStringValue("handle_no"));
		domain.setAdvisorName(row.getCellStringValue("accuser_name"));
		domain.setPathFrom(row.getCellStringValue("accuser_route"));
		domain.setPathTo(row.getCellStringValue("accuser_route_to"));
		if (row.getCellStringValue("accuser_time") != null
				&& row.getCellStringValue("accuser_time").length() > 0) {
			domain.setRidingTimeStart(formatCalendar(row
					.getCellStringValue("accuser_time")));
		}
		if (row.getCellStringValue("end_time") != null
				&& row.getCellStringValue("end_time").length() > 0) {
			domain.setRidingTimeEnd(formatCalendar(row
					.getCellStringValue("end_time")));
		}
		domain.setAdvisorSex(row.getCellStringValue("accuser_sex"));
		if (row.getCellStringValue("accuser_age") != null
				&& row.getCellStringValue("accuser_age").length() > 0) {
			domain.setAdvisorAge(Float.parseFloat(row
					.getCellStringValue("accuser_age")));
		}
		domain.setAdvisorPhone(row.getCellStringValue("accuser_tel"));
		domain.setAdvisorCert(row.getCellStringValue("accuser_id"));
		domain.setOldUnitName(row.getCellStringValue("taxi_dept"));
		domain.setCarPlate(row.getCellStringValue("taxi_no"));
		// 投诉来源
		if (row.getCellStringValue("datafrom") != null
				&& row.getCellStringValue("datafrom").length() > 0) {

		}
		// 乘车人数(男)
		if (row.getCellStringValue("male_amount") != null
				&& row.getCellStringValue("male_amount").length() > 0) {
			domain.setPassengerManCount(Integer.parseInt(row
					.getCellStringValue("male_amount")));
		}
		// 乘车人数(女)
		if (row.getCellStringValue("female_amount") != null
				&& row.getCellStringValue("female_amount").length() > 0) {
			domain.setPassengerWomanCount(Integer.parseInt(row
					.getCellStringValue("female_amount")));
		}
		// 乘车人数(童)
		if (row.getCellStringValue("child_amount") != null
				&& row.getCellStringValue("child_amount").length() > 0) {
			domain.setPassengerChildCount(Integer.parseInt(row
					.getCellStringValue("child_amount")));
		}
		// 转协查时间
		if (row.getCellStringValue("child_amount") != null
				&& row.getCellStringValue("child_amount").length() > 0) {

		}
		// 处理期限
		if (row.getCellStringValue("待协查操作时限") != null
				&& row.getCellStringValue("待协查操作时限").length() > 0) {

		}

		String plate = domain.getCarPlate();// 格式为粤A.XXXX
		int index = plate.indexOf(".");
		if (index != -1) {
			plate = plate.substring(index + 1);
		}
		// 通过车牌号查找此车辆所属的分公司与车队
		carInfoMap = this.carService.findcarInfoByCarPlateNo2(plate);
		if (carInfoMap != null && carInfoMap.get("unit_name") != null) {
			domain.setUnitName(carInfoMap.get("unit_name") + ""); // 设置分公司
		}
		if (carInfoMap != null && carInfoMap.get("motorcade_name") != null) {
			domain.setMotorcadeName(carInfoMap.get("motorcade_name") + ""); // 设置所属车队
		}

		domain.setDriverCert(row.getCellStringValue("driver_id"));
		domain.setDriverChar(row.getCellStringValue("driver_char_id"));
		domain.setContent(row.getCellStringValue("content"));
		domain.setReceiveDate(row.getCellCalendarValue("handle_time"));
		domain.setResult(row.getCellStringValue("handle_result"));
		domain.setAdviceBs(row.getCellStringValue("accuse_type"));
		domain.setSubject(row.getCellStringValue("c_item"));
		domain.setSubject2(row.getCellStringValue("c_item_class"));
		domain.setMachinePrice(row.getCellStringValue("fee_show"));
		domain.setTicket(row.getCellStringValue("invoice_no"));
		domain.setCharge(row.getCellStringValue("fee_accepted"));
		domain.setDriverSex(row.getCellStringValue("driver_sex"));
		domain.setSuggestBs(row.getCellStringValue("accuse_industry"));
		domain.setBuslines(row.getCellStringValue("buslineno"));
		domain.setBusColor(row.getCellStringValue("bus_color"));
		domain.setReply(row.getCellStringValue("是否回复"));

		return domain;
	}

	public Calendar formatCalendar(String Date) {
		Calendar calendar = Calendar.getInstance();
		;
		String dateStr = Date.replaceAll("/", "-");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date;
		try {
			date = df.parse(dateStr);
		} catch (ParseException e) {
			return null;
		}
		calendar.setTime(date);
		return calendar;
	}
}
