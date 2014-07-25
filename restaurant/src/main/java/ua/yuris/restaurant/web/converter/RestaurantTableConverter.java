package ua.yuris.restaurant.web.converter;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ua.yuris.restaurant.model.RestaurantTable;
import ua.yuris.restaurant.service.OrderService;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 25.05.14
 * Time: 20:33
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@RequestScoped
public class RestaurantTableConverter
        implements Converter {
    private static final Logger LOG = LoggerFactory.getLogger(RestaurantTableConverter.class);

    @ManagedProperty(value = "#{orderService}")
    private OrderService orderService;

    public List<RestaurantTable> restaurantTables;

    public RestaurantTableConverter() {
    }

    @PostConstruct
    public void init() {
        restaurantTables = orderService.findAllActiveTable();
    }

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent,
                              String submittedValue) {
        if (submittedValue.trim().equals("")) {
            return null;
        } else {
            try {
                long id = Long.parseLong(submittedValue);
                for (RestaurantTable t : restaurantTables) {
                    if (t.getId().longValue() == id) {
                        return t;
                    }
                }

            } catch (NumberFormatException numberFormatException) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Conversion Error", "Not a valid table"));
            }
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object value) {
        if (value == null || value.equals("")) {
            return "";
        }
        return String.valueOf(((RestaurantTable) value).getId());
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
}
