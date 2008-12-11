package GuiInterception;


import Interceptors.*;


public class GuiTunableInterceptor extends HiddenTunableInterceptor<Guihandler> {
	
	public GuiTunableInterceptor(){
		super( new GuiHandlerFactory() );
	}
	
		
}
	