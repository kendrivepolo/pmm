// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package tw.idv.ken.mymovies.model;

import java.util.Iterator;
import java.util.List;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import tw.idv.ken.mymovies.model.Film;
import tw.idv.ken.mymovies.model.FilmDataOnDemand;
import tw.idv.ken.mymovies.model.FilmIntegrationTest;

privileged aspect FilmIntegrationTest_Roo_IntegrationTest {
    
    declare @type: FilmIntegrationTest: @RunWith(SpringJUnit4ClassRunner.class);
    
    declare @type: FilmIntegrationTest: @ContextConfiguration(locations = "classpath*:/META-INF/spring/applicationContext*.xml");
    
    declare @type: FilmIntegrationTest: @Transactional;
    
    @Autowired
    FilmDataOnDemand FilmIntegrationTest.dod;
    
    @Test
    public void FilmIntegrationTest.testCountFilms() {
        Assert.assertNotNull("Data on demand for 'Film' failed to initialize correctly", dod.getRandomFilm());
        long count = Film.countFilms();
        Assert.assertTrue("Counter for 'Film' incorrectly reported there were no entries", count > 0);
    }
    
    @Test
    public void FilmIntegrationTest.testFindFilm() {
        Film obj = dod.getRandomFilm();
        Assert.assertNotNull("Data on demand for 'Film' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'Film' failed to provide an identifier", id);
        obj = Film.findFilm(id);
        Assert.assertNotNull("Find method for 'Film' illegally returned null for id '" + id + "'", obj);
        Assert.assertEquals("Find method for 'Film' returned the incorrect identifier", id, obj.getId());
    }
    
    @Test
    public void FilmIntegrationTest.testFindAllFilms() {
        Assert.assertNotNull("Data on demand for 'Film' failed to initialize correctly", dod.getRandomFilm());
        long count = Film.countFilms();
        Assert.assertTrue("Too expensive to perform a find all test for 'Film', as there are " + count + " entries; set the findAllMaximum to exceed this value or set findAll=false on the integration test annotation to disable the test", count < 250);
        List<Film> result = Film.findAllFilms();
        Assert.assertNotNull("Find all method for 'Film' illegally returned null", result);
        Assert.assertTrue("Find all method for 'Film' failed to return any data", result.size() > 0);
    }
    
    @Test
    public void FilmIntegrationTest.testFindFilmEntries() {
        Assert.assertNotNull("Data on demand for 'Film' failed to initialize correctly", dod.getRandomFilm());
        long count = Film.countFilms();
        if (count > 20) count = 20;
        int firstResult = 0;
        int maxResults = (int) count;
        List<Film> result = Film.findFilmEntries(firstResult, maxResults);
        Assert.assertNotNull("Find entries method for 'Film' illegally returned null", result);
        Assert.assertEquals("Find entries method for 'Film' returned an incorrect number of entries", count, result.size());
    }
    
    @Test
    public void FilmIntegrationTest.testPersist() {
        Assert.assertNotNull("Data on demand for 'Film' failed to initialize correctly", dod.getRandomFilm());
        Film obj = dod.getNewTransientFilm(Integer.MAX_VALUE);
        Assert.assertNotNull("Data on demand for 'Film' failed to provide a new transient entity", obj);
        Assert.assertNull("Expected 'Film' identifier to be null", obj.getId());
        try {
            obj.persist();
        } catch (final ConstraintViolationException e) {
            final StringBuilder msg = new StringBuilder();
            for (Iterator<ConstraintViolation<?>> iter = e.getConstraintViolations().iterator(); iter.hasNext();) {
                final ConstraintViolation<?> cv = iter.next();
                msg.append("[").append(cv.getRootBean().getClass().getName()).append(".").append(cv.getPropertyPath()).append(": ").append(cv.getMessage()).append(" (invalid value = ").append(cv.getInvalidValue()).append(")").append("]");
            }
            throw new IllegalStateException(msg.toString(), e);
        }
        obj.flush();
        Assert.assertNotNull("Expected 'Film' identifier to no longer be null", obj.getId());
    }
    
    @Test
    public void FilmIntegrationTest.testRemove() {
        Film obj = dod.getRandomFilm();
        Assert.assertNotNull("Data on demand for 'Film' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'Film' failed to provide an identifier", id);
        obj = Film.findFilm(id);
        obj.remove();
        obj.flush();
        Assert.assertNull("Failed to remove 'Film' with identifier '" + id + "'", Film.findFilm(id));
    }
    
}