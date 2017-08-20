package federator.core;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotQos;

/**
 * This class extends {@link AWSIotMessage} to provide customized handlers for
 * non-blocking message publishing.
 */
public class PublishTopicListener extends AWSIotMessage {
	
	public static String availableSparqlEndpoint;
	DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:mm:mm");

    public PublishTopicListener(String topic, AWSIotQos qos, String payload) {
        super(topic, qos, payload);
    }

    

    
    @Override
    public void onSuccess() {
    	availableSparqlEndpoint="SparqlEndpoint for publishedTopic: " + getTopic() + getStringPayload();
        System.out.println(availableSparqlEndpoint);
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - Main.startTime;
        System.out.println(elapsedTime);
        //Date afterQuery = new Date();
       // System.out.println(dateFormat.format(afterQuery)); 
    }

    @Override
    public void onFailure() {
        System.out.println(System.currentTimeMillis() + ": publish failed for " + getStringPayload());
    }

    @Override
    public void onTimeout() {
        System.out.println(System.currentTimeMillis() + ": publish timeout for " + getStringPayload());
    }

}


