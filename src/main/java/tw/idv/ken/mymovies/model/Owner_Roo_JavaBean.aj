// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package tw.idv.ken.mymovies.model;

import tw.idv.ken.mymovies.model.Owner;

privileged aspect Owner_Roo_JavaBean {
    
    public String Owner.getOwnerName() {
        return this.ownerName;
    }
    
    public void Owner.setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
    
    public String Owner.getOwnerID() {
        return this.ownerID;
    }
    
    public void Owner.setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }
    
    public String Owner.getPassword() {
        return this.password;
    }
    
    public void Owner.setPassword(String password) {
        this.password = password;
    }
    
}
