package Props;

import Tunable.*;
import java.lang.reflect.*;
import java.util.*;
import Utils.*;

public class ListSinglePropHandler<T> implements PropHandler {
	Field f;
	Object o;
	String propKey;
	ListSingleSelection<T> LSS;

	
	@SuppressWarnings("unchecked")
	public ListSinglePropHandler(Field f, Object o, Tunable t) {
		this.f = f;
		this.o = o;
		propKey = f.getName();
		try{
			LSS = (ListSingleSelection<T>) f.get(o);
		}catch(Exception e){e.printStackTrace();}
		
	}

	
	public Properties getProps() {
		Properties p = new Properties();
		p.put(propKey,LSS.getSelectedValue());
		return p;
	}

	
	@SuppressWarnings("unchecked")
	public void add(Properties p) {
		LSS.setSelectedValue((T) "");
//		if(LSS.getSelectedValue()==null)
		p.put(propKey,LSS.getSelectedValue());
//		else{
//			try{
//				p.put(propKey,LSS.getSelectedValue());
//			}catch(Exception e){e.printStackTrace();}
//		}
	}
	
	
	@SuppressWarnings("unchecked")
	public void setProps(Properties p) {
		try {
			if (p.containsKey(propKey)) {
				String val = p.get(propKey).toString();
				if (val != null){
					LSS.setSelectedValue((T) val);
					f.set(o, LSS);
				}
			}
		} catch (IllegalAccessException iae) {
			iae.printStackTrace();
		}
	}
}
