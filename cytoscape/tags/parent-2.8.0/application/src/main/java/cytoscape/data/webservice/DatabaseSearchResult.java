package cytoscape.data.webservice;

import cytoscape.data.webservice.CyWebServiceEvent.WSEventType;

public class DatabaseSearchResult<R> {
	private final Integer resultSize;
	private final R searchResult;
	private final WSEventType nextMove;
	
	public DatabaseSearchResult(Integer resultSize, R searchResult, WSEventType nextMove) {
		this.resultSize = resultSize;
		this.searchResult = searchResult;
		this.nextMove = nextMove;
	}
	
	public R getResult() {
		return searchResult;
	}
	
	public Integer getResultSize() {
		return resultSize;
	}
	
	public WSEventType getNextMove() {
		return nextMove;
	}
}
