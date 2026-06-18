package com.plataforma.Vapor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.plataforma.Vapor.model.SteamUser;

@Repository
public interface SteamUserRepository extends JpaRepository<SteamUser, Long> {

}
