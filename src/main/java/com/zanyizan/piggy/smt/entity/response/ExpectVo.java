package com.zanyizan.piggy.smt.entity.response;

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
public class ExpectVo {

    private Integer days;
    private String message;
}
