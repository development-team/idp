package idp.sandBox.client.gui.components.tabs;

import idp.sandBox.client.managers.IDPWindowManager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TabMenuListener implements ActionListener {
	public void actionPerformed(ActionEvent e)
	{
		IDPTabbedPane jTabbedPane = IDPWindowManager.getJTabbedPane();
		jTabbedPane.unhideTab(e.getActionCommand());
	}
}
