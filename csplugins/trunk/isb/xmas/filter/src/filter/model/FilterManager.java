package filter.model;

import java.util.*;
import filter.view.*;
import javax.swing.*;
import javax.swing.event.SwingPropertyChangeSupport;

public class FilterManager {

  protected static FilterManager DEFAULT_MANAGER;
  protected static FilterView DEFAULT_VIEW;
  protected static JDialog VIEW_DIALOG;

  public static String FILTER_EVENT = "FILTER_EVENT";
  public static String EDITOR_ADDED = "EDITOR_ADDED";
  public static String EDITOR_EVENT = "EDITOR_EVENT";

  protected Map filterTreeMap;
  protected Map filterMap;
  protected Map editorMap;

  /**
   *  PCS support
   */
  protected SwingPropertyChangeSupport pcs = new SwingPropertyChangeSupport(this);

  /**
   * Returns the Default Filter Manager
   */
  public static FilterManager defaultManager () {
    if ( DEFAULT_MANAGER == null ) {
      DEFAULT_MANAGER = new FilterManager();
    }
    return DEFAULT_MANAGER;
  }

  private FilterManager () {
    filterMap = new HashMap();
    filterTreeMap = new HashMap();
    editorMap = new HashMap();
  }

  public static FilterView defaultView () {
    if ( DEFAULT_VIEW == null ) {
      VIEW_DIALOG = new JDialog();
      DEFAULT_VIEW = new FilterView();
      VIEW_DIALOG.getContentPane().add( DEFAULT_VIEW );
    }
    return DEFAULT_VIEW;
  }

  public static void showFilterView () {
    VIEW_DIALOG.show();
  }


  //----------------------------------------//
  // PCS Methods

  /**
   * PCS Support
   */
  public SwingPropertyChangeSupport getSwingPropertyChangeSupport () {
    return pcs;
  }

  public void fireFilterEvent () {
    // we don't pass any objects, so that listeners can choose to get
    // just the Filters, or just the FilterTrees.
    //  System.out.println( "Fire Filter Event" );
    pcs.firePropertyChange( FILTER_EVENT, null, null );
  }

  public void fireEditorEvent () {
    // System.out.println( "Firing Editor Event" );
    pcs.firePropertyChange( EDITOR_EVENT, null, null );
  }


  //----------------------------------------//
  // Editor Methods

  public boolean addEditor ( FilterEditor editor ) {
    if ( editorExists( editor.getFilterID() ) ) {
      return false;
    }
    editorMap.put( editor.toString(), editor );
    //    fireEditorEvent();
    pcs.firePropertyChange( EDITOR_ADDED, null, editor );
    return true;
  }

  public boolean editorExists ( String name ) {
    if ( editorMap.get( name ) == null ) {
      return false;
    } 
    return true;
  }

  public FilterEditor getEditor ( String name ) {
    return ( FilterEditor )editorMap.get( name );
  }

  public void removeEditor ( String name ) {
    editorMap.remove( name );
    fireEditorEvent();
  }

  public Iterator getEditors () {
    ArrayList editor_list = new ArrayList();
    Set keys = editorMap.keySet();
    for ( Iterator i = keys.iterator(); i.hasNext(); ) {
      editor_list.add( editorMap.get( i.next() ) );
    }
    return editor_list.iterator();
  }

  //----------------------------------------//
  // Filter and Filter Tree Methods
  

  public boolean filterExists ( String name ) {
    if ( filterMap.get( name ) == null && filterTreeMap.get( name ) == null ) {
      return false;
    }
    return true;
  }

  public boolean filterTreeExists ( String name ) {
    if ( filterTreeMap.get( name ) == null ) {
      return false;
    }
    return true;
  }

  public Set getFilters ( boolean include_trees ) {
    if ( include_trees ) {
      HashSet filters = new HashSet( filterMap.keySet() );
      Set tree_filters = filterTreeMap.keySet();
      Iterator tfi = tree_filters.iterator();
      while ( tfi.hasNext() ) {
        filters.add( tfi.next() );
      }
      //filters.addAll( tree_filters );
      return filters;
    }
    return filterMap.keySet();
  }

  public Set getFilterTrees () {
    return filterTreeMap.keySet();
  }

  public boolean addFilterTree ( FilterTree tree ) {
    if ( filterTreeExists( tree.toString() ) ) {
      return false;
    }
    filterTreeMap.put( tree.toString(), tree );
    fireFilterEvent();
    return true;
  }
  
  /**
   * Add/Replace
   */
  public boolean addFilter ( Filter filter ) {
    if (  filter == null  ) {
      return false;
    }
    filterMap.put( filter.toString(), filter );
    fireFilterEvent();
    return true;
  }

  public FilterTree getFilterTree ( String name ) {
    return ( FilterTree )filterTreeMap.get( name );
  }

  public Filter getFilter ( String name ) {
    if ( filterTreeExists( name ) ) {
      return ( Filter )filterTreeMap.get( name );
    } 
    return ( Filter )filterMap.get( name );
  }

  public void renameFilter ( String old_name, String new_name ) {
    Filter f = getFilter( old_name );
    removeFilter( old_name );
    addFilter( f );
  }
  
  public void removeFilter ( String name ) {
      filterMap.remove( name );
      fireFilterEvent();

  }

  public void removeFilter ( Filter f ) {
   
    filterMap.remove( f.toString() );
    fireFilterEvent();
  }

  public void removeFilterTree( String name ) {
    
    filterTreeMap.remove( name );
    fireFilterEvent();
  }

  public Filter createFilterFromString ( String desc ) {
    String[] array = desc.split( "," );
    
    if ( array[0].equals( "filter.cytoscape.StringPatternFilter" ) ) {
      System.out.println( "Found String Filter" );
      Filter new_filter = new filter.cytoscape.StringPatternFilter( array[1], array[2], array[3], array[4] );
      addFilter( new_filter );
      return new_filter;
    } else if ( array[0].equals( "filter.cytoscape.NumericAttributeFilter" ) ) {
      System.out.println( "Found Numeric Filter" );
      Filter new_filter = new filter.cytoscape.NumericAttributeFilter( array[1], array[2], array[3], array[4], array[5] );
      addFilter( new_filter );
      return new_filter;
    } else if ( array[0].equals( "filter.cytoscape.NodeTopologyFilter" ) ) {
      System.out.println( "Found Topology Filter" );
      Filter new_filter = new filter.cytoscape.NodeTopologyFilter( array[1], array[2], array[3], array[4] );
      addFilter( new_filter );
      return new_filter;
    } else if ( array[0].equals( "filter.cytoscape.BooleanMetaFilter" ) ) {
      System.out.println( "Found Boolean Filter" );
      Filter new_filter = new filter.cytoscape.BooleanMetaFilter( array[1], array[2], array[3] );
      addFilter( new_filter );
      return new_filter;
    } else if ( array[0].equals( "filter.cytoscape.InteractionFilter" ) ) {
      System.out.println( "Found Interaction Filter" );
      Filter new_filter = new filter.cytoscape.InteractionFilter( array[1], array[2], array[3] );
      addFilter( new_filter );
      return new_filter;
    }


    return null;
  }




}
