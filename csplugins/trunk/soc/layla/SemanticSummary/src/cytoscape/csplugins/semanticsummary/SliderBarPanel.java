/*
 File: SliderBarPanel.java

 Copyright 2010 - The Cytoscape Consortium (www.cytoscape.org)
 
 Code written by: Layla Oesper
 Authors: Layla Oesper, Ruth Isserlin, Daniele Merico
 
 This library is free software: you can redistribute it and/or modify
 it under the terms of the GNU Lesser General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.
 
 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
 
 You should have received a copy of the GNU Lesser General Public License
 along with this project.  If not, see <http://www.gnu.org/licenses/>.
 */

package cytoscape.csplugins.semanticsummary;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.util.Iterator;
import java.util.Hashtable;
import java.awt.*;

import giny.model.Node;

import prefuse.data.query.NumberRangeModel;

public class SliderBarPanel extends JPanel {

    //height of panel
    private final int DIM_HEIGHT = 72;
    //width of panel
    private final int DIM_WIDTH = 150;

    //min and max values for the slider
    private int min;
    private int max;
    private NumberRangeModel rangeModel;

    //precision that the slider can be adjusted to
    private double precision = 1000.0;
    private int dec_precision = (int) Math.log10(precision);

    private JLabel label;
    private String sliderLabel;
    
    private JSlider slider;

    /**
     * Class constructor
     *
     * @param min - slider mininmum value
     * @param max - slider maximum value
     * @param sliderLabel
     * @param params - cloud parameters for current cloud
     * @param attrib - attribute that the slider bar is specific to (i.e. network normalization)
     * @param desired_width
     */
    public SliderBarPanel(double min, double max, String sliderLabel,String attrib, int desired_width) {
        this.setPreferredSize(new Dimension(DIM_WIDTH, DIM_HEIGHT));
        this.setLayout(new BorderLayout(0,0));
        this.setOpaque(false);

        if((min <= 1) && (max <= 1)){
            this.min = (int)(min*precision);
            this.max = (int)(max*precision);
        }
        else{
           this.min = (int)min;
           this.max = (int)max;
        }
        this.sliderLabel = sliderLabel;

        label = new JLabel(sliderLabel);

        Dimension currentsize = label.getPreferredSize();
        currentsize.height = DIM_HEIGHT/12;
        label.setPreferredSize(currentsize);
        initPanel(attrib, desired_width);
    }

    /**
     * Initialize panel based on cloudParameters and desired attribute
     *
     * @param attrib - attribute that the slider bar is specific to (i.e. network normalization)
     * @param desired_width
     */
    public void initPanel(String attrib, int desired_width){

        slider = new JSlider(JSlider.HORIZONTAL,
                                      min, max, min);

        slider.addChangeListener(new SliderBarActionListener(this,attrib));

        slider.setMajorTickSpacing((max-min)/5);
        slider.setPaintTicks(true);

        //Create the label table
        Hashtable labelTable = new Hashtable();
        labelTable.put( new Integer( min ), new JLabel(""+ min/precision));
        labelTable.put( new Integer( max ), new JLabel("" + max/precision));
        slider.setLabelTable( labelTable );

        slider.setPaintLabels(true);

        Dimension currentsize = slider.getPreferredSize();
        currentsize.width = desired_width;
        currentsize.height = (DIM_HEIGHT/12) * 11;
        slider.setPreferredSize(currentsize);

        this.setLayout(new GridLayout(2,1));

        this.add(label, BorderLayout.NORTH);

        this.add(slider,  BorderLayout.SOUTH);

        this.revalidate();
    }
    
    /**
     * Translates the provided network normalization value to an integer that this
     * sliderBarPanel can handle and sets the pointer appropriately.
     * @param val
     * @return
     */
    public void setNetNormValue(Double val)
    {
    	long value = Math.round(val * this.getPrecision());
		int intValue = (int)value;
		
		this.getSlider().setValue(intValue);
    }
    

    //Getters and Setters

    public void setLabel(int current_value) {
        label.setText(String.format( "<html>" + sliderLabel +                   // "P-value Cutoff" or "Q-value Cutoff"
                " &#8594; " +                                                   // HTML entity right-arrow ( &rarr; )
                "<font size=\"-2\"> %." + dec_precision + "f </font></html>",   // dec_precision is the number of decimals for given precision
                (current_value/precision)                                       // the current P/Q-value cutoff
                ) );

        this.revalidate();
    }
    
    public void setEnabled(boolean enabled)
    {
    	slider.setEnabled(enabled);
    	label.setEnabled(enabled);
    }

    public double getPrecision() {
        return precision;
    }

    public double getMin() {
        return min/precision;
    }

    public void setMin(double min) {
        this.min = (int)(min * precision);
    }

    public double getMax() {
        return max/precision;
    }

    public void setMax(double max) {
        this.max = (int) (max*precision);
    }

    public NumberRangeModel getRangeModel() {
        return rangeModel;
    }

    public void setRangeModel(NumberRangeModel rangeModel) {
        this.rangeModel = rangeModel;
    }
    
    public JSlider getSlider()
    {
    	return slider;
    }
}
