-- bcӪ�˹�����ϵͳ�Ľ����ű�,���б����븽��ǰ׺"BS_"
-- ���д˽ű�֮ǰ��������ƽ̨�Ľ����ű�framework.db.mysql.CREATE.sql

-- ���� 
CREATE TABLE BS_CAR (
    ID number(19) NOT NULL,
    UNIT_ID number(19),
    NAME varchar2(500),
    DESC_ varchar2(4000),
    primary key (ID)
);
COMMENT ON TABLE BS_CAR IS '����';
COMMENT ON COLUMN BS_CAR.NAME IS '����';
COMMENT ON COLUMN BS_CAR.UNIT_ID IS '������λID';
COMMENT ON COLUMN BS_CAR.DESC_ IS '��ע';

-- ������Ϣ
CREATE TABLE BS_MOTORCADE (
    ID number(19) NOT NULL,
    CODE varchar2(100) NOT NULL,
    NAME varchar2(255) NOT NULL,
    FULLNAME varchar2(255) NOT NULL,
    STATUS_ number(1) NOT NULL,
    PAYMENT_DATE date,
    COMPANY varchar2(255) NOT NULL,
    COLOUR varchar2(255) NOT NULL,
    ADDRESS varchar2(255) NOT NULL,
    PRINCIPAL varchar2(255) NOT NULL,
    PHONE varchar2(255) NOT NULL,
    FAX varchar2(255) NOT NULL,
    DESC_ varchar2(4000) ,
    UID_ varchar2(36),
    FILE_DATE date NOT NULL,
    AUTHOR_ID number(19) NOT NULL,
    AUTHOR_NAME varchar2(100) NOT NULL,
    DEPART_ID number(19),
    DEPART_NAME varchar2(255),
    UNIT_ID number(19) NOT NULL,
    UNIT_NAME varchar2(255) NOT NULL,
    MODIFIER_ID number(19) ,
    MODIFIER_NAME varchar2(255) ,
    MODIFIED_DATE date ,
    primary key (ID)
);
COMMENT ON TABLE BS_MOTORCADE IS '������Ϣ';
COMMENT ON COLUMN BS_MOTORCADE.NAME IS '���';
COMMENT ON COLUMN BS_MOTORCADE.CODE IS '����';
COMMENT ON COLUMN BS_MOTORCADE.FULLNAME IS 'ȫ��';
COMMENT ON COLUMN BS_MOTORCADE.STATUS_ IS '״̬��0-�ѽ���,1-������,2-��ɾ��';
COMMENT ON COLUMN BS_MOTORCADE.PAYMENT_DATE IS '�ɷ�����';
COMMENT ON COLUMN BS_MOTORCADE.COMPANY IS '��˾';
COMMENT ON COLUMN BS_MOTORCADE.COLOUR IS '��ɫ';
COMMENT ON COLUMN BS_MOTORCADE.ADDRESS IS '��ַ';
COMMENT ON COLUMN BS_MOTORCADE.PRINCIPAL IS '������';
COMMENT ON COLUMN BS_MOTORCADE.PHONE IS '�绰';
COMMENT ON COLUMN BS_MOTORCADE.FAX IS '����';
COMMENT ON COLUMN BS_MOTORCADE.DESC_ IS '��ע';
COMMENT ON COLUMN BS_MOTORCADE.UID_ IS '���������ı�ʶ��';
COMMENT ON COLUMN BS_MOTORCADE.FILE_DATE IS '����ʱ��';
COMMENT ON COLUMN BS_MOTORCADE.AUTHOR_ID IS '������ID';
COMMENT ON COLUMN BS_MOTORCADE.AUTHOR_NAME IS '����������';
COMMENT ON COLUMN BS_MOTORCADE.DEPART_ID IS '���������ڲ���ID';
COMMENT ON COLUMN BS_MOTORCADE.DEPART_NAME IS '���������ڲ�������';
COMMENT ON COLUMN BS_MOTORCADE.UNIT_ID IS '���������ڵ�λID';
COMMENT ON COLUMN BS_MOTORCADE.UNIT_NAME IS '���������ڵ�λ����';
COMMENT ON COLUMN BS_MOTORCADE.MODIFIER_ID IS '����޸���ID';
COMMENT ON COLUMN BS_MOTORCADE.MODIFIER_NAME IS '����޸�������';
COMMENT ON COLUMN BS_MOTORCADE.MODIFIED_DATE IS '����޸�ʱ��';
ALTER TABLE BS_MOTORCADE ADD CONSTRAINT BS_MOTORCADE_AUTHOR FOREIGN KEY (AUTHOR_ID) 
	REFERENCES BC_IDENTITY_ACTOR (ID);
	
	
