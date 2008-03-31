/**
 * Chart2D, a java library for drawing two dimensional charts.
 * Copyright (C) 2001 Jason J. Simas
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * The author of this library may be contacted at:
 * E-mail:  jjsimas@users.sourceforge.net
 * Street Address:  J J Simas, 887 Tico Road, Ojai, CA 93023-3555 USA
 */


package net.sourceforge.chart2d;


import java.awt.*;
import java.util.*;


/**
 * An abstract class for the common methods of LBChart2D and LLChart2D.
 * A GraphChart2D object is an area that contains axes and one or more overlaid graphs.
 * Changes through its set methods are updated upon next repaint() or getImage() calls.
 */
public abstract class GraphChart2D extends Chart2D {


  /**
   * Indicates a LBGraphArea.
   */
  static final int LABELS_BOTTOM = 0;

  /**
   * Indicates a LLGraphArea.
   */
  static final int LABELS_LEFT = 1;


  private GraphChart2DProperties graphChart2DProps;
  private Vector graphPropsVector = new Vector (5, 5);
  private Vector datasetsVector = new Vector (5, 5);
  private Vector multiColorsPropsVector = new Vector (5, 5);
  private Vector warningRegionPropsVector = new Vector (5, 5);
  private boolean needsUpdate;


  /**
   * Creates a GraphChart2D object with its defaults.
   * A GraphChart2DProperties object must be set for this object before it is used.
   * A GraphProperties object must be added for this object before it is used.
   * A Dataset object must be added for this object before it is used.
   * A MultiColorsProperties object must be added for this object before it is used.
   */
  public GraphChart2D() {
    needsUpdate = true;
  }


  /**
   * Sets the GraphChart2DProperties for this GraphChart2D.
   * @param props The GraphChart2DProperties.
   */
  public final void setGraphChart2DProperties (GraphChart2DProperties props) {

    needsUpdate = true;
    props.addGraphChart2D (this);
    if (graphChart2DProps != null) graphChart2DProps.removeGraphChart2D (this);
    graphChart2DProps = props;
  }


  /**
   * Sets the allocation of space to each component of a graph chart.
   * There are four components:  numbers axis, labels axis, graph, and legend.
   * The ratios of the axes can be determined from the ratios of the graph and the legend.
   * Depending on the chart type, the left and bottom axes can be the numbers axis and labels axis.
   * The width of the left axis is 1f - graphW - legendW.
   * The width of the bottom axis is graphW.
   * The height of the left axis is graphH.
   * The ratio of the legend height is always 1f.
   * @param graphW   The ratio of graph width to total.
   * @param graphH   The ratio of graph height to total.
   * @param legendW  The ratio of graph width to total.
   * @param legendH  The ratio of legend height to total.
   */
  public final void setLayoutRatios (float graphW, float graphH, float legendW) {

    needsUpdate = true;
    GraphChartArea chart = (GraphChartArea)getObjectArea();

    chart.setGraphToWidthRatio (graphW);
    chart.setGraphToHeightRatio (graphH);
    chart.setLegendToWidthRatio (legendW);
    chart.setLegendToHeightRatio (1f);

    float numbersW, numbersH, labelsW, labelsH;
    if (getGraphChartType() == LABELS_BOTTOM) {

      numbersW = 1f - graphW - legendW;
      numbersH = graphH;
      labelsW = graphW;
      labelsH = 1f - graphH;

      chart.setYAxisToWidthRatio (numbersW);
      chart.setYAxisToHeightRatio (numbersH);
      chart.setXAxisToWidthRatio (labelsW);
      chart.setXAxisToHeightRatio (labelsH);
    }
    else {

      numbersW = graphW;
      numbersH = 1 - graphH;
      labelsW = 1 - graphW - legendW;
      labelsH = graphH;

      chart.setYAxisToWidthRatio (labelsW);
      chart.setYAxisToHeightRatio (labelsH);
      chart.setXAxisToWidthRatio (numbersW);
      chart.setXAxisToHeightRatio (numbersH);
    }

    chart.setAutoSetLayoutRatios (true);
  }


