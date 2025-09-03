package com.edp2021c1.randomseatgenerator.v2.util;

import com.edp2021c1.randomseatgenerator.RandomSeatGenerator;
import com.edp2021c1.randomseatgenerator.v2.util.exception.ExceptionHandler;
import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class I18N {

    private static final TypeToken<Map<String, String>> MAP_TYPE = new TypeToken<>() {
    };

    private static final Map<String, String> translations = Maps.newHashMap();

    private static final Map<String, String> fallback = Maps.newHashMap();

    public static final String LANG_PATH = "assets/lang/%s.json";

    public static final String CONSTANT_KEY = "randomseatgenerator.constant.";

    private static String code = "zh_cn";

    public static void init(@NotNull String code) {
        I18N.code = code.toLowerCase();

        String json;

        try {
            json = FileUtils.readResource(LANG_PATH.formatted(I18N.code));
        } catch (Exception e) {
            ExceptionHandler.INSTANCE.handleException(e);
            return;
        }
        translations.putAll(RandomSeatGenerator.GSON.fromJson(json, MAP_TYPE));

        try {
            json = FileUtils.readResource(LANG_PATH.formatted("en_us"));
        } catch (Exception e) {
            ExceptionHandler.INSTANCE.handleException(e);
            return;
        }
        fallback.putAll(RandomSeatGenerator.GSON.fromJson(json, MAP_TYPE));

    }

    public static String tr(@NotNull String key, Object... args) {
        return translations.getOrDefault(key, key).formatted(args);
    }

    public static String getTranslation(@NotNull String key) {
        return translations.get(key);
    }

    public static String constant(String name) {
        return tr(CONSTANT_KEY + name);
    }

}
