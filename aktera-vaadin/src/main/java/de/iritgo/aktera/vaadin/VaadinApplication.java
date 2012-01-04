
package de.iritgo.aktera.vaadin;


import org.springframework.stereotype.Component;
import com.vaadin.Application;
import com.vaadin.ui.*;


@Component
@SuppressWarnings("serial")
public class VaadinApplication extends Application
{
	@Override
	public void init()
	{
		setTheme("runo");
		Window loginWindow = new Window("TELCAT-UC Login");
		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();
		loginWindow.setContent(layout);

		Panel panel = new Panel("TELCAT-UC Login");
		panel.setWidth(null);
		panel.setHeight(null);
		layout.addComponent(panel);
		layout.setComponentAlignment(panel, Alignment.MIDDLE_CENTER);

		LoginForm loginForm = new LoginForm();
		panel.addComponent(loginForm);

		setMainWindow(loginWindow);
	}
}
