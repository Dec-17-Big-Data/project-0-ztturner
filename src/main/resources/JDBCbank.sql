--drop tables
drop table bank_transactions;
drop table bank_accounts;
drop table bank_users;

--drop sequences
drop sequence transaction_id_sequence;
drop sequence user_id_sequence;
drop sequence bank_account_id_sequence;

--create tables
create table bank_users
(
	user_id number(10) primary key, 
	username varchar2(50) unique not null,
	password varchar2(50) not null
);

create table bank_accounts
(
	bank_account_id number(10) primary key,
	account_name varchar2(50) not null,
	balance binary_double default 0,
	user_id number(10) not null
);

create table bank_transactions
(
    transaction_id number(10) primary key,
    transaction_type varchar2(15) not null,
    transaction_amount binary_double not null,
    bank_account_id number(10) not null
);

--create foreign keys
alter table bank_Accounts add constraint fk_account_user 
foreign key (user_id) references bank_users (user_id) on delete cascade;

alter table bank_transactions add constraint fk_transaction_user
foreign key (bank_account_id) references bank_accounts (bank_account_id) on delete cascade;

--create sequences
create sequence user_id_sequence
    start with 1
    increment by 1
;

create sequence bank_account_id_sequence
    start with 1
    increment by 1
;

create sequence transaction_id_sequence
    start with 1
    increment by 1
;

--create procedures
create or replace procedure createUser(new_username in varchar2, user_password in varchar2, new_user_id out number) is
    existing_user_id number;
    begin
        if (new_username is null) or (user_password is null) or (length(user_password) not between 8 and 50) then
            new_user_id := 0;
        else
            select user_id into existing_user_id from bank_users where username = new_username;
            new_user_id := 0;
        end if;
    exception
        when no_data_found then
            insert into bank_users (user_id, username, password) 
            values (user_id_sequence.nextval, new_username, user_password);
            new_user_id := user_id_sequence.currval;
            commit;
    end;
/

create or replace procedure updateUserPassword(update_user_id in number, new_password in varchar2, update_success out number) as
    begin
        if (length(new_password) not between 8 and 50) or (new_password is null) then
            update_success := 0;
        else
            update bank_users 
            set password = new_password where user_id = update_user_id;
            update_success := 1;
        end if;
        commit;
    end;
/

create or replace procedure deleteUser(delete_user_id in number, delete_success out number) as
    user bank_users%rowtype;
    begin
        select * into user from bank_users where user_id = delete_user_id;
        delete from bank_users where user_id = delete_user_id;
        delete_success := 1;
        commit;
    exception
        when no_data_found then
            delete_success := 0;
    end;
/

create or replace procedure loginUser(login_username in varchar2, login_password in varchar2, login_user_id out number) as
    user bank_users%rowtype;
    begin
        select * into user from bank_users where username = login_username;
        if user.password = login_password then
            login_user_id := user.user_id;
        else
            login_user_id := 0;
        end if;
    exception
        when no_data_found then
            login_user_id := 0;
    end;
/

create or replace procedure createBankAccount(new_account_name in varchar2, initial_balance in binary_double, account_user_id in number, new_account_id out number) as
    begin
        if new_account_name is null or initial_balance < 0 then
            new_account_id := 0;
        else
            --insert the bank account
            insert into bank_accounts (bank_account_id, account_name, balance, user_id) 
            values (bank_account_id_sequence.nextval, new_account_name, initial_balance, account_user_id);
            new_account_id := bank_account_id_sequence.currval;
            --insert a deposit transaction for the initial amount
            insert into bank_transactions (transaction_id, transaction_type, transaction_amount, bank_account_id)
            values (transaction_id_sequence.nextval, 'Initial Deposit', initial_balance, new_account_id);
            commit;
        end if;
    end;
/

create or replace procedure deleteBankAccount(delete_account_id in number, delete_success out number) as
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
            commit;
        end if;
    exception
        when no_data_found then
            delete_success := 0;
    end;
/

create or replace procedure makeDeposit(deposit_account_id in number, deposit_amount in binary_double, deposit_success out number) as
    amountInAccount binary_double;
    begin
        select balance into amountInAccount
        from bank_accounts
        where bank_account_id = deposit_account_id;
        
        amountInAccount := amountInAccount + deposit_amount;
        --update the amount in the account
        update bank_accounts set balance = amountInAccount where bank_account_id = deposit_account_id;
        deposit_success := 1;
        --insert a transaction for the deposit
        insert into bank_transactions (transaction_id, transaction_type, transaction_amount, bank_account_id)
        values (transaction_id_sequence.nextval, 'Deposit', deposit_amount, deposit_account_id);
        commit;
    exception
        when no_data_found then
            deposit_success := 0;
    end;
/

create or replace procedure makeWithdrawal(withdrawal_account_id in number, withdrawalAmount in binary_double, withdrawal_success out number) as
    amountInAccount binary_double;
    begin
        select balance into amountInAccount
        from bank_accounts
        where bank_account_id = withdrawal_account_id;
        
        if withdrawalAmount > amountInAccount or withdrawalAmount < 0 then
            withdrawal_success := 0;
        else
            amountInAccount := amountInAccount - withdrawalAmount;
            --update the amount in the account
            update bank_accounts set balance = amountInAccount where bank_account_id = withdrawal_account_id;
            withdrawal_success := 1;
            --insert a transcation for the withdrawal
            insert into bank_transactions (transaction_id, transaction_type, transaction_amount, bank_account_id)
            values (transaction_id_sequence.nextval, 'Withdrawal', withdrawalAmount, withdrawal_account_id);
            commit;
        end if;
    exception
        when no_data_found then
            withdrawal_success := 0;
    end;
/

-- populate tables and test procedures
variable id number

exec createUser('newuser', 'password', :id);
exec createUser('belmontr', 'bloodlines', :id);
exec createUser('anotheruser', 'anotherpassword', :id);
exec createBankAccount('MySavings', 5.00, 1, :id);
exec createBankAccount('Checking', 5.00, 1, :id);
exec createBankAccount('FamilySavings', 1000.50, 2, :id);

variable success number

exec updateUserPassword(2, 'begonemonster', :success);
exec makeDeposit(2, 50.00, :success);
exec makeWithdrawal(2, 50.00, :success);