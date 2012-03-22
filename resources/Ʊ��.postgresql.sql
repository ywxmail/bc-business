DROP TABLE IF EXISTS BS_INVOICE_SELL_DETAIL;
DROP TABLE IF EXISTS BS_INVOICE_SELL;
DROP TABLE IF EXISTS BS_INVOICE_BUY;

-- 发票采购单
CREATE TABLE BS_INVOICE_BUY (
   ID                   INTEGER           	NOT NULL,
   STATUS_              NUMERIC(1)			NOT NULL,
   COMPANY              VARCHAR(255)		NOT NULL,
   CODE                 VARCHAR(255)		NOT NULL,
   TYPE_                NUMERIC(1)			NOT NULL,
   START_NO             VARCHAR(255)		NOT NULL,
   END_NO               VARCHAR(255)		NOT NULL,
   COUNT_               INTEGER           	NOT NULL,
   EACH_COUNT			INTEGER				NOT NULL,
   UNIT_                INTEGER            	NOT NULL,
   BUY_PRICE            NUMERIC(10,2)		NOT NULL,
   SELL_PRICE           NUMERIC(10,2)		NOT NULL,
   BUYER_ID             INTEGER,
   BUY_DATE             TIMESTAMP			NOT NULL,
   FILE_DATE            TIMESTAMP			NOT NULL,
   AUTHOR_ID            INTEGER           	NOT NULL,
   MODIFIED_DATE        TIMESTAMP,
   MODIFIER_ID          INTEGER,
   DESC_                VARCHAR(4000),
   CONSTRAINT BSPK_INVOICE_BUY PRIMARY KEY (ID)
);
COMMENT ON TABLE BS_INVOICE_BUY IS '发票采购单';
COMMENT ON COLUMN BS_INVOICE_BUY.STATUS_ IS '状态:0-正常,1-作废';
COMMENT ON COLUMN BS_INVOICE_BUY.COMPANY IS '公司';
COMMENT ON COLUMN BS_INVOICE_BUY.CODE IS '发票代码';
COMMENT ON COLUMN BS_INVOICE_BUY.TYPE_ IS '发票类型:1-打印票,2-手撕票';
COMMENT ON COLUMN BS_INVOICE_BUY.START_NO IS '开始号';
COMMENT ON COLUMN BS_INVOICE_BUY.END_NO IS '结束号';
COMMENT ON COLUMN BS_INVOICE_BUY.COUNT_ IS '采购数量';
COMMENT ON COLUMN BS_INVOICE_BUY.EACH_COUNT IS '每(卷/本)数量';
COMMENT ON COLUMN BS_INVOICE_BUY.UNIT_ IS '单位:1-卷,2-本;每卷100张';
COMMENT ON COLUMN BS_INVOICE_BUY.BUY_PRICE IS '采购单价';
COMMENT ON COLUMN BS_INVOICE_BUY.SELL_PRICE IS '销售单价';
COMMENT ON COLUMN BS_INVOICE_BUY.BUYER_ID IS '采购人ID';
COMMENT ON COLUMN BS_INVOICE_BUY.BUY_DATE IS '采购日期';
COMMENT ON COLUMN BS_INVOICE_BUY.FILE_DATE IS '创建时间';
COMMENT ON COLUMN BS_INVOICE_BUY.AUTHOR_ID IS '创建人ID';
COMMENT ON COLUMN BS_INVOICE_BUY.MODIFIED_DATE IS '最后修改时间';
COMMENT ON COLUMN BS_INVOICE_BUY.MODIFIER_ID IS '最后修改人ID';
COMMENT ON COLUMN BS_INVOICE_BUY.DESC_ IS '备注';
ALTER TABLE BS_INVOICE_BUY ADD CONSTRAINT BSFK_INVOICEBUY_BUYER FOREIGN KEY (BUYER_ID)
      REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID);
ALTER TABLE BS_INVOICE_BUY ADD CONSTRAINT BSFK_INVOICEBUY_AUTHOR FOREIGN KEY (AUTHOR_ID)
      REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID);
