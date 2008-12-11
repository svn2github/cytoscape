package Interceptors;

import Command.Command;
import HandlerFactory.Handler;
import HandlerFactory.HandlerFactory;
import Properties.PropertiesImpl;
import TunableDefinition.Tunable;
import java.lang.reflect.*;
import java.util.LinkedList;



public abstract class HiddenTunableInterceptor<H extends Handler> implements TunableInterceptor{
	
	protected HandlerFactory<H> factory;

	public HiddenTunableInterceptor(HandlerFactory<H> tunablehandlerfactory) {
		this.factory=tunablehandlerfactory;
	}
	
	public LinkedList<H> intercept(Command command){
		
		//PropertiesImpl properties = new PropertiesImpl("TunableSampler");
		
		LinkedList<H> HandlerTunablesList = new LinkedList<H>();
		for(Field field : command.getClass().getFields()){
			if(field.isAnnotationPresent(Tunable.class)){
				try{
					Tunable tunable = field.getAnnotation(Tunable.class);
					H handler = factory.getHandlerType(field, command, tunable);
					if(handler!=null) HandlerTunablesList.add(handler);
					
				}catch (Exception e){e.printStackTrace();}			
			}
		}
		
		return HandlerTunablesList;
		}
}