//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//>                 					Evaluation DB                   
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
def rspAssertsStatus;
def status;
def result;

if (dbRsp.startsWith("<Results>")) {
	
	rspStatusCode = "200";
	log.info "rspStatusCode =" +  rspStatusCode;
}
else {
	
	rspStatusCode = "500";
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
rspAssertsStatus = "1";
testRunner.testCase.setPropertyValue("rspAssertsStatus", "1");

obj = context.testCase.getTestStepByName(stepName);
assertions = obj.getAssertionList();
assertions.each { 
      
	log.info(it.name + " - " + it.status);
	status = (it.status).toString();

	//assert status is failed or unknown set assert status as failed
	if(status.startsWith("FAIL")) {
		
		rspAssertsStatus = "-1"
		testRunner.testCase.setPropertyValue("rspAssertsStatus", "-1");
		log.info "ASSERTS FAILED";
	}
}

//set result
if(rspStatusCode.startsWith("20") && rspAssertsStatus.equals("1")) {

	result = "1i";
}
else {
	
	result = "-1i";
}

//def metrics tags and fields 
def metric = context.expand('${#Project#measurement}');
def app = context.expand('${#Project#app}');
def appComponent = context.expand('${#Project#appComponent}');
def endpoint = (context.expand('${#TestCase#dbCon}').toLowerCase().contains("postgre")) ? context.expand('${#TestCase#dbCon}').split("\\?")[0] : context.expand('${#TestCase#dbCon}')
String host = (context.expand('${#TestCase#dbCon}').toLowerCase().contains("postgre")) ? endpoint.split("\\?")[0].split("/")[2] : endpoint.split("@")[1]
def tcId = context.expand('${InputProps#tcId}');
def tcName = context.expand('${InputProps#tcName}');
def tcDesc = context.expand('${InputProps#tcDesc}');
def testVariantId = context.expand('${InputProps#testVariantId}');
def testVariantDesc = context.expand('${InputProps#testVariantDesc}');
def testRunId = context.expand('${#Project#testRunId}');

String resultMetric = metric + "," \
					+ "environment=" + env2test + ","\
					+ "host=" + host + ","\
					+ "endpoint=" + endpoint + ","\
					+ "app=" + app + ","\
					+ "app_comp=" + appComponent + ","\
					+ "tc_id=" + tcId + ","\
					+ "tc_name=" + tcName + ","\
					+ "tc_desc=" + tcDesc + ","\
					+ "test_variant_id=" + testVariantId + ","\
					+ "test_variant_desc=" + testVariantDesc + ","\
					+ "test_step_name=" + stepName + " "\
					+ "rsp_status_code=" + "\"" + rspStatusCode + "\"" + ","\
					+ "rsp_asserts_status=" + "\"" + rspAssertsStatus + "\"" + ","\
					+ "result=" + result + ","\
					+ "rand_str=" + "\"" + randStr + "\"" + ","\
					+ "test_run_id=" + "\"" + testRunId + "\"" + ","\
					+ "req_exec_date_time=" + "\"" + reqExecDateTime + "\"" + ","\
					+ "rsp_time_taken=" + "\"" + rspTimeTaken + "\"";

if(appComponent.isEmpty() || appComponent == null) {
	resultMetric = resultMetric.replace("app_comp=,", "");
}

log.info "resultMetric = " + resultMetric;

//create file with metrics
def file = new File(projRoot + "/02_Results/resultsMetrics.csv");
file.append(resultMetric + "\n");

log.info "******************************************************NEXT CALL*************************************************";
