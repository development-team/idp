package ubique.idp.normalisation.ga.date.matcher;

import ubique.idp.utils.Tuple3;

/**
 * Interface for static normalisation methods
 * @author talanovm
 *
 */
public interface ANormalizer<T> {
	/**
	 * Normalises part of date string.
	 * @param probDate - String to normalise
	 * @return - Tuple3<T, Integer, Integer> of result of normalisation(Integer, String), start position of matched string, end position of matched string
	 */
	public Tuple3<T, Integer, Integer> normalise(String probDate);
}
