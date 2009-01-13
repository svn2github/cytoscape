package Utils;

/**
 * A bounded long object.
 */
public class BoundedLong extends AbstractBounded<Long> {

	/**
	 * Creates a new Bounded object.
	 *
	 * @param lower  DOCUMENT ME!
	 * @param initValue  DOCUMENT ME!
	 * @param upper  DOCUMENT ME!
	 * @param lowerStrict  DOCUMENT ME!
	 * @param upperStrict  DOCUMENT ME!
	 */
	public BoundedLong(final Long lower, final Long initValue, final Long upper, boolean lowerStrict, boolean upperStrict) {
		super(lower,initValue,upper,lowerStrict,upperStrict);
	}
}
