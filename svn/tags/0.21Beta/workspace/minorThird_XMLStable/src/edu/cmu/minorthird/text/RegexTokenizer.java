/* Copyright 2007, Carnegie Mellon, All Rights Reserved */

package edu.cmu.minorthird.text;

import org.apache.log4j.Logger;
//import org.apache.log4j.Level;
import java.io.InputStream;
import java.util.Properties;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Maintains information about what's in a set of documents.
 * Specifically, this contains a set of character sequences (TextToken's)
 * from some sort of set of containing documents - typically found by
 * tokenization.
 */

public class RegexTokenizer implements Tokenizer
{
    private static Logger log = Logger.getLogger(RegexTokenizer.class);

    /** How to split tokens up */
    public static final String TOKEN_REGEX_PROP = "edu.cmu.minorthird.tokenRegex";
    // Attempt to create universal tokenizer including XML entities
    // &quot; &amp; &apos; &lt; &gt;
    // public static final String TOKEN_REGEX_DEFAULT_VALUE = "\\s*([0-9]+|[a-zA-Z]+|\\W)\\s*";
    // public static final String TOKEN_REGEX_DEFAULT_VALUE = "\\s*([\\p{L}[0-9]&&[^\\p{Punct}]]+|\\W)\\s*";
    // Select space + any letter(en) + numbers + everything except punctuation + XML entities and one non word letter
    public static final String TOKEN_REGEX_DEFAULT_VALUE = "\\s*([\\p{L}[0-9]&&[^\\p{Punct}]]+|\\&[a-z]+\\;|\\W)\\s*";
    

    public static String standardTokenRegexPattern;
    static {
        Properties props = new Properties();
	try {
	    InputStream in = FancyLoader.class.getClassLoader().getResourceAsStream("token.properties");
	    if (in != null) {
		props.load(in);
		log.debug("loaded properties from stream "+in);
	    } else {
		log.info("no token.properties found on classpath");
	    }
        } catch (Exception ex) {
            log.debug("can't open token.properties:"+ex);
        }
        standardTokenRegexPattern = 
            props.getProperty(TOKEN_REGEX_PROP, System.getProperty(TOKEN_REGEX_PROP,TOKEN_REGEX_DEFAULT_VALUE));
        log.info("tokenization regex: "+standardTokenRegexPattern);
    }

    public String regexPattern = standardTokenRegexPattern;

    public RegexTokenizer() {}
    public RegexTokenizer(String pattern) { this.regexPattern = pattern; }

    /** Tokenize a string. */
    public String[] splitIntoTokens(String string) {
	ArrayList list = new ArrayList();
	Pattern pattern = Pattern.compile(regexPattern);
	Matcher matcher = pattern.matcher(string);
	while (matcher.find()) {
	    list.add( matcher.group(1) );
	}
	return (String[]) list.toArray( new String[list.size()] );
    }

    /** Tokenize a document. */
    public TextToken[] splitIntoTokens(Document document) {
        ArrayList tokenList = new ArrayList();
	TextToken[] tokenArray;
        String string = document.getText();

        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(string);				
        
        while (matcher.find())  {
            tokenList.add( new TextToken(document, matcher.start(1), matcher.end(1)-matcher.start(1)) );
        }
        tokenArray = (TextToken[])tokenList.toArray(new TextToken[0]);
        
	return tokenArray;
    }
}
