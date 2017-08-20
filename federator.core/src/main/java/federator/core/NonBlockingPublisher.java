package federator.core;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotQos;



public class NonBlockingPublisher implements Runnable {
	
	        public String PublishTopic;
	        public static final AWSIotQos TopicQos = AWSIotQos.QOS0;
	        public AWSIotMqttClient awsIotClient;
	        public String SparqlEndpoint;
	        public String location;
	        

	        public NonBlockingPublisher(AWSIotMqttClient awsIotClient,String PublishTopic,String SparqlEndpoint,String location) {
	            this.awsIotClient = awsIotClient;
	            this.PublishTopic= PublishTopic;
	            this.SparqlEndpoint=SparqlEndpoint;
	            this.location=location;
	        }

	        @Override
	        public void run() {
	                String payload = " in "+location+ " is "+SparqlEndpoint;
	                AWSIotMessage message = new PublishTopicListener(PublishTopic, TopicQos, payload);
	               while(true){
	                try {
	                    awsIotClient.publish(message);
	       
	                } catch (AWSIotException e) {
	                	 System.out.println(System.currentTimeMillis() + ": Error");
	                   
	                }
	                try {
	                    Thread.sleep(6000);
	                } catch (InterruptedException e) {
	                    System.out.println(System.currentTimeMillis() + ": NonBlockingPublisher was interrupted");
	                    return;
	                }
	               }
	              
	            
	        }
	        
	      
	        
	    }


