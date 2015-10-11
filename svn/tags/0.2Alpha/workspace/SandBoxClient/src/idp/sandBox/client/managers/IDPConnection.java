package idp.sandBox.client.managers;

import idp.sandBox.client.constants.Constants;
import idp.sandBox.server.MessagingInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Singleton class for connection
 * 
 * @author Natfullin A
 *
 */
public class IDPConnection {

	static private MessagingInterface connection = null;
	
	static public MessagingInterface getConnection()
	{
		if (connection == null)
		{
			try 
			{
				// get the registry
				Registry registry = LocateRegistry.getRegistry(
						IDPSettingsManager.getServerAddress(),
						Integer.parseInt(IDPSettingsManager.getServerPort())
				);
				// look up the remote object
				connection = (MessagingInterface) (registry
						.lookup(Constants.defaultServerName));
			}
			catch (RemoteException e0) {
				e0.printStackTrace();
			}
			catch (NotBoundException e0) {
				e0.printStackTrace();
			}
		}
		return connection;
	}
	
	static public void disconnect()
	{
		connection = null;
	}
}
