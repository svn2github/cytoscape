
package org.isb.bionet.datasource;
import java.sql.*;
import java.util.*;
import org.isb.bionet.datasource.*;
import org.isb.xmlrpc.handler.db.*;

/**
 * A class to optimize the tables in the database that Jung made.
 * TODO: Once we get Jung's code, include these changed into it.
 * @author iavila
 *
 */

public class DBOptimizer {
 
    public static void optimizeProlinks (){
        
//        SQLDBHandler sqlHandler = 
//            new SQLDBHandler("jdbc:mysql://biounder.kaist.ac.kr/prolinks1?user=bioinfo&password=qkdldhWkd",
//                    SQLDBHandler.MYSQL_JDBC_DRIVER);
//        
//        Connection connection = sqlHandler.getConnection();
//        
//        // Get all the table names
//        String tableNamePattern = ".+"; // any character, one or more times 
//        ResultSet rs;
//        try{
//            rs = connection.getMetaData().getTables(null,null,tableNamePattern,null);
//        }catch(Exception e){
//            e.printStackTrace();
//            return;
//        }
//        
//        ArrayList tableNames = new ArrayList();
//        try{
//            while(rs.next()){
//                String table = rs.getString("TABLE_NAME");
//                System.out.println(table);
//                tableNames.add(table);
//            }
//        }catch(Exception e){
//            e.printStackTrace();
//            return;
//        }
//        
//        Iterator it = tableNames.iterator();
//        
//        try{
//            while(it.hasNext()){
//                String tableName = (String)it.next();
//                String sql = "ALTER TABLE " +  tableName + " RENAME to " + tableName + "_temp";
//                sqlHandler.queryAndDump(sql);
//                // Change the types to int:
//                sql = "SELECT INT(gene_id_a) AS gene_id_a, INT(gene_id_b) AS gene_id_b" + 
//                      " INTO TABLE " + tableName +
//                      " FROM " + tableName + "_temp";
//                sqlHandler.queryAndDump(sql);
//                
//                sql = "DROP TABLE " + tableName + "_temp";
//                sqlHandler.queryAndDump(sql);
//                
//                // Delete repeated interactions that only difer in direction
//                //LEFT HERE, how do I remove duplicates?????
//                
//                sql = " DELETE FROM " + tableName +
//                      " WHERE gene_id_a IN("+
//                      "         SELECT e2.gene_id_b " +
//                      "         FROM  " + tableName + " AS e, " + tableName + " AS e2 " +
//                      "         WHERE e.gene_id_a = e2.gene_id_b AND e.gene_id_b = e2.gene_id_a AND e.method = e2.method )";
//                
//                sqlHandler.queryAndDump(sql);
//                
//                // Create index
//                
//            
//            }//while it.hasNext
//        }catch(Exception ex){
//            ex.printStackTrace();
//            return;
//        }    
//        
    }
    public static void optimizeSynonyms (){}
    public static void optimizeKegg (){}
    
    public static void main (String [] args){
        optimizeProlinks();
        optimizeSynonyms();
        optimizeKegg();
    }
}