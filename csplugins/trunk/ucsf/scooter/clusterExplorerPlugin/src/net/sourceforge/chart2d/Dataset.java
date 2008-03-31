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


import java.util.Vector;
import java.awt.geom.*;


/**
 * The container for the data values to chart.
 * A dataset is like a third order array (ex. float[][][]).  The first order is
 * the "sets" order.  A set contains data divided by category.  The "sets" refer
 * to the objects described by the legend labels, if a legend exists.  The
 * second order is the "cats" or categories order.  Data within a set can be
 * divided by category.  A category can have multiple data items.  If using a
 * graph chart, the categories are described by the labels-axis labels.  For pie
 * charts, there is only one category.  The third order is the "items" order.
 * The "items" order are the values for a particular category and set.<br>
 * For example, if we had data for the years 1999 and 2000 of how many units were
 * sold on each day during that time, then one way to chart this would be using
 * a graph chart and making the years correspond to the "sets" order, "months",
 * correspond to the "cats" order, and the number of units sold per day
 * correspond to the "items" order.<br>
 * There are two important rules to
 * mention about this.  For each set, the number of cats must be the same.  For
 * each cat, the number of items must be the same.  In our example, we would
 * probably want to choose the number of items per category to be 30,
 * corresponding the average number of days per month.  For months with less
 * than thirty days, we would populate the unfilled days with the average of the
 * filled days or simply carry the last value of the filled days into the
 * unfilled days.  For months with more than thirty days, we would average the
 * last two days, and use that value as the value of the last day.<br>
 * If passing to a PieChart2DProperties, the value used for each pie sector is
 * the sum of the values in each set of data.
 * Pass this to any number of PieChart2D or GraphChart2D objects.
 */
public final class Dataset {


  private Vector setVector;
  private float greatest;
  private float least;

  private boolean needsUpdate = true;
  private Vector needsUpdateVector = new Vector (5, 5);
  private Vector chart2DVector = new Vector (5, 5);


  /**
   * Creates a dataset with 0 sets, 0 categories, and 0 items.
   * Use the add (int, int, int, float) method to add data items.
   */
  public Dataset() {

    needsUpdate = true;
    setDatasetToDefaults();
  }


  /**
   * Creates a dataset with the specified number of sets, categories per set,
   * and items per set and per category.
   * All internal objects are created at once.
   * Use the set (int, int, int, float) method to add data items.
   * @param sets The number of sets.
   * @param cats The number of categories per set.
   * @param items The number of items per category and per set.
   */
  public Dataset (int sets, int cats, int items) {

    needsUpdate = true;
    constructor (sets, cats, items);
  }


  /**
   * Creates a dataset that is a copy of another dataset.
   * Copying is a deep copy.
   * @param dataset The dataset to copy.
   */
  public Dataset (Dataset dataset) {

    needsUpdate = true;
    set (dataset);
  }


  /**
   * Sets the dataset to its default state.
   * It's default state is a dataset with no sets, no cats, and no items.
   */
  public final void setDatasetToDefaults() {

    needsUpdate = true;
    constructor (0, 0, 0);
  }


  /**
   * Sets the value for the variable in the set specified by set, the
   * category specified by cat within the set, and the item specified by item
   * within the category.
   * If internal resources have not yet been allocated for this item, a null pointer will occurr.
   * @param set The specified set of the variable.
   * @param cat The specified cat of the variable.
   * @param item The specified item of the variable.
   * @param value The specified value of the variable.
   */
   public final void set (int set, int cat, int item, float value) {

    needsUpdate = true;
    ((Vector)((Vector)setVector.get (set)).get (cat)).set (item, new Float (value));
   }


  /**
   * Sets all the values of this dataset from another Dataset.
   * The values are copied using a deep copy.
   * @param dataset The Dataset to copy.
   */
  public final void set (Dataset dataset) {

    needsUpdate = true;
    constructor (dataset.getNumSets(), dataset.getNumCats(), dataset.getNumItems());
    int numSets = getNumSets();
    int numCats = getNumCats();
    int numItems = getNumItems();
    for (int i = 0; i < numSets; ++i) {
      for (int j = 0; j < numCats; ++j) {
        for (int k = 0; k < numItems; ++k) {
          set (i, j, k, dataset.get (i, j, k));
        }
      }
    }
  }


