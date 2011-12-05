package nl.topicus.konijn.xmpp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import nl.topicus.konijn.xmpp.authorization.AccountManagementBridge;
import nl.topicus.konijn.xmpp.modules.bind.CustomBindIQDictionary;
import nl.topicus.konijn.xmpp.modules.custom.CustomMessageDictionary;
import nl.topicus.konijn.xmpp.modules.md5digest.DigestMD5StanzaDictionary;
import nl.topicus.konijn.xmpp.modules.session.CustomSessionDictionary;
import nl.topicus.konijn.xmpp.modules.sources.VioletSourcesDictionary;
import nl.topicus.konijn.xmpp.util.BunniePresenceCache;
import nl.topicus.konijn.xmpp.util.CustomResourceRegistry;
import nl.topicus.konijn.xmpp.util.CustomStanzaRelayBroker;
import nl.topicus.konijn.xmpp.util.MyServerRuntimeContext;

import org.apache.vysper.storage.StorageProviderRegistry;
import org.apache.vysper.xmpp.addressing.EntityImpl;
import org.apache.vysper.xmpp.authentication.SASLMechanism;
import org.apache.vysper.xmpp.cryptography.BogusTrustManagerFactory;
import org.apache.vysper.xmpp.cryptography.InputStreamBasedTLSContextFactory;
import org.apache.vysper.xmpp.delivery.OfflineStanzaReceiver;
import org.apache.vysper.xmpp.delivery.inbound.DeliveringExternalInboundStanzaRelay;
import org.apache.vysper.xmpp.delivery.inbound.DeliveringInternalInboundStanzaRelay;
import org.apache.vysper.xmpp.modules.Module;
import org.apache.vysper.xmpp.modules.core.base.BaseStreamStanzaDictionary;
import org.apache.vysper.xmpp.modules.core.compatibility.jabber_iq_auth.JabberIQAuthDictionary;
import org.apache.vysper.xmpp.modules.core.starttls.StartTLSStanzaDictionary;
import org.apache.vysper.xmpp.modules.extension.xep0077_inbandreg.InBandRegistrationModule;
import org.apache.vysper.xmpp.modules.extension.xep0160_offline_storage.OfflineStorageProvider;
import org.apache.vysper.xmpp.modules.roster.RosterModule;
import org.apache.vysper.xmpp.modules.servicediscovery.ServiceDiscoveryModule;
import org.apache.vysper.xmpp.protocol.HandlerDictionary;
import org.apache.vysper.xmpp.server.Endpoint;
import org.apache.vysper.xmpp.server.ServerFeatures;
import org.apache.vysper.xmpp.server.ServerRuntimeContext;
import org.apache.wicket.Application;

/**
 * Custom XMPPServer entity. The original one does not give us room to edit the
 * dictionary and other important parts we need to re-invent without touching
 * the base code.
 * 
 * @author Joost Limburg
 * 
 */
public class MyXMPPServer {
	private final List<SASLMechanism> saslMechanisms = new ArrayList<SASLMechanism>();

	private String serverDomain;

	private MyServerRuntimeContext serverRuntimeContext;

	private StorageProviderRegistry storageProviderRegistry;

	private InputStream tlsCertificate;

	private String tlsCertificatePassword;

	private Application app;

	private String tlsKeyStoreType;

	private final List<Endpoint> endpoints = new ArrayList<Endpoint>();

	private final List<Module> initialModules = new ArrayList<Module>();

	public MyXMPPServer(String domain, Application app) {
		this.serverDomain = domain;
		this.setApp(app);
		// default list of SASL mechanisms
		// saslMechanisms.add(new Plain());

		// add default modules
		initialModules.add(new InBandRegistrationModule());
		initialModules.add(new ServiceDiscoveryModule());
		initialModules.add(new RosterModule());
	}

	public void setSASLMechanisms(List<SASLMechanism> validMechanisms) {
		saslMechanisms.addAll(validMechanisms);
	}

	public void setStorageProviderRegistry(
			StorageProviderRegistry storageProviderRegistry) {
		this.storageProviderRegistry = storageProviderRegistry;
	}

	public void setTLSCertificateInfo(File certificate, String password)
			throws FileNotFoundException {
		tlsCertificate = new FileInputStream(certificate);
		tlsCertificatePassword = password;
	}

	public void setTLSCertificateInfo(InputStream certificate, String password) {
		setTLSCertificateInfo(certificate, password, null);
	}

