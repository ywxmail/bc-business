/**
 * 
 */
package cn.bc.business.sync.service;

import cn.bc.business.sync.dao.JiaoWeiADVICEDao;
import cn.bc.business.sync.domain.JiaoWeiADVICE;
import cn.bc.core.service.DefaultCrudService;

/**
 * 交委投诉与建议Service的实现
 * 
 * @author wis
 */
public class JiaoWeiADVICEServiceImpl extends DefaultCrudService<JiaoWeiADVICE> implements
	JiaoWeiADVICEService {
	private JiaoWeiADVICEDao jiaoWeiADVICEDao;

	public JiaoWeiADVICEDao getjiaoWeiADVICEDao() {
		return jiaoWeiADVICEDao;
	}

	public void setJiaoWeiADVICEDao(JiaoWeiADVICEDao jiaoWeiADVICEDao) {
		this.jiaoWeiADVICEDao = jiaoWeiADVICEDao;
		this.setCrudDao(jiaoWeiADVICEDao);
	}
}