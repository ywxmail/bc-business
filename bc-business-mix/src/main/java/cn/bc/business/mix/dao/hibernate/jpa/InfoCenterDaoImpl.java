package cn.bc.business.mix.dao.hibernate.jpa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.orm.jpa.JpaTemplate;
import org.springframework.util.StringUtils;

import cn.bc.business.carman.domain.CarByDriver;
import cn.bc.business.carman.domain.CarByDriverHistory;
import cn.bc.business.carman.domain.CarMan;
import cn.bc.business.contract.domain.Contract4Charger;
import cn.bc.business.contract.domain.Contract4Labour;
import cn.bc.business.mix.dao.InfoCenterDao;
import cn.bc.business.mix.domain.InfoCenter;
import cn.bc.business.policy.domain.Policy;
import cn.bc.core.util.DateUtils;
import cn.bc.identity.domain.ActorDetail;

/**
 * 信息中心综合查询
 * 
 * @author dragon
 * 
 */
public class InfoCenterDaoImpl implements InfoCenterDao {
	protected static final Log logger = LogFactory
			.getLog(InfoCenterDaoImpl.class);
	private JpaTemplate jpaTemplate;

	@Autowired
	public void setJpaTemplate(JpaTemplate jpaTemplate) {
		this.jpaTemplate = jpaTemplate;
	}

	public JSONArray findCars(Long unitId, Long motorcadeId) {
		if (unitId == null && motorcadeId == null)
			return new JSONArray();

		final StringBuffer sql = new StringBuffer();
		final List<Object> args = new ArrayList<Object>();
		sql.append("select c.id,c.code,c.plate_type,c.plate_no,c.status_,c.return_date from bs_car c");
		sql.append(" inner join bs_motorcade m on m.id=c.motorcade_id");
		sql.append(" inner join bc_identity_actor unit on unit.id=m.unit_id");
		if (unitId != null) {
			sql.append(" where unit.id=?");
			args.add(unitId);
			if (motorcadeId != null) {
				sql.append(" and m.id=?");
				args.add(motorcadeId);
			}
		} else {
			sql.append(" where m.id=?");
			args.add(motorcadeId);
		}
		sql.append(" order by c.status_,c.register_date desc");

		if (logger.isDebugEnabled()) {
			logger.debug("args="
					+ StringUtils.collectionToCommaDelimitedString(args)
					+ ";sql=" + sql);
		}
		return findCars(sql, args.toArray());
	}

	public JSONArray findCars(String searchType, String searchText) {
		if (searchType == null || searchText == null
				|| searchType.length() == 0 || searchText.length() == 0)
			return new JSONArray();

		final String value = "%" + searchText.toUpperCase() + "%";
		final StringBuffer sql = new StringBuffer();
		sql.append("select distinct car.id,car.code,car.plate_type,car.plate_no,car.status_,car.return_date,car.register_date");
		sql.append(" from bs_car car");
		if (searchType.equals(InfoCenter.TYPE_CAR_PLATE)) {// 车牌-车牌
			sql.append(" where car.plate_no like ?");
			sql.append(" and car.status_ in (0,1)");
		} else if (searchType.equals(InfoCenter.TYPE_CAR_CODE)) {// 车牌-自编号
			sql.append(" where car.code like ?");
			sql.append(" and car.status_ in (0,1)");
		} else if (searchType.equals(InfoCenter.TYPE_CAR_ENGINENO)) {// 车牌-发动机号
			sql.append(" where car.engine_no like ?");
			sql.append(" and car.status_ in (0,1)");
		} else if (searchType.equals(InfoCenter.TYPE_CAR_VIN)) {// 车牌-车架号
			sql.append(" where car.vin like ?");
			sql.append(" and car.status_ in (0,1)");
		} else if (searchType.equals(InfoCenter.TYPE_CAR_INVOICENO)) {// 车牌-购置税发票号
			sql.append(" where car.invoice_no2 like ?");
			sql.append(" and car.status_ in (0,1)");
		} else if (searchType.indexOf("man") == 0) {// 司机或责任人
			sql.append(" inner join bs_car_contract carc on carc.car_id = car.id");
			sql.append(" inner join bs_contract c on c.id = carc.contract_id");// 合同
			sql.append(" inner join bs_carman_contract mc on mc.contract_id = c.id");
			sql.append(" inner join bs_carman m on m.id = mc.man_id");
			sql.append(" left join bs_contract_labour cl on cl.id = c.id");// 劳动合同
			sql.append(" left join bs_contract_charger cc on cc.id = c.id");// 经济合同
			if (searchType.equals(InfoCenter.TYPE_MAN_CERT_FWZG)) {// 服务资格证
				sql.append(" where m.cert_fwzg like ?");
				sql.append(" and m.status_ in (0,1)");
			} else if (searchType.equals(InfoCenter.TYPE_MAN_NAME)) {// 姓名
				sql.append(" where m.name like ?");
				sql.append(" and m.status_ in (0,1)");
			}
		} else {// 默认按车牌
			sql.append(" where car.plate_no like ?");
			sql.append(" and car.status_ in (0,1)");
		}

		sql.append(" order by car.status_,car.register_date desc");

		if (logger.isDebugEnabled()) {
			logger.debug("searchType=" + searchType + ",searchText="
					+ searchText + ";sql=" + sql);
		}
		return findCars(sql, new Object[] { value });
	}

	private JSONArray findCars(final StringBuffer sql, final Object[] args) {
		return this.jpaTemplate.execute(new JpaCallback<JSONArray>() {
			@SuppressWarnings("unchecked")
			public JSONArray doInJpa(EntityManager em)
					throws PersistenceException {
				Query queryObject = em.createNativeQuery(sql.toString());
				// jpaTemplate.prepareQuery(queryObject);

				// 注入参数
				if (args != null) {
					int i = 0;
					for (Object value : args) {
						queryObject.setParameter(i + 1, value);// jpa的索引号从1开始
						i++;
					}
				}

				List<Object[]> rs = queryObject.getResultList();
				JSONArray jsons = new JSONArray();
				if (rs != null) {
					JSONObject json;
					for (int j = 0; j < rs.size(); j++) {
						json = new JSONObject();
						try {
							json.put("id", rs.get(j)[0]);
							json.put("code", rs.get(j)[1]);
							json.put("plate", rs.get(j)[2] + "." + rs.get(j)[3]);
							json.put("status", rs.get(j)[4]);
							json.put("returnDate",
									DateUtils.formatDate((Date) rs.get(j)[5]));
						} catch (JSONException e) {
							logger.error(e.getMessage(), e);
						}
						jsons.put(json);
					}
				}
				return jsons;
			}
		});
	}

