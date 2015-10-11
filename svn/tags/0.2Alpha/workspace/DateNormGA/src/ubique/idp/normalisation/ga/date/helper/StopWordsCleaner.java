/**
 * Copyright Talanov Max under GPL license.
 */
package ubique.idp.normalisation.ga.date.helper;

import java.io.File;
import java.io.FileNotFoundException;

import ubique.idp.utils.io.FileReader;
import ubique.idp.utils.string.StringCorrector;

/**
 * Class to clean words from specified file delimited by new line from incoming string.
 * @author Talanov Max
 * 
 */
public class StopWordsCleaner {

	/**
	 * Cleans words from specified file from specified string. 
	 * @param in - String to clean
	 * @param stopwordsFileAddress - file address with stop words
	 * @param encoding - the encoding of stop words file
	 * @return String cleaned
	 * @throws FileNotFoundException - in case FileReader throws exception 
	 */
	static public String apply(String in, String stopwordsFileAddress,
			String encoding) throws FileNotFoundException {

		File stopWordsFile = new File(stopwordsFileAddress);
		String[] stopWords = FileReader.readFileByLine(stopWordsFile, encoding);
		String temp = in;
		for (String word : stopWords) {
			// in case this is a word in the middle of string
			String sWord = StringCorrector.slashifyPeriods(word);
			temp = temp.replaceAll("(?:\\s*)" + sWord + "(?:[\\s\\n]+)", "");
			
			// in case this is begining of the string
			int spaceIndex = temp.indexOf(" ");
			int stopWordIndex = temp.indexOf(word);
			String tempTail = "";
			String tempHead = "";
			if (stopWordIndex < spaceIndex) {
				temp = temp.replaceFirst(sWord, "");
			}
	
			// in case this is the end of the string
			spaceIndex = temp.lastIndexOf(" ");
			stopWordIndex = temp.lastIndexOf(word);
			if (stopWordIndex > spaceIndex && (word.length() + stopWordIndex) == temp.length()) {
				tempHead = temp.substring(0, spaceIndex);
				tempTail = temp.substring(spaceIndex);
				temp = tempHead + tempTail.replaceFirst(sWord, "");
			}
		}
		return temp;
	}
}
