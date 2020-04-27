package it.polito.tdp.meteo.DAO;

import java.sql.Connection;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import it.polito.tdp.meteo.model.Citta;
import it.polito.tdp.meteo.model.Rilevamento;

public class MeteoDAO {
	
	Set<Rilevamento> umiditaMedia;
	
	public List<Rilevamento> getAllRilevamenti() {
		
		final String sql = "SELECT Localita, Data, Umidita FROM situazione ORDER BY data ASC";

		List<Rilevamento> rilevamenti = new ArrayList<Rilevamento>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet rs = st.executeQuery();

			while (rs.next()) {

				Rilevamento r = new Rilevamento(rs.getString("Localita"), rs.getDate("Data"), rs.getInt("Umidita"));
				rilevamenti.add(r);
			}

			conn.close();
			return rilevamenti;

		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public Set<Rilevamento> getUmiditaMedia (int mese){
		
		final String sql = "SELECT Localita, AVG(Umidita) FROM situazione WHERE MONTH(Data)=? GROUP BY Localita";
		umiditaMedia = new HashSet<Rilevamento>();
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);

			st.setInt(1, mese);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Rilevamento r = new Rilevamento(rs.getString("Localita"),rs.getDouble("AVG(Umidita)"));
				umiditaMedia.add(r);
			}

			conn.close();
			return umiditaMedia;

		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
	}
	
public int getUmiditaMedia (int mese, String localita){
		
		final String sql = "SELECT AVG(Umidita) FROM situazione WHERE MONTH(Data)=? and Localita=?";
		int umiditaMedia1 = 0;
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			
			
			st.setInt(1, mese);
			st.setString(2, "Localita");
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				umiditaMedia1 = rs.getInt("AVG(Umidita)");
			}

			conn.close();
			return umiditaMedia1;

		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
	}
	
	
	public List<Rilevamento> getAllRilevamentiLocalitaMese(int mese, String localita) {
		final String sql = "SELECT Localita, DATA, Umidita FROM situazione WHERE MONTH(DATA)=? AND DAY(DATA) <=15 AND Localita=? order BY Localita, DATA ";
		List<Rilevamento> rilevamenti = new ArrayList<Rilevamento>();
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			
			st.setInt(1, mese);
			st.setString(2, localita);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				rilevamenti.add(new Rilevamento(rs.getString("Localita"),rs.getDate("Data"), rs.getInt("Umidita")));
			}
			int count = 1;
			/*if(rilevamenti.size()!=15) {
				for(Rilevamento r : rilevamenti) {
					if(r.getData().getDay()!=count-1) {
						rilevamenti.add(count-1,new Rilevamento(localita, r.setData(2013-mese), getUmiditaMedia(mese, localita)));
					}
					count++;
				}
			}*/
			conn.close();
			return rilevamenti;

		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public List<Citta> getTutteLeCitta(int mese) {
		final String sql = "SELECT Localita FROM situazione GROUP BY Localita";
		List<Citta> citta = new ArrayList<Citta>();
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Citta c = new Citta(rs.getString("Localita"));
				citta.add(c);
				c.setRilevamenti(getAllRilevamentiLocalitaMese(mese, c.getNome()));
			
			}

			conn.close();
			return citta;

		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}



}
