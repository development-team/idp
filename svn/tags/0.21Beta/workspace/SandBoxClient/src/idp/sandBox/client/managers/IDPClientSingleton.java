package idp.sandBox.client.managers;

import idp.sandBox.client.gui.SandBoxClient;

/**
 * Singleton class for client - SandBoxSessionFrame.
 * 
 * @author Natfullin A
 *
 * @see SandBoxClient
 */
public class IDPClientSingleton {

	static private SandBoxClient client = null;
	
	static public SandBoxClient getIDPClient()
	{
		if (client == null)
		{
			client = new SandBoxClient();
		}
		return client;
	}
}
