// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package tw.idv.ken.mymovies.model;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;
import tw.idv.ken.mymovies.model.Film;

privileged aspect Film_Roo_Jpa_ActiveRecord {
    
    @PersistenceContext
    transient EntityManager Film.entityManager;
    
    public static final List<String> Film.fieldNames4OrderClauseFilter = java.util.Arrays.asList("owner", "studio", "title", "performers", "streetDate", "mediaFiles", "rating", "comment", "description");
    
    public static final EntityManager Film.entityManager() {
        EntityManager em = new Film().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long Film.countFilms() {
        return entityManager().createQuery("SELECT COUNT(o) FROM Film o", Long.class).getSingleResult();
    }
    
    public static List<Film> Film.findAllFilms() {
        return entityManager().createQuery("SELECT o FROM Film o", Film.class).getResultList();
    }
    
    public static List<Film> Film.findAllFilms(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM Film o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, Film.class).getResultList();
    }
    
    public static Film Film.findFilm(Long id) {
        if (id == null) return null;
        return entityManager().find(Film.class, id);
    }
    
    public static List<Film> Film.findFilmEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM Film o", Film.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    public static List<Film> Film.findFilmEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM Film o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, Film.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional
    public void Film.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void Film.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            Film attached = Film.findFilm(this.id);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void Film.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void Film.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public Film Film.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        Film merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}
