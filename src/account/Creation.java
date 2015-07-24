package src.account;

import javax.swing.UnsupportedLookAndFeelException;

import src.Client;
import src.DrawingArea474;
import src.TextClass;

/**
 * 
 * @author Galkon & Tringan
 * 
 * Handles account creation - Most credits to galkon of course
 *
 */
public class Creation {
	
	private Client c;
	public Creation(Client c) {
		this.c = c;
	}
	
	/**
	 * The Username to register
	 * @return
	 */
	public String getName() {
		return username == "" ? null : TextClass.fixName(username);
	}
	
	/**
	 * The Password to register
	 * @return
	 */
	public String getPass() {
		return password == "" ? null : password;
	}
	
	/**
	 * The Password confirmation
	 * @return
	 */
	public String getConfirmPass() {
		return confirmPass == "" ? null : confirmPass;
	}
	
	/**
	 * The email to register
	 * @return
	 */
	public String getEmail() {
		return email == "" ? null : email;
	}
	
	/**
	 * The player referrer (optional)
	 * @return
	 */
	public String getReferrer() {
		return referrer == "" ? null : TextClass.fixName(referrer);
	}
	
	/**
	 * Gets the messages based on type
	 * @param type
	 * @return
	 */
	public String[] getInfoMessage(int type) {
		switch(type) {
		case 0:
			return new String[] {"Your username must consist of alphanumeric", "characters and spaces, and must not exceed", "12 characters."};
		case 1:
			return new String[] {"Your password must consist of alphanumeric", "characters, must be at least 5 characters", "and must not exceed 20 characters."};
		case 2:
			return new String[] {"Confirm your password by retyping it."};
		case 3:
			return new String[] {"If you do not enter a valid email address,", "you will not be able to recover your account", "if your password is changed or forgotten."};
		case 4:
			return new String[] {"A referrer is not required, but if you", "wish to benefit the person referring you", "please enter his name."};
		}
		return null;
	}
	
	/**
	 * Gets the error message based on type
	 * @param type
	 * @return
	 */
	public String[] getErrorMessage(int type) {
		switch(type) {
		case 0:
			return new String[] { "Username contains invalid characters." };
		case 1:
			return new String[] { "Your password cannot be your username!" };
		case 2:
			return new String[] { "Your password must be at least", "5 characters long." };
		case 3:
			return new String[] { "This must match the previously", "typed password." };
		case 4:
			return new String[] { "This is not a valid email address." };
		case 5:
			return new String[] { "You can't refer yourself!" };
		case 6:
			return new String[] { "Referrer name contains invalid characters." };
		}
		return null;
	}
	
	public String[] getMessageForPath(int path) {
		switch(path) {
		case 0:
			return usernameMessage;
		case 1:
			return passwordMessage;
		case 2:
			return confirmPassMessage;
		case 3:
			return emailMessage;
		case 4:
			return referrerMessage;
		}
		return null;
	}
	
	/**
	 * Checks if the username entered is valid
	 * @return
	 */
	public boolean validUsername() {
		if(getName() == null) {
			return false;
		}	
		if(getName().length() > 0) {
			if(!TextClass.isValidName(getName())) {
				usernameMessage = getErrorMessage(0);
			}	
			return TextClass.isValidName(getName());
		}
		return false;
	}
	
	/**
	 * Checks if the password entered is valid
	 * @return
	 */
	public boolean validPassword() {
		if (getPass() == null) {
			return false;
		}
		if (getPass().equalsIgnoreCase(getName())) {
			passwordMessage = getErrorMessage(1);
			return false;
		}
		if (getPass().length() < 5) {
			passwordMessage = getErrorMessage(2);
			return false;
		}
		return TextClass.isAlphanumeric(getPass());
	}

	/**
	 * Checks if the player gives a valid password confirmation
	 * @return
	 */
	public boolean validConfirmation() {
		if (getPass() == null) {
			return false;
		}
		if (getPass().length() > 0 && confirmPass.length() > 0) {
			if (getPass().equalsIgnoreCase(confirmPass)) {
				return true;
			} else {
				confirmPassMessage = getErrorMessage(3);
				return false;
			}
		}
		return false;
	}

	/**
	 * Checks if a player gives a valid email
	 * @return
	 */
	public boolean validEmail() {
		if (getEmail() == null) {
			return false;
		}
		if (getEmail().length() == 0) {
			return false;
		}
		if (!TextClass.isValidEmail(getEmail())) {
			emailMessage = getErrorMessage(4);
		}
		return TextClass.isValidEmail(getEmail());
	}

	/**
	 * Checks if the referrer username is valid
	 * @return
	 */
	public boolean validReferrer() {
		if (getReferrer() == null) {
			return false;
		}
		if (getReferrer().length() == 0) {
			return true;
		}
		if (getName().equalsIgnoreCase(getReferrer())) {
			referrerMessage = getErrorMessage(5);
			return false;
		}
		if (getReferrer().length() > 0) {
			if (!TextClass.isValidName(getReferrer())) {
				referrerMessage = getErrorMessage(6);
			}
			return TextClass.isValidName(getReferrer());
		}
		return false;
	}	
	
