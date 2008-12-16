package HandlerFactory;


import java.lang.reflect.*;
import Tunable.Tunable;


public interface HandlerFactory<H extends Handler>{
	
	 H getHandler(Field f, Object o, Tunable t);
	 
}