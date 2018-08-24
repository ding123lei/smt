package com.zanyizan.piggy.smt.entity.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author dinglei
 * @date 2018/07/18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IndexVo {

    private Image image;
    private String userId;
    private String friendUserId;
    private List<FriendStatus> friendStatus;
    private String remark;
    private int status;
    private List<Integer> money;
    //截止到目前总共存钱天数
    private int totalDays;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Image {
        private String avatar;
        private String friendAvatar;
        private String background;
        private String decorate;
        private String subject;
        private String share;
        //图片编号 目前表示开始天数
        private int imageCode;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FriendStatus {
        private int type;
        private String message;
    }
}
