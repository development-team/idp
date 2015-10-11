package idp.sandBox.client.gui.components.tabs;

import idp.sandBox.client.constants.Constants;
import idp.sandBox.client.gui.SandBoxClient;
import idp.sandBox.client.managers.IDPClientSingleton;
import idp.sandBox.client.managers.IDPWindowManager;
import idp.sandBox.models.TextFileInfo;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.net.URL;
import java.util.HashMap;
import java.util.Set;

import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.apache.log4j.Logger;

/**
 * Tabbed pane with tabs' info, close button and enhanced UI.
 * 
 * @author Natfullin A.
 * 
 */
public class IDPTabbedPane extends JTabbedPane implements ComponentListener {

	private static final long serialVersionUID = 1L;

	private static final int ADDITIONAL_SPACE = 30;

	private static final int TAIL_WIDTH = 31;

	/**
	 * Close Event listener. Is used by parent to remove tab and make other
	 * specific operations before deleting. Can be null.
	 */
	private CloseEventListener closeEvListener = null;

	/**
	 * Hash map. Is used to collect specific information about each tab in
	 * tabbed pane.
	 */
	private HashMap<TextFileInfo, JScrollPane> tabsInfo = new HashMap<TextFileInfo, JScrollPane>();

	/**
	 * Hash map. Is used to store information about hidden tabs that appear in
	 * tab scroller control.
	 */
	private HashMap<TextFileInfo, JScrollPane> tabsHidden = new HashMap<TextFileInfo, JScrollPane>();

	/**
	 * Enhanced tabbed pane UI.
	 */
	private IDPTabbedPaneUI enhancedUI = null;

	/**
	 * Tab icon.
	 */
	private Image tabImage;

	private Logger log = Logger.getLogger(IDPTabbedPane.class);

	/**
	 * Public constructor
	 */
	public IDPTabbedPane() {

		// ////////////////////////////////////////////////////////////
		// Load image from resource
		URL imageURL = null;
		imageURL = SandBoxClient.class.getResource(Constants.resourcePath
				+ "/sc.png");

		if (imageURL != null) {
			tabImage = Toolkit.getDefaultToolkit().getImage(imageURL);
		}
		// ////////////////////////////////////////////////////////////

		enhancedUI = new IDPTabbedPaneUI(this);
		setUI(enhancedUI);

		addComponentListener(this);

		Dimension d = new Dimension(300, 100);
		setMinimumSize(d);
	}

	/**
	 * Set listener for close button
	 * 
	 * @param cel -
	 *            listener, must implement CloseEventListener interface
	 * 
	 * @see #removeCloseEventListener
	 */
	public synchronized void addCloseEventListener(CloseEventListener cel) {
		this.closeEvListener = cel;
	}

	/**
	 * Removes listener for close button.
	 * 
	 * @see #addCloseEventListener
	 */
	public synchronized void removeCloseEventListener() {
		this.closeEvListener = null;
	}

	/**
	 * Send a <code>MouseEvent</code> to close event listener.
	 * 
	 * @param e -
	 *            the <code>MouseEvent</code> to be sent
	 * @param tabIndex -
	 *            the index of tab to close
	 */
	public void fireCloseTabEvent(MouseEvent e, int tabIndex) {
		if (closeEvListener != null)
			closeEvListener.closeTab(e, tabIndex);
	}

	/**
	 * Overridden JTabbedPane method, additionally sets icon to every tab,
	 * enables menu item.
	 * 
	 * @param title
	 * @param component
	 * 
	 * @see #tabImage
	 */
	@Override
	public void addTab(String title, Component component) {
		addTab(title, component, true);
		IDPWindowManager.getCloseAllTabsMenuItem().setEnabled(true);
	}

	/**
	 * Adds tab to pane.
	 * @param title
	 * @param component
	 * @param bCheck
	 */
	public void addTab(String title, Component component, boolean bCheck) {
		if (bCheck == true) {
			int widthSum = enhancedUI.getTabsWidth();

			// find out the size of text rectangle for title
			Graphics g = getGraphics();
			Graphics2D g2D = (Graphics2D) g;

			Font textFont = getFont();
			FontRenderContext frc = g2D.getFontRenderContext();
			int textWidth = (int) textFont.getStringBounds(title, frc)
					.getWidth();

			int newWidth = widthSum + textWidth + TAIL_WIDTH;

			int widthAllowed = getMaxAllowedWidth();
			while (newWidth > widthAllowed && getTabCount() > 0) {
				newWidth -= enhancedUI.getTabWidth(0);
				hideTab(0);
			}

		}

		super.addTab(title, component);

		if (tabsHidden.size() > 0) {
			SandBoxClient client = IDPClientSingleton.getIDPClient();
			client.updateTabInfoBounds(tabsHidden.size());
		}

		setSelectedComponent(component);
	}

