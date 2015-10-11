package idp.sandBox.client.gui.components.trees;

import idp.sandBox.client.constants.Constants;
import idp.sandBox.client.dialogs.IDPMessageDlg;
import idp.sandBox.client.gui.SandBoxClient;
import idp.sandBox.client.gui.components.IDPGlassPane;
import idp.sandBox.client.managers.IDPClientSingleton;
import idp.sandBox.client.managers.IDPConnection;
import idp.sandBox.server.MessagingInterface;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public class IDPContentTree extends JTree implements MouseListener {

	private DefaultMutableTreeNode treeRoot; 
	private DefaultTreeModel treeModel;
	
	private ContentTreeRenderer treeCellRenderer;

	/**
	 * Pop up menu.
	 */
	JPopupMenu popup;
	
	/**
	 * Pop up menu item.
	 */
	JMenuItem menuItemCreateFolder;

	/**
	 * Pop up menu item.
	 */
	JMenuItem menuItemUpload;
	
	/**
	 * Pop up menu item.
	 */
	JMenuItem menuItemDelete;

	/**
	 * JFileChooser for uploading files to server.
	 */
	private JFileChooser fc = null;
	
	/**
	 * Relative path on the server to upload file to
	 */
	private String relativePath;
	
	/**
	 * Default constructor
	 */
	public IDPContentTree()
	{
		super(new DefaultTreeModel(new DefaultMutableTreeNode(
				Constants.defaultProjectName)));
		
		treeModel = (DefaultTreeModel)getModel();
		treeRoot = (DefaultMutableTreeNode)treeModel.getRoot();
		
		//////////////////////////////////////////////////////////////////
		// init pop up menu
		popup = new JPopupMenu();
		menuItemCreateFolder = new JMenuItem("Create folder...");
		popup.add(menuItemCreateFolder);
		menuItemUpload = new JMenuItem("Upload...");
		popup.add(menuItemUpload);
		menuItemDelete = new JMenuItem("Delete");
		popup.add(menuItemDelete);
		
		menuItemCreateFolder.addActionListener(new MenuListenerCreateFolder());
		menuItemUpload.addActionListener(new MenuListenerUploadFile());
		menuItemDelete.addActionListener(new MenuListenerDeleteFile());
		addMouseListener(this);
		//////////////////////////////////////////////////////////////////
		
		//////////////////////////////////////////////////////////////////
		// File Chooser
		fc = new JFileChooser();
		fc.setMultiSelectionEnabled(true);
		//////////////////////////////////////////////////////////////////
	}
	
	/**
	 * This method initializes jTree
	 */
	public void initTree() {
		MessagingInterface connection = IDPConnection.getConnection();
		if (connection != null) {
			try {
				DefaultMutableTreeNode n1 = connection.getProjectTreeNodes();
				
				//////////////////////////////////////////////////////////
				// for current state we'd better delete whole tree contents, but
				// later we should check for project name
				treeRoot.removeAllChildren();
				
				//////////////////////////////////////////////////////////
				// check
				if (n1 == null) return;
				//////////////////////////////////////////////////////////

				treeRoot.add(n1);
				treeModel.reload();
				//////////////////////////////////////////////////////////
			} catch (RemoteException e1) {
				e1.printStackTrace();
			}
		}
	}
	//////////////////////////////////////////////////////////////////////
	
	/**
	 *  Recursive procedure for searching path to root 
	 */
	private TreeNode[] searchPath(DefaultMutableTreeNode node, String[] str_path, int depth)
	{
		TreeNode[] path = null;
		
		//TODO

		String current_node_name = node.getUserObject().toString();
		
		if (depth == 0 && current_node_name.compareTo(str_path[0]) == 0)
		{
			path = treeModel.getPathToRoot(node);
		}
		else
		{
			int n = treeModel.getChildCount(node);
			for (int i = 0; i < n; i++)
			{
				DefaultMutableTreeNode obj = (DefaultMutableTreeNode) treeModel.getChild(node, i);
				if (obj == null) break;
				String child_name = obj.getUserObject().toString();
				if (child_name.compareTo(str_path[depth-1]) != 0) continue;
				path = searchPath(obj, str_path, depth - 1);
				if (path != null) break;
			}
		}
		
		return path;
	}
	//////////////////////////////////////////////////////////////////////
	
	public void selectPath(String[] str_path)
	{
		TreeNode[] path = searchPath((DefaultMutableTreeNode)treeModel.getRoot(), str_path, str_path.length - 1);
		
		if (path != null)
		{
			TreePath tree_path = new TreePath(path);
			setSelectionPath(tree_path);
		}
	}
	//////////////////////////////////////////////////////////////////////
	

	/**
	 * Menu action listener. Create a folder on the server.
	 * 
	 */
	private class MenuListenerCreateFolder implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			
			boolean result = false;
			
			String msg = "Enter folder name:";
			String title = "Creating folder";
			String folderName = (String)JOptionPane.showInputDialog(null, msg, title, JOptionPane.QUESTION_MESSAGE, null, null, "New folder");
			
			if (folderName != null)
			{
				try
				{
					// upload
					MessagingInterface connection = IDPConnection.getConnection();
					System.out.println("Creating folder: ");
					result = connection.createFolder(relativePath, folderName);
						
					SandBoxClient client = IDPClientSingleton.getIDPClient();
					client.refreashTree();
				}
				catch(RemoteException e1)
				{
					e1.printStackTrace();
				}
				
				if (result == true)
				{
					IDPClientSingleton.getIDPClient().glassPaneFadeIn(IDPGlassPane.IDPDialogs.MessageDialog,"Information",
							"Folder has been created succefully!",
							IDPMessageDlg.messageType.INFORMATION_MESSAGE);
				}
				else 
				{
					IDPClientSingleton.getIDPClient().glassPaneFadeIn(IDPGlassPane.IDPDialogs.MessageDialog,"Error",
							"Couldn't create a folder",
							IDPMessageDlg.messageType.ERROR_MESSAGE);	
				}
			}
		}

	}
	//////////////////////////////////////////////////////////////////////
	
	/**
	 * Menu action listener. Shows open file dialog and uploads file to server if chosen.
	 * 
	 */
	private class MenuListenerUploadFile implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			
			int returnVal = fc.showOpenDialog(null);
			
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				
				File[] files = fc.getSelectedFiles();
				FileInputStream fstream = null;
				
				try
				{
					MessagingInterface connection = IDPConnection.getConnection();
					
					for (File file : files)
					{
						fstream = new FileInputStream(file);
						long len = file.length();
						
						// if more than 1 GB
						if (len > 1000000000)
						{
							IDPClientSingleton.getIDPClient().glassPaneFadeIn(IDPGlassPane.IDPDialogs.MessageDialog,"Warning - can't upload the file",
									 "File is too big (more that 1 GB). Skipped.",
									IDPMessageDlg.messageType.WARNING_MESSAGE);
							System.out.println("["+file.getName()+"] File is too big (more that 1 GB). Skipped.");
							return;
						}
							
						int ilen = (int)(len);
						byte[] content = new byte[ilen];
						
						// read content
						fstream.read(content);
					
						// upload
						System.out.println("Uploading to "+relativePath + "/" + file.getName());
						connection.uploadFile(relativePath, file.getName(), content);
						
						fstream.close();
					}
					
					SandBoxClient client = IDPClientSingleton.getIDPClient();
					client.refreashTree();
				}
				catch(FileNotFoundException fnfexc)
				{
					fnfexc.printStackTrace();
					IDPClientSingleton.getIDPClient().glassPaneFadeIn(IDPGlassPane.IDPDialogs.MessageDialog,"Error",
							"Couldn't upload file to the server",
							IDPMessageDlg.messageType.ERROR_MESSAGE);					
					return;
				}
				catch(IOException ioexc)
				{
					IDPClientSingleton.getIDPClient().glassPaneFadeIn(IDPGlassPane.IDPDialogs.MessageDialog,"Error",
							"Couldn't upload file to the server",
							IDPMessageDlg.messageType.ERROR_MESSAGE);
					ioexc.printStackTrace();
					return;
				}
				
				String msg = "File has been uploaded succefully!";
				if (files.length > 1) msg = "Files have been uploaded succefully!";
				
				IDPClientSingleton.getIDPClient().glassPaneFadeIn(IDPGlassPane.IDPDialogs.MessageDialog,"Information",
						msg, IDPMessageDlg.messageType.INFORMATION_MESSAGE);
			}
		}

	}
	//////////////////////////////////////////////////////////////////////
	
	/**
	 * Menu action listener. Shows open file dialog and uploads file to server if chosen.
	 * 
	 */
	private class MenuListenerDeleteFile implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			MessagingInterface connection = IDPConnection.getConnection();
			try
			{
				// TODO : messages - file\dir
				if (JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this file(s)?", "Deleting", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
				{
					Integer result = connection.deleteFile(relativePath);
					if (result == 0)
					{
						IDPClientSingleton.getIDPClient().glassPaneFadeIn(IDPGlassPane.IDPDialogs.MessageDialog,"Information",
								"File has been deleted successfully!",
								IDPMessageDlg.messageType.INFORMATION_MESSAGE);
			
						SandBoxClient client = IDPClientSingleton.getIDPClient();
						client.refreashTree();
					}
					{
						if (result == 1)
						{
							// files don't exist
						}

						IDPClientSingleton.getIDPClient().glassPaneFadeIn(IDPGlassPane.IDPDialogs.MessageDialog,"Error",
								"Couldn't delete file(s)",
								IDPMessageDlg.messageType.INFORMATION_MESSAGE);
					}
				}
			}
			catch(RemoteException re)
			{
				re.printStackTrace();
			}
		}

	}
	//////////////////////////////////////////////////////////////////////	
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3)
		{
			TreePath selected = getSelectionPath();
			Rectangle rect = getPathBounds(selected);
			if (selected != null && rect.contains(e.getX(), e.getY()))
			{
				System.out.println("upload to: "+selected);
				DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)selected.getLastPathComponent();
				relativePath = "";
				while (selected != null)
				{
					treeNode = (DefaultMutableTreeNode)selected.getLastPathComponent();
					String pathComponent = treeNode.toString();
					if (pathComponent.compareTo("Project") == 0) break;
					if (pathComponent.compareTo("example_1") == 0) break;
					relativePath = "/" + pathComponent + relativePath;
					selected = selected.getParentPath();
				}
				
				popup.show(this, e.getX(), e.getY());
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}	
}
