package metricTree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class MetricTreeTester {
	
	public static void main(String[] args) {
		//MetricInteger.initComputations();
		Random ran = new Random();
		MetricIntegerTree tree = new MetricIntegerTree();
		System.out.println("**********************");
		System.out.println("Rakennetaan 100000 alkion puu.");
		for (int i = 0; i < 100000; i++) {
			tree.insert(new MetricInteger(ran.nextInt(100000)));
		}
		//System.out.println("Operaatioita: " + MetricInteger.getComputations());
		// System.out.println(tree);
		// System.out.println();
		System.out.println();
		System.out.println("**********************");
		System.out.println("Haetaan alkiosta 5 korkeintaan etäisyydellä 1 olevat objektit.");
		//MetricInteger.initComputations();
		ArrayList<MetricInteger> res = tree.getRange(new MetricInteger(5), 1);
		Iterator<MetricInteger> iter = res.iterator();
		// while (iter.hasNext()) {
			// System.out.print(iter.next() + ", ");
		// }
		// System.out.println();
		//System.out.println("Operaatioita: " + MetricInteger.getComputations());
		// res = tree.getObjects();
		// iter = res.iterator();
		// while (iter.hasNext()) {
			// System.out.print(iter.next() + ", ");
		// }
		System.out.println();
		System.out.println("**********************");
		System.out.println("Testataan alkion 500 5 lähintä naapuria.");
		//MetricInteger.initComputations();
		res = tree.getNearest(new MetricInteger(500), 5);
		//System.out.println("Operaatioita: " + MetricInteger.getComputations());
	}
	
}
