package idp.sandBox.client.gui.components.trees;

import idp.sandBox.client.constants.Constants;
import idp.sandBox.client.gui.Offset;
import idp.sandBox.client.managers.IDPSettingsManager;
import idp.sandBox.client.managers.IDPWindowManager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

public class CheckTreeCellRenderer extends JPanel implements TreeCellRenderer {
	private DefaultTreeSelectionModel selectionModel;
	private TreeCellRenderer delegate;

	private JCheckBox checkBox = new JCheckBox();
	private JLabel color_mark = new JLabel();
	private HashMap<String, Color> color_scheme = new HashMap<String, Color>();
	private HashMap<String, Offset> tag_scheme = null;
	
	/**
	 * Overriden tree cell renderer  
	 * @param delegate
	 * @param selectionModel
	 */
	public CheckTreeCellRenderer(TreeCellRenderer delegate,
			DefaultTreeSelectionModel selectionModel) {
		this.delegate = delegate;
		this.selectionModel = selectionModel;
		setLayout(new BorderLayout());
		setOpaque(false);
		//setOpaque(true);
		checkBox.setOpaque(false);
		color_mark.setOpaque(true);
		color_mark.setText("  ");
		
		/////////////////////////////////////////////////////////////////////////////////
		// load properties file - we are taking color scheme from it
		// then get and save colors from it
		Properties properties = IDPSettingsManager.getProperties();

		Enumeration names = properties.propertyNames();
		String tag_name = "";
		String s_tag_color = "";
		for (;names.hasMoreElements();)
		{
			tag_name = (String)names.nextElement();
			s_tag_color = (String)properties.get(tag_name);
			color_scheme.put(tag_name, StringToColor(s_tag_color));
		}
		/////////////////////////////////////////////////////////////////////////////////		
	}
	/////////////////////////////////////////////////////////////////////////////////////
	
	public void setCurrentScheme(HashMap<String, Offset> scheme)
	{
		this.tag_scheme = scheme;
	}
	
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		Component renderer = delegate.getTreeCellRendererComponent(tree, value,
				selected, expanded, leaf, row, hasFocus);

		////////////////////////////////////////////////////////////////////////
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		String tag_name = "<unknown>";
		if (node != null)
		{
			tag_name = node.getUserObject().toString();
			tag_name = tag_name.replace(":", "_");
		}
		////////////////////////////////////////////////////////////////////////
		
		////////////////////////////////////////////////////////////////////////
		Color tag_color = color_scheme.get(tag_name);
		if (tag_color == null)
		{
			int color_index = (int)(Math.random() * 10);
			while (color_index > 6) color_index = (int)(Math.random() * 10);
			//String s_tag_color = Constants.defaultColor;
			System.out.println("Index: " + Integer.toString(color_index));
			String s_tag_color = Constants.favouriteColors[color_index];
			tag_color = StringToColor(s_tag_color);
			color_scheme.put(tag_name, tag_color);
		}
		////////////////////////////////////////////////////////////////////////
		
		color_mark.setBackground(tag_color);
		
		if (tag_scheme != null)
		{
			tag_name = tag_name.replace("i_", "i:");
			Offset offset = tag_scheme.get(tag_name);
			if (offset == null) 
			{
				color_mark.setText("x");
				if (selected == true) IDPWindowManager.getJToolBarButtonNewMark().setEnabled(true);
			}
			else
			{
				color_mark.setText("  ");
				if (selected == true) IDPWindowManager.getJToolBarButtonNewMark().setEnabled(false);
			}
		}
		
		TreePath path = tree.getPathForRow(row);
		if (path != null) {
			if (selectionModel.isPathSelected(path))
				checkBox.setSelected(Boolean.TRUE);
			else
				//checkBox.setSelected(selectionModel.isPartiallySelected(path) ? Boolean.TRUE : Boolean.FALSE);
				checkBox.setSelected(Boolean.FALSE);
		}
		removeAll();
		
		add(color_mark, BorderLayout.WEST);
		add(checkBox, BorderLayout.CENTER);
		add(renderer, BorderLayout.EAST);
		
		return this;
	}
	
	private static class CheckBoxTreeNode {
	    private String text;
	    private boolean selected;

	    public CheckBoxTreeNode(String text, boolean selected) {
	        this.text = text;
	        this.selected = selected;
	    }

	    public boolean isSelected() {
	        return selected;
	    }

	    public void setSelected(boolean selected) {
	        this.selected = selected;
	    }

	    public String getText() {
	        return text;
	    }

	    public void setText(String text) {
	        this.text = text;
	    }
	    
	}
	
	private Color StringToColor(String sColor)
	{
		////////////////////////////////////////////////////////////////////////
		// convert from hex (presented as string) to RGB
		int r = 0;
		int g = 0;
		int b = 0;
		try {
			String strr = sColor.substring(0, 2);
			String strg = sColor.substring(2, 4);
			String strb = sColor.substring(4, 6);
			r = Integer.valueOf(strr, 16);
			g = Integer.valueOf(strg, 16);
			b = Integer.valueOf(strb, 16);
		} catch (IndexOutOfBoundsException ex) {
			r = 255;
		} catch (NumberFormatException ex) {
			r = 255;
		}
		/////////////////////////////////////////////////////////////////////////
		
		return new Color(r,g,b);
	}
	
	/**
	 * Update current tag color in the color scheme.
	 * @param tag_name - tag's name which color should be changed 
	 * @param newColor - new color value
	 */
	public void updateTagColor(String tag_name, Color newColor)
	{
		color_scheme.put(tag_name, newColor);
		repaint();
	}
	
	/**
	 * Get current color for specific tag. If color is undefined then return default color
	 * @param tag_name - tag's name  
	 * @return Color - defined or default tag highlighting color
	 */
	public Color getTagColor(String tag_name)
	{
		Color result = color_scheme.get(tag_name);
		if (result == null)
		{
			String s_default_color = Constants.defaultColor;
			result = StringToColor(s_default_color);
		}
		return result;
	}
	
	/**
	 * Convert Java Color to hex String.
	 * @param color - color to be converted
	 * @return String
	 */
	private String ColorToHexString(Color color)
	{
		return Integer.toHexString(color.getRGB() & 0x00ffffff);
	}
	
	/**
	 * Save current color scheme in properties file.
	 */
	public void saveColorScheme()
	{
			Set<String> keys = color_scheme.keySet();
			
			for (String key : keys)
			{
				Color newColor = color_scheme.get(key);
				IDPSettingsManager.setProperty(key, ColorToHexString(newColor));
			}
			
			try
			{
				IDPSettingsManager.store(Constants.propertiesFileName);
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
	}
}


