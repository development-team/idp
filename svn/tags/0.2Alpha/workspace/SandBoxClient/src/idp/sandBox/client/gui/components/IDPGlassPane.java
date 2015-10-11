package idp.sandBox.client.gui.components;

import idp.sandBox.client.constants.Constants;
import idp.sandBox.client.dialogs.IDPAboutDlg;
import idp.sandBox.client.dialogs.IDPColorsDlg;
import idp.sandBox.client.dialogs.IDPMessageDlg;
import idp.sandBox.client.dialogs.IDPPropertiesDlg;
import idp.sandBox.client.dialogs.IDPSettingsDlg;
import idp.sandBox.client.gui.SandBoxClient;
import idp.sandBox.client.gui.components.tabs.TabbedPaneScroller;
import idp.sandBox.client.managers.IDPClientSingleton;
import idp.sandBox.client.toolkit.Transparency;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.jdesktop.swingx.image.GaussianBlurFilter;

public class IDPGlassPane extends JComponent implements MouseListener, MouseMotionListener {

	public enum IDPDialogs {
		none, ColorChooser, SettingsDialog, AboutDialog, MessageDialog, PropertiesDialog 
	}

	public static final double defaultWatermarkAlpha = 1.0;

	public static final int BUTTON_WIDTH = 30;
	public static final int BUTTON_HEIGHT = 19;

	private Image image;
	private int imageWidth, imageHeight;
	private BufferedImage doubleBuffer = null;
	private BufferedImage backBuffer;
	private BufferedImage blurEffectBuffer;
	private double alpha;
	private float alphaBlur = 1.0f;

	private IDPDialogs currentDialog = IDPDialogs.none;

	private IDPColorsDlg colorChooserDialog = null;
	
	private IDPAboutDlg aboutDialog = null;
	
	private IDPSettingsDlg settingsDialog = null;
	
	private IDPMessageDlg messageDialog = null;
	
	private IDPPropertiesDlg propertiesDialog = null;

	private int screen_width = 0;
	private int screen_height = 0;

	private TabbedPaneScroller jTabScroller;
	
	private boolean isWithLoadmark = true;
	
	public IDPGlassPane(double alpha) {
		
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

		this.alpha = alpha;

		colorChooserDialog = new IDPColorsDlg();
		colorChooserDialog.parent = this;
		colorChooserDialog.setVisible(false);
		add(colorChooserDialog);
		
		aboutDialog = new IDPAboutDlg();
		aboutDialog.parent = this;
		aboutDialog.setVisible(false);
		add(aboutDialog);		

		settingsDialog = new IDPSettingsDlg();
		settingsDialog.parent = this;
		settingsDialog.setVisible(false);
		add(settingsDialog);		

		messageDialog = new IDPMessageDlg("Warning","Couldn't get response from server");
		messageDialog.parent = this;
		messageDialog.setVisible(false);
		add(messageDialog);
		
		propertiesDialog = new IDPPropertiesDlg();
		propertiesDialog.parent = this;
		propertiesDialog.setVisible(false);
		add(propertiesDialog);
		
		jTabScroller = new TabbedPaneScroller();
		jTabScroller.setVisible(true);
		add(jTabScroller);

	}

	public void setScreenWidth(int screen_width) {
		this.screen_width = screen_width;
	}

	public void setScreenHeight(int screen_height) {
		this.screen_height = screen_height;
	}

	public void loadWaterMark() {
		Rectangle r = new Rectangle(imageWidth, imageHeight);

		doubleBuffer = new BufferedImage(r.width, r.height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2D = (Graphics2D) doubleBuffer.getGraphics();

		g2D.drawImage(image, 0, 0, r.width, r.height, this);

		doubleBuffer = Transparency.makeImageTranslucent(doubleBuffer, alpha);

		g2D.dispose();
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
		loadWaterMark();
		repaint();
	}

	public void paintComponent(Graphics g) {
		
		if (isWithLoadmark == false) return;
			
		if (currentDialog == IDPDialogs.none) {
			Rectangle r = new Rectangle(imageWidth, imageHeight);

			if (doubleBuffer == null) {
				loadWaterMark();
			}

			int width = getWidth();
			int height = getHeight();
			int dx = (width - r.width) / 2;
			int dy = (height - r.height) / 2;

			g.drawImage(doubleBuffer, dx, dy, r.width, r.height, this);

		} 
	}

	private void makeScreenShot() {
		JRootPane root = SwingUtilities.getRootPane(this);
		int width = getWidth();
		int height = getHeight();
		//backBuffer = GraphicsUtilities.createCompatibleImage(width, height);
		backBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2D = backBuffer.createGraphics();
		root.paint(g2D);
		g2D.dispose();
	}

	private void makeBlur() {

		int width = getWidth();

		makeScreenShot();

		blurEffectBuffer = backBuffer;

		blurEffectBuffer = GraphicsUtilities.createThumbnailFast(
				blurEffectBuffer, width / 2);
		blurEffectBuffer = new GaussianBlurFilter(10).filter(blurEffectBuffer,
				null);
	}

	public void setDlgBounds(Rectangle r) {
		if (currentDialog == IDPDialogs.ColorChooser)
		{
			colorChooserDialog.setBounds(r.x, r.y, r.width, r.height);
			colorChooserDialog.updatePreferredSize(r.width, r.height);
		}
		if (currentDialog == IDPDialogs.AboutDialog)
		{
			aboutDialog.setBounds(r.x, r.y, r.width, r.height);
			aboutDialog.updatePreferredSize(r.width, r.height);
		}
		if (currentDialog == IDPDialogs.SettingsDialog)
		{
			settingsDialog.setBounds(r.x, r.y, r.width, r.height);
			settingsDialog.updatePreferredSize(r.width, r.height);
		}
		if (currentDialog == IDPDialogs.MessageDialog)
		{
			messageDialog.setBounds(r.x, r.y, r.width, r.height);
			messageDialog.updatePreferredSize(r.width, r.height);
		}
		if (currentDialog == IDPDialogs.PropertiesDialog)
		{
			propertiesDialog.setBounds(r.x, r.y, r.width, r.height);
			propertiesDialog.updatePreferredSize(r.width, r.height);
		}

	}

	public void fadeIn() {
		//if (currentDialog != IDPDialogs.none) return;
		fadeIn(IDPDialogs.ColorChooser);
	}

	public void fadeIn(IDPDialogs dialog) {
		
		if (currentDialog != IDPDialogs.none) return;
		
		this.currentDialog = dialog;

		if (dialog == IDPDialogs.ColorChooser) {
			setVisible(false);
			makeBlur();
			setVisible(true);

			screen_width = IDPClientSingleton.getIDPClient().getScreenWidth();
			screen_height = IDPClientSingleton.getIDPClient().getScreenHeight();
			
			int w = (int) (screen_width * 0.6);
			int h = (int) (screen_height * 0.6);
			
			if (IDPClientSingleton.getIDPClient().getScreenWidth() < 1025) 
			{
				w = (int) (screen_width * 0.7);
				h = (int) (screen_height * 0.7);
			}
			
			if (IDPClientSingleton.getIDPClient().getScreenWidth() < 801) 
			{
				w = (int) (screen_width * 0.8);
				h = (int) (screen_height * 0.8);
			}
			
			int height = getHeight();
			int width = getWidth();
			
			int x = (int) ((width - w) / 2);
			int y = (int) ((height - h) / 2);
			
			setDlgBounds(new Rectangle(x, y, w, h));

			colorChooserDialog.showDialog(true);
		}
		
		if (dialog == IDPDialogs.SettingsDialog) {
			setVisible(false);
			makeScreenShot();
			setVisible(true);

			screen_width = IDPClientSingleton.getIDPClient().getScreenWidth();
			screen_height = IDPClientSingleton.getIDPClient().getScreenHeight();
			
			int w = (int) (screen_width * 0.4);
			int h = (int) (screen_height * 0.25);
			
			if (IDPClientSingleton.getIDPClient().getScreenWidth() < 1025) 
			{
				w = (int) (screen_width * 0.6);
				h = (int) (screen_height * 0.5);
			}
			
			if (IDPClientSingleton.getIDPClient().getScreenWidth() < 801) 
			{
				w = (int) (screen_width * 0.8);
				h = (int) (screen_height * 0.8);
			}
			
			int height = getHeight();
			int width = getWidth();
			
			int x = (int) ((width - w) / 2);
			int y = (int) ((height - h) / 2);
			
			setDlgBounds(new Rectangle(x, y, w, h));

			settingsDialog.showDialog(true);
		}
		
		if (dialog == IDPDialogs.AboutDialog) {
			setVisible(false);
			makeScreenShot();
			setVisible(true);

			screen_width = IDPClientSingleton.getIDPClient().getScreenWidth();
			screen_height = IDPClientSingleton.getIDPClient().getScreenHeight();
			
			int w = (int) (screen_width * 0.5);
			int h = (int) (screen_height * 0.42);
			
			if (IDPClientSingleton.getIDPClient().getScreenWidth() < 1025) 
			{
				w = (int) (screen_width * 0.65);
				h = (int) (screen_height * 0.6);
			}
			
			if (IDPClientSingleton.getIDPClient().getScreenWidth() < 801) 
			{
				w = (int) (screen_width * 0.82);
				h = (int) (screen_height * 0.8);
			}
			
			int height = getHeight();
			int width = getWidth();
			
			int x = (int) ((width - w) / 2);
			int y = (int) ((height - h) / 2);
			
			setDlgBounds(new Rectangle(x, y, w, h));

			aboutDialog.showDialog(true);
		}
		
		if (dialog == IDPDialogs.MessageDialog) {
			setVisible(false);
			makeScreenShot();
			setVisible(true);

			screen_width = IDPClientSingleton.getIDPClient().getScreenWidth();
			screen_height = IDPClientSingleton.getIDPClient().getScreenHeight();
			
			int w = (int) (380);
			int h = (int) (210);
			
			// TODO: calculating width
			int len = messageDialog.getMessage().length();
			if (len > 30)
			{
				w = 450;
			}
			
			int height = getHeight();
			int width = getWidth();
			
			int x = (int) ((width - w) / 2);
			int y = (int) ((height - h) / 2);
			
			setDlgBounds(new Rectangle(x, y, w, h));

			messageDialog.showDialog(true);
		}

		if (dialog == IDPDialogs.PropertiesDialog) {
			setVisible(false);
			makeScreenShot();
			setVisible(true);

			screen_width = IDPClientSingleton.getIDPClient().getScreenWidth();
			screen_height = IDPClientSingleton.getIDPClient().getScreenHeight();
			
			int w = (int) (screen_width * 0.5);
			int h = (int) (screen_height * 0.42);
			
			if (IDPClientSingleton.getIDPClient().getScreenWidth() < 1025) 
			{
				w = (int) (screen_width * 0.6);
				h = (int) (screen_height * 0.5);
			}
			
			if (IDPClientSingleton.getIDPClient().getScreenWidth() < 801) 
			{
				w = (int) (screen_width * 0.8);
				h = (int) (screen_height * 0.8);
			}
			
			int height = getHeight();
			int width = getWidth();
			
			int x = (int) ((width - w) / 2);
			int y = (int) ((height - h) / 2);
			
			setDlgBounds(new Rectangle(x, y, w, h));

			if (propertiesDialog.updateProperties() == false)
			{
				// we couldn't get properties from server
				return;
			}
			
			propertiesDialog.showDialog(true);
		}
		
		if (dialog != IDPDialogs.none)
		{
			addMouseListener(this);
			addMouseMotionListener(this);
		}
	}
	
	public void fadeIn(IDPDialogs dialog, String title, String message, IDPMessageDlg.messageType messageType) {
		if (currentDialog != IDPDialogs.none) return;
		
		if (dialog == IDPDialogs.MessageDialog)
		{
			messageDialog.setTitle(title);
			messageDialog.updateTitle();
			
			messageDialog.setMessage(message);
			messageDialog.updateMessage();

			messageDialog.setMessageType(messageType);
		}
		fadeIn(dialog);
	}

	public void fadeOut() {
		
		if (currentDialog == IDPDialogs.ColorChooser)
		{
			colorChooserDialog.showDialog(false);
		}
		else if (currentDialog == IDPDialogs.SettingsDialog)
		{
			settingsDialog.showDialog(false);
		}
		else if (currentDialog == IDPDialogs.AboutDialog)
		{
			aboutDialog.showDialog(false);	
		}
		else if (currentDialog == IDPDialogs.MessageDialog)
		{
			messageDialog.showDialog(false);	
		}
		else if (currentDialog == IDPDialogs.PropertiesDialog)
		{
			propertiesDialog.showDialog(false);	
		}
		
		this.currentDialog = IDPDialogs.none;			
			
		MouseListener[] listeners = getMouseListeners();
		for (int i = 0; i < listeners.length; i++)
		{
			removeMouseListener(this);	
		}
		
		MouseMotionListener[] listeners2 = getMouseMotionListeners();
		for (int i = 0; i < listeners2.length; i++)
		{
			removeMouseMotionListener(this);	
		}

		repaint();
	}

	public void setTabButtonLocation(int x, int y) {
		jTabScroller.setBounds(x - BUTTON_WIDTH - 2, y + 3, BUTTON_WIDTH,
				BUTTON_HEIGHT);
		repaint();
	}

	public void setTabScrollerInfo(int value) {
		jTabScroller.setHiddenTabCount(value);
		if (value == 0) {
			jTabScroller.setVisible(false);
		} else {
			jTabScroller.setVisible(true);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// do nothing!
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// do nothing!
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// do nothing!
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// do nothing!
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// do nothing!
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// do nothing!
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// do nothing!
	}
	
	/**
	 * Fade out \ fade in.
	 */
	public void repaintAll()
	{
		IDPDialogs tempCurrentDialog = this.currentDialog;
		fadeOut();
		fadeIn(tempCurrentDialog);
	}
	
	public void discardCurrentDialog()
	{
		this.currentDialog = IDPDialogs.none;
	}

	public void setWithLoadmark(boolean isWithLoadmark) {
		this.isWithLoadmark = isWithLoadmark;
	}
}
