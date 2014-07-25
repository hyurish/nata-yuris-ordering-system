package ua.yuris.restaurant.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ua.yuris.restaurant.model.RestaurantTable;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 12.05.14
 * Time: 10:19
 * To change this template use File | Settings | File Templates.
 */
public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {

    List<RestaurantTable> findByActiveTrueOrderByTableNumberAsc();

}
