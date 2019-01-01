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

create sequence user_id_sequence
    start with 1
    increment by 1
;

create sequence bank_account_id_sequence
    start with 1
    increment by 1
;

create or replace procedure createUser(new_username in varchar2, user_password in varchar2, new_user_id out number) is  
    begin
        insert into bank_users (user_id, username, password) 
        values (user_id_sequence.nextval, new_username, user_password);
        new_user_id := user_id_sequence.currval;
    end;
/

create or replace procedure updateUserPassword(update_user_id in number, new_password in varchar2, update_success out number) is
    begin
        update bank_users 
        set password = new_password where user_id = update_user_id;
        update_success := 1;
    end;
/

create or replace procedure deleteUser(delete_user_id in number, delete_success out number) is
    begin
        delete from bank_users where user_id = delete_user_id;
        delete_success := 1;
    end;
/

create or replace procedure loginUser(login_username in varchar2, login_password in varchar2, login_user_id out number) is
    user bank_users%rowtype;
    begin
        select * into user from bank_users where username = login_username;
        if user.password = login_password then
            login_user_id := user.user_id;
        else
            login_user_id := 0;
        end if;
    end;
/

create or replace procedure createBankAccount(new_account_name in varchar2, initial_balance in binary_double, account_user_id in number, new_account_id out number) is
    begin
        insert into bank_accounts (bank_account_id, account_name, balance, user_id) 
        values (bank_account_id_sequence.nextval, new_account_name, initial_balance, account_user_id);
        new_account_id := bank_account_id_sequence.currval;
    end;
/

create or replace procedure deleteBankAccount(delete_account_id in number, delete_success out number) is
    amountInAccount binary_double;
    begin
        select balance into amountInAccount 
        from bank_accounts
        where bank_account_id = delete_account_id;
        if amountInAccount > 0 then
            delete_success := 0;
        else
            delete from bank_accounts where bank_account_id = delete_account_id;
            delete_success := 1;
        end if;
    end;
/

create or replace procedure makeDeposit(deposit_account_id in number, deposit_amount in binary_double, deposit_success out number) is
    amountInAccount binary_double;
    begin
        select balance into amountInAccount
        from bank_accounts
        where bank_account_id = deposit_account_id;
        
        amountInAccount := amountInAccount + deposit_amount;
        update bank_accounts set balance = amountInAccount where bank_account_id = deposit_account_id;
        deposit_success := 1;
    end;
/

create or replace procedure makeWithdrawal(withdrawal_account_id in number, withdrawalAmount in binary_double, withdrawal_success out number) is
    amountInAccount binary_double;
    begin
        select balance into amountInAccount
        from bank_accounts
        where bank_account_id = withdrawal_account_id;
        
        if withdrawalAmount > amountInAccount then
            withdrawal_success := 0;
        else
            amountInAccount := amountInAccount - withdrawalAmount;
            update bank_accounts set balance = amountInAccount where bank_account_id = withdrawal_account_id;
            withdrawal_success := 1;
        end if;
    end;
/

--drop tables
--drop table bank_accounts;
--drop table bank_users;