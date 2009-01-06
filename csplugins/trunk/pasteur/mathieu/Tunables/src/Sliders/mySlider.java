package Sliders;


import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import Sliders.stringLib;



public class mySlider extends JComponent {

    private Number     m_min, m_max, m_value;
    private boolean    m_ignore = false;
    
    private JLabel     m_label;
    private JSlider    m_slider;
    private JTextField m_field;
    private List       m_listeners;
    
    private int majortickspace;

    private int m_smin = 0;
    private int m_srange = 100;
    

//    public mySlider(String title, double min, double max, double value) {
//        this(title, new Double(min), new Double(max), new Double(value));
//    }
    

//    public MySlider(String title, float min, float max, float value) {
//        this(title, new Float(min), new Float(max), new Float(value));
//    }
//    
//    public MySlider(String title, int min, int max, int value) {
//        this(title, new Integer(min), new Integer(max), new Integer(value));
//        m_smin = min;
//        m_srange = max-min;
//        m_slider.setMinimum(min);
//        m_slider.setMaximum(max);
//        setValue(new Integer(value));
//    }
//    
//    public MySlider(String title, long min, long max, long value) {
//        this(title, new Long(min), new Long(max), new Long(value));
//    }

    
    public mySlider(String title, Number min, Number max, Number value) {
        m_min    = min;
        m_max    = max;
        m_value  = value;
        m_slider = new JSlider();
        m_label  = new JLabel();//title
        m_field  = new JTextField(4);
        m_listeners = new ArrayList();
        //m_field.setBorder(null);
        
        majortickspace = (max.intValue()-min.intValue())/5;

        Hashtable labelTable = new Hashtable();

        int i=m_min.intValue();
        int j=0;
        while(i <= m_max.intValue()){
        	labelTable.put(j,new JLabel(new Integer(i).toString()));
        	i+=majortickspace;
        	j+=20;   	
        }
        m_slider.setMajorTickSpacing(20);
        m_slider.setMinorTickSpacing(5);
        m_slider.setLabelTable(labelTable);
        m_slider.setPaintTicks(true);
        m_slider.setPaintLabels(true);
        setSliderValue();
        setFieldValue();
        initUI();
    }
    

    protected void initUI() {
        m_slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if ( m_ignore ) return;
                m_ignore = true;
                // update the value
                m_value = getSliderValue();
                // set text field value
                setFieldValue();
                // fire event
                fireChangeEvent();
                m_ignore = false;
            }
        });
        m_field.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if ( m_ignore ) return;
                m_ignore = true;
                Number v = getFieldValue();
                if ( v != m_value ) {
                    // update the value
                    m_value = v;
                    // set slider value
                    setSliderValue();
                }
                // fire event
                fireChangeEvent();
                m_ignore = false;
            }
        });
        m_field.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                String s = m_field.getText();
                if ( isTextObscured(m_field, s) )
                    m_field.setToolTipText(s);
            }
            public void mouseExited(MouseEvent e) {
                m_field.setToolTipText(null);
            }
        });
        m_label.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                String s = m_label.getText();
                if ( isTextObscured(m_label, s) )
                    m_label.setToolTipText(s);
            }
            public void mouseExited(MouseEvent e) {
                m_label.setToolTipText(null);
            }
        });
        
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
//       add(m_label);
        add(m_slider);
        add(m_field);
    }
    

    private static boolean isTextObscured(JComponent c, String s) {
        Graphics g = c.getGraphics();
        FontMetrics fm = g.getFontMetrics(c.getFont());
        int sw = fm.stringWidth(s);
        return ( sw > c.getWidth() );
    }

    
