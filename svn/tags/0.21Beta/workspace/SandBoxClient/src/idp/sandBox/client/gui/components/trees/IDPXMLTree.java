package idp.sandBox.client.gui.components.trees;

import idp.sandBox.client.constants.Constants;
import idp.sandBox.client.gui.Offset;
import idp.sandBox.client.gui.SandBoxClient;
import idp.sandBox.client.gui.components.IDPGlassPane;
import idp.sandBox.client.managers.CheckTreeManager;
import idp.sandBox.client.managers.IDPClientSingleton;
import idp.sandBox.client.managers.IDPConnection;
import idp.sandBox.client.managers.IDPWindowManager;
import idp.sandBox.server.MessagingInterface;

import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.log4j.Logger;

public class IDPXMLTree extends JTree implements MouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8308769211661315758L;

	private static Logger log = Logger.getLogger(IDPXMLTree.class);

	private DefaultMutableTreeNode xmlTreeRoot;

	private JTextPane curTextPane;
	private String currentXML;

	private String xmlns_prefix = null; // namespace prefix PLUS ":"
	private String xmlns_url = "foo";

	/**
	 * Structure of XML tree. Is being received from server.
	 */
	static private String xmlTreeStructure = null;;

	// ////////////////////////////////////////////////////////////////////
	// pop up menu appearing on the nodes in XML Tree if there are no
	// corresponding tags
	private JPopupMenu popup_XMLTree;
	private JMenuItem menuItem_NewMark;
	private JMenuItem menuItem_NewTag;
	private JMenuItem menuItem_DeleteTag;
	private JMenuItem menuItem_ChangeColor;
	private DefaultMutableTreeNode popup_tree_node;

	/**
	 * Public constructor
	 */
	public IDPXMLTree(DefaultMutableTreeNode xmlTreeRoot) {
		super(xmlTreeRoot);
		this.xmlTreeRoot = xmlTreeRoot;

		// for pop up menu
		addMouseListener(this);

		// ////////////////////////////////////////////////////////////////
		// only one element is allowed to be selected in the JTree component
		getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		// ////////////////////////////////////////////////////////////////

		// ////////////////////////////////////////////////////////////////
		// set icons

		Icon closedIcon = new ImageIcon();
		Icon openIcon = new ImageIcon();
		// Icon leafIcon = new ImageIcon();

		URL imageURL = SandBoxClient.class.getResource(Constants.resourcePath
				+ "/closed.jpg");
		if (imageURL != null) {
			closedIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
					imageURL));
		}

		imageURL = SandBoxClient.class.getResource(Constants.resourcePath
				+ "/opened.jpg");
		if (imageURL != null) {
			openIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
					imageURL));
		}

		/*
		 * imageURL = SandBoxSessionFrame.class
		 * .getResource(Constants.resourcePath + "/right.jpg"); if (imageURL !=
		 * null) { leafIcon = new
		 * ImageIcon(Toolkit.getDefaultToolkit().getImage( imageURL)); }
		 */
		// ////////////////////////////////////////////////////////////
		DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) getCellRenderer();
		renderer.setClosedIcon(closedIcon);
		// renderer.setLeafIcon(leafIcon);
		renderer.setOpenIcon(openIcon);

		// renderer.setClosedIcon(null);
		renderer.setLeafIcon(null);
		// renderer.setOpenIcon(null);
		// ////////////////////////////////////////////////////////////////

		// ////////////////////////////////////////////////////////////////
		// popup menu
		popup_XMLTree = new JPopupMenu();
		menuItem_NewMark = new JMenuItem("Colour the text");
		menuItem_NewTag = new JMenuItem("Add a child tag");
		menuItem_DeleteTag = new JMenuItem("Delete the tag");
		menuItem_ChangeColor = new JMenuItem("Change tag color...");
		popup_XMLTree.add(menuItem_NewMark);
		popup_XMLTree.add(menuItem_NewTag);
		popup_XMLTree.add(menuItem_DeleteTag);
		popup_XMLTree.add(menuItem_ChangeColor);
		menuItem_NewMark.addActionListener(new MenuListener());
		menuItem_NewTag.addActionListener(new MenuListener());
		menuItem_DeleteTag.addActionListener(new MenuListener());
		menuItem_ChangeColor.addActionListener(new MenuListener());
		// ////////////////////////////////////////////////////////////////

		// ////////////////////////////////////////////////////////////////
		popup_XMLTree.add(menuItem_NewTag);
		popup_XMLTree.add(menuItem_DeleteTag);
		popup_XMLTree.add(menuItem_ChangeColor);
		// ////////////////////////////////////////////////////////////////

		// ////////////////////////////////////////////////////////////////
		// load the list of xml tags that is used by trainer for data marking
		// out
		getDataFromServer();
		// ////////////////////////////////////////////////////////////////
	}

	public DefaultMutableTreeNode getXmlTreeRoot() {
		return xmlTreeRoot;
	}

	public void setCurrentPane(JTextPane textPane) {
		this.curTextPane = textPane;
	}

	/**
	 * This method loads XML tree from the server
	 * 
	 */
	private void getDataFromServer() {

		// ////////////////////////////////////////////////////////////////
		// get from server
		try {
			MessagingInterface connection = IDPConnection.getConnection();
			xmlTreeStructure = connection
					.getFileContentByRelativePath("/annotators/tree.xml");
			if (xmlTreeStructure.isEmpty() == true)
				IDPWindowManager.getJToolBarButtonAdd().setEnabled(true);
		} catch (RemoteException e) {
			// e.printStackTrace();
			System.out
					.println("[Warning] Couldn't receive tag structure from server.");
			// we need to activate "Add new tag" button
			IDPWindowManager.getJToolBarButtonAdd().setEnabled(true);
			return;
		}
		// ////////////////////////////////////////////////////////////////

		// ////////////////////////////////////////////////////////////////
		// pass the received xml to JTree component
		IDPClientSingleton.getIDPClient().parseXMLforTree(xmlTreeStructure);
		// ////////////////////////////////////////////////////////////////

		// ////////////////////////////////////////////////////////////////
		// get xml namespace prefix
		xmlns_prefix = getXMLnamespacePrefix(xmlTreeStructure);
		// ////////////////////////////////////////////////////////////////
	}

	// ////////////////////////////////////////////////////////////////////

	/**
	 * get XML namespace prefix
	 */
	String getXMLnamespacePrefix(String currentXML) {
		String prefix = "";

		int index = currentXML.indexOf("xmlns:");

		if (index != -1) {
			index = index + "xmlns:".length();
			int index2 = currentXML.indexOf("=", index);
			prefix = currentXML.substring(index, index2);
			prefix = prefix.trim() + ":";
		} else
			return null;

		return prefix;
	}

	// ////////////////////////////////////////////////////////////////////

	/**
	 * Action for pressing pop up menu above XML tree node
	 */
	public class MenuListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String str = e.getActionCommand();

			// ////////////////////////////////////////////////////////////
			// if we've chosen to mark the text
			if (str.compareTo(menuItem_NewMark.getText()) == 0)
				AskAboutNewMark();
			// ////////////////////////////////////////////////////////////

			// ////////////////////////////////////////////////////////////
			// if we've chosen to add child tag
			else if (str.compareTo(menuItem_NewTag.getText()) == 0)
				AskAboutAddingTag();
			// ////////////////////////////////////////////////////////////

			// ////////////////////////////////////////////////////////////
			// if we've chosen to delete tag
			else if (str.compareTo(menuItem_DeleteTag.getText()) == 0)
				AskAboutDeletingTag();
			// ////////////////////////////////////////////////////////////

			// ////////////////////////////////////////////////////////////
			// if we've chosen to change tag color
			else if (str.compareTo(menuItem_ChangeColor.getText()) == 0)
				showColorChooserDialog();
			// ////////////////////////////////////////////////////////////

		}
	}

	// ////////////////////////////////////////////////////////////////////

	/**
	 * Ask user if he wants to add new mark. If he does then add mouse listener
	 * for current text pane component
	 */
	public void AskAboutNewMark() {
		if (curTextPane == null || popup_tree_node == null)
			return;

		String new_tag_name = popup_tree_node.getUserObject().toString();

		if (JOptionPane.showConfirmDialog(null, "Mark the text for the tag "
				+ new_tag_name + " then confirm the selection.", "New tag",
				JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {

			Cursor customCursor = new Cursor(Cursor.S_RESIZE_CURSOR);
			this.setCursor(customCursor);
			curTextPane.setCursor(customCursor);

			// IDPClientSingleton.getIDPClient().setGlassPaneAlpha(0.0);
			IDPClientSingleton.getIDPClient()
					.setCursorOnGlassPane(customCursor);

			curTextPane.addMouseListener(new MouseAdapter() {
				public void mouseReleased(MouseEvent e) {
					int left = curTextPane.getSelectionStart();
					int right = curTextPane.getSelectionEnd();
					Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
					if (left != right) {
						int result = JOptionPane
								.showConfirmDialog(
										null,
										"Press Yes to confirm\n"
												+ "Press No to retry\n"
												+ "Press Cancel to decline the whole operation",
										"Mark the text",
										JOptionPane.YES_NO_CANCEL_OPTION);

						if (result == JOptionPane.YES_OPTION) {
							applyNewTag();
							curTextPane.setCursor(new Cursor(
									Cursor.DEFAULT_CURSOR));
							setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
							curTextPane.removeMouseListener(this);
							IDPClientSingleton.getIDPClient()
									.setCursorOnGlassPane(defaultCursor);
						} else if (result == JOptionPane.CANCEL_OPTION) {
							curTextPane.setCursor(new Cursor(
									Cursor.DEFAULT_CURSOR));
							setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
							curTextPane.removeMouseListener(this);
							IDPClientSingleton.getIDPClient()
									.setCursorOnGlassPane(defaultCursor);
						}
					}
				}
			});
		}
	}

	// ////////////////////////////////////////////////////////////////////

	/**
	 * after we have confirmed the text marking, we need to put the information
	 * about tag position to hash map and update the current view in the text
	 * pane
	 */
	public void applyNewTag() {
		if (getSelectionPath() != null
				&& getSelectionPath().getLastPathComponent() != null) {
			// ////////////////////////////////////////////////////////////
			// get text
			String tag_name = getSelectionPath().getLastPathComponent()
					.toString();

			// ////////////////////////////////////////////////////////////
			// get text
			char[] str = curTextPane.getText().toCharArray();

			// ////////////////////////////////////////////////////////////
			// set offset
			Offset offset = new Offset(0, 0);
			offset.left = curTextPane.getSelectionStart();
			offset.right = curTextPane.getSelectionEnd();

			// ////////////////////////////////////////////////////////////
			// StyledDocument ignores new line characters so
			// we need to find offset modifiers for both indexes
			int offset_md1 = 0;
			int offset_md2 = 0;
			int len = str.length;
			for (int i = 0; i < offset.left; i++) {
				if (str[i] == '\r')
					offset_md1++;
			}
			for (int i = offset.left; i < offset.right; i++) {
				if (i >= len)
					break;
				if (str[i] == '\r')
					offset_md2++;
			}
			// ////////////////////////////////////////////////////////////

			// ////////////////////////////////////////////////////////////
			// make correction
			offset.left = offset.left + offset_md1;
			offset.right = offset.right + offset_md1 + offset_md2;
			// ////////////////////////////////////////////////////////////

			// ////////////////////////////////////////////////////////////
			// check validity of the new xml
			DefaultMutableTreeNode xmlnode = (DefaultMutableTreeNode) getLastSelectedPathComponent();
			DefaultMutableTreeNode parent_xmlnode = (DefaultMutableTreeNode) xmlnode
					.getParent();
			String parent_tag_name = parent_xmlnode.getUserObject().toString();

			HashMap<String, Offset> tag_scheme = IDPClientSingleton
					.getIDPClient().getTagScheme();
			Offset parent_offset = tag_scheme.get(parent_tag_name);

			boolean bValid = false;
			if (parent_offset != null) {
				if (offset.left >= parent_offset.left
						&& offset.right <= parent_offset.right)
					bValid = true;

				// overlap check for siblings
				bValid = checkOverlapTags(tag_scheme, parent_xmlnode, tag_name,
						offset);

			} else
				bValid = true;
			if (!bValid) {
				JOptionPane
						.showMessageDialog(
								null,
								"The new mark position does not fit the xml tree structure. The action has been canceled.",
								"Info", JOptionPane.INFORMATION_MESSAGE);
				curTextPane.setCaretPosition(0);
				return;
			}
			// ////////////////////////////////////////////////////////////

			// ////////////////////////////////////////////////////////////
			// save in the hash map
			IDPClientSingleton.getIDPClient().updateTagScheme(tag_name, offset);
			updateUI();
			// ////////////////////////////////////////////////////////////

			// ////////////////////////////////////////////////////////////
			// update the current text pane
			IDPClientSingleton.getIDPClient().updatePane();
			// ////////////////////////////////////////////////////////////

			IDPWindowManager.getJButtonSaveChanges().setEnabled(true);
			IDPClientSingleton.getIDPClient().curTabContext.setChanged(true);
		}
	}

	/**
	 * Checks Tags overlaps of current selection with tags in tree.
	 * 
	 * @param tagScheme
	 *            - HashMap contains Offsets of all tags in current tree.
	 * @param parentTagXMLNode
	 *            - parent tree node where current tag have been created.
	 * @param currentTagName
	 *            - tag name of current tag, that been marked
	 * @param currentOffset
	 *            - offset(position in text) of current tag, that been marked
	 * @return - true in case no overlaps detected, fasle otherwise.
	 */
	private boolean checkOverlapTags(HashMap<String, Offset> tagScheme,
			DefaultMutableTreeNode parentTagXMLNode, String currentTagName,
			Offset currentOffset) {
		String childName = null;
		Offset startTag = null;
		Offset endTag = null;
		Offset currentTag = null;
		boolean bValid = true;
		if (parentTagXMLNode.getChildCount() > 0) {
			Enumeration<DefaultMutableTreeNode> children = parentTagXMLNode
					.children();
			while (children.hasMoreElements()) {
				childName = children.nextElement().getUserObject().toString();
				if (!childName.equals(currentTagName)) {
					currentTag = tagScheme.get(childName);
					if (currentOffset.left >= currentTag.left
							&& currentOffset.left <= currentTag.right) {
						startTag = currentTag;
					}
					if (currentOffset.right >= currentTag.left
							&& currentOffset.right <= currentTag.right) {
						endTag = currentTag;
					}
					if (currentOffset.left == currentTag.left
							&& currentOffset.right == currentTag.right) {
						bValid = false;
					}
				}
			}
			if ((startTag == null && endTag != null)
					|| (startTag != null && endTag == null)) {
				bValid = false;
			} else {
				if (startTag != null && endTag != null
						&& !startTag.equals(endTag)) {
					bValid = false;
				}
			}
		}
		return bValid;
	}

	// ////////////////////////////////////////////////////////////////////

	/**
	 * Ask user if he wants to add child tag.
	 */
	public void AskAboutAddingTag() {
		if (popup_tree_node == null) {
			AskAboutCreatingRootTag();
			return;
		}

		String msg = "Enter the name:";
		String title = "Adding child for "
				+ popup_tree_node.getUserObject().toString();
		String new_tag_name = (String) JOptionPane.showInputDialog(null, msg,
				title, JOptionPane.QUESTION_MESSAGE, null, null, "some");

		// if OK was pressed
		if (new_tag_name != null) {
			new_tag_name = ValidateTagName(new_tag_name);
			String closing_tag = new_tag_name.replace("<", "</");
			String parent_tag = popup_tree_node.getUserObject().toString();
			parent_tag = parent_tag.substring(0, parent_tag.length() - 1);
			int index = xmlTreeStructure.indexOf(parent_tag);
			if (index != -1) {
				int index2 = xmlTreeStructure.indexOf(">", index);
				// ////////////////////////////////////////////////////////
				// modify parent tag - it may contain namespace prefix
				if (xmlns_prefix != null) {
					int index3 = xmlTreeStructure.indexOf(" xmlns:"
							+ xmlns_prefix + "=\"" + xmlns_url + "\"", index);

					if (index3 != -1 && index3 > index && index3 < index2) {
						parent_tag = parent_tag + " xmlns:" + xmlns_prefix
								+ "=\"" + xmlns_url + "\"";
					}
				}
				parent_tag = xmlTreeStructure.substring(index, index2) + ">";
				// ////////////////////////////////////////////////////////

				xmlTreeStructure = xmlTreeStructure.replace(parent_tag,
						parent_tag + new_tag_name + closing_tag);
				IDPClientSingleton.getIDPClient().updateGlobalTagList(
						xmlTreeStructure);
				// add new node to the tree
				popup_tree_node.insert(
						new DefaultMutableTreeNode(new_tag_name), 0);
				IDPWindowManager.getJToolBarButtonSave().setEnabled(true);
				updateUI();
				log.info("done adding node " + new_tag_name + " to "
						+ popup_tree_node.getUserObject());
			}
		}
	}

	// ////////////////////////////////////////////////////////////////////

	/**
	 * Ask user if he wants to create root tag.
	 */
	public void AskAboutCreatingRootTag() {
		String msg = "Enter a root tag name:";
		String title = "Creating root element";
		String new_tag_name = (String) JOptionPane.showInputDialog(null, msg,
				title, JOptionPane.QUESTION_MESSAGE, null, null, "doc");

		// if OK was pressed
		if (new_tag_name != null) {
			msg = "Enter namespace prefix (e.g. \"i\" for \"i:root\"):";
			title = "Namespace prefix";
			xmlns_prefix = (String) JOptionPane.showInputDialog(null, msg,
					title, JOptionPane.QUESTION_MESSAGE, null, null, "i");

			// if OK was pressed
			if (xmlns_prefix != null) {
				xmlns_prefix += ":";
				String closing_tag = "</" + xmlns_prefix + new_tag_name + ">";
				xmlTreeStructure = "<" + xmlns_prefix + new_tag_name
						+ " xmlns:" + xmlns_prefix + "=\"foo\"" + ">"
						+ closing_tag;
				IDPClientSingleton.getIDPClient().parseXMLforTree(
						xmlTreeStructure);
				IDPWindowManager.getJToolBarButtonSave().setEnabled(true);
				IDPWindowManager.getJToolBarButtonAdd().setEnabled(false);
				updateUI();
			}
		}
	}

	// ////////////////////////////////////////////////////////////////////

	/**
	 * Ask user if he wants to delete current tag.
	 */
	public void AskAboutDeletingTag() {
		if (popup_tree_node == null)
			return;

		String tag_name = popup_tree_node.getUserObject().toString();
		if (JOptionPane.showConfirmDialog(null,
				"Are you sure you want to remove " + tag_name
						+ " and all its children tags (if exist)?", "Delete",
				JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
			String closing_tag_name = tag_name.replace("<", "</");
			tag_name = tag_name.substring(0, tag_name.length() - 1);

			// ////////////////////////////////////////////////////////////
			// remove
			if (popup_tree_node.isLeaf() == true) // simple remove one leaf
			{
				int index = xmlTreeStructure.indexOf(tag_name);
				xmlTreeStructure = xmlTreeStructure.replace(tag_name, "");
				int index2 = xmlTreeStructure.indexOf(">", index);
				if (index == 0)
					xmlTreeStructure = xmlTreeStructure.substring(index2,
							xmlTreeStructure.length());
				else
					xmlTreeStructure = xmlTreeStructure.substring(0, index - 1)
							+ xmlTreeStructure.substring(index2,
									xmlTreeStructure.length());
				xmlTreeStructure = xmlTreeStructure.replace(closing_tag_name,
						"");
			} else // remove also all inner nodes (children)
			{
				int index1 = xmlTreeStructure.indexOf(tag_name);
				int index2 = xmlTreeStructure.indexOf(closing_tag_name);
				if (index1 != -1 && index2 != -1) {
					xmlTreeStructure = xmlTreeStructure.substring(0, index1)
							+ xmlTreeStructure.substring(index2
									+ closing_tag_name.length());
				}
			}
			// remove tag from JTree
			popup_tree_node.removeFromParent();
			// ////////////////////////////////////////////////////////////

			// ////////////////////////////////////////////////////////////
			// rebuild tree
			IDPClientSingleton.getIDPClient().updateGlobalTagList(
					xmlTreeStructure);
			IDPWindowManager.getJToolBarButtonSave().setEnabled(true);
			updateUI();
			// ////////////////////////////////////////////////////////////
		}
	}

	// ////////////////////////////////////////////////////////////////////

	/**
	 * Show JColorChooser dialog
	 */
	public void showColorChooserDialog() {
		if (popup_tree_node == null)
			return;

		String tag_name = popup_tree_node.getUserObject().toString();
		tag_name = tag_name.replace(":", "_");
		CheckTreeManager checkTreeManager = IDPWindowManager
				.getCheckTreeManager();
		if (checkTreeManager == null) {
			System.out
					.println("[Error]: Couldn't get xml tree's CheckTreeManager.");
			return;
		}

		/*
		 * Color oldColor = checkTreeManager.getTagColor(tag_name); Color
		 * newColor =
		 * JColorChooser.showDialog(IDPClientSingleton.getIDPClient(), "Choose
		 * Tag Highlighting Color", oldColor); if (newColor != null) {
		 * checkTreeManager.updateTagColor(tag_name, newColor);
		 * IDPClientSingleton.getIDPClient().updatePane(); }
		 */

		// replace by another listener
		IDPWindowManager.removeTreeSelectionListener();

		// fadeIn
		SandBoxClient client = IDPClientSingleton.getIDPClient();
		client.glassPaneFadeIn(IDPGlassPane.IDPDialogs.ColorChooser);
	}

	/**
	 * Check and update tag name
	 */
	private String ValidateTagName(String tag_name) {
		tag_name = tag_name.replace("<", "");
		tag_name = tag_name.replace(">", "");
		// add namespace.
		tag_name = "<" + xmlns_prefix + tag_name + ">";
		return tag_name;
	}

	// ////////////////////////////////////////////////////////////////////

	/**
	 * Setter for xmlns_prefix
	 */
	public void setXmlns_prefix(String xmlns_prefix) {
		this.xmlns_prefix = xmlns_prefix;
	}

	// ////////////////////////////////////////////////////////////////////

	/**
	 * Setter for xmlns_url
	 */
	public void setXmlns_url(String xmlns_url) {
		this.xmlns_url = xmlns_url;
	}

	// ////////////////////////////////////////////////////////////////////

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	/**
	 * Shows pop up only if 1. we pressed right mouse button</br> 2. it has happened
	 * above jXMLTree node</br> 3. there is no such tag in the text (so we need this
	 * pop up to create tags)
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3) {
			TreePath selected = getSelectionPath();
			Rectangle rect = getPathBounds(selected);

			if (selected != null && rect != null
					&& rect.contains(e.getX(), e.getY())) {
				popup_tree_node = (DefaultMutableTreeNode) selected
						.getLastPathComponent();
				if (popup_tree_node != null) {
					String tag_name = popup_tree_node.toString().replace(">",
							"");

					// ////////////////////////////////////////////////////
					int index = -1;
					if (currentXML == null)
						index = 0;
					else
						index = currentXML.indexOf(tag_name);
					// ////////////////////////////////////////////////////

					// ////////////////////////////////////////////////////
					// popup_XMLTree.removeAll();
					// ////////////////////////////////////////////////////

					// ////////////////////////////////////////////////////
					if (index == -1) {
						popup_XMLTree.add(menuItem_NewMark);
					} else {
						popup_XMLTree.remove(menuItem_NewMark);
					}
					// ////////////////////////////////////////////////////

					// ////////////////////////////////////////////////////
					popup_XMLTree.show(e.getComponent(), e.getX(), e.getY());
					// ////////////////////////////////////////////////////
				}
			}
		}
	}

	// ////////////////////////////////////////////////////////////////////

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	public void setCurrentXML(String currentXML) {
		this.currentXML = currentXML;
	}

	public void updateSelectedTreeNode() {
		TreePath selected = getSelectionPath();
		if (selected != null)
			this.popup_tree_node = (DefaultMutableTreeNode) selected
					.getLastPathComponent();
	}

	public DefaultMutableTreeNode getTreeNode() {
		updateSelectedTreeNode();
		return popup_tree_node;
	}

	/**
	 * Send current XML tree to the server for saving.
	 */
	public void sendXMLTree() {
		// ////////////////////////////////////////////////////////////////
		// send
		try {
			MessagingInterface connection = IDPConnection.getConnection();
			connection.sendXMLTreeFile(xmlTreeStructure);
		} catch (RemoteException e) {
			e.printStackTrace();
			return;
		}
		// ////////////////////////////////////////////////////////////////
	}

	public void initTree() {
		getDataFromServer();
		updateUI();
	}
}
