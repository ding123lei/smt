package com.zanyizan.piggy.smt.entity;

import com.zanyizan.piggy.smt.tables.records.ZyzUserRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotNull;

/**
 * @author dinglei
 * @date 2018/07/18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WxUserInfoEntity {

    @NotNull
    private String userId;
    private String userName;
    private Integer gender;
    private String avatar;
    private String language;
    private String country;
    private String province;
    private String city;
    private String remark;

    public boolean check(){
        if(StringUtils.isEmpty(userId)){
            return false;
        }
        if(gender != null){
            if(gender < 0 || gender > 2){
                return false;
            }
        }
        return true;
    }
}
