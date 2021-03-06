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
import tw.idv.ken.mymovies.model.MediaFile;
import tw.idv.ken.mymovies.model.MediaFileDataOnDemand;
import tw.idv.ken.mymovies.model.MediaType;

privileged aspect MediaFileDataOnDemand_Roo_DataOnDemand {
    
    declare @type: MediaFileDataOnDemand: @Component;
    
    private Random MediaFileDataOnDemand.rnd = new SecureRandom();
    
    private List<MediaFile> MediaFileDataOnDemand.data;
    
    public MediaFile MediaFileDataOnDemand.getNewTransientMediaFile(int index) {
        MediaFile obj = new MediaFile();
        setFormat(obj, index);
        setStorage(obj, index);
        return obj;
    }
    
    public void MediaFileDataOnDemand.setFormat(MediaFile obj, int index) {
        MediaType format = MediaType.class.getEnumConstants()[0];
        obj.setFormat(format);
    }
    
    public void MediaFileDataOnDemand.setStorage(MediaFile obj, int index) {
        String storage = "storage_" + index;
        obj.setStorage(storage);
    }
    
    public MediaFile MediaFileDataOnDemand.getSpecificMediaFile(int index) {
        init();
        if (index < 0) {
            index = 0;
        }
        if (index > (data.size() - 1)) {
            index = data.size() - 1;
        }
        MediaFile obj = data.get(index);
        Long id = obj.getId();
        return MediaFile.findMediaFile(id);
    }
    
    public MediaFile MediaFileDataOnDemand.getRandomMediaFile() {
        init();
        MediaFile obj = data.get(rnd.nextInt(data.size()));
        Long id = obj.getId();
        return MediaFile.findMediaFile(id);
    }
    
    public boolean MediaFileDataOnDemand.modifyMediaFile(MediaFile obj) {
        return false;
    }
    
    public void MediaFileDataOnDemand.init() {
        int from = 0;
        int to = 10;
        data = MediaFile.findMediaFileEntries(from, to);
        if (data == null) {
            throw new IllegalStateException("Find entries implementation for 'MediaFile' illegally returned null");
        }
        if (!data.isEmpty()) {
            return;
        }
        
        data = new ArrayList<MediaFile>();
        for (int i = 0; i < 10; i++) {
            MediaFile obj = getNewTransientMediaFile(i);
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