	public JSONObject findCarDetail(Long carId) throws Exception {
		Date now = new Date();
		DateUtils.setToZeroTime(now);
		JSONObject json = new JSONObject();
		json.put("id", carId);

		// ==基本信息：车辆==
		JSONObject car = this.getCar(carId);
		json.put("car", car);
		boolean isLogoutCar = car.getInt("status") != 0;

		// ==基本信息：经济合同==
		JSONObject contract4Charger = this.getContract4Charger(carId);
		if (contract4Charger != null) {
			car.put("businessType1", contract4Charger.get("businessType"));
			car.put("contract4ChargerDate", contract4Charger.get("dateRange"));
			car.put("paymentDate", contract4Charger.get("paymentDate"));
			car.put("includeCost", contract4Charger.get("includeCost"));
		}

		// ==基本信息：保单==
		JSONObject policy = this.getPolicy(carId);
		if (policy != null) {
			// 设置车辆是否自保的信息
			car.put("zb", policy.get("zb"));
		} else {
			car.put("zb", false);
		}

		// ==联系人信息==
		JSONArray mans = this.getMans(carId);
		json.put("mans", mans);

		// ==提醒信息==
		// {module:"..",id:1,link:"..",limit:"..",date:"yyyy-MM-dd",subject:".."}
		JSONArray messages = new JSONArray();
		json.put("messages", messages);
		JSONObject msg;

		// ==提醒信息/黑名单==
		// {module:"黑名单",id:1,link:"张三,李四",limit:"不可退押金",date:"yyyy-MM-dd",subject:".."}
		JSONArray blacklist = this.getBlacklist(carId);
		if (blacklist != null && blacklist.length() > 0) {
			for (int i = 0; i < blacklist.length(); i++) {
				messages.put(blacklist.get(i));
			}
		}

		if (!isLogoutCar) {
			// ==提醒信息/车辆保单==
			// {module:"车辆保单",id:1,link:"",limit:"",date:"yyyy-MM-dd",subject:".."}
			msg = buildPolicyMessage(now, policy);
			if (msg != null)
				messages.put(msg);

			// ==提醒信息/经济合同==
			// {module:"经济合同",id:1,link:"张三,李四",limit:"",date:"yyyy-MM-dd",subject:".."}
			msg = buildContract4ChargerMessage(now, contract4Charger);
			if (msg != null)
				messages.put(msg);

			// ==提醒信息/劳动合同==
			// {module:"劳动合同",id:1,link:"张三",limit:"",date:"yyyy-MM-dd",subject:".."}
			JSONObject man;
			String judgeType;
			for (int i = 0; i < mans.length(); i++) {
				man = mans.getJSONObject(i);

				// 不是在案的就不检验劳动合同提醒信息了
				if (man.getInt("judgeStatus") != 0)
					break;

				// 不是司机或顶班司机 也不检验劳动合同提醒信息
				judgeType = man.getString("judgeType");
				if (!("司机".equals(judgeType) || "司机和责任人".equals(judgeType))
						|| "顶班".equals(man.getString("judgeClasses")))
					continue;

				// 构建到期提醒信息
				msg = buildContract4LabourMessage(now, man);
				if (msg != null)
					messages.put(msg);
			}
		}

		// 返回
		return json;
	}

	private JSONObject buildContract4ChargerMessage(Date now,
			JSONObject contract4Charger) throws JSONException {
		JSONObject msg = new JSONObject();
		if (contract4Charger != null) {
			msg.put("module", "经济合同");
			msg.put("id", contract4Charger.get("id"));
			int status = contract4Charger.getInt("status");

			// 获取过期期限 判断是否已过期或将于30日后到期：
			Date endDate = (Date) contract4Charger.get("endDate");

			// 即将过期的日期
			msg.put("date", DateUtils.formatDate(endDate));

			// 限制项目
			msg.put("limit", "");

			// 关系人:TODO
			msg.put("link", "");

			// 计算过期天数
			if (status != Contract4Charger.STATUS_NORMAL) {
				msg.put("subject", "经济合同已注销");
			} else {
				if (endDate == null) {
					msg.put("subject", "经济合同没有设置过期期限");
				} else {
					DateUtils.setToZeroTime(endDate);
					String subject = buildOverdueSubject(now, endDate, 30,
							"经济合同");
					if (subject != null)
						msg.put("subject", subject);
					else
						msg = null;
				}
			}
		} else {
			msg.put("module", "经济合同");
			msg.put("id", "");
			msg.put("subject", "没有签订有效的经济合同");
			msg.put("date", "");
			msg.put("limit", "");
			msg.put("link", "");
		}
		return msg;
	}

	private JSONObject buildContract4LabourMessage(Date now, JSONObject man)
			throws JSONException {
		JSONObject msg = new JSONObject();
		JSONObject contract4Labour;
		if (man.has("autoInfo")) {
			// 获取劳动合同信息
			contract4Labour = man.getJSONObject("autoInfo");
		} else {
			contract4Labour = null;
		}

		if (contract4Labour != null) {
			msg.put("module", "劳动合同");
			msg.put("id", contract4Labour.get("id"));
			int status = contract4Labour.getInt("status");

			Date endDate = DateUtils.getDate(contract4Labour
					.getString("endDate"));

			// 即将过期的日期
			msg.put("date", contract4Labour.getString("endDate"));

			// 限制项目
			msg.put("limit", "");

			// 关系人
			msg.put("link", man.getString("name"));// 司机姓名

			// 计算过期天数
			if (status != Contract4Labour.STATUS_NORMAL) {
				msg.put("subject", "劳动合同已注销");
			} else {
				if (endDate == null) {
					msg.put("subject", "劳动合同没有设置过期期限");
				} else {
					DateUtils.setToZeroTime(endDate);
					String subject = buildOverdueSubject(now, endDate, 30,
							"劳动合同");
					if (subject != null)
						msg.put("subject", subject);
					else
						msg = null;
				}
			}
		} else {
			// 添加没有有效劳动合同的提醒信息
			msg.put("module", "劳动合同");
			msg.put("id", "");
			msg.put("subject", "没有签定有效的劳动合同");
			msg.put("date", "");
			msg.put("limit", "");
			msg.put("link", man.getString("name"));// 司机姓名
		}
		return msg;
	}

	/**
	 * @param now
	 *            当前时间
	 * @param endDate
	 *            到期时间
	 * @param days
	 *            到期提醒的天数
	 * @param type
	 *            提醒的类型
	 * @return
	 * @throws JSONException
	 */
	private String buildOverdueSubject(Date now, Date endDate, int days,
			String type) throws JSONException {
		// 忽略毫秒的比较（24*60*60=86400秒=1天）
		long dc = (endDate.getTime() / 1000 - now.getTime() / 1000) / 86400;
		if (dc < 0) {
			return type + "已过期";
		} else if (dc == 0) {
			return type + "今日到期";
		} else if (dc <= days) {
			return type + dc + "日后到期";
		} else {
			return null;// 不提醒
		}
	}

	private JSONObject buildPolicyMessage(Date now, JSONObject policy)
			throws JSONException {
		JSONObject msg = new JSONObject();
		if (policy != null) {
			msg.put("module", "车辆保单");
			msg.put("id", policy.get("id"));
			int status = policy.getInt("status");

			// 获取保单的过期期限 判断保单是否已过期或将于30日后到期：
			Date endDate = (Date) policy.get("commerialEndDate");
			boolean greenslipSameDate = "true".equalsIgnoreCase(policy.get(
					"greenslipSameDate").toString())
					|| "1".equalsIgnoreCase(policy.get("greenslipSameDate")
							.toString());
			if (!greenslipSameDate) {// 强制险与商业险不同期
				Date greenslipEndDate = (Date) policy.get("greenslipEndDate");
				if (greenslipEndDate != null
						&& greenslipEndDate.before(endDate))
					endDate = greenslipEndDate;
			}

			// 即将过期的日期
			msg.put("date", DateUtils.formatDate(endDate));

			// 限制项目
			msg.put("limit", "");

			// 最后修改人或作者:TODO
			msg.put("link", "");

			// 计算过期天数
			if (status == Policy.STATUS_DISABLED) {
				msg.put("subject", "保单已注销");
			} else if (status == Policy.STATUS_SURRENDER) {
				msg.put("subject", "保单已停保");
			} else {
				if (endDate == null) {
					msg.put("subject", "保单没有设置过期期限");
				} else {
					DateUtils.setToZeroTime(endDate);
					String subject = buildOverdueSubject(now, endDate, 30, "保单");
					if (subject != null)
						msg.put("subject", subject);
					else
						msg = null;
				}
			}
		} else {
			msg.put("module", "车辆保单");
			msg.put("id", "");
			msg.put("subject", "无有效保单信息");
			msg.put("date", "");
			msg.put("limit", "");
			msg.put("link", "");
		}
		return msg;
	}

