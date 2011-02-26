
package cytoscape.genomespace;


import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;

import org.genomespace.datamanager.core.GSFileMetadata;

final class GSUtils {

	private GSUtils() {};

	public static Map<String,GSFileMetadata> getFileNameMap(Collection<GSFileMetadata> l) {
		Map<String,GSFileMetadata> nm = new HashMap<String,GSFileMetadata>();
		for ( GSFileMetadata f : l )
			nm.put(f.getName(), f);

		return nm;
	}
}

