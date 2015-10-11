package idp.sandBox.client.gui.components.tabs;

import java.awt.event.MouseEvent;
import java.util.EventListener;

public interface CloseEventListener extends EventListener {
	public void closeTab(MouseEvent e, int tabIndex);
}
