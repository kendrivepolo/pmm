/**
 * 
 */
package tw.idv.ken.mymovies.cli;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import tw.idv.ken.mymovies.model.Film;
import tw.idv.ken.mymovies.service.SearchServiceIF;

/**
 * @author ken
 *
 */
public class IndexRebuilder {
	public static void main(String[] args) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext(
				"/META-INF/spring/applicationContext.xml");
		SearchServiceIF searcher = ctx.getBean(SearchServiceIF.class);
		List<Film> films = Film.findAllFilms();
		System.out.println("Create lucene indexes...");
		for (Film film : films) {
			searcher.createSearchIndex(film);
		}
		System.out.println("Finished !");
	}
}
