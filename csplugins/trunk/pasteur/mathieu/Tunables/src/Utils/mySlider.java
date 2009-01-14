package Utils;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
//import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
//import Utils.stringLib;



@SuppressWarnings("serial")
public class mySlider extends JComponent {

    private Number     m_min, m_max, m_value;
    private boolean    m_ignore = false;
    
    private JSlider    m_slider;
    private JTextField m_field;
    @SuppressWarnings("unchecked")
	private List       m_listeners;
    
    java.text.DecimalFormat df = new java.text.DecimalFormat("##.##");
    
    private Number majortickspace;
    private int m_smin = 0;
    private int m_srange = 100;
  

    
    @SuppressWarnings("unchecked")
	public mySlider(String title, Number min, Number max, Number value) {
        m_min    = min;
        m_max    = max;
        m_value  = value;
        m_slider = new JSlider();
        m_field  = new JTextField(4);
        m_field.setHorizontalAlignment(JTextField.RIGHT);
        m_listeners = new ArrayList();

        Hashtable labelTable = new Hashtable();
        majortickspace = (max.doubleValue()-min.doubleValue())/5;
        if(m_value instanceof Double || m_value instanceof Float){
        	Float major = new Float(majortickspace.floatValue());
        	
            float i = m_min.floatValue();
	        int j=0;
	        while(i <= m_max.doubleValue()){
	        	JLabel label = new JLabel(df.format(i));
	        	label.setFont(new Font("",Font.BOLD,9));
	        	labelTable.put(j,label);
	        	i+=major;
	        	j+=20;

	        }
	        
        }
        else if(m_value instanceof Long || m_value instanceof Integer){
        	Integer majortick = new Integer(majortickspace.intValue());
        	int i=m_min.intValue();
	        int j=0;
	        while(i <= m_max.intValue()){
	        	JLabel label = new JLabel(df.format(i));
	        	label.setFont(new Font("",Font.BOLD,9));
	        	labelTable.put(j,label);
	        	//labelTable.put(j,new JLabel(Integer.toString(i)));
	        	i+=majortick;
	        	j+=20;   	
	        }
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
        
//        CaretListener caretupdate = new CaretListener(){
//        	public void caretUpdate(javax.swing.event.CaretEvent e){
//        		JTextField text = (JTextField)e.getSource();
//        		Double test = Double.parseDouble(text.getText());
//        		m_value = test;
//        		setSliderValue();
//        	}
//        };
//        m_field.addCaretListener(caretupdate);

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
//        m_field.addMouseListener(new MouseAdapter() {
//            public void mouseEntered(MouseEvent e) {
//                String s = m_field.getText();
//                if ( isTextObscured(m_field, s) )
//                    m_field.setToolTipText(s);
//            }
//            public void mouseExited(MouseEvent e) {
//                m_field.setToolTipText(null);
//            }
//        });
        
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(m_slider);
        add(m_field);
    }
    

    private static boolean isTextObscured(JComponent c, String s) {
        Graphics g = c.getGraphics();
        FontMetrics fm = g.getFontMetrics(c.getFont());
        int sw = fm.stringWidth(s);
        return ( sw > c.getWidth() );
    }
    

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
    
  
    private Number getFieldValue(){
    	Double val = null;
    	try{
    		val = Double.parseDouble(m_field.getText());
    	}catch(NumberFormatException nfe){
    		JOptionPane.showMessageDialog(null, "Not a value!","Alert", JOptionPane.ERROR_MESSAGE);
    		try{
    			val = m_value.doubleValue();
    		}catch(Exception e){e.printStackTrace();}
    	}

        if ( m_value instanceof Double || m_value instanceof Float ){
            if ( val < m_min.doubleValue() || val > m_max.doubleValue()){
            	JOptionPane.showMessageDialog(null, "Value is out of Bounds","Alert",JOptionPane.ERROR_MESSAGE);
            	return m_value;
//            	if ( val < m_min.doubleValue())return m_min.doubleValue();
//            	if ( val > m_max.doubleValue())return m_max.doubleValue();
//            	return m_value;
            }
            return m_value instanceof Double ? (Number)val.doubleValue() : val.floatValue();
        }
        else {
            if ( val < m_min.longValue() || val > m_max.longValue() ) {
            	JOptionPane.showMessageDialog(null, "Value is out of Bounds","Alert",JOptionPane.ERROR_MESSAGE);
            	return m_value;
//            	if ( val < m_min.longValue())return m_min.longValue();
//            	if ( val > m_max.longValue())return m_max.longValue();
//            	return m_value;
            }
            return m_value instanceof Long ? (Number)val.longValue() : val.intValue();
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
    
    @SuppressWarnings("unchecked")
	public void addChangeListener(ChangeListener cl) {
        if ( !m_listeners.contains(cl) )
            m_listeners.add(cl);
    }

    public void removeChangeListener(ChangeListener cl) {
        m_listeners.remove(cl);
    }
    
    @SuppressWarnings("unchecked")
	protected void fireChangeEvent() {
        Iterator iter = m_listeners.iterator();
        ChangeEvent evt = new ChangeEvent(this); 
        while ( iter.hasNext() ) {
            ChangeListener cl = (ChangeListener)iter.next();
            cl.stateChanged(evt);
        }
    }   
}