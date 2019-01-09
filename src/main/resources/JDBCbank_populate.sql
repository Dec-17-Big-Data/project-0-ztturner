-- populate tables and test procedures
variable id number

exec createUser('newuser', 'password', :id);
exec createUser('belmonts', 'vampirekiller', :id);
exec createUser('belmontr', 'divinebloodlines', :id);
exec createUser('tepesaf', 'crissaegrim', :id);
exec createUser('belmontj', 'heartoffire', :id);
exec createBankAccount('Savings', 10.00, 1, :id);
exec createBankAccount('Checking', 5.00, 1, :id);
exec createBankAccount('Savings', 250.00, 2, :id);
exec createBankAccount('Checking', 50.00, 2, :id);
exec createBankAccount('Savings', 555.55, 3, :id);
exec createBankAccount('Checking', 35.00, 3, :id);
exec createBankAccount('Savings', 1000.00, 4, :id);
exec createBankAccount('Checking', 50.00, 4, :id);
exec createBankAccount('Savings', 100.00, 5, :id);
exec createBankAccount('Checking', 10.00, 5, :id);

variable success number

exec makeDeposit(1, 50.00, :success);
exec makeDeposit(2, 50.00, :success);
exec makeWithdrawal(1, 50.00, :success);
exec makeWithdrawal(2, 50.00, :success);