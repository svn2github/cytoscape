package integration.readers;

import integration.data.*;
import integration.util.*;
import integration.view.*;

import java.io.*;
import javax.swing.*;
import java.util.*;

public class ReaderMTX {


  JFileChooser chooser;
  String lastFileName = null;

  public ReaderMTX () {

  }

  public DataCube createDataCube ( IntegrationWindow window ) {

    JFileChooser chooser = new JFileChooser( "/users/xmas/CSBI/csplugins/integration");
   chooser.setDialogTitle( "Load MTX File" );
    if( chooser.showOpenDialog(window ) == chooser.APPROVE_OPTION ) {
      lastFileName = chooser.getSelectedFile().toString();
    }
    //Open a file and prepare to send the Buffered Reader to the parser
    BufferedReader br = null;
    try {
       br = new BufferedReader(new FileReader(chooser.getSelectedFile() ) );
      
    } catch ( java.io.IOException ex ) {} 

    
   

    // Store 
    Map data = new HashMap();
    ArrayList genes  = new ArrayList();
    ArrayList experiments = new ArrayList();
    ArrayList types = new ArrayList();

    boolean fileEnd = false;
    String currentLine;

    String firstNode, secondNode;
    String burn;

    while ( !fileEnd ) {
      
       try { 
         
         currentLine =  br.readLine();
        
         if ( currentLine == null ) {
         
           // Detects File End
          fileEnd = true;
        
         } else { 

           StringTokenizer t  = new StringTokenizer(currentLine);
           if ( t.countTokens() == 2 ) {
             // this is the top line
             while ( t.hasMoreTokens() ) {
               types.add( t.nextToken() );
             }
           } else {
             String first_column = t.nextToken();
             int count = t.countTokens();
             int exp =  ( count - 3 ) / 2;
             // System.out.println( "there are: "+count+" tokesns and "+exp+ "experiments");
             //System.out.println( "FirstToken: "+first_column );
             if ( first_column.equals( "GENE" ) ) {
               burn = t.nextToken();
               
               int c = 0;
                for ( int i = 2; i <= exp + 2; ++i ) {
                  String toke = t.nextToken();
                  experiments.add(  toke );
                  System.out.println(c+ ": "+ toke+" added to experiments. " );
                  c++;
               }
                continue;
             } else if ( first_column.startsWith( "NumSig" ) ) {
               continue;
             } else {
               String gene = t.nextToken();
               genes.add( gene );
               // System.out.println( "Gene: "+gene+" added. ");
               ArrayList gene_ratio = new ArrayList( experiments.size() );
               ArrayList gene_lamda = new ArrayList( experiments.size() );

               for (  int i = 3; i <= exp + 2; ++i ) {
                 String toke =  t.nextToken();
                 //System.out.println( "Toke: "+toke+ " for: "+experiments.get( i - 2)  );
                 gene_ratio.add( new Double( toke ) );
               }
               // System.out.println( "NUm of Experiments: "+experiments.size() );
               for ( int i = exp + 2; i < count ; ++i ) {
                 gene_lamda.add( new Double( t.nextToken() ) );
               }
               data.put( gene, new Object[] { gene_ratio, gene_lamda } );
             }
                  
           }
         }
         
       } catch ( IOException excp ) {}
    } //while !fileEnd


    // OK, I guess all the data is now loaded, kinda...
    // gene names
    String[] slice_names = new String[ genes.size() ];
    Iterator g = genes.iterator();
    int gc = 0;
    while( g.hasNext() ) {
      slice_names[gc] = ( String )g.next();
      gc++;
    }

    // Experiment names
    String[] row_names = new String[ experiments.size() ];
    Iterator e = experiments.iterator();
    int ec = 0;
    while( e.hasNext() ){
      row_names[ ec ] = ( String )e.next();
      ec++;
    }
    // DataType Names
    String[] column_names = new String[ types.size() ];
    Iterator t = types.iterator();
    int tc =0;
    while ( t.hasNext() ){
      column_names[ tc ] =  ( String )t.next();
      tc++;
    }

    Object[][][] cube = new Object[ genes.size() ][ experiments.size() ][ types.size()  ];

    
    g = genes.iterator();
    gc = 0;
    while ( g.hasNext() ) {
      String gene_name = ( String )g.next();
      Object[] lists = ( Object[] )data.get( gene_name );
      List ratio = ( List )lists[0];
      List lambda = ( List )lists[1];


      e = experiments.iterator();
     
      ec = 0;
      while ( e.hasNext() ) {
        // System.out.println( "Cube: "+gc+ " " +ec+ " set to: "+ratio.get( ec )+ " and "+lambda.get( ec ) );
        try {
          cube[ gc ][ ec ][ 0 ] = ratio.get( ec );
        } catch ( Exception ex ) {
          cube[ gc ][ ec ][ 0 ] = new Double( 0 );
        }
        try {
          cube[ gc ][ ec ][ 1 ] = lambda.get( ec );
        } catch ( Exception ex) {
          //System.out.println( "Cube: "+gc+ " " +ec );
          cube[ gc ][ ec ][ 1 ] = new Double( 0 );
          //ex.printStackTrace();
        }
        e.next();
        ec++;
      }
     
      gc++;
    }
  
    return new DataCube( cube, slice_names, row_names, column_names );

  }



} // class ReaderMTX
