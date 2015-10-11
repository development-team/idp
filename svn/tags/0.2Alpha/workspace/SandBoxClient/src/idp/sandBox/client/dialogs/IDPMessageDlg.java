package idp.sandBox.client.dialogs;

import idp.sandBox.client.constants.Constants;
import idp.sandBox.client.gui.SandBoxClient;
import idp.sandBox.client.gui.components.IDPGlassPane;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle;

import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.jdesktop.swingx.graphics.ShadowRenderer;

public class IDPMessageDlg extends JPanel implements ActionListener {
	
	public enum messageType
	{
		ERROR_MESSAGE, INFORMATION_MESSAGE, WARNING_MESSAGE, QUESTION_MESSAGE
	}
	
	private static int iconWidth = 64;
	private static int iconHeight = 64;
	
	private static int X_OFFSET = 30;
	private static int Y_OFFSET = 30;
	
	private static int CY_OFFSET = 5;
	
	public IDPGlassPane parent = null;
	
	private BufferedImage shadow = null;
	
	private JButton jCloseButton = null;
	
	private Font titleFont = null;
	private Font textFont = null;
	
	private JLabel jTitleLabel = null;
	private JPanel jMessagePane = null;
	private JLabel jMessageLabel = null;
	private JPanel jButtonsPane = null;
	
	private JLabel jIcon = null;
	
	private String title = null;
	private String message = null;
	
	private JTextArea jMessage = null;
	
	private messageType jMessageType;	
	
	public IDPMessageDlg(String title, String message)
	{
		this.title = title;
		this.message = message;

		// Font
		titleFont = new Font("Arial", Font.BOLD, 16);
		textFont = new Font("Times New Roman", Font.PLAIN, 14);
	}
	
	public void setTitle(String title)
	{
		this.title = title;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}
	
	public String getMessage()
	{
		return this.message;
	}
	
	public void setMessageType(messageType type)
	{
		jMessageType = type;
		getJIcon();
	}
	
	public void setLayouts(int width, int height) {
		GroupLayout groupLayout = new GroupLayout(this);
		setLayout(groupLayout);
		
		groupLayout.setHorizontalGroup(
			groupLayout.createSequentialGroup()
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
						(int) (X_OFFSET + 20),
						(int) (X_OFFSET + 20))
				.addGroup(
					groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(getJTitleLabel(),GroupLayout.PREFERRED_SIZE,GroupLayout.PREFERRED_SIZE,GroupLayout.PREFERRED_SIZE)
						.addComponent(getMessagePane(),GroupLayout.PREFERRED_SIZE,GroupLayout.PREFERRED_SIZE,GroupLayout.PREFERRED_SIZE)
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
				.addComponent(getMessagePane(),GroupLayout.PREFERRED_SIZE,GroupLayout.PREFERRED_SIZE,GroupLayout.PREFERRED_SIZE)
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
		int h = (int) (height - 20 - 30);
		
		jTitleLabel.setPreferredSize(new Dimension(w, 20));
		
		jMessagePane.setPreferredSize(new Dimension(w, h - 4*CY_OFFSET - 5 - 5)); // minus gaps
		
		jButtonsPane.setPreferredSize(new Dimension(w, 30));		
	}
	
	public void showDialog(boolean bShow)
	{
		setVisible(bShow);
	}
	
	private JLabel getJTitleLabel() {
		if (jTitleLabel == null) {
			jTitleLabel = new JLabel(title);
			jTitleLabel.setFont(titleFont);
			jTitleLabel.setForeground(Color.BLACK);
		}
		return jTitleLabel;
	}
	
	private JPanel getButtonsPane() {
		if (jButtonsPane == null)
		{
			jButtonsPane = new JPanel();
			jButtonsPane.setOpaque(false);
			
			jButtonsPane.setLayout(new FlowLayout());
			jButtonsPane.add(getJCloseButton());
			
		}
		return jButtonsPane;
	}	
	
	private JButton getJCloseButton() {
		if (jCloseButton == null) {
			jCloseButton = new JButton("OK");
			jCloseButton.addActionListener(this);
		}
		return jCloseButton;
	}
	
	private JPanel getMessagePane() {
		if (jMessagePane == null)
		{
			jMessagePane = new JPanel();
			jMessagePane.setOpaque(false);
			
			jMessagePane.setLayout(null);
			
			int dx = 10;
			
			JLabel icon = getJIcon();
			icon.setBounds(dx, 0, iconWidth, iconHeight);
			jMessagePane.add(icon);
			
			int gapX = 20;
			
			jMessage = new JTextArea(message);
			jMessage.setOpaque(false);
			jMessage.setEditable(false);
			jMessage.setFont(textFont);
			jMessage.setForeground(Color.black);
			
			// TODO: calculating width
			jMessage.setBounds(dx + iconWidth + gapX, 10, 250, 80);

			//jMessage.setBorder(BorderFactory.createLineBorder(Color.green));
			
			jMessagePane.add(jMessage);
		}
		return jMessagePane;
	}
	
	private JLabel getJIcon()
	{
		if (jIcon == null)
		{
			jIcon = new JLabel("");
		}
		
		if (jMessageType == messageType.WARNING_MESSAGE)
			jIcon.setIcon(getWarningImageIcon());
		else if (jMessageType == messageType.ERROR_MESSAGE)
			jIcon.setIcon(getErrorImageIcon());
		else if (jMessageType == messageType.INFORMATION_MESSAGE)
			jIcon.setIcon(getInfoImageIcon());
		
		return jIcon;
	}
	
	private ImageIcon getWarningImageIcon() {

		MediaTracker media = new MediaTracker(this);
		
		//////////////////////////////////////////////////////////////
		// Load image from resource
		URL imageURL = null;
		imageURL = SandBoxClient.class
				.getResource(Constants.resourcePath + "/Warning.png");
		
		Image image = null;		
		if (imageURL != null)
		{
			image = Toolkit.getDefaultToolkit().getImage(imageURL);
		}
		else return new ImageIcon();
		//////////////////////////////////////////////////////////////
		
		media.addImage(image, 0);
		
		ImageIcon res = null;
		try
		{
			media.waitForID(0);
			res = new ImageIcon(image.getScaledInstance(iconWidth, iconHeight, Image.SCALE_SMOOTH));
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
		
		return res;
	}
	
	private ImageIcon getErrorImageIcon() {

		MediaTracker media = new MediaTracker(this);
		
		//////////////////////////////////////////////////////////////
		// Load image from resource
		URL imageURL = null;
		imageURL = IDPMessageDlg.class
				.getResource(Constants.resourcePath + "/Error.png");
		
		Image image = null;		
		if (imageURL != null)
		{
			image = Toolkit.getDefaultToolkit().getImage(imageURL);
		}
		else return new ImageIcon();
		//////////////////////////////////////////////////////////////
		
		media.addImage(image, 0);
		
		ImageIcon res = null;
		try
		{
			media.waitForID(0);
			res = new ImageIcon(image.getScaledInstance(iconWidth, iconHeight, Image.SCALE_SMOOTH));
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
		
		return res;
	}
	
	private ImageIcon getInfoImageIcon() {

		MediaTracker media = new MediaTracker(this);
		
		//////////////////////////////////////////////////////////////
		// Load image from resource
		URL imageURL = null;
		imageURL = SandBoxClient.class
				.getResource(Constants.resourcePath + "/Information.png");
		
		Image image = null;		
		if (imageURL != null)
		{
			image = Toolkit.getDefaultToolkit().getImage(imageURL);
		}
		else return new ImageIcon();
		//////////////////////////////////////////////////////////////
				
		media.addImage(image, 0);
		
		ImageIcon res = null;
		try
		{
			media.waitForID(0);
			res = new ImageIcon(image.getScaledInstance(iconWidth, iconHeight, Image.SCALE_SMOOTH));
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
		
		return res;
	}
	
	public void updateTitle()
	{
		if (jTitleLabel != null) 
		{
			jTitleLabel.setText(title);
		}
	}
	
	public void updateMessage()
	{
		if (jMessage != null) 
		{
			jMessage.setText(message);
			if (message.length() > 30)
				jMessage.setBounds(10 + iconWidth + 20, 10, 380, 80);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		parent.fadeOut();
	}
}
