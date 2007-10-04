package org.mskcc.pathway_commons.web_service.model;

/**
 * Encapsulates a Bundle Summary.
 *
 * @author Ethan Cerami
 */
public class NetworkSummary {
    private String dataSourceName;
    private long dataSourceId;

    /**
     * Gets data source name.
     * @return data source name.
     */
    public String getDataSourceName() {
        return this.dataSourceName;
    }

    /**
     * Sets data source name.
     * @param dataSourceName Data Source Name.
     */
    public void setDataSourceName (String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    /**
     * Gets datas ource ID.
     * @return data source ID.
     */
    public long getDataSourceId() {
        return dataSourceId;
    }

    /**
     * Sets data source ID.
     * @param id data source ID.
     */
    public void setDataSourceId(long id) {
        this.dataSourceId = id;
    }
}
