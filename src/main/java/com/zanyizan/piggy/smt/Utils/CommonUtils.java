package com.zanyizan.piggy.smt.Utils;

import com.zanyizan.piggy.smt.common.enums.MessageType;
import com.zanyizan.piggy.smt.service.UserService;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * @author dinglei
 * @date 2018/07/18
 */
@Data
@Component
public class CommonUtils {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat dataTime_sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static Date parseDateTimeToDate(Date nowDate){
        if(nowDate == null){
            nowDate = new Date();
        }
        String time = sdf.format(nowDate);
        try {
            return sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return nowDate;
    }

    public static String parseDateToString(Date nowDate){
        if(nowDate == null){
            nowDate = new Date();
        }
        return dataTime_sdf.format(nowDate);
    }

    public static String buildMessage(MessageType type){
        String msg = "";
        switch (type) {
            case SAVING_MONEY:
                msg = "已经存钱，看看TA说了什么";
                break;
            case GLANCE:
                msg = "TA来偷看了你好几次，去看看TA";
                break;
        }
        return msg;
    }

    public static int twoDaysDiffer(Date date1, Date date2){
        long DAY = 24L * 60L * 60L * 1000L;
        date1 = parseDateTimeToDate(date1);
        date2 = parseDateTimeToDate(date2);
        return Long.valueOf((date2.getTime() - date1.getTime()) / DAY).intValue();
    }

    public static void main(String[] args) throws ParseException {
        Date date1 = sdf.parse("2018-08-05");
        int diff = CommonUtils.twoDaysDiffer(date1, null);
        System.out.println(diff);
    }

}