  /**
   * Resets the size of the dataset.  Similar to the constructor Dataset (sets, cats, items).
   * If current size is smaller, then fills with zeroes.
   * @param sets The number of sets.
   * @param cats The number of categories per set.
   * @param items The number of items per category and per set.
   */
  public final void setSize (int sets, int cats, int items) {

    needsUpdate = true;
    //correct number of sets
    if (setVector.size() > sets) setVector.setSize (sets);
    for (int i = setVector.size(); i < sets; ++i) setVector.add (new Vector (cats, 10));
    //correct number of cats in sets
    for (int i = 0; i < sets; ++i) {
      Vector catVector = (Vector)setVector.get (i);
      if (catVector.size() > cats) catVector.setSize (cats);
      for (int j = catVector.size(); j < cats; ++j) catVector.add (new Vector (items, 10));
    }
    //correct number of items in cats
    for (int i = 0; i < sets; ++i) {
      Vector catVector = (Vector)setVector.get (i);
      for (int j = 0; j < cats; ++j) {
        Vector itemVector = (Vector)catVector.get (j);
        if (itemVector.size() > items) itemVector.setSize (items);
        for (int k = itemVector.size(); k < items; ++k) itemVector.add (new Float (0f));
      }
    }
  }


  /**
   * Gets the value for the variable in the set specified by set, the
   * category specified by cat within the set, and the item specified by item
   * within the category.  If internal resources have not yet been allocated to
   * contain such a variable a null pointer error may occurr.
   * @param set The specified set of the variable.
   * @param cat The specified cat of the variable.
   * @param item The specified item of the variable.
   * @return float The value of this variable.
   */
  public final float get (int set, int cat, int item) {

    return ((Float)((Vector)((Vector)setVector.get (set)).get (cat)).get (item)).floatValue();
  }


  /**
   * Gets the number of sets of data in this dataset.
   * @return int The number of sets of this dataset.
   */
  public final int getNumSets() {
    return setVector.size();
  }


  /**
   * Gets the number of categories per set of data in this dataset.  This
   * method requires that the dataset be valid according to the method
   * validate().  If not, then a null pointer error may occurr.
   * @return int The number of cats per set of this dataset.
   */
  public final int getNumCats() {

    if (getNumSets() > 0) return ((Vector)setVector.get (0)).size();
    else return 0;
  }


  /**
   * Gets the number of items per category of data in this dataset.  This
   * method requires that the dataset be valid according to the method
   * validate().  If not, then a null pointer error may occurr.
   * @return int The number of items per category of this dataset.
   */
  public final int getNumItems() {

    if (getNumSets() > 0 && getNumCats() > 0) {
      return ((Vector)(((Vector)setVector.get (0))).get (0)).size();
    }
    else return 0;
  }


  /**
   * Gets the greatest value of all the data in the datset.  If the dataset
   * is invalid or empty, then returns Float.MIN_VALUE;
   * @return float The greatest value in the dataset.
   */
  public final float getGreatest() {

    update();
    return greatest;
  }


  /**
   * Gets the least value of all the data in the datset.  If the dataset
   * is invalid or empty, then returns Float.MAX_VALUE;
   * @return float The least value in the dataset.
   */
  public final float getLeast() {

    update();
    return least;
  }


  /**
   * Gets the average of some set of numbers.
   * There are three possibilities.
   * If you want the average of all data items in a particular set and cat, then pass -1 for item.
   * If you want the average of all data items in a particular set and item, then pass -1 for cat.
   * If you want the average of all data items in a particular cat and item, then pass -1 for set.
   * Note, only one -1 may be passed in over all three parameters.
   * @param set Which particular set or -1 for all.
   * @param cat Which particular cat or -1 for all.
   * @param item Which particular item or -1 for all.
   */
  public final float getAverage (int set, int cat, int item) {

    float average = 0f;
    if (getNumSets() > 0 && getNumCats() > 0 && getNumItems() > 0) {
      if (set == -1) {
        float sum = 0f;
        for (int i = 0; i < getNumSets(); ++i) {
          sum += get (i, cat, item);
        }
        average = sum / getNumSets();
      }
      if (cat == -1) {
        float sum = 0f;
        for (int j = 0; j < getNumCats(); ++j) {
          sum += get (set, j, item);
        }
        average = sum / getNumCats();
      }
      if (item == -1) {
        float sum = 0f;
        for (int k = 0; k < getNumItems(); ++k) {
          sum += get (set, cat, k);
        }
        average = sum / getNumItems();
      }
    }
    return average;
  }


