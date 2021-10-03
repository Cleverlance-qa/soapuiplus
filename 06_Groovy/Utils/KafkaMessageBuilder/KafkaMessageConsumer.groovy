//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//>                 					KafkaMessageConsumer
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

//set values for kafka topic
def kafkaConsumerTopic = context.expand('${InputProps#kafkaConsumerTopic}');
testRunner.testCase.setPropertyValue("kafkaConsumerTopic", kafkaConsumerTopic);

return;