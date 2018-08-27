package com.zanyizan.piggy.smt.entity.response;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * @author dinglei
 * @date 2018/07/17
 */
@Data
@Builder
public class UserVo {

    private String userId;
    private String userName;
    private String avatar;
    private Date createTime;
    private String money;
    private boolean isNew = false;
}
