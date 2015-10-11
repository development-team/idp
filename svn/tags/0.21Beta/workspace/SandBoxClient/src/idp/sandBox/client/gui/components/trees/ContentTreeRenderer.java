package idp.sandBox.client.gui.components.trees;

import idp.sandBox.client.constants.Constants;
import idp.sandBox.client.gui.SandBoxClient;
import idp.sandBox.client.managers.IDPConnection;
import idp.sandBox.models.TextFileInfo;
import idp.sandBox.server.MessagingInterface;

import java.awt.Component;
import java.awt.Toolkit;
import java.io.File;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Properties;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.apache.log4j.Logger;

public class ContentTreeRenderer extends DefaultTreeCellRenderer {

	private static final long serialVersionUID = 6428659564545439012L;
	private static final Logger log = Logger
			.getLogger(ContentTreeRenderer.class);
	MessagingInterface connection = IDPConnection.getConnection();

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {

		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
				row, hasFocus);

		if (connection != null) {
			try {
				Properties p = connection.getServerProperties();
				String separator = connection.getPropertiesPathSeparator();
				boolean needToChangeSeparator = !separator.equals(File.separator);
				applySpecialFolderIcons(leaf, value, p, needToChangeSeparator, separator, File.separator);
				// applyDefaultFoldersIcons(leaf, value);
			} catch (RemoteException e) {
				log.error(e.getMessage());
				applyDefaultFoldersIcons(leaf, value);
			}
		} else {
			applyDefaultFoldersIcons(leaf, value);
		}
		return this;
	}

	private void applySpecialFolderIcons(boolean leaf, Object value,
			Properties properties, boolean needTochangeSeparator, String propertiesSeparator, String FSSeparator) {
		Icon iconFolder = getDefaultClosedIcon();
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		Object nodeInfo = node.getUserObject();
		String properPath = null;
		if (nodeInfo instanceof TextFileInfo) {
			TextFileInfo info = (TextFileInfo) nodeInfo;
			if (info.isDirectory()) {
				// log.info("TextFileInfo " + info);
				properPath= info.getRelativePath();
				if (properPath != null
						&& !properPath.equals("")) {
					if (needTochangeSeparator) {
						properPath = properPath.replace(FSSeparator, propertiesSeparator);
					}
					String folderProperty = findProperty(properties, properPath);
					if (folderProperty != null && !folderProperty.equals("")) {
						URL imageURL = SandBoxClient.class
								.getResource(Constants.resourcePath
										+ "/" + folderProperty + ".png");
						if (imageURL != null) {
							iconFolder = new ImageIcon(Toolkit
									.getDefaultToolkit().getImage(imageURL));
						} else {
							log.error("Icon is not found " + folderProperty);
						}
					} else {
						log.error("Property is not found "
								+ properPath);
					}

				} else {
					log.error("No relative path found! " + info);
				}
				setIcon(iconFolder);
			}
			// otherwise go in default way
		}
	}

	private String findProperty(Properties props, String value) {
		String res = null;
		if (value != null && !value.trim().equals("") && props != null
				&& props.containsValue(value)) {
			Set<Object> keys = props.keySet();
			String valueTemp = null;
			String keyString = "";
			for (Object key : keys) {
				keyString = key.toString();
				valueTemp = props.getProperty(keyString);
				if (valueTemp != null && !valueTemp.trim().equals("") && value.endsWith(valueTemp)) {
					res = keyString;
					break;
				}
			}
		}
		return res;
	}

	private void applyDefaultFoldersIcons(boolean leaf, Object value) {
		if (leaf) {
			Icon iconFolder = getDefaultClosedIcon();

			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

			Object nodeInfo = node.getUserObject();

			if (nodeInfo instanceof TextFileInfo) {
				TextFileInfo info = (TextFileInfo) nodeInfo;
				if (info.isDirectory())
					setIcon(iconFolder);
			} else
				setIcon(iconFolder);
		}
	}

}