-- �鿴��ʷ������
CREATE TABLE BS_HISTORY_CAR_QUANTITY(
    ID number(19) NOT NULL,
    STATUS_ number(1) ,
    MOTORCADE_ID number(19) NOT NULL ,
    MOTORCADE_NAME varchar2(100) NOT NULL ,
    YEAR varchar2(100) NOT NULL ,
    MONTH varchar2(255) NOT NULL ,
    CARQUANTITY number(19) NOT NULL ,
    UID_ varchar2(36),
    FILE_DATE date NOT NULL,
    AUTHOR_ID number(19) NOT NULL,
    AUTHOR_NAME varchar2(100) NOT NULL,
    DEPART_ID number(19),
    DEPART_NAME varchar2(255),
    UNIT_ID number(19) NOT NULL,
    UNIT_NAME varchar2(255) NOT NULL,
    MODIFIER_ID number(19) ,
    MODIFIER_NAME varchar2(255) ,
    MODIFIED_DATE date ,
    primary key (ID)
);
COMMENT ON TABLE BS_HISTORY_CAR_QUANTITY IS '�鿴��ʷ������';
COMMENT ON COLUMN BS_HISTORY_CAR_QUANTITY.MOTORCADE_ID IS '����ID';
COMMENT ON COLUMN BS_HISTORY_CAR_QUANTITY.MOTORCADE_NAME IS '������';
COMMENT ON COLUMN BS_HISTORY_CAR_QUANTITY.YEAR IS '���';
COMMENT ON COLUMN BS_HISTORY_CAR_QUANTITY.MONTH IS '�·�';
COMMENT ON COLUMN BS_HISTORY_CAR_QUANTITY.CARQUANTITY IS '������';
COMMENT ON COLUMN BS_HISTORY_CAR_QUANTITY.STATUS_ IS '״̬��0-�ѽ���,1-������,2-��ɾ��';
COMMENT ON COLUMN BS_HISTORY_CAR_QUANTITY.UID_ IS '���������ı�ʶ��';
COMMENT ON COLUMN BS_HISTORY_CAR_QUANTITY.FILE_DATE IS '����ʱ��';
COMMENT ON COLUMN BS_HISTORY_CAR_QUANTITY.AUTHOR_ID IS '������ID';
COMMENT ON COLUMN BS_HISTORY_CAR_QUANTITY.AUTHOR_NAME IS '����������';
COMMENT ON COLUMN BS_HISTORY_CAR_QUANTITY.DEPART_ID IS '���������ڲ���ID';
COMMENT ON COLUMN BS_HISTORY_CAR_QUANTITY.DEPART_NAME IS '���������ڲ�������';
COMMENT ON COLUMN BS_HISTORY_CAR_QUANTITY.UNIT_ID IS '���������ڵ�λID';
COMMENT ON COLUMN BS_HISTORY_CAR_QUANTITY.UNIT_NAME IS '���������ڵ�λ����';
COMMENT ON COLUMN BS_HISTORY_CAR_QUANTITY.MODIFIER_ID IS '����޸���ID';
COMMENT ON COLUMN BS_HISTORY_CAR_QUANTITY.MODIFIER_NAME IS '����޸�������';
COMMENT ON COLUMN BS_HISTORY_CAR_QUANTITY.MODIFIED_DATE IS '����޸�ʱ��';
ALTER TABLE BS_HISTORY_CAR_QUANTITY ADD CONSTRAINT BS_H_CAR_QUANTITY_MOTORCADE FOREIGN KEY (MOTORCADE_ID) 
	REFERENCES BS_MOTORCADE (ID);
ALTER TABLE BS_HISTORY_CAR_QUANTITY ADD CONSTRAINT BS_H_CAR_QUANTITY_AUTHOR FOREIGN KEY (AUTHOR_ID) 
	REFERENCES BC_IDENTITY_ACTOR (ID);


