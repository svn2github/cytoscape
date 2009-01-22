package Utils;

import java.util.ArrayList;


public class Group{
	
	ArrayList<String> title;
	
	public Group(ArrayList<String> group){
		this.title = group;
	}
	
	public ArrayList<String> getValue(){
		return title;
	}
}