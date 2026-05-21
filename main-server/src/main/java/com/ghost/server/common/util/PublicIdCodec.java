package com.ghost.server.common.util;

import java.util.Optional;

public final class PublicIdCodec {

    private PublicIdCodec() {
    }

    public static String encode(String prefix, Long id) {
        return prefix + id;
    }

    public static Optional<Long> decode(String prefix, String publicId) {
        if (publicId == null || !publicId.startsWith(prefix)) {
            return Optional.empty();
        }
        try {
            return Optional.of(Long.parseLong(publicId.substring(prefix.length())));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
