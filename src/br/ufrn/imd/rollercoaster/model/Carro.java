package br.ufrn.imd.rollercoaster.model;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Semaphore;

import br.ufrn.imd.rollercoaster.Mensagens;
import br.ufrn.imd.rollercoaster.util.Notes;

public class Carro extends Thread{

	private MontanhaRussa montanhaRussaREF;
	private List<Passageiro> passageiros;
	private int capacidade;
	private Semaphore semaphore;
	private Semaphore entrando;
	private int qtdPasseios;
	private int qtdPasseiosLimite;
	private boolean ligado;

	public Carro(int capacidade, MontanhaRussa montanhaRussaREF, int qtdPasseiosLimite) {
		this.setCapacidade(capacidade);
		this.passageiros = new ArrayList<Passageiro>();
		this.montanhaRussaREF = montanhaRussaREF;
		this.qtdPasseiosLimite = qtdPasseiosLimite;
		this.qtdPasseios = 0;
		semaphore = new Semaphore(capacidade);
		entrando = new Semaphore(1);
		fecharCarro(); //Esperar load para o primeiro passsageiro entrar no carro
	}
	
	public void run() {
		load(); //Passageiros podem entrar
		while ( isLigado()) { //enquanto limite de passeios ok
			if (carroCheio()) { //Se carro tiver lotado
				iniciarPasseio(); //Passeia pela trilha ... conluido passeio
				unload(); //Passageiros podem sair
				load(); //novos passageiros podem entrar
			}
		}
	}
	
	public void load() {
		if (isLigado()){
			Notes.print(this, "load.");
		}
		semaphore.release(capacidade);
	}

	public void unload() {
		Notes.print(this, "unload.");

		Iterator<Passageiro> itPassageiros = passageiros.iterator();
		while (itPassageiros.hasNext()) {
			itPassageiros.next().unboard();
			itPassageiros.remove();
		}
	}
	
	public void iniciarPasseio(){
		someQtdPasseios();
		Passeio passeio = new Passeio(montanhaRussaREF.getTrilha());
		passeio.run();
		Notes.print(this, Mensagens.CARRO_QTD_PASSEIO, qtdPasseios, qtdPasseiosLimite);
	}
	
	public void someQtdPasseios() {
		qtdPasseios+=1;
		
		if(qtdPasseios == qtdPasseiosLimite){
			setLigado(false);
			Notes.print(this, Mensagens.CARRO_LIMITE);
			liberarFila();
		}
	}
	
	public boolean carroCheio(){
		//Todos as pessoas est�o no carro, nem entrando nem esperando para entrar.
		return semaphore.availablePermits() == 0 && !entrando.hasQueuedThreads() && entrando.availablePermits() != 0;
	}

	public boolean embarcar(Passageiro passageiro) {
		try {
			if (semaphore.availablePermits() == 0){
				Notes.print(this, Mensagens.CARRO_FILA_ESPERA,(semaphore.getQueueLength() + 1));
			}
			
			semaphore.acquire();
			
			if(isLigado()){

				entrando.acquire();
				
				passageiros.add(passageiro);
				Notes.print(this, Mensagens.CARRO_PASSAGEIRO_EMBARCOU, passageiro.toString());
				Notes.print(this, Mensagens.CARRO_LOTACAO, passageiros.size(), capacidade, passageiros.toString());
				
				entrando.release();
				
			}else{
				Notes.print(this, Mensagens.CARRO_NO_EMBARCOU, passageiro.toString());
				return false;
			}
			
		} catch (InterruptedException | ConcurrentModificationException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		return true;
	}
	
	public void fecharCarro(){
		try {
			semaphore.acquire(capacidade);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public void liberarFila(){
		semaphore.release(semaphore.getQueueLength());
	}

	public boolean contemPassageiro(Passageiro passageiro) {
		return passageiros.contains(passageiro);
	}

	public int getCapacidade() {
		return capacidade;
	}

	public void setCapacidade(int capacidade) {
		this.capacidade = capacidade;
	}

	public int getQtdPassageiros() {
		return passageiros.size();
	}

	public boolean isLigado() {
		return ligado;
	}

	public void setLigado(boolean ligado) {
		this.ligado = ligado;
	}
	
	public int getQtdPasseios() {
		return qtdPasseios;
	}

	public int getQtdPasseiosLimite() {
		return qtdPasseiosLimite;
	}
	public void setQtdPasseiosLimite(int qtdPasseiosLimite) {
		this.qtdPasseiosLimite = qtdPasseiosLimite;
	}

}