  /**
   * Gets the GraphChart2DProperties for this Chart2D.
   * @param props The GraphChart2DProperties.
   */
  public final GraphChart2DProperties getGraphChart2DProperties() {
    return graphChart2DProps;
  }


  /**
   * Gets a graph properties based on the order the graph properties were added.
   * First added is number zero.
   * @return The graph properties object added to this chart.
   */
  public final GraphProperties getGraphProperties (int i) {
    return (GraphProperties)graphPropsVector.get (i);
  }


  /**
   * Gets a dataset based on the order the dataset was added.
   * First added is number zero.
   * @return The dataset added to this chart.
   */
  public final Dataset getDataset (int i) {
    return (Dataset)datasetsVector.get (i);
  }


  /**
   * Gets a multiColorsProps based on the order the multiColorsProps was added.
   * First added is number zero.
   * @return The multiColorsProps added to this chart.
   */
  public final MultiColorsProperties getMultiColorsProperties (int i) {
    return (MultiColorsProperties)multiColorsPropsVector.get (i);
  }


  /**
   * Gets a warning region properties based on the order the properties were added.
   * First added is number zero.
   * @return The warning region properties object added to this chart.
   */
  public final WarningRegionProperties getWarningRegionProperties (int i) {
    return (WarningRegionProperties)warningRegionPropsVector.get (i);
  }


  /**
   * Adds a GraphProperties object to this chart.
   * @param graphProps The graph properties to add to this chart.
   */
  public final void addGraphProperties (GraphProperties graphProps) {
    needsUpdate = true;
    graphProps.addGraphChart2D (this);
    graphPropsVector.add (graphProps);
  }


  /**
   * Adds a Dataset object to this chart.
   * @param dataset The dataset to add to this chart.
   */
  public final void addDataset (Dataset dataset) {
    needsUpdate = true;
    dataset.addChart2D (this);
    datasetsVector.add (dataset);
  }


  /**
   * Adds a MultiColorsProperties object to this chart.
   * @param multiColorsProps The multi colors properties to add to this chart.
   */
  public final void addMultiColorsProperties (MultiColorsProperties multiColorsProps) {
    needsUpdate = true;
    multiColorsProps.addObject2D (this);
    multiColorsPropsVector.add (multiColorsProps);
  }


  /**
   * Adds a WarningRegionProperties object to this chart.
   * @param warningRegion The warning region properties to add to this chart.
   */
  public final void addWarningRegionProperties (WarningRegionProperties warningRegionProps) {
    needsUpdate = true;
    warningRegionProps.addGraphChart2D (this);
    warningRegionPropsVector.add (warningRegionProps);
  }


  /**
   * Removes GraphProperties object from this chart.
   * @param graphProps The graph properties to remove from this chart.
   */
  public final void removeGraphProperties (GraphProperties graphProps) {
    needsUpdate = true;
    graphProps.removeGraphChart2D (this);
    graphPropsVector.remove (graphProps);
  }


  /**
   * Removes a Dataset object from this chart.
   * @param dataset The dataset to remove from this chart.
   */
  public final void removeDataset (Dataset dataset) {
    needsUpdate = true;
    dataset.removeChart2D (this);
    datasetsVector.remove (dataset);
  }


  /**
   * Removes a MultiColorsProperties object from this chart.
   * @param multiColorsProperties The multi colors properties to remove from this chart.
   */
  public final void removeMultiColorsProperties (MultiColorsProperties multiColorsProps) {
    needsUpdate = true;
    multiColorsProps.removeObject2D (this);
    multiColorsPropsVector.remove (multiColorsProps);
  }


  /**
   * Removes a warning region with the specified properties to this chart.
   * @param warningRegion The warning region to remove from this chart.
   */
  public final void removeWarningRegionProperties (WarningRegionProperties warningRegionProps) {
    needsUpdate = true;
    warningRegionProps.removeGraphChart2D (this);
    warningRegionPropsVector.remove (warningRegionProps);
  }


  /**
   * Returns either LABELS_BOTTOM or LABELS_LEFT depending on whether LBChart2D or LLChart2D.
   * @return The type of the chart.
   */
  abstract int getGraphChartType();


