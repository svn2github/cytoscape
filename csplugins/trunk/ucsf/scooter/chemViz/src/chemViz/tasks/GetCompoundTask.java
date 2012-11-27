/*
  Copyright (c) 2006, 2007, 2008 The Cytoscape Consortium (www.cytoscape.org)

  The Cytoscape Consortium is:
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Institut Pasteur
  - Agilent Technologies

  This library is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation; either version 2.1 of the License, or
  any later version.

  This library is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
  documentation provided hereunder is on an "as is" basis, and the
  Institute for Systems Biology and the Whitehead Institute
  have no obligations to provide maintenance, support,
  updates, enhancements or modifications.  In no event shall the
  Institute for Systems Biology and the Whitehead Institute
  be liable to any party for direct, indirect, special,
  incidental or consequential damages, including lost profits, arising
  out of the use of this software and its documentation, even if the
  Institute for Systems Biology and the Whitehead Institute
  have been advised of the possibility of such damage.  See
  the GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */

package chemViz.tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import giny.model.GraphObject;

import chemViz.model.Compound;
import chemViz.model.Compound.AttriType;


public class GetCompoundTask implements Callable<Compound> {
	private GraphObject go;
	private String attr;
	private String cstring;
	private AttriType type;
	private Compound result = null;
	static List<Compound> threadResultsList = null;

	static public List<Compound> runThreads(int maxThreads, List<GetCompoundTask> getList) {
		if (getList == null || getList.size() == 0) 
			return new ArrayList<Compound>();

		int nThreads = Runtime.getRuntime().availableProcessors()-1;
		if (maxThreads > 0)
			nThreads = maxThreads;

		// System.out.println("Getting "+getList.size()+" compounds using "+nThreads+" threads");

		ExecutorService threadPool = Executors.newFixedThreadPool(nThreads);
		threadResultsList = Collections.synchronizedList(new ArrayList<Compound>(getList.size()));
		List<Future<Compound>> results = new ArrayList<Future<Compound>>();

		try {
			results = threadPool.invokeAll(getList);
		} catch (Exception e) {
			System.out.println("Execution exception: "+e);
			e.printStackTrace();
		}

		return threadResultsList;
	}


	public GetCompoundTask(GraphObject go, String attr, String cstring, AttriType type) {
		this.go = go;
		this.attr = attr;
		this.cstring = cstring;
		this.type = type;
	}

	public Compound call() {
		result = new Compound(go, attr, cstring, type);
		threadResultsList.add(result);
		return result;
	}

	public Compound get() { 
		if (result == null) 
			return call();
		else
			return result; 
	}

}