	/**
	 * Checks if the player is ready to proceed to the next step(create the account)
	 * @return
	 */
	public boolean canCreate() {
		return validUsername() && validPassword() && validConfirmation() && validEmail();
	}
	
	public void drawRegisterScreen() {
		final String[] text = {
				getName(), getPass(), getConfirmPass(), getEmail(), getReferrer()
		};
		c.titleBox[12].drawAdvancedSprite((Client.clientWidth / 2)
				- (c.titleBox[12].myWidth / 2), (Client.clientHeight / 2)
				- (c.titleBox[12].myHeight / 2));
		
		c.backButton[0].drawSprite(Client.clientWidth/2 - 150, Client.clientHeight/2  - 160);
		
		if(c.mouseX >= Client.clientWidth/2 - 150 && c.mouseX <= Client.clientWidth/2 - 150 + c.backButton[0].myWidth
				&& c.mouseY >= Client.clientHeight/2 - 168 && c.mouseY <= Client.clientHeight/2 - 160 +c.backButton[0].myHeight)
			c.backButton[1].drawSprite(Client.clientWidth/2 - 150, Client.clientHeight/2  - 160);
			
		for(int i = 0; i < coordsY2.length; i++) {
			if(text[i] != null && !text[i].equals(""))
				c.titleBox[verified[i] ? 16 : 15].drawRegisterSprite(c, Client.clientWidth/2 + 80, Client.clientHeight/2 - coordsY2[i],
						getMessageForPath(i) ,false, i);
		}	
		
		for(int i = 0; i < coordsY.length; i++)
			if(c.mouseX >= Client.clientWidth/2 - 137 && c.mouseX <= Client.clientWidth / 2 + 57
					&& c.mouseY >= Client.clientHeight/2 - coordsY[i][0] && c.mouseY <= Client.clientHeight/2 - coordsY[i][1])
				c.titleBox[4].drawTooltippedSprite(c, Client.clientWidth/2 - c.titleBox[4].myWidth/2 - 38
						, Client.clientHeight/2 - coordsY[i][1] - c.titleBox[4].myHeight + ( (i == 2 || i == 3) ? 9 : 10),
						getInfoMessage(i),true);
		
		if(c.mouseX >= Client.clientWidth/2 + 116 && c.mouseX <= Client.clientWidth/2 + 181
				&& c.mouseY >= Client.clientHeight/2 - 34 && c.mouseY <= Client.clientHeight/2 + 32) {
			c.titleBox[3].drawAdvancedSprite(Client.clientWidth/2 + 111,Client.clientHeight/2 - 33 );
		}
		
		c.arial[0].drawStringCenter("By visiting our website or starting up our java game client,", Client.clientWidth / 2 -2, 
				Client.clientHeight / 2 + 165, 0xffffff, true);
		c.arial[0].drawStringCenter("you agree to our Terms and conditions.", Client.clientWidth / 2 -2, 
				Client.clientHeight / 2 + 180, 0xffffff, true);
		DrawingArea474.drawHorizontalLine(Client.clientWidth/2 - 14, Client.clientHeight/2 + 182, 105, 0xFFFFFF);
		
		if(c.mouseX >= (Client.clientWidth/2 - 16)
				&& c.mouseX <= Client.clientWidth/2 + 90 
			&&	c.mouseY >= Client.clientHeight/2 + 162 
			&& c.mouseY <= Client.clientHeight/2 +183)
			c.setCursor(60);
		else
			c.setCursor(0);
		
		for (int index = 0; index < text.length; index++) {
			if(text[index] == null)
				text[index] = "";
			c.smallText.method389(false,
					(Client.clientWidth / 2 - 126),16777215,
					new StringBuilder().append("").append(index == 0 ? Client.capitalize(text[index]) : (index == 1 || index == 2) ? 
							TextClass.passwordAsterisks(text[index]) : text[index]).
					append(((c.loginScreenCursorPos == index+2 ? 1 : 0) & (Client.loopCycle % 40 < 20 ? 1 : 0)) != 0 ? "|" : "").
					toString(),(Client.clientHeight / 2 - coordsY3[index]));
		}
	}
	
