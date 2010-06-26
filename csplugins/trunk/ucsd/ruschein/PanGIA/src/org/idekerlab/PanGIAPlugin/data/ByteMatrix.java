package org.idekerlab.PanGIAPlugin.data;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.idekerlab.PanGIAPlugin.utilities.ByteConverter;


public class ByteMatrix extends DataMatrix{

	private byte[][] data;
	
	public ByteMatrix()
	{
		Initialize(0,0,false,false);
	}
	
	public ByteMatrix(ByteMatrix dt)
	{
		this.data = new byte[dt.dim(0)][dt.dim(1)];
		
		for (int i=0;i<dt.dim(0);i++)
			for (int j=0;j<dt.dim(1);j++)
				data[i][j] = dt.get(i, j);
		
		if (dt.hasColNames()) this.setColNames(new ArrayList<String>(dt.getColNames()));
		if (dt.hasRowNames()) this.setRowNames(new ArrayList<String>(dt.getRowNames()));
	}
	
	public ByteMatrix(int rows, int cols)
	{
		Initialize(rows,cols,false,false);
	}
	
	public ByteMatrix(int rows, int cols, byte val)
	{
		Initialize(rows,cols,val);
	}
	
	public ByteMatrix(int rows, int cols, int val)
	{
		Initialize(rows,cols,val);
	}
	
	public ByteMatrix(int rows, int cols, boolean arerownames, boolean arecolnames)
	{
		Initialize(rows,cols,arerownames,arecolnames);
	}
	
	public ByteMatrix(int rows, int cols, List<String> rownames, List<String> colnames)
	{
		Initialize(rows,cols);
		setRowNames(rownames);
		setColNames(colnames);
	}
	
	public ByteMatrix(int[][] data)
	{
		if (data.length==0) return;
		
		Initialize(data.length,data[0].length);
		
		for (int i=0;i<data.length;i++)
			for (int j=0;j<data[0].length;j++)
				set(i,j,data[i][j]);
	}
	
	public ByteMatrix(byte[][] data)
	{
		if (data.length==0) return;
		
		Initialize(data.length,data[0].length);
		
		for (int i=0;i<data.length;i++)
			for (int j=0;j<data[0].length;j++)
				set(i,j,data[i][j]);
	}
	
	public ByteMatrix(double[][] data)
	{
		if (data.length==0) return;
		
		Initialize(data.length,data[0].length);
		
		for (int i=0;i<data.length;i++)
			for (int j=0;j<data[0].length;j++)
				set(i,j,data[i][j]);
	}
	
	public ByteMatrix(String file, boolean arerownames, boolean arecolnames)
	{
		Load(file,arerownames,arecolnames,"\t");
	}

	public ByteMatrix(String file, boolean arerownames, boolean arecolnames, String delimiter)
	{
		Load(file,arerownames,arecolnames,delimiter);
	}
	
	public ByteMatrix abs()
	{
		ByteMatrix out = this.clone();
		
		for (int i=0;i<dim(0);i++)
			for (int j=0;j<dim(1);j++)
				if (out.get(i,j)<0) out.set(i,j, -this.get(i,j));
		
		return out;
	}
	
	public ByteMatrix plus(byte val)
	{
		ByteMatrix out = this.clone();
		
		for (int i=0;i<dim(0);i++)
			for (int j=0;j<dim(1);j++)
				out.set(i,j, this.get(i, j)+val);
		
		return out;
	}
	
	public ByteMatrix plus(ByteMatrix data2)
	{
		if (data2.dim(0)!=dim(0) || data2.dim(1)!=dim(1))
		{
			System.out.println("Data tables must be the same size.");
			System.exit(0);
		}
		
		ByteMatrix out = this.clone();
		
		for (int i=0;i<this.dim(0);i++)
			for (int j=0;j<this.dim(1);j++)
				out.set(i,j, this.get(i,j)+data2.get(i,j));
		
		return out;
	}
	
	public byte[][] getData()
	{
		return data;
	}
	
