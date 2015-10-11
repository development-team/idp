package idp.sandBox.client.gui.components.tabs;

import idp.sandBox.client.gui.IDPDragAndDrop;
import idp.sandBox.client.gui.Offset;
import idp.sandBox.models.TextFileInfo;

import java.util.HashMap;

import javax.swing.JTextPane;

public class IDPTabContext {
	
	private IDPDragAndDrop drag_and_drop;
	
	private TextFileInfo curTextFileInfo;

	private String currentXML;
	private HashMap<String, Offset> tag_scheme;
	private String xmlns_prefix = null; // namespace prefix PLUS ":"
	private String xmlns_url = null;
	private JTextPane curTextPane = null;
	
	private boolean isChanged = false;
	
	public IDPDragAndDrop getDrag_and_drop() {
		return drag_and_drop;
	}
	
	public void setDrag_and_drop(IDPDragAndDrop drag_and_drop) {
		this.drag_and_drop = drag_and_drop;
	}
	
	public TextFileInfo getCurTextFileInfo() {
		return curTextFileInfo;
	}
	
	public void setCurTextFileInfo(TextFileInfo curTextFileInfo) {
		this.curTextFileInfo = curTextFileInfo;
	}
	
	public String getCurrentXML() {
		return currentXML;
	}
	
	public void setCurrentXML(String currentXML) {
		this.currentXML = currentXML;
	}
	
	public HashMap<String, Offset> getTag_scheme() {
		return tag_scheme;
	}
	
	public void setTag_scheme(HashMap<String, Offset> tag_scheme) {
		this.tag_scheme = tag_scheme;
	}
	
	public void clearTag_scheme()
	{
		this.tag_scheme.clear();
	}
	
	public String getXmlns_prefix() {
		return xmlns_prefix;
	}
	
	public void setXmlns_prefix(String xmlns_prefix) {
		this.xmlns_prefix = xmlns_prefix;
	}
	
	public String getXmlns_url() {
		return xmlns_url;
	}
	
	public void setXmlns_url(String xmlns_url) {
		this.xmlns_url = xmlns_url;
	}
	
	public JTextPane getCurTextPane() {
		return curTextPane;
	}
	
	public void setCurTextPane(JTextPane curTextPane) {
		this.curTextPane = curTextPane;
	}
	
	public boolean isChanged() {
		return isChanged;
	}
	
	public void setChanged(boolean isChanged) {
		this.isChanged = isChanged;
	}
	
}
