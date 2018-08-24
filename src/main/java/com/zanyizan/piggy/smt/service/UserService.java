package com.zanyizan.piggy.smt.service;

import com.zanyizan.piggy.smt.Utils.SnowFlake;
import com.zanyizan.piggy.smt.common.enums.UserSource;
import com.zanyizan.piggy.smt.dao.UserFormIdMapper;
import com.zanyizan.piggy.smt.dao.UserMapper;
import com.zanyizan.piggy.smt.entity.UserFormIdEntity;
import com.zanyizan.piggy.smt.entity.WxUserInfoEntity;
import com.zanyizan.piggy.smt.entity.response.UserVo;
import com.zanyizan.piggy.smt.tables.records.ZyzTaskFlowLogRecord;
import com.zanyizan.piggy.smt.tables.records.ZyzUserRecord;
import lombok.Data;
import org.jooq.DSLContext;
import org.jooq.UpdateSetFirstStep;
import org.jooq.UpdateSetMoreStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static com.zanyizan.piggy.smt.Tables.ZYZ_USER;

/**
 * @author dinglei
 * @date 2018/07/17
 */
@Service
@Data
public class UserService {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private UserFormIdMapper userFormIdMapper;

    @Autowired
    private UserMapper userMapper;

    private static final Random random = new Random(5);

    @Autowired
    SnowFlake snowFlake;

    private final static String DEFAULT_USER_PREFIX = "u_";
    public final static String DEFAULT_USERNAME_PREFIX = "zyz_";

    private final static String DEFAULT_AVATAR_MALE = "https://zyz-image.oss-cn-beijing.aliyuncs.com/background_image/avatar/avatar_1.png";
    private final static String DEFAULT_AVATAR_FEMALE = "https://zyz-image.oss-cn-beijing.aliyuncs.com/background_image/avatar/avatar_2.png";

    public Optional<UserVo> login(String openId){
        ZyzUserRecord userRecord = dslContext.selectFrom(ZYZ_USER).where(ZYZ_USER.THIRD_ID.eq(openId)).fetchOne();
        UserVo vo = null;
        if (userRecord != null){
            vo = UserVo.builder().
                    userId(userRecord.getUserId()).
                    avatar(getAvatar(userRecord)).
                    userName(userRecord.getUserName()).
                    build();
        } else {
            /*Optional<WxUserInfoEntity> optional = WxUtils.getUserInfo(openId, accessToken);
            if (optional.isPresent()) {
                userRecord = new ZyzUserRecord();
                userRecord.setUserId(DEFAULT_USER_PREFIX + snowFlake.nextId());
                userRecord.setThirdId(openId);
                userRecord.setAvatar(optional.get().getAvatar());
                userRecord.setUserName(optional.get().getUserName());
                userRecord.setGender((byte)1);
                userRecord.setSource(UserSource.XCX.ordinal());
                dslContext.insertInto(ZYZ_USER).set(userRecord).execute();
                vo = UserVo.builder().
                        userId(userRecord.getUserId()).
                        avatar(userRecord.getAvatar()).
                        userName(userRecord.getUserName()).
                        isNew(true).
                        build();
            } else {
                return Optional.empty();
            }*/
            userRecord = new ZyzUserRecord();
            String userId = DEFAULT_USER_PREFIX + snowFlake.nextId();
            userRecord.setUserId(userId);
            userRecord.setThirdId(openId);
            //userRecord.setUserName(DEFAULT_USERNAME_PREFIX + getRandomUserId());
            userRecord.setGender((byte)1);
            userRecord.setSource(UserSource.XCX.ordinal());
            dslContext.insertInto(ZYZ_USER).set(userRecord).execute();
            vo = UserVo.builder().
                    userId(userRecord.getUserId()).
                    //avatar(getAvatar(userRecord)).
                    //userName(userRecord.getUserName()).
                    isNew(true).
                    build();
        }
        return Optional.of(vo);
    }

    public Optional<ZyzUserRecord> getUser(String userId){
        ZyzUserRecord record = dslContext.selectFrom(ZYZ_USER).where(ZYZ_USER.USER_ID.eq(userId)).fetchOne();
        if(record == null){
            return Optional.empty();
        }
        return Optional.of(record);
    }

    public void update(WxUserInfoEntity entity){
        Optional<ZyzUserRecord> optional = getUser(entity.getUserId());
        if(!optional.isPresent()){
            return;
        }
        if(!StringUtils.isEmpty(entity.getAvatar()) || !StringUtils.isEmpty(entity.getUserName()) || entity.getGender() != null){
            UpdateSetFirstStep<ZyzUserRecord> firstStep = dslContext.update(ZYZ_USER);
            UpdateSetMoreStep<ZyzUserRecord> moreStep = null;
            if(!StringUtils.isEmpty(entity.getAvatar())){
                moreStep = firstStep.set(ZYZ_USER.AVATAR, entity.getAvatar());
            }
            if(!StringUtils.isEmpty(entity.getUserName())){
                if(moreStep == null){
                    firstStep.set(ZYZ_USER.USER_NAME, entity.getUserName());
                }else{
                    moreStep.set(ZYZ_USER.USER_NAME, entity.getUserName());
                }
            }
            if(entity.getGender() != null){
                if(moreStep == null){
                    firstStep.set(ZYZ_USER.GENDER, entity.getGender().byteValue());
                }else{
                    moreStep.set(ZYZ_USER.GENDER, entity.getGender().byteValue());
                }
            }
            if(moreStep != null){
                moreStep.where(ZYZ_USER.USER_ID.eq(entity.getUserId())).execute();
            }
        }
    }

    public static String getAvatar(ZyzUserRecord record){
        /*if(record == null){
            return DEFAULT_AVATAR_MALE;
        }
        if(StringUtils.isEmpty(record.getAvatar())){
            if(record.getGender() == null || record.getGender() == 1){
                return DEFAULT_AVATAR_MALE;
            }
            return DEFAULT_AVATAR_FEMALE;
        }*/
        //no need return default avatar
        if(record == null){
            return null;
        }
        return record.getAvatar();
    }

    public void upsertFromId(String userId, String formId){
        userFormIdMapper.upsertFormId(userId, formId);
    }

    public String getUserFormId(String userId){
        return userFormIdMapper.getUserFormId(userId);
    }

    public UserFormIdEntity getUserFormIdEntityByFriendUserId(String taskId, String userId){
        return userMapper.getUserFormIdEntityByFriendUserId(taskId, userId);
    }

    public List<UserFormIdEntity> getNotSavingMoneyUsers(){
        return userMapper.getNotSavingMoneyUsers();
    }

}
