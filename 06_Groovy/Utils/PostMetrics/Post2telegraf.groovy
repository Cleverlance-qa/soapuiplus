//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//>                 					Post2telegraf
//>                 					version 3.0.0
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

def debug_error = context.expand('${#Project#debug_error}');

log.info "Running Post2telegraf";

testRunner.testCase.testSuite.project.testSuites["Runner"].testCases["Runner"].testSteps["Post2telegraf"].run(testRunner, context);

def responseCode = testRunner.testCase.testSuite.project.testSuites["Runner"].testCases["Runner"].testSteps["Post2telegraf"].testRequest.response.responseHeaders["#status#"].toString();

def rspStatusCode = (responseCode =~ "[1-5]\\d\\d")[0];

log.info "ResponseCode for Post2telegraf = " + rspStatusCode;
log.info "Ending Post2telegraf";

if (rspStatusCode.equals("204")) {
	log.info "Results were sent to Grafana (InfluxDB)";
}
else {
	log.info "Results were not sent to Grafana (InfluxDB)";
}