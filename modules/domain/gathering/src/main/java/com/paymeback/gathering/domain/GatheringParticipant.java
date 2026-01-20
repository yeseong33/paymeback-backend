package com.paymeback.gathering.domain;

import java.time.Instant;

public record GatheringParticipant(
    Long id,
    Long gatheringId,
    Long userId,
    Instant joinedAt
) {

    public static GatheringParticipant of(Long gatheringId, Long userId) {
        return new GatheringParticipant(null, gatheringId, userId, null);
    }
}
