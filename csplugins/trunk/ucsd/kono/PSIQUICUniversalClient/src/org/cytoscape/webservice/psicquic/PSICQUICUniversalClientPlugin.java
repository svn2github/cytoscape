package org.cytoscape.webservice.psicquic;

import cytoscape.data.webservice.WebServiceClientManager;
import cytoscape.plugin.CytoscapePlugin;

public class PSICQUICUniversalClientPlugin extends CytoscapePlugin {

	public PSICQUICUniversalClientPlugin() {
		
		try {
			WebServiceClientManager.registerClient(PSICQUICUniversalClient.getClient());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

//	private void test() throws Exception {
//		// Create a reader
//
//		File inputFile = new File(
//				"/Users/kono/Documents/ic/src/main/resources/samples/psixml25/17129783.xml");
//
//		PsimiXmlLightweightReader reader = new PsimiXmlLightweightReader(
//				inputFile);
//
//		// Read the whole file into an EntrySet
//		final List<IndexedEntry> indexedEntries = reader.getIndexedEntries();
//
//		System.out.println("Indexing finished!!");
//
//		// Show all interactions, their respective id and label
//		for (IndexedEntry entry : indexedEntries) {
//			final Iterator<Interaction> iterator = entry
//					.unmarshallInteractionIterator();
//			while (iterator.hasNext()) {
//				Interaction interaction = iterator.next();
//
//				final String label = interaction.getNames().getShortLabel();
//				final int id = interaction.getId();
//
//				System.out.println("Interaction " + id + ": " + label);
//			}
//		}
//
//		// PsicquicService ps = service.getPsicquic();
//		// System.out.println( "--------> Version: " + ps.getVersion());
//		//        
//		//        
//		//        
//		// DbRefRequest query = new DbRefRequest();
//		// query.setResultType("count");
//		// // query.setResultType("psimi25/xml");
//		//        
//		// QueryResponse res = ps.getByInteractor(query);
//		//       
//		// ResultSet resSet = res.getResultSet();
//		//        
//		// resSet.getEntrySet();
//		//        
//		// EntrySet eSet = resSet.getEntrySet();
//		//        
//		// List<Entry> entryList = eSet.getEntry();
//		//        
//
//	}

}
