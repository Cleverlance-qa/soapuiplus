//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//>                 					Evaluation TC            
//>										version 3.0.0
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

import com.eviware.soapui.impl.wsdl.teststeps.WsdlTestRequestStep;
import com.eviware.soapui.impl.wsdl.teststeps.RestTestRequestStep;
import com.eviware.soapui.impl.wsdl.teststeps.HttpTestRequestStep;
import com.eviware.soapui.impl.wsdl.teststeps.JdbcRequestTestStep;

log.info "********************************************GET TC STATUS********************************************************";
//get project root path
def projRoot = context.expand('${projectDir}');
def env2test = context.expand('${#Project#env2test}');
def reqExecDateTime = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:SS").format(new Date());

def testCaseName = testRunner.testCase.name.toString();
def testCaseStatus = testRunner.testCase.setPropertyValue("testCaseStatus", "1");
def assertionName;
def assertionStatus = "PASS";



testRunner.testCase.testSuite.getTestCaseList().forEach {
	
	testCase -> testCase.getTestStepList().forEach {
		
		testStep ->
		if(testStep instanceof WsdlTestRequestStep || testStep instanceof RestTestRequestStep || testStep instanceof HttpTestRequestStep || testStep instanceof JdbcRequestTestStep) {
			
			assertions = testStep.getAssertionList();
			assertions.each {assertion -> 
				
				assertionName = assertion.name.toString();
				assertionStatus = assertion.status.toString();
				//log.info(assertionName + " - " + assertionStatus);
				if(assertionStatus.startsWith("FAIL")){
					
					testCaseStatus = testRunner.testCase.setPropertyValue("testCaseStatus", "-1");
				}
			}
		}	
	}
}
testCaseStatus = context.expand('${#TestCase#testCaseStatus}');

if(testCaseStatus.equals("1")) {
	
	log.info testCaseName + " = " + "PASSED";
}
else {
	
	log.info testCaseName + " = " + "FAILED";
}


//def metrics tags and fields 
def metric = context.expand('${#Project#measurement}');
def app = context.expand('${#Project#app}');
def testVariantId = context.expand('${InputProps#testVariantId}');
def testVariantDesc = context.expand('${InputProps#testVariantDesc}');
def rspAssertsStatus = context.expand('${#TestCase#rspAssertsStatus}');
def testRunId = context.expand('${#Project#testRunId}');
testCaseStatus = context.expand('${#TestCase#testCaseStatus}');

def String resultMetric;

resultMetric = metric + "," \
                                 + "app=" + app + ","\
                                 + "environment=" + env2test + ","\
                                 + "testVariantId=" + testVariantId + ","\
                                 + "testVariantDesc=" + testVariantDesc + " "\
                                 + "testCaseStatus=" + "\"" + testCaseStatus + "\"" + ","\
                                 + "testRunId=" + "\"" + testRunId + "\"";

log.info "resultMetric = " + resultMetric;

//create file with metrics
def file = new File(projRoot + "/02_Results/resultsMetrics.csv");
file.append(resultMetric + "\n");

log.info "******************************************************NEXT CALL*************************************************";