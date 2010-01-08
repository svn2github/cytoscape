package structures;

public class Duo<T1,T2> {

	protected T1 i1;
	protected T2 i2;
	
	/**
	 * Copy constructor
	 * @param inter Interaction you wish to copy
	 */
	public Duo(Duo<T1,T2> inter)
	{
		i1 = inter.getI1();
		i2 = inter.getI2();
	}
	
	public Duo(T1 s1, T2 s2)
	{
		i1 = s1;
		i2 = s2;
	}
	
	public T1 getI1()
	{
		return i1;
	}
	
	public T2 getI2()
	{
		return i2;
	}
	
	public void setI1(T1 val)
	{
		i1 = val;
	}
	
	public void setI2(T2 val)
	{
		i2 = val;
	}
			
	public boolean contains(Object o)
	{
		return (i1.equals(o) || i2.equals(o));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object inter)
	{
		if (inter == null) return false;
		if (inter instanceof Duo)
		{
			Duo other = (Duo)inter;
			if (i1.equals(other.getI1()) && i2.equals(other.getI2())) return true;
			else return false;
		}else return false;
	}
	
	@Override
	public int hashCode()
	{
		return i1.hashCode()+i2.hashCode();
	}
	
	@Override
	public Duo<T1,T2> clone()
	{
		Duo<T1,T2> newint = new Duo<T1,T2>(i1,i2);
		
		return newint;
	}
		
	public String toString()
	{
		return i1.toString()+"-"+i2.toString();
	}
}