-- ֤��
create table BS_CERT (
    ID number(19) NOT NULL,
    CODE varchar(255) NOT NULL,
    NAME varchar(255) NOT NULL,
    FULL_NAME varchar(255),
    LICENCER varchar(255),
    START_DATE date,
    END_DATE date,
    primary key (ID)
);
COMMENT ON TABLE BS_CERT IS '֤��';
COMMENT ON COLUMN BS_CERT.CODE IS '֤����';
COMMENT ON COLUMN BS_CERT.NAME IS '֤�����';
COMMENT ON COLUMN BS_CERT.FULL_NAME IS '֤��ȫ��';
COMMENT ON COLUMN BS_CERT.LICENCER IS '��֤����';
COMMENT ON COLUMN BS_CERT.START_DATE IS '��Ч����';
COMMENT ON COLUMN BS_CERT.END_DATE IS '��������';
CREATE INDEX BS_CERT_CODE ON BS_CERT (CODE ASC);
CREATE INDEX BS_CERT_NAME ON BS_CERT (NAME ASC);

-- ֤��:����֤
create table BS_CERT_IDENTITY (
    ID number(19) NOT NULL,
    ADDRESS varchar(500) NOT NULL,
    primary key (ID)
);
COMMENT ON TABLE BS_CERT_IDENTITY IS '֤��:����֤';
COMMENT ON COLUMN BS_CERT_IDENTITY.ADDRESS IS '��ַ';
ALTER TABLE BS_CERT_IDENTITY ADD CONSTRAINT BS_CERT4IDENTITY_CERT FOREIGN KEY (ID) 
	REFERENCES BS_CERT (ID);

-- ֤��:��ʻ֤
create table BS_CERT_DRIVING (
    ID number(19) NOT NULL,
    MODEL varchar(255) NOT NULL,
    primary key (ID)
);
COMMENT ON TABLE BS_CERT_DRIVING IS '֤��:��ʻ֤';
COMMENT ON COLUMN BS_CERT_DRIVING.MODEL IS '׼�ݳ���';
ALTER TABLE BS_CERT_DRIVING ADD CONSTRAINT BS_CERT4DRIVING_CERT FOREIGN KEY (ID) 
	REFERENCES BS_CERT (ID);

-- ˾��������
create table BS_CARMAN (
    ID number(19) NOT NULL,
    UID_ varchar(36) NOT NULL,
    STATUS_ number(1) NOT NULL,
    TYPE_ number(1) default 0 NOT NULL,
    CODE varchar(255) NOT NULL,
    NAME varchar(255) NOT NULL,
    ORDER_ varchar(100),
    SEX number(1) default 0 NOT NULL,
    WORK_DATE date,
    primary key (ID)
);
COMMENT ON TABLE BS_CARMAN IS '˾��������';
COMMENT ON COLUMN BS_CARMAN.UID_ IS 'UID';
COMMENT ON COLUMN BS_CARMAN.STATUS_ IS '״̬��0-�ѽ���,1-������,2-��ɾ��';
COMMENT ON COLUMN BS_CARMAN.TYPE_ IS '���:0-˾��,1-������,2-˾����������';
COMMENT ON COLUMN BS_CARMAN.CODE IS '���';
COMMENT ON COLUMN BS_CARMAN.NAME IS '����';
COMMENT ON COLUMN BS_CARMAN.ORDER_ IS '�����';
COMMENT ON COLUMN BS_CARMAN.SEX IS '�Ա�0-δ����,1-��,2-Ů';
COMMENT ON COLUMN BS_CARMAN.WORK_DATE IS '��ְ����';

-- ˾����������֤���Ĺ���
CREATE TABLE BS_CARMAN_CERT (
    MAN_ID number(19) NOT NULL,
    CERT_ID number(19) NOT NULL,
    PRIMARY KEY (MAN_ID,CERT_ID)
);
COMMENT ON TABLE BS_CARMAN_CERT IS '˾����������֤���Ĺ���';
COMMENT ON COLUMN BS_CARMAN_CERT.MAN_ID IS '˾��������ID';
COMMENT ON COLUMN BS_CARMAN_CERT.CERT_ID IS '֤��ID';
ALTER TABLE BS_CARMAN_CERT ADD CONSTRAINT BSFK_CARMANCERT_MAN FOREIGN KEY (MAN_ID) 
	REFERENCES BS_CARMAN (ID);
ALTER TABLE BS_CARMAN_CERT ADD CONSTRAINT BSFK_CARMANCERT_CERT FOREIGN KEY (CERT_ID) 
	REFERENCES BS_CERT (ID);