package cytoscape.data.readers;
import java.util.*;
public class GMLNode{
	public boolean terminal;
	public boolean quotes = false;
	private String string_value;
	private static String TAB = "\t";
	private HashMap key2GMLNodeVec;


	public GMLNode(String string_value){
		if(string_value.startsWith("\"")&&string_value.endsWith("\"")){
			quotes = true;
			string_value = string_value.substring(1,string_value.length()-1);
		}
		this.string_value = string_value;
		terminal = true;
	}

	public GMLNode(){
		terminal = false;
		key2GMLNodeVec = new HashMap();
	}

	public String toString(){
		return toString("");
	}
	
	private String toString(String indent){
		String result = "";
		if(terminal){
			if(!quotes){
				return TAB+string_value;
			}else{
				return TAB+"\""+string_value+"\"";
			}
		}
		else{
			result += "\n"+indent+"[\n";
			Iterator it = key2GMLNodeVec.keySet().iterator();
			while(it.hasNext()){
				String key = (String)it.next();
				Iterator mapIt = ((Vector)key2GMLNodeVec.get(key)).iterator();
				while(mapIt.hasNext()){
					GMLNode next = (GMLNode)mapIt.next();
					result += (indent+key+next.toString(indent+TAB)+"\n");	
				}
			}
			result += (indent+"]");
		}
		return result;
	}
	
	public Double doubleValue(){
		return new Double(string_value);
	}

	public Integer integerValue(){
		return new Integer(string_value);
	}

	public String stringValue(){
		return string_value;
	}

	public void addMapping(String key, GMLNode node){
		Vector values;
		values = (Vector)key2GMLNodeVec.get(key);
		if(values == null){
			values = new Vector();
			key2GMLNodeVec.put(key,values);
		}
		values.add(node);
	}

	public Vector getMapping(String key){
		return (Vector)key2GMLNodeVec.get(key);
	}
		
}
