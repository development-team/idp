package ubique.idp.utils.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class contains util/helpers methods for IO.
 * @author Talanov Max
 *
 */
public class FileReader {

	/** 
	 * Reads file contents line by line and returns array of this lines.
	 * @param aFile - the file to read 
	 * @param encoding - encoding to read file
	 * @return array of Strings that contains lines contents of file
	 * @throws FileNotFoundException in case aFile is not found
	 * @throws IllegalArgumentException in case encoding is not supported, IOException is thrown during file read or close
	 */
	static public String[] readFileByLine(File aFile, String encoding)
			throws FileNotFoundException, IllegalArgumentException {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					aFile), encoding));
			String thisLine;
			List<String> res = new ArrayList<String>();
			while ((thisLine = br.readLine()) != null) {
				res.add(thisLine);
			}
			return res.toArray(new String[0]);
		} catch (FileNotFoundException e) {
			throw e;
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("Specified encoding " + encoding + " is not supported");
		} catch (IOException e) {
			throw new IllegalArgumentException("Could not read specified file " + aFile);
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				throw new IllegalArgumentException("Could not close specified file" + aFile);
			}
		}
		
	}
}
