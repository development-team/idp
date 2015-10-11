package idp.sandBox.client.managers;

import idp.sandBox.client.constants.Constants;
import idp.sandBox.client.dialogs.IDPMessageDlg;
import idp.sandBox.client.gui.ProgressCounter;
import idp.sandBox.client.gui.SandBoxClient;
import idp.sandBox.client.gui.components.IDPGlassPane;
import idp.sandBox.client.gui.components.IDPMenuListener;
import idp.sandBox.client.gui.components.tabs.CloseEventListener;
import idp.sandBox.client.gui.components.tabs.IDPTabbedPane;
import idp.sandBox.client.gui.components.trees.ContentTreeRenderer;
import idp.sandBox.client.gui.components.trees.IDPContentTree;
import idp.sandBox.client.gui.components.trees.IDPContentTreeListener;
import idp.sandBox.client.gui.components.trees.IDPXMLTree;
import idp.sandBox.client.gui.components.trees.IDPXMLTreeListener;
import idp.sandBox.models.TextFileInfo;
import idp.sandBox.server.MessagingInterface;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URL;
import java.rmi.RemoteException;
import org.apache.log4j.Logger;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ToolTipManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicSplitPaneUI;

public class IDPWindowManager {

	static final boolean withLargeMenu = false;

	/**
	 * Windows and components.
	 */
	static private JPanel jContentPane = null;
	static private JLabel jConnectLabel = null;
	static private JScrollPane jTreeScrollPane = null;
	static private JScrollPane jXMLTreeScrollPane = null;
	static private JPanel jXMLTreePane = null;
	static private IDPTabbedPane jTabbedPane = null;
	static private JLabel jStatusLabel = null;
	static private JProgressBar jProgressBar = null;

	static private JButton jButtonConnect = null;
	static private JButton jButtonTest = null;
	static private JButton jButtonTrain = null;
	static private JButton jButtonApply = null;
	static private JButton jButtonNormalize = null;
	static private JButton jButtonChooseColor = null;
	static private JButton jButtonSaveXML = null;
	static private JButton jButtonProperties = null;
	static private JButton jButtonSettings = null;

	static private JToolBar jToolBar = null;
	static private JToolBar jToolBarLower = null;

	static private Logger log = Logger.getLogger(IDPWindowManager.class);

	/**
	 * Toolbar for JXMLTree
	 */
	static private JToolBar jXMLTreeToolBar = null;

	/**
	 * Change tag colour button. Tool bar button for JXMLTreeToolBar.
	 */
	static private JButton jToolBarButtonColors = null;

	/**
	 * Add new tag button. Tool bar button for JXMLTreeToolBar.
	 */
	static private JButton jToolBarButtonAdd = null;

	/**
	 * Delete tag button. Tool bar button for JXMLTreeToolBar.
	 */
	static private JButton jToolBarButtonDelete = null;

	/**
	 * Delete tag button. Tool bar button for JXMLTreeToolBar.
	 */
	static private JButton jToolBarButtonNewMark = null;

	/**
	 * Save button. Tool bar button for JXMLTreeToolBar.
	 */
	static private JButton jToolBarButtonSave = null;

	/**
	 * Used to save toolbar background colour.
	 */
	static private Color oldColor;

	static private JSplitPane jSplitPane = null;
	static private JSplitPane jInnerSplitPane = null;
	// ////////////////////////////////////////////////////////////////////

	// ////////////////////////////////////////////////////////////////////
	// Trees
	static private IDPContentTree jTree = null;
	static private IDPXMLTree jXMLTree = null;

	static private TreeSelectionListener treeSelectionListener = null;
	// ////////////////////////////////////////////////////////////////////

	/**
	 * Main menu;
	 */
	static private JMenuBar menuBar = null;

	/**
	 * Connect\disconnect menu item
	 */
	static private JMenuItem connectMenuItem = null;

	/**
	 * Choose Colours Dialog menu item
	 */
	static private JMenuItem chooseColorsMenuItem = null;

	/**
	 * Properties Dialog menu item
	 */
	static private JMenuItem propertiesMenuItem = null;

	/**
	 * Close all tabs menu item.
	 */
	static private JMenuItem closeAllTabsMenuItem = null;

	/**
	 * Train menu item
	 */
	static private JMenuItem trainMenuItem = null;

	/**
	 * Apply menu item
	 */
	static private JMenuItem applyMenuItem = null;

	/**
	 * Retrain menu item
	 */
	// static private JMenuItem retrainMenuItem = null;
	/**
	 * Save menu item
	 */
	static private JMenuItem saveMenuItem = null;

	/**
	 * Main menu's listener
	 */
	static private IDPMenuListener menuListener = null;

	/**
	 * JTree manager. Add check box feature to standard JTree component.
	 */
	static private CheckTreeManager checkTreeManager = null;

	/**
	 * Console Scroll Pane
	 */
	static JScrollPane jConsoleView = null;

	/**
	 * Console Window
	 */
	static JTextPane jConsoleContent = null;

	/**
	 * RMI connection to the server.
	 */
	static private MessagingInterface connection = null;

	/**
	 * Class for processing in background.
	 * 
	 * @see ProgressCounter
	 */
	static private ProgressCounter progressCounter;

	static private Image imageConnect;
	static private Image imageDisconnect;

	static private Image imageColors = null;
	static private Image imageSave = null;
	static private Image imageApply = null;
	static private Image imageNormalize = null;
	static private Image imageCorrect = null;
	static private Image imageTrain = null;

	static private Image imageApplyMenu = null;
	static private Image imageTrainMenu = null;
	static private Image imageSaveMenu = null;
	static private Image imageColorsMenu = null;
	static private Image imageConnectMenu = null;
	static private Image imageDisconnectMenu = null;
	static private Image imageSettings = null;
	static private Image imageChooseColors = null;
	static private Image imageProperties = null;
	static private Image imageAboutMenu = null;

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	static public JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJToolBar(), BorderLayout.NORTH);
			jContentPane.add(getJToolBarLower(), BorderLayout.SOUTH);
			jContentPane.add(getJMiddleSplitPane(), BorderLayout.CENTER);
		}
		return jContentPane;
	}

	// ////////////////////////////////////////////////////////////////////

	/**
	 * This method initializes Label with status comments
	 * 
	 * @return javax.swing.JCheckBox
	 */
	static private JLabel getConnectLabel() {
		if (jConnectLabel == null) {
			jConnectLabel = new JLabel();
			jConnectLabel.setText("Not connected");
		}
		return jConnectLabel;
	}

	// ////////////////////////////////////////////////////////////////////

	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	static public JScrollPane getJTreeScrollPane() {
		if (jTreeScrollPane == null) {
			jTreeScrollPane = new JScrollPane();
			// jScrollPane.setViewportView(getJTree());
		}
		return jTreeScrollPane;
	}

	// ////////////////////////////////////////////////////////////////////

	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	static private JScrollPane getJXMLTreeScrollPane() {
		if (jXMLTreeScrollPane == null) {
			jXMLTreeScrollPane = new JScrollPane();

			// ////////////////////////////////////////////////////////////
			// set default size of component
			Dimension preferredSize = new Dimension();
			preferredSize.width = 200;
			preferredSize.height = IDPClientSingleton.getIDPClient()
					.getScreenHeight();
			jXMLTreeScrollPane.setPreferredSize(preferredSize);
			// ////////////////////////////////////////////////////////////
		}
		return jXMLTreeScrollPane;
	}

	// ////////////////////////////////////////////////////////////////////

	/**
	 * This method initializes jTabbedPane
	 * 
	 * @return javax.swing.JTabbedPane
	 */
	static public IDPTabbedPane getJTabbedPane() {
		if (jTabbedPane == null) {
			jTabbedPane = new IDPTabbedPane();

			jTabbedPane
					.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

			jTabbedPane.addChangeListener(new ChangeListener() {
				// This method is called whenever the selected tab changes
				public void stateChanged(ChangeEvent evt) {
					JTabbedPane pane = (JTabbedPane) evt.getSource();

					JScrollPane scrollPane = (JScrollPane) pane
							.getSelectedComponent();

					SandBoxClient client = IDPClientSingleton.getIDPClient();

					client.updateContext(scrollPane);

					jXMLTree.repaint();
					jXMLTree.updateUI();
				}
			});

			jTabbedPane.addCloseEventListener(new CloseEventListener() {
				@Override
				public void closeTab(MouseEvent e, int tabIndex) {
					IDPTabbedPane itp = (IDPTabbedPane) e.getComponent();

					SandBoxClient client = IDPClientSingleton.getIDPClient();

					if (client.isCurContextChanged() == true) {
						int result = JOptionPane.showConfirmDialog(null,
								"Document was changed. Save the changes?",
								"Closing", JOptionPane.YES_NO_CANCEL_OPTION);

						if (result == JOptionPane.YES_OPTION) // save before
						// closing
						{
							client.saveCurrentXML();
						} else if (result == JOptionPane.CANCEL_OPTION) // don't
						// close
						{
							return;
						}
					}

					itp.remove(tabIndex);
					itp.checkAndUnhideTabs();
					itp.updateRectangles();
				}

			});

		}
		return jTabbedPane;
	}

	// ////////////////////////////////////////////////////////////////////

	/**
	 * This method initializes jToolBarLower
	 * 
	 * @return javax.swing.JToolBar
	 */
	static public JToolBar getJToolBarLower() {
		if (jToolBarLower == null) {
			jToolBarLower = new JToolBar();
			jToolBarLower
					.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

			jToolBarLower.setLayout(new java.awt.BorderLayout());
			// jToolBarLower.setFloatable(false);

			jToolBarLower.add(getConnectLabel(), BorderLayout.WEST);
			jToolBarLower.add(getJStatusLabel(), BorderLayout.CENTER);
			jToolBarLower.add(getJProgressBar(), BorderLayout.EAST);
		}
		return jToolBarLower;
	}

	// ////////////////////////////////////////////////////////////////////

	/**
	 * This method initializes Label with status comments
	 * 
	 * @return javax.swing.JLabel
	 */
	static public JLabel getJStatusLabel() {
		if (jStatusLabel == null) {
			jStatusLabel = new JLabel();
			jStatusLabel.setText("");
		}
		return jStatusLabel;
	}

	// ////////////////////////////////////////////////////////////////////

	/**
	 * This method initializes jProgressBar
	 * 
	 * @return javax.swing.JProgressBar
	 */
	static private JProgressBar getJProgressBar() {
		if (jProgressBar == null) {
			jProgressBar = new JProgressBar();
			jProgressBar.setPreferredSize(new Dimension(300, 14));

			jProgressBar.setStringPainted(true);
			jProgressBar.setValue(0);
			jProgressBar.setString("");
			jProgressBar.setMinimum(0);
			jProgressBar.setMaximum(100);

			progressCounter = new ProgressCounter(jProgressBar, jStatusLabel);
		}
		return jProgressBar;
	}

	// ////////////////////////////////////////////////////////////////////

	/**
	 * This method initializes jToolBar
	 * 
	 * @return javax.swing.JToolBar
	 */
	static public JToolBar getJToolBar() {
		if (jToolBar == null) {
			jToolBar = new JToolBar();

			jToolBar
					.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
			jToolBar.setFloatable(false);

			// ////////////////////////////////////////////////////////////
			// Connect button
			jToolBar.add(getJButtonConnect());
			// ////////////////////////////////////////////////////////////

			// ////////////////////////////////////////////////////////////
			// Preferences button
			jToolBar.add(getJButtonSettings());
			// ////////////////////////////////////////////////////////////

			// ////////////////////////////////////////////////////////////
			// Add separator
			jToolBar.addSeparator();
			// ////////////////////////////////////////////////////////////

			// ////////////////////////////////////////////////////////////
			// Train button
			jToolBar.add(getJButtonTrain());
			// ////////////////////////////////////////////////////////////

			// ////////////////////////////////////////////////////////////
			// Apply button
			jToolBar.add(getJButtonApply());
			// ////////////////////////////////////////////////////////////

			// ////////////////////////////////////////////////////////////
			// Normalize button
			jToolBar.add(getJButtonNormalize());
			// ////////////////////////////////////////////////////////////

			// ////////////////////////////////////////////////////////////
			// ReTrain button
			// jToolBar.add(getJButtonReTrain());
			// ////////////////////////////////////////////////////////////

			// ////////////////////////////////////////////////////////////
			// Save XML button
			jToolBar.add(getJButtonSaveChanges());
			// ////////////////////////////////////////////////////////////

			// ////////////////////////////////////////////////////////////
			// Add separator
			jToolBar.addSeparator();
			// ////////////////////////////////////////////////////////////

			// ////////////////////////////////////////////////////////////
			// Choose colors button
			jToolBar.add(getJButtonChooseColors());
			// ////////////////////////////////////////////////////////////

			// ////////////////////////////////////////////////////////////
			// Settings button
			jToolBar.add(getJButtonProperties());
			// ////////////////////////////////////////////////////////////

			// ////////////////////////////////////////////////////////////
			// Exit button
			jToolBar.add(getJButtonTest());
			// ////////////////////////////////////////////////////////////
		}
		return jToolBar;
	}

	// ////////////////////////////////////////////////////////////////////

	/**
	 * This method initializes Connect to RMI server
	 * 
	 * @return javax.swing.JButton
	 */
	static private JButton getJButtonConnect() {
		if (jButtonConnect == null) {

			Rectangle rec = new Rectangle(50, 50);

			// ////////////////////////////////////////////////////////////
			// Load image from resource
			URL imageURL = SandBoxClient.class
					.getResource(Constants.resourcePath + "/Connect.png");

			if (imageURL != null) {
				imageConnect = Toolkit.getDefaultToolkit().getImage(imageURL);
				imageConnect = imageConnect.getScaledInstance(rec.width,
						rec.height, java.awt.Image.SCALE_SMOOTH);
			}
			// ////////////////////////////////////////////////////////////

			// ////////////////////////////////////////////////////////////
			// Load image from resource
			imageURL = SandBoxClient.class.getResource(Constants.resourcePath
					+ "/Disconnect.png");

			if (imageURL != null) {
				imageDisconnect = Toolkit.getDefaultToolkit()
						.getImage(imageURL);
				imageDisconnect = imageDisconnect.getScaledInstance(rec.width,
						rec.height, java.awt.Image.SCALE_SMOOTH);
			}
			// ////////////////////////////////////////////////////////////

			jButtonConnect = new JButton("Connect", new ImageIcon(imageConnect));
			jButtonConnect.setEnabled(true);
			jButtonConnect.setFocusable(false);

			jButtonConnect.setToolTipText("Connect");

			jButtonConnect.setVerticalTextPosition(AbstractButton.BOTTOM);
			jButtonConnect.setHorizontalTextPosition(AbstractButton.CENTER);

			jButtonConnect
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {

							if (connection == null) {
								System.out.println("Connecting...");
								connectToServer();
							} else {
								System.out.println("Disconnecting...");
								disconnectFromServer();
							}
						}
					});
		}

		return jButtonConnect;
	}

	// ////////////////////////////////////////////////////////////////////

	/**
	 * Connect
	 */
	static public void connectToServer() {
		try {
			SandBoxClient client = IDPClientSingleton.getIDPClient();

			// //////////////////////////////////////////////////////
			// get connection
			connection = IDPConnection.getConnection();
			if (connection == null) {
				// JOptionPane.showMessageDialog(null,"Couldn't connect to
				// server!!!", "Connection", JOptionPane.WARNING_MESSAGE);
				client.glassPaneFadeIn(IDPGlassPane.IDPDialogs.MessageDialog,
						"Error", "Couldn't connect to the server",
						IDPMessageDlg.messageType.ERROR_MESSAGE);
				return;
			}
			// //////////////////////////////////////////////////////

			// //////////////////////////////////////////////////////
			// change status bar and print out info
			jConnectLabel.setText("Connected   ");
			System.out.println("Connected" + connection);
			if (jConsoleView != null)
				jConsoleContent.setText("Connected:\n"
						+ IDPConnection.getConnection().toString());
			// //////////////////////////////////////////////////////

			// //////////////////////////////////////////////////////
			// make watermark more translucent
			// turn off water mark
			// /client.setGlassPaneAlpha(0.5);
			client.turnOffWaterMark();
			// //////////////////////////////////////////////////////

			// //////////////////////////////////////////////////////
			// change icon and text
			jButtonConnect.setText("Disconnect");
			jButtonConnect.setIcon(new ImageIcon(imageDisconnect));
			// //////////////////////////////////////////////////////

			// //////////////////////////////////////////////////////
			// change menu item
			connectMenuItem.setText("Disconnect");
			connectMenuItem.setActionCommand("disconnect");
			// //////////////////////////////////////////////////////

			// //////////////////////////////////////////////////////
			// set view port view
			if (jTreeScrollPane != null) {
				jTreeScrollPane.setViewportView(getJTree());
			}
			// //////////////////////////////////////////////////////

			// //////////////////////////////////////////////////////
			// change buttons' accessibility
			// TODO move to open project method
			boolean trainButtonEnabled = false;
			try {
				connection.collectTagNames();
				trainButtonEnabled = true;
			} catch (RemoteException e) {
				trainButtonEnabled = false;
			}
			jButtonTrain.setEnabled(trainButtonEnabled);
			if (connection.isTrainCompleted())
				jButtonApply.setEnabled(true);
			if (connection.isApplied())
				jButtonNormalize.setEnabled(true);
			jButtonChooseColor.setEnabled(true);
			jButtonProperties.setEnabled(true);
			jButtonTest.setEnabled(true);
			// //////////////////////////////////////////////////////

			// //////////////////////////////////////////////////////
			// change menu items accessibility
			chooseColorsMenuItem.setEnabled(true);
			trainMenuItem.setEnabled(true);
			if (connection.isTrainCompleted() == true)
				applyMenuItem.setEnabled(true);
			saveMenuItem.setEnabled(true);
			propertiesMenuItem.setEnabled(true);
			// //////////////////////////////////////////////////////

			// //////////////////////////////////////////////////////
			// load (left) tree from server
			jTree.initTree();
			// //////////////////////////////////////////////////////

			// //////////////////////////////////////////////////////
			// set up right JTree component
			if (jXMLTreeScrollPane != null) {
				jXMLTreeScrollPane.setViewportView(getJXMLTree());
				setEnabled_XMLTreeToolbar(true);
			}
			// //////////////////////////////////////////////////////
		} catch (RemoteException e0) {
			e0.printStackTrace();
		}
	}

	// ////////////////////////////////////////////////////////////////////

	/**
	 * Disconnect
	 */
	static public void disconnectFromServer() {
		// ////////////////////////////////////////////////////////////////
		SandBoxClient client = IDPClientSingleton.getIDPClient();

		// ////////////////////////////////////////////////////////////////
		// get connection
		IDPConnection.disconnect();
		connection = null;
		// ////////////////////////////////////////////////////////////////

		// ////////////////////////////////////////////////////////////////
		// change status bar
		jConnectLabel.setText("Not Connected");
		System.out.println("Disconnected");
		// ////////////////////////////////////////////////////////////////

		// ////////////////////////////////////////////////////////////////
		// make watermark less translucent
		// client.setGlassPaneAlpha(IDPGlassPane.defaultWatermarkAlpha);
		client.turnOnWaterMark();
		// ////////////////////////////////////////////////////////////////

		// ////////////////////////////////////////////////////////////////
		// change icon and text
		jButtonConnect.setText("Connect");
		jButtonConnect.setIcon(new ImageIcon(imageConnect));
		// ////////////////////////////////////////////////////////////////

		// ////////////////////////////////////////////////////////////////
		// change menu item
		connectMenuItem.setText("Connect");
		connectMenuItem.setActionCommand("connect");
		// ////////////////////////////////////////////////////////////////

		// ////////////////////////////////////////////////////////////////
		// change buttons' accessibility
		jButtonTrain.setEnabled(false);
		jButtonApply.setEnabled(false);
		jButtonNormalize.setEnabled(false);
		jButtonChooseColor.setEnabled(false);
		jButtonSaveXML.setEnabled(false);
		jButtonProperties.setEnabled(false);
		jButtonTest.setEnabled(false);
		// ////////////////////////////////////////////////////////////////

		// ////////////////////////////////////////////////////////////////
		// change menu items accessibility
		chooseColorsMenuItem.setEnabled(false);
		trainMenuItem.setEnabled(false);
		applyMenuItem.setEnabled(false);
		saveMenuItem.setEnabled(false);
		propertiesMenuItem.setEnabled(false);
		// ////////////////////////////////////////////////////////////////

		// ////////////////////////////////////////////////////////////////
		// clear
		jTreeScrollPane.setViewport(null);
		// ////////////////////////////////////////////////////////////////

		// ////////////////////////////////////////////////////////////////
		// clear
		jXMLTreeScrollPane.setViewport(null);
		jXMLTree = null;
		setEnabled_XMLTreeToolbar(false);
		// ////////////////////////////////////////////////////////////////

		// ////////////////////////////////////////////////////////////////
		// disable toolbar buttons
		getJToolBarButtonAdd().setEnabled(Boolean.FALSE);
		getJToolBarButtonDelete().setEnabled(Boolean.FALSE);
		getJToolBarButtonColors().setEnabled(Boolean.FALSE);
		getJToolBarButtonNewMark().setEnabled(Boolean.FALSE);
		// ////////////////////////////////////////////////////////////////

		// ////////////////////////////////////////////////////////////////
		// clear
		for (ChangeListener l : jTabbedPane.getChangeListeners()) {
			jTabbedPane.removeChangeListener(l);
		}
		jTabbedPane.removeAll();
		closeAllTabsMenuItem.setEnabled(false);
		jConsoleView = null;
		// ////////////////////////////////////////////////////////////////

		// ////////////////////////////////////////////////////////////////
		jStatusLabel.setText("");
		// ////////////////////////////////////////////////////////////////

		System.out.println("done");
	}

	// ////////////////////////////////////////////////////////////////////

	/**
	 * set jXMLTree as Viewport
	 */
	static public void setViewportView() {
		if (jXMLTreeScrollPane != null) {
			jXMLTreeScrollPane.setViewportView(getJXMLTree());
		}
	}

	// ////////////////////////////////////////////////////////////////////

	/**
	 * This method initializes Train button
	 * 
	 * @return javax.swing.JButton
	 */
	static private JButton getJButtonTrain() {
		if (jButtonTrain == null) {

			Rectangle rec = new Rectangle(50, 50);

			// ////////////////////////////////////////////////////////////
			// Load image from resource
			URL imageURL = SandBoxClient.class
					.getResource(Constants.resourcePath + "/Train.png");

			if (imageURL != null) {
				imageTrain = Toolkit.getDefaultToolkit().getImage(imageURL);
				imageTrain = imageTrain.getScaledInstance(rec.width,
						rec.height, java.awt.Image.SCALE_SMOOTH);
			}
			// ////////////////////////////////////////////////////////////

			jButtonTrain = new JButton("Train", new ImageIcon(imageTrain));
			jButtonTrain.setEnabled(false);
			jButtonTrain.setFocusable(Boolean.FALSE);

			jButtonTrain.setToolTipText("Train");

			jButtonTrain.setVerticalTextPosition(AbstractButton.BOTTOM);
			jButtonTrain.setHorizontalTextPosition(AbstractButton.CENTER);

			jButtonTrain.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					train();
				}
			});
		}
		return jButtonTrain;
	}

	// ////////////////////////////////////////////////////////////////////

	/**
	 * Send train command to server and run progress counter in background
	 */
	// ////////////////////////////////////////////////////////////////////
	static public void train() {
		System.out.println("Sending train command to server");
		jStatusLabel.setText("Sending train command to server");
		try {

			// ///////////////////////////////////////////////////////
			// Console Window
			openConsoleWindow();
			progressCounter.setConsoleWindow(jConsoleContent);
			// ///////////////////////////////////////////////////////

			// ///////////////////////////////////////////////////////
			// going to synchronous mode, all asynchronous work is done by
			// server
			progressCounter.setConnection(connection);
			progressCounter.addActivateButton(jButtonTrain);
			// progressCounter.addActivateButton(jButtonReTrain);
			progressCounter.addActivateButton(jButtonApply);

			try {
				connection.train();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				log.error("Couldn't find train directory. Check your project properties.");
				jStatusLabel
						.setText("Couldn't find train directory. Check your project properties.");
				IDPClientSingleton.getIDPClient().glassPaneFadeIn(
						IDPGlassPane.IDPDialogs.MessageDialog, "Error",
						"Couldn't find train directory.",
						IDPMessageDlg.messageType.ERROR_MESSAGE);
				return;
			}
			// ///////////////////////////////////////////////////////

			// ///////////////////////////////////////////////////////
			// Create thread for train operation and for progress bar state
			// changing
			progressCounter.setOperation("Training");
			Thread threadProgressBar = new Thread(progressCounter);
			threadProgressBar.start();
			// ///////////////////////////////////////////////////////

			System.out
					.println("Data processing on the server. Waiting for completion...");
			jStatusLabel
					.setText("Data processing on the server. Waiting for completion...");

		} catch (RemoteException ex) {
			ex.printStackTrace();
			// JOptionPane.showMessageDialog(null, "Couldn't get respond from
			// the server.\n"+
			// "Try again or press disconnect\\connect.", "Error",
			// JOptionPane.WARNING_MESSAGE);
			IDPClientSingleton.getIDPClient().glassPaneFadeIn(
					IDPGlassPane.IDPDialogs.MessageDialog, "Error",
					"Couldn't get response from the server",
					IDPMessageDlg.messageType.ERROR_MESSAGE);
		} finally {

		}
	}

	/**
	 * This method initialises Apply button
	 * 
	 * @return javax.swing.JButton
	 */
	static private JButton getJButtonApply() {
		if (jButtonApply == null) {

			Rectangle rec = new Rectangle(50, 50);

			// ////////////////////////////////////////////////////////////
			// Load image from resource
			URL imageURL = SandBoxClient.class
					.getResource(Constants.resourcePath + "/Apply.png");

			if (imageURL != null) {
				imageApply = Toolkit.getDefaultToolkit().getImage(imageURL);
				imageApply = imageApply.getScaledInstance(rec.width,
						rec.height, java.awt.Image.SCALE_SMOOTH);
			}
			// ////////////////////////////////////////////////////////////

			jButtonApply = new JButton("Apply", new ImageIcon(imageApply));
			jButtonApply.setEnabled(false);
			jButtonApply.setFocusable(Boolean.FALSE);

			jButtonApply.setToolTipText("Apply");

			jButtonApply.setVerticalTextPosition(AbstractButton.BOTTOM);
			jButtonApply.setHorizontalTextPosition(AbstractButton.CENTER);

			jButtonApply.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					apply();
				}
			});
		}
		return jButtonApply;
	}

	// ////////////////////////////////////////////////////////////////////

	/**
	 * Send apply command to server and run progress counter in background
	 */
	static public void apply() {
		log.info("Sending apply command to server");
		jStatusLabel.setText("Sending apply command to server");
		try {

			// ///////////////////////////////////////////////////////
			// Console Window
			openConsoleWindow();
			progressCounter.setConsoleWindow(jConsoleContent);
			// ///////////////////////////////////////////////////////

			// //////////////////////////////////////////////////////
			// going to synchronous mode, all asynchronous work is done by
			// server
			progressCounter.setConnection(connection);
			progressCounter.addActivateButton(jButtonTrain);
			progressCounter.addActivateButton(jButtonApply);
			progressCounter.addActivateButton(jButtonNormalize);

			try {
				connection.apply();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				log
						.error("Couldn't find apply directory. Check your project properties.");
				jStatusLabel
						.setText("Couldn't find apply directory. Check your project properties.");
				IDPClientSingleton.getIDPClient().glassPaneFadeIn(
						IDPGlassPane.IDPDialogs.MessageDialog, "Error",
						"Couldn't find apply directory.",
						IDPMessageDlg.messageType.ERROR_MESSAGE);
				return;
			}
			// //////////////////////////////////////////////////////

			// //////////////////////////////////////////////////////
			// Create thread for apply operation and for progress bar state
			// changing
			progressCounter.setOperation("Applying");
			Thread threadProgressBar = new Thread(progressCounter);
			threadProgressBar.start();
			// //////////////////////////////////////////////////////

			log
					.info("Data processing on the server. Waiting for completion...");
			jStatusLabel
					.setText("Data processing on the server. Waiting for completion...");

		} catch (RemoteException ex) {
			ex.printStackTrace();
			// JOptionPane.showMessageDialog(null, "Couldn't get respond from
			// the server.\n"+
			// "Try again or press disconnect\\connect.", "Error",
			// JOptionPane.WARNING_MESSAGE);
			IDPClientSingleton.getIDPClient().glassPaneFadeIn(
					IDPGlassPane.IDPDialogs.MessageDialog, "Error",
					"Couldn't get response from the server",
					IDPMessageDlg.messageType.ERROR_MESSAGE);

		} finally {

		}
	}

	/**
	 * This method initialises Normalize button
	 * 
	 * @return javax.swing.JButton
	 */
	static private JButton getJButtonNormalize() {
		if (jButtonNormalize == null) {

			Rectangle rec = new Rectangle(50, 50);

			// ////////////////////////////////////////////////////////////
			// Load image from resource
			URL imageURL = SandBoxClient.class
					.getResource(Constants.resourcePath + "/Normalize.png");

			if (imageURL != null) {
				imageNormalize = Toolkit.getDefaultToolkit().getImage(imageURL);
				imageNormalize = imageNormalize.getScaledInstance(rec.width,
						rec.height, java.awt.Image.SCALE_SMOOTH);
			}
			// ////////////////////////////////////////////////////////////

			jButtonNormalize = new JButton("Normalize", new ImageIcon(
					imageNormalize));
			jButtonNormalize.setEnabled(false);
			jButtonNormalize.setFocusable(Boolean.FALSE);

			jButtonNormalize.setToolTipText("Normailze");

			jButtonNormalize.setVerticalTextPosition(AbstractButton.BOTTOM);
			jButtonNormalize.setHorizontalTextPosition(AbstractButton.CENTER);

			jButtonNormalize
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							normalize();
						}
					});
		}
		return jButtonNormalize;
	}

	/**
	 * Send Normalize command to server and run progress counter in background
	 */
	static public void normalize() {
		log.info("Sending normalize command to server");
		jStatusLabel.setText("Sending normalize command to server");
		try {

			// ///////////////////////////////////////////////////////
			// Console Window
			openConsoleWindow();
			progressCounter.setConsoleWindow(jConsoleContent);
			// ///////////////////////////////////////////////////////

			// //////////////////////////////////////////////////////
			// going to synchronous mode, all asynchronous work is done by
			// server
			progressCounter.setConnection(connection);
			progressCounter.addActivateButton(jButtonTrain);
			progressCounter.addActivateButton(jButtonApply);
			progressCounter.addActivateButton(jButtonNormalize);
			// progressCounter.addActivateButton(jButtonReTrain);

			try {
				connection.normalize();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				log
						.error("Couldn't find applyRes directory. Check your project properties.");
				jStatusLabel
						.setText("Couldn't find applyRes directory. Check your project properties.");
				IDPClientSingleton.getIDPClient().glassPaneFadeIn(
						IDPGlassPane.IDPDialogs.MessageDialog, "Error",
						"Couldn't find applyRes directory.",
						IDPMessageDlg.messageType.ERROR_MESSAGE);
				return;
			}
			// //////////////////////////////////////////////////////

			// //////////////////////////////////////////////////////
			// Create thread for normalize operation and for progress bar state
			// changing
			progressCounter.setOperation("Normalizing");
			Thread threadProgressBar = new Thread(progressCounter);
			threadProgressBar.start();
			// //////////////////////////////////////////////////////

			log
					.info("Data processing on the server. Waiting for completion...");
			jStatusLabel
					.setText("Data processing on the server. Waiting for completion...");

		} catch (RemoteException ex) {
			ex.printStackTrace();
			// JOptionPane.showMessageDialog(null, "Couldn't get respond from
			// the server.\n"+
			// "Try again or press disconnect\\connect.", "Error",
			// JOptionPane.WARNING_MESSAGE);
			IDPClientSingleton.getIDPClient().glassPaneFadeIn(
					IDPGlassPane.IDPDialogs.MessageDialog, "Error",
					"Couldn't get response from the server",
					IDPMessageDlg.messageType.ERROR_MESSAGE);

		} finally {

		}
	}

	// ////////////////////////////////////////////////////////////////////

	/**
	 * Send retrain command to server and run progress counter in background
	 */
	static public void retrain() {
		System.out.println("Sending retrain and apply commands to server");
		jStatusLabel.setText("Sending retrain and apply commands to server");
		try {

			// going to synchronous mode, all asynchronous work is done by
			// server
			progressCounter.setConnection(connection);
			// progressCounter.addActivateButton(jButtonReTrain);
			progressCounter.addActivateButton(jButtonTrain);
			progressCounter.addActivateButton(jButtonApply);
			connection.reTrainApply();

			// ///////////////////////////////////////////////////////
			// Create thread for train-apply operation and for progress bar
			// state changing
			Thread threadProgressBar = new Thread(progressCounter);
			threadProgressBar.start();
			// ///////////////////////////////////////////////////////

			System.out
					.println("Data processing on the server. Waiting for completion...");
			jStatusLabel
					.setText("Data processing on the server. Waiting for completion...");

		} catch (RemoteException ex) {
			ex.printStackTrace();
			// JOptionPane.showMessageDialog(null, "Couldn't get respond from
			// the server.\n"+
			// "Try again or press disconnect\\connect.", "Error",
			// JOptionPane.WARNING_MESSAGE);
			IDPClientSingleton.getIDPClient().glassPaneFadeIn(
					IDPGlassPane.IDPDialogs.MessageDialog, "Error",
					"Couldn't get response from the server",
					IDPMessageDlg.messageType.ERROR_MESSAGE);

		} finally {

		}
	}

	// ////////////////////////////////////////////////////////////////////

	/**
	 * This method initializes jButtonSaveXML
	 * 
	 * @return javax.swing.JButton
	 */
	static public JButton getJButtonSaveChanges() {
		if (jButtonSaveXML == null) {

			Rectangle rec = new Rectangle(50, 50);

			// ////////////////////////////////////////////////////////////
			// Load image from resource
			URL imageURL = SandBoxClient.class
					.getResource(Constants.resourcePath + "/Save.png");

			if (imageURL != null) {
				imageSave = Toolkit.getDefaultToolkit().getImage(imageURL);
				imageSave = imageSave.getScaledInstance(rec.width, rec.height,
						java.awt.Image.SCALE_SMOOTH);
			}
			// ////////////////////////////////////////////////////////////

			jButtonSaveXML = new JButton("Save changes", new ImageIcon(
					imageSave));
			jButtonSaveXML.setEnabled(Boolean.FALSE);
			jButtonSaveXML.setFocusable(Boolean.FALSE);

			jButtonSaveXML.setToolTipText("Save changes");

			jButtonSaveXML.setVerticalTextPosition(AbstractButton.BOTTOM);
			jButtonSaveXML.setHorizontalTextPosition(AbstractButton.CENTER);

			jButtonSaveXML
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							SandBoxClient client = IDPClientSingleton
									.getIDPClient();
							client.saveCurrentXML();
						}
					});
		}
		return jButtonSaveXML;
	}

	// ////////////////////////////////////////////////////////////////////

	/**
	 * This method initializes jButtonChooseColors
	 * 
	 * @return javax.swing.JButton
	 */
	static private JButton getJButtonChooseColors() {
		if (jButtonChooseColor == null) {
			Rectangle rec = new Rectangle(50, 50);

			// ////////////////////////////////////////////////////////////
			// Load image from resource
			URL imageURL = SandBoxClient.class
					.getResource(Constants.resourcePath + "/colors.png");

			if (imageURL != null) {
				imageChooseColors = Toolkit.getDefaultToolkit().getImage(
						imageURL);
				imageChooseColors = imageChooseColors.getScaledInstance(
						rec.width, rec.height, java.awt.Image.SCALE_SMOOTH);
			}
			// ////////////////////////////////////////////////////////////

			jButtonChooseColor = new JButton("Colors...", new ImageIcon(
					imageChooseColors));
			jButtonChooseColor.setEnabled(Boolean.FALSE);
			jButtonChooseColor.setFocusable(Boolean.FALSE);

			jButtonChooseColor.setToolTipText("Edit colors");

			jButtonChooseColor.setVerticalTextPosition(AbstractButton.BOTTOM);
			jButtonChooseColor.setHorizontalTextPosition(AbstractButton.CENTER);

			jButtonChooseColor
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							// replace by another listener
							removeTreeSelectionListener();
							// fadeIn
							SandBoxClient client = IDPClientSingleton
									.getIDPClient();
							client
									.glassPaneFadeIn(IDPGlassPane.IDPDialogs.ColorChooser);
						}
					});
		}
		return jButtonChooseColor;
	}

	// ////////////////////////////////////////////////////////////////////

	/**
	 * This method initializes jButtonPreferences
	 * 
	 * @return javax.swing.JButton
	 */
	static private JButton getJButtonSettings() {
		if (jButtonSettings == null) {
			Rectangle rec = new Rectangle(50, 50);

			// ////////////////////////////////////////////////////////////
			// Load image from resource
			URL imageURL = SandBoxClient.class
					.getResource(Constants.resourcePath + "/Settings.png");

			if (imageURL != null) {
				imageSettings = Toolkit.getDefaultToolkit().getImage(imageURL);
				imageSettings = imageSettings.getScaledInstance(rec.width,
						rec.height, java.awt.Image.SCALE_SMOOTH);
			}
			// ////////////////////////////////////////////////////////////

			jButtonSettings = new JButton("Settings", new ImageIcon(
					imageSettings));
			jButtonSettings.setEnabled(Boolean.TRUE);
			jButtonSettings.setFocusable(Boolean.FALSE);

			jButtonSettings.setToolTipText("Settings");

			jButtonSettings.setVerticalTextPosition(AbstractButton.BOTTOM);
			jButtonSettings.setHorizontalTextPosition(AbstractButton.CENTER);

			jButtonSettings
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							// fadeIn
							SandBoxClient client = IDPClientSingleton
									.getIDPClient();
							client
									.glassPaneFadeIn(IDPGlassPane.IDPDialogs.SettingsDialog);
						}
					});
		}
		return jButtonSettings;
	}

	// ////////////////////////////////////////////////////////////////////

	/**
	 * This method initializes jButtonSettings
	 * 
	 * @return javax.swing.JButton
	 */
	static private JButton getJButtonProperties() {
		if (jButtonProperties == null) {
			Rectangle rec = new Rectangle(50, 50);

			// ////////////////////////////////////////////////////////////
			// Load image from resource
			URL imageURL = SandBoxClient.class
					.getResource(Constants.resourcePath + "/Properties.png");

			if (imageURL != null) {
				imageProperties = Toolkit.getDefaultToolkit()
						.getImage(imageURL);
				imageProperties = imageProperties.getScaledInstance(rec.width,
						rec.height, java.awt.Image.SCALE_SMOOTH);
			}
			// ////////////////////////////////////////////////////////////

			jButtonProperties = new JButton("Properties", new ImageIcon(
					imageProperties));
			jButtonProperties.setEnabled(Boolean.FALSE);
			jButtonProperties.setFocusable(Boolean.FALSE);

			jButtonProperties.setToolTipText("Properties");

			jButtonProperties.setVerticalTextPosition(AbstractButton.BOTTOM);
			jButtonProperties.setHorizontalTextPosition(AbstractButton.CENTER);

			jButtonProperties
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							// fadeIn
							SandBoxClient client = IDPClientSingleton
									.getIDPClient();
							client
									.glassPaneFadeIn(IDPGlassPane.IDPDialogs.PropertiesDialog);
						}
					});
		}
		return jButtonProperties;
	}

	// ////////////////////////////////////////////////////////////////////

	/**
	 * This method initializes Exit button
	 * 
	 * @return javax.swing.JButton
	 */
	static private JButton getJButtonTest() {
		if (jButtonTest == null) {

			Rectangle rec = new Rectangle(50, 50);

			// ////////////////////////////////////////////////////////////
			// Load image from resource
			URL imageURL = SandBoxClient.class
					.getResource(Constants.resourcePath + "/Test.png");

			Image imageExit = null;
			if (imageURL != null) {
				imageExit = Toolkit.getDefaultToolkit().getImage(imageURL);
				imageExit = imageExit.getScaledInstance(rec.width, rec.height,
						java.awt.Image.SCALE_SMOOTH);
			}
			// ////////////////////////////////////////////////////////////

			jButtonTest = new JButton("Server console",
					new ImageIcon(imageExit));
			jButtonTest.setFocusable(false);
			jButtonTest.setEnabled(false);

			jButtonTest.setVerticalTextPosition(AbstractButton.BOTTOM);
			jButtonTest.setHorizontalTextPosition(AbstractButton.CENTER);

			jButtonTest.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					// System.exit(1);
					
					openConsoleWindow();
				}
			});
		}
		return jButtonTest;
	}

	/**
	 * Refreshes Train button, checks if Train button is enabled and enables it
	 * in case collectTagNames runs without exception.
	 */
	static public void refreshTrainButton() {
		if (!jButtonTrain.isEnabled()) {
			MessagingInterface connection = IDPConnection.getConnection();
			boolean trainEnabled = true;
			try {
				connection.collectTagNames();
			} catch (RemoteException e) {
				trainEnabled = false;
			}
			jButtonTrain.setEnabled(trainEnabled);
		}
	}

	// ////////////////////////////////////////////////////////////////////

	/**
	 * Get menu.
	 */
	static public JMenuBar getJMenuBar() {
		if (menuBar == null) {
			menuBar = new JMenuBar();

			menuBar.add(getConnectionMenu());
			menuBar.add(getIDPMenu());
			menuBar.add(getOptionsMenu());
			menuBar.add(getAboutMenu());
		}

		return menuBar;
	}

	static private IDPMenuListener getMenuListener() {
		if (menuListener == null) {
			menuListener = new IDPMenuListener();
		}

		return menuListener;
	}

	static private JMenu getConnectionMenu() {
		JMenu menu = new JMenu("Connection");

		menu.add(getConnectMenuItem());
		menu.add(getSettingsMenuItem());
		menu.add(getQuitMenuItem());

		return menu;
	}

	static private JMenu getIDPMenu() {
		JMenu menu = new JMenu("IDP");

		menu.add(getTrainMenuItem());
		menu.add(getApplyMenuItem());
		menu.add(getSaveMenuItem());

		return menu;
	}

	static private JMenu getOptionsMenu() {
		JMenu menu = new JMenu("Options");

		menu.add(getColorsMenuItem());
		menu.add(getPropertiesMenuItem());
		menu.add(initCloseAllTabsMenuItem());
		return menu;
	}

	static private JMenu getAboutMenu() {
		JMenu menu = new JMenu("About");

		menu.add(getAboutMenuItem());

		return menu;
	}

	/**
	 * Get "Connect"\"Disconnect" menu item
	 * 
	 * @return JMenuItem
	 */
	static private JMenuItem getConnectMenuItem() {
		MediaTracker media = new MediaTracker(menuBar);

		// ////////////////////////////////////////////////////////////
		// Load image from resource
		URL imageURL = SandBoxClient.class.getResource(Constants.resourcePath
				+ "/Connect.png");

		if (imageURL != null) {
			imageConnectMenu = Toolkit.getDefaultToolkit().getImage(imageURL);
			if (withLargeMenu == false) // resize
			{
				imageConnectMenu = imageConnectMenu.getScaledInstance(16, 16,
						java.awt.Image.SCALE_SMOOTH);
			}
		}
		// ////////////////////////////////////////////////////////////

		media.addImage(imageConnectMenu, 0);
		try {
			media.waitForID(0);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// ////////////////////////////////////////////////////////////
		// Load image from resource
		imageURL = SandBoxClient.class.getResource(Constants.resourcePath
				+ "/Disconnect.png");

		if (imageURL != null) {
			imageDisconnectMenu = Toolkit.getDefaultToolkit()
					.getImage(imageURL);
			if (withLargeMenu == false) // resize
			{
				imageDisconnectMenu = imageDisconnectMenu.getScaledInstance(16,
						16, java.awt.Image.SCALE_SMOOTH);
			}
		}
		// ////////////////////////////////////////////////////////////

		media.addImage(imageDisconnectMenu, 0);
		try {
			media.waitForID(0);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// ////////////////////////////////////////////////////////////////

		// ////////////////////////////////////////////////////////////////
		// set icon
		connectMenuItem = new JMenuItem("Connect") {
			/**
			 * UID
			 */
			private static final long serialVersionUID = 1L;

			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				if (this.getText().compareTo("Connect") == 0) {
					if (imageConnectMenu != null) {
						g.drawImage(imageConnectMenu, 0, 0, this);
					}
				} else {
					if (imageDisconnectMenu != null) {
						g.drawImage(imageDisconnectMenu, 0, 0, this);
					}
				}
			}
		};
		connectMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
				ActionEvent.ALT_MASK));
		connectMenuItem.setActionCommand("connect");
		connectMenuItem.addActionListener(getMenuListener());
		// ////////////////////////////////////////////////////////////////

		return connectMenuItem;
	}

	/**
	 * Get "Settings" menu item
	 * 
	 * @return JMenuItem
	 */
	static private JMenuItem getSettingsMenuItem() {
		MediaTracker media = new MediaTracker(menuBar);

		// ////////////////////////////////////////////////////////////
		// Load image from resource
		URL imageURL = SandBoxClient.class.getResource(Constants.resourcePath
				+ "/Settings.png");

		if (imageURL != null) {
			imageSettings = Toolkit.getDefaultToolkit().getImage(imageURL);
			if (withLargeMenu == false) // resize
			{
				imageSettings = imageSettings.getScaledInstance(16, 16,
						java.awt.Image.SCALE_SMOOTH);
			}
		}
		// ////////////////////////////////////////////////////////////

		media.addImage(imageSettings, 0);
		try {
			media.waitForID(0);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// ////////////////////////////////////////////////////////////////

		// ////////////////////////////////////////////////////////////////
		// set icon
		JMenuItem menuItem = new JMenuItem("Settings") {
			/**
			 * UID
			 */
			private static final long serialVersionUID = 1L;

			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				if (imageSettings != null) {
					g.drawImage(imageSettings, 0, 0, this);
				}
			}
		};
		// menuItem.setAccelerator(KeyStroke.getKeyStroke("F10"));
		// menuItem.setMnemonic(java.awt.event.KeyEvent.VK_F10);
		menuItem.setActionCommand("settings");
		menuItem.addActionListener(getMenuListener());
		// ////////////////////////////////////////////////////////////////

		return menuItem;
	}

	/**
	 * Get "Quit" menu item
	 * 
	 * @return JMenuItem
	 */
	static private JMenuItem getQuitMenuItem() {
		JMenuItem menuItem = new JMenuItem("Quit");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
				ActionEvent.ALT_MASK));
		menuItem.setActionCommand("quit");
		menuItem.addActionListener(getMenuListener());

		return menuItem;
	}

	/**
	 * Get "Train" menu item
	 * 
	 * @return JMenuItem
	 */
	static private JMenuItem getTrainMenuItem() {
		MediaTracker media = new MediaTracker(menuBar);

		// ////////////////////////////////////////////////////////////
		// Load image from resource
		URL imageURL = SandBoxClient.class.getResource(Constants.resourcePath
				+ "/Train.png");

		if (imageURL != null) {
			imageTrainMenu = Toolkit.getDefaultToolkit().getImage(imageURL);
			if (withLargeMenu == false) // resize
			{
				imageTrainMenu = imageTrainMenu.getScaledInstance(16, 16,
						java.awt.Image.SCALE_SMOOTH);
			}
		}
		// ////////////////////////////////////////////////////////////

		media.addImage(imageTrainMenu, 0);
		try {
			media.waitForID(0);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// ////////////////////////////////////////////////////////////////

		// ////////////////////////////////////////////////////////////////
		// set icon
		trainMenuItem = new JMenuItem("Train") {
			/**
			 * UID
			 */
			private static final long serialVersionUID = 1L;

			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				if (imageTrainMenu != null) {
					g.drawImage(imageTrainMenu, 0, 0, this);
				}
			}
		};
		trainMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T,
				ActionEvent.ALT_MASK));
		trainMenuItem.setActionCommand("train");
		trainMenuItem.addActionListener(getMenuListener());
		trainMenuItem.setEnabled(false);
		// ////////////////////////////////////////////////////////////////

		return trainMenuItem;
	}

	/**
	 * Get "Apply" menu item
	 * 
	 * @return JMenuItem
	 */
	static private JMenuItem getApplyMenuItem() {
		MediaTracker media = new MediaTracker(menuBar);

		// ////////////////////////////////////////////////////////////
		// Load image from resource
		URL imageURL = SandBoxClient.class.getResource(Constants.resourcePath
				+ "/Apply.png");

		if (imageURL != null) {
			imageApplyMenu = Toolkit.getDefaultToolkit().getImage(imageURL);
			if (withLargeMenu == false) // resize
			{
				imageApplyMenu = imageApplyMenu.getScaledInstance(16, 16,
						java.awt.Image.SCALE_SMOOTH);
			}
		}
		// ////////////////////////////////////////////////////////////

		media.addImage(imageApplyMenu, 0);
		try {
			media.waitForID(0);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// ////////////////////////////////////////////////////////////////

		// ////////////////////////////////////////////////////////////////
		// set icon
		applyMenuItem = new JMenuItem("Apply") {
			/**
			 * UID
			 */
			private static final long serialVersionUID = 1L;

			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				if (imageApplyMenu != null) {
					g.drawImage(imageApplyMenu, 0, 0, this);
				}
			}
		};
		applyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
				ActionEvent.ALT_MASK));
		applyMenuItem.setActionCommand("apply");
		applyMenuItem.addActionListener(getMenuListener());
		applyMenuItem.setEnabled(false);
		// ////////////////////////////////////////////////////////////////

		return applyMenuItem;
	}

	/**
	 * Get "Save" menu item
	 * 
	 * @return JMenuItem
	 */
	static private JMenuItem getSaveMenuItem() {
		MediaTracker media = new MediaTracker(menuBar);

		// ////////////////////////////////////////////////////////////
		// Load image from resource
		URL imageURL = SandBoxClient.class.getResource(Constants.resourcePath
				+ "/Save.png");

		if (imageURL != null) {
			imageSaveMenu = Toolkit.getDefaultToolkit().getImage(imageURL);
			if (withLargeMenu == false) // resize
			{
				imageSaveMenu = imageSaveMenu.getScaledInstance(16, 16,
						java.awt.Image.SCALE_SMOOTH);
			}
		}
		// ////////////////////////////////////////////////////////////

		media.addImage(imageSaveMenu, 0);
		try {
			media.waitForID(0);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// ////////////////////////////////////////////////////////////////

		// ////////////////////////////////////////////////////////////////
		// set icon
		saveMenuItem = new JMenuItem("Save") {
			/**
			 * UID
			 */
			private static final long serialVersionUID = 1L;

			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				if (imageSaveMenu != null) {
					g.drawImage(imageSaveMenu, 0, 0, this);
				}
			}
		};
		saveMenuItem.setActionCommand("send");
		saveMenuItem.addActionListener(getMenuListener());
		saveMenuItem.setEnabled(false);
		// ////////////////////////////////////////////////////////////////

		return saveMenuItem;
	}

	/**
	 * Get "Choose colors..." menu item
	 * 
	 * @return JMenuItem
	 */
	static private JMenuItem getColorsMenuItem() {
		MediaTracker media = new MediaTracker(menuBar);

		// ////////////////////////////////////////////////////////////
		// Load image from resource
		URL imageURL = SandBoxClient.class.getResource(Constants.resourcePath
				+ "/colors.png");

		if (imageURL != null) {
			imageColorsMenu = Toolkit.getDefaultToolkit().getImage(imageURL);
			if (withLargeMenu == false) // resize
			{
				imageColorsMenu = imageColorsMenu.getScaledInstance(16, 16,
						java.awt.Image.SCALE_SMOOTH);
			}
		}
		// ////////////////////////////////////////////////////////////

		media.addImage(imageColorsMenu, 0);
		try {
			media.waitForID(0);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// ////////////////////////////////////////////////////////////////

		// ////////////////////////////////////////////////////////////////
		// set icon
		chooseColorsMenuItem = new JMenuItem("Choose colors...") {
			/**
			 * UID
			 */
			private static final long serialVersionUID = 1L;

			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				if (imageColorsMenu != null) {
					g.drawImage(imageColorsMenu, 0, 0, this);
				}
			}
		};

		chooseColorsMenuItem.setActionCommand("colors");
		chooseColorsMenuItem.addActionListener(getMenuListener());
		chooseColorsMenuItem.setEnabled(false);
		// ////////////////////////////////////////////////////////////////

		return chooseColorsMenuItem;
	}

	/**
	 * Get "Properties" menu item
	 * 
	 * @return JMenuItem
	 */
	static private JMenuItem getPropertiesMenuItem() {
		MediaTracker media = new MediaTracker(menuBar);

		// ////////////////////////////////////////////////////////////
		// Load image from resource
		URL imageURL = SandBoxClient.class.getResource(Constants.resourcePath
				+ "/Properties.png");

		if (imageURL != null) {
			imageProperties = Toolkit.getDefaultToolkit().getImage(imageURL);
			if (withLargeMenu == false) // resize
			{
				imageProperties = imageProperties.getScaledInstance(16, 16,
						java.awt.Image.SCALE_SMOOTH);
			}
		}
		// ////////////////////////////////////////////////////////////

		media.addImage(imageProperties, 0);
		try {
			media.waitForID(0);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// ////////////////////////////////////////////////////////////////

		// ////////////////////////////////////////////////////////////////
		// set icon
		propertiesMenuItem = new JMenuItem("Properties") {
			/**
			 * IDP
			 */
			private static final long serialVersionUID = 1L;

			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				if (imageProperties != null) {
					g.drawImage(imageProperties, 0, 0, this);
				}
			}
		};
		propertiesMenuItem.setActionCommand("properties");
		propertiesMenuItem.addActionListener(getMenuListener());
		propertiesMenuItem.setEnabled(false);
		// ////////////////////////////////////////////////////////////////

		return propertiesMenuItem;
	}

	/**
	 * Get "About" menu item
	 * 
	 * @return JMenuItem
	 */
	static private JMenuItem getAboutMenuItem() {
		MediaTracker media = new MediaTracker(menuBar);

		// ////////////////////////////////////////////////////////////
		// Load image from resource
		URL imageURL = SandBoxClient.class.getResource(Constants.resourcePath
				+ "/About.png");

		if (imageURL != null) {
			imageAboutMenu = Toolkit.getDefaultToolkit().getImage(imageURL);
			if (withLargeMenu == false) // resize
			{
				imageAboutMenu = imageAboutMenu.getScaledInstance(16, 16,
						java.awt.Image.SCALE_SMOOTH);
			}
		}
		// ////////////////////////////////////////////////////////////

		media.addImage(imageAboutMenu, 0);
		try {
			media.waitForID(0);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// ////////////////////////////////////////////////////////////////

		// ////////////////////////////////////////////////////////////////
		// set icon
		JMenuItem menuItem = new JMenuItem("About IDP...") {
			/**
			 * UID
			 */
			private static final long serialVersionUID = 1L;

			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				if (imageAboutMenu != null) {
					g.drawImage(imageAboutMenu, 0, 0, this);
				}
			}
		};
		menuItem.setAccelerator(KeyStroke.getKeyStroke("F1"));
		menuItem.setActionCommand("about");
		menuItem.addActionListener(getMenuListener());
		// ////////////////////////////////////////////////////////////////

		return menuItem;
	}

	/**
	 * Initializes Close all tabs menu item
	 * 
	 * @return JMenuItem - close all tabs menu item
	 */
	static private JMenuItem initCloseAllTabsMenuItem() {
		MediaTracker media = new MediaTracker(menuBar);

		try {
			media.waitForID(0);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		closeAllTabsMenuItem = new JMenuItem("Close all tabs") {
			/**
			 * UID
			 */
			private static final long serialVersionUID = 1L;

		};
		closeAllTabsMenuItem.setActionCommand("closeAllTabs");
		closeAllTabsMenuItem.addActionListener(getMenuListener());
		closeAllTabsMenuItem.setEnabled(false);
		
		return closeAllTabsMenuItem;
	}
	
	/**
	 * Returns close all menu item.
	 * @return close all JMenuItem
	 */
	static public JMenuItem getCloseAllTabsMenuItem() {
		return closeAllTabsMenuItem;
	}

	/**
	 * This method initializes IDPContentTree
	 * 
	 * @return IDPContentTree
	 */
	static public IDPContentTree getJTree() {
		if (jTree == null) {

			jTree = new IDPContentTree();
			jTree.setRootVisible(false);
			jTree.setScrollsOnExpand(true);

			// jTree.addTreeSelectionListener(new IDPContentTreeListener());

			// ////////////////////////////////////////////////////////////
			// set cell renderer
			ContentTreeRenderer treeCellRenderer = new ContentTreeRenderer();
			jTree.setCellRenderer(treeCellRenderer);

			// Enable tool tips.
			ToolTipManager.sharedInstance().registerComponent(jTree);
			// ////////////////////////////////////////////////////////////
		}
		return jTree;
	}

	// ////////////////////////////////////////////////////////////////////

	/**
	 * This method initializes jXMLTree
	 * 
	 * @return javax.swing.JTree
	 */
	static public IDPXMLTree getJXMLTree() {
		if (jXMLTree == null) {

			// ///////////////////////////////////////////////////////////////////////////
			// create and initialize the JTree component
			SandBoxClient client = IDPClientSingleton.getIDPClient();
			jXMLTree = new IDPXMLTree(client.getXmlTreeRoot());
			jXMLTree.setRootVisible(false);
			jXMLTree.setScrollsOnExpand(true);
			// ///////////////////////////////////////////////////////////////////////////

			// ///////////////////////////////////////////////////////////////////////////
			// for check boxes and color node labels (legenda)
			// have custom CheckTreeCellRenderer
			checkTreeManager = new CheckTreeManager(jXMLTree);
			// ///////////////////////////////////////////////////////////////////////////

			// ///////////////////////////////////////////////////////////////////////////
			// Listener. Updates the current text coloring in the focused text
			// pane.
			treeSelectionListener = new IDPXMLTreeListener();
			addTreeSelectionListener();
			// ///////////////////////////////////////////////////////////////////////////

			// ///////////////////////////////////////////////////////////////////////////
			jXMLTree.updateUI();
			// ///////////////////////////////////////////////////////////////////////////
		}
		return jXMLTree;
	}

	// ////////////////////////////////////////////////////////////////////

	/**
	 * Add tree selectin listener. public cause is called from other classed
	 */
	static public void addTreeSelectionListener() {
		getJXMLTree().addTreeSelectionListener(treeSelectionListener);
	}

	/**
	 * Remove tree selectin listener. public cause is called from other classed
	 */
	static public void removeTreeSelectionListener() {
		getJXMLTree().removeTreeSelectionListener(treeSelectionListener);
	}

	/**
	 * This method initializes jSplitPane
	 * 
	 * @return javax.swing.JSplitPane
	 */
	static public JSplitPane getJMiddleSplitPane() {
		if (jSplitPane == null) {
			jSplitPane = new JSplitPane();

			// ////////////////////////////////////////////////////////////
			// Set components

			// ////////////////////////////////////////////////////////////
			// that means that all new spaces will be given to right component
			// (which is got from getJInnerSplitPane())
			jSplitPane.setResizeWeight(0.15);

			// ////////////////////////////////////////////////////////////
			// set right component - JTabbedPane plus jXMLTree
			jSplitPane.setRightComponent(getJInnerSplitPane());

			// ////////////////////////////////////////////////////////////
			// set left component - jTree
			jSplitPane.setLeftComponent(getJTreeScrollPane());

			jSplitPane.setDividerSize(5);
			// ////////////////////////////////////////////////////////////

			// ////////////////////////////////////////////////////////////
			// add divider listener
			BasicSplitPaneUI paneUI = (BasicSplitPaneUI) jSplitPane.getUI();
			paneUI.getDivider().addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					getJTabbedPane().updateRectangles();
				}

				@Override
				public void mouseEntered(MouseEvent arg0) {
					super.mouseEntered(arg0);
					Cursor cursor = new Cursor(Cursor.E_RESIZE_CURSOR);
					SandBoxClient client = IDPClientSingleton.getIDPClient();
					client.setCursor(cursor);
				}

				@Override
				public void mouseExited(MouseEvent arg0) {
					super.mouseExited(arg0);
					Cursor cursor = new Cursor(Cursor.DEFAULT_CURSOR);
					SandBoxClient client = IDPClientSingleton.getIDPClient();
					client.setCursor(cursor);
				}

			});
			// ////////////////////////////////////////////////////////////
		}
		return jSplitPane;
	}

	// ////////////////////////////////////////////////////////////////////

	/**
	 * This method initializes jSplitPane
	 * 
	 * @return javax.swing.JSplitPane
	 */
	static public JSplitPane getJInnerSplitPane() {
		if (jInnerSplitPane == null) {
			jInnerSplitPane = new JSplitPane();

			// ////////////////////////////////////////////////////////////
			// set components

			// ////////////////////////////////////////////////////////////
			// that means that all new spaces will be given to right component
			// (which is got from getJInnerSplitPane())
			jInnerSplitPane.setResizeWeight(1);

			// ////////////////////////////////////////////////////////////
			// set left component - JTabbedPane, where the data from server is
			// shown
			jInnerSplitPane.setLeftComponent(getJTabbedPane());

			// ////////////////////////////////////////////////////////////
			// set right component - JTree where XML tree from server is shown
			jInnerSplitPane.setRightComponent(getJXMLTreePane());

			// ////////////////////////////////////////////////////////////
			// set divider size
			jInnerSplitPane.setDividerSize(5);
			// ////////////////////////////////////////////////////////////

			// ////////////////////////////////////////////////////////////
			// add divider listener
			BasicSplitPaneUI paneUI = (BasicSplitPaneUI) jInnerSplitPane
					.getUI();
			paneUI.getDivider().addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					getJTabbedPane().updateRectangles();
				}

				@Override
				public void mouseEntered(MouseEvent arg0) {
					super.mouseEntered(arg0);
					Cursor cursor = new Cursor(Cursor.E_RESIZE_CURSOR);
					SandBoxClient client = IDPClientSingleton.getIDPClient();
					client.setCursor(cursor);
				}

				@Override
				public void mouseExited(MouseEvent arg0) {
					super.mouseExited(arg0);
					Cursor cursor = new Cursor(Cursor.DEFAULT_CURSOR);
					SandBoxClient client = IDPClientSingleton.getIDPClient();
					client.setCursor(cursor);
				}
			});
			// ////////////////////////////////////////////////////////////

		}
		return jInnerSplitPane;
	}

	// ////////////////////////////////////////////////////////////////////

	/**
	 * This method initializes jXMLTreePane
	 * 
	 * @return javax.swing.JPanel
	 */
	static private JPanel getJXMLTreePane() {
		if (jXMLTreePane == null) {
			jXMLTreePane = new JPanel();

			jXMLTreePane.setLayout(new BorderLayout());

			jXMLTreePane.add(getJXMLTreeScrollPane(), BorderLayout.CENTER);
			jXMLTreePane.add(getJXMLTreeToolBar(), BorderLayout.EAST);
		}
		return jXMLTreePane;
	}

	/**
	 * This method initializes jXMLTreeToolBar
	 * 
	 * @return javax.swing.JToolBar
	 */
	static private JToolBar getJXMLTreeToolBar() {
		if (jXMLTreeToolBar == null) {
			jXMLTreeToolBar = new JToolBar();

			// jXMLTreeToolBar.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
			jXMLTreeToolBar.setFloatable(false);
			// jXMLTreeToolBar.setBackground(Color.white);

			jXMLTreeToolBar.setLayout(new BoxLayout(jXMLTreeToolBar,
					BoxLayout.Y_AXIS));

			jXMLTreeToolBar.add(getJToolBarButtonAdd());
			jXMLTreeToolBar.add(getJToolBarButtonDelete());
			jXMLTreeToolBar.add(getJToolBarButtonSave());
			jXMLTreeToolBar.add(getJToolBarButtonColors());
			jXMLTreeToolBar.add(getJToolBarButtonNewMark());

			jXMLTreeToolBar.setBorder(BorderFactory.createEtchedBorder());
		}
		return jXMLTreeToolBar;
	}

	/**
	 * This method initializes Change Color toolbar button
	 * 
	 * @return javax.swing.JButton
	 */
	static public JButton getJToolBarButtonColors() {
		if (jToolBarButtonColors == null) {

			Rectangle rec = new Rectangle(18, 18);

			// ////////////////////////////////////////////////////////////
			// Load image from resource
			URL imageURL = SandBoxClient.class
					.getResource(Constants.resourcePath + "/colors.png");

			Image imageColors = null;
			if (imageURL != null) {
				imageColors = Toolkit.getDefaultToolkit().getImage(imageURL);
				imageColors = imageColors.getScaledInstance(rec.width,
						rec.height, java.awt.Image.SCALE_SMOOTH);
			}
			// ////////////////////////////////////////////////////////////

			jToolBarButtonColors = new JButton("", new ImageIcon(imageColors));
			jToolBarButtonColors.setEnabled(false);
			jToolBarButtonColors.setOpaque(Boolean.FALSE);
			jToolBarButtonColors.setFocusable(Boolean.FALSE);

			jToolBarButtonColors.setToolTipText("Change tag color...");

			jToolBarButtonColors
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {

							// ////////////////////////////////////////////
							// old change color dialog
							// getJXMLTree().updateSelectedTreeNode();
							// getJXMLTree().showColorChooserDialog();
							// ////////////////////////////////////////////

							// ////////////////////////////////////////////
							// new change color dialog

							// replace by another listener
							removeTreeSelectionListener();

							// fadeIn
							SandBoxClient client = IDPClientSingleton
									.getIDPClient();
							client
									.glassPaneFadeIn(IDPGlassPane.IDPDialogs.ColorChooser);

							// ////////////////////////////////////////////

						}
					});
		}
		return jToolBarButtonColors;
	}

	// ////////////////////////////////////////////////////////////////////

	/**
	 * This method initializes Add new tag toolbar button
	 * 
	 * @return javax.swing.JButton
	 */
	static public JButton getJToolBarButtonAdd() {
		if (jToolBarButtonAdd == null) {

			Rectangle rec = new Rectangle(18, 18);

			// ////////////////////////////////////////////////////////////
			// Load image from resource
			URL imageURL = SandBoxClient.class
					.getResource(Constants.resourcePath + "/AddNew.png");

			Image imageAddNew = null;
			if (imageURL != null) {
				imageAddNew = Toolkit.getDefaultToolkit().getImage(imageURL);
				imageAddNew = imageAddNew.getScaledInstance(rec.width,
						rec.height, java.awt.Image.SCALE_SMOOTH);
			}
			// ////////////////////////////////////////////////////////////

			jToolBarButtonAdd = new JButton("", new ImageIcon(imageAddNew));
			jToolBarButtonAdd.setEnabled(false);
			jToolBarButtonAdd.setOpaque(Boolean.FALSE);
			jToolBarButtonAdd.setFocusable(Boolean.FALSE);

			jToolBarButtonAdd.setToolTipText("Add a child tag");

			jToolBarButtonAdd
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							getJXMLTree().updateSelectedTreeNode();
							getJXMLTree().AskAboutAddingTag();
						}
					});
		}
		return jToolBarButtonAdd;
	}

	// ////////////////////////////////////////////////////////////////////

	/**
	 * This method initializes Add new tag toolbar button
	 * 
	 * @return javax.swing.JButton
	 */
	static public JButton getJToolBarButtonDelete() {
		if (jToolBarButtonDelete == null) {

			Rectangle rec = new Rectangle(18, 18);

			// ////////////////////////////////////////////////////////////
			// Load image from resource
			URL imageURL = SandBoxClient.class
					.getResource(Constants.resourcePath + "/Delete.png");

			Image imageDelete = null;
			if (imageURL != null) {
				imageDelete = Toolkit.getDefaultToolkit().getImage(imageURL);
				imageDelete = imageDelete.getScaledInstance(rec.width,
						rec.height, java.awt.Image.SCALE_SMOOTH);
			}
			// ////////////////////////////////////////////////////////////

			jToolBarButtonDelete = new JButton("", new ImageIcon(imageDelete));
			jToolBarButtonDelete.setEnabled(false);
			jToolBarButtonDelete.setOpaque(Boolean.FALSE);
			jToolBarButtonDelete.setFocusable(Boolean.FALSE);

			jToolBarButtonDelete.setToolTipText("Delete the tag");

			jToolBarButtonDelete
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							getJXMLTree().updateSelectedTreeNode();
							getJXMLTree().AskAboutDeletingTag();
						}
					});
		}
		return jToolBarButtonDelete;
	}

	// ////////////////////////////////////////////////////////////////////

	/**
	 * This method initializes NEw mark toolbar button
	 * 
	 * @return javax.swing.JButton
	 */
	static public JButton getJToolBarButtonNewMark() {
		if (jToolBarButtonNewMark == null) {

			Rectangle rec = new Rectangle(18, 18);

			// ////////////////////////////////////////////////////////////
			// Load image from resource
			URL imageURL = SandBoxClient.class
					.getResource(Constants.resourcePath + "/Mark.png");

			Image imageNewMark = null;
			if (imageURL != null) {
				imageNewMark = Toolkit.getDefaultToolkit().getImage(imageURL);
				imageNewMark = imageNewMark.getScaledInstance(rec.width,
						rec.height, java.awt.Image.SCALE_SMOOTH);
			}
			// ////////////////////////////////////////////////////////////

			jToolBarButtonNewMark = new JButton("", new ImageIcon(imageNewMark));
			jToolBarButtonNewMark.setEnabled(false);
			jToolBarButtonNewMark.setOpaque(Boolean.FALSE);
			jToolBarButtonNewMark.setFocusable(Boolean.FALSE);

			jToolBarButtonNewMark.setToolTipText("Mark the text");

			jToolBarButtonNewMark
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							getJXMLTree().updateSelectedTreeNode();
							getJXMLTree().AskAboutNewMark();
						}
					});
		}
		return jToolBarButtonNewMark;
	}

	// ////////////////////////////////////////////////////////////////////

	/**
	 * This method initializes Save toolbar button
	 * 
	 * @return javax.swing.JButton
	 */
	static public JButton getJToolBarButtonSave() {
		if (jToolBarButtonSave == null) {

			Rectangle rec = new Rectangle(18, 18);

			// ////////////////////////////////////////////////////////////
			// Load image from resource
			URL imageURL = SandBoxClient.class
					.getResource(Constants.resourcePath + "/SaveTree.png");

			Image imageSave = null;
			if (imageURL != null) {
				imageSave = Toolkit.getDefaultToolkit().getImage(imageURL);
				imageSave = imageSave.getScaledInstance(rec.width, rec.height,
						java.awt.Image.SCALE_SMOOTH);
			}
			// ////////////////////////////////////////////////////////////

			jToolBarButtonSave = new JButton("", new ImageIcon(imageSave));
			jToolBarButtonSave.setEnabled(false);
			jToolBarButtonSave.setOpaque(Boolean.FALSE);
			jToolBarButtonSave.setFocusable(Boolean.FALSE);

			jToolBarButtonSave.setToolTipText("Upload tree changes");

			jToolBarButtonSave
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							IDPWindowManager.getJXMLTree().sendXMLTree();
							IDPWindowManager.getJToolBarButtonSave()
									.setEnabled(false);
						}
					});
		}
		return jToolBarButtonSave;
	}

	// ////////////////////////////////////////////////////////////////////

	/**
	 * Enable XML Tree tool bar.
	 */
	static private void setEnabled_XMLTreeToolbar(boolean bEnabled) {
		if (bEnabled) {
			oldColor = getJXMLTreeToolBar().getBackground();
			getJXMLTreeToolBar().setBackground(Color.white);
			getJXMLTreeToolBar().repaint();
		} else
			getJXMLTreeToolBar().setBackground(oldColor);
	}

	// ////////////////////////////////////////////////////////////////////

	/**
	 * Create Console Window
	 */
	static private void openConsoleWindow() {
		if (jConsoleView == null) {
			jConsoleContent = new JTextPane();
			jConsoleContent.setEditable(false);

			jConsoleView = new JScrollPane(jConsoleContent);
		}

		jTabbedPane.addTab("Server Console", jConsoleView);
		jTabbedPane.setSelectedComponent(jConsoleView);

		// ////////////////////////////////////////////////////////////////
		// put to Hash Map
		// as console window differs from other tabs then we'll create dummy
		// structure
		// with any file (e.g., SandBoxClient.properties - doesn't matter what
		// the file exactly is)
		TextFileInfo dummy = new TextFileInfo(new File(
				Constants.propertiesFileName));
		dummy.setName("Server Console");
		jTabbedPane.putTabInfo(dummy, jConsoleView);
		// ////////////////////////////////////////////////////////////////
	}

	// ////////////////////////////////////////////////////////////////////

	/**
	 * Get XML tree's check boxes manager
	 * 
	 * @return CheckTreeManager
	 * 
	 * @see CheckTreeManager
	 */
	static public CheckTreeManager getCheckTreeManager() {
		return checkTreeManager;
	}
	// ////////////////////////////////////////////////////////////////////

}
