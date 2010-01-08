package plot;
import data.BooleanVector;
import data.DoubleVector;

public final class MyXYSeries {

	private DoubleVector x;
	private DoubleVector y;
	private String name;
	
	public MyXYSeries(DoubleVector x, DoubleVector y, String name)
	{
		this.x = x;
		this.y = y;
		this.name = name;
	}
	
	public DoubleVector getX()
	{
		return x.clone();
	}
	
	public DoubleVector getY()
	{
		return y.clone();
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public int size()
	{
		return x.size();
	}
	
	public void removeXZeros()
	{
		BooleanVector notzeros = x.notEqualTo(0);
		x = x.get(notzeros);
		y = y.get(notzeros);
	}
	
	public void removeYZeros()
	{
		BooleanVector notzeros = y.notEqualTo(0);
		x = x.get(notzeros);
		y = y.get(notzeros);
	}
	
	public void removeXNonReal()
	{
		BooleanVector notzeros = x.isReal();
		x = x.get(notzeros);
		y = y.get(notzeros);
	}
	
	public void removeYNonReal()
	{
		BooleanVector notzeros = y.isReal();
		x = x.get(notzeros);
		y = y.get(notzeros);
	}
	
}