	/**
	 * 获取司机、责任人信息
	 * <p>
	 * [{id:1,uid:"uid1",type:"司机",name:"张三",sex:"男",phones:"电话1,电话2",moveType:
	 * "迁移类型", moveDate
	 * :"迁移日期",identity:"身份证号码",address1:"身份证地址",address2:"暂住地址",region
	 * :"籍贯",cert4fwzg:"资格证号",classes:"营运班次"},...]
	 * </p>
	 * 
	 * @param carId
	 * @return
	 * @throws JSONException
	 */
	private JSONArray getMans(final Long carId) throws JSONException {
		// 预定义查询司机的相关sql信息
		StringBuffer manSql = new StringBuffer();
		manSql.append("select m.id,m.uid_,m.status_,m.type_,m.name,m.sex,m.origin,m.house_type,m.address,m.address1");
		manSql.append(",m.phone,m.phone1,m.cert_identity,m.cert_fwzg,m.classes");
		manSql.append(",m.move_type,m.move_date,m.shiftwork_end_date,m.carinfo,m.main_car_id,m.desc_");

		// 获取迁移记录对应的司机信息
		List<JSONObject> mansFromCarByDriverHistory = this
				.getMansFromCarByDriverHistory(carId, manSql);
		boolean noHistory = mansFromCarByDriverHistory.isEmpty();

		// 获取经济合同对应的责任人信息
		List<JSONObject> mansFromContract4Charger = this
				.getMansFromContract4Charger(carId, manSql);

		// 获取营运班次对应的顶班司机信息
		List<JSONObject> mansFromCarByDriver = this.getMansFromCarByDriver(
				carId, manSql);

		if (logger.isDebugEnabled()) {
			logger.debug("mansFromCarByDriverHistory="
					+ mansFromCarByDriverHistory);
			logger.debug("mansFromContract4Charger=" + mansFromContract4Charger);
			logger.debug("mansFromCarByDriver=" + mansFromCarByDriver);
		}

		// 信息合并
		List<JSONObject> mans = new ArrayList<JSONObject>();
		List<JSONObject> exists = new ArrayList<JSONObject>();
		JSONObject man, charger;
		if (!noHistory) {// 有迁移记录的情况：迁移记录(司机) + 经济合同(责任人)
			// 判断一下司机是否兼为责任人并做相应处理
			for (int i = 0; i < mansFromCarByDriverHistory.size(); i++) {
				man = mansFromCarByDriverHistory.get(i);
				charger = this.findCharger(mansFromContract4Charger,
						man.getLong("id"));
				if (charger != null) {
					exists.add(charger);
					// 对司机信息做相应处理
					if (charger.getInt("c_status") == 0) {
						if (man.getInt("judgeStatus") == 0) {// 在案的经济合同 + 在案的司机
							man.put("judgeType", "司机和责任人");
						} else {// 在案经济合同 + 注销司机
							man.put("judgeType", "责任人");
							man.put("judgeStatus", 0);
						}
					} else {
						if (man.getInt("judgeStatus") == 0) {// 注销的经济合同 + 在案的司机
							man.put("judgeType", "司机");
						} else {// 注销的经济合同 + 注销司机
							man.put("judgeType", "司机和责任人");
						}
					}
				}
				mans.add(man);
			}

			// 添加经济合同中单独的责任人
			mansFromContract4Charger.removeAll(exists);
			mans.addAll(mansFromContract4Charger);
		} else {// 无迁移记录的情况：劳动合同(司机) + 经济合同(责任人)
			// 获取劳动合同对应的司机信息：没有迁移记录才查劳动合同
			List<JSONObject> mansFromContract4Labour = this
					.getMansFromContract4Labour(carId, manSql);
			if (logger.isDebugEnabled()) {
				logger.debug("mansFromContract4Labour"
						+ mansFromContract4Labour);
			}

			// 判断一下司机是否兼为责任人并做相应处理
			for (int i = 0; i < mansFromContract4Labour.size(); i++) {
				man = mansFromContract4Labour.get(i);
				charger = this.findCharger(mansFromContract4Charger,
						man.getLong("id"));
				if (charger != null) {
					// 对司机信息做相应处理
					man.put("judgeType", "司机和责任人");

					exists.add(charger);
				}
				mans.add(man);
			}

			// 添加经济合同中单独的责任人
			mansFromContract4Charger.removeAll(exists);
			mans.addAll(mansFromContract4Charger);
		}

		// 添加顶班司机
		if (mansFromCarByDriver != null) {
			List<JSONObject> toAdds = new ArrayList<JSONObject>();
			for (JSONObject json : mansFromCarByDriver) {
				if (!exists(mans, json.getLong("id")))
					toAdds.add(json);
			}
			mans.addAll(toAdds);
		}

		// 获取司机对应的劳动合同的拼装信息
		Long[] ids = new Long[mans.size()];
		for (int i = 0; i < mans.size(); i++) {
			ids[i] = mans.get(i).getLong("id");
		}
		Map<String, JSONObject> autoInfos = this
				.getContract4Labours(carId, ids);
		String id;
		for (JSONObject _man : mans) {
			id = carId + "." + _man.getString("id");
			if (autoInfos.containsKey(id)) {
				_man.put("autoInfo", autoInfos.get(id));
			}
		}

		// 重新排序
		Collections.sort(mans, new ManStatusComparator());

		return new JSONArray(mans);
	}

	private boolean exists(List<JSONObject> mans, long manId)
			throws JSONException {
		for (JSONObject man : mans) {
			if (man.getLong("id") == manId) {
				return true;
			}
		}
		return false;
	}

	private JSONObject findCharger(List<JSONObject> mans, long manId)
			throws JSONException {
		JSONObject man;
		for (int i = 0; i < mans.size(); i++) {
			man = mans.get(i);
			if (man.getLong("id") == manId) {
				return man;
			}
		}
		return null;
	}

