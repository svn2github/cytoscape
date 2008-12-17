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
		BitSet fp1 = compound1.getFingerprint();
		BitSet fp2 = compound2.getFingerprint();
		
		double score = Double.MIN_VALUE;
		if (fp1 != null && fp2 != null) {
			try {
				score = Tanimoto.calculate(fp1, fp2);
			} catch (Exception e) {
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
