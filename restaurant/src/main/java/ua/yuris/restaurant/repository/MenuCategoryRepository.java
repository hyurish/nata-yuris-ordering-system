package ua.yuris.restaurant.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ua.yuris.restaurant.model.MenuCategory;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 08.05.14
 * Time: 22:48
 * To change this template use File | Settings | File Templates.
 */
public interface MenuCategoryRepository extends JpaRepository<MenuCategory, Long> {

    MenuCategory findByActiveTrueAndId(Long id);

    List<MenuCategory> findByActiveTrueOrderByPriorityAsc();

    List<MenuCategory> findByOrderByActiveDescPriorityAsc();

    @Query("select max(c.priority) from MenuCategory c")
    Integer getMaxPriority();
}
