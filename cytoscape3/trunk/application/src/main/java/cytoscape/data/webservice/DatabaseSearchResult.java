package cytoscape.data.webservice;

import cytoscape.data.webservice.CyWebServiceEvent.WSEventType;

public class DatabaseSearchResult {
	private final Integer resultSize;
	private final Object resultObject;
	private final WSEventType nextMove;
	
	public DatabaseSearchResult(Integer resultSize, Object resultObject, WSEventType nextMove) {
		this.resultSize = resultSize;
		this.resultObject = resultObject;
		this.nextMove = nextMove;
	}
	
	public Object getResult() {
		return resultObject;
	}
	
	public Integer getResultSize() {
		return resultSize;
	}
	
	public WSEventType getNextMove() {
		return nextMove;
	}
}
