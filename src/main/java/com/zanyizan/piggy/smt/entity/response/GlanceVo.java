package com.zanyizan.piggy.smt.entity.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author dinglei
 * @date 2018/08/11
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GlanceVo {

    private List<GlanceMessage> message;
    private boolean glanced;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GlanceMessage{
        private String title;
        private String message;
    }
}