	public byte[][] asbyteArray()
	{
		byte[][] da = new byte[dim(0)][dim(1)];
		
		for (int i=0;i<dim(0);i++)
			for (int j=0;j<dim(1);j++)
				da[i][j] = get(i,j);
		
		return da;
	}
	
	public double[][] asdoubleArray()
	{
		double[][] da = new double[dim(0)][dim(1)];
		
		for (int i=0;i<dim(0);i++)
			for (int j=0;j<dim(1);j++)
				da[i][j] = get(i,j);
		
		return da;
	}
	
	public int[][] asintArray()
	{
		int[][] da = new int[dim(0)][dim(1)];
		
		for (int i=0;i<dim(0);i++)
			for (int j=0;j<dim(1);j++)
				da[i][j] = (int)get(i,j);
		
		return da;
	}
	
	public ByteVector getRow(String rowname)
	{
		return getRow(getRowIndex(rowname));
	}
	
	public ByteMatrix getRow(StringVector names)
	{
		return getRow(getRowIs(names));
	}
	
	public ByteVector getCol(String colname)
	{
		return getCol(getColIs(colname));
	}
	
	public ByteMatrix getCol(StringVector names)
	{
		return getCol(getColIs(names));
	}
	
	public static byte[][] joinCols(byte[][] dm1, byte[][] dm2)
	{
		byte[][] out = new byte[dm1.length][dm1[0].length+dm2[0].length];
		
		for (int i=0;i<dm1.length;i++)
			for (int j=0;j<dm1[0].length;j++)
				out[i][j] = dm1[i][j];
		
		for (int i=0;i<dm2.length;i++)
			for (int j=0;j<dm2[0].length;j++)
				out[i][j+dm1[0].length] = dm2[i][j];
		
		return out;
	}
	
	public ByteMatrix clone()
	{
		return new ByteMatrix(this);
	}
	
	public int dim(int dimension)
	{	
		if (dimension==0) return data.length;
		
		if (data.length>0 && dimension==1) return data[0].length;
		
		return -1;
	}
	
	public ByteMatrix divideBy(double val)
	{
		ByteMatrix out = this.clone();
		
		for (int i=0;i<dim(0);i++)
			for (int j=0;j<dim(1);j++)
				out.set(i,j, get(i,j)/val);
		
		return out;
	}
	
	public ByteMatrix divideBy(ByteMatrix dt)
	{
		ByteMatrix out = this.clone();
		
		for (int i=0;i<dim(0);i++)
			for (int j=0;j<dim(1);j++)
				out.set(i,j, get(i,j)/dt.get(i, j));
		
		return out;
	}
	
	public byte get(int i, int j)
	{
		return(data[i][j]);
	}
	
	public double getAsDouble(int i, int j)
	{
		return(data[i][j]);
	}
	
	public float getAsFloat(int i, int j)
	{
		return(data[i][j]);
	}
	
	public byte get(int i, String col)
	{
		return(get(i,this.colnames.indexOf(col)));
	}
	
	public byte get(String row, int j)
	{
		return(get(this.colnames.indexOf(row),j));
	}
	
	public byte get(String row, String col)
	{
		return(get(this.colnames.indexOf(row),this.colnames.indexOf(col)));
	}
	
	public String getAsString(int row, int col)
	{
		return Byte.toString(get(row,col));
	}
	
	public ByteVector getCol(int col)
	{
		byte[] cola = new byte[data.length];
		
		for (int r=0;r<data.length;r++)
			cola[r] = data[r][col];
		
		ByteVector column = new ByteVector(cola);
		
		LabelCol(column,col);
		
		return column;
	}
	
	public ByteMatrix getCol(int col0, int col1)
	{
		int numCols = col1-col0+1;
		ByteMatrix cols = new ByteMatrix(this.numRows(),numCols,this.hasColNames(),this.hasRowNames());
		
		if (this.hasRowNames()) cols.setRowNames(this.getRowNames());
		if (this.hasColNames()) cols.setColNames(this.colnames.subList(col0, col1));
		
		for (int r=0;r<this.numRows();r++)
			for (int c=col0;c<col1;c++)
				cols.set(r,c-col0,this.get(r,c));
		
		return cols;
	}
	
