DROP TABLE IF EXISTS BS_INVOICE_SELL_DETAIL;
DROP TABLE IF EXISTS BS_INVOICE_SELL;
DROP TABLE IF EXISTS BS_INVOICE_BUY;

-- 发票采购单
CREATE TABLE BS_INVOICE_BUY (
   ID                   INTEGER           	NOT NULL,
   STATUS_              NUMERIC(1)          	NOT NULL,
   COMPANY              VARCHAR(255)        	NOT NULL,
   CODE                 VARCHAR(255)        	NOT NULL,
   TYPE_                NUMERIC(1)          	NOT NULL,
   START_NO             VARCHAR(255)        	NOT NULL,
   END_NO               VARCHAR(255)        	NOT NULL,
   CONUT_               INTEGER           	NOT NULL,
   UNIT_                INTEGER            	NOT NULL,
   BUY_PRICE            NUMERIC(10,2)       	NOT NULL,
   SELL_PRICE           NUMERIC(10,2)       	NOT NULL,
   BUYER_ID             INTEGER,
   BUY_DATE             TIMESTAMP           	NOT NULL,
   FILE_DATE            TIMESTAMP           	NOT NULL,
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
COMMENT ON COLUMN BS_INVOICE_BUY.CONUT_ IS '采购数量';
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
   BUYER_ID             INTEGER           	NOT NULL,
   BUYER_NAME           VARCHAR(255)        	NOT NULL,
   CAR_ID               INTEGER          	NOT NULL,
   CAR_PLATE            VARCHAR(255)        	NOT NULL,
   MOTORCADE_ID        	INTEGER           	NOT NULL,
   COMPANY              VARCHAR(255)        	NOT NULL,
   SELL_DATE            TIMESTAMP      		NOT NULL,
   CASHIER_ID           INTEGER           	NOT NULL,
   PAY_TYPE             NUMERIC(1)      	NOT NULL,
   BANK_CODE            VARCHAR(255),
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
   CONUT_               INTEGER          	NOT NULL,
   PRICE                NUMERIC(10,2)    	NOT NULL,
   START_NO             VARCHAR(255) 		NOT NULL,
   END_NO               VARCHAR(255)  		NOT NULL,
   CONSTRAINT BSPK_INVOICE_SELL_DETAIL PRIMARY KEY (ID)
);
COMMENT ON TABLE BS_INVOICE_SELL_DETAIL IS '发票销售明细';
COMMENT ON COLUMN BS_INVOICE_SELL_DETAIL.SELL_ID IS '所属销售单ID';
COMMENT ON COLUMN BS_INVOICE_SELL_DETAIL.BUY_ID IS '对应采购单ID';
COMMENT ON COLUMN BS_INVOICE_SELL_DETAIL.CONUT_ IS '销售数量';
COMMENT ON COLUMN BS_INVOICE_SELL_DETAIL.PRICE IS '销售单价';
COMMENT ON COLUMN BS_INVOICE_SELL_DETAIL.START_NO IS '开始号';
COMMENT ON COLUMN BS_INVOICE_SELL_DETAIL.END_NO IS '结束号';
ALTER TABLE BS_INVOICE_SELL_DETAIL ADD CONSTRAINT BSFK_INVOICESELLDETAIL_BUY FOREIGN KEY (BUY_ID)
      REFERENCES BS_INVOICE_BUY (ID);
ALTER TABLE BS_INVOICE_SELL_DETAIL ADD CONSTRAINT BSFK_INVOICESELLDETAIL_SELL FOREIGN KEY (SELL_ID)
      REFERENCES BS_INVOICE_SELL (ID); 
CREATE INDEX BSIDX_INVOICESELLDETAIL_STARTNO ON BS_INVOICE_SELL_DETAIL (START_NO);
CREATE INDEX BSIDX_INVOICESELLDETAIL_ENDNO ON BS_INVOICE_SELL_DETAIL (END_NO);
