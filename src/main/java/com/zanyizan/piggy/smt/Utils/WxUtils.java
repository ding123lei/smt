package com.zanyizan.piggy.smt.Utils;

import com.alibaba.fastjson.JSONObject;
import com.zanyizan.piggy.smt.entity.WxUserInfoEntity;
import com.zanyizan.piggy.smt.service.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

public class WxUtils {

    private static Logger log = LoggerFactory.getLogger(WxUtils.class);

    public static Optional<String> getOpenId(String jsCode){
        String requestUrl = WxConfig.WX_OPENID_URL.replace("APPID", WxConfig.APPID).replace("SECRET", WxConfig.APPSECRECT)
                .replace("JSCODE", jsCode).replace("authorization_code", WxConfig.GRANTTYPE);
        JSONObject jsonObject = WxUtils.httpsRequest(requestUrl, "GET", null);
        if (jsonObject != null) {
            try {
                //判断获取token是否正确
                String openId = jsonObject.getString("openid");
                //String sessionKey = jsonObject.getString("session_key");
                if(!StringUtils.isEmpty(openId)){
                    //WxAccesstokenEntity entity = WxAccesstokenEntity.builder().openId(openId).sessionKey(sessionKey).build();
                    return Optional.of(openId);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return Optional.empty();
    }

    public static void getAccesstoken(){
        String requestUrl = WxConfig.WX_ACCESSTOKEN_URL.replace("APPID", WxConfig.APPID).replace("SECRET", WxConfig.APPSECRECT);
        JSONObject accessToken = WxUtils.httpsRequest(requestUrl, "GET", null);
        if(!StringUtils.isEmpty(accessToken.get("access_token"))){
            int expire = accessToken.getInteger("expires_in");
            WxConfig.ACCESSTOKEN = accessToken.getString("access_token");
            WxConfig.EXPIRE_DATE = getExpireDate(expire);
        }
    }

    public static Optional<WxUserInfoEntity> getUserInfo(String openId, String accesstoken){
        String requestUrl = WxConfig.WX_USERINFO_URL.replace("ACCESS_TOKEN", accesstoken).replace("OPENID", openId);
        JSONObject userInfoJO = WxUtils.httpsRequest(requestUrl, "GET", null);
        if (userInfoJO != null) {
            try {
                WxUserInfoEntity entity = WxUserInfoEntity.builder().
                        userName(userInfoJO.getString("nickname")).
                        avatar(userInfoJO.getString("headimgurl")).
                        gender(userInfoJO.getInteger("sex")).
                        country(userInfoJO.getString("country")).
                        province(userInfoJO.getString("province")).
                        city(userInfoJO.getString("city")).
                        country(userInfoJO.getString("country")).
                        remark(userInfoJO.getString("remark")).
                        build();
                return Optional.of(entity);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return Optional.empty();
    }

    /**
     * 发送https请求
     * @param requestUrl 请求地址
     * @param requestMethod 请求方式（GET、POST）
     * @param outputStr 提交的数据
     * @return JSONObject(通过JSONObject.get(key)的方式获取json对象的属性值)
     */
    public static JSONObject httpsRequest(String requestUrl, String requestMethod, String outputStr) {
        JSONObject jsonObject = null;
        try {
            // 创建SSLContext对象，并使用我们指定的信任管理器初始化
            TrustManager[] tm = { new MyX509TrustManager() };
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, tm, new java.security.SecureRandom());
            // 从上述SSLContext对象中得到SSLSocketFactory对象
            SSLSocketFactory ssf = sslContext.getSocketFactory();

            URL url = new URL(requestUrl);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setSSLSocketFactory(ssf);

            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            // 设置请求方式（GET/POST）
            conn.setRequestMethod(requestMethod);

            // 当outputStr不为null时向输出流写数据
            if (null != outputStr) {
                OutputStream outputStream = conn.getOutputStream();
                // 注意编码格式
                outputStream.write(outputStr.getBytes("UTF-8"));
                outputStream.close();
            }

            // 从输入流读取返回内容
            InputStream inputStream = conn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            StringBuffer buffer = new StringBuffer();
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }

            // 释放资源
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            inputStream = null;
            conn.disconnect();
            jsonObject = JSONObject.parseObject(buffer.toString());
        } catch (ConnectException ce) {
            log.error("连接超时：{}", ce);
        } catch (Exception e) {
            log.error("https请求异常：{}", e);
        }
        return jsonObject;
    }

    private static Date getExpireDate(int expire){
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, expire);
        return calendar.getTime();
    }
}