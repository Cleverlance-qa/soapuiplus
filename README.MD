# Release Notes

## v3.8.1 - Sept 23th, 2021
Updates
- added logic to prevent sending app_comp tag if value is emtpy or null to the InfluxDB

## v3.8.0 - Sept 23th, 2021
Updates
- added result field to the metrics for InfluxDB

## v3.7.7 - Sept 3th, 2021
Bugfixes
- xrayTestExecKeyMaster value in DS_envConfig must not be empty, because of DSEnvLooper if tests running from tbd pipeline, added n/a value

## v3.7.6 - Jul 22th, 2021
Updates 
- added feature debugSingleRow (true/false in DS_envConfig) for choice row number from dataSource for testCase

## v3.7.5 - Jul 22th, 2021
Bugfixes 
- fix typo in KafkaProducerAvro.groovy - changed schamaId to schemaId

## v3.7.4 - Jul 8th, 2021
Updates 
- added xrayTestExecKeyMaster - when the xrayTestExecKeyMaster not empty overwrites the xrayTestExecKey

## v3.7.3 - Jun 29th, 2021
Updates 
- kafka avro - schemaId in headers insted of body

## v3.7.2 - May 25th, 2021
Bugfixes
- added EvaluationDB function call into Runner TC

## v3.7.1 - May 25th, 2021
Bugfixes

- update kafkaAvro - added group, schemaId into message, small fixes and cleanup

## v3.7.0 - May 13th, 2021
Updates

- added runner.groovy, update DSLoopers, update ENVLooper

## v3.6.1 - May 11th, 2021
Bugfixes

- fix appComponent attribute in DS_envConfig

## v3.6.0 - May 10th, 2021
Updates:

- added new attribute app_comp to metrics for influxDB

## v3.5.0 - March 26th, 2021
Updates:

- added support for artemis activemq

## v3.4.2 - March 26th, 2021
Bugfixes:

- fix name convention for influxDB attrs

## v3.4.1 - March 25th, 2021
Bugfixes:

- fix KafkaConsumerAvro.groovy - missing body in req/rsp file

## v3.4.0 - March 25th, 2021
Updates:

- added groovy files for kafka Avro	added groovy files for kafka Avro

## v3.3.4 - March 23th, 2021
Bugfixes:

- fix condition for endpoint on STAGE env

## v3.3.3 - March 15th, 2021
Bugfixes:

- fix assert fro reqCorrelationId
- update props paths

## v3.3.2 - March 12th, 2021
Bugfixes:

- fix metric tag host and endpoint for postgreDB

## v3.3.1 - March 9th, 2021
Bugfixes:

- fix stopLoop conndition for tests dataSources

## v3.3.0 - Feb 15th, 2021
Updates:

- added support to reflect pipeline parameters "ENVIRONMENT" and "ENDPOINT"
- afterStepListener optimalization

## v3.2.1 - Nov 13th, 2020
Bugfixes:

- Fix rspStatusCode "null" and rspAssertsStatus = "1" (passed) when timeOut occures. Now will be rspStatusCode "408" for better work in grafana and rspAssertsStatus "-1" (failed) - 1f96c738b7b

## v3.2.0 - Nov 5th, 2020
Update:

-  post2telegraf and post2Xray steps moved to Runner testSuite/testCase 

## v3.1.1 - Oct 27th, 2020
Bugfixes:

- Fix post2telefraf.groovy, post2xray.groovy - condition with status code for loging

## v3.1.0 - Oct 19th, 2020
Updates: 

- Added - conndition for creating rer/rsp when time out occures
- Added - flag (true/false) for wraiting failed only req/rsp to file

Bugfixes: 

- Fix different names of dataSource in XLS looper
- Fix hostname in endpoint evaluation (in req/rsp file)

## v3.0.0 - Oct 1th, 2020
Updates: 

- Refactoring - @Utils relocated outside of the project for better updates
- Added - XLSX support
- Added - Debug true/false
- Added - Xray support
- Added - KafkaConnector, KafkaMessageBuilder, EvaluationKafka, EvaluationTC

## v2.6.1 - Apr 14th, 2020
Updates: 

- Added birthNumberGenerator - 59b066e64e1

## v2.6.0 - Mar 29th, 2020
Bugfixes: 

- Fix evaluation randStr when req/rsp files not created

## v2.6.0 - Mar 29th, 2020
Updates: 

- Update name convention for inlufxDB tags and fields




# Documentation
TBD

# Teams channel
TBD

# Contact/support
martin.zelenka@cleverlance.com