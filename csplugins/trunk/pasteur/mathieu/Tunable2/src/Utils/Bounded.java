package Utils;


public class Bounded<O extends Comparable<String>>{
	
	O value;
	O lowerBound;
	O upperBound;
	Boolean lowerBool;
	Boolean upperBool;
	
	
	public Bounded(O lowerBound,O upperBound,Boolean lowerBool,Boolean upperBool){
		this.lowerBound=lowerBound;
		this.upperBound=upperBound;
		this.lowerBool=lowerBool;
		this.upperBool=upperBool;
		
		if (lowerBound == null)
			throw new NullPointerException("lower bound is null!");

		if (upperBound == null)
			throw new NullPointerException("upper bound is null!");

		if (lowerBound.compareTo((String) upperBound) >= 0)
			throw new IllegalArgumentException("lower value is greater than or equal to upper value");
	}
	
	public O getValue(){
		return value;
	}

	public O getLowerBound(){
		return lowerBound;
	}
	
	public O getUpperBound(){
		return upperBound;
	}
	
	public Boolean isLowerBool(){
		return lowerBool;
	}
	
	public Boolean isUpperBool(){
		return upperBool;
	}
	
	public void setValue(O v){
		if (v == null)
			throw new NullPointerException("value is null!");

		//int up = v.compareTo((String) upperBound);
		int up = Integer.parseInt((String) v) - Integer.parseInt((String) upperBound);
		
		
		if (upperBool) {
			if (up >= 0)
				throw new IllegalArgumentException("value is greater than or equal to upper limit");
		} else {
			if (up > 0)
				throw new IllegalArgumentException("value is greater than upper limit");
		}

		//int low = v.compareTo((String) lowerBound);
		int low = Integer.parseInt((String) v) - Integer.parseInt((String) lowerBound);
		if (lowerBool) {
			if (low <= 0)
				throw new IllegalArgumentException("value is less than or equal to lower limit");
		} else {
			if (low < 0)
				throw new IllegalArgumentException("value is less than lower limit");
		}
		value = v;
	}
	
}
