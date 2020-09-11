package com.connectrn.tablereservation.cofig;

import com.connectrn.tablereservation.selector.TableSelector;
import com.connectrn.tablereservation.selector.TableSelectorImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public TableSelector tableSelector() {
        return new TableSelectorImpl();
    }

}