	public void setTLSCertificateInfo(InputStream certificate, String password,
			String keyStoreType) {
		tlsCertificate = certificate;
		tlsCertificatePassword = password;
		tlsKeyStoreType = keyStoreType;
	}

	public void addEndpoint(Endpoint endpoint) {
		endpoints.add(endpoint);
	}

	public void start() throws Exception {

		BogusTrustManagerFactory bogusTrustManagerFactory = new BogusTrustManagerFactory();
		InputStreamBasedTLSContextFactory tlsContextFactory = new InputStreamBasedTLSContextFactory(
				tlsCertificate);
		tlsContextFactory.setPassword(tlsCertificatePassword);
		tlsContextFactory.setTrustManagerFactory(bogusTrustManagerFactory);
		if (tlsKeyStoreType != null) {
			tlsContextFactory.setKeyStoreType(tlsKeyStoreType);
		}

		List<HandlerDictionary> dictionaries = new ArrayList<HandlerDictionary>();
		addCoreDictionaries(dictionaries);

		CustomResourceRegistry resourceRegistry = new CustomResourceRegistry();

		EntityImpl serverEntity = new EntityImpl(null, serverDomain, null);

		AccountManagementBridge accountManagement = (AccountManagementBridge) storageProviderRegistry
				.retrieve(AccountManagementBridge.class);
		OfflineStanzaReceiver offlineReceiver = (OfflineStanzaReceiver) storageProviderRegistry
				.retrieve(OfflineStorageProvider.class);
		DeliveringInternalInboundStanzaRelay internalStanzaRelay = new DeliveringInternalInboundStanzaRelay(
				serverEntity, resourceRegistry, accountManagement,
				offlineReceiver);
		DeliveringExternalInboundStanzaRelay externalStanzaRelay = new DeliveringExternalInboundStanzaRelay();

		CustomStanzaRelayBroker stanzaRelayBroker = new CustomStanzaRelayBroker();
		stanzaRelayBroker.setInternalRelay(internalStanzaRelay);
		stanzaRelayBroker.setExternalRelay(externalStanzaRelay);

		ServerFeatures serverFeatures = new ServerFeatures();
		serverFeatures.setAuthenticationMethods(saslMechanisms);

		serverRuntimeContext = new MyServerRuntimeContext(serverEntity,
				stanzaRelayBroker, serverFeatures, dictionaries,
				resourceRegistry);
		serverRuntimeContext.setApplication(app);
		serverRuntimeContext.setPresenceCache(new BunniePresenceCache());
		serverRuntimeContext
				.setStorageProviderRegistry(storageProviderRegistry);
		serverRuntimeContext.setTlsContextFactory(tlsContextFactory);

		for (Module module : initialModules) {
			serverRuntimeContext.addModule(module);
		}

		stanzaRelayBroker.setServerRuntimeContext(serverRuntimeContext);
		internalStanzaRelay.setServerRuntimeContext(serverRuntimeContext);
		externalStanzaRelay.setServerRuntimeContext(serverRuntimeContext);

		if (endpoints.size() == 0)
			throw new IllegalStateException(
					"server must have at least one endpoint");
		for (Endpoint endpoint : endpoints) {
			endpoint.setServerRuntimeContext(serverRuntimeContext);
			endpoint.start();
		}
	}

	public void stop() {
		for (Endpoint endpoint : endpoints) {
			endpoint.stop();
		}

		for (Module module : serverRuntimeContext.getModules()) {
			try {
				module.close();
			} catch (RuntimeException e) {
				// ignore
			}
		}

		serverRuntimeContext.getServerConnectorRegistry().close();
	}

	public void addModule(Module module) {
		if (serverRuntimeContext != null) {
			serverRuntimeContext.addModule(module);
		} else {
			initialModules.add(module);
		}
	}

	private void addCoreDictionaries(List<HandlerDictionary> dictionaries) {
		dictionaries.add(new BaseStreamStanzaDictionary());
		dictionaries.add(new StartTLSStanzaDictionary());
		dictionaries.add(new DigestMD5StanzaDictionary());
		dictionaries.add(new CustomBindIQDictionary());
		dictionaries.add(new VioletSourcesDictionary());
		dictionaries.add(new CustomSessionDictionary());
		dictionaries.add(new JabberIQAuthDictionary());
		dictionaries.add(new CustomMessageDictionary());
	}

	public ServerRuntimeContext getServerRuntimeContext() {
		return serverRuntimeContext;
	}

	public void setApp(Application app) {
		this.app = app;
	}

	public Application getApp() {
		return app;
	}

}
