package com.batch.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.stereotype.Component;

import lombok.Data;

@Entity
@Table(name = "yokin")
@Data
@Component
public class Yokin {
	
	@Id
	@Column(name = "kouzabangou", nullable = false)
	private String kouzabangou;

	
	@Column(name = "zandaka", nullable = false)
	private long zandaka;
	
	@Column(name = "cifno", nullable = false)
	private String cifno;
	
}
