/*
 * SearchService.java    1.0 2015年12月28日
 *
 * Copyright (c) 2015-2030 Monmouth Technologies, Inc.
 * http://www.mt.com.tw
 * 10F-1 No. 306 Chung-Cheng 1st Road, Linya District, 802, Kaoshiung, Taiwan
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Monmouth
 * Technologies, Inc. You shall not disclose such Confidential Information and 
 * shall use it only in accordance with the terms of the license agreement you
 * entered into with Monmouth Technologies.
 */
package tw.idv.ken.mymovies.service;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.search.BooleanClause.Occur;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import tw.idv.ken.mymovies.model.Film;
import tw.idv.ken.mymovies.model.FilmSearchResult;

/**
 * Implementations of Lucene full text search functions.
 *
 * @version 1.0 2015年12月28日
 * @author ken
 *
 */
@Service
public class SearchService implements SearchServiceIF {
	/**
	 * Logger instance.
	 */
	private Logger Log = Logger.getLogger(SearchService.class);
	@Value( "${userdata.path}" )
	private String userdataPath;
	private final String SEARCH_INDEX_PATH = "search"; 

	/* (non-Javadoc)
	 * @see tw.idv.ken.mymovies.service.SearchServiceIF#createSearchIndex(tw.idv.ken.mymovies.model.Film)
	 */
	@Override
	public void createSearchIndex(Film film) {
		Log.info("create lucene indexes for film: " + film.getId());
		IndexWriter iwriter = getIndexWriter();
	    Document doc = filmToLuceneDocument(film);
		try {
			iwriter.addDocument(doc);
			iwriter.close();
		} catch (IOException e) {
			Log.warn("create lucene  search index fails ! ", e);
			return;
		}
	}
	
	/**
	 * Delete lucene search index of an existed film.
	 * @param filmKey film key
	 */
	private void deleteSearchIndex(final int filmKey) {
		IndexWriter iwriter = getIndexWriter();
		Log.debug("delete search index for film: " + filmKey);
        try {
        	iwriter.deleteDocuments(new Term("filmKey", ""+filmKey));
        } catch (Exception e) {
        	Log.warn(String.format("delete search index for film(%s) fails ", filmKey), e);
        } finally {
            if (iwriter != null) try {iwriter.close(); } catch(Exception e1) {};
        }
	}

	/**
	 * Compose the full path of search index files.
	 * @return full path as a string
	 */
	private String getSearchIndexPath() {
		return userdataPath + "/" +  this.SEARCH_INDEX_PATH;
	}
	
	private IndexWriter getIndexWriter() {
		Directory directory = null;
		IndexWriter iwriter = null;
		Analyzer analyzer = new CJKAnalyzer();
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		Path indexPath = FileSystems.getDefault().getPath(getSearchIndexPath());
		try {
			directory = FSDirectory.open(indexPath);
			iwriter = new IndexWriter(directory, config);
		} catch (IOException e) {
			Log.warn("get index writer fails ! ", e);
		}
		return iwriter;
	}
	
	private Document filmToLuceneDocument(final Film film) {
		Document doc = new Document();
        doc.add(new IntField("filmKey",film.getId().intValue(),Field.Store.YES));
        doc.add(new StringField("ownerId", film.getOwnerId(), Field.Store.NO));
        doc.add(new StoredField("title", film.getTitle()));
        doc.add(new TextField("body", composeLuceneDocBody(film), Field.Store.YES));

		return doc;
	}
	
	private String composeLuceneDocBody(final Film film) {
		StringBuilder sb = new StringBuilder();
		sb.append(film.getTitle());
		sb.append(film.getPerformers());
		sb.append(film.getDescription());
		sb.append(film.getComment());
		
		return sb.toString();
	}

	@Override
	public List<FilmSearchResult> searchFilms(String ownerId, String keyword) {
		Log.info(String.format("search films with keyword(%s) by user(%s)",
				keyword, ownerId));
		List<FilmSearchResult> result = new LinkedList<>();
		Directory directory = null;
	    IndexSearcher isearcher = null;
	    DirectoryReader ireader  = null;
		Analyzer analyzer = new CJKAnalyzer();
		Path indexPath = FileSystems.getDefault().getPath(getSearchIndexPath());
		ScoreDoc[] hits = new ScoreDoc[0];
		try {
			directory = FSDirectory.open(indexPath);
			ireader = DirectoryReader.open(directory);
			isearcher = new IndexSearcher(ireader);
			TermQuery ownerIdQuery = new TermQuery(new Term("ownerId", ownerId));
			//TermQuery bodyQuery = new TermQuery(new Term("body", keyword));
			QueryParser parser = new QueryParser("body", analyzer);
			Query bodyQuery = parser.parse(keyword);
			BooleanQuery.Builder builder = new BooleanQuery.Builder();
			BooleanQuery q = builder.add(ownerIdQuery, Occur.MUST)
					.add(bodyQuery, Occur.MUST).build();
			
			//prepare highlighter
			QueryScorer scorer  = new QueryScorer(bodyQuery, "body");
			Fragmenter fragmenter = new SimpleSpanFragmenter(scorer);
			Highlighter highlighter = new Highlighter(scorer);
			highlighter.setTextFragmenter(fragmenter);
			
			hits = isearcher.search(q, 50).scoreDocs;
			Log.info("how many matched result ? " + hits.length);
			// Iterate through the results:
			for (int i = 0; i < hits.length; i++) {
				Document hitDoc = isearcher.doc(hits[i].doc);
				result.add(luceneDocToFilmSearchResult(
						hitDoc,
						getHighlightedText("body", hitDoc, highlighter,
								analyzer)));
			}
		} catch (IOException e) {
			Log.warn("open search index path fails ! ", e);
		/*
			Log.warn("parse query keyword fails ! ", pe);*/
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				ireader.close();
				directory.close();
			} catch (IOException | NullPointerException e) {
			}
		}

		return result;
	}

	/**
	 * Retrieve highlighted text from a Lucene search result.
	 * @param colName column name in a Lucene document which contains the text we need
	 * @param doc a Lucene search result
	 * @param highlighter Lucene highlighter instance
	 * @param analyzer Lucene analyzer instance
	 * @return highlighted text
	 */
	private String getHighlightedText(String colName,
            Document doc,
            Highlighter highlighter,
            Analyzer analyzer){
        String result = doc.get(colName);

        TokenStream tokenStream = analyzer.tokenStream("body",
                new StringReader(result));
        try{
            result = highlighter.getBestFragment(tokenStream,doc.get(colName));
        } catch (IOException | InvalidTokenOffsetsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        return result;
    }

	/*
	 * Create FilmSearchResult by Lucene document.
	 */
	private FilmSearchResult luceneDocToFilmSearchResult(final Document doc, final String highlightedText) {
		FilmSearchResult result = new FilmSearchResult();
		result.setFilmId(Integer.parseInt(doc.get("filmKey")));
		result.setTitle(doc.get("title"));
		result.setHighlightedText(highlightedText);
		
		return result;
	}

	@Override
	public void reBuildSearchIndexes() {
		List<Film> allFilms = Film.findAllFilms();
		for (Film f : allFilms) {
			this.createSearchIndex(f);
		}
	}

	@Override
	public void updateSearchIndex(Film film) {
		this.deleteSearchIndex(Long.valueOf(film.getId()).intValue());
		this.createSearchIndex(film);
	}
}
