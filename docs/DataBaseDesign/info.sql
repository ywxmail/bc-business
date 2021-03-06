DROP TABLE IF EXISTS BS_INFO;

-- 信息：包含公司文件、通知、法规文件
CREATE TABLE BS_INFO(
	ID INT NOT NULL,
	UID_ VARCHAR(32) DEFAULT '0' NOT NULL,
	TYPE_ INT NOT NULL,
	STATUS_ INT DEFAULT 0 NOT NULL,
	SUBJECT VARCHAR(500) NOT NULL,
	CONTENT TEXT,
	SOURCE_ VARCHAR(255),
	SEND_DATE TIMESTAMP NOT NULL,
	END_DATE TIMESTAMP,
	AUTHOR_ID INT NOT NULL,
	FILE_DATE TIMESTAMP NOT NULL,
	MODIFIER_ID INT,
	MODIFIED_DATE TIMESTAMP,
	CONSTRAINT BSPK_INFO PRIMARY KEY (ID)
);
COMMENT ON TABLE BS_INFO IS '信息';
COMMENT ON COLUMN BS_INFO.ID IS 'ID';
COMMENT ON COLUMN BS_INFO.UID_ IS 'UID';
COMMENT ON COLUMN BS_INFO.TYPE_ IS '类型 : 0-公告,1-通知,2-邮件';
COMMENT ON COLUMN BS_INFO.STATUS_ IS '状态 : 0-草稿,1-正常,2-已归档';
COMMENT ON COLUMN BS_INFO.SUBJECT IS '标题';
COMMENT ON COLUMN BS_INFO.CONTENT IS '内容';
COMMENT ON COLUMN BS_INFO.SOURCE_ IS '来源';
COMMENT ON COLUMN BS_INFO.SEND_DATE IS '发送日期 : 对于提醒信息与创建时间相等';
COMMENT ON COLUMN BS_INFO.END_DATE IS '结束日期';
COMMENT ON COLUMN BS_INFO.AUTHOR_ID IS '创建人ID';
COMMENT ON COLUMN BS_INFO.FILE_DATE IS '创建时间';
COMMENT ON COLUMN BS_INFO.MODIFIER_ID IS '最后修改人ID';
COMMENT ON COLUMN BS_INFO.MODIFIED_DATE IS '最后修改时间';
ALTER TABLE BS_INFO ADD CONSTRAINT BCFK_INFO_MODIFIER FOREIGN KEY (MODIFIER_ID)
	REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID) ON UPDATE RESTRICT ON DELETE RESTRICT;
ALTER TABLE BS_INFO	ADD CONSTRAINT BCFK_INFO_AUTHOR FOREIGN KEY (AUTHOR_ID)
	REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID) ON UPDATE RESTRICT ON DELETE RESTRICT;
