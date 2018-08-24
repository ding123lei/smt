package com.zanyizan.piggy.smt.service;

import com.zanyizan.piggy.smt.common.enums.TaskStatus;
import com.zanyizan.piggy.smt.tables.pojos.ZyzUserTask;
import com.zanyizan.piggy.smt.tables.records.ZyzTaskRecord;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dinglei
 * @date 2018/07/25
 */
@Service
public class ImageService {

    public static final int NORMAL = 1;
    public static final int SAD = 2;

    private static final String IMAGE_URL = "https://zyz-image.oss-cn-beijing.aliyuncs.com/background_image/%s.png";
    private static final String BACKGROUND_IMAGE_URL = "https://zyz-image.oss-cn-beijing.aliyuncs.com/background_image/background/BACKGROUND_%s.png";
    private static final String SHARE_IMAGE_URL = "https://zyz-image.oss-cn-beijing.aliyuncs.com/background_image/share/%s.png";
    private static final String SUMMARY_IMAGE_URL = "https://zyz-image.oss-cn-beijing.aliyuncs.com/background_image/summary/%s.png";

    public static final Map<Integer, String> summaryMap = new HashMap<>();

    static{
        summaryMap.put(1, "ZYZ55");
        summaryMap.put(2, "ZYZ50");
        summaryMap.put(3, "ZYZ33");
        summaryMap.put(4, "ZYZ30");
        summaryMap.put(5, "ZYZQT");
    }

    public static String getBackgroundImage(int days){
        return String.format(BACKGROUND_IMAGE_URL, days);
    }

    public static String getSubjectImage(ZyzTaskRecord record, String userId, ZyzUserTask friend, int days){
        String friendUserId = null;
        if(friend == null){
            friendUserId = "0";
        } else {
            friendUserId = friend.getUserId();
        }
        String imageName = buildImageName(record, userId, friendUserId, days);
        return String.format(IMAGE_URL, imageName);
    }

    public static String getShareImage(ZyzTaskRecord record, String userId, ZyzUserTask friend, int days){
        String friendUserId = null;
        if(friend == null){
            friendUserId = "0";
        } else {
            friendUserId = friend.getUserId();
        }
        String imageName = buildImageName(record, userId, friendUserId, days);
        return String.format(SHARE_IMAGE_URL, imageName);
    }

    public static String getSummaryImage(int value){
        return String.format(SUMMARY_IMAGE_URL, value);
    }

    private static String buildImageName(ZyzTaskRecord record, String userId, String friendUserId, int days){
        int userNumber = userId.hashCode() >= friendUserId.hashCode() ? NORMAL : SAD;
        int statusNumber = TaskStatus.SLEEP.ordinal() != record.getStatus() ? NORMAL : SAD;
        return days + "_" + userNumber + "_" + statusNumber;
    }

    public static String getDefaultBackgroundImage(){
        return String.format(BACKGROUND_IMAGE_URL, "1");
    }

    public static String getDefaultSubjectImage(){
        return String.format(IMAGE_URL, "1_1_1");
    }

    public static String getDefaultShareImage(){
        return String.format(SHARE_IMAGE_URL, "1_1_1");
    }
}
