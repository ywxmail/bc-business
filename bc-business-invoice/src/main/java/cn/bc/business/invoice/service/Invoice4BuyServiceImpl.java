package cn.bc.business.invoice.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.business.invoice.dao.Invoice4BuyDao;
import cn.bc.business.invoice.domain.Invoice4Buy;
import cn.bc.core.service.DefaultCrudService;
import cn.bc.web.formater.NubmerFormater;

/**
 * 票务采购Service实现类
 * 
 * @author wis
 */
public class Invoice4BuyServiceImpl extends DefaultCrudService<Invoice4Buy> implements
		Invoice4BuyService {
	private Invoice4BuyDao invoice4BuyDao;
	
	@Autowired
	public void setInvoice4BuyDao(Invoice4BuyDao invoice4BuyDao) {
		this.invoice4BuyDao = invoice4BuyDao;
		this.setCrudDao(invoice4BuyDao);
	}

	public List<Map<String, String>> findEnabled4Option() {
		return this.invoice4BuyDao.findEnabled4Option();
	}

	public List<Invoice4Buy> selectInvoice4BuyByCode(String code) {
		return this.invoice4BuyDao.selectInvoice4BuyByCode(code);
	}

	public List<Invoice4Buy> selectInvoice4BuyByCode(String code, Long id) {
		return this.invoice4BuyDao.selectInvoice4BuyByCode(code,id);
	}
	
	public List<Map<String, String>> findCompany4Option(){
		return invoice4BuyDao.findCompany4Option();
	}

	public List<Map<String, String>> findSellDetail(Long id) {
		return invoice4BuyDao.findSellDetail(id);
	}

	public List<String> findBalanceNumberByInvoice4BuyId(Long id) {
		return invoice4BuyDao.findBalanceNumberByInvoice4BuyId(id);
	}

	public List<String> findBalanceCountByInvoice4BuyId(Long id) {
		return invoice4BuyDao.findBalanceCountByInvoice4BuyId(id);
	}

	public List<Map<String, String>> findOneInvoice4Buy(Long id) {
		return invoice4BuyDao.findOneInvoice4Buy(id);
	}

	public List<Map<String, String>> findRefundEnabled4Option() {
		return invoice4BuyDao.findRefundEnabled4Option();
	}
	
	public List<Map<String,String>> findBalanceNumber(Long id) {
		if(id==null)
			return null;
		return buyBalanceNumber(id,null);
	}

	public List<Map<String, String>> findBalanceNumberExSell(Long id,
			Long sellId) {
		if(id==null||sellId==null)
			return null;
		return buyBalanceNumber(id,sellId);
	}

	public List<Map<String, String>> findBalanceNumberExRefund(Long id,
			Long refundId) {
		if(id==null||refundId==null)
			return null;
		return buyBalanceNumber(id,refundId);
	}
	
	//求采购单剩余号码段
	private List<Map<String, String>> buyBalanceNumber(Long id,
			Long sid){
		Invoice4Buy buy=this.load(id);
		Map<String,String> bmap=new HashMap<String, String>();
		bmap.put("sNo", buy.getStartNo());
		bmap.put("eNo", buy.getEndNo());
		//声明剩余号码段集合
		List<Map<String, String>> bnumber=new ArrayList<Map<String,String>>();
		//先加进采购单的号码范围
		bnumber.add(bmap);
		//采购单对应的销售单和退票单的号码范围集合
		List<Map<String, String>> sDetailList =this.invoice4BuyDao.findInvoiceDetail(id, sid);
		
		for(Map<String, String> m4sell:sDetailList){
			//销售或退票单开始号
			String startNo4Sell=m4sell.get("startNo");
			//销售或退票单结束号
			String endNo4Sell=m4sell.get("endNo");
			int s4s=Integer.valueOf(startNo4Sell);
			int e4s=Integer.valueOf(endNo4Sell);
			
			//实销
			if(m4sell.get("sellType").equals("1")){
				//遍历去剩余号码段集合
				for(int i=0;i<bnumber.size();i++){
				   //取剩余号码
				   String startNo4b=bnumber.get(i).get("sNo");
				   String endNo4b=bnumber.get(i).get("eNo");
				   int s4b=Integer.valueOf(startNo4b);
				   int e4b=Integer.valueOf(endNo4b);
				   
				   //根据情况生成新的剩余号码段
				   if(s4s<=s4b&&e4s>=e4b){
					   bnumber.remove(i);
					   break;
				   }else if(s4s>s4b&&e4s<e4b){
					   bmap=new HashMap<String, String>(); 
					   bmap.put("sNo", startNo4b);
					   bmap.put("eNo", addZore(startNo4b, s4s-1));
					   bnumber.add(bmap);
					   bmap=new HashMap<String, String>(); 
					   bmap.put("sNo", addZore(endNo4b, e4s+1));
					   bmap.put("eNo", endNo4b);
					   bnumber.add(bmap);
					   //删除原剩余号码段
					   bnumber.remove(i);
					   break;
				   }else if(s4s==s4b&&e4s<e4b){
					   bmap=new HashMap<String, String>(); 
					   bmap.put("sNo", addZore(endNo4b, e4s+1));
					   bmap.put("eNo", endNo4b);
					   bnumber.add(bmap);
					   //删除原剩余号码段
					   bnumber.remove(i);	   
					   break;
				   }else if(e4s==e4b&&s4s>s4b){
					   bmap=new HashMap<String, String>(); 
					   bmap.put("sNo", startNo4b);
					   bmap.put("eNo", addZore(startNo4b, s4s-1));
					   bnumber.add(bmap);
					   //删除原剩余号码段
					   bnumber.remove(i);
					   break;
				   }
				}
			//退票
			}else if(m4sell.get("sellType").equals("2")){
				//遍历去剩余号码段集合
				int j=0;
				for(;j<bnumber.size();j++){
				   //取剩余号码
				   String startNo4b=bnumber.get(j).get("sNo");
				   String endNo4b=bnumber.get(j).get("eNo");
				   int s4b=Integer.valueOf(startNo4b);
				   int e4b=Integer.valueOf(endNo4b);
				   
				   //根据情况生成新的剩余号码段
				   if(s4s>=s4b&&e4s<=e4b){
					   j=0;
					   break;
				   }else if(s4s<s4b&&e4s>e4b){
					   bmap=new HashMap<String, String>(); 
					   bmap.put("sNo", startNo4Sell);
					   bmap.put("eNo", endNo4Sell);
					   bnumber.add(bmap);
					   //删除原剩余号码段
					   bnumber.remove(j);
					   j=0;
					   break;
				   }else if(s4s==s4b&&e4s>e4b){
					   bmap=new HashMap<String, String>(); 
					   bmap.put("sNo", addZore(endNo4Sell, e4b+1));
					   bmap.put("eNo", endNo4Sell);
					   bnumber.add(bmap);
					   //删除原剩余号码段
					   bnumber.remove(j);
					   j=0;
					   break;
				   }else if(e4s==e4b&&s4s<s4b){
					   bmap=new HashMap<String, String>(); 
					   bmap.put("sNo", startNo4Sell);
					   bmap.put("eNo", addZore(startNo4Sell, s4b-1));
					   bnumber.add(bmap);
					   //删除原剩余号码段
					   bnumber.remove(j);
					   j=0;
					   break;
				   }
				}   
				if(j==bnumber.size()){
				   bmap=new HashMap<String, String>(); 
					bmap.put("sNo", startNo4Sell);
					bmap.put("eNo", endNo4Sell);
					bnumber.add(bmap);
				}
			}
		}		
		return bnumber;
	}
	
	//发票编码 整数补零前序
	private String addZore(String pattenStr,int value){
		int length=pattenStr.length();
		//格式化样式根据字符串的长度生成
		String patten="";
		int i=0;
		while(i<length){
			patten+="0";
			i++;
		}
		return new NubmerFormater(patten).format(value);
	}
	
}