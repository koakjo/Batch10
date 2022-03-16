package com.batch.domain.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.batch.domain.entity.Yokin;

@Repository
public interface YokinRepository extends JpaRepository<Yokin,String>{

}
