package cytoscape.data.webservice;

public class AttributeImportQuery {
	
	private final Object parameter;
	private final String keyCyAttributeName;
	private final String keyNameInWebService;
	
	public AttributeImportQuery(Object parameter, String keyAttrName, String keyNameInWebService) {
		this.parameter = parameter;
		this.keyCyAttributeName = keyAttrName;
		this.keyNameInWebService = keyNameInWebService;
		
	}
	
	public Object getParameter() {
		return parameter;
	}
	
	public String getKeyCyAttrName() {
		return this.keyCyAttributeName;
	}
	
	public String getKeyNameInWebService() {
		return this.keyNameInWebService;
	}

}
