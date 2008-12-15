package GuiInterception;


import HandlerFactory.Handler;
import Interceptors.*;


public class GuiTunableInterceptor<H extends Handler> extends HiddenTunableInterceptor<Guihandler> {
	
	public GuiTunableInterceptor(){
		super( new GuiHandlerFactory() );
	}
	
		
}
	