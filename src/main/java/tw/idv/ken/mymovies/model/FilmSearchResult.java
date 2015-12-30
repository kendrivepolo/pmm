/*
 * FilmSearchResult.java    1.0 2015年12月30日
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
package tw.idv.ken.mymovies.model;

/**
 * Search result of lucene full text searching for film.
 * Use this class for serialization to json format.
 *
 * @version 1.0 2015年12月30日
 * @author ken
 *
 */
public class FilmSearchResult {
private int filmId;
private String title;
private String highlightedText;
public int getFilmId() {
	return filmId;
}
public void setFilmId(int filmId) {
	this.filmId = filmId;
}
public String getTitle() {
	return title;
}
public void setTitle(String title) {
	this.title = title;
}
public String getHighlightedText() {
	return highlightedText;
}
public void setHighlightedText(String highlightedText) {
	this.highlightedText = highlightedText;
}
}
