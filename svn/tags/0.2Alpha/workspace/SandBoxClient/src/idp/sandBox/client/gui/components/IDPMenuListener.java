package idp.sandBox.client.gui.components;

import idp.sandBox.client.gui.SandBoxClient;
import idp.sandBox.client.managers.IDPClientSingleton;
import idp.sandBox.client.managers.IDPWindowManager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

public class IDPMenuListener implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		if ("connect".equals(e.getActionCommand())) { // new
			IDPWindowManager.connectToServer();
		}
		else if ("disconnect".equals(e.getActionCommand())) { // new
			IDPWindowManager.disconnectFromServer();
		}
		else if ("train".equals(e.getActionCommand())) {
			IDPWindowManager.train();
		}
		else if ("apply".equals(e.getActionCommand())) {
			IDPWindowManager.apply();
		}
		else if ("retrain".equals(e.getActionCommand())) {
			IDPWindowManager.retrain();
		}
		else if ("send".equals(e.getActionCommand())) {
			IDPClientSingleton.getIDPClient().saveCurrentXML();
		}
		else if ("settings".equals(e.getActionCommand())) {
			SandBoxClient client = IDPClientSingleton.getIDPClient();
			client.glassPaneFadeIn(IDPGlassPane.IDPDialogs.SettingsDialog);
		}
		else if ("colors".equals(e.getActionCommand())) {
			SandBoxClient client = IDPClientSingleton.getIDPClient();
			client.glassPaneFadeIn(IDPGlassPane.IDPDialogs.ColorChooser);
		}
		else if ("properties".equals(e.getActionCommand())) {
			SandBoxClient client = IDPClientSingleton.getIDPClient();
			client.glassPaneFadeIn(IDPGlassPane.IDPDialogs.PropertiesDialog);
		}
		else if ("quit".equals(e.getActionCommand())) {
			SandBoxClient client = IDPClientSingleton.getIDPClient();
			client.quit();
		}
		else if ("about".equals(e.getActionCommand())) {
			SandBoxClient client = IDPClientSingleton.getIDPClient();
			client.glassPaneFadeIn(IDPGlassPane.IDPDialogs.AboutDialog);
		}
		else { // else quit
			JOptionPane.showMessageDialog(null, "Under construction!!!", "Sorry", JOptionPane.INFORMATION_MESSAGE);
		}
	}

}
