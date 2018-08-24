package com.zanyizan.piggy.smt.service;

import com.zanyizan.piggy.smt.Utils.MoneyProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @author dinglei
 * @date 2018/07/19
 */
@Service
@EnableConfigurationProperties(MoneyProperties.class)
public class MoneyService {

    @Autowired
    private MoneyProperties moneyProperties;

    private static Random random = new Random();

    public List<Integer> getDailyMoney(){
        int min = random.nextInt(moneyProperties.getMinRange().get(0)) + moneyProperties.getMinRange().get(1);
        int middle = random.nextInt(moneyProperties.getMiddleRange().get(0)) + moneyProperties.getMiddleRange().get(1);
        int max = moneyProperties.getDailySum() - min - middle;
        return Arrays.asList(min, middle, max);
    }
}
