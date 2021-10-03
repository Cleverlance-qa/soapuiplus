//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//>                 					KafkaConsumer
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeaders;

Properties props = new Properties()
def bootstrapServer = context.expand('${#Project#bootstrapServer}');
props.put('bootstrap.servers', bootstrapServer);
props.put('key.deserializer', 'org.apache.kafka.common.serialization.StringDeserializer');
props.put('value.deserializer', 'org.apache.kafka.common.serialization.StringDeserializer');
//props.put("session.timeout.ms", "5000");
props.put('group.id', 'soapUIplus');

//SSL config
/*
def JKS_KEYSTORE_PASSWORD = "password";
def JKS_KEYSTORE = (context.expand('${projectDir}') + "/04_Data/kafka-oleg-nonprod.keystore.jks").replaceAll("\\\\","/");
def JKS_TRUSTSTORE_PASSWORD = "changeit";
def JKS_TRUSTSTORE = (context.expand('${projectDir}') + "/04_Data/kafka-test.truststore.jks").replaceAll("\\\\","/");
props.put("security.protocol", "SSL");
props.put("ssl.endpoint.identification.algorithm", "");
props.put("ssl.keystore.location", JKS_KEYSTORE);
props.put("ssl.keystore.password", JKS_KEYSTORE_PASSWORD);
props.put("ssl.key.password", JKS_KEYSTORE_PASSWORD);
props.put("ssl.truststore.location", JKS_TRUSTSTORE);
props.put("ssl.truststore.password", JKS_TRUSTSTORE_PASSWORD);
*/

String topic = context.expand('${InputProps#kafkaConsumerTopic}');
def topics = [topic]
def reqCorrelationId = context.expand('${#TestCase#reqCorrelationId}');

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
	else {
		consumerRecords.each{ record ->
			log.info "Received message from Kafka topic =  " + record.value();
			String kafkaMessageRecieved = record.value();
			
			for (Header header : record.headers()) {  
				def headerKey = header.key();
				def headerVal = new String(header.value());
				if(headerKey.contains("correlationId")) {
					testRunner.testCase.setPropertyValue("kafkaMessageRecievedCorrelationId", headerVal);
				}
    		}
			testRunner.testCase.setPropertyValue("kafkaMessageRecieved", kafkaMessageRecieved);
			kafkaMessageRecievedCheck = consumerRecords.size();
			testRunner.testCase.setPropertyValue("kafkaMessageRecievedCheck", kafkaMessageRecievedCheck);
		}
	}		
}

consumer.commitAsync();
consumer.close();

//evaluation
evaluate(new File(context.expand(context.expand('${projectDir}') + "/06_Groovy/Utils/Evaluation/EvaluationKafka.groovy").replaceAll("\\\\","/")));