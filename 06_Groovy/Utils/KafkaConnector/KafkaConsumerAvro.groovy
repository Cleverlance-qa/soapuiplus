//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//>                 					KafkaConsumerAvro
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeaders;
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

def topic = context.expand('${InputProps#kafkaConsumerTopic}');
def schemaRegistry =  context.expand('${#Project#schemaRegistry}');
def schemaGroup = context.expand('${InputProps#schemaGroup}');
def schemaName = context.expand('${InputProps#schemaName}');
def reqCorrelationId = context.expand('${#TestCase#reqCorrelationId}');

Properties props = new Properties()
def bootstrapServer = context.expand('${#Project#bootstrapServer}');
props.put('bootstrap.servers', bootstrapServer);
props.put('key.deserializer', 'org.apache.kafka.common.serialization.StringDeserializer');
props.put('value.deserializer', 'org.apache.kafka.common.serialization.ByteArrayDeserializer');
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

def topics = [topic]

KafkaConsumer<String, byte[]> consumer = new KafkaConsumer(props);
consumer.subscribe(topics);

boolean consumed = false;
int cyclesToTimeout = 10;
String kafkaMessageRecievedCheck = "";
testRunner.testCase.setPropertyValue("kafkaMessageRecievedCheck", kafkaMessageRecievedCheck);
byte[] kafkaMessageRecieved;

for (int i = cyclesToTimeout; !consumed && i > 0; i--) {
	
	ConsumerRecords<String,  byte[]> consumerRecords = consumer.poll(1000);
	consumed = consumerRecords.size() > 0;
	log.info('Polled ' + consumerRecords.size() + ' records.');
	
	if(consumerRecords.size() == 0) {
		testRunner.testCase.setPropertyValue("kafkaMessageRecieved", "N/A - check retry queue");
	}
	else {
		consumerRecords.each{ record ->
			kafkaMessageRecieved = record.value();
									
			for (Header header : record.headers()) {  
				def headerKey = header.key();
				def headerVal = new String(header.value());
				if(headerKey.contains("correlationId")) {
					testRunner.testCase.setPropertyValue("kafkaMessageRecievedCorrelationId", headerVal);
				}
    			}
			kafkaMessageRecievedCheck = consumerRecords.size();
			testRunner.testCase.setPropertyValue("kafkaMessageRecievedCheck", kafkaMessageRecievedCheck);
		}
	}		
}

consumer.commitAsync();
consumer.close();

//get Avro schema
String avroSchemaFromRegistry = new URL(schemaRegistry + "/apis/registry/v2/groups/" + schemaGroup + "/artifacts/" + schemaName).getText();
log.info "Avro schema from registry = " + avroSchemaFromRegistry;

//Avro Deserializer
GenericDatumReader<GenericRecord> reader = null;
JsonEncoder encoder = null;
ByteArrayOutputStream output = null;

try {
	if (kafkaMessageRecieved != null && kafkaMessageRecieved.length > 0) {
	Schema schema = new Schema.Parser().parse(avroSchemaFromRegistry);
	reader = new GenericDatumReader<GenericRecord>(schema);
	InputStream input = new ByteArrayInputStream(kafkaMessageRecieved);
	output = new ByteArrayOutputStream();
	DatumWriter<GenericRecord> writer = new GenericDatumWriter<GenericRecord>(schema);
	encoder = EncoderFactory.get().jsonEncoder(schema, output, false);
	Decoder decoder = DecoderFactory.get().binaryDecoder(input, null);
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
	output.flush();
	
	kafkaAvroMessageRecieved = new String(output.toByteArray());
	testRunner.testCase.setPropertyValue("kafkaMessageRecieved", kafkaAvroMessageRecieved);
	}
	else {
		log.info "kafkaMessageRecieved N/A - check retry queue"
	}
} 
finally {
	try { 
		if (output != null) {
			output.close();
		}
	} 
	catch (Exception e) { 
	}
}