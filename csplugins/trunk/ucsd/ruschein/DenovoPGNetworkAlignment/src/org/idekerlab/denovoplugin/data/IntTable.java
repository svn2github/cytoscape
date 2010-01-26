package data;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.primitives.ArrayIntList;

public class IntTable extends DataTable{

	private List<ArrayIntList> data;
	
	public static IntTable join(IntTable dt1, IntTable dt2)
	{
		int numrows = dt1.dim(0) + dt2.dim(0);
		int numcols = dt1.dim(1);
		
		ArrayList<String> rownames = dt1.getRowNames();
		rownames.addAll(dt2.getRowNames());
		
		IntTable dtout = new IntTable(numrows, numcols, rownames, dt1.getColNames());
		
		for (int i=0;i<dt1.dim(0);i++)
			for (int j=0;j<numcols;j++)
				dtout.set(i, j, dt1.get(i, j));
		
		for (int i=0;i<dt2.dim(0);i++)
			for (int j=0;j<numcols;j++)
				dtout.set(i+dt1.dim(0), j, dt2.get(i, j));
		
		return dtout;
	}
	
	public static IntTable joinAll(List<IntTable> dtlist)
	{
		if (dtlist.size()==0) return null;
		if (dtlist.size()==1) return dtlist.get(0);
		
		IntTable dtout = join(dtlist.get(0), dtlist.get(1));
		
		for (int i=2;i<dtlist.size();i++)
			dtout = join(dtout, dtlist.get(i));
		
		return dtout;
	}
	
	public IntTable()
	{
		Initialize(0,0,false,false);
	}
	
	public IntTable(int rows, int cols)
	{
		Initialize(rows,cols,false,false);
		InitializeRows(rows,cols);
	}
	
	public IntTable(int rows, int cols, double val)
	{
		Initialize(rows,cols,val);
	}
	
	public IntTable(int rows, int cols, boolean arerownames, boolean arecolnames)
	{
		Initialize(rows,cols,arerownames,arecolnames);
	}
	
	public IntTable(int rows, int cols, ArrayList<String> rownames, ArrayList<String> colnames)
	{
		InitializeRows(rows,cols);
		setRowNames(rownames);
		setColNames(colnames);
	}
	
	public IntTable(List<Integer> vals)
	{
		Initialize(1,vals.size(),0);
		
		for (int col=0;col<vals.size();col++)
			data.get(0).set(col,vals.get(col));
	}
	
	public IntTable(int[][] data)
	{
		if (data.length==0) return;
		
		InitializeRows(data.length,data[0].length);
		
		for (int i=0;i<data.length;i++)
			for (int j=0;j<data[0].length;j++)
				set(i,j,data[i][j]);
	}
	
	public IntTable(String file, boolean arerownames, boolean arecolnames)
	{
		Load(file,arerownames,arecolnames,"\t");
	}

	public IntTable(String file, boolean arerownames, boolean arecolnames, String delimiter)
	{
		Load(file,arerownames,arecolnames,delimiter);
	}
	
	public void add(int row, int val)
	{
		data.get(row).add(val);
	}
	
	public void add(int row, double val)
	{
		data.get(row).add((int)val);
	}
	
	public void add(int row, String val)
	{
		data.get(row).add(Double.valueOf(val).intValue());
	}
	
	public int getRowLength(int row)
	{
		return data.get(row).size();
	}
	
	public IntTable abs()
	{
		IntTable out = this.clone();
		
		for (int i=0;i<dim(0);i++)
			for (int j=0;j<dim(1);j++)
				if (out.get(i,j)<0) out.set(i,j, -data.get(i).get(j));
		
		return out;
	}
	
	public IntTable plus(double val)
	{
		IntTable out = this.clone();
		
		for (int i=0;i<dim(0);i++)
			for (int j=0;j<dim(1);j++)
				out.set(i,j, data.get(i).get(j)+val);
		
		return out;
	}
	
