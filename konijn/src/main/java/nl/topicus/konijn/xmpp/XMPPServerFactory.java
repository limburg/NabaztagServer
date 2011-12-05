package nl.topicus.konijn.xmpp;

import java.util.ArrayList;
import java.util.List;

import nl.topicus.konijn.config.modules.Setting;
import nl.topicus.konijn.xmpp.authentication.DigestMD5;
import nl.topicus.konijn.xmpp.authorization.AccountManagementBridge;
import nl.topicus.konijn.xmpp.authorization.AuthorizationManagementBridge;

import org.apache.log4j.Logger;
import org.apache.vysper.mina.C2SEndpoint;
import org.apache.vysper.storage.OpenStorageProviderRegistry;
import org.apache.vysper.xmpp.authentication.Plain;
import org.apache.vysper.xmpp.authentication.SASLMechanism;
import org.apache.vysper.xmpp.modules.roster.persistence.MemoryRosterManager;
import org.apache.wicket.Application;
import org.apache.wicket.guice.GuiceComponentInjector;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
/**
 * XMPPServerFactory supplies the businesslayer with a configured xmppserver entity.
 * 
 * @author Joost Limburg
 */
public class XMPPServerFactory {
	@Inject
	private Setting settings;
	
	// Xmppserver entity
	private MyXMPPServer xmpps;

	private static final Logger LOGGER = Logger
			.getLogger(XMPPServerFactory.class);

	/**
	 * Stop xmppServer
	 */
	public void stop() {
		xmpps.stop();
	}

	/**
	 * Start xmppServer & confgure the instance
	 */
	public void start(GuiceComponentInjector injector) {
		xmpps = new MyXMPPServer(settings.getHost(),
				Application.get());
		try {

			OpenStorageProviderRegistry providerRegistry = new OpenStorageProviderRegistry();

			// a roster manager is also required
			providerRegistry.add(new MemoryRosterManager());
			providerRegistry.add(new AuthorizationManagementBridge());
			providerRegistry.add(new AccountManagementBridge());

			xmpps.setStorageProviderRegistry(providerRegistry);
			xmpps.addEndpoint(new C2SEndpoint());
			//xmpps.setTLSCertificateInfo(new File(
			//		"src/main/resources/META-INF/keystore.jks"), "sekret");
			xmpps.start();
			LOGGER.info("Vysper server is running in domain: "
					+ settings.getHost());

			List<SASLMechanism> methods = new ArrayList<SASLMechanism>();
			methods.add(new DigestMD5());
			methods.add(new Plain());
			xmpps.getServerRuntimeContext().getServerFeatures()
					.setAuthenticationMethods(methods);
			xmpps.getServerRuntimeContext().getServerFeatures()
					.setStartTLSRequired(false);
			// xmpps.addModule(new InBandRegistrationModule());
			// xmpps.addModule(new InBandRegistrationModule());
			/*
			 * xmpps.addModule(new EntityTimeModule()); xmpps.addModule(new
			 * VcardTempModule()); xmpps.addModule(new XmppPingModule());
			 * xmpps.addModule(new PrivateDataModule());
			 */
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(100);
		}
	}

	/**
	 * Constructor
	 */
	public XMPPServerFactory() {

	}

	/**
	 * Returns the xmppServer entity
	 * 
	 * @return MyXMPPServer
	 */
	public MyXMPPServer getServer() {
		return xmpps;
	}
}
