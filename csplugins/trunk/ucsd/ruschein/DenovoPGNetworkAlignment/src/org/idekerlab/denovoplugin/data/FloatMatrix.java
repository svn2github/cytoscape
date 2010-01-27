package org.idekerlab.denovoplugin.data;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import Jama.Matrix;
import Jama.SingularValueDecomposition;

import java.util.concurrent.*;

import org.idekerlab.denovoplugin.utilities.ByteConversion;
import org.idekerlab.denovoplugin.utilities.ThreadPriorityFactory;
import org.idekerlab.denovoplugin.utilities.files.FileUtil;
import org.idekerlab.denovoplugin.data.matrixMath.*;

public class FloatMatrix extends DataMatrix{

	private float[][] data;
	
	public FloatMatrix()
	{
		Initialize(0,0,false,false);
	}
	
	public FloatMatrix(DataMatrix dt)
	{
		this.data = new float[dt.numRows()][dt.numCols()];
		
		for (int i=0;i<dt.numRows();i++)
			for (int j=0;j<dt.numCols();j++)
				data[i][j] = dt.getAsFloat(i, j);
		
		if (dt.hasColNames()) this.setColNames(new ArrayList<String>(dt.getColNames()));
		if (dt.hasRowNames()) this.setRowNames(new ArrayList<String>(dt.getRowNames()));
	}
	
	public FloatMatrix(int rows, int cols)
	{
		Initialize(rows,cols,false,false);
	}
	
	public FloatMatrix(int rows, int cols, float val)
	{
		Initialize(rows,cols,val);
	}
	
	public FloatMatrix(int rows, int cols, boolean arerownames, boolean arecolnames)
	{
		Initialize(rows,cols,arerownames,arecolnames);
	}
	
	public FloatMatrix(int rows, int cols, ArrayList<String> rownames, ArrayList<String> colnames)
	{
		Initialize(rows,cols);
		setRowNames(rownames);
		setColNames(colnames);
	}
	
	public FloatMatrix(int[][] data)
	{
		if (data.length==0) return;
		
		Initialize(data.length,data[0].length);
		
		for (int i=0;i<data.length;i++)
			for (int j=0;j<data[0].length;j++)
				set(i,j,data[i][j]);
	}
	
	public FloatMatrix(float[][] data)
	{
		if (data.length==0) return;
		
		Initialize(data.length,data[0].length);
		
		for (int i=0;i<data.length;i++)
			for (int j=0;j<data[0].length;j++)
				set(i,j,data[i][j]);
	}
	
	public FloatMatrix(byte[][] data)
	{
		if (data.length==0) return;
		
		Initialize(data.length,data[0].length);
		
		for (int i=0;i<data.length;i++)
			for (int j=0;j<data[0].length;j++)
				set(i,j,data[i][j]);
	}
	
	public FloatMatrix(double[][] data)
	{
		if (data.length==0) return;
		
		Initialize(data.length,data[0].length);
		
		for (int i=0;i<data.length;i++)
			for (int j=0;j<data[0].length;j++)
				set(i,j,data[i][j]);
	}
	
	public FloatMatrix(String file, boolean arerownames, boolean arecolnames)
	{
		Load(file,arerownames,arecolnames,"\t");
	}

	public FloatMatrix(String file, boolean arerownames, boolean arecolnames, String delimiter)
	{
		Load(file,arerownames,arecolnames,delimiter);
	}
	
	public FloatMatrix abs()
	{
		FloatMatrix out = this.clone();
		
		for (int i=0;i<dim(0);i++)
			for (int j=0;j<dim(1);j++)
				if (out.get(i,j)<0) out.set(i,j, -this.get(i,j));
		
		return out;
	}
	
	public FloatMatrix plus(float val)
	{
		FloatMatrix out = this.clone();
		
		for (int i=0;i<dim(0);i++)
			for (int j=0;j<dim(1);j++)
				out.set(i,j, this.get(i, j)+val);
		
		return out;
	}
	
	public FloatMatrix plus(FloatMatrix data2)
	{
		if (data2.dim(0)!=dim(0) || data2.dim(1)!=dim(1))
		{
			System.out.println("Data tables must be the same size.");
			System.exit(0);
		}
		
		FloatMatrix out = this.clone();
		
		for (int i=0;i<this.dim(0);i++)
			for (int j=0;j<this.dim(1);j++)
				out.set(i,j, this.get(i,j)+data2.get(i,j));
		
		return out;
	}
	
	public float[][] getData()
	{
		return data;
	}
	
