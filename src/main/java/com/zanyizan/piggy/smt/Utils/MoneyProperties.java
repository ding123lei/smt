package com.zanyizan.piggy.smt.Utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author dinglei
 * @date 2018/07/19
 */
@Data
@Component
@ConfigurationProperties(prefix="zyz.money")
public class MoneyProperties {

    private Integer dailySum;
    private List<Integer> minRange;
    private List<Integer> middleRange;
}
