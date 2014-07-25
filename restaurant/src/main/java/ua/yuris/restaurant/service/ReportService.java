package ua.yuris.restaurant.service;

import java.util.Date;
import java.util.List;

import ua.yuris.restaurant.model.results.PopularDishesReportWrapper;
import ua.yuris.restaurant.model.results.RevenueReport;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 08.06.14
 * Time: 23:59
 * To change this template use File | Settings | File Templates.
 */
public interface ReportService {
    List<RevenueReport> findRevenue(Date fromDate, Date toDate);

    List<PopularDishesReportWrapper> findPopularDishesRevenue(Date fromDate, Date toDate);
}