ALTER TABLE BS_INVOICE_BUY ADD CONSTRAINT BSFK_INVOICEBUY_MODIFIER FOREIGN KEY (MODIFIER_ID)
      REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID);
CREATE INDEX BSIDX_INVOICEBUY_COMPANY ON BS_INVOICE_BUY (COMPANY);
CREATE INDEX BSIDX_INVOICEBUY_STARTNO ON BS_INVOICE_BUY (START_NO);
CREATE INDEX BSIDX_INVOICEBUY_ENDNO ON BS_INVOICE_BUY (END_NO);

-- 发票销售单
CREATE TABLE BS_INVOICE_SELL (
   ID                   INTEGER          	NOT NULL,
   STATUS_              NUMERIC(1)      	NOT NULL,
   BUYER_ID             INTEGER,
   BUYER_NAME           VARCHAR(255),
   CAR_ID               INTEGER          	NOT NULL,
   CAR_PLATE            VARCHAR(255)		NOT NULL,
   MOTORCADE_ID        	INTEGER           	NOT NULL,
   COMPANY              VARCHAR(255)		NOT NULL,
   SELL_DATE            TIMESTAMP      		NOT NULL,
   CASHIER_ID           INTEGER           	NOT NULL,
   PAY_TYPE             NUMERIC(1)      	NOT NULL,
   BANK_CODE            VARCHAR(255),
   DESC_                VARCHAR(4000),
   FILE_DATE            TIMESTAMP      		NOT NULL,
   AUTHOR_ID           	INTEGER          	NOT NULL,
   MODIFIED_DATE        TIMESTAMP,
   MODIFIER_ID          INTEGER,
   CONSTRAINT BSPK_INVOICE_SELL PRIMARY KEY (ID)
);
COMMENT ON TABLE BS_INVOICE_SELL IS '发票销售单';
COMMENT ON COLUMN BS_INVOICE_SELL.STATUS_ IS '状态:0-正常,1-作废';
COMMENT ON COLUMN BS_INVOICE_SELL.BUYER_ID IS '购买人ID';
COMMENT ON COLUMN BS_INVOICE_SELL.BUYER_NAME IS '购买人姓名';
COMMENT ON COLUMN BS_INVOICE_SELL.CAR_ID IS '车辆ID';
COMMENT ON COLUMN BS_INVOICE_SELL.CAR_PLATE IS '车牌';
COMMENT ON COLUMN BS_INVOICE_SELL.MOTORCADE_ID IS '车队ID';
COMMENT ON COLUMN BS_INVOICE_SELL.COMPANY IS '公司';
COMMENT ON COLUMN BS_INVOICE_SELL.SELL_DATE IS '销售日期';
COMMENT ON COLUMN BS_INVOICE_SELL.CASHIER_ID IS '收银员ID';
COMMENT ON COLUMN BS_INVOICE_SELL.PAY_TYPE IS '收款方式';
COMMENT ON COLUMN BS_INVOICE_SELL.BANK_CODE IS '银行流水号';
COMMENT ON COLUMN BS_INVOICE_SELL.DESC_ IS '备注';
COMMENT ON COLUMN BS_INVOICE_SELL.FILE_DATE IS '创建时间';
COMMENT ON COLUMN BS_INVOICE_SELL.AUTHOR_ID IS '创建人ID';
COMMENT ON COLUMN BS_INVOICE_SELL.MODIFIED_DATE IS '最后修改时间';
COMMENT ON COLUMN BS_INVOICE_SELL.MODIFIER_ID IS '最后修改人ID';
ALTER TABLE BS_INVOICE_SELL ADD CONSTRAINT BSFK_INVOICESELL_BUYER FOREIGN KEY (BUYER_ID)
      REFERENCES BS_CARMAN (ID);
ALTER TABLE BS_INVOICE_SELL ADD CONSTRAINT BSFK_INVOICESELL_CAR FOREIGN KEY (CAR_ID)
      REFERENCES BS_CAR (ID);
ALTER TABLE BS_INVOICE_SELL ADD CONSTRAINT BSFK_INVOICESELL_MOTORCADE FOREIGN KEY (MOTORCADE_ID)
      REFERENCES BS_MOTORCADE (ID);
