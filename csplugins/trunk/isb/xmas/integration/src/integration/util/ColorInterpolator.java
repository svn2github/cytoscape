package integration.util;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.BorderFactory;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

public class ColorInterpolator
{

  // The default is to color negative values red and positive values grey.
  public static final double DEFAULT_CENTER_VALUE = 0.0;
  public static final double DEFAULT_LOW_VALUE = -1.0;
  public static final double DEFAULT_HIGH_VALUE = 1.0;
  public static final Color DEFAULT_CENTER_COLOR = Color.white;
  public static final Color DEFAULT_LOW_COLOR = Color.red;
  public static final Color DEFAULT_HIGH_COLOR = Color.green;

  // The ColorCanvas knows which color to represent because its whichColor is one of these:
  protected static final int LOW_COLOR = 1;
  protected static final int CENTER_COLOR = 2;
  protected static final int HIGH_COLOR = 3;

  /**
   * @beaninfo (rwb)
   */
  protected double centerValue = 0.0;
  /**
   * @beaninfo (rwb)
   */
  protected Color centerColor = null;
  /**
   * @beaninfo (rwb)
   */
  protected double lowValue = 0.0;
  /**
   * @beaninfo (rwb)
   */
  protected Color lowColor = null;
  /**
   * @beaninfo (rwb)
   */
  protected double highValue = 0.0;
  /**
   * @beaninfo (rwb)
   */
  protected Color highColor = null;

  protected PropertyChangeSupport pcs =
    new PropertyChangeSupport( this );

  public ColorInterpolator () {
    this( DEFAULT_CENTER_VALUE,
          DEFAULT_CENTER_COLOR,
          DEFAULT_LOW_VALUE,
          DEFAULT_LOW_COLOR,
          DEFAULT_HIGH_VALUE,
          DEFAULT_HIGH_COLOR );
  }

  public ColorInterpolator (
    double center_value,
    Color center_color,
    double low_value,
    Color low_color,
    double high_value,
    Color high_color
  ) {
    setCenterValue( center_value );
    setCenterColor( center_color );
    setLowValue( low_value );
    setLowColor( low_color );
    setHighValue( high_value );
    setHighColor( high_color );
  }

  public Color colorFromValue ( double value ) {
    return ColorInterpolator.colorFromValue(
      centerValue,
      centerColor,
      lowValue,
      lowColor,
      highValue,
      highColor,
      value
    );
  } // colorFromValue(..)

  /**
   * @beaninfo (rwb)
   */
  public void setCenterValue ( double value ) {
    if( centerValue == value ) {
      return;
    }
    double old_value = centerValue;
    centerValue = value;
    pcs.firePropertyChange(
      "centerValue",
      new Double( old_value ),
      new Double( value )
    );
  } // setCenterValue(..)

  public double getCenterValue () {
    return centerValue;
  }

  /**
   * @beaninfo (rwb)
   */
  public void setLowValue ( double value ) {
    if( lowValue == value ) {
      return;
    }
    double old_value = lowValue;
    lowValue = value;
    pcs.firePropertyChange(
      "lowValue",
      new Double( old_value ),
      new Double( value )
    );
  } // setLowValue(..)

  public double getLowValue () {
    return lowValue;
  }

  /**
   * @beaninfo (rwb)
   */
  public void setHighValue ( double value ) {
    if( highValue == value ) {
      return;
    }
    double old_value = highValue;
    highValue = value;
    pcs.firePropertyChange(
      "highValue",
      new Double( old_value ),
      new Double( value )
    );
  } // setHighValue(..)

  public double getHighValue () {
    return highValue;
  }

  /**
   * @beaninfo (rwb)
   */
  public void setCenterColor ( Color color ) {
    if( color == null ) {
      throw new IllegalArgumentException( "The color must be non-null." );
    }
    if( ( centerColor != null ) && centerColor.equals( color ) ) {
      return;
    }
    Color old_color = centerColor;
    centerColor = color;
    pcs.firePropertyChange( "centerColor", old_color, color );
  } // setCenterColor( Color )

  public Color getCenterColor () {
    return centerColor;
  }

  /**
   * @beaninfo (rwb)
   */
  public void setLowColor ( Color color ) {
    if( color == null ) {
      throw new IllegalArgumentException( "The color must be non-null." );
    }
    if( ( lowColor != null ) && lowColor.equals( color ) ) {
      return;
    }
    Color old_color = lowColor;
    lowColor = color;
    pcs.firePropertyChange( "lowColor", old_color, color );
  } // setLowColor( Color )

