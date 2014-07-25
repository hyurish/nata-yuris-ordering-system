package ua.yuris.restaurant.web;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ua.yuris.restaurant.model.Account;
import ua.yuris.restaurant.model.MenuCategory;
import ua.yuris.restaurant.model.MenuItem;
import ua.yuris.restaurant.model.Order;
import ua.yuris.restaurant.model.OrderDetail;
import ua.yuris.restaurant.model.RestaurantTable;
import ua.yuris.restaurant.model.enums.OrderStatusType;
import ua.yuris.restaurant.service.AccountService;
import ua.yuris.restaurant.service.MenuStateService;
import ua.yuris.restaurant.service.OrderService;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 06.06.14
 * Time: 20:53
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class GenerateBackingBean
        implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(GenerateBackingBean.class);

    @ManagedProperty(value = "#{orderService}")
    private OrderService orderService;

    @ManagedProperty(value = "#{accountService}")
    private AccountService accountService;

    @ManagedProperty(value = "#{menuStateService}")
    private MenuStateService menuStateService;

    List<Account> waiters;
    List<RestaurantTable> restaurantTables;

    int[] categoryRatios = {11, 18, 23, 33, 38, 41, 61, 69, 80, 87, 97, 110, 114, 122};
    int[] menuItemsRatios = {11, 13, 20, 37, 53, 58, 68, 80, 87, 97, 110, 114, 122};

    int[] orderInfosRatios = {30, 50, 70, 82, 90, 96, 100, 103, 105, 106, 107, 108};
    int[] itemQuantityRatios = {100, 130, 140, 145, 148, 150};

    public GenerateBackingBean() {
    }

    @PostConstruct
    public void initialize() {
        waiters = accountService.findAllAccountByRoleDescription("ROLE_WAITER");
        restaurantTables = orderService.findAllActiveTable();
    }


    public void serviceGenerate() {
        Random rand = new Random(47);
        DateTime dateTime = new DateTime().minusDays(50).withTimeAtStartOfDay();
        Order order = null;

        for (int i = 0; i < 40; i++) {
            for (int j = 0; j < 48; j++) {
                order = new Order();
                order.setGuestName("Bren_" + i + j);
                order.setRestaurantTable(generateRestaurantTable(rand, restaurantTables));
                order.setWaiter(generateWaiter(rand, waiters));
                order.setStatus(generateOrderStatus(rand, 0.03));
                order.setActive(generateActive(rand, 0.05));

                dateTime = dateTime.plusMinutes(30);
                order.setOrderTime(dateTime.plusMinutes(rand.nextInt(30) - 14));


                for (int k = 0; k < generateOrderInfoQuantity(rand); k++) {
                    order.addOrderDetail(generateOrderInfo(rand));
                }
                orderService.saveOrder(order);
            }
        }
    }

    private RestaurantTable generateRestaurantTable(Random rand,
                                                    List<RestaurantTable> restaurantTables) {
        return restaurantTables.get(rand.nextInt(restaurantTables.size()));
    }

    private Account generateWaiter(Random rand, List<Account> waiters) {
        return waiters.get(rand.nextInt(waiters.size()));
    }

    private OrderStatusType generateOrderStatus(Random rand, double notPaidRatio) {

        if (rand.nextFloat() < notPaidRatio) {
            return OrderStatusType.UNPAID;
        }
        return OrderStatusType.PAID;
    }

    private Boolean generateActive(Random rand, double notActive) {

        if (rand.nextFloat() < notActive) {
            return false;
        }
        return true;
    }

    private OrderDetail generateOrderInfo(Random rand) {
        OrderDetail orderDetail = new OrderDetail();
        MenuCategory menuCategory = generateCategory(rand);
        MenuItem menuItem = generateMenuItem(rand, menuCategory);
        orderDetail.setMenuItem(menuItem);
        orderDetail.setCooked(true);
        orderDetail.setItemQuantity(generateItemQuantity(rand));
        orderDetail.setItemPrice(menuItem.getPrice());
        orderDetail.setActive(generateActive(rand, 0.04));
        return orderDetail;
    }

    private MenuCategory generateCategory(Random rand) {
        List<MenuCategory> categories = menuStateService.findAllActiveCategories();
        int n = rand.nextInt(categoryRatios[categories.size() - 1]);
        for (int i = 0; i < categories.size(); i++) {
            if (n < categoryRatios[i]) {
                return categories.get(i);
            }
        }
        return null;

    }

    private MenuItem generateMenuItem(Random rand, MenuCategory menuCategory) {
        List<MenuItem> menuItems = menuStateService.findAllActiveMenuItemByCategory(menuCategory);
        int n = rand.nextInt(menuItemsRatios[menuItems.size() - 1]);
        for (int i = 0; i < menuItems.size(); i++) {
            if (n < menuItemsRatios[i]) {
                return menuItems.get(i);
            }
        }
        return null;
    }

    private int generateOrderInfoQuantity(Random rand) {
        int n = rand.nextInt(orderInfosRatios[orderInfosRatios.length - 1]);
        for (int i = 0; i < orderInfosRatios.length; i++) {
            if (n < orderInfosRatios[i]) {
                return ++i;
            }
        }
        return 0;
    }

    private int generateItemQuantity(Random rand) {
        int n = rand.nextInt(itemQuantityRatios[itemQuantityRatios.length - 1]);
        LOG.info(String.valueOf(n));
        int k = 0;
        for (int i = 0; i < itemQuantityRatios.length; i++) {
            if (n < itemQuantityRatios[i]) {
                k = ++i;
                LOG.info("" + k);
                return k;
            }
        }
        LOG.info("(())");
        return 0;
    }

    public AccountService getAccountService() {
        return accountService;
    }

    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    public MenuStateService getMenuStateService() {
        return menuStateService;
    }

    public void setMenuStateService(MenuStateService menuStateService) {
        this.menuStateService = menuStateService;
    }

    public OrderService getOrderService() {
        return orderService;
    }

    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    public List<RestaurantTable> getRestaurantTables() {
        return restaurantTables;
    }

    public void setRestaurantTables(List<RestaurantTable> restaurantTables) {
        this.restaurantTables = restaurantTables;
    }

    public List<Account> getWaiters() {
        return waiters;
    }

    public void setWaiters(List<Account> waiters) {
        this.waiters = waiters;
    }
}