ALTER TABLE BS_INVOICE_SELL ADD CONSTRAINT BSFK_INVOICESELL_CASHIER FOREIGN KEY (CASHIER_ID)
      REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID);
ALTER TABLE BS_INVOICE_SELL ADD CONSTRAINT BSFK_INVOICESELL_AUTHOR FOREIGN KEY (AUTHOR_ID)
      REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID);
ALTER TABLE BS_INVOICE_SELL ADD CONSTRAINT BSFK_INVOICESELL_MODIFIER FOREIGN KEY (MODIFIER_ID)
      REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID);
CREATE INDEX BSIDX_INVOICESELL_COMPANY ON BS_INVOICE_SELL (COMPANY);
CREATE INDEX BSIDX_INVOICESELL_BUYER ON BS_INVOICE_SELL (BUYER_ID);
CREATE INDEX BSIDX_INVOICESELL_CAR ON BS_INVOICE_SELL (CAR_ID);
CREATE INDEX BSIDX_INVOICESELL_MOTORCADE ON BS_INVOICE_SELL (MOTORCADE_ID);

-- 发票销售明细
CREATE TABLE BS_INVOICE_SELL_DETAIL (
   ID                   INTEGER          	NOT NULL,
   SELL_ID              INTEGER          	NOT NULL,
   BUY_ID               INTEGER          	NOT NULL,
   COUNT_               INTEGER          	NOT NULL,
   PRICE                NUMERIC(10,2)    	NOT NULL,
   START_NO             VARCHAR(255) 		NOT NULL,
   END_NO               VARCHAR(255)  		NOT NULL,
   CONSTRAINT BSPK_INVOICE_SELL_DETAIL PRIMARY KEY (ID)
);
COMMENT ON TABLE BS_INVOICE_SELL_DETAIL IS '发票销售明细';
COMMENT ON COLUMN BS_INVOICE_SELL_DETAIL.SELL_ID IS '所属销售单ID';
COMMENT ON COLUMN BS_INVOICE_SELL_DETAIL.BUY_ID IS '对应采购单ID';
COMMENT ON COLUMN BS_INVOICE_SELL_DETAIL.COUNT_ IS '销售数量';
COMMENT ON COLUMN BS_INVOICE_SELL_DETAIL.PRICE IS '销售单价';
COMMENT ON COLUMN BS_INVOICE_SELL_DETAIL.START_NO IS '开始号';
COMMENT ON COLUMN BS_INVOICE_SELL_DETAIL.END_NO IS '结束号';
ALTER TABLE BS_INVOICE_SELL_DETAIL ADD CONSTRAINT BSFK_INVOICESELLDETAIL_BUY FOREIGN KEY (BUY_ID)
      REFERENCES BS_INVOICE_BUY (ID);
ALTER TABLE BS_INVOICE_SELL_DETAIL ADD CONSTRAINT BSFK_INVOICESELLDETAIL_SELL FOREIGN KEY (SELL_ID)
      REFERENCES BS_INVOICE_SELL (ID); 
CREATE INDEX BSIDX_INVOICESELLDETAIL_STARTNO ON BS_INVOICE_SELL_DETAIL (START_NO);
CREATE INDEX BSIDX_INVOICESELLDETAIL_ENDNO ON BS_INVOICE_SELL_DETAIL (END_NO);

-- 发票管理入口
insert into BC_IDENTITY_RESOURCE (ID,STATUS_,INNER_,TYPE_,BELONG,ORDER_,NAME,URL,ICONCLASS) 
	select NEXTVAL('CORE_SEQUENCE'), 0, false, 1, m.id, '031900','发票管理', null, 'i0002' from BC_IDENTITY_RESOURCE m where m.order_='030000';
insert into BC_IDENTITY_RESOURCE (ID,STATUS_,INNER_,TYPE_,BELONG,ORDER_,NAME,URL,ICONCLASS) 
	select NEXTVAL('CORE_SEQUENCE'), 0, false, 2, m.id, '031901','发票采购', '/bc-business/invoice4Buys/paging', 'i0404' from BC_IDENTITY_RESOURCE m where m.order_='031900';
