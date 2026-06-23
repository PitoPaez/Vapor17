package com.plataforma.Vapor.repository;

import com.plataforma.Vapor.model.SteamGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SteamGameRepository extends JpaRepository<SteamGame, Long> {
}