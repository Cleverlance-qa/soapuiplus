//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//>                 					Evaluation TC            
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
def result = "1i";



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
					result = "-1i";
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
def appComponent = context.expand('${#Project#appComponent}');
def testVariantId = context.expand('${InputProps#testVariantId}');
def testVariantDesc = context.expand('${InputProps#testVariantDesc}');
def rspAssertsStatus = context.expand('${#TestCase#rspAssertsStatus}');
def testRunId = context.expand('${#Project#testRunId}');
testCaseStatus = context.expand('${#TestCase#testCaseStatus}');

String resultMetric = metric + "," \
					+ "environment=" + env2test + ","\
					+ "app=" + app + ","\
					+ "app_comp=" + appComponent + ","\
					+ "test_variant_id=" + testVariantId + ","\
					+ "test_variant_desc=" + testVariantDesc + " "\
					+ "test_case_status=" + "\"" + testCaseStatus + "\"" + ","\
					+ "result=" + result + ","\
					+ "test_run_id=" + "\"" + testRunId + "\"";

if(appComponent.isEmpty() || appComponent == null) {
	resultMetric = resultMetric.replace("app_comp=,", "");
}

log.info "resultMetric = " + resultMetric;

//create file with metrics
def file = new File(projRoot + "/02_Results/resultsMetrics.csv");
file.append(resultMetric + "\n");

log.info "******************************************************NEXT CALL*************************************************";