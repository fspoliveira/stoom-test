package br.com.stoom.configuration;

import br.com.stoom.filter.TransactionIdFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebFilterConfiguration {

    @Autowired
    private TransactionIdFilter transactionIdFilter;

    @Bean
    public FilterRegistrationBean<TransactionIdFilter> transactionIdFilterRegistrationBean() {
        FilterRegistrationBean<TransactionIdFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(transactionIdFilter);
        filterRegistrationBean.setOrder(1);
        return filterRegistrationBean;
    }
}
