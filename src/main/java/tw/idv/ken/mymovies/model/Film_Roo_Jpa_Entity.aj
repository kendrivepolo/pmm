// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package tw.idv.ken.mymovies.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import tw.idv.ken.mymovies.model.Film;

privileged aspect Film_Roo_Jpa_Entity {
    
    declare @type: Film: @Entity;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long Film.id;
    
    public Long Film.getId() {
        return this.id;
    }
    
    public void Film.setId(Long id) {
        this.id = id;
    }
    
}
