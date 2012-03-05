/**
 * 
 */
package cn.bc.business.invoice.dao.hibernate.jpa;

import cn.bc.business.invoice.dao.InvoiceDao;
import cn.bc.business.invoice.domain.Invoice;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;

/**
 * 票务Dao的hibernate jpa实现
 * 
 * @author wis
 */
public class InvoiceDaoImpl extends HibernateCrudJpaDao<Invoice> implements InvoiceDao {

}