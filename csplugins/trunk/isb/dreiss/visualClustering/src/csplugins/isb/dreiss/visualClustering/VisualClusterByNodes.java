package csplugins.isb.dreiss.visualClustering;

import javax.swing.*;
import java.util.*;
import java.io.*;

import cytoscape.*;
import cytoscape.view.*;
import cytoscape.plugin.*;

import djr.util.bio.Sequence;
import csplugins.isb.dreiss.cytoTalk.CytoTalkHandler;

/**
 * Class <code>VisualClusterByNodes</code>.
 *
 * @author <a href="mailto:dreiss@systemsbiology.org">David Reiss</a>
 * @version 1.9962 (Tue Aug 26 01:44:23 PDT 2003)
 */
public class VisualClusterByNodes extends VisualClustering {

   public VisualClusterByNodes( CytoscapeDesktop cWindow, CytoTalkHandler handler ) {
      init( cWindow, handler);
   }

   protected void init( CytoscapeDesktop cWindow, JMenu menu, CytoTalkHandler handler ) {
      this.cWindow = cWindow;
      this.handler = handler;
   }

   public void doCallback( String attributes[], AttributeChooser chooser ) {
      chooser.hide();
      //restoreSavedValues();
      createEdgesBetweenAllNodesWithSharedAttributes( attributes, chooser );
   }

   public void createEdgesBetweenAllNodesWithSharedAttributes( String attributes[],
							       AttributeChooser chooser ) {
      boolean reLayout = chooser.doRelayout();
      boolean hideOthers = chooser.hideOthers();
      if ( reLayout && hideOthers ) handler.hideAllEdges();
      boolean deleteEdgesLater = chooser.deleteEdges();
      Vector addedEdges = null;
      if ( deleteEdgesLater ) addedEdges = new Vector();
      boolean restoreOthers = chooser.restoreOthers();

      for ( int i = 0; i < attributes.length; i ++ ) {
	 Vector newEdges = addCategoryEdgesBetweenNodes( attributes[ i ], chooser, false );
	 if ( deleteEdgesLater ) addedEdges.addAll( newEdges );
      }

      for ( int i = 0; i < attributes.length; i ++ ) {
	 boolean isRequired = chooser.getCombineViaAnd( attributes[ i ] );
	 if ( ! isRequired ) continue;
	 addCategoryEdgesBetweenNodes( attributes[ i ], chooser, true );
      }

      //while ( mAction != null && ! mAction.done() ) {
      // System.err.println("HERE: "+(mAction==null)+" "+mAction.done());
      // try { Thread.sleep( 100 ); } catch ( Exception e ) { };
      //}

      if ( reLayout ) {
	 /*if ( edgeWeights != null ) {
	    Layouter layouter = cWindow.getLayouter();
	    if ( layouter instanceof OrganicLayouter ) {
	       DataProviderAdapter edgeLengthData = new DataProviderAdapter() {
		     public int getInt( Object o ) {
			Double dval = (Double) edgeWeights.get( o );
			if ( dval == null ) return 200;
			double val = dval.doubleValue();
			int out = (int) ( 100.0 * val ) + 1;
			//System.err.println("HEREX: "+o+" "+val+" "+out);
			return out;
		     } };
	       graph.addDataProvider( OrganicLayouter.PREFERRED_EDGE_LENGTH_DATA, 
				      edgeLengthData );
	    }
	    }*/
	 handler.relayoutGraph();
	 if ( hideOthers && restoreOthers ) handler.unhideAllEdges();
      }
      if ( deleteEdgesLater ) { // Hide if not too many; delete if there are a lot
	 for ( int i = 0, sz = addedEdges.size(); i < sz; i ++ ) {
	    if ( sz < 1000 ) handler.hideEdge( (String) addedEdges.get( i ) );
	    else handler.removeEdge( (String) addedEdges.get( i ) );
	 }
      }
      handler.redrawGraph();
   }

