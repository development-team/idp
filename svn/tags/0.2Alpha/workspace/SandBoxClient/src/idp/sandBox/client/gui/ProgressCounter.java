package idp.sandBox.client.gui;

import idp.sandBox.client.dialogs.IDPMessageDlg;
import idp.sandBox.client.gui.components.IDPGlassPane;
import idp.sandBox.client.gui.components.trees.IDPContentTree;
import idp.sandBox.client.managers.IDPClientSingleton;
import idp.sandBox.client.managers.IDPWindowManager;
import idp.sandBox.server.MessagingInterface;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.Style;

import ubique.idp.processing.state.State;

/**
 * This class is used for updating progress bar state in separate thread
 * 
 */
public class ProgressCounter implements Runnable {
	
	private final int CONSOLE_ON = 1;
	
	private boolean isRunning;
	private JProgressBar m_progress_bar;
	private MessagingInterface connection;
	private State state;
	private ArrayList<JButton> list_activateButton = new ArrayList<JButton>();
	private ArrayList<String> console = new ArrayList<String>();
	
	private JLabel jStatusLabel;
	
	private JTextPane jConsole;
	
	private String operation = "Training";

	public ProgressCounter(JProgressBar pb, JLabel jStatusLabel) {
		m_progress_bar = pb;
		isRunning = false;
		this.jStatusLabel = jStatusLabel;
	}

	public void run() {
		if (m_progress_bar == null)
			return;

		double i = 0;
		String message = "In progress";
		isRunning = true;
		m_progress_bar.setMinimum(0);
		m_progress_bar.setMaximum(100);
		
		for (JButton jb: list_activateButton)
		{
			jb.setEnabled(false);
		}

		//setConsoleWindow();
		
		// main loop
		Date d1 = new Date();
		while (isRunning) {
			if (connection != null) {
				try {
					state = connection.getState();
					i = state.getPercent();
					message = state.getStageName();
					isRunning = !state.isComplete();
					
					//////////////////////////////////////////////////////
					// server console
					if (CONSOLE_ON == 1)
					{
						console = connection.getConsoleStrings();
						Document doc = jConsole.getDocument();
						String text = "";
						for (String line: console)
						{
							text += line;
						}
						try
						{
							doc.insertString(doc.getEndPosition().getOffset(), text, null);
							jConsole.setCaretPosition(jConsole.getDocument().getLength());
						}
						catch (BadLocationException e)
						{
							e.printStackTrace();
						}
						//jConsole.setDocument(doc);
					}
					//////////////////////////////////////////////////////
					
				}
				catch (RemoteException e1) {
					e1.printStackTrace();
					m_progress_bar.setString("Connection problem...");
					jStatusLabel.setText("Connection problem...");
					IDPClientSingleton.getIDPClient().glassPaneFadeIn(IDPGlassPane.IDPDialogs.MessageDialog,"Error",
							"Couldn't get response from the server",
							IDPMessageDlg.messageType.ERROR_MESSAGE);
					for (JButton jb: list_activateButton)
					{
						jb.setEnabled(true);
					}
					return;
				}
				if (i > 100)
					i = 100;
			}
			m_progress_bar.setValue((int) i);
			m_progress_bar.setString(message);
			// i += 1;				
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {

			}
		}

		for (JButton jb: list_activateButton)
		{
			jb.setEnabled(true);
		}

		m_progress_bar.setString("Done");
		System.out.println(operation + " completed");
		jStatusLabel.setText(operation + " completed, " + ((new Date()).getTime() - d1.getTime()) + " miliseconds spent.");
		
		//////////////////////////////////////////////////////////////////
		// reload (left) content tree and (right) xml tree
		IDPWindowManager.getJTree().initTree();
		if (operation.compareTo("Training") == 0)
		{
			IDPWindowManager.getJXMLTree().initTree();
		}
		//////////////////////////////////////////////////////////////////

		IDPClientSingleton.getIDPClient().glassPaneFadeIn(IDPGlassPane.IDPDialogs.MessageDialog,"Information",
				operation + " has been completed succefully!",
				IDPMessageDlg.messageType.INFORMATION_MESSAGE);
	}

	public void stop() {
		isRunning = false;
	}

	public void setConnection(MessagingInterface connection) {
		this.connection = connection;
	}

	public void setConsoleWindow(JTextPane consoleWindow) {
		this.jConsole = consoleWindow;
	}

	public void addActivateButton(JButton activateButton) {
		list_activateButton.add(activateButton);
	}
	
	public void setOperation(String name)
	{
		operation = name;
	}

}
/////////////////////////////////////////////////////////////////////////////////////