  /**
   * Gets the number of sets total over all the Dataset objects.
   * @return The total number of sets.
   */
  final int getNumSetsTotal() {

    int numSets = 0;
    for (int i = 0; i < datasetsVector.size(); ++i) {
      numSets += ((Dataset)datasetsVector.get (i)).getNumSets();
    }
    return numSets;
  }


  /**
   * Gets the vector of GraphProperties objects that were added.
   * @return The graph properties objects vector.
   */
  final Vector getGraphPropertiesVector() {
    return graphPropsVector;
  }


  /**
   * Gets the vector of Dataset objects that were added.
   * @return The dataset objects vector.
   */
  final Vector getDatasetsVector() {
    return datasetsVector;
  }


  /**
   * Gets the vector of MultiColorsProperties objects that were added.
   * @return The multi colors properties objects vector.
   */
  final Vector getMultiColorsPropertiesVector() {
    return multiColorsPropsVector;
  }


  /**
   * Gets the vector of WarningRegionProperties objects that were added.
   * @return The warning region properties objects vector.
   */
  final Vector getWarningRegionPropertiesVector() {
    return warningRegionPropsVector;
  }


  /**
   * Gets whether this object needs to be updated.
   * @return If true then needs update.
   */
  final boolean getNeedsUpdateGraphChart2D() {

    if (needsUpdate) return true;
    if (getNeedsUpdateChart2D()) return true;
    if (graphChart2DProps.getGraphChart2DNeedsUpdate (this)) return true;

    for (int i = 0; i < graphPropsVector.size(); ++i) {
      if (((GraphProperties)graphPropsVector.get (i)).
        getGraphChart2DNeedsUpdate (this)) return true;
    }
    for (int i = 0; i < datasetsVector.size(); ++i) {
      if (((Dataset)datasetsVector.get (i)).
        getChart2DNeedsUpdate (this)) return true;
    }
    for (int i = 0; i < multiColorsPropsVector.size(); ++i) {
      if (((MultiColorsProperties)multiColorsPropsVector.get (i)).
        getObject2DNeedsUpdate (this)) return true;
    }
    for (int i = 0; i < warningRegionPropsVector.size(); ++i) {
      if (((WarningRegionProperties)warningRegionPropsVector.get (i)).
        getGraphChart2DNeedsUpdate (this)) return true;
    }

    return false;
  }


