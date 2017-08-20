package federator.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.jena.atlas.lib.Timer;
import org.apache.jena.query.*;
import org.apache.jena.sparql.function.library.e;

import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.sample.sampleUtil.CommandArguments;
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil;
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil.KeyStorePasswordPair;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.json.JSONArray;
import org.json.JSONObject;

public class Initializer {
	
    public static String SparqlEndpoint;
    static String latitude;
	static String longitude;
	static String sensorlocation;
	static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:mm:mm");
	
	
	
//This function is to get the list of all published topics	
    public static String[] getPublishTopicsArray(String publishSensorInfo) {
       String[] PublishSensorInfo = publishSensorInfo.split("new");
       /*for(int i=0;i<PublishSensorInfo.length;i++){  
   		System.out.println(PublishSensorInfo[i]);  
     	}*/
       String[] PublishTopicsArray=new String[PublishSensorInfo.length];
       String[] PublishSensorInformation;
        for(int i=0;i<PublishSensorInfo.length;i++){
        	PublishSensorInformation=PublishSensorInfo[i].split("/");
        	PublishTopicsArray[i]=(PublishSensorInformation[0]+","+PublishSensorInformation[1]);
        }
        
       /* for(int i=0;i<PublishTopicsArray.length;i++){
    		System.out.println(PublishTopicsArray[i]);
    	}*/
        return PublishTopicsArray;
        
    }
    


    
    
   //This function is to get the location of sensors publishing their data to AWS IoT 
    public static String[] getPublishLocationsArray(String publishSensorInfo) {
    	 String[] PublishSensorInfo = publishSensorInfo.split("new");
         /* for(int i=0;i<PublishSensorInfo.length;i++){
      		System.out.println(PublishSensorInfo[i]);
        	}*/
          String[] PublishLocationsArray=new String[PublishSensorInfo.length];
          String[] PublishSensorInformation;
           for(int i=0;i<PublishSensorInfo.length;i++){
           	PublishSensorInformation=PublishSensorInfo[i].split("/");
           	PublishLocationsArray[i]=(PublishSensorInformation[2]);
           }
           
          /* for(int i=0;i<PublishLocationsArray.length;i++){
       		System.out.println(PublishLocationsArray[i]);
       	}*/
           return PublishLocationsArray;
    }
    
    
    
    
/* This function reads the SPARQL endpoint from the properties file*/
    public static String getSparqlEndpoint(CommandArguments arguments) {
    	SparqlEndpoint= arguments.getNotNull("SparqlEndpoint", FileReader.getConfig("SparqlEndpoint"));
        return SparqlEndpoint;
       
    }

