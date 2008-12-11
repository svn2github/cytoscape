package Interceptors;

import java.util.LinkedList;
import HandlerFactory.Handler;
import Command.*;


public interface TunableInterceptor<H extends Handler> {
	
	public LinkedList<H> intercept(Command command);
	
}