	public IntTable plus(IntTable data2)
	{
		if (data2.dim(0)!=dim(0) || data2.dim(1)!=dim(1))
		{
			System.out.println("Data tables must be the same size.");
			System.exit(0);
		}
		
		IntTable out = this.clone();
		
		for (int i=0;i<data.size();i++)
			for (int j=0;j<data.get(0).size();j++)
				out.set(i,j, data.get(i).get(j)+data2.get(i,j));
		
		return out;
	}
	
	public void addCol(DoubleVector dv)
	{
		for (int i=0;i<dim(0);i++)
			this.addtoRow(i, dv.get(i));
	}
	
	public void addCols(IntTable dt)
	{
		boolean needsalignment = addColumns(dt);
		
		if (!needsalignment)
		{
			for (int newc=0;newc<dt.dim(1);newc++)
				for (int r=0;r<dt.dim(0);r++)
					data.get(r).add(dt.get(r, newc));
		}else
		{
			for (int r=0;r<dim(0);r++)
			{
				int ri = dt.getRowNames().indexOf(rownames.get(r));
				
				for (int newc=0;newc<dt.dim(1);newc++)
					data.get(r).add(dt.get(ri, newc));
			}		
		}
	}
	
	//made public from protected
	public void addtoRow(int row, String val)
	{
		data.get(row).add(new Integer(val));
	}
	
	public void addtoRow(int row, double val)
	{
		data.get(row).add((int)val);
	}
	
	public void addtoRow(int row, int val)
	{
		data.get(row).add(val);
	}
	
	public double[][] asdoubleArray()
	{
		double[][] da = new double[dim(0)][dim(1)];
		
		for (int i=0;i<data.size();i++)
			for (int j=0;j<data.get(0).size();j++)
				da[i][j] = get(i,j);
		
		return da;
	}
	
	public int[][] asintArray()
	{
		int[][] da = new int[dim(0)][dim(1)];
		
		for (int i=0;i<data.size();i++)
			for (int j=0;j<data.get(0).size();j++)
				da[i][j] = get(i,j);
		
		return da;
	}
	
	public IntVector getRow(String rowname)
	{
		return getRow(getRowIndex(rowname));
	}
	
	public IntTable getRow(StringVector names)
	{
		return getRow(getRowIs(names));
	}
	
	public IntVector getCol(String colname)
	{
		return getCol(getColIs(colname));
	}
	
	public IntTable getCol(StringVector names)
	{
		return getCol(getColIs(names));
	}
	
	public IntTable clone()
	{
		IntTable copy = new IntTable();
		
		if (this.hasColNames()) copy.setColNames(new ArrayList<String>(this.getColNames()));
		if (this.hasRowNames()) copy.setRowNames(new ArrayList<String>(this.getRowNames()));
		
		int rows = data.size();
		int cols = data.get(0).size();
		
		copy.data = new ArrayList<ArrayIntList>(rows);
		
		for (int row=0;row<rows;row++)
		{
			ArrayIntList temp = new ArrayIntList(cols);
			for (int col=0;col<cols;col++)
				temp.add(get(row,col));
			
			copy.data.add(temp);
		}
		
		return(copy);
	}
	
	public int dim(int dimension)
	{	
		if (dimension==0) return data.size();
		
		if (data.size()>0 && dimension==1) return data.get(0).size();
		
		return -1;
	}
	
	public void Discretize(List<Double> breaks)
	{
		for (int i=0;i<dim(0);i++)
			for (int j=0;j<dim(1);j++)
				for (int b=0;b<breaks.size();b++)
					if (b==0 && get(i,j)<=breaks.get(0))
					{
						set(i,j,0);
						break;
					}else if (b==breaks.size()-1 && get(i,j)>=breaks.get(breaks.size()-1))
					{
						set(i,j,breaks.size());
						break;
					}else if (b!=0 && get(i,j)>=breaks.get(b-1) && get(i,j)<=breaks.get(b))
					{
						set(i,j,b);
						break;
					}
	}
	