insert into BC_IDENTITY_RESOURCE (ID,STATUS_,INNER_,TYPE_,BELONG,ORDER_,NAME,URL,ICONCLASS) 
	select NEXTVAL('CORE_SEQUENCE'), 0, false, 2, m.id, '031902','发票销售', '/bc-business/invoice4Sells/paging', 'i0800' from BC_IDENTITY_RESOURCE m where m.order_='031900';
insert into BC_IDENTITY_RESOURCE (ID,STATUS_,INNER_,TYPE_,BELONG,ORDER_,NAME,URL,ICONCLASS) 
	select NEXTVAL('CORE_SEQUENCE'), 0, false, 2, m.id, '031903','发票余额表', '/bc-business/invoice4Balance/main', 'i0801' from BC_IDENTITY_RESOURCE m where m.order_='031900';

	
	
-- 插入票务管理角色数据
--  发票管理：BS_INVOICE_MANAGE,对发票所有信息进行无限制的修改。
insert into  BC_IDENTITY_ROLE (ID, STATUS_,INNER_,TYPE_,ORDER_,CODE,NAME) 
	values(NEXTVAL('CORE_SEQUENCE'), 0, false,  0,'0119', 'BS_INVOICE_MANAGE','发票管理');
--  发票查询：BS_INVOICE_READ,对发票所有信息进行查询阅读但不可以执行任何修改操作。
insert into  BC_IDENTITY_ROLE (ID, STATUS_,INNER_,TYPE_,ORDER_,CODE,NAME) 
	values(NEXTVAL('CORE_SEQUENCE'), 0, false,  0,'0120', 'BS_INVOICE_READ','发票查询');
--  发票采购管理：BS_INVOICE4BUY_MANAGE,对发票采购信息进行无限制的修改。
insert into  BC_IDENTITY_ROLE (ID, STATUS_,INNER_,TYPE_,ORDER_,CODE,NAME) 
	values(NEXTVAL('CORE_SEQUENCE'), 0, false,  0,'0121', 'BS_INVOICE4BUY_MANAGE','发票采购管理');
--  发票采购查询：BS_INVOICE4BUY_READ,对发票采购信息只可以查询阅读不可以执行任何修改操作。
insert into  BC_IDENTITY_ROLE (ID, STATUS_,INNER_,TYPE_,ORDER_,CODE,NAME) 
	values(NEXTVAL('CORE_SEQUENCE'), 0, false,  0,'0122', 'BS_INVOICE4BUY_READ','发票采购查询');
--  发票销售管理：BS_INVOICE4SELL_MANAGE,对发票销售信息进行无限制的修改。
insert into  BC_IDENTITY_ROLE (ID, STATUS_,INNER_,TYPE_,ORDER_,CODE,NAME) 
	values(NEXTVAL('CORE_SEQUENCE'), 0, false,  0,'0123', 'BS_INVOICE4SELL_MANAGE','发票销售管理');
--  发票销售查询：BS_INVOICE4SELL_MANAGE,对发票销售信息进行无限制的修改。
insert into  BC_IDENTITY_ROLE (ID, STATUS_,INNER_,TYPE_,ORDER_,CODE,NAME) 
	values(NEXTVAL('CORE_SEQUENCE'), 0, false,  0,'0124', 'BS_INVOICE4SELL_READ','发票销售查询');
--  发票余额表查询：BS_INVOICE4BALANCE_READ,对发票库存信息只可以查询阅读不可以执行任何修改操作。
insert into  BC_IDENTITY_ROLE (ID, STATUS_,INNER_,TYPE_,ORDER_,CODE,NAME) 
	values(NEXTVAL('CORE_SEQUENCE'), 0, false,  0,'0125', 'BS_INVOICE4BALANCE_READ','发票余额表查询');

-- 发票管理
insert into BC_IDENTITY_ROLE_RESOURCE (RID,SID) 
	select r.id,m.id from BC_IDENTITY_ROLE r,BC_IDENTITY_RESOURCE m where r.code='BS_INVOICE_MANAGE' 
	and m.type_ > 1 and m.order_ in ('031901','031902','031903')
	order by m.order_;
