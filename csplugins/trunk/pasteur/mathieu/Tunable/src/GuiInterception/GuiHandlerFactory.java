package GuiInterception;

import Command.*;
import Factory.*;
import HandlerFactory.HandlerFactory;
import java.lang.reflect.*;
import javax.swing.JButton;
import javax.swing.JList;
import TunableDefinition.Tunable;


public class GuiHandlerFactory implements HandlerFactory<Guihandler> {


	Command command = new Input();
	public Guihandler getHandlerType(Field f, Object o, Tunable t){
		//Command command = new Input();

		if(t.type()== BoundedInteger.class)
			return new BoundedIntegerHandler(f,o,t);
		if(t.type()== BoundedDouble.class)
			return new BoundedDoubleHandler(f,o,t);
		if(t.type()== Integer.class)
			return new IntegerHandler(f,o,t);
		if(t.type()== JList.class)
			return new ListHandler(f,o,t);
		if(t.type()== Double.class)
			return new DoubleHandler(f,o,t);
		if(t.type()==Boolean.class)
			return new BooleanHandler(f,o,t);
		if(t.type()== String.class)
			return new StringHandler(f,o,t);
		if(t.type()== JButton.class)
			return new ButtonHandler(f,o,t);
		
		//else if(t.type()==Double.class)
		//	return new DoubleHandler(f,o,t);
		
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
