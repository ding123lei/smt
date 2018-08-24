package com.zanyizan.piggy.smt.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author dinglei
 * @date 2018/08/09
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageEntity {

    private String taskId;
    private String sourceUid;
    private String targetUid;
    private String content;
    private int type;
    private boolean isRead;
    private Date createTime;
}