insert into BC_IDENTITY_ROLE_RESOURCE (RID,SID) 
	select r.id,m.id from BC_IDENTITY_ROLE r,BC_IDENTITY_RESOURCE m where r.code='BS_INVOICE_READ' 
	and m.type_ > 1 and m.order_ in ('031901','031902','031903')
	order by m.order_;
-- 发票采购
insert into BC_IDENTITY_ROLE_RESOURCE (RID,SID) 
	select r.id,m.id from BC_IDENTITY_ROLE r,BC_IDENTITY_RESOURCE m where r.code='BS_INVOICE4BUY_MANAGE' 
	and m.type_ > 1 and m.order_ in ('031901')
	order by m.order_;
insert into BC_IDENTITY_ROLE_RESOURCE (RID,SID) 
	select r.id,m.id from BC_IDENTITY_ROLE r,BC_IDENTITY_RESOURCE m where r.code='BS_INVOICE4BUY_READ' 
	and m.type_ > 1 and m.order_ in ('031901')
	order by m.order_;
--	发票销售
insert into BC_IDENTITY_ROLE_RESOURCE (RID,SID) 
	select r.id,m.id from BC_IDENTITY_ROLE r,BC_IDENTITY_RESOURCE m where r.code='BS_INVOICE4SELL_MANAGE' 
	and m.type_ > 1 and m.order_ in ('031902')
	order by m.order_;
insert into BC_IDENTITY_ROLE_RESOURCE (RID,SID) 
	select r.id,m.id from BC_IDENTITY_ROLE r,BC_IDENTITY_RESOURCE m where r.code='BS_INVOICE4SELL_READ' 
	and m.type_ > 1 and m.order_ in ('031902')
	order by m.order_;
--  发票余额表
insert into BC_IDENTITY_ROLE_RESOURCE (RID,SID) 
	select r.id,m.id from BC_IDENTITY_ROLE r,BC_IDENTITY_RESOURCE m where r.code='BS_INVOICE4BALANCE_READ' 
	and m.type_ > 1 and m.order_ in ('031903')
	order by m.order_;

--  超级管理员
insert into BC_IDENTITY_ROLE_RESOURCE (RID,SID) 
	select r.id,m.id from BC_IDENTITY_ROLE r,BC_IDENTITY_RESOURCE m where r.code='BC_ADMIN' 
	and m.type_ > 1 and m.order_ in ('031901','031902','031903')
	order by m.order_;

--	普通用户
insert into BC_IDENTITY_ROLE_RESOURCE (RID,SID) 
	select r.id,m.id from BC_IDENTITY_ROLE r,BC_IDENTITY_RESOURCE m where r.code='BC_COMMON' 
	and m.type_ > 1 and m.order_ in ('031902')
	order by m.order_;
	
-- 统计剩余数量函数
CREATE OR REPLACE FUNCTION getbalancecountbyinvoicebuyid(bid integer)
	RETURNS integer AS
$BODY$
DECLARE
	-- 定义变量
	count_ INTEGER;
BEGIN
	select b.count_-sum(d.count_) 
	into count_
	from bs_invoice_buy b
		left join bs_invoice_sell_detail d on d.buy_id=b.id
		left join bs_invoice_sell s on s.id=d.sell_id 
		where b.id=bid and s.status_=0
		group by b.id;
		-- 若为空时，表示还没销售，所以剩余数量应该等于采购数量
		IF count_ is null THEN
			select b.count_ 
			into count_
			from bs_invoice_buy b
				where b.id=bid;
		END IF;
		return count_;
END
$BODY$
  LANGUAGE plpgsql;

-- 统计采购单剩余号码段函数
CREATE OR REPLACE FUNCTION getbalancenumberbyinvoicebuyid(bid integer,startno character varying,endno character varying)
	RETURNS character varying  AS
$BODY$
DECLARE
		-- 定义变量
		-- 临时保存开始和结束号的值
		startno_tmp character varying;
		endno_tmp character varying;
		startno_number_temp1 INTEGER;
		startno_number_temp2 INTEGER;
		-- 剩余号码段
		remainingNumber VARCHAR(4000);
		-- 一行的记录	
		rowinfo RECORD;
