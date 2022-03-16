package com.batch.domain.repos;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.batch.domain.entity.AsyncExec;

@Repository
public interface AsyncExecRepository extends JpaRepository<AsyncExec,Long>{
	
}
