package org.cytoscape.webservice.client.internal;

import org.cytoscape.webservice.client.SearchResult;
import org.cytoscape.webservice.client.WSEventType;


public class DatabaseSearchResult<R> implements SearchResult<R> {
	private final Integer resultSize;
	private final R searchResult;
	private final WSEventType nextMove;
	
	public DatabaseSearchResult(Integer resultSize, R searchResult, WSEventType nextMove) {
		this.resultSize = resultSize;
		this.searchResult = searchResult;
		this.nextMove = nextMove;
	}
	
	/* (non-Javadoc)
	 * @see org.cytoscape.webservice.client.internal.SearchResult#getResult()
	 */
	public R getResult() {
		return searchResult;
	}
	
	/* (non-Javadoc)
	 * @see org.cytoscape.webservice.client.internal.SearchResult#getResultSize()
	 */
	public Integer getResultSize() {
		return resultSize;
	}
	
	/* (non-Javadoc)
	 * @see org.cytoscape.webservice.client.internal.SearchResult#getNextMove()
	 */
	public WSEventType getNextMove() {
		return nextMove;
	}
}
