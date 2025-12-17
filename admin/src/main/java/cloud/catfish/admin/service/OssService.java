package cloud.catfish.admin.service;

import cloud.catfish.api.dto.OssCallbackResult;
import cloud.catfish.api.dto.OssPolicyResult;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Oss对象存储管理Service
 * Created by macro on 2018/5/17.
 */
public interface OssService {
    /**
     * Oss上传策略生成
     */
    OssPolicyResult policy();
    /**
     * Oss上传成功回调
     */
    OssCallbackResult callback(HttpServletRequest request);
}
