package com.zanyizan.piggy.smt.controller;

import com.zanyizan.piggy.smt.tables.records.ZyzUserRecord;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static com.zanyizan.piggy.smt.Tables.ZYZ_USER;

/**
 * Demo controller
 * @author dinglei
 * @date 2018/07/11
 * @param
 */
@RestController
@RequestMapping("hello")
public class HelloWorldController {

    private static final Logger logger = LoggerFactory.getLogger(HelloWorldController.class);

    @Autowired
    private DSLContext dslContext;

    @RequestMapping(value = "echo/{name}")
    public String echo(@PathVariable String name) {
        logger.info("input : {}, test : {}", name, name);
        Optional<ZyzUserRecord> optional = dslContext.selectFrom(ZYZ_USER).
                where(ZYZ_USER.USER_ID.eq(name))
                .fetchOptional();
        if(optional.isPresent()){
            logger.info("User : {}", optional.get().toString());
        }
        return name;
    }
}
