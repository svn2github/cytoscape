package data.matrixMath;


public class XXTRunner implements Runnable {
	private final double[][] datad;
	private final double[][] outd;
	private final float[][] dataf;
	private final float[][] outf;
	private final int i;

	public XXTRunner(double[][] data, double[][] out, int i) {
		this.datad = data;
		this.outd = out;
		this.dataf = null;
		this.outf = null;
		this.i = i;
	}

	public XXTRunner(float[][] data, float[][] out, int i) {
		this.dataf = data;
		this.outf = out;
		this.datad = null;
		this.outd = null;
		this.i = i;
	}

	public void run() {
		if (datad != null) {
			for (int j = 0; j < datad.length; j++) {
				double sum = 0;
				for (int k = 0; k < datad[0].length; k++)
					sum += datad[j][k] * datad[i][k];

				outd[i][j] = sum;
			}
		}

		if (dataf != null) {
			for (int j = 0; j < dataf.length; j++) {
				float sum = 0;
				for (int k = 0; k < dataf[0].length; k++)
					sum += dataf[j][k] * dataf[i][k];

				outf[i][j] = sum;
			}
		}

		if (i % 100 == 0)
			System.out.println(i);
	}
}
