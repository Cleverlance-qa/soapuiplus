//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//>                 					KafkaProducer
//>                 					version 3.0.0
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

Properties props = new Properties();
def bootstrapServer = context.expand('${#Project#bootstrapServer}');
props.put('bootstrap.servers', bootstrapServer);
props.put('key.serializer', 'org.apache.kafka.common.serialization.StringSerializer');
props.put('value.serializer', 'org.apache.kafka.common.serialization.StringSerializer');

def topic = context.expand('${#TestCase#kafkaProducerTopic}');
def MessageType = context.expand('${#TestCase#kafkaMessageType}');
def message = context.expand('${#TestCase#kafkaMessage}');

def producer = new KafkaProducer(props);
String key = new Random().nextLong();
ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, message);
record.headers().add("MessageType", MessageType.getBytes());
//log.info("sending message to Kafka topic $message");
log.info("Sending message to Kafka topic");

producer.send(record,
	{ RecordMetadata metadata, Exception e ->
          log.info( "The offset of the record we just sent is: ${metadata.offset()}");
          String kafkaMessageSendCheck = metadata.offset();
          testRunner.testCase.setPropertyValue("kafkaMessageSendCheck", kafkaMessageSendCheck);
       } as Callback
)

producer.close();