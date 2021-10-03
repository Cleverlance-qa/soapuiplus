//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//>                 					Evaluation Kafka
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

def kafkaMessageRecievedCorrelationId = context.expand('${#TestCase#kafkaMessageRecievedCorrelationId}');

def randStr = org.apache.commons.lang.RandomStringUtils.random(10, true, true);
testRunner.testCase.setPropertyValue("randStr", randStr); 

//create folder for req/rsp if not exist
File directory = new File(projRoot + "/03_Requests_Responses/" + env2test);
if (!directory.exists()) {

	directory.mkdir();	
}

//get kafka konfig data

def kafkaBootstrapServer = context.expand('${#Project#bootstrapServer}');
log.info "kafkaBootstrapServer = " + kafkaBootstrapServer;

def kafkaTopic 
if(stepName.contains("KafkaProducer")){
	
	kafkaTopic = context.expand('${InputProps#kafkaProducerTopic}');
	log.info "kafkaProducerTopic = " + kafkaTopic;
}
if(stepName.contains("KafkaConsumer")){

	kafkaTopic = context.expand('${InputProps#kafkaConsumerTopic}');
	log.info "kafkaConsumer = " + kafkaTopic;
}

//def kafkaConsumerTopic = context.expand('${#TestCase#kafkaConsumerTopic}');
//log.info "kafkaConsumerTopic = " + kafkaConsumerTopic;

def kafkaMessage = context.expand('${InputProps#dsKafkaMessage}');
log.info "kafkaMessage = " + kafkaMessage;

//get time testVariantId for req/rsp name prefix
def testVariantId = context.expand('${InputProps#testVariantId}')

def rawRequest;
def reqRspRaw;

if(stepName.contains("KafkaProducer")){

	rawRequest = "KafkaBootstrapServer : " + kafkaBootstrapServer +  "\r\n" + "KafkaTopic : " + kafkaTopic + "\r\n\r\n" + kafkaMessage;
	
	//create raw req/rsp property 
	reqRspRaw = "ExecDateTime = " + reqExecDateTime + "\r\n\r\n" + "RAW REQUEST" + "\r\n\r\n" + rawRequest + 
		
	"\r\n\r\n" + 
	"=================================================================================================================================================================================" +
	"\r\n\r\n" + 
	"RAW RESPONSE" + "\r\n\r\n" + "N/A - async call";	

}

if(stepName.contains("KafkaConsumer")){

	log.info "kafkaMessageRecievedCorrelationId = " + kafkaMessageRecievedCorrelationId;

	rawRequest = "KafkaBootstrapServer : " + kafkaBootstrapServer +  "\r\n" + "KafkaTopic : " + kafkaTopic;
	def kafkaMessageRecieved = context.expand('${#TestCase#kafkaMessageRecieved}');
	
	//create raw req/rsp property 
	reqRspRaw = "ExecDateTime = " + reqExecDateTime + "\r\n\r\n" + "RAW REQUEST" + "\r\n\r\n" + rawRequest + "\r\n\r\n" + "N/A - async call" + 
		
	"\r\n\r\n" + 
	"=================================================================================================================================================================================" +
	"\r\n\r\n" + 
	"RAW RESPONSE" + "\r\n\r\n" + "\r\n\r\n" + kafkaMessageRecieved;	

}

//create file with raw req/rsp
def reqRspRawFile = new File(projRoot + "/03_Requests_Responses/" + env2test + "/" + stepName + "_req_rsp" + "(" + testVariantId + ").txt").write(reqRspRaw);


//check assertion status
def rspStatusCode = testRunner.testCase.setPropertyValue("rspStatusCode", "200");
def rspAssertsStatus = testRunner.testCase.setPropertyValue("rspAssertsStatus", "1");
def result = "1i";
def kafkaMessageSendCheck = context.expand('${#TestCase#kafkaMessageSendCheck}');
def kafkaMessageRecieved = context.expand('${#TestCase#kafkaMessageRecieved}');
def kafkaMessageRecievedCheck = context.expand('${#TestCase#kafkaMessageRecievedCheck}');
def correlationId = context.expand('${#TestCase#reqCorrelationId}');
//assert status is failed or unknown set assert status as failed

if(stepName.contains("KafkaProducer")){

	//empty message offset check
	if(kafkaMessageSendCheck.isEmpty()) {
		testRunner.testCase.setPropertyValue("rspAssertsStatus", "-1");
		testRunner.testCase.setPropertyValue("rspStatusCode", "-1");
		result = "-1i";
		log.info "ASSERTS FAILED";
	}	
}
if(stepName.contains("KafkaConsumer")){
	//zero recieved message check
	if(kafkaMessageRecievedCheck.equals("0")) {
		testRunner.testCase.setPropertyValue("rspStatusCode", "-1");
		testRunner.testCase.setPropertyValue("rspAssertsStatus", "-1");
		result = "-1i";
		log.info "ASSERTS FAILED";
	}
	//reqCorrelation vs recievdCorrelationId check (optional)
	if(!reqCorrelationId.equals(kafkaMessageRecievedCorrelationId)) {
		testRunner.testCase.setPropertyValue("rspAssertsStatus", "-1");
		result = "-1i";
		log.info "ASSERTS FAILED";
	}	
}

// metrics tags and fields 
def metric = context.expand('${#Project#measurement}');
def app = context.expand('${#Project#app}');
def appComponent = context.expand('${#Project#appComponent}');
def endpoint = kafkaBootstrapServer;
String host = endpoint.split(":")[0];
def tcId = context.expand('${InputProps#tcId}');
def tcName = context.expand('${InputProps#tcName}');
def tcDesc = context.expand('${InputProps#tcDesc}');
def testVariantDesc = context.expand('${InputProps#testVariantDesc}');
rspStatusCode = context.expand('${#TestCase#rspStatusCode}');
rspAssertsStatus = context.expand('${#TestCase#rspAssertsStatus}');
def rspTimeTaken = "N/A";
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
