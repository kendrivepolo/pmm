/*
 * SearchIF.java    1.0 2015年12月28日
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

import java.util.List;
import tw.idv.ken.mymovies.model.Film;
import tw.idv.ken.mymovies.model.FilmSearchResult;

/**
 * Methods related with Lucene full text indexing and searching.
 *
 * @version 1.0 2015年12月28日
 * @author ken
 *
 */
public interface SearchServiceIF {
	/**
	 * Create lucene indexes for all films.
	 */
	void reBuildSearchIndexes();
	/**
	 * Create lucene indexes for a film.
	 * @param film a film
	 */
    void createSearchIndex(Film film);
	/**
	 * Update lucene indexes for an existed film.
	 * @param film a film
	 */
    void updateSearchIndex(Film film);

    /**
     * Search films by user specified keyword for his own films.
     * @param ownerId film owner's id
     * @param keyword user specified keyword
     * @return matched results
     */
    List<FilmSearchResult> searchFilms(String ownerId, String keyword);
}
