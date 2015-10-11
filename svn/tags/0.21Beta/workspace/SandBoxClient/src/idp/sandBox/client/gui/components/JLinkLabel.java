package idp.sandBox.client.gui.components;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JLabel;

/**
 * Hyper link class
 * 
 * @author Natfullin Ayrat
 * 
 *  Based on Shahzad's solution
 *  @link http://forum.java.sun.com/thread.jspa?threadID=527199&messageID=2530618
 */
public class JLinkLabel extends JLabel {
	public String url;
	public Color rc;

	public JLinkLabel(String text, Icon icon, String url) {
		super(text);
		this.setIcon(icon);
		this.url = url;
		addListeners();
	}

	private void addListeners() {
		//final String thisUrl = url;
		
		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				JLinkJumper.displayURL(JLinkLabel.this.url);
			}

			public void mouseEntered(MouseEvent e) {
				JLinkLabel.this.rc = JLinkLabel.this.getForeground();
				JLinkLabel.this.setCursor(new Cursor(Cursor.HAND_CURSOR));
				JLinkLabel.this.setForeground(Color.blue);
			}

			public void mouseExited(MouseEvent e) {
				JLinkLabel.this.setForeground(JLinkLabel.this.rc);
				JLinkLabel.this
						.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}

		});
	}

}
