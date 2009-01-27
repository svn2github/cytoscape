package GuiInterception;

import java.lang.reflect.Field;
import HandlerFactory.Handler;
import Tunable.Tunable;
import javax.swing.*;

public interface Guihandler extends Handler{
	
	public void handle();
	
	public JPanel getPanel();
	public JPanel getOutputPanel();
	
	public Tunable getTunable();
	public Field getField();
	public Object getObject();

}
