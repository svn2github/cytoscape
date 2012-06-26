package org.cytoscape.cytobridge.rpc;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.xmlrpc.XmlRpcException;

/**
 *
 * @author Jan Bot
 */
public class RPCLogger {

    public static enum RPCLogLevel {
        DEBUG(10, "DEBUG"),
        MESSAGE(20, "MESSAGE"),
        WARNING(30, "WARNING"),
        ERROR(40, "ERROR");

        int level;
        String str;
        RPCLogLevel(int level, String str) {
            this.level = level;
            this.str = str;
        }
    }

    public static Map<String, RPCLogLevel> RPCLogMap =
            Collections.unmodifiableMap(new HashMap<String, RPCLogLevel>() {
        {
            put("DEBUG", RPCLogLevel.DEBUG);
            put("MESSAGE", RPCLogLevel.MESSAGE);
            put("WARNING", RPCLogLevel.WARNING);
            put("ERROR", RPCLogLevel.ERROR);
        }
    });

    private RPCLogger(){

    }

    public static void setLogLevel(RPCLogLevel l) {
        level = l;
    }

    public static void setLogLevel(String logStr) {
        level = RPCLogMap.get(logStr);
    }

    public static void addMessage(RPCLogLevel l, String message) {
        if(l.level >= level.level) {
            //Cytoscape.getDesktop().setStatusBarMsg(message);
        }
    }

    public static boolean setRPCLogLevel(String l) throws XmlRpcException {
        if(RPCLogMap.containsKey(l)) {
            level = RPCLogMap.get(l);
        }
        else {
            throw new XmlRpcException("Log level does not exist.");
        }
        return true;
    }

    public static RPCLogLevel getRPCLogLevel() {
        return level;
    }

    private static RPCLogLevel level = RPCLogLevel.ERROR;
}