	public ByteMatrix getCol(List<?> indexes)
	{
		ByteMatrix cols = new ByteMatrix(dim(0),indexes.size(),this.hasColNames(),this.hasRowNames());
		
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
	
	public ByteMatrix getCol(BooleanVector bv)
	{
		if (bv.size()!=this.numCols()) throw new java.lang.IllegalArgumentException("Vector must be the same size as number of columns. vec="+bv.size()+", cols="+this.numCols());
		
		return getCol(bv.asIndexes());
	}
	
	public ByteMatrix getCol(IntVector indexes)
	{
		ByteMatrix cols = new ByteMatrix(this.numRows(),indexes.size(),this.hasRowNames(),this.hasColNames());
		
		if (this.hasRowNames()) cols.setRowNames(rownames);
		
		for (int i=0;i<indexes.size();i++)
			cols.setCol(i,getCol(indexes.get(i)));
		
		return cols;
	}
	
	public ByteMatrix getCol(Set<Integer> indexes)
	{
		ByteMatrix cols = new ByteMatrix(this.numRows(),indexes.size(),this.hasRowNames(),this.hasColNames());
		
		if (this.hasRowNames()) cols.setRowNames(rownames);
		
		int j=0;
		for (int i=0;i<this.numCols();i++)
			if (indexes.contains(i))
			{
				cols.setCol(j,getCol(i));
				j++;
			}
		
		return cols;
	}
	
	public ByteMatrix getCol(int[] indexes)
	{
		ByteMatrix cols = new ByteMatrix(this.numRows(),indexes.length,this.hasRowNames(),this.hasColNames());
		
		if (this.hasRowNames()) cols.setRowNames(rownames);
		
		for (int i=0;i<indexes.length;i++)
			cols.setCol(i,getCol(indexes[i]));
		
		return cols;
	}
	
	public ByteMatrix getColDistanceMatrix()
	{
		ByteMatrix cdm = new ByteMatrix(dim(1),dim(1),getColNames(),getColNames());

		for (int c1=0;c1<dim(1);c1++)
			for (int c2=c1+1;c2<dim(1);c2++)
				{
					cdm.set(c1, c2, java.lang.Math.sqrt(getCol(c1).subtract(getCol(c2)).pow(2.0).sum()));
					cdm.set(c2, c1, cdm.get(c1,c2));
				}
		
		return cdm;
	}
	
	public ByteVector getRow(int row)
	{
		ByteVector arow = new ByteVector(dim(1));
		
		for (int c=0;c<dim(1);c++)
			arow.add(get(row,c));
		
		LabelRow(arow,row);
		
		return arow;
	}
	
	public ByteMatrix getRow(List<?> indexes)
	{
		ByteMatrix rows = new ByteMatrix(indexes.size(),dim(1),this.hasRowNames(),this.hasColNames());
		
		if (this.hasColNames()) rows.setColNames(this.getColNames());
		
		IntVector is = this.getRowIs(indexes);
		
		return getRow(is);
	}
	
	public ByteMatrix getRow(int[] indexes)
	{
		ByteMatrix rows = new ByteMatrix(indexes.length,dim(1));
		
		for (int i=0;i<indexes.length;i++)
			rows.setRow(i,getRow(indexes[i]));
		
		if (this.hasColNames()) rows.setColNames(colnames);
		
		if (this.hasRowNames())
		{
			List<String> newRowNames = new ArrayList<String>(indexes.length);
			for (int i=0;i<indexes.length;i++)
				newRowNames.add(this.getRowName(indexes[i]));
			
			rows.setRowNames(newRowNames);
		}
		
		return rows;
	}
	
	public ByteMatrix getRow(IntVector indexes)
	{
		ByteMatrix rows = new ByteMatrix(indexes.size(),dim(1));
		
		for (int i=0;i<indexes.size();i++)
			rows.setRow(i,getRow(indexes.get(i)));
		
		if (this.hasColNames()) rows.setColNames(colnames);
		
		if (this.hasRowNames())
		{
			List<String> newRowNames = new ArrayList<String>(indexes.size());
			for (int i=0;i<indexes.size();i++)
				newRowNames.add(this.getRowName(indexes.get(i)));
			
			rows.setRowNames(newRowNames);
		}
		
		return rows;
	}
	
	public ByteMatrix getRow(DoubleVector indexes)
	{
		return getRow(indexes.asIntVector());
	}
	
	public ByteMatrix getRow(BooleanVector bv)
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
		data = new byte[numrows][numcols];
	}
	
