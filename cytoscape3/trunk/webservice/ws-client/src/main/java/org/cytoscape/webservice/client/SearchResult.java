package org.cytoscape.webservice.client;


public interface SearchResult<R> {

	public R getResult();

	public Integer getResultSize();

	public WSEventType getNextMove();

}