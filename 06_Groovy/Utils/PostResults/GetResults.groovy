//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//>                 					GetMetrics                   
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

log.info "********************************************SEND RESULTS********************************************************";

def inputProps = testRunner.testCase.getTestStepByName("InputProps"); 
def testSuite = testRunner.testCase.testSuite.name;
def testCase = testRunner.testCase.name;
def sendResults = context.expand('${#Project#sendResults}');
def testCaseName = testRunner.testCase.name.toString();
def testCaseResult = testRunner.getStatus().toString();
def testEexecKey = context.expand('${InputProps#xrayTestExecKey}');
def testEexecKeyMaster = context.expand('${#Project#xrayTestExecKeyMaster}');
def testKey = context.expand('${InputProps#xrayTestKey}');

if(!testEexecKeyMaster.equals("n/a")) {

    log.info "xrayTestExecKeyMaster in DS_envConfig is not n/a, setting as xrayTestExecKey"
    inputProps.setPropertyValue("xrayTestExecKey", testEexecKeyMaster);
    log.info "testEexecKey" + " = " + testEexecKeyMaster;
}
else {

    log.info "testEexecKey" + " = " + testEexecKey;
}

log.info "testKey" + " = " + testKey;
 
if(testCaseResult.equals("FINISHED")) {
     
    testRunner.testCase.testSuite.project.testSuites[testSuite].testCases[testCase].setPropertyValue("testCaseResult", "PASS");
    log.info testCaseName + " = PASS";
}
else {
 
    testRunner.testCase.testSuite.project.testSuites[testSuite].testCases[testCase].setPropertyValue("testCaseResult", "FAIL");
    log.info testCaseName + " = FAIL";
}