package pingo.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

public class IndexedTermModel {

	private static final String GO_ID = "ID";
	private static final String GO_NAME = "TERM";

	private final Analyzer luceneAnalyzer;
	private final QueryParser queryParser;
	private Searcher searcher;

	public IndexedTermModel(final URL oboFile) throws IOException {
		if(oboFile == null)
			throw new NullPointerException("Source URL is null.");
		
		this.luceneAnalyzer = new StandardAnalyzer(Version.LUCENE_29);
		this.queryParser = new QueryParser(Version.LUCENE_29, GO_NAME,
				luceneAnalyzer);
		index(oboFile);
	}

	private void index(URL target) throws IOException {

		final Directory ramIndex = new RAMDirectory();

		final IndexWriter writer = new IndexWriter(ramIndex, luceneAnalyzer, true,
				IndexWriter.MaxFieldLength.UNLIMITED);

		read(writer, target);

		writer.optimize();
		writer.close();

		this.searcher = new IndexSearcher(ramIndex);
	}

	private void read(IndexWriter writer, final URL source) throws IOException {
		final BufferedReader reader = new BufferedReader(new InputStreamReader(
				source.openStream()));
		String line = new String();

		while ((line = reader.readLine()) != null) {
			final String[] parts = line.split("=");
			if (parts.length != 2)
				continue;

			String[] termParts = parts[1].split("\\[");
			final String id = parts[0].trim();
			final String term = termParts[0].trim();
			// System.out.println("Pair = " + id + ", " + term);
			final Document doc = new Document();
			doc.add(new Field(GO_ID, id, Store.YES, Index.ANALYZED));
			doc.add(new Field(GO_NAME, term, Store.YES, Index.ANALYZED));
			writer.addDocument(doc);
		}
		reader.close();
	}

	public Map<String, String> query(final String queryString)
			throws ParseException, IOException {
		final Map<String, String> result = new HashMap<String, String>();

		final Query query = queryParser.parse(queryString);
		final TopScoreDocCollector collector = TopScoreDocCollector.create(1000000,
				true);
		searcher.search(query, collector);
		final int hitCount = collector.getTotalHits();
		

		final ScoreDoc[] hits = collector.topDocs().scoreDocs;
		for (int i = 0; i < hitCount; i++) {
			ScoreDoc scoreDoc = hits[i];
			int docId = scoreDoc.doc;
			final Document doc = searcher.doc(docId);
			result.put(doc.get(GO_ID), doc.get(GO_NAME));
		}

		System.out.println("Total number of recoreds found: " + hitCount);
		System.out.println("Total number of recoreds found in Map: " + result.size());
		
		return result;
	}

}
