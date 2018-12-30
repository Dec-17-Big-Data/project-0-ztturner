create table bank_users
(
	user_id number(10) primary key, 
	username varchar2(50) unique not null,
	password varchar2(50) not null
);

create table bank_Accounts
(
	bank_account_id number(10) primary key,
	account_name varchar2(50) not null,
	balance binary_double default 0,
	user_id number(10) not null
);

alter table bank_Accounts add constraint fk_account_user 
foreign key (user_id) references users (user_id) on delete cascade;