//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//>                 					EvaluationArtemisAmq
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

def reqCorrelationId = context.expand('${#TestCase#reqCorrelationId}');
//log.info "reqCorrelationId = " + reqCorrelationId;

def randStr = org.apache.commons.lang.RandomStringUtils.random(10, true, true);
testRunner.testCase.setPropertyValue("randStr", randStr); 

//create folder for req/rsp if not exist
File directory = new File(projRoot + "/03_Requests_Responses/" + env2test);
if (!directory.exists()) {

	directory.mkdir();	
}

//get amq hostname
def amqHost = context.expand('${#Project#amqUrl}');
log.info "amqHost = " + amqHost;
def amqReqMessage = context.expand('${#TestCase#amqReqMessage}');
def amqRspMessage = context.expand('${#TestCase#amqRspMessage}');

//get time testVariantId for req/rsp name prefix
def testVariantId = context.expand('${InputProps#testVariantId}');

def rspStatusCode;
def rspAssertsStatus;
def result;
def rawRequest;
def reqRspRaw;

if(stepName.contains("ArtemisAmqProducer")) {

	rawRequest = "amqHost : " + amqHost + "\r\n\r\n" + amqReqMessage;
	
	//create raw req/rsp property 
	reqRspRaw = "ExecDateTime = " + reqExecDateTime + "\r\n\r\n" + "RAW REQUEST" + "\r\n\r\n" + rawRequest + 
		
	"\r\n\r\n" + 
	"=================================================================================================================================================================================" +
	"\r\n\r\n" + 

	"RAW RESPONSE" + "\r\n\r\n" + "N/A - async call";	

}

if(stepName.contains("ArtemisAmqConsumer")) {

	rawRequest = "amqHost : " + amqHost;
		
	//create raw req/rsp property 
	reqRspRaw = "ExecDateTime = " + reqExecDateTime + "\r\n\r\n" + "RAW REQUEST" + "\r\n\r\n" + rawRequest + "\r\n\r\n" + "N/A - async call" + 
		
	"\r\n\r\n" + 
	"=================================================================================================================================================================================" +
	"\r\n\r\n" + 

	"RAW RESPONSE" + "\r\n\r\n" + "\r\n\r\n" + amqRspMessage;	

}

//create file with raw req/rsp
def reqRspRawFile = new File(projRoot + "/03_Requests_Responses/" + env2test + "/" + stepName + "_req_rsp" + "(" + testVariantId + ").txt").write(reqRspRaw);

//set assertions and result
def amqRspStatusCode = context.expand('${#TestCase#rspStatusCode}');
log.info "rspStatusCode = " + amqRspStatusCode
rspAssertsStatus = testRunner.testCase.setPropertyValue("rspAssertsStatus", "1");
rspAssertsStatus = "1";
result = "1i";

if(stepName.contains("ArtemisAmqProducer")){

	//empty message offset check
	if(amqRspStatusCode != "200" || amqReqMessage.isEmpty()) {
		testRunner.testCase.setPropertyValue("rspAssertsStatus", "-1");
		rspAssertsStatus = "-1";
		result = "-1i";
		log.info "ASSERTS FAILED";
	}	
}
if(stepName.contains("ArtemisAmqConsumer")){
	//zero recieved message check
	if(amqRspStatusCode != "200" || amqRspMessage.isEmpty()) {
		testRunner.testCase.setPropertyValue("rspAssertsStatus", "-1");
		rspAssertsStatus = "-1";
		result = "-1i";
		log.info "ASSERTS FAILED";
	}
	//reqCorrelation vs recievdCorrelationId check (optional)
	
	def rspCorrelationId = context.expand('${#TestCase#rspCorrelationId}');
	log.info "rspCorrelationId = " + rspCorrelationId;
	if(!reqCorrelationId.equals(rspCorrelationId)) {
		testRunner.testCase.setPropertyValue("rspAssertsStatus", "-1");
		rspAssertsStatus = "-1";
		result = "-1i";
		log.info "ASSERTS FAILED";
	}	
}

//def metrics tags and fields 
def metric = context.expand('${#Project#measurement}');
def app = context.expand('${#Project#app}');
def appComponent = context.expand('${#Project#appComponent}');
def endpoint = amqHost;
String host = endpoint.split(":")[0];
def tcId = context.expand('${InputProps#tcId}');
def tcName = context.expand('${InputProps#tcName}');
def testVariantDesc = context.expand('${InputProps#testVariantDesc}');
def rspTimeTaken = "N/A";
def testRunId = context.expand('${#Project#testRunId}');
def businessDate = context.expand('${#Project#businessDate}')

String resultMetric = metric + "," \
	               + "environment=" + env2test + ","\
	               + "host=" + host + ","\
	               + "endpoint=" + endpoint + ","\
	               + "app=" + app + ","\
	               + "app_comp=" + appComponent + ","\
	               + "tc_id=" + tcId + ","\
	               + "tc_name=" + tcName + ","\
	               + "test_varinat_id=" + testVariantId + ","\
	               + "test_varinat_desc=" + testVariantDesc + ","\
	               + "test_step_name=" + stepName + " "\
	               + "req_correlation_id=" + "\"" + reqCorrelationId + "\"" + ","\
	               + "rsp_status_code=" + "\"" + amqRspStatusCode + "\"" + ","\
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