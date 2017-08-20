package federator.core;

import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.AWSIotTopic;

/**
 * This class extends {@link AWSIotTopic} to receive messages from a subscribed
 * topic.
 */
public class SubscribeTopicListener extends AWSIotTopic {
	
	public static String availableSparqlEndpoint;

    public SubscribeTopicListener(String topic, AWSIotQos qos) {
        super(topic, qos);
    }

    @Override
    public void onMessage(AWSIotMessage message) {
    	availableSparqlEndpoint="SparqlEndPoint for Subscribed Topic: " + message.getTopic() + message.getStringPayload();
        System.out.println("SparqlEndPoint for Subscribed Topic: " + message.getTopic() + message.getStringPayload());
        long stopTime3 = System.currentTimeMillis();
        long elapsedTime =stopTime3 - Main.stopTime2 ;
        System.out.println(elapsedTime);
    }

}


