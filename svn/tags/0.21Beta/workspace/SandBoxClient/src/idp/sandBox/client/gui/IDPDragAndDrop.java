package idp.sandBox.client.gui;

import idp.sandBox.client.constants.Constants;
import idp.sandBox.client.managers.IDPClientSingleton;
import idp.sandBox.client.managers.IDPWindowManager;

import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.dnd.MouseDragGestureRecognizer;
import java.net.URL;
import java.util.HashMap;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class IDPDragAndDrop {

	private ViewTransferable transferable;
	
	private DropTarget dropTarget = null;
	private DragSource dragSourceLeft;
	private DragSource dragSourceRight;
	private TagDragGestureListener dragGestureListener;
	private TagDropTargetListener dropTargetListener;
	private TagDragSourceListener dragSourceListener;
	
	private String current_tag_name = "";
	
	private JLabel left_tag = null; // left dropable tag
	private JLabel right_tag = null; // right dropable tag
	private JLabel current_tag = null;
	
	private JTextPane curTextPane = null;
	
	private int left_offset = 0;
	private int right_offset = 0;
	private int t_left_offset = 0; // unmodified offset
	private int t_right_offset = 0; // unmodified offset
	private int current_offset = 0;

	private SimpleAttributeSet curBgColor = null;
	
	private Image rightTagImage = null;
	private Image leftTagImage = null;
	
	/**
	 * Default constructor
	 */
	IDPDragAndDrop()
	{
		//////////////////////////////////////////////////////////////
		// Load image from resource
		URL imageURL = SandBoxClient.class
				.getResource(Constants.resourcePath + "/cursor.png");
		
		if (imageURL != null)
		{
			leftTagImage = Toolkit.getDefaultToolkit().getImage(imageURL);
		}
		//////////////////////////////////////////////////////////////

		//////////////////////////////////////////////////////////////
		// Load image from resource
		imageURL = SandBoxClient.class
				.getResource(Constants.resourcePath + "/cursor2.png");
		
		if (imageURL != null)
		{
			rightTagImage = Toolkit.getDefaultToolkit().getImage(imageURL);
		}
		//////////////////////////////////////////////////////////////
	}
	
	/**
	 * Colors tag in the document according to tag position (got from tag_scheme), 
	 *  color scheme (got from properties) and selection
	 */
	public void insertDragAndDropComponents(String tag_to_modify, int left, int right)
	{
		/////////////////////////////////////////////////////////////////////////////////
		if (tag_to_modify.length() == 0) return;
		/////////////////////////////////////////////////////////////////////////////////
		
		/////////////////////////////////////////////////////////////////////////////////
		// initialize drag and drop
		current_tag_name = tag_to_modify;
		InitDragAndDrop(left, right);
		/////////////////////////////////////////////////////////////////////////////////
	}
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Some
	 */
	class ViewTransferable implements Transferable {
		DataFlavor[] flavors = new DataFlavor[] { DataFlavor.stringFlavor };

		public ViewTransferable() {
		}

		public DataFlavor[] getTransferDataFlavors() {
			return flavors;
		}

		public boolean isDataFlavorSupported(DataFlavor flavor) {
			if (flavor.equals(flavors[0])) {
				return true;
			}
			return false;
		}

		public Object getTransferData(DataFlavor flavor)
				throws UnsupportedFlavorException {
			if (!isDataFlavorSupported(flavor)) {
				System.out.println("unsuported flavor");
				return null;
			}
			if (flavor.equals(flavors[0])) {
				return (null);
			}
			return null;
		}
	}
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Some
	 */
	class TagDragSourceListener implements DragSourceListener {
		public void dragEnter(DragSourceDragEvent dsde) {
			System.out.println("C3pDragSourceListener : drag Enter");
		}

		public void dragOver(DragSourceDragEvent dsde) {
			//System.out.println("C3pDragSourceListener : drag Over");
		}

		public void dropActionChanged(DragSourceDragEvent dsde) {
			System.out.println("C3pDragSourceListener : drop Action Changed");
		}

		public void dragExit(DragSourceEvent dse) {
			System.out.println("C3pDragSourceListener : drag Exit");
		}

		public void dragDropEnd(DragSourceDropEvent dsde) {

			System.out.println("C3pDragSourceListener : dragDropEnd");
		}

	}
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Some
	 */
	class TagDragGestureListener implements DragGestureListener {

		public void dragGestureRecognized(DragGestureEvent dge) {
			try {
				Object o = dge.getSource();
				MouseDragGestureRecognizer wmdgr = (MouseDragGestureRecognizer)o;

				/////////////////////////////////////////////////////////////////////////
				// get component
				JLabel jl = (JLabel) wmdgr.getComponent();
				/////////////////////////////////////////////////////////////////////////
				
				/////////////////////////////////////////////////////////////////////////
				// check if it's left or right tag, then save it
				if (jl.getName() == "<")
				{
					current_tag = left_tag;
					current_offset = left_offset;
				}
				else
				{
					current_tag = right_tag;
					current_offset = right_offset;
				}
				/////////////////////////////////////////////////////////////////////////
				
				/////////////////////////////////////////////////////////////////////////
				// start Drag and Drop operation
				dge.startDrag(DragSource.DefaultMoveDrop,
						transferable,
						dragSourceListener);
				/////////////////////////////////////////////////////////////////////////
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Some
	 */
	class TagDropTargetListener implements DropTargetListener {
		public void dragEnter(DropTargetDragEvent dtde) {
			System.out.println("drag Enter");
		}

		public void dragOver(DropTargetDragEvent dtde) {
			//System.out.println("drag Over");
		}

		public void dropActionChanged(DropTargetDragEvent dtde) {
			System.out.println("drop Action Changed");
		}

		public void dragExit(DropTargetEvent dte) {
			System.out.println("drag Exit");
		}

		/////////////////////////////////////////////////////////////////////////////////
		// main d'n'd function
		public void drop(DropTargetDropEvent dtde) {
			Point dropLocation = dtde.getLocation();
			System.out.println("DropTargetListener : drop");
			
			StyledDocument doc = curTextPane.getStyledDocument();
			
			try
			{
				Style style = doc.getStyle("Style");
				StyleConstants.setComponent(style, current_tag);
				
				double x = dropLocation.getX();
				double y = dropLocation.getY();
				
				Point p = new Point((int)x,(int)y); 
				int offset = curTextPane.viewToModel(p);
				
				/////////////////////////////////////////////////////////////////////////
				// StyledDocument ignores new line characters so 
				//  we need to find offset modifiers for both indexes
				char[] str = curTextPane.getText().toCharArray();
				int offset_md1 = 0;
				int offset_md2 = 0;
				int offset_md3 = 0;
				int offset_md4 = 0;
				int len = str.length;
				for (int i = 0; i < t_left_offset; i++)
				{
					if (str[i] == '\r')	offset_md1++;
				}
				for (int i = t_left_offset; i < t_right_offset; i++)
				{
					if (i >= len)  break;
					if (str[i] == '\r')	offset_md2++;
				}
				/////////////////////////////////////////////////////////////////////////
				
				/////////////////////////////////////////////////////////////////////////
				// Find the new_offset
				int new_offset = 0;
				if (left_tag == current_tag)
				{
					new_offset = offset+offset_md1;
					for (int i = t_left_offset; i < new_offset; i++)
					{
						if (i >= len)  break;
						if (str[i] == '\r')	offset_md4++;
					}
					for (int i = t_left_offset; i > new_offset; i--)
					{
						if (i >= len)  break;
						if (str[i] == '\r')	offset_md4--;
					}
				}
				else
				{
					new_offset = offset+offset_md1+offset_md2-1;
					
				}
				new_offset += offset_md3 + offset_md4;
				/////////////////////////////////////////////////////////////////////////				
				
				/////////////////////////////////////////////////////////////////////////
				// check xml validity 
				// if xml isn't valid then reject the drag and drop
				if (left_tag == current_tag && new_offset >= right_offset + offset_md1 + offset_md2 - 1) 
				{
					JOptionPane.showMessageDialog(null, "Wrong cursor position.", "Warning", JOptionPane.WARNING_MESSAGE);
					return; 
				}
				if (right_tag == current_tag && new_offset < left_offset + offset_md1 + 1)
				{
					JOptionPane.showMessageDialog(null, "Wrong cursor position.", "Warning", JOptionPane.WARNING_MESSAGE);
					return;
				}
				/////////////////////////////////////////////////////////////////////////
				
				/////////////////////////////////////////////////////////////////////////
				// check xml validity
				//if (checkValidityByRelative(current_tag_name, new_offset) == false)
				//if (checkValidityBySchema(current_tag_name, new_offset) == false)
				//{
				//	JOptionPane.showMessageDialog(null, "The new mark position does not fit the xml tree structure. The action has been canceled.", "Info", JOptionPane.INFORMATION_MESSAGE);
				//	dtde.rejectDrop();
				//	return;
				//}
				/////////////////////////////////////////////////////////////////////////
				
				/////////////////////////////////////////////////////////////////////////
				// check xml validity
				if (checkValidityBySchema(current_tag_name, new_offset) == false)
				{
					JOptionPane.showMessageDialog(null, "The new mark position does not fit the xml tree structure. The action has been canceled.", "Info", JOptionPane.INFORMATION_MESSAGE);
					dtde.rejectDrop();
					return;
				}
				/////////////////////////////////////////////////////////////////////////

				/////////////////////////////////////////////////////////////////////////
				// remove previous d'n'd tag and insert new one
				doc.remove(current_offset,1);
				if (left_tag == current_tag)
				{
					 doc.insertString(offset, "<", style);
				}
				else
				{
					// TODO: check
					//////////////////////////////////////////////////////////////////////////
					// enhancement
					//if (str[offset] == '\n' || str[offset-1] == '\n') offset--;
					//////////////////////////////////////////////////////////////////////////
					
					doc.insertString(offset, ">", style);
				}
				current_offset = offset;
				/////////////////////////////////////////////////////////////////////////
				
				/////////////////////////////////////////////////////////////////////////
				// white brush
				SimpleAttributeSet white = new SimpleAttributeSet();
				StyleConstants.setBackground(white, Color.white);
				/////////////////////////////////////////////////////////////////////////
				
				/////////////////////////////////////////////////////////////////////////
				// save changed position and colourize the range with current or white brush
				if (left_tag == current_tag)
				{
					// if enlarged selection range
					if (current_offset < left_offset)
					{
						SandBoxClient.highlightRange(curTextPane, current_offset, left_offset+1, curBgColor, false);
					}
					else // if narrowed selection range
					{
						SandBoxClient.highlightRange(curTextPane, left_offset, current_offset, white, false);
					}
					left_offset = current_offset;
					t_left_offset = new_offset;
				}
				else
				{
					// if enlarged selection range
					if (current_offset > right_offset)
					{
						SandBoxClient.highlightRange(curTextPane, right_offset, current_offset, curBgColor, false);
					}
					else // if narrowed selection range
					{
						SandBoxClient.highlightRange(curTextPane, current_offset, right_offset+1, white, false);
					}
					right_offset = current_offset;
					t_right_offset = new_offset+1;
				}
				/////////////////////////////////////////////////////////////////////////
				
				/////////////////////////////////////////////////////////////////////////
				// save tag positions to tag scheme
				IDPClientSingleton.getIDPClient().saveTagPosition(current_tag_name, left_offset+offset_md1+offset_md4, right_offset+offset_md1+offset_md2+offset_md3-1);
				// "-1" cause after offset_md1\offset_md2 calculation, we removed one symbol
				// (where d'n'd tag was)
				/////////////////////////////////////////////////////////////////////////
				
				/////////////////////////////////////////////////////////////////////////
				// drag and drop is completed
				dtde.dropComplete(true);
				/////////////////////////////////////////////////////////////////////////
				
				/////////////////////////////////////////////////////////////////////////
				// enable "Save changes" button
				IDPWindowManager.getJButtonSaveChanges().setEnabled(true);
				IDPClientSingleton.getIDPClient().setCurContextIsChanged(true);
				/////////////////////////////////////////////////////////////////////////
			}
			catch (BadLocationException ex)
			{
				ex.printStackTrace();
			}
		}
	}
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 *  Check if the result xml is valid after tag will get new_offset position
	 *  Relative algorithm 
	 */
	protected boolean checkValidityByRelative(String tag, int new_offset)
	{
		boolean bResult = true;
		
		/////////////////////////////////////////////////////////////////////////////////
		HashMap<String, Offset> tag_scheme = IDPClientSingleton.getIDPClient().getTagScheme();
		if (tag_scheme == null) return false;
		Set<String> keys = tag_scheme.keySet();
		/////////////////////////////////////////////////////////////////////////////////
		
		/////////////////////////////////////////////////////////////////////////////////
		if (left_tag == current_tag)
		{
			int old_offset = tag_scheme.get(tag).left;
			for (Object key: keys)
			{
				if (tag.compareTo(key.toString()) == 0) continue;
				Offset offset = tag_scheme.get(key);
				if (old_offset >= offset.right && new_offset < offset.right)
				{
					// we moved into incorrect position
					bResult = false;
					return bResult;
				}
			}
		}
		else
		{
			int old_offset = tag_scheme.get(tag).right;
			for (Object key: keys)
			{
				if (tag.compareTo(key.toString()) == 0) continue;
				Offset offset = tag_scheme.get(key);
				if (old_offset <= offset.left && new_offset > offset.left)
				{
					// we moved into incorrect position
					bResult = false;
					return bResult;
				}
				if (old_offset >= offset.right && new_offset < offset.right)
				{
					// we moved into incorrect position
					bResult = false;
					return bResult;
				}
			}
		}
		/////////////////////////////////////////////////////////////////////////////////
		
		return bResult;
	}
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 *  Check if the result xml is valid after tag will get new_offset position
	 *  Compares according to tag_scheme - it shouldn't be changed
	 */
	protected boolean checkValidityBySchema(String tag, int new_offset)
	{
		/////////////////////////////////////////////////////////////////////////////////
		// Get keys from hash map

		/////////////////////////////////////////////////////////////////////////////////
		HashMap<String, Offset> tag_scheme = IDPClientSingleton.getIDPClient().getTagScheme();
		if (tag_scheme == null) return false;
		Set<String> keys = tag_scheme.keySet();
		/////////////////////////////////////////////////////////////////////////////////
		
		int num = keys.size();
		Integer[] pos = new Integer[2*num];
		Integer[] pos2 = new Integer[2*num];		
		String[] tag_name = new String[2*num];
		String[] tag_name2 = new String[2*num];
		
		/////////////////////////////////////////////////////////////////////////////////
		tag = tag.replace("i:", "");
		
		/////////////////////////////////////////////////////////////////////////////////
		// put into separate array to sort it in future
		int i = 0;
		String tagName = "";
		for (Object key : keys)
		{
			/////////////////////////////////////////////////////////////////////////////
			// get offset
			Offset offset = tag_scheme.get(key);
			
			/////////////////////////////////////////////////////////////////////////////
			// LEFT
			pos[i] = offset.left;
			
			/////////////////////////////////////////////////////////////////////////////
			tagName = key.toString();
			tagName = tagName.replace("i:", "");
			tag_name[i] = tagName;
			
			/////////////////////////////////////////////////////////////////////////////
			// copy
			pos2[i] = pos[i];
			tag_name2[i] = tag_name[i];
			
			/////////////////////////////////////////////////////////////////////////////
			if (tagName.compareTo(tag) == 0 && left_tag == current_tag)
			{
				pos2[i] = new_offset;
			}
			
			i++;
			
			// LEFT
			/////////////////////////////////////////////////////////////////////////////
			
			/////////////////////////////////////////////////////////////////////////////
			// RIGHT
			pos[i] = offset.right;
			
			/////////////////////////////////////////////////////////////////////////////
			tag_name[i] = tagName.replace("<","</");
			
			/////////////////////////////////////////////////////////////////////////////
			// copy
			pos2[i] = pos[i];
			tag_name2[i] = tag_name[i];
			
			/////////////////////////////////////////////////////////////////////////////
			if (tagName.compareTo(tag) == 0 && right_tag == current_tag)
			{
				pos2[i] = new_offset;
			}
			
			i++;
			
			// RIGHT
			/////////////////////////////////////////////////////////////////////////////
		}
		
		/////////////////////////////////////////////////////////////////////////////////
		// Sort tags positions
		sortPositions(pos, tag_name, num);
		sortPositions(pos2, tag_name2, num);
		
		/////////////////////////////////////////////////////////////////////////////////
		// compare and get the result
		for (i = 0; i < 2*num; i++)
		{
			if (tag_name[i].compareTo(tag_name2[i]) != 0) return false;
		}
		
		/////////////////////////////////////////////////////////////////////////////////
		// validity result is true
		return true;
		/////////////////////////////////////////////////////////////////////////////////
	}
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Sort positions of tags' occurrences in the tag scheme
	 */
	void sortPositions(Integer[] pos, String[] tag_name, int size)
	{
		String currentXML = IDPClientSingleton.getIDPClient().getCurrentXML();
		String l_currentXML = currentXML.replace("i:", "");
		int temp = 0;
		String stemp = "";
		int i = 0;
		for (i = 0; i < 2*size - 1; i++)
			for (int j = i + 1; j < 2*size; j++)
			{
				// just swap
				if (pos[i].compareTo(pos[j]) > 0)
				{
					temp = pos[i];
					pos[i] = pos[j];
					pos[j] = temp;
					stemp = tag_name[i];
					tag_name[i] = tag_name[j];
					tag_name[j] = stemp;
				}
				// if positions are equal then we should check XML
				if (pos[i].compareTo(pos[j]) == 0)
				{
					String tag1 = tag_name[i];
					String tag2 = tag_name[j];
					int i1 = l_currentXML.indexOf(tag1);
					int i2 = l_currentXML.indexOf(tag2); 
					if (i1 > i2)
					{
						temp = pos[i];
						pos[i] = pos[j];
						pos[j] = temp;
						stemp = tag_name[i];
						tag_name[i] = tag_name[j];
						tag_name[j] = stemp;
					}
				}
			}
	}
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 *  Delete drag and drop components from the text
	 */
	public StringBuffer DeleteDragAndDropTags(StringBuffer text)
	{
		int i = 0;
		/////////////////////////////////////////////////////////////////////////////////
		// remove drag and drop tags
		if (IsWithDnD() == true) // if they weren't removed before
		{
			/////////////////////////////////////////////////////////////////////////////
			// StyledDocument ignores new line characters so 
			//  we need to find offset modifiers for both indexes
			int offset_md1 = 0;
			int offset_md2 = 0;
			for (i = 0; i < t_left_offset; i++)
			{
				if (text.charAt(i) == '\r')	offset_md1++;
			}
			for (i = t_left_offset; i < t_right_offset; i++)
			{
				if (text.charAt(i) == '\r')	offset_md2++;
			}
			
			/////////////////////////////////////////////////////////////////////////////
			// delete d'n'd components
			text.deleteCharAt(right_offset+offset_md1+offset_md2);
			text.deleteCharAt(left_offset+offset_md1);
			
			/////////////////////////////////////////////////////////////////////////////
			// nullification
			left_offset = 0;
			right_offset = 0;
		}
		/////////////////////////////////////////////////////////////////////////////////
	
		/////////////////////////////////////////////////////////////////////////////////
		// Result
		return text;
	}
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 *  Clear text from drag and drop components
	 *  @param text - text to clear
	 */
	public StringBuffer ClearFromDragAndDropTags(StringBuffer text)
	{
		int i = 0;

		/////////////////////////////////////////////////////////////////////////////
		// StyledDocument ignores new line characters so 
		//  we need to find offset modifiers for both indexes
		int offset_md1 = 0;
		int offset_md2 = 0;
		for (i = 0; i < t_left_offset; i++)
		{
			if (text.charAt(i) == '\r')	offset_md1++;
		}
		for (i = t_left_offset; i < t_right_offset; i++)
		{
			if (text.charAt(i) == '\r')	offset_md2++;
		}
			
		/////////////////////////////////////////////////////////////////////////////
		// delete d'n'd components
		text.deleteCharAt(right_offset+offset_md1+offset_md2);
		text.deleteCharAt(left_offset+offset_md1);
		
		/////////////////////////////////////////////////////////////////////////////////
		// Result
		return text;
	}
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Determines is there d'n'd components 
	 */
	public boolean IsWithDnD()
	{
		return left_offset != right_offset;
	}
	/////////////////////////////////////////////////////////////////////////////////////
	
	/////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Insert two components (JLabel with images above them)
	 */
	private void InitDragAndDrop(int left, int right)
	{
		/////////////////////////////////////////////////////////////////////////////////
		// save positions
		this.left_offset = left;
		this.right_offset = right;
		/////////////////////////////////////////////////////////////////////////////////
		
		/////////////////////////////////////////////////////////////////////////////////
		// get the styled doc from the current pane
		StyledDocument doc = curTextPane.getStyledDocument();
				
		/////////////////////////////////////////////////////////////////////////////////
		// add style
		Style style = doc.addStyle("Style", null);
		//Style style = doc.getStyle("Style");
		
		/////////////////////////////////////////////////////////////////////////////////
		// get text
		char[] str = curTextPane.getText().toCharArray();
		
		/////////////////////////////////////////////////////////////////////////////////
		// set the view of drag and drop tags
		left_tag = new JLabel("");
		left_tag.setName("<");
		right_tag = new JLabel("");
		right_tag.setName(">");
		
		right_tag.setIcon(new ImageIcon(rightTagImage));
		left_tag.setIcon(new ImageIcon(leftTagImage));
		
		////////////////////////////////////////////////////////////////////////////////
		// StyledDocument ignores new line characters so 
		//  we need to find offset modifiers for both indexes
		int offset_md1 = 0;
		int offset_md2 = 0;
		int len = str.length;
		for (int i = 0; i < left_offset; i++)
		{
			if (str[i] == '\r')	offset_md1++;
		}
		for (int i = left_offset; i < right_offset; i++)
		{
			if (i >= len)  break;
			if (str[i] == '\r')	offset_md2++;
		}
		/////////////////////////////////////////////////////////////////////////////////
		
		/////////////////////////////////////////////////////////////////////////////////
		// insert tags (labels) into the text
		try 
		{
			StyleConstants.setComponent(style, right_tag);
			doc.insertString(right_offset-offset_md1-offset_md2, ">", style);
			// +1 because we're also inserting left tag before this one
			t_right_offset = right_offset;
			right_offset = right_offset-offset_md1-offset_md2+1;

			StyleConstants.setComponent(style, left_tag);
			doc.insertString(left_offset-offset_md1, "<", style);
			t_left_offset = left_offset;
			left_offset = left_offset-offset_md1;
		}
		catch (BadLocationException ex)
		{
			ex.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		/////////////////////////////////////////////////////////////////////////////////
		
		/////////////////////////////////////////////////////////////////////////////////
		// Drag and Drop Initializing
		transferable = new ViewTransferable();
		dropTargetListener = new TagDropTargetListener();
		dropTarget = new DropTarget(curTextPane, dropTargetListener);
		dropTarget.toString(); // to remove warning
		dragSourceLeft = new DragSource();
		dragSourceRight = new DragSource();
		dragGestureListener = new TagDragGestureListener();
		dragSourceListener = new TagDragSourceListener();
		dragSourceLeft.createDefaultDragGestureRecognizer(left_tag,
				java.awt.dnd.DnDConstants.ACTION_MOVE, dragGestureListener);
		dragSourceRight.createDefaultDragGestureRecognizer(right_tag,
				java.awt.dnd.DnDConstants.ACTION_MOVE, dragGestureListener);
		/////////////////////////////////////////////////////////////////////////////////
	}
	/////////////////////////////////////////////////////////////////////////////////////

	public SimpleAttributeSet getCurBgColor() {
		return curBgColor;
	}

	public void setCurBgColor(SimpleAttributeSet curBgColor) {
		this.curBgColor = curBgColor;
	}

	public void setCurTextPane(JTextPane curTextPane) {
		this.curTextPane = curTextPane;
	}
}
