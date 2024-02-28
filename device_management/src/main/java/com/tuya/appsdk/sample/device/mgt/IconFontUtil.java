package com.tuya.appsdk.sample.device.mgt;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;

/**
 * create by dongdaqing[mibo] 2023/9/19 17:31
 */
public class IconFontUtil {
    private static final String DEFAULT_ICON_FONT_MAP = "{\n" +
            "    \"icon-dp_add\":\"e600\",\n" +
            "    \"icon-dp_reduce\":\"e601\",\n" +
            "    \"icon-dp_temp\":\"e602\",\n" +
            "    \"icon-dp_time3\":\"e603\",\n" +
            "    \"icon-dp_mode\":\"e604\",\n" +
            "    \"icon-dp_loop\":\"e605\",\n" +
            "    \"icon-dp_filter\":\"e606\",\n" +
            "    \"icon-dp_smile\":\"e607\",\n" +
            "    \"icon-dp_sleep\":\"e608\",\n" +
            "    \"icon-dp_dust\":\"e609\",\n" +
            "    \"icon-eco\":\"e60a\",\n" +
            "    \"icon-eoc2\":\"e60b\",\n" +
            "    \"icon-dp_tvoc\":\"e60c\",\n" +
            "    \"icon-dp_smart\":\"e60d\",\n" +
            "    \"icon-dp_o2\":\"e60e\",\n" +
            "    \"icon-dp_time2\":\"e60f\",\n" +
            "    \"icon-dp_light2\":\"e610\",\n" +
            "    \"icon-dp_shake\":\"e611\",\n" +
            "    \"icon-dp_home2\":\"e612\",\n" +
            "    \"icon-dp_direction\":\"e613\",\n" +
            "    \"icon-dp_power3\":\"e614\",\n" +
            "    \"icon-dp_wash\":\"e615\",\n" +
            "    \"icon-dp_location\":\"e616\",\n" +
            "    \"icon-dp_battery\":\"e617\",\n" +
            "    \"icon-dp_wind\":\"e618\",\n" +
            "    \"icon-dp_fresh\":\"e619\",\n" +
            "    \"icon-dp_wet2\":\"e61a\",\n" +
            "    \"icon-dp_half\":\"e61b\",\n" +
            "    \"icon-dp_sun\":\"e61c\",\n" +
            "    \"icon-dp_bag\":\"e61d\",\n" +
            "    \"icon-dp_wet\":\"e61e\",\n" +
            "    \"icon-dp_water\":\"e61f\",\n" +
            "    \"icon-dp_lock\":\"e620\",\n" +
            "    \"icon-dp_time\":\"e621\",\n" +
            "    \"icon-dp_light\":\"e622\",\n" +
            "    \"icon-dp_0\":\"e623\",\n" +
            "    \"icon-dp_1\":\"e624\",\n" +
            "    \"icon-dp_2\":\"e625\",\n" +
            "    \"icon-dp_3\":\"e626\",\n" +
            "    \"icon-dp_4\":\"e627\",\n" +
            "    \"icon-dp_5\":\"e628\",\n" +
            "    \"icon-dp_6\":\"e629\",\n" +
            "    \"icon-dp_7\":\"e62a\",\n" +
            "    \"icon-dp_8\":\"e62b\",\n" +
            "    \"icon-dp_9\":\"e62c\",\n" +
            "    \"icon-dp_c\":\"e62d\",\n" +
            "    \"icon-dp_f\":\"e62e\",\n" +
            "    \"icon-dp_power2\":\"e62f\",\n" +
            "    \"icon-dp_power\":\"e630\",\n" +
            "    \"icon-dp_right\":\"e631\",\n" +
            "    \"icon-dp_dot\":\"e632\",\n" +
            "    \"icon-dp_play\":\"e633\",\n" +
            "    \"icon-dp_pause\":\"e634\",\n" +
            "    \"icon-dp_down\":\"e635\",\n" +
            "    \"icon-dp_anti-clockwise\":\"e636\",\n" +
            "    \"icon-dp_clockwise\":\"e637\",\n" +
            "    \"icon-dp_up\":\"e638\",\n" +
            "    \"icon-dp_lightning\":\"e63a\",\n" +
            "    \"icon-dp_voice\":\"e639\",\n" +
            "    \"icon-dp_down1\":\"e63b\",\n" +
            "    \"icon-dp_cloud\":\"e63c\",\n" +
            "    \"icon-dp_upload\":\"e63d\",\n" +
            "    \"icon-dp_doc\":\"e63e\",\n" +
            "    \"icon-dp_curve\":\"e63f\",\n" +
            "    \"icon-dp_heart\":\"e640\",\n" +
            "    \"icon-dp_email\":\"e641\",\n" +
            "    \"icon-dp_circle\":\"e642\",\n" +
            "    \"icon-dp_plus\":\"e643\",\n" +
            "    \"icon-dp_home\":\"e644\",\n" +
            "    \"icon-dp_magnifier\":\"e645\",\n" +
            "    \"icon-dp_fly\":\"e646\",\n" +
            "    \"icon-dp_i\":\"e647\",\n" +
            "    \"icon-dp_down2\":\"e648\",\n" +
            "    \"icon-dp_book\":\"e649\",\n" +
            "    \"icon-dp_rabbish\":\"e64a\",\n" +
            "    \"icon-dp_hill\":\"e64b\",\n" +
            "    \"icon-dp_compass\":\"e64c\",\n" +
            "    \"icon-dp_gift\":\"e64d\",\n" +
            "    \"icon-dp_eye\":\"e64e\",\n" +
            "    \"icon-dp_notice\":\"e64f\",\n" +
            "    \"icon-dp_camera\":\"e650\",\n" +
            "    \"icon-dp_puzzle\":\"e651\",\n" +
            "    \"icon-dp_ratio\":\"e652\",\n" +
            "    \"icon-dp_block\":\"e653\",\n" +
            "    \"icon-dp_chat\":\"e654\",\n" +
            "    \"icon-dp_list2\":\"e655\",\n" +
            "    \"icon-dp_bottle\":\"e656\",\n" +
            "    \"icon-dp_doc2\":\"e657\",\n" +
            "    \"icon-dp_what\":\"e658\",\n" +
            "    \"icon-dp_warming\":\"e659\",\n" +
            "    \"icon-dp_updown\":\"e65a\",\n" +
            "    \"icon-dp_tool\":\"e65b\",\n" +
            "    \"icon-dp_tag\":\"e65c\",\n" +
            "    \"icon-dp_shield\":\"e65d\",\n" +
            "    \"icon-dp_box2\":\"e65e\",\n" +
            "    \"icon-dp_box\":\"e65f\",\n" +
            "    \"icon-dp_money\":\"e660\",\n" +
            "    \"icon-dp_house\":\"e661\",\n" +
            "    \"icon-dp_mic\":\"e662\",\n" +
            "    \"icon-dp_calendar\":\"e663\",\n" +
            "    \"icon-dp_list\":\"e664\",\n" +
            "    \"icon-dp_flag\":\"e665\",\n" +
            "    \"icon-dp_flower\":\"e666\",\n" +
            "    \"icon-baifeng\":\"e8c1\",\n" +
            "    \"icon-chushi\":\"e8c2\",\n" +
            "    \"icon-chengzhong\":\"e8c3\",\n" +
            "    \"icon-chongnai\":\"e8c4\",\n" +
            "    \"icon-chushi1\":\"e8c5\",\n" +
            "    \"icon-chushi2\":\"e8c6\",\n" +
            "    \"icon-cuowu\":\"e8c7\",\n" +
            "    \"icon-deng\":\"e8c8\",\n" +
            "    \"icon-dianliang\":\"e8c9\",\n" +
            "    \"icon-fengli\":\"e8ca\",\n" +
            "    \"icon-geren\":\"e8cb\",\n" +
            "    \"icon-gaodiyin\":\"e8cc\",\n" +
            "    \"icon-gongneng\":\"e8cd\",\n" +
            "    \"icon-guanjia\":\"e8ce\",\n" +
            "    \"icon-huoyan\":\"e8cf\",\n" +
            "    \"icon-qita\":\"e8d0\",\n" +
            "    \"icon-jiare\":\"e8d1\",\n" +
            "    \"icon-liangdu\":\"e8d2\",\n" +
            "    \"icon-jiare1\":\"e8d3\",\n" +
            "    \"icon-shangsheng\":\"e8d4\",\n" +
            "    \"icon-shouji\":\"e8d5\",\n" +
            "    \"icon-shoucang\":\"e8d6\",\n" +
            "    \"icon-shezhi\":\"e8d7\",\n" +
            "    \"icon-qiangli\":\"e8d8\",\n" +
            "    \"icon-tianjia\":\"e8d9\",\n" +
            "    \"icon-shoushimima\":\"e8da\",\n" +
            "    \"icon-shenghua\":\"e8db\",\n" +
            "    \"icon-shuibeng\":\"e8dc\",\n" +
            "    \"icon-tongji\":\"e8dd\",\n" +
            "    \"icon-tongji1\":\"e8de\",\n" +
            "    \"icon-yinshui\":\"e8df\",\n" +
            "    \"icon-yinliang\":\"e8e0\",\n" +
            "    \"icon-yanse\":\"e8e1\",\n" +
            "    \"icon-wendu\":\"e8e2\",\n" +
            "    \"icon-yundong\":\"e8e3\",\n" +
            "    \"icon-yunhang\":\"e8e4\",\n" +
            "    \"icon-zanting\":\"e8e5\",\n" +
            "    \"icon-zhengque\":\"e8e6\",\n" +
            "    \"icon-zhuangtai\":\"e8e7\",\n" +
            "    \"icon-zhileng\":\"e8e8\",\n" +
            "    \"icon-zhileng1\":\"e8e9\",\n" +
            "    \"icon-chushuang\":\"e8ea\",\n" +
            "    \"icon-zanting1\":\"e8eb\",\n" +
            "    \"icon-tongji2\":\"e8ec\",\n" +
            "    \"icon-baifeng1\":\"e8ed\",\n" +
            "    \"icon-set\":\"e931\",\n" +
            "    \"icon-yueliang\":\"e932\",\n" +
            "    \"icon-xue\":\"e933\",\n" +
            "    \"icon-fangzi\":\"e934\",\n" +
            "    \"icon-wendu1\":\"e935\",\n" +
            "    \"icon-taiyang\":\"e936\",\n" +
            "    \"icon-fangzi1\":\"e937\",\n" +
            "    \"icon-icon-percent\":\"e938\",\n" +
            "    \"icon-p6\":\"e9aa\",\n" +
            "    \"icon-p7\":\"e9ab\",\n" +
            "    \"icon-p8\":\"e9ac\",\n" +
            "    \"icon-p9\":\"e9ad\",\n" +
            "    \"icon-p10\":\"e9ae\",\n" +
            "    \"icon-p11\":\"e9af\",\n" +
            "    \"icon-p12\":\"e9b0\",\n" +
            "    \"icon-p13\":\"e9b1\",\n" +
            "    \"icon-p14\":\"e9b2\",\n" +
            "    \"icon-p15\":\"e9b3\",\n" +
            "    \"icon-ziyouchengxu\":\"e9b4\",\n" +
            "    \"icon-zhouchengxu\":\"e9b5\",\n" +
            "    \"icon-minus\":\"e9ba\",\n" +
            "    \"icon-plus\":\"e9bb\",\n" +
            "    \"icon-a_fan_low\":\"e9bd\",\n" +
            "    \"icon-a_fan_auto\":\"e9be\",\n" +
            "    \"icon-a_fan_med\":\"e9bf\",\n" +
            "    \"icon-a_fan_high\":\"e9c0\",\n" +
            "    \"icon-a_function_celsius\":\"e9c1\",\n" +
            "    \"icon-a_function_fahrenhei\":\"e9c2\",\n" +
            "    \"icon-a_function_hs\":\"e9c3\",\n" +
            "    \"icon-a_function_eco\":\"e9c4\",\n" +
            "    \"icon-a_function_filter\":\"e9c5\",\n" +
            "    \"icon-a_function_sleep\":\"e9c6\",\n" +
            "    \"icon-a_function_pump\":\"e9c7\",\n" +
            "    \"icon-a_function_vs\":\"e9c8\",\n" +
            "    \"icon-a_function_turbo\":\"e9c9\",\n" +
            "    \"icon-a_mode_basement\":\"e9ca\",\n" +
            "    \"icon-a_mode_continuous\":\"e9cb\",\n" +
            "    \"icon-a_mode_cool\":\"e9cc\",\n" +
            "    \"icon-a_mode_fan\":\"e9cd\",\n" +
            "    \"icon-a_mode_clothes\":\"e9ce\",\n" +
            "    \"icon-a_mode_feel\":\"e9cf\",\n" +
            "    \"icon-a_mode_heat\":\"e9d0\",\n" +
            "    \"icon-a_mode_livingroom\":\"e9d1\",\n" +
            "    \"icon-a_mode_dry\":\"e9d2\",\n" +
            "    \"icon-a_nav_fan\":\"e9d3\",\n" +
            "    \"icon-a_nav_function\":\"e9d4\",\n" +
            "    \"icon-a_nav_mode\":\"e9d5\",\n" +
            "    \"icon-a_power\":\"e9d6\",\n" +
            "    \"icon-a_mode_turbo\":\"e9d7\",\n" +
            "    \"icon-a_nav_timer\":\"e9d8\",\n" +
            "    \"icon-a_down\":\"e9d9\",\n" +
            "    \"icon-a_up\":\"e9da\",\n" +
            "    \"icon-a_water\":\"e9db\",\n" +
            "    \"icon-a_selected\":\"e9dc\",\n" +
            "    \"icon-Mute\":\"e9dd\",\n" +
            "    \"icon-FanSpeed\":\"e9de\",\n" +
            "    \"icon-Lamp\":\"e9df\",\n" +
            "    \"icon-Heal\":\"e9e0\",\n" +
            "    \"icon-Ele\":\"e9e1\",\n" +
            "    \"icon-Strong\":\"e9e2\",\n" +
            "    \"icon-dp_bag1\":\"e9e3\",\n" +
            "    \"icon-off\":\"e9e4\",\n" +
            "    \"icon-edit\":\"e9e5\",\n" +
            "    \"icon-on\":\"e9e6\",\n" +
            "    \"icon-timer\":\"e9e7\",\n" +
            "    \"icon-power\":\"e9e8\",\n" +
            "    \"icon-timer1\":\"e9ea\",\n" +
            "    \"icon-Disarm\":\"e9e9\",\n" +
            "    \"icon-SystemReady\":\"e9eb\",\n" +
            "    \"icon-Arm\":\"e9ec\",\n" +
            "    \"icon-HomeArm\":\"e9ed\",\n" +
            "    \"icon-AwayArm\":\"e9ee\",\n" +
            "    \"icon-power1\":\"e9ef\",\n" +
            "    \"icon-Panic\":\"e9f0\",\n" +
            "    \"icon-battery\":\"e9f1\",\n" +
            "    \"icon-setting\":\"e9f2\",\n" +
            "    \"icon-Trigger\":\"e9f3\",\n" +
            "    \"icon-CMS\":\"e9f4\",\n" +
            "    \"icon-tcl_function_eco\":\"ea17\",\n" +
            "    \"icon-tcl_function_vs\":\"ea18\",\n" +
            "    \"icon-tcl_mode_shoes\":\"ea19\",\n" +
            "    \"icon-tcl_function_hs\":\"ea1a\",\n" +
            "    \"icon-tcl_function_vs1\":\"ea1b\",\n" +
            "    \"icon-tcl_function_vs2\":\"ea1c\",\n" +
            "    \"icon-tcl_function_light\":\"ea1d\",\n" +
            "    \"icon-tcl_function_vs3\":\"ea1e\",\n" +
            "    \"icon-function_eh\":\"ea22\",\n" +
            "    \"icon-air_quality\":\"ea23\",\n" +
            "    \"icon-sound\":\"ea24\",\n" +
            "    \"icon-icon-test6\":\"e819\",\n" +
            "    \"icon-icon-test7\":\"e81a\",\n" +
            "    \"icon-icon-test8\":\"e81c\",\n" +
            "    \"icon-icon-test9\":\"e81d\",\n" +
            "    \"icon-icon-test10\":\"e81e\",\n" +
            "    \"icon-gongnuan\":\"e820\",\n" +
            "    \"icon-lengnuan\":\"e821\",\n" +
            "    \"icon-icon-test11\":\"eaa8\",\n" +
            "    \"icon-zidong\":\"eaa9\",\n" +
            "    \"icon-icon-test12\":\"eaaa\",\n" +
            "    \"icon-icon-test13\":\"eaab\",\n" +
            "    \"icon-xiegang\":\"ea96\",\n" +
            "    \"icon-a_selected-copy\":\"eaac\",\n" +
            "    \"icon-dp_play-copy\":\"eaad\",\n" +
            "    \"icon-jiaquan\":\"eb05\",\n" +
            "    \"icon-baojing\":\"eb06\",\n" +
            "    \"icon-menci\":\"eb07\",\n" +
            "    \"icon-shidu\":\"eb08\",\n" +
            "    \"icon-ranqi\":\"eb09\",\n" +
            "    \"icon-renxingyidong\":\"eb0a\",\n" +
            "    \"icon-zhendong\":\"eb0b\",\n" +
            "    \"icon-wendu2\":\"eb0c\",\n" +
            "    \"icon-yanwu\":\"eb0d\",\n" +
            "    \"icon-liangdu1\":\"eb0e\",\n" +
            "    \"icon-pm\":\"eb0f\",\n" +
            "    \"icon-voc\":\"eb10\",\n" +
            "    \"icon-CH\":\"eb28\",\n" +
            "    \"icon-gongneng1\":\"eb29\",\n" +
            "    \"icon-CO\":\"eb2a\",\n" +
            "    \"icon-yali\":\"eb2b\",\n" +
            "    \"icon-Light\":\"eb2c\",\n" +
            "    \"icon-CO1\":\"eb2d\",\n" +
            "    \"icon-chanpin\":\"e87b\"\n" +
            "}";

    private static JSONObject sIconFont;

    public static String getIconFontContent(String name) {
        if (sIconFont == null) {
            sIconFont = JSONObject.parseObject(DEFAULT_ICON_FONT_MAP);
        }

        if (sIconFont == null || name == null) {
            return null;
        } else {
            String content = sIconFont.getString(name);
            if (TextUtils.isEmpty(content)) {
                return null;
            } else {
                String real = convert(content);
                if (TextUtils.isEmpty(real)) {
                    return null;
                } else {
                    return real;
                }
            }
        }
    }

    private static String convert(String str) {
        String value = str.toLowerCase();

        boolean fail = false;
        long hex = 0;
        for (int i = 0; i < value.length(); i++) {
            int curV = getValueOfX(value.charAt(i));
            if (curV == -1) {
                fail = true;
                break;
            } else {
                hex += curV * Math.pow(16, value.length() - i - 1);
            }
        }

        if (fail) {
            return "";
        } else {
            return "&#" + String.valueOf(hex);
        }
    }

    private static int getValueOfX(char c) {
        char[] codes = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        int v = -1;
        for (int i = 0; i < codes.length; i++) {
            if (codes[i] == c) {
                v = i;
                break;
            }
        }

        return v;
    }
}
