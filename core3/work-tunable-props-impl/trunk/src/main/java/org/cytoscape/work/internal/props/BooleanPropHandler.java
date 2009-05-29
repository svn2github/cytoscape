package org.cytoscape.work.internal.props;

import java.lang.reflect.Field;
import java.util.Properties;

import org.cytoscape.work.Tunable;

public class BooleanPropHandler extends AbstractPropHandler{
	
	public BooleanPropHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
	}

	
	public Properties getProps(){
		Properties p = new Properties();
	 	try{
	 		p.setProperty(propKey,f.get(o).toString());
	 	}catch(Exception e){e.printStackTrace();}
		return p;
	}
	
	
	public void setProps(Properties p){
		try{
			if(p.containsKey(propKey)){
				String val = p.getProperty(propKey).toString();
				if(val != null) f.set(o, Boolean.valueOf(Boolean.parseBoolean(val)));
			}
		}catch(Exception e){e.printStackTrace();}
	}

}
