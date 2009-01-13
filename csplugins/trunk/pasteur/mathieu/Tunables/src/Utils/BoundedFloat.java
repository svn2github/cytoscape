package Utils;

/**
 * A bounded float object.
 */
public class BoundedFloat extends AbstractBounded<Float> {

	/**
	 * Creates a new Bounded object.
	 *
	 * @param lower  DOCUMENT ME!
	 * @param initValue  DOCUMENT ME!
	 * @param upper  DOCUMENT ME!
	 * @param lowerStrict  DOCUMENT ME!
	 * @param upperStrict  DOCUMENT ME!
	 */
	public BoundedFloat(final Float lower, final Float initValue, final Float upper, boolean lowerStrict, boolean upperStrict) {
		super(lower,initValue,upper,lowerStrict,upperStrict);
	}
}
