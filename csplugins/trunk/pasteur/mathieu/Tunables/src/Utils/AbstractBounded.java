package Utils;

import javax.swing.JOptionPane;

/**
 * A bounded number object.
 *
 * @param <N>  DOCUMENT ME!
 */
abstract class AbstractBounded<N extends Comparable<N>> {

	protected N value;

	protected N initValue;
	final protected N lower;
	final protected N upper;
	final protected boolean upperStrict;
	final protected boolean lowerStrict;

	/**
	 * Creates a new Bounded object.
	 *
	 * @param lower  DOCUMENT ME!
	 * @param upper  DOCUMENT ME!
	 * @param lowerStrict  DOCUMENT ME!
	 * @param upperStrict  DOCUMENT ME!
	 */
	AbstractBounded(final N lower, final N initValue, final N upper, boolean lowerStrict, boolean upperStrict) {
		if (lower == null)
			throw new NullPointerException("lower bound is null!");

		if (upper == null)
			throw new NullPointerException("upper bound is null!");

		if (lower.compareTo(upper) >= 0)
			throw new IllegalArgumentException("lower value is greater than or equal to upper value");

		
		this.lower = lower;
		this.upper = upper;
		this.lowerStrict = lowerStrict;
		this.upperStrict = upperStrict;
		this.initValue = initValue;
		setValue(initValue);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public N getUpperBound() {
		return upper;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public N getLowerBound() {
		return lower;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean isUpperBoundStrict() {
		return upperStrict;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean isLowerBoundStrict() {
		return lowerStrict;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public N getValue() {
		return value;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param v DOCUMENT ME!
	 */
	public void setValue(N v) {
		if (v == null){
			JOptionPane.showMessageDialog(null, "Value is missing", "Alert",JOptionPane.ERROR_MESSAGE);
			//throw new NullPointerException("value is null!");
			value = initValue;
		}

		int up = v.compareTo(upper);

		value = v;
		
		if (upperStrict) {
			if (up > 0){
				JOptionPane.showMessageDialog(null, "Value is much than upper limit", "Error",JOptionPane.ERROR_MESSAGE);
				//////////throw new IllegalArgumentException("value is greater than or equal to upper limit");
				//value = upper;
				value = initValue;
			}
			if(up == 0){
				JOptionPane.showMessageDialog(null, "Value can not be equal to upper limit", "Error",JOptionPane.ERROR_MESSAGE);
				//////////throw new IllegalArgumentException("value is greater than or equal to upper limit");
				//value = upper;
				value = initValue;
				
			}
		} else {
			if (up > 0){
				JOptionPane.showMessageDialog(null, "Value is much than upper limit", "Error",JOptionPane.ERROR_MESSAGE);
				///////////throw new IllegalArgumentException("value is greater than upper limit");
				//value = upper;
				value = initValue;
			}
		}

		int low = v.compareTo(lower);

		if (lowerStrict) {
			if (low < 0){	
				JOptionPane.showMessageDialog(null, "Value is less than lower limit", "Error",JOptionPane.ERROR_MESSAGE);
				///////////throw new IllegalArgumentException("value is less than or equal to lower limit");
				//value = lower;
				value = initValue;
			}
			if (low == 0){	
				JOptionPane.showMessageDialog(null, "Value can not be equal to lower limit", "Error",JOptionPane.ERROR_MESSAGE);
				///////////throw new IllegalArgumentException("value is less than or equal to lower limit");
				//value = lower;
				value = initValue;
			}
		} else {
			if (low < 0){
				JOptionPane.showMessageDialog(null, "Value is less than lower limit", "Error",JOptionPane.ERROR_MESSAGE);
				//////////throw new IllegalArgumentException("value is less than lower limit");
				//value = lower;
				value = initValue;
			}
		}		
	}
}
