package ua.yuris.restaurant.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ua.yuris.restaurant.model.Order;
import ua.yuris.restaurant.model.enums.OrderStatusType;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 28.04.14
 * Time: 0:14
 * To change this template use File | Settings | File Templates.
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("select o from Order o where o.orderTime in " +
            "(select max(ord.orderTime) from Order ord where ord.waiter is null)")
    List<Order> findLastNoMansOrder();

    List<Order> findByActiveTrueAndWaiterIdIsNullOrderByOrderTimeDesc();

/*
    List<Order> findByStatusOrderByOrderTimeDesc(OrderStatusType status);
*/

    Integer countByStatus(OrderStatusType status);

/*
    List<Order> findByStatusOrderByOrderTimeDesc(OrderStatusType status, Pageable pageable);
*/

    List<Order> findByStatus(OrderStatusType status, Pageable pageable);

/*
    List<Order> findByIsActiveTrueAndStatusOrderByOrderTimeDesc(OrderStatusType status);
*/

    Integer countByActiveTrueAndStatus(OrderStatusType status);

/*
    List<Order> findByIsActiveTrueAndStatusOrderByOrderTimeDesc(OrderStatusType status,
                                                                Pageable pageable);
*/

    List<Order> findByActiveTrueAndStatus(OrderStatusType status, Pageable pageable);

    List<Order> findByWaiterIdAndStatusOrderByOrderTimeDesc(Long waiterId, OrderStatusType status);

    List<Order> findByActiveTrueAndWaiterIdAndStatusOrderByOrderTimeDesc(Long waiterId,
                                                                         OrderStatusType status);

}
