package nl.topicus.konijn.web.entrypoints;

import java.io.IOException;
import java.io.PrintWriter;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.topicus.konijn.WicketApplication;
import nl.topicus.konijn.config.modules.Setting;

import org.apache.log4j.Logger;
import org.hibernate.cfg.Settings;

import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * Entrypoint for sending the location of the servers.
 * 
 * @author Joost Limburg
 * 
 */
public class LocateEntryPoint extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger
			.getLogger(LocateEntryPoint.class);

	private Injector injector = null;

	public LocateEntryPoint() {
		InitialContext context = null;
		try {
			context = new InitialContext();
		} catch (NamingException e) {
			e.printStackTrace();
		}
		try {
			injector = (Injector) context.lookup("com.google.inject.Injector");
		} catch (NamingException e) {
			e.printStackTrace();
		}

	}

	private String getConfig() {
		// String address = WicketApplication.getServerAddress();
		Setting setting = injector.getInstance(Setting.class);
		String address = setting.getHost(); 

		return "ping " + address + "\nbroad " + address + "\nxmpp_domain "
				+ address + "\n";
	}

	public void doGet(HttpServletRequest inRequest,
			HttpServletResponse inResponse) throws IOException {
		final PrintWriter out = inResponse.getWriter();
		final String theSerial = inRequest.getParameter("sn");
		final String theHardware = inRequest.getParameter("h");
		final String theFirmware = inRequest.getParameter("v");

		Setting setting = injector.getInstance(Setting.class);
		String address = setting.getHost(); 
		
		if (theSerial != null && theFirmware != null && theHardware != null) {
			LOGGER.info("Locate request: SN[" + theSerial + "], V["
					+ theFirmware + "], H[" + theHardware + "] to: "
					+ address);
			out.print(getConfig());
		} else {
			LOGGER.info("Invalid locate request");
			out.print("");
			inResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
		}

		out.close();
	}
}
