//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//>                 					KafkaConsumer
//>                 					version 3.0.0
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;

Properties props = new Properties()
def bootstrapServer = context.expand('${#Project#bootstrapServer}');
props.put('bootstrap.servers', bootstrapServer);
props.put('key.deserializer', 'org.apache.kafka.common.serialization.StringDeserializer');
props.put('value.deserializer', 'org.apache.kafka.common.serialization.StringDeserializer');
props.put('group.id', 'groovy');

String topic = context.expand('${#TestCase#kafkaConsumerTopic}');
def topics = [topic]
def reqCorrelationId = context.expand('${#TestCase#reqCorrelationId}');
log.info(reqCorrelationId);

KafkaConsumer<String, String> consumer = new KafkaConsumer(props);
consumer.subscribe(topics);

boolean consumed = false;
int cyclesToTimeout = 10;
String kafkaMessageRecievedCheck = "";
testRunner.testCase.setPropertyValue("kafkaMessageRecievedCheck", kafkaMessageRecievedCheck);


for (int i = cyclesToTimeout; !consumed && i > 0; i--) {

	ConsumerRecords<String, String>  consumerRecords = consumer.poll(1000);
	consumed = consumerRecords.size() > 0;
	log.info('Polled ' + consumerRecords.size() + ' records.');

	if(consumerRecords.size() == 0) {
		
		testRunner.testCase.setPropertyValue("kafkaMessageRecieved", "N/A - check retry queue");
	}
	consumerRecords.each{ record ->
	log.info("Received message from Kafka topic: " + record.value());
	
		if(record.value().contains(reqCorrelationId)){
			
			log.info("Received message from Kafka topic - OK");
			
			String kafkaMessageRecieved = record.value();
			testRunner.testCase.setPropertyValue("kafkaMessageRecieved", kafkaMessageRecieved);
			kafkaMessageRecievedCheck = consumerRecords.size();
	          testRunner.testCase.setPropertyValue("kafkaMessageRecievedCheck", kafkaMessageRecievedCheck);
		}
	}	
}

consumer.commitAsync();
consumer.close();