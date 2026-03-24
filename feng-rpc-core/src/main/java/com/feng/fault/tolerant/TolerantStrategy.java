package com.feng.fault.tolerant;

import com.feng.model.RpcResponse;

import java.util.Map;

/**
 * 容错策略
 */
public interface TolerantStrategy {
    /**
     * 容错
     * @param context 上下文传递数据
     * @param e 异常信息
     * @return
     */
    RpcResponse doTolerant(Map<String, Object> context, Exception e);
}
