package nl.topicus.konijn.web.entrypoints;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

/**
 * Entrypoint for sending the bootcode.
 * 
 * @author Joost Limburg
 * 
 */
public class BcEntryPoint extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(BcEntryPoint.class);
	private static final String CHARSET = "ISO-8859-1";

	public void doGet(HttpServletRequest inRequest,
			HttpServletResponse inResponse) throws IOException {
		final PrintWriter out = inResponse.getWriter();
		final String theSerial = inRequest.getParameter("m");
		final String theFirmware = inRequest.getParameter("v");

		try {
			if (theSerial != null && theFirmware != null) {
				LOGGER.info("Request for boot image: Serial[" + theSerial
						+ "], Version[" + theFirmware + "]");
				out.print(BcEntryPoint.process(theSerial));
			} else {
				LOGGER.info("Invalid request for boot image");
				out.print("");
				inResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
			}
		} catch (final InterruptedException e) {
			BcEntryPoint.LOGGER.fatal(e, e);
			out.print("");
			inResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		out.close();
	}

	private static String process(String inSerial) throws InterruptedException {
		InputStream in = BcEntryPoint.class
				.getResourceAsStream("bc-nominal-h4.bin");
		byte[] array = null;

		try {
			array = IOUtils.toByteArray(in);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			IOUtils.closeQuietly(in);
		}

		return getStringFromBytes(array, CHARSET);
	}

	public static String getStringFromBytes(byte[] inBytes, String inCharset) {
		try {
			return (inBytes != null) ? new String(inBytes,
					(inCharset != null) ? inCharset : CHARSET) : "";
		} catch (final UnsupportedEncodingException e) {
			LOGGER.fatal(e, e);
		}

		return "";
	}
}
