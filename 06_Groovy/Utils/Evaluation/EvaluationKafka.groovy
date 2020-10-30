//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//>                 					Evaluation Kafka                 
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

def reqCorrelationId = context.expand('${#TestCase#reqCorrelationId}');
log.info "reqCorrelationId = " + reqCorrelationId;

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
	
	kafkaTopic = context.expand('${#TestCase#kafkaProducerTopic}');
	log.info "kafkaProducerTopic = " + kafkaTopic;
}
if(stepName.contains("KafkaConsumer")){
	
	kafkaTopic = context.expand('${#TestCase#kafkaConsumerTopic}');
	log.info "kafkaConsumer = " + kafkaTopic;
}

def kafkaConsumerTopic = context.expand('${#TestCase#kafkaConsumerTopic}');
log.info "kafkaConsumerTopic = " + kafkaConsumerTopic;

def kafkaMessageType = context.expand('${#TestCase#kafkaMessageType}');
log.info "kafkaMessageType = " + kafkaMessageType;

def kafkaMessage = context.expand('${#TestCase#kafkaMessage}');
log.info "kafkaMessage = " + kafkaMessage;

//get time testVariantId for req/rsp name prefix
def testVariantId = context.expand('${InputProps#testVariantId}')

def rawRequest;
def reqRspRaw;

if(stepName.contains("KafkaProducer")){

	rawRequest = "KafkaBootstrapServer : " + kafkaBootstrapServer +  "\r\n" + "KafkaTopic : " + kafkaTopic + "\r\n" + "KafkaMessageType : " + kafkaMessageType + "\r\n\r\n" + kafkaMessage;
	
	//create raw req/rsp property 
	reqRspRaw = "ExecDateTime = " + reqExecDateTime + "\r\n\r\n" + "RAW REQUEST" + "\r\n\r\n" + rawRequest + 
		
	"\r\n\r\n" + 
	"=================================================================================================================================================================================" +
	"\r\n\r\n" + 
	"RAW RESPONSE" + "\r\n\r\n" + "N/A - async call";	

}

if(stepName.contains("KafkaConsumer")){

	rawRequest = "KafkaBootstrapServer : " + kafkaBootstrapServer +  "\r\n" + "KafkaTopic : " + kafkaTopic + "\r\n" + "KafkaMessageType : " + kafkaMessageType;
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
def kafkaMessageSendCheck = context.expand('${#TestCase#kafkaMessageSendCheck}');
def kafkaMessageRecieved = context.expand('${#TestCase#kafkaMessageRecieved}');
def kafkaMessageRecievedCheck = context.expand('${#TestCase#kafkaMessageRecievedCheck}');
def correlationId = context.expand('${#TestCase#reqCorrelationId}');
//assert status is failed or unknown set assert status as failed

if(stepName.contains("KafkaProducer")){
	
	if(kafkaMessageSendCheck.isEmpty()) {
	
		log.info "ASSERTS FAILED";
		testRunner.testCase.setPropertyValue("rspAssertsStatus", "-1");
		testRunner.testCase.setPropertyValue("rspStatusCode", "-1");
	}	
}
if(stepName.contains("KafkaConsumer")){


	if(kafkaMessageRecievedCheck.equals("0")) {

		testRunner.testCase.setPropertyValue("rspStatusCode", "-1");
		log.info "ASSERTS FAILED";
		testRunner.testCase.setPropertyValue("rspAssertsStatus", "-1");

	}
	
	if(!kafkaMessageRecieved.contains(correlationId)) {

		log.info "ASSERTS FAILED";
		testRunner.testCase.setPropertyValue("rspAssertsStatus", "-1");
	}
		
}

//def metrics tags and fields 
def metric = context.expand('${#Project#measurement}');
def app = context.expand('${#Project#app}');
def endpoint = kafkaBootstrapServer;
def String host = endpoint.split(":")[0];
def tcId = context.expand('${InputProps#tcId}');
def tcName = context.expand('${InputProps#tcName}');
//def testVariantId = context.expand('${InputProps#testVariantId}');
def testVariantDesc = context.expand('${InputProps#testVariantDesc}');
rspStatusCode = context.expand('${#TestCase#rspStatusCode}');
rspAssertsStatus = context.expand('${#TestCase#rspAssertsStatus}');
def rspTimeTaken = "N/A";
def testRunId = context.expand('${#Project#testRunId}');

def String resultMetric;

resultMetric = metric + "," \
	               + "app=" + app + ","\
	               + "environment=" + env2test + ","\
	               + "host=" + host + ","\
	               + "endpoint=" + endpoint + ","\
	               + "tcId=" + tcId + ","\
	               + "tcName=" + tcName + ","\
	               + "testVariantId=" + testVariantId + ","\
	               + "testVariantDesc=" + testVariantDesc + ","\
	               + "testStepName=" + stepName + " "\
	               + "reqCorrelationId=" + "\"" + reqCorrelationId + "\"" + ","\
	               + "rspStatusCode=" + "\"" + rspStatusCode + "\"" + ","\
	               + "rspAssertsStatus=" + "\"" + rspAssertsStatus + "\"" + ","\
	               + "randStr=" + "\"" + randStr + "\"" + ","\
	               + "testRunId=" + "\"" + testRunId + "\"" + ","\
	               + "reqExecDateTime=" + "\"" + reqExecDateTime + "\"" + ","\
	               + "rspTimeTaken=" + "\"" + rspTimeTaken + "\"";

log.info "resultMetric = " + resultMetric;

//create file with metrics
def file = new File(projRoot + "/02_Results/resultsMetrics.csv");
file.append(resultMetric + "\n");

log.info "******************************************************NEXT CALL*************************************************";
