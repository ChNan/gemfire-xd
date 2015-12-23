hydra.Prms-testDescription = " tbd ";

INCLUDE $JTESTS/hydraconfig/hydraparams1.inc;
INCLUDE $JTESTS/hydraconfig/topology_hct_2_locator.inc;

hydra.VmPrms-extraClassPaths        += $GEMFIRE/../product-gfxd/lib/gemfirexd.jar;
hydra.VmPrms-extraClassPaths        += $GEMFIRE/../product-gfxd/lib/gemfirexd-client.jar;

hydra.Prms-manageDerbyServer = ${manageDerbyServer};
sql.SQLPrms-testUniqueKeys = ${testUniqueKeys};	

hydra.Prms-totalTaskTimeSec           = 900;
hydra.Prms-maxResultWaitSec           = 600;
hydra.Prms-maxCloseTaskResultWaitSec  = 300;
hydra.Prms-serialExecution            = false;	
hydra.gemfirexd.GfxdHelperPrms-persistDD = fcn "hydra.TestConfigFcns.oneof(\"true\", \"false\")" ncf;

/*
sql.SQLPrms-initCustomersSizePerThread = RANGE 20 50 EGNAR;
sql.SQLPrms-initSecuritiesSizePerThread = RANGE 10 30 EGNAR;                        
*/

sql.SQLPrms-hasNetworth = true;		//will create networth table
sql.SQLPrms-dmlOperations = ONEOF insert update update update query query query delete FOENO;
sql.SQLPrms-dmlTables = "trade.securities" "trade.customers" "trade.networth" "trade.portfolio" "trade.sellorders" "trade.buyorders" "trade.txhistory" "trade.sellordersdup" "trade.companies"; 	
sql.SQLPrms-createSchemas = "create schema trade" "create schema default1" "create schema emp" ;
sql.SQLPrms-dropIndex = ${dropIndex};
sql.SQLPrms-createIndex = true;

THREADGROUP locator
  totalThreads = fcn  ${locatorHosts} * ${locatorVMsPerHost} * ${locatorThreadsPerVM} ncf
  totalVMs     = fcn "(${locatorHosts} * ${locatorVMsPerHost})" ncf
  clientNames  = fcn "hydra.TestConfigFcns.generateNames(\"locator\",
                                ${locatorHosts}, true)" ncf;
  
THREADGROUP connstoreThreads
            totalThreads = fcn "(${${A}bridgeHosts} * ${${A}bridgeVMsPerHost} *  ${${A}bridgeThreadsPerVM}) " ncf
            totalVMs     = fcn "(${${A}bridgeHosts} * ${${A}bridgeVMsPerHost})" ncf
            clientNames  = fcn "hydra.TestConfigFcns.generateNames(\"${A}bridge\",
                                ${${A}bridgeHosts}, true)" ncf; 
                                
THREADGROUP datastoreThreads
            totalThreads = fcn "(${${B}bridgeHosts} * ${${B}bridgeVMsPerHost} *  ${${B}bridgeThreadsPerVM}) " ncf
            totalVMs     = fcn "(${${B}bridgeHosts} * ${${B}bridgeVMsPerHost})" ncf
            clientNames  = fcn "hydra.TestConfigFcns.generateNames(\"${B}bridge\",
                                ${${B}bridgeHosts}, true)" ncf;  

THREADGROUP edgeThreads
            totalThreads = fcn "(${edgeHosts} * ${edgeVMsPerHost} *  ${edgeThreadsPerVM}) -1 " ncf
            totalVMs     = fcn "(${edgeHosts} * ${edgeVMsPerHost})" ncf
            clientNames  = fcn "hydra.TestConfigFcns.generateNames(\"edge\",
                                ${edgeHosts}, true)" ncf;
                                
THREADGROUP ddlThread
            totalThreads = 1
            totalVMs     = 1
            clientNames  = fcn "hydra.TestConfigFcns.generateNames(\"edge\",
                                ${edgeHosts}, true)" ncf;      

INITTASK taskClass = sql.sqlBridge.SQLBridgeTest taskMethod = createLocatorTask
         threadGroups = locator;
         
INITTASK taskClass = sql.sqlBridge.SQLBridgeTest taskMethod = startAndConnectLocatorTask
         threadGroups = locator;
                                
INITTASK     taskClass   = sql.sqlBridge.SQLBridgeTest taskMethod  = HydraTask_initialize
         runMode = always
         threadGroups = connstoreThreads, datastoreThreads;
         
INITTASK     taskClass   = sql.sqlBridge.SQLBridgeTest taskMethod  = HydraTask_initConnBridgePort
         threadGroups = connstoreThreads;         
         
INITTASK     taskClass   = sql.sqlBridge.SQLBridgeTest taskMethod  = HydraTask_createGFXDDB
		runMode = always
		threadGroups =  connstoreThreads, datastoreThreads;     
		
INITTASK     taskClass   = sql.sqlBridge.SQLBridgeTest taskMethod  = HydraTask_createGFXDNetServer
		runMode = always
		threadGroups =  connstoreThreads;    
		