  /**
   * Adds a value to the dataset increasing its internal data structure if necessary.
   * @param set The specified set of the variable.
   * @param cat The specified cat of the variable.
   * @param item The specified item of the variable.
   * @param value The specified value of the variable.
   */
  public final void add (int set, int cat, int item, float value) {

    needsUpdate = true;
    for (int i = setVector.size(); i <= set; ++i) {
      setVector.add (new Vector (10, 10));
    }
    Vector catVector = (Vector)setVector.get (set);
    for (int i = catVector.size(); i <= cat; ++i) {
      catVector.add (new Vector (10, 10));
    }
    Vector itemVector = (Vector)catVector.get (cat);
    for (int i = itemVector.size(); i <= item; ++i) {
      itemVector.add (new Float (0f));
    }
    itemVector.set (item, new Float (value));
  }


  /**
   * Analyzes the (input) dataset and adds moving average trend data to this dataset.
   * Moving averages are computed by taking some odd number of adjacent items, computing their
   * average and doing that from left to right for each item.  How many adjacent items are used in
   * computing the moving average is specified in the scope parameter.
   * @param dataset The dataset to analyze.
   * @param scope The number of data points to compute over.
   */
  public final void addMovingAverage (Dataset dataset, int scope) {

    //make sure there is enough data
    int numItemsTotal = dataset.getNumSets() > 0 ? dataset.getNumCats() * dataset.getNumItems() : 0;

    if (numItemsTotal >= 3) { //do moving average calculation

      //make sure scope is valid
      if (scope < 3) scope = 3;
      else {
        if (scope > numItemsTotal) scope = numItemsTotal;
        if (scope % 2 == 0) scope--;
      }

      //begin moving average calculation
      int internalSetOffset = getNumSets();  //offset in case this object holds other sets of data
      int itemOffset = (scope / 2);  //location of first real moving average data
      Vector itemGroup = new Vector (scope); //create enough data holders

      //prepare for first real moving average data
      ItemsForSet items = new ItemsForSet (dataset, 0, 0);
      itemGroup.add (items);
      for (int i = 0; i < scope - 1; ++i) {
        items = items.getNextItemsForSet();
        itemGroup.add (items);
      }

      //get the first moving average data (guaranteed)
      ItemsForSet firstItems = (ItemsForSet)itemGroup.get (0);  //hold first item for later
      items = (ItemsForSet)itemGroup.get (itemOffset);
      float firstAverage = getAverageOfItemsForSetGroup (itemGroup);
      add (internalSetOffset, items.getCat(), items.getItem(), firstAverage);
      itemGroup.remove (0);
      itemGroup.add (((ItemsForSet)itemGroup.lastElement()).getNextItemsForSet());  //maybe null

      //get the rest except for the last real moving average data
      for (int i = itemOffset + 1; i < numItemsTotal - itemOffset - 1; ++i) {
        items = (ItemsForSet)itemGroup.get (itemOffset);
        add (internalSetOffset, items.getCat(), items.getItem(),
          getAverageOfItemsForSetGroup (itemGroup));
        itemGroup.remove (0);
        itemGroup.add(((ItemsForSet)itemGroup.lastElement()).getNextItemsForSet());
      }

      //check if only one real moving average data
      float lastAverage = firstAverage;
      ItemsForSet lastItems = items.getNextItemsForSet(); //okay for 3,3
      if (numItemsTotal > scope) {

        //get the last real moving average data
        items = (ItemsForSet)itemGroup.get (itemOffset);
        lastAverage = getAverageOfItemsForSetGroup (itemGroup);
        add (internalSetOffset, items.getCat(), items.getItem(), lastAverage);
        lastItems = items.getNextItemsForSet();
      }

      //populate beginning with fake moving average data
      for (int i = 0; i < itemOffset; ++i) {
        add (internalSetOffset, firstItems.getCat(), firstItems.getItem(), firstAverage);
        firstItems = firstItems.getNextItemsForSet();
      }

      //populate end with fake moving average data
      for (int i = numItemsTotal - itemOffset; i < numItemsTotal; ++i) {
        add (internalSetOffset, lastItems.getCat(), lastItems.getItem(), lastAverage);
        lastItems = lastItems.getNextItemsForSet();
      }
    }
    else if (numItemsTotal > 0) { //do standard average calculation
      int currSet = setVector.size();
      for (int j = 0; j < dataset.getNumCats(); ++j) {
        for (int k = 0; k < dataset.getNumItems(); ++k) {
          add (currSet, j, k, dataset.getAverage (-1, j, k));
        }
      }
      needsUpdate = true;
    }
  }


