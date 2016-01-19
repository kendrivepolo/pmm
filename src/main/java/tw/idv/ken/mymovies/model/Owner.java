package tw.idv.ken.mymovies.model;
import java.util.List;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.roo.addon.json.RooJson;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(versionField="")
@RooJson
public class Owner {

    /**
     */
    @NotNull
    @Column(name = "owner_name")
    @Size(min = 5, max = 20)
    private String ownerName;

    /**
     */
    @NotNull
    @Column(name = "owner_id", unique = true)
    @Size(min = 5, max = 10)
    private String ownerID;

    /**
     */
    @NotNull
    @Column(name = "password")
    @Size(min = 5, max = 10)
    private String password;
    
	public static Owner findOwnerByOwnerId(final String ownerId) {
		return entityManager()
				.createQuery(
						"SELECT o FROM Owner o WHERE o.ownerID = :ownerId",
						Owner.class).setParameter("ownerId", ownerId)
				.getSingleResult();
	}
}