	public IntTable divideBy(double val)
	{
		IntTable out = this.clone();
		
		for (int i=0;i<dim(0);i++)
			for (int j=0;j<dim(1);j++)
				out.set(i,j, data.get(i).get(j)/val);
		
		return out;
	}
	
	public int get(int i, int j)
	{
		return(data.get(i).get(j));
	}
	
	public int get(int i, String col)
	{
		return(data.get(i).get(getColNames().indexOf(col)));
	}
	
	public int get(String row, int j)
	{
		return(data.get(getRowNames().indexOf(row)).get(j));
	}
	
	public int get(String row, String col)
	{
		return(data.get(getRowNames().indexOf(row)).get(getColNames().indexOf(col)));
	}
	
	public String getAsString(int row, int col)
	{
		return Integer.toString(data.get(row).get(col));
	}
	
	public IntVector getCol(int col)
	{
		IntVector column = new IntVector(dim(0));
		
		LabelCol(column,col);
		
		for (int r=0;r<dim(0);r++)
			column.add(get(r,col));
		
		return column;
	}
	
	public IntTable getCol(List<?> indexes)
	{
		IntTable cols = new IntTable(dim(0),indexes.size(),this.hasColNames(),this.hasRowNames());
		
		if (this.hasRowNames()) cols.setRowNames(this.getRowNames());
		
		IntVector is = getColIs(indexes);
		
		if (indexes.size()>is.size())
		{
			System.out.println("Error getCol(List<?> indexes): index value does not exist.");
			System.exit(0);
		}
		
		for (int i=0;i<indexes.size();i++)
			cols.setCol(i,getCol(is.get(i)));
		
		return cols;
	}
	
	public IntTable getCol(BooleanVector bv)
	{
		if (bv.size()!=dim(1))
		{
			System.out.println("Error getCols(BooleanVector): Vector must be the same size as number of columns.");
			System.exit(0);
		}
		
		return getCol(bv.asIndexes());
	}
	
	public IntTable getCol(IntVector indexes)
	{
		IntTable cols = new IntTable(dim(0),indexes.size(),rownames!=null,rownames!=null);
		
		if (rownames!=null) cols.setRowNames(rownames);
		
		for (int i=0;i<indexes.size();i++)
			cols.setCol(i,getCol(indexes.get(i)));
		
		return cols;
	}
	
	public IntTable getColDistanceMatrix()
	{
		IntTable cdm = new IntTable(dim(1),dim(1),getColNames(),getColNames());

		for (int c1=0;c1<dim(1);c1++)
			for (int c2=c1+1;c2<dim(1);c2++)
				{
					cdm.set(c1, c2, java.lang.Math.sqrt(getCol(c1).subtract(getCol(c2)).pow(2.0).sum()));
					cdm.set(c2, c1, cdm.get(c1,c2));
				}
		
		return cdm;
	}
	
	public IntVector getRow(int row)
	{
		IntVector arow = new IntVector(dim(1));
		
		for (int c=0;c<dim(1);c++)
			arow.add(get(row,c));
		
		LabelRow(arow,row);
		
		return arow;
	}
	
	public IntTable getRow(List<?> indexes)
	{
		IntTable rows = new IntTable(indexes.size(),dim(1),this.hasRowNames(),this.hasColNames());
		
		if (this.hasColNames()) rows.setColNames(this.getColNames());
		
		IntVector is = this.getRowIs(indexes);
		
		return getRow(is);
	}
	
	public IntTable getRow(IntVector indexes)
	{
		IntTable rows = new IntTable(indexes.size(),dim(1),this.hasRowNames(),this.hasColNames());
		
		if (this.hasColNames()) rows.setColNames(colnames);
		
		for (int i=0;i<indexes.size();i++)
			rows.addRow(getRow(indexes.get(i)));
		
		return rows;
	}
	
