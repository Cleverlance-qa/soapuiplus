//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//>                 					KafkaMessageProducer
//>                 					version 3.0.0
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

//get body from rest call for kafka message 
def stepName = testRunner.testCase.getTestStepAt(context.getCurrentStepIndex()-1);

def restMessage = context.expand('${${#TestCase#stepName}#Request}');

//get new correlationId and replace it
def reqCorrelationId = context.expand('${#TestCase#reqCorrelationId}');
def reqKafkaCorrelationId = UUID.randomUUID().toString();
testRunner.testCase.setPropertyValue("reqCorrelationId", reqKafkaCorrelationId);
def kafkaMessage = restMessage.replace(reqCorrelationId, reqKafkaCorrelationId);
testRunner.testCase.setPropertyValue("kafkaMessage", kafkaMessage);

//set values for kafka topic and message
def kafkaMessageType = context.expand('${InputProps#kafkaMessageType}');
testRunner.testCase.setPropertyValue("kafkaMessageType", kafkaMessageType);

def kafkaProducerTopic = context.expand('${InputProps#kafkaProducerTopic}');
testRunner.testCase.setPropertyValue("kafkaProducerTopic", kafkaProducerTopic);

return;