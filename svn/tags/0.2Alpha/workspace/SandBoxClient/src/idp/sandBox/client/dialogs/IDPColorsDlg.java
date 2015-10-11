package idp.sandBox.client.dialogs;

import idp.sandBox.client.gui.SandBoxClient;
import idp.sandBox.client.gui.components.IDPGlassPane;
import idp.sandBox.client.managers.CheckTreeManager;
import idp.sandBox.client.managers.IDPClientSingleton;
import idp.sandBox.client.managers.IDPWindowManager;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.LayoutStyle;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.jdesktop.swingx.graphics.ShadowRenderer;

public class IDPColorsDlg extends JPanel implements ActionListener, TreeSelectionListener {

	public IDPGlassPane parent = null;

	private static int X_OFFSET = 30;
	private static int Y_OFFSET = 30;

	private static int CY_OFFSET = 5;

	private JLabel jLabel = null;
	private JTree jXMLTree = null;
	private JColorChooser jColorChooser = null;
	private JScrollPane jScrollPane = null;
	private JButton jCloseButton = null;
	private JButton jSaveButton = null;

	private JPanel jColorAndTreePane = null;
	private JPanel jButtonsPane = null;

	private Font textFont = null;
	
	private SandBoxClient client = null;

	private CheckTreeManager checkTreeManager;
	private String currentTagName = "";

	private BufferedImage shadow = null;

	public IDPColorsDlg() {
		setOpaque(false);

		// Font
		textFont = new Font("Arial", Font.BOLD, 15);
		setFont(textFont);
	}

	public void setLayouts(int width, int height) {
		jColorAndTreePane = new JPanel();
		jColorAndTreePane.setOpaque(false);
		GroupLayout layoutCT = new GroupLayout(jColorAndTreePane);
		jColorAndTreePane.setLayout(layoutCT);

		layoutCT.setHorizontalGroup(
			layoutCT.createSequentialGroup()
				.addComponent(getJScrollPane(),
								GroupLayout.PREFERRED_SIZE,
								GroupLayout.PREFERRED_SIZE,
								GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 5, 5)
				.addComponent(getJColorChooser(), 
								GroupLayout.PREFERRED_SIZE,
								GroupLayout.PREFERRED_SIZE,
								GroupLayout.PREFERRED_SIZE)
		);

		layoutCT.setVerticalGroup(
				layoutCT.createParallelGroup()
						.addComponent(getJScrollPane(),
								GroupLayout.PREFERRED_SIZE,
								GroupLayout.PREFERRED_SIZE,
								GroupLayout.PREFERRED_SIZE)
						.addComponent(getJColorChooser(),
								GroupLayout.PREFERRED_SIZE,
								GroupLayout.PREFERRED_SIZE,
								GroupLayout.PREFERRED_SIZE)
		);

		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);

		// layout.setAutoCreateGaps(Boolean.TRUE);
		// layout.setAutoCreateContainerGaps(Boolean.TRUE);