	/**
	 * Overridden JTabbedPane method. Additionally removes corresponding tab
	 * info from <code>tabsInfo</code>.
	 * 
	 * @param index -
	 *            tab index to remove
	 * 
	 * @see #tabsInfo
	 */
	@Override
	public void remove(int index) {
		Component component = getComponent(index);

		if (component instanceof JScrollPane) {
			JScrollPane js = (JScrollPane) component;

			// ////////////////////////////////////////////////////////////
			// if such record exists in hash map then remove it
			if (tabsInfo.containsValue(js) == true) {
				// ////////////////////////////////////////////////////////
				// look through all keys to find this record
				Set<TextFileInfo> keys = tabsInfo.keySet();
				for (TextFileInfo key : keys) {
					JScrollPane value = tabsInfo.get(key);
					if (value == js) {
						// ////////////////////////////////////////////////
						// asta la vista baby
						tabsInfo.remove(key);
						break;
						// ////////////////////////////////////////////////
					}
				}
				// ////////////////////////////////////////////////////////
			} else // else ignore closing
			{
				System.out.println("couldn't find tab info to delete");
				return;
			}
			// ////////////////////////////////////////////////////////////
		} else // else ignore closing
		{
			// return;
		}
		super.remove(index);

		// remove all tabs button deactivate
		if (tabsInfo.size() < 1)
			IDPWindowManager.getCloseAllTabsMenuItem().setEnabled(false);
	}

	/**
	 * Overridden JTabbedPane method. Calls overridden method <b>remove</b>
	 * step by step.
	 * 
	 * @see #tabsInfo
	 * @see #remove
	 */
	@Override
	public void removeAll() {
		int count = getTabCount();
		for (int i = count - 1; i >= 0; i--) {
			remove(i);
		}
		super.removeAll();

		removeAllHidden();
	}

	public void removeAllHidden() {
		tabsHidden.clear();
		SandBoxClient client = IDPClientSingleton.getIDPClient();
		client.updateTabInfoBounds(0);
	}

	/**
	 * Get info about tab according to <code>tfi</code> key.
	 * 
	 * @param tfi -
	 *            key to get info
	 * @return JScrollPane linked to tab
	 * 
	 * @see #putTabInfo
	 */
	public JScrollPane getTabInfo(TextFileInfo tfi) {
		return this.tabsInfo.get(tfi);
	}

	/**
	 * Get info about tab according to relative path.
	 * 
	 * @param relativePath
	 * @return JScrollPane linked to tab
	 * 
	 * @see #putTabInfo
	 */
	public JScrollPane getTabInfo(String relativePath) {
		JScrollPane jScrollPane = null;
		Set<TextFileInfo> keys = this.tabsInfo.keySet();
		for (TextFileInfo key : keys) {
			if (key.getRelativePath().compareTo(relativePath) == 0) {
				jScrollPane = this.tabsInfo.get(key);
				break;
			}
		}
		return jScrollPane;
	}

	/**
	 * Set info about tab.
	 * 
	 * @param tfi -
	 *            key
	 * @param scrollPane -
	 *            JScrollPane to link with tab
	 * 
	 * @see #getTabInfo
	 */
	public void putTabInfo(TextFileInfo tfi, JScrollPane scrollPane) {
		this.tabsInfo.put(tfi, scrollPane);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
	}

	@Override
	public void componentHidden(ComponentEvent arg0) {
	}

	@Override
	public void componentMoved(ComponentEvent arg0) {
	}

	@Override
	public void componentResized(ComponentEvent e) {
		int widthAllowed = getMaxAllowedWidth();
		int h = getHeight();

		// System.out.println("Resized: " + Integer.toString(widthAllowed) + "x"
		// + Integer.toString(h));
		int widthSum = enhancedUI.getPrevTabsWidth();
		// int widthSum = enhancedUI.getCurrentTabsWidth();
		// System.out.println("Width: " + Integer.toString(widthSum));

		if (widthSum > widthAllowed) // reduced
		{
			int secur = 0;
			while (widthSum > widthAllowed && getTabCount() > 0) {
				int index = getTabCount();
				hideTab(index - 1);

				widthSum = enhancedUI.getPrevTabsWidth();
				// System.out.println("Removing. New width: " +
				// Integer.toString(widthSum));
				secur++;

				// for safety
				if (secur > 50) {
					System.out.println("[warning]unsafe loop!!!");
					break;
				}
			}
		} else // enlarged
		{
			checkAndUnhideTabs();
		}

		SandBoxClient client = IDPClientSingleton.getIDPClient();
		client.updateTabInfoBounds(tabsHidden.size());
	}