   protected Vector addCategoryEdgesBetweenNodes( final String attributeName, 
						  final AttributeChooser chooser,
						  final boolean toBeRemoved ) {
      final boolean deleteEdgesLater = chooser.deleteEdges();
      final Vector addedEdges = deleteEdgesLater ? new Vector() : null;

      handler.setWaitCursor();

      final String nodes[] = (String[]) handler.getSelectedNodes().toArray( new String[ 0 ] );
      final int nnodes = nodes.length;
      final String nodeNames[] = nodes;

      boolean isExprAttr = false, isHomAttr = false;
      //ExpressionData dat = null;
      double exprLevels[][] = null;
      Map seqMap = null;
      int corrType = -1; // 1 if DOTNORM; 2 if PEARSON; 3 if EQUIVALENT
      if ( attributeName.equals( MRNA_ATTRIBUTE ) ) {
	 //System.err.println("CO-EXPRESSION");
	 isExprAttr = true;
	 //dat = cWindow.getExpressionData();
	 corrType = chooser.getCorrelationType( attributeName );
	 exprLevels = new double[ nnodes ][];
      } else if ( attributeName.equals( HOMOLOGY_ATTRIBUTE ) ) {
	 //System.err.println("HOMOLOGY");
	 isHomAttr = true;
	 seqMap = new HashMap();
	 for ( int i = 0; i < nnodes; i ++ ) {
	    String seq = (String) handler.getNodeAttribute( nodeNames[ i ], "sequence" ).get( 0 );
	    if ( seq != null && seq.length() > 0 && ! "UNKNOWN".equalsIgnoreCase( seq ) ) {
	       seqMap.put( nodeNames[ i ], seq );
	    }
	 }
      }

      final boolean isExpressionAttr = isExprAttr, isHomologyAttr = isHomAttr;
      //final ExpressionData data = dat;
      final double expressionLevels[][] = exprLevels;
      final int correlationType = corrType;
      final Map seqsMap = seqMap;

      final double scaling = chooser.getScaling( attributeName );
      final double range = chooser.getRange( attributeName );
      final boolean relative = chooser.getRelative( attributeName );
      final boolean closest = chooser.closestOnly();
      final boolean isRequired = chooser.getCombineViaAnd( attributeName );
      //System.err.println("HEREX: "+attributeName+" "+isHomologyAttr+" "+isRequired);

      //edgeWeights = null;
      final Object matchedValue[] = new Object[ 1 ];

      for ( int i = 0; i < nnodes; i ++ ) { 
	 double bestScore = Double.MAX_VALUE;
	 int bestNode = -1;
	 for ( int j = i + 1; j < nnodes; j ++ ) { 
	    double score = 2.0;
	    if ( isExpressionAttr ) {
	       score = getNodeExpressionComparison( i, j, nodeNames, /*data,*/ range,
						    expressionLevels, correlationType,
						    matchedValue );
	    } else if ( isHomologyAttr ) {
	       score = getNodeHomologyComparison( i, j, nodeNames, range, seqsMap,
						  matchedValue );
	    } else {
	       score = getNodeComparison( i, j, nodeNames, attributeName, range, 
					  relative, matchedValue );
	    }
	    if ( closest && score < bestScore ) {
	       bestScore = score;
	       bestNode = j;
	    }
	    if ( ! closest && score <= 1.0 && ! toBeRemoved ) {
	       String node1 = nodes[ i ], node2 = nodes[ j ];
	       String newEdge = handler.createEdge( node1, attributeName + "=" + matchedValue[ 0 ], node2 );
	       //if ( edgeWeights == null ) edgeWeights = new HashMap();
	       //edgeWeights.put( newEdge, new Double( score ) );
	       if ( deleteEdgesLater ) addedEdges.add( newEdge );
	       handler.setEdgeAttribute( newEdge, "VisualClustering", attributeName );
	    } else if ( isRequired && score > 1.0 && toBeRemoved ) { // This edge is required so remove all other edges
	       String node1 = nodes[ i ], node2 = nodes[ j ];
	       Vector edges = handler.getAllEdges();
	       for ( int k = 0, sz = edges.size(); k < sz; k ++ ) {
		  String e = (String) edges.get( k );
		  String test = handler.getConnectedEdge( node1, node2 );
		  if ( test != null && test.indexOf( "(" + attributeName + "=" ) >= 0 ) continue;
		  if ( handler.hasEdgeAttribute( test, "VisualClustering" ) ) handler.removeEdge( e );
	       }
	    }
	 }

	 if ( closest && bestNode >= 0 && bestScore <= 1.0 && ! toBeRemoved ) {
	    int j = bestNode;
	    String node1 = nodes[ i ], node2 = nodes[ j ];
	    String newEdge = handler.createEdge( node1, attributeName + "=" + matchedValue[ 0 ], node2 );
	    //if ( edgeWeights == null ) edgeWeights = new HashMap();
	    //edgeWeights.put( newEdge, new Double( bestScore ) );
	    if ( deleteEdgesLater ) addedEdges.add( newEdge );
	    handler.setEdgeAttribute( newEdge, "VisualClustering", attributeName );
	 }
      }

      //mAction.setProgress( nnodes + 1, nnodes, "Done" );
      //} } ).start();
      handler.setDefaultCursor();
      return addedEdges;
   }