   /*This function initializes the client(OpenIoT platform) to be connected to AWS IoT by reading AWS clientEndpoint, the certificate file
    *  and the private key file from the properties file*/
    public static AWSIotMqttClient getClient(CommandArguments arguments) {
        AWSIotMqttClient awsIotClient=null;
        String clientEndpoint = arguments.getNotNull("clientEndpoint", FileReader.getConfig("clientEndpoint"));
        String certificateFile = arguments.get("certificateFile", FileReader.getConfig("certificateFile"));
        String privateKeyFile = arguments.get("privateKeyFile", FileReader.getConfig("privateKeyFile"));
        Random rn = new Random();
        Integer random = rn.nextInt(1000000);
        String clientId = random.toString();
        if (awsIotClient == null && certificateFile != null && privateKeyFile != null) {
            String algorithm = arguments.get("keyAlgorithm", FileReader.getConfig("keyAlgorithm"));
            KeyStorePasswordPair pair = SampleUtil.getKeyStorePasswordPair(certificateFile, privateKeyFile, algorithm);
            awsIotClient = new AWSIotMqttClient(clientEndpoint, clientId, pair.keyStore, pair.keyPassword);
            return awsIotClient;
        }

        if (awsIotClient == null) {
            String awsAccessKeyId = arguments.get("awsAccessKeyId", FileReader.getConfig("awsAccessKeyId"));
            String awsSecretAccessKey = arguments.get("awsSecretAccessKey", FileReader.getConfig("awsSecretAccessKey"));
            String sessionToken = arguments.get("sessionToken", FileReader.getConfig("sessionToken"));

            if (awsAccessKeyId != null && awsSecretAccessKey != null) {
                awsIotClient = new AWSIotMqttClient(clientEndpoint, clientId, awsAccessKeyId, awsSecretAccessKey,
                        sessionToken);
                return awsIotClient;
            }
        }

        if (awsIotClient == null) {
            throw new IllegalArgumentException("Failed to construct client due to missing certificate or credentials.");

        }
        return awsIotClient;

    }
    
    
//This function runs a SPARQL query to OpenIoT Triplestore to get the sensor metadata
    public static JSONArray getRawSensorsInformation(String SparqlEndpoint) {
    	
    	
        String queryString = "SELECT ?sensorType ?observedValue  ?lat ?long \n"
                + "FROM <http://lsm.deri.ie/OpenIoT/guest_demo/sensormeta#> \n"
                + "WHERE\n"
                + "{ ?sensorType   <http://www.w3.org/2000/01/rdf-schema#subClassOf> <http://purl.oclc.org/NET/ssnx/ssn#Sensor>.\n"
                + " ?sensorType <http://purl.oclc.org/NET/ssnx/ssn#observes> ?observedValue.\n"
                + " ?sensorID   <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?SensorType.\n"
                + " ?sensorID <http://www.loa-cnr.it/ontologies/DUL.owl#hasLocation> ?locationID.\n"
                + " ?locationID <http://www.w3.org/2003/01/geo/wgs84_pos#lat> ?lat.\n"
                + " ?locationID <http://www.w3.org/2003/01/geo/wgs84_pos#long> ?long.\n"
                + "  }\n"
                + "";

       // Date beforeQuery = new Date();
    	//System.out.println(dateFormat.format(beforeQuery));
        Timer timer = new Timer() ;
        timer.startTimer() ;
        Query query = QueryFactory.create(queryString);
        QueryExecution qExe = QueryExecutionFactory.sparqlService(SparqlEndpoint, query);
        ResultSet results = qExe.execSelect();
        
        
        /*
        List<QuerySolution> QuerySolutions = new ArrayList<QuerySolution>();
        QuerySolutions = ResultSetFormatter.toList(results);
        ArrayList<String> availableSensors = new ArrayList<String>();
        for (int i = 0; i < QuerySolutions.size(); i++) {
            availableSensors.add(QuerySolutions.get(i).toString());
            System.out.println(availableSensors.get(i).toString());
        }
        */

     
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ResultSetFormatter.outputAsJSON(outputStream, results);
        long x=timer.endTimer(); 
        System.out.println(x+"");
       

       
        JSONObject result_json = new JSONObject(new String(outputStream.toByteArray()));
        //System.out.print(result_json.toString());
        JSONArray availableSensors = new JSONArray();
        for (int i =0;i<result_json.getJSONObject("results").getJSONArray("bindings").length();i++){
            availableSensors.put(result_json.getJSONObject("results").getJSONArray("bindings").getJSONObject(i).getJSONObject("sensorType").getString("value")+","+
            		             result_json.getJSONObject("results").getJSONArray("bindings").getJSONObject(i).getJSONObject("observedValue").getString("value")+","+
            		             result_json.getJSONObject("results").getJSONArray("bindings").getJSONObject(i).getJSONObject("lat").getString("value")+","+
            		             result_json.getJSONObject("results").getJSONArray("bindings").getJSONObject(i).getJSONObject("long").getString("value")
            		             );
        }
        
       
        //System.out.print(availableSensors);
        return availableSensors;

    }
    
    
    //This function is to display the raw sensor information in a neat way
    public static JSONArray getSensorInfo(){
    	JSONArray sensors=getRawSensorsInformation(SparqlEndpoint);
    	String []Items=new String[4];
    	ArrayList<String> items=new ArrayList<String>();
    	JSONArray jsArray = new JSONArray();
    	for (int i =0;i<sensors.length();i++){
            Items=sensors.getString(i).replaceAll(" ", "").split(",");
            /*for(int m=0;m<Items.length;m++){
        		System.out.println(Items[m]);	
        	}*/
            int j=items.size();
            items.add(j,Items[0].replace("http://openiot.eu/ontology/ns/", "") ); 
            items.add(j+1,Items[1].replace("http://services.openiot.eu/", ""));
            items.add(j+2,getSensorLocation(items.get(j))); 
            jsArray.put(items.get(j)+"/"+items.get(j+1)+"/"+items.get(j+2)+"");
         
        }
    	
  
    	/*for(int j=0;j<items.size();j++){
    		System.out.println(items.get(j));	
    	}
    	*/
    	
    	
    	System.out.println(jsArray);
    	return jsArray;
    	
    }
    
    
  //this function is to translate the longitude and latitude to a readable address using google maps API
	public static  String getSensorLocation(String sensorType) {
	   latitude=getLatitude(sensorType);
	   longitude=getLongitude(sensorType);
   	   String readUserFeed = readSensorLocationFeed(latitude.trim() + "," + longitude.trim());
   	   try {
   	      JSONObject Strjson = new JSONObject(readUserFeed);
   	      JSONArray jsonArray = new JSONArray(Strjson.get("results").toString());
   	      sensorlocation = jsonArray.getJSONObject(1)
   	            .getString("formatted_address").toString();
   	   } catch (Exception e) {
   	      e.printStackTrace();
   	   }
   	  // System.out.println(sensorlocation);
   	  
   	   return sensorlocation;
   	}

   
	//this function is to translate the longitude and latitude to a readable address using google maps API
	public static String readSensorLocationFeed(String address) {
   	   StringBuilder builder = new StringBuilder();
   	   HttpClient client=new DefaultHttpClient();
   	   HttpGet httpGet = new HttpGet(
   	         "http://maps.google.com/maps/api/geocode/json?latlng=" + address
   	               + "&sensor=false");
   	   try {
   	      HttpResponse response = client.execute(httpGet);
   	      StatusLine statusLine = response.getStatusLine();
   	      int statusCode = statusLine.getStatusCode();
   	      if (statusCode == 200) {
   	         HttpEntity entity = response.getEntity();
   	         InputStream content = entity.getContent();
   	         BufferedReader reader = new BufferedReader(new InputStreamReader(
   	               content));
   	         String line;
   	         while ((line = reader.readLine()) != null) {
   	            builder.append(line);
   	         }
   	      } else {
   	        System.out.println( "Failed to download file");
   	      }
   	   } catch (ClientProtocolException e) {
   	      e.printStackTrace();
   	   } catch (IOException e) {
   	      e.printStackTrace();
   	   }
   	   return builder.toString();
   	} 
	
	
	//This function is to get the latitude of a certain sensor type
	public static String getLatitude(String sensorType){
	
		JSONArray sensorInfo=Initializer.getRawSensorsInformation(SparqlEndpoint);
		String[] Items;
		
		for (int i =0;i<sensorInfo.length();i++){
            Items=sensorInfo.getString(i).split(",");
            if(Items[0].contains(sensorType)==true){
            	return Items[2];
            }
        }
		return "sensor not found";
	}
	
	//This function is to get the longitude of a certain sensor type
	public static String getLongitude(String sensorType){
		JSONArray sensorInfo=Initializer.getRawSensorsInformation(SparqlEndpoint);
		String[] Items;
		
		for (int i =0;i<sensorInfo.length();i++){
            Items=sensorInfo.getString(i).split(",");
            if(Items[0].contains(sensorType)==true){
            	return Items[3];
            }
        }
		return "sensor not found";
		
	}

  

}
