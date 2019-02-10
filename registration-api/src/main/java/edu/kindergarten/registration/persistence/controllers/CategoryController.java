package edu.kindergarten.registration.persistence.controllers;

import edu.kindergarten.registration.persistence.model.Category;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Stateless
@Transactional
public class CategoryController {
    @PersistenceContext(unitName = "KinderGartenDS")
    private EntityManager em;
    private final static Logger log = Logger.getLogger(CategoryController.class);
    public Category findById(int id) {
        log.infov("finding Category instances for id {0}", id);
        Category category;
        try {
            category = em.find(Category.class, id);
        } catch (RuntimeException re) {
            log.error("find by Id failed", re);
            return null;
        }
        log.infov("Found {0} Category relationships!", category.getCategoryid());
        return category;
    }

    public List<Category> findAll() {
        log.infov("finding all Category instances");
        List<Category> results = new ArrayList<>();
        try {
            results = em.createNamedQuery("Category.findAll", Category.class).getResultList();
        } catch (RuntimeException re) {
            log.error("find all failed", re);
        }
        log.infov("Found {0} Category relationships!", results.size());
        return results;
    }
}
