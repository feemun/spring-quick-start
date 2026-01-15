package cloud.catfish.api.security;

import cloud.catfish.api.domain.UmsResource;
import cn.hutool.core.util.StrUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class UmsResourceScope {
    private UmsResourceScope() {
    }

    public static String fromResource(UmsResource resource) {
        if (resource == null) {
            return null;
        }
        String url = resource.getUrl();
        if (StrUtil.isNotEmpty(url)) {
            return fromUrl(url);
        }
        return fromName(resource.getName());
    }

    public static String fromUrl(String url) {
        if (StrUtil.isEmpty(url)) {
            return null;
        }
        String normalized = url.trim();
        int queryIndex = normalized.indexOf('?');
        if (queryIndex >= 0) {
            normalized = normalized.substring(0, queryIndex);
        }
        int fragmentIndex = normalized.indexOf('#');
        if (fragmentIndex >= 0) {
            normalized = normalized.substring(0, fragmentIndex);
        }
        normalized = normalized.replace('\\', '/');
        while (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }
        while (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        if (StrUtil.isEmpty(normalized)) {
            return null;
        }
        String[] segments = normalized.split("/");
        List<String> parts = new ArrayList<>(segments.length);
        for (String segment : segments) {
            String part = normalizePart(segment);
            if (StrUtil.isNotEmpty(part)) {
                parts.add(part);
            }
        }
        if (parts.isEmpty()) {
            return null;
        }
        return String.join(".", parts);
    }

    private static String fromName(String name) {
        if (StrUtil.isEmpty(name)) {
            return null;
        }
        return normalizePart(name);
    }

    private static String normalizePart(String raw) {
        if (raw == null) {
            return null;
        }
        String part = raw.trim();
        if (part.startsWith("{") && part.endsWith("}") && part.length() > 2) {
            part = part.substring(1, part.length() - 1);
        }
        part = part.toLowerCase(Locale.ROOT);
        part = part.replaceAll("[^a-z0-9]+", "_");
        part = part.replaceAll("^_+|_+$", "");
        return StrUtil.isEmpty(part) ? null : part;
    }
}