	/**
	 * 获取司机、责任人信息
	 * <p>
	 * [{id:1,uid:"uid1",type:"司机",name:"张三",sex:"男",phones:"电话1,电话2",moveType:
	 * "迁移类型", moveDate
	 * :"迁移日期",identity:"身份证号码",address1:"身份证地址",address2:"暂住地址",region
	 * :"籍贯",cert4fwzg:"资格证号",classes:"营运班次"},...]
	 * </p>
	 * 
	 * @param carId
	 * @return
	 */
	protected JSONArray getMansOld(final Long carId) {
		final StringBuffer sql = new StringBuffer();
		sql.append("select m.id,m.uid_,m.status_,m.type_,m.name,m.sex,m.origin,m.house_type,m.address,m.address1");
		sql.append(",m.phone,m.phone1,m.cert_identity,m.cert_fwzg,m.classes");
		sql.append(",m.move_type,m.move_date,m.shiftwork_end_date,m.carinfo,m.desc_");
		sql.append(" from bs_carman m");
		sql.append(" where m.id in (");

		// 从营运班次表取司机信息
		sql.append("(select cd.driver_id from bs_car_driver cd where cd.car_id = ?)");
		sql.append(" union ");

		// 从经济合同表取责任人信息
		sql.append("(select mc.man_id from BS_CONTRACT_CHARGER cc");
		sql.append(" inner join BS_CONTRACT c on cc.id = c.id");
		sql.append(" inner join BS_CAR_CONTRACT carc on c.id = carc.contract_id");
		sql.append(" inner join bs_carman_contract mc on c.id = mc.contract_id");
		sql.append(" where carc.car_id = ? and c.status_ = 0 and c.main = 0)");

		// 在案的排在前面
		sql.append(") order by m.status_,m.type_ desc,m.file_date");

		if (logger.isDebugEnabled()) {
			logger.debug("carId=" + carId + ";sql=" + sql);
		}
		return this.jpaTemplate.execute(new JpaCallback<JSONArray>() {
			public JSONArray doInJpa(EntityManager em)
					throws PersistenceException {
				Query queryObject = em.createNativeQuery(sql.toString());
				queryObject.setParameter(1, carId);
				queryObject.setParameter(2, carId);
				@SuppressWarnings("unchecked")
				List<Object[]> objs = queryObject.getResultList();
				JSONArray jsons = new JSONArray();
				if (objs != null && !objs.isEmpty()) {
					JSONObject json;
					for (Object[] obj : objs) {
						try {
							json = new JSONObject();
							int i = 0;
							json.put("id", obj[i++]);
							json.put("uid", obj[i++]);
							json.put("status", obj[i++]);
							json.put("type", convert2ManTypeDesc(obj[i++]));
							json.put("name", obj[i++]);
							json.put("sex", convert2ManSexDesc(obj[i++]));
							json.put("origin", null2Empty(obj[i++]));
							json.put("houseType", null2Empty(obj[i++]));
							json.put("address1", null2Empty(obj[i++]));
							json.put("address2", null2Empty(obj[i++]));
							json.put(
									"phones",
									convert2ManPhones((String) obj[i++],
											(String) obj[i++]));
							json.put("identity", null2Empty(obj[i++]));
							json.put("cert4fwzg", null2Empty(obj[i++]));
							json.put("classes", obj[i++]);
							json.put("classesDesc",
									convert2ManClassesDesc(json.get("classes")));
							json.put("moveType", obj[i++]);
							json.put("moveTypeDesc",
									convert2ManMoveTypeDesc(json
											.get("moveType")));
							json.put(
									"moveDate",
									convert2ManMoveDate((Date) obj[i++],
											(Date) obj[i++]));
							json.put("carInfo", null2Empty(obj[i++]));
							json.put("desc", null2Empty(obj[i++]));

							jsons.put(json);
						} catch (JSONException e) {
							logger.error(e.getMessage(), e);
						}
					}
				}
				return jsons;
			}

		});
	}

	/**
	 * 获取黑名单信息：
	 * <p>
	 * [{module:"黑名单",id:1,link:"张三",limit:"不可退押金",date:"2012-01-01",subject:
	 * "...."},...]
	 * </p>
	 * 
	 * @param carId
	 * @return
	 */
	private JSONArray getBlacklist(final Long carId) {
		final StringBuffer sql = new StringBuffer();
		sql.append("select b.id,b.subject,b.lock_date,b.type_,b.drivers,appoint_date,conversion_type");
		sql.append(" from bs_blacklist b");
		// sql.append(" left join bs_carman_blacklist cb on cb.blacklist_id=b.id");
		// sql.append(" left join bs_carman m on m.id=cb.man_id");
		sql.append(" where b.car_id = ? and b.status_ = 0");
		sql.append(" order by b.file_date desc");

		if (logger.isDebugEnabled()) {
			logger.debug("carId=" + carId + ";sql=" + sql);
		}
		return this.jpaTemplate.execute(new JpaCallback<JSONArray>() {
			public JSONArray doInJpa(EntityManager em)
					throws PersistenceException {
				Query queryObject = em.createNativeQuery(sql.toString());
				queryObject.setParameter(1, carId);
				@SuppressWarnings("unchecked")
				List<Object[]> objs = queryObject.getResultList();
				JSONArray jsons = new JSONArray();
				if (objs != null && !objs.isEmpty()) {
					JSONObject json;
					for (Object[] obj : objs) {
						try {
							json = new JSONObject();
							json.put("module", "黑名单");
							int i = 0;
							json.put("id", obj[i++]);
							json.put("subject", null2Empty(obj[i++]));
							json.put("date",
									DateUtils.formatDate((Date) obj[i++]));
							String type = (String) null2Empty(obj[i++]);
							json.put("link",
									getDriverInfo(null2Empty(obj[i++])));
							// 黑名单指定日期
							Date appointDate = (Date) obj[i++];
							// 指定日期后变换的限制项目
							String conversionType = null2Empty(obj[i++]);
							// 现在的时间
							Date now = new Date();
							// 如果有指定时间
							if (appointDate != null) {
								// 到了指定日期或指定日期后就将限制项目更变为指定日期后变换的限制项目
								if (now.after(appointDate)
										|| appointDate.equals(now)) {
									json.put("limit", conversionType);
								} else {
									json.put("limit", type);
								}
							} else {
								json.put("limit", type);
							}
							jsons.put(json);
						} catch (JSONException e) {
							logger.error(e.getMessage(), e);
						}
					}
				}
				return jsons;
			}
		});
	}

	/**
	 * 获取司机的信息{将姓名,班次,id;姓名2,班次2,id2...}转为[姓名1,姓名2]
	 * 
	 * @param Drivers
	 * @return
	 */
	private String getDriverInfo(String Drivers) {
		if (Drivers == null || Drivers.trim().length() == 0) {
			return "";
		}
		Drivers = Drivers.trim();
		String[] vvs = Drivers.split(";");
		// 循环每个司机执行格式化处理
		String[] vs;
		String driverInfo = "";
		int i = 0;
		for (String vv : vvs) {
			if (i > 0)
				driverInfo += ",";

			vs = vv.split(",");// [0]-司机姓名,[1]-营运班次,[2]-司机id
			if (vs.length == 3) {
				driverInfo += vs[0];
			} else {
				driverInfo += vv;
			}

			i++;
		}
		return driverInfo;

	}