	public double[][] asdoubleArray()
	{
		double[][] da = new double[this.numRows()][this.numCols()];
		
		for (int i=0;i<this.numRows();i++)
			for (int j=0;j<this.numCols();j++)
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
	
	public FloatVector getRow(String rowname)
	{
		return getRow(getRowIndex(rowname));
	}
	
	public FloatMatrix getRow(StringVector names)
	{
		return getRow(getRowIs(names));
	}
	
	public FloatVector getCol(String colname)
	{
		return getCol(getColIs(colname));
	}
	
	public FloatMatrix getCol(StringVector names)
	{
		return getCol(getColIs(names));
	}
	
	public FloatMatrix clone()
	{
		return new FloatMatrix(this);
	}
	
	public int dim(int dimension)
	{	
		if (dimension==0) return data.length;
		
		if (data.length>0 && dimension==1) return data[0].length;
		
		return -1;
	}
	
	public void Discretize(List<Float> breaks)
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
	
	public FloatMatrix divideBy(float val)
	{
		FloatMatrix out = this.clone();
		
		for (int i=0;i<dim(0);i++)
			for (int j=0;j<dim(1);j++)
				out.set(i,j, get(i,j)/val);
		
		return out;
	}
	
	public FloatMatrix divideBy(FloatMatrix dt)
	{
		FloatMatrix out = this.clone();
		
		for (int i=0;i<dim(0);i++)
			for (int j=0;j<dim(1);j++)
				out.set(i,j, get(i,j)/dt.get(i, j));
		
		return out;
	}
	
	public float get(int i, int j)
	{
		return(data[i][j]);
	}
	
	public int getAsInteger(int i, int j)
	{
		return((int)data[i][j]);
	}
	
	public double getAsDouble(int i, int j)
	{
		return(data[i][j]);
	}
	
	public float getAsFloat(int i, int j)
	{
		return(data[i][j]);
	}
	
	public float get(int i, String col)
	{
		return(get(i,this.colnames.indexOf(col)));
	}
	
	public float get(String row, int j)
	{
		return(get(this.colnames.indexOf(row),j));
	}
	
	public float get(String row, String col)
	{
		return(get(this.colnames.indexOf(row),this.colnames.indexOf(col)));
	}
	
	public String getAsString(int row, int col)
	{
		return Float.toString(get(row,col));
	}
	
	public FloatVector getCol(int col)
	{
		float[] fv = new float[this.numRows()];
		
		for (int r=0;r<this.numRows();r++)
			fv[r] = data[r][col];
		
		FloatVector column = new FloatVector(fv);
		
		LabelCol(column,col);
		
		return column;
	}
	
	public FloatMatrix getCol(List<?> indexes)
	{
		FloatMatrix cols = new FloatMatrix(dim(0),indexes.size(),this.hasColNames(),this.hasRowNames());
		
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
	
	public FloatMatrix getCol(BooleanVector bv)
	{
		if (bv.size()!=dim(1))
		{
			System.out.println("Error getCols(BooleanVector): Vector must be the same size as number of columns.");
			System.exit(0);
		}
		
		return getCol(bv.asIndexes());
	}
	
	public FloatMatrix getCol(IntVector indexes)
	{
		FloatMatrix cols = new FloatMatrix(this.numRows(),indexes.size(),this.hasRowNames(),this.hasColNames());
		
		if (rownames!=null) cols.setRowNames(rownames);
		
		for (int i=0;i<indexes.size();i++)
			cols.setCol(i,getCol(indexes.get(i)));
		
		return cols;
	}
	
	public FloatMatrix getCol(int[] indexes)
	{
		FloatMatrix cols = new FloatMatrix(this.numRows(),indexes.length,this.hasRowNames(),this.hasColNames());
		
		if (rownames!=null) cols.setRowNames(rownames);
		
		for (int i=0;i<indexes.length;i++)
			cols.setCol(i,getCol(indexes[i]));
		
		return cols;
	}
	
	public FloatMatrix getColDistanceMatrix()
	{
		FloatMatrix cdm = new FloatMatrix(dim(1),dim(1),getColNames(),getColNames());

		for (int c1=0;c1<dim(1);c1++)
			for (int c2=c1+1;c2<dim(1);c2++)
				{
					cdm.set(c1, c2, (float)java.lang.Math.sqrt(getCol(c1).subtract(getCol(c2)).pow(2.0f).sum()));
					cdm.set(c2, c1, cdm.get(c1,c2));
				}
		
		return cdm;
	}
	
	public FloatVector getRow(int row)
	{
		FloatVector arow = new FloatVector(dim(1));
		
		for (int c=0;c<this.numCols();c++)
			arow.add(get(row,c));
		
		LabelRow(arow,row);
		
		return arow;
	}
	
	public FloatMatrix getRow(List<?> indexes)
	{
		FloatMatrix rows = new FloatMatrix(indexes.size(),dim(1),this.hasRowNames(),this.hasColNames());
		
		if (this.hasColNames()) rows.setColNames(this.getColNames());
		
		IntVector is = this.getRowIs(indexes);
		
		return getRow(is);
	}
	
	public FloatMatrix getRow(IntVector indexes)
	{
		FloatMatrix rows = new FloatMatrix(indexes.size(),dim(1));
		
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
	
	public FloatMatrix getRow(FloatVector indexes)
	{
		return getRow(indexes.asIntVector());
	}
	
	public FloatMatrix getRow(BooleanVector bv)
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
		data = new float[numrows][numcols];
	}
	
	public void Initialize(int numrows, int numcols, float val)
	{
		Initialize(numrows,numcols);
		
		for (int row=0;row<numrows;row++)
			for (int col=0;col<numcols;col++)
				set(row,col,val);
	}
	
	public boolean isNaN()
	{
		for (int i=0;i<dim(0);i++)
			for (int j=0;j<dim(1);j++)
				if (!Float.isNaN(get(i,j))) return false;
		
		return true;
	}
	
	public FloatMatrix log(double base)
	{
		FloatMatrix out = this.clone();
		
		for (int i=0;i<dim(0);i++)
			for (int j=0;j<dim(1);j++)
				out.set(i,j, java.lang.Math.log(get(i,j))/java.lang.Math.log(base));
		
		return out;
	}
	
	public static float[][] log(float[][] m)
	{
		float[][] out = new float[m.length][m[0].length];
		
		for (int i=0;i<m.length;i++)
			for (int j=0;j<m[0].length;j++)
				out[i][j] = (float)Math.log(m[i][j]);
		
		return out;
	}
	
	public static float[][] negative(float[][] m)
	{
		float[][] out = new float[m.length][m[0].length];
		
		for (int i=0;i<m.length;i++)
			for (int j=0;j<m[0].length;j++)
				out[i][j] = -m[i][j];
		
		return out;
	}
	
	public static float max(float[][] m)
	{
		float mx = Float.NEGATIVE_INFINITY;
		
		for (int i=0;i<m.length;i++)
			for (int j=0;j<m[0].length;j++)
				if (m[i][j]>mx) mx = m[i][j];
			
		return mx;
	}
	
	public float max()
	{
		return FloatMatrix.max(data);
	}
	
	public FloatVector maxByRow(boolean nanOk)
	{
		FloatVector maxs = new FloatVector(dim(0));
		
		for (int r=0;r<dim(0);r++)
			maxs.set(r, getRow(r).max(nanOk));
		
		return maxs;
	}
	
	public float mean()
	{
		float sum = 0.0f;
		int valcount = 0;
		
		for (int i=0;i<dim(0);i++)
			for (int j=0;j<dim(1);j++)
				if (!Float.isNaN(get(i,j)))
					{
						sum += get(i,j);
						valcount++;
					}
		
		if (valcount==0) return Float.NaN;
		
		return sum / (valcount);
	}
	
	public FloatVector meanByRow()
	{
		FloatVector means = new FloatVector(this.numRows());
		
		for (int r=0;r<this.numRows();r++)
		{
			float mean = 0;
			for (int c=0;c<this.numCols();c++)
				mean += this.get(r, c)/this.numCols();
				
			means.add(getRow(r).mean());
		}
		
		return means;
	}
	
	public FloatVector meanByCol()
	{
		FloatVector stds = new FloatVector(dim(1));
		
		for (int c=0;c<dim(1);c++)
			stds.set(c, getCol(c).mean());
		
		return stds;
	}
	
	public void MeanCenterRows()
	{
		for (int i=0;i<dim(0);i++)
		{
			double mean = getRow(i).mean();
			
			for (int j=0;j<dim(1);j++)
				set(i,j,get(i,j)-mean);
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
	
	public FloatMatrix minus(FloatMatrix dt)
	{
		FloatMatrix diff = this.clone();
		
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
		this.data = FloatMatrix.negative(data);
	}
	
	public FloatVector pearsonCorrelationByCol(FloatVector v2)
	{
		if (dim(0)!=v2.size())
		{
			System.err.println("Error pearsonCorrelationByCol(FloatVector): Vector size much match number of table rows.");
			System.exit(0);
		}
		
		FloatVector corrs = new FloatVector(dim(1));
		
		for (int c=0;c<dim(1);c++)
			corrs.set(c, FloatVector.pearsonCorrelation(getCol(c), v2));
				
		return corrs;
	}
	
	public FloatVector pearsonCorrelationByRow(FloatVector v2)
	{
		if (dim(1)!=v2.size())
		{
			System.err.println("Error pearsonCorrelationByRow(FloatVector): Vector size much match number of table columns.");
			System.exit(0);
		}
		
		FloatVector corrs = new FloatVector(dim(0));
		
		for (int r=0;r<dim(0);r++)
			corrs.set(r, FloatVector.pearsonCorrelation(getRow(r), v2));
				
		return corrs;
	}
	
	public FloatMatrix pow(double power)
	{
		FloatMatrix pdt = this.clone();
		
		for (int i=0;i<dim(0);i++)
			for (int j=0;j<dim(1);j++)
				pdt.set(i,j, java.lang.Math.pow(get(i,j),power));
		
		return pdt;
	}
	
	public void ReOne(float one)
	{
		for (int i=0;i<dim(0);i++)
			for (int j=0;j<dim(1);j++)
				if (get(i,j)==1) set(i,j, one);
	}

	public void ReZero(float zero)
	{	
		for (int i=0;i<dim(0);i++)
			for (int j=0;j<dim(1);j++)
				if (get(i,j)==0) set(i,j, zero);
	}
	
	public void set(int i, int j, float val)
	{
		data[i][j] = val;
	}
	
	public void set(int i, int j, double val)
	{
		data[i][j] = (float)val;
	}
	
	public void set(int i, int j, Integer val)
	{
		data[i][j] = val.floatValue();
	}
	
	public void set(int row, int col, String val)
	{
		if (val.equals("Inf")) this.set(row,col, Float.POSITIVE_INFINITY);
		else if (val.equals("-Inf")) this.set(row,col, Float.NEGATIVE_INFINITY);
		else if (val.equals("")) this.set(row,col, Float.NaN);
		else this.set(row,col, Float.valueOf(val));
	}
	
	public void set(int i, String col, float val)
	{
		set(i, this.colnames.indexOf(col), val);
	}
	
	public void set(int i, String col, Integer val)
	{
		set(i, this.colnames.indexOf(col), val.floatValue());
	}
	
	public void set(String row, int j, float val)
	{
		set(this.rownames.indexOf(row), j, val);
	}
	
	public void set(String row, int j, Integer val)
	{
		set(this.rownames.indexOf(row), j, val.floatValue());
	}
	
	public void set(String row, String col, float val)
	{
		set(this.rownames.indexOf(row), this.colnames.indexOf(col), val);
	}
	
	public void set(String row, String col, Integer val)
	{
		set(this.rownames.indexOf(row), this.colnames.indexOf(col), val.floatValue());
	}
	
	public void setRow(int index, FloatVector vec)
	{
		if (vec.size()!=dim(1))
		{
			System.err.println("Error setRow(int, FloatVector): Vector size must equal number of columns.");
			System.exit(0);
		}
		
		if (vec.hasListName() && this.hasRowNames())
			this.setRowName(index, vec.getListName());
		
		for (int c=0;c<vec.size();c++)
			set(index,c,vec.get(c));
	}
	
	public void setRow(int index, double[] vec)
	{
		if (vec.length!=dim(1))
		{
			System.err.println("Error setRow(int, FloatVector): Vector size must equal number of columns.");
			System.exit(0);
		}
		
		for (int c=0;c<vec.length;c++)
			set(index,c,vec[c]);
	}
	
	public void setCol(int index, FloatVector vec)
	{
		if (vec.size()!=this.numRows())
			throw new java.lang.IllegalArgumentException("Error setCol(int, FloatVector): Vector size must equal number of rows. vec="+vec.size()+",rows="+this.numRows());
		
		if (vec.listname!=null && this.colnames!=null)
			this.setColName(index, vec.listname);
		
		for (int r=0;r<vec.size();r++)
			set(r,index,vec.get(r));
	}
	
	public void setCol(int index, double[] vec)
	{
		if (vec.length!=numRows())
		{
			System.err.println("Error setCol(int, FloatVector): Vector size must equal number of rows.");
			System.exit(0);
		}
		
		for (int r=0;r<vec.length;r++)
			set(r,index,vec[r]);
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
				if (!Double.isNaN(get(i,j)))
					{
						sum += java.lang.Math.pow((get(i,j) - avg),2);
						valcount++;
					}
		
		if (valcount==0) return Double.NaN;
		
		sum = sum / (valcount - 1);
		
		return java.lang.Math.pow(sum,.5);
	}
	
	public FloatVector stdByRow()
	{
		FloatVector stds = new FloatVector(dim(0));
		
		for (int r=0;r<dim(0);r++)
			stds.set(r, getRow(r).std());
		
		return stds;
	}
	
	public FloatMatrix subtract(double val)
	{
		FloatMatrix out = this.clone();
		
		for (int i=0;i<dim(0);i++)
			for (int j=0;j<dim(1);j++)
				out.set(i,j, get(i,j)-val);
		
		return out;
	}
	
	public FloatMatrix subtract(FloatMatrix data2)
	{
		if (data2.dim(0)!=dim(0) || data2.dim(1)!=dim(1))
		{
			System.out.println("Error subtract(DoubleTable): Data tables must be the same size.");
			System.exit(0);
		}
		
		FloatMatrix out = this.clone();
		
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
				if (!Double.isNaN(get(i,j)))
					{
						sum += get(i,j);
						valcount++;
					}
		
		if (valcount==0) return Double.NaN;
		
		return sum;
	}
	
	public FloatVector sumByCol()
	{
		FloatVector out = new FloatVector(this.numCols(),0);
		
		for (int c=0;c<this.numCols();c++)
			for (int r=0;r<this.numRows();r++)
				out.set(c, out.get(c)+this.get(r,c));
		
		return out;
	}
	
	public FloatVector sumByCol_ignoreNaN()
	{
		FloatVector out = new FloatVector(this.numCols(),0);
		
		for (int c=0;c<this.numCols();c++)
			for (int r=0;r<this.numRows();r++)
				if (!Double.isNaN(this.get(r,c))) out.set(c, out.get(c)+this.get(r,c));
		
		return out;
	}
	
	public FloatVector sumSquareByCol()
	{
		FloatVector out = new FloatVector(this.numCols(),0);
		
		for (int c=0;c<this.numCols();c++)
			for (int r=0;r<this.numRows();r++)
				out.set(c, out.get(c)+this.get(r,c)*this.get(r,c));
		
		return out;
	}
	
	public FloatVector sumSquareByCol_ignoreNaN()
	{
		FloatVector out = new FloatVector(this.numCols(),0);
		
		for (int c=0;c<this.numCols();c++)
			for (int r=0;r<this.numRows();r++)
				if (!Double.isNaN(this.get(r,c))) out.set(c, out.get(c)+this.get(r,c)*this.get(r,c));
		
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
	
	public void SortRows(int keycol)
	{
		if (this.dim(0)<=1) return;
		
		FloatVector dv = this.getCol(keycol);
		
		IntVector index = dv.sort_I();
		
		if (this.hasRowNames())
		{
			List<String> newRowNames = new ArrayList<String>(dv.size());
			for (int row=0;row<dim(0);row++)
				newRowNames.add(this.getRowName(index.get(row)));
			
			this.setRowNames(newRowNames);
		}
		
		float[][] mydata = new float[dim(0)][dim(1)];
		for (int row=0;row<dim(0);row++)
			for (int col=0;col<dim(1);col++)
				mydata[row][col] = this.get(index.get(row),col);
					
		data = mydata;
	}
	
	public void SortCols(int keyrow)
	{
		FloatVector dv = this.getRow(keyrow);
		
		IntVector index = dv.sort_I();
		
		if (this.hasColNames())	this.setColNames(dv.getElementNames());

		float[][] mydata = new float[dim(0)][dim(1)];
		for (int row=0;row<dim(0);row++)
			for (int col=0;col<dim(1);col++)
				mydata[row][col] = this.get(index.get(row),col);
		
		data = mydata;
	}
	
	public static double pearsonCorrelation(FloatMatrix dt1, FloatMatrix dt2)
	{
		org.apache.commons.math.stat.regression.SimpleRegression sr = new org.apache.commons.math.stat.regression.SimpleRegression();
		
		for (int i=0;i<dt1.dim(0);i++)
			for (int j=0;j<dt1.dim(1);j++)
				if (!Double.isNaN(dt1.get(i, j)) && !Double.isNaN(dt2.get(i, j))) sr.addData(dt1.get(i,j), dt2.get(i,j));
		
		return sr.getR();
	}
	
	public FloatMatrix xTx()
	{
		FloatMatrix out = new FloatMatrix(this.numCols(),this.numCols());
		
		for (int i=0;i<this.numCols();i++)
			for (int j=0;j<this.numCols();j++)
			{
				double sum = 0;
				for (int k=0;k<this.dim(0);k++)
					sum+=this.get(k, i)*this.get(k, j);
				
				out.set(i, j, sum);
			}
		
		return out;
	}
	
	public FloatMatrix xxT()
	{
		FloatMatrix out = new FloatMatrix(this.numRows(),this.numRows());
		
		for (int i=0;i<this.numRows();i++)
		{
			if (i%100==0) System.out.println(i+"/"+this.numRows());
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
	
	public FloatVector xTy(FloatVector y)
	{
		FloatVector out = new FloatVector(y.size());
		
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
		if (data.length==0) return 0;
		return data[0].length;
	}
	
	public FloatMatrix transpose()
	{
		FloatMatrix out = new FloatMatrix(this.numCols(),this.numRows());
		
		for (int i=0;i<this.numRows();i++)
			for (int j=0;j<this.numCols();j++)
				out.set(j, i, this.get(i, j));
		
		if (this.hasColNames()) out.setRowNames(this.getColNames());
		if (this.hasRowNames()) out.setColNames(this.getRowNames());
		
		return out;
	}
	
	public FloatMatrix pseudoInverse()
	{
		Matrix x = new Matrix(this.asdoubleArray());
		
		SingularValueDecomposition svd = x.svd();
		
		Matrix u = svd.getU();
		Matrix s = svd.getS();
		Matrix v = svd.getV();
		
		double smax = 0;
		
		for (int i=0;i<s.getColumnDimension();i++)
			if (s.get(i, i)>smax) smax = s.get(i, i);
		
		//Based on the machine double precision of 2.220446e-16
		double tol = 2.220446e-16*Math.max(x.getRowDimension(), x.getColumnDimension())*smax;
		
		for (int i=0;i<s.getColumnDimension();i++)
		{
			if (s.get(i, i)<=tol) s.set(i, i, 0);
			else s.set(i, i, 1/s.get(i, i));
		}
		
		return new FloatMatrix(v.times(s).times(u.transpose()).getArray());
	}
	
	public FloatVector times(FloatVector v)
	{
		FloatVector out = new FloatVector(this.numRows());
		
		for (int i=0;i<this.numRows();i++)
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

	public static FloatMatrix joinRows(FloatMatrix dm1, FloatMatrix dm2)
	{
		FloatMatrix out = new FloatMatrix(dm1.numRows()+dm2.numRows(),dm1.numCols());
		
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
	
	public static FloatMatrix joinRows(List<FloatMatrix> dms)
	{
		int totalRows = 0;
		for (FloatMatrix dm : dms)
			totalRows+=dm.numRows();
		
		FloatMatrix out = new FloatMatrix(totalRows,dms.get(0).numCols());
		
		int curRow = 0;
		for (int dmi=0;dmi<dms.size();dmi++)
			for (int i=0;i<dms.get(dmi).numRows();i++)
				for (int j=0;j<dms.get(0).numCols();j++)
				{
					out.set(curRow, j, dms.get(dmi).get(i, j));
					curRow++;
				}
		
		
		if (dms.get(0).hasColNames()) out.setColNames(dms.get(0).getColNames());
		
		for (FloatMatrix dm : dms)
			if (!dm.hasRowNames()) return out;
		
		List<String> newNames = new ArrayList<String>(totalRows);
		
		for (FloatMatrix dm : dms)
			newNames.addAll(dm.getRowNames());
		
		out.setRowNames(newNames);
		
		return out;
	}
	
	public static FloatMatrix joinCols(FloatMatrix dm1, FloatMatrix dm2)
	{
		FloatMatrix out = new FloatMatrix(FloatMatrix.joinCols(dm1.data, dm2.data));
		
		if (dm1.hasRowNames()) out.setRowNames(dm1.getRowNames());
		if (dm1.hasColNames() && dm2.hasColNames())
		{
			List<String> newNames = dm1.getColNames();
			newNames.addAll(dm2.getColNames());
			out.setColNames(newNames);
		}
		
		return out;
	}
	
	public static float[][] joinCols(float[][] dm1, float[][] dm2)
	{
		float[][] out = new float[dm1.length][dm1[0].length+dm2[0].length];
		
		for (int i=0;i<dm1.length;i++)
			for (int j=0;j<dm1[0].length;j++)
				out[i][j] = dm1[i][j];
		
		for (int i=0;i<dm2.length;i++)
			for (int j=0;j<dm2[0].length;j++)
				out[i][j+dm1[0].length] = dm2[i][j];
		
		return out;
	}
	
	public void centerRows()
	{
		for (int i=0;i<this.numRows();i++)
		{
			double mean = 0;
			for (int j=0;j<this.numCols();j++)
				mean+=this.get(i, j)/this.numCols();
			
			for (int j=0;j<this.numCols();j++)
				this.set(i, j, this.get(i, j)-mean);
		}
	}
	
	public void centerCols_ignoreNaN()
	{
		for (int j=0;j<this.numCols();j++)
		{
			double mean = 0;
			for (int i=0;i<this.numRows();i++)
				if (!Double.isNaN(this.get(i, j))) mean+=this.get(i, j)/this.numRows();
			
			for (int i=0;i<this.numRows();i++)
				this.set(i, j, this.get(i, j)-mean);
		}
	}
	
	public void centerCols()
	{
		for (int j=0;j<this.numCols();j++)
		{
			double mean = 0;
			for (int i=0;i<this.numRows();i++)
				mean+=this.get(i, j)/this.numRows();
			
			for (int i=0;i<this.numRows();i++)
				this.set(i, j, this.get(i, j)-mean);
		}
	}
	
	public void set(int a, int b, FloatMatrix fm)
	{
		for (int i=0;i<fm.numRows();i++)
			for (int j=0;j<fm.numCols();j++)
				this.data[i+a][j+b] = fm.get(i, j);
	}
	
	/**
	 * Ignores row and column names.
	 */
	public void saveAsByte(String file)
	{
		try
		{
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file,false));
		
		
			bos.write( ByteConversion.toByta(this.numRows()) );
			bos.write( ByteConversion.toByta(this.numCols()) );
			
			for (int i=0;i<this.numRows();i++)
				for (int j=0;j<this.numCols();j++)
					bos.write( ByteConversion.toByta(data[i][j]) );
		
			bos.close();
		}catch (Exception e) {System.out.println(e.getMessage()); e.printStackTrace();}
	}
	
	public static FloatMatrix loadFromByte(String file)
	{
		try
		{
			BufferedInputStream bos = new BufferedInputStream(new FileInputStream(file));
			
			byte[] f4 = new byte[4];
			bos.read(f4);
			int numRows = ByteConversion.toInt(f4);
			bos.read(f4);
			int numCols = ByteConversion.toInt(f4);
			
			float[][] out = new float[numRows][numCols];
			
			for (int i=0;i<numRows;i++)
				for (int j=0;j<numCols;j++)
				{
					bos.read(f4);
					out[i][j] = ByteConversion.toFloat(f4);
				}
			
			bos.close();
			
			return new FloatMatrix(out);		
		}catch (Exception e) {System.out.println(e.getMessage()); e.printStackTrace();}
		
		return null;
		
	}
	
	public static FloatMatrix headFromByte(String file)
	{
		FloatMatrix out = null;
		
		try
		{
			BufferedInputStream bos = new BufferedInputStream(new FileInputStream(file));
			
			byte[] f4 = new byte[4];
			bos.read(f4);
			int numRows = ByteConversion.toInt(f4);
			bos.read(f4);
			int numCols = ByteConversion.toInt(f4);
			
			out = new FloatMatrix(Math.min(numRows, 10), Math.min(numCols, 10));
			
			for (int i=0;i<numRows;i++)
				for (int j=0;j<numCols;j++)
				{
					bos.read(f4);
					if (i<out.numRows() && j<out.numCols()) out.set(i, j, ByteConversion.toFloat(f4));
				}
			
			bos.close();
			
					
		}catch (Exception e) {System.out.println(e.getMessage()); e.printStackTrace();}
		
		return out;
		
	}
	
	public static FloatVector loadColumnFromByte(String file, int col)
	{
		FloatVector out = null;
		
		try
		{
			BufferedInputStream bos = new BufferedInputStream(new FileInputStream(file));
			
			byte[] f4 = new byte[4];
			bos.read(f4);
			int numRows = ByteConversion.toInt(f4);
			bos.read(f4);
			int numCols = ByteConversion.toInt(f4);
			
			out = new FloatVector(numRows);
			
			for (int i=0;i<numRows;i++)
				for (int j=0;j<numCols;j++)
				{
					bos.read(f4);
					if (i==col) out.add(ByteConversion.toFloat(f4));
				}
			
			bos.close();
			
					
		}catch (Exception e) {System.out.println(e.getMessage()); e.printStackTrace();}
		
		return out;
		
	}
	
	public static float[][] loadColsFromByte(String file, int[] cols)
	{
		float[][] out = null;
		
		try
		{
			BufferedInputStream bos = new BufferedInputStream(new FileInputStream(file));
			
			byte[] f4 = new byte[4];
			bos.read(f4);
			int numRows = ByteConversion.toInt(f4);
			bos.read(f4);
			int numCols = ByteConversion.toInt(f4);
			
			if (numRows<0 || numCols <0)
				throw new java.lang.AssertionError("Invalid byte file.");
			
			out = new float[numRows][cols.length];
			
			for (int i=0;i<numRows;i++)
			{
				float[] row = new float[numCols];
				for (int j=0;j<numCols;j++)
				{
					bos.read(f4);
					row[j] = ByteConversion.toFloat(f4);
				}
				
				for (int j=0;j<cols.length;j++)
					out[i][j] = row[cols[j]];
			}
			
			bos.close();
			
					
		}catch (Exception e) {System.out.println(e.getMessage()); e.printStackTrace();}
		
		return out;
		
	}
	
	public FloatMatrix xxT_MultiThreaded()
	{
		FloatMatrix out = new FloatMatrix(this.numRows(),this.numRows());
		
		ExecutorService exec = Executors.newCachedThreadPool(new ThreadPriorityFactory(Thread.MIN_PRIORITY));
		
		for (int i=0;i<this.numRows();i++)
			exec.execute(new XXTRunner(data,out.data,i));
				
		exec.shutdown();
		
		try
		{
			exec.awaitTermination(3000000, TimeUnit.SECONDS);
			if (!exec.isTerminated()) System.out.println("Did not fully terminate!");
		}catch (InterruptedException e) {System.out.println(e);exec.shutdownNow();}
		
		return out;
	}
	
	public static float[] meanByRow(float[][] m)
	{
		float[] means = new float[m.length];
		
		for (int r=0;r<m.length;r++)
		{
			float mean = 0;
			for (int c=0;c<m[0].length;c++)
				mean += m[r][c];
				
			means[r] = mean/m[0].length;
		}
		
		return means;
	}
	
	public static float[] meanByCol(float[][] m)
	{
		float[] means = new float[m[0].length];
		
		for (int c=0;c<m[0].length;c++)
		{
			float mean = 0;
			for (int r=0;r<m.length;r++)
				mean += m[r][c];
				
			means[c] = mean/m.length;
		}
		
		return means;
	}
	
	public static float[] meanByCol_IgnoreNaN(float[][] m)
	{
		float[] means = new float[m[0].length];
		
		for (int c=0;c<m[0].length;c++)
		{
			int count = 0;
			float mean = 0;
			for (int r=0;r<m.length;r++)
			{
				if (!Float.isNaN(m[r][c]))
				{
					mean += m[r][c];
					count++;
				}
				
			}
				
			means[c] = (count==0) ? Float.NaN : mean/count;
		}
		
		return means;
	}
	
	public static float[] meanByRow_IgnoreNaN(float[][] m)
	{
		float[] means = new float[m.length];
		
		for (int r=0;r<m.length;r++)
		{
			int count = 0;
			float mean = 0;
			for (int c=0;c<m[0].length;c++)
			{
				if (!Float.isNaN(m[r][c]))
				{
					mean += m[r][c];
					count++;
				}
				
			}
				
			means[r] = (count==0) ? Float.NaN : mean/count;
			
		}
		
		
		return means;
	}
	
	public static float[] stdByRow(float[][] m, float[] means)
	{
		float[] stds = new float[m.length];
		
		for (int r=0;r<m.length;r++)
		{
			float sum = 0;
			for (int c=0;c<m[0].length;c++)
			{
				float diff = m[r][c]-means[r];
				sum+= diff*diff;
			}
			
			stds[r] = (float)Math.sqrt(sum/(m[0].length));
		}
			
		
		return stds;
	}
	
	public static float[] stdByRow_IgnoreNaN(float[][] m, float[] means)
	{
		float[] stds = new float[m.length];
		
		for (int r=0;r<m.length;r++)
		{
			int count = 0;
			float sum = 0;
			for (int c=0;c<m[0].length;c++)
				if (!Float.isNaN(m[r][c]))
				{
					float diff = m[r][c]-means[c];
					sum+= diff*diff;
					count++;
				}
			
			stds[r] = (count==0) ? Float.NaN : (float)Math.sqrt(sum/count);

		}
		
		return stds;
	}
	
	public static float[] stdByCol(float[][] m, float[] means)
	{
		float[] stds = new float[m[0].length];
		
		for (int c=0;c<m[0].length;c++)
		{
			float sum = 0;
			for (int r=0;r<m.length;r++)
			{
				float diff = m[r][c]-means[c];
				sum+= diff*diff;
			}
			
			stds[c] = (float)Math.sqrt(sum/(m.length));
		}
			
		
		return stds;
	}
	
	public static float[] stdByCol_IgnoreNaN(float[][] m, float[] means)
	{
		float[] stds = new float[m[0].length];
		
		for (int c=0;c<m[0].length;c++)
		{
			int count = 0;
			float sum = 0;
			for (int r=0;r<m.length;r++)
				if (!Float.isNaN(m[r][c]))
				{
					float diff = m[r][c]-means[c];
					sum+= diff*diff;
					count++;
				}
			
			stds[c] = (count==0) ? Float.NaN : (float)Math.sqrt(sum/count);

		}
		
		return stds;
	}
	
	public static float[][] randNorm(int m, int n)
	{
		float[][] out = new float[m][n];
		
		java.util.Random randgen = new java.util.Random();
		randgen.setSeed(System.nanoTime());
		
		for (int i=0;i<m;i++)
			for (int j=0;j<n;j++)
				out[i][j] =  (float)randgen.nextGaussian();
		
		return out;
	}
	
	public static float[][] randUnif(int m, int n)
	{
		float[][] out = new float[m][n];
		
		java.util.Random randgen = new java.util.Random();
		randgen.setSeed(System.nanoTime());
		
		for (int i=0;i<m;i++)
			for (int j=0;j<n;j++)
				out[i][j] = (float)randgen.nextDouble();
		
		return out;
	}
	
	public static float[][] getRow(float[][] x, int[] indexes)
	{
		if (indexes.length==0) return new float[0][0];
		
		float[][] rows = new float[indexes.length][x[0].length];
		
		for (int i=0;i<indexes.length;i++)
		{
			int indexi = indexes[i];
			for (int j=0;j<x[0].length;j++)
				rows[i][j] = x[indexi][j];
		}
			
		return rows;
	}
	
	public static float[][] getCol(float[][] x, int[] indexes)
	{
		if (indexes.length==0) return new float[0][0];
		
		float[][] cols = new float[x[0].length][indexes.length];
		
		for (int i=0;i<indexes.length;i++)
		{
			int indexi = indexes[i];
			for (int r=0;r<x.length;r++)
				cols[r][i] = x[r][indexi];
		}
			
		return cols;
	}
	
	public static float[][] getRowCol(float[][] x, int[] rows, int[] cols)
	{
		if (rows.length==0 || cols.length==0) return new float[0][0];
		
		float[][] out = new float[rows.length][cols.length];
		
		for (int i=0;i<rows.length;i++)
			for (int j=0;j<cols.length;j++)
				out[i][j] = x[rows[i]][cols[j]];
			
		return out;
	}
	
	public static void meanCenterCols(float[][] x)
	{
		for (int j=0;j<x[0].length;j++)
		{
			double sum = 0;
			
			for (int i=0;i<x.length;i++)
				sum += x[i][j];
			
			sum /= x.length;
			
			for (int i=0;i<x.length;i++)
				x[i][j] -= sum;
		}
	}
	
	public static float covByCol(float[][] x, int c1, int c2)
	{
		float cov = 0;
		for (int i=0;i<x.length;i++)
			cov += x[i][c1]*x[i][c2];
		
		return cov/x.length;
	}
	
	public static float covByCol_IgnoreNaN(float[][] x, int c1, int c2)
	{
		int count = 0;
		float cov = 0;
		for (int i=0;i<x.length;i++)
			if (!Float.isNaN(x[i][c1]) && !Float.isNaN(x[i][c2]))
			{
				cov += x[i][c1]*x[i][c2];
				count++;
			}
		
		return (count==0) ? Float.NaN : cov/count;
	}
	
	public static float[][] corrSymmetric(float[][] m)
	{
		int mmm1 = m.length-1;
		
		float[] means = FloatMatrix.meanByCol(m);
		float[] sd = FloatMatrix.stdByCol(m, means);
		
		float[][] m2 = new float[m.length][m.length];
		for (int i=0;i<m.length;i++)
			for (int j=0;j<m.length;j++)
				m2[i][j] = m[i][j]-means[j];
		
		float[][] out = new float[m.length][m.length];
		
		for (int i=0;i<m.length;i++)
			out[i][i] = 1;
		
		for (int i=0;i<mmm1;i++)
			for (int j=i+1;j<m.length;j++)
			{
				float cor = covByCol(m2,i,j)/(sd[i]*sd[j]);
				out[i][j] = cor;
				out[j][i] = cor;
			}
		
		return out;
	}
	
	public static float[][] corrSymmetric_IgnoreNaN(float[][] m)
	{
		int mmm1 = m.length-1;
		
		float[] means = FloatMatrix.meanByCol_IgnoreNaN(m);
		float[] sd = FloatMatrix.stdByCol_IgnoreNaN(m, means);
		
		float[][] m2 = new float[m.length][m.length];
		for (int i=0;i<m.length;i++)
			for (int j=0;j<m.length;j++)
				m2[i][j] = m[i][j]-means[j];
		
		float[][] out = new float[m.length][m.length];
		
		for (int i=0;i<m.length;i++)
			out[i][i] = 1;
		
		for (int i=0;i<mmm1;i++)
			for (int j=i+1;j<m.length;j++)
			{
				float cor = covByCol_IgnoreNaN(m2,i,j)/(sd[i]*sd[j]);
				out[i][j] = cor;
				out[j][i] = cor;
			}
		
		return out;
	}
	
	public static void Square(float[][] m)
	{
		for (int i=0;i<m.length;i++)
			for (int j=0;j<m[0].length;j++)
				m[i][j] = m[i][j]*m[i][j];
	}
}

