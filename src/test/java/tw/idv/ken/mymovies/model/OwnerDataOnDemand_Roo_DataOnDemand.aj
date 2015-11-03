// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package tw.idv.ken.mymovies.model;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.springframework.stereotype.Component;
import tw.idv.ken.mymovies.model.Owner;
import tw.idv.ken.mymovies.model.OwnerDataOnDemand;

privileged aspect OwnerDataOnDemand_Roo_DataOnDemand {
    
    declare @type: OwnerDataOnDemand: @Component;
    
    private Random OwnerDataOnDemand.rnd = new SecureRandom();
    
    private List<Owner> OwnerDataOnDemand.data;
    
    public Owner OwnerDataOnDemand.getNewTransientOwner(int index) {
        Owner obj = new Owner();
        setOwnerID(obj, index);
        setOwnerName(obj, index);
        setPassword(obj, index);
        return obj;
    }
    
    public void OwnerDataOnDemand.setOwnerID(Owner obj, int index) {
        String ownerID = "ownerID_" + index;
        if (ownerID.length() > 10) {
            ownerID = new Random().nextInt(10) + ownerID.substring(1, 10);
        }
        obj.setOwnerID(ownerID);
    }
    
    public void OwnerDataOnDemand.setOwnerName(Owner obj, int index) {
        String ownerName = "ownerName_" + index;
        if (ownerName.length() > 20) {
            ownerName = ownerName.substring(0, 20);
        }
        obj.setOwnerName(ownerName);
    }
    
    public void OwnerDataOnDemand.setPassword(Owner obj, int index) {
        String password = "password_" + index;
        if (password.length() > 10) {
            password = password.substring(0, 10);
        }
        obj.setPassword(password);
    }
    
    public Owner OwnerDataOnDemand.getSpecificOwner(int index) {
        init();
        if (index < 0) {
            index = 0;
        }
        if (index > (data.size() - 1)) {
            index = data.size() - 1;
        }
        Owner obj = data.get(index);
        Long id = obj.getId();
        return Owner.findOwner(id);
    }
    
    public Owner OwnerDataOnDemand.getRandomOwner() {
        init();
        Owner obj = data.get(rnd.nextInt(data.size()));
        Long id = obj.getId();
        return Owner.findOwner(id);
    }
    
    public boolean OwnerDataOnDemand.modifyOwner(Owner obj) {
        return false;
    }
    
    public void OwnerDataOnDemand.init() {
        int from = 0;
        int to = 10;
        data = Owner.findOwnerEntries(from, to);
        if (data == null) {
            throw new IllegalStateException("Find entries implementation for 'Owner' illegally returned null");
        }
        if (!data.isEmpty()) {
            return;
        }
        
        data = new ArrayList<Owner>();
        for (int i = 0; i < 10; i++) {
            Owner obj = getNewTransientOwner(i);
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
            data.add(obj);
        }
    }
    
}
