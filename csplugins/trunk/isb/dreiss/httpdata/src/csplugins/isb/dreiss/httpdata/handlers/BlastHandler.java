package csplugins.isb.dreiss.httpdata.handlers;

import java.io.*;
import java.util.*;

import csplugins.isb.dreiss.httpdata.xmlrpc.*;
import csplugins.isb.dreiss.httpdata.*;

/**
 * Class <code>BlastHandler</code> 
 *
 * @author <a href="mailto:dreiss@systemsbiology.org">David Reiss</a>
 * @version 1.0
 */
public class BlastHandler {
   public static final String serviceName = "blast";

   boolean useSGDBlast = false;
   File executable;
   File sequenceDataDirectory;
   int invocations = 0;
   Vector sequenceFileNames;
   LocalBlast localBlast;
   
   public BlastHandler( String pathToBlastExe, String sequenceDataDir ) throws Exception {
      File pathToBlastExecutable = new File( pathToBlastExe );
      File sequenceDataDirectory = new File( sequenceDataDir );
      if (!pathToBlastExecutable.canRead() ) 
	 throw new IllegalArgumentException ("cannot read blast executable: " +
					     executable.getPath() );

      if (!sequenceDataDirectory.canRead() ) 
	 throw new IllegalArgumentException ("cannot read sequenceDirectory: " +
					     sequenceDataDirectory.getPath() );

      this.executable = pathToBlastExecutable;
      this.sequenceDataDirectory = sequenceDataDirectory;
      deduceSequenceFileNames();
   }

   public BlastHandler() throws Exception {
      useSGDBlast = true;
   }
   
   private synchronized void deduceSequenceFileNames() {
      File [] allFiles = sequenceDataDirectory.listFiles();
      sequenceFileNames = new Vector();

      for (int f = 0; f < allFiles.length; f ++) {
	 String name = allFiles[ f ].getName();
	 String [] tokens = name.split ("\\.");
	 String choppedName = "";
	 for (int t = 0; t < (tokens.length - 1); t ++ ) {
	    choppedName += tokens[ t ];
	    if ( t < tokens.length - 2 ) choppedName += ".";
	 }
	 if (!sequenceFileNames.contains( choppedName ) )
	    sequenceFileNames.add( choppedName );
      } // for f
   }
   
   public synchronized Vector getSequenceFileNames() {
      return sequenceFileNames;
   }
   
   public synchronized boolean setup (String blastVariant, String sequence, 
		      String sequenceName, String sourceSpecies,
		      String targetSequenceFile) throws Exception {
      if ( ! useSGDBlast ) {
	 localBlast = new LocalBlast (blastVariant, sequence, sequenceName, 
				      sourceSpecies, targetSequenceFile);
	 localBlast.setSequenceFileDirectory( sequenceDataDirectory.getAbsolutePath() + "/" );
	 localBlast.setBlastCommand( executable.getAbsolutePath() );
      } else localBlast = new SGDBlast (blastVariant, sequence, sequenceName, 
					sourceSpecies, targetSequenceFile);
      return true;
   }

   public synchronized boolean setup (String blastVariant, String sequence, 
		      String sequenceName, String sourceSpecies,
		      String targetSequenceFile, double evalue) throws Exception {
      if ( ! useSGDBlast ) {
	 localBlast = new LocalBlast (blastVariant, sequence, sequenceName, 
				      sourceSpecies, targetSequenceFile);
	 localBlast.setSequenceFileDirectory( sequenceDataDirectory.getAbsolutePath() + "/" );
	 localBlast.setBlastCommand( executable.getAbsolutePath() );
      } else localBlast = new SGDBlast (blastVariant, sequence, sequenceName, 
					sourceSpecies, targetSequenceFile);
      localBlast.setEValueThreshold( evalue );
      return true;
   }
   
   public synchronized boolean setMatrix( String newValue ) {
      localBlast.setMatrix( newValue ); return true; }
   public synchronized boolean setCostToOpenGap( int newValue ) {
      localBlast.setCostToOpenGap( newValue ); return true; }
   public synchronized boolean setCostToExtendGap( int newValue ) {
      localBlast.setCostToExtendGap( newValue ); return true; }
   public synchronized boolean run() throws Exception {
      invocations++; localBlast.run(); return true; }
   public synchronized int getInvocationCount() {
      return invocations; }
   public synchronized String getBlastCommand() {
      return localBlast.getActualBlastCommand(); }
   public synchronized String getErrorMessage() {
      return localBlast.getErrorMessage(); }
   public synchronized Hashtable getResults() {
      Hashtable tab = XmlRpcUtils.GetObjectAsStruct( localBlast.getResults() );
      return tab; }
   
   public static void main (String [] args) throws Exception {
      if ( args.length < 3 ) {
	 System.err.println( "Usage: java csplugins.httpdata.handlers.BlastHandler <port> <pathToBlastExecutable> " +
			     "<pathToSequenceDataDirectory>" );
	 System.exit( -1 );
      }

      File executable = new File( args[ 1 ] );
      File dataDirectory = new File( args[ 2 ] );
      if ( ! executable.canRead() ) 
	 throw new IllegalArgumentException( "cannot read blast executable: " + 
					     executable.getPath() );

      if ( ! dataDirectory.canRead() ) 
	 throw new IllegalArgumentException( "cannot read data directory: " + 
					     dataDirectory.getPath() );

      if ( ! dataDirectory.isDirectory() ) 
	 throw new IllegalArgumentException( "data directory is not a directory: " + 
					     dataDirectory.getPath() );

      try {
	 MyXmlRpcServer server = new MyXmlRpcServer( args );
	 server.addService( serviceName, "csplugins.httpdata.BlastHandler", executable.getAbsolutePath(), 
			    dataDirectory.getAbsolutePath() );
      } catch( Exception e ) {
	 System.err.println( "Could not start server: " + e.getMessage() );
      }
   }
}

