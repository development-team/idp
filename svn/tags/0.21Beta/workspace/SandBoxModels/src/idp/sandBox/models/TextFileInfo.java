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
public class TextFileInfo implements Serializable {

	// private File file;
	private String description;
	@Deprecated
	private String absolutePath = "";
	private String relativePath = "";
	private String name;
	private boolean isDirectory;

	private final String FSDelimiter = "/";
	private String abstractRelativePath;

	public String getAbstractRelativePath() {
		return abstractRelativePath;
	}

	/**
	 * Constructor that sets up filename, absolutePath and isDirectory flag.
	 * 
	 * @param file
	 *            - File to setup.
	 */
	@Deprecated
	public TextFileInfo(File file) {
		this.absolutePath = file.getAbsolutePath();
		this.name = file.getName();
		this.isDirectory = file.isDirectory();
	}

	/**
	 * Creates TextFileInfo for folder.
	 * 
	 * @param folderName
	 *            - the name of the folder.
	 * @param relativePath
	 *            - the relative path to this folder.
	 */
	public TextFileInfo(String folderName, String relativePath) {
		this.absolutePath = null;
		this.name = folderName;
		this.relativePath = relativePath;
		this.isDirectory = true;
	}

	/**
	 * Creates TextFileInfo on base of file name and relative path provided
	 * 
	 * @param file
	 *            - the file to create TextFileInfo
	 * @param relativePath
	 *            - the String of relative path
	 */
	public TextFileInfo(File file, String relativePath) {
		this.absolutePath = null;
		this.name = file.getName();
		this.relativePath = relativePath + FSDelimiter + this.name;
		this.isDirectory = file.isDirectory();
	}

	/**
	 * Constructor that setups filename, absolute, relative path and isDirectory
	 * flag, on base of File and project directory File, most complete and
	 * recommended.
	 * 
	 * @param file
	 *            - File to process.
	 * @param projectDir
	 *            - project directory File.
	 */
	public TextFileInfo(File file, File projectDir) {
		
		this.absolutePath = file.getAbsolutePath();
		this.name = file.getName();
		this.relativePath = this.absolutePath.substring(projectDir
				.getAbsolutePath().length());
		this.isDirectory = file.isDirectory();
		this.abstractRelativePath = file.getPath().substring(projectDir.getPath().length());
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Returns the name of the file as string representation of it
	 * 
	 * @return String representation of the Text File
	 */
	public String toString() {
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

	public void setName(String name) {
		this.name = name;
	}

	@Deprecated
	public String getAbsolutePath() {
		return absolutePath;
	}

	public String getRelativePath() {
		return relativePath;
	}

	public boolean isDirectory() {
		return isDirectory;
	}

	public boolean equals(TextFileInfo toCompare) {
		return this.relativePath.equals(toCompare.getRelativePath());
	}

	@Override
	public int hashCode() {
		return this.relativePath.hashCode();
	}

}
