package org.example.tunable.internal.props;

import java.lang.reflect.Field;
import java.util.Properties;

import org.example.tunable.Tunable;
import org.example.tunable.util.ListSingleSelection;

public class ListSingleSelectionPropHandler<T> extends AbstractPropHandler{
	
	
	public ListSingleSelectionPropHandler(Field f, Object o, Tunable t){
		super(f,o,t);
	}
	
	public Properties getProps(){
		Properties p = new Properties();
		try{
			p.setProperty(propKey,((ListSingleSelection<T>)f.get(o)).getSelectedValue().toString());
		}catch(Exception e){e.printStackTrace();}
		return p;
	}
	
	public void setProps(Properties p){
		try{
			if(p.containsKey(propKey)){
				ListSingleSelection<T> lss = (ListSingleSelection<T>) f.get(o);
				T val = (T) p.getProperty(propKey).toString();
				if(val != null) {
					lss.setSelectedValue(val);
					f.set(o, lss);
				}
			}
		}catch(Exception e){e.printStackTrace();}
	}
	
}