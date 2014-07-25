package ua.yuris.restaurant.model.results;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.style.ToStringCreator;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 10.06.14
 * Time: 19:27
 * To change this template use File | Settings | File Templates.
 */
public class PopularDishesReportWrapper {
    private static final Logger LOG =
            LoggerFactory.getLogger(PopularDishesReportWrapper.class);

    private Long categoryId;
    private String categoryTitle;
    private List<PopularDishesReport> popularDishesReports;


    public PopularDishesReportWrapper() {
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
    }

    public List<PopularDishesReport> getPopularDishesReports() {
        if (popularDishesReports == null) {
            popularDishesReports = new ArrayList<>();
        }
        return popularDishesReports;
    }

    public void setPopularDishesReports(List<PopularDishesReport> popularDishesReports) {
        this.popularDishesReports = popularDishesReports;
    }

    public Long getTotalQuantity() {
        if (popularDishesReports != null) {
            Long totalNumber = 0L;
            for (PopularDishesReport popularDishesReport : popularDishesReports) {
                totalNumber += popularDishesReport.getMenuItemQuantity().longValue();
            }
            return totalNumber;
        }
        return 0L;
    }

    public Double getTotalRevenue() {
        if (popularDishesReports != null) {
            BigDecimal totalRevenue = BigDecimal.ZERO;
            for (PopularDishesReport popularDishesReport : popularDishesReports) {
                totalRevenue = totalRevenue.add(popularDishesReport.getRevenue());
            }
            return totalRevenue.divide(new BigDecimal(100.)).doubleValue();
        }
        return 0.;
    }

    public BigDecimal getBDTotalRevenue() {
        if (popularDishesReports != null) {
            BigDecimal totalRevenue = BigDecimal.ZERO;
            for (PopularDishesReport popularDishesReport : popularDishesReports) {
                totalRevenue = totalRevenue.add(popularDishesReport.getRevenue());
            }
            return totalRevenue;
        }
        return BigDecimal.ZERO;
    }

    @Override
    public String toString() {
        ToStringCreator sc = new ToStringCreator(this);
        sc
                .append("categoryId", this.getCategoryId())
                .append("categoryTitle", this.getCategoryTitle());
        if (popularDishesReports != null && popularDishesReports.size() > 0) {
            for (PopularDishesReport result : popularDishesReports) {
                sc.append(result);
            }
        }
        return sc.toString();
    }
}
