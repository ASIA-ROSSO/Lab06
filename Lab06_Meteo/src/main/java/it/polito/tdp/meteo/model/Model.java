package it.polito.tdp.meteo.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import it.polito.tdp.meteo.DAO.MeteoDAO;

public class Model {
	
	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;
	
	private List<Citta> citta;
	int bestCosto;
	private List<Rilevamento> bestSoluzione;
	private int costo;

	public int getBestCosto() {
		return bestCosto;
	}

	private MeteoDAO dao;
	public Model() {
		dao = new MeteoDAO();
	}

	public String getUmiditaMedia(int mese) {
		int m;
		if(mese>=1 || mese<=9) {
			m=Integer.parseInt(""+0+mese);
		}else {
			m=mese;
		}
		Set<Rilevamento> umiditaMedia = dao.getUmiditaMedia(m);
		String s="";
		for(Rilevamento r : umiditaMedia) {
			if(s.equals(""))
				s+= r.toStringUmiditaMedia();
			else
				s+= "\n" + r.toStringUmiditaMedia();
		}
		
		return s;
	}
	
	public String trovaSequenza(int mese) {
		bestCosto = 0;
		bestSoluzione = null;
		costo = 0;
		this.citta = dao.getTutteLeCitta(mese);
		/*
		for(Citta c : citta) {
			System.out.println(c.toString() + " " + c.getRilevamenti() + " stop");
		}*/
		
		List<Rilevamento> parziale = new LinkedList<Rilevamento>();
		
		cerca(parziale, 0);
		
		String s="";
		for(Rilevamento r : bestSoluzione) {
			if(s.equals(""))
				s+= r.toString();
			else
				s+= "\n" + r.toString();
		}
		
		return s;
	}

	private void cerca(List<Rilevamento> parziale, int L) {
		//casi terminali
		if(L == NUMERO_GIORNI_TOTALI) {
			System.out.println(parziale);
			int costo = calcolaCosto(parziale);
			if(costo>bestCosto) {
				bestSoluzione = new LinkedList<Rilevamento>(parziale);
				bestCosto = costo;
			}
			return;
		}			
				
		//1) generiamo i sotto-problemi
		for(Citta c : citta) {
			if(c.getCounter()<NUMERO_GIORNI_CITTA_MAX) {
				parziale.add(c.getRilevamenti().get(L));
				parziale.add(c.getRilevamenti().get(L+1));
				parziale.add(c.getRilevamenti().get(L+2));
				c.incrementaContatore();
				
				cerca(parziale, L+NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN);
				
				parziale.remove(c.getRilevamenti().get(L));
				parziale.remove(c.getRilevamenti().get(L+1));
				parziale.remove(c.getRilevamenti().get(L+2));
				c.decrementaContatore();
			}
		}				
	}
	
	private int calcolaCosto(List<Rilevamento> parziale) {
		int costo = 0;
		String Localita = null;
		
		for(Rilevamento r : parziale) {
			if(Localita==null) {
				Localita = r.getLocalita();
			}
			costo += r.getUmidita();
			if(!r.getLocalita().equals(Localita)) {
				costo+=COST;
			}
			Localita=r.getLocalita();
		}
		
		return costo;
	}
	

}