	public IntTable getRow(DoubleVector indexes)
	{
		return getRow(indexes.asIntVector());
	}
	
	public IntTable getRow(BooleanVector bv)
	{
		if (bv.size()!=dim(0))
		{
			System.out.println("Error getRows(BooleanVector): Vector must be the same size as number of rows.");
			System.exit(0);
		}
		
		return getRow(bv.asIndexes());
	}
	
	public void Initialize(int numrows, int numcols)
	{
		data = new ArrayList<ArrayIntList>(numrows);
	}
	
	public void Initialize(int numrows, int numcols, double val)
	{
		InitializeRows(numrows,numcols);
		
		for (int row=0;row<numrows;row++)
			for (int col=0;col<numcols;col++)
				data.get(row).add(0);
	}
	
	public void InitializeRows(int numrows, int numcols)
	{
		data = new ArrayList<ArrayIntList>(numrows);
		for (int row=0;row<numrows;row++)
		{
			ArrayIntList newlist = new ArrayIntList(numcols);
			data.add(newlist);
		}
	}
	
	public boolean isNaN()
	{
		for (int i=0;i<dim(0);i++)
			for (int j=0;j<dim(1);j++)
				if (!Double.isNaN(data.get(i).get(j))) return false;
		
		return true;
	}
	
	public IntTable log(double base)
	{
		IntTable out = this.clone();
		
		for (int i=0;i<dim(0);i++)
			for (int j=0;j<dim(1);j++)
				out.set(i,j, java.lang.Math.log(data.get(i).get(j))/java.lang.Math.log(base));
		
		return out;
	}
	
	public double max()
	{
		double mx = data.get(0).get(0);
		
		boolean found = false;
		
		for (int i=0;i<data.size();i++)
			for (int j=0;j<data.get(0).size();j++)
				{
					if (!Double.isNaN(data.get(i).get(j)) && data.get(i).get(j)>mx) mx = data.get(i).get(j);
					found = true;
				}
		
		if (!found) return (Double.NaN);
		else return (mx);
	}
	
	public IntVector maxByRow()
	{
		IntVector maxs = new IntVector(dim(0));
		
		for (int r=0;r<dim(0);r++)
			maxs.set(r, getRow(r).max());
		
		return maxs;
	}
	
	public double mean()
	{
		double sum = 0.0;
		int valcount = 0;
		
		for (int i=0;i<dim(0);i++)
			for (int j=0;j<dim(1);j++)
				if (!Double.isNaN(data.get(i).get(j)))
					{
						sum += data.get(i).get(j);
						valcount++;
					}
		
		if (valcount==0) return Double.NaN;
		
		return sum / (valcount);
	}
	
	public DoubleVector meanByRow()
	{
		DoubleVector stds = new DoubleVector(dim(0));
		
		for (int r=0;r<dim(0);r++)
			stds.set(r, getRow(r).mean());
		
		return stds;
	}
	
	public DoubleVector meanByCol()
	{
		DoubleVector stds = new DoubleVector(dim(1));
		
		for (int c=0;c<dim(1);c++)
			stds.set(c, getCol(c).mean());
		
		return stds;
	}
	
	public void MeanCenterRows()
	{
		for (int i=0;i<data.size();i++)
		{
			double mean = getRow(i).mean();
			
			for (int j=0;j<data.get(i).size();j++)
				set(i,j,data.get(i).get(j)-mean);
		}
	}
	
	public void MeanCenterCols()
	{
		for (int j=0;j<dim(1);j++)
		{
			double mean = getCol(j).mean();
			
			for (int i=0;i<dim(0);i++)
				set(i,j,get(i,j)-mean);
		}
	}

	public double min()
	{
		double mn = data.get(0).get(0);
		
		boolean found = false;
		
		for (int i=0;i<data.size();i++)
			for (int j=0;j<data.get(0).size();j++)
				{
					if (!Double.isNaN(data.get(i).get(j)) && data.get(i).get(j)<mn) mn = data.get(i).get(j);
					found = true;
				}
		
		if (!found) return (Double.NaN);
		else return (mn);
	}
	
