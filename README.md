# Trade Enricher Application
 
## Installation Instructions
You can import the project as a maven application to your IDE as a maven project.
 
## To run the application
Use one of the several ways of running a Spring Boot application. Below are just three options:

1. Build using maven goal (or by using maven wrapper): `mvn clean package` and execute the resulting artifact as follows `java -jar TradeEnricher-1.0.jar` or
2. On Unix/Linux based systems: run `mvn clean package` then run the resulting jar as any other executable `./TradeEnricher-1.0.jar`
3. Run on IntelliJ after importing - TradeEnricherApplication as main class

## To test the application
1. Http.Post request example:
`curl --request POST --data @trade.csv --header "Content-Type: text/csv" -header "Accept: text/csv" http://localhost:8080/api/v1/enrich

2. Using postman

 
## Information
- Currently running this application with following jvm parameters
   -Xms100m -Xmx100m
   Was able to process 5 million (trades) records successfully within 30 seconds.
   Request (trade.csv) payload size was around 120MB and Response payload size was 200 MB.  
- Capable of processing large data set
- This application processes batch of 10000 records at a time before writing it to response Output Stream asynchronously

## Assumptions
- File header and type is always in valid format

## Limitations
- Currently only caches 100000 Products, after that product details are searched in product.csv file if not available in cache

## Improvements
- As a next improvement would like to split product.csv in multiple files on application load and save on disk
  and then search the product details in an indexed fashion for quick access
- As next step would like to containerize the application, would like to host this on a cloud platform like Openshift with running multiple pods for better throughput. 
- Would like to use reactive framework like WebFlux for asynch communication and Streaming.
- Wish to multi-thread processing within a single batch to utilize the multiple cores  





