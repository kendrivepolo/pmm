/**
 * 
 */
package tw.idv.ken.mymovies.cli;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author ken
 *
 */
public class IndexRebuilder {
	public static void main(String[] args) {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				"/META-INF/spring/applicationContext.xml");
	}
}
