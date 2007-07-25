/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.metadata;

import infovis.Column;
import infovis.Tree;
import infovis.utils.BasicFactory;

import java.lang.reflect.Method;
import java.util.*;

import org.apache.log4j.Logger;

/**
 * Class AggregationFactory manages hierarchical aggregation in columns. 
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.3 $
 */
public class AggregationFactory extends BasicFactory
    implements AggregationConstants {
    /** Instance of AggregationFactory. */
    public static final AggregationFactory instance = new AggregationFactory();
    private static final Logger logger = Logger.getLogger(AggregationFactory.class);
    protected HashMap aggregationMap = new HashMap();    
    
    /**
     * Returns the instance.
     * @return the instance.
     */
    public static AggregationFactory getInstance() {
        return instance;
    }
    
    /**
     * Returns the aggregation type name of a specified column
     * in a specified tree.
     * @param col the column
     * @param tree the tree
     * @return the aggregation type name
     */
    public static String getAggregationType(Column col, Tree tree) {
        return getInstance().getType(col, tree);
    }
    
    public static Aggregation getAggregation(Column col, Tree tree) {
        return getInstance().get(col, tree);
    }
    
    public AggregationFactory() {
        super();
        addDefaultCreators("aggregationfactory");
    }
    
    protected void add(String name, String className, String data) {
        try {
            Class c = Class.forName(className);
            Method m = c.getMethod("getInstance", null);
            Aggregation aggr = (Aggregation)m.invoke(null, null);

            putAggregationType(name, aggr);
        }
        catch(Exception e) {
            logger.error("Cannot instantiate Aggregation class "+className, e);
        }
    }
    
    public Aggregation get(String type) {
        return (Aggregation) aggregationMap.get(type);
    }
    
    public Aggregation get(Column col, Tree tree) {
        return get(getType(col, tree));
    }
    
    public String getType(Column col, Tree tree) {
        String type = (String)col.getMetadata().getAttribute(AGGREGATION_TYPE);
        if (type == null) {
            type = guessAggregationType(col, tree);
        }
        return type;
    }
    
    public String guessAggregationType(Column col, Tree tree) {
        for (Iterator iter = aggregationMap.keySet().iterator(); iter.hasNext(); ) {
            String type = (String)iter.next();
            Aggregation aggr = get(type);
            if (aggr.isAggregating(col, tree)) {
                return aggr.getType();
            }
        }
        return AGGREGATION_TYPE_NONE;
//        String compType = null;
//        // Special case for ATLEAF
//        if (AtLeafAggregation.sharedInstance().isAggregating(col, tree))
//            return AGGREGATION_TYPE_ATLEAF;
//        for (Iterator iter = iterator(); iter.hasNext(); ) {
//            Map.Entry entry = (Map.Entry)iter.next();
//            Aggregation aggr = (Aggregation)entry.getValue();
//            if (aggr.isAggregating(col, tree)) {
//                return (String)entry.getKey();
//            }
//        }
//        if (compType == null) {
//            compType = AGGREGATION_TYPE_NONE;
//        }
//            // insures we stick with this aggregation for now
//        col.getMetadata().put(AGGREGATION_TYPE, compType);
//        
//        return compType;
    }
    
    public Aggregation putAggregationType(
        String type, 
        Aggregation aggr) {
        return (Aggregation)aggregationMap.put(type, aggr);
    }
    
    public Iterator iterator() {
        return aggregationMap.entrySet().iterator();
    }
}
