package GuiInterception;


import Command.*;


public interface TunableInterceptor {
	
	public void intercept(command command);
	public void Process();
	public void Display();
	public void Save();	
	public void Cancel();
	public void ProcessProperties();
	public void addProperties();
}