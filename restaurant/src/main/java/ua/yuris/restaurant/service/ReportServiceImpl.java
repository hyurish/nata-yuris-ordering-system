package ua.yuris.restaurant.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import ua.yuris.restaurant.model.results.PopularDishesReport;
import ua.yuris.restaurant.model.results.PopularDishesReportWrapper;
import ua.yuris.restaurant.model.results.RevenueReport;
import ua.yuris.restaurant.repository.OrderDetailRepository;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 09.06.14
 * Time: 0:02
 * To change this template use File | Settings | File Templates.
 */
@Named("reportService")
@Transactional(readOnly = true)
public class ReportServiceImpl
        implements ReportService {
    private static final Logger LOG = LoggerFactory.getLogger(ReportServiceImpl.class);

    @Inject
    private OrderDetailRepository orderDetailRepository;

    public ReportServiceImpl() {
    }

    @Override
    public List<RevenueReport> findRevenue(Date fromDate, Date toDate) {
        return orderDetailRepository.getRevenueWithNamedNativeQuery(fromDate, toDate);
    }

    @Override
    public List<PopularDishesReportWrapper> findPopularDishesRevenue(Date fromDate, Date toDate) {
        List<PopularDishesReportWrapper> popularDishesReportWrappers = new ArrayList<>();
        List<PopularDishesReport> popularDishesReports =
                orderDetailRepository.getPopularDishesWithNamedNativeQuery(fromDate, toDate);

        PopularDishesReportWrapper wrapper;
        for (PopularDishesReport popularDishesReport : popularDishesReports) {
            wrapper = getPopularDishesReportWrapperByCategoryId(popularDishesReportWrappers,
                    popularDishesReport.getCategoryId());
            if (wrapper != null) {
                wrapper.getPopularDishesReports().add(popularDishesReport);
            } else {
                wrapper = createPopularDishesReportWrapper(popularDishesReport);
                wrapper.getPopularDishesReports().add(popularDishesReport);
                popularDishesReportWrappers.add(wrapper);
            }
        }

        return popularDishesReportWrappers;
    }

    private PopularDishesReportWrapper createPopularDishesReportWrapper(
            PopularDishesReport popularDishesReport
    ) {
        PopularDishesReportWrapper wrapper = new PopularDishesReportWrapper();
        wrapper.setCategoryId(popularDishesReport.getCategoryId());
        wrapper.setCategoryTitle(popularDishesReport.getCategoryTitle());
        return wrapper;
    }

    private PopularDishesReportWrapper getPopularDishesReportWrapperByCategoryId(
            List<PopularDishesReportWrapper> popularDishesReportWrappers, Long categoryId) {
        for (PopularDishesReportWrapper reportWrapper : popularDishesReportWrappers) {
            if (reportWrapper.getCategoryId().equals(categoryId)) {
                return reportWrapper;
            }
        }
        return null;
    }

}