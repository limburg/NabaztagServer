package nl.topicus.konijn.xmpp.util;

import nl.topicus.konijn.WicketApplication;

import org.apache.log4j.Logger;
import org.apache.vysper.xmpp.server.SessionContext;
import org.apache.wicket.ThreadContext;

public class WicketInjector {
	private static final Logger LOGGER = Logger.getLogger(WicketInjector.class);

	public static WicketApplication inject(SessionContext sessionContext) {
		WicketApplication app = null;

		if (ThreadContext.getApplication() == null) {

			MyServerRuntimeContext my = (MyServerRuntimeContext) sessionContext
					.getServerRuntimeContext();
			app = (WicketApplication) my.getApplication();
			ThreadContext.setApplication(app);
			LOGGER.info("Injected Wicket into Vysper");
		} else {
			app = (WicketApplication) ThreadContext.getApplication();
		}
		return app;
	}
}
