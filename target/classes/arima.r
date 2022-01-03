library("forecast")
print(csvFile)
data <- read.csv("C:\\Eclipse\\StockPredictor\\tempData.csv", header = TRUE, sep = ",")

set.seed(141)
mymodel <- auto.arima(ts(data,frequency=365),D=1)

fcData = forecast(mymodel,h=365)
forecasted = as.numeric(fcData$mean)