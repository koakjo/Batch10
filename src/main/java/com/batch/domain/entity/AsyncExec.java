package com.batch.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.stereotype.Component;

import lombok.Data;

@Entity
@Table(name = "kafkaexec")
@Data
@Component
public class AsyncExec {
	
	@Id
	@Column(name = "ID", nullable = false)
	private long id;
	
	@Column(name = "STATUS", nullable = false)
	private String status;

}