//	private Hashtable createLabels(JSlider slider) {
//		return slider.createStandardLabels(m_min.intValue(),majortickspace);
//	}

    

    public Number getValue() {
        return m_value;
    }

    public void setValue(Number value) {
        m_value = value;
        setSliderValue();
        setFieldValue();
    }
    
    private Number getSliderValue() {
        if ( m_value instanceof Integer ) {
            int val = m_slider.getValue();
            int min = m_min.intValue();
            int max = m_max.intValue();
            return new Integer(min + (val-m_smin)*(max-min)/m_srange);
        } else if ( m_value instanceof Long ) {
            int val = m_slider.getValue();
            long min = m_min.longValue();
            long max = m_max.longValue();
            return new Long(min + (val-m_smin)*(max-min)/m_srange);
        } else {
            double f = (m_slider.getValue()-m_smin)/(double)m_srange;
            double min = m_min.doubleValue();
            double max = m_max.doubleValue();
            double val = min + f*(max-min);
            return (m_value instanceof Double ? (Number)new Double(val)
                                              : new Float((float)val));
        }
    }
    
  
    private void setSliderValue() {
        int val;
        if ( m_value instanceof Double || m_value instanceof Float ) {
            double value = m_value.doubleValue();
            double min = m_min.doubleValue();
            double max = m_max.doubleValue();
            val = m_smin + (int)Math.round(m_srange*((value-min)/(max-min)));
        } else {
            long value = m_value.longValue();
            long min = m_min.longValue();
            long max = m_max.longValue();
            val = m_smin + (int)((m_srange*(value-min))/(max-min));
        }
        m_slider.setValue(val);
    }
    
  
    private Number getFieldValue() {
        if ( m_value instanceof Double || m_value instanceof Float ) {
            double v;
            try {
                v = Double.parseDouble(m_field.getText());
            } catch ( Exception e ) {
                // TODO handle exception
                return m_value;
            }
            if ( v < m_min.doubleValue() || v > m_max.doubleValue() ) {
                // TODO handle exception
                return m_value;
            }
            return m_value instanceof Double ? (Number)new Double(v) 
                                             : new Float((float)v);
        } else {
            long v;
            try {
                v = Long.parseLong(m_field.getText());
            } catch ( Exception e ) {
                // TODO handle exception
                return m_value;
            }
            if ( v < m_min.longValue() || v > m_max.longValue() ) {
                // TODO handle exception
                return m_value;
            }
            return m_value instanceof Long ? (Number)new Long(v) 
                                           : new Integer((int)v);
        }
    }
    
 
    private void setFieldValue() {
        String text;
        if ( m_value instanceof Double || m_value instanceof Float )
            text = stringLib.formatNumber(m_value.doubleValue(),3);
        else
            text = String.valueOf(m_value.longValue());
        m_field.setText(text);
    }
    
 
    
    
    
    
    public void addChangeListener(ChangeListener cl) {
        if ( !m_listeners.contains(cl) )
            m_listeners.add(cl);
    }

    public void removeChangeListener(ChangeListener cl) {
        m_listeners.remove(cl);
    }
    
    protected void fireChangeEvent() {
        Iterator iter = m_listeners.iterator();
        ChangeEvent evt = new ChangeEvent(this); 
        while ( iter.hasNext() ) {
            ChangeListener cl = (ChangeListener)iter.next();
            cl.stateChanged(evt);
        }
    }
    
//    public void setBackground(Color c) {
//        m_field.setBackground(c);
//        m_label.setBackground(c);
//        m_slider.setBackground(c);
//        super.setBackground(c);
//    }
//    
//    public void setForeground(Color c) {
//        m_field.setForeground(c);
//        m_label.setForeground(c);
//        m_slider.setForeground(c);
//        super.setForeground(c);
//    }
//    
//    public void setFont(Font f) {
//        m_field.setFont(f);
//        m_label.setFont(f);
//        m_slider.setFont(f);
//        super.setFont(f);
//    }
//    
//    public void setPreferredSize(Dimension d) {
//        int fw = Math.min(40, d.width/5);
//        int lw = Math.min(100, (d.width-fw)/2);
//        int sw = d.width-fw-lw;
//        super.setPreferredSize(d);
//        Dimension dd = new Dimension(lw, d.height);
//        m_label.setPreferredSize(dd);
//        dd = new Dimension(sw, d.height);
//        m_slider.setPreferredSize(dd);
//        dd = new Dimension(fw, d.height);
//        m_field.setPreferredSize(dd);
//    }
    
}