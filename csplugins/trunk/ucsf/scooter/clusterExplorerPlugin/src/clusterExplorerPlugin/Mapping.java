package clusterExplorerPlugin;

import java.util.Enumeration;
import java.util.Hashtable;


public class Mapping {
	
	
	private Hashtable<String,Integer> numberForGivenID = new Hashtable<String,Integer>();
	private Hashtable<Integer,String> idForGivenNumber = new Hashtable<Integer,String>();
	
	public void add(String id, int nr) {
		
		numberForGivenID.put(id, nr);
		idForGivenNumber.put(nr, id);
		
	}
	
	public int getNumber(String id) {
		return numberForGivenID.get(id);
	}
	
	public String getID(int nr) {
		return idForGivenNumber.get(nr);
	}
	
	public int size() {
		return idForGivenNumber.size();
	}
	
	public void printMapping() {
		Enumeration<Integer> e = idForGivenNumber.keys();
		while(e.hasMoreElements()) {
			int nr = e.nextElement();
			String id = idForGivenNumber.get(nr);
			System.out.println(nr + "\t" + id);
		}
	}
	
}



















