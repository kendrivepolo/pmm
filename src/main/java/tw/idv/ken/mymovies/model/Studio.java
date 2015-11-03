package tw.idv.ken.mymovies.model;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;
import java.util.List;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.springframework.roo.addon.json.RooJson;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(versionField="")
@RooJson
public class Studio {

    /**
     */
    @NotNull
    @Column(name = "studio_name")
    @Size(min = 5, max = 255)
    private String name;

    /**
     */
    @Column(name = "website_url")
    @Size(min = 10, max = 255)
    private String url;

    /**
     */
    private Integer filmCount;

	public static List<Studio> findAllStudios(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM Studio o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY o." + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, Studio.class).getResultList();
    }
}
