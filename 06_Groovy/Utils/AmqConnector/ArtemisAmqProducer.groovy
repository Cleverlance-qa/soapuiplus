//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//>                 					ArtemisAmqProducer
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.DeliveryMode;

//get amq params
def amqUrl = context.expand('${#Project#amqUrl}');
def queueName = context.expand('${InputProps#amqProducerQueue}');
def username = context.expand('${#Project#amqUsername}');
def password = context.expand('${#Project#amqPassword}');

try {
	//create amq connection
	ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(amqUrl);
	Connection connection = connectionFactory.createConnection(username, password);
	connection.start();
	log.info "Connection started successfully";
	
	//create amq session
	Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	Destination destination = session.createQueue(queueName);
	MessageProducer producer = session.createProducer(destination);
	producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
	
	//get message
	def amqReqMessage = context.expand('${InputProps#amqMessage}');
	testRunner.testCase.setPropertyValue("amqReqMessage", amqReqMessage);
	TextMessage message = session.createTextMessage(amqReqMessage);
	def correlationId = context.expand('${#TestCase#reqCorrelationId}');
	message.setJMSCorrelationID(correlationId);
	
	//send message
	producer.send(message);
	log.info "Message has been sent successfully";
	testRunner.testCase.setPropertyValue("rspStatusCode", "200");
} 

finally {
	//close session
	session.close();
	log.info "Session has been closed";
	//close connection
	connection.close();
	log.info "Connection has been closed"
}