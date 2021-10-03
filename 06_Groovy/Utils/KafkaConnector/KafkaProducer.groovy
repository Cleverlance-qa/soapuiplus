//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//>                 					KafkaProducer
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

//create properties
Properties props = new Properties();
def bootstrapServer = context.expand('${#Project#bootstrapServer}');
props.put('bootstrap.servers', bootstrapServer);
props.put('key.serializer', 'org.apache.kafka.common.serialization.StringSerializer');
props.put('value.serializer', 'org.apache.kafka.common.serialization.StringSerializer');
props.put("session.timeout.ms", "5000");

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

//get topic and message
def topic = context.expand('${InputProps#kafkaProducerTopic}');
def message = context.expand('${InputProps#dsKafkaMessage}');
def reqCorrelationId = context.expand('${#TestCase#reqCorrelationId}');

//call kafka as producer
def producer = new KafkaProducer(props);
String key = new Random().nextLong();
ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, message);
record.headers().add("correlationId", reqCorrelationId.getBytes());
log.info("Sending message to Kafka topic");

producer.send(record,
	{ RecordMetadata metadata, Exception e ->
          log.info( "The offset of the record we just sent is: ${metadata.offset()}");
          String kafkaMessageSendCheck = metadata.offset();
          testRunner.testCase.setPropertyValue("kafkaMessageSendCheck", kafkaMessageSendCheck);
       } as Callback
)

producer.close();

//evaluation
evaluate(new File(context.expand(context.expand('${projectDir}') + "/06_Groovy/Utils/Evaluation/EvaluationKafka.groovy").replaceAll("\\\\","/")));