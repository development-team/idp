package idp.sandBox.client.gui.components.tabs;

import idp.sandBox.client.constants.Constants;
import idp.sandBox.client.gui.SandBoxClient;
import idp.sandBox.client.toolkit.Transparency;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

/**
 * Enhanced tabben pane UI.
 * Have lots of restrictions on account of having been developed for IDP purposes only.
 * 
 * @author Natfullin Airat
 *
 */public class IDPTabbedPaneUI extends BasicTabbedPaneUI {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Tab height
	 */
	private static final int TAB_HEIGHT = 21;

	public static final int HEAD_WIDTH = 6;
	
	/**
	 * Images for tabs. Every tabs is made of head image, background, 
	 */
	private Image ImageTail;
	private Image ImageTailRed;
	private Image ImageTailU;
	private Image ImageTailW;
	
	private Image ImageBG;
	private Image ImageHead;
	private Image ImageHeadU;
	
	/**
	 * Images' dimensions.
	 */
	private int imageTailWidth;
	private int imageTailHeight;
	private int imageTailWidthU;
	private int imageTailHeightU;	
	private int imageTailWidthW;
	private int imageTailHeightW;	

	private int imageHeadWidth;
	private int imageHeadHeight;
	private int imageHeadWidthU;
	private int imageHeadHeightU;
	
	private Integer tabRects[] = new Integer[50];
	
	/**
	 * Close button status enum. 
	 */
	enum eCloseBtnStatus
	{
		outside,
		over,
		pressed
	}
	
	/**
	 * Close button status
	 * Is changed according to mouse cursor position.
	 */
	private eCloseBtnStatus closeBtnStatus = eCloseBtnStatus.outside;
	
	/**
	 *  Is used to reset close button status. 
	 */
	private boolean bMouseMoved = false;
	
	/**
	 * The index of current selected (focused) tab.
	 */
	private int selectedTabIndex = -1;
	
	/**
	 * TODO
	 */	
	private JTabbedPane parentTabPane;
	
	/**
	 * TODO
	 */
	public Rectangle rect;
	
	/**
	 * TODO
	 */
	public IDPTabbedPaneUI(JTabbedPane parentTabPane)
	{
		this.parentTabPane = parentTabPane;
		
	}
	
	/**
	 * TODO
	 */
	protected int calculateTabHeight(int tabPlacement, int tabIndex,
			int fontHeight) {
		return TAB_HEIGHT;
	}

	protected void installDefaults() {
		super.installDefaults();
		
		//////////////////////////////////////////////////////////////////
		// set insets

		//contentBorderInsets = new Insets(5, 5, 5, 5);
		
		contentBorderInsets = new Insets(-1, 3, 5, 5);
		selectedTabPadInsets = new Insets(3, 3, 3, 3);
		
		//tabAreaInsets = new Insets(-10, -10, -10, -10);
		//tabPane.setFocusable(false);
		
		//////////////////////////////////////////////////////////////////
		
		//////////////////////////////////////////////////////////////////
		// add listeners
		tabPane.addMouseMotionListener(new IDPMouseMotionListener());
		tabPane.addMouseListener(new IDPMouseListener());
		//////////////////////////////////////////////////////////////////
		
		//////////////////////////////////////////////////////////////////
		// Load Images
		MediaTracker media = new MediaTracker(tabPane);
		try
		{
			//////////////////////////////////////////////////////////////
			// Load image from resource
			URL imageURL = null;
			imageURL = SandBoxClient.class
					.getResource(Constants.resourcePath + "/flourish.png");
			
			ImageIcon image = null;
			if (imageURL != null)
			{
				image = new ImageIcon(Toolkit.getDefaultToolkit().getImage(imageURL));
			}
			//////////////////////////////////////////////////////////////

			imageTailWidth = image.getIconWidth();
			imageTailHeight = image.getIconHeight();
			ImageTail = Transparency.makeColorTransparent(image.getImage(), Color.yellow);
			media.addImage(ImageTail, 0);
		
			media.waitForID(0);
			
			//////////////////////////////////////////////////////////////
			// Load image from resource
			imageURL = SandBoxClient.class
					.getResource(Constants.resourcePath + "/flourish_wcross.png");
			
			if (imageURL != null)
			{
				image = new ImageIcon(Toolkit.getDefaultToolkit().getImage(imageURL));
			}
			//////////////////////////////////////////////////////////////
			
			imageTailWidthW= image.getIconWidth();
			imageTailHeightW = image.getIconHeight();
			ImageTailW = Transparency.makeColorTransparent(image.getImage(), Color.yellow);
			media.addImage(ImageTailW, 1);
			
			media.waitForID(1);
			
			//////////////////////////////////////////////////////////////
			// Load image from resource
			imageURL = SandBoxClient.class
					.getResource(Constants.resourcePath + "/flourish_red.png");
			
			if (imageURL != null)
			{
				image = new ImageIcon(Toolkit.getDefaultToolkit().getImage(imageURL));
			}
			//////////////////////////////////////////////////////////////
			
			ImageTailRed = Transparency.makeColorTransparent(image.getImage(), Color.yellow);
			media.addImage(ImageTailRed, 5);

			media.waitForID(5);

			//////////////////////////////////////////////////////////////
			// Load image from resource
			imageURL = SandBoxClient.class
					.getResource(Constants.resourcePath + "/tabbg.png");
			
			if (imageURL != null)
			{
				image = new ImageIcon(Toolkit.getDefaultToolkit().getImage(imageURL));
			}
			//////////////////////////////////////////////////////////////
			
			ImageBG = Transparency.makeColorTransparent(image.getImage(), Color.yellow);
			media.addImage(ImageBG, 1);
			
			media.waitForID(1);
			
			//////////////////////////////////////////////////////////////
			// Load image from resource
			imageURL = SandBoxClient.class
					.getResource(Constants.resourcePath + "/tabhead.png");
			
			if (imageURL != null)
			{
				image = new ImageIcon(Toolkit.getDefaultToolkit().getImage(imageURL));
			}
			//////////////////////////////////////////////////////////////

			imageHeadWidth = image.getIconWidth();
			imageHeadHeight = image.getIconHeight();
			ImageHead = Transparency.makeColorTransparent(image.getImage(), Color.yellow);
			media.addImage(ImageHead, 2);
			
			media.waitForID(2);
			
			//////////////////////////////////////////////////////////////
			// Load image from resource
			imageURL = SandBoxClient.class
					.getResource(Constants.resourcePath + "/tabhead_un.png");
			
			if (imageURL != null)
			{
				image = new ImageIcon(Toolkit.getDefaultToolkit().getImage(imageURL));
			}
			//////////////////////////////////////////////////////////////
			
			imageHeadWidthU= image.getIconWidth();
			imageHeadHeightU = image.getIconHeight();
			ImageHeadU = Transparency.makeColorTransparent(image.getImage(), Color.yellow);
			media.addImage(ImageHeadU, 3);
			
			media.waitForID(3);

			//////////////////////////////////////////////////////////////
			// Load image from resource
			imageURL = SandBoxClient.class
					.getResource(Constants.resourcePath + "/tabtail_un.png");
			
			if (imageURL != null)
			{
				image = new ImageIcon(Toolkit.getDefaultToolkit().getImage(imageURL));
			}
			//////////////////////////////////////////////////////////////
			
			imageTailWidthU= image.getIconWidth();
			imageTailHeightU = image.getIconHeight();
			ImageTailU = Transparency.makeColorTransparent(image.getImage(), Color.yellow);
			media.addImage(ImageTailU, 4);
			
			media.waitForID(4);			
			
		}
		catch(InterruptedException e)
		{
			
		}
		//////////////////////////////////////////////////////////////////
	}

	/**
	 * Overridden BasicTabbedPaneUI's method.
	 * As against parent method, it doesn't call paintTabBorder and paintFocusIndicator.
	 * 
	 * @param g
	 * @param tabPlacement
	 * @param rects
	 * @param tabIndex
	 * @param iconRect
	 * @param textRect
	 * 
	 */
	@Override
	protected void paintTab(Graphics g, int tabPlacement, Rectangle[] rects,
			int tabIndex, Rectangle iconRect, Rectangle textRect) {

		//////////////////////////////////////////////////////////////////
		//prev_tabRects = tabRects;
		//tabRects = rects;
		//////////////////////////////////////////////////////////////////
		
		//////////////////////////////////////////////////////////////////
		// get current tab's rectangle
		Rectangle tabRect = rects[tabIndex];
		//////////////////////////////////////////////////////////////////
		
		//////////////////////////////////////////////////////////////////
		// get selected, them save the index
		int selectedIndex = tabPane.getSelectedIndex();
		selectedTabIndex = selectedIndex;
		boolean isSelected = selectedIndex == tabIndex;
		//////////////////////////////////////////////////////////////////
		
		//////////////////////////////////////////////////////////////////
		// paint background - overridden, look below
		paintTabBackground(g, tabPlacement, tabIndex, tabRect.x, tabRect.y,
				tabRect.width, tabRect.height, isSelected);
		//////////////////////////////////////////////////////////////////

		//////////////////////////////////////////////////////////////////
		// this standard call is turned down
		
		//*** paintTabBorder(g, tabPlacement, tabIndex, tabRect.x, tabRect.y,
		//*** 	 tabRect.width, tabRect.height, isSelected);
		
		//////////////////////////////////////////////////////////////////

		String title = tabPane.getTitleAt(tabIndex);
		Font font = tabPane.getFont();
		FontMetrics metrics = g.getFontMetrics(font);
		Icon icon = getIconForTab(tabIndex);

		//////////////////////////////////////////////////////////////////
		// call parent method 
		layoutLabel(tabPlacement, metrics, tabIndex, title, icon, tabRect,
				iconRect, textRect, isSelected);
		//////////////////////////////////////////////////////////////////

		//////////////////////////////////////////////////////////////////
		// modify rectangles that are painted after selected tab cause it has tail
		if (tabIndex > selectedIndex)
		{
			textRect.x += 20;
			iconRect.x += 20;
			iconRect.y -= 1;
		}
		//////////////////////////////////////////////////////////////////

		//////////////////////////////////////////////////////////////////
		// call parent method 		
		paintText(g, tabPlacement, font, metrics, tabIndex, title, textRect,
				isSelected);
		//////////////////////////////////////////////////////////////////
		
		//////////////////////////////////////////////////////////////////
		// call parent method 
		paintIcon(g, tabPlacement, tabIndex, icon, iconRect, isSelected);
		//////////////////////////////////////////////////////////////////

		//////////////////////////////////////////////////////////////////
		// this standard call is turned down

		//*** paintFocusIndicator(g, tabPlacement, rects, tabIndex, iconRect,
		//*** 		textRect, isSelected);		
		//////////////////////////////////////////////////////////////////
		
		//////////////////////////////////////////////////////////////////
		//paintAdditionalButton(g);
		//////////////////////////////////////////////////////////////////
	}

	private void paintAdditionalButton(Graphics g)
	{
		//int w = getCurrentTabsWidth();
		//int w = getPrevTabsWidth();
		//g.setColor(Color.red);
		//g.drawLine(0, 0, w, 0);
		
		/*
		int count = ((IDPTabbedPane)tabPane).getHiddenTabsCount();
		if (count > 0)
		{
			g.setColor(new Color(255,255,255,100));
			Rectangle r = tabPane.getBounds();
			rect = new Rectangle(r.width - 35, 2, 30, 18);
			g.fillRect(rect.x, rect.y, rect.width, rect.height);
			g.setColor(Color.black);
			Font f = new Font("Verdana", Font.BOLD, 8);
			g.setFont(f);
			g.drawString(">>"+Integer.toString(count), rect.x, 10);
		}
		else rect = null;
		*/
	}
	
	/**
	 * Overridden BasicTabbedPaneUI's method.
	 * 
	 * @param g
	 * @param tabPlacement
	 * @param tabIndex
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param isSelected
	 */
	@Override
	protected void paintTabBackground(Graphics g, int tabPlacement,
			int tabIndex, int x, int y, int w, int h, boolean isSelected) {

		
		Rectangle rect = rects[tabIndex];
		
		if (tabIndex == tabPane.getSelectedIndex()) {
			
			Rectangle rectBG = new Rectangle(rect);
			rectBG.width -= (imageTailWidth - 20);
			rectBG.height -= 2;
			
			//////////////////////////////////////////////////////////////
			// resize background image to fill all tab
			// (can be different because of different text length)
			Image ImageResized = ImageBG.getScaledInstance(rectBG.width, rectBG.height, Image.SCALE_REPLICATE);
			try
			{
				MediaTracker media = new MediaTracker(tabPane);
				media.addImage(ImageResized, 0);
				media.waitForID(0);
			}
			catch(InterruptedException e) {}
			//////////////////////////////////////////////////////////////
			
			//////////////////////////////////////////////////////////////
			g.drawImage(ImageResized, rect.x + imageHeadWidth, rect.y,
					rectBG.width, rectBG.height, null);
			//////////////////////////////////////////////////////////////
			
			//////////////////////////////////////////////////////////////
			g.drawImage(ImageHead, rect.x, rect.y,
					imageHeadWidth, imageHeadHeight, null);
			//////////////////////////////////////////////////////////////

			//////////////////////////////////////////////////////////////
			boolean bConsoleWindow = false;
			if (tabPane instanceof IDPTabbedPane) {
				Component component = tabPane.getComponent(tabIndex);

				if (component instanceof JScrollPane) {
					JScrollPane js = (JScrollPane) component;
					
					if (((IDPTabbedPane)tabPane).containsValue(js) == false)
					{
						bConsoleWindow = true;
					}
				}
			}
			
			if (bConsoleWindow == true)
			{
				g.drawImage(ImageTailW, rect.x + rect.width - 20, rect.y,
						imageTailWidthW, imageTailHeightW, null);
			}
			else
			{
				//////////////////////////////////////////////////////////
				// chose what tail to draw according to mouse position status (close button status)
				if (closeBtnStatus == eCloseBtnStatus.outside)
				{
					g.drawImage(ImageTail, rect.x + rect.width - 20, rect.y,
							imageTailWidth, imageTailHeight, null);
				}
				else
				{
					g.drawImage(ImageTailRed, rect.x + rect.width - 20, rect.y,
							imageTailWidth, imageTailHeight, null);
				}
				//////////////////////////////////////////////////////////
			}
			//////////////////////////////////////////////////////////////
			
			//curCloseBtnRect = getCloseRect(rect);
			//g.setColor(Color.red);
			//g.drawRect(curCloseBtnRect.x, curCloseBtnRect.y, curCloseBtnRect.width, curCloseBtnRect.height);
			
			//////////////////////////////////////////////////////////////
			// draw double lined rectangle
			BasicStroke dashed = new BasicStroke(3);
			Graphics2D g2D = (Graphics2D) g;

			Stroke old = g2D.getStroke();
			g2D.setStroke(dashed);

			Rectangle r = tabPane.getBounds();
			
			g2D.setColor(new Color(139, 188, 241));
			//g2D.setColor(Color.red);
			//g2D.setColor(new Color(192, 193, 203));
			
			g2D.drawRect(r.x, r.y + TAB_HEIGHT + 2, r.width - 4, r.height
					- TAB_HEIGHT - 7);
			
			//g2D.setColor(Color.red);
			//g2D.drawLine(r.x+1, r.y + TAB_HEIGHT + 3, r.x+1, r.height - 3);
			
			g2D.setStroke(old);
			//////////////////////////////////////////////////////////////
			
			//////////////////////////////////////////////////////////////
			g2D.setColor(new Color(170,170,170));
			g2D.drawLine(r.x, r.y + TAB_HEIGHT + 1, rect.x, r.y + TAB_HEIGHT + 1);
			g2D.drawLine(rect.x+rect.width+24, r.y + TAB_HEIGHT, r.width - 3, r.y + TAB_HEIGHT);
			//////////////////////////////////////////////////////////////
			
		}
		else
		{
			//////////////////////////////////////////////////////////////
			// gray
			g.setColor(new Color(180,180,180));
			//////////////////////////////////////////////////////////////
			
			//////////////////////////////////////////////////////////////
			if (tabIndex < tabPane.getSelectedIndex())
			{
				g.drawImage(ImageHeadU, rect.x, rect.y-1,
						imageHeadWidthU, imageHeadHeightU, null);

				Rectangle rectNext = rects[tabIndex+1];
				g.drawLine(rect.x+ imageHeadWidth, rect.y-2, rectNext.x+10, rect.y-2);
			}
			else
			{
				g.drawLine(rect.x-5, rect.y-2, rect.x + rect.width, rect.y-2);

				g.drawImage(ImageTailU, rect.x+rect.width, rect.y-2,
						imageTailWidthU, imageTailHeightU, null);
			}
			//////////////////////////////////////////////////////////////
		}
	}

	/**
	 * Return tab width.
	 * Overridden to make some changes.
	 * 
	 * @param tabPlacement
	 * @param tabIndex
	 * @param metrics
	 *
	 * @return tab width 
	 */
	@Override
	protected int calculateTabWidth(int tabPlacement, int tabIndex,
			FontMetrics metrics) {
		return super.calculateTabWidth(tabPlacement, tabIndex, metrics) + 20;
	}

	/**
	 * Return label X shift.
	 * Overridden to make some changes.
	 * 
	 * @param tabPlacement
	 * @param tabIndex
	 * @param isSelected
	 *
	 * @return X-direction label shift
	 */
	@Override
	protected int getTabLabelShiftX(int tabPlacement, int tabIndex,
			boolean isSelected) {
		return super.getTabLabelShiftX(tabPlacement, tabIndex, isSelected) - 10;
	}

	/**
	 * Mouse motion listener.
	 * Updates close button picture (to white or red). 
	 * 
	 * @see MouseMotionListener
	 */
	class IDPMouseMotionListener implements MouseMotionListener {

		public void mouseMoved(MouseEvent e) {
			//bMouseMoved = false;
			updateCloseButton(e.getX(), e.getY());
		}

		public void mouseDragged(MouseEvent e) {
			//bMouseMoved = true;
			//updateCloseButton(e.getX(), e.getY());
		}
	}

	/**
	 * Mouse listener.
	 * Changes the close button status and fire close event when close button is pressed.
	 * 
	 * @see MouseListener
	 */
	class IDPMouseListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent arg0) {}

		@Override
		public void mouseEntered(MouseEvent arg0) {}

		@Override
		public void mouseExited(MouseEvent arg0) {}

		@Override
		public void mousePressed(MouseEvent arg0) {
			if (closeBtnStatus == eCloseBtnStatus.over)
			{
				closeBtnStatus = eCloseBtnStatus.pressed;
				bMouseMoved = false;
			}
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			updateCloseButton(arg0.getX(), arg0.getY());
			if (closeBtnStatus == eCloseBtnStatus.pressed && bMouseMoved == false)
			{
				//System.out.println("pressed");
				if (tabPane instanceof IDPTabbedPane) {
					((IDPTabbedPane) tabPane).fireCloseTabEvent(arg0, selectedTabIndex);
					//System.out.println("fire!!!");
				}
			}
		}


	}
	
	/**
	 * Get tab index under mouse cursor.
	 * 
	 * @param x - the <b>x</b> position of mouse cursor
	 * @param y - the <b>y</b> position of mouse cursor
	 * @return index of tab
	 */
	private int getTabAtLocation(int x, int y) {
		// TODO - ?
		//ensureCurrentLayout();

		int tabCount = tabPane.getTabCount();
		for (int i = 0; i < tabCount; i++) {
			if (rects[i].contains(x, y)) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Check if cursor is above close button rectangle.
	 * Update close button status if needed.
	 * 
	 * @param x - the <b>x</b> position of mouse cursor to check
	 * @param y - the <b>y</b> position of mouse cursor to check
	 * 
	 * @see getCloseRect
	 */
	protected void updateCloseButton(int x, int y) {

		int index = getTabAtLocation(x, y);
		
		if (index != -1 && index == selectedTabIndex)
		{
			Rectangle closeRect = getCloseButtonRect(rects[selectedTabIndex]);
			if (closeRect.contains(x, y) == true)
			{
				eCloseBtnStatus oldStatus = closeBtnStatus;
				if (closeBtnStatus != eCloseBtnStatus.pressed)
					closeBtnStatus = eCloseBtnStatus.over;
				
				if (oldStatus != closeBtnStatus)
						tabPane.repaint();
			}
			else
			{
				eCloseBtnStatus oldStatus = closeBtnStatus;
				closeBtnStatus = eCloseBtnStatus.outside;
				
				if (oldStatus != closeBtnStatus)
					tabPane.repaint();
			}
		}
		else closeBtnStatus = eCloseBtnStatus.outside;
	}
	
	/**
	 * Get close button location as rectangle for specific tab that presented as input parameter <b>rect</b>
	 * 
	 * @param rect - tab's rectangle for which the method should return location
	 * @return close button's rectangle
	 */
	protected Rectangle getCloseButtonRect(Rectangle rect) {
		int dx = rect.x + rect.width - 16;
		int dy = (rect.y + rect.height) / 2 - 7;
		return new Rectangle(dx, dy, 11, 11);
	}
	
	public int getTabsWidth()
	{
		int width = 0;
		
		for (int i = 0; i < tabPane.getTabCount(); i++)
		{
			Rectangle r = new Rectangle();
			getTabBounds(i, r);
			width += r.width;
			tabRects[i] = new Integer(r.width);
		}

		return width + 20 + 5;
	}

	public int getTabWidth(int index)
	{
		Rectangle r = new Rectangle();
		getTabBounds(index, r);
		return r.width - HEAD_WIDTH;
	}
	
	public int getTabWidthFromHistory(int index)
	{
		int width = tabRects[index];
		return width - HEAD_WIDTH;
	}
	
	public void updateTabsWidth()
	{
		for (int i = 0; i < tabPane.getTabCount(); i++)
		{
			Rectangle r = new Rectangle();
			try
			{
				getTabBounds(i, r);
			}
			catch(ArrayIndexOutOfBoundsException e)
			{
				System.out.println("*");
				return;
			}
			tabRects[i] = new Integer(r.width);
		}
	}
	
	public int getPrevTabsWidth()
	{
		int width = 0;
		
		for (int i = 0; i < tabPane.getTabCount(); i++)
		{
			if (tabRects[i] != null)
				width += tabRects[i];
		}

		return width + 20 + 5;
	}

}
