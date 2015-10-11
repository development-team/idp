package idp.sandBox.client.gui.components.trees;

import idp.sandBox.models.TextFileInfo;

import java.awt.Component;
import java.awt.Image;
import java.awt.MediaTracker;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

public class ContentTreeRenderer extends DefaultTreeCellRenderer {
	
	public Component getTreeCellRendererComponent(
			JTree tree,
            Object value,
            boolean sel,
            boolean expanded,
            boolean leaf,
            int row,
            boolean hasFocus) {

		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

		if (leaf)
		{
			Icon iconFolder = getDefaultClosedIcon();

			DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
			
			Object nodeInfo = node.getUserObject();
			
			if (nodeInfo instanceof TextFileInfo)
			{
				TextFileInfo info = (TextFileInfo)nodeInfo;
				if (info.isDirectory() == true) setIcon(iconFolder);
			}
			else setIcon(iconFolder);
		}
		
		return this;
}
   
	
}