  /**
   * Analyzes the (input) dataset and sets moving average trend data to this dataset.
   * Moving averages are computed by taking some odd number of adjacent items, computing their
   * average and doing that from left to right for each item.  How many adjacent items are used in
   * computing the moving average is specified in the scope parameter.
   * Assumes this dataset already has the same number of cats and items, and that the set exists.
   * @param set The set into this dataset to put the moving average.
   * @param dataset The dataset to analyze.
   * @param scope The number of data points to compute over.
   */
  public final void setMovingAverage (int set, Dataset dataset, int scope) {

    //make sure there is enough data
    int numItemsTotal = dataset.getNumSets() > 0 ? dataset.getNumCats() * dataset.getNumItems() : 0;

    if (numItemsTotal >= 3) { //do moving average calculation

      //make sure scope is valid
      if (scope < 3) scope = 3;
      else {
        if (scope > numItemsTotal) scope = numItemsTotal;
        if (scope % 2 == 0) scope--;
      }

      //begin moving average calculation
      int internalSetOffset = set;  //offset in case this object holds other sets of data
      int itemOffset = (scope / 2);  //location of first real moving average data
      Vector itemGroup = new Vector (scope); //create enough data holders

      //prepare for first real moving average data
      ItemsForSet items = new ItemsForSet (dataset, 0, 0);
      itemGroup.add (items);
      for (int i = 0; i < scope - 1; ++i) {
        items = items.getNextItemsForSet();
        itemGroup.add (items);
      }

      //get the first moving average data (guaranteed)
      ItemsForSet firstItems = (ItemsForSet)itemGroup.get (0);  //hold first item for later
      items = (ItemsForSet)itemGroup.get (itemOffset);
      float firstAverage = getAverageOfItemsForSetGroup (itemGroup);
      set (internalSetOffset, items.getCat(), items.getItem(), firstAverage);
      itemGroup.remove (0);
      itemGroup.add (((ItemsForSet)itemGroup.lastElement()).getNextItemsForSet());  //maybe null

      //get the rest except for the last real moving average data
      for (int i = itemOffset + 1; i < numItemsTotal - itemOffset - 1; ++i) {
        items = (ItemsForSet)itemGroup.get (itemOffset);
        set (internalSetOffset, items.getCat(), items.getItem(),
          getAverageOfItemsForSetGroup (itemGroup));
        itemGroup.remove (0);
        itemGroup.add(((ItemsForSet)itemGroup.lastElement()).getNextItemsForSet());
      }

      //check if only one real moving average data
      float lastAverage = firstAverage;
      ItemsForSet lastItems = items.getNextItemsForSet(); //okay for 3,3
      if (numItemsTotal > scope) {

        //get the last real moving average data
        items = (ItemsForSet)itemGroup.get (itemOffset);
        lastAverage = getAverageOfItemsForSetGroup (itemGroup);
        set (internalSetOffset, items.getCat(), items.getItem(), lastAverage);
        lastItems = items.getNextItemsForSet();
      }

      //populate beginning with fake moving average data
      for (int i = 0; i < itemOffset; ++i) {
        set (internalSetOffset, firstItems.getCat(), firstItems.getItem(), firstAverage);
        firstItems = firstItems.getNextItemsForSet();
      }

      //populate end with fake moving average data
      for (int i = numItemsTotal - itemOffset; i < numItemsTotal; ++i) {
        set (internalSetOffset, lastItems.getCat(), lastItems.getItem(), lastAverage);
        lastItems = lastItems.getNextItemsForSet();
      }
    }
    else if (numItemsTotal > 0) { //do standard average calculation
      int currSet = setVector.size();
      for (int j = 0; j < dataset.getNumCats(); ++j) {
        for (int k = 0; k < dataset.getNumItems(); ++k) {
          set (currSet, j, k, dataset.getAverage (-1, j, k));
        }
      }
      needsUpdate = true;
    }
  }


