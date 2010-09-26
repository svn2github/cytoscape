package de.mpg.mpi_inf.bioinf.netanalyzer.dec;

import javax.swing.JDialog;

import org.jfree.chart.JFreeChart;

import de.mpg.mpi_inf.bioinf.netanalyzer.data.settings.XMLSerializable;
import de.mpg.mpi_inf.bioinf.netanalyzer.ui.ComplexParamVisualizer;

/**
 * Base class for all decorators in NetworkAnalyzer.
 * 
 * @author Yassen Assenov
 */
public abstract class Decorator implements Cloneable, XMLSerializable {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public abstract Object clone();

	/**
	 * Adds a decoration to the specified chart, or updates the current one.
	 * 
	 * @param aOwner Analysis dialog instance which uses this decorator.
	 * @param aChart Chart to be decorated.
	 * @param aVisualizer Visualizer that that created the chart.
	 * @param aVerbose Flag indicating if the decorator must be run in verbose mode - asking the
	 *        user user to specify parameters and/or informing the user about the results.
	 */
	public abstract void decorate(JDialog aOwner, JFreeChart aChart, ComplexParamVisualizer aVisualizer, boolean aVerbose);

	/**
	 * Removes the decoration added to the specified chart.
	 * <p>
	 * If the chart was not decorated by this decorator, calling this method has no effect.
	 * </p>
	 * 
	 * @param aChart Chart to be cleared of the decoration previously added.
	 */
	public abstract void undecorate(JFreeChart aChart);

	/**
	 * Gets the label of the decorator's button.
	 * <p>
	 * The return value of this method typically depends on the state of the decorator, as returned
	 * by the {@link #isActive()} method.
	 * </p>
	 * 
	 * @return Label for the button in the form of a <code>String</code> instance.
	 */
	public abstract String getButtonLabel();

	/**
	 * Gets the tooltip for the decorator's button.
	 * <p>
	 * The return value of this method typically depends on the state of the decorator, as returned
	 * by the {@link #isActive()} method.
	 * </p>
	 * 
	 * @return Tooltip for the button in the form of a <code>String</code> instance;
	 *         <code>null</code> if no tooltip is to be displayed.
	 */
	public abstract String getButtonToolTip();

	/**
	 * Checks if the decorator is currently active.
	 * 
	 * @return <code>true</code> if the decorator is active; <code>false</code> otherwise.
	 */
	public abstract boolean isActive();
}
