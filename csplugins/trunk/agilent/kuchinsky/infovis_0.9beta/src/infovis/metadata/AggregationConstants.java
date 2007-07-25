/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.metadata;

/**
 * Aggregation constants.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.4 $
 */
public interface AggregationConstants extends Constants {
    /**
     * Qualify how a column aggregates when it is hierarchical.
     */
    String AGGREGATION_TYPE = "AGGREGATION_TYPE";
    /** No aggregation. */
    String AGGREGATION_TYPE_NONE = null;
    /** Attributes only at leaves. */
    String AGGREGATION_TYPE_ATLEAF= "AGGREGATION_TYPE_ATLEAF";
    /** Aggregation additive, required for treemaps. */
    String AGGREGATION_TYPE_ADDITIVE = "AGGREGATION_TYPE_ADDITIVE";
    /** Aggreation by max value. */
    String AGGREGATION_TYPE_MAX = "AGGREGATION_TYPE_MAX";
    /** Aggregation by min value. */
    String AGGREGATION_TYPE_MIN = "AGGREGATION_TYPE_MIN";
    /** Aggregation by mean value. */
    String AGGREGATION_TYPE_MEAN = "AGGREGATION_TYPE_MEAN";
    /** Aggregation by string concatenation. */
    String AGGREGATION_TYPE_CONCAT = "AGGREGATION_TYPE_CONCAT";

    /** Value when the column is not aggregating. */
    short AGGREGATE_NO = 0;
    /** Value when the column is aggregating. */
    short AGGREGATE_YES = 1;
    /** Value when the column is compatible with a specified aggregation. */
    short AGGREGATE_COMPATIBLE = -1;
}
