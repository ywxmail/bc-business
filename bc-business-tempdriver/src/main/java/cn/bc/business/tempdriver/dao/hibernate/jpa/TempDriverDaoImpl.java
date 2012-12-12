package cn.bc.business.tempdriver.dao.hibernate.jpa;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import cn.bc.business.tempdriver.dao.TempDriverDao;
import cn.bc.business.tempdriver.domain.TempDriver;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.NotEqualsCondition;
import cn.bc.docs.domain.Attach;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;

/**
 * 司机招聘Dao的实现
 * 
 * @author lbj
 * 
 */
public class TempDriverDaoImpl extends HibernateCrudJpaDao<TempDriver>
		implements TempDriverDao {
	private JdbcTemplate jdbcTemplate;
	//private static Log logger = LogFactory.getLog(TempDriverDaoImpl.class);

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public boolean isUniqueCertIdentity(Long id, String certIdentity) {
		AndCondition ac = new AndCondition();
		ac.add(new EqualsCondition("certIdentity", certIdentity));
		if (id != null) {
			ac.add(new NotEqualsCondition("id", id));
		}
		return this.createQuery().condition(ac).count() < 1;
	}

	public TempDriver loadByCertIdentity(String certIdentity) {
		Condition c = new EqualsCondition("certIdentity", certIdentity);
		return this.createQuery().condition(c).singleResult();
	}

	private static DateFormat df2month = new SimpleDateFormat("yyyyMM");
	private static DateFormat df2ms = new SimpleDateFormat("yyyyMMddHHmmssSSSS");

	public void doSyncPortrait() throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append("select p.file_date p_file_date,p.type_ p_type,p.data_ p_data,c.code d_code");
		sql.append(",c.name d_name,d.id d_id,d.uid_ d_uid,a.id as attach_id,a.path attach_path");
		sql.append(",(select h.id from bc_identity_actor_history h where h.current=true and h.actor_id=c.author_id) hid");
		sql.append(" from bc_idcard_pic p");
		sql.append(" inner join bc_idcard c on c.id=p.pid");
		sql.append(" inner join bs_temp_driver d on d.cert_identity=c.code");
		sql.append(" left join bc_docs_attach a on a.puid=d.uid_");
		sql.append(" where (a.id is null or (a.ptype='portrait' and a.modified_date < p.file_date))");
		sql.append(" and not exists (");
		sql.append("	select 0 from bc_idcard_pic p1 inner join bc_idcard c1 on c1.id=p1.pid");
		sql.append("	where p1.pid = p.pid and p1.file_date > p.file_date");
		sql.append(")");
		sql.append(" order by p.file_date asc;");

		if (logger.isDebugEnabled()) {
			logger.debug("doSyncPortrait:sql=" + sql);
		}

		// 获取要处理的信息
		List<Map<String, Object>> list = this.jdbcTemplate.queryForList(sql
				.toString());
		if (logger.isInfoEnabled()) {
			logger.info("	找到" + list.size() + "张需要同步的司机身份证照片");
		}

		// 循环保存每张照片到司机招聘模块
		Calendar now;
		String path, subPath;
		File file;
		byte[] data;
		String sql1;
		int rs;
		for (Map<String, Object> map : list) {
			now = Calendar.getInstance();
			subPath = df2month.format(now.getTime()) + "/"
					+ df2ms.format(Calendar.getInstance().getTime()) + "."
					+ map.get("p_type");
			path = Attach.DATA_REAL_PATH + "/" + subPath;
			logger.debug("path=" + path);

			// 构建文件要保存到的目录
			file = new File(path);
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}

			// 写入文件
			data = (byte[]) map.get("p_data");
			FileUtils.writeByteArrayToFile(new File(path), data);

			// 保存为附件
			Integer attach_id = (Integer) map.get("attach_id");
			if (attach_id != null) {// 更新原有的附件
				sql1 = "UPDATE bc_docs_attach SET size_=?, format=?, path=?, modifier_id=?, modified_date=? WHERE id=?";
				rs = this.jdbcTemplate.update(sql1, new Integer(data.length),
						map.get("p_type"), subPath, map.get("hid"),
						map.get("p_file_date"), attach_id);
				if (logger.isDebugEnabled()) {
					logger.debug("	更新附件：result=" + rs + ",sql=" + sql1);
				}
			} else {// 创建新的附件
				sql1 = "INSERT INTO bc_docs_attach(id, status_, ptype, count_, apppath, puid, size_, format, subject";
				sql1 += ", path, file_date, author_id, modified_date, modifier_id)";
				sql1 += " VALUES (NEXTVAL('hibernate_sequence'), 0, 'portrait', 0, false, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
				rs = this.jdbcTemplate.update(sql1, map.get("d_uid"),
						new Integer(data.length), map.get("p_type"),
						map.get("d_name") + "的身份证照片", subPath,
						map.get("p_file_date"), map.get("hid"),
						map.get("p_file_date"), map.get("hid"));
				if (logger.isDebugEnabled()) {
					logger.debug("	插入附件：result=" + rs + ",sql=" + sql1);
				}
			}
		}
	}
}
