/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.panel.dqinter;

import infovis.Column;
import infovis.column.NumberColumn;
import infovis.column.StringColumn;
import infovis.metadata.ValueCategory;
import infovis.panel.DynamicQuery;
import infovis.utils.BasicFactory;

import java.util.ArrayList;

/**
 * Creates a Dynamic Query from a Column.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.6 $
 */
public class DynamicQueryFactory extends BasicFactory {
    private static DynamicQueryFactory instance;
    
    protected ArrayList creators = new ArrayList();
    static public final String QUERY_TYPE_DEFAULT = "default";
    static public final String QUERY_TYPE_SLIDER = "slider";
    static public final String QUERY_TYPE_TOGGLE = "toggle";
    static public final String QUERY_TYPE_RADIO = "radio";
    static public final String QUERY_TYPE_SEARCH = "search";

    public static DynamicQueryFactory getInstance() {
        if (instance == null) {
            instance = new DynamicQueryFactory();
        }
        return instance;
    }
    
    public static void setInstance(DynamicQueryFactory inst) {
        instance = inst;
    }
    
    public static DynamicQuery createDQ(Column c, String type) {
        return getInstance().create(c, type);
    }
    
    public static DynamicQuery createDQ(Column c) {
        return getInstance().create(c);
    }
    
    /**
     * Constructor for DynamicQueryFactory.
     */
    public DynamicQueryFactory() {
        addDefaultCreators("dynamicqueryfactory");
    }

    protected void addDefaultCreators(String factoryName) {
        super.addDefaultCreators(factoryName);
        add(new Creator() {
            public DynamicQuery create(Column c, String type) {
                if (type == QUERY_TYPE_SEARCH || c instanceof StringColumn) {
                    return new StringSearchDynamicQuery(c);
                }
                return null;
            }
        });
        add(new Creator() {
            public DynamicQuery create(Column c, String type) {
                int category = ValueCategory.findValueCategory(c);
            
                if (c instanceof NumberColumn
                    && category != ValueCategory.TYPE_CATEGORIAL) {
                    NumberColumn number = (NumberColumn) c;
                    return new NumberColumnBoundedRangeModel(number);
                }
                return null;
            }
        });
        add(new Creator() {
            public DynamicQuery create(Column c, String type) {
                int category = ValueCategory.findValueCategory(c);
                if (c instanceof NumberColumn
                    && category == ValueCategory.TYPE_CATEGORIAL) {
                    NumberColumn number = (NumberColumn) c;
                    return new CategoricalDynamicQuery(number);
                }
                return null;
            }
        });
    }
    
    protected void add(String name, String className, String data) {
        //TODO
        //Don't know how to do it yet
    }

    /**
     * Creates a dynamic query from a column.
     *
     * @param c The column
     * @param type the default type of DynamicQuery.
     *
     * @return A Dynamic query or null.
     */
    public DynamicQuery create(
        Column c,
        String type) {
        DynamicQuery ret = null;
        for (int i = 0; i < creators.size(); i++) {
            Creator creator = (Creator) creators.get(i);
            ret = creator.create(c, type);
            if (ret != null)
                break;
        }
        return ret;
    }

    /**
     * Creates a dynamic query of default type from a column.
     *
     * @param c The column
     *
     * @return A Dynamic query or null.
     */
    public DynamicQuery create(Column c) {
        return create(c, QUERY_TYPE_DEFAULT);
    }

    /**
     * Adds a default creator for a specific kind of column.
     *
     * @param c The creator
     */
    public void add(Creator c) {
        creators.add(c);
    }

    /**
     * Creator interface for building a Dynamic Query from a column type.
     */
    public interface Creator {
        /**
         * Creates a Dynamic Query from a column.
         */
        public DynamicQuery create(Column c, String type);
    }
 }
