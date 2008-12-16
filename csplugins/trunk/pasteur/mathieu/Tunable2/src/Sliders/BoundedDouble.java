package Sliders;


public class BoundedDouble{
	
	Double value;
	Double lowerBound;
	Double upperBound;
	Boolean lowerBool;
	Boolean upperBool;
	
	
	public BoundedDouble(Double value,Double lowerBound,Double upperBound,Boolean lowerBool,Boolean upperBool){
		this.value=value;
		this.lowerBound=lowerBound;
		this.upperBound=upperBound;
		this.lowerBool=lowerBool;
		this.upperBool=upperBool;
	}
	
	public Double getValue(){
		return value;
	}

	public Double getLowerBound(){
		return lowerBound;
	}
	
	public Double getUpperBound(){
		return upperBound;
	}
	
	public Boolean getLowerBool(){
		return lowerBool;
	}
	
	public Boolean getUpperBool(){
		return upperBool;
	}
}


