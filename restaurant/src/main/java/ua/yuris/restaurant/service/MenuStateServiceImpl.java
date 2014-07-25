package ua.yuris.restaurant.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.transaction.annotation.Transactional;

import ua.yuris.restaurant.model.MenuCategory;
import ua.yuris.restaurant.model.MenuItem;
import ua.yuris.restaurant.repository.MenuCategoryRepository;
import ua.yuris.restaurant.repository.MenuItemRepository;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 29.05.14
 * Time: 22:19
 * To change this template use File | Settings | File Templates.
 */
@Named("menuStateService")
@Transactional(readOnly = true)
@Scope("session")
public class MenuStateServiceImpl
        implements MenuStateService {
    private static final Logger LOG = LoggerFactory.getLogger(MenuStateServiceImpl.class);

    @Inject
    private MenuItemRepository menuItemRepository;
    @Inject
    private MenuCategoryRepository menuCategoryRepository;

    public MenuStateServiceImpl() {
    }

    private List<MenuCategory> categories;
    private Map<MenuCategory, List<MenuItem>> items = new HashMap<>();

    @PostConstruct
    public void initialize() {
        categories = menuCategoryRepository.findByActiveTrueOrderByPriorityAsc();
        List<MenuItem> list;
        for (MenuCategory menuCategory : categories) {
            list = menuItemRepository.findByActiveTrueAndMenuCategoryIdOrderByPriorityAsc(
                    menuCategory.getId());
            items.put(menuCategory, list);
        }
    }

    @Override
    public List<MenuCategory> findAllActiveCategories() {
        return categories;
    }

    @Override
    public List<MenuItem> findAllActiveMenuItemByCategory(MenuCategory menuCategory) {
        return items.get(menuCategory);
    }

    @Override
    public MenuCategory findCategory(Long categoryId) {
        for (MenuCategory menuCategory : categories) {
            if (menuCategory.getId().equals(categoryId)) {
                return menuCategory;
            }
        }
        return null;
    }

    public List<MenuCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<MenuCategory> categories) {
        this.categories = categories;
    }

    public Map<MenuCategory, List<MenuItem>> getItems() {
        return items;
    }

    public void setItems(Map<MenuCategory, List<MenuItem>> items) {
        this.items = items;
    }
}