  public Color getLowColor () {
    return lowColor;
  }

  /**
   * @beaninfo (rwb)
   */
  public void setHighColor ( Color color ) {
    if( color == null ) {
      throw new IllegalArgumentException( "The color must be non-null." );
    }
    if( ( highColor != null ) && highColor.equals( color ) ) {
      return;
    }
    Color old_color = highColor;
    highColor = color;
    pcs.firePropertyChange( "highColor", old_color, color );
  } // setHighColor( Color )

  public Color getHighColor () {
    return highColor;
  }

  public ColorInterpolatorPanel createPanel () {
    // TODO: Problem: once the listener is added, the listener cannot be
    // garbage-collected until this is.  This is a general problem that comes
    // up again and again.  I think that we need a weak-reference
    // implementation of the propertychangesupport.
    ColorInterpolatorPanel panel = new ColorInterpolatorPanel();
    addPropertyChangeListener( panel );
    return panel;
  } // createPanel()

  public class ColorInterpolatorPanel
    extends JPanel
    implements PropertyChangeListener {

    protected ColorCanvas lowColorCanvas;
    protected ColorCanvas centerColorCanvas;
    protected ColorCanvas highColorCanvas;

    /**
     * Default constructor delegates to the superclass void constructor, then
     * calls {@link #initializeColorInterpolatorPanel()}.
     */
    public ColorInterpolatorPanel () {
      super();
      initializeColorInterpolatorPanel();
    }

    protected void initializeColorInterpolatorPanel () {
      GridBagLayout gb = new GridBagLayout() ;
      GridBagConstraints c = new GridBagConstraints();
      this.setLayout( gb );
      this.setOpaque( true );
      this.setBackground( Color.lightGray );
      this.setForeground( Color.black );

      c.insets = new Insets( 10, 10, 10, 10 );
      c.weighty = 0.0;
      c.weightx = 1.0;
      c.gridheight = 1;
      c.gridwidth = 1;
      c.anchor = GridBagConstraints.CENTER;
      c.fill = GridBagConstraints.HORIZONTAL;

      c.gridy = 0;

      c.insets.bottom = 0;
      c.insets.right = 0;
      JLabel low_label = new JLabel( "Low", SwingConstants.CENTER );
      gb.setConstraints( low_label, c );
      this.add( low_label );

      c.insets.left = 0;
      JLabel center_label = new JLabel( "Center", SwingConstants.CENTER );
      gb.setConstraints( center_label, c );
      this.add( center_label );

      c.insets.right = 10;
      JLabel high_label = new JLabel( "High", SwingConstants.CENTER );
      gb.setConstraints( high_label, c );
      this.add( high_label );

      c.gridy++;

      c.insets.top = 0;
      c.insets.right = 0;
      c.insets.left = 10;
      final JTextField low_text_field =
        new JTextField( String.valueOf( getLowValue() ) );
      low_text_field.setEditable( true );
      low_text_field.addActionListener( new ActionListener() {
          public void actionPerformed ( ActionEvent event ) {
            try {
              setLowValue( Double.parseDouble( low_text_field.getText() ) );
            } catch( NumberFormatException e ) {
              // TODO: Error handling
              System.err.println( "WARNING: Unable to parse the value \"" +
                                  low_text_field.getText() + "\" as a double." );
            }
          }
        } );
      gb.setConstraints( low_text_field, c );
      this.add( low_text_field );

      c.insets.left = 0;
      final JTextField center_text_field =
        new JTextField( String.valueOf( getCenterValue() ) );
      center_text_field.setEditable( true );
      center_text_field.addActionListener( new ActionListener() {
          public void actionPerformed ( ActionEvent event ) {
            try {
              setCenterValue( Double.parseDouble( center_text_field.getText() ) );
            } catch( NumberFormatException e ) {
              // TODO: Error handling
              System.err.println( "WARNING: Unable to parse the value \"" +
                                  center_text_field.getText() + "\" as a double." );
            }
          }
        } );
      gb.setConstraints( center_text_field, c );
      this.add( center_text_field );

      c.insets.right = 10;
      final JTextField high_text_field = new JTextField( String.valueOf( getHighValue() ) );
      high_text_field.setEditable( true );
      high_text_field.addActionListener( new ActionListener() {
          public void actionPerformed ( ActionEvent event ) {
            try {
              setHighValue( Double.parseDouble( high_text_field.getText() ) );
            } catch( NumberFormatException e ) {
              // TODO: Error handling
              System.err.println( "WARNING: Unable to parse the value \"" +
                                  high_text_field.getText() + "\" as a double." );
            }
          }
        } );
      gb.setConstraints( high_text_field, c );
      this.add( high_text_field );

      c.gridy++;

      c.insets.bottom = 10;
      c.insets.right = 0;
      c.insets.left = 10;
      c.weighty = 1.0;
      c.fill = GridBagConstraints.BOTH;
      c.gridheight = GridBagConstraints.REMAINDER;

      lowColorCanvas = createColorCanvas( LOW_COLOR );
      gb.setConstraints( lowColorCanvas, c );
      this.add( lowColorCanvas );

      c.insets.left = 0;
      centerColorCanvas = createColorCanvas( CENTER_COLOR );
      gb.setConstraints( centerColorCanvas, c );
      this.add( centerColorCanvas );

      c.insets.right = 10;
      highColorCanvas = createColorCanvas( HIGH_COLOR );
      gb.setConstraints( highColorCanvas, c );
      this.add( highColorCanvas );

    } // initializeColorInterpolatorPanel()

    /**
     * Listen for color changes to the ColorInterpolator and update the
     * appropriate ColorCanvas in response.
     */
    // implements PropertyChangeListener
    public void propertyChange ( PropertyChangeEvent event ) {
      String property = event.getPropertyName();
      if( property.equals( "lowColor" ) ) {
        lowColorCanvas.updateColorCanvas();
      } else if( property.equals( "centerColor" ) ) {
        centerColorCanvas.updateColorCanvas();
      } else if( property.equals( "highColor" ) ) {
        highColorCanvas.updateColorCanvas();
      } else {
        // Do nothing.
      }
    } // propertyChange(..)

    protected ColorCanvas createColorCanvas ( int which_color ) {
      return new ColorCanvas( which_color );
    }
    public class ColorCanvas
      extends JLabel {

      protected int whichColor = 0;
      protected JColorChooser colorChooser;
      protected JDialog colorChooserDialog;

      /**
       * int constructor delegates to the superclass void constructor, then
       * sets the whichColor to that given, then calls initializeColorCanvas().
       */
      public ColorCanvas ( int which_color ) {
        super( " " );
        initializeColorCanvas();
        setWhichColor( which_color );
      }

      protected void initializeColorCanvas () {

        setOpaque( true );

        setBorder(
          BorderFactory.createBevelBorder( BevelBorder.LOWERED )
        );

        colorChooser = new JColorChooser();
        // Set the colorChooser's present color to the right one.
        colorChooserDialog = JColorChooser.createDialog(
          this,
          "Color",
          true,
          colorChooser,
          new ActionListener() {
              public void actionPerformed ( ActionEvent event ) {
                switch( ColorCanvas.this.whichColor ) {
                case LOW_COLOR:
                  setLowColor( colorChooser.getColor() );
                  break;
                case CENTER_COLOR:
                  setCenterColor( colorChooser.getColor() );
                  break;
                case HIGH_COLOR:
                  setHighColor( colorChooser.getColor() );
                  break;
                default:
                  // Do nothing.  The error will be handled in updateBackgroundColor().
                }
                ColorCanvas.this.repaint();
              } // actionPerformed(..)
          },
          null
        );
        addMouseListener( createMouseListener() );
      } // initializeColorCanvas();

      public void setWhichColor ( int which_color ) {
        if( ( which_color >= LOW_COLOR ) && ( which_color <= HIGH_COLOR ) ) {
          whichColor = which_color;
          updateColorCanvas();
        } else {
          // TODO: Error handling.
          throw new IllegalArgumentException( "whichColor must be one of { LOW_COLOR, CENTER_COLOR, HIGH_COLOR }." );
        }
      } // setWhichColor(..)

      public int getWhichColor () {
        return whichColor;
      }

      public void updateColorCanvas () {
          switch( whichColor ) {
          case LOW_COLOR:
            setBackground( getLowColor() );
            colorChooser.setColor( getLowColor() );
            colorChooserDialog.setTitle( "Low Color" );
            break;
          case CENTER_COLOR:
            setBackground( getCenterColor() );
            colorChooser.setColor( getCenterColor() );
            colorChooserDialog.setTitle( "Center Color" );
            break;
          case HIGH_COLOR:
            setBackground( getHighColor() );
            colorChooser.setColor( getHighColor() );
            colorChooserDialog.setTitle( "High Color" );
            break;
          default:
            // TODO: Error handling
            System.err.println( "WARNING: The whichColor of a ColorCanvas is invalid: " + whichColor + "." );
          }
      } // updateColorCanvas()

      protected MouseListener createMouseListener () {
        return new MouseAdapter () {
            public void mouseClicked ( MouseEvent event ) {
              if( event.getClickCount() == 2 ) {
                colorChooserDialog.setVisible( true );
              }
            } // mouseClicked(..)
          };
      } // createMouseListener()

    } // inner class ColorCanvas

  } // inner class ColorInterpolatorPanel

