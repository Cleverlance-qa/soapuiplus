//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//>                 					ArtemisAmqConsumer
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import javax.jms.*;

//get amq params
def amqUrl = context.expand('${#Project#amqUrl}');
def queueName = context.expand('${InputProps#amqConsumerQueue}');
def username = context.expand('${#Project#amqUsername}');
def password = context.expand('${#Project#amqPassword}');
def reqCorrelationId = context.expand('${#TestCase#reqCorrelationId}');

try {
	//create amq connection
	ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(amqUrl);
	Connection connection = connectionFactory.createConnection(username, password);
	connection.start();
	log.info "Connection started successfully";
	
	//create amq session
	Session session = connection.createSession(true, Session.CLIENT_ACKNOWLEDGE);
	Queue queue = session.createQueue(queueName);
	QueueBrowser browser = session.createBrowser(queue);
	Enumeration messagesInQueue = browser.getEnumeration();
	//get message from queue
	while (messagesInQueue.hasMoreElements()) {
		Message queueMessage = (Message) messagesInQueue.nextElement();
		def rspCorrelationId = queueMessage.getJMSCorrelationID()
		testRunner.testCase.setPropertyValue("rspCorrelationId", rspCorrelationId);
		if (queueMessage != null && reqCorrelationId.equals(queueMessage.getJMSCorrelationID())) {
			try {
				if (queueMessage instanceof TextMessage) {
					TextMessage txtMsg = (TextMessage) queueMessage;
					String messageString = txtMsg.getText();
					if (messageString.isEmpty()) {
						testRunner.testCase.setPropertyValue("rspStatusCode", "500");
						testRunner.testCase.setPropertyValue("amqRspMessage", messageString);
						session.close();
						log.info('Session has been closed');
						connection.close();
						log.info('Connection has been closed');
						throw new Exception("Message reply found, but is empty!");
					} 
					else {
						log.info('Message has been read successfully');
						def projectContext = context.testCase.testSuite.project.context;
						testRunner.testCase.setPropertyValue("rspStatusCode", "200");
						testRunner.testCase.setPropertyValue("amqRspMessage", messageString);
						log.info "Recieved message = " + messageString;
						session.close();
						connection.close();
					break;
					}
				}
			}
			catch (JMSException e) {
				log.info "Some error occured: " + e;
				testRunner.testCase.setPropertyValue("amqRspStatusCode", "500");
				session.close();
				connection.close();
			}
		}
		else if (queueMessage == null) {
			testRunner.testCase.setPropertyValue("amqRspStatusCode", "500");
			log.info "Message response was not found in the queue !";
		}
	}
}
finally {
	session.close();
	log.info "Session has been closed";
	connection.close();
	log.info "Connection has been closed";
}