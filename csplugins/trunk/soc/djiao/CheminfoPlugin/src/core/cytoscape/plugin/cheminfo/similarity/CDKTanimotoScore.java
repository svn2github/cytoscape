package cytoscape.plugin.cheminfo.similarity;

import java.util.BitSet;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.fingerprint.Fingerprinter;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.similarity.Tanimoto;
import org.openscience.cdk.smiles.SmilesParser;

import cytoscape.plugin.cheminfo.model.Compound;

/**
 * This is an implementation of SimilarityScore class. It uses the Tanimoto
 * coefficient method provided by CDK. It uses the fingerprint of a molecule,
 * which is also calculated using CDK.
 *
 */
public class CDKTanimotoScore extends SimilarityScore {

	public CDKTanimotoScore(Compound compound1, Compound compound2) {
		super (compound1, compound2);
	}

	@Override
	public double calculateSimilarity() {
		IMolecule m1 = compound1.getIMolecule();
		IMolecule m2 = compound2.getIMolecule();
		
		double score = Double.MIN_VALUE;
		if (m1 != null && m2 != null) {
			Fingerprinter fper = new Fingerprinter();
			try {
				BitSet fp1 = fper.getFingerprint(m1);
				BitSet fp2 = fper.getFingerprint(m2);
				score = Tanimoto.calculate(fp1, fp2);
			} catch (Exception ex) {
				score = 0.0;
			}
		}
		if (score == Double.MIN_VALUE) score = 0.0;

		return score;
	}

	private boolean isBlank(String smiles) {
		return null == smiles || "".equals(smiles.trim());
	}
}
