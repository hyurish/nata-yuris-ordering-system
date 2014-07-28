package ua.yuris.restaurant.web;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ua.yuris.restaurant.model.results.PopularDishesReportWrapper;
import ua.yuris.restaurant.model.results.RevenueReport;
import ua.yuris.restaurant.service.ReportService;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 08.06.14
 * Time: 12:54
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class ReportsBackingBean implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(ReportsBackingBean.class);


    @ManagedProperty(value = "#{reportService}")
    private ReportService reportService;

    private Date fromDate;
    private Date toDate;

    List<RevenueReport> revenueReports;
    List<PopularDishesReportWrapper> popularDishesReportWrappers;

    public ReportsBackingBean() {
    }

    @PostConstruct
    public void initialize() {
        fromDate = new DateTime().minusMonths(1).withDayOfMonth(1).toDate();
        toDate = new DateTime().withDayOfMonth(1).minusDays(1).toDate();

    }

    public void onRevenueForPeriod() {
        clearReports();
        revenueReports = reportService.findRevenue(getFromDate(),
                new DateTime(getToDate()).plusDays(1).toDate());
    }

    public void onPopularDishes() {
        clearReports();
        popularDishesReportWrappers =
                reportService.findPopularDishesRevenue(getFromDate(),
                        new DateTime(getToDate()).plusDays(1).toDate());
    }

    private void clearReports() {
        revenueReports = null;
        popularDishesReportWrappers = null;
    }

    public ReportService getReportService() {
        return reportService;
    }

    public void setReportService(ReportService reportService) {
        this.reportService = reportService;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public List<RevenueReport> getRevenueReports() {
        return revenueReports;
    }

    public void setRevenueReports(List<RevenueReport> revenueReports) {
        this.revenueReports = revenueReports;
    }

    public List<PopularDishesReportWrapper> getPopularDishesReportWrappers() {
        return popularDishesReportWrappers;
    }

    public void setPopularDishesReportWrappers(
            List<PopularDishesReportWrapper> popularDishesReportWrappers) {
        this.popularDishesReportWrappers = popularDishesReportWrappers;
    }

    public Long getTotalOrdersNumber() {
        if (revenueReports != null) {
            Long totalNumber = 0L;
            for (RevenueReport reportResult : revenueReports) {
                totalNumber += reportResult.getOrdersNumber();
            }
            return totalNumber;
        }
        return 0L;
    }

    public Double getTotalPrettyRevenue() {
        if (revenueReports != null) {
            BigDecimal totalRevenue = BigDecimal.ZERO;
            for (RevenueReport reportResult : revenueReports) {
                totalRevenue = totalRevenue.add(reportResult.getRevenue());
            }
            return totalRevenue.divide(new BigDecimal(100.)).doubleValue();
        }
        return 0.;
    }

    public Long getTotalMenuItemsNumber() {
        if (popularDishesReportWrappers != null) {
            Long totalNumber = 0L;
            for (PopularDishesReportWrapper wrapper :
                    popularDishesReportWrappers) {
                totalNumber += wrapper.getTotalQuantity();
            }
            return totalNumber;
        }
        return 0L;
    }

    public Double getTotalMenuItemsPrettyRevenue() {
        if (popularDishesReportWrappers != null) {
            BigDecimal totalRevenue = BigDecimal.ZERO;
            for (PopularDishesReportWrapper wrapper :
                    popularDishesReportWrappers) {
                totalRevenue = totalRevenue.add(wrapper.getBDTotalRevenue());
            }
            return totalRevenue.divide(new BigDecimal(100.)).doubleValue();
        }
        return 0.;
    }

}