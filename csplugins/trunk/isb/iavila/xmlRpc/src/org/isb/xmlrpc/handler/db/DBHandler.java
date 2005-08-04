package org.isb.xmlrpc.handler.db;

import java.io.*;
import java.util.*;

/**
 * TODO: username, password for other methods?
 * @author <a href="mailto:iavila@systemsbiology.org">Iliana Avila-Campillo</a>
 */

public interface DBHandler {

  public static final String CLOSED = "Closed"; // db connection is closed
  public static final String OPEN = "Open"; //db connection is open

  /**
   * Sets the database URL
   *
   * @param db_url the database URL
   * @param props a list of arbitrary string tag/value pairs as connection arguments; 
   * normally at least a "user" and "password" property should be included
   */
  public void setURL (String db_url, Hashtable props);
  
  /**
   * @return the database URL
   */
  public String getURL ();

  /**
   * @return status of the database (CLOSED, OPEN)
   * 
   */
  public String getStatus ();

  /**
   * Updates the data in the database
   *
   * @param props a list of arbitrary string tag/value pairs as connection arguments; 
   * normally at least a "user" and "password" property should be included
   */
  public boolean update (Hashtable props);

  /**
   * Shuts down the DB
   */
  public void shutdown ();

  /**
   * @return whether or not debugging print statements should be output
   */
  public boolean getDebug ();

  /**
   * Sets whether or not debugging print statements should be output
   */
  public void setDebug (boolean debug);
  
}//DBHandler

