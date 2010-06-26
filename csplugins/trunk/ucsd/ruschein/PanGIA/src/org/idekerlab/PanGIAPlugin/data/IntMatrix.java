package org.idekerlab.PanGIAPlugin.data;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.idekerlab.PanGIAPlugin.utilities.ByteConverter;


public class IntMatrix extends DataMatrix{

	private int[][] data;
	
	public IntMatrix()
	{
		Initialize(0,0,false,false);
	}
	
	public IntMatrix(IntMatrix dt)
	{
		this.data = new int[dt.dim(0)][dt.dim(1)];
		
		for (int i=0;i<dt.dim(0);i++)
			for (int j=0;j<dt.dim(1);j++)
				data[i][j] = dt.get(i, j);
		
		if (dt.hasColNames()) this.setColNames(new ArrayList<String>(dt.getColNames()));
		if (dt.hasRowNames()) this.setRowNames(new ArrayList<String>(dt.getRowNames()));
	}
	
	public IntMatrix(int rows, int cols)
	{
		Initialize(rows,cols,false,false);
	}
	
	public IntMatrix(int rows, int cols, byte val)
	{
		Initialize(rows,cols,val);
	}
	
	public IntMatrix(int rows, int cols, int val)
	{
		Initialize(rows,cols,val);
	}
	
	public IntMatrix(int rows, int cols, boolean arerownames, boolean arecolnames)
	{
		Initialize(rows,cols,arerownames,arecolnames);
	}
	
	public IntMatrix(int rows, int cols, ArrayList<String> rownames, ArrayList<String> colnames)
	{
		Initialize(rows,cols);
		setRowNames(rownames);
		setColNames(colnames);
	}
	
	public IntMatrix(int[][] data)
	{
		this.data = data;
	}
	
	public IntMatrix(byte[][] data)
	{
		if (data.length==0) return;
		
		Initialize(data.length,data[0].length);
		
		for (int i=0;i<data.length;i++)
			for (int j=0;j<data[0].length;j++)
				set(i,j,data[i][j]);
	}
	
	public IntMatrix(double[][] data)
	{
		if (data.length==0) return;
		
		Initialize(data.length,data[0].length);
		
		for (int i=0;i<data.length;i++)
			for (int j=0;j<data[0].length;j++)
				set(i,j,data[i][j]);
	}
	
	public IntMatrix(String file, boolean arerownames, boolean arecolnames)
	{
		Load(file,arerownames,arecolnames,"\t");
	}

	public IntMatrix(String file, boolean arerownames, boolean arecolnames, String delimiter)
	{
		Load(file,arerownames,arecolnames,delimiter);
	}
	
	public IntMatrix abs()
	{
		IntMatrix out = this.clone();
		
		for (int i=0;i<dim(0);i++)
			for (int j=0;j<dim(1);j++)
				if (out.get(i,j)<0) out.set(i,j, -this.get(i,j));
		
		return out;
	}
	
	public IntMatrix plus(int val)
	{
		IntMatrix out = this.clone();
		
		for (int i=0;i<dim(0);i++)
			for (int j=0;j<dim(1);j++)
				out.set(i,j, this.get(i, j)+val);
		
		return out;
	}
	
	public IntMatrix plus(IntMatrix data2)
	{
		if (data2.dim(0)!=dim(0) || data2.dim(1)!=dim(1))
		{
			System.out.println("Data tables must be the same size.");
			System.exit(0);
		}
		
		IntMatrix out = this.clone();
		
		for (int i=0;i<this.dim(0);i++)
			for (int j=0;j<this.dim(1);j++)
				out.set(i,j, this.get(i,j)+data2.get(i,j));
		
		return out;
	}
	
	public byte[][] asbyteArray()
	{
		byte[][] da = new byte[dim(0)][dim(1)];
		
		for (int i=0;i<dim(0);i++)
			for (int j=0;j<dim(1);j++)
				da[i][j] = (byte)get(i,j);
		
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
				da[i][j] = get(i,j);
		
		return da;
	}
	
	public int[][] getData()
	{
		return data;
	}
	
	public IntVector getRow(String rowname)
	{
		return getRow(getRowIndex(rowname));
	}
	
	public IntMatrix getRow(StringVector names)
	{
		return getRow(getRowIs(names));
	}
	
	public IntVector getCol(String colname)
	{
		return getCol(getColIs(colname));
	}
	
	public IntMatrix getCol(StringVector names)
	{
		return getCol(getColIs(names));
	}
	
	public IntMatrix clone()
	{
		return new IntMatrix(this);
	}
	
	public int dim(int dimension)
	{	
		if (dimension==0) return data.length;
		
		if (data.length>0 && dimension==1) return data[0].length;
		
		return -1;
	}
	
	public IntMatrix divideBy(double val)
	{
		IntMatrix out = this.clone();
		
		for (int i=0;i<dim(0);i++)
			for (int j=0;j<dim(1);j++)
				out.set(i,j, get(i,j)/val);
		
		return out;
	}
	
	public IntMatrix divideBy(IntMatrix dt)
	{
		IntMatrix out = this.clone();
		
		for (int i=0;i<dim(0);i++)
			for (int j=0;j<dim(1);j++)
				out.set(i,j, get(i,j)/dt.get(i, j));
		
		return out;
	}
	
	public int get(int i, int j)
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
	
	public int get(int i, String col)
	{
		return(get(i,this.colnames.indexOf(col)));
	}
	
	public int get(String row, int j)
	{
		return(get(this.colnames.indexOf(row),j));
	}
	
	public int get(String row, String col)
	{
		return(get(this.colnames.indexOf(row),this.colnames.indexOf(col)));
	}
	
	public String getAsString(int row, int col)
	{
		return Integer.toString(get(row,col));
	}
	
	public IntVector getCol(int col)
	{
		IntVector column = new IntVector(dim(0));
		
		LabelCol(column,col);
		
		for (int r=0;r<this.numRows();r++)
			column.add(get(r,col));
		
		return column;
	}
	
	public IntMatrix getCol(int col0, int col1)
	{
		int numCols = col1-col0+1;
		IntMatrix cols = new IntMatrix(this.numRows(),numCols,this.hasColNames(),this.hasRowNames());
		
		if (this.hasRowNames()) cols.setRowNames(this.getRowNames());
		if (this.hasColNames()) cols.setColNames(this.colnames.subList(col0, col1));
		
		for (int r=0;r<this.numRows();r++)
			for (int c=col0;c<col1;c++)
				cols.set(r,c-col0,this.get(r,c));
		
		return cols;
	}
	
	public IntMatrix getCol(List<?> indexes)
	{
		IntMatrix cols = new IntMatrix(dim(0),indexes.size(),this.hasColNames(),this.hasRowNames());
		
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
	
	public IntMatrix getCol(BooleanVector bv)
	{
		if (bv.size()!=dim(1))
		{
			System.out.println("Error getCols(BooleanVector): Vector must be the same size as number of columns.");
			System.exit(0);
		}
		
		return getCol(bv.asIndexes());
	}
	
	public IntMatrix getCol(IntVector indexes)
	{
		IntMatrix cols = new IntMatrix(dim(0),indexes.size(),rownames!=null,rownames!=null);
		
		if (rownames!=null) cols.setRowNames(rownames);
		
		for (int i=0;i<indexes.size();i++)
			cols.setCol(i,getCol(indexes.get(i)));
		
		return cols;
	}
	
	public IntMatrix getCol(int[] indexes)
	{
		IntMatrix cols = new IntMatrix(dim(0),indexes.length,rownames!=null,rownames!=null);
		
		if (rownames!=null) cols.setRowNames(rownames);
		
		for (int i=0;i<indexes.length;i++)
			cols.setCol(i,getCol(indexes[i]));
		
		return cols;
	}
	
	public IntMatrix getColDistanceMatrix()
	{
		IntMatrix cdm = new IntMatrix(dim(1),dim(1),getColNames(),getColNames());

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
	
	public IntMatrix getRow(List<?> indexes)
	{
		IntMatrix rows = new IntMatrix(indexes.size(),dim(1),this.hasRowNames(),this.hasColNames());
		
		if (this.hasColNames()) rows.setColNames(this.getColNames());
		
		IntVector is = this.getRowIs(indexes);
		
		return getRow(is);
	}
	
	public IntMatrix getRow(int[] indexes)
	{
		IntMatrix rows = new IntMatrix(indexes.length,dim(1));
		
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
	
	public IntMatrix getRow(IntVector indexes)
	{
		IntMatrix rows = new IntMatrix(indexes.size(),dim(1));
		
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
	
	public IntMatrix getRow(DoubleVector indexes)
	{
		return getRow(indexes.asIntVector());
	}
	
	public IntMatrix getRow(BooleanVector bv)
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
		data = new int[numrows][numcols];
	}
	
	public void Initialize(int numrows, int numcols, double val)
	{
		Initialize(numrows,numcols);
		
		for (int row=0;row<numrows;row++)
			for (int col=0;col<numcols;col++)
				set(row,col,val);
	}
	
	public int max()
	{
		int mx = get(0,0);
		
		for (int i=0;i<dim(0);i++)
			for (int j=0;j<dim(1);j++)
				if (get(i,j)>mx) mx = get(i,j);
					
		return (mx);
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
	
	public int min()
	{
		int mn = get(0,0);
		
		for (int i=0;i<dim(0);i++)
			for (int j=0;j<dim(1);j++)
				if (get(i,j)<mn) mn = get(i,j);
				
		return (mn);
	}
	
	public IntVector minByRow()
	{
		IntVector mins = new IntVector(dim(0));
		
		for (int r=0;r<dim(0);r++)
			mins.set(r, getRow(r).min());
		
		return mins;
	}
	
	public IntMatrix minus(IntMatrix dt)
	{
		IntMatrix diff = this.clone();
		
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
		data[i][j] = (int)val;
	}
	
	public void set(int i, int j, float val)
	{
		data[i][j] = (int)val;
	}
	
	public void set(int i, int j, int val)
	{
		data[i][j] = val;
	}
	
	public void set(int row, int col, String val)
	{
		this.set(row,col, Integer.valueOf(val));
	}
	
	public void set(int i, String col, byte val)
	{
		set(i, this.colnames.indexOf(col), val);
	}
	
	public void set(int i, String col, int val)
	{
		set(i, this.colnames.indexOf(col), val);
	}
	
	public void set(String row, int j, byte val)
	{
		set(this.rownames.indexOf(row), j, val);
	}
	
	public void set(String row, int j, int val)
	{
		set(this.rownames.indexOf(row), j, val);
	}
	
	public void set(String row, String col, byte val)
	{
		set(this.rownames.indexOf(row), this.colnames.indexOf(col), val);
	}
	
	public void set(String row, String col, int val)
	{
		set(this.rownames.indexOf(row), this.colnames.indexOf(col), val);
	}
	
	public void setCols(int startindex, IntMatrix dt)
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
	
	public void setCol(int index, IntVector vec)
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
	
	public IntMatrix subtract(byte val)
	{
		IntMatrix out = this.clone();
		
		for (int i=0;i<dim(0);i++)
			for (int j=0;j<dim(1);j++)
				out.set(i,j, get(i,j)-val);
		
		return out;
	}
	
	public IntMatrix subtract(IntMatrix data2)
	{
		if (data2.dim(0)!=dim(0) || data2.dim(1)!=dim(1))
		{
			System.out.println("Error subtract(DoubleTable): Data tables must be the same size.");
			System.exit(0);
		}
		
		IntMatrix out = this.clone();
		
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
		
		IntVector dv = this.getCol(keycol);
		
		IntVector index = dv.sort_I();
		
		if (this.hasRowNames())
		{
			List<String> newRowNames = new ArrayList<String>(dv.size());
			for (int row=0;row<dim(0);row++)
				newRowNames.add(this.getRowName(index.get(row)));
			
			this.setRowNames(newRowNames);
		}
		
		int[][] mydata = new int[dim(0)][dim(1)];
		for (int row=0;row<dim(0);row++)
			for (int col=0;col<dim(1);col++)
				mydata[row][col] = this.get(index.get(row),col);
					
		data = mydata;
	}
	
	public void SortCols(int keyrow)
	{
		IntVector dv = this.getRow(keyrow);
		
		IntVector index = dv.sort_I();
		
		if (this.hasColNames())	this.setColNames(dv.getElementNames());

		int[][] mydata = new int[dim(0)][dim(1)];
		for (int row=0;row<dim(0);row++)
			for (int col=0;col<dim(1);col++)
				mydata[row][col] = this.get(index.get(row),col);
		
		data = mydata;
	}

	
	public IntMatrix xTx()
	{
		IntMatrix out = new IntMatrix(this.dim(1),this.dim(1));
		
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
	
	public IntMatrix transpose()
	{
		IntMatrix out = new IntMatrix(this.numCols(),this.numRows());
		
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

	public static IntMatrix joinRows(IntMatrix dm1, IntMatrix dm2)
	{
		IntMatrix out = new IntMatrix(dm1.numRows()+dm2.numRows(),dm1.numCols());
		
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
	
	public static IntMatrix joinRows(List<IntMatrix> dms)
	{
		int totalRows = 0;
		for (IntMatrix dm : dms)
			totalRows+=dm.numRows();
		
		IntMatrix out = new IntMatrix(totalRows,dms.get(0).numCols());
		
		int curRow = 0;
		for (int dmi=0;dmi<dms.size();dmi++)
			for (int i=0;i<dms.get(dmi).numRows();i++)
				for (int j=0;j<dms.get(0).numCols();j++)
				{
					out.set(curRow, j, dms.get(dmi).get(i, j));
					curRow++;
				}
		
		
		if (dms.get(0).hasColNames()) out.setColNames(dms.get(0).getColNames());
		
		for (IntMatrix dm : dms)
			if (!dm.hasRowNames()) return out;
		
		List<String> newNames = new ArrayList<String>(totalRows);
		
		for (IntMatrix dm : dms)
			newNames.addAll(dm.getRowNames());
		
		out.setRowNames(newNames);
		
		return out;
	}
	
	public static IntMatrix loadColumns(String file, int[] colIndexes)
	{
		IntMatrix i = new IntMatrix();
		i.LoadCols(file, false, false, "\t", colIndexes);
		
		return i;
	}
	
	/***
	 * Loads a doublematrix from a byte-file encoded in three states (0,1,2).
	 */
	public static IntMatrix loadFromByte(String file)
	{
		
		IntMatrix out = null;
		try
		{
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
			
			byte[] numRowsByte = new byte[4];
			byte[] numColsByte = new byte[4];
			bis.read(numRowsByte, 0, 4);
			bis.read(numColsByte, 0, 4);
			
			int numRows = ByteConverter.convertToInteger(numRowsByte);
			int numCols = ByteConverter.convertToInteger(numColsByte);
			
			out = new IntMatrix(numRows, numCols);
			
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
						
					}else bos.write(ByteConverter.convertDataTextToByte(this, i, j, 3, 5) + 128);
				}
				
				bos.write(255);
			}
				
				
									
			bos.close();
		}catch (Exception e) {
			System.err.println("Error DoubleMatrix.saveAsByte(String,boolean): "+e.getMessage());e.printStackTrace();}
	}
	
	public int getAsInteger(int i, int j)
	{
		return data[i][j];
	}
	
	public IntMatrix xxT()
	{
		IntMatrix out = new IntMatrix(this.numRows(),this.numRows());
		
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
}


