//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//>                 					Evaluation DB                   
//>										version 3.0.0
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

import org.apache.commons.io.FileUtils;

//get project root path
def projRoot = context.expand('${projectDir}');

//get actual environment
def env2test = context.expand('${#Project#env2test}');

//get call execution dateTime
def reqExecDateTime = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:SS").format(new Date());
log.info "reqExecDateTime = " + reqExecDateTime;

//get call step name
def stepName = testRunner.testCase.getTestStepAt(context.getCurrentStepIndex()).getLabel();
log.info "stepName = " + stepName;

//get response of db step
def dbRsp = context.expand('${${stepName}#ResponseAsXml}');
testRunner.testCase.setPropertyValue("rspRaw", dbRsp);
log.info "dbRsp = " + dbRsp;

//set response status code
def rspStatusCode;

if (dbRsp.startsWith("<Results>")) {

	rspStatusCode = "OK";
	log.info "rspStatusCode =" +  rspStatusCode;
}
else {

	rspStatusCode = "FAILED";
	log.info "rspStatusCode =" +  rspStatusCode;
}

//get response time
def rspTimeTaken = testRunner.testCase.testSteps[stepName].testRequest.response.timeTaken;
log.info "rspTimeTaken = " + rspTimeTaken + "ms";

//get raw request/response and create as txt file
if(!rspStatusCode.startsWith("x")) {

	//create folder for req/rsp if not exist
	File directory = new File(projRoot + "/03_Requests_Responses/" + env2test);
	
	if (!directory.exists()) {
	
		directory.mkdir();	
	}

	//get time dateTime for req/rsp name prefix
	def reqExecDateTimeForFileName = new java.text.SimpleDateFormat("yyyyMMdd_HHmmSS").format(new Date());

	//get time testVariantId for req/rsp name prefix
	def testVariantId = context.expand('${InputProps#testVariantId}');
	
	//get random string as file unique identificator
	randStr = org.apache.commons.lang.RandomStringUtils.random(10, true, true);
	testRunner.testCase.setPropertyValue("randStr", randStr);

	def reqStep = testRunner.testCase.getTestStepByName(stepName);
	
	//get connection string of db step
	def dbCon = reqStep.getConnectionString();
	testRunner.testCase.setPropertyValue("dbCon", dbCon);
	log.info "dbConnectionString = " + dbCon;
	
	//get requset of db step
	def dbReq = reqStep.getQuery();
	testRunner.testCase.setPropertyValue("reqRaw", dbReq);
	log.info "dbReq = " + dbReq;

	//create raw req/rsp property 
	def reqRaw = context.expand('${#TestCase#reqRaw}');
	def rspRaw = context.expand('${#TestCase#rspRaw}');
	def reqRspRaw = "ExecDateTime = " + reqExecDateTime + "\r\n\r\n" + "RAW REQUEST" + "\r\n\r\n" + reqRaw + 

	"\r\n\r\n" + 
	"=================================================================================================================================================================================" +
	"\r\n\r\n" + 
	"RAW RESPONSE" + "\r\n\r\n" + rspRaw;

	//create file with raw req/rsp
	def reqRspRawFile = new File(projRoot + "/03_Requests_Responses/" + env2test + "/" + stepName + "_req_rsp" + "(" + randStr + ").txt").write(reqRspRaw);
}

//check assertion status
testRunner.testCase.setPropertyValue("rspAssertsStatus", "1");

obj = context.testCase.getTestStepByName(stepName);
assertions = obj.getAssertionList();
assertions.each { 
      
	log.info(it.name + " - " + it.status);
	status = (it.status).toString();

	//assert status is failed or unknown set assert status as failed
	if(status.startsWith("FAIL")) {
	
		log.info "ASSERTS FAILED";
		testRunner.testCase.setPropertyValue("rspAssertsStatus", "-1");
	}
}

//def metrics tags and fields 
def metric = context.expand('${#Project#measurement}');
def app = context.expand('${#Project#app}');
def endpoint = context.expand('${#TestCase#dbCon}');
def String host = endpoint.split("@")[1];
def tcId = context.expand('${InputProps#tcId}');
def tcName = context.expand('${InputProps#tcName}');
def tcDesc = context.expand('${InputProps#tcDesc}');
def testVariantId = context.expand('${InputProps#testVariantId}');
def testVariantDesc = context.expand('${InputProps#testVariantDesc}');
def rspAssertsStatus = context.expand('${#TestCase#rspAssertsStatus}');
def testRunId = context.expand('${#Project#testRunId}');

def String resultMetric;

resultMetric = metric + "," \
	               + "app=" + app + ","\
	               + "environment=" + env2test + ","\
	               + "host=" + host + ","\
	               + "endpoint=" + endpoint + ","\
	               + "tcId=" + tcId + ","\
	               + "tcName=" + tcName + ","\
	               + "tcDesc=" + tcDesc + ","\
	               + "tvId=" + testVariantId + ","\
	               + "tvDesc=" + testVariantDesc + ","\
	               + "testStepName=" + stepName + " "\
	               + "rsp_statusCode=" + "\"" + rspStatusCode + "\"" + ","\
	               + "rsp_assertsStatus=" + "\"" + rspAssertsStatus + "\"" + ","\
	               + "randStr=" + "\"" + randStr + "\"" + ","\
	               + "testRunId=" + "\"" + testRunId + "\"" + ","\
	               + "req_execDateTime=" + "\"" + reqExecDateTime + "\"" + ","\
	               + "rsp_timeTaken=" + "\"" + rspTimeTaken + "\"";

log.info "resultMetric = " + resultMetric;

//create file with metrics
def file = new File(projRoot + "/02_Results/resultsMetrics.csv");
file.append(resultMetric + "\n");

log.info "******************************************************NEXT CALL*************************************************";
