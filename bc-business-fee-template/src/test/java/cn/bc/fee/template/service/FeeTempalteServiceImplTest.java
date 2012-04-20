package cn.bc.fee.template.service;

import java.util.Calendar;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import cn.bc.BCConstants;
import cn.bc.business.fee.template.domain.FeeTemplate;
import cn.bc.business.fee.template.service.FeeTemplateService;
import cn.bc.identity.service.ActorHistoryService;
import cn.bc.test.AbstractEntityCrudTest;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration("classpath:spring-test.xml")
public class FeeTempalteServiceImplTest extends
		AbstractEntityCrudTest<Long, FeeTemplate> {

	FeeTemplateService feeTemplateService;
	ActorHistoryService actorHistoryService;

	@Autowired
	public void setTemplateService(FeeTemplateService feeTemplateService) {
		this.feeTemplateService = feeTemplateService;
		this.crudOperations = feeTemplateService;
	}

	@Autowired
	public void setActorHistoryService(ActorHistoryService actorHistoryService) {
		this.actorHistoryService = actorHistoryService;
	}

	@Override
	protected FeeTemplate createInstance(String config) {
		FeeTemplate feeTemplate = super.createInstance(config);
		feeTemplate.setAuthor(this.actorHistoryService.loadByCode("admin"));
		feeTemplate.setType(FeeTemplate.TYPE_FEE);
		feeTemplate.setCount(1100);
		feeTemplate.setPrice(100f);
		feeTemplate.setFileDate(Calendar.getInstance());
		feeTemplate.setDesc("desc");
		feeTemplate.setModule("经济合同");
		feeTemplate.setOrder("01");
		feeTemplate.setName("费用测试1");
		feeTemplate.setPayType(FeeTemplate.PAY_TYPE_ALL);
		feeTemplate.setStatus(BCConstants.STATUS_ENABLED);
		return feeTemplate;
	}

}
