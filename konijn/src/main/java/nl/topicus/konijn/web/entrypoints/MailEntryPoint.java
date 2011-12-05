package nl.topicus.konijn.web.entrypoints;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * Entrypoint mail.
 * 
 * @author Joost Limburg
 * 
 */
public class MailEntryPoint extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(MailEntryPoint.class);

	public void doGet(HttpServletRequest inRequest,
			HttpServletResponse inResponse) throws IOException {
		LOGGER.warn("MailError received:");

		@SuppressWarnings("unchecked")
		Map<String, String[]> req = inRequest.getParameterMap();
		Iterator<String> iterator = req.keySet().iterator();

		while (iterator.hasNext()) {
			String key = iterator.next().toString();
			String value[] = (String[]) req.get(key);

			LOGGER.warn("	" + key + " " + Arrays.toString(value));
		}

	}
}
