package idp.sandBox.client.dialogs;

import idp.sandBox.client.constants.Constants;
import idp.sandBox.client.gui.components.IDPGlassPane;
import idp.sandBox.client.managers.IDPClientSingleton;
import idp.sandBox.client.managers.IDPConnection;
import idp.sandBox.client.toolkit.RegexFormatter;
import idp.sandBox.server.MessagingInterface;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.rmi.RemoteException;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle;
import javax.swing.border.TitledBorder;

import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.jdesktop.swingx.graphics.ShadowRenderer;

public class IDPPropertiesDlg extends AbstractSaveDialog implements ActionListener {
	
	private static final long serialVersionUID = 3491207756993604724L;

	private static final boolean ISBORDERED = false;

	private static int X_OFFSET = 30;
	private static int Y_OFFSET = 30;

	private static int CY_OFFSET = 5;

	private BufferedImage shadow = null;

	private JPanel jContentPane = null;
	private JPanel jProjectSettingsPane = null;
	private JPanel jButtonsPane = null;

	private JLabel jLabel = null;
	private JButton jCloseButton = null;
	private JButton jSaveButton = null;

	private Font labelFont = null;
	private Font textFont = null;

	private JFormattedTextField namespace = null;
	private JFormattedTextField trainDir = null;
	private JFormattedTextField annotatorsDir = null;
	private JFormattedTextField applyDir = null;
	private JFormattedTextField resultDir = null;
	private JFormattedTextField correctedDir = null;

	public IDPPropertiesDlg() {
		setOpaque(false);

		// Font
		labelFont = new Font("Arial", Font.BOLD, 16);
		textFont = new Font("Arial", Font.PLAIN, 15);
	}

	public void setLayouts(int width, int height) {

		GroupLayout groupLayout = new GroupLayout(this);
		setLayout(groupLayout);

		groupLayout.setHorizontalGroup(groupLayout.createSequentialGroup()
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
						(int) (X_OFFSET + 0.025 * width),
						(int) (X_OFFSET + 0.025 * width)).addGroup(
						groupLayout.createParallelGroup(
								GroupLayout.Alignment.LEADING).addComponent(
								getJLabel(), GroupLayout.PREFERRED_SIZE,
								GroupLayout.PREFERRED_SIZE,
								GroupLayout.PREFERRED_SIZE).addComponent(
								getJContentPane(), GroupLayout.PREFERRED_SIZE,
								GroupLayout.PREFERRED_SIZE,
								GroupLayout.PREFERRED_SIZE).addComponent(
								getButtonsPane(), GroupLayout.PREFERRED_SIZE,
								GroupLayout.PREFERRED_SIZE,
								GroupLayout.PREFERRED_SIZE)));

		groupLayout
				.setVerticalGroup(groupLayout.createSequentialGroup()
						.addPreferredGap(
								LayoutStyle.ComponentPlacement.RELATED,
								(int) (Y_OFFSET + 2 * CY_OFFSET),
								(int) (Y_OFFSET + 2 * CY_OFFSET)).addComponent(
								getJLabel(), GroupLayout.PREFERRED_SIZE,
								GroupLayout.PREFERRED_SIZE,
								GroupLayout.PREFERRED_SIZE).addPreferredGap(
								LayoutStyle.ComponentPlacement.RELATED,
								(int) (2 * CY_OFFSET), (int) (2 * CY_OFFSET))
						.addComponent(getJContentPane(),
								GroupLayout.PREFERRED_SIZE,
								GroupLayout.PREFERRED_SIZE,
								GroupLayout.PREFERRED_SIZE).addPreferredGap(
								LayoutStyle.ComponentPlacement.RELATED,
								(int) (CY_OFFSET), (int) (CY_OFFSET))
						.addComponent(getButtonsPane(),
								GroupLayout.PREFERRED_SIZE,
								GroupLayout.PREFERRED_SIZE,
								GroupLayout.PREFERRED_SIZE));
	}

	@Override
	protected void paintComponent(Graphics g) {
		int x = X_OFFSET;
		int y = Y_OFFSET;
		int w = getWidth() - 2 * X_OFFSET;
		int h = getHeight() - 2 * Y_OFFSET;
		int arc = 30;

		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		if (shadow != null) {
			int xOffset = (shadow.getWidth() - w) / 2;
			int yOffset = (shadow.getHeight() - h) / 2;
			g2.drawImage(shadow, x - xOffset, y - yOffset, null);
		}

		// ////////////////////////////////////////////////////////////////
		// fill content
		g2.setColor(new Color(255, 255, 255, 220));
		g2.fillRoundRect(x, y, w, h, arc, arc);
		// ////////////////////////////////////////////////////////////////

		// ////////////////////////////////////////////////////////////////
		// draw border
		g2.setStroke(new BasicStroke(3f));
		g2.setColor(Color.BLACK);
		g2.drawRoundRect(x, y, w, h, arc, arc);
		// ////////////////////////////////////////////////////////////////

		g2.dispose();
	}

	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);

		if (width == 0 || height == 0) {
			return;
		}
		
		int w = getWidth() - 2 * X_OFFSET;
		int h = getHeight() - 2 * Y_OFFSET;
		int arc = 30;
		int shadowSize = 20;

		shadow = GraphicsUtilities.createCompatibleTranslucentImage(w, h);
		Graphics2D g2 = shadow.createGraphics();
		g2.setColor(Color.WHITE);
		g2.fillRoundRect(0, 0, w, h, arc, arc);
		g2.dispose();

		ShadowRenderer renderer = new ShadowRenderer(shadowSize, 0.5f,
				Color.GRAY);
		shadow = renderer.createShadow(shadow);

	}

	public void updatePreferredSize(int width, int height) {
		width = width - 2 * X_OFFSET;
		height = height - 2 * Y_OFFSET;

		setLayouts(width, height);

		int w = (int) (0.95 * width);
		int h = (int) (height - 4 * CY_OFFSET);

		jLabel.setPreferredSize(new Dimension(w, 20));

		Dimension cpsize = new Dimension(w, h - 30 - 20 - 20); // content pane
		// size
		jContentPane.setPreferredSize(cpsize);

		jButtonsPane.setPreferredSize(new Dimension(w, 30));

	}

	public void showDialog(boolean bShow) {
		setVisible(bShow);
	}

	public boolean updateProperties() {
		MessagingInterface connection = IDPConnection.getConnection();
		if (connection != null) {
			try {
				Properties p = connection.getServerProperties();

				if (p != null) {
					if (p.containsKey("namespace"))
						namespace.setValue(p.getProperty("namespace"));
					if (p.containsKey("trainDirectory"))
						trainDir.setValue(p.getProperty("trainDirectory"));
					if (p.containsKey("annotatorsDirectory"))
						annotatorsDir.setValue(p
								.getProperty("annotatorsDirectory"));
					if (p.containsKey("applyDirectory"))
						applyDir.setValue(p.getProperty("applyDirectory"));
					if (p.containsKey("destinationDirectory"))
						resultDir.setValue(p
								.getProperty("destinationDirectory"));
					if (p.containsKey("correctedDirectory"))
						correctedDir.setValue(p
								.getProperty("correctedDirectory"));
				}
			} catch (RemoteException e) {
				e.printStackTrace();
				IDPClientSingleton.getIDPClient().discardCurrentDialog();
				IDPClientSingleton.getIDPClient().glassPaneFadeIn(
						IDPGlassPane.IDPDialogs.MessageDialog, "Error",
						"Couldn't get properties from the server",
						IDPMessageDlg.messageType.ERROR_MESSAGE);
				return false;
			}
		}

		return true;
	}

	private JLabel getJLabel() {
		if (jLabel == null) {
			jLabel = new JLabel("Properties");
			jLabel.setFont(labelFont);

			if (ISBORDERED)
				jLabel.setBorder(BorderFactory.createLineBorder(Color.blue));
		}
		return jLabel;
	}

	private JButton getJCloseButton() {
		if (jCloseButton == null) {
			jCloseButton = new JButton("Close");
			jCloseButton.addActionListener(this);
			jCloseButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
					KeyStroke.getKeyStroke("ESCAPE"), "esc");
			jCloseButton.getActionMap().put("esc", new AbstractAction() {

				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					parent.fadeOut();
				}
			});
		}
		return jCloseButton;
	}

	private JButton getJSaveButton() {
		if (jSaveButton == null) {
			jSaveButton = new JButton("Save");
			jSaveButton.addActionListener(this);
			subscriveSaveEnterAction(jSaveButton);
		}
		return jSaveButton;
	}

	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();

			jContentPane.setOpaque(false);

			jContentPane.setLayout(new BoxLayout(jContentPane,
					BoxLayout.PAGE_AXIS));

			jContentPane.add(getJProjectSettingsPane());

			if (ISBORDERED)
				jContentPane.setBorder(BorderFactory
						.createLineBorder(Color.red));
		}
		return jContentPane;
	}

	private RegexFormatter createDirFormatter() {
		// String dir = "[a-zA-z]+[0-9a-zA-z]*/?";
		// Pattern patern = Pattern.compile("(/("+dir+")*)|(/)$");
		String dir = "[0-9a-zA-z]+([0-9a-zA-z]*/)?";
		Pattern patern = Pattern.compile("(/(" + dir + ")*)$");
		RegexFormatter dirFormatter = new RegexFormatter(patern);
		dirFormatter.setAllowsInvalid(false);
		dirFormatter.setOverwriteMode(false);
		return dirFormatter;
	}

	private RegexFormatter createURLFormatter() {
		// String dir = "[a-zA-z]+[0-9a-zA-z]*/?";
		// Pattern patern = Pattern.compile("(/("+dir+")*)|(/)$");
		String url = "[0-9a-zA-z]+([0-9a-zA-z/:.]*)?";
		Pattern patern = Pattern.compile("^(" + url + ")*$");
		RegexFormatter dirFormatter = new RegexFormatter(patern);
		dirFormatter.setAllowsInvalid(false);
		dirFormatter.setOverwriteMode(false);
		return dirFormatter;
	}
	
	private JPanel getJProjectSettingsPane() {
		MessagingInterface connection = IDPConnection.getConnection();
		// TODO change to project properties when moved to multiproject

		Properties props;
		try {
			props = connection.getServerProperties();

			if (jProjectSettingsPane == null) {
				jProjectSettingsPane = new JPanel();

				jProjectSettingsPane.setOpaque(false);

				GridBagLayout gridBag = new GridBagLayout();
				GridBagConstraints c = new GridBagConstraints();

				jProjectSettingsPane.setLayout(gridBag);

				c.insets = new Insets(10, 10, 10, 10);

				// ////////////////////////////////////////////////////////////
				// namespace
				// c.gridwidth = 1;
				JLabel jLabelNS = new JLabel("Namespace:");
				jLabelNS.setFont(textFont);

				gridBag.setConstraints(jLabelNS, c);
				jProjectSettingsPane.add(jLabelNS);
				
				String value = props.getProperty("namespace",
						Constants.defaultNamespace);
				namespace = new JFormattedTextField(createURLFormatter());
				namespace.setValue(value);
				namespace.setPreferredSize(new Dimension(100, 20));
				namespace.setFont(textFont);
				namespace.setBorder(BorderFactory.createLineBorder(Color.black,
						2));
				subscriveSaveEnterAction(namespace);
				c.gridwidth = GridBagConstraints.REMAINDER;
				gridBag.setConstraints(namespace, c);
				jProjectSettingsPane.add(namespace);
				// ////////////////////////////////////////////////////////////

				// ////////////////////////////////////////////////////////////
				// train directory
				JLabel jLabel1 = new JLabel("Train directory:");
				jLabel1.setFont(textFont);
				c.gridwidth = 1;
				gridBag.setConstraints(jLabel1, c);
				jProjectSettingsPane.add(jLabel1);
				
				trainDir = new JFormattedTextField(createDirFormatter());
				trainDir.setValue("/train");
				trainDir.setPreferredSize(new Dimension(100, 20));
				trainDir.setFont(textFont);
				trainDir.setBorder(BorderFactory.createLineBorder(Color.black,
						2));
				subscriveSaveEnterAction(trainDir);
				c.gridwidth = GridBagConstraints.REMAINDER;
				gridBag.setConstraints(trainDir, c);
				jProjectSettingsPane.add(trainDir);
				// ////////////////////////////////////////////////////////////

				// ////////////////////////////////////////////////////////////
				// annotators directory
				JLabel jLabel2 = new JLabel("Annotators directory:");
				jLabel2.setFont(textFont);
				c.gridwidth = 1;
				gridBag.setConstraints(jLabel2, c);
				jProjectSettingsPane.add(jLabel2);

				annotatorsDir = new JFormattedTextField(createDirFormatter());
				annotatorsDir.setValue("/annotators");
				annotatorsDir.setPreferredSize(new Dimension(100, 20));
				annotatorsDir.setFont(textFont);
				annotatorsDir.setBorder(BorderFactory.createLineBorder(
						Color.black, 2));
				subscriveSaveEnterAction(annotatorsDir);
				
				c.gridwidth = GridBagConstraints.REMAINDER;
				gridBag.setConstraints(annotatorsDir, c);
				jProjectSettingsPane.add(annotatorsDir);
				// ////////////////////////////////////////////////////////////

				// ////////////////////////////////////////////////////////////
				// apply directory
				c.gridwidth = 1;
				JLabel jLabel3 = new JLabel("Apply directory:");
				jLabel3.setFont(textFont);
				gridBag.setConstraints(jLabel3, c);
				jProjectSettingsPane.add(jLabel3);

				applyDir = new JFormattedTextField(createDirFormatter());
				applyDir.setValue("/apply");
				applyDir.setPreferredSize(new Dimension(100, 20));
				applyDir.setFont(textFont);
				applyDir.setBorder(BorderFactory.createLineBorder(Color.black,
						2));
				subscriveSaveEnterAction(applyDir);
				c.gridwidth = GridBagConstraints.REMAINDER;
				gridBag.setConstraints(applyDir, c);
				jProjectSettingsPane.add(applyDir);
				// ////////////////////////////////////////////////////////////

				// ////////////////////////////////////////////////////////////
				// result directory
				JLabel jLabel4 = new JLabel("Result directory:");
				jLabel4.setFont(textFont);
				c.gridwidth = 1;
				gridBag.setConstraints(jLabel4, c);
				jProjectSettingsPane.add(jLabel4);

				resultDir = new JFormattedTextField(createDirFormatter());
				resultDir.setValue("/res");
				resultDir.setPreferredSize(new Dimension(100, 20));
				resultDir.setFont(textFont);
				resultDir.setBorder(BorderFactory.createLineBorder(Color.black,
						2));
				subscriveSaveEnterAction(resultDir);
				c.gridwidth = GridBagConstraints.REMAINDER;

				gridBag.setConstraints(resultDir, c);
				jProjectSettingsPane.add(resultDir);
				// ////////////////////////////////////////////////////////////

				// ////////////////////////////////////////////////////////////
				// corrected directory
				JLabel jLabel5 = new JLabel("Corrected directory:");
				jLabel5.setFont(textFont);
				c.gridwidth = 1;
				gridBag.setConstraints(jLabel5, c);
				jProjectSettingsPane.add(jLabel5);

				correctedDir = new JFormattedTextField(createDirFormatter());
				correctedDir.setValue("/corrected");
				correctedDir.setPreferredSize(new Dimension(100, 20));
				correctedDir.setFont(textFont);
				correctedDir.setBorder(BorderFactory.createLineBorder(
						Color.black, 2));
				subscriveSaveEnterAction(correctedDir);
				c.gridwidth = GridBagConstraints.REMAINDER;

				gridBag.setConstraints(correctedDir, c);
				jProjectSettingsPane.add(correctedDir);
				// ////////////////////////////////////////////////////////////

				jProjectSettingsPane.setBorder(BorderFactory
						.createTitledBorder(null, "Project",
								TitledBorder.DEFAULT_JUSTIFICATION,
								TitledBorder.DEFAULT_POSITION, null,
								Color.black));
			}

		} catch (RemoteException re) {
			re.printStackTrace();
			IDPClientSingleton.getIDPClient().glassPaneFadeIn(
					IDPGlassPane.IDPDialogs.MessageDialog, "Error",
					"Couldn't read properties from the server",
					IDPMessageDlg.messageType.ERROR_MESSAGE);
		}

		return jProjectSettingsPane;
	}

	private JPanel getButtonsPane() {
		if (jButtonsPane == null) {
			jButtonsPane = new JPanel();
			jButtonsPane.setOpaque(false);

			jButtonsPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			jButtonsPane.add(getJSaveButton());
			jButtonsPane.add(getJCloseButton());

			if (ISBORDERED)
				jButtonsPane.setBorder(BorderFactory
						.createLineBorder(Color.green));
		}
		return jButtonsPane;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		parent.fadeOut();

		if ("Save".compareTo(e.getActionCommand()) == 0) {
			save();
		}
	}
		
	protected void save() {
		MessagingInterface connection = IDPConnection.getConnection();
		if (connection != null) {
			try {
				Properties properties = new Properties();
				String namespaceString = namespace.getText();
				String trainDirectory = trainDir.getText();
				String annotatorsDirectory = annotatorsDir.getText();
				String applyDirectory = applyDir.getText();
				String destinationDirectory = resultDir.getText();
				String correctedDirectory = correctedDir.getText();

				properties.setProperty("namespace", namespaceString);
				properties.setProperty("trainDirectory", trainDirectory);
				properties.setProperty("annotatorsDirectory",
						annotatorsDirectory);
				properties.setProperty("applyDirectory", applyDirectory);
				properties.setProperty("destinationDirectory",
						destinationDirectory);
				properties.setProperty("correctedDirectory",
						correctedDirectory);

				connection.setServerProperties(properties);

				IDPClientSingleton.getIDPClient().glassPaneFadeIn(
						IDPGlassPane.IDPDialogs.MessageDialog,
						"Information",
						"Properties have been saved succefully!",
						IDPMessageDlg.messageType.INFORMATION_MESSAGE);
			} catch (RemoteException re) {
				re.printStackTrace();
				IDPClientSingleton.getIDPClient().glassPaneFadeIn(
						IDPGlassPane.IDPDialogs.MessageDialog, "Error",
						"Couldn't save properties to the server",
						IDPMessageDlg.messageType.ERROR_MESSAGE);
			}
		}
	}

}
