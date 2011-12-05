package nl.topicus.konijn;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import nl.topicus.konijn.data.dao.hibernate.NabaztagDao;
import nl.topicus.konijn.security.AuthenticatedSession;
import nl.topicus.konijn.security.UserRolesAuthorizer;
import nl.topicus.konijn.web.HomePage;
import nl.topicus.konijn.web.pages.CreateInit;
import nl.topicus.konijn.web.pages.home.RegisterPage;
import nl.topicus.konijn.web.pages.home.UserHomePage;
import nl.topicus.konijn.web.pages.security.LogoutPage;
import nl.topicus.konijn.xmpp.XMPPServerFactory;
import nl.topicus.konijn.xmpp.util.VysperDelegator;

import org.apache.log4j.Logger;
import org.apache.wicket.Application;
import org.apache.wicket.Page;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authroles.authorization.strategies.role.RoleAuthorizationStrategy;
import org.apache.wicket.guice.GuiceComponentInjector;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.settings.IRequestLoggerSettings;
import org.apache.wicket.util.time.Duration;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * Bunny wicket application
 * 
 * @author Joost Limburg
 * 
 */
public class WicketApplication extends AuthenticatedWebApplication {
	static final Logger LOGGER = Logger.getLogger(WicketApplication.class);

	private Injector injector = null;

	private Scheduler scheduler;

	private GuiceComponentInjector gInjector;

	private XMPPServerFactory xmppFactory;

	@Inject
	private NabaztagDao nabDao;

	public VysperDelegator getDelegatorInstance() {
		return new VysperDelegator(nabDao);
	}

	/**
	 * Constructor
	 */
	public WicketApplication() {
		// xmppFactory = Guice.createInjector().getInstance(
		// XMPPServerFactory.class);
	}

	public XMPPServerFactory getXmppFactory() {
		return xmppFactory;
	}

	protected GuiceComponentInjector getGuiceInjector() {
		return gInjector;
	}

	@Override
	protected void init() {
		super.init();

		RuntimeConfigurationType type = this.getConfigurationType();

		// Get the logger
		if (type.equals(RuntimeConfigurationType.DEVELOPMENT)) {
			LOGGER.info("You are in DEVELOPMENT mode");
			getResourceSettings().setResourcePollFrequency(Duration.ONE_SECOND);
			getDebugSettings().setComponentUseCheck(true);
			getMarkupSettings().setStripWicketTags(false);
		} else if (type.equals(RuntimeConfigurationType.DEPLOYMENT)) {
			LOGGER.info("You are in DEPLOYMENT mode");
			getResourceSettings().setResourcePollFrequency(null);
			getDebugSettings().setComponentUseCheck(false);
			getMarkupSettings().setStripWicketTags(true);
		}

		IRequestLoggerSettings reqLogger = Application.get()
				.getRequestLoggerSettings();
		Application.get().getMarkupSettings().setStripWicketTags(true);

		// Security
		getSecuritySettings().setAuthorizationStrategy(
				new RoleAuthorizationStrategy(new UserRolesAuthorizer()));

		// Enable the logger
		reqLogger.setRequestLoggerEnabled(true);
		// Injector injector = Guice.createInjector(new Module());

		// setup the properties...
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

		gInjector = new GuiceComponentInjector(this, injector);
		getComponentInstantiationListeners().add(gInjector);

		xmppFactory = injector.getInstance(XMPPServerFactory.class);
		xmppFactory.start(gInjector);

		gInjector.inject(this);

		mountPage("init", CreateInit.class);
		mountPage("logout", LogoutPage.class);
		mountPage("home", UserHomePage.class);
		mountPage("main", HomePage.class);
		mountPage("register", RegisterPage.class);

		// Start scheduler
		try {
			// Grab the Scheduler instance from the Factory
			Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

			// and start it off
			scheduler.start();
		} catch (SchedulerException se) {
			se.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	@Override
	public Class<? extends Page> getHomePage() {
		return HomePage.class;
	}

	public void onDestroy() {
		System.out.println(">>> Stopping XMPP Server");

		if (xmppFactory != null) {
			xmppFactory.stop();
		} else {
			System.out.println(">>> XMPP Server was already dead.");
		}

		if (scheduler != null) {
			try {
				scheduler.shutdown();
			} catch (SchedulerException e) {
				e.printStackTrace();
			}
		}

		super.onDestroy();
	}

	@Override
	protected Class<? extends WebPage> getSignInPageClass() {
		return HomePage.class;
	}

	@Override
	protected Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass() {

		return AuthenticatedSession.class;
	}

	@Override
	public Session newSession(Request request, Response response) {
		Session s = new AuthenticatedSession(request);
		gInjector.inject(s);
		return s;
	}
}
