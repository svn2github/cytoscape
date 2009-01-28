package GuiInterception;

public interface TunableInterceptor {
	
	public int intercept(Object o);
	public void processProperties(Object o);
}