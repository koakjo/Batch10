package com.batch.domain;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.batch.app.message.FurikomiExecMessage;
import com.batch.domain.entity.AsyncExec;
import com.batch.domain.entity.Idomeisai;
import com.batch.domain.entity.Yokin;
import com.batch.domain.repos.AsyncExecRepository;
import com.batch.domain.repos.IdomeisaiReposotory;
import com.batch.domain.repos.YokinRepository;

@Service
public class BatchService {

	@Autowired
	AsyncExecRepository asyncExecRepository; 
	@Autowired
	IdomeisaiReposotory idomeisaiRepository;
	@Autowired
	YokinRepository yokinRepository;
	@Autowired
	Yokin shimukeyokin;
	@Autowired
	Yokin hishimukeyokin;
	@Autowired
	Idomeisai idomeisai;

	/*
	 * 振込非同期実行
	 */
	@Transactional(isolation = Isolation.SERIALIZABLE)
	public boolean furikomiConsume (FurikomiExecMessage furikomiExecMessage){

		try {
			//タイムスタンプ取得
			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			
			//出金
			shimukeyokin = yokinRepository.getById(furikomiExecMessage.getShimukekouza());
			shimukeyokin.setZandaka(shimukeyokin.getZandaka() - furikomiExecMessage.getKingaku());
			yokinRepository.saveAndFlush(shimukeyokin);
			
			//入金
			hishimukeyokin = yokinRepository.getById(furikomiExecMessage.getHishimukekouza());
			hishimukeyokin.setZandaka(hishimukeyokin.getZandaka() + furikomiExecMessage.getKingaku());
			yokinRepository.saveAndFlush(hishimukeyokin);
			
			
	        //異動明細へインサート
			idomeisai = idomeisaiRepository.getById(furikomiExecMessage.getIdono());
			idomeisai.setExectime(format.format(calendar.getTime()));
			idomeisai.setStatus("DONE");
			idomeisaiRepository.saveAndFlush(idomeisai);
			
			return true;
		
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Transactional(isolation = Isolation.SERIALIZABLE)
	public boolean checkStatus2() {
		Optional<AsyncExec> oexec = asyncExecRepository.findById(com.batch.app.GlobalValueables.furikomiBatchNo);
		if (oexec.get().getStatus().equals("2")) {
			return true;
		} else {
			return false;
		}
	}
	
	@Transactional(isolation = Isolation.SERIALIZABLE)
	public boolean checkStatus1() {
		Optional<AsyncExec> oexec = asyncExecRepository.findById(com.batch.app.GlobalValueables.furikomiBatchNo);
		if (oexec.get().getStatus().equals("1")) {
			return true;
		} else {
			return false;
		}
	}
	
	@Transactional(isolation = Isolation.SERIALIZABLE)
	public boolean checkStatus0() {
		Optional<AsyncExec> oexec = asyncExecRepository.findById(com.batch.app.GlobalValueables.furikomiBatchNo);
		if (oexec.get().getStatus().equals("0")) {
			return true;
		} else {
			return false;
		}
	}
	
	@Transactional(isolation = Isolation.SERIALIZABLE)
	public boolean startProcess() {
		AsyncExec exec = new AsyncExec();
		exec.setId(com.batch.app.GlobalValueables.furikomiBatchNo);
		Optional<AsyncExec> oexec = asyncExecRepository.findById(com.batch.app.GlobalValueables.furikomiBatchNo);
		exec = oexec.get();
		exec.setStatus("1");
		asyncExecRepository.saveAndFlush(exec);
		return true;
	}
	
	@Transactional(isolation = Isolation.SERIALIZABLE)
	public boolean endProcess() {
		AsyncExec exec = new AsyncExec();
		exec.setId(com.batch.app.GlobalValueables.furikomiBatchNo);
		Optional<AsyncExec> oexec = asyncExecRepository.findById(com.batch.app.GlobalValueables.furikomiBatchNo);
		exec = oexec.get();
		exec.setStatus("0");
		asyncExecRepository.saveAndFlush(exec);
		return true;
	}
	
}
