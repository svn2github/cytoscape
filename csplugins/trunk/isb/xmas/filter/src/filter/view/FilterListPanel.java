package filter.view;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import filter.model.*;
import javax.swing.border.*;
import java.beans.*;
import javax.swing.event.SwingPropertyChangeSupport;
 
import ViolinStrings.Strings;

/**
 * A FilterListPanel will
 * be able to search through its contents based on the Filter title using
 * wildcard search
 */
public class FilterListPanel 
  extends JPanel 
  implements ActionListener,
             PropertyChangeListener,
             ListSelectionListener {

  JTextField searchField;
  JList filterList;
  JList filterTreeList;
  JList compoundList;

  DefaultListModel filterModel;
  DefaultListModel filterTreeModel;
  DefaultListModel compoundModel;

  public static int SHOW_SEPARATE = 0;
  public static int SHOW_TOGETHER = 1;
  public static int SHOW_TREES = 2;
  public static int SHOW_FILTERS = 3;

  public static String FILTER_SELECTED = "FILTER_SELECTED";

  int type;
  Object lastSelectedFilter;
  
  protected SwingPropertyChangeSupport pcs = new SwingPropertyChangeSupport( this );


  public FilterListPanel ( int type ) {
    super();
    this.type = type;
    initialize();
  }



  protected void initialize () {
    
    // register ourselves as a listener to FilterManager, so that
    // we can update the lists accordingly
    FilterManager.defaultManager().getSwingPropertyChangeSupport().addPropertyChangeListener( this );

    JPanel searchPanel = new JPanel();
    searchPanel.setBorder( new TitledBorder( "Search Filters" ) );
    //searchPanel.add( new JLabel( "Search Filters: " ) );
    searchField = new JTextField( 10 );
    searchField.addActionListener( this );
    searchPanel.add( searchField );
    searchPanel.add( new JButton (new AbstractAction( "Go!" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  // do the search
                  performSearch();
                }
              } ); } } ) );
    searchPanel.add( new JButton (new AbstractAction( "Reset" ) {
        public void actionPerformed ( ActionEvent e ) {
          // Do this in the GUI Event Dispatch thread...
          SwingUtilities.invokeLater( new Runnable() {
              public void run() {
                // do the search
                updateLists();
              }
            } ); } } ) );

    JPanel listPanel = new JPanel();
    listPanel.setBorder( new TitledBorder( "Available Filters" ) );
    if ( type == SHOW_SEPARATE ) {
      filterList = new JList();
      filterTreeList = new JList();
      filterList.addListSelectionListener( this );
      filterTreeList.addListSelectionListener( this );
      updateLists();
      listPanel.setLayout( new GridLayout( 1,0 ) );
      JScrollPane fl = new JScrollPane( filterList );
      JScrollPane tl = new JScrollPane( filterTreeList );
      JPanel flPanel = new JPanel();
       flPanel.setBorder( new TitledBorder( "Filters" ) );
      flPanel.add( fl );
      JPanel tlPanel = new JPanel();
      tlPanel.setBorder( new TitledBorder( "FilterTrees" ) );
      tlPanel.add( tl );
      listPanel.add( flPanel );
      listPanel.add( tlPanel );

    } else if ( type == SHOW_TOGETHER ) {
      compoundList = new JList();
      compoundList.addListSelectionListener( this );
      updateLists();
      //listPanel.setBorder( new TitledBorder( "Filters and FilterTrees" ) );
      JScrollPane scroll = new JScrollPane( compoundList );
      listPanel.add( scroll, BorderLayout.CENTER);
				} else if ( type == SHOW_TREES ) {
      filterTreeList = new JList();
      filterTreeList.addListSelectionListener( this );
      updateLists();
      //listPanel.setBorder( new TitledBorder( "FilterTrees" ) );
      JScrollPane scroll = new JScrollPane( filterTreeList );
      listPanel.add( scroll, BorderLayout.CENTER );
    } else {
      filterList = new JList();
      filterList.addListSelectionListener( this );
      updateLists();
      //listPanel.setBorder( new TitledBorder( "Filters" ) );
      JScrollPane scroll = new JScrollPane( filterList );
      listPanel.add( scroll, BorderLayout.CENTER );
						//add(scroll,BorderLayout.CENTER); 
				}

    setLayout( new BorderLayout() );
    add( searchPanel, BorderLayout.NORTH );
    add( listPanel, BorderLayout.CENTER );

  }

  public SwingPropertyChangeSupport getSwingPropertyChangeSupport () {
    return pcs;
  }

  protected void fireFilterSelected () {
    pcs.firePropertyChange( FILTER_SELECTED, null, lastSelectedFilter );
  }


  public void valueChanged ( ListSelectionEvent e ) {

    if ( e.getSource() == filterList ) {
      lastSelectedFilter = filterList.getSelectedValue();
    } else if ( e.getSource() == filterTreeList ) {
      lastSelectedFilter = filterTreeList.getSelectedValue();
    } else if ( e.getSource() == compoundList ) {
      lastSelectedFilter = compoundList.getSelectedValue();
    }

    fireFilterSelected();

  }

  public Filter getLastSelectedFilter () {
    //System.out.println( "Last Selected Filter is: "+lastSelectedFilter.toString() );
    return FilterManager.defaultManager().getFilter( ( String )lastSelectedFilter );
  }


  public void actionPerformed ( ActionEvent e ) {
    if ( e.getSource() == searchField ) {
      performSearch();
    }
  }


  protected void performSearch () {
    if ( type == SHOW_SEPARATE ) {
      searchFilters();
      searchFilterTrees();
    } else if ( type == SHOW_TOGETHER ) {
      searchCompound();
    } else  if ( type == SHOW_TREES ) {
      searchFilterTrees();
    } else {
      searchFilters();
    }
  }


  public Filter[] getSelectedFilters () {
    
    if ( type == SHOW_SEPARATE ) {
      Object[] selected_filters = filterList.getSelectedValues();
      Object[] selected_filtertrees = filterTreeList.getSelectedValues();
      Filter[] filters = new Filter[ selected_filters.length + selected_filtertrees.length ];
      int i = 0;
      for ( i = 0; i < selected_filters.length; ++i ) {
        filters[i] =  FilterManager.defaultManager().getFilter( ( String )selected_filters[i] );
      }
      for ( int j = 0; j < selected_filtertrees.length; ++j ) {
        filters[i] =  FilterManager.defaultManager().getFilter( ( String )selected_filtertrees[i] );
        i++;
      }
      return filters;
    } else if ( type == SHOW_TOGETHER ) {
      Object[] selected_filters = compoundList.getSelectedValues();
      Filter[] filters = new Filter[ selected_filters.length ];
      int i = 0;
      for ( i = 0; i < selected_filters.length; ++i ) {
        filters[i] =  FilterManager.defaultManager().getFilter( ( String )selected_filters[i] );
      }
      return filters;

    } else  if ( type == SHOW_TREES ) {
      Object[] selected_filters = filterTreeList.getSelectedValues();
      Filter[] filters = new Filter[ selected_filters.length ];
      int i = 0;
      for ( i = 0; i < selected_filters.length; ++i ) {
        filters[i] =  FilterManager.defaultManager().getFilter( ( String )selected_filters[i] );
      }
      return filters;
    } else {
      Object[] selected_filters = filterList.getSelectedValues();
      Filter[] filters = new Filter[ selected_filters.length ];
      int i = 0;
      for ( i = 0; i < selected_filters.length; ++i ) {
        filters[i] =  FilterManager.defaultManager().getFilter( ( String )selected_filters[i] );
      }
      return filters;
    }

  }


  protected void searchFilters () {

    ArrayList filters_pass = new ArrayList();
    
    Enumeration filter_enum = filterModel.elements();
    String[] pattern = searchField.getText().split("\\s");
    while ( filter_enum.hasMoreElements() ) {
      String s = ( String )filter_enum.nextElement();
      for ( int p = 0; p < pattern.length; ++p ) {
        if ( Strings.isLike( s.toString(), pattern[p], 0, true ) ) {
          filters_pass.add( s );
          System.out.println( "Filter: "+s.toString()+" matches "+pattern[p] );
        }
      }
    }
    Iterator fpi = filters_pass.iterator();
    DefaultListModel filter_passed_model = new DefaultListModel();
    while ( fpi.hasNext() ) {
      filter_passed_model.addElement( fpi.next() );
    }
    filterList.setModel( filter_passed_model );
  }

  protected void searchFilterTrees () {

    ArrayList filters_pass = new ArrayList();
    
    Enumeration filter_enum = filterTreeModel.elements();
    String[] pattern = searchField.getText().split("\\s");
    while ( filter_enum.hasMoreElements() ) {
      String s = ( String )filter_enum.nextElement();
      for ( int p = 0; p < pattern.length; ++p ) {
        if ( Strings.isLike( s.toString(), pattern[p], 0, true ) ) {
          filters_pass.add( s );
          System.out.println( "Filter: "+s.toString()+" matches "+pattern[p] );
        }
      }
    }
    Iterator fpi = filters_pass.iterator();
    DefaultListModel filter_passed_model = new DefaultListModel();
    while ( fpi.hasNext() ) {
      filter_passed_model.addElement( fpi.next() );
    }
    filterTreeList.setModel( filter_passed_model );
  }

  protected void searchCompound () {

    ArrayList filters_pass = new ArrayList();
    
    Enumeration filter_enum = compoundModel.elements();
    String[] pattern = searchField.getText().split("\\s");
    while ( filter_enum.hasMoreElements() ) {
      String s = ( String )filter_enum.nextElement();
      for ( int p = 0; p < pattern.length; ++p ) {
        if ( Strings.isLike( s.toString(), pattern[p], 0, true ) ) {
          filters_pass.add( s );
          System.out.println( "Filter: "+s.toString()+" matches "+pattern[p] );
        }
      }
    }
    Iterator fpi = filters_pass.iterator();
    DefaultListModel filter_passed_model = new DefaultListModel();
    while ( fpi.hasNext() ) {
      filter_passed_model.addElement( fpi.next() );
    }
    compoundList.setModel( filter_passed_model );
  }


  public void propertyChange ( PropertyChangeEvent e ) {
    updateLists();
  }

  

  protected void updateLists () {
    if ( type == SHOW_SEPARATE ) {
      updateFilters();
      updateFilterTrees();
    } else if ( type == SHOW_TOGETHER ) {
      updateCompound();
    } else  if ( type == SHOW_TREES ) {
      updateFilterTrees();
    } else {
      updateFilters();
    }
  }

  protected void updateFilters () {
    Set filters = FilterManager.defaultManager().getFilters( false );
    filterModel = new DefaultListModel();
    filterModel.ensureCapacity( filters.size() );
    for ( Iterator i = filters.iterator(); i.hasNext();  ) {
      filterModel.addElement( ( String )i.next() );
    }
    filterList.setModel( filterModel );
  }

   protected void updateFilterTrees () {
    Set filters = FilterManager.defaultManager().getFilterTrees();
    filterTreeModel = new DefaultListModel();
    filterTreeModel.ensureCapacity( filters.size() );
    for ( Iterator i = filters.iterator(); i.hasNext();  ) {
      filterTreeModel.addElement( ( String )i.next() );
    }
    filterTreeList.setModel( filterTreeModel );
  }
  
   protected void updateCompound () {
    Set filters = FilterManager.defaultManager().getFilters( true );
    compoundModel = new DefaultListModel();
    compoundModel.ensureCapacity( filters.size() );
    for ( Iterator i = filters.iterator(); i.hasNext();  ) {
      compoundModel.addElement( ( String )i.next() );
    }
    compoundList.setModel( compoundModel );
  }

 

}
