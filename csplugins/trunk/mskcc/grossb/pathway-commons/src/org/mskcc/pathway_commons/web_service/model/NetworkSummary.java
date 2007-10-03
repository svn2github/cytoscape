package org.mskcc.pathway_commons.web_service.model;

/**
 * Encapsulates a Bundle Summary.
 *
 * @author Ethan Cerami
 */
public interface NetworkSummary {

    /**
     * Gets datasource name.
     * @return data source name.
     */
    public String getDataSourceName();

    /**
     * Gets datasource ID.
     * @return data source ID.
     */
    public String getDataSourceId();
}
