package ua.yuris.restaurant.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ua.yuris.restaurant.model.OrderDetail;
import ua.yuris.restaurant.model.enums.CookingPlaceType;
import ua.yuris.restaurant.model.enums.OrderStatusType;
import ua.yuris.restaurant.model.results.PopularDishesReport;
import ua.yuris.restaurant.model.results.RevenueReport;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 02.06.14
 * Time: 18:05
 * To change this template use File | Settings | File Templates.
 */
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

    List<OrderDetail>
    findByCookedIsFalseAndActiveTrueAndOrderActiveTrueAndMenuItemCookingPlaceAndOrderStatusOrderByOrderOrderTimeAsc
            (CookingPlaceType cookingPlace, OrderStatusType status);

    List<RevenueReport> getRevenueWithNamedNativeQuery(Date fromDate, Date toDate);

    List<PopularDishesReport> getPopularDishesWithNamedNativeQuery(Date fromDate,
                                                                         Date toDate);
}
