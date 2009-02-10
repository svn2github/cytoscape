package Command;

import Tunable.Tunable;
import Utils.myURL;


public class URLChoose implements command{

	@Tunable(description = "URL choose",group = {"Import Network File"})
	public myURL url = new myURL("");

	public void execute(){}
	
}