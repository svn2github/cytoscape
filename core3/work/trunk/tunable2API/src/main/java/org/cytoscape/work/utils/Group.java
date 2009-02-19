package org.cytoscape.work.utils;

import java.util.ArrayList;


public class Group{
	
	ArrayList<String> title;
	boolean collapsed;
	
	public Group(ArrayList<String> group,boolean collapsed){
		this.title = group;
		this.collapsed = collapsed;
	}
	
	public ArrayList<String> getValue(){
		return title;
	}
	
	public boolean isCollapsed(){
		return collapsed;
	}
	
	public void setCollapsed(boolean input){
		collapsed = input;
	}
	
}