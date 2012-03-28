package cn.bc.business.ownership.web.struts2;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.ownership.domain.Ownership;
import cn.bc.business.web.struts2.FileEntityAction;

/**
 * 车辆经营权Action
 * 
 * @author zxr
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class OwnershipAction extends FileEntityAction<Long, Ownership> {
	private static final long serialVersionUID = 1L;

}
