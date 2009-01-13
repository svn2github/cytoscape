package Utils;

/**
 * A bounded double object.
 */
public class BoundedDouble extends AbstractBounded<Double> {

	/**
	 * Creates a new Bounded object.
	 *
	 * @param lower  DOCUMENT ME!
	 * @param initValue  DOCUMENT ME!
	 * @param upper  DOCUMENT ME!
	 * @param lowerStrict  DOCUMENT ME!
	 * @param upperStrict  DOCUMENT ME!
	 */
	public BoundedDouble(final Double lower, final Double initValue, final Double upper, boolean lowerStrict, boolean upperStrict) {
		super(lower,initValue,upper,lowerStrict,upperStrict);
	}
}
