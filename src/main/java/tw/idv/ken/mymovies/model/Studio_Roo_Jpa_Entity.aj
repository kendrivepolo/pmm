// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package tw.idv.ken.mymovies.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import tw.idv.ken.mymovies.model.Studio;

privileged aspect Studio_Roo_Jpa_Entity {
    
    declare @type: Studio: @Entity;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long Studio.id;
    
    public Long Studio.getId() {
        return this.id;
    }
    
    public void Studio.setId(Long id) {
        this.id = id;
    }
    
}