  /**
   * Removes values from the dataset.  Depending on the values of the paramters,
   * eight different operations may result.  The simplest is to remove a
   * particular item from a particular set and a particular category.  But one
   * can also perform more complex operations.  For example, one can remove
   * every set of data.  The key to modifying the behavior is passing in
   * a negative one or -1 value for a parameter.  A -1 value specifies that
   * whatever is to be removed, will be removed for all such things
   * (ie sets, cats, or items) corresponding to that parameter.  So if one
   * wanted to remove every set of data, one could pass -1 to set, to cat, and
   * to item.  This would be specify to remove all items, for all cats, for all
   * sets.  If one wanted to remove only a single item from a particular set and
   * a particular category, then one would pass a non -1 value for each of the
   * parameters.  Below is a listing of the options and a short description.
   * The letters "a", "b", "c" indicates a non -1 value.<br>
   * set= -1, cat= -1, item= -1  Removes every set of data.<br>
   * set= a, cat= -1, item= -1  Removes a particular set of data.<br>
   * set= -1, cat= a, item= -1  Removes a particular category of data for every
   * set.<br>
   * set= a, cat= b, item= -1  Removes a particular category of data for a
   * particular set.<br>
   * set= -1, cat= -1, item= a  Removes a particular item for every set and
   * every category.<br>
   * set= -1, cat= a, item= b  Removes a particular item of a particular
   * category in every set.<br>
   * set= a, cat= -1, item= b  Removes a particular item of a particular set in
   * every category.<br>
   * set= a, cat= b, item= c  Removes a particular item of a particular category
   * of a particular set.<br>
   * @param set The set of data within which data is to be removed.
   * @param cat The cat of data within which data is to be removed.
   * @param item The item of data that is to be removed.
   */
   public final void remove (int set, int cat, int item) {

    needsUpdate = true;

    //remove all sets
    if (set == -1 && cat == -1 && item == -1) {
      constructor (0, 0, 0);
      return;
    }

    int numSets = getNumSets();
    int numCats = getNumCats();
    int numItems = getNumItems();
    if (set != -1 && set >= numSets) return;
    if (cat != -1 && cat >= numCats) return;
    if (item != -1 && item >= numItems) return;

    //removes a particular set
    if (set != -1 && cat == -1 && item == -1) {
      setVector.remove (set);
    }
    //removes a cat for all sets
    else if (set == -1 && cat != -1 && item == -1) {
      for (int i = 0; i < numSets; ++i) {
        ((Vector)setVector.get (i)).remove (cat);
      }
    }
    //removes a cat for a particular set
    else if (set != 1 && cat != 1 && item == -1) {
     ((Vector)setVector.get (set)).remove (cat);
    }
    //removes an item for all sets/cats
    else if (set == -1 && cat == -1 && item != 1) {
      for (int i = 0; i < numSets; ++i) {
        Vector catVector = (Vector)setVector.get (i);
        for (int j = 0; j < numCats; ++i) {
          ((Vector)catVector.get (j)).remove (item);
        }
      }
    }
    //removes an item for all sets and a particular cat
    else if (set == -1 && cat != -1 && item != -1) {
      for (int i = 0; i < numSets; ++i) {
        ((Vector)((Vector)setVector.get (i)).get (cat)).remove (item);
      }
    }
    //removes an item for all cats and a particular set
    else if (set != -1 && cat == -1 && item != -1) {
      for (int j = 0; j < numCats; ++j) {
        ((Vector)((Vector)setVector.get (set)).get (j)).remove (item);
      }
    }
    //removes an item for a particular set/cat
    else if (set != -1 && cat != -1 && item != -1) {
     ((Vector)((Vector)setVector.get (set)).get (cat)).remove (item);
    }
   }



  /**
   * Shifts all the data items one place, from the higher order to the lower
   * order, replacing the highest order items with the specified items.  This
   * method is designed to be used with graph charts where the data is
   * dynamically updated and graphed over time categories.  For example, if you
   * picture a normal line chart with say three sets of data, that charts the
   * amount of memory being used by various programs on your computer and is
   * updated every second.  The number of lines would correspond to the number
   * of programs being charted.  The number of x axis labels would refer to the
   * number of seconds of data is being charted (ex 10 seconds).  Every second,
   * we could call shiftLower and this would shift left all the old data in the
   * chart, and shift in to the right of the chart, some new data.  There would
   * have to be a new data value for each line in the chart.  The the number of
   * lines corresponds to the number of programs, and the number of programs,
   * corresponds to the number of legend labels, etc, the number of data values
   * shifted in must be equal to the number of sets in the dataset.
   * @param values  An array of values of length getNumSets() to shift in.
   */
  public final void doShiftLower (float[] values) {
    needsUpdate = true;
    for (int i = 0; i < setVector.size() && i < values.length; ++i) {
      Vector catVector = (Vector)setVector.get (i);
      for (int j = 0; j < catVector.size() - 1; ++j) {
        Vector itemVector = (Vector)catVector.get (j);
        itemVector.remove (0);
        Vector itemVectorNext = (Vector)catVector.get (j + 1);
        itemVector.add (itemVectorNext.get (0));
      }
      Vector itemVector = (Vector)catVector.get (catVector.size() - 1);
      itemVector.remove (0);
      itemVector.add (new Float (values[i]));
    }
  }


