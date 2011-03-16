package PPI;

import java.util.Hashtable;
import java.util.Enumeration;

public class pair {
    private Hashtable _pair;
    
    public pair(){
	_pair = new Hashtable();
    }

    public void add_Pair(Object o1, Object o2, Object weight){
	Hashtable hash_tmp;

	if(_pair.containsKey(o1)){
	    hash_tmp = (Hashtable)_pair.get(o1);
	}
	else {
	    hash_tmp = new Hashtable();
	    _pair.put(o1, hash_tmp);
	}

	hash_tmp.put(o2, weight);
	
    }

    public void print_all_pairs(){
    		for(Enumeration e1 = _pair.keys();
    			e1.hasMoreElements();){
    			Object o1 = e1.nextElement();
    			Hashtable o1h = (Hashtable)_pair.get(o1);

    			for(Enumeration e2 = o1h.keys();
    				e2.hasMoreElements();){
    				Object o2 = e2.nextElement();
    				Object w = o1h.get(o2);
    				System.out.println(o1 + "\t" + o2 + "\t" + w);
    			}
    			
    		}
    }
    
    public static void main(String args[]){
    	
        pair ppi = new pair();
        ppi.add_Pair((Object)"Protein-A", (Object)"Protein-B", (Object)"w1");
        ppi.add_Pair((Object)"Protein-A", (Object)"Protein-C", (Object)"w1.5");
        ppi.add_Pair((Object)"Protein-C", (Object)"Protein-D", (Object)"w1.9");
        ppi.print_all_pairs();
    }

}

