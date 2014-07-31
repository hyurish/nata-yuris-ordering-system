package ua.yuris.restaurant.model;

import java.util.HashSet;
import java.util.Set;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 01.08.14
 * Time: 0:34
 * To change this template use File | Settings | File Templates.
 */
public class MenuItemTest {
    DateTime createTime;

    @Before
    public void setup() {
        createTime = new DateTime();
    }

    @Test
    public void testEqualReflexive() {
        MenuItem menuItem = new MenuItem();
        menuItem.setTitle("Dessert");
        assertTrue(menuItem.equals(menuItem));
    }

    @Test
    public void testEqualLogical() {
        MenuItem menuItemExpected = new MenuItem();
        menuItemExpected.setTitle("Dessert");
        menuItemExpected.setCreateTime(createTime);

        MenuItem menuItemActual = new MenuItem();
        menuItemActual.setTitle("Dessert");
        menuItemActual.setCreateTime(createTime);
        assertTrue(menuItemExpected.equals(menuItemActual));

        menuItemActual.setTitle("Barbecue");
        menuItemActual.setCreateTime(createTime);
        assertFalse(menuItemExpected.equals(menuItemActual));

        menuItemActual.setTitle("Dessert");
        menuItemActual.setCreateTime(createTime.plusMillis(1));
        assertFalse(menuItemExpected.equals(menuItemActual));
    }

    @Test
    public void testEqualInSet() {
        Set<MenuItem> set = new HashSet<>();
        MenuItem menuItemDessert = new MenuItem();
        menuItemDessert.setTitle("Dessert");
        menuItemDessert.setCreateTime(createTime);
        set.add(menuItemDessert);
        set.add(menuItemDessert);
        assertThat(set.size(), is(1));

        MenuItem menuItemCoffee = new MenuItem();
        menuItemCoffee.setTitle("Coffee");
        menuItemCoffee.setCreateTime(createTime);
        set.add(menuItemCoffee);
        assertThat(set.size(), is(2));

        MenuItem menuItemDessertCreatedLater = new MenuItem();
        menuItemDessertCreatedLater.setTitle("Dessert");
        menuItemDessertCreatedLater.setCreateTime(createTime.plusMillis(1));
        set.add(menuItemDessertCreatedLater);
        assertThat(set.size(), is(3));
    }
}