	@Override
	public void componentShown(ComponentEvent arg0) {
	}

	public void checkAndUnhideTabs() {
		int widthAllowed = getMaxAllowedWidth();
		int widthSum = enhancedUI.getPrevTabsWidth();
		;

		// find out the size of text rectangle for title
		Graphics g = getGraphics();
		Graphics2D g2D = (Graphics2D) g;
		Font textFont = getFont();
		FontRenderContext frc = g2D.getFontRenderContext();

		String[] names = getHiddenTabNames();

		// sort
		String tmp;
		for (int i = 0; i < names.length - 1; i++)
			for (int j = i + 1; j < names.length; j++) {
				if (names[i].compareTo(names[j]) > 0) {
					tmp = names[i];
					names[i] = names[j];
					names[j] = tmp;
				}
			}

		int i = 0;
		while (i < names.length) {
			String title = names[i];
			LineMetrics metrics = textFont.getLineMetrics(title, frc);
			int textWidth = (int) textFont.getStringBounds(title, frc)
					.getWidth();
			int newWidth = widthSum + textWidth + TAIL_WIDTH;

			// can't unhide any more tabs - there is no enough space for them
			if (newWidth > widthAllowed)
				break;

			unhideTab(title, false);

			widthSum = newWidth;

			i++;
		}

		SandBoxClient client = IDPClientSingleton.getIDPClient();
		client.updateTabInfoBounds(tabsHidden.size());
	}

	public void hideTab(int index) {
		JScrollPane jScrollPane = (JScrollPane) getComponent(index);

		// ////////////////////////////////////////////////////////////
		// if such record exists in hash map then remove it
		if (tabsInfo.containsValue(jScrollPane) == true) {
			// ////////////////////////////////////////////////////////
			// look through all keys to find this record
			Set<TextFileInfo> keys = tabsInfo.keySet();
			for (TextFileInfo key : keys) {
				JScrollPane value = tabsInfo.get(key);
				if (value == jScrollPane) {
					// ////////////////////////////////////////////////
					// move from one hash map to another
					tabsHidden.put(key, tabsInfo.remove(key));
					break;
					// ////////////////////////////////////////////////
				}
			}
			// ////////////////////////////////////////////////////////

			super.remove(index);
		} else
			return;
	}

	public void unhideTab(String name) {
		unhideTab(name, true);
	}

	public void unhideTab(String name, boolean bWithCheck) {
		// System.out.println("unhide, try to find: " + name);
		Set<TextFileInfo> keys = tabsHidden.keySet();
		for (TextFileInfo key : keys) {
			if (name.equals(key.getName())) {
				JScrollPane js = tabsHidden.remove(key);
				putTabInfo(key, js);
				addTab(name, js, bWithCheck);
				log.info("unhide: " + key.getName());
				break;
			}
		}
	}

	public int getHiddenTabsCount() {
		return tabsHidden.size();
	}

	public void updateRectangles() {
		enhancedUI.updateTabsWidth();
	}

	public String[] getHiddenTabNames() {
		String[] names = new String[tabsHidden.size()];

		Set<TextFileInfo> keys = tabsHidden.keySet();
		int i = 0;
		for (TextFileInfo info : keys) {
			names[i++] = info.getName();
		}

		return names;
	}

	/**
	 * Get maximum width of container taking into account place needed for
	 * IDPTabbedPaneScroller component
	 * 
	 * @return
	 */
	private int getMaxAllowedWidth() {
		return getWidth() - ADDITIONAL_SPACE;
	}

	/**
	 * Check out if such JScrollPane exists.
	 * 
	 * @param js JScrollPane to process.
	 * @return true of tabs contains specified JScrollPane, false otherwise.
	 */
	public boolean containsValue(JScrollPane js) {
		return tabsInfo.containsValue(js);
	}

	/**
	 * Check out if specified JScrollPane exists.
	 * 
	 * @param key the {@link TextFileInfo} to process 
	 * @return true if Tabbed pane contains hidden tab of {@link TextFileInfo}.
	 */
	public boolean containsHidden(TextFileInfo key) {
		// seems we have different objects
		// return tabsHidden.containsKey(key);
		Set<TextFileInfo> keys = tabsHidden.keySet();
		boolean found = false;
		for (TextFileInfo aKey : keys) {
			if (aKey.getRelativePath().equals(key.getRelativePath())) {
				found = true;
				break;
			}
		}
		return found;
	}

}
