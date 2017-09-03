create table log_entity_update
(
	id int not null primary key auto_increment,
	entity_name varchar(255) not null,
	entity_content text not null,
	sql_type char(6) not null,
	sql_content varchar(2048) not null,
	oper_account varchar(64),
	oper_time timestamp default CURRENT_TIMESTAMP
);

create table demo_account
(
  usercode VARCHAR(32) not null,
  username VARCHAR(32)
);