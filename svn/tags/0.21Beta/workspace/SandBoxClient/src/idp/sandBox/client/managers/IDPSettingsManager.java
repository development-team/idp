package idp.sandBox.client.managers;

import idp.sandBox.client.constants.Constants;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class IDPSettingsManager {
	
	static private Properties projectProperties = null;
	
	static private String serverPort = "3232";
	static private String serverAddress = "127.0.0.1";
	
	static
	{
		projectProperties = new Properties();
		try
		{
			projectProperties.load(new FileInputStream(Constants.propertiesFileName));
			serverPort = projectProperties.getProperty("port",Constants.defaultServerPort);
			serverAddress = projectProperties.getProperty("address",Constants.defaultServerAddress);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	static public String getServerPort()
	{
		return serverPort;
	}
	
	static public void setServerPort(String value)
	{
		serverPort = value;
	}

	static public String getServerAddress()
	{
		return serverAddress;
	}
	
	static public void setServerAddress(String value)
	{
		serverAddress = value;
	}
	
	static public Properties getProperties()
	{
		return projectProperties;
	}
	
	static public void setProperty(String key, String value)
	{
		projectProperties.setProperty(key, value);
	}
	
	static public void store(String fileName) throws IOException
	{
		projectProperties.setProperty("port", serverPort);
		projectProperties.setProperty("address", serverAddress);
		projectProperties.store(new FileOutputStream(fileName), "");
	}
}