	public IntVector minByRow()
	{
		IntVector mins = new IntVector(dim(0));
		
		for (int r=0;r<dim(0);r++)
			mins.set(r, getRow(r).min());
		
		return mins;
	}
	
	public IntTable minus(IntTable dt)
	{
		IntTable diff = this.clone();
		
		if (dt.dim(0)!=diff.dim(0) || dt.dim(1)!=diff.dim(1))
		{
			System.out.println("Data tables must be the same size.");
			System.exit(0);
		}
		
		for (int i=0;i<data.size();i++)
			for (int j=0;j<data.get(0).size();j++)
				diff.set(i,j, diff.get(i,j)-dt.get(i,j));
		
		return diff;
	}
	
	public void Negative()
	{
		for (int i=0;i<dim(0);i++)
			for (int j=0;j<dim(1);j++)
				data.get(i).set(j, -data.get(i).get(j));
	}
	
	public ZStat Normalize()
	{
		ZStat zs = new ZStat(mean(),this.std());
		
		for (int i=0;i<data.size();i++)
			for (int j=0;j<data.get(0).size();j++)
				set(i,j,(get(i,j)-zs.getMean()) / zs.getSD());
		
		return zs;
	}
	
	public void NormalizeByRow()
	{
		for (int i=0;i<data.size();i++)
		{
			IntVector row = getRow(i);
			double mean = row.mean();
			double std = row.std();
			
			for (int j=0;j<data.get(i).size();j++)
				set(i,j,(data.get(i).get(j)-mean) / std);
		}
	}
	
	public void NormalizeByCol()
	{
		for (int c=0;c<dim(1);c++)
		{
			IntVector col = getCol(c);
			double mean =col.mean();
			double std = col.std();
						
			for (int r=0;r<dim(0);r++)
				set(r,c,(get(r,c)-mean) / std);
		}
	}
	
	public IntTable pow(double power)
	{
		IntTable pdt = this.clone();
		
		for (int i=0;i<data.size();i++)
			for (int j=0;j<data.get(0).size();j++)
				pdt.set(i,j, java.lang.Math.pow(get(i,j),power));
		
		return pdt;
	}
	
	protected void removeDataCol(int col)
	{
		if (col<0 || col>dim(1))
		{
			System.out.println("RemoveCol: Column does not exist.");
			System.exit(0);
		}
		
		for (int r=0;r<data.size();r++)
			data.get(r).removeElementAt(col);
	}

	protected void removeDataRow(int row)
	{
		data.remove(row);
	}
	
	public void RemoveNaNRows(int oknans)
	{
		for (int r=0;r<dim(0);r++)
		{
			int nans = 0;
			for (int c=0;c<dim(1);c++)
				if (Double.isNaN(get(r,c)))
				{
					nans++;
					if (nans > oknans) break;
				}
			
			if (nans>oknans)
			{
				this.removeRow(r);
				r--;
			}
		}
	}
	
	public void ReOne(int one)
	{
		for (int i=0;i<data.size();i++)
			for (int j=0;j<data.get(0).size();j++)
				if (data.get(i).get(j)==1) data.get(i).set(j, one);
	}

	public void ReZero(int zero)
	{	
		for (int i=0;i<data.size();i++)
			for (int j=0;j<data.get(0).size();j++)
				if (data.get(i).get(j)==0) data.get(i).set(j, zero);
	}
	
	public void set(int i, int j, int val)
	{
		data.get(i).set(j, val);
	}
	
	public void set(int i, int j, double val)
	{
		data.get(i).set(j, (int)val);
	}
	
	public void set(int row, int col, String val)
	{
		this.set(row,col, Double.valueOf(val).intValue());
	}
	
