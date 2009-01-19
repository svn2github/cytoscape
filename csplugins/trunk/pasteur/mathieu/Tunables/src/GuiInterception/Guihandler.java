package GuiInterception;


import java.lang.reflect.Field;
import HandlerFactory.Handler;
import Tunable.Tunable;
import javax.swing.*;

public interface Guihandler extends Handler{
	public void handle();
	public JPanel getInputPanel();
	public Tunable getTunable();
	public Field getField();
	public Object getObject();
	public JPanel getOutputPanel();
}
