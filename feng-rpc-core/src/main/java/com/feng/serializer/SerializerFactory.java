package com.feng.serializer;

import com.feng.spi.SpiLoader;

import java.util.HashMap;
import java.util.Map;

/**
 * 序列化器工厂
 */
public class SerializerFactory {

//
//    static {
//        SpiLoader.load(Serializer.class);
//    }
//
//    /**
//     * 默认序列化器
//     */
//    private static final Serializer DEFAULT_SERIALIZER = new JdkSerializer();
//
//    /**
//     * 获取序列化器实例
//     */
//    public static Serializer getInstance(String key) {
//        return SpiLoader.getInstance(Serializer.class, key);
//    }
//

    /**
     * 序列化映射
     */
    private static final Map<String, Serializer> KEY_SERIALIZER_MAP = new HashMap<String, Serializer>(){{
        put("json", new JsonSerializer());
        put("kryo", new KryoSerializer());
        put("hessian", new HessianSerializer());
        put("jdk",new JdkSerializer());
    }};

    /**
     * 默认序列化器
     */
    private static final Serializer DEFAULT_SERIALIZER = KEY_SERIALIZER_MAP.get("jdk");
    public static Serializer getInstance(String key){
        return KEY_SERIALIZER_MAP.getOrDefault(key, DEFAULT_SERIALIZER);
    }
}