	public void set(int i, String col, int val)
	{
		data.get(i).set(getColNames().indexOf(col), val);
	}
	
	public void set(int i, String col, double val)
	{
		data.get(i).set(getColNames().indexOf(col), (int)val);
	}
	
	public void set(String row, int j, int val)
	{
		data.get(getRowNames().indexOf(row)).set(j, val);
	}
	
	public void set(String row, int j, double val)
	{
		data.get(getRowNames().indexOf(row)).set(j, (int)val);
	}
	
	public void set(String row, String col, int val)
	{
		data.get(getRowNames().indexOf(row)).set(getColNames().indexOf(col), val);
	}
	
	public void set(String row, String col, double val)
	{
		data.get(getRowNames().indexOf(row)).set(getColNames().indexOf(col), (int)val);
	}
	
	public void setCols(int startindex, IntTable dt)
	{
		//boolean needsalignment = AddColumns(dt);
		
		//if (!needsalignment)
		//{
			for (int newc=0;newc<dt.dim(1);newc++)
				setCol(startindex+newc,dt.getCol(newc));
		/*}else
		{
			for (int r=0;r<dim(0);r++)
			{
				int ri = dt.getrownames().indexOf(rownames.get(r));
				
				if (ri==-1)
					for (int newc=0;newc<dt.dim(1);newc++)
						data.get(r).set(startindex+newc,Double.NaN);
				else
					for (int newc=0;newc<dt.dim(1);newc++)
						data.get(r).set(startindex+newc,dt.get(ri, newc));
			}		
		}*/
	}
	
	public void setRow(int index, IntVector vec)
	{
		if (vec.size()!=dim(1))
		{
			System.err.println("Error setRow(int, DoubleVector): Vector size must equal number of columns.");
			System.exit(0);
		}
		
		if (vec.hasListName() && this.hasRowNames())
			this.setRowName(index, vec.getListName());
		
		for (int c=0;c<vec.size();c++)
			set(index,c,vec.get(c));
	}
	
	public void addRow(IntVector vec)
	{
		if (vec.size()!=dim(1) && dim(0)!=0)
		{
			System.err.println("Error IntTable.addRow(DoubleVector): Vector size must equal number of columns.");
			System.err.println("Vector size: "+vec.size()+", dim(1): "+dim(1));
			System.exit(0);
		}
		
		if (vec.hasListName() && this.hasRowNames())
			this.addRowName(vec.getListName());
		
		ArrayIntList newRow = new ArrayIntList(Math.max(0, dim(1)));
		
		for (int c=0;c<vec.size();c++)
			newRow.add(vec.get(c));
		
		data.add(newRow);
	}
	
	public void setCol(int index, IntVector vec)
	{
		if (vec.size()!=dim(0))
		{
			System.err.println("Error setCol(int, DoubleVector): Vector size must equal number of rows.");
			System.exit(0);
		}
		
		if (vec.listname!=null && this.colnames!=null)
			this.setColName(index, vec.listname);
		
		for (int r=0;r<vec.size();r++)
			set(r,index,vec.get(r));
	}
	
	public int size()
	{
		return dim(0) * dim(1);
	}
	
	public double std()
	{
		double avg = mean();
		
		double sum = 0.0;
		int valcount = 0;
		
		for (int i=0;i<dim(0);i++)
			for (int j=0;j<dim(1);j++)
				if (!Double.isNaN(data.get(i).get(j)))
					{
						sum += java.lang.Math.pow((data.get(i).get(j) - avg),2);
						valcount++;
					}
		
		if (valcount==0) return Double.NaN;
		
		sum = sum / (valcount - 1);
		
		return java.lang.Math.pow(sum,.5);
	}
	
	public DoubleVector stdByRow()
	{
		DoubleVector stds = new DoubleVector(dim(0));
		
		for (int r=0;r<dim(0);r++)
			stds.set(r, getRow(r).std());
		
		return stds;
	}
	
