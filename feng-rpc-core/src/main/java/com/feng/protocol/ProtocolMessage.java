package com.feng.protocol;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProtocolMessage<T> {
    /**
     * 消息头
     */
    private Header header;
    /**
     * 消息体(请求或响应对象)
     */
    private T body;
    /**
     * 请求协议头
     */
    @Data
    public static class Header {
        /**
         * 魔数
         */
        private byte magic;
        /**
         * 版本号
         */
        private byte version;
        /**
         * 序列化方式
         */
        private byte serializer;
        /**
         * 状态
         */
        private byte status;
        /**
         * 类型
         */
        private byte type;
        /**
         * 请求id
         */
        private Long requestId;
        /**
         * 消息体长度
         */
        private int bodyLength;
    }

}
