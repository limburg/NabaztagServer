package nl.topicus.konijn.config.modules;

import nl.topicus.konijn.WicketApplication;
import nl.topicus.konijn.data.dao.hibernate.EventDao;
import nl.topicus.konijn.data.dao.hibernate.EventSettingDao;
import nl.topicus.konijn.data.dao.hibernate.NabaztagDao;
import nl.topicus.konijn.data.dao.hibernate.UserDao;
import nl.topicus.konijn.data.dao.interfaces.IEventDao;
import nl.topicus.konijn.data.dao.interfaces.IEventSettingDao;
import nl.topicus.konijn.data.dao.interfaces.INabaztagDao;
import nl.topicus.konijn.data.dao.interfaces.IUserDao;
import nl.topicus.konijn.data.entity.Event;
import nl.topicus.konijn.data.entity.Nabaztag;
import nl.topicus.konijn.data.entity.User;
import nl.topicus.konijn.xmpp.XMPPServerFactory;

import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WicketFilter;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;

import com.google.inject.persist.PersistFilter;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.servlet.ServletModule;

/**
 * Guice Module
 * 
 * @author Joost Limburg
 * 
 */
public class Module extends ServletModule {

	private static final String APP_MAPPING_PATTERN = "/*";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override
	protected void configureServlets() {
		install(new JpaPersistModule("myappdb"));

		bind(WicketFilter.class).toInstance(new WicketFilter());
		bind(WebApplication.class).to(WicketApplication.class);

		filter(APP_MAPPING_PATTERN).through(PersistFilter.class);

		bind(XMPPServerFactory.class);

		// //hibernate stuff
		AnnotationConfiguration annotationConfiguration = new AnnotationConfiguration();
		annotationConfiguration.configure();
		annotationConfiguration.addAnnotatedClass(Event.class);
		annotationConfiguration.addAnnotatedClass(User.class);
		annotationConfiguration.addAnnotatedClass(Nabaztag.class);

		// bind(IBaseDao.class).to(BaseDao.class);

		bind(IUserDao.class).to(UserDao.class);
		bind(INabaztagDao.class).to(NabaztagDao.class);
		bind(IEventDao.class).to(EventDao.class);
		bind(IEventSettingDao.class).to(EventSettingDao.class);
		bind(Configuration.class).toInstance(annotationConfiguration);
	}

	protected boolean initData() {
		return true;
	}
}
