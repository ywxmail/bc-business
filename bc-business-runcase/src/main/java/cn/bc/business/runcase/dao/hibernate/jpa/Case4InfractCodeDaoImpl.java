/**
 * 
 */
package cn.bc.business.runcase.dao.hibernate.jpa;

import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import cn.bc.business.runcase.dao.Case4InfractCodeDao;
import cn.bc.business.runcase.domain.Case4InfractCode;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;

/**
 * 违法代码管理Dao的hibernate jpa实现
 * 
 * @author zxr
 */
public class Case4InfractCodeDaoImpl extends
		HibernateCrudJpaDao<Case4InfractCode> implements Case4InfractCodeDao {
	protected final Log logger = LogFactory.getLog(getClass());
	@SuppressWarnings("unused")
	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public Case4InfractCode getEntityByCode(String code) {
		Case4InfractCode case4InfractCode = null;
		String hql = "select c from Case4InfractCode c where c.code=?";
		List<?> list = this.getJpaTemplate().find(hql, new Object[] { code });
		if (list.size() == 1) {
			case4InfractCode = (Case4InfractCode) list.get(0);
			return case4InfractCode;
		} else if (list.size() == 0) {
			return null;
		} else {
			case4InfractCode = (Case4InfractCode) list.get(0);
		}
		return case4InfractCode;
	}

}