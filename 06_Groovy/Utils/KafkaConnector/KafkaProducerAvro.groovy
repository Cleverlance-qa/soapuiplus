//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//>                 					KafkaProducerAvro
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.JsonEncoder;
import java.nio.ByteBuffer;
import java.util.UUID;

//create properties
Properties props = new Properties();
def bootstrapServer = context.expand('${#Project#bootstrapServer}');
props.put('bootstrap.servers', bootstrapServer);
props.put('key.serializer', 'org.apache.kafka.common.serialization.StringSerializer');
props.put('value.serializer', 'org.apache.kafka.common.serialization.ByteArraySerializer');
props.put('session.timeout.ms', '5000');
props.put('group.id', 'soapUIplus');

//SSL config
/*
def JKS_KEYSTORE_PASSWORD = "password";
def JKS_KEYSTORE = (context.expand('${projectDir}') + "/04_Data/mq-test-client.jks").replaceAll("\\\\","/");
def JKS_TRUSTSTORE_PASSWORD = "password";
def JKS_TRUSTSTORE = (context.expand('${projectDir}') + "/04_Data/mq-test-client.jks").replaceAll("\\\\","/");
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
def schemaRegistry =  context.expand('${#Project#schemaRegistry}');
def schemaGroup = context.expand('${InputProps#schemaGroup}');
def schemaName = context.expand('${InputProps#schemaName}');
def schemaId = context.expand('${InputProps#schemaId}');
def message = context.expand('${InputProps#dsKafkaMessage}');
def reqCorrelationId = context.expand('${#TestCase#reqCorrelationId}');

//get avro schema from registry
String avroSchemaFromRegistry = new URL(schemaRegistry + "/apis/registry/v2/groups/" + schemaGroup + "/artifacts/" + schemaName).getText();
log.info "Avro schema from registry = " + avroSchemaFromRegistry;
InputStream input = null;
GenericDatumWriter<GenericRecord> writer = null;
Encoder encoder = null;
ByteArrayOutputStream output = null;
byte[] kafkaAvroMessage;

try {
	Schema schema = new Schema.Parser().parse(avroSchemaFromRegistry);
	DatumReader<GenericRecord> reader = new GenericDatumReader<GenericRecord>(schema);
	input = new ByteArrayInputStream(message.getBytes());
	output = new ByteArrayOutputStream();
	DataInputStream din = new DataInputStream(input);
	writer = new GenericDatumWriter<GenericRecord>(schema);
	Decoder decoder = DecoderFactory.get().jsonDecoder(schema, din);
	encoder = EncoderFactory.get().binaryEncoder(output, null);
	GenericRecord datum;
	while (true) {
		try {
			datum = reader.read(null, decoder);
		} 
		catch (EOFException eofe) {
			break;
		}
		writer.write(datum, encoder);
	}
	encoder.flush();
	kafkaAvroMessage = output.toByteArray();
} 
finally {
	try { 
		input.close(); 
	}
	catch (Exception e) {
	}
}

//call kafka as producer
def producer = new KafkaProducer(props);
String key = new Random().nextLong();

ByteBuffer buff = ByteBuffer.allocate(8);
buff.putLong(Long.parseLong(schemaId));

ByteArrayOutputStream output2 = new ByteArrayOutputStream();
output2.write(kafkaAvroMessage);
byte[] c = output2.toByteArray();
String convertedString = new String(c);

log.info("Kafka message in avro froat = " + convertedString);
ProducerRecord<String, byte[]> record = new ProducerRecord<>(topic, key, c);
record.headers().add("correlationId", reqCorrelationId.getBytes());
record.headers().add("contentType", "avro/binary".getBytes());
record.headers().add("apicurio.key.globalId", buff.array());
record.headers().add("messaging.id", reqCorrelationId.getBytes());
log.info("Sending message to Kafka topic");

//send message
producer.send(record, { 
	RecordMetadata metadata, Exception e ->
	log.info "The offset of the record we just sent is: ${metadata.offset()}";
	log.info "reqCorrelationId = " + reqCorrelationId;
	String kafkaMessageSendCheck = metadata.offset();
	testRunner.testCase.setPropertyValue("kafkaMessageSendCheck", kafkaMessageSendCheck);
} 
as Callback
)
producer.close();