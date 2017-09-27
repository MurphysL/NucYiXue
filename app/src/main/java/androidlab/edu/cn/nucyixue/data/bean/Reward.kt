package androidlab.edu.cn.nucyixue.data.bean

import androidlab.edu.cn.nucyixue.utils.config.LCConfig
import com.avos.avoscloud.AVClassName
import com.avos.avoscloud.AVObject

/**
 * 悬赏
 * Created by MurphySL on 2017/9/26.
 */
@AVClassName("Xuanshang")
class Reward : AVObject(){

    companion object {
        val CREATOR : AVObjectCreator = AVObjectCreator.instance
    }

    var des : String?
        get() = getString(LCConfig.REWARD_DES)
        set(value) = put(LCConfig.REWARD_DES, value)

    var tags : List<String>?
        get() = getList(LCConfig.REWARD_TAGS) as List<String>?
        set(value) = put(LCConfig.REWARD_TAGS, value)

    var money : String
        get() = getString(LCConfig.REWARD_MONEY)
        set(value) = put(LCConfig.REWARD_MONEY, value)

    var images : List<String>?
        get() = getList(LCConfig.REWARD_IMG) as List<String>?
        set(value) = put(LCConfig.REWARD_MONEY, value)

    var location : String?
        get() = getString(LCConfig.REWARD_LOC)
        set(value) = put(LCConfig.REWARD_LOC, value)

    var user : String
        get() = getAVObject<AVObject>(LCConfig.REWARD_USER).objectId
        set(value) = put(LCConfig.REWARD_USER, AVObject.createWithoutData(LCConfig.USER_TABLE, value))

    var username : String
        get() = getString(LCConfig.REWARD_USER_NAME)
        set(value) = put(LCConfig.REWARD_USER_NAME, value)

    var isTrue : Boolean?
        get() = getBoolean(LCConfig.REWARD_IS_TRUE)
        set(value) = put(LCConfig.REWARD_IS_TRUE, value)


    override fun toString(): String {
        return """
            {
              objectId : $objectId ,
              des : $des ,
              money : $money ,
              location  : $location,
              user : $user ,
              isTrue : $isTrue ,
              username : $username
            }
        """
    }


}