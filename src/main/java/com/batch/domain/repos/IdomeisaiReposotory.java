package com.batch.domain.repos;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.batch.domain.entity.Idomeisai;

@Repository
public interface IdomeisaiReposotory extends JpaRepository<Idomeisai,UUID>{
	//カスタムクエリ。すごいですよね。
	List<Idomeisai> findByShimukekouza(String shimukekouza);
}
