package idp.sandBox.client.gui.components.trees;

import idp.sandBox.client.gui.SandBoxClient;
import idp.sandBox.client.managers.IDPClientSingleton;
import idp.sandBox.client.managers.IDPWindowManager;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

public class IDPXMLTreeListener implements TreeSelectionListener {

	@Override
	public void valueChanged(TreeSelectionEvent arg0) {
		
		SandBoxClient client = IDPClientSingleton.getIDPClient();
		client.updatePane();
		
		//////////////////////////////////////////////////////////////////
		// Enable (XML Tree) ToolBar buttons
		IDPWindowManager.getJToolBarButtonAdd().setEnabled(true);
		IDPWindowManager.getJToolBarButtonDelete().setEnabled(true);
		IDPWindowManager.getJToolBarButtonColors().setEnabled(true);
		//////////////////////////////////////////////////////////////////
		
		//////////////////////////////////////////////////////////////////
		// Enable "Add new mark" button only if there is no such tag
		DefaultMutableTreeNode treeNode = IDPWindowManager.getJXMLTree().getTreeNode();
		if (treeNode != null)
		{
			String tag_name = treeNode.toString().replace(">", "");
			
			String currentXML = IDPClientSingleton.getIDPClient().getCurrentXML();
			IDPWindowManager.getJToolBarButtonNewMark().setEnabled(false);
			
			//////////////////////////////////////////////////////////////
			if (currentXML != null)
			{
				int index = currentXML.indexOf(tag_name);
				if (index == -1)
					IDPWindowManager.getJToolBarButtonNewMark().setEnabled(true);
			}
			//////////////////////////////////////////////////////////////
		}
		//////////////////////////////////////////////////////////////////
	}

}
