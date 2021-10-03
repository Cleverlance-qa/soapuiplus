//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//>                 					Evaluation
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

import org.apache.commons.io.FileUtils;

//get project root path
def projRoot = context.expand('${projectDir}');

//get actual environment
def env2test = context.expand('${#Project#env2test}');

//get writeFailReqRspOnly
def writeFailReqRspOnly = context.expand('${#Project#writeFailReqRspOnly}');

//get call execution dateTime
def reqExecDateTime = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:SS").format(new Date());
log.info "reqExecDateTime = " + reqExecDateTime;

//get call step name
def stepName = testRunner.testCase.getTestStepAt(context.getCurrentStepIndex()).getLabel();
log.info "stepName = " + stepName;

def reqCorrelationId = context.expand('${#TestCase#reqCorrelationId}');
log.info "reqCorrelationId = " + reqCorrelationId;

//create folder for req/rsp if not exist
File directory = new File(projRoot + "/03_Requests_Responses/" + env2test);
if (!directory.exists()) {
	
	directory.mkdir();	
}

def response = context.expand( '${${#TestCase#stepName}#Response}' );

//get random string as file unique identificator
def randStr = org.apache.commons.lang.RandomStringUtils.random(10, true, true);
testRunner.testCase.setPropertyValue("randStr", randStr);

def rspStatusCode;
def rspStatus;
def result;
def rspTimeTaken;


try {
	rspStatus = context.testCase.testSteps[stepName].testRequest.response.responseHeaders["#status#"];
}
catch(Exception e) {
	rspStatus = null;
}
if (!response.isEmpty() || rspStatus != null) {

	//get response http status code
	rspStatusCode = (rspStatus =~ "[1-5]\\d\\d")[0];
	testRunner.testCase.setPropertyValue("rspStatusCode", rspStatusCode);
	log.info "rspStatusCode = " + rspStatusCode;
	
	//get response time
	rspTimeTaken = testRunner.testCase.testSteps[stepName].testRequest.response.timeTaken;
	log.info "rspTimeTaken = " + rspTimeTaken + "ms";

	def writeReqRsp;
	if( writeFailReqRspOnly == "true"){
		writeReqRsp = "20";
	} 
	else {
		writeReqRsp = "x";
	}

	//get raw request/response and create as txt file
	if(!rspStatusCode.startsWith(writeReqRsp)) {
		
		//get time dateTime for req/rsp name prefix
		def reqExecDateTimeForFileName = new java.text.SimpleDateFormat("yyyyMMdd_HHmmSS").format(new Date());
	
		//get time testVariantId for req/rsp name prefix
		def testVariantId = context.expand('${InputProps#testVariantId}')
		
		//get call url and method
		def method = context.testCase.testSteps[stepName].getHttpRequest().getMethod().toString();
		def url=testRunner.testCase.testSteps[stepName].getHttpRequest().getResponse().getURL().toString();

		//get request headers
		def requestHeaders = context.testCase.testSteps[stepName].getHttpRequest().messageExchange.getRequestHeaders().toString()
		//get request
		def request = context.expand( '${${#TestCase#stepName}#Request}' );
		//create rawRequest
		def rawRequest = method + " " + url + "\r\n\r\n" + requestHeaders + "\r\n" + request;
		testRunner.testCase.setPropertyValue("rawRequest", rawRequest);
	
		//get response headers
		def responseHeaders = context.testCase.testSteps[stepName].testRequest.response.responseHeaders.toString();
		
		//get response
		response = context.expand( '${${#TestCase#stepName}#Response}' );
		if (!response.isEmpty()) {
			
			log.info "response = " + response;
		}
		else {
			
			response = "No data - Empty response";
			log.info "No data - Empty response";
		}
		//create rawResponse
		def rawResponse = responseHeaders + "\r\n" + response;
		testRunner.testCase.setPropertyValue("rawResponse", rawResponse);
	
		log.info "*** Saving req/rsp (HTTP 20*) ***"
		//create raw req/rsp property 
		def reqRspRaw = "ExecDateTime = " + reqExecDateTimeForFileName + "\r\n\r\n" + "RAW REQUEST" + "\r\n\r\n" + rawRequest + 
	
		"\r\n\r\n" + 
		"=================================================================================================================================================================================" +
		"\r\n\r\n" + 
		"RAW RESPONSE" + "\r\n\r\n" + rawResponse;
	
		//create file with raw req/rsp
		def reqRspRawFile = new File(projRoot + "/03_Requests_Responses/" + env2test + "/" + stepName + "_req_rsp" + "(" + randStr + ").txt").write(reqRspRaw);
	}
}
else {

	log.info "rspStatusCode is not available - set Timmed out -> 408";
	rspStatusCode = "408"
	testRunner.testCase.setPropertyValue("rspStatusCode", rspStatusCode);

	//get time dateTime for req/rsp name prefix
	def reqExecDateTimeForFileName = new java.text.SimpleDateFormat("yyyyMMdd_HHmmSS").format(new Date());

	//get time testVariantId for req/rsp name prefix
	def testVariantId = context.expand('${InputProps#testVariantId}')
	
	//get call url and method
	def method = context.testCase.testSteps[stepName].getHttpRequest().getMethod().toString();
	def url = context.testCase.testSteps[stepName].getHttpRequest().getEndpoint().toString()
	
	//get request headers
	def requestHeaders = context.testCase.testSteps[stepName].getHttpRequest().getRequestHeaders().toString()
	//get request
	def request = context.expand( '${${#TestCase#stepName}#Request}' );
	//create rawRequest
	def rawRequest = method + " " + url + "\r\n\r\n" + requestHeaders + "\r\n" + request;
	testRunner.testCase.setPropertyValue("rawRequest", rawRequest);

	//create raw req/rsp property 
	def reqRspRaw = "ExecDateTime = " + reqExecDateTimeForFileName + "\r\n\r\n" + "RAW REQUEST" + "\r\n\r\n" + rawRequest + 

	"\r\n\r\n" + 
	"=================================================================================================================================================================================" +
	"\r\n\r\n" + 
	"RAW RESPONSE" + "\r\n\r\n" + "Not available due to time out";

	//create file with raw req/rsp
	log.info "*** Saving req/rsp (HTTP Other then 20*) ***"
	def reqRspRawFile = new File(projRoot + "/03_Requests_Responses/" + env2test + "/" + stepName + "_req_rsp" + "(" + randStr + ").txt").write(reqRspRaw);
}

def rspAssertsStatus = "1";

//check assertion status
if(rspStatusCode.equals("408")) {
	rspAssertsStatus = "-1"
	testRunner.testCase.setPropertyValue("rspAssertsStatus", "-1");
}
else {
	testRunner.testCase.setPropertyValue("rspAssertsStatus", "1");
}

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
if((rspStatusCode.startsWith("20") || rspStatusCode.startsWith ("30")) && rspAssertsStatus.equals("1")) {
	
	result = "1i";
}
else {
	
	result = "-1i";
}

//def metrics tags and fields 
def metric = context.expand('${#Project#measurement}');
def app = context.expand('${#Project#app}');
def appComponent = context.expand('${#Project#appComponent}');
def endpoint = context.expand('${${#TestCase#stepName}#Endpoint}');
String host = endpoint.split("/")[2];
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
					+ "req_correlation_id=" + "\"" + reqCorrelationId + "\"" + ","\
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