   /** Return 0.0 if exactly the same, up to 1.0, and >1.0 for no edge. */
   protected double getNodeComparison( int i, int j, String nodeNames[], 
				       String attributeName, double range, 
				       boolean relative, Object matchedValue[] ) {
      double bestScore = 2.0;
      Vector values1 = handler.getNodeAttribute( nodeNames[ i ], attributeName );
      if ( values1 == null ) return bestScore;
      Vector values2 = handler.getNodeAttribute( nodeNames[ j ], attributeName );
      if ( values2 == null ) return bestScore;
      for ( int c1 = 0, sz1 = values1.size(); c1 < sz1; c1 ++ ) {
	 Object value1 = values1.get( c1 );
	 if ( value1 == null || value1.toString().indexOf( "unknown" ) > 0 ) continue;
	 for ( int c2 = 0, sz2 = values2.size(); c2 < sz2; c2 ++ ) {
	    Object value2 = values2.get( c2 );
	    if ( value2 == null || value2.toString().indexOf( "unknown" ) > 0 ) continue;
	    double score = compareValues( value1, value2, range, relative );
	    if ( score <= 1.0 && score < bestScore ) {
	       bestScore = score;
	       matchedValue[ 0 ] = value1;
	    }
	 }
      }
      return bestScore;
   }

   protected double getNodeExpressionComparison( int i, int j, String nodeNames[], 
						 /*ExpressionData data,*/ double range,
						 double expressionLevels[][],
						 int correlationType, 
						 Object matchedValue[] ) {
      if ( expressionLevels[ i ] == null ) expressionLevels[ i ] = 
					      getExpressionLevels( nodeNames[ i ]/*, data*/ );
      if ( expressionLevels[ i ] == null || expressionLevels[ i ].length <= 1 ) return 2.0;
      if ( expressionLevels[ j ] == null ) expressionLevels[ j ] = 
					      getExpressionLevels( nodeNames[ j ]/*, data*/ );
      if ( expressionLevels[ j ] == null || expressionLevels[ j ].length <= 1 ) return 2.0;
      double coreg = 0.0;
      //String key1 = nodeNames[ i ] + "_" + nodeNames[ j ];
      //String key2 = nodeNames[ j ] + "_" + nodeNames[ i ];
      //if ( coexpressionLevels == null ) coexpressionLevels = new HashMap();
      //Double Coreg = (Double) coexpressionLevels.get( key1 );
      //if ( Coreg == null ) Coreg = (Double) coexpressionLevels.get( key2 );
      //if ( Coreg == null ) {
      coreg = getCorrelation( expressionLevels[ i ], expressionLevels[ j ],
			      correlationType );
      //coexpressionLevels.put( key1, new Double( coreg ) );
      //coexpressionLevels.put( key2, new Double( coreg ) );
      //} else coreg = Coreg.doubleValue();
      matchedValue[ 0 ] = new Double( (double) (int) ( coreg * 100.0 ) / 100.0 );
      double out = coreg >= range ? 1.0 - ( coreg - range ) / ( 1.0 - range ) : 2.0;
      //System.err.println("HERE3: "+nodeNames[i]+" "+nodeNames[j]+" "+coreg+" "+out);
      return out;
   }

