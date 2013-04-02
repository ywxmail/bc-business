-- 2013年1月份出租汽车企业服务质量测评表
INSERT INTO bs_industry_rank(id, year_, month_,company, s1, s2, s3, s4, s5, s6, s7, s8, s9, total, mock, rank, author_id, file_date)
    select NEXTVAL('CORE_SEQUENCE'),2013,1,'天湖旅游',4.29,20.79,17.6,20,5,25,1.5,1,1,96.18,false,1
    ,(SELECT id FROM bc_identity_actor_history where current=true and actor_code='admin'), now()
    from bc_dual where not exists(select 0 from bs_industry_rank where company='天湖旅游' and year_=2013 and month_=1);
INSERT INTO bs_industry_rank(id, year_, month_,company, s1, s2, s3, s4, s5, s6, s7, s8, s9, total, mock, rank, author_id, file_date)
    select NEXTVAL('CORE_SEQUENCE'),2013,1,'花园',4.92,19.92,17.94,20,5,24.69,0.5,0,1,93.97,false,2
    ,(SELECT id FROM bc_identity_actor_history where current=true and actor_code='admin'), now()
    from bc_dual where not exists(select 0 from bs_industry_rank where company='花园' and year_=2013 and month_=1);
INSERT INTO bs_industry_rank(id, year_, month_,company, s1, s2, s3, s4, s5, s6, s7, s8, s9, total, mock, rank, author_id, file_date)
    select NEXTVAL('CORE_SEQUENCE'),2013,1,'交通集团',4.48,19.61,17.45,19.81,5,25,0.5,1,1,93.85,false,3
    ,(SELECT id FROM bc_identity_actor_history where current=true and actor_code='admin'), now()
    from bc_dual where not exists(select 0 from bs_industry_rank where company='交通集团' and year_=2013 and month_=1);
INSERT INTO bs_industry_rank(id, year_, month_,company, s1, s2, s3, s4, s5, s6, s7, s8, s9, total, mock, rank, author_id, file_date)
    select NEXTVAL('CORE_SEQUENCE'),2013,1,'宝城',5,17.78,17.21,19.64,5,24.85,1,1,1,92.48,false,4
    ,(SELECT id FROM bc_identity_actor_history where current=true and actor_code='admin'), now()
    from bc_dual where not exists(select 0 from bs_industry_rank where company='宝城' and year_=2013 and month_=1);
INSERT INTO bs_industry_rank(id, year_, month_,company, s1, s2, s3, s4, s5, s6, s7, s8, s9, total, mock, rank, author_id, file_date)
    select NEXTVAL('CORE_SEQUENCE'),2013,1,'白云集团',4.54,19.05,16.32,19.82,5,24.71,1.5,1,0,91.94,false,5
    ,(SELECT id FROM bc_identity_actor_history where current=true and actor_code='admin'), now()
    from bc_dual where not exists(select 0 from bs_industry_rank where company='白云集团' and year_=2013 and month_=1);
INSERT INTO bs_industry_rank(id, year_, month_,company, s1, s2, s3, s4, s5, s6, s7, s8, s9, total, mock, rank, author_id, file_date)
    select NEXTVAL('CORE_SEQUENCE'),2013,1,'新东方',4.74,17.93,16.36,20,5,24.87,1,1,1,91.9,false,6
    ,(SELECT id FROM bc_identity_actor_history where current=true and actor_code='admin'), now()
    from bc_dual where not exists(select 0 from bs_industry_rank where company='新东方' and year_=2013 and month_=1);
INSERT INTO bs_industry_rank(id, year_, month_,company, s1, s2, s3, s4, s5, s6, s7, s8, s9, total, mock, rank, author_id, file_date)
    select NEXTVAL('CORE_SEQUENCE'),2013,1,'广发',4.75,17.74,16.85,19.55,5,25,1,1,1,91.89,false,7
    ,(SELECT id FROM bc_identity_actor_history where current=true and actor_code='admin'), now()
    from bc_dual where not exists(select 0 from bs_industry_rank where company='广发' and year_=2013 and month_=1);
