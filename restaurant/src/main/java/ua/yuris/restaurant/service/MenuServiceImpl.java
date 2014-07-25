package ua.yuris.restaurant.service;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import ua.yuris.restaurant.model.MenuCategory;
import ua.yuris.restaurant.model.MenuItem;
import ua.yuris.restaurant.repository.MenuCategoryRepository;
import ua.yuris.restaurant.repository.MenuItemRepository;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 07.05.14
 * Time: 11:42
 * To change this template use File | Settings | File Templates.
 */
@Named("menuService")
@Transactional(readOnly = true)
public class MenuServiceImpl
        implements MenuService {
    private static final Logger LOG = LoggerFactory.getLogger(MenuServiceImpl.class);

    @Inject
    private MenuItemRepository menuItemRepository;
    @Inject
    private MenuCategoryRepository menuCategoryRepository;

    public MenuServiceImpl() {
    }

    @Override
    public MenuItem findMenuItem(Long menuItemId) {
        return menuItemRepository.findOne(menuItemId);
    }

    @Override
    public List<MenuItem> findAllMenuItem() {
        return menuItemRepository.findAll();
    }

    @Override
    public MenuCategory findCategory(Long categoryId) {
        return menuCategoryRepository.findOne(categoryId);
    }

    @Override
    public List<MenuItem> findAllMenuItemsByCategory(MenuCategory menuCategory) {
        return menuItemRepository.findByMenuCategoryIdOrderByActiveDescPriorityAsc(
                menuCategory.getId());
    }

    @Override
    public List<MenuItem> findAllActiveMenuItemsByCategory(MenuCategory menuCategory) {
        return menuItemRepository.findByActiveTrueAndMenuCategoryIdOrderByPriorityAsc(
                menuCategory.getId());
    }

    @Override
    public List<MenuCategory> findAllActiveCategories() {
        return menuCategoryRepository.findByActiveTrueOrderByPriorityAsc();
    }

    @Override
    public List<MenuCategory> findAllCategories() {
        return menuCategoryRepository.findByOrderByActiveDescPriorityAsc();
    }

    @Override
    public Integer getCategoryMaxPriority() {
        return menuCategoryRepository.getMaxPriority();
    }

    @Override
    public Integer getMenuItemMaxPriority() {
        return menuItemRepository.getMaxPriority();
    }

    @Override
    @Transactional(readOnly = false)
    public MenuCategory saveCategory(MenuCategory menuCategory) {
        Assert.notNull(menuCategory, "The entity must not be null!");

        return menuCategoryRepository.save(menuCategory);
    }

    @Override
    @Transactional(readOnly = false)
    public MenuItem saveMenuItem(MenuItem menuItem) {
        Assert.notNull(menuItem, "The entity must not be null!");

        return menuItemRepository.save(menuItem);
    }
}
