package HandlerFactory;


import java.lang.reflect.*;

import Properties.PropertiesImpl;
import TunableDefinition.Tunable;


//Lister toutes les possibles fonctions a effectuer sur les handlers suivant les types

public interface HandlerFactory<H extends Handler>{
	
	 H getHandlerType(Field f, Object o, Tunable t);
	 
}