INSERT INTO bs_industry_rank(id, year_, month_,company, s1, s2, s3, s4, s5, s6, s7, s8, s9, total, mock, rank, author_id, file_date)
    select NEXTVAL('CORE_SEQUENCE'),2013,1,'乐途',5,18.85,17.48,19.45,5,25,0,0,1,91.78,false,8
    ,(SELECT id FROM bc_identity_actor_history where current=true and actor_code='admin'), now()
    from bc_dual where not exists(select 0 from bs_industry_rank where company='乐途' and year_=2013 and month_=1);
INSERT INTO bs_industry_rank(id, year_, month_,company, s1, s2, s3, s4, s5, s6, s7, s8, s9, total, mock, rank, author_id, file_date)
    select NEXTVAL('CORE_SEQUENCE'),2013,1,'省珠航',3.8,21.14,17.28,18.52,5,25,0,0,1,91.74,false,9
    ,(SELECT id FROM bc_identity_actor_history where current=true and actor_code='admin'), now()
    from bc_dual where not exists(select 0 from bs_industry_rank where company='省珠航' and year_=2013 and month_=1);
INSERT INTO bs_industry_rank(id, year_, month_,company, s1, s2, s3, s4, s5, s6, s7, s8, s9, total, mock, rank, author_id, file_date)
    select NEXTVAL('CORE_SEQUENCE'),2013,1,'金路',5,18.42,16.58,20,5,25,0,0,1,91,false,10
    ,(SELECT id FROM bc_identity_actor_history where current=true and actor_code='admin'), now()
    from bc_dual where not exists(select 0 from bs_industry_rank where company='金路' and year_=2013 and month_=1);
INSERT INTO bs_industry_rank(id, year_, month_,company, s1, s2, s3, s4, s5, s6, s7, s8, s9, total, mock, rank, author_id, file_date)
    select NEXTVAL('CORE_SEQUENCE'),2013,1,'龙的',4.34,18.69,16.02,19.76,5,24.73,1.5,0,0,90.04,false,11
    ,(SELECT id FROM bc_identity_actor_history where current=true and actor_code='admin'), now()
    from bc_dual where not exists(select 0 from bs_industry_rank where company='龙的' and year_=2013 and month_=1);
INSERT INTO bs_industry_rank(id, year_, month_,company, s1, s2, s3, s4, s5, s6, s7, s8, s9, total, mock, rank, author_id, file_date)
    select NEXTVAL('CORE_SEQUENCE'),2013,1,'江南',4.2,18.85,17.38,20,5,23.36,0,0,1,89.79,false,12
    ,(SELECT id FROM bc_identity_actor_history where current=true and actor_code='admin'), now()
    from bc_dual where not exists(select 0 from bs_industry_rank where company='江南' and year_=2013 and month_=1);
INSERT INTO bs_industry_rank(id, year_, month_,company, s1, s2, s3, s4, s5, s6, s7, s8, s9, total, mock, rank, author_id, file_date)
    select NEXTVAL('CORE_SEQUENCE'),2013,1,'广骏集团',4.63,15.64,16.81,19.83,5,25,0.5,1,1,89.41,false,13
    ,(SELECT id FROM bc_identity_actor_history where current=true and actor_code='admin'), now()
    from bc_dual where not exists(select 0 from bs_industry_rank where company='广骏集团' and year_=2013 and month_=1);
INSERT INTO bs_industry_rank(id, year_, month_,company, s1, s2, s3, s4, s5, s6, s7, s8, s9, total, mock, rank, author_id, file_date)
    select NEXTVAL('CORE_SEQUENCE'),2013,1,'丽新',3.95,19.59,15.58,20,5,25,0,0,0,89.12,false,14
    ,(SELECT id FROM bc_identity_actor_history where current=true and actor_code='admin'), now()
    from bc_dual where not exists(select 0 from bs_industry_rank where company='丽新' and year_=2013 and month_=1);
INSERT INTO bs_industry_rank(id, year_, month_,company, s1, s2, s3, s4, s5, s6, s7, s8, s9, total, mock, rank, author_id, file_date)
    select NEXTVAL('CORE_SEQUENCE'),2013,1,'德善',3.7,25,14.74,18.2,5,21.99,0,0,0,88.63,false,15
    ,(SELECT id FROM bc_identity_actor_history where current=true and actor_code='admin'), now()
    from bc_dual where not exists(select 0 from bs_industry_rank where company='德善' and year_=2013 and month_=1);
