package com.zanyizan.piggy.smt.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zanyizan.piggy.smt.Utils.WxConfig;
import com.zanyizan.piggy.smt.Utils.WxUtils;
import com.zanyizan.piggy.smt.entity.WxTemplateKeyword;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @author dinglei
 * @date 2018/08/21
 */
@Service
@Slf4j
public class TemplateService {

    @Autowired
    private TokenService tokenService;

    @Value("${zyz.template.id}")
    private String templateId;

    public WxTemplateKeyword buildNormalNotice(){
        return WxTemplateKeyword.builder()
                .keyword1(WxTemplateKeyword.Value.builder().value("攒钱养成计划").build())
                .keyword2(WxTemplateKeyword.Value.builder().value("").build())
                .keyword3(WxTemplateKeyword.Value.builder().value("不要放弃").build())
                .keyword4(WxTemplateKeyword.Value.builder().value("点击“进入小程序查看”").build())
                .keyword5(WxTemplateKeyword.Value.builder().value("两个人7天攒钱养成计划").build())
                .build();
    }

    public WxTemplateKeyword buildNewTaskNotice(){
        return WxTemplateKeyword.builder()
                .keyword1(WxTemplateKeyword.Value.builder().value("存钱计划").build())
                .keyword2(WxTemplateKeyword.Value.builder().value("").build())
                .keyword3(WxTemplateKeyword.Value.builder().value("你的Ta已开启新计划").build())
                .keyword4(WxTemplateKeyword.Value.builder().value("点击“进入小程序查看”").build())
                .keyword5(WxTemplateKeyword.Value.builder().value("两个人一起完成新的存钱计划").build())
                .build();
    }

    public void sendTemplate(String openId, String formId, @NonNull WxTemplateKeyword keyword){
        log.info("Send template WeiXin request, openId : {}, formId : {}, message : {}", openId, formId, keyword.toString());
        String requestUrl = WxConfig.WX_TEMPLATE_SEND_URL.replace("ACCESS_TOKEN", tokenService.getAccesstoken());
        TemplateRequest request = TemplateRequest.builder()
                .touser(openId)
                .template_id(templateId)
                .form_id(formId)
                .page("pages/index")
                .data(keyword)
                .build();
        JSONObject response = WxUtils.httpsRequest(requestUrl, "POST", JSON.toJSONString(request));
        log.info("Send template WeiXin response, errcode : {}, errmsg : {}", response.get("errcode"), response.get("errmsg"));
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    private static class TemplateRequest{
        private String touser;
        private String template_id;
        private String form_id;
        private String page;
        private WxTemplateKeyword data;
    }
}
