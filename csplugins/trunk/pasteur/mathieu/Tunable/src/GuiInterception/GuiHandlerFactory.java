package GuiInterception;

import Factory.*;
import HandlerFactory.HandlerFactory;

import java.lang.reflect.*;

import Properties.PropertiesImpl;
import TunableDefinition.Tunable;

public class GuiHandlerFactory implements HandlerFactory<Guihandler> {

	public Guihandler getHandlerType(Field f, Object o, Tunable t){
			switch(t.type()){
				case 0://INTEGER
					return new IntegerHandler(f,o,t);
				case 1://DOUBLE
					return new DoubleHandler(f,o,t);
				case 2://BOOLEAN
					return new BooleanHandler(f,o,t);
				case 3://STRING
					return new StringHandler(f,o,t);
				//case 5 & 6:
				//	return new AttributeHandler(f,o,t);
				case 6://LIST
					return new ListHandler(f,o,t);
				case 7://GROUP
					return new GroupHandler(f,o,t);
				case 8://BUTTON
					return new ButtonHandler(f,o,t);
				default:return null;
			}
	}
}
