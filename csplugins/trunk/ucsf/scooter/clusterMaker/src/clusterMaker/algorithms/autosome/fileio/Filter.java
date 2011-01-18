/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package clusterMaker.algorithms.autosome.fileio;

/**
 *
 * @author Aaron
 */

import java.util.*;
import clusterMaker.algorithms.autosome.view.view2d.*;
import clusterMaker.algorithms.autosome.cluststruct.dataItem;

public class Filter {

    private static HashMap kept;

    public void filter(File_Open fo, viewer2D v2d){

        kept = new HashMap();

        float fmin = Float.MAX_VALUE;
        float fmax = Float.MIN_VALUE;


        for(int i = 0; i < v2d.s.input.length; i++){
            float min=Float.MAX_VALUE;
            float max=Float.MIN_VALUE;
            float fold=0;
            float ave=0;
            for(int j = 0; j < v2d.s.input[i].getOriginalValues().length; j++){
                float f = v2d.s.input[i].getOriginalValues()[j];
                if(f<min) min=f;
                if(f>max) max=f;
                ave+=f;
            }
            ave/=v2d.s.input[i].getOriginalValues().length;
            if(fo.jCheckBox3.isSelected()) fold=(float)(Math.pow(2,max-min));
            else fold = (float)(max/min);//(Math.log10(max) / Math.log10(2))-(Math.log10(min) / Math.log10(2)));
            if(fo.jCheckBox4.isSelected() && fold<Float.valueOf(fo.jTextField1.getText())) continue;
            if(fo.jCheckBox5.isSelected() && ave<Float.valueOf(fo.jTextField2.getText())) continue;
            kept.put(i,i);
            //System.out.println(v2d.s.input[i].toDescString());
            if(min<fmin) fmin=min;
            if(max>fmax) fmax=max;
        }

        File_Open.jLabel14.setText(String.valueOf(kept.size()));

        if(kept.isEmpty()){
            fmax=0;
            fmin=0;
        }
        File_Open.jLabel20.setText(String.valueOf(((Math.abs(fmax)>0) ? fo.Format.format(fmax) :fmax)));
        File_Open.jLabel17.setText(String.valueOf(((Math.abs(fmin)>0) ? fo.Format.format(fmin) :fmin)));
        executed=true;
        File_Open.jButton4.setEnabled(true);
    }

    public static HashMap getKept() {return kept;}

    public static void reset() {kept=new HashMap();}

    public static boolean executed=false;

}
