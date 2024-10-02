package com.example.mmominesweeper.repos;



import com.example.mmominesweeper.models.GameResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.UUID;

@Repository
public interface GameResultRepository extends JpaRepository<GameResult, UUID> {
    List<GameResult> findAllByOrderByTimestampDesc();
}
