package com.webapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.webapp.domain.Appuser;
@Repository
public interface AppuserRepository extends JpaRepository<Appuser,Long>{
	Appuser findUserByUsername(String username);
	Appuser findUserByEmail(String email);
}
