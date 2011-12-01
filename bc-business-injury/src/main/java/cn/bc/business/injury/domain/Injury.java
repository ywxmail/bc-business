/**
 * 
 */
package cn.bc.business.injury.domain;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import cn.bc.identity.domain.RichFileEntityImpl;

/**
 * 工伤类
 * 
 * @author wis
 */
@Entity
@Table(name = "BS_INDUSTRIAL_INJURY")
@Inheritance(strategy = InheritanceType.JOINED)
public class Injury extends RichFileEntityImpl {
	private static final long serialVersionUID = 1L;
	public static final String ATTACH_TYPE = Injury.class.getSimpleName();
	
}