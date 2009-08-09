/*
 * @(#)HistoChangeListener.java
 *
 *
 * @author
 * @version 1.00 2009/7/14
 */

package clusterMaker.ui;

public interface HistoChangeListener {

       /**
        * This method will be called when the user sets a new
        * bounds value in the histogram
        *
        * @param bounds the value the user set
        */
       void histoValueChanged(double bounds);


}
