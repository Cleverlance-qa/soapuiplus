//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//>                 					Post2xray
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

log.info "Running Post2xray";

testRunner.testCase.testSuite.project.testSuites["Runner"].testCases["Runner"].testSteps["Post2xray"].run(testRunner, context);

def responseCode = testRunner.testCase.testSuite.project.testSuites["Runner"].testCases["Runner"].testSteps["Post2xray"].testRequest.response.responseHeaders["#status#"].toString();

def rspStatusCode = (responseCode =~ "[1-5]\\d\\d")[0];

log.info "rspStatusCode Post2xray = " + rspStatusCode;
log.info "Ending Post2xray";

if (rspStatusCode.equals("200")) {
	log.info "Results were sent to Xray (Jira)";
}
else {
	log.info "Results were not sent to Xray (Jira)";
}