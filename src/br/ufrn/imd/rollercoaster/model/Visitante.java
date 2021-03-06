package br.ufrn.imd.rollercoaster.model;
import java.util.concurrent.Semaphore;

import br.ufrn.imd.rollercoaster.Mensagens;
import br.ufrn.imd.rollercoaster.util.Notes;
import br.ufrn.imd.rollercoaster.util.RandInt;

public abstract class Visitante extends Thread{

	private final static int MAX_SEG_PASSEIO_PARQUE = 10;
	private final static int MIN_SEG_PASSEIO_PARQUE = 4;
	
	private RandInt randInt;
	private int id;
	private Semaphore parado;
	private ParqueDiversoes parqueDiversoesREF;
	private int tempoPasseandoNoParque;
	
	public Visitante(int id, ParqueDiversoes parqueDiversoesREF) {
		this.parqueDiversoesREF = parqueDiversoesREF;
		this.id = id;
		this.parado = new Semaphore(0);
		this.randInt = new RandInt(MIN_SEG_PASSEIO_PARQUE, MAX_SEG_PASSEIO_PARQUE);
		this.parado = new Semaphore(0);
		this.setTempoPasseandoNoParque(0);
	}

	public void run() {
		Notes.print(this, Mensagens.VISITANTE_CHEGOU, toString());
		while (condicaoPermanencia()) {
			action();
		}
		Notes.print(this, Mensagens.VISITANTE_SAIU, toString());
	}
	
	
	
	public void parar(){
		try {
			parado.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public void continuar(){
		parado.release();
	}
	
	public abstract void action();
	
	public abstract boolean condicaoPermanencia();

	protected void passearNoParque() {
		try {
			int tempo_seg = randInt.rand();
			Notes.print(this, Mensagens.VISITANTE_PASSEANDO, toString(), tempo_seg);
			Thread.sleep(tempo_seg * 1000);
			Notes.print(this, Mensagens.VISITANTE_PASSEIO_CONCLUIDO, toString());
			setTempoPasseandoNoParque(getTempoPasseandoNoParque() + tempo_seg);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public void gerarRelatorio(){
		Notes.print(this, Mensagens.VISITANTE_RELATORIO, toString(), tempoPasseandoNoParque);
	}


	public int getID() {
		return id;
	}
	
	public void setID(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return  " ["+id+"] ";
	}
	
	public RandInt getRandInt() {
		return randInt;
	}
	
	public void setRandInt(RandInt randInt) {
		this.randInt = randInt;
	}


	public ParqueDiversoes getParqueDiversoesREF() {
		return parqueDiversoesREF;
	}


	public void setParqueDiversoesREF(ParqueDiversoes parqueDiversoesREF) {
		this.parqueDiversoesREF = parqueDiversoesREF;
	}


	public Semaphore getParado() {
		return parado;
	}


	public void setParado(Semaphore parado) {
		this.parado = parado;
	}

	public int getTempoPasseandoNoParque() {
		return tempoPasseandoNoParque;
	}

	public void setTempoPasseandoNoParque(int tempoPasseandoNoParque) {
		this.tempoPasseandoNoParque = tempoPasseandoNoParque;
	}


}
