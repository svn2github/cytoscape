/* BEGIN_HEADER                                              Java TreeView
*
* $Author: alokito $
* $RCSfile: RectData.java,v $
* $Revision: 1.6 $
* $Date: 2005/12/05 05:27:53 $
* $Name:  $
*
* This file is part of Java TreeView
* Copyright (C) 2001-2003 Alok Saldanha, All Rights Reserved. Modified by Alex Segal 2004/08/13. Modifications Copyright (C) Lawrence Berkeley Lab.
*
* This software is provided under the GNU GPL Version 2. In particular,
*
* 1) If you modify a source file, make a comment in it containing your name and the date.
* 2) If you distribute a modified version, you must do it under the GPL 2.
* 3) Developers are encouraged but not required to notify the Java TreeView maintainers at alok@genome.stanford.edu when they make a useful addition. It would be nice if significant contributions could be merged into the main distribution.
*
* A full copy of the license can be found in gpl.txt or online at
* http://www.gnu.org/licenses/gpl.txt
*
* END_HEADER
*/
package edu.stanford.genetics.treeview.model;
import java.util.ArrayList;

import edu.stanford.genetics.treeview.LogBuffer;
/**
 * 
 * @author aloksaldanha
 *
 * Represents a rectangle of data, where some columns are strings and some columns are doubles.
 */
public class RectData {
	private Column[] dataArray;
	
	/**
	 * 
	 */
	public RectData(String[] names, ColumnFormat[] formats, int gap) {
		int col = names.length;
		dataArray = new Column[col];
		for (int i = 0; i < names.length; i++){
			dataArray[i] = ColumnFormat.initColumn(formats[i], names[i], gap);
		}
	}

	public int addData(String[] data){
		int index = 0;
		int col = getCol();
		int len = data.length;
		for (int i = 0; i < col; i++){
			if (i < len){
				index = dataArray[i].addData(data[i]);
			}else{
				index = dataArray[i].addData(null);
			}
		}
		return index;
	}
	
	public String getString(int row, int col){
		return dataArray[col].getString(row);
	}
	
	public double getDouble(int row, int col){
		return dataArray[col].getDouble(row);
	}

	public int getRow(){
		if (dataArray.length < 1){
			return 0;
		}else{
			return dataArray[0].getNum();
		}
	}
	
	public int getCol(){
		return dataArray.length;
	}
	
	
	public String getColumnName(int index){
		return dataArray[index].getName();
	}
	//make it works like Vector
	public Object elementAt(int index){
		int col = getCol();
		String[] string = new String[col];
		if (index == 0){
			for (int i = 0; i < col; i++){
				string[i] = dataArray[i].getName();
			}
		}else{
			for (int i = 0; i < col; i++){
				string[i] = getString(index - 1, i);
			}
		}
		return string;
	}
	
	public int size(){
		return getRow() + 1;
	}
	
	public Object firstElement(){
		return elementAt(0);
	}
}

/**
 * @author gcong
 *
 * This represents the column of a RectData object.
 */
abstract class Column {

	protected String name;
	protected int gap;
	protected int num;
	protected ArrayList dataArray;
	protected boolean isDouble;
	
	/**
	 * 
	 */
	public Column(String name, int gap) {
		this.name = name;
		this.gap = gap;
		dataArray = new ArrayList();
		num = 0;
	}

	protected int incIndex(){
		if (num % gap == 0){
			dataArray.add(initData());
		}
		return (num ++)  % gap;
	}
	
	public String getName(){
		return name;
	}
	
	public String getString(int index){
		int ind = index / gap;
		int off = index % gap;
		return getString(ind, off);
	}

	public double getDouble(int index){
		int ind = index / gap;
		int off = index % gap;
		return getDouble(ind, off);
	
	}

	public int addData(String string){
		int off = incIndex();
		int ind = dataArray.size() - 1;
		addData(ind, off, string);
		return num;
	}
	
	public int getNum(){
		return num;
	}

