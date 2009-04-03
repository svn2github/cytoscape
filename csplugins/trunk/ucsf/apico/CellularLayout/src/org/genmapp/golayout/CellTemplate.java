package org.genmapp.golayout;

import java.util.Collection;
import java.util.List;

import cytoscape.Cytoscape;
import cytoscape.layout.LayoutProperties;
import cytoscape.layout.Tunable;
import ding.view.DGraphView;
import ding.view.DingCanvas;

public class CellTemplate {

	public static void buildRegionsFromTepmlate(double dbn) {

		double distanceBetweenNodes = dbn;
		
		RegionManager.clearAll();

		// Hard-coded templates
		String Color;
		String CenterX;
		String CenterY;
		String Width;
		String Height;
		String ZOrder;
		String Rotation;
		String cG;
		Double xG;
		Double yG;
		Double wG;
		Double hG;
		int zG;
		Double rG;

		Color = "999999";
		CenterX = "6254.75";
		CenterY = "1837.25";
		Width = "8670.5";
		Height = "1185.5";
		ZOrder = "16384";
		Rotation = "0.0";
		cG = "#".concat(Color);
		xG = Double.parseDouble(CenterX);
		yG = Double.parseDouble(CenterY);
		wG = Double.parseDouble(Width);
		hG = Double.parseDouble(Height);
		zG = Integer.parseInt(ZOrder);
		rG = Double.parseDouble(Rotation);
		Region a = new Region("Rectangle", cG, xG, yG, wG, hG, zG, rG,
				"extracellular region");

		Color = "000000";
		CenterX = "2767.25";
		CenterY = "3877.25";
		Width = "1305.5";
		Height = "885.5";
		ZOrder = "16384";
		Rotation = "0.0";
		cG = "#".concat(Color);
		xG = Double.parseDouble(CenterX);
		yG = Double.parseDouble(CenterY);
		wG = Double.parseDouble(Width);
		hG = Double.parseDouble(Height);
		zG = Integer.parseInt(ZOrder);
		rG = Double.parseDouble(Rotation);
		Region f = new Region("Rectangle", cG, xG, yG, wG, hG, zG, rG,
				"mitochondrion");

		Color = "000000";
		CenterX = "4987.25";
		CenterY = "6067.5";
		Width = "1395.5";
		Height = "735.0";
		ZOrder = "16384";
		Rotation = "0.0";
		cG = "#".concat(Color);
		xG = Double.parseDouble(CenterX);
		yG = Double.parseDouble(CenterY);
		wG = Double.parseDouble(Width);
		hG = Double.parseDouble(Height);
		zG = Integer.parseInt(ZOrder);
		rG = Double.parseDouble(Rotation);
		Region g = new Region("Rectangle", cG, xG, yG, wG, hG, zG, rG,
				"endoplasmic reticulum");

		Color = "000000";
		CenterX = "6269.75";
		CenterY = "2635.25";
		Width = "8640.5";
		Height = "380.5";
		ZOrder = "16384";
		Rotation = "0.0";
		cG = "#".concat(Color);
		xG = Double.parseDouble(CenterX);
		yG = Double.parseDouble(CenterY);
		wG = Double.parseDouble(Width);
		hG = Double.parseDouble(Height);
		zG = Integer.parseInt(ZOrder);
		rG = Double.parseDouble(Rotation);
		Region b = new Region("Rectangle", cG, xG, yG, wG, hG, zG, rG,
				"plasma membrane"); //Should be "Line"

		Color = "000000";
		CenterX = "8479.75";
		CenterY = "5002.25";
		Width = "3620.5";
		Height = "2685.5";
		ZOrder = "16384";
		Rotation = "0.0";
		cG = "#".concat(Color);
		xG = Double.parseDouble(CenterX);
		yG = Double.parseDouble(CenterY);
		wG = Double.parseDouble(Width);
		hG = Double.parseDouble(Height);
		zG = Integer.parseInt(ZOrder);
		rG = Double.parseDouble(Rotation);
		Region d = new Region("Oval", cG, xG, yG, wG, hG, zG, rG, "nucleus");

		Color = "999999";
		CenterX = "6269.75";
		CenterY = "4747.25";
		Width = "8640.5";
		Height = "3765.5";
		ZOrder = "16384";
		Rotation = "0.0";
		cG = "#".concat(Color);
		xG = Double.parseDouble(CenterX);
		yG = Double.parseDouble(CenterY);
		wG = Double.parseDouble(Width);
		hG = Double.parseDouble(Height);
		zG = Integer.parseInt(ZOrder);
		rG = Double.parseDouble(Rotation);
		Region c = new Region("Rectangle", cG, xG, yG, wG, hG, zG, rG,
				"cytoplasm");

		Color = "999999";
		CenterX = "12089.75";
		CenterY = "4020.0";
		Width = "1920.5";
		Height = "3990.0";
		ZOrder = "16384";
		Rotation = "0.0";
		cG = "#".concat(Color);
		xG = Double.parseDouble(CenterX);
		yG = Double.parseDouble(CenterY);
		wG = Double.parseDouble(Width);
		hG = Double.parseDouble(Height);
		zG = Integer.parseInt(ZOrder);
		rG = Double.parseDouble(Rotation);
		Region e = new Region("Rectangle", cG, xG, yG, wG, hG, zG, rG,
				"unassigned");

		// SIZE UP REGIONS:
		Collection<Region> allRegions = RegionManager.getAllRegions();

		// calculate free space in overlapped regions
		for (Region r : allRegions) {

			Double comX = 0.0d;
			Double comY = 0.0d;

			List<Region> orList = r.getOverlappingRegions();
			// does not include Line shapes
			int orListSize = orList.size();
			Double[][] xy = new Double[orListSize * 8][2];
			int i = 0;
			for (Region or : orList) {
				// define points to exclude: corners and midpoints
				xy[i][0] = or.getRegionLeft();
				xy[i][1] = or.getRegionTop();
				i++;
				xy[i][0] = or.getRegionLeft();
				xy[i][1] = or.getRegionTop() + or.getRegionHeight() / 2;
				i++;
				xy[i][0] = or.getRegionLeft();
				xy[i][1] = or.getRegionBottom();
				i++;
				xy[i][0] = or.getRegionLeft() + or.getRegionWidth() / 2;
				xy[i][1] = or.getRegionBottom();
				i++;
				xy[i][0] = or.getRegionRight();
				xy[i][1] = or.getRegionBottom();
				i++;
				xy[i][0] = or.getRegionRight();
				xy[i][1] = or.getRegionBottom() - or.getRegionHeight() / 2;
				i++;
				xy[i][0] = or.getRegionRight();
				xy[i][1] = or.getRegionTop();
				i++;
				xy[i][0] = or.getRegionRight() - or.getRegionWidth() / 2;
				xy[i][1] = or.getRegionTop();
				i++;

				// define center of overlapped region
				// comX += or.getCenterX();
				// comY += or.getCenterY();
			}
			// if (orListSize > 1) {
			// comX = comX / orList.size();
			// comY = comY / orList.size();
			// } else {
			comX = r.getCenterX();
			comY = r.getCenterY();
			// }
			// check center against overlapping regions
			boolean skip = false;
			for (Region or : orList) {
				if (comX > or.getRegionLeft() && comX < or.getRegionRight()
						&& comY > or.getRegionTop()
						&& comY < or.getRegionBottom()) {
					skip = true;
					System.out.println("Inner area skipped!");
				}
			}
			if (skip)
				continue;

			// initialize with starting rectangle;
			Double freeL = r.getFreeLeft();
			Double freeR = r.getFreeRight();
			Double freeT = r.getFreeTop();
			Double freeB = r.getFreeBottom();

			// shrink to fit free area around center
			// adapted from ex2_1.m by E. Alpaydin, i2ml, Learning a
			// rectangle
			for (i = 0; i < orListSize * 8; i++) {
				Double x = xy[i][0];
				Double y = xy[i][1];
				if (x > freeL && x < freeR && y > freeT && y < freeB) {
					if (x < comX)
						freeL = x;
					else if (x > comX)
						freeR = x;
					else if (y < comY)
						freeT = y;
					else if (y > comY)
						freeB = y;
				}
			}
			if (((freeR - freeL) < (distanceBetweenNodes * 2))
					|| ((freeB - freeT) < (distanceBetweenNodes * 2))) {
				continue; // skip using inner of too thin or short
			}
			
			//commented out to ignore finding unoverlapped area
			//using layout and oil&water instead.
//			r.setFreeCenterX((freeL + freeR) / 2);
//			r.setFreeCenterY((freeT + freeB) / 2);
//			r.setFreeWidth(freeR - freeL);
//			r.setFreeHeight(freeB - freeT);
		}

		// calculate the maximum scale factor among all regions
		double maxScaleFactor = Double.MIN_VALUE;
		double minPanX = Double.MAX_VALUE;
		double minPanY = Double.MAX_VALUE;
		for (Region r : allRegions) {
			// max scale
			if (r.getShape() == "Line") {
				int col = r.getNodeCount(); // columns == count
				// calculate available length
				double scaleX;
				scaleX = ((col + 1) * distanceBetweenNodes) / r.getLineLength();
				if (scaleX > maxScaleFactor)
					maxScaleFactor = scaleX;
			} else { // Rectangle, Oval
				int col = r.getColumns();
				// System.out.println("col: " + r.getAttValue() + col);
				double scaleX = ((col + 1) * distanceBetweenNodes)
						/ r.getFreeWidth();
				double scaleY = ((col + 1) * distanceBetweenNodes)
						/ r.getFreeHeight();
				double scaleAreaSqrt = Math.sqrt(scaleX * scaleY);
				// System.out.println("scaleX,Y,Area: " + scaleX + "," + scaleY
				// + "," + scaleAreaSqrt);
				// use area to scale regions efficiently
				if (scaleAreaSqrt > maxScaleFactor)
					maxScaleFactor = scaleAreaSqrt;
			}

			// Allan hack!
			maxScaleFactor *= 1.1;

			// // min pan
			// if (r.getShape() == "Line") {
			// // don't bother
			// } else { // Rectangle, Oval
			// double x = r.getCenterX() - r.getRegionWidth() / 2;
			// double y = r.getCenterY() - r.getRegionHeight() / 2;
			// if (x < minPanX)
			// minPanX = x;
			// if (y < minPanY)
			// minPanY = y;
			// }
		}

		// apply max scale and min pan to all regions
		for (Region r : allRegions) {
			if (r.getShape() == "Line") {
				r.setLineLength(r.getLineLength() * maxScaleFactor);
				r.setFreeLength(r.getFreeLength() * maxScaleFactor);
			} else { // Rectangle, Oval
				r.setRegionWidth(r.getRegionWidth() * maxScaleFactor);
				r.setRegionHeight(r.getRegionHeight() * maxScaleFactor);
				r.setFreeWidth(r.getFreeWidth() * maxScaleFactor);
				r.setFreeHeight(r.getFreeHeight() * maxScaleFactor);
			}

			// r.setCenterX(r.getCenterX() - minPanX);
			// r.setCenterY(r.getCenterY() - minPanY);

			r.setCenterX(r.getCenterX() * maxScaleFactor);
			r.setCenterY(r.getCenterY() * maxScaleFactor);

			// r.setFreeCenterX(r.getFreeCenterX() - minPanX);
			// r.setFreeCenterY(r.getFreeCenterY() - minPanY);

			r.setFreeCenterX(r.getFreeCenterX() * maxScaleFactor);
			r.setFreeCenterY(r.getFreeCenterY() * maxScaleFactor);
		}

		// GRAPHICS
		DGraphView dview = (DGraphView) Cytoscape.getCurrentNetworkView();
		DingCanvas bCanvas = dview
				.getCanvas(DGraphView.Canvas.BACKGROUND_CANVAS);
		bCanvas.add(a);
		bCanvas.add(b);
		bCanvas.add(c);
		bCanvas.add(d);
		bCanvas.add(e);
		bCanvas.add(f);
		bCanvas.add(g);
		Cytoscape.getCurrentNetworkView().fitContent();
	}
}
