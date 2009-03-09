package org.cytoscape.work.util;


public class InputStream{
	
	Object o;
	Class<?> t;
	
	public InputStream(Object obj,Class<?> type){
		this.o=obj;
		this.t=type;
	}
	
	public InputStream(Object obj){
		this.o=obj;
	}
	
	public Class<?> getType(){
		return t;
	}
	
	public Object getFileURL(){
		return o;
	}
	
}