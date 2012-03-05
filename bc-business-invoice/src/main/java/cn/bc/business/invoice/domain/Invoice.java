/**
 * 
 */
package cn.bc.business.invoice.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

import cn.bc.identity.domain.RichFileEntityImpl;

/**
 * 票务
 * 
 * @author wis
 */
@Entity
@Table(name = "BS_INVOICE")
public class Invoice extends RichFileEntityImpl {
	private static final long serialVersionUID = 1L;
	public static final String KEY_UID = "invoice.uid";
	public static final String KEY_CODE = "invoice.code";


}