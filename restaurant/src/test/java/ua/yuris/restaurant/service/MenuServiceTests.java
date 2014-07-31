package ua.yuris.restaurant.service;

import java.util.List;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import ua.yuris.restaurant.model.MenuCategory;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 31.07.14
 * Time: 2:18
 * To change this template use File | Settings | File Templates.
 */
@ContextConfiguration(locations = {"classpath:spring/applicationContext.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class MenuServiceTests {

    @Inject
    protected MenuService menuService;

    @Test
    @Transactional
    public void findMenuCategories() {
        List<MenuCategory> categories = menuService.findAllActiveCategories();
        assertThat(categories.size(), not(0));
        MenuCategory menuCategory = menuService.findCategory(1L);
        assertThat(menuCategory.getTitle(), is("Barbecue"));
    }
}
