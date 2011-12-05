package nl.topicus.konijn;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.webapp.WebAppContext;

/**
 * Start the server within eclipse/from cmd with this class
 * @author limburg
 *
 */
public class Start {

	public static void main(String[] args) throws Exception {
		// Create Jetty Server
		Server server = new Server();
		SocketConnector connector = new SocketConnector();

		// This should be forwarded to port 80, otherwise bunny won't be able to connect.
		connector.setPort(8080);
		server.setConnectors(new Connector[] { connector });

		WebAppContext bb = new WebAppContext();

		bb.setServer(server);
		bb.setContextPath("/");
		bb.setWar("src/main/webapp");
		server.addHandler(bb);

		// Start Jetty server
		try {
			System.out
					.println(">>> STARTING EMBEDDED JETTY SERVER, PRESS ANY KEY TO STOP");
			server.start();
			while (System.in.available() == 0) {
				Thread.sleep(5000);
			}

			System.out.println(">>> Stopping HTTP Server");
			server.stop();
			server.join();

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(100);
		}
	}
}
