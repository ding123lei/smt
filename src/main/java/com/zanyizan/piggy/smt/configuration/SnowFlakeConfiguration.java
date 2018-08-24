package com.zanyizan.piggy.smt.configuration;

import com.zanyizan.piggy.smt.Utils.SnowFlake;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author dinglei
 * @date 2018/07/24
 */
@Configuration
public class SnowFlakeConfiguration {

    @Bean
    public SnowFlake build(){
        return new SnowFlake(1, 1);
    }
}
