package com.roomiematcher.match.controller;

import com.roomiematcher.common.dto.ApiResponse;
import com.roomiematcher.common.dto.match.MatchDTO;
import com.roomiematcher.match.service.MatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/matches")
@RequiredArgsConstructor
@Tag(name = "Match Management", description = "APIs for finding and managing roommate matches")
public class MatchController {

    private final MatchService matchService;

    @GetMapping("/potential/{userId}")
    @Operation(summary = "Find potential matches for a user")
    public ResponseEntity<ApiResponse<List<MatchDTO>>> findPotentialMatches(@PathVariable Long userId) {
        List<MatchDTO> matches = matchService.findPotentialMatches(userId);
        return ResponseEntity.ok(ApiResponse.success(matches));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all matches for a user")
    public ResponseEntity<ApiResponse<List<MatchDTO>>> getMatchesForUser(@PathVariable Long userId) {
        List<MatchDTO> matches = matchService.getMatchesForTenant(userId);
        return ResponseEntity.ok(ApiResponse.success(matches));
    }

    @GetMapping("/{matchId}")
    @Operation(summary = "Get a specific match by ID")
    public ResponseEntity<ApiResponse<MatchDTO>> getMatchById(@PathVariable Long matchId) {
        MatchDTO match = matchService.getMatchById(matchId);
        return ResponseEntity.ok(ApiResponse.success(match));
    }
} 