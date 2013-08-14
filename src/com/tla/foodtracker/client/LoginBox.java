package com.tla.foodtracker.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class LoginBox extends VerticalPanel
{
	private FlexTable table;
	private TextBox userNameBox;
	private PasswordTextBox passwordBox;
	private Button loginButton;
	
	
	private String userName = "banana";
	private String password = "king";
	
	private FoodTracker parent;
	
	
	/**
	 * CONSTRUCTORS
	 */
	public LoginBox(FoodTracker ft)
	{
		parent = ft;
		
		// inits the goals table
		table = new FlexTable();
		userNameBox = new TextBox();
		table.setText(0, 0, "User Name:");
		table.setWidget(0, 1, userNameBox);
		passwordBox = new PasswordTextBox();
		table.setText(1, 0, "Password:");
		table.setWidget(1, 1, passwordBox);    
		loginButton = new Button("Login");
		table.setWidget(2, 0, loginButton);
	    
		Label title = new Label("Login");
		title.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
		this.add(title);
		this.add(table);
	   
		loginButton.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				// close window
				verifyUserAndPass();
			}
			
		});
		passwordBox.addKeyPressHandler(new KeyPressHandler()
		{
			public void onKeyPress(KeyPressEvent event)
			{
				boolean enterPressed = KeyCodes.KEY_ENTER == event.getNativeEvent().getKeyCode();
				if (enterPressed)
				{
					verifyUserAndPass();
				}
			}
			
		});		
		
		// give focus to user name for convinience
		userNameBox.setFocus(true);
	    
	} // end LoginBox()
	
	
	private void verifyUserAndPass()
	{
		if (userNameBox.getText().equals(userName) &&
			passwordBox.getText().equals(password))
		{
			parent.loadApp();
		}
		
	} // end verifyUserAndPass()

} // end class NavigationBar