  /**
   * Validates the properties of this object.
   * If debug is true then prints a messages indicating whether each property is valid.
   * Returns true if all the properties were valid and false otherwise.
   * @param debug If true then will print status messages.
   * @return If true then valid.
   */
  final boolean validateGraphChart2D (boolean debug) {

    if (debug) System.out.println ("Validating GraphChart2D");

    boolean valid = true;

    if (!validateChart2D (debug)) valid = false;

    GraphChartArea chart = (GraphChartArea)getObjectArea();

    if (
      chart.getXAxisToWidthRatio() < 0f || chart.getXAxisToWidthRatio() > 1f ||
      chart.getXAxisToHeightRatio() < 0f || chart.getXAxisToHeightRatio() > 1f ||
      chart.getYAxisToWidthRatio() < 0f || chart.getYAxisToWidthRatio() > 1f ||
      chart.getYAxisToHeightRatio() < 0f || chart.getYAxisToHeightRatio() > 1f ||
      chart.getGraphToWidthRatio() < 0f || chart.getGraphToWidthRatio() > 1f ||
      chart.getGraphToHeightRatio() < 0f || chart.getGraphToHeightRatio() > 1f ||
      chart.getLegendToWidthRatio() < 0f || chart.getLegendToWidthRatio() > 1f ||
      chart.getLegendToHeightRatio() < 0f || chart.getLegendToHeightRatio() > 1f) {
      valid = false;
      if (debug) System.out.println ("Chart components ratios need to be between 0 and 1");
    }

    if (graphChart2DProps == null) {
      valid = false;
      if (debug) System.out.println ("GraphChart2DProperties is null");
    }
    else if (!graphChart2DProps.validate (debug)) valid = false;

    //Restriction:  Number of GraphProperties must equal number of Datasets and MultiColorsProps
    if (graphPropsVector.size() != datasetsVector.size() ||
      datasetsVector.size() != multiColorsPropsVector.size()) {
      valid = false;
      if (debug) System.out.println (
        "Number of GraphProperties, Datasets, and MultiColorsPropertes objects must be equal");
    }

    //Restriction:  At least one object of each GraphProperties, Dataset, and MultiColorsProperties
    if (valid) {
      if (graphPropsVector.size() < 1) {
        valid = false;
        if (debug) System.out.println (
          "Need at least one of each GraphProperties, Dataset, and MultiColorsProperties");
      }
    }

    if (valid) {

      for (int i = 0; i < graphPropsVector.size(); ++i) {

        if (debug) System.out.println ("Checking GraphProperties Object " + i);
        if (!((GraphProperties)graphPropsVector.get (i)).validate (debug)) valid = false;
      }
      for (int i = 0; i < datasetsVector.size(); ++i) {

        if (debug) System.out.println ("Checking Datasets Object " + i);
        if (!((Dataset)datasetsVector.get (i)).validate (debug)) valid = false;
      }
      for (int i = 0; i < multiColorsPropsVector.size(); ++i) {

        if (debug) System.out.println ("Checking MultiColorsProperties Object " + i);
        if (
          !((MultiColorsProperties)multiColorsPropsVector.get (i)).validate (debug)) valid = false;
      }
      for (int i = 0; i < warningRegionPropsVector.size(); ++i) {

        if (debug) System.out.println ("Checking WarningRegionProperties Object " + i);
        if (!((WarningRegionProperties)warningRegionPropsVector.get (i)).validate (debug)) {
          valid = false;
        }
      }
    }

    //Restriction:  There must be a legend label for each dataset, if the legend exists.
    if (valid) {

      if (getLegendProperties().getLegendExistence() &&
        getLegendProperties().getLegendLabelsTexts().length != getNumSetsTotal()) {
        valid = false;
        if (debug) System.out.println (
          "Number of legend labels must equal total number of sets for all datasets");
      }
    }

    //Restriction:  If dataset at least one and multi colors custom, then at least one custom color
    if (valid) {

      for (int i = 0; i < datasetsVector.size(); ++i) {

        MultiColorsProperties multiColorsProps =
          (MultiColorsProperties)multiColorsPropsVector.get (i);
        if (((Dataset)datasetsVector.get (i)).getNumSets() > 0 &&
          multiColorsProps.getColorsCustomize() &&
          multiColorsProps.getColorsCustom().length < 1) {
            valid = false;
          if (debug) System.out.println ("Not enough custom colors for MultiColorsProperties " + i);
        }
      }
    }

    //Restriction:  All datasets must have equal num cats.
    if (valid) {

      int numCats = ((Dataset)datasetsVector.get (0)).getNumCats();
      for (int i = 1; i < datasetsVector.size(); ++i) {

        if (numCats != ((Dataset)datasetsVector.get (i)).getNumCats()) {
          valid = false;
          if (debug) System.out.println (
            "Dataset doesn't have same number of cats as first dataset " + i);
        }
      }
    }

    //Restriction:  If labels axis exists, then num labels must equal num dataset cats
    if (valid) {

      if (graphChart2DProps.getLabelsAxisExistence() &&
        graphChart2DProps.getLabelsAxisLabelsTexts().length !=
        ((Dataset)datasetsVector.get (0)).getNumCats()) {
        valid = false;
        if (debug) System.out.println (
          "Number of labels axis labels must equal number of cats in each dataset");
      }
    }

    //Restriction:  If cat coloring and num cats > 0, then cat colors > 0
    if (valid) {

      if (graphChart2DProps.getGraphComponentsColoringByCat() &&
      ((Dataset)datasetsVector.get (0)).getNumCats() > 0 &&
      graphChart2DProps.getGraphComponentsColorsByCat().getColorsCustomize() &&
      graphChart2DProps.getGraphComponentsColorsByCat().getColorsCustom().length < 1) {
        valid = false;
        if (debug) System.out.println ("Not enough custom colors for cat coloring");
      }
    }

    if (debug) {
      if (valid) System.out.println ("GraphChart2D was valid");
      else {
        System.out.println (
          "Possibly unable to check for all invalidities because of early invalidity");
        System.out.println ("GraphChart2D was invalid");
      }
    }

    return valid;
  }


  /**
   * Updates this object.
   */
  final void updateGraphChart2D() {

    if (getNeedsUpdateGraphChart2D()) {

      needsUpdate = false;

      updateChart2D();

      graphChart2DProps.updateGraphChart2D (this);
      for (int i = 0; i < datasetsVector.size(); ++i) {
        ((Dataset)datasetsVector.get (i)).updateChart2D (this);
      }
      for (int i = 0; i < multiColorsPropsVector.size(); ++i) {
        ((MultiColorsProperties)multiColorsPropsVector.get (i)).updateObject2D (this);
      }
      for (int i = 0; i < graphPropsVector.size(); ++i) {
        ((GraphProperties)graphPropsVector.get (i)).updateGraphChart2D (this);
      }
      for (int i = 0; i < warningRegionPropsVector.size(); ++i) {
        ((WarningRegionProperties)warningRegionPropsVector.get (i)).updateGraphChart2D (this);
      }

      int chartType = getGraphChartType();
      GraphChartArea graphChart = (GraphChartArea)getObjectArea();

      //configure chart's data
      graphChart.setDatasetVector (datasetsVector);

      //configure chart's graph properties
      GraphProperties backgroundGraphProps =
        (GraphProperties)graphPropsVector.get (graphPropsVector.size() - 1);
      Vector graphVector = new Vector (graphPropsVector.size(), 0);

      for (int i = graphPropsVector.size() - 2; i >= 0; --i) {
        GraphArea graph =
          chartType == LABELS_BOTTOM ? (GraphArea)new LBGraphArea() : (GraphArea)new LLGraphArea();
        ((GraphProperties)graphPropsVector.get (i)).configureGraphArea (
          backgroundGraphProps, chartType, graph);
        graphVector.add (graph);
      }

      GraphArea graph =
        chartType == LABELS_BOTTOM ? (GraphArea)new LBGraphArea() : (GraphArea)new LLGraphArea();
      backgroundGraphProps.configureGraphArea (chartType, graph);
      graphVector.add (graph);

      graphChart.setGraphVector (graphVector);

      //configure warning regions for background graph only
      Vector warningRegionsVector = new Vector (warningRegionPropsVector.size(), 0);
      for (int i = 0; i < warningRegionPropsVector.size(); ++i) {
        WarningRegion warningRegion =
          ((WarningRegionProperties)warningRegionPropsVector.get (i)).configureWarningRegion();
        warningRegion.setGraphType (chartType);
        warningRegionsVector.add (warningRegion);

      }
      graphChart.setWarningRegions (warningRegionsVector);

      //create color array from multi colors, one color for each set
      int numSets = getNumSetsTotal();
      Color[] multiColors = new Color[numSets];
      int k = 0;
      for (int i = 0; i < multiColorsPropsVector.size(); ++i) {
        MultiColorsProperties multiColorsProps =
          (MultiColorsProperties)multiColorsPropsVector.get (i);
        int numSetsThis = ((Dataset)datasetsVector.get (i)).getNumSets();
        Color[] producer = multiColorsProps.getColorsArray (numSetsThis);
        for (int j = 0; j < producer.length; ++j) {
          multiColors[k] = producer[j];
          ++k;
        }
      }
      graphChart.setDatasetColors (multiColors);

      graphChart.setCustomGreatestValue (
        graphChart2DProps.getChartDatasetCustomizeGreatestValue(),
        graphChart2DProps.getChartDatasetCustomGreatestValue());
      graphChart.setCustomLeastValue (
        graphChart2DProps.getChartDatasetCustomizeLeastValue(),
        graphChart2DProps.getChartDatasetCustomLeastValue());
      graphChart.setGraphableToAvailableRatio (
        graphChart2DProps.getChartGraphableToAvailableRatio());
      graphChart.setNumPlotAxisLabels (graphChart2DProps.getNumbersAxisNumLabels());

      graphChart.setGraphComponentsColoringByCat (
        graphChart2DProps.getGraphComponentsColoringByCat());
      if (graphChart2DProps.getGraphComponentsColoringByCat()) {
        graphChart.setGraphComponentsColorsByCat (
          graphChart2DProps.getGraphComponentsColorsByCat().getColorsArray (
          ((Dataset)datasetsVector.get (0)).getNumCats()));
      }

      AxisArea labelsAxis;
      AxisArea numbersAxis;
      TextListArea labelsAxisTextList;
      TextListArea numbersAxisTextList;
      if (chartType == LABELS_BOTTOM) {
        labelsAxis = graphChart.getXAxis();
        numbersAxis = graphChart.getYAxis();
        labelsAxisTextList = ((XAxisArea)labelsAxis).getTextList();
        numbersAxisTextList = ((YAxisArea)numbersAxis).getTextList();
      }
      else {
        labelsAxis = graphChart.getYAxis();
        numbersAxis = graphChart.getXAxis();
        labelsAxisTextList = ((YAxisArea)labelsAxis).getTextList();
        numbersAxisTextList = ((XAxisArea)numbersAxis).getTextList();
      }

      labelsAxis.setTicksAlignment (graphChart2DProps.getLabelsAxisTicksAlignment());
      labelsAxisTextList.setLabels (graphChart2DProps.getLabelsAxisLabelsTexts());

      labelsAxis.setTitleExistence (graphChart2DProps.getLabelsAxisTitleExistence());
      labelsAxis.setTitle (graphChart2DProps.getLabelsAxisTitleText());
      labelsAxis.setFontPointModel (graphChart2DProps.getLabelsAxisTitleFontPointModel());
      labelsAxis.setFontName (graphChart2DProps.getLabelsAxisTitleFontName());
      labelsAxis.setFontColor (graphChart2DProps.getLabelsAxisTitleFontColor());
      labelsAxis.setFontStyle (graphChart2DProps.getLabelsAxisTitleFontStyle());
      labelsAxis.setBetweenTitleAndSpaceGapExistence (
        graphChart2DProps.getLabelsAxisTitleBetweenRestGapExistence());
      labelsAxis.setBetweenTitleAndSpaceGapThicknessModel (
        graphChart2DProps.getLabelsAxisTitleBetweenRestGapThicknessModel());
      labelsAxis.setTicksSizeModel (graphChart2DProps.getLabelsAxisTicksExistence() ?
        graphChart2DProps.getLabelsAxisTicksSizeModel() : new Dimension());
      labelsAxis.setTicksColor (graphChart2DProps.getLabelsAxisTicksColor());
      labelsAxisTextList.setBulletsOutline (graphChart2DProps.getLabelsAxisTicksOutlineExistence());
      labelsAxisTextList.setBulletsOutlineColor (graphChart2DProps.getLabelsAxisTicksOutlineColor());
      labelsAxisTextList.setFontPointModel (graphChart2DProps.getLabelsAxisLabelsFontPointModel());
      labelsAxisTextList.setFontName (graphChart2DProps.getLabelsAxisLabelsFontName());
      labelsAxisTextList.setFontColor (graphChart2DProps.getLabelsAxisLabelsFontColor());
      labelsAxisTextList.setFontStyle (graphChart2DProps.getLabelsAxisLabelsFontStyle());
      labelsAxisTextList.setBetweenBulletsGapExistence (
        graphChart2DProps.getLabelsAxisBetweenLabelsOrTicksGapExistence());
      labelsAxisTextList.setBetweenLabelsGapExistence (
        graphChart2DProps.getLabelsAxisBetweenLabelsOrTicksGapExistence());
      labelsAxisTextList.setBetweenBulletsGapThicknessModel (
        graphChart2DProps.getLabelsAxisBetweenLabelsOrTicksGapThicknessModel());
      labelsAxisTextList.setBetweenLabelsGapThicknessModel (
        graphChart2DProps.getLabelsAxisBetweenLabelsOrTicksGapThicknessModel());
      labelsAxisTextList.setBetweenBulletsAndLabelsGapExistence (
        graphChart2DProps.getLabelsAxisBetweenLabelsAndTicksGapExistence());
      labelsAxisTextList.setBetweenBulletsAndLabelsGapThicknessModel (
        graphChart2DProps.getLabelsAxisBetweenLabelsAndTicksGapThicknessModel());

      if (!graphChart2DProps.getLabelsAxisExistence()) {
        int numCats = ((Dataset)datasetsVector.get (0)).getNumCats();
        labelsAxis.setTitleExistence (false);
        labelsAxisTextList.setBetweenBulletsAndLabelsGapExistence (false);
        labelsAxisTextList.setBetweenBulletsGapExistence (false);
        labelsAxisTextList.setBetweenLabelsGapExistence (false);
        labelsAxisTextList.setBulletsSizeModel (new Dimension());
        labelsAxisTextList.setFontPointModel (0);
        String[] labels = new String[numCats];
        for (int i = 0; i < numCats; ++i) labels[i] = "";
        labelsAxisTextList.setLabels (labels);
      }

      numbersAxis.setTitleExistence (graphChart2DProps.getNumbersAxisTitleExistence());
      numbersAxis.setTitle (graphChart2DProps.getNumbersAxisTitleText());
      numbersAxis.setFontPointModel (graphChart2DProps.getNumbersAxisTitleFontPointModel());
      numbersAxis.setFontName (graphChart2DProps.getNumbersAxisTitleFontName());
      numbersAxis.setFontColor (graphChart2DProps.getNumbersAxisTitleFontColor());
      numbersAxis.setFontStyle (graphChart2DProps.getNumbersAxisTitleFontStyle());
      numbersAxis.setBetweenTitleAndSpaceGapExistence (
        graphChart2DProps.getNumbersAxisTitleBetweenRestGapExistence());
      numbersAxis.setBetweenTitleAndSpaceGapThicknessModel (
        graphChart2DProps.getNumbersAxisTitleBetweenRestGapThicknessModel());
      numbersAxis.setTicksSizeModel (graphChart2DProps.getNumbersAxisTicksExistence() ?
        graphChart2DProps.getNumbersAxisTicksSizeModel() : new Dimension());
      numbersAxis.setTicksColor (graphChart2DProps.getNumbersAxisTicksColor());
      numbersAxisTextList.setBulletsOutline (graphChart2DProps.getNumbersAxisTicksOutlineExistence());
      numbersAxisTextList.setBulletsOutlineColor (graphChart2DProps.getNumbersAxisTicksOutlineColor());
      numbersAxisTextList.setFontPointModel (graphChart2DProps.getNumbersAxisLabelsFontPointModel());
      numbersAxisTextList.setFontName (graphChart2DProps.getNumbersAxisLabelsFontName());
      numbersAxisTextList.setFontColor (graphChart2DProps.getNumbersAxisLabelsFontColor());
      numbersAxisTextList.setFontStyle (graphChart2DProps.getNumbersAxisLabelsFontStyle());
      numbersAxisTextList.setBetweenBulletsGapExistence (
        graphChart2DProps.getNumbersAxisBetweenLabelsOrTicksGapExistence());
      numbersAxisTextList.setBetweenLabelsGapExistence (
        graphChart2DProps.getNumbersAxisBetweenLabelsOrTicksGapExistence());
      numbersAxisTextList.setBetweenBulletsGapThicknessModel (
        graphChart2DProps.getNumbersAxisBetweenLabelsOrTicksGapThicknessModel());
      numbersAxisTextList.setBetweenLabelsGapThicknessModel (
        graphChart2DProps.getNumbersAxisBetweenLabelsOrTicksGapThicknessModel());
      numbersAxisTextList.setBetweenBulletsAndLabelsGapExistence (
        graphChart2DProps.getNumbersAxisBetweenLabelsAndTicksGapExistence());
      numbersAxisTextList.setBetweenBulletsAndLabelsGapThicknessModel (
        graphChart2DProps.getNumbersAxisBetweenLabelsAndTicksGapThicknessModel());
    }
  }
}