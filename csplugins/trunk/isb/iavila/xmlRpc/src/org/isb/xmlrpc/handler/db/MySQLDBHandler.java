package org.isb.xmlrpc.handler.db;

import java.io.*;
import java.util.*;
import java.sql.*;

/**
 * @author <a href="mailto:iavila@systemsbiology.org">Iliana Avila-Campillo</a>
 */

public class MySQLDBHandler implements DBHandler {


  protected String url;
  protected Connection connection;
  protected boolean debug = false;
  protected static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
  
  /**
   * Constructor
   */
  public MySQLDBHandler (){
    this(null);
  }//MySQLDBHandler

  /**
   * @param db_url the database url
   */
  public MySQLDBHandler (String db_url){
    if(loadDriver()){
      setURL(db_url);
    }
     // If the JVM shuts-down, make sure the connection is closed
    Runtime.getRuntime().addShutdownHook( 
               new Thread() { 
                 public void run() {
                   try { 
                     MySQLDBHandler.this.shutdown(); 
                   } catch (Exception e) { }; 
                 }//run 
               } );
  }//MySQLDBHandler

  /**
   * Loads the JDBC driver for mySQL
   *
   * @return true if loaded, false is there was an exception
   */
  protected boolean loadDriver (){
    
    boolean loaded = true;

    try {
      // load the mySQL JDBC driver
      Class.forName(JDBC_DRIVER).newInstance();
      loaded = true;
    }catch (Exception e){
      e.printStackTrace();
      loaded = false;
    }
    
    return loaded;
  
  }//loadDriver
  
  /**
   * 	For now, args are ignored. Calls setURL(db_url).
   */
  public void setURL (String db_url, Properties args){
	  setURL(db_url);
  }//setURL

  /**
   * Sets the url and connects to it
   *
   * @param db_url JDBC URL of the form jdbc:mysql:<db subname and params>
   */
  public void setURL (String db_url){
    if(db_url == null){
      return;
    }
    this.url = db_url;
    
    // In case that there is already a connection:
    shutdown();
    
    try{
      // Make a connection to the db
      this.connection = DriverManager.getConnection(this.url);
      // Make sure the connection is OK
      if(!this.connection.isClosed()){
        System.out.println("Successfully connected to mySQL database " + this.url);
      }
      
    }catch (Exception e){
      e.printStackTrace();
    }
   
  }//setURL
  
  /**
   * @return the URL for this db
   */
  public String getURL (){
    return this.url;
  }//getURL

  /**
   * Closes the connection to the DB
   */
  public void shutdown (){
    try{
      if(this.connection != null && !this.connection.isClosed()){
        this.connection.close();
      }
    }catch (SQLException se){
      se.printStackTrace();
    }
  }//shutdown

  /**
   * @return the status of this DB (CLOSED, OPEN)
   */
  public String getStatus (){
	  try {
		  if(this.connection == null || this.connection.isClosed()){
			  return DBHandler.CLOSED;
		  }
	  }catch (SQLException e){
		  // Database access error
		  e.printStackTrace();
		  //TODO: Have a ACCESS_ERROR return value???
		  return DBHandler.CLOSED;
	  }
    return DBHandler.OPEN;
  }//getStatus
  
  
  /**
   * Updates the data in the database
   *
   * @param props a list of arbitrary string tag/value pairs as connection arguments; 
   * normally at least a "user" and "password" property should be included
   */
  public boolean update (Properties props){
    return false; //for now
  }//update
  
  /**
   * @return whether or not debugging print statements should be output
   */
  public boolean getDebug (){
    return this.debug;
  }//getDebug

  /**
   * Sets whether or not debugging print statements should be output
   */
  public void setDebug (boolean d){
    this.debug = d;
  }//setDebug

}//MySQLDBHandler
