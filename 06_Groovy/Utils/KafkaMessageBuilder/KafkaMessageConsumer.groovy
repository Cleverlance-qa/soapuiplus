//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//>                 					KafkaMessageConsumer
//>                 					version 3.0.0
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

//set values for kafka topic
def kafkaConsumerTopic = context.expand('${InputProps#kafkaConsumerTopic}');
testRunner.testCase.setPropertyValue("kafkaConsumerTopic", kafkaConsumerTopic);

return;