INITTASK     taskClass   = sql.sqlBridge.SQLBridgeTest taskMethod  = HydraTask_initEdges
		threadGroups = edgeThreads, ddlThread;	
        
INITTASK     taskClass   = sql.sqlBridge.SQLBridgeTest taskMethod  = HydraTask_createDiscDB
	threadGroups = edgeThreads;

INITTASK     taskClass   = sql.sqlBridge.SQLBridgeTest taskMethod  = HydraTask_createDiscSchemas
	threadGroups = ddlThread;

INITTASK     taskClass   = sql.sqlBridge.SQLBridgeTest taskMethod  = HydraTask_createUDTPriceType
	threadGroups = ddlThread;	
	
INITTASK     taskClass   = sql.sqlBridge.SQLBridgeTest taskMethod  = HydraTask_createUUIDType
	threadGroups = ddlThread;	
	
INITTASK     taskClass   = sql.sqlBridge.SQLBridgeTest taskMethod  = hydraTask_createUDTPriceFunctions
	threadGroups = ddlThread;	
	
INITTASK     taskClass   = sql.sqlBridge.SQLBridgeTest taskMethod  = HydraTask_createDiscTables
	threadGroups = ddlThread;	
	
INITTASK     taskClass   = sql.sqlBridge.SQLBridgeTest taskMethod  = HydraTask_createGFETablesByClients
		threadGroups = ddlThread;   	

INITTASK     taskClass   = sql.sqlBridge.SQLBridgeTest taskMethod  = HydraTask_createCompaniesTable
	threadGroups = ddlThread;

//set table colomun info for each used by index creation, need only one thread to set in the BB
INITTASK     taskClass   = sql.sqlBridge.SQLBridgeTest taskMethod  = HydraTask_setTableCols
	threadGroups = ddlThread;	
			
INITTASK     taskClass   = sql.sqlBridge.SQLBridgeTest taskMethod  = HydraTask_dropCompaniesFK
	threadGroups = ddlThread;
	
INITTASK     taskClass   = sql.sqlBridge.SQLBridgeTest taskMethod  = HydraTask_populateTables
	threadGroups = edgeThreads;	
	
INITTASK     taskClass   = sql.sqlBridge.SQLBridgeTest taskMethod  = HydraTask_addCompaniesFK
	threadGroups = ddlThread;		

INITTASK     taskClass   = sql.sqlBridge.SQLBridgeTest taskMethod  = HydraTask_createIndexOnCompanies
	threadGroups = ddlThread;

	
TASK     taskClass   = sql.sqlBridge.SQLBridgeTest  taskMethod = HydraTask_createIndex
	threadGroups = edgeThreads, ddlThread;
	
TASK     taskClass   = sql.sqlBridge.SQLBridgeTest taskMethod  = HydraTask_doDMLOp
	 threadGroups = edgeThreads, ddlThread;   

CLOSETASK     taskClass   = sql.sqlBridge.SQLBridgeTest taskMethod  = HydraTask_verifyResultSets
	     threadGroups = ddlThread;
	     	     
//securities has primary key and unique constraints
/* with the cascade delete
sql.SQLPrms-createTablesStatements =
	"create table trade.securities (sec_id int not null, symbol varchar(10) not null, price decimal (30, 20), exchange varchar(10) not null, tid int, constraint sec_pk primary key (sec_id), constraint sec_uq unique (symbol, exchange), constraint exc_ch check (exchange in ('nasdaq', 'nye', 'amex', 'lse', 'fse', 'hkse', 'tse')))"
	"create table trade.customers (cid int not null, cust_name varchar(100), since date, addr varchar(100), tid int, primary key (cid))"
	"create table trade.networth (cid int not null, cash decimal (30, 20), securities decimal (30, 20), loanlimit int, availloan decimal (30, 20),  tid int, constraint netw_pk primary key (cid), constraint cust_newt_fk foreign key (cid) references trade.customers (cid) on delete cascade, constraint cash_ch check (cash>=0), constraint sec_ch check (securities >=0), constraint availloan_ck check (loanlimit>=availloan and availloan >=0))"
	"create table trade.portfolio (cid int not null, sid int not null, qty int not null, availQty int not null, subTotal decimal(30,20), tid int, constraint portf_pk primary key (cid, sid), constraint cust_fk foreign key (cid) references trade.customers (cid) on delete cascade, constraint sec_fk foreign key (sid) references trade.securities (sec_id) on delete cascade, constraint qty_ck check (qty>=0), constraint avail_ch check (availQty>=0 and availQty<=qty))" 
	"create table trade.sellorders (oid int not null constraint orders_pk primary key, cid int, sid int, qty int, ask decimal (30, 20), order_time timestamp, status varchar(10), tid int, constraint portf_fk foreign key (cid, sid) references trade.portfolio (cid, sid) on delete restrict, constraint status_ch check (status in ('cancelled', 'open', 'filled')))"
	"create table trade.buyorders(oid int not null constraint buyorders_pk primary key, cid int, sid int, qty int, bid decimal (30, 20), ordertime timestamp, status varchar(10), tid int, constraint bo_cust_fk foreign key (cid) references trade.customers (cid), constraint bo_sec_fk foreign key (sid) references trade.securities (sec_id) on delete restrict, constraint bo_qty_ck check (qty>=0))"
        "create table trade.txhistory(cid int, oid int, sid int, qty int, price decimal (30, 20), ordertime timestamp, type varchar(10), tid int,  constraint type_ch check (type in ('buy', 'sell')))"
	"create table emp.employees (eid int not null constraint employees_pk primary key, emp_name varchar(100), since date, addr varchar(100), ssn varchar(9))"
	"create table trade.trades (tid int, cid int, eid int, tradedate date, primary Key (tid), foreign key (cid) references trade.customers (cid), constraint emp_fk foreign key (eid) references emp.employees (eid))";
*/

