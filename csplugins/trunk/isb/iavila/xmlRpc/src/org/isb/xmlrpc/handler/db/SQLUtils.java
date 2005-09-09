package org.isb.xmlrpc.handler.db;

import java.sql.*;

public class SQLUtils {
    
    /**
     * @param rs
     * @return an integer that is located on the 1st column of rs, -1 if there is an exception
     */
    public static int getInt (ResultSet rs){
        try{
            rs.next();
            int num = rs.getInt(1);
            return num;
        }catch(SQLException e){
            e.printStackTrace();
            return -1;
        }
        
    }//getInt
    
}