	/**
	 * 获取保单信息
	 * 
	 * @param carId
	 * @return
	 */
	private JSONObject getPolicy(final Long carId) {
		final StringBuffer sql = new StringBuffer();
		sql.append("select p.id,p.status_,p.ownrisk");
		sql.append(",p.commerial_start_date,p.commerial_end_date");// 商业险
		sql.append(",p.greenslip_same_date,p.greenslip_start_date,p.greenslip_end_date");// 强制险
		sql.append(" from bs_car_policy p");
		sql.append(" where p.car_id = ? and p.main = 0");
		sql.append(" order by p.file_date desc");

		if (logger.isDebugEnabled()) {
			logger.debug("carId=" + carId + ";sql=" + sql);
		}
		return this.jpaTemplate.execute(new JpaCallback<JSONObject>() {
			public JSONObject doInJpa(EntityManager em)
					throws PersistenceException {
				Query queryObject = em.createNativeQuery(sql.toString());
				queryObject.setParameter(1, carId);
				queryObject.setFirstResult(0);
				queryObject.setMaxResults(1);
				Object[] obj;
				try {
					obj = (Object[]) queryObject.getSingleResult();
				} catch (NoResultException e) {
					if (logger.isDebugEnabled())
						logger.debug("policy = null,id=" + carId);
					return null;
				}
				if (obj != null) {
					JSONObject json = new JSONObject();
					try {
						int i = 0;
						json.put("id", obj[i++]);
						json.put("status", obj[i++]);
						json.put("zb", obj[i++]);
						json.put("commerialStartDate", obj[i++]);
						json.put("commerialEndDate", obj[i++]);
						json.put("greenslipSameDate", obj[i++]);
						json.put("greenslipStartDate", obj[i++]);
						json.put("greenslipEndDate", obj[i++]);
						return json;
					} catch (JSONException e) {
						logger.error(e.getMessage(), e);
					}
				}
				return null;
			}
		});
	}

	/**
	 * 获取车辆信息
	 * 
	 * @param carId
	 * @return
	 */
	private JSONObject getCar(final Long carId) {
		final StringBuffer sql = new StringBuffer();
		sql.append("select c.id as id,c.plate_type,c.plate_no,c.status_");
		sql.append(",c.code,c.company,unit.name as unit_name,m.name as motorcade_name");
		sql.append(",c.factory_type,c.factory_model,c.engine_no,c.vin,c.color");
		sql.append(",c.bs_type,c.register_date,c.operate_date,c.cert_no4");
		sql.append(",c.taximeter_factory,c.taximeter_type,c.desc1,c.desc2,c.desc3");
		sql.append(",c.lpg_name,c.lpg_model,c.car_tv_screen,getContract4ChargerCarmaintain(c.id)");
		sql.append(" from bs_car c");
		sql.append(" inner join bs_motorcade m on m.id=c.motorcade_id");
		sql.append(" inner join bc_identity_actor unit on unit.id=m.unit_id");
		sql.append(" where c.id = ?");

		if (logger.isDebugEnabled()) {
			logger.debug("carId=" + carId + ";sql=" + sql);
		}
		return this.jpaTemplate.execute(new JpaCallback<JSONObject>() {
			public JSONObject doInJpa(EntityManager em)
					throws PersistenceException {
				Query queryObject = em.createNativeQuery(sql.toString());
				queryObject.setParameter(1, carId);
				Object[] car;
				try {
					car = (Object[]) queryObject.getSingleResult();
				} catch (NoResultException e) {
					if (logger.isDebugEnabled())
						logger.debug("car = null,id=" + carId);
					car = null;
				}
				if (car != null) {
					try {
						JSONObject json = new JSONObject();
						// int i = 0;
						// json.put("id", car[i++]);
						// json.put("plate", car[i++] + "." + car[i++]);

						int i = 3;
						json.put("status", car[i++]);
						json.put("code", car[i++]);
						json.put("company", null2Empty(car[i++]));
						json.put("unitName", null2Empty(car[i++]));
						json.put("motorcadeName", null2Empty(car[i++]));
						json.put("factoryType", null2Empty(car[i++]));
						json.put("factoryModel", null2Empty(car[i++]));
						json.put("engineNo", null2Empty(car[i++]));
						json.put("vin", null2Empty(car[i++]));
						json.put("color", null2Empty(car[i++]));

						json.put("businessType", null2Empty(car[i++]));
						json.put("registeDate",
								DateUtils.formatDate((Date) car[i++]));
						json.put("operateDate",
								DateUtils.formatDate((Date) car[i++]));
						json.put("certNo4", null2Empty(car[i++]));
						json.put(
								"taximeter",
								getTaximeter((String) car[i++],
										(String) car[i++]));
						json.put(
								"desc",
								getDesc((String) car[i++], (String) car[i++],
										(String) car[i++]));

						json.put("lpg",
								getLPG((String) car[i++], (String) car[i++]));
						json.put("tv", null2Empty(car[i++]));
						json.put("Carmaintain", null2Empty(car[i++]));
						
						return json;
					} catch (JSONException e) {
						logger.error(e.getMessage(), e);
					}
				}
				return null;
			}

			private String getTaximeter(String factoryType, String factoryModel) {
				if (factoryType != null && factoryType.length() > 0) {
					if (factoryModel != null && factoryModel.length() > 0) {
						return factoryType + " " + factoryModel;
					} else {
						return factoryType;
					}
				} else {
					if (factoryModel != null && factoryModel.length() > 0) {
						return factoryModel;
					} else {
						return "";
					}
				}
			}

			private String getLPG(String name, String model) {
				if (name != null && name.length() > 0) {
					if (model != null && model.length() > 0) {
						return name + " " + model;
					} else {
						return name;
					}
				} else {
					if (model != null && model.length() > 0) {
						return model;
					} else {
						return "";
					}
				}
			}

			private String getDesc(String desc1, String desc2, String desc3) {
				return desc1
						+ (desc2 != null && desc2.length() > 0 ? "\r\n----备注2----\r\n"
								+ desc2
								: "")
						+ (desc3 != null && desc3.length() > 0 ? "\r\n----备注3----\r\n"
								+ desc3
								: "");
			}
		});
	}

	/**
	 * 获取经济合同信息
	 * 
	 * @param carId
	 * @return
	 */
	private JSONObject getContract4Charger(final Long carId) {
		final StringBuffer sql = new StringBuffer();
		sql.append("select cc.id,c.status_,cc.bs_type");
		sql.append(",c.start_date,c.end_date");
		sql.append(",cc.payment_date,cc.include_cost,c.main");
		sql.append(" from bs_contract_charger cc");
		sql.append(" inner join bs_contract c on cc.id = c.id");
		sql.append(" inner join bs_car_contract carc on c.id = carc.contract_id");
		sql.append(" where carc.car_id = ? and c.main = 0");
		sql.append(" order by c.file_date desc");

		if (logger.isDebugEnabled()) {
			logger.debug("carId=" + carId + ";sql=" + sql);
		}
		return this.jpaTemplate.execute(new JpaCallback<JSONObject>() {
			public JSONObject doInJpa(EntityManager em)
					throws PersistenceException {
				Query queryObject = em.createNativeQuery(sql.toString());
				queryObject.setParameter(1, carId);
				queryObject.setFirstResult(0);
				queryObject.setMaxResults(1);
				Object[] obj;
				try {
					obj = (Object[]) queryObject.getSingleResult();
				} catch (NoResultException e) {
					if (logger.isDebugEnabled())
						logger.debug("contract4Charger=null,id=" + carId);
					obj = null;
				}
				if (obj != null) {
					try {
						JSONObject json = new JSONObject();
						int i = 0;
						String t;
						json.put("id", obj[i++]);
						json.put("status", obj[i++]);
						json.put("businessType", null2Empty(obj[i++]));
						json.put("startDate", obj[i++]);
						json.put("endDate", obj[i++]);
						json.put(
								"dateRange",
								DateUtils.formatDate((Date) json
										.get("startDate"))
										+ "～"
										+ DateUtils.formatDate((Date) json
												.get("endDate")));
						t = (String) obj[i++];
						json.put("paymentDate", ("0".equals(t) ? "月末"
								: (t == null ? "" : t)));
						json.put("includeCost", obj[i++]);
						return json;
					} catch (JSONException e) {
						logger.error(e.getMessage(), e);
					}
				}
				return null;
			}

		});
	}

