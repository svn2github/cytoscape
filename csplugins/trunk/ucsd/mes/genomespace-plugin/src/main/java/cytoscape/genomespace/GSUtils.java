
package cytoscape.genomespace;


import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.genomespace.client.GsFile;

final class GSUtils {

	private GSUtils() {};

	public static Map<String,GsFile> getFileNameMap(List<GsFile> l) {
		Map<String,GsFile> nm = new HashMap<String,GsFile>();
		for ( GsFile f : l )
			nm.put(f.getFilename(), f);

		return nm;
	}
}