	public void processInput() {
		for(int i = 0; i < coordsY.length; i++) {
			if(c.saveClickX >= Client.clientWidth/2 - 137 && c.saveClickX <= Client.clientWidth / 2 + 57
					&& c.saveClickY >= Client.clientHeight/2 - coordsY[i][0] && c.saveClickY <= Client.clientHeight/2 - coordsY[i][1]) {
				c.loginScreenCursorPos = 2 + i;
			}
		}
		if(c.clickMode3 == 1 && (c.saveClickX >= (Client.clientWidth/2 - 16)
				&& c.saveClickX <= Client.clientWidth/2 + 90 
			&&	c.saveClickY >= Client.clientHeight/2 + 162 
			&& c.saveClickY <= Client.clientHeight/2 +183)) {
			c.launchURL("http://zaros-rsps.info/");
			return;
		}
		
		if(c.clickMode3 == 1 && (c.saveClickX >= Client.clientWidth/2 - 150 && c.saveClickX <= Client.clientWidth/2 - 150 + c.backButton[0].myWidth
				&& c.saveClickY >= Client.clientHeight/2 - 168 && c.saveClickY <= Client.clientHeight/2 - 160 +c.backButton[0].myHeight)) {
			c.loginScreenState = 0;
			username = null;
			password = null;
			confirmPass = null;
			email = null;
			referrer = null;
		}
			
		if(c.clickMode3 == 1
				&&(c.saveClickX >= Client.clientWidth/2 + 116 && c.saveClickX <= Client.clientWidth/2 + 181
				&& c.saveClickY >= Client.clientHeight/2 - 34 && c.saveClickY <= Client.clientHeight/2 + 32)) {
			if(canCreate()) {
				c.previousScreenState = 3;
				c.loginScreenState = 1;
			}
		}		
		verified[1] = validPassword();
		verified[2] = validConfirmation();
		do {
			int keyPressed = c.readChar(-796);
			if (keyPressed == -1)
				break;
			boolean validKey = false;
			for (int i2 = 0; i2 < Client.validUserPassChars.length(); i2++) {
				if (keyPressed != Client.validUserPassChars.charAt(i2))
					continue;
				validKey = true;
				break;
			}
			verified[0] = validUsername();
			verified[3] = validEmail();
			verified[4] = validReferrer();
			if (c.loginScreenCursorPos == 2) {
				if (keyPressed == 8 && username.length() > 0)
					username = username.substring(0,
							username.length() - 1);
				if (keyPressed == 9 || keyPressed == 10 || keyPressed == 13)
					c.loginScreenCursorPos = 3;

				if (validKey)
					username += (char) keyPressed;
				if (username.length() > 12)
					username = username.substring(0, 12);
			} else if (c.loginScreenCursorPos == 3) {
				if (keyPressed == 8 && password.length() > 0)
					password = password.substring(0,
							password.length() - 1);
				if (keyPressed == 9 || keyPressed == 10 || keyPressed == 13)
					c.loginScreenCursorPos = 4;

				if (validKey)
					password += (char) keyPressed;
				if (password.length() > 20)
					password = password.substring(0, 20);
			} else if (c.loginScreenCursorPos == 4) {
				if (keyPressed == 8 && confirmPass.length() > 0)
					confirmPass = confirmPass.substring(0,
							confirmPass.length() - 1);
				if (keyPressed == 9 || keyPressed == 10 || keyPressed == 13)
					c.loginScreenCursorPos = 5;

				if (validKey)
					confirmPass += (char) keyPressed;
				if (confirmPass.length() > 20)
					confirmPass = confirmPass.substring(0, 20);
			} else if (c.loginScreenCursorPos == 5) {
				if (keyPressed == 8 && email.length() > 0)
					email = email.substring(0,
							email.length() - 1);
				if (keyPressed == 9 || keyPressed == 10 || keyPressed == 13)
					c.loginScreenCursorPos = 6;

				if (validKey)
					email += (char) keyPressed;
				if (email.length() > 34)
					email = email.substring(0, 34);
			} else if (c.loginScreenCursorPos == 6) {
				if (keyPressed == 8 && referrer.length() > 0)
					referrer = referrer.substring(0,
							referrer.length() - 1);
				if (keyPressed == 9 || keyPressed == 10 || keyPressed == 13)
					c.loginScreenCursorPos = 2;

				if (validKey)
					referrer += (char) keyPressed;
				if (referrer.length() > 12)
					referrer = referrer.substring(0, 12);
			}	
		} while (true);
		return;
	}
	
	public String[] usernameMessage = { "This field is valid." };
	public String[] passwordMessage = { "This field is valid." };
	public String[] confirmPassMessage = { "This field is valid." };
	public String[] emailMessage = { "This field is valid." };
	public String[] referrerMessage = { "This field is valid." };
	public String username = "";
	public String password = "";
	public String confirmPass = "";
	public String email = "";
	public String referrer = "";
	private final int[][] coordsY = {
		{126, 89}, {72, 32}, {13, -28},
		{-45, -83}, {-103, -139}
	};
	private final int[] coordsY2 = {
		108, 50, -6, -62, -120
	};
	private final int[] coordsY3 = {
		95, 38, -22, -75, -131
	};
	public boolean[] verified = { 
		validUsername(),  validPassword(), validConfirmation(), validEmail(), validReferrer()
	};
}