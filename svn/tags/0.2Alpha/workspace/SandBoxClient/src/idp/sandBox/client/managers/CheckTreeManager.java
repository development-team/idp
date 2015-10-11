package idp.sandBox.client.managers;

import idp.sandBox.client.gui.Offset;
import idp.sandBox.client.gui.components.trees.CheckTreeCellRenderer;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;

public class CheckTreeManager extends MouseAdapter implements TreeSelectionListener, MouseListener {
	
	private DefaultTreeSelectionModel selectionModel;
	private JTree tree = new JTree();
	int hotspot = new JCheckBox().getPreferredSize().width;
	private CheckTreeCellRenderer checkTreeCellRenderer;

	private HashMap<String, Color> tmp_color_scheme = new HashMap<String, Color>();
	
	/**
	 * Default constructor
	 * @param tree
	 */
	public CheckTreeManager(JTree tree) {
		this.tree = tree;
		//selectionModel = new CheckTreeSelectionModel(tree.getModel());
		selectionModel = new DefaultTreeSelectionModel();
		checkTreeCellRenderer = new CheckTreeCellRenderer(tree.getCellRenderer(), selectionModel);
		tree.setCellRenderer(checkTreeCellRenderer);
		
		tree.addMouseListener(this);
		selectionModel.addTreeSelectionListener(this);
	}

	public void mouseClicked(MouseEvent me) {
		TreePath path = tree.getPathForLocation(me.getX(), me.getY());
		if (path == null)
			return;
		if (me.getX() > tree.getPathBounds(path).x + hotspot) {
			// expand/collapse
			/*if (SwingUtilities.isLeftMouseButton(me) && me.getClickCount() == 2){
				tree.collapsePath(path);
			} else {
				tree.expandPath(path);
			}*/
			return;
		}

		boolean selected = selectionModel.isPathSelected(path);
		selectionModel.removeTreeSelectionListener(this);

		try {
			if (selected)
				selectionModel.removeSelectionPath(path);
			else
				selectionModel.addSelectionPath(path);
		} finally {
			selectionModel.addTreeSelectionListener(this);
			tree.treeDidChange();
		}
	}

	public DefaultTreeSelectionModel getSelectionModel() {
		return selectionModel;
	}

	public void valueChanged(TreeSelectionEvent e) {
		tree.treeDidChange();
	}
	
	public void setCurrentScheme(HashMap<String, Offset> scheme)
	{
		checkTreeCellRenderer.setCurrentScheme(scheme);
	}
	
	public Color getTagColor(String tag_name)
	{
		return checkTreeCellRenderer.getTagColor(tag_name);
	}
	
	public void updateTagColor(String tag_name, Color newColor)
	{
		checkTreeCellRenderer.updateTagColor(tag_name, newColor);
	}
	
	public void addTagColor(String tag_name, Color newColor)
	{
		if (tmp_color_scheme.containsKey(tag_name) == false)
		{
			Color oldColor = checkTreeCellRenderer.getTagColor(tag_name);
			tmp_color_scheme.put(tag_name, oldColor);
		}
		updateTagColor(tag_name, newColor);		
	}
	
	public void Apply()
	{
		tmp_color_scheme.clear();
		checkTreeCellRenderer.saveColorScheme();
	}
	
	public void Reset()
	{
		Set<String> keys = tmp_color_scheme.keySet();
		for (String key : keys)
		{
			Color oldColor = tmp_color_scheme.get(key);
			updateTagColor(key, oldColor);
		}
		tmp_color_scheme.clear();
	}
}
