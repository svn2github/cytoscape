package Properties; 


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import javax.swing.JPanel;
import javax.swing.JTextField;
import TunableDefinition.Tunable;


public class PropertiesImpl implements properties {
	protected HashMap<String, String> propertyMap = null;
	protected HashMap<String, String> savedPropertyMap = null;
	protected HashMap<String, Object> tunablesMap = null;
	protected List<Field> tunablesList = null;
	protected String propertyPrefix = null;
	
	protected Object o;
	protected Field f;
	protected Tunable t;

	
	public PropertiesImpl(String propertyPrefix) {
		this.propertyPrefix = propertyPrefix;
		this.tunablesMap = new HashMap<String, Object>();
		this.tunablesList = new ArrayList<Field>();
	}

	
	public void setAll(Tunable t, Object o, Field f){
		this.t=t;
		this.o=o;
		this.f=f;
	}
	
	public void add() {
		try{
			tunablesMap.put(f.getName(),f.get(o));
			tunablesList.add(f);
		}catch(Exception e){e.printStackTrace();}
	}
	
	
	public Tunable get(String name) {
		if (tunablesMap.containsKey(name))
			return (Tunable) tunablesMap.get(name);

		return null;
	}

	
	public void initializeProperties(Properties props) {
		getProperties(props);
		for (Iterator<Field> iter = tunablesList.iterator(); iter.hasNext();) {
			//Tunable tunable = (Tunable) iter.next();
			Field field = (Field) iter.next();
			String property = field.getName();
			try{
				// Do we have this property?
				if (propertyMap.containsKey(property)) {
					// Yes -- set it in our array
					field.set(o, propertyMap.get(property));
					field.set(o, savedPropertyMap.get(property));
					
	//				tunable.setValue(propertyMap.get(property));
				} else {
					// No, set the default
	//				setProperty(property, tunable.getValue().toString());
					setProperty(property, field.get(o).toString());
					
	
	//				setSavedProperty(property, tunable.getValue().toString());
					setSavedProperty(property, field.get(o).toString());
	
				}
			}catch(Exception e){e.printStackTrace();}
		}
	}
	
	
	public HashMap<String,String> getProperties(Properties props) {
		String prefix = getPrefix();
		propertyMap = new HashMap<String,String>();
		savedPropertyMap = new HashMap<String,String>();

		// Find all properties with this prefix
		Enumeration<?> iter = props.propertyNames();
		while (iter.hasMoreElements()) {
			String property = (String) iter.nextElement();

			if (property.startsWith(prefix)) {
				int start = prefix.length() + 1;
				propertyMap.put(property.substring(start + 1), props.getProperty(property));
				savedPropertyMap.put(property.substring(start + 1), props.getProperty(property));
			}
		}
		return propertyMap;
	}
	
	
	public void setProperty(String property, String value) {
		propertyMap.put(property, value);
	}

	
	public void setSavedProperty(String property, String value) {
		savedPropertyMap.put(property, value);	
	}
	
	
	
	
	

	


	
	
	
	public void revertProperties() {
		propertyMap = new HashMap<String,String>();
		Set<String> keys = savedPropertyMap.keySet();
		for (Iterator<String> iter = keys.iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			propertyMap.put(new String(key), new String((String) savedPropertyMap.get(key)));
			System.out.println("default value for " + key + " = " + propertyMap.get(key));	
		}
	}


	public void saveProperties(Properties props) { // DANS PropertyMap
		for (Iterator<Field> iter = tunablesList.iterator(); iter.hasNext();) {
			Field field = (Field) iter.next();
			String property = field.getName();
			try{
				// Do we have this property?
				if (propertyMap.containsKey(property)) {
					// Yes -- set it in our array
					System.out.println("current value for " + property + " = " + propertyMap.get(property));
					setProperty(property, field.get(o).toString());
					System.out.println("saved value for " + property + " = " + propertyMap.get(property));
				}
			}catch(Exception e){e.printStackTrace();}
		}	
	}

	

	protected String getPrefix() {
		String prefix = propertyPrefix;
		if (prefix.lastIndexOf('.') != prefix.length())
			prefix = prefix + ".";
		return prefix;
	}
	
	
	public JPanel getSavedValue(){
		JPanel pane = new JPanel();
		JTextField tf = null;
		
		for (Iterator<Field> iter = tunablesList.iterator(); iter.hasNext();) {
			Field field = (Field) iter.next();
			String property = field.getName();
			if (propertyMap.containsKey(property)) {
				tf = new JTextField();
				//System.out.println("current value for " + property + " = " + propertyMap.get(property));
				//System.out.println("saved value for " + property + " = " + propertyMap.get(property));
			tf.setText("saved value for " + property + " = " + propertyMap.get(property));
			}
			pane.add(tf);
		}
		return pane;
	}
	
	public JPanel getDefaultValue(){
		JPanel pane = new JPanel();
		JTextField tf = null;
		
		for (Iterator<Field> iter = tunablesList.iterator(); iter.hasNext();) {
			Field field = (Field) iter.next();
			String property = field.getName();
			if (savedPropertyMap.containsKey(property)) {
				tf = new JTextField();
				//System.out.println("current value for " + property + " = " + propertyMap.get(property));
				//System.out.println("saved value for " + property + " = " + propertyMap.get(property));
			tf.setText("saved value for " + property + " = " + savedPropertyMap.get(property));
			}
			pane.add(tf);
		}
		return pane;
	}
	
}