   protected double getNodeHomologyComparison( int i, int j, String nodeNames[], 
					       double range, Map seqsMap,
					       Object matchedValue[] ) {
      String s1 = (String) seqsMap.get( nodeNames[ i ] );
      if ( s1 == null ) s1 = (String) seqsMap.get( nodeNames[ i ].toUpperCase() );
      if ( s1 == null ) return 2.0;
      String s2 = (String) seqsMap.get( nodeNames[ j ] );
      if ( s2 == null ) s2 = (String) seqsMap.get( nodeNames[ j ].toUpperCase() );
      if ( s2 == null ) return 2.0;

      double score = 0.0;
      score = Sequence.GetMatchScore( s1, s2 ) * ( s1.length() + s2.length() );
                  
      if ( score < range ) {
	 return 2.0;
      } else { // Scale from range => 1000.0 (all matches better than that are equivalent)
	 matchedValue[ 0 ] = new Integer( (int) score );
	 if ( score > 1000.0 ) score = 1000.0;
	 double out = 1.0 - ( score - range ) / ( 1000.0 - range );
	 return out;
      }
   }

   /** Return 0.0 if exactly the same, up to 1.0, and >1.0 for no edge. */
   protected double compareValues( Object value1, Object value2, double range, 
				   boolean relative ) {
      // It's a string or boolean:
      if ( range == 0.0 ) return value1.equals( value2 ) ? 0.0 : 2.0; 

      if ( value1.getClass().equals( java.lang.Integer.class ) ) {
	 double diff = (double) Math.abs( ( (Integer) value1 ).intValue() - 
					  ( (Integer) value2 ).intValue() );
	 return diff <= range ? diff/range : 2.0;
      } else if ( value1.getClass().equals( java.lang.Double.class ) ) {
	 double diff = Math.abs( ( (Double) value1 ).doubleValue() - 
				 ( (Double) value2 ).doubleValue() );
	 return diff <= range ? diff/range : 2.0;
      }
      return 2.0;
   }

   protected double[] getExpressionLevels( String name/*, ExpressionData data*/ ) {
      /*Vector out = data.getMeasurements( name );
      if ( out != null ) return convertExpressionLevelsToData( out );
      if ( cWindow == null ) return convertExpressionLevelsToData( out );
      BioDataServer bds = cWindow.getBioDataServer();
      if ( bds == null ) return convertExpressionLevelsToData( out );
      String species[] = cWindow.getAllSpecies();

      for ( int i = 0; i < species.length; i ++ ) {
	 String common = bds.getCommonName( species[ i ], name );
	 if ( common != null ) {
	    String commons[] = bds.getAllCommonNames( species[ i ], common );
	    if ( commons == null || commons.length <= 0 ) commons = new String[] { common };
	    for ( int j = 0; j < commons.length; j ++ ) {
	       out = data.getMeasurements( commons[ j ] );
	       if ( out != null ) return convertExpressionLevelsToData( out );
	       String canon = bds.getCanonicalName( species[ i ], commons[ j ] );
	       out = data.getMeasurements( canon );
	       if ( out != null ) return convertExpressionLevelsToData( out );
	    }
	 }
      }
      return convertExpressionLevelsToData( out );
      */
      return null;
   }

   protected double[] convertExpressionLevelsToData( Vector levels ) {
      /*if ( levels == null ) return new double[ 0 ];
      int sz = levels.size();
      double out[] = new double[ sz ];
      for ( int i = 0; i < sz; i ++ )
	 out[ i ] = ( (mRNAMeasurement) levels.get( i ) ).getRatio();
	 return out;*/
      return null;
   }

