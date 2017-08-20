package federator.core;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.AWSIotTopic;

//This class is to run the openIoT publisher as a subscriber and subscribe the topics to AWS IoT
public class NonBlockingSubscriber implements Runnable {
	
	
	   public String SubscribeTopic;
       public static final AWSIotQos TopicQos = AWSIotQos.QOS0;
       public AWSIotMqttClient awsIotClient;
     
       

       public NonBlockingSubscriber(AWSIotMqttClient awsIotClient,String SubscribeTopic) {
           this.awsIotClient = awsIotClient;
           this.SubscribeTopic=SubscribeTopic;
        
       }

       @Override
       public void run() {
    	   
    	   AWSIotTopic subscribeTopic = new SubscribeTopicListener(SubscribeTopic, TopicQos);
             try {
            	 awsIotClient.subscribe(subscribeTopic, false);
               } catch (AWSIotException e) {
               	 System.out.println(System.currentTimeMillis() + ": Error");
                  }
              
              }
       
     

}

