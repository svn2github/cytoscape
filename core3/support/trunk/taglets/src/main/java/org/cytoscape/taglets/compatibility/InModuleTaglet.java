package org.cytoscape.taglets.compatibility;

import java.util.Arrays;
import java.util.Map;

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;

@SuppressWarnings("restriction")
public class InModuleTaglet implements Taglet {
	public static final String NAME = "CyAPI.InModule";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public boolean inConstructor() {
		return false;
	}

	@Override
	public boolean inField() {
		return false;
	}

	@Override
	public boolean inMethod() {
		return false;
	}

	@Override
	public boolean inOverview() {
		return false;
	}

	@Override
	public boolean inPackage() {
		return true;
	}

	@Override
	public boolean inType() {
		return true;
	}

	@Override
	public boolean isInlineTag() {
		return false;
	}

	@Override
	public String toString(Tag tag) {
		return "<hr/><p><b>Module:</b> <code>"
				+ tag.text()
				+ "</code></p>"
				+ "<p>To use this in your app, include the following dependency in your POM:</p>"
				+ "<pre>&lt;dependency>\n    &lt;groupId>org.cytoscape&lt;/groupId>\n    &lt;artifactId><b>"
				+ tag.text() + "</b>&lt;/artifactId>\n&lt;/dependency></pre>";
	}

	@Override
	public String toString(Tag[] tags) {
		System.out.println(Arrays.asList(tags));
		if (tags.length == 0) {
			return "";
		}
		System.out.println(System.getProperties());
		return toString(tags[0]);
	}

	public static void register(Map<String, Taglet> tagletMap) {
		if (tagletMap.containsKey(NAME))
			tagletMap.remove(NAME);
		Taglet tag = new InModuleTaglet();
		tagletMap.put(NAME, tag);
	}
}
