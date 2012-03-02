/**
 * 
 */
package cn.bc.business.sync.dao.hibernate.jpa;

import cn.bc.business.spider.domain.JinDunJTWF;
import cn.bc.business.sync.dao.JinDunJTWFDao;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;


/**
 * 金盾网交通违章Dao 的hibernate jpa实现
 * 
 * @author wis
 */
public class JinDunJTWFDaoImpl extends HibernateCrudJpaDao<JinDunJTWF> implements JinDunJTWFDao{
//  TODO
//	/**
//	 * 根据违章顺序号查找金盾网交通违章记录
//	 * @param syncCode
//	 * @return
//	 */
//	public JinDunJTWF findJinDunJTWFBySyscCode(String syncCode) {
//		@SuppressWarnings("unchecked")
//		List<JinDunJTWF> list = this.getJpaTemplate()
//			.find("from JinDunJTWF jindunJTWF where jindunJTWF.syncCode=?",
//					syncCode);
//		if(list.size() > 0){
//			return list.get(0);
//		}else{
//			return null;
//		}
//	}

}