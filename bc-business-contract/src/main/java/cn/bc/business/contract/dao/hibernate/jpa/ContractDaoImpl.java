/**
 * 
 */
package cn.bc.business.contract.dao.hibernate.jpa;

import cn.bc.business.contract.dao.ContractDao;
import cn.bc.business.contract.domain.Contract;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;


/**
 * 合同Dao的hibernate jpa实现
 * 
 * @author dragon
 */
public class ContractDaoImpl extends HibernateCrudJpaDao<Contract> implements ContractDao{


}