package org.cytoscape.command;

import org.cytoscape.work.*;
import org.cytoscape.work.util.*;

public class URLChoose implements Command{

	@Tunable(description = "URL choose",group = {"Import Network File"})
	public myURL url = new myURL("");

	public void execute(){}
	
}