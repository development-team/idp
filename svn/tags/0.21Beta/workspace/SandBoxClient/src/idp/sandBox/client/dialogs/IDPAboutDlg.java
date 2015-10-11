package idp.sandBox.client.dialogs;

import idp.sandBox.client.constants.Constants;
import idp.sandBox.client.gui.SandBoxClient;
import idp.sandBox.client.gui.components.IDPGlassPane;
import idp.sandBox.client.gui.components.JLinkLabel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.jdesktop.swingx.graphics.ShadowRenderer;

public class IDPAboutDlg extends JPanel implements ActionListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4329037001945039181L;

	private static Logger log =  Logger.getLogger(IDPAboutDlg.class);

	private static final boolean ISBORDERED = false;
	
	private static int X_OFFSET = 30;
	private static int Y_OFFSET = 30;
	
	private static int CY_OFFSET = 5;
	
	public IDPGlassPane parent = null;
	
	private BufferedImage shadow = null;
	
	private JButton jCloseButton = null;
	
	private Image image;
	private int imageWidth, imageHeight;
	private BufferedImage doubleBuffer = null;
	
	private Font titleFont = null;
	private Font labelFont = null;
	private Font labelFont2 = null;	
	private Font textFont = null;
	private Font textFont2 = null;
	
	private JPanel jButtonsPane = null;
	private JPanel jLabelAndImagePane = null;
	private JPanel jLinksPane = null;
	
	private JLabel jTitleLabel = null;
	private JLabel jNameLabel = null;
	private JLabel jVersionLabel = null;
	private JLabel jAboutLabel = null;
	private JLabel jAuthorsLabel = null;
	
	public IDPAboutDlg() {
		setOpaque(false);

		// Font
		titleFont = new Font("Arial", Font.BOLD, 16);
		labelFont = new Font("Arial", Font.BOLD, 15);
		labelFont2 = new Font("Arial", Font.PLAIN, 13);
		textFont = new Font("Arial", Font.PLAIN, 12);
		textFont2 = new Font("Arial", Font.BOLD, 12);
		
		//////////////////////////////////////////////////////////////
		// Load image from resource
		URL imageURL = null;
		imageURL = SandBoxClient.class
				.getResource(Constants.resourcePath + "/idp2.png");
		
		if (imageURL != null)
		{
			image = Toolkit.getDefaultToolkit().getImage(imageURL);

			imageWidth = image.getWidth(this);
			imageHeight = image.getHeight(this);
		}
		else
		{
			imageWidth = 0;
			imageHeight = 0;
		}
		//////////////////////////////////////////////////////////////
		
		imageWidth /= 1.5;
		imageHeight /= 1.5;

		this.getInputMap().put(KeyStroke.getKeyStroke("F1"), "escKeyPressed");
		this.getActionMap().put("escKeyPressed", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				System.out.println(" ESC pressed ");
			}
		});
		this.requestFocus();
		this.requestFocusInWindow();
	}
	
	public void setLayouts(int width, int height) {
		GroupLayout groupLayout = new GroupLayout(this);
		setLayout(groupLayout);
		
		groupLayout.setHorizontalGroup(
			groupLayout.createSequentialGroup()
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
						(int) (X_OFFSET + 0.025 * width),
						(int) (X_OFFSET + 0.025 * width))
				.addGroup(
					groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(getJTitleLabel(),GroupLayout.PREFERRED_SIZE,GroupLayout.PREFERRED_SIZE,GroupLayout.PREFERRED_SIZE)
						.addComponent(getLabelImagePane(),GroupLayout.PREFERRED_SIZE,GroupLayout.PREFERRED_SIZE,GroupLayout.PREFERRED_SIZE)
						.addComponent(getLinksPane(),GroupLayout.PREFERRED_SIZE,GroupLayout.PREFERRED_SIZE,GroupLayout.PREFERRED_SIZE)
						.addComponent(getButtonsPane(),GroupLayout.PREFERRED_SIZE,GroupLayout.PREFERRED_SIZE,GroupLayout.PREFERRED_SIZE)
				)						
				
				
		);
		
		groupLayout.setVerticalGroup(
			groupLayout.createSequentialGroup()
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
						(int) (Y_OFFSET + 2*CY_OFFSET),
						(int) (Y_OFFSET + 2*CY_OFFSET))
				.addComponent(getJTitleLabel(),GroupLayout.PREFERRED_SIZE,GroupLayout.PREFERRED_SIZE,GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
						(int) (2*CY_OFFSET),
						(int) (2*CY_OFFSET))
				.addComponent(getLabelImagePane(),GroupLayout.PREFERRED_SIZE,GroupLayout.PREFERRED_SIZE,GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
						(int) (2*CY_OFFSET),
						(int) (2*CY_OFFSET))
				.addComponent(getLinksPane(),GroupLayout.PREFERRED_SIZE,GroupLayout.PREFERRED_SIZE,GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, CY_OFFSET, CY_OFFSET)
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

		//////////////////////////////////////////////////////////////////
		// fill content
		g2.setColor(new Color(255, 255, 255, 220));
		g2.fillRoundRect(x, y, w, h, arc, arc);
		//////////////////////////////////////////////////////////////////

		//////////////////////////////////////////////////////////////////
		// draw border
		g2.setStroke(new BasicStroke(3f));
		g2.setColor(Color.BLACK);
		//g2.setColor(Color.GRAY);
		g2.drawRoundRect(x, y, w, h, arc, arc);
		//////////////////////////////////////////////////////////////////
		
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
		int h = (int) (0.5 * height - 15 - 20);
		
		jTitleLabel.setPreferredSize(new Dimension(w, 20));
		
		jLabelAndImagePane.setPreferredSize(new Dimension(w, h - 3*CY_OFFSET - 30));
		
		jLinksPane.setPreferredSize(new Dimension(w, h - 3*CY_OFFSET + 30));
		
		jButtonsPane.setPreferredSize(new Dimension(w, 30));
	}
	
	public void showDialog(boolean bShow)
	{
		setVisible(bShow);
	}
	
	public void loadWaterMark() {
		Rectangle r = new Rectangle(imageWidth, imageHeight);
		
		image = image.getScaledInstance(imageWidth, imageWidth, Image.SCALE_SMOOTH);
		MediaTracker media = new MediaTracker(this);
		media.addImage(image, 0);
		try
		{
			media.waitForID(0);
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}

		doubleBuffer = new BufferedImage(r.width, r.height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2D = (Graphics2D) doubleBuffer.getGraphics();
		
		g2D.drawImage(image, 0, 0, r.width, r.height, this);

		g2D.dispose();
	}
	
	private JButton getJCloseButton() {
		if (jCloseButton == null) {
			jCloseButton = new JButton("OK");
			jCloseButton.addActionListener(this);
			jCloseButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "esc");
			jCloseButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "esc");
			jCloseButton.getActionMap().put("esc", new AbstractAction () {

				private static final long serialVersionUID = 1824421125879350547L;

				public void actionPerformed(ActionEvent e) {
					log.info(" ESC pressed ");
					parent.fadeOut();
				}
			});
		}
		return jCloseButton;
	}
	
	private JPanel getLabelImagePane() {
		if (jLabelAndImagePane == null)
		{
			jLabelAndImagePane = new JPanel();
			jLabelAndImagePane.setOpaque(false);
			
			jLabelAndImagePane.setLayout(null);
			
			jLabelAndImagePane.add(getJNameLabel());
			jNameLabel.setBounds(new Rectangle(16,0,100,30));
			
			jLabelAndImagePane.add(getJVersionLabel());
			jVersionLabel.setBounds(new Rectangle(16,20,200,30));

			jLabelAndImagePane.add(getJAboutLabel());
			jAboutLabel.setBounds(new Rectangle(16,40,200,30));

			jLabelAndImagePane.add(getJAuthorsLabel());
			jAuthorsLabel.setBounds(new Rectangle(16,70,200,30));

			//////////////////////////////////////////////////////////////////
			// draw IDP image
			if (doubleBuffer == null)
			{
				loadWaterMark();
			}		
			int dx = 320;
			int dy = -10;
			JLabel imageContainer = new JLabel();
			imageContainer.setIcon(new ImageIcon(doubleBuffer));
			imageContainer.setBounds(new Rectangle(dx,dy,imageWidth,imageHeight));
			jLabelAndImagePane.add(imageContainer);
			//////////////////////////////////////////////////////////////////
			
			if (ISBORDERED) jLabelAndImagePane.setBorder(BorderFactory.createLineBorder(Color.red));
		}
		return jLabelAndImagePane;
	}
	
	private JPanel getLinksPane() {
		if (jLinksPane == null)
		{
			jLinksPane = new JPanel();
			jLinksPane.setOpaque(false);
			
			jLinksPane.setLayout(null);
			
			JLabel jLink1 = new JLabel("Links:");
			jLink1.setFont(textFont2);
			//jLink1.setForeground(Color.gray);
			jLink1.setForeground(Color.black);
			jLink1.setBounds(16, 0, 100, 30);
			jLinksPane.add(jLink1);
			
			/*
			JLabel jLink2 = new JLabel("minorthird - textmining toolkit by CMU");
			jLink2.setFont(textFont);
			jLink2.setForeground(Color.gray);
			jLink2.setBounds(16, 25, 300, 30);
			jLinksPane.add(jLink2);
			*/

			JLabel jLink2 = new JLabel("Toolkits");
			jLink2.setFont(textFont);
			jLink2.setForeground(Color.gray);
			jLink2.setBounds(16, 25, 300, 30);
			jLinksPane.add(jLink2);
			
			String url = "http://minorthird.sourceforge.net";
			JLinkLabel jLink3 = new JLinkLabel("Minorthird - textmining toolkit by CMU",null,url);
			jLink3.setFont(textFont);
			jLink3.setForeground(Color.blue);
			jLink3.setBounds(16, 45, 300, 30);
			jLinksPane.add(jLink3);

			/*
			JLabel jLink4 = new JLabel("JGAP - Java genetic algorithms package");
			jLink4.setFont(textFont);
			jLink4.setForeground(Color.gray);
			jLink4.setBounds(16, 70, 300, 30);
			jLinksPane.add(jLink4);
			*/
			
			url = "http://jgap.sourceforge.net/";
			JLinkLabel jLink5 = new JLinkLabel("JGAP - Java genetic algorithms package",null,url);
			jLink5.setFont(textFont);
			jLink5.setForeground(Color.blue);
			jLink5.setBounds(16, 65, 300, 30);
			jLinksPane.add(jLink5);
			
			url = "http://ganttproject.biz/";
			JLinkLabel jLink9 = new JLinkLabel("Gantt project scheduling and management tool",null,url);
			jLink9.setFont(textFont);
			jLink9.setForeground(Color.blue);
			jLink9.setBounds(16, 85, 300, 30);
			jLinksPane.add(jLink9);
			
			url = "http://www.eclipse.org/";
			JLinkLabel jLink10 = new JLinkLabel("Eclipse - an open development platform",null,url);
			jLink10.setFont(textFont);
			jLink10.setForeground(Color.blue);
			jLink10.setBounds(16, 105, 300, 30);
			jLinksPane.add(jLink10);

			url = "https://swingx.dev.java.net/";
			JLinkLabel jLink13 = new JLinkLabel("SwingX - Swing GUI toolkit",null,url);
			jLink13.setFont(textFont);
			jLink13.setForeground(Color.blue);
			jLink13.setBounds(16, 125, 300, 30);
			jLinksPane.add(jLink13);

			JLabel jLink6 = new JLabel("Icon packages");
			jLink6.setFont(textFont);
			jLink6.setForeground(Color.gray);
			jLink6.setBounds(320, 25, 300, 30);
			jLinksPane.add(jLink6);
			
			url = "http://www.vistaicons.com/";
			JLinkLabel jLink7 = new JLinkLabel(url,null,url);
			jLink7.setFont(textFont);
			jLink7.setForeground(Color.blue);
			jLink7.setBounds(320, 45, 300, 30);
			jLinksPane.add(jLink7);
			
			url = "http://www.vistaico.com";
			JLinkLabel jLink8 = new JLinkLabel("VistaICO.com",null,url);
			jLink8.setFont(textFont);
			jLink8.setForeground(Color.blue);
			jLink8.setBounds(320, 65, 300, 30);
			jLinksPane.add(jLink8);
			
			JLabel jLink12 = new JLabel("+");
			jLink12.setFont(textFont);
			jLink12.setForeground(Color.gray);
			jLink12.setBounds(320, 90, 100, 30);
			jLinksPane.add(jLink12);
			
			url = "http://www.scala-lang.org/";
			JLinkLabel jLink11 = new JLinkLabel("The Scala Programming Language",null,url);
			jLink11.setFont(textFont);
			jLink11.setForeground(Color.blue);
			jLink11.setBounds(320, 105, 300, 30);
			jLinksPane.add(jLink11);			
			
			if (ISBORDERED) jLinksPane.setBorder(BorderFactory.createLineBorder(Color.blue));
		}
		return jLinksPane;
	}	
	
	private JPanel getButtonsPane() {
		if (jButtonsPane == null)
		{
			jButtonsPane = new JPanel();
			jButtonsPane.setOpaque(false);
			
			jButtonsPane.setLayout(new FlowLayout());
			jButtonsPane.add(getJCloseButton());
			
			if (ISBORDERED) jButtonsPane.setBorder(BorderFactory.createLineBorder(Color.green));
		}
		return jButtonsPane;
	}	
	
	private JLabel getJTitleLabel() {
		if (jTitleLabel == null) {
			jTitleLabel = new JLabel("About");
			jTitleLabel.setFont(titleFont);
			jTitleLabel.setForeground(Color.black);
			
			if (ISBORDERED) jTitleLabel.setBorder(BorderFactory.createLineBorder(Color.magenta));
		}
		return jTitleLabel;
	}
	
	private JLabel getJNameLabel() {
		if (jNameLabel == null) {
			jNameLabel = new JLabel("IDP");
			jNameLabel.setFont(labelFont);
			jNameLabel.setForeground(Color.gray);
		}
		return jNameLabel;
	}

	private JLabel getJVersionLabel() {
		if (jVersionLabel == null) {
			jVersionLabel = new JLabel("Version 0.2 Aplha");
			jVersionLabel.setFont(labelFont2);
			jVersionLabel.setForeground(Color.gray);
		}
		return jVersionLabel;
	}
	
	private JLabel getJAboutLabel() {
		if (jAboutLabel == null) {
			jAboutLabel = new JLabel("Structuring semi-structured texts");
			jAboutLabel.setFont(textFont);
			jAboutLabel.setForeground(Color.gray);
		}
		return jAboutLabel;
	}
	
	private JLabel getJAuthorsLabel() {
		if (jAuthorsLabel == null) {
			jAuthorsLabel = new JLabel("by Max Talanov, Ayrat Natfullin");
			jAuthorsLabel.setFont(textFont);
			jAuthorsLabel.setForeground(Color.gray);
		}
		return jAuthorsLabel;
	}	

	@Override
	public void actionPerformed(ActionEvent e) {
		log.info("Action performed");
		parent.fadeOut();
	}
	
}
