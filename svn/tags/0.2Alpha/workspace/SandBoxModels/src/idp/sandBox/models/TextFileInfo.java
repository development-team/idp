/**
 *
 */
package idp.sandBox.models;

import java.io.File;
import java.io.Serializable;

/**
 * @author Mtalanov
 *
 */
public class TextFileInfo implements Serializable{

	// private File file;
	private String description;
	private String absolutePath = "";
	private String relativePath = "";
	private String name;
	private boolean isDirectory;

	public TextFileInfo (File file) {
		this.absolutePath = file.getAbsolutePath();
		this.name = file.getName();
		isDirectory = file.isDirectory();
	}
	
	public TextFileInfo(File file, File dir) {
		this.absolutePath = file.getAbsolutePath();
		this.name = file.getName();
		this.relativePath = this.absolutePath.substring(dir.getAbsolutePath().length());
		isDirectory = file.isDirectory();
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Returns the name of the file as string representation of it
	 * @return String representation of the Text File
	 */
	public String toString(){
		return this.name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	public String getName() {
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getAbsolutePath() {
		return absolutePath;
	}

	public String getRelativePath() {
		return relativePath;
	}
	
	public boolean isDirectory()
	{
		return isDirectory;
	}

}
