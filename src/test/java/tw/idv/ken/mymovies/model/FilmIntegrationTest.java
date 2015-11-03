package tw.idv.ken.mymovies.model;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.addon.test.RooIntegrationTest;

@RooIntegrationTest(entity = Film.class)
public class FilmIntegrationTest {
    @Autowired
    StudioDataOnDemand studioDoD;
    
    @Test
    public void testMarkerMethod() {
    }

	@Test
    public void testLoadFilmsByPage() {
        Assert.assertNotNull("Data on demand for 'Film' failed to initialize correctly", dod.getRandomFilm());
        int pageSize = 9;
        long count = Film.countFilms();
        Assert.assertTrue("There are 10 entries at least before we can do this test", count >= pageSize);
        List<Film> result = Film.loadFilmsByPage(1, pageSize);
        Assert.assertNotNull("Load Films in the first Page illegally returned null", result);
        Assert.assertTrue("Load Films in the first Page failed to return correct data", result.size() == pageSize);
    }
	
	@Test
	public void testChangeFilmStudio() {
		Film film = dod.getRandomFilm();
		Assert.assertNotNull("Load a random Film illegally returned null", film);
		Studio studio1 = studioDoD.getSpecificStudio(1);
		film.setStudio(studio1);
		film.merge();
		
		Film filmInDB = Film.findFilm(film.getId());
		Assert.assertNotNull("Load a Film by id illegally returned null", filmInDB);
		
		Studio studio2 = studioDoD.getSpecificStudio(2);
		filmInDB.setStudio(studio2);
		filmInDB.merge();
	}
	
	@Test
	public void testCreateFilmWithExistedStudio() {
		Film film = dod.getNewTransientFilm(100);
		Studio studio1 = studioDoD.getSpecificStudio(1);
		Studio studio1b = studioDoD.getNewTransientStudio(1);
		film.setStudio(studio1b);
		film.persist();
		}
}