	/**
	 * 获取司机签订指定车辆的劳动合同信息,对同一个司机的多条劳动合同只获取最新的那条：
	 * <p>
	 * [{劳动合同1信息},{劳动合同2信息},...]
	 * </p>
	 * 
	 * @param carId
	 * @param driverIds
	 * @return
	 */
	private Map<String, JSONObject> getContract4Labours(final Long carId,
			Long[] driverIds) {
		if (carId == null || driverIds == null || driverIds.length == 0) {
			logger.warn("参数不足：getContract4Labours:carId" + carId
					+ ";driverIds="
					+ StringUtils.arrayToCommaDelimitedString(driverIds));
			return new HashMap<String, JSONObject>();
		}
		final StringBuffer sql = new StringBuffer();
		final List<Object> args = new ArrayList<Object>();
		sql.append("select c.id,c.status_,c.start_date,c.end_date,cc.joindate,cc.insurcode,cc.insurance_type,cc.remark");
		sql.append(",carc.car_id car_id,m.id driver_id,m.name driver_name");
		sql.append(" from bs_contract_labour cc");
		sql.append(" inner join bs_contract c on c.id=cc.id");
		sql.append(" inner join bs_carman_contract mc on mc.contract_id=c.id");
		sql.append(" inner join bs_car_contract carc on carc.contract_id=c.id");
		sql.append(" inner join bs_carman m on m.id = mc.man_id");
		sql.append(" where carc.car_id = ?");
		args.add(carId);
		if (driverIds.length == 1) {
			sql.append(" and m.id = ?");
			args.add(driverIds[0]);
		} else {
			sql.append(" and m.id in (?");
			args.add(driverIds[0]);
			for (int i = 1; i < driverIds.length; i++) {
				sql.append(",?");
				args.add(driverIds[i]);
			}
			sql.append(")");
		}
		// 排除相同司机的旧记录
		sql.append(" and not exists (select 1 from bs_contract_labour cci");
		sql.append("	inner join bs_contract ci on ci.id=cci.id");
		sql.append("	inner join bs_carman_contract mci on mci.contract_id=ci.id");
		sql.append("	inner join bs_car_contract carci on carci.contract_id=ci.id");
		sql.append("	inner join bs_carman mi on mi.id = mci.man_id");
		sql.append("	where carci.car_id = carc.car_id and mci.man_id=mc.man_id and ci.start_date > c.start_date)");
		sql.append(" order by c.file_date desc");

		if (logger.isDebugEnabled()) {
			logger.debug("getContract4Labours:carId=" + carId + ";driverIds="
					+ StringUtils.arrayToCommaDelimitedString(driverIds)
					+ ";sql=" + sql);
		}
		return this.jpaTemplate
				.execute(new JpaCallback<Map<String, JSONObject>>() {
					public Map<String, JSONObject> doInJpa(EntityManager em)
							throws PersistenceException {
						Query queryObject = em.createNativeQuery(sql.toString());
						int j = 0;
						for (Object value : args) {
							queryObject.setParameter(j + 1, value);// jpa的索引号从1开始
							j++;
						}
						@SuppressWarnings("unchecked")
						List<Object[]> objs = queryObject.getResultList();
						Map<String, JSONObject> jsons = new HashMap<String, JSONObject>();
						if (objs != null && !objs.isEmpty()) {
							JSONObject json;
							for (Object[] obj : objs) {
								try {
									json = new JSONObject();
									int i = 0;

									// 合同的相关信息
									json.put("id", obj[i++]);
									json.put("status", obj[i++]);
									json.put("startDate", DateUtils
											.formatDate((Date) obj[i++]));
									json.put("endDate", DateUtils
											.formatDate((Date) obj[i++]));
									json.put("joinDate", DateUtils
											.formatDate((Date) obj[i++]));
									json.put("insurcode", null2Empty(obj[i++]));
									json.put("insuranceType",
											null2Empty(obj[i++]));
									json.put("remark", null2Empty(obj[i++]));

									// 车辆、司机的相关信息
									json.put("car_id", obj[i++]);
									json.put("driver_id", obj[i++]);
									json.put("driver_name", obj[i++]);

									jsons.put(json.getString("car_id") + "."
											+ json.getString("driver_id"), json);
								} catch (JSONException e) {
									logger.error(e.getMessage(), e);
								}
							}
						}
						return jsons;
					}
				});
	}

	/**
	 * 获取车辆迁移记录对应的司机信息,对同一个司机的多条迁移记录只获取最新的那条：
	 * <p>
	 * [{司机1信息},{司机2信息},...]
	 * </p>
	 * 
	 * @param carId
	 * @return
	 */
	private List<JSONObject> getMansFromCarByDriverHistory(final Long carId,
			StringBuffer manSql) {
		final StringBuffer sql = new StringBuffer(manSql);
		sql.append(",h.id h_id,h.move_type h_moveType,h.move_date h_moveDate,h.end_date h_endDate,h.from_car_id h_fromCarId,h.to_car_id h_toCarId");
		sql.append(",h.from_classes h_fromClasses,h.to_classes h_toClasses");
		sql.append(" from bs_carman m");
		sql.append(" inner join bs_car_driver_history h on m.id=h.driver_id");
		sql.append(" where (h.from_car_id=? or h.to_car_id=?)");
		sql.append(" and m.status_ in (0,1)");
		// 排除相同司机的旧记录
		sql.append(" and not exists (select 1 from bs_carman mi inner join bs_car_driver_history hi on mi.id=hi.driver_id");
		sql.append(" where (hi.from_car_id=? or hi.to_car_id=?) and hi.driver_id=h.driver_id and hi.move_date > h.move_date)");
		sql.append(" order by h.move_date desc");

		if (logger.isDebugEnabled()) {
			logger.debug("getMansFromCarByDriverHistory:carId=" + carId
					+ ";sql=" + sql);
		}
		return this.jpaTemplate.execute(new JpaCallback<List<JSONObject>>() {
			public List<JSONObject> doInJpa(EntityManager em)
					throws PersistenceException {
				Query queryObject = em.createNativeQuery(sql.toString());
				queryObject.setParameter(1, carId);
				queryObject.setParameter(2, carId);
				queryObject.setParameter(3, carId);
				queryObject.setParameter(4, carId);
				@SuppressWarnings("unchecked")
				List<Object[]> objs = queryObject.getResultList();
				List<JSONObject> jsons = new ArrayList<JSONObject>();
				if (objs != null && !objs.isEmpty()) {
					JSONObject json;
					for (Object[] obj : objs) {
						try {
							json = new JSONObject();
							int i = 0;

							// 司机的相关信息
							i = buildManJson(json, obj, i);

							// 迁移记录的相关信息
							json.put("h_id", obj[i++]);
							json.put("moveType", obj[i++]);
							json.put("moveTypeDesc",
									convert2ManMoveTypeDesc(json
											.get("moveType")));
							json.put(
									"moveDate",
									convert2ManMoveDate((Date) obj[i++],
											(Date) obj[i++]));
							json.put("fromCarId", null2Empty(obj[i++]));
							json.put("toCarId", null2Empty(obj[i++]));
							json.put("h_fromClasses", obj[i++]);
							json.put("h_toClasses", obj[i++]);

							// 特殊处理
							json.put("judgeType", "司机");// 先假定全部都是司机
							int moveType = json.getInt("moveType");
							if (CarByDriverHistory.isActive(moveType)
									&& carId.toString().equals(
											json.getString("toCarId"))) {
								json.put("judgeStatus", 0);// 当前营运司机
								json.put("judgeClasses",
										convert2ManClassesDesc(json
												.get("h_toClasses")));// 按迁往的班次
							} else {
								json.put("judgeStatus", 1);// 已注销司机
								json.put("judgeClasses",
										convert2ManClassesDesc(json
												.get("h_fromClasses")));// 按迁自的班次
							}

							jsons.add(json);
						} catch (JSONException e) {
							logger.error(e.getMessage(), e);
						}
					}
				}
				return jsons;
			}
		});
	}

	/**
	 * 获取车辆经济合同对应的责任人信息,对同一个责任人的多条经济合同只获取最新的那条：
	 * <p>
	 * [{责任人1信息},{责任人2信息},...]
	 * </p>
	 * 
	 * @param carId
	 * @return
	 */
	private List<JSONObject> getMansFromContract4Charger(final Long carId,
			StringBuffer manSql) {
		final StringBuffer sql = new StringBuffer(manSql);
		sql.append(",c.id c_id,c.status_ c_status");
		sql.append(" from bs_carman m");
		sql.append(" inner join bs_carman_contract mc on mc.man_id=m.id");
		sql.append(" inner join bs_contract c on c.id = mc.contract_id");
		sql.append(" inner join bs_contract_charger cc on cc.id=c.id");
		sql.append(" inner join bs_car_contract carc on carc.contract_id=c.id");
		sql.append(" where carc.car_id = ?");
		sql.append(" and m.status_ in (0,1)");
		// 排除相同责任人的旧记录
		sql.append(" and not exists (select 1 from bs_carman_contract mci");
		sql.append(" 	inner join bs_contract ci on ci.id = mci.contract_id");
		sql.append("	inner join bs_contract_charger cci on cci.id=ci.id");
		sql.append("	inner join bs_car_contract carci on carci.contract_id=ci.id");
		sql.append("	where carci.car_id = carc.car_id and mci.man_id=mc.man_id and ci.start_date > c.start_date ");
		sql.append("    or( cc.agreement_start_date < current_date and cc.quitter_id =mc.man_id))");
		sql.append(" order by c.start_date desc");
		if (logger.isDebugEnabled()) {
			logger.debug("getMansFromContract4Charger:carId=" + carId + ";sql="
					+ sql);
		}
		return this.jpaTemplate.execute(new JpaCallback<List<JSONObject>>() {
			public List<JSONObject> doInJpa(EntityManager em)
					throws PersistenceException {
				Query queryObject = em.createNativeQuery(sql.toString());
				queryObject.setParameter(1, carId);
				@SuppressWarnings("unchecked")
				List<Object[]> objs = queryObject.getResultList();
				List<JSONObject> jsons = new ArrayList<JSONObject>();
				if (objs != null && !objs.isEmpty()) {
					JSONObject json;
					for (Object[] obj : objs) {
						try {
							json = new JSONObject();
							int i = 0;

							// 司机的相关信息
							i = buildManJson(json, obj, i);

							// 合同的相关信息
							json.put("c_id", obj[i++]);
							json.put("c_status", obj[i++]);

							// 特殊处理
							json.put("judgeType", "责任人");// 先假定全部为责任人
							json.put("judgeStatus", json.getInt("c_status"));// 按合同的状态
							json.put("judgeClasses", "");// 无营运班次

							jsons.add(json);
						} catch (JSONException e) {
							logger.error(e.getMessage(), e);
						}
					}
				}
				return jsons;
			}
		});
	}

	/**
	 * 获取车辆劳动合同对应的司机信息,对同一个司机的多条劳动合同只获取最新的那条:112929/
	 * <p>
	 * [{司机1信息},{司机2信息},...]
	 * </p>
	 * 
	 * @param carId
	 * @return
	 */
	private List<JSONObject> getMansFromContract4Labour(final Long carId,
			StringBuffer manSql) {
		final StringBuffer sql = new StringBuffer(manSql);
		sql.append(",c.id c_id,c.status_ c_status");
		sql.append(" from bs_carman m");
		sql.append(" inner join bs_carman_contract mc on mc.man_id=m.id");
		sql.append(" inner join bs_contract c on c.id = mc.contract_id");
		sql.append(" inner join bs_contract_labour cc on cc.id=c.id");
		sql.append(" inner join bs_car_contract carc on carc.contract_id=c.id");
		sql.append(" where carc.car_id = ?");
		// 排除相同司机的旧记录
		sql.append(" and not exists (select 1 from bs_carman_contract mci");
		sql.append(" 	inner join bs_contract ci on ci.id = mci.contract_id");
		sql.append("	inner join bs_contract_labour cci on cci.id=ci.id");
		sql.append("	inner join bs_car_contract carci on carci.contract_id=ci.id");
		sql.append("	where carci.car_id = carc.car_id and mci.man_id=mc.man_id and ci.start_date > c.start_date)");
		sql.append(" order by c.start_date desc");
		if (logger.isDebugEnabled()) {
			logger.debug("getMansFromContract4Labour:carId=" + carId + ";sql="
					+ sql);
		}
		return this.jpaTemplate.execute(new JpaCallback<List<JSONObject>>() {
			public List<JSONObject> doInJpa(EntityManager em)
					throws PersistenceException {
				Query queryObject = em.createNativeQuery(sql.toString());
				queryObject.setParameter(1, carId);
				@SuppressWarnings("unchecked")
				List<Object[]> objs = queryObject.getResultList();
				List<JSONObject> jsons = new ArrayList<JSONObject>();
				if (objs != null && !objs.isEmpty()) {
					JSONObject json;
					for (Object[] obj : objs) {
						try {
							json = new JSONObject();
							int i = 0;

							// 司机的相关信息
							i = buildManJson(json, obj, i);

							// 合同的相关信息
							json.put("c_id", obj[i++]);
							json.put("c_status", obj[i++]);

							// 特殊处理
							json.put("judgeType", "司机");// 先假定全部为司机
							json.put("judgeStatus", json.getInt("c_status"));// 按合同的状态
							json.put("judgeClasses", "");// 无营运班次

							jsons.add(json);
						} catch (JSONException e) {
							logger.error(e.getMessage(), e);
						}
					}
				}
				return jsons;
			}
		});
	}

