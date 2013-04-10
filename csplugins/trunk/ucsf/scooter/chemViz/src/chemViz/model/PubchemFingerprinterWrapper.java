package chemViz.model;

import org.openscience.cdk.fingerprint.PubchemFingerprinter;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

public class PubchemFingerprinterWrapper extends PubchemFingerprinter {
	public PubchemFingerprinterWrapper() {
		super(SilentChemObjectBuilder.getInstance());
	}
}