  /**
   * Converts the dataset for use in "stacked" charts.  This is a convenience
   * method.  Stacked charts are those where each set of data is to be stacked
   * on top of the previous set.  So if you have multiple sets of data and you
   * want the graph components corresponding to each set to be stacked on top
   * of the previous set but you don't want to have to adjust your data
   * yourself to get this ***affect*** then use this method.  Multiple calls
   * to this method will change your dataset each time.  You will only want to call it once. Unless
   * you repopulate your whole dataset with virgin (i.e. never before stacked) values again.
   */
  public final void doConvertToStacked() {

    needsUpdate = true;
    for (int j = 0; j < getNumCats(); ++j) {
      for (int k = 0; k < getNumItems(); ++k) {
        float posSum = 0f;
        float negSum = 0f;
        for (int i = 0; i < getNumSets(); ++i) {
          float item = get (i, j, k);
          if (item >= 0f) {
            posSum = posSum + get (i, j, k);
            set (i, j, k, posSum);
          }
          else {
            negSum = negSum + get (i, j, k);
            set (i, j, k, negSum);
          }
        }
      }
    }
  }


  /**
   * Gets a float[][] representation of this dataset for use by GraphChartArea.
   * @return float[][] A representation of this dataset.
   */
  final float[][] getOldGraphStruct() {

    float[][] dataset;
    if (setVector.size() == 0) dataset = new float[0][0];
    else {
      dataset = new float
        [setVector.size()]
        [((Vector)setVector.get(0)).size() *
        ((Vector)((Vector)setVector.get(0)).get(0)).size()];
      for (int i = 0; i < setVector.size(); ++i) {
        Vector catVector = (Vector)setVector.get (i);
        for (int j = 0; j < catVector.size(); ++j) {
          Vector itemVector = (Vector)catVector.get (j);
          for (int k = 0; k < itemVector.size(); ++k) {
            dataset[i][j*itemVector.size() + k] =
              ((Float)itemVector.get (k)).floatValue();
          }
        }
      }
    }
    return dataset;
  }


  /**
   * Gets a float[] representation of this dataset for use by PieChartArea.
   * @return float[] A representation of this dataset.
   */
  final float[] getOldPieStruct() {

    float[] dataset = new float[setVector.size()];
    for (int i = 0; i < setVector.size(); ++i) {
      float sum = 0;
      Vector catVector = (Vector)setVector.get(i);
      for (int j = 0; j < catVector.size(); ++j) {
        Vector itemVector = (Vector)catVector.get(j);
        for (int k = 0; k < itemVector.size(); ++k) {
          sum += ((Float)itemVector.get(k)).floatValue();
        }
      }
      dataset[i] = sum;
    }
    return dataset;
  }


  /**
   * Gets whether this object needs to be updated with new properties.
   * @param chart2D The object that may need to be updated.
   * @return If true then needs update.
   */
  final boolean getChart2DNeedsUpdate (Chart2D chart2D) {
    if (needsUpdate) return true;
    int index = -1;
    if ((index = chart2DVector.indexOf (chart2D)) != -1) {
      return ((Boolean)needsUpdateVector.get (index)).booleanValue();
    }
    return false;
  }


  /**
   * Adds a Chart2D to the set of objects using these properties.
   * @param chart2D The object to add.
   */
  final void addChart2D (Chart2D chart2D) {

    if (!chart2DVector.contains (chart2D)) {
      chart2DVector.add (chart2D);
      needsUpdateVector.add (new Boolean (true));
    }
  }


  /**
   * Removes a Chart2D from the set of objects using these properties.
   * @param chart2D The object to remove.
   */
  final void removeChart2D (Chart2D chart2D) {

    int index = -1;
    if ((index = chart2DVector.indexOf (chart2D)) != -1) {
      chart2DVector.remove (index);
      needsUpdateVector.remove (index);
    }
  }


