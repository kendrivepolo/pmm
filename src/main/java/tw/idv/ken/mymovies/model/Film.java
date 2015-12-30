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
    @ManyToOne
    private Owner owner;

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
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
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
	public static List<Film> loadFilmsByPage(int pageNumber, int pageSize) {
		Query query = entityManager().createQuery("SELECT F From Film F ORDER BY F.id DESC");
		query.setFirstResult((pageNumber-1) * pageSize);
		query.setMaxResults(pageSize);
		@SuppressWarnings("unchecked")
		List <Film> films = query.getResultList();
        return films;
    }

	public String getDescription() {
        return (this.description != null) ? this.description.trim() : "";
    }



	public String toJson() {
        return new JSONSerializer()
        .exclude("*.class").deepSerialize(this);
    }
}
