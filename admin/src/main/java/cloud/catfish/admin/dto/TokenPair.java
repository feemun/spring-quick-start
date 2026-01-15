package cloud.catfish.admin.dto;

public record TokenPair(
        String accessToken,
        String refreshToken
) {
}

