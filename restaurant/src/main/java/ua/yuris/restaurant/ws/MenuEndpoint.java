package ua.yuris.restaurant.ws;

import java.util.List;

import javax.inject.Inject;

import localhost._8080.menu.schemas.CategoryType;
import localhost._8080.menu.schemas.MenuByCategoryIdResponse;
import localhost._8080.menu.schemas.MenuItemType;
import localhost._8080.menu.schemas.MenuResponse;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.xpath.XPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import ua.yuris.restaurant.model.MenuCategory;
import ua.yuris.restaurant.model.MenuItem;
import ua.yuris.restaurant.service.MenuService;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 25.06.14
 * Time: 21:37
 * To change this template use File | Settings | File Templates.
 */
@Endpoint
public class MenuEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(MenuEndpoint.class);

    private static final String NAMESPACE_URI = "http://localhost:8080/menu/schemas";

    private XPath categoryIdExpression;

    @Inject
    private MenuService menuService;

    public MenuEndpoint()
            throws JDOMException {

        Namespace namespace = Namespace.getNamespace("mn", NAMESPACE_URI);

        categoryIdExpression = XPath.newInstance("//mn:Id");
        categoryIdExpression.addNamespace(namespace);

    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "MenuRequest")
    @ResponsePayload
    public MenuResponse handleMenuRequest(@RequestPayload Element menuRequest)
            throws Exception {

        List<MenuCategory> categories = menuService.findAllActiveCategories();
        MenuResponse menuResponse = createMenuResponse(categories);

        return menuResponse;
    }

    private MenuResponse createMenuResponse(List<MenuCategory> categories) {
        MenuResponse menuResponse = new MenuResponse();
        CategoryType categoryType = null;
        for (MenuCategory menuCategory : categories) {
            List<MenuItem> menuItems =
                    menuService.findAllActiveMenuItemsByCategory(menuCategory);
            categoryType = createCategoryType(menuCategory, menuItems);
            menuResponse.getCategories().add(categoryType);
        }
        return menuResponse;
    }

    private CategoryType createCategoryType(MenuCategory menuCategory, List<MenuItem> menuItems) {
        CategoryType categoryType = new CategoryType();
        categoryType.setId(menuCategory.getId());
        categoryType.setTitle(menuCategory.getTitle());
        categoryType.setPriority(menuCategory.getPriority());
        List<MenuItemType> menuItemTypes = categoryType.getMenuItems();
        MenuItemType menuItemType;
        for (MenuItem menuItem : menuItems) {
            menuItemType = createMenuItemType(menuItem);
            menuItemTypes.add(menuItemType);
        }
        return categoryType;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "MenuByCategoryIdRequest")
    @ResponsePayload
    public MenuByCategoryIdResponse handleMenuByCategoryIdRequest(
            @RequestPayload Element menuRequest)
            throws Exception {
        Long categoryId = Long.parseLong(categoryIdExpression.valueOf(menuRequest));

        MenuCategory menuCategory = menuService.findCategory(categoryId);
        List<MenuItem> menuItems = menuService.findAllActiveMenuItemsByCategory(menuCategory);

        MenuByCategoryIdResponse menuResponse =
                createMenuByCategoryIdResponse(menuCategory, menuItems);

        return menuResponse;
    }

    private MenuByCategoryIdResponse createMenuByCategoryIdResponse(
            MenuCategory menuCategory, List<MenuItem> menuItems
    ) {
        MenuByCategoryIdResponse menuResponse = new MenuByCategoryIdResponse();
        CategoryType categoryType = new CategoryType();
        categoryType.setId(menuCategory.getId());
        categoryType.setTitle(menuCategory.getTitle());
        categoryType.setPriority(menuCategory.getPriority());
        List<MenuItemType> menuItemTypes = categoryType.getMenuItems();
        MenuItemType menuItemType = null;
        for (MenuItem menuItem : menuItems) {
            menuItemType = createMenuItemType(menuItem);
            menuItemTypes.add(menuItemType);
        }
        menuResponse.getCategories().add(categoryType);
        return menuResponse;
    }

    private MenuItemType createMenuItemType(MenuItem menuItem) {
        MenuItemType menuItemType = new MenuItemType();
        menuItemType.setId(menuItem.getId());
        menuItemType.setTitle(menuItem.getTitle());
        menuItemType.setDescription(menuItem.getDescription());
        menuItemType.setWeight(menuItem.getMeasure());
        menuItemType.setPrice(menuItem.getPrettyPrice());
        menuItemType.setStatus(menuItem.getCookingPlace().name());
        menuItemType.setPriority(menuItem.getPriority());
        return menuItemType;
    }
}