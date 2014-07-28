package ua.yuris.restaurant.web;

import java.io.File;
import java.io.FilenameFilter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.primefaces.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 20.05.14
 * Time: 12:47
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@RequestScoped
public class SelectImageBackingBean implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(SelectImageBackingBean.class);

    private List<String> images;

    public SelectImageBackingBean() {
    }

    @PostConstruct
    public void initialize() {
        images = new ArrayList<>();

        String relativeWebPath = "/resources/images/gallery";
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext externalContext = context.getExternalContext();
        ServletContext sc = (ServletContext) externalContext.getContext();
        File dir = null;
        try {
            dir = new File(sc.getRealPath(relativeWebPath));
            if (dir.isDirectory()) {
                FilenameFilter filter = new FilenameFilter() {
                    public boolean accept(File directory, String fileName) {
                        return fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") ||
                                fileName.endsWith(".gif") || fileName.endsWith(".png");
                    }
                };
                for (File file : dir.listFiles(filter)) {
                    images.add(file.getName());
                }
            }
        } catch (Exception e) {
            LOG.error("Gallery with pictures can't be created");
        }

    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public void selectImageFromDialog(String image) {
        RequestContext.getCurrentInstance().closeDialog(image);
    }
}
