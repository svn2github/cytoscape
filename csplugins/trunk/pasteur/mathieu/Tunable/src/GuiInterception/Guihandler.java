package GuiInterception;


import java.lang.reflect.Field;

import HandlerFactory.Handler;
import TunableDefinition.Tunable;
import Command.*;
import javax.swing.*;
import Interceptors.*;


public interface Guihandler extends Handler{
	public void handle();
	public JPanel getInputPanel();
	public JPanel getresultpanel();
	public Tunable getTunable();
	public Field getField();
	public Object getObject();
	public JPanel update();
	public void cancel();
}
