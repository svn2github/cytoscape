package Utils;



public class Bounded<String extends Comparable<String>>{	
	String value;
	String lowerBound;
	String upperBound;
	Boolean lowerBool;
	Boolean upperBool;
	
	
	public Bounded(String lowerBound,String upperBound,Boolean lowerBool,Boolean upperBool){
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
	
	public String getValue(){
		return value;
	}

	public String getLowerBound(){
		return lowerBound;
	}
	
	public String getUpperBound(){
		return upperBound;
	}
	
	public Boolean isLowerBool(){
		return lowerBool;
	}
	
	public Boolean isUpperBool(){
		return upperBool;
	}
	
	public void setValue(String v,Class<?> boundedclass){
		if (v == null)
			throw new NullPointerException("value is null!");
		
		if(v.toString().equals("null")==false){
			Double val = Double.parseDouble(v.toString());
			//System.out.println("val="+val.intValue());
			
			if(boundedclass==Double.class){
				
				Double upbound =  Double.parseDouble(upperBound.toString());
				int up = val.compareTo(upbound);
				if (upperBool){
					if (up >= 0)
						throw new IllegalArgumentException("value is greater than or equal to upper limit");
				} else {
					if (up > 0)
						throw new IllegalArgumentException("value is greater than upper limit");
				}
		
				Double lowbound = Double.parseDouble(lowerBound.toString());
				int low = val.compareTo(lowbound);
				if (lowerBool){
					if (low <= 0)
						throw new IllegalArgumentException("value is less than or equal to lower limit");
				} else {
					if (low < 0)
						throw new IllegalArgumentException("value is less than lower limit");
				}
				
				v=(String) val;
				System.out.println("v pour val(double)"+ v);
			}
			else if(boundedclass==Integer.class){
				//Integer val = Integer.parseInt(v.toString());
				Integer valu = val.intValue();
				Integer upbound =  Integer.parseInt(upperBound.toString());
				int up = valu.compareTo(upbound);
				if (upperBool) {
					if (up >= 0)
						throw new IllegalArgumentException("value is greater than or equal to upper limit");
				} else {
					if (up > 0)
						throw new IllegalArgumentException("value is greater than upper limit");
				}
		
				Integer lowbound = Integer.parseInt(lowerBound.toString());
				int low = valu.compareTo(lowbound);
				if (lowerBool) {
					if (low <= 0)
						throw new IllegalArgumentException("value is less than or equal to lower limit");
				} else {
					if (low < 0)
						throw new IllegalArgumentException("value is less than lower limit");
				}
			v=(String) Integer.toString(valu);
			System.out.println("v pour valu(int) ="+ v);
			}
		}
		value = v;
		System.out.println("value="+value);
	}
	
}
