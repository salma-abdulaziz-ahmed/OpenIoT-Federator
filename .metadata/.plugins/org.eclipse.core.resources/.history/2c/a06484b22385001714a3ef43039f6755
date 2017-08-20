package federator.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;



public class FileReader {
	
	 private static final String PropertyFile = "aws-iot-sdk-samples.properties";
	 
	 public  static String getConfig(String name) {
	        Properties prop = new Properties();
	        URL resource = FileReader.class.getResource(PropertyFile);
	        //System.out.println(resource+"");
	        if (resource == null) {
	            return "null";
	        }
	        try (InputStream stream = resource.openStream()) {
	            prop.load(stream);
	        } catch (IOException e) {
	            return null;
	        }
	        String value = prop.getProperty(name);
	        if (value == null || value.trim().length() == 0) {
	            return null;
	            
	        } else {
	            return value;
	        }
	    }
	 
	 
	

}

