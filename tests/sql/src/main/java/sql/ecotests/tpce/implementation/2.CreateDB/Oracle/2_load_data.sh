#!/bin/sh
USERID=tpcegfxd/tpcegfxd
sqlldr $USERID control=ctlfiles/AccountPermission.ctl
sqlldr $USERID control=ctlfiles/Customer.ctl
sqlldr $USERID control=ctlfiles/CustomerAccount.ctl
sqlldr $USERID control=ctlfiles/CustomerTaxrate.ctl
sqlldr $USERID control=ctlfiles/Holding.ctl
sqlldr $USERID control=ctlfiles/HoldingHistory.ctl
sqlldr $USERID control=ctlfiles/HoldingSummary.ctl
sqlldr $USERID control=ctlfiles/WatchItem.ctl
sqlldr $USERID control=ctlfiles/WatchList.ctl
sqlldr $USERID control=ctlfiles/Broker.ctl
sqlldr $USERID control=ctlfiles/CashTransaction.ctl
sqlldr $USERID control=ctlfiles/Charge.ctl
sqlldr $USERID control=ctlfiles/CommissionRate.ctl
sqlldr $USERID control=ctlfiles/Settlement.ctl
sqlldr $USERID control=ctlfiles/Trade.ctl
sqlldr $USERID control=ctlfiles/TradeHistory.ctl
#sqlldr $USERID control=ctlfiles/TradeRequest.ctl
sqlldr $USERID control=ctlfiles/TradeType.ctl
sqlldr $USERID control=ctlfiles/Company.ctl
sqlldr $USERID control=ctlfiles/CompanyCompetitor.ctl
sqlldr $USERID control=ctlfiles/DailyMarket.ctl
sqlldr $USERID control=ctlfiles/Exchange.ctl
sqlldr $USERID control=ctlfiles/Financial.ctl
sqlldr $USERID control=ctlfiles/Industry.ctl
sqlldr $USERID control=ctlfiles/LastTrade.ctl
sqlldr $USERID control=ctlfiles/NewsItem.ctl
sqlldr $USERID control=ctlfiles/NewsXRef.ctl
sqlldr $USERID control=ctlfiles/Sector.ctl
sqlldr $USERID control=ctlfiles/Security.ctl
sqlldr $USERID control=ctlfiles/Address.ctl
sqlldr $USERID control=ctlfiles/StatusType.ctl
sqlldr $USERID control=ctlfiles/Taxrate.ctl
sqlldr $USERID control=ctlfiles/ZipCode.ctl
