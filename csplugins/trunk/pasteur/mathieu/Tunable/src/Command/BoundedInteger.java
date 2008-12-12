package Command;


public class BoundedInteger{
	
	Integer value;
	Integer lowerBound;
	Integer upperBound;
	Boolean lowerBool;
	Boolean upperBool;
	
	
	public BoundedInteger(Integer value,Integer lowerBound,Integer upperBound,Boolean lowerBool,Boolean upperBool){
		this.value=value;
		this.lowerBound=lowerBound;
		this.upperBound=upperBound;
		this.lowerBool=lowerBool;
		this.upperBool=upperBool;
	}
	
	public Integer getValue(){
		return value;
	}

	public Integer getLowerBound(){
		return lowerBound;
	}
	
	public Integer getUpperBound(){
		return upperBound;
	}
	
	public Boolean getLowerBool(){
		return lowerBool;
	}
	
	public Boolean getUpperBool(){
		return upperBool;
	}
}
