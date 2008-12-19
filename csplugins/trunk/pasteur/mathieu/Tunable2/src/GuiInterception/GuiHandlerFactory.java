package GuiInterception;


import Factory.*;
import HandlerFactory.HandlerFactory;
import java.lang.reflect.*;
import java.security.acl.Group;
import java.util.List;
import Tunable.Tunable;
import Tunable.Tunable.Param;
import Utils.Bounded;
import Sliders.*;


public class GuiHandlerFactory<T> implements HandlerFactory<Guihandler> {


	public Guihandler getHandler(Field f, Object o, Tunable t){
		Param parameter= t.flag();
		Class<?> type = f.getType();
		
		if(type== Integer.class)
			return new IntegerHandler(f,o,t);
		if(type== Double.class)
			return new DoubleHandler(f,o,t);
		if(type==Boolean.class)
			return new BooleanHandler(f,o,t);
		if(type== String.class)
			return new StringHandler(f,o,t);
		if(type==Group.class)
			return new GroupHandler(f,o,t);
		if(type==Bounded.class)
			return new BoundedHandler(f,o,t);
		if(type == List.class){
			if(parameter==Param.MultiSelect){
				return new ListMultipleHandler<T>(f,o,t);
			}
			else	return new ListSingleHandler<T>(f,o,t);
		}
		return null;
	}
}