  // implements PropertyChangeEventSource
  public void addPropertyChangeListener (
    PropertyChangeListener listener
  ) {
    pcs.addPropertyChangeListener( listener );
  }

  // implements PropertyChangeEventSource
  public void addPropertyChangeListener (
    String property,
    PropertyChangeListener listener
  ) {
    pcs.addPropertyChangeListener( property, listener );
  }

  // implements PropertyChangeEventSource
  public void removePropertyChangeListener (
    PropertyChangeListener listener
  ) {
    pcs.removePropertyChangeListener( listener );
  }

  // implements PropertyChangeEventSource
  public void removePropertyChangeListener (
    String property,
    PropertyChangeListener listener
  ) {
    pcs.removePropertyChangeListener( property, listener );
  }

  // implements PropertyChangeEventSource
  public PropertyChangeListener[] getPropertyChangeListeners () {
    return pcs.getPropertyChangeListeners();
  }

  // implements PropertyChangeEventSource
  public PropertyChangeListener[] getPropertyChangeListeners (
    String property
  ) {
    return pcs.getPropertyChangeListeners( property );
  }

  public static Color colorFromValue (
    double center_value,
    Color center_color,
    double low_value,
    Color low_color,
    double high_value,
    Color high_color,
    double value
  ) {

    // TODO: REMOVE
    //System.err.println( "colorFromValue( " + center_value + ", " + center_color + ", " + low_value + ", " + low_color + ", " + high_value + ", " + high_color + ", " + value + " ).." );

    Color value_color;
    int center_r = center_color.getRed();
    int center_g = center_color.getGreen();
    int center_b = center_color.getBlue();
    if( value == center_value ) {
      value_color = center_color;
    } else if( value > center_value ) {
      if( value >= high_value ) {
        value_color = high_color;
      } else {
        int high_r = high_color.getRed();
        int high_g = high_color.getGreen();
        int high_b = high_color.getBlue();
        double value_percent =
          ( ( value - center_value ) / ( high_value - center_value ) );
        int value_r = ( int )
          ( ( high_r > center_r ) ?
            ( center_r + ( ( high_r - center_r ) * value_percent ) ):
            ( center_r - ( ( center_r - high_r ) * value_percent ) ) );
        int value_g = ( int )
          ( ( high_g > center_g ) ?
            ( center_g + ( ( high_g - center_g ) * value_percent ) ):
            ( center_g - ( ( center_g - high_g ) * value_percent ) ) );
        int value_b = ( int )
          ( ( high_b > center_b ) ?
            ( center_b + ( ( high_b - center_b ) * value_percent ) ):
            ( center_b - ( ( center_b - high_b ) * value_percent ) ) );
        value_color = new Color( value_r, value_g, value_b );
      }
    } else {
      if( value <= low_value ) {
        value_color = low_color;
      } else {
        int low_r = low_color.getRed();
        int low_g = low_color.getGreen();
        int low_b = low_color.getBlue();
        double value_percent =
          ( ( center_value - value ) / ( center_value - low_value ) );
        int value_r = ( int )
          ( ( low_r > center_r ) ?
            ( center_r + ( ( low_r - center_r ) * value_percent ) ):
            ( center_r - ( ( center_r - low_r ) * value_percent ) ) );
        int value_g = ( int )
          ( ( low_g > center_g ) ?
            ( center_g + ( ( low_g - center_g ) * value_percent ) ):
            ( center_g - ( ( center_g - low_g ) * value_percent ) ) );
        int value_b = ( int )
          ( ( low_b > center_b ) ?
            ( center_b + ( ( low_b - center_b ) * value_percent ) ):
            ( center_b - ( ( center_b - low_b ) * value_percent ) ) );
        value_color = new Color( value_r, value_g, value_b );
      }
    }

    // TODO: REMOVE
    //System.err.println( "Returning " + value_color + "." );

    return value_color;
  } // static colorFromValue(..)

} // class ColorInterpolator
