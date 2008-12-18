package GuiInterception;

import Command.*;
import Factory.*;
import HandlerFactory.HandlerFactory;
import java.lang.reflect.*;
import java.security.acl.Group;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JList;
import Tunable.Tunable;
import Tunable.Tunable.Param;
//import Utils.ListSelection;
import Utils.ListSingleSelection;
import Sliders.*;


public class GuiHandlerFactory<T> implements HandlerFactory<Guihandler> {


	command command = new input();
	public Guihandler getHandler(Field f, Object o, Tunable t){
		Param parameter= t.flag();
		Class<?> type = f.getType();
		
		if(type== BoundedInteger.class)
			return new BoundedIntegerHandler(f,o,t);
		if(type== BoundedDouble.class)
			return new BoundedDoubleHandler(f,o,t);
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
		if(type == List.class){
			if(parameter==Param.MultiSelect){
				return new ListMultipleHandler<T>(f,o,t);
			}
			else	return new ListSingleHandler<T>(f,o,t);
		}
		return null;
		
		
		
/*			switch(t.type()){
				case 0://INTEGER
					return new IntegerHandler(f,o,t);
				case 1://DOUBLE
				//	return new DoubleHandler(f,o,t);
				case 2://BOOLEAN
				//	return new BooleanHandler(f,o,t);
				case 3://STRING
				//	return new StringHandler(f,o,t);
				//case 5 & 6:
				//	return new AttributeHandler(f,o,t);
				case 6://LIST
				//	return new ListHandler(f,o,t);
				case 7://GROUP
				//	return new GroupHandler(f,o,t);
				case 8://BUTTON
				//	return new ButtonHandler(f,o,t);
				default:return null;
			}
	*/
	}
}
