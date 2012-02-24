package cn.bc.business.mix.dao.hibernate.jpa;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import cn.bc.business.mix.dao.InfoCenterDao;
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
		sql.append(" order by c.register_date desc");

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
		sql.append("select c.id,c.code,c.plate_type,c.plate_no,c.status_,c.return_date from bs_car c");
		sql.append(" where c.plate_no like ?");
		sql.append(" order by c.register_date desc");

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
							json.put("returnDate", DateUtils.formatDate((Date)rs.get(j)[5]));
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
		JSONObject json = new JSONObject();
		json.put("id", carId);

		// ==基本信息：车辆==
		JSONObject car = this.getCar(carId);
		json.put("car", car);

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
			car.put("zb", policy.get("zb"));
		}else{
			car.put("zb", false);
		}

		// ==提醒信息==
		JSONArray messages = new JSONArray();
		json.put("messages", messages);

		// ==提醒信息：黑名单==
		JSONArray blacklist = this.getBlacklist(carId);
		if (blacklist != null && blacklist.length() > 0) {
			for (int i = 0; i < blacklist.length(); i++) {
				messages.put(blacklist.get(i));
			}
		}

		// ==联系人信息==
		JSONArray mans = this.getMans(carId);
		json.put("mans", mans);

		// 返回
		return json;
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
	private JSONArray getMans(final Long carId) {
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
		sql.append(" where carc.car_id = ? and c.main = 0)");

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
							json.put("type", getManType(obj[i++]));
							json.put("name", obj[i++]);
							json.put("sex", getManSex(obj[i++]));
							json.put("origin", null2Empty(obj[i++]));
							json.put("houseType", null2Empty(obj[i++]));
							json.put("address1", null2Empty(obj[i++]));
							json.put("address2", null2Empty(obj[i++]));
							json.put(
									"phones",
									getManPhones((String) obj[i++],
											(String) obj[i++]));
							json.put("identity", null2Empty(obj[i++]));
							json.put("cert4fwzg", null2Empty(obj[i++]));
							json.put("classes", getManClasses(obj[i++]));
							json.put("moveType", getManMoveType(obj[i++]));
							json.put(
									"moveDate",
									getManMoveDate((Date) obj[i++],
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

			private String getManMoveDate(Date move_date,
					Date shiftwork_end_date) {
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

			private String getManType(Object type) {
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
					return "未知";
			}

			private String getManClasses(Object classes) {
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
					return "未知";
			}

			private String getManMoveType(Object moveType) {
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
					return "未知";
			}

			private String getManSex(Object sex) {
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

			private String getManPhones(String phone1, String phone2) {
				if (phone1 != null && phone1.length() > 0) {
					if (phone2 != null && phone2.length() > 0) {
						return phone1 + "，" + phone2;
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
		sql.append("select b.id,b.subject,b.lock_date,b.type_,m.name");
		sql.append(" from bs_blacklist b");
		sql.append(" inner join bs_carman m on m.id=b.driver_id");
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
							json.put("limit", null2Empty(obj[i++]));
							json.put("link", null2Empty(obj[i++]));

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
						int i = 2;
						json.put("zb", obj[i++]);
						json.put("commerialStartDate", obj[i++]);
						json.put("commerialEndDate", obj[i++]);
						json.put("greenslipSameDate", obj[i++]);
						json.put("greenslipStartDate", obj[i++]);
						json.put("greenslipEndDate", obj[i++]);
					} catch (JSONException e) {
						logger.error(e.getMessage(), e);
					}
					return json;
				}else{
					return null;
				}
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
		sql.append("select c.id as id,c.status_,c.plate_type,c.plate_no");
		sql.append(",c.code,c.company,unit.name as unit_name,m.name as motorcade_name");
		sql.append(",c.factory_type,c.factory_model,c.engine_no,c.vin,c.color");
		sql.append(",c.bs_type,c.register_date,c.operate_date,c.cert_no4");
		sql.append(",c.taximeter_factory,c.taximeter_type,c.desc1,c.desc2,c.desc3");
		sql.append(",c.lpg_name,c.lpg_model,c.car_tv_screen");
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
				JSONObject json = new JSONObject();
				if (car != null) {
					try {
						// int i = 0;
						// json.put("id", car[i++]);
						// json.put("status", car[i++]);
						// json.put("plate", car[i++] + "." + car[i++]);

						int i = 4;
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
						json.put("taximeter", car[i++] + " " + car[i++]);
						json.put(
								"desc",
								getDesc((String) car[i++], (String) car[i++],
										(String) car[i++]));

						json.put("lpg",
								getLPG((String) car[i++], (String) car[i++]));
						json.put("tv", null2Empty(car[i++]));
					} catch (JSONException e) {
						logger.error(e.getMessage(), e);
					}
				}
				return json;
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
						+ (desc3 != null && desc2.length() > 0 ? "\r\n----备注3----\r\n"
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
		sql.append("select cc.id,c.status_,cc.sign_type,cc.bs_type");
		sql.append(",c.start_date,c.end_date");
		sql.append(",cc.payment_date,cc.include_cost,c.main");
		sql.append(" from BS_CONTRACT_CHARGER cc");
		sql.append(" inner join BS_CONTRACT c on cc.id = c.id");
		sql.append(" inner join BS_CAR_CONTRACT carc on c.id = carc.contract_id");
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
						logger.debug("car = null,id=" + carId);
					obj = null;
				}
				JSONObject json = new JSONObject();
				if (obj != null) {
					try {
						int i = 3;
						String t;
						json.put("businessType", null2Empty(obj[i++]));
						json.put("dateRange",
								DateUtils.formatDate((Date) obj[i++]) + "～"
										+ DateUtils.formatDate((Date) obj[i++]));
						t = (String) obj[i++];
						json.put("paymentDate", ("0".equals(t) ? "月末"
								: (t == null ? "" : t)));
						json.put("includeCost", obj[i++]);
					} catch (JSONException e) {
						logger.error(e.getMessage(), e);
					}
				}
				return json;
			}

		});
	}

	private String null2Empty(Object obj) {
		if (obj == null)
			return "";
		return obj.toString();
	}
}