   protected double getCorrelation( double a[], double b[], int correlationType ) {
      if ( a.length != b.length ) return 0.0;
      double out = 0.0;
      double norm1 = 0.0, norm2 = 0.0, norm1a = 0.0, norm2a = 0.0;
      int sz = a.length;
      if ( correlationType == 1 ) { // dotnorm
	 for ( int i = 0; i < sz; i ++ ) {
	    norm1 += a[ i ] * a[ i ]; norm2 += b[ i ] * b[ i ];
	    out += a[ i ] * b[ i ];
	 }
	 return out / Math.sqrt( norm1 * norm2 );
      } else if ( correlationType == 2 ) { // pearson
	 for ( int i = 0; i < sz; i ++ ) { norm1 += a[ i ]; norm2 += b[ i ]; }
	 norm1 /= (double) sz; norm2 /= (double) sz; // means
	 for ( int i = 0; i < sz; i ++ ) { 
	    double aa = a[ i ] - norm1, bb = b[ i ] - norm2;
	    out += aa * bb; norm1a += aa * aa; norm2a += bb * bb; 
	 }
	 return Math.abs( out / Math.sqrt( norm1a * norm2a ) );
      } else if ( correlationType == 3 ) { // equivalence (dumb right now)
	 for ( int i = 0; i < sz; i ++ ) out += Math.abs( a[ i ] ) - Math.abs( b[ i ] );
	 out = (double) sz / out;
      }
      return out;
   }

   /*protected void performSave() { 
      if ( coexpressionLevels != null || homologyValues != null ) {
	 String outFile = null;
	 String currDir = System.getProperty( "user.dir" );
	 JFileChooser fd = new JFileChooser( currDir );
	 fd.setDialogTitle( "Enter file to save information into:" );
	 fd.setDialogType( JFileChooser.SAVE_DIALOG );
	 int returnVal = fd.showSaveDialog( cWindow ); 
	 if ( returnVal == JFileChooser.APPROVE_OPTION )
	    outFile = fd.getSelectedFile().getAbsolutePath();
	 if ( ! outFile.endsWith( ".gz" ) && ! outFile.endsWith( ".GZ" ) )
	    outFile += ".gz";
	 try {
	    OutputStream os = djr.util.MyUtils.OpenOutputFile( outFile );
	    ObjectOutputStream out = new ObjectOutputStream( os );
	    out.writeObject( coexpressionLevels );
	    out.writeObject( homologyValues );
	    out.flush(); out.close();
	 } catch( Exception e ) { e.printStackTrace(); }
      }
   }

   protected void restoreSavedValues() {
      if ( cWindow.getConfiguration().getProperties().
	   get( "plugin.visualClustering.savedData" ) == null ) return;
      String url = ( (String) cWindow.getConfiguration().getProperties().
		     get( "plugin.visualClustering.savedData" ) ).trim();
      String fname = url;
      boolean isFile = false;
      String flc = url.trim().toLowerCase();
      if ( flc.startsWith( "file://" ) || ! djr.util.MyUtils.IsURL( url ) ) isFile = true;
      if ( isFile ) {
	 fname = url;
	 if ( flc.startsWith( "file://" ) ) fname = url.substring( "file://".length() );
	 fname = new File( fname ).getAbsolutePath();
	 if ( ! fname.startsWith( File.separator ) &&
	      ! fname.startsWith( (String) System.getProperty( "user.dir" ) ) ) 
	    fname = System.getProperty( "user.dir" ) + File.separator + fname;
	 if ( ! new File( fname ).exists() ) return;
      } else {
	 fname = url;
      }
      
      try {
	 InputStream is = djr.util.MyUtils.OpenFile( fname );
	 ObjectInputStream in = new ObjectInputStream( is );	 
	 coexpressionLevels = (Map) in.readObject();
	 homologyValues = (Map) in.readObject();
      } catch( Exception e ) { e.printStackTrace(); }
   }
   */
}
