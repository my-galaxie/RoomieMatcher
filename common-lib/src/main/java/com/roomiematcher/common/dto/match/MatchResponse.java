package com.roomiematcher.common.dto.match;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchResponse {
    private String id;
    private String tenant1Id;
    private String tenant1Name;
    private String tenant2Id;
    private String tenant2Name;
    private Double matchScore;
    private LocalDateTime createdAt;
} 