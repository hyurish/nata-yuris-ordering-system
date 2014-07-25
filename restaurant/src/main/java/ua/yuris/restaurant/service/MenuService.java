package ua.yuris.restaurant.service;

import java.util.List;

import ua.yuris.restaurant.model.MenuCategory;
import ua.yuris.restaurant.model.MenuItem;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 07.05.14
 * Time: 11:38
 * To change this template use File | Settings | File Templates.
 */
public interface MenuService {

    MenuItem findMenuItem(Long menuItemId);

    List<MenuItem> findAllMenuItem();

    List<MenuItem> findAllMenuItemsByCategory(MenuCategory menuCategory);

    List<MenuItem> findAllActiveMenuItemsByCategory(MenuCategory menuCategory);

    Integer getMenuItemMaxPriority();

    MenuItem saveMenuItem(MenuItem menuItem);

    MenuCategory findCategory(Long categoryId);

    List<MenuCategory> findAllCategories();

    List<MenuCategory> findAllActiveCategories();

    Integer getCategoryMaxPriority();

    MenuCategory saveCategory(MenuCategory menuCategory);
}
