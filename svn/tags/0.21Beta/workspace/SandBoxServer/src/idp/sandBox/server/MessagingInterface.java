/**
 * 
 */
package idp.sandBox.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.tree.DefaultMutableTreeNode;

import ubique.idp.processing.state.State;

/**
 * Interface of SandBox RMI server for messaging
 * 
 * @author talanovm
 * 
 */
public interface MessagingInterface extends Remote {
	void receiveMessage(String x) throws RemoteException;

	public DefaultMutableTreeNode getProjectTreeNodes() throws RemoteException;

	public String getFileContentByAbsolutePath(String absolutePath)
			throws RemoteException;

	public String getFileContentByRelativePath(String relativePath)
			throws RemoteException;

	public void saveFileContentByRelativePath(String relativePath, String content)
			throws RemoteException;

	public void sendXMLTreeFile(String content)	throws RemoteException;
	
	public void sendCorrected(String filename, String content) throws RemoteException;
	
	public void trainApply() throws RemoteException;

	public void train() throws RemoteException;

	public void apply() throws RemoteException;
	
	public void reTrainApply() throws RemoteException;

	public State getState() throws RemoteException;
	
	/**
	 * Returns annotators train status.
	 * @return true if annotators were trained
	 * @throws RemoteException
	 */
	public boolean isTrainCompleted() throws RemoteException;
	
	/**
	 * Returns annotators application status.
	 * @return true if annotators were applied
	 * @throws RemoteException
	 */
	public boolean isApplied() throws RemoteException;
	
	public ArrayList<String> getConsoleStrings() throws RemoteException;
	
	public void uploadFile(String relativePath, String filename, byte[] content) throws RemoteException;
	
	public Integer deleteFile(String relativePath) throws RemoteException;
	
	public boolean createFolder(String relativePath, String foldername) throws RemoteException;
	
	/**
	 * Sets properties of server.
	 * @param prop - Properties
	 * @throws RemoteException 
	 */
	public void setServerProperties(Properties prop) throws RemoteException;
	
	/**
	 * Set properties of project.
	 * @param prop - Properties
	 * @throws RemoteException
	 */
	public void setProjectProperties(Properties prop) throws RemoteException;
	
	public Properties getServerProperties() throws RemoteException;
	
	public void shutDownServer(long key) throws RemoteException;
	
	public void collectTagNames() throws RemoteException;
	
	public void normalize() throws RemoteException;

	public String getPropertiesPathSeparator() throws RemoteException;
}
