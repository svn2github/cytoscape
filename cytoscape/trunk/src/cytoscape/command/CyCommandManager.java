 /*
 File: CyCommandManager.java

 Copyright (c) 2009, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies
 - University of California San Francisco

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

package cytoscape.command;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

/**
 * CyCommandManager is a singleton Cytoscape class that provides a global registry
 * of {@link CyCommandHandler}.  Each CyCommandHandler is added to the registry through the register
 * method.  This method registers both the command name (e.g. "view layout") as well as
 * the class name (e.g. ViewLayoutCommand).  Both may be accessed by calling the getCommand
 * method.
 */
public class CyCommandManager {
       private static HashMap<String, CyCommandHandler> classMap = new HashMap();
       private static HashMap<String, CyCommandHandler> nameMap = new HashMap();

       /**
        * register a new CyCommandHandler.
        *
        * @param handler the handler we want to register
        * @throws Exception if the command is already registered
        */
       public static void register(CyCommandHandler handler) throws Exception {
               if (handler == null) return;

               // Register the class name
               if (classMap.containsKey(handler.getClass().getName()))
                       throw new Exception("Handler "+handler.getClass().getName()+" is already registered");

               // Register the command name
               if (nameMap.containsKey(handler.getHandlerName()))
                       throw new Exception("Handler "+handler.getHandlerName()+" is already registered");

               classMap.put(handler.getClass().getName(), handler);
               nameMap.put(handler.getHandlerName(), handler);
       }

       /**
        * return a CyCommandHandler by either name or class name.
        *
        * @param name either the name of the class or command
        * @return the handler, or null if a handler with name or class doesn't exist
        */
       public static CyCommandHandler getHandler(String name) {
               if (classMap.containsKey(name))
                       return classMap.get(name);

               if (nameMap.containsKey(name))
                       return nameMap.get(name);

               return null;
       }

       /**
        * Get the list of all handlers that are currently registered.
        *
        * @return the list of handlers that are currently registered
        */
       public static List<CyCommandHandler> getHandlerList() {
               List<CyCommandHandler> list = new ArrayList();
               list.addAll(classMap.values());
               return list;
       }

       /**
        * Unregister a command handler
        *
        * @param handler the command handler to unregister
        */
       public static void unRegister(CyCommandHandler handler) {
               classMap.remove(handler.getClass().getName());
               nameMap.remove(handler.getHandlerName());
       }
}

