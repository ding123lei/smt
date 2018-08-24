package com.zanyizan.piggy.smt.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author dinglei
 * @date 2018/08/12
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SummaryEntity {

    private String userId;
    private int consume;
    private int deposit;
    private int manage;
    private int financing;
}