  /**
   * Validates the properties of this object.
   * If debug is true then prints a messages indicating whether each property is valid.
   * Returns true if all the properties were valid and false otherwise.
   * @param debug If true then will print status messages.
   * @return If true then valid.
   */
  final boolean validate (boolean debug) {

    if (debug) System.out.println ("Validating Dataset");

    boolean valid = true;

    int numSets = setVector.size();
    int numCats = 0;
    Vector catVector = null;
    if (numSets > 0) {
      catVector = (Vector)setVector.get (0);
      numCats = catVector.size();
    }
    int numItems = 0;
    Vector itemVector = null;
    if (numCats > 0) {
      itemVector = (Vector)catVector.get (0);
      numItems = itemVector.size();
    }

    for (int i = 0; i < numSets && valid; ++i) {
      catVector = (Vector)setVector.get (i);
      if (numCats != catVector.size()) {
        valid = false;
        break;
      }
      for (int j = 0; j < catVector.size() && valid; ++j) {
        itemVector = (Vector)catVector.get (j);
        if (numItems != itemVector.size()) {
          valid = false;
          break;
        }
      }
    }

    if (debug && !valid) System.out.println ("Problem with Dataset");

    if (debug) {
      if (valid) System.out.println ("Dataset was valid");
      else System.out.println ("Dataset was invalid");
    }

    return valid;
  }


  /**
   * Updates the properties of this Chart2D.
   * @param chart2D The object to update.
   */
  final void updateChart2D (Chart2D chart2D) {

    if (getChart2DNeedsUpdate (chart2D)) {

      update();

      int index = -1;
      if ((index = chart2DVector.indexOf (chart2D)) != -1) {
        needsUpdateVector.set (index, new Boolean (false));
      }
    }
  }


  private void constructor (int sets, int cats, int items) {

    needsUpdate = true;
    setVector = new Vector (sets, 10);
    for (int i = 0; i < sets; ++i) {
      Vector catVector = new Vector (cats, 10);
      setVector.add (i, catVector);
      for (int j = 0; j < cats; ++j) {
        Vector itemVector = new Vector (items, 10);
        catVector.add (j, itemVector);
        for (int k = 0; k < items; ++k) {
          itemVector.add (k, new Float (0f));
        }
      }
    }
  }


  private void update() {

    if (needsUpdate) {

      for (int i = 0; i < needsUpdateVector.size(); ++i) {
        needsUpdateVector.set (i, new Boolean (true));
      }
      needsUpdate = false;

      greatest = -9999999999999999f;
      least = 9999999999999999f;
      for (int i = 0; i < setVector.size(); ++i) {
        Vector catVector = (Vector)setVector.get (i);
        for (int j = 0; j < catVector.size(); ++j) {
          Vector itemVector = (Vector)catVector.get (j);
          for (int k = 0; k < itemVector.size(); ++k) {
            float value = ((Float)itemVector.get (k)).floatValue();
            greatest = value > greatest ? value : greatest;
            least = value < least ? value : least;
      } } }
      if (greatest < least) greatest = least = 0;
    }
  }


  private float getAverageOfItemsForSetGroup (Vector group) {

    float sum = 0f;
    for (int i = 0; i < group.size(); ++i) {
      sum += ((ItemsForSet)group.get(i)).getSum();
    }
    return sum / group.size();
  }


  private class ItemsForSet {

    private int cat;
    private int item;
    private Dataset dataset;

    ItemsForSet (Dataset dataset, int cat, int item) {

      this.cat = cat;
      this.item = item;
      this.dataset = dataset;
    }

    private int getCat() {
      return cat;
    }

    private int getItem() {
      return item;
    }

    private float getSum() {
      float sum = 0;
      int numSets = dataset.getNumSets();
      for (int i = 0; i < numSets; ++i) {
        sum += dataset.get (i, cat, item);
      }
      return sum;
    }

    private ItemsForSet getNextItemsForSet() {
      int nextCat = cat;
      int nextItem = item;
      if (item >= (dataset.getNumItems() - 1)) {
        nextCat++;
        nextItem = 0;
      }
      else {
        nextItem++;
      }
      if (nextCat >= dataset.getNumCats()) return null;
      else return new ItemsForSet (dataset, nextCat, nextItem);
    }
  }
}