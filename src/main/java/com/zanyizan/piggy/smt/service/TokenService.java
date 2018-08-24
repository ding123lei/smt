package com.zanyizan.piggy.smt.service;

import com.zanyizan.piggy.smt.Utils.WxConfig;
import com.zanyizan.piggy.smt.Utils.WxUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

/**
 * @author dinglei
 * @date 2018/07/26
 */
@Service
public class TokenService implements InitializingBean{

    @Override
    public void afterPropertiesSet() throws Exception {
        WxUtils.getAccesstoken();
    }

    public String getAccesstoken(){
        synchronized (this){
            if(WxConfig.EXPIRE_DATE.getTime() <= System.currentTimeMillis()){
                WxUtils.getAccesstoken();
            }
        }
        return WxConfig.ACCESSTOKEN;
    }
}
