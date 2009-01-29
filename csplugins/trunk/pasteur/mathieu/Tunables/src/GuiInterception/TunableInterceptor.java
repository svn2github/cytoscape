package GuiInterception;



public interface TunableInterceptor {
	
	public int intercept(Object o);
	public void processProperties(Object o);
	public void interceptAndReinitializeObjects(Object o);
	public void interceptandDisplayResults(Object obj);
}