	protected abstract ColumnFormat getFormat();
	protected abstract double getDouble(int index, int offset);
	protected abstract String getString(int index, int offset);
	protected abstract void addData(int index, int offset, String string);
	protected abstract Object initData();

	
}

class DoubleColumn extends Column {

	public DoubleColumn(String name, int gap) {
		super(name, gap);
	}
	
	public String getString(int index, int offset){
		double data = ((double[])dataArray.get(index))[offset];
		return (data == Double.NaN)? null : "" + data;
	}
	
	public double getDouble(int index, int offset){
		return ((double[])dataArray.get(index))[offset];
	}
	
	protected void addData(int index, int offset, String string){
		double data;
		if (string == null) {
			data = Double.NaN;
		} else try {
			data=  Double.parseDouble(string);
		} catch (Exception e) {
			LogBuffer.println("error converting double:" +e);
			e.printStackTrace();
			data = Double.NaN;
		}
		((double[])dataArray.get(index))[offset] = data;
	}
	
	protected Object initData(){
		return new double[gap];
	}
	
	public ColumnFormat getFormat(){
		return ColumnFormat.DoubleFormat;
	}
}

class ColumnFormat {
	private final String name;
	private ColumnFormat(String name){
		this.name = name;
	}
	/**
	 * 
	 */
	public String toString(){
		return name;
	}
	
	public static Column initColumn(ColumnFormat format, String name, int gap){
		if (format == StringFormat){
			return new StringColumn(name, gap);
		}else if (format == DoubleFormat){
			return new DoubleColumn(name, gap);
		}else if (format == IntFormat){
			return new IntColumn(name, gap);
		}
		return null;
	}
	
	public static final ColumnFormat StringFormat = new ColumnFormat("String Format");
	public static final ColumnFormat DoubleFormat = new ColumnFormat("Double Format");
	public static final ColumnFormat IntFormat = new ColumnFormat("Int Format");
	
}

class IntColumn extends Column {

	/**
	 * @param name
	 * @param gap
	 */
	public IntColumn(String name, int gap) {
		super(name, gap);
	}

	/* (non-Javadoc)
	 * @see edu.stanford.genetics.treeview.model.lbl.Column#getFormat()
	 */
	public ColumnFormat getFormat() {
		return ColumnFormat.IntFormat;
	}

	/* (non-Javadoc)
	 * @see edu.stanford.genetics.treeview.model.lbl.Column#getString(int, int)
	 */
	protected String getString(int index, int offset) {
		double data = ((int[])dataArray.get(index))[offset];
		return (data == 0)? null : "" + data;
	}

	protected double getDouble(int index, int offset) {
		return ((int[])dataArray.get(index))[offset];
	}

	/* (non-Javadoc)
	 * @see edu.stanford.genetics.treeview.model.lbl.Column#addData(int, int, java.lang.String)
	 */
	protected void addData(int index, int offset, String string) {
		int data = (string == null)? 0 : Integer.parseInt(string);
		((int[])dataArray.get(index))[offset] = data;
	}

	/* (non-Javadoc)
	 * @see edu.stanford.genetics.treeview.model.lbl.Column#initData()
	 */
	protected Object initData() {
		return new int[gap];
	}
}

class StringColumn extends Column {

	public StringColumn(String name, int gap) {
		super(name, gap);
	}
	
	protected Object initData(){
		return new byte[gap][];
	}
	
	protected String getString(int index, int offset){
		byte[] tmp = ((byte[][])dataArray.get(index))[offset];
		return (tmp == null) ? null : new String(tmp);
	}

	protected double getDouble(int index, int offset){
		String string = getString(index, offset);
		return (string == null)? Double.NaN : Double.parseDouble(string);
	}

	protected void addData(int index, int offset, String string){
		if (string != null){
			((byte[][])dataArray.get(index))[offset] = string.getBytes();
		}
	}
	
	public ColumnFormat getFormat(){
		return ColumnFormat.StringFormat;
	}
}