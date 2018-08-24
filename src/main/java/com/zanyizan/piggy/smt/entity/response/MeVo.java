package com.zanyizan.piggy.smt.entity.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author dinglei
 * @date 2018/07/22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeVo {

    private UserVo user;
    private UserVo friend;
    private List<FriendStatus> friendStatus;
    private boolean glanced;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FriendStatus{
        private String time;
        private long ts;
        private String text;
        private Integer type;
    }
}
