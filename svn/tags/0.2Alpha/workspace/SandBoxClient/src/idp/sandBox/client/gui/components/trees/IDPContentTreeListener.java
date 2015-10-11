package idp.sandBox.client.gui.components.trees;

import idp.sandBox.client.gui.SandBoxClient;
import idp.sandBox.client.managers.IDPClientSingleton;
import idp.sandBox.client.managers.IDPWindowManager;
import idp.sandBox.models.TextFileInfo;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;

public class IDPContentTreeListener implements javax.swing.event.TreeSelectionListener {
	@Override
	public void valueChanged(TreeSelectionEvent e) {
		JTree tree = (JTree) e.getSource();
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
				.getLastSelectedPathComponent();

		if (node == null)
			return;
		Object nodeInfo = node.getUserObject();
		
		SandBoxClient client = IDPClientSingleton.getIDPClient();
		
		IDPWindowManager.getJButtonSaveChanges().setEnabled(Boolean.FALSE);
		
		if (node.isLeaf()) {
			TextFileInfo textFileInfo = (TextFileInfo) nodeInfo;
			
			if (client.openView(textFileInfo) == true) {
			}
		} else {
			//IDPWindowManager.getJButtonSaveXML().setEnabled(Boolean.FALSE);
		}
		
		
	}

}