BEGIN
	-- 变量赋值
	remainingNumber := '';
	-- 循环销售第一明细时将采购单的开始号 赋值 临时开始号变量；
	startno_tmp := trim(startno);

	-- 查询返回以开始号排序的记录，循环每一行的记录
	FOR rowinfo IN select d.start_no,d.end_no
										from bs_invoice_buy b
										left join bs_invoice_sell_detail d on d.buy_id=b.id
										left join bs_invoice_sell s on s.id=d.sell_id
										where b.id=bid and s.status_=0
										ORDER BY d.start_no
	LOOP
		-- 返回的记录为空则直接返回
		IF rowinfo.start_no IS NULL THEN
			remainingNumber := '['||startno||'~'||endno||']'; 
			RETURN remainingNumber;
		END IF;
		-- 将明细的开始号和临时开始号变量转为数字临时变量
		startno_number_temp1 := convert_stringtonumber(rowinfo.start_no);
		startno_number_temp2 := convert_stringtonumber(startno_tmp);
		-- 明细中的开始号大于临时变量的开始号
		IF startno_number_temp1 > startno_number_temp2 THEN
			-- 若结束号有 0 开始 需要进行补0操作
			endno_tmp := convert_numbertostring(convert_stringtonumber(trim(rowinfo.start_no))-1,startno_tmp);
			remainingNumber := remainingNumber||'['||startno_tmp||'~'||endno_tmp||'] '; 
			-- 临时的开始号转为每条销售明细结束号+1
			startno_tmp := convert_numbertostring(convert_stringtonumber(trim(rowinfo.end_no))+1,trim(rowinfo.end_no));
			endno_tmp:= trim(rowinfo.end_no);
		END IF;
		IF startno_number_temp1=startno_number_temp2	THEN
			startno_tmp := convert_numbertostring(convert_stringtonumber(trim(rowinfo.end_no))+1,trim(rowinfo.end_no));
			endno_tmp:= trim(rowinfo.end_no);
		END IF;
	END LOOP;
	IF remainingNumber = '' THEN
			remainingNumber := '['||startno||'~'||endno||']'; 
			RETURN remainingNumber;
	END IF;
	-- 若循环到明细最后，明细的结束号小于采购单的结束号 则范围[最后一条明细的结束号+1，采购单的结束号]
	IF convert_stringtonumber(endno_tmp)<convert_stringtonumber(trim(endno)) THEN
				startno_tmp= convert_numbertostring(convert_stringtonumber(endno_tmp)+1,endno_tmp);
				endno_tmp=trim(endno);
				remainingNumber := remainingNumber||'['||startno_tmp||'~'||endno_tmp||'] '; 
	END IF;
	
	RETURN remainingNumber;
END;
$BODY$
 LANGUAGE plpgsql;
 
-- 字符串转数字
CREATE OR REPLACE FUNCTION convert_stringtonumber(string_ character varying)
	RETURNS integer  AS
$BODY$
DECLARE
		-- 定义变量
		number_ integer;
		text_expression character varying;
		length_ integer;
		i integer;
BEGIN
	-- 检测字符串的长度
	length_ := char_length(trim(string_));
	text_expression := '';
	FOR i IN 1..length_
	LOOP
	-- 生成匹配的表达式
	text_expression := text_expression||'9';
	END LOOP;
	number_ := to_number(string_,text_expression);
	return number_;
END;
$BODY$
 LANGUAGE plpgsql;


-- 数字转字符串函数
CREATE OR REPLACE FUNCTION convert_numbertostring(int_ integer,text_ character varying)
	RETURNS character varying  AS
$BODY$
DECLARE
		-- 定义变量
		string_ character varying;
		text_expression character varying;
		length_ integer;
		i integer;
BEGIN
	-- 检测字符串的长度
	length_ := char_length(trim(text_));
	text_expression := '';
	FOR i IN 1..length_
	LOOP 
	-- 生成匹配的表达式
	text_expression := text_expression||'0';
	END LOOP;
	string_ := to_char(int_,text_expression);
	RETURN string_;
END;
$BODY$
 LANGUAGE plpgsql;
