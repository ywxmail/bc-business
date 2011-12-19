/**
 * 
 */
package cn.bc.business.carman.dao.hibernate.jpa;

import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import cn.bc.business.car.domain.Car;
import cn.bc.business.carman.dao.CarByDriverDao;
import cn.bc.business.carman.domain.CarByDriver;
import cn.bc.core.exception.CoreException;
import cn.bc.db.JdbcUtils;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;

/**
 * 司机营运车辆Dao的hibernate jpa实现
 * 
 * @author dragon
 */
public class CarByDriverDaoImpl extends HibernateCrudJpaDao<CarByDriver>
		implements CarByDriverDao {
	protected final Log logger = LogFactory.getLog(getClass());
	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public Car findBycarManId(Long carManId) {
		Car car = null;
		String hql = "select c.car from CarByDriver c where c.driver.id=? and c.classes=?";
		List<?> list = this.getJpaTemplate()
				.find(hql,
						new Object[] { carManId,
								new Integer(CarByDriver.TYPE_ZHENGBAN) });
		if (list.size() == 1) {
			car = (Car) list.get(0);
			return car;
		} else if (list.size() == 0) {
			return null;
		} else {
			car = (Car) list.get(0);

			if (logger.isDebugEnabled()) {
				logger.debug("有两个或两个以上正班的信息，已选择其中一个正班信息显示！");

			}
			return car;
		}

	}

	/**
	 * 更新车辆模块的司机信息
	 * 
	 * @param id
	 */
	public void updateCar4Driver(Long id) {
		// 根据不同数据库生成相应sql：http://qun.qq.com/air/#6577377/bbs/view/cd/1/td/8
		String sql = "update BS_CAR as c set driver = (select";
		String caseStr = "(case when cm.classes=1 then '正班' when cm.classes=2 then '副班' when cm.classes=3 then '顶班' else '无' end)";
		if (JdbcUtils.DB_MYSQL.equals(JdbcUtils.dbtype)) {
			sql += " group_concat(concat(m.name,'('," + caseStr + ",')'))";
		} else if (JdbcUtils.DB_ORACLE.equals(JdbcUtils.dbtype)) {
			sql += " wmsys.wm_concat(m.name || '(' || " + caseStr + " || ')')";
		} else if ("mssql".equals(JdbcUtils.dbtype)) {
		} else if (JdbcUtils.DB_POSTGRESQL.equals(JdbcUtils.dbtype)) {
			sql += " string_agg(concat(m.name,'('," + caseStr + ",')'),',')";
		} else {
			throw new CoreException("unsupport database for updateCar4Driver");
		}
		sql += " from BS_CAR_DRIVER cm inner join BS_CARMAN m on m.id = cm.driver_id where cm.status_=0 and cm.car_id = c.id";
		sql += ") where id = ?";

		if (logger.isDebugEnabled()) {
			logger.debug("sql=" + sql);
			logger.debug("id=" + id);
		}
		this.jdbcTemplate.update(sql, new Object[] { id });
	}
}