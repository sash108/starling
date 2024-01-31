# Starling Round Up Service 

### Assumption
1. I used FeedItem.amount instead of FeedItem.sourceAmount for round-up calculations, as the source amount seemed to represent the initial amount. 
2. I focused on FeedItem.OUT transactions, considering only spending transactions where money is paid to someone. I filtered transactions with specific accepted statuses (SETTLED, UPCOMING, PENDING) to include only settled or future transactions. 
3. I employed an access token in the REST API for performing operations on behalf of a customer. This allows the service to work on any customer's behalf, given the appropriate customer token. 
4. Authentication of service itself has not been implemented for simplicity. The API is public and accessible to everyone. 
5. StartingTime in the JSON payload is mandatory; otherwise, an error will occur. EndingTime is optional. If EndingTime is not provided, I consider it as 7 days added to the StartingTime. 
6. If a SavingGoal does not exist, which is expected for a new user, I will first check and then create a new SavingGoal with the name nomenclature "saving_goal_" + startingTime.

### Improvement
1. Adding security to the REST application, even authentication file-based without a database, will be sufficient for the first draft, ensuring the app is not visible to the outside world. 
2. Adding a database to record details, such as which customer initiated the analysis, when it was done, and how much amount was requested to Starling Bank to move etc. 
3. Including Swagger definitions for REST endpoints, even if there's only one in the application. 
4. Considering metrics for the application, such as the number of requests served per minute and the time it took for processing etc.
5. Certainly, with the introduction of a database, achieving idempotency for requests becomes feasible. This way, there's no need to repeatedly process the same request if the content remains identical. This helps optimize resource usage and enhances the overall efficiency of the application. 
6. For heavy load, we may need to make it asynchronous behind the scenes. Since in case of this app, it is not really necessary to be performed in real-time, we can send a 202 HTTP status to the customer and then do all processing asynchronously through any messaging queuing system, such as Kafka, for example. 

### How to run and test application
#### Run Program
##### Prerequisite
1. Apache Maven 3+ version is required.
2. Docker

Run below instructions:

```
cd starling-round-up-saving
mvn clean package           
docker image build -t starling-round-up-saving:latest .
docker run -p 8080:8080 starling-round-up-saving:latest
```

#### Test application
After running the application, it will open a port on 8080 for interaction. The following curl command can be used to run a working example. Please note that it may not work when you check it, as you'll need to provide a fresh token and reasonable startingTime and endingTime where you may have some transactions.
```
curl --request PUT \
  --url 'http://localhost:8080/api/v1/roundup/saving/transfer-money?=' \
  --header 'Content-Type: application/json' \
  --data '{
	 "customerToken": "eyJhbGciOiJQUzI1NiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAA_21T246jMAz9lRXP4xGXhNvbvu0P7AeYxLTRQIKS0NnRaP59A4FSqj5U4pxjH9ux-50o55I2wUmBpNG8O492UPrSof54F2ZM3hI3dyGC8qyuOpTASkJgVc2hbjIEUbA8lz1jRV6FYPo3JW1WpWXF0qYp3xKFPhIlZ3whUAgza__HDJLsXyU3by4pha6qOmAMBXQFcShYWTNe51XOZPD25oN0zEhLXhRFTsB4xoEVKQcsRAkdw64R4cdzETLCWL-FIOeOOjmlDTRVEWbo0gzqrC5Dfk88TbO-yotlYGEmWh4ldgrXtVXQOFJrCeWvJ8F_TU-CkqS96hXZMz8o50_MBqS0ocmWpPJ3EBXvUVxHukfO_mqscmFDoLRUNyVnHKLW4YBabJ0ItBKE0d6aIfouzKYZ3Ss7oldGg-mhn7Xc6onZeTPubdOIasseUUv01EoayNMdrmEjeQwIWxHgIu54zZzwi2iXIthMIjiCQI142TyjdnyCt6gdiqXnOw2DEWH6wzsSYJZneGa3LGt6NeylYu0TtUZZEqQmfwLuLH1aFdwd3sIqHFzM0ceJ20Y9cavPIxOH68Ozv7A4xBdehxhNxZXkPJCEMPZxNY68DwPO0wYn3M8k_N3DFYVjMlY-lD-ze90z-yIfzKe-856WBkC42zM1yT5SjztdV_G85OTnP3hvLZmhBAAA.oCeXeicGI-gBeEe___3BuuTZmNAaFxBO5YWP6fbwwRz2KRIszXsc-QrT8oeNRXKli1gY_5gVdnHabl_TL8GsI8ghy5AcvYE8GOET7CxEsAEC-Pr7XOV67cKpKb6ru9sK47YaVZOdOlCSQBDKEQj7NhL5uaXCYmYpJiNRKreWQRp6DCuMLaZsR63O1A1eeE3sguaa-GdsrO-pdmxhmnN2nVQ30ZuSS_zbXx0-Lb9Hhg2ESz2amxopEtvJk3jVUIe2mhbClphZ_CHP0AAco0_6paFv9vjRxXLaoCjoVUW7z3vebMEnP_eNN2vVppPFDrV8GSm9lvz7eFagjuJaM2xQQ9D6lbqgZwXLEDIeeo93KcEbTvQ_JjQPHxOsXRU5VcayDLJnVW3jOgcynUvR1ORdGNyre3npqqHl8CprC74T5bw5hHKzLezj8WX2b_LbXpIHtJqjO8GCYXngzzVOm74PVRBFBmv09bW9HV8qOsrLtL8KuZroUHToqu2I33S1l-DTQF3lxzMs0O1QnvhyOnsyJyz0QtQgyxTf9rheib7veslMRYrtZh8ePcTQZ-Ba7bkaJvXEMrPCc9NshWrYdixfxzeRDnWqkUTCso8dqe71CFWmxQIYQf6bkD58HICJ08OtVAriseo3YoXApy138v4iJCo4PhRnAXnPWxBIG0EEMZE",
	"startingTime":"2024-01-24T00:00:00.000Z",
	"endingTime":"2024-01-31T00:00:00.000Z"
}'
```
The response with these fields must be returned and the values could be changed since it gave result in my run.
```
[
	{
		"accountUid": "e2181806-1baf-44a4-8098-1b4f23badad8",
		"accountCurrency": "GBP",
		"categoryUid": "e218f8c9-1fbe-424d-9d52-ff9bd029ccd9",
		"savingUid": "06eccfba-ba8d-4a23-8788-3f06a8a3c335",
		"transferUid": "1e2ca693-534f-493b-8b1c-ce82ebf6fec1",
		"transferredAmount": {
			"currency": "GBP",
			"minorUnits": 830
		},
		"success": true
	}
]

```
