import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.hamcrest.core.AllOf;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class ServerMain extends Thread {

	List<ClientWorker> clients;
	ServerSocket server;
	int port = 9090;
	static DBConnection connection;
	static int id;
	String zona;

	public ServerMain(DBConnection con, int port, String zona) {
		clients = new ArrayList<ClientWorker>();
		this.port = port;
		connection = con;
		this.zona = zona;

	}

	public void run() {
		try {
			ServerSocket server = new ServerSocket(port);
			System.out.println("Start server");

			while (!server.isClosed()) {
				try {
					Socket socket = server.accept();
					connection.addClient(socket);
					ClientWorker clientWorker = new ClientWorker(this, socket, connection, ++id);
					new Thread(clientWorker).start();
					clients.add(clientWorker);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}

	}

	public void removeClient(ClientWorker client) {
		clients.remove(client);
	}

	public boolean allowClients() {
		return clients.size() < 5;
	}

	public int getPort() {
		return port;
	}

	public static void main(String[] args) {
		try {
			int port = 9090;
			List<ServerMain> servers = new ArrayList<ServerMain>();
			DBConnection conection = new DBConnection();
			ServerSocket server = new ServerSocket(port);
			System.out.println("Start server");
			id = 0;
			String str[] = { "A", "B", "C", "D" };
			for (int i = 0; i < 4; i++) {
				port++;
				ServerMain srv = new ServerMain(conection, port, str[i]);
				srv.start();
				servers.add(srv);
			}

			while (!server.isClosed()) {
				try {
					Socket socket = server.accept();
					PrintWriter pw = new PrintWriter(socket.getOutputStream());
					InputStreamReader dis = new InputStreamReader(socket.getInputStream());
					BufferedReader br = new BufferedReader(dis);
					String s = br.readLine();
					int p = 0;
					if (s.equalsIgnoreCase("A")) {
						ServerMain srv = servers.get(0);
						System.out.println("Iti dau server din zona A!");
						p = srv.getPort();
						pw.write("port: " + p);
						pw.write("\n");
						pw.flush();
					} else if (s.equalsIgnoreCase("B")) {
						ServerMain srv = servers.get(1);
						System.out.println("Iti dau server din zona B!");
						p = srv.getPort();
						pw.write("port: " + p);
						pw.write("\n");
						pw.flush();
					} else if (s.equalsIgnoreCase("C")) {
						ServerMain srv = servers.get(2);
						System.out.println("Iti dau server din zona C!");
						p = srv.getPort();
						pw.write("port: " + p);
						pw.write("\n");
						pw.flush();
					}
					if (s.equalsIgnoreCase("D")) {
						ServerMain srv = servers.get(3);
						System.out.println("Iti dau server din zona D!");
						p = srv.getPort();
						pw.write("port: " + p);
						pw.write("\n");
						pw.flush();
					}

					socket.close();

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}

	}

}

//Manager de servere
//Se instantiaza 4 servere coresp a 4 zone A, B , C , D
//Cand se conecteaza se conecteaza cu zona din care provine
//Manegerul ii va returna adresa IP a serverului coresp zonei respective
//Clientul specifica zona