	/**
	 * 获取车辆营运班次对应的顶班司机信息,对同一个司机的多条营运班次只获取最新的那条
	 * <p>
	 * [{司机1信息},{司机2信息},...]
	 * </p>
	 * 
	 * @param carId
	 * @return
	 */
	private List<JSONObject> getMansFromCarByDriver(final Long carId,
			StringBuffer manSql) {
		final StringBuffer sql = new StringBuffer(manSql);
		sql.append(",cd.id cd_id,cd.status_ cd_status,cd.classes cd_classes");
		sql.append(" from bs_carman m");
		sql.append(" inner join bs_car_driver cd on cd.driver_id=m.id");
		sql.append(" where cd.car_id = ? and cd.classes = 4");
		sql.append(" and m.status_ in (0,1)");
		// 排除相同司机的旧记录
		sql.append(" and not exists (select 1 from bs_car_driver cdi");
		sql.append(" 	where cdi.car_id = cd.car_id and cdi.classes = cd.classes and cdi.driver_id=cd.driver_id");
		sql.append("	and cdi.file_date > cd.file_date)");
		sql.append(" order by cd.status_,cd.classes,cd.file_date desc");
		if (logger.isDebugEnabled()) {
			logger.debug("getMansFromCarByDriver:carId=" + carId + ";sql="
					+ sql);
		}
		return this.jpaTemplate.execute(new JpaCallback<List<JSONObject>>() {
			public List<JSONObject> doInJpa(EntityManager em)
					throws PersistenceException {
				Query queryObject = em.createNativeQuery(sql.toString());
				queryObject.setParameter(1, carId);
				@SuppressWarnings("unchecked")
				List<Object[]> objs = queryObject.getResultList();
				List<JSONObject> jsons = new ArrayList<JSONObject>();
				if (objs != null && !objs.isEmpty()) {
					JSONObject json;
					for (Object[] obj : objs) {
						try {
							json = new JSONObject();
							int i = 0;

							// 司机的相关信息
							i = buildManJson(json, obj, i);

							// 营运班次的相关信息
							json.put("cd_id", obj[i++]);
							json.put("cd_status", obj[i++]);
							json.put("cd_classes", obj[i++]);

							// 特殊处理
							json.put("judgeType", "司机");
							json.put("judgeStatus", json.getInt("cd_status"));// 按营运班次的状态
							json.put("judgeClasses",
									convert2ManClassesDesc(json
											.get("cd_classes")));// 营运班次

							jsons.add(json);
						} catch (JSONException e) {
							logger.error(e.getMessage(), e);
						}
					}
				}
				return jsons;
			}
		});
	}

	// 构建司机的json信息
	private int buildManJson(JSONObject json, Object[] obj, int startIndex)
			throws JSONException {
		json.put("id", obj[startIndex++]);
		json.put("uid", obj[startIndex++]);
		json.put("status", obj[startIndex++]);
		json.put("type", obj[startIndex++]);
		json.put("judgeType", json.get("type"));
		json.put("typeDesc", convert2ManTypeDesc(json.get("type")));
		json.put("name", obj[startIndex++]);
		json.put("sex", convert2ManSexDesc(obj[startIndex++]));
		json.put("origin", null2Empty(obj[startIndex++]));
		json.put("houseType", null2Empty(obj[startIndex++]));
		json.put("address1", null2Empty(obj[startIndex++]));
		json.put("address2", null2Empty(obj[startIndex++]));
		json.put("phone1", null2Empty(obj[startIndex++]));
		json.put("phone2", null2Empty(obj[startIndex++]));
		json.put(
				"phones",
				convert2ManPhones(json.getString("phone1"),
						json.getString("phone2")));
		json.put("identity", null2Empty(obj[startIndex++]));
		json.put("cert4fwzg", null2Empty(obj[startIndex++]));
		json.put("classes", obj[startIndex++]);
		json.put("classesDesc", convert2ManClassesDesc(json.get("classes")));
		json.put("moveType", obj[startIndex++]);
		json.put("moveTypeDesc", convert2ManMoveTypeDesc(json.get("moveType")));
		json.put(
				"moveDate",
				convert2ManMoveDate((Date) obj[startIndex++],
						(Date) obj[startIndex++]));
		json.put("carInfo", null2Empty(obj[startIndex++]));// 营运车辆
		json.put("mainCarId", null2Empty(obj[startIndex++]));// 主车辆id
		json.put("desc", null2Empty(obj[startIndex++]));

		json.put("judgeStatus", json.get("status"));
		return startIndex;
	}

	private String null2Empty(Object obj) {
		if (obj == null)
			return "";
		return obj.toString();
	}

	private String convert2ManMoveDate(Date move_date, Date shiftwork_end_date) {
		if (move_date != null) {
			if (shiftwork_end_date != null) {
				return DateUtils.formatDate(move_date) + "～"
						+ DateUtils.formatDate(shiftwork_end_date);
			} else {
				return DateUtils.formatDate(move_date);
			}
		} else {
			if (shiftwork_end_date != null) {
				return "～" + DateUtils.formatDate(shiftwork_end_date);
			} else {
				return "";
			}
		}
	}

	private String convert2ManTypeDesc(Object type) {
		if (type == null)
			return "";

		int s = Integer.parseInt(type.toString());
		if (s == CarMan.TYPE_DRIVER)
			return "司机";
		else if (s == CarMan.TYPE_CHARGER)
			return "责任人";
		else if (s == CarMan.TYPE_DRIVER_AND_CHARGER)
			return "司机和责任人";
		else if (s == CarMan.TYPE_FEIBIAN)
			return "非编";
		else
			return "";
	}

	private String convert2ManClassesDesc(Object classes) {
		if (classes == null)
			return "";

		int s = Integer.parseInt(classes.toString());
		if (s == CarByDriver.TYPE_ZHENGBAN)
			return "正班";
		else if (s == CarByDriver.TYPE_FUBAN)
			return "副班";
		else if (s == CarByDriver.TYPE_DINGBAN)
			return "顶班";
		else if (s == CarByDriver.TYPE_ZHUGUA)
			return "主挂";
		else
			return "";
	}

	private String convert2ManMoveTypeDesc(Object moveType) {
		if (moveType == null)
			return "";

		int s = Integer.parseInt(moveType.toString());
		if (s == CarByDriverHistory.MOVETYPE_CLDCL)
			return "车辆到车辆";
		else if (s == CarByDriverHistory.MOVETYPE_GSDGSYZX)
			return "公司到公司(已注销)";
		else if (s == CarByDriverHistory.MOVETYPE_ZXWYQX)
			return "注销未有去向";
		else if (s == CarByDriverHistory.MOVETYPE_YWGSQH)
			return "由外公司迁回";
		else if (s == CarByDriverHistory.MOVETYPE_JHWZX)
			return "交回未注销";
		else if (s == CarByDriverHistory.MOVETYPE_XRZ)
			return "新入职";
		else if (s == CarByDriverHistory.MOVETYPE_ZCD)
			return "转车队";
		else if (s == CarByDriverHistory.MOVETYPE_DINGBAN)
			return "顶班";
		else if (s == CarByDriverHistory.MOVETYPE_JHZC)
			return "交回后转车";
		else
			return "";
	}

	private String convert2ManSexDesc(Object sex) {
		if (sex == null)
			return "无";

		int s = Integer.parseInt(sex.toString());
		if (s == ActorDetail.SEX_MAN)
			return "男";
		else if (s == ActorDetail.SEX_WOMAN)
			return "女";
		else
			return "无";
	}

	private String convert2ManPhones(String phone1, String phone2) {
		if (phone1 != null && phone1.length() > 0) {
			if (phone2 != null && phone2.length() > 0) {
				return phone1 + ", " + phone2;
			} else {
				return phone1;
			}
		} else {
			if (phone2 != null && phone2.length() > 0) {
				return phone2;
			} else {
				return "";
			}
		}
	}
}
