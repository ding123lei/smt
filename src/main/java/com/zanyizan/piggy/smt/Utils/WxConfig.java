package com.zanyizan.piggy.smt.Utils;

import java.util.Date;

/**
 * @author dinglei
 * @date 2018/07/17
 */
public class WxConfig {

    public static volatile String ACCESSTOKEN = "";
    public static volatile Date EXPIRE_DATE = new Date();
    //public static final String WX_OPENID_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=JSCODE&grant_type=authorization_code";
    public static final String WX_OPENID_URL = "https://api.weixin.qq.com/sns/jscode2session?appid=APPID&secret=SECRET&js_code=JSCODE&grant_type=authorization_code";
    public static final String WX_ACCESSTOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=SECRET";
    public static final String WX_USERINFO_URL = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID";
    public static final String WX_TEMPLATE_SEND_URL = "https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=ACCESS_TOKEN";

    public static final String APPID = "wxaef62d82e6ed6bb8";
    public static final String APPSECRECT = "4ff671b6e23f244da63c6ca0833a2824";
    public static final String GRANTTYPE = "authorization_code";
}
