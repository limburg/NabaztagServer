package nl.topicus.konijn.web.components.panels.common;

import nl.topicus.konijn.data.entity.Nabaztag;
import nl.topicus.konijn.models.PersistenceModel;
import nl.topicus.konijn.web.components.modal.ConfigureClockPanel;
import nl.topicus.konijn.web.components.modal.ConfigureRandomSpeechPanel;
import nl.topicus.konijn.web.components.modal.ConfigureSleepPanel;
import nl.topicus.konijn.web.components.modal.ConfigureWeatherPanel;
import nl.topicus.konijn.web.components.panels.events.RadioContainerPanel;
import nl.topicus.konijn.xmpp.util.WicketDelegator;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.time.Duration;
import org.odlabs.wiquery.ui.accordion.Accordion;
import org.odlabs.wiquery.ui.dialog.Dialog;

@AuthorizeInstantiation(Roles.USER)
public class NabaztagContainerPanel extends Panel {
	private static int WIDTH = 540;
	private static int HEIGHT = 320;
	private static int TIMER_NAB_ONLINE = 15;

	private static final long serialVersionUID = 1L;

	private PersistenceModel<Nabaztag> nabaztag;
	private Label status;
	private Accordion accordion;

	private Dialog sleepDialog;
	private Dialog randomDialog;
	private Dialog weatherDialog;
	private Dialog clockDialog;
	private WebMarkupContainer dialogContainer;

	public static boolean isOnline(String uid) {
		return WicketDelegator.isNabaztagOnline(uid);
	}

	public NabaztagContainerPanel(String id,
			final PersistenceModel<Nabaztag> nabaztag) {
		super(id);
		this.nabaztag = nabaztag;

		// Popup configuration.
		dialogContainer = new WebMarkupContainer("dialogContainer");
		dialogContainer.setOutputMarkupId(true);
		add(dialogContainer);

		clockDialog = configurationPopup("clockDialog",
				"Configure clock settings", new ConfigureClockPanel("child",
						nabaztag));
		sleepDialog = configurationPopup("sleepDialog",
				"Configure when your nabaztag sleeps", new ConfigureSleepPanel(
						"child", nabaztag));
		weatherDialog = configurationPopup("weatherDialog",
				"Configure weather settings", new ConfigureWeatherPanel(
						"child", nabaztag));
		randomDialog = configurationPopup("randomDialog",
				"Configure witty remarks", new ConfigureRandomSpeechPanel(
						"child", nabaztag));

		// Service Accordion:
		accordionServices();

		// Online/Offline status:
		nabaztagStatus();

		// Register services:
		registerServices();

		// Configure buttons:
		configureButtons();
	}

	/**
	 * Registers services in the accordion.
	 */
	private void registerServices() {
		accordion.add(new RadioContainerPanel("radioContainerPanel", nabaztag
				.getObject().getUid()));

	}

	/**
	 * Enables feedback to the user about his/her nabaztag, is it online or
	 * offline?
	 */
	private void nabaztagStatus() {
		add(new Label("mac", nabaztag.getObject().getUid()));

		IModel<String> statString = new LoadableDetachableModel<String>() {
			private static final long serialVersionUID = 1L;

			protected String load() {
				if (NabaztagContainerPanel.isOnline(nabaztag.getObject()
						.getUid())) {
					return "Online";
				} else {

					return "Offline";
				}
			}
		};

		WebMarkupContainer statusContainer = new WebMarkupContainer(
				"statusContainer");
		status = new Label("status", statString);
		status.setOutputMarkupId(true);

		statusContainer.add(status);
		statusContainer.setOutputMarkupId(true);
		statusContainer.add(new AjaxSelfUpdatingTimerBehavior(Duration
				.seconds(1)) {
			private static final long serialVersionUID = 1L;

			protected void onPostProcessTarget(final AjaxRequestTarget target) {
				setUpdateInterval(Duration.seconds(TIMER_NAB_ONLINE));

				if (status.getDefaultModelObject().equals("Offline")) {
					status.add(new AttributeModifier("style",
							new Model<String>("color: #DD0000;")));
				} else {
					status.add(new AttributeModifier("style",
							new Model<String>("color: #00DD00;")));
				}
			}
		});
		statusContainer.setOutputMarkupId(true);
		add(statusContainer);
	}

	/**
	 * Accordion that holds event-services.
	 */
	private void accordionServices() {
		accordion = new Accordion("accordion");
		add(accordion);
	}

	/**
	 * Popup used by the services for configuration.
	 * 
	 * @param panel
	 */
	private Dialog configurationPopup(String containerName, String title,
			Component panel) {
		Dialog configurationDialog = new Dialog(containerName);
		configurationDialog.setModal(true);
		configurationDialog.setWidth(WIDTH);
		configurationDialog.setHeight(HEIGHT);
		configurationDialog.add(panel);
		configurationDialog.setOutputMarkupId(true);
		configurationDialog.setMarkupId(containerName);
		configurationDialog.setTitle(title);
		dialogContainer.add(configurationDialog);

		return configurationDialog;
	}

	/**
	 * Buttons used by the various services for configuration.
	 */
	private void configureButtons() {
		// Create clock configuration button:
		accordion.add(new AjaxLink<String>("configureClock") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				// clockDialog.setTitle("Configure clock settings");
				// target.add(clockDialog);
				clockDialog.open(target);
			}

		});
		// Create randomspeech configuration button:
		accordion.add(new AjaxLink<String>("configureRandomSpeech") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				// randomDialog.setTitle("Configure witty remarks");
				// target.add(randomDialog);
				randomDialog.open(target);
			}

		});
		// Create weather configuration button:
		accordion.add(new AjaxLink<String>("configureWeather") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				// weatherDialog.setTitle("Configure weather settings");
				// target.add(weatherDialog);
				weatherDialog.open(target);
			}
		});
		// Create sleep configuration button:
		accordion.add(new AjaxLink<String>("configureSleep") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				// sleepDialog.setTitle("Configure when your nabaztag sleeps");
				// target.add(sleepDialog);
				sleepDialog.open(target);
			}

		});
	}
}
