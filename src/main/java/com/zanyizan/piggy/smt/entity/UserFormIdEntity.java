package com.zanyizan.piggy.smt.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author dinglei
 * @date 2018/08/21
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserFormIdEntity {

    private String userId;
    private String thirdId;
    private String formId;
}
