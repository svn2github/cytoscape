package Utils;

/**
 * A bounded Integer object.
 */
public class BoundedInteger extends AbstractBounded<Integer> {

	/**
	 * Creates a new Bounded object.
	 *
	 * @param lower  DOCUMENT ME!
	 * @param initValue  DOCUMENT ME!
	 * @param upper  DOCUMENT ME!
	 * @param lowerStrict  DOCUMENT ME!
	 * @param upperStrict  DOCUMENT ME!
	 */
	public BoundedInteger(final Integer lower, final Integer initValue, final Integer upper, boolean lowerStrict, boolean upperStrict) {
		super(lower,initValue,upper,lowerStrict,upperStrict);
	}
}
