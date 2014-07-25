package ua.yuris.restaurant.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ua.yuris.restaurant.model.MenuItem;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 07.05.14
 * Time: 11:35
 * To change this template use File | Settings | File Templates.
 */
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    List<MenuItem> findByActiveTrueOrderByPriorityAsc();

    List<MenuItem> findByActiveTrueAndMenuCategoryIdOrderByPriorityAsc(Long categoryId);

    List<MenuItem> findByMenuCategoryIdOrderByActiveDescPriorityAsc(Long categoryId);

    @Query("select max(m.priority) from MenuItem m")
    Integer getMaxPriority();
}
