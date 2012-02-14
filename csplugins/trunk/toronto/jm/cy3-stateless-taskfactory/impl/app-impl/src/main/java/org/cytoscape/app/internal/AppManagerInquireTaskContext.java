package org.cytoscape.app.internal;

public class AppManagerInquireTaskContext {

	private AppInquireAction action;
	private String url;

	public String getUrl() {
		return url;
	}

	public AppInquireAction getAction() {
		return action;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setAction(AppInquireAction action) {
		this.action = action;
	}

}
