package nl.topicus.konijn.web.pages.home;

import java.util.Date;

import nl.topicus.konijn.data.dao.hibernate.UserDao;
import nl.topicus.konijn.data.entity.User;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.extensions.markup.html.captcha.CaptchaImageResource;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.EmailTextField;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.odlabs.wiquery.ui.dialog.util.DialogUtilsBehavior;

import com.google.inject.Inject;

/**
 * Main page
 * 
 * @author Joost Limburg
 */
public class RegisterPage extends WebPage {

	@Inject
	private UserDao userDao;

	private static final long serialVersionUID = 1L;

	private CaptchaImageResource captchaImageResource;

	private EmailTextField email;

	private DialogUtilsBehavior dialogUtilsBehavior;

	private PasswordTextField password;

	private RequiredTextField<String> captchaField;

	private final Image image;

	/**
	 * Constructor that is invoked when page is invoked without a em.
	 * 
	 * @param parameters
	 *            Page parameters
	 */
	public RegisterPage(final PageParameters parameters) {
		captchaImageResource = new CaptchaImageResource(imagePass);

		Form<String> form = new Form<String>("register");

		dialogUtilsBehavior = new DialogUtilsBehavior();
		add(dialogUtilsBehavior);

		// Email:
		form.add(email = new EmailTextField("email", ""));
		email.setRequired(true);
		email.setOutputMarkupId(true);

		// Password:
		form.add(password = new PasswordTextField("password",
				new Model<String>("")));
		password.setRequired(true);
		password.setOutputMarkupId(true);

		// Captcha:
		form.add(new AjaxLink<String>("refreshCaptcha") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				imagePass = randomString(6, 8);
				captchaImageResource = new CaptchaImageResource(imagePass);
				image.setImageResource(captchaImageResource);
				target.add(image);
			}
		});

		// Image:
		form.add(image = new Image("captchaImage", captchaImageResource));
		image.setOutputMarkupId(true);

		// Captcha textfield:
		form.add(captchaField = new RequiredTextField<String>("captcha",
				new Model<String>("")) {
			private static final long serialVersionUID = 1L;

			@Override
			protected final void onComponentTag(final ComponentTag tag) {
				super.onComponentTag(tag);
				// clear the field after each render
				tag.put("value", "");
			}

		});
		captchaField.setRequired(true);
		captchaField.setOutputMarkupId(true);

		// Submit button:
		form.add(new AjaxSubmitLink("submit") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				if (processForm(target)) {
					form.clearInput();
					target.add(form);
				}
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.appendJavaScript(dialogUtilsBehavior
						.warningDialog(
								"One or more necessary fields have not been entered correctly.")
						.render().toString());
			}

		});

		add(form);
	}

	/**
	 * Process register form
	 * 
	 * @param target
	 */
	private boolean processForm(AjaxRequestTarget target) {
		String error = "";
		String usernameText = email.getModelObject().toLowerCase();
		String passwordText = password.getModelObject();
		String captchaText = captchaField.getModelObject();

		// Validate password:
		if (passwordText.length() < 8 || passwordText.length() > 12) {
			error = "Your password should be at least 8 characters, with a max of 12.";
		}

		// Validate email:
		if (error.length() == 0 && !email.isValid()) {
			error = "The email address entered is invalid.";
		}

		// Check if user exists:
		if (error.length() == 0 && userDao.getUser(usernameText) != null) {
			error = "The email adress entered has been registered already.";
		}

		// Check captcha:
		if (error.length() == 0
				&& !getCaptcha().toLowerCase()
						.equals(captchaText.toLowerCase())) {
			error = "Invalid captcha entered. Please try again.";
		}

		imagePass = randomString(6, 8);
		captchaImageResource = new CaptchaImageResource(imagePass);
		image.setImageResource(captchaImageResource);
		target.add(image);

		// Process
		if (error.length() == 0) {
			User user = new User();
			user.setCreatedAt(new Date());
			user.setChangedAt(new Date());
			user.setPassword(passwordText);
			user.setUsername(usernameText);
			user.setRoles(Roles.USER);
			userDao.save(user);

			target.appendJavaScript(dialogUtilsBehavior
					.simpleDialog("You have been registered.",
							"Success! Now you can login with the email and password entered.")
					.render().toString());
			return true;
		} else {
			target.appendJavaScript(dialogUtilsBehavior.warningDialog(error)
					.render().toString());
			return false;
		}
	}

	private static int randomInt(int min, int max) {
		return (int) (Math.random() * (max - min) + min);
	}

	private static String randomString(int min, int max) {
		int num = randomInt(min, max);
		byte b[] = new byte[num];
		for (int i = 0; i < num; i++)
			b[i] = (byte) randomInt('a', 'z');
		return new String(b);
	}

	/** Random captcha password to match against. */
	private String imagePass = randomString(6, 8);

	private String getCaptcha() {
		return imagePass;
	}
}
