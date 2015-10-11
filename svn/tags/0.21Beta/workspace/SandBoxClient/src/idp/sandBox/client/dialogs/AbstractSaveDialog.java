/**
 * 
 */
package idp.sandBox.client.dialogs;

import idp.sandBox.client.gui.components.IDPGlassPane;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.apache.log4j.Logger;

/**
 * Abstract class for Save dialogs.
 * @author talanov m
 *
 */
abstract public class AbstractSaveDialog extends JPanel {

	private static final long serialVersionUID = 790664012251763180L;	
	protected Logger log = Logger.getLogger(AbstractSaveDialog.class);
	
	public IDPGlassPane parent = null;
	
	/**
	 * Action class that calls save() method.
	 * @author talanov m
	 *
	 */
	private class SaveAction  extends AbstractAction {
		private static final long serialVersionUID = -129883944220031051L;

		@Override
		public void actionPerformed(ActionEvent e) {
			log.info("field save pressed");
			parent.fadeOut();
			save();
		}
	}
	
	/**
	 * Subscribes input JComponent to the save action that calls abstract method save on ENTER keystroke.
	 * @param aComponent component to subscribe
	 */
	protected void subscriveSaveEnterAction(JComponent aComponent) {
		aComponent.getInputMap(JComponent.WHEN_FOCUSED).put(
				KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER,0), "save");
		aComponent.getActionMap().put("save", new SaveAction());
	}
	
	/**
	 * Method that is called by SaveAction, that is subscribed by subscribeSaveEnterAction
	 */
	abstract protected void save();
}
