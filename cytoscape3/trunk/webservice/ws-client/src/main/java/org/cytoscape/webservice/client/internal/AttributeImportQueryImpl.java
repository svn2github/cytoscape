package org.cytoscape.webservice.client.internal;

import org.cytoscape.webservice.client.AttributeImportQuery;

public class AttributeImportQueryImpl implements AttributeImportQuery {
	
	private final Object parameter;
	private final String keyCyAttributeName;
	private final String keyNameInWebService;
	
	public AttributeImportQueryImpl(Object parameter, String keyAttrName, String keyNameInWebService) {
		this.parameter = parameter;
		this.keyCyAttributeName = keyAttrName;
		this.keyNameInWebService = keyNameInWebService;
		
	}
	
	/* (non-Javadoc)
	 * @see org.cytoscape.webservice.client.internal.AttributeImportQuery#getParameter()
	 */
	public Object getParameter() {
		return parameter;
	}
	
	/* (non-Javadoc)
	 * @see org.cytoscape.webservice.client.internal.AttributeImportQuery#getKeyCyAttrName()
	 */
	public String getKeyCyAttrName() {
		return this.keyCyAttributeName;
	}
	
	/* (non-Javadoc)
	 * @see org.cytoscape.webservice.client.internal.AttributeImportQuery#getKeyNameInWebService()
	 */
	public String getKeyNameInWebService() {
		return this.keyNameInWebService;
	}

}
