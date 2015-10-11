package idp.sandBox.client.dialogs;

import idp.sandBox.client.constants.Constants;
import idp.sandBox.client.managers.IDPSettingsManager;
import idp.sandBox.client.toolkit.RegexFormatter;

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
import java.io.IOException;
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

import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.jdesktop.swingx.graphics.ShadowRenderer;

public class IDPSettingsDlg extends AbstractSaveDialog implements
		ActionListener {
	private static final long serialVersionUID = 7632679778439244219L;

	private static final boolean ISBORDERED = false;

	private static int X_OFFSET = 30;
	private static int Y_OFFSET = 30;

	private static int CY_OFFSET = 5;

	private BufferedImage shadow = null;

	private JPanel jContentPane = null;
	private JPanel jConnectionPane = null;
	private JPanel jButtonsPane = null;

	private JLabel jLabel = null;
	private JButton jCloseButton = null;
	private JButton jSaveButton = null;

	private Font labelFont = null;
	private Font textFont = null;

	private JFormattedTextField ipAddress;
	private JFormattedTextField portNumber;

	public IDPSettingsDlg() {
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

	private JLabel getJLabel() {
		if (jLabel == null) {
			jLabel = new JLabel("Settings");
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

			jContentPane.add(getJConnectionPane());

			if (ISBORDERED)
				jContentPane.setBorder(BorderFactory
						.createLineBorder(Color.red));
		}
		return jContentPane;
	}

	private JPanel getJConnectionPane() {
		if (jConnectionPane == null) {
			jConnectionPane = new JPanel();

			jConnectionPane.setOpaque(false);

			GridBagLayout gridBag = new GridBagLayout();
			GridBagConstraints c = new GridBagConstraints();

			jConnectionPane.setLayout(gridBag);

			// c.weightx = 1.0;
			c.insets = new Insets(5, 10, 7, 10);

			// ////////////////////////////////////////////////////////////
			// server address
			JLabel jLabel1 = new JLabel("Server address:");
			jLabel1.setFont(textFont);

			gridBag.setConstraints(jLabel1, c);
			jConnectionPane.add(jLabel1);

			c.gridwidth = GridBagConstraints.REMAINDER;

			ipAddress = new JFormattedTextField(createIPFormatter());
			ipAddress.setValue(IDPSettingsManager.getServerAddress());
			ipAddress.setPreferredSize(new Dimension(120, 20));
			ipAddress.setFont(textFont);
			ipAddress.setBorder(BorderFactory.createLineBorder(Color.black, 2));
			subscriveSaveEnterAction(ipAddress);

			gridBag.setConstraints(ipAddress, c);
			jConnectionPane.add(ipAddress);
			// ////////////////////////////////////////////////////////////

			// ////////////////////////////////////////////////////////////
			// server port
			JLabel jLabel2 = new JLabel("Server port:");
			jLabel2.setFont(textFont);

			c.gridwidth = 1;
			gridBag.setConstraints(jLabel2, c);
			jConnectionPane.add(jLabel2);

			portNumber = new JFormattedTextField(createPortFormatter());
			portNumber.setValue(IDPSettingsManager.getServerPort());
			portNumber.setPreferredSize(new Dimension(120, 20));
			portNumber.setFont(textFont);
			portNumber
					.setBorder(BorderFactory.createLineBorder(Color.black, 2));
			portNumber.setAlignmentX(java.awt.Component.RIGHT_ALIGNMENT);
			subscriveSaveEnterAction(portNumber);

			gridBag.setConstraints(portNumber, c);
			jConnectionPane.add(portNumber);
			// ////////////////////////////////////////////////////////////

			// if (ISBORDERED)
			// jConnectionPane.setBorder(BorderFactory.createLineBorder(Color.ORANGE));
			/*
			 * jConnectionPane.setBorder(BorderFactory.createTitledBorder(null,
			 * "Connection", TitledBorder.DEFAULT_JUSTIFICATION,
			 * TitledBorder.DEFAULT_POSITION, null, Color.black));
			 */

		}
		return jConnectionPane;
	}

	private RegexFormatter createIPFormatter() {
		String digitFrom0to255 = "(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
		Pattern patern = Pattern.compile("^(?:" + digitFrom0to255 + "\\.){3}"
				+ digitFrom0to255 + "$");
		RegexFormatter ipFormatter = new RegexFormatter(patern);
		ipFormatter.setAllowsInvalid(false);
		ipFormatter.setOverwriteMode(false);
		return ipFormatter;
	}

	private RegexFormatter createPortFormatter() {
		Pattern patern = Pattern.compile("^[1-9][0-9][0-9][0-9]?[0-9]?$");
		RegexFormatter ipFormatter = new RegexFormatter(patern);
		ipFormatter.setAllowsInvalid(false);
		ipFormatter.setOverwriteMode(false);
		return ipFormatter;
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
		} else {
			// IDPSettingsManager.loadDefaults();
		}
	}

	protected void save() {
		parent.fadeOut();

		IDPSettingsManager.setServerAddress(ipAddress.getText());
		IDPSettingsManager.setServerPort(portNumber.getText());

		try {
			IDPSettingsManager.store(Constants.propertiesFileName);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}