INSERT INTO bs_industry_rank(id, year_, month_,company, s1, s2, s3, s4, s5, s6, s7, s8, s9, total, mock, rank, author_id, file_date)
    select NEXTVAL('CORE_SEQUENCE'),2013,1,'大新',5,16.41,16.48,19.67,5,25,0,0,1,88.56,false,16
    ,(SELECT id FROM bc_identity_actor_history where current=true and actor_code='admin'), now()
    from bc_dual where not exists(select 0 from bs_industry_rank where company='大新' and year_=2013 and month_=1);
INSERT INTO bs_industry_rank(id, year_, month_,company, s1, s2, s3, s4, s5, s6, s7, s8, s9, total, mock, rank, author_id, file_date)
    select NEXTVAL('CORE_SEQUENCE'),2013,1,'八达',5,13.64,17.73,20,5,25,0,0,1,87.37,false,17
    ,(SELECT id FROM bc_identity_actor_history where current=true and actor_code='admin'), now()
    from bc_dual where not exists(select 0 from bs_industry_rank where company='八达' and year_=2013 and month_=1);
INSERT INTO bs_industry_rank(id, year_, month_,company, s1, s2, s3, s4, s5, s6, s7, s8, s9, total, mock, rank, author_id, file_date)
    select NEXTVAL('CORE_SEQUENCE'),2013,1,'天龙',5,17.71,15.5,17,5,25,0,0,0,85.21,false,18
    ,(SELECT id FROM bc_identity_actor_history where current=true and actor_code='admin'), now()
    from bc_dual where not exists(select 0 from bs_industry_rank where company='天龙' and year_=2013 and month_=1);
INSERT INTO bs_industry_rank(id, year_, month_,company, s1, s2, s3, s4, s5, s6, s7, s8, s9, total, mock, rank, author_id, file_date)
    select NEXTVAL('CORE_SEQUENCE'),2013,1,'柏祥',4.08,15.99,15.77,19.13,5,24.52,0,0.2,0,84.69,false,19
    ,(SELECT id FROM bc_identity_actor_history where current=true and actor_code='admin'), now()
    from bc_dual where not exists(select 0 from bs_industry_rank where company='柏祥' and year_=2013 and month_=1);
INSERT INTO bs_industry_rank(id, year_, month_,company, s1, s2, s3, s4, s5, s6, s7, s8, s9, total, mock, rank, author_id, file_date)
    select NEXTVAL('CORE_SEQUENCE'),2013,1,'广达',3.95,12.29,16.44,20,5,25,0,1,1,84.68,false,20
    ,(SELECT id FROM bc_identity_actor_history where current=true and actor_code='admin'), now()
    from bc_dual where not exists(select 0 from bs_industry_rank where company='广达' and year_=2013 and month_=1);
INSERT INTO bs_industry_rank(id, year_, month_,company, s1, s2, s3, s4, s5, s6, s7, s8, s9, total, mock, rank, author_id, file_date)
    select NEXTVAL('CORE_SEQUENCE'),2013,1,'珠江',4.2,13.13,17.38,18.73,5,23.73,0,1,1,84.17,false,21
    ,(SELECT id FROM bc_identity_actor_history where current=true and actor_code='admin'), now()
    from bc_dual where not exists(select 0 from bs_industry_rank where company='珠江' and year_=2013 and month_=1);
INSERT INTO bs_industry_rank(id, year_, month_,company, s1, s2, s3, s4, s5, s6, s7, s8, s9, total, mock, rank, author_id, file_date)
    select NEXTVAL('CORE_SEQUENCE'),2013,1,'安通',4.4,14.04,15.83,19.24,5,24.05,0.5,1,0,84.06,false,22
    ,(SELECT id FROM bc_identity_actor_history where current=true and actor_code='admin'), now()
    from bc_dual where not exists(select 0 from bs_industry_rank where company='安通' and year_=2013 and month_=1);
