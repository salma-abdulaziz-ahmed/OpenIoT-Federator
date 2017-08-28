package federator.core;

import com.amazonaws.services.iot.client.AWSIotMqttClient;


import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotTimeoutException;
import com.amazonaws.services.iot.client.sample.sampleUtil.CommandArguments;


import org.json.JSONArray;
import org.json.JSONObject;
import static spark.Spark.get;
import static spark.Spark.post;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;


public class Main {

    private static AWSIotMqttClient awsIotClient;
    private static String[] PublishTopics;
    private static String[] SubscribeTopics;
    private static String[] PublishLocations;
    private static String SparqlEndpoint;
    private static CommandArguments arguments;
    private static String publishSensorInfo="";
    private static String subscribeTopic="";
    private static int count=0;
    static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:mm:mm");
    public static long startTime;
    public static long startTime2;
    public static long stopTime2;

    public static void main(String args[]) throws InterruptedException, AWSIotException, AWSIotTimeoutException {
    	
    	arguments = CommandArguments.parse(args);
    	SparqlEndpoint=Initializer.getSparqlEndpoint(arguments);
    	awsIotClient = Initializer.getClient(arguments); 
    	awsIotClient.connect(); //OpenIoT connects to AWS IoT 
    	   	
    	/**
       	 * This function is the rest call to get all the available sensors in openIoT triplestore and send them to the UI
       	 * http://localhost:4567/getAvailableSensors
       	 */
        get("/getAvailableSensors", (request, response) -> {
            JSONObject result = new JSONObject();
            JSONArray AvailableSensors = Initializer.getSensorInfo();
            result.put("result", AvailableSensors);
            //System.out.println(result.toString());
            String res = "res_callback(" + result.toString() + ");";
            return res;
        });

        
        /**
       	 * This function is the rest call to post the published topic to AWS IoT
       	 * http://localhost:4567/postPublishTopic
       	 */
        post("/postPublishTopic", (request, response) -> {
        	publishSensorInfo=publishSensorInfo+request.queryParams("publishTopic")+"new";
            //System.out.println(publishSensorInfo);
            PublishTopics=Initializer.getPublishTopicsArray(publishSensorInfo);
            PublishLocations=Initializer.getPublishLocationsArray(publishSensorInfo);
         
                String PublishTopic = PublishTopics[count];
                System.out.println(PublishTopic);
                String location=PublishLocations[count];
                System.out.println(location);
                count++;
                //Date beforeQuery = new Date();
               // System.out.println(dateFormat.format(beforeQuery));
                startTime = System.currentTimeMillis();
                NonBlockingPublisher Publisher = new NonBlockingPublisher(awsIotClient, PublishTopic, SparqlEndpoint, location);
                Thread NonBlockingPublisherThraed = new Thread(Publisher);
                NonBlockingPublisherThraed.start();
            
            return request.queryParams("publishTopic");
            
        });
        
        /**
       	 * This function is the rest call to post the subscribed topic to AWS IoT
       	 * http://localhost:4567/postSubscribeTopic
       	 */
        post("/postSubscribeTopic", (request, response) -> {
        	
        	subscribeTopic=request.queryParams("subscribeTopic");
        	//System.out.println(subscribeSensorInfo);
        	 startTime2 = System.currentTimeMillis();
        	 NonBlockingSubscriber Subscriber = new NonBlockingSubscriber(awsIotClient, subscribeTopic);
             Thread nonBlockingSubscriberThread = new Thread(Subscriber);
             nonBlockingSubscriberThread.start();
             stopTime2 = System.currentTimeMillis();
             long elapsedTime2 = stopTime2 - Main.startTime2;
             System.out.println(elapsedTime2+"");
        	
            return request.queryParams("subscribeTopic");
            
        });
        
        
        /**
       	 * This function is the rest call to get the endpoint of the publishing client
       	 * http://localhost:4567/getEndpoint
       	 */
     
        get("/getEndpoint", (request, response) -> {
            JSONObject result = new JSONObject();
            result.put("result", SubscribeTopicListener.availableSparqlEndpoint);
            //System.out.println(result.toString());
            String res = "res_callback(" + result.toString() + ");";
            return res;
        });
        
        
        
        
    }
  
    
    
    
    
    
    
 

}
