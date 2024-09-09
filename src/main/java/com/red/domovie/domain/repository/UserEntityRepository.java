package com.red.domovie.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.red.domovie.domain.entity.UserEntity;

public interface UserEntityRepository extends JpaRepository<UserEntity, Long>{

	Optional<UserEntity> findByEmail(String email);


	//Optional<UserEntity> findByEmailOrSocialId(String email, String socialId);


}
