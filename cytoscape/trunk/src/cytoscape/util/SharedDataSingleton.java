package cytoscape.util;

import java.util.HashMap;

/*
 * I added this as a quick way to share data between plugins and core
 * and whatever other classes need it. I found that with the current
 * (10/4/04) method of loading plugins from jars, that this class ,
 * which I normally put in a plugin package and loaded as a flat file
 * (not a jar), had to be in the core or no other classes could find it.
 * -DRT
 * 
 */

/**
 * The actual singleton object (an extension of HashMap) containing
 * the list of shared objects.
 * Use it as follows.<P>
 * You can get this object into your class in a static manner, outside
 * of any instance-specific code: 
 * 
 * <code>
 * public class MyClass  {
 * 		private SharedDataSingleton sharedData = SharedDataSingleton.getInstance();
	...
 * </code>
 * 
 * To retrieve, add, and remove items from the list of shared objects,
 * use the appropriate methods in java.util.HashMap: 
 * get(), put(),  and remove().
 * 
 * @author Dan Tenenbaum
 */
public final class SharedDataSingleton extends HashMap {
	
   private final static String CLASSNAME = "cytoscape.util.SharedDataSingleton"; 
   private static HashMap map = new HashMap();
   
   private SharedDataSingleton() {
   }

   
   /**
    * The method used to get an instance of this object. This class follows
    * the Singleton design pattern, so this method can guarantee that it will 
    * always return the identical object, no matter which thread or classloader
    * it is called from.
    * 
    * @return The only extant instance of SharedDataSingleton.
    */
   public static synchronized SharedDataSingleton getInstance() {
	  SharedDataSingleton singleton = (SharedDataSingleton)map.get(CLASSNAME);

	  if(singleton != null) {
		 return singleton;
	  }
	  try {
		 Class klass = getClass(CLASSNAME);
		 singleton = (SharedDataSingleton)klass.newInstance();
	  }
	  catch(ClassNotFoundException cnf) {
		 System.out.println("Couldn't find class " + CLASSNAME);    
	  }
	  catch(InstantiationException ie) {
		 System.out.println("Couldn't instantiate an object of type " + CLASSNAME);    
	  }
	  catch(IllegalAccessException ia) {
		 System.out.println("Couldn't access class " + CLASSNAME);    
	  }
	  map.put(CLASSNAME, singleton);
	  //System.out.println("created singleton: " + singleton);
	  //check map contents here to make sure no dupes

	  return singleton;
   }
   
   /**
    * Makes sure that the SharedDataSingleton object is loaded by the same
    * classloader that called getInstance(). 
    * 
    * @param classname
    * @return
    * @throws ClassNotFoundException
    */
   private static Class getClass(String classname) 
										 throws ClassNotFoundException {
	  ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

	  if(classLoader == null)
		 classLoader = SharedDataSingleton.class.getClassLoader();

	  return (classLoader.loadClass(classname));
   }
}
