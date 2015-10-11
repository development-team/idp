package idp.sandBox.client.gui.components.tabs;

import idp.sandBox.client.gui.components.IDPGlassPane;
import idp.sandBox.client.managers.IDPWindowManager;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

public class TabbedPaneScroller extends JPanel implements MouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5979873017308447411L;

	private boolean bCursorAbove;
	
	private int hiddenTabCount;
	
	private Font f1;
	private Font f2;
	
	private JPopupMenu jHiddenTabsList;
	
	public TabbedPaneScroller()
	{
		addMouseListener(this);
		
		f1 = new Font("Calibri", Font.BOLD, 10);
		f2 = new Font("New Times Roman", Font.PLAIN, 10);
		
		jHiddenTabsList = new JPopupMenu();
		
		setOpaque(false);
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
       	Graphics2D g2D = (Graphics2D)g;
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        			RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2D.setFont(f1);
		g2D.drawString(">", 4, 8);
		g2D.drawString(">", 8, 8);

		g2D.setFont(f2);
		g2D.drawString(Integer.toString(hiddenTabCount), 13, 16);

		if (bCursorAbove == true)
		{
			int w = IDPGlassPane.BUTTON_WIDTH;
			int h = IDPGlassPane.BUTTON_HEIGHT;
			
			g2D.setColor(Color.black);
			g2D.drawRoundRect(2,1,w-3,h-2,8,8);
			
			g2D.setColor(new Color(255,255,255,120));
			g2D.fillRoundRect(2,1,w-2,h-1,8,8);
		}		
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// nothing to do
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		bCursorAbove = true;
		repaint();
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		bCursorAbove = false;
		repaint();
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		IDPTabbedPane tabbedPane = IDPWindowManager.getJTabbedPane();
		String[] names = tabbedPane.getHiddenTabNames();
		
		// sort
		String tmp;
		for (int i = 0; i < names.length - 1; i++)
			for (int j = i + 1; j < names.length; j++)
			{
				if (names[i].compareTo(names[j]) > 0)
				{
					tmp = names[i];
					names[i] = names[j];
					names[j] = tmp; 
				}
			}
		
		// form menu items
		jHiddenTabsList.removeAll();
		for (int i = 0; i < names.length; i++)
		{
			//System.out.println(names[i]);
			JMenuItem item = new JMenuItem(names[i]);
			jHiddenTabsList.add(item);
			item.addActionListener(new TabMenuListener());
		}
		Rectangle r = getBounds();
		jHiddenTabsList.show(this, 5, r.height + 5);
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// nothing to do
	}
	
	public void setHiddenTabCount(int hiddenTabCount)
	{
		this.hiddenTabCount = hiddenTabCount;
	}
}