	public void Initialize(int numrows, int numcols, double val)
	{
		Initialize(numrows,numcols);
		
		for (int row=0;row<numrows;row++)
			for (int col=0;col<numcols;col++)
				set(row,col,val);
	}
	
	public byte max()
	{
		byte mx = get(0,0);
		
		for (int i=0;i<dim(0);i++)
			for (int j=0;j<dim(1);j++)
				if (get(i,j)>mx) mx = get(i,j);
					
		return (mx);
	}
	
	public DoubleVector maxByRow()
	{
		DoubleVector maxs = new DoubleVector(dim(0));
		
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
				if (!Double.isNaN(get(i,j)))
					{
						sum += get(i,j);
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
	
	public double min()
	{
		double mn = get(0,0);
		
		for (int i=0;i<dim(0);i++)
			for (int j=0;j<dim(1);j++)
				if (get(i,j)<mn) mn = get(i,j);
				
		return (mn);
	}
	
	public DoubleVector minByRow()
	{
		DoubleVector mins = new DoubleVector(dim(0));
		
		for (int r=0;r<dim(0);r++)
			mins.set(r, getRow(r).min());
		
		return mins;
	}
	
	public ByteMatrix minus(ByteMatrix dt)
	{
		ByteMatrix diff = this.clone();
		
		if (dt.dim(0)!=diff.dim(0) || dt.dim(1)!=diff.dim(1))
		{
			System.out.println("Data tables must be the same size.");
			System.exit(0);
		}
		
		for (int i=0;i<dim(0);i++)
			for (int j=0;j<dim(1);j++)
				diff.set(i,j, diff.get(i,j)-dt.get(i,j));
		
		return diff;
	}
	
	public void Negative()
	{
		for (int i=0;i<dim(0);i++)
			for (int j=0;j<dim(1);j++)
				set(i,j, -get(i,j));
	}
	
	public void set(int i, int j, byte val)
	{
		data[i][j] = val;
	}
	
	public void set(int i, int j, double val)
	{
		data[i][j] = new Double(val).byteValue();
	}
	
	public void set(int i, int j, int val)
	{
		data[i][j] = new Integer(val).byteValue();
	}
	
	public void set(int i, int j, float val)
	{
		data[i][j] = new Float(val).byteValue();
	}
	
	public void set(int row, int col, String val)
	{
		this.set(row,col, Byte.valueOf(val));
	}
	
	public void set(int i, String col, byte val)
	{
		set(i, this.colnames.indexOf(col), val);
	}
	
	public void set(int i, String col, Integer val)
	{
		set(i, this.colnames.indexOf(col), val.doubleValue());
	}
	
	public void set(String row, int j, byte val)
	{
		set(this.rownames.indexOf(row), j, val);
	}
	
	public void set(String row, int j, Integer val)
	{
		set(this.rownames.indexOf(row), j, val.doubleValue());
	}
	
	public void set(String row, String col, byte val)
	{
		set(this.rownames.indexOf(row), this.colnames.indexOf(col), val);
	}
	
	public void set(String row, String col, Integer val)
	{
		set(this.rownames.indexOf(row), this.colnames.indexOf(col), val.doubleValue());
	}
	
	public void setCols(int startindex, ByteMatrix dt)
	{
		//boolean needsalignment = AddColumns(dt);
		
		//if (!needsalignment)
		//{
			for (int newc=0;newc<dt.numCols();newc++)
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
	
	public void setRow(int index, ByteVector vec)
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
	
	public void setCol(int index, ByteVector vec)
	{
		if (vec.size()!=dim(0))
		{
			System.err.println("Error setCol(int, DoubleVector): Vector size must equal number of rows.");
			System.err.println("Vecsize = "+vec.size()+", Numrows = "+this.numRows());
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
			{
				sum += java.lang.Math.pow((get(i,j) - avg),2);
				valcount++;
			}
		
		if (valcount==0) return Double.NaN;
		
		sum = sum / (valcount - 1);
		
		return sum;
	}
	
	public DoubleVector stdByRow()
	{
		DoubleVector stds = new DoubleVector(dim(0));
		
		for (int r=0;r<dim(0);r++)
			stds.set(r, getRow(r).std());
		
		return stds;
	}
	
	public ByteMatrix subtract(byte val)
	{
		ByteMatrix out = this.clone();
		
		for (int i=0;i<dim(0);i++)
			for (int j=0;j<dim(1);j++)
				out.set(i,j, get(i,j)-val);
		
		return out;
	}
	
	public ByteMatrix subtract(ByteMatrix data2)
	{
		if (data2.dim(0)!=dim(0) || data2.dim(1)!=dim(1))
		{
			System.out.println("Error subtract(DoubleTable): Data tables must be the same size.");
			System.exit(0);
		}
		
		ByteMatrix out = this.clone();
		
		for (int i=0;i<dim(0);i++)
			for (int j=0;j<dim(1);j++)
				out.set(i,j, get(i,j)-data2.get(i,j));
		
		return out;
	}
	
	public double sum()
	{
		double sum = 0.0;
		int valcount = 0;
		
		for (int i=0;i<dim(0);i++)
			for (int j=0;j<dim(1);j++)
			{
				sum += get(i,j);
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
	
	public void SortRows(int keycol)
	{
		if (this.dim(0)<=1) return;
		
		ByteVector dv = this.getCol(keycol);
		
		IntVector index = dv.sort_I();
		
		if (this.hasRowNames())
		{
			List<String> newRowNames = new ArrayList<String>(dv.size());
			for (int row=0;row<dim(0);row++)
				newRowNames.add(this.getRowName(index.get(row)));
			
			this.setRowNames(newRowNames);
		}
		
		byte[][] mydata = new byte[dim(0)][dim(1)];
		for (int row=0;row<dim(0);row++)
			for (int col=0;col<dim(1);col++)
				mydata[row][col] = this.get(index.get(row),col);
					
		data = mydata;
	}
	
	public void SortCols(int keyrow)
	{
		ByteVector dv = this.getRow(keyrow);
		
		IntVector index = dv.sort_I();
		
		if (this.hasColNames())	this.setColNames(dv.getElementNames());

		byte[][] mydata = new byte[dim(0)][dim(1)];
		for (int row=0;row<dim(0);row++)
			for (int col=0;col<dim(1);col++)
				mydata[row][col] = this.get(index.get(row),col);
		
		data = mydata;
	}
	
	public ByteMatrix xTx()
	{
		ByteMatrix out = new ByteMatrix(this.dim(1),this.dim(1));
		
		for (int i=0;i<this.dim(1);i++)
			for (int j=0;j<this.dim(1);j++)
			{
				double sum = 0;
				for (int k=0;k<this.dim(0);k++)
					sum+=this.get(k, i)*this.get(k, j);
				
				out.set(i, j, sum);
			}
		
		return out;
	}
	
	public DoubleVector xTy(DoubleVector y)
	{
		DoubleVector out = new DoubleVector(y.size());
		
		for (int i=0;i<this.numCols();i++)
		{
			double sum = 0;
			for (int j=0;j<y.size();j++)
				sum += this.get(j, i)*y.get(j);
			
			out.add(sum);
		}
		
		return out;
	}
	
	public int numRows()
	{
		return data.length;
	}
	
	public int numCols()
	{
		return data[0].length;
	}
	
	public ByteMatrix transpose()
	{
		ByteMatrix out = new ByteMatrix(this.numCols(),this.numRows());
		
		for (int i=0;i<this.numRows();i++)
			for (int j=0;j<this.numCols();j++)
				out.set(j, i, this.get(i, j));
		
		if (this.hasColNames()) out.setRowNames(this.getColNames());
		if (this.hasRowNames()) out.setColNames(this.getRowNames());
		
		return out;
	}
	
	public DoubleVector times(DoubleVector v)
	{
		DoubleVector out = new DoubleVector(v.size());
		
		for (int i=0;i<v.size();i++)
		{
			double sum = 0;
			
			for (int j=0;j<this.numCols();j++)
				sum+=v.get(j)*this.get(i, j);
			
			out.add(sum);
		}
		
		return out;
	}
	
	public void shuffleRows()
	{
		IntVector perm = IntVector.getScale(0, this.numRows()-1, 1).permutation();
		
		for (int i=0;i<this.numRows();i++)
			for (int j=0;j<this.numCols();j++)
				this.set(i, j, this.get(perm.get(i), j));
	}

	public static ByteMatrix joinRows(ByteMatrix dm1, ByteMatrix dm2)
	{
		ByteMatrix out = new ByteMatrix(dm1.numRows()+dm2.numRows(),dm1.numCols());
		
		for (int i=0;i<dm1.numRows();i++)
			for (int j=0;j<dm1.numCols();j++)
				out.set(i, j, dm1.get(i, j));
		
		for (int i=0;i<dm2.numRows();i++)
			for (int j=0;j<dm2.numCols();j++)
				out.set(i+dm1.numRows(), j, dm2.get(i, j));
		
		if (dm1.hasColNames()) out.setColNames(dm1.getColNames());
		if (dm1.hasRowNames() && dm2.hasRowNames())
		{
			List<String> newNames = dm1.getRowNames();
			newNames.addAll(dm2.getRowNames());
			out.setRowNames(newNames);
		}
		
		return out;
	}
	
	public static ByteMatrix joinRows(List<ByteMatrix> dms)
	{
		int totalRows = 0;
		for (ByteMatrix dm : dms)
			totalRows+=dm.numRows();
		
		byte[][] outm = new byte[totalRows][dms.get(0).numCols()];
				
		int curRow = 0;
		for (int dmi=0;dmi<dms.size();dmi++)
			for (int i=0;i<dms.get(dmi).numRows();i++)
			{
				for (int j=0;j<dms.get(0).numCols();j++)
					outm[curRow][j] = dms.get(dmi).get(i, j);
				curRow++;
			}
		
		ByteMatrix out = new ByteMatrix(outm);
		
		if (dms.get(0).hasColNames()) out.setColNames(dms.get(0).getColNames());
		
		for (ByteMatrix dm : dms)
			if (!dm.hasRowNames()) return out;
		
		List<String> newNames = new ArrayList<String>(totalRows);
		
		for (ByteMatrix dm : dms)
			newNames.addAll(dm.getRowNames());
		
		out.setRowNames(newNames);
		
		return out;
	}
	
	/***
	 * Loads a doublematrix from a byte-file encoded in three states (0,1,2).
	 */
	public static ByteMatrix loadFromByte(String file)
	{
		
		ByteMatrix out = null;
		try
		{
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
			
			byte[] numRowsByte = new byte[4];
			byte[] numColsByte = new byte[4];
			bis.read(numRowsByte, 0, 4);
			bis.read(numColsByte, 0, 4);
			
			int numRows = ByteConverter.convertToInteger(numRowsByte);
			int numCols = ByteConverter.convertToInteger(numColsByte);
			
			out = new ByteMatrix(numRows, numCols);
			
			int i=0;
			int j=0;
			int length = 5;
			
			int b = bis.read();
			while (b!=-1)
			{
				if (b==255)
				{
					i++;
					j=0;
					length = 5;
				}else if (b>242)
				{
					length = b-242;
				}else
				{
					for (int s : ByteConverter.convertDataByteToInt(b,3,length))
					{
						out.set(i, j, s);
						j++;
					}
				}
				
				b = bis.read();
			}
			
			
			bis.close();
		}catch (Exception e) {
			System.err.println("Error DoubleMatrix.loadFromByte(String,int,int): "+e.getMessage());e.printStackTrace();System.exit(1);}
		
		return out;
	}
	
	/***
	 * Loads a doublematrix from a byte-file encoded in three states (0,1,2).
	 */
	public static byte[][] loadColsFromByte(String file, int[] cols)
	{
		byte[][] out = null;
		
		try
		{
			BufferedInputStream bos = new BufferedInputStream(new FileInputStream(file));
			
			byte[] f4 = new byte[4];
			bos.read(f4);
			int numRows = ByteConverter.convertToInteger(f4);
			bos.read(f4);
			int numCols = ByteConverter.convertToInteger(f4);
			
			out = new byte[numRows][cols.length];
			
			int i=0;
			int j=0;
			int length = 5;
			byte[] row = new byte[numCols];
			
			int b = bos.read();
			while (b!=-1)
			{
				if (b==255)
				{
					for (int k=0;k<cols.length;k++)
						out[i][k] = row[cols[k]];
					
					i++;
					j=0;
					length = 5;
				}else if (b>242)
				{
					length = b-242;
				}else
				{
					for (int s : ByteConverter.convertDataByteToInt(b,3,length))
					{
						row[j] = (byte)s;
						j++;
					}
				}
				
				b = bos.read();
			}
			
			bos.close();
			
					
		}catch (Exception e) {System.out.println(e.getMessage()); e.printStackTrace();}
		
		return out;
		
	}
	
	/***
	 * Saves a doublematrix as a 3-state byte-file (0,1,2).
	 */
	public void saveAsByte(String file)
	{
		this.saveAsByte(file, false, this.numRows(), this.numCols());
	}
	
	/***
	 * Saves a doublematrix as a 3-state byte-file (0,1,2).
	 */
	public void saveAsByte(String file, boolean append, int totalRows, int totalCols)
	{
		try
		{
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file,append));
			
			if (!append)
			{
				bos.write(ByteConverter.convertToByte(totalRows,4));
				bos.write(ByteConverter.convertToByte(totalCols,4));
			}
						
			for (int i=0;i<this.numRows();i++)
			{
				for (int j=0;j<this.numCols();j+=5)
				{
					if (j>=this.numCols()-4)
					{
						int newLen = this.numCols()-j;
						bos.write(242+newLen);
						bos.write(ByteConverter.convertDataTextToByte(this,i,j,3,newLen)+128);
						
					}else bos.write(ByteConverter.convertDataTextToByte(this,i,j,3,5)+128);
				}
				
				bos.write(255);
			}
				
				
									
			bos.close();
		}catch (Exception e) {
			System.err.println("Error DoubleMatrix.saveAsByte(String,boolean): "+e.getMessage());e.printStackTrace();}
	}
	
	public int getAsInteger(int i, int j)
	{
		return((int)data[i][j]);
	}
	
	public ByteMatrix xxT()
	{
		ByteMatrix out = new ByteMatrix(this.numRows(),this.numRows());
		
		for (int i=0;i<this.numRows();i++)
		{
			//if (i%100==0) System.out.println(i+"/"+this.numRows());
			for (int j=0;j<this.numRows();j++)
			{
				double sum = 0;
				for (int k=0;k<this.numCols();k++)
					sum+=this.get(j, k)*this.get(i, k);
				
				out.set(i, j, sum);
			}
		}
		
		return out;
	}
	
	public static void fill(byte[][] m, byte val)
	{
		for (int i=0;i<m.length;i++)
			for (int j=0;j<m[i].length;j++)
				m[i][j] = val;
	}
}