sql.SQLPrms-createTablesStatements =
	"create table trade.securities (sec_id int not null, symbol varchar(10) not null, price decimal (30, 20), exchange varchar(10) not null, tid int, constraint sec_pk primary key (sec_id), constraint sec_uq unique (symbol, exchange), constraint exc_ch check (exchange in ('nasdaq', 'nye', 'amex', 'lse', 'fse', 'hkse', 'tse')))"
	"create table trade.customers (cid int not null, cust_name varchar(100), since date, addr varchar(100), tid int, primary key (cid))"
	"create table trade.networth (cid int not null, cash decimal (30, 20), securities decimal (30, 20), loanlimit int, availloan decimal (30, 20),  tid int, constraint netw_pk primary key (cid), constraint cust_newt_fk foreign key (cid) references trade.customers (cid) on delete restrict, constraint cash_ch check (cash>=0), constraint sec_ch check (securities >=0), constraint availloan_ck check (loanlimit>=availloan and availloan >=0))"
	"create table trade.portfolio (cid int not null, sid int not null, qty int not null, availQty int not null, subTotal decimal(30,20), tid int, constraint portf_pk primary key (cid, sid), constraint cust_fk foreign key (cid) references trade.customers (cid) on delete restrict, constraint sec_fk foreign key (sid) references trade.securities (sec_id) on delete restrict, constraint qty_ck check (qty>=0), constraint avail_ch check (availQty>=0 and availQty<=qty))" 
	"create table trade.sellorders (oid int not null constraint orders_pk primary key, cid int, sid int, qty int, ask decimal (30, 20), order_time timestamp, status varchar(10), tid int, constraint portf_fk foreign key (cid, sid) references trade.portfolio (cid, sid) on delete restrict, constraint status_ch check (status in ('cancelled', 'open', 'filled')))"
	"create table trade.buyorders(oid int not null constraint buyorders_pk primary key, cid int, sid int, qty int, bid decimal (30, 20), ordertime timestamp, status varchar(10), tid int, constraint bo_cust_fk foreign key (cid) references trade.customers (cid), constraint bo_sec_fk foreign key (sid) references trade.securities (sec_id) on delete restrict, constraint bo_qty_ck check (qty>=0))"
        "create table trade.txhistory(cid int, oid int, sid int, qty int, price decimal (30, 20), ordertime timestamp, type varchar(10), tid int,  constraint type_ch check (type in ('buy', 'sell')))"
	"create table emp.employees (eid int not null constraint employees_pk primary key, emp_name varchar(100), since date, addr varchar(100), ssn varchar(9))"
	"create table trade.trades (tid int, cid int, eid int, tradedate date, primary Key (tid), foreign key (cid) references trade.customers (cid), constraint emp_fk foreign key (eid) references emp.employees (eid))"
	"create table trade.sellordersdup (oid int not null constraint ordersdup_pk primary key, cid int, sid int, qty int, ask decimal (30, 20), order_time timestamp, status varchar(10) default 'open', tid int, constraint dup_portf_fk foreign key (cid, sid) references trade.portfolio (cid, sid) on delete restrict, constraint dup_status_ch check (status in ('cancelled', 'open', 'filled')))";
	//add the sub select into 
	
sql.SQLPrms-testPartitionBy = true;

sql.SQLPrms-gfeDDLExtension =
	"trade.securities:random" 
    "trade.customers:random"  
	"trade.networth:random"
	"trade.portfolio:random"
	"trade.sellorders:random"
    "trade.buyorders:random" 
    "trade.txhistory:random"
	"emp.employees:replicate"
	"trade.trades:replicate" 
	"trade.sellordersdup:random"; //add the sub select into 
	
hydra.VmPrms-extraVMArgs  += "-Xmx1024m";
sql.SQLPrms-hasCompanies=true;
sql.SQLPrms-companiesTableDDLExtension = "trade.companies:random";
sql.SQLPrms-numOfWorkers = fcn ${edgeHosts} * ${edgeVMsPerHost} * ${edgeThreadsPerVM} ncf;