package tw.idv.ken.mymovies.model;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

import flexjson.JSONSerializer;

import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.persistence.Column;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Enumerated;
import javax.persistence.Query;

import org.springframework.roo.addon.json.RooJson;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(versionField="")
@RooJson
public class Film {

    /**
     */
	@Size(max = 255)
	@Column(name = "owner_id")
    private String ownerId;

    /**
     */
    @ManyToOne(cascade = {CascadeType.PERSIST,CascadeType.MERGE })
    private Studio studio;

    /**
     */
    @NotNull
    @Size(max = 255)
    private String title;

    /**
     */
    @Column(length = 1024)
    @Size(max = 1024)
    private String performers;

    /**
     */
    @Column(name = "street_date")
    @Size(max = 255)
    private String streetDate;

    /**
     */
    @OneToMany(cascade = {CascadeType.PERSIST,CascadeType.MERGE }, fetch = FetchType.EAGER)
    private List<MediaFile> mediaFiles = new ArrayList<MediaFile>();

    /**
     */
    @Enumerated
    private Rating rating;

    /**
     */
    @Lob
    private String comment;

    /**
     */
    @Lob
    private String description;

    /**
     * Load Films by page.
     * @param pageNumber page number starts with 1
     * @param pageSize how many films in a page
     * @return a list of Film instances
     */
	public static List<Film> loadFilmsByPage(String ownerId, int pageNumber, int pageSize) {
		Query query = entityManager().createQuery("SELECT F From Film F WHERE F.ownerId = :ownerId ORDER BY F.id DESC");
		query.setParameter("ownerId", ownerId);
		query.setFirstResult((pageNumber-1) * pageSize);
		query.setMaxResults(pageSize);
		@SuppressWarnings("unchecked")
		List <Film> films = query.getResultList();
        return films;
    }
	
	/**
	 * Find all films owned by a specific user.
	 * @param ownerId an user id
	 * @return a list of Film instances
	 */
	public static List<Film> findFilmsByOwner(String ownerId) {
		return entityManager()
				.createQuery("SELECT o FROM Film o WHERE o.ownerId = :ownerId",
						Film.class).setParameter("ownerId", ownerId)
				.getResultList();
	}
	
	/**
	 * Count the number of owned films of a specific user.
	 * @param ownerId an user id
	 * @return the number of films
	 */
	public static long countFilms(String ownerId) {
		return entityManager()
				.createQuery(
						"SELECT COUNT(o) FROM Film o WHERE o.ownerId = :ownerId",
						Long.class).setParameter("ownerId", ownerId)
				.getSingleResult();
	}

	public String getDescription() {
        return (this.description != null) ? this.description.trim() : "";
    }



	public String toJson() {
        return new JSONSerializer()
        .exclude("*.class").deepSerialize(this);
    }
}
