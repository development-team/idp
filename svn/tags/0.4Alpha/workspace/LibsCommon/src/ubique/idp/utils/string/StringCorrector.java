package ubique.idp.utils.string;

/**
 * Class to process Strings.
 * @author Talanov Max
 *
 */
public class StringCorrector {

	static public String slashifyPeriods(String in) {
		return in.replaceAll("\\.", "\\\\.");
	}
}
