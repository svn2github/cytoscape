package GuiInterception;

import Command.command;
import HandlerFactory.Handler;
import HandlerFactory.HandlerFactory;
import Tunable.Tunable;
import java.lang.reflect.*;
import java.util.LinkedList;
import java.util.List;


public abstract class HiddenTunableInterceptor<T extends Handler> implements TunableInterceptor{
	
	protected HandlerFactory<T> factory;
	List<T> handlerList = new LinkedList<T>();
	
	
	public HiddenTunableInterceptor(HandlerFactory<T> tunablehandlerfactory) {
		this.factory=tunablehandlerfactory;
	}
	
	
	public final void intercept(command command){
		// Find each public field in the class.
		for(Field field : command.getClass().getFields()){
			// See if the field is annotated as a Tunable.
			if(field.isAnnotationPresent(Tunable.class)){
				try{
					Tunable tunable = field.getAnnotation(Tunable.class);
					// Get a handler for this particular field type and
					// add it to the list.
					
					T handler = factory.getHandler(field, command, tunable);
					if(handler!=null) handlerList.add(handler);
					else
						System.out.println("No handler for type: " + field.getType().getName());
				}catch (Throwable ex) {
					System.out.println("tunable intercept failed: " + field.toString());
					ex.printStackTrace();
				}			
			}
		}
	}
	


	public final void GetInputPanes(){
		getInputPanes(handlerList);
	}
	
	public final void Display(){
		display(handlerList);
	}
	
	public final void Save(){
		save(handlerList);
	}
	
	public final void ProcessProperties(){
		processProps(handlerList);
	}
	
	public final void addProperties(){
		addProps(handlerList);
	}
	
	protected abstract void save(List<T> handlerList);
	protected abstract void display(List<T> handlerList);
	protected abstract void getInputPanes(List<T> handlerList);
	protected abstract void processProps(List<T> handlerList);
	protected abstract void addProps(List<T> handlerList);
}