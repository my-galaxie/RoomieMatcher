package com.roomiematcher.repository;

import com.roomiematcher.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    // Custom queries can be defined here
}
