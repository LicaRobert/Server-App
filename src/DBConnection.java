import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Vector;

import org.json.simple.JSONObject;

public class DBConnection 
{
	private Connection conn = null;
	private Vector<Socket> clienti;

	public DBConnection() 
	{
		clienti = new Vector<Socket>();
		try {
			Class.forName("org.mariadb.jdbc.Driver").newInstance();
			// conn = DriverManager.getConnection("jdbc:mariadb://localhost:3308/angajati?user=proelite&password=proelite");
			conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/elev", "root", "ProElite");
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}
	
	public synchronized void addClient(Socket s)
	{
		clienti.add(s);
	}
	
	public synchronized void removeClient(Socket s)
	{
		clienti.remove(s);
	}
	
	public synchronized List<Elev> getElevi() {
		if (conn == null)
			return null;
		try {
			List<Elev> elevi = new ArrayList<Elev>();
			String sql = "SELECT id, nume, prenume, varsta FROM elev";
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				int id = rs.getInt("id");
				int age = rs.getInt("varsta");
				String name = rs.getString("nume");
				String prenume = rs.getString("prenume");
				Elev angajat = new Elev();
				angajat.setId(id);
				angajat.setVarsta(age);
				angajat.setNume(name);
				angajat.setPrenume(prenume);
				elevi.add(angajat);
			}
			
			rs.close();
			stmt.close();
			return elevi;
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
		return null;

	}

	private void sendInformtoClients(int idClient)
	{
		int i = 1;
		for(Socket clientSocket: clienti)
		{
			if(i != idClient)
			{
				PrintWriter pw;
				try {
					pw = new PrintWriter(clientSocket.getOutputStream());
					JSONObject obj = new JSONObject();
					String mesaj = "Baza de date a fost updatata de clientul " + idClient;
					obj.put("action", mesaj);
					JSONObject resp = new JSONObject();
					resp.put("response", obj);
					String msg = resp.toJSONString();
					pw.println(msg);
					pw.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			i++;
		}
	}
	
	public synchronized boolean updateElev(Elev elev, int idClient) {
		
		boolean returnValue = false;
		if (conn == null)
			return false;
		PreparedStatement stmt = null;
		try {
			String sql ="UPDATE elev SET nume = ?, prenume = ?, varsta = ? WHERE id = ?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, elev.getNume());
			stmt.setString(2, elev.getPrenume());
			stmt.setInt(3, elev.getVarsta());
			stmt.setInt(4, elev.getId());
			returnValue = 1 <= stmt.executeUpdate();
			if(returnValue)
			{
				sendInformtoClients(idClient);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// finally block used to close resources
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException se) {
			} // do nothing
		}
		return returnValue;
	}
	
	public synchronized boolean insertElev(Elev elev, int idClient) {
		boolean returnValue = false;
		
		if (conn == null)
		{
			return false;
		}
		PreparedStatement stmt = null;
		try {
			String sql ="INSERT INTO elev VALUES (?, ?, ?, ?)";
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, elev.getId());
			stmt.setString(2, elev.getNume());
			stmt.setString(3, elev.getPrenume());
			stmt.setInt(4, elev.getVarsta());
			returnValue = (1 == stmt.executeUpdate());
			if(returnValue)
			{
				sendInformtoClients(idClient);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// finally block used to close resources
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException se) {
			} // do nothing
		}
		return returnValue;
	}
	
	public synchronized int deleteElev(Elev elev, int idClient) {
		int returnValue = 0;
		if (conn == null)
			return 0;
		PreparedStatement stmt = null;
		try {
			String sql ="DELETE FROM elev WHERE id = ?";
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, elev.getId());
			returnValue = stmt.executeUpdate();
			if(returnValue > 0 )
			{
				sendInformtoClients(idClient);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// finally block used to close resources
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException se) {
			} // do nothing
		}
		return returnValue;
	}
	
	public synchronized void closeConnection() {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			conn = null;
		}
	}
}