	public IntTable subtract(double val)
	{
		IntTable out = this.clone();
		
		for (int i=0;i<dim(0);i++)
			for (int j=0;j<dim(1);j++)
				out.set(i,j, data.get(i).get(j)-val);
		
		return out;
	}
	
	public IntTable subtract(IntTable data2)
	{
		if (data2.dim(0)!=dim(0) || data2.dim(1)!=dim(1))
		{
			System.out.println("Error subtract(DoubleTable): Data tables must be the same size.");
			System.exit(0);
		}
		
		IntTable out = this.clone();
		
		for (int i=0;i<data.size();i++)
			for (int j=0;j<data.get(0).size();j++)
				out.set(i,j, data.get(i).get(j)-data2.get(i,j));
		
		return out;
	}
	
	public double sum()
	{
		double sum = 0.0;
		int valcount = 0;
		
		for (int i=0;i<dim(0);i++)
			for (int j=0;j<dim(1);j++)
				if (!Double.isNaN(data.get(i).get(j)))
					{
						sum += data.get(i).get(j);
						valcount++;
					}
		
		if (valcount==0) return Double.NaN;
		
		return sum;
	}
	
	public DoubleVector sumByCol()
	{
		DoubleVector out = new DoubleVector(dim(1));
		
		for (int c=0;c<dim(1);c++)
			out.set(c, this.getCol(c).sum());
		
		return out;
	}
	
	public void NRandomize()
	{
		java.util.Random randgen = new java.util.Random();
		
		randgen.setSeed(System.nanoTime());
		
		for (int r=0;r<dim(0);r++)
			for (int c=0;c<dim(1);c++)
				set(r,c,randgen.nextGaussian());
	}
	
	public void Randomize()
	{
		java.util.Random randgen = new java.util.Random();
		
		randgen.setSeed(System.nanoTime());
		
		for (int r=0;r<dim(0);r++)
			for (int c=0;c<dim(1);c++)
				set(r,c,randgen.nextDouble());
	}
	
	protected void TransposeData()
	{
		ArrayList<ArrayIntList> tempdata = new ArrayList<ArrayIntList>(dim(1));
		for (int row=0;row<dim(1);row++)
		{
			ArrayIntList newlist = new ArrayIntList(dim(0));


			for (int col=0;col<dim(0);col++)
				newlist.add(get(col,row));
			
			tempdata.add(newlist);
		}
		
		data = tempdata;
	}
	
	public void SortRows(int keycol)
	{
		IntVector dv = this.getCol(keycol);
		
		IntVector index = dv.sort_I();
		
		if (this.hasRowNames())	this.setRowNames(dv.getElementNames());

		ArrayList<ArrayIntList> mydata = new ArrayList<ArrayIntList>(dim(0));
		for (int row=0;row<dim(0);row++)
		{
			ArrayIntList newlist = new ArrayIntList(dim(1));
			for (int col=0;col<dim(1);col++)
				newlist.add(this.get(index.get(row),col));
			
			mydata.add(newlist);
		}
		
		data = mydata;
	}
	
	public void SortCols(int keyrow)
	{
		IntVector dv = this.getRow(keyrow);
		
		IntVector index = dv.sort_I();
		
		if (this.hasColNames())	this.setColNames(dv.getElementNames());

		ArrayList<ArrayIntList> mydata = new ArrayList<ArrayIntList>(dim(1));
		for (int row=0;row<dim(0);row++)
		{
			ArrayIntList newlist = new ArrayIntList(dim(1));
			for (int col=0;col<dim(1);col++)
				newlist.add(this.get(row,index.get(col)));
			
			mydata.add(newlist);
		}
		
		data = mydata;
	}
	
	public int numRows()
	{
		if (data!=null) return data.size();
		else return 0;
	}
	
	public int numCols()
	{
		if (data!=null && data.size()>0) return data.get(0).size();
		else return 0;
	}
}