INSERT INTO bs_industry_rank(id, year_, month_,company, s1, s2, s3, s4, s5, s6, s7, s8, s9, total, mock, rank, author_id, file_date)
    select NEXTVAL('CORE_SEQUENCE'),2013,1,'颖丰',5,13.51,16.09,20,5,23.85,0,0.2,0,83.65,false,23
    ,(SELECT id FROM bc_identity_actor_history where current=true and actor_code='admin'), now()
    from bc_dual where not exists(select 0 from bs_industry_rank where company='颖丰' and year_=2013 and month_=1);
INSERT INTO bs_industry_rank(id, year_, month_,company, s1, s2, s3, s4, s5, s6, s7, s8, s9, total, mock, rank, author_id, file_date)
    select NEXTVAL('CORE_SEQUENCE'),2013,1,'庆星',5,14.16,15.2,19.39,5,24.49,0,0,0,83.24,false,24
    ,(SELECT id FROM bc_identity_actor_history where current=true and actor_code='admin'), now()
    from bc_dual where not exists(select 0 from bs_industry_rank where company='庆星' and year_=2013 and month_=1);
INSERT INTO bs_industry_rank(id, year_, month_,company, s1, s2, s3, s4, s5, s6, s7, s8, s9, total, mock, rank, author_id, file_date)
    select NEXTVAL('CORE_SEQUENCE'),2013,1,'广和',3.95,15,16,18.56,5,23.4,0,0,0,81.91,false,25
    ,(SELECT id FROM bc_identity_actor_history where current=true and actor_code='admin'), now()
    from bc_dual where not exists(select 0 from bs_industry_rank where company='广和' and year_=2013 and month_=1);
INSERT INTO bs_industry_rank(id, year_, month_,company, s1, s2, s3, s4, s5, s6, s7, s8, s9, total, mock, rank, author_id, file_date)
    select NEXTVAL('CORE_SEQUENCE'),2013,1,'海运',5,10.82,15.63,20,5,25,0,0,0,81.45,false,26
    ,(SELECT id FROM bc_identity_actor_history where current=true and actor_code='admin'), now()
    from bc_dual where not exists(select 0 from bs_industry_rank where company='海运' and year_=2013 and month_=1);
INSERT INTO bs_industry_rank(id, year_, month_,company, s1, s2, s3, s4, s5, s6, s7, s8, s9, total, mock, rank, author_id, file_date)
    select NEXTVAL('CORE_SEQUENCE'),2013,1,'联星',5,12.31,15.49,18.65,5,25,0,0,0,81.45,false,27
    ,(SELECT id FROM bc_identity_actor_history where current=true and actor_code='admin'), now()
    from bc_dual where not exists(select 0 from bs_industry_rank where company='联星' and year_=2013 and month_=1);
INSERT INTO bs_industry_rank(id, year_, month_,company, s1, s2, s3, s4, s5, s6, s7, s8, s9, total, mock, rank, author_id, file_date)
    select NEXTVAL('CORE_SEQUENCE'),2013,1,'振中活通',4.15,12.98,14.99,19.33,5,24.11,0.5,0.2,0,81.26,false,28
    ,(SELECT id FROM bc_identity_actor_history where current=true and actor_code='admin'), now()
    from bc_dual where not exists(select 0 from bs_industry_rank where company='振中活通' and year_=2013 and month_=1);
INSERT INTO bs_industry_rank(id, year_, month_,company, s1, s2, s3, s4, s5, s6, s7, s8, s9, total, mock, rank, author_id, file_date)
    select NEXTVAL('CORE_SEQUENCE'),2013,1,'东方',4.9,12.37,13.83,20,5,23.94,0.5,0,0,80.54,false,29
    ,(SELECT id FROM bc_identity_actor_history where current=true and actor_code='admin'), now()
    from bc_dual where not exists(select 0 from bs_industry_rank where company='东方' and year_=2013 and month_=1);
