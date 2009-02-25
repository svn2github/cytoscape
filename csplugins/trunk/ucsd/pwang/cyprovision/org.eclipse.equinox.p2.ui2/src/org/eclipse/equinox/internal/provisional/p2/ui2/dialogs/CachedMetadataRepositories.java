package org.eclipse.equinox.internal.provisional.p2.ui2.dialogs;

import java.util.Hashtable;
import org.eclipse.equinox.internal.p2.ui2.model.MetadataRepositoryElement;
import org.eclipse.equinox.internal.provisional.p2.ui2.model.MetadataRepositories;
import org.eclipse.equinox.internal.provisional.p2.ui2.policy.QueryProvider;
import org.eclipse.equinox.internal.provisional.p2.ui2.policy.Policy;

public class CachedMetadataRepositories extends MetadataRepositories{
	Hashtable cachedElements;

	CachedMetadataRepositories(Policy policy) {
		super(policy);
		setIncludeDisabledRepositories(true);
	}

	public int getQueryType() {
		return QueryProvider.METADATA_REPOS;
	}

	public Object[] getChildren(Object o) {
		if (cachedElements == null) {
			Object[] children = super.getChildren(o);
			cachedElements = new Hashtable(children.length);
			for (int i = 0; i < children.length; i++) {
				if (children[i] instanceof MetadataRepositoryElement)
					cachedElements.put(((MetadataRepositoryElement) children[i]).getLocation().toString(), children[i]);
			}
		}
		return cachedElements.values().toArray();
	}

}
