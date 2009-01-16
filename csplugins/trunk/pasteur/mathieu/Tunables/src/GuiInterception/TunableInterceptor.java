package GuiInterception;


import Command.*;


public interface TunableInterceptor {
	
	public void intercept(command command);
	public void GetInputPanes();
	public void Display();
	public void Save();	
	public void ProcessProperties();
	public void addProperties();
}