INSERT INTO bs_industry_rank(id, year_, month_,company, s1, s2, s3, s4, s5, s6, s7, s8, s9, total, mock, rank, author_id, file_date)
    select NEXTVAL('CORE_SEQUENCE'),2013,1,'华之源',5,15.5,14.4,17.12,5,23.4,0,0,0,80.42,false,30
    ,(SELECT id FROM bc_identity_actor_history where current=true and actor_code='admin'), now()
    from bc_dual where not exists(select 0 from bs_industry_rank where company='华之源' and year_=2013 and month_=1);
INSERT INTO bs_industry_rank(id, year_, month_,company, s1, s2, s3, s4, s5, s6, s7, s8, s9, total, mock, rank, author_id, file_date)
    select NEXTVAL('CORE_SEQUENCE'),2013,1,'南油',4.2,13.64,17.64,20,5,18.64,0,0,1,80.12,false,31
    ,(SELECT id FROM bc_identity_actor_history where current=true and actor_code='admin'), now()
    from bc_dual where not exists(select 0 from bs_industry_rank where company='南油' and year_=2013 and month_=1);
INSERT INTO bs_industry_rank(id, year_, month_,company, s1, s2, s3, s4, s5, s6, s7, s8, s9, total, mock, rank, author_id, file_date)
    select NEXTVAL('CORE_SEQUENCE'),2013,1,'燕岭',4.2,15.02,16.81,15.83,5,22.22,0,0,1,80.08,false,32
    ,(SELECT id FROM bc_identity_actor_history where current=true and actor_code='admin'), now()
    from bc_dual where not exists(select 0 from bs_industry_rank where company='燕岭' and year_=2013 and month_=1);
INSERT INTO bs_industry_rank(id, year_, month_,company, s1, s2, s3, s4, s5, s6, s7, s8, s9, total, mock, rank, author_id, file_date)
    select NEXTVAL('CORE_SEQUENCE'),2013,1,'利士风',4.54,11.36,16,18.41,5,23.35,0,1,0,79.66,false,33
    ,(SELECT id FROM bc_identity_actor_history where current=true and actor_code='admin'), now()
    from bc_dual where not exists(select 0 from bs_industry_rank where company='利士风' and year_=2013 and month_=1);
INSERT INTO bs_industry_rank(id, year_, month_,company, s1, s2, s3, s4, s5, s6, s7, s8, s9, total, mock, rank, author_id, file_date)
    select NEXTVAL('CORE_SEQUENCE'),2013,1,'协成',4.7,9.38,15,20,5,25,0,0,0,79.08,false,34
    ,(SELECT id FROM bc_identity_actor_history where current=true and actor_code='admin'), now()
    from bc_dual where not exists(select 0 from bs_industry_rank where company='协成' and year_=2013 and month_=1);
INSERT INTO bs_industry_rank(id, year_, month_,company, s1, s2, s3, s4, s5, s6, s7, s8, s9, total, mock, rank, author_id, file_date)
    select NEXTVAL('CORE_SEQUENCE'),2013,1,'新光',5,8.13,15.4,20,5,25,0,0,0,78.53,false,35
    ,(SELECT id FROM bc_identity_actor_history where current=true and actor_code='admin'), now()
    from bc_dual where not exists(select 0 from bs_industry_rank where company='新光' and year_=2013 and month_=1);
INSERT INTO bs_industry_rank(id, year_, month_,company, s1, s2, s3, s4, s5, s6, s7, s8, s9, total, mock, rank, author_id, file_date)
    select NEXTVAL('CORE_SEQUENCE'),2013,1,'新富广',5,11.25,16.8,14,5,25,0,0,1,78.05,false,36
    ,(SELECT id FROM bc_identity_actor_history where current=true and actor_code='admin'), now()
    from bc_dual where not exists(select 0 from bs_industry_rank where company='新富广' and year_=2013 and month_=1);
INSERT INTO bs_industry_rank(id, year_, month_,company, s1, s2, s3, s4, s5, s6, s7, s8, s9, total, mock, rank, author_id, file_date)
    select NEXTVAL('CORE_SEQUENCE'),2013,1,'蚬富',4.48,8.74,15.61,19.77,5,24.24,0,0,0,77.84,false,37
    ,(SELECT id FROM bc_identity_actor_history where current=true and actor_code='admin'), now()
    from bc_dual where not exists(select 0 from bs_industry_rank where company='蚬富' and year_=2013 and month_=1);
INSERT INTO bs_industry_rank(id, year_, month_,company, s1, s2, s3, s4, s5, s6, s7, s8, s9, total, mock, rank, author_id, file_date)
    select NEXTVAL('CORE_SEQUENCE'),2013,1,'海发',5,7.95,17.45,20,5,21.36,0,0,1,77.76,false,38
    ,(SELECT id FROM bc_identity_actor_history where current=true and actor_code='admin'), now()
    from bc_dual where not exists(select 0 from bs_industry_rank where company='海发' and year_=2013 and month_=1);
INSERT INTO bs_industry_rank(id, year_, month_,company, s1, s2, s3, s4, s5, s6, s7, s8, s9, total, mock, rank, author_id, file_date)
    select NEXTVAL('CORE_SEQUENCE'),2013,1,'广物汽贸',4.6,12.88,15.2,15.41,5,24.49,0,0,0,77.58,false,39
    ,(SELECT id FROM bc_identity_actor_history where current=true and actor_code='admin'), now()
    from bc_dual where not exists(select 0 from bs_industry_rank where company='广物汽贸' and year_=2013 and month_=1);
INSERT INTO bs_industry_rank(id, year_, month_,company, s1, s2, s3, s4, s5, s6, s7, s8, s9, total, mock, rank, author_id, file_date)
    select NEXTVAL('CORE_SEQUENCE'),2013,1,'新羊',5,8.98,14.17,20,5,23.49,0.5,0,0,77.14,false,40
    ,(SELECT id FROM bc_identity_actor_history where current=true and actor_code='admin'), now()
    from bc_dual where not exists(select 0 from bs_industry_rank where company='新羊' and year_=2013 and month_=1);
INSERT INTO bs_industry_rank(id, year_, month_,company, s1, s2, s3, s4, s5, s6, s7, s8, s9, total, mock, rank, author_id, file_date)
    select NEXTVAL('CORE_SEQUENCE'),2013,1,'国旅',3.7,12.31,16.69,16.39,5,21.99,0,0,1,77.08,false,41
    ,(SELECT id FROM bc_identity_actor_history where current=true and actor_code='admin'), now()
    from bc_dual where not exists(select 0 from bs_industry_rank where company='国旅' and year_=2013 and month_=1);
INSERT INTO bs_industry_rank(id, year_, month_,company, s1, s2, s3, s4, s5, s6, s7, s8, s9, total, mock, rank, author_id, file_date)
    select NEXTVAL('CORE_SEQUENCE'),2013,1,'新开达',3.95,15.78,13.11,17.05,5,21.72,0,0,0,76.61,false,42
    ,(SELECT id FROM bc_identity_actor_history where current=true and actor_code='admin'), now()
    from bc_dual where not exists(select 0 from bs_industry_rank where company='新开达' and year_=2013 and month_=1);
INSERT INTO bs_industry_rank(id, year_, month_,company, s1, s2, s3, s4, s5, s6, s7, s8, s9, total, mock, rank, author_id, file_date)
    select NEXTVAL('CORE_SEQUENCE'),2013,1,'建兴',4.2,13.99,13.68,17.82,5,21.89,0,0,0,76.58,false,43
    ,(SELECT id FROM bc_identity_actor_history where current=true and actor_code='admin'), now()
    from bc_dual where not exists(select 0 from bs_industry_rank where company='建兴' and year_=2013 and month_=1);
INSERT INTO bs_industry_rank(id, year_, month_,company, s1, s2, s3, s4, s5, s6, s7, s8, s9, total, mock, rank, author_id, file_date)
    select NEXTVAL('CORE_SEQUENCE'),2013,1,'云通',4.6,6.71,14.63,20,5,25,0,0.4,0,76.34,false,44
    ,(SELECT id FROM bc_identity_actor_history where current=true and actor_code='admin'), now()
    from bc_dual where not exists(select 0 from bs_industry_rank where company='云通' and year_=2013 and month_=1);
INSERT INTO bs_industry_rank(id, year_, month_,company, s1, s2, s3, s4, s5, s6, s7, s8, s9, total, mock, rank, author_id, file_date)
    select NEXTVAL('CORE_SEQUENCE'),2013,1,'天成',5,6.25,15.6,20,5,23,0,0,0,74.85,false,45
    ,(SELECT id FROM bc_identity_actor_history where current=true and actor_code='admin'), now()
    from bc_dual where not exists(select 0 from bs_industry_rank where company='天成' and year_=2013 and month_=1);
INSERT INTO bs_industry_rank(id, year_, month_,company, s1, s2, s3, s4, s5, s6, s7, s8, s9, total, mock, rank, author_id, file_date)
    select NEXTVAL('CORE_SEQUENCE'),2013,1,'强龙',3.45,7.06,13.52,20,5,24.07,0,0,0,73.1,false,46
    ,(SELECT id FROM bc_identity_actor_history where current=true and actor_code='admin'), now()
    from bc_dual where not exists(select 0 from bs_industry_rank where company='强龙' and year_=2013 and month_=1);
INSERT INTO bs_industry_rank(id, year_, month_,company, s1, s2, s3, s4, s5, s6, s7, s8, s9, total, mock, rank, author_id, file_date)
    select NEXTVAL('CORE_SEQUENCE'),2013,1,'远洋',5,5.36,17.14,17.43,5,22.14,0,0,1,73.07,false,47
    ,(SELECT id FROM bc_identity_actor_history where current=true and actor_code='admin'), now()
    from bc_dual where not exists(select 0 from bs_industry_rank where company='远洋' and year_=2013 and month_=1);
INSERT INTO bs_industry_rank(id, year_, month_,company, s1, s2, s3, s4, s5, s6, s7, s8, s9, total, mock, rank, author_id, file_date)
    select NEXTVAL('CORE_SEQUENCE'),2013,1,'黄企',4.5,0,17.27,20,5,25,0,0,1,72.77,false,48
    ,(SELECT id FROM bc_identity_actor_history where current=true and actor_code='admin'), now()
    from bc_dual where not exists(select 0 from bs_industry_rank where company='黄企' and year_=2013 and month_=1);
INSERT INTO bs_industry_rank(id, year_, month_,company, s1, s2, s3, s4, s5, s6, s7, s8, s9, total, mock, rank, author_id, file_date)
    select NEXTVAL('CORE_SEQUENCE'),2013,1,'巨星',3.95,0,16.39,20,5,24.35,0,0,1,70.69,false,49
    ,(SELECT id FROM bc_identity_actor_history where current=true and actor_code='admin'), now()
    from bc_dual where not exists(select 0 from bs_industry_rank where company='巨星' and year_=2013 and month_=1);
INSERT INTO bs_industry_rank(id, year_, month_,company, s1, s2, s3, s4, s5, s6, s7, s8, s9, total, mock, rank, author_id, file_date)
    select NEXTVAL('CORE_SEQUENCE'),2013,1,'东运达',5,6,12.55,17.65,5,24.02,0,0,0,70.22,false,50
    ,(SELECT id FROM bc_identity_actor_history where current=true and actor_code='admin'), now()
    from bc_dual where not exists(select 0 from bs_industry_rank where company='东运达' and year_=2013 and month_=1);
INSERT INTO bs_industry_rank(id, year_, month_,company, s1, s2, s3, s4, s5, s6, s7, s8, s9, total, mock, rank, author_id, file_date)
    select NEXTVAL('CORE_SEQUENCE'),2013,1,'万润',5,0,15.5,20,5,22.5,0,0.2,0,68.2,false,51
    ,(SELECT id FROM bc_identity_actor_history where current=true and actor_code='admin'), now()
    from bc_dual where not exists(select 0 from bs_industry_rank where company='万润' and year_=2013 and month_=1);
INSERT INTO bs_industry_rank(id, year_, month_,company, s1, s2, s3, s4, s5, s6, s7, s8, s9, total, mock, rank, author_id, file_date)
    select NEXTVAL('CORE_SEQUENCE'),2013,1,'天东',5,0,14.67,20,5,23.33,0,0,0,68,false,52
    ,(SELECT id FROM bc_identity_actor_history where current=true and actor_code='admin'), now()
    from bc_dual where not exists(select 0 from bs_industry_rank where company='天东' and year_=2013 and month_=1);
INSERT INTO bs_industry_rank(id, year_, month_,company, s1, s2, s3, s4, s5, s6, s7, s8, s9, total, mock, rank, author_id, file_date)
    select NEXTVAL('CORE_SEQUENCE'),2013,1,'东泰',4.5,0,14.84,20,5,23.39,0,0,0,67.73,false,53
    ,(SELECT id FROM bc_identity_actor_history where current=true and actor_code='admin'), now()
    from bc_dual where not exists(select 0 from bs_industry_rank where company='东泰' and year_=2013 and month_=1);
INSERT INTO bs_industry_rank(id, year_, month_,company, s1, s2, s3, s4, s5, s6, s7, s8, s9, total, mock, rank, author_id, file_date)
    select NEXTVAL('CORE_SEQUENCE'),2013,1,'恒锦',5,0,17.54,14.74,5,23.25,0,0,1,66.53,false,54
    ,(SELECT id FROM bc_identity_actor_history where current=true and actor_code='admin'), now()
    from bc_dual where not exists(select 0 from bs_industry_rank where company='恒锦' and year_=2013 and month_=1);
INSERT INTO bs_industry_rank(id, year_, month_,company, s1, s2, s3, s4, s5, s6, s7, s8, s9, total, mock, rank, author_id, file_date)
    select NEXTVAL('CORE_SEQUENCE'),2013,1,'金士利',5,0,14.85,16.36,5,25,0,0,0,66.21,false,55
    ,(SELECT id FROM bc_identity_actor_history where current=true and actor_code='admin'), now()
    from bc_dual where not exists(select 0 from bs_industry_rank where company='金士利' and year_=2013 and month_=1);
INSERT INTO bs_industry_rank(id, year_, month_,company, s1, s2, s3, s4, s5, s6, s7, s8, s9, total, mock, rank, author_id, file_date)
    select NEXTVAL('CORE_SEQUENCE'),2013,1,'合兴',3.95,0,15.66,17.83,5,22.59,0,0.2,0,65.23,false,56
    ,(SELECT id FROM bc_identity_actor_history where current=true and actor_code='admin'), now()
    from bc_dual where not exists(select 0 from bs_industry_rank where company='合兴' and year_=2013 and month_=1);
INSERT INTO bs_industry_rank(id, year_, month_,company, s1, s2, s3, s4, s5, s6, s7, s8, s9, total, mock, rank, author_id, file_date)
    select NEXTVAL('CORE_SEQUENCE'),2013,1,'惠宝',5,0,16.47,20,5,14.71,0,0,1,62.18,false,57
    ,(SELECT id FROM bc_identity_actor_history where current=true and actor_code='admin'), now()
    from bc_dual where not exists(select 0 from bs_industry_rank where company='惠宝' and year_=2013 and month_=1);
INSERT INTO bs_industry_rank(id, year_, month_,company, s1, s2, s3, s4, s5, s6, s7, s8, s9, total, mock, rank, author_id, file_date)
    select NEXTVAL('CORE_SEQUENCE'),2013,1,'荣泰',4.25,0,0,18.15,5,24.38,0,0.4,0,52.18,false,58
    ,(SELECT id FROM bc_identity_actor_history where current=true and actor_code='admin'), now()
    from bc_dual where not exists(select 0 from bs_industry_rank where company='荣泰' and year_=2013 and month_=1);
INSERT INTO bs_industry_rank(id, year_, month_,company, s1, s2, s3, s4, s5, s6, s7, s8, s9, total, mock, rank, author_id, file_date)
    select NEXTVAL('CORE_SEQUENCE'),2013,1,'小蚂蚁',5,0,15.76,0,5,21.97,0,0,0,47.73,false,59
    ,(SELECT id FROM bc_identity_actor_history where current=true and actor_code='admin'), now()
    from bc_dual where not exists(select 0 from bs_industry_rank where company='小蚂蚁' and year_=2013 and month_=1);

select * from bs_industry_rank order by year_,month_,rank;