		layout.setHorizontalGroup(
			layout.createSequentialGroup()
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
						(int) (X_OFFSET + 0.025 * width),
						(int) (X_OFFSET + 0.025 * width))
				.addGroup(
					layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(getJLabel())
						.addComponent(jColorAndTreePane)
						.addComponent(getButtonsPane(),GroupLayout.PREFERRED_SIZE,GroupLayout.PREFERRED_SIZE,GroupLayout.PREFERRED_SIZE)
				)
		);

		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
						(int) (Y_OFFSET + 2*CY_OFFSET),
						(int) (Y_OFFSET + 2*CY_OFFSET))
				.addComponent(getJLabel())
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
						(int) (CY_OFFSET + CY_OFFSET),
						(int) (CY_OFFSET + CY_OFFSET))
				.addComponent(jColorAndTreePane)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 5, 5)
				.addComponent(getButtonsPane(),GroupLayout.PREFERRED_SIZE,GroupLayout.PREFERRED_SIZE,GroupLayout.PREFERRED_SIZE)
		);
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

		g2.setColor(new Color(255, 255, 255, 220));
		g2.fillRoundRect(x, y, w, h, arc, arc);

		g2.setStroke(new BasicStroke(3f));
		g2.setColor(Color.BLACK);
		g2.drawRoundRect(x, y, w, h, arc, arc);

		g2.dispose();
	}

	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);

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

	private JScrollPane getJScrollPane() {
		if (jScrollPane == null)
		{
			jScrollPane = new JScrollPane();
		}
		jScrollPane.setViewportView(getJXMLTree());
		return jScrollPane;
	}

	/**
	 * This method initializes jXMLTree
	 * 
	 * @return javax.swing.JTree
	 */
	private JTree getJXMLTree() {
		//client = IDPClientSingleton.getIDPClient();
		jXMLTree = IDPWindowManager.getJXMLTree();
		return jXMLTree;
	}

	/**
	 * This method initializes jColorChooser
	 * 
	 * @return JColorChooser
	 */
	private JColorChooser getJColorChooser() {
		if (jColorChooser == null) {
			jColorChooser = new JColorChooser(Color.white);
			jColorChooser.setPreferredSize(new Dimension(100, 100));
			jColorChooser.setOpaque(false);
		}
		return jColorChooser;
	}

	private JButton getJCloseButton() {
		if (jCloseButton == null) {
			jCloseButton = new JButton("Close");
			jCloseButton.addActionListener(this);
		}
		return jCloseButton;
	}

	private JButton getJSaveButton() {
		if (jSaveButton == null) {
			jSaveButton = new JButton("Save");
			jSaveButton.addActionListener(this);
		}
		return jSaveButton;
	}

	private JLabel getJLabel() {
		if (jLabel == null) {
			jLabel = new JLabel("Edit tags' hightligting colors");
			jLabel.setFont(textFont);
		}
		return jLabel;
	}

	public void updatePreferredSize(int width, int height) {
		width = width - 2 * X_OFFSET;
		height = height - 2 * Y_OFFSET;

		setLayouts(width, height);

		int w = (int) (0.25 * width);
		int h = (int) (height - 70 - 10);

		jScrollPane.setPreferredSize(new Dimension(w, h - 5));
		
		w = (int) (width - X_OFFSET);
		jLabel.setPreferredSize(new Dimension(w, 20));

		w = (int) (0.7 * width - 5);
		jColorChooser.setPreferredSize(new Dimension(w, h - 5));
		
		jButtonsPane.setPreferredSize(new Dimension((int)(0.95*width), 30));
	}
	
	public void showDialog(boolean bShow)
	{
		setVisible(bShow);
		
		if (bShow == true)
		{
			jXMLTree.addTreeSelectionListener(this);
			
			jColorChooser.getSelectionModel().addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
				    Color newColor = jColorChooser.getColor();
				    if (checkTreeManager != null)
				    {
				    	checkTreeManager.addTagColor(currentTagName, newColor);
				    	jXMLTree.repaint();
				    }
				}
			});
			
			DefaultMutableTreeNode xmlnode = (DefaultMutableTreeNode) jXMLTree.getLastSelectedPathComponent();
			if (xmlnode != null)
			{
				//////////////////////////////////////////////////////////
				// get selected tag name
				currentTagName = xmlnode.getUserObject().toString();
				currentTagName = currentTagName.replace(":", "_");
				//////////////////////////////////////////////////////////
				
				//////////////////////////////////////////////////////////
				checkTreeManager = IDPWindowManager.getCheckTreeManager();
				if (checkTreeManager != null) 
				{
					jColorChooser.setColor(checkTreeManager.getTagColor(currentTagName));
				}
				//////////////////////////////////////////////////////////
			}
		}
		else
		{
			jXMLTree.removeTreeSelectionListener(this);
			IDPWindowManager.setViewportView();
		}
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		
		//////////////////////////////////////////////////////////////////
		// determine the current pane and selected tag
		DefaultMutableTreeNode xmlnode = (DefaultMutableTreeNode) jXMLTree.getLastSelectedPathComponent();
		//////////////////////////////////////////////////////////////////
		
		//////////////////////////////////////////////////////////////////
		if (xmlnode == null) return;
		//////////////////////////////////////////////////////////////////
		
		//////////////////////////////////////////////////////////////////
		// get selected tag name
		currentTagName = xmlnode.getUserObject().toString();
		currentTagName = currentTagName.replace(":", "_");
		//////////////////////////////////////////////////////////////////
		
		//////////////////////////////////////////////////////////////////
		// get tree manager
		checkTreeManager = IDPWindowManager.getCheckTreeManager();
		if (checkTreeManager == null) 
		{
			System.out.println("[Error]: Couldn't get xml tree's CheckTreeManager.");
			return;
		}
		//////////////////////////////////////////////////////////////////
		
		//////////////////////////////////////////////////////////////////
		jColorChooser.setColor(checkTreeManager.getTagColor(currentTagName));
		//////////////////////////////////////////////////////////////////
	}
	
	private JPanel getButtonsPane() {
		if (jButtonsPane == null)
		{
			jButtonsPane = new JPanel();
			jButtonsPane.setOpaque(false);
			
			jButtonsPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			jButtonsPane.add(getJSaveButton());
			jButtonsPane.add(getJCloseButton());
		}
		return jButtonsPane;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		parent.fadeOut();
		
		IDPWindowManager.addTreeSelectionListener();
		
		checkTreeManager = IDPWindowManager.getCheckTreeManager();
		
		if ("Save".compareTo(e.getActionCommand()) == 0)
		{
			checkTreeManager.Apply();
		}
		else
		{
			checkTreeManager.Reset();
		}

		IDPClientSingleton.getIDPClient().updatePane();
	}
}