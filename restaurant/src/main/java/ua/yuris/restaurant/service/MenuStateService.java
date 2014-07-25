package ua.yuris.restaurant.service;

import java.util.List;

import ua.yuris.restaurant.model.MenuCategory;
import ua.yuris.restaurant.model.MenuItem;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 29.05.14
 * Time: 22:19
 * To change this template use File | Settings | File Templates.
 */
public interface MenuStateService {

    List<MenuItem> findAllActiveMenuItemByCategory(MenuCategory menuCategory);

    MenuCategory findCategory(Long categoryId);

    List<MenuCategory> findAllActiveCategories();

}
