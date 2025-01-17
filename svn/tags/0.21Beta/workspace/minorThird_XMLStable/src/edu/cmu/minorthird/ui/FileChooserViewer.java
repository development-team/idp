package edu.cmu.minorthird.ui;

import edu.cmu.minorthird.text.FancyLoader;
import edu.cmu.minorthird.util.gui.ComponentViewer;
import edu.cmu.minorthird.util.gui.Viewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * A panel with a text area and a browse button
 * @author ksteppe
 */
public class FileChooserViewer extends ComponentViewer implements ActionListener
{
  private JFileChooser chooser = new JFileChooser();
  private JTextField textField = new JTextField(30);
  private File selectedFile = null;
  private boolean openFile = true;
  private JButton button = new JButton("Browse");
  private String defaultDir = "."; //= System.getProperty("minorthird");

  public FileChooserViewer(Viewer superView)
  {
    setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    setSuperView(superView);
    setContent(selectedFile, true);
    button.addActionListener(this);
    componentFor(null);

  }

  public void setFileSelectionMode(int mode)
  { chooser.setFileSelectionMode(mode); }

  public void setFile(File f)
  {
    selectedFile = f;

    textField.setText( selectedFile.getAbsolutePath() );
    //fire the action up the chain
    sendSignal(OBJECT_SELECTED, selectedFile);
    componentFor(null);
  }

  public JComponent componentFor(Object o)
  {
    removeAll();
    log.debug("build panel");
    //build up panel
    setLayout(new GridLayout(0, 2));
    add(textField);
    textField.setEditable(false);
    add(button);
    this.setVisible(true);

    return this;
  }

  public void addActionListener(ActionListener l)
  { button.addActionListener(l); }

  public void actionPerformed(ActionEvent e)
  {
    if (selectedFile != null)
      chooser.setSelectedFile(selectedFile);
    else
    {
      chooser.setSelectedFile(new File(defaultDir));
    }
    int returnVal = openFile ? chooser.showOpenDialog(null) : chooser.showSaveDialog(null);
    if (returnVal==JFileChooser.APPROVE_OPTION)
      setFile(chooser.getSelectedFile());
  }

  // default: recieve anything that can be converted to a component
  public boolean canReceive(Object obj)
  { return false; }

  public void receiveContent(Object content)
  { return; }

  public String getDefaultDir()
  { return defaultDir; }

  public void setDefaultDir(String defaultDir)
  {
    if (defaultDir != null)
      this.defaultDir = defaultDir;
  }
}
