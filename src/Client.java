package src;

import java.applet.AppletContext;
import java.awt.*;
import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.util.zip.CRC32;
import java.lang.reflect.Method;

import src.account.Creation;
import src.item.Item;
import src.item.ItemDef;
import src.item.ItemList;
import src.sign.signlink;
import src.sprite.Sprite;
import src.sprite.SpriteLoader;

import javax.swing.*;

import java.text.NumberFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;
import javax.sound.midi.*;

import java.io.InputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Client extends RSApplet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 461042660071422207L;

	private static boolean LoadedBG = true;
	static boolean openedLogger = false;
	private boolean loggerEnabled = false;
	private static int loginFailures = 0;
	public String printedMessage;
	public Logger logger;

	public String getPrefix(int rights) {
		String prefix = "cr";
		if (rights > 10) {
			prefix = "c";
		}
		return "@" + prefix + rights + "@";
	}

	public int getPrefixRights(String prefix) {
		int rights = 0;
		int start = 3;
		int end = 4;
		if (!prefix.contains("cr")) {
			start = 2;
		}
		try {
			rights = Integer.parseInt(prefix.substring(start, end));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rights;
	}

	/**
	 * Draws a black pane, used for quests
	 * 
	 * @param opacity
	 */
	public void drawBlackPane() {
		if (paneOpacity >= 255) {
			minus = true;
		}
		if (paneOpacity <= 0) {
			minus = false;
			timesLooped = true;
		}
		paneOpacity += (minus ? -2 : 2);
		DrawingArea474.drawAlphaFilledPixels(0, 0, getClientWidth(),
				getClientHeight(), 0, paneOpacity);
		if (timesLooped)
			drawPane = false;

	}

	private boolean timesLooped = false;
	private boolean minus = false;
	private int paneOpacity = 0;

	public static int clientSize = 0;
	public static int clientWidth = 765, clientHeight = 503;
	private int gameAreaWidth = 512, gameAreaHeight = 334;
	public int appletWidth = 765, appletHeight = 503;
	private static final int resizableWidth = getMaxWidth() - 200;
	private static final int resizableHeight = getMaxHeight() - 200;

	public static int getMaxWidth() {
		return (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	}

	public static int getMaxHeight() {
		return (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
	}

	public void toggleSize(int size) {
		if (clientSize != size) {
			clientSize = size;
			int width = 765;
			int height = 503;
			if (size == 0) {
				size = 0;
				width = 765;
				height = 503;
				showChat = true;
				showTab = true;
			} else if (size == 1) {
				size = 1;
				width = isWebclient() ? appletWidth : resizableWidth;
				height = isWebclient() ? appletHeight : resizableHeight;
			} else if (size == 2) {
				size = 2;
				width = getMaxWidth();
				height = getMaxHeight();
			}
			rebuildFrame(size, width, height);
			updateGameArea();
		}
	}

	public boolean isWebclient() {
		return mainFrame == null && isApplet == true;
	}

	public void checkSize() {
		if (clientSize == 1) {
			if (clientWidth != (isWebclient() ? getGameComponent().getWidth()
					: mainFrame.getFrameWidth())) {
				clientWidth = (isWebclient() ? getGameComponent().getWidth()
						: mainFrame.getFrameWidth());
				gameAreaWidth = clientWidth;
				updateGameArea();
			}
			if (clientHeight != (isWebclient() ? getGameComponent().getHeight()
					: mainFrame.getFrameHeight())) {
				clientHeight = (isWebclient() ? getGameComponent().getHeight()
						: mainFrame.getFrameHeight());
				gameAreaHeight = clientHeight;
				updateGameArea();
			}
		}
	}

	public void rebuildFrame(int size, int width, int height) {
		try {
			gameAreaWidth = (size == 0) ? 512 : width;
			gameAreaHeight = (size == 0) ? 334 : height;
			clientWidth = width;
			clientHeight = height;
			instance.rebuildFrame(size == 2, width, height, size == 1,
					size >= 1);
			updateGameArea();
			super.mouseX = super.mouseY = -1;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void updateGameArea() {
		try {
			Texture.setBounds(clientWidth, clientHeight);
			fullScreenTextureArray = Texture.lineOffsets;
			Texture.setBounds(
					clientSize == 0 ? (chatAreaIP != null ? chatAreaIP.anInt316
							: 519) : clientWidth,
							clientSize == 0 ? (chatAreaIP != null ? chatAreaIP.anInt317
									: 165) : clientHeight);
			anIntArray1180 = Texture.lineOffsets;
			Texture.setBounds(
					clientSize == 0 ? (tabAreaIP != null ? tabAreaIP.anInt316
							: 250) : clientWidth,
							clientSize == 0 ? (tabAreaIP != null ? tabAreaIP.anInt317
									: 335) : clientHeight);
			anIntArray1181 = Texture.lineOffsets;
			Texture.setBounds(!loggedIn ? clientWidth : gameAreaWidth,
					!loggedIn ? clientHeight : gameAreaHeight);
			anIntArray1182 = Texture.lineOffsets;
			int ai[] = new int[9];
			for (int i8 = 0; i8 < 9; i8++) {
				int k8 = 128 + i8 * 32 + 15;
				int l8 = 600 + k8 * 3;
				int i9 = Texture.SINE[k8];
				ai[i8] = l8 * i9 >> 16;
			}
			WorldController.method310(500, 800, gameAreaWidth, gameAreaHeight,
					ai);
			if (loggedIn) {
				gameScreenIP = new RSImageProducer(gameAreaWidth,
						gameAreaHeight, getGameComponent());
			} else {
				titleScreen = new RSImageProducer(clientWidth, clientHeight,
						getGameComponent());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void drawUnfixedGame() {
		if (clientSize != 0) {
			drawChatArea();
			drawTabArea();
			drawMinimap();
		}
	}

	public void drawChatArea() {
		//clientSize seems to refer to fixed vs resizeable mode
		//clientSize == 0 (fixed), clientSize != 0 (resizeable) 
		int offsetX = 0;
		int offsetY = clientSize != 0 ? clientHeight - 165 : 0;
		
		if (clientSize == 0) {
			chatAreaIP.initDrawingArea();
		}
		
		Texture.lineOffsets = anIntArray1180;
		TextDrawingArea textDrawingArea = normalFont;
		
		if (showChat) {
			if (clientSize == 0) {
				cacheSprite[0].drawSprite(0 + offsetX, 0 + offsetY);
			} else {
				cacheSprite[88].drawARGBSprite(7 + offsetX, 7 + offsetY);
			}
		}
		
		drawChannelButtons(offsetX, offsetY);
		
		if (showInput) {
			cacheSprite[64].drawSprite(0 + offsetX, 0 + offsetY);
			newBoldFont.drawCenteredString(promptMessage, 259 + offsetX,
					60 + offsetY, 0, -1);
			newBoldFont.drawCenteredString(promptInput + "*", 259 + offsetX,
					80 + offsetY, 128, -1);
					
		} else if (inputDialogState == 1) {
			cacheSprite[64].drawSprite(0 + offsetX, 0 + offsetY);
			newBoldFont.drawCenteredString("Enter amount:", 259 + offsetX,
					60 + offsetY, 0, -1);
			newBoldFont.drawCenteredString(amountOrNameInput + "*",
					259 + offsetX, 80 + offsetY, 128, -1);
					
		} else if (inputDialogState == 2) {
			cacheSprite[64].drawSprite(0 + offsetX, 0 + offsetY);
			newBoldFont.drawCenteredString("Enter name:", 259 + offsetX,
					60 + offsetY, 0, -1);
			newBoldFont.drawCenteredString(amountOrNameInput + "*",
					259 + offsetX, 80 + offsetY, 128, -1);
					
		} else if (inputDialogState == 3) {
			/*
			 * if (amountOrNameInput != "") { itemSearch(amountOrNameInput); }
			 * DrawingArea.setDrawingArea(121 + offsetY, 8 + offsetX, 512 +
			 * offsetX, 7 + offsetY); for (int j = 0; j < totalItemResults; j++)
			 * { final int yPos = 18 + j * 14 - itemResultScrollPos; if (yPos >
			 * 0 && yPos < 132) {
			 * newRegularFont.drawBasicString(itemResultNames[j] + " - " +
			 * itemResultIDs[j], 10 + offsetX, yPos + offsetY, 0, -1); } }
			 * DrawingArea.defaultDrawingAreaSize(); if (totalItemResults > 8) {
			 * drawScrollbar(114, itemResultScrollPos, 7 + offsetY, 496 +
			 * offsetX, totalItemResults * 14 + 7, false, clientSize == 0 ?
			 * false : true); } if (amountOrNameInput.length() == 0) {
			 * newBoldFont.drawCenteredString("Enter item name", 259 + offsetX,
			 * 70 + offsetY, 0, -1); } else if (totalItemResults == 0) {
			 * newBoldFont.drawCenteredString("No matching items found", 259 +
			 * offsetX, 70 + offsetY, 0, -1); }
			 * newRegularFont.drawBasicString(amountOrNameInput + "*", 10 +
			 * offsetX, 132 + offsetY, 0xffffff, 0);
			 * DrawingArea.drawHorizontalLine(121 + offsetY, clientSize == 0 ?
			 * 0x807660 : 0xffffff, 505, 7);
			 */
			 
			displayItemSearch();
			
		} else if (aString844 != null) {
			cacheSprite[64].drawSprite(0 + offsetX, 0 + offsetY);
			newBoldFont.drawCenteredString(aString844, 259 + offsetX,
					60 + offsetY, 0, -1);
			newBoldFont.drawCenteredString("Click to continue", 259 + offsetX,
					80 + offsetY, 128, -1);
		} else if (backDialogID != -1) {
			cacheSprite[64].drawSprite(0 + offsetX, 0 + offsetY);
			drawInterface(0, 20 + offsetX,
					RSInterface.interfaceCache[backDialogID], 20 + offsetY);
		} else if (dialogID != -1) {
			cacheSprite[64].drawSprite(0 + offsetX, 0 + offsetY);
			drawInterface(0, 20 + offsetX,
					RSInterface.interfaceCache[dialogID], 20 + offsetY);
		} else if (!quickChat && showChat) {
			int messageY = -3;
			int scrollPosition = 0;
			DrawingArea.setDrawingArea(121 + offsetY, 8 + offsetX,
					512 + offsetX, 7 + offsetY);
			for (int index = 0; index < 500; index++)
				if (chatMessages[index] != null) {
					int chatType = chatTypes[index];
					int positionY = (68 - messageY * 14) + anInt1089 + 6; //The Y offset of where sent messages appear.
					String name = chatNames[index];
					String prefixName = name;
					final String time = "[" + /* chatTimes[index] */"" + "]";
					int playerRights = 0;
					if (name != null && name.indexOf("@") == 0) {
						name = name.substring(5);
						playerRights = getPrefixRights(prefixName.substring(0,
								prefixName.indexOf(name)));
					}
					if (chatType == 0) {
						if (chatTypeView == 5 || chatTypeView == 0) {
							if (positionY > 0 && positionY < 210) {
								int xPos = 11;
								newRegularFont.drawBasicString(
										chatMessages[index], xPos + offsetX,
										positionY + offsetY,
										clientSize == 0 ? 0 : 0xffffff,
												clientSize == 0 ? -1 : 0);
							}
							scrollPosition++;
							messageY++;
						}
					}
					if ((chatType == 1 || chatType == 2)
							&& (chatType == 1 || publicChatMode == 0 || publicChatMode == 1
							&& isFriendOrSelf(name))) {
						if (chatTypeView == 1 || chatTypeView == 0
								|| (playerRights > 0 && playerRights <= 4)) {
							if (positionY > 0 && positionY < 210) {
								int xPos = 11;
								if (timeStamp) {
									newRegularFont.drawBasicString(time, xPos
											+ offsetX, positionY + offsetY,
											clientSize == 0 ? 0 : 0xffffff,
													clientSize == 0 ? -1 : 0);
									xPos += newRegularFont.getTextWidth(time);
								}
								if (playerRights > 0) {
									modIcons[playerRights]
											.drawSprite(xPos + 1 + offsetX,
													positionY - 11 + offsetY);
									xPos += 14;
								}
								newRegularFont.drawBasicString(name + ":", xPos
										+ offsetX, positionY + offsetY,
										clientSize == 0 ? 0 : 0xffffff,
												clientSize == 0 ? -1 : 0);
								xPos += newRegularFont.getTextWidth(name) + 7;
								newRegularFont.drawBasicString(
										chatMessages[index], xPos + offsetX,
										positionY + offsetY,
										clientSize == 0 ? 255 : 0x7FA9FF,
												clientSize == 0 ? -1 : 0);
							}
							scrollPosition++;
							messageY++;
						}
					}
					if ((chatType == 3 || chatType == 7)
							&& (splitPrivateChat == 0 || chatTypeView == 2)
							&& (chatType == 7 || privateChatMode == 0 || privateChatMode == 1
							&& isFriendOrSelf(name))) {
						if (chatTypeView == 2 || chatTypeView == 0) {
							if (positionY > 0 && positionY < 210) {
								int xPos = 11;
								if (timeStamp) {
									newRegularFont.drawBasicString(time, xPos
											+ offsetX, positionY + offsetY,
											clientSize == 0 ? 0 : 0xffffff,
													clientSize == 0 ? -1 : 0);
									xPos += newRegularFont.getTextWidth(time);
								}
								newRegularFont.drawBasicString("From", xPos
										+ offsetX, positionY + offsetY,
										clientSize == 0 ? 0 : 0xffffff,
												clientSize == 0 ? -1 : 0);
								xPos += newRegularFont.getTextWidth("From ");
								if (playerRights > 0) {
									modIcons[playerRights]
											.drawSprite(xPos + 1 + offsetX,
													positionY - 11 + offsetY);
									xPos += 14;
								}
								newRegularFont.drawBasicString(name + ":", xPos
										+ offsetX, positionY + offsetY,
										clientSize == 0 ? 0 : 0xffffff,
												clientSize == 0 ? -1 : 0);
								xPos += newRegularFont.getTextWidth(name) + 8;
								newRegularFont.drawBasicString(
										chatMessages[index], xPos + offsetX,
										positionY + offsetY,
										clientSize == 0 ? 0x800000 : 0xFF5256,
												clientSize == 0 ? -1 : 0);
							}
							scrollPosition++;
							messageY++;
						}
					}
					if (chatType == 4
							&& (tradeMode == 0 || tradeMode == 1
							&& isFriendOrSelf(name))) {
						if (chatTypeView == 3 || chatTypeView == 0) {
							if (positionY > 0 && positionY < 210) {
								newRegularFont.drawBasicString(name + " "
										+ chatMessages[index], 11 + offsetX,
										positionY + offsetY,
										clientSize == 0 ? 0x800080 : 0xFF00D4,
												clientSize == 0 ? -1 : 0);
							}
							scrollPosition++;
							messageY++;
						}
					}
					if (chatType == 5 && splitPrivateChat == 0
							&& privateChatMode < 2) {
						if (chatTypeView == 2 || chatTypeView == 0) {
							if (positionY > 0 && positionY < 210)
								newRegularFont.drawBasicString(
										chatMessages[index], 11 + offsetX,
										positionY + offsetY,
										clientSize == 0 ? 0x800000 : 0xFF5256,
												clientSize == 0 ? -1 : 0);
							scrollPosition++;
							messageY++;
						}
					}
					if (chatType == 6
							&& (splitPrivateChat == 0 || chatTypeView == 2)
							&& privateChatMode < 2) {
						if (chatTypeView == 2 || chatTypeView == 0) {
							if (positionY > 0 && positionY < 210) {
								newRegularFont.drawBasicString("To " + name
										+ ":", 11 + offsetX, positionY
										+ offsetY, clientSize == 0 ? 0
												: 0xffffff, clientSize == 0 ? -1 : 0);
								newRegularFont.drawBasicString(
										chatMessages[index],
										15
										+ newRegularFont
										.getTextWidth("To :"
												+ name)
												+ offsetX + offsetX, positionY
												+ offsetY,
												clientSize == 0 ? 0x800000 : 0xFF5256,
														clientSize == 0 ? -1 : 0);
							}
							scrollPosition++;
							messageY++;
						}
					}
					if (chatType == 8
							&& (tradeMode == 0 || tradeMode == 1
							&& isFriendOrSelf(name))) {
						if (chatTypeView == 3 || chatTypeView == 0) {
							if (positionY > 0 && positionY < 210)
								textDrawingArea.method385(0x7e3200, name + " "
										+ chatMessages[index], positionY
										+ offsetY, 11 + offsetX);
							scrollPosition++;
							messageY++;
						}
					}
					if (chatType == 16) {
						if (chatTypeView == 11 || chatTypeView == 0) {
							if (positionY > 0 && positionY < 210) {
								int positionX = 11;
								String title = (clientSize == 0 ? "<col=0000FF>"
										: "<col=7FA9FF>")
										+ clanname + "</col>";
								String username = (chatRights[index] > 0 ? "<img="
										+ (chatRights[index]) + ">"
										: "")
										+ capitalize(chatNames[index]);
								String message = (clientSize == 0 ? "<col=800000>"
										: "<col=FF5256>")
										+ chatMessages[index] + "</col>";
								newRegularFont.drawBasicString("[" + title
										+ "] " + username + ": " + message,
										positionX, positionY + offsetY,
										clientSize == 0 ? 0 : 0xffffff,
												clientSize == 0 ? -1 : 0);
							}
							scrollPosition++;
							messageY++;
						}
					}
				}
			DrawingArea.defaultDrawingAreaSize();
			anInt1211 = scrollPosition * 14 + 7 + 5;
			if (anInt1211 < 111) {
				anInt1211 = 111;
			}
			drawScrollbar(114, anInt1211 - anInt1089 - 113, 7 + offsetY,
					496 + offsetX, anInt1211, false, clientSize != 0);
			String name;
			if (myPlayer != null && myPlayer.name != null) {
				name = myPlayer.name;
			} else {
				name = TextClass.fixName(myUsername);
			}
			if (myRights > 0) {
				modIcons[myRights].drawSprite(12 + offsetX, 122 + offsetY);
				offsetX += 14;
			}
			if (muteReason.length() > 0) {
				textDrawingArea.method389(clientSize == 0 ? false : true,
						11 + offsetX, clientSize == 0 ? 0 : 0xffffff,
								"You are currently muted. Reason: " + muteReason + ".",
								133 + offsetY);
			} else {
				textDrawingArea.method389(clientSize == 0 ? false : true,
						11 + offsetX, clientSize == 0 ? 0 : 0xffffff, name,
								133 + offsetY); //Player's name
				cacheSprite[14].drawSprite(textDrawingArea.getTextWidth(name)
						+ 11 + offsetX, 123 + offsetY); //QuickChat sprite
				textDrawingArea.method389(clientSize == 0 ? false : true,
						textDrawingArea.getTextWidth(name) + 24 + offsetX,
						clientSize == 0 ? 0 : 0xffffff, ": ", 133 + offsetY); //No detectable change
				/*
				//This block causes typed text to orient right-to-left instead of left-to-right
				newRegularFont.drawRAString(inputString + "*", 24 +
				newRegularFont.getTextWidth(s + ": ") + xPosOffset, 133 +
				yPosOffset, clientSize == 0 ? 255 : 0x7FA9FF, clientSize == 0
				? -1 : 0);
				*/
				newRegularFont.drawBasicString(inputString + "*", 24
						+ newRegularFont.getTextWidth(name + ": ") + offsetX,
						133 + offsetY, clientSize == 0 ? 255 : 0x7FA9FF,
								clientSize == 0 ? -1 : 0); //The place where typed text goes before it is sent
			}
			if (clientSize == 0)
				DrawingArea.drawHorizontalLine(121 + offsetY,
						clientSize == 0 ? 0x807660 : 0xffffff, 505, 7); //Horizontal line above player's  name
		} else if (quickChat) {
			cacheSprite[64].drawSprite(0 + offsetX, 0 + offsetY);
			displayQuickChat(offsetX, offsetY);
		}
		if (menuOpen && menuScreenArea == 2) {
			drawMenu();
		}
		if (clientSize == 0) {
			chatAreaIP.drawGraphics(338, super.graphics, 0);
		}
		gameScreenIP.initDrawingArea();
		Texture.lineOffsets = anIntArray1182;
	}

	public int channel;
	public boolean showChat = true;
	public boolean timeStamp = false;
	public String mutedBy = "";
	public String muteReason = "";
	public int chatColor = 0;
	public int chatEffect = 0;

	/**
	 * quickChat: is quick chat open? canTalk: can player submit text(type in
	 * the chatbox)? quickHoverY: hover position of the green box.
	 **/
	public boolean quickChat = false, canTalk = true, divideSelections = false,
			divideSelectedSelections = false;
	public int quickSelY = -1, quickSelY2 = -1, quickHoverY = -1,
			quickHoverY2 = -1, shownSelection = -1,
			shownSelectedSelection = -1;
	public String quickChatDir = "Quick Chat";
	public int quickHOffsetX = shownSelection != -1 ? 110 : 0;

	public void openQuickChat() {
		resetQuickChat();
		quickChat = true;
		canTalk = false;
		inputTaken = true;
	}

	public void resetQuickChat() {
		divideSelections = false;
		divideSelectedSelections = false;
		shownSelection = -1;
		shownSelectedSelection = -1;
		quickSelY = -1;
		quickSelY2 = -1;
		quickHoverY = -1;
		quickHoverY2 = -1;
	}

	/**
	 * Draws the quick chat interface.
	 **/
	public void displayQuickChat(int x, int y) {
		String[] shortcutKey = { "G", "T", "S", "E", "C", "M", "Enter" };
		String[] name = { "General", "Trade/Items", "Skills", "Group Events",
				"Clans", "Inter-game", "I'm muted." };
		cacheSprite[65].drawSprite(7 + x, 7 + y);
		if (cButtonHPos == 8) {
			cacheSprite[66].drawSprite(7 + x, 7 + y);
		}
		DrawingArea.drawPixels(2, 23 + y, 7 + x, 0x847963, 506);
		if (divideSelections) {
			DrawingArea.drawPixels(111, 25 + y, 116 + x, 0x847963, 2);
		}
		if (divideSelectedSelections) {
			DrawingArea.drawPixels(111, 25 + y, 116 + 158 + x, 0x847963, 2);
		}
		normalFont.method389(false, 45 + x, 255, quickChatDir, 20 + y);
		if (quickHoverY != -1 && shownSelection == -1 && quickHOffsetX == 0) {
			DrawingArea.drawPixels(14, quickHoverY + y, 7 + x, 0x577E45, 109);
		} else if (quickHoverY != -1 && shownSelection != -1
				&& quickHOffsetX == 0) {
			DrawingArea.drawPixels(14, quickHoverY + y, 7 + x, 0x969777, 109);
		}
		/**
		 * Hovering over text on selected->selections.
		 **/
		if (quickHoverY2 != -1 && shownSelectedSelection == -1
				&& quickHOffsetX == 0) {
			DrawingArea.drawPixels(14, quickHoverY2 + y, 118 + 159 + x,
					0x577E45, 109);
		} else if (quickHoverY2 != -1 && shownSelectedSelection != -1
				&& quickHOffsetX == 0) {
			DrawingArea.drawPixels(14, quickHoverY2 + y, 118 + 159 + x,
					0x969777, 109);
		}
		if (quickSelY != -1) {
			DrawingArea.drawPixels(14, quickSelY + y, 7 + x, 0x969777, 109);
		}
		if (quickSelY2 != -1) {
			DrawingArea.drawPixels(14, quickSelY2 + y, 118 + x, 0x969777, 156);
		}
		for (int i1 = 0, y2 = 36; i1 < name.length; i1++, y2 += 14) {
			normalFont.method389(false, 10 + x, 0x555555,
					shortcutKey[i1] + ".", y + y2);
			if (i1 == name.length - 1)
				normalFont
				.method389(
						false,
						12
						+ x
						+ normalFont
						.getTextWidth(shortcutKey[i1]
								+ "."), 0, name[i1], y
								+ y2);
			else
				normalFont
				.method389(
						false,
						12
						+ x
						+ normalFont
						.getTextWidth(shortcutKey[i1]
								+ "."), 0, name[i1]
										+ " ->", y + y2);
		}
		if (shownSelection != -1) {
			showSelections(shownSelection, x, y);
		}
		if (shownSelectedSelection != -1) {
			showSelectedSelections(shownSelectedSelection, x, y);
		}
	}

	public void showSelections(int selections, int x, int y) {
		switch (selections) {
		case 0:
			String[] keys1 = { "R", "H", "G", "C", "S", "M", "B", "P" };
			String[] names1 = { "Responses", "Hello", "Goodbye", "Comments",
					"Smilies", "Mood", "Banter", "Player vs Player" };
			if (quickHoverY != -1 && quickHOffsetX == 110)
				DrawingArea.drawPixels(14, quickHoverY + y, 118 + x, 0x577E45,
						156);
			for (int i1 = 0, y2 = 36; i1 < names1.length; i1++, y2 += 14) {
				normalFont.method389(false, 118 + x, 0x555555, keys1[i1] + ".",
						y + y2);
				normalFont.method389(false,
						120 + x + normalFont.getTextWidth(keys1[i1] + "."), 0,
						names1[i1] + " ->", y + y2);
			}
			break;
		case 1:
			String[] keys2 = { "T", "I" };
			String[] names2 = { "Trade", "Items" };
			if (quickHoverY != -1 && quickHoverY < 53 && quickHOffsetX == 110)
				DrawingArea.drawPixels(14, quickHoverY + y, 118 + x, 0x577E45,
						101);
			for (int i2 = 0, y2 = 36; i2 < names2.length; i2++, y2 += 14) {
				normalFont.method389(false, 118 + x, 0x555555, keys2[i2] + ".",
						y + y2);
				normalFont.method389(false,
						120 + x + normalFont.getTextWidth(keys2[i2] + "."), 0,
						names2[i2] + " ->", y + y2);
			}
			break;

		default:
			break;
		}
	}

	public void showSelectedSelections(int selections, int x, int y) {
		switch (selections) {
		case 0:
			String[] keys1 = { "1", "2", "3", "4", "5" };
			String[] names1 = { "Hi.", "Hey!", "Sup?", "Hello.", "Yo dawg." };
			if (quickHoverY2 != -1 && quickHOffsetX == 269)
				DrawingArea.drawPixels(14, quickHoverY2 + y, 118 + 158 + x,
						0x577E45, 156);
			for (int i1 = 0, y2 = 36; i1 < names1.length; i1++, y2 += 14) {
				normalFont.method389(false, 118 + 159 + x, 0x555555, keys1[i1]
						+ ".", y + y2);
				normalFont.method389(
						false,
						120 + 159 + x
						+ normalFont.getTextWidth(keys1[i1] + "."), 0,
						names1[i1], y + y2);
			}
			break;

		default:
			break;
		}
	}

	public void processQuickChatArea() {
		int y = clientHeight - 503;
		if (super.mouseX < 117 && super.mouseY > 363) {
			quickHOffsetX = 0;
			quickHoverY2 = -1;
		} else if (super.mouseX > 117 && super.mouseX < 117 + 158
				&& super.mouseY > 363) {
			quickHOffsetX = 110;
			quickHoverY2 = -1;
		} else {
			quickHOffsetX = 269;
			quickHoverY2 = quickHoverY;
		}
		if (super.mouseX >= 7 && super.mouseX <= 23 && super.mouseY >= y + 345
				&& super.mouseY <= y + 361) {
			cButtonHPos = 8;
			inputTaken = true;
		} else if (super.mouseX >= 8 + quickHOffsetX
				&& super.mouseX <= 117 + quickHOffsetX
				&& super.mouseY >= y + 364 && super.mouseY <= y + 377) {
			quickHoverY = 25;
			inputTaken = true;
		} else if (super.mouseX >= 8 + quickHOffsetX
				&& super.mouseX <= 117 + quickHOffsetX
				&& super.mouseY >= y + 378 && super.mouseY <= y + 391) {
			quickHoverY = 39;
			inputTaken = true;
		} else if (super.mouseX >= 8 + quickHOffsetX
				&& super.mouseX <= 117 + quickHOffsetX
				&& super.mouseY >= y + 392 && super.mouseY <= y + 405) {
			quickHoverY = 53;
			inputTaken = true;
		} else if (super.mouseX >= 8 + quickHOffsetX
				&& super.mouseX <= 117 + quickHOffsetX
				&& super.mouseY >= y + 406 && super.mouseY <= y + 419) {
			quickHoverY = 67;
			inputTaken = true;
		} else if (super.mouseX >= 8 + quickHOffsetX
				&& super.mouseX <= 117 + quickHOffsetX
				&& super.mouseY >= y + 420 && super.mouseY <= y + 433) {
			quickHoverY = 81;
			inputTaken = true;
		} else if (super.mouseX >= 8 + quickHOffsetX
				&& super.mouseX <= 117 + quickHOffsetX
				&& super.mouseY >= y + 434 && super.mouseY <= y + 447) {
			quickHoverY = 95;
			inputTaken = true;
		} else if (super.mouseX >= 8 + quickHOffsetX
				&& super.mouseX <= 117 + quickHOffsetX
				&& super.mouseY >= y + 448 && super.mouseY <= y + 461) {
			quickHoverY = 109;
			inputTaken = true;
		} else if (super.mouseX >= 8 + quickHOffsetX
				&& super.mouseX <= 117 + quickHOffsetX
				&& super.mouseY >= y + 462 && super.mouseY <= y + 474
				&& shownSelection == 0) {
			quickHoverY = 123;
			inputTaken = true;
		} else {
			quickHoverY = -1;
			quickHoverY2 = -1;
			inputTaken = true;
		}
		if (super.clickMode3 == 1) {
			if (super.saveClickX >= 8 && super.saveClickX <= 117
					&& super.saveClickY >= y + 364
					&& super.saveClickY <= y + 377) {
				setSelection(25, "Quick Chat @bla@-> @blu@General", 0);
			} else if (super.saveClickX >= 8 && super.saveClickX <= 117
					&& super.saveClickY >= y + 378
					&& super.saveClickY <= y + 391) {
				setSelection(39, "Quick Chat @bla@-> @blu@Trade/Items", 1);
			} else if (clickInRegion(118, clientHeight - 126, 118 + 156,
					clientHeight - 113)) {
				if (shownSelection == 0) {
					setSelectedSelection(
							25,
							39,
							"Quick Chat @bla@-> @blu@General @bla@-> @blu@Hello",
							0);
				}
			} else if (clickInRegion(277, clientHeight - 140, 277 + 156,
					clientHeight - 126)) {
				if (shownSelectedSelection == 0) {
					quickSay("Hi.");
				}
			} else if (clickInRegion(277, clientHeight - 126, 277 + 156,
					clientHeight - 112)) {
				if (shownSelectedSelection == 0) {
					quickSay("Hey!");
				}
			} else if (clickInRegion(277, clientHeight - 112, 277 + 156,
					clientHeight - 98)) {
				if (shownSelectedSelection == 0) {
					quickSay("Sup?");
				}
			} else if (clickInRegion(277, clientHeight - 98, 277 + 156,
					clientHeight - 84)) {
				if (shownSelectedSelection == 0) {
					quickSay("Hello.");
				}
			} else if (clickInRegion(277, clientHeight - 84, 277 + 156,
					clientHeight - 70)) {
				if (shownSelectedSelection == 0) {
					quickSay("Yo dawg.");
				}
			} else if (clickInRegion(7, clientHeight - 56, 116,
					clientHeight - 42)) {
				quickSay("I'm muted and I can only use quick chat.");
			} else {
				inputTaken = true;
			}
		}
	}

	public void setSelection(int y, String directory, int selection) {
		quickSelY = y;
		quickSelY2 = -1;
		divideSelections = true;
		divideSelectedSelections = false;
		quickChatDir = directory;
		shownSelection = selection;
		shownSelectedSelection = -1;
		inputTaken = true;
	}

	public void setSelectedSelection(int y1, int y2, String directory,
			int selectedSelection) {
		divideSelections = true;
		divideSelectedSelections = true;
		quickSelY = y1;
		quickSelY2 = y2;
		quickChatDir = directory;
		shownSelectedSelection = selectedSelection;
		inputTaken = true;
	}

	public void quickSay(String text) {
		say(text, true);
		isQuickChat = true;
		resetQuickChat();
		quickChat = false;
		canTalk = true;
		inputTaken = true;
	}

	public boolean isQuickChat = false;

	public void say(String text, boolean isQuickSay) {
		isQuickChat = true;
		stream.createFrame(4);
		stream.writeByte(0);
		int j3 = stream.currentOffset;
		stream.method425(chatEffect);
		stream.method425(chatColor);
		aStream_834.currentOffset = 0;
		TextInput.method526(text, aStream_834);
		stream.method441(0, aStream_834.buffer, aStream_834.currentOffset);
		stream.writeBytes(stream.currentOffset - j3);
		text = TextInput.processText(text);
		myPlayer.textSpoken = text;
		myPlayer.anInt1513 = chatColor;
		myPlayer.anInt1531 = chatEffect;
		myPlayer.textCycle = 150;
		pushMessage(myPlayer.textSpoken, 2, getPrefix(myRights) + myPlayer.name);
		if (publicChatMode == 2) {
			publicChatMode = 3;
			stream.createFrame(95);
			stream.writeByte(publicChatMode);
			stream.writeByte(privateChatMode);
			stream.writeByte(tradeMode);
		}
	}

	public boolean filterMessages = false;
	public String[] filteredMessages = { "You catch a",
			"You successfully cook the", "You accidentally burn the",
			"You manage to get", "You get some" };// add more

	public void drawChannelButtons(int xPosOffset, int yPosOffset) {
		cacheSprite[5].drawSprite(5 + xPosOffset, 142 + yPosOffset);
		String text[] = { "On", "Friends", "Off", "Hide", "Filter", "All" };
		int textColor[] = { 65280, 0xffff00, 0xff0000, 65535, 0xffff00, 65280 };
		int[] x = { 5, 62, 119, 176, 233, 290, 347, 404 };
		switch (cButtonCPos) {
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
			cacheSprite[2].drawSprite(x[cButtonCPos] + xPosOffset,
					142 + yPosOffset);
			break;
		}
		if (cButtonHPos == cButtonCPos) {
			switch (cButtonHPos) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
				cacheSprite[3].drawSprite(x[cButtonHPos] + xPosOffset,
						142 + yPosOffset);
				break;
			case 7:
				cacheSprite[4].drawSprite(x[cButtonHPos] + xPosOffset,
						142 + yPosOffset);
				break;
			}
		} else {
			switch (cButtonHPos) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
				cacheSprite[1].drawSprite(x[cButtonHPos] + xPosOffset,
						142 + yPosOffset);
				break;
			case 7:
				cacheSprite[4].drawSprite(x[cButtonHPos] + xPosOffset,
						142 + yPosOffset);
				break;
			}
		}
		smallText.method382(0xffffff, x[0] + 28 + xPosOffset, "All",
				157 + yPosOffset, true);
		smallText.method382(0xffffff, x[1] + 28 + xPosOffset, "Game",
				152 + yPosOffset, true);
		smallText.method382(0xffffff, x[2] + 28 + xPosOffset, "Public",
				152 + yPosOffset, true);
		smallText.method382(0xffffff, x[3] + 28 + xPosOffset, "Private",
				152 + yPosOffset, true);
		smallText.method382(0xffffff, x[4] + 28 + xPosOffset, "Clan",
				152 + yPosOffset, true);
		smallText.method382(0xffffff, x[5] + 28 + xPosOffset, "Trade",
				152 + yPosOffset, true);
		smallText.method382(0xffffff, x[6] + 28 + xPosOffset, "Yell",
				152 + yPosOffset, true);
		smallText.method382(0xffffff, x[7] + 55 + xPosOffset, "Report Abuse",
				157 + yPosOffset, true);
		smallText.method382(textColor[gameChatMode], 62 + 28 + xPosOffset,
				text[gameChatMode], 163 + yPosOffset, true);
		smallText.method382(textColor[publicChatMode], x[2] + 28 + xPosOffset,
				text[publicChatMode], 163 + yPosOffset, true);
		smallText.method382(textColor[privateChatMode], x[3] + 28 + xPosOffset,
				text[privateChatMode], 163 + yPosOffset, true);
		smallText.method382(textColor[clanChatMode], x[4] + 28 + xPosOffset,
				text[clanChatMode], 163 + yPosOffset, true);
		smallText.method382(textColor[tradeMode], x[5] + 28 + xPosOffset,
				text[tradeMode], 163 + yPosOffset, true);
		// smallText.method382(textColor[yellMode], x[6] + 28 + xPosOffset,
		// text[yellMode], 163 + yPosOffset, true);
	}

	private void processChatModeClick() {
		int[] x = { 5, 62, 119, 176, 233, 290, 347, 404 };
		if (super.mouseX >= x[0] && super.mouseX <= x[0] + 56
				&& super.mouseY >= clientHeight - 23
				&& super.mouseY <= clientHeight) {
			cButtonHPos = 0;
			inputTaken = true;
		} else if (super.mouseX >= x[1] && super.mouseX <= x[1] + 56
				&& super.mouseY >= clientHeight - 23
				&& super.mouseY <= clientHeight) {
			cButtonHPos = 1;
			inputTaken = true;
		} else if (super.mouseX >= x[2] && super.mouseX <= x[2] + 56
				&& super.mouseY >= clientHeight - 23
				&& super.mouseY <= clientHeight) {
			cButtonHPos = 2;
			inputTaken = true;
		} else if (super.mouseX >= x[3] && super.mouseX <= x[3] + 56
				&& super.mouseY >= clientHeight - 23
				&& super.mouseY <= clientHeight) {
			cButtonHPos = 3;
			inputTaken = true;
		} else if (super.mouseX >= x[4] && super.mouseX <= x[4] + 56
				&& super.mouseY >= clientHeight - 23
				&& super.mouseY <= clientHeight) {
			cButtonHPos = 4;
			inputTaken = true;
		} else if (super.mouseX >= x[5] && super.mouseX <= x[5] + 56
				&& super.mouseY >= clientHeight - 23
				&& super.mouseY <= clientHeight) {
			cButtonHPos = 5;
			inputTaken = true;
		} else if (super.mouseX >= x[6] && super.mouseX <= x[6] + 56
				&& super.mouseY >= clientHeight - 23
				&& super.mouseY <= clientHeight) {
			cButtonHPos = 6;
			inputTaken = true;
		} else if (super.mouseX >= x[7] && super.mouseX <= x[7] + 111
				&& super.mouseY >= clientHeight - 23
				&& super.mouseY <= clientHeight) {
			cButtonHPos = 7;
			inputTaken = true;
		} else {
			cButtonHPos = -1;
			inputTaken = true;
		}
		if (super.clickMode3 == 1) {
			if (super.saveClickX >= x[0] && super.saveClickX <= x[0] + 56
					&& super.saveClickY >= clientHeight - 23
					&& super.saveClickY <= clientHeight) {
				if (clientSize != 0) {
					if (channel != 0) {
						cButtonCPos = 0;
						chatTypeView = 0;
						inputTaken = true;
						channel = 0;
					} else {
						showChat = showChat ? false : true;
					}
				} else {
					cButtonCPos = 0;
					chatTypeView = 0;
					inputTaken = true;
					channel = 0;
				}
				stream.createFrame(95);
				stream.writeByte(publicChatMode);
				stream.writeByte(privateChatMode);
				stream.writeByte(tradeMode);
			} else if (super.saveClickX >= x[1]
					&& super.saveClickX <= x[1] + 56
					&& super.saveClickY >= clientHeight - 23
					&& super.saveClickY <= clientHeight) {
				if (clientSize != 0) {
					if (channel != 1 && clientSize != 0) {
						cButtonCPos = 1;
						chatTypeView = 5;
						inputTaken = true;
						channel = 1;
					} else {
						showChat = showChat ? false : true;
					}
				} else {
					cButtonCPos = 1;
					chatTypeView = 5;
					inputTaken = true;
					channel = 1;
				}
				stream.createFrame(95);
				stream.writeByte(publicChatMode);
				stream.writeByte(privateChatMode);
				stream.writeByte(tradeMode);
			} else if (super.saveClickX >= x[2]
					&& super.saveClickX <= x[2] + 56
					&& super.saveClickY >= clientHeight - 23
					&& super.saveClickY <= clientHeight) {
				if (clientSize != 0) {
					if (channel != 2 && clientSize != 0) {
						cButtonCPos = 2;
						chatTypeView = 1;
						inputTaken = true;
						channel = 2;
					} else {
						showChat = showChat ? false : true;
					}
				} else {
					cButtonCPos = 2;
					chatTypeView = 1;
					inputTaken = true;
					channel = 2;
				}
				stream.createFrame(95);
				stream.writeByte(publicChatMode);
				stream.writeByte(privateChatMode);
				stream.writeByte(tradeMode);
			} else if (super.saveClickX >= x[3]
					&& super.saveClickX <= x[3] + 56
					&& super.saveClickY >= clientHeight - 23
					&& super.saveClickY <= clientHeight) {
				if (clientSize != 0) {
					if (channel != 3 && clientSize != 0) {
						cButtonCPos = 3;
						chatTypeView = 2;
						inputTaken = true;
						channel = 3;
					} else {
						showChat = showChat ? false : true;
					}
				} else {
					cButtonCPos = 3;
					chatTypeView = 2;
					inputTaken = true;
					channel = 3;
				}
				stream.createFrame(95);
				stream.writeByte(publicChatMode);
				stream.writeByte(privateChatMode);
				stream.writeByte(tradeMode);
			} else if (super.saveClickX >= x[4]
					&& super.saveClickX <= x[4] + 56
					&& super.saveClickY >= clientHeight - 23
					&& super.saveClickY <= clientHeight) {
				if (clientSize != 0) {
					if (channel != 4 && clientSize != 0) {
						cButtonCPos = 4;
						chatTypeView = 11;
						inputTaken = true;
						channel = 4;
					} else {
						showChat = showChat ? false : true;
					}
				} else {
					cButtonCPos = 4;
					chatTypeView = 11;
					inputTaken = true;
					channel = 4;
				}
				stream.createFrame(95);
				stream.writeByte(publicChatMode);
				stream.writeByte(privateChatMode);
				stream.writeByte(tradeMode);
			} else if (super.saveClickX >= x[5]
					&& super.saveClickX <= x[5] + 56
					&& super.saveClickY >= clientHeight - 23
					&& super.saveClickY <= clientHeight) {
				if (clientSize != 0) {
					if (channel != 5 && clientSize != 0) {
						cButtonCPos = 5;
						chatTypeView = 3;
						inputTaken = true;
						channel = 5;
					} else {
						showChat = showChat ? false : true;
					}
				} else {
					cButtonCPos = 5;
					chatTypeView = 3;
					inputTaken = true;
					channel = 5;
				}
				stream.createFrame(95);
				stream.writeByte(publicChatMode);
				stream.writeByte(privateChatMode);
				stream.writeByte(tradeMode);
			} else if (super.saveClickX >= x[6]
					&& super.saveClickX <= x[6] + 56
					&& super.saveClickY >= clientHeight - 23
					&& super.saveClickY <= clientHeight) {
				if (clientSize != 0) {
					if (channel != 6 && clientSize != 0) {
						cButtonCPos = 6;
						chatTypeView = 6;
						inputTaken = true;
						channel = 6;
					} else {
						showChat = showChat ? false : true;
					}
				} else {
					cButtonCPos = 6;
					chatTypeView = 6;
					inputTaken = true;
					channel = 6;
				}
				stream.createFrame(95);
				stream.writeByte(publicChatMode);
				stream.writeByte(privateChatMode);
				stream.writeByte(tradeMode);
			} else if (super.saveClickX >= 404 && super.saveClickX <= 515
					&& super.saveClickY >= clientHeight - 23
					&& super.saveClickY <= clientHeight) {
				if (openInterfaceID == -1) {
					clearTopInterfaces();
					// CustomUserInput.input = "";
					reportAbuseInput = "";
					canMute = false;
					for (int i = 0; i < RSInterface.interfaceCache.length; i++) {
						if (RSInterface.interfaceCache[i] == null
								|| RSInterface.interfaceCache[i].contentType != 600)
							continue;
						reportAbuseInterfaceID = openInterfaceID = RSInterface.interfaceCache[i].parentID;
						break;
					}
				} else {
					pushMessage(
							"Please close the interface you have open before using 'report abuse'",
							0, "");
				}
			}
			if (!showChat) {
				cButtonCPos = -1;
			}
		}
	}

	private void rightClickChatButtons() {
		int y = clientHeight - 503;
		int[] x = { 5, 62, 119, 176, 233, 290, 347, 404 };
		if (super.mouseX >= 7 && super.mouseX <= 23 && super.mouseY >= y + 345
				&& super.mouseY <= y + 361) {
			if (quickChat) {
				menuActionName[1] = "Close";
				menuActionID[1] = 1004;
				menuActionRow = 2;
			}
		} else if (super.mouseX >= 7
				&& super.mouseX <= newRegularFont.getTextWidth(myUsername) + 24
				&& super.mouseY >= clientHeight - 43
				&& super.mouseY <= clientHeight - 31) {
			if (!quickChat) {
				menuActionName[1] = "Open quickchat";
				menuActionID[1] = 1005;
				menuActionRow = 2;
			}
		}
		if (super.mouseX >= x[0] && super.mouseX <= x[0] + 56
				&& super.mouseY >= clientHeight - 23
				&& super.mouseY <= clientHeight) {
			menuActionName[1] = "View All";
			menuActionID[1] = 999;
			menuActionRow = 2;
		} else if (super.mouseX >= x[1] && super.mouseX <= x[1] + 56
				&& super.mouseY >= clientHeight - 23
				&& super.mouseY <= clientHeight) {
			menuActionName[1] = "Filter Game";
			menuActionID[1] = 798;
			menuActionName[2] = "All Game";
			menuActionID[2] = 797;
			menuActionName[3] = "View Game";
			menuActionID[3] = 998;
			menuActionRow = 4;
		} else if (super.mouseX >= x[2] && super.mouseX <= x[2] + 56
				&& super.mouseY >= clientHeight - 23
				&& super.mouseY <= clientHeight) {
			menuActionName[1] = "Hide Public";
			menuActionID[1] = 997;
			menuActionName[2] = "Off Public";
			menuActionID[2] = 996;
			menuActionName[3] = "Friends Public";
			menuActionID[3] = 995;
			menuActionName[4] = "On Public";
			menuActionID[4] = 994;
			menuActionName[5] = "View Public";
			menuActionID[5] = 993;
			menuActionRow = 6;
		} else if (super.mouseX >= x[3] && super.mouseX <= x[3] + 56
				&& super.mouseY >= clientHeight - 23
				&& super.mouseY <= clientHeight) {
			menuActionName[1] = "Off Private";
			menuActionID[1] = 992;
			menuActionName[2] = "Friends Private";
			menuActionID[2] = 991;
			menuActionName[3] = "On Private";
			menuActionID[3] = 990;
			menuActionName[4] = "View Private";
			menuActionID[4] = 989;
			menuActionRow = 5;
		} else if (super.mouseX >= x[4] && super.mouseX <= x[4] + 56
				&& super.mouseY >= clientHeight - 23
				&& super.mouseY <= clientHeight) {
			menuActionName[1] = "Off Clan chat";
			menuActionID[1] = 1003;
			menuActionName[2] = "Friends Clan chat";
			menuActionID[2] = 1002;
			menuActionName[3] = "On Clan chat";
			menuActionID[3] = 1001;
			menuActionName[4] = "View Clan chat";
			menuActionID[4] = 1000;
			menuActionRow = 5;
		} else if (super.mouseX >= x[5] && super.mouseX <= x[5] + 56
				&& super.mouseY >= clientHeight - 23
				&& super.mouseY <= clientHeight) {
			menuActionName[1] = "Off Trade";
			menuActionID[1] = 987;
			menuActionName[2] = "Friends Trade";
			menuActionID[2] = 986;
			menuActionName[3] = "On Trade";
			menuActionID[3] = 985;
			menuActionName[4] = "View Trade";
			menuActionID[4] = 984;
			menuActionRow = 5;
		} else if (super.mouseX >= x[6] && super.mouseX <= x[6] + 56
				&& super.mouseY >= clientHeight - 23
				&& super.mouseY <= clientHeight) {
			menuActionName[1] = "Hide Yell";
			menuActionID[1] = 1012;
			menuActionName[2] = "Off Yell";
			menuActionID[2] = 1011;
			menuActionName[3] = "Friends Yell";
			menuActionID[3] = 1010;
			menuActionName[4] = "On Yell";
			menuActionID[4] = 1009;
			menuActionName[5] = "View Yell";
			menuActionID[5] = 1008;
			menuActionRow = 6;
		}
	}

	public boolean canClick() {
		if (mouseInRegion(clientWidth - (clientWidth < smallTabs ? 240 : 480),
				clientHeight - (clientWidth < smallTabs ? 74 : 37),
				clientWidth, clientHeight)) {
			return false;
		}
		if (showChat) {
			if (super.mouseX > 0 && super.mouseX < 519
					&& super.mouseY > clientHeight - 165
					&& super.mouseY < clientHeight
					|| super.mouseX > clientWidth - 220
					&& super.mouseX < clientWidth && super.mouseY > 0
					&& super.mouseY < 165) {
				return false;
			}
		}
		if (mouseInRegion2(clientWidth - 216, clientWidth, 0, 172)) {
			return false;
		}
		if (showTab) {
			if (clientWidth >= smallTabs) {
				if (super.mouseX >= clientWidth - 420
						&& super.mouseX <= clientWidth
						&& super.mouseY >= clientHeight - 37
						&& super.mouseY <= clientHeight
						|| super.mouseX > clientWidth - 204
						&& super.mouseX < clientWidth
						&& super.mouseY > clientHeight - 37 - 274
						&& super.mouseY < clientHeight)
					return false;
			} else {
				if (super.mouseX >= clientWidth - 210
						&& super.mouseX <= clientWidth
						&& super.mouseY >= clientHeight - 74
						&& super.mouseY <= clientHeight
						|| super.mouseX > clientWidth - 204
						&& super.mouseX < clientWidth
						&& super.mouseY > clientHeight - 74 - 274
						&& super.mouseY < clientHeight)
					return false;
			}
		}
		return true;
	}

	public int getOrbX(int orb) {
		switch (orb) {
		case 0:
			return clientSize != 0 ? clientWidth - 212 : 172;
		case 1:
			return clientSize != 0 ? clientWidth - 215 : 188;
		case 2:
			return clientSize != 0 ? clientWidth - 203 : 188;
		case 3:
			return clientSize != 0 ? clientWidth - 180 : 172;
		}
		return 0;
	}

	public int getOrbY(int orb) {
		switch (orb) {
		case 0:
			return clientSize != 0 ? 39 : 15;
		case 1:
			return clientSize != 0 ? 73 : 54;
		case 2:
			return clientSize != 0 ? 107 : 93;
		case 3:
			return clientSize != 0 ? 141 : 128;
		}
		return 0;
	}

	public double fillHP;

	public void drawHPOrb() {
		int currentHp = 0;
		try {
			currentHp = Integer
					.parseInt(RSInterface.interfaceCache[19001].message);
		} catch (Exception e) {
		}
		int health = (int) (((double) currentHp / (double) myPlayer.maxConstitution) * 100D);
		int x = getOrbX(0);
		int y = getOrbY(0);
		orbs[clientSize == 0 ? 0 : 11].drawSprite(x, y);
		if (health >= 75) {
			newSmallFont.drawCenteredString(Integer.toString(currentHp), x
					+ (clientSize == 0 ? 42 : 15), y + 26, 65280, 0);
		} else if (health <= 74 && health >= 50) {
			newSmallFont.drawCenteredString(Integer.toString(currentHp), x
					+ (clientSize == 0 ? 42 : 15), y + 26, 0xffff00, 0);
		} else if (health <= 49 && health >= 25) {
			newSmallFont.drawCenteredString(Integer.toString(currentHp), x
					+ (clientSize == 0 ? 42 : 15), y + 26, 0xfca607, 0);
		} else if (health <= 24 && health >= 0) {
			newSmallFont.drawCenteredString(Integer.toString(currentHp), x
					+ (clientSize == 0 ? 42 : 15), y + 26, 0xf50d0d, 0);
		}
		orbs[2].drawSprite(x + (clientSize == 0 ? 3 : 27), y + 3);
		double percent = (health / 100D);
		fillHP = 27 * percent;
		int depleteFill = 27 - (int) fillHP;
		orbs[1].myHeight = depleteFill;
		orbs[1].height = depleteFill;
		orbs[1].drawSprite(x + (clientSize == 0 ? 3 : 27), y + 3);
		orbs[3].drawSprite(x + (clientSize == 0 ? 9 : 33), y + 11);
	}

	public double fillPrayer;
	private boolean prayClicked;
	private String prayerBook;

	public void drawPrayerOrb() {
		int currentPray = 0;
		int maxPray = 0;
		try {
			currentPray = Integer
					.parseInt(RSInterface.interfaceCache[4012].message);
			maxPray = Integer
					.parseInt(RSInterface.interfaceCache[4013].message);
		} catch (Exception e) {
		}
		int prayer = (int) (((double) currentPray / (double) maxPray) * 100D);
		int x = getOrbX(1);
		int y = getOrbY(1);
		orbs[clientSize == 0 ? (hoverPos == 1 ? 12 : 0) : (hoverPos == 1 ? 13
				: 11)].drawSprite(x, y);
		if (prayer <= 100 && prayer >= 75) {
			newSmallFont.drawCenteredString(Integer.toString(currentPray), x
					+ (clientSize == 0 ? 42 : 15), y + 26, 65280, 0);
		} else if (prayer <= 74 && prayer >= 50) {
			newSmallFont.drawCenteredString(Integer.toString(currentPray), x
					+ (clientSize == 0 ? 42 : 15), y + 26, 0xffff00, 0);
		} else if (prayer <= 49 && prayer >= 25) {
			newSmallFont.drawCenteredString(Integer.toString(currentPray), x
					+ (clientSize == 0 ? 42 : 15), y + 26, 0xfca607, 0);
		} else if (prayer <= 24 && prayer >= 0) {
			newSmallFont.drawCenteredString(Integer.toString(currentPray), x
					+ (clientSize == 0 ? 42 : 15), y + 26, 0xf50d0d, 0);
		}
		orbs[prayClicked ? 10 : 4].drawSprite(x + (clientSize == 0 ? 3 : 27),
				y + 3);
		double percent = (prayer / 100D);
		fillPrayer = 27 * percent;
		int depleteFill = 27 - (int) fillPrayer;
		orbs[17].myHeight = depleteFill;
		orbs[17].height = depleteFill;
		orbs[17].drawSprite(x + (clientSize == 0 ? 3 : 27), y + 3);
		orbs[5].drawSprite(x + (clientSize == 0 ? 7 : 31), y + 7);
	}

	public double fillRun;
	public boolean running;
	public int currentEnergy;

	public void drawRunOrb() {
		int run = (int) (((double) currentEnergy / (double) 100) * 100D);
		int x = getOrbX(2);
		int y = getOrbY(2);
		orbs[clientSize == 0 ? (hoverPos == 2 ? 12 : 0) : (hoverPos == 2 ? 13
				: 11)].drawSprite(x, y);
		if (run <= 100 && run >= 75) {
			newSmallFont.drawCenteredString(Integer.toString(currentEnergy), x
					+ (clientSize == 0 ? 42 : 15), y + 26, 65280, 0);
		} else if (run <= 74 && run >= 50) {
			newSmallFont.drawCenteredString(Integer.toString(currentEnergy), x
					+ (clientSize == 0 ? 42 : 15), y + 26, 0xffff00, 0);
		} else if (run <= 49 && run >= 25) {
			newSmallFont.drawCenteredString(Integer.toString(currentEnergy), x
					+ (clientSize == 0 ? 42 : 15), y + 26, 0xfca607, 0);
		} else if (run <= 24 && run >= 0) {
			newSmallFont.drawCenteredString(Integer.toString(currentEnergy), x
					+ (clientSize == 0 ? 42 : 15), y + 26, 0xf50d0d, 0);
		}
		orbs[!running ? 6 : 8]
				.drawSprite(x + (clientSize == 0 ? 3 : 27), y + 3);
		double percent = (run / 100D);
		fillRun = 27 * percent;
		int depleteFill = 27 - (int) fillRun;
		orbs[18].myHeight = depleteFill;
		orbs[18].height = depleteFill;
		orbs[18].drawSprite(x + (clientSize == 0 ? 3 : 27), y + 3);
		orbs[!running ? 7 : 9].drawSprite(x + (clientSize == 0 ? 10 : 34),
				y + 7);
	}

	public double fillSummoning;

	public void drawSummoningOrb() {
		int summoning = (int) (((double) currentStats[23] / (double) maxStats[23]) * 100D);
		int x = getOrbX(3);
		int y = getOrbY(3);
		orbs[clientSize == 0 ? (hoverPos == 3 ? 12 : 0) : (hoverPos == 3 ? 13
				: 11)].drawSprite(x, y);
		if (summoning <= 100 && summoning >= 75) {
			newSmallFont.drawCenteredString(Integer.toString(currentStats[23]),
					x + (clientSize == 0 ? 42 : 15), y + 26, 65280, 0);
		} else if (summoning <= 74 && summoning >= 50) {
			newSmallFont.drawCenteredString(Integer.toString(currentStats[23]),
					x + (clientSize == 0 ? 42 : 15), y + 26, 0xffff00, 0);
		} else if (summoning <= 49 && summoning >= 25) {
			newSmallFont.drawCenteredString(Integer.toString(currentStats[23]),
					x + (clientSize == 0 ? 42 : 15), y + 26, 0xfca607, 0);
		} else if (summoning <= 24 && summoning >= 0) {
			newSmallFont.drawCenteredString(Integer.toString(currentStats[23]),
					x + (clientSize == 0 ? 42 : 15), y + 26, 0xf50d0d, 0);
		}
		orbs[getFamiliar().isActive() ? 16 : 14].drawSprite(x
				+ (clientSize == 0 ? 3 : 27), y + 3);
		double percent = (summoning / 100D);
		fillSummoning = 27 * percent;
		int depleteFill = 27 - (int) fillSummoning;
		orbs[19].myHeight = depleteFill;
		orbs[19].height = depleteFill;
		orbs[19].drawSprite(x + (clientSize == 0 ? 3 : 27), y + 3);
		orbs[15].drawSprite(x + (clientSize == 0 ? 9 : 33), y + 9);
	}

	private NumberFormat format = NumberFormat.getInstance();
	public boolean showXP;
	public boolean showBonus;
	public int gainedExpY = 0;
	public static boolean xpGained = false, canGainXP = true;
	public static int totalXP = 0;

	private LinkedList<XPGain> gains = new LinkedList<XPGain>();

	public void addXP(int skillID, int xp) {
		if (xp != 0 && canGainXP) {
			// totalXP += xp;
			gains.add(new XPGain(skillID, xp));
		}
	}

	public class XPGain {
		/**
		 * The skill which gained the xp
		 */
		private int skill;

		/**
		 * The XP Gained
		 */
		private int xp;
		private int y;
		private int alpha = 0;

		public XPGain(int skill, int xp) {
			this.skill = skill;
			this.xp = xp;
		}

		public void increaseY() {
			y++;
		}

		public int getSkill() {
			return skill;
		}

		public int getXP() {
			return xp;
		}

		public int getY() {
			return y;
		}

		public int getAlpha() {
			return alpha;
		}

		public void increaseAlpha() {
			alpha += alpha < 256 ? 30 : 0;
			alpha = alpha > 256 ? 256 : alpha;
		}

		public void decreaseAlpha() {
			alpha -= alpha > 0 ? 30 : 0;
			alpha = alpha > 256 ? 256 : alpha;
		}
	}

	public void displayXPCounter() {
		int x = clientSize == 0 ? 419 : clientWidth - 310;
		int y = clientSize == 0 ? 0 : -36;
		int currentIndex = 0;
		int offsetY = 0;
		int stop = 70;
		cacheSprite[40].drawSprite(x, clientSize == 0 ? 50 : 48 + y);
		normalFont.method389(true, x + 1, 0xffffff,
				"XP:" + String.format("%, d", totalXP), (clientSize == 0 ? 63
						: 61) + y);
		if (!gains.isEmpty()) {
			Iterator<XPGain> it$ = gains.iterator();
			while (it$.hasNext()) {
				XPGain gain = it$.next();
				if (gain.getY() < stop) {
					if (gain.getY() <= 10) {
						gain.increaseAlpha();
					}
					if (gain.getY() >= stop - 10) {
						gain.decreaseAlpha();
					}
					gain.increaseY();
				} else if (gain.getY() == stop) {
					it$.remove();
				}
				Sprite sprite = cacheSprite[gain.getSkill() + 41];
				if (gains.size() > 1) {
					offsetY = (clientSize == 0 ? 0 : -20) + (currentIndex * 28);
				}
				if (gain.getY() < stop) {
					sprite.drawSprite((x + 15) - (sprite.myWidth / 2),
							gain.getY() + offsetY + 66 - (sprite.myHeight / 2),
							gain.getAlpha());
					newSmallFont.drawBasicString("<trans=" + gain.getAlpha()
							+ ">+" + format.format(gain.getXP()) + "xp",
							x + 30, gain.getY() + offsetY + 70, 0xCC6600, 0);
				}
				currentIndex++;
			}
		}
	}

	public int hoverPos;

	public void processMapAreaMouse() {
		if (mouseInRegion(clientWidth - (clientSize == 0 ? 249 : 217),
				clientSize == 0 ? 46 : 3, clientWidth
						- (clientSize == 0 ? 249 : 217) + 34,
						(clientSize == 0 ? 46 : 3) + 34)) {
			hoverPos = 0;// xp counter
		} else if (mouseInRegion(clientSize == 0 ? clientWidth - 58
				: getOrbX(1), getOrbY(1), (clientSize == 0 ? clientWidth - 58
						: getOrbX(1)) + 57, getOrbY(1) + 34)) {
			hoverPos = 1;// prayer
		} else if (mouseInRegion(clientSize == 0 ? clientWidth - 58
				: getOrbX(2), getOrbY(2), (clientSize == 0 ? clientWidth - 58
						: getOrbX(2)) + 57, getOrbY(2) + 34)) {
			hoverPos = 2;// run
		} else if (mouseInRegion(clientSize == 0 ? clientWidth - 74
				: getOrbX(3), getOrbY(3), (clientSize == 0 ? clientWidth - 74
						: getOrbX(3)) + 57, getOrbY(3) + 34)) {
			hoverPos = 3;// summoning
		} else {
			hoverPos = -1;
		}
	}

	public boolean choosingLeftClick;
	public int leftClick;
	public String[] leftClickNames = { "Call Follower", "Dismiss", "Take BoB",
			"Renew Familiar", "Interact", "Attack", "Follower Details", "Cast"
			// "Follower Details", "Attack", "Interact", "Renew Familiar", "Take BoB",
			// "Dismiss", "Call Follower"
	};
	public int[] leftClickActions = { 1018, 1019, 1020, 1021, 1022, 1023, 1024,
			1026 };

	public void rightClickMapArea() {
		if (mouseInRegion(clientWidth - (clientSize == 0 ? 249 : 217),
				clientSize == 0 ? 46 : 3, clientWidth
						- (clientSize == 0 ? 249 : 217) + 34,
						(clientSize == 0 ? 46 : 3) + 34)) {
			menuActionName[1] = "Reset counter";
			menuActionID[1] = 1013;
			menuActionName[2] = showXP ? "Hide counter" : "Show counter";
			menuActionID[2] = 1006;
			menuActionRow = 3;
		}
		if (mouseInRegion(clientSize == 0 ? clientWidth - 58 : getOrbX(1),
				getOrbY(1),
				(clientSize == 0 ? clientWidth - 58 : getOrbX(1)) + 57,
				getOrbY(1) + 34)) {
			menuActionName[2] = prayClicked ? "Toggle Quick-" + prayerBook
					+ " off" : "Toggle Quick-" + prayerBook + " on";
			menuActionID[2] = 1500;
			menuActionRow = 2;
			menuActionName[1] = "Select quick " + prayerBook;
			menuActionID[1] = 1506;
			menuActionRow = 3;
		}
		if (mouseInRegion(clientSize == 0 ? clientWidth - 58 : getOrbX(2),
				getOrbY(2),
				(clientSize == 0 ? clientWidth - 58 : getOrbX(2)) + 57,
				getOrbY(2) + 34)) {
			menuActionName[1] = running ? "Turn run off" : "Turn run on";
			menuActionID[1] = 1014;
			menuActionRow = 2;
		}
		int x = super.mouseX; // Face North on compass
		int y = super.mouseY;
		if (x >= 531 && x <= 557 && y >= 7 && y <= 40) {
			menuActionName[1] = "Face North";
			menuActionID[1] = 696;
			menuActionRow = 2;
		}
		if (mouseInRegion(clientSize == 0 ? clientWidth - 74 : getOrbX(3),
				getOrbY(3),
				(clientSize == 0 ? clientWidth - 74 : getOrbX(3)) + 57,
				getOrbY(3) + 34)) {
			if (leftClick != -1 && leftClick < 8) {
				menuActionName[1] = "Select left-click option";
				menuActionID[1] = 1027;
				menuActionName[2] = leftClickNames[leftClick].equals("Cast") ? leftClickNames[leftClick]
						+ " @gre@" + getFamiliar().getSpecialAttack()
						: leftClickNames[leftClick];
						menuActionID[2] = leftClickActions[leftClick];
						menuActionRow = 3;
			} else if (choosingLeftClick) {
				menuActionName[1] = "Select left-click option";
				menuActionID[1] = 1027;
				menuActionName[2] = "Call Follower";
				menuActionID[2] = 1018;
				menuActionName[3] = "Dismiss";
				menuActionID[3] = 1019;
				menuActionName[4] = "Take BoB";
				menuActionID[4] = 1020;
				menuActionName[5] = "Renew Familiar";
				menuActionID[5] = 1021;
				menuActionName[6] = "Interact";
				menuActionID[6] = 1022;
				menuActionName[7] = "Attack";
				menuActionID[7] = 1023;
				if (getFamiliar().isActive()
						&& getFamiliar().getSpecialAttack().length() > 0) {
					menuActionName[8] = "Cast @gre@"
							+ getFamiliar().getSpecialAttack();
					menuActionID[8] = 1026;
					menuActionName[9] = "Follower Details";
					menuActionID[9] = 1024;
					menuActionRow = 10;
				} else {
					menuActionName[8] = "Follower Details";
					menuActionID[8] = 1024;
					menuActionRow = 9;
				}
			} else {
				menuActionName[1] = "Select left-click option";
				menuActionID[1] = 1027;
				menuActionRow = 2;
			}
		}
	}

	private void drawMinimap() {
		int xPosOffset = clientSize == 0 ? 0 : clientWidth - 246;
		if (clientSize == 0)
			mapAreaIP.initDrawingArea();
		if (anInt1021 == 2) {
			cacheSprite[67].drawSprite((clientSize == 0 ? 32
					: clientWidth - 162), (clientSize == 0 ? 9 : 5));
			if (clientSize == 0) {
				cacheSprite[6].drawSprite(0 + xPosOffset, 0);
			} else {
				cacheSprite[36].drawSprite(clientWidth - 167, 0);
				cacheSprite[37].drawSprite(clientWidth - 172, 0);
			}
			cacheSprite[38].drawSprite(
					clientSize == 0 ? -1 : clientWidth - 188,
							clientSize == 0 ? 46 : 40);
			if (hoverPos == 0) {
				cacheSprite[39].drawSprite(clientSize == 0 ? -1
						: clientWidth - 188, clientSize == 0 ? 46 : 40);
			}
			cacheSprite[30].drawSprite(
					(clientSize == 0 ? 246 : clientWidth) - 21, 0);
			if (tabHover != -1) {
				if (tabHover == 10) {
					cacheSprite[34].drawSprite((clientSize == 0 ? 246
							: clientWidth) - 21, 0);
				}
			}
			if (tabInterfaceIDs[tabID] != -1) {
				if (tabID == 10) {
					cacheSprite[35].drawSprite((clientSize == 0 ? 246
							: clientWidth) - 21, 0);
				}
			}
			drawHPOrb();
			drawPrayerOrb();
			drawRunOrb();
			drawSummoningOrb();
			drawWorldMap();
			compass[0].method352(33, viewRotation, anIntArray1057, 256,
					anIntArray968, 25, (clientSize == 0 ? 8 : 5),
					(clientSize == 0 ? 8 + xPosOffset : clientWidth - 167), 33,
					25);
			gameScreenIP.initDrawingArea();
			return;
		}
		int i = viewRotation + minimapRotation & 0x7ff;
		int j = 48 + myPlayer.x / 32;
		int l2 = 464 - myPlayer.y / 32;
		miniMap.method352(152, i, anIntArray1229, 256 + minimapZoom,
				anIntArray1052, l2, (clientSize == 0 ? 10 : 5),
				(clientSize == 0 ? 32 : clientWidth - 162), 152, j);
		for (int j5 = 0; j5 < anInt1071; j5++) {
			int k = (anIntArray1072[j5] * 4 + 2) - myPlayer.x / 32;
			int i3 = (anIntArray1073[j5] * 4 + 2) - myPlayer.y / 32;
			try {
				markMinimap(aClass30_Sub2_Sub1_Sub1Array1140[j5], k, i3);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		for (int k5 = 0; k5 < 104; k5++) {
			for (int l5 = 0; l5 < 104; l5++) {
				Deque class19 = groundArray[plane][k5][l5];
				if (class19 != null) {
					int l = (k5 * 4 + 2) - myPlayer.x / 32;
					int j3 = (l5 * 4 + 2) - myPlayer.y / 32;
					markMinimap(mapDotItem, l, j3);
				}
			}
		}
		for (int i6 = 0; i6 < npcCount; i6++) {
			NPC npc = npcArray[npcIndices[i6]];
			if (npc != null && npc.isVisible()) {
				EntityDef entityDef = npc.desc;// to review
				if (entityDef.childrenIDs != null)
					entityDef = entityDef.method161();
				if (entityDef != null && entityDef.aBoolean87
						&& entityDef.aBoolean84) {
					int i1 = npc.x / 32 - myPlayer.x / 32;
					int k3 = npc.y / 32 - myPlayer.y / 32;
					markMinimap(mapDotNPC, i1, k3);
				}
			}
		}
		for (int j6 = 0; j6 < playerCount; j6++) {
			Player player = playerArray[playerIndices[j6]];
			if (player != null && player.isVisible()) {
				int j1 = player.x / 32 - myPlayer.x / 32;
				int l3 = player.y / 32 - myPlayer.y / 32;
				boolean flag1 = false;
				long l6 = TextClass.longForName(player.name);
				for (int k6 = 0; k6 < friendsCount; k6++) {
					if (l6 != friendsListAsLongs[k6] || friendsNodeIDs[k6] == 0)
						continue;
					flag1 = true;
					break;
				}
				boolean flag2 = false;
				if (myPlayer.team != 0 && player.team != 0
						&& myPlayer.team == player.team)
					flag2 = true;
				if (flag1)
					markMinimap(mapDotFriend, j1, l3);
				else if (flag2)
					markMinimap(mapDotTeam, j1, l3);
				else
					markMinimap(mapDotPlayer, j1, l3);
			}
		}
		if (anInt855 != 0 && loopCycle % 20 < 10) {
			if (anInt855 == 1 && anInt1222 >= 0 && anInt1222 < npcArray.length) {
				NPC class30_sub2_sub4_sub1_sub1_1 = npcArray[anInt1222];
				if (class30_sub2_sub4_sub1_sub1_1 != null) {
					int k1 = class30_sub2_sub4_sub1_sub1_1.x / 32 - myPlayer.x
							/ 32;
					int i4 = class30_sub2_sub4_sub1_sub1_1.y / 32 - myPlayer.y
							/ 32;
					method81(mapMarker, i4, k1);
				}
			}
			if (anInt855 == 2) {
				int l1 = ((anInt934 - baseX) * 4 + 2) - myPlayer.x / 32;
				int j4 = ((anInt935 - baseY) * 4 + 2) - myPlayer.y / 32;
				method81(mapMarker, j4, l1);
			}
			if (anInt855 == 10 && anInt933 >= 0
					&& anInt933 < playerArray.length) {
				Player class30_sub2_sub4_sub1_sub2_1 = playerArray[anInt933];
				if (class30_sub2_sub4_sub1_sub2_1 != null) {
					int i2 = class30_sub2_sub4_sub1_sub2_1.x / 32 - myPlayer.x
							/ 32;
					int k4 = class30_sub2_sub4_sub1_sub2_1.y / 32 - myPlayer.y
							/ 32;
					method81(mapMarker, k4, i2);
				}
			}
		}
		if (destX != 0) {
			int j2 = (destX * 4 + 2) - myPlayer.x / 32;
			int l4 = (destY * 4 + 2) - myPlayer.y / 32;
			markMinimap(mapFlag, j2, l4);
		}
		DrawingArea.drawPixels(3, (clientSize == 0 ? 84 : 80),
				(clientSize == 0 ? 107 + xPosOffset : clientWidth - 88),
				0xffffff, 3);
		if (clientSize == 0) {
			cacheSprite[6].drawSprite(0 + xPosOffset, 0);
		} else {
			cacheSprite[36].drawSprite(clientWidth - 167, 0);
			cacheSprite[37].drawSprite(clientWidth - 172, 0);
		}
		cacheSprite[38].drawSprite(clientSize == 0 ? -1 : clientWidth - 217,
				clientSize == 0 ? 46 : 3);
		if (hoverPos == 0) {
			cacheSprite[39].drawSprite(
					clientSize == 0 ? -1 : clientWidth - 217,
							clientSize == 0 ? 46 : 3);
		}
		cacheSprite[30].drawSprite((clientSize == 0 ? 246 : clientWidth) - 21,
				0);
		if (tabHover != -1) {
			if (tabHover == 10) {
				cacheSprite[34].drawSprite(
						(clientSize == 0 ? 246 : clientWidth) - 21, 0);
			}
		}
		if (tabInterfaceIDs[tabID] != -1) {
			if (tabID == 10) {
				cacheSprite[35].drawSprite(
						(clientSize == 0 ? 246 : clientWidth) - 21, 0);
			}
		}
		drawHPOrb();
		drawPrayerOrb();
		drawRunOrb();
		drawSummoningOrb();
		drawWorldMap();
		compass[0].method352(33, viewRotation, anIntArray1057, 256,
				anIntArray968, 25, (clientSize == 0 ? 8 : 5),
				(clientSize == 0 ? 8 + xPosOffset : clientWidth - 167), 33, 25);
		if (menuOpen && menuScreenArea == 3)
			drawMenu();
		gameScreenIP.initDrawingArea();
	}

	public int tabHover = -1;
	public boolean showTab = true;
	private int smallTabs = 1000;

	public void drawTabHover(boolean fixed) {
		if (fixed) {
			if (tabHover != -1) {
				if (tabInterfaceIDs[tabHover] != -1) {
					int[] positionX = { 0, 30, 60, 120, 150, 180, 210, 90, 30,
							60, -1, 120, 150, 180, 90, 0, 210 };
					int[] positionY = { 0, 0, 0, 0, 0, 0, 0, 298, 298, 298, -1,
							298, 298, 298, 0, 298, 298 };
					if (tabHover != 10) {
						cacheSprite[16].drawSprite(3 + positionX[tabHover],
								positionY[tabHover]);
					}
				}
			}
		} else {
			if (tabHover != -1) {
				int[] tab = { 0, 1, 2, 14, 3, 4, 5, 6, 15, 8, 9, 7, 11, 12, 13,
						16 };
				int[] positionX = { 0, 30, 60, 90, 120, 150, 180, 210, 0, 30,
						60, 90, 120, 150, 180, 210 };
				int offsetX = 0;
				for (int index = 0; index < tab.length; index++) {
					if (tabInterfaceIDs[tab[index]] != -1) {
						if (tabHover == tab[index]) {
							offsetX = index > 7 && clientWidth >= smallTabs ? 240
									: 0;
							cacheSprite[16]
									.drawARGBSprite(
											(clientWidth - (clientWidth >= smallTabs ? 480
													: 240))
													+ positionX[index]
															+ offsetX,
															clientHeight
															- (clientWidth >= smallTabs ? 37
																	: (index < 8 ? 74
																			: 37)));
						}
					}
				}
			}
		}
	}

	public void handleTabArea(boolean fixed) {
		if (fixed) {
			cacheSprite[13].drawSprite(0, 0);
		} else {
			if (clientWidth >= smallTabs) {
				for (int positionX = clientWidth - 480, positionY = clientHeight - 37, index = 0; positionX <= clientWidth - 30
						&& index < 16; positionX += 30, index++) {
					cacheSprite[15].drawSprite(positionX, positionY);
				}
				if (showTab) {
					cacheSprite[18].drawTransparentSprite(clientWidth - 197,
							clientHeight - 37 - 267, 150);
					cacheSprite[19].drawSprite(clientWidth - 204,
							clientHeight - 37 - 274);
				}
			} else {
				for (int positionX = clientWidth - 240, positionY = clientHeight - 74, index = 0; positionX <= clientWidth - 30
						&& index < 8; positionX += 30, index++) {
					cacheSprite[15].drawSprite(positionX, positionY);
				}
				for (int positionX = clientWidth - 240, positionY = clientHeight - 37, index = 0; positionX <= clientWidth - 30
						&& index < 8; positionX += 30, index++) {
					cacheSprite[15].drawSprite(positionX, positionY);
				}
				if (showTab) {
					cacheSprite[18].drawTransparentSprite(clientWidth - 197,
							clientHeight - 74 - 267, 150);
					cacheSprite[19].drawSprite(clientWidth - 204,
							clientHeight - 74 - 274);
				}
			}
		}
		if (invOverlayInterfaceID == -1) {
			drawTabHover(fixed);
			if (showTab) {
				drawTabs(fixed);
			}
			drawSideIcons(fixed);
		}
	}

	public void drawTabs(boolean fixed) {
		if (fixed) {
			int xPos = 2;
			int yPos = 0;
			if (tabID < tabInterfaceIDs.length && tabInterfaceIDs[tabID] != -1) {
				switch (tabID) {
				case 0:
				case 1:
				case 2:
					xPos += tabID * 30;
					yPos = 0;
					break;
				case 3:
				case 4:
				case 5:
				case 6:
					xPos += (tabID + 1) * 30;
					yPos = 0;
					break;
				case 7:
					xPos = 2 + ((tabID - 4) * 30);
					yPos = 299;
					break;
				case 8:
				case 9:
				case 11:
				case 12:
				case 13:
					xPos = 2 + ((tabID - 7) * 30);
					yPos = 299;
					break;
				case 14:
					xPos = 92;
					yPos = 0;
					break;
				case 15:
					xPos = 2;
					yPos = 299;
					break;
				case 16:
					xPos = 212;
					yPos = 299;
					break;
				}
				if (tabID != 10) {
					cacheSprite[17].drawARGBSprite(xPos - 4, yPos);
				}
			}
		} else {
			int[] tab = { 0, 1, 2, 14, 3, 4, 5, 6, 15, 8, 9, 7, 11, 12, 13, 16 };
			int[] positionX = { 0, 30, 60, 90, 120, 150, 180, 210, 0, 30, 60,
					90, 120, 150, 180, 210 };
			for (int index = 0; index < tab.length; index++) {
				int offsetX = clientWidth >= smallTabs ? 481 : 241;
				if (offsetX == 481 && index > 7) {
					offsetX -= 240;
				}
				int offsetY = clientWidth >= smallTabs ? 37 : (index > 7 ? 37
						: 74);
				if (tabID == tab[index] && tabInterfaceIDs[tab[index]] != -1) {
					cacheSprite[17].drawARGBSprite((clientWidth - offsetX - 4)
							+ positionX[index], (clientHeight - offsetY) + 0);
				}
			}
		}
	}

	public void drawSideIcons(boolean fixed) {
		if (fixed) {
			int[] id = { 20, 89, 21, 22, 23, 24, 25, 26, 95, 28, 29, 27, 31,
					32, 33, 90, 149 };
			int[] tab = { 0, 1, 2, 14, 3, 4, 5, 6, 15, 8, 9, 7, 11, 12, 13, 16 };
			int[] positionX = { 8, 37, 67, 97, 127, 159, 187, 217, 7, 38, 69,
					97, 127, 157, 187, 217 };
			int[] positionY = { 9, 9, 8, 8, 8, 8, 8, 8, 307, 306, 306, 307,
					306, 306, 306, 308 };
			for (int index = 0; index < 16; index++) {
				if (tabInterfaceIDs[tab[index]] != -1) {
					if (id[index] != -1) {
						cacheSprite[id[((tabInterfaceIDs[14] == 26224 || tabInterfaceIDs[14] == 27224) && index == 3) ? 16
								: index]].drawSprite(positionX[index],
										positionY[index]);
					}
				}
			}
		} else {
			int[] id = { 20, 89, 21, 22, 23, 24, 25, 26, 95, 28, 29, 27, 31,
					32, 33, 90, 149 };
			int[] tab = { 0, 1, 2, 14, 3, 4, 5, 6, 15, 8, 9, 7, 11, 12, 13, 16 };
			int[] positionX = { 8, 37, 67, 97, 127, 159, 187, 217, 7, 38, 69,
					97, 127, 157, 187, 217 };
			int[] positionY = { 9, 9, 8, 8, 8, 8, 8, 8, /* second row */8, 8,
					8, 9, 8, 8, 8, 9 };
			for (int index = 0; index < tab.length; index++) {
				int offsetX = clientWidth >= smallTabs ? 482 : 242;
				if (offsetX == 482 && index > 7) {
					offsetX -= 240;
				}
				int offsetY = clientWidth >= smallTabs ? 37 : (index > 7 ? 37
						: 74);
				if (tabInterfaceIDs[tab[index]] != -1) {
					if (id[index] != -1) {
						cacheSprite[id[((tabInterfaceIDs[14] == 26224 || tabInterfaceIDs[14] == 27224) && index == 3) ? 16
								: index]].drawSprite((clientWidth - offsetX)
										+ positionX[index], (clientHeight - offsetY)
										+ positionY[index]);
					}
				}
			}
		}
	}

	private void drawTabArea() {
		if (clientSize == 0) {
			tabAreaIP.initDrawingArea();
		}
		Texture.lineOffsets = anIntArray1181;
		handleTabArea(clientSize == 0);
		int y = clientWidth >= smallTabs ? 37 : 74;
		if (showTab) {
			if (invOverlayInterfaceID != -1) {
				drawInterface(0, (clientSize == 0 ? 28 : clientWidth - 197),
						RSInterface.interfaceCache[invOverlayInterfaceID],
						(clientSize == 0 ? 37 : clientHeight - y - 267));
			} else if (tabInterfaceIDs[tabID] != -1) {
				drawInterface(0, (clientSize == 0 ? 28 : clientWidth - 197),
						RSInterface.interfaceCache[tabInterfaceIDs[tabID]],
						(clientSize == 0 ? 37 : clientHeight - y - 267));
			}
			if (menuOpen && menuScreenArea == 1) {
				drawMenu();
			}
		}
		if (clientSize == 0)
			tabAreaIP.drawGraphics(168, super.graphics, 519);
		gameScreenIP.initDrawingArea();
		Texture.lineOffsets = anIntArray1182;
	}

	private void processTabAreaTooltips(int TabHoverId) {
		String[] tooltipString = { "Combat Styles", "Task List", "Stats",
				"Inventory", "Worn Equipment", "Prayer List",
				"Magic Spellbook", "Clan Chat", "Friends List", "Ignore List",
				"Logout", "Options", "Emotes", "Music", "Quest Journals",
				"Summoning", "Notes" };
		menuActionName[1] = tooltipString[TabHoverId];
		menuActionID[1] = 1076;
		menuActionRow = 2;
	}

	private void processTabAreaHovers() {
		if (clientSize == 0) {
			int positionX = clientWidth - 244;
			int positionY = 169, positionY2 = clientHeight - 36;
			if (mouseInRegion(clientWidth - 21, 0, clientWidth, 21)) {
				tabHover = 10;
				processTabAreaTooltips(tabHover);
			} else if (mouseInRegion(positionX, positionY, positionX + 30,
					positionY + 36)) {
				needDrawTabArea = true;
				tabHover = 0;
				processTabAreaTooltips(tabHover);
				tabAreaAltered = true;
			} else if (mouseInRegion(positionX + 30, positionY, positionX + 60,
					positionY + 36)) {
				needDrawTabArea = true;
				tabHover = 1;
				processTabAreaTooltips(tabHover);
				tabAreaAltered = true;
			} else if (mouseInRegion(positionX + 60, positionY, positionX + 90,
					positionY + 36)) {
				needDrawTabArea = true;
				tabHover = 2;
				processTabAreaTooltips(tabHover);
				tabAreaAltered = true;
			} else if (mouseInRegion(positionX + 90, positionY,
					positionX + 120, positionY + 36)) {
				needDrawTabArea = true;
				tabHover = 14;
				processTabAreaTooltips(tabHover);
				tabAreaAltered = true;
			} else if (mouseInRegion(positionX + 120, positionY,
					positionX + 150, positionY + 36)) {
				needDrawTabArea = true;
				tabHover = 3;
				processTabAreaTooltips(tabHover);
				tabAreaAltered = true;
			} else if (mouseInRegion(positionX + 150, positionY,
					positionX + 180, positionY + 36)) {
				needDrawTabArea = true;
				tabHover = 4;
				processTabAreaTooltips(tabHover);
				tabAreaAltered = true;
			} else if (mouseInRegion(positionX + 180, positionY,
					positionX + 210, positionY + 36)) {
				needDrawTabArea = true;
				tabHover = 5;
				processTabAreaTooltips(tabHover);
				tabAreaAltered = true;
			} else if (mouseInRegion(positionX + 210, positionY,
					positionX + 240, positionY + 36)) {
				needDrawTabArea = true;
				tabHover = 6;
				processTabAreaTooltips(tabHover);
				tabAreaAltered = true;
			} else if (mouseInRegion(positionX, positionY2, positionX + 30,
					positionY2 + 36)) {
				needDrawTabArea = true;
				tabHover = 15;
				processTabAreaTooltips(tabHover);
				tabAreaAltered = true;
			} else if (mouseInRegion(positionX + 30, positionY2,
					positionX + 60, positionY2 + 36)) {
				needDrawTabArea = true;
				tabHover = 8;
				processTabAreaTooltips(tabHover);
				tabAreaAltered = true;
			} else if (mouseInRegion(positionX + 60, positionY2,
					positionX + 90, positionY2 + 36)) {
				needDrawTabArea = true;
				tabHover = 9;
				processTabAreaTooltips(tabHover);
				tabAreaAltered = true;
			} else if (mouseInRegion(positionX + 90, positionY2,
					positionX + 120, positionY2 + 36)) {
				needDrawTabArea = true;
				tabHover = 7;
				processTabAreaTooltips(tabHover);
				tabAreaAltered = true;
			} else if (mouseInRegion(positionX + 120, positionY2,
					positionX + 150, positionY2 + 36)) {
				needDrawTabArea = true;
				tabHover = 11;
				processTabAreaTooltips(tabHover);
				tabAreaAltered = true;
			} else if (mouseInRegion(positionX + 150, positionY2,
					positionX + 180, positionY2 + 36)) {
				needDrawTabArea = true;
				tabHover = 12;
				processTabAreaTooltips(tabHover);
				tabAreaAltered = true;
			} else if (mouseInRegion(positionX + 180, positionY2,
					positionX + 210, positionY2 + 36)) {
				needDrawTabArea = true;
				tabHover = 13;
				processTabAreaTooltips(tabHover);
				tabAreaAltered = true;
			} else if (mouseInRegion(positionX + 210, positionY2,
					positionX + 240, positionY2 + 36)) {
				needDrawTabArea = true;
				tabHover = 16;
				processTabAreaTooltips(tabHover);
				tabAreaAltered = true;
			} else {
				needDrawTabArea = true;
				tabHover = -1;
				tabAreaAltered = true;
			}
		} else {
			int[] positionX = { 0, 30, 60, 90, 120, 150, 180, 210, 0, 30, 60,
					90, 120, 150, 180, 210 };
			int[] tab = { 0, 1, 2, 14, 3, 4, 5, 6, 15, 8, 9, 7, 11, 12, 13, 16 };
			int offsetX = (clientWidth >= smallTabs ? clientWidth - 480
					: clientWidth - 240);
			int positionY = (clientWidth >= smallTabs ? clientHeight - 37
					: clientHeight - 74);
			int secondPositionY = clientHeight - 37;
			int secondOffsetX = clientWidth >= smallTabs ? 240 : 0;
			if (mouseInRegion(clientWidth - 21, 0, clientWidth, 21)) {
				tabHover = 10;
			} else if (mouseInRegion(positionX[0] + offsetX, positionY,
					positionX[0] + offsetX + 30, positionY + 37)) {
				tabHover = tab[0];
				processTabAreaTooltips(tabHover);
				needDrawTabArea = true;
				tabAreaAltered = true;
			} else if (mouseInRegion(positionX[1] + offsetX, positionY,
					positionX[1] + offsetX + 30, positionY + 37)) {
				tabHover = tab[1];
				processTabAreaTooltips(tabHover);
				needDrawTabArea = true;
				tabAreaAltered = true;
			} else if (mouseInRegion(positionX[2] + offsetX, positionY,
					positionX[2] + offsetX + 30, positionY + 37)) {
				tabHover = tab[2];
				processTabAreaTooltips(tabHover);
				needDrawTabArea = true;
				tabAreaAltered = true;
			} else if (mouseInRegion(positionX[3] + offsetX, positionY,
					positionX[3] + offsetX + 30, positionY + 37)) {
				tabHover = tab[3];
				processTabAreaTooltips(tabHover);
				needDrawTabArea = true;
				tabAreaAltered = true;
			} else if (mouseInRegion(positionX[4] + offsetX, positionY,
					positionX[4] + offsetX + 30, positionY + 37)) {
				tabHover = tab[4];
				processTabAreaTooltips(tabHover);
				needDrawTabArea = true;
				tabAreaAltered = true;
			} else if (mouseInRegion(positionX[5] + offsetX, positionY,
					positionX[5] + offsetX + 30, positionY + 37)) {
				tabHover = tab[5];
				processTabAreaTooltips(tabHover);
				needDrawTabArea = true;
				tabAreaAltered = true;
			} else if (mouseInRegion(positionX[6] + offsetX, positionY,
					positionX[6] + offsetX + 30, positionY + 37)) {
				tabHover = tab[6];
				processTabAreaTooltips(tabHover);
				needDrawTabArea = true;
				tabAreaAltered = true;
			} else if (mouseInRegion(positionX[7] + offsetX, positionY,
					positionX[7] + offsetX + 30, positionY + 37)) {
				tabHover = tab[7];
				processTabAreaTooltips(tabHover);
				needDrawTabArea = true;
				tabAreaAltered = true;
			} else if (mouseInRegion(positionX[8] + offsetX + secondOffsetX,
					secondPositionY, positionX[8] + offsetX + secondOffsetX
					+ 30, secondPositionY + 37)) {
				tabHover = tab[8];
				processTabAreaTooltips(tabHover);
				needDrawTabArea = true;
				tabAreaAltered = true;
			} else if (mouseInRegion(positionX[9] + offsetX + secondOffsetX,
					secondPositionY, positionX[9] + offsetX + secondOffsetX
					+ 30, secondPositionY + 37)) {
				tabHover = tab[9];
				processTabAreaTooltips(tabHover);
				needDrawTabArea = true;
				tabAreaAltered = true;
			} else if (mouseInRegion(positionX[10] + offsetX + secondOffsetX,
					secondPositionY, positionX[10] + offsetX + secondOffsetX
					+ 30, secondPositionY + 37)) {
				tabHover = tab[10];
				processTabAreaTooltips(tabHover);
				needDrawTabArea = true;
				tabAreaAltered = true;
			} else if (mouseInRegion(positionX[11] + offsetX + secondOffsetX,
					secondPositionY, positionX[11] + offsetX + secondOffsetX
					+ 30, secondPositionY + 37)) {
				tabHover = tab[11];
				processTabAreaTooltips(tabHover);
				needDrawTabArea = true;
				tabAreaAltered = true;
			} else if (mouseInRegion(positionX[12] + offsetX + secondOffsetX,
					secondPositionY, positionX[12] + offsetX + secondOffsetX
					+ 30, secondPositionY + 37)) {
				tabHover = tab[12];
				processTabAreaTooltips(tabHover);
				needDrawTabArea = true;
				tabAreaAltered = true;
			} else if (mouseInRegion(positionX[13] + offsetX + secondOffsetX,
					secondPositionY, positionX[13] + offsetX + secondOffsetX
					+ 30, secondPositionY + 37)) {
				tabHover = tab[13];
				processTabAreaTooltips(tabHover);
				needDrawTabArea = true;
				tabAreaAltered = true;
			} else if (mouseInRegion(positionX[14] + offsetX + secondOffsetX,
					secondPositionY, positionX[14] + offsetX + secondOffsetX
					+ 30, secondPositionY + 37)) {
				tabHover = tab[14];
				processTabAreaTooltips(tabHover);
				needDrawTabArea = true;
				tabAreaAltered = true;
			} else if (mouseInRegion(positionX[15] + offsetX + secondOffsetX,
					secondPositionY, positionX[15] + offsetX + secondOffsetX
					+ 30, secondPositionY + 37)) {
				tabHover = tab[15];
				processTabAreaTooltips(tabHover);
				needDrawTabArea = true;
				tabAreaAltered = true;
			} else {
				tabHover = -1;
				needDrawTabArea = true;
				tabAreaAltered = true;
			}
		}
	}

	private void processTabAreaClick() {
		if (clientSize == 0) {
			int positionX = clientWidth - 244;
			int positionY = 169;
			if (super.clickMode3 == 1) {
				if (clickInRegion(positionX, positionY, positionX + 30,
						positionY + 36) && tabInterfaceIDs[0] != -1) {
					needDrawTabArea = true;
					tabID = 0;
					tabAreaAltered = true;
				}
				positionX += 30;
				if (clickInRegion(positionX, positionY, positionX + 30,
						positionY + 36) && tabInterfaceIDs[1] != -1) {
					needDrawTabArea = true;
					tabID = 1;
					tabAreaAltered = true;
				}
				positionX += 30;
				if (clickInRegion(positionX, positionY, positionX + 30,
						positionY + 36) && tabInterfaceIDs[2] != -1) {
					needDrawTabArea = true;
					tabID = 2;
					tabAreaAltered = true;
				}
				positionX += 30;
				if (clickInRegion(positionX, positionY, positionX + 30,
						positionY + 36) && tabInterfaceIDs[14] != -1) {
					needDrawTabArea = true;
					tabID = 14;
					tabAreaAltered = true;
				}
				positionX += 30;
				if (clickInRegion(positionX, positionY, positionX + 30,
						positionY + 36) && tabInterfaceIDs[3] != -1) {
					needDrawTabArea = true;
					tabID = 3;
					tabAreaAltered = true;
				}
				positionX += 30;
				if (clickInRegion(positionX, positionY, positionX + 30,
						positionY + 36) && tabInterfaceIDs[4] != -1) {
					needDrawTabArea = true;
					tabID = 4;
					tabAreaAltered = true;
				}
				positionX += 30;
				if (clickInRegion(positionX, positionY, positionX + 30,
						positionY + 36) && tabInterfaceIDs[5] != -1) {
					needDrawTabArea = true;
					tabID = 5;
					tabAreaAltered = true;
				}
				positionX += 30;
				if (clickInRegion(positionX, positionY, positionX + 30,
						positionY + 36) && tabInterfaceIDs[6] != -1) {
					needDrawTabArea = true;
					tabID = 6;
					tabAreaAltered = true;
				}
				positionX = clientWidth - 244;
				positionY = clientHeight - 36;
				if (clickInRegion(positionX, positionY, positionX + 30,
						positionY + 36) && tabInterfaceIDs[15] != -1) {
					needDrawTabArea = true;
					tabID = 15;
					tabAreaAltered = true;
				}
				positionX += 30;
				if (clickInRegion(positionX, positionY, positionX + 30,
						positionY + 36) && tabInterfaceIDs[8] != -1) {
					needDrawTabArea = true;
					tabID = 8;
					tabAreaAltered = true;
				}
				positionX += 30;
				if (clickInRegion(positionX, positionY, positionX + 30,
						positionY + 36) && tabInterfaceIDs[9] != -1) {
					needDrawTabArea = true;
					tabID = 9;
					tabAreaAltered = true;
				}
				positionX += 30;
				if (clickInRegion(positionX, positionY, positionX + 30,
						positionY + 36) && tabInterfaceIDs[7] != -1) {
					needDrawTabArea = true;
					tabID = 7;
					tabAreaAltered = true;
				}
				positionX += 30;
				if (clickInRegion(clientWidth - 21, 0, clientWidth, 21)
						&& tabInterfaceIDs[10] != -1) {
					needDrawTabArea = true;
					tabID = 10;
					tabAreaAltered = true;
				}

				if (clickInRegion(positionX, positionY, positionX + 30,
						positionY + 36) && tabInterfaceIDs[11] != -1) {
					needDrawTabArea = true;
					tabID = 11;
					tabAreaAltered = true;
				}
				positionX += 30;
				if (clickInRegion(positionX, positionY, positionX + 30,
						positionY + 36) && tabInterfaceIDs[12] != -1) {
					needDrawTabArea = true;
					tabID = 12;
					tabAreaAltered = true;
				}
				positionX += 30;
				if (clickInRegion(positionX, positionY, positionX + 30,
						positionY + 36) && tabInterfaceIDs[13] != -1) {
					needDrawTabArea = true;
					tabID = 13;
					tabAreaAltered = true;
				}
				positionX += 30;
				if (clickInRegion(positionX, positionY, positionX + 30,
						positionY + 36) && tabInterfaceIDs[16] != -1) {
					needDrawTabArea = true;
					tabID = 16;
					tabAreaAltered = true;
				}
			}
		} else {
			int[] positionX = { 0, 30, 60, 90, 120, 150, 180, 210, 0, 30, 60,
					90, 120, 150, 180, 210 };
			int[] tab = { 0, 1, 2, 14, 3, 4, 5, 6, 15, 8, 9, 7, 11, 12, 13, 16 };
			int offsetX = (clientWidth >= smallTabs ? clientWidth - 480
					: clientWidth - 240);
			int positionY = (clientWidth >= smallTabs ? clientHeight - 37
					: clientHeight - 74);
			int secondPositionY = clientHeight - 37;
			int secondOffsetX = clientWidth >= smallTabs ? 240 : 0;
			if (super.clickMode3 == 1) {
				if (clickInRegion(positionX[0] + offsetX, positionY,
						positionX[0] + offsetX + 30, positionY + 37)
						&& tabInterfaceIDs[tab[0]] != -1) {
					if (tabID == tab[0]) {
						showTab = !showTab;
					} else {
						showTab = true;
					}
					tabID = tab[0];
					needDrawTabArea = true;
					tabAreaAltered = true;
				} else if (clickInRegion(positionX[1] + offsetX, positionY,
						positionX[1] + offsetX + 30, positionY + 37)
						&& tabInterfaceIDs[tab[1]] != -1) {
					if (tabID == tab[1]) {
						showTab = !showTab;
					} else {
						showTab = true;
					}
					tabID = tab[1];
					needDrawTabArea = true;
					tabAreaAltered = true;
				} else if (clickInRegion(positionX[2] + offsetX, positionY,
						positionX[2] + offsetX + 30, positionY + 37)
						&& tabInterfaceIDs[tab[2]] != -1) {
					if (tabID == tab[2]) {
						showTab = !showTab;
					} else {
						showTab = true;
					}
					tabID = tab[2];
					needDrawTabArea = true;
					tabAreaAltered = true;
				} else if (clickInRegion(positionX[3] + offsetX, positionY,
						positionX[3] + offsetX + 30, positionY + 37)
						&& tabInterfaceIDs[tab[3]] != -1) {
					if (tabID == tab[3]) {
						showTab = !showTab;
					} else {
						showTab = true;
					}
					tabID = tab[3];
					needDrawTabArea = true;
					tabAreaAltered = true;
				} else if (clickInRegion(positionX[4] + offsetX, positionY,
						positionX[4] + offsetX + 30, positionY + 37)
						&& tabInterfaceIDs[tab[4]] != -1) {
					if (tabID == tab[4]) {
						showTab = !showTab;
					} else {
						showTab = true;
					}
					tabID = tab[4];
					needDrawTabArea = true;
					tabAreaAltered = true;
				} else if (clickInRegion(positionX[5] + offsetX, positionY,
						positionX[5] + offsetX + 30, positionY + 37)
						&& tabInterfaceIDs[tab[5]] != -1) {
					if (tabID == tab[5]) {
						showTab = !showTab;
					} else {
						showTab = true;
					}
					tabID = tab[5];
					needDrawTabArea = true;
					tabAreaAltered = true;
				} else if (clickInRegion(positionX[6] + offsetX, positionY,
						positionX[6] + offsetX + 30, positionY + 37)
						&& tabInterfaceIDs[tab[6]] != -1) {
					if (tabID == tab[6]) {
						showTab = !showTab;
					} else {
						showTab = true;
					}
					tabID = tab[6];
					needDrawTabArea = true;
					tabAreaAltered = true;
				} else if (clickInRegion(positionX[7] + offsetX, positionY,
						positionX[7] + offsetX + 30, positionY + 37)
						&& tabInterfaceIDs[tab[7]] != -1) {
					if (tabID == tab[7]) {
						showTab = !showTab;
					} else {
						showTab = true;
					}
					tabID = tab[7];
					needDrawTabArea = true;
					tabAreaAltered = true;
				} else if (clickInRegion(
						positionX[8] + offsetX + secondOffsetX,
						secondPositionY, positionX[8] + offsetX + secondOffsetX
						+ 30, secondPositionY + 37)) {
					if (tabID == tab[8]) {
						showTab = !showTab;
					} else {
						showTab = true;
					}
					tabID = tab[8];
					needDrawTabArea = true;
					tabAreaAltered = true;
				} else if (clickInRegion(
						positionX[9] + offsetX + secondOffsetX,
						secondPositionY, positionX[9] + offsetX + secondOffsetX
						+ 30, secondPositionY + 37)) {
					if (tabID == tab[9]) {
						showTab = !showTab;
					} else {
						showTab = true;
					}
					tabID = tab[9];
					needDrawTabArea = true;
					tabAreaAltered = true;
				} else if (clickInRegion(positionX[10] + offsetX
						+ secondOffsetX, secondPositionY, positionX[10]
								+ offsetX + secondOffsetX + 30, secondPositionY + 37)) {
					if (tabID == tab[10]) {
						showTab = !showTab;
					} else {
						showTab = true;
					}
					tabID = tab[10];
					needDrawTabArea = true;
					tabAreaAltered = true;
				} else if (clickInRegion(positionX[11] + offsetX
						+ secondOffsetX, secondPositionY, positionX[11]
								+ offsetX + secondOffsetX + 30, secondPositionY + 37)) {
					if (tabID == tab[11]) {
						showTab = !showTab;
					} else {
						showTab = true;
					}
					tabID = tab[11];
					needDrawTabArea = true;
					tabAreaAltered = true;
				} else if (clickInRegion(positionX[12] + offsetX
						+ secondOffsetX, secondPositionY, positionX[12]
								+ offsetX + secondOffsetX + 30, secondPositionY + 37)) {
					if (tabID == tab[12]) {
						showTab = !showTab;
					} else {
						showTab = true;
					}
					tabID = tab[12];
					needDrawTabArea = true;
					tabAreaAltered = true;
				} else if (clickInRegion(positionX[13] + offsetX
						+ secondOffsetX, secondPositionY, positionX[13]
								+ offsetX + secondOffsetX + 30, secondPositionY + 37)) {
					if (tabID == tab[13]) {
						showTab = !showTab;
					} else {
						showTab = true;
					}
					tabID = tab[13];
					needDrawTabArea = true;
					tabAreaAltered = true;
				} else if (clickInRegion(positionX[14] + offsetX
						+ secondOffsetX, secondPositionY, positionX[14]
								+ offsetX + secondOffsetX + 30, secondPositionY + 37)) {
					if (tabID == tab[14]) {
						showTab = !showTab;
					} else {
						showTab = true;
					}
					tabID = tab[14];
					needDrawTabArea = true;
					tabAreaAltered = true;
				} else if (clickInRegion(positionX[15] + offsetX
						+ secondOffsetX, secondPositionY, positionX[15]
								+ offsetX + secondOffsetX + 30, secondPositionY + 37)) {
					if (tabID == tab[15]) {
						showTab = !showTab;
					} else {
						showTab = true;
					}
					tabID = tab[15];
					needDrawTabArea = true;
					tabAreaAltered = true;
				} else if (clickInRegion(clientWidth - 21, 0, clientWidth, 21)) {
					if (tabID == 10) {
						showTab = !showTab;
					} else {
						showTab = true;
					}
					tabID = 10;
					needDrawTabArea = true;
					tabAreaAltered = true;
				}
			}
		}
	}

	public int MapX, MapY;
	public static int spellID = 0;
	public static boolean newDamage;
	public Sprite magicAuto;
	public boolean Autocast = false;
	public boolean xpLock;
	public int xpCounter;
	public int autocastId = 0;
	public int followPlayer = 0;
	public int followNPC = 0;
	public int followDistance = 1;
	public boolean downloading = false;
	public String clanName = "";
	public boolean buttonclicked = false;
	public String TalkingFix = "";
	public int GEItemId = 0;
	public Sprite Search;
	public Sprite search;

	public void magicOnItem(int id) {
		spellSelected = 1;
		spellID = id;
		anInt1137 = id;
		spellUsableOn = 16;
		itemSelected = 0;
		needDrawTabArea = true;
		spellTooltip = "Cast on";
		needDrawTabArea = true;
		tabID = 3;
		tabAreaAltered = true;
	}

	public final String methodR(int j) {
		if (j >= 0 && j < 10000)
			return String.valueOf(j);
		if (j >= 10000 && j < 10000000)
			return j / 1000 + "K";
		if (j >= 10000000 && j < 999999999)
			return j / 1000000 + "M";
		if (j >= 999999999)
			return "*";
		else
			return "?";
	}

	public static final byte[] ReadFile(String s) {
		try {
			byte abyte0[];
			File file = new File(s);
			int i = (int) file.length();
			abyte0 = new byte[i];
			DataInputStream datainputstream = new DataInputStream(
					new BufferedInputStream(new FileInputStream(s)));
			datainputstream.readFully(abyte0, 0, i);
			datainputstream.close();
			return abyte0;
		} catch (Exception e) {
			System.out.println((new StringBuilder()).append("Read Error: ")
					.append(s).toString());
			return null;
		}
	}

	public void maps() {
		for (int MapIndex = 0; MapIndex < 5174; MapIndex++) {
			byte[] abyte0 = getMap(MapIndex);
			if (abyte0 != null && abyte0.length > 0) {
				cacheIndices[4].method234(abyte0.length, abyte0, MapIndex);
			}
		}
	}

	public byte[] getMap(int Index) {
		try {
			File Map = new File("C:/Users/AJ/ExileCacheV3/Maps/" + Index
					+ ".dat.gz");
			byte[] aByte = new byte[(int) Map.length()];
			FileInputStream Fis = new FileInputStream(Map);
			Fis.read(aByte);
			System.out.println("" + Index + " aByte = [" + aByte + "]!");
			Fis.close();
			return aByte;
		} catch (Exception e) {
			return null;
		}
	}

	public void models() {
		for (int ModelIndex = 12000; ModelIndex < 70000; ModelIndex++) {
			byte[] abyte0 = getModel(ModelIndex);
			if (abyte0 != null && abyte0.length > 0) {
				cacheIndices[1].method234(abyte0.length, abyte0, ModelIndex);
			}
		}
	}

	public byte[] getModel(int Index) {
		try {
			File Model = new File(signlink.findcachedir() + "/Model/" + Index
					+ ".gz");
			byte[] aByte = new byte[(int) Model.length()];
			FileInputStream fis = new FileInputStream(Model);
			fis.read(aByte);
			System.out.println("Loaded model:" + Index);
			fis.close();
			return aByte;
		} catch (Exception e) {
			return null;
		}
	}

	public static boolean displayScrollbar;

	public void displayItemSearch() {
		int yPosOffset = (clientSize > 0) ? clientHeight - 165 : 0;
		int xPosOffset = 0;
		try {
			quickChat = false;
			if (amountOrNameInput != "") {
				itemSearch(amountOrNameInput);
			}
			cacheSprite[64].drawSprite(0 + xPosOffset, 0 + yPosOffset);
			DrawingArea.setDrawingArea(121 + yPosOffset, 8, 512, 7);
			search.drawSprite(18, 18 + yPosOffset);
			for (int j = 0; j < totalItemResults; j++) {
				int x = super.mouseX;
				int y = super.mouseY;
				final int yPos = 21 + j * 14 - itemResultScrollPos;
				if (yPos > 0 && yPos < 210) {
					String n = itemResultNames[j];
					for (int i = 0; i <= 20; i++)
						if (n.contains("<img=" + i + ">"))
							n = n.replaceAll("<img=" + i + ">", "");
					chatTextDrawingArea.method591(capitalizeFirstChar(n), 78,
							0xA05A00, yPos + yPosOffset
							+ (totalItemResults < 8 ? 6 : 0));
					if (x > 74
							&& x < 495
							&& y > ((clientSize == 0) ? 338
									: clientHeight - 165)
									+ yPos
									- 13
									+ (totalItemResults < 8 ? 6 : 0)
									&& y < ((clientSize == 0) ? 338
											: clientHeight - 165)
											+ yPos
											+ 2
											+ (totalItemResults < 8 ? 6 : 0)) {
						DrawingArea.method335(0x807660, yPos - 12 + yPosOffset
								+ (totalItemResults < 8 ? 6 : 0), 424, 15, 60,
								75);
						Sprite itemImg = ItemDef.getSprite(itemResultIDs[j], 1,
								0);
						if (itemImg != null)
							itemImg.drawSprite(22, 20 + yPosOffset);
						GEItemId = itemResultIDs[j];
					}
				}
			}
			DrawingArea.drawPixels(113, 8 + yPosOffset, 74, 0x807660, 2);
			DrawingArea.defaultDrawingAreaSize();
			if (totalItemResults > 8) {
				displayScrollbar = true;
				drawScrollbar(114, itemResultScrollPos, 8 + yPosOffset,
						496 + xPosOffset, totalItemResults * 14, false, false);
				// drawScrollbar(112, itemResultScrollPos, 8, 496,
				// totalItemResults * 14 + 12, 0);
			} else {
				displayScrollbar = false;
			}
			boolean showMatches = true;
			showMatches = true;
			if (amountOrNameInput.length() == 0) {
				chatTextDrawingArea.method382(0xA05A00, 259,
						"Grand Exchange Item Search", 30 + yPosOffset, false);
				smallText
				.method382(
						0xA05A00,
						259,
						"To search for an item, start by typing part of it's name.",
						80 + yPosOffset, false);
				smallText
				.method382(
						0xA05A00,
						259,
						"Then, simply select the item you want from the results on the display.",
						80 + 15 + yPosOffset, false);
				// chatTextDrawingArea.drawText(0xffffff, amountOrNameInput +
				// "*", 32, 133);
				showMatches = false;
			}
			if (totalItemResults == 0 && showMatches) {
				smallText.method382(0xA05A00, 259, "No matching items found",
						80 + yPosOffset, false);
			}
			DrawingArea.method335(0x807660, 121 + yPosOffset, 506, 15, 120, 7);// box
			// chatTextDrawingArea.drawText(0, "<img=8>", 133, 12);
			chatTextDrawingArea.method591(amountOrNameInput + "*",
					28 + xPosOffset, 0xffffff, 133 + yPosOffset);
			// chatTextDrawingArea.drawText(0xffffff, amountOrNameInput + "*",
			// 133, 122);
			DrawingArea.method339(121 + yPosOffset, 0x807660, 506, 7);// line
			// drawClose(496, 122, 496, 345 + 112, 496 + 19, 361 + 112);
			// drawClose(496, 122, 496, 345 + 112, 496 + 19, 361 + 112);
			// drawClose(496, 122, 496, 345 + 112, 496 + 19, 361 + 112);
			Search.drawSprite(11, 122 + yPosOffset);
		} catch (Exception e) {

		}
	}

	public void drawClose(int x, int y, int x2, int y2, int x3, int y3) {
		cacheSprite[31].drawSprite(x, y);
		if (super.mouseX >= x2 && super.mouseX <= x3 && super.mouseY >= y2
				&& super.mouseY <= y3) {
			cacheSprite[32].drawSprite(x, y);
		}
	}

	public int interfaceButtonAction = 0;

	void sendPacket(int packet) {
		if (packet == 103) {
			stream.createFrame(103);
			stream.writeWordBigEndian(inputString.length() - 1);
			stream.writeString(inputString.substring(2));
			inputString = "";
			promptInput = "";
			interfaceButtonAction = 0;
		}
		if (packet == 1003) {
			stream.createFrame(103);
			inputString = "::" + inputString;
			stream.writeWordBigEndian(inputString.length() - 1);
			stream.writeString(inputString.substring(2));
			inputString = "";
			promptInput = "";
			interfaceButtonAction = 0;
		}
	}

	public void playSong(int id) {
		if (currentSong != id) {
			nextSong = id;
			songChanging = true;
			onDemandFetcher.requestFileData(2, nextSong);
			currentSong = id;
		}
	}

	public void stopMidi() {
		signlink.fadeMidi = 0;
		signlink.midi = "stop";
		try {
			signlink.music.stop();
		} catch (Exception e) {
		}
	}

	private void adjustVolume(boolean updateMidi, int volume) {
		try {
			signlink.setVolume(volume);
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		}
		if (updateMidi) {
			signlink.midi = "voladjust";
		}
	}

	private boolean menuHasAddFriend(int j) {
		if (j < 0)
			return false;
		int k = menuActionID[j];
		if (k >= 2000)
			k -= 2000;
		return k == 337;
	}

	private Sprite min = new Sprite("224");
	private int minY = 0, timer = 0;
	private boolean fin = true, up = false;

	private void method497(int w) {
		minY = 0;
		minY -= 140;
		timer = 0;
		fin = false;
		up = false;
	}

	public void method498() {
		if (!fin) {
			if (minY <= 10 && !up) {
				minY += 2;
			} else {
				up = true;
				timer++;
				if (timer > 30)
					minY -= 2;
			}
			if (minY == -145) {
				fin = true;
			}
			min.drawSprite(290, minY);
		}
	}

	public int getXPForLevel(int level) {
		int points = 0;
		int output = 0;
		for (int lvl = 1; lvl <= level; lvl++) {
			points += Math.floor(lvl + 300.0 * Math.pow(2.0, lvl / 7.0));
			if (lvl >= level) {
				return output;
			}
			output = (int) Math.floor(points / 4);
		}
		return 0;
	}

	public String[] skillNames = { "Attack", "Hitpoints", "Mining", "Strength",
			"Agility", "Smithing", "Defence", "Herblore", "Fishing", "Range",
			"Thieving", "Cooking", "Prayer", "Crafting", "Firemaking", "Magic",
			"Fletching", "Woodcutting", "Runecrafting", "Slayer", "Farming",
			"Construction", "Hunter", "Summoning", "Dungeoneering" };

	public String setMessage(int level) {
		String[] messages = new String[4];
		String message = "";
		int[] stuff = { 0, 3, 14, 2, 16, 13, 1, 15, 10, 4, 17, 7, 5, 12, 11, 6,
				9, 8, 20, 18, 19, 21, 22, 23, 24 };
		messages[0] = skillNames[level] + ": " + currentStats[stuff[level]]
				+ "/" + maxStats[stuff[level]] + "\\n";
		messages[1] = "Current XP: "
				+ String.format("%, d", currentExp[stuff[level]]) + "\\n";
		messages[2] = "Remainder: "
				+ String.format(
						"%, d",
						(getXPForLevel(maxStats[stuff[level]] + 1) - getXPForLevel(maxStats[stuff[level]])))
						+ "\\n";
		messages[3] = "Next level: "
				+ String.format("%, d",
						getXPForLevel(maxStats[stuff[level]] + 1));
		message = messages[0] + messages[1] + messages[2] + messages[3];
		if (maxStats[stuff[level]] >= 99) {
			message = messages[0] + messages[1];
		}
		return message;
	}

	public void init() {
		try {
			nodeID = 10;
			portOff = 0;
			setHighMem();
			isMembers = true;
			signlink.storeid = 32;
			signlink.startpriv(InetAddress.getLocalHost());
			initClientFrame(503, 765);
			instance = this;
		} catch (Exception exception) {
			return;
		}
	}

	public void startRunnable(Runnable runnable, int i) {
		if (i > 10)
			i = 10;
		if (signlink.mainapp != null) {
			signlink.startthread(runnable, i);
		} else {
			super.startRunnable(runnable, i);
		}
	}

	public Socket openSocket(int port) throws IOException {
		return new Socket(InetAddress.getByName(Configuration.server), port);
	}

	public String indexLocation(int cacheIndex, int index) {
		return signlink.findcachedir() + "index" + cacheIndex + "/"
				+ (index != -1 ? index + ".gz" : "");
	}

	public void repackCacheIndex(int cacheIndex) {
		System.out.println("Started repacking index " + cacheIndex + ".");
		int indexLength = new File(indexLocation(cacheIndex, -1)).listFiles().length;
		File[] file = new File(indexLocation(cacheIndex, -1)).listFiles();
		try {
			for (int index = 0; index < indexLength; index++) {
				int fileIndex = Integer
						.parseInt(getFileNameWithoutExtension(file[index]
								.toString()));
				byte[] data = fileToByteArray(cacheIndex, fileIndex);
				if (data != null && data.length > 0) {
					cacheIndices[cacheIndex].method234(data.length, data,
							fileIndex);
					System.out.println("Repacked " + fileIndex + ".");
				} else {
					System.out.println("Unable to locate index " + fileIndex
							+ ".");
				}
			}
		} catch (Exception e) {
			System.out.println("Error packing cache index " + cacheIndex + ".");
		}
		System.out.println("Finished repacking " + cacheIndex + ".");
	}

	public byte[] fileToByteArray(int cacheIndex, int index) {
		try {
			if (indexLocation(cacheIndex, index).length() <= 0
					|| indexLocation(cacheIndex, index) == null) {
				return null;
			}
			File file = new File(indexLocation(cacheIndex, index));
			byte[] fileData = new byte[(int) file.length()];
			FileInputStream fis = new FileInputStream(file);
			fis.read(fileData);
			fis.close();
			return fileData;
		} catch (Exception e) {
			return null;
		}
	}

	private boolean processMenuClick() {
		if (activeInterfaceType != 0)
			return false;
		int j = super.clickMode3;
		if (spellSelected == 1 && super.saveClickX >= 516
				&& super.saveClickY >= 160 && super.saveClickX <= 765
				&& super.saveClickY <= 205)
			j = 0;
		if (menuOpen) {
			if (j != 1) {
				int k = super.mouseX;
				int j1 = super.mouseY;
				if (menuScreenArea == 0) {
					k -= clientSize == 0 ? 4 : 0;
					j1 -= clientSize == 0 ? 4 : 0;
				}
				if (menuScreenArea == 1) {
					k -= 519;
					j1 -= 168;
				}
				if (menuScreenArea == 2) {
					k -= 17;
					j1 -= 338;
				}
				if (menuScreenArea == 3) {
					k -= 519;
					j1 -= 0;
				}
				if (k < menuOffsetX - 10 || k > menuOffsetX + menuWidth + 10
						|| j1 < menuOffsetY - 10
						|| j1 > menuOffsetY + menuHeight + 10) {
					menuOpen = false;
					if (menuScreenArea == 1)
						needDrawTabArea = true;
					if (menuScreenArea == 2)
						inputTaken = true;
				}
			}
			if (j == 1) {
				int l = menuOffsetX;
				int k1 = menuOffsetY;
				int i2 = menuWidth;
				int k2 = super.saveClickX;
				int l2 = super.saveClickY;
				if (menuScreenArea == 0) {
					k2 -= clientSize == 0 ? 4 : 0;
					l2 -= clientSize == 0 ? 4 : 0;
				}
				if (menuScreenArea == 1) {
					k2 -= 519;
					l2 -= 168;
				}
				if (menuScreenArea == 2) {
					k2 -= 17;
					l2 -= 338;
				}
				if (menuScreenArea == 3) {
					k2 -= 519;
					l2 -= 0;
				}
				int i3 = -1;
				for (int j3 = 0; j3 < menuActionRow; j3++) {
					int k3 = k1 + 31 + (menuActionRow - 1 - j3) * 15;
					if (k2 > l && k2 < l + i2 && l2 > k3 - 13 && l2 < k3 + 3)
						i3 = j3;
				}
				// System.out.println(i3);
				if (i3 != -1)
					doAction(i3);
				menuOpen = false;
				if (menuScreenArea == 1)
					needDrawTabArea = true;
				if (menuScreenArea == 2) {
					inputTaken = true;
				}
			}
			return true;
		} else {
			if (j == 1 && menuActionRow > 0) {
				int i1 = menuActionID[menuActionRow - 1];
				if (i1 == 632 || i1 == 78 || i1 == 867 || i1 == 431 || i1 == 53
						|| i1 == 74 || i1 == 454 || i1 == 539 || i1 == 493
						|| i1 == 847 || i1 == 447 || i1 == 1125) {
					int l1 = menuActionCmd2[menuActionRow - 1];
					int j2 = menuActionCmd3[menuActionRow - 1];
					RSInterface rsi = RSInterface.interfaceCache[j2];
					if (rsi.aBoolean259 || rsi.aBoolean235) {
						aBoolean1242 = false;
						anInt989 = 0;
						anInt1084 = j2;
						anInt1085 = l1;
						activeInterfaceType = 2;
						anInt1087 = super.saveClickX;
						anInt1088 = super.saveClickY;
						if (RSInterface.interfaceCache[j2].parentID == openInterfaceID)
							activeInterfaceType = 1;
						if (RSInterface.interfaceCache[j2].parentID == backDialogID)
							activeInterfaceType = 3;
						return true;
					}
				}
			}
			if (j == 1
					&& (anInt1253 == 1 || menuHasAddFriend(menuActionRow - 1))
					&& menuActionRow > 2)
				j = 2;
			if (j == 1 && menuActionRow > 0)
				doAction(menuActionRow - 1);
			if (j == 2 && menuActionRow > 0)
				determineMenuSize();
			return false;
		}
	}

	public static int totalRead = 0;

	public static String getFileNameWithoutExtension(String fileName) {
		File tmpFile = new File(fileName);
		tmpFile.getName();
		int whereDot = tmpFile.getName().lastIndexOf('.');
		if (0 < whereDot && whereDot <= tmpFile.getName().length() - 2) {
			return tmpFile.getName().substring(0, whereDot);
		}
		return "";
	}

	public void preloadModels() {
		try {
			File file = new File(signlink.findcachedir() + "Raw/");
			File[] fileArray = file.listFiles();
			for (int y = 0; y < fileArray.length; y++) {
				String s = fileArray[y].getName();
				byte[] buffer = ReadFile(signlink.findcachedir() + "Raw/" + s);
				Model.method460(buffer,
						Integer.parseInt(getFileNameWithoutExtension(s)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void saveMidi(boolean flag, byte abyte0[]) {
		signlink.fadeMidi = flag ? 1 : 0;
		signlink.midisave(abyte0, abyte0.length);
	}

	private void method22() {
		try {
			anInt985 = -1;
			aClass19_1056.clear();
			aClass19_1013.clear();
			Texture.clearTextureCache();
			unlinkMRUNodes();
			worldController.initToNull();
			System.gc();
			for (int i = 0; i < 4; i++)
				aClass11Array1230[i].method210();

			for (int l = 0; l < 4; l++) {
				for (int k1 = 0; k1 < 104; k1++) {
					for (int j2 = 0; j2 < 104; j2++)
						byteGroundArray[l][k1][j2] = 0;

				}

			}

			ObjectManager objectManager = new ObjectManager(byteGroundArray,
					intGroundArray);
			int k2 = aByteArrayArray1183.length;
			if (loggedIn)
				stream.createFrame(0);
			if (!aBoolean1159) {
				for (int i3 = 0; i3 < k2; i3++) {
					int i4 = (anIntArray1234[i3] >> 8) * 64 - baseX;
					int k5 = (anIntArray1234[i3] & 0xff) * 64 - baseY;
					byte abyte0[] = aByteArrayArray1183[i3];
					if (FileOperations.FileExists(signlink.findcachedir()
							+ "maps/" + anIntArray1235[i3] + ".dat"))
						abyte0 = FileOperations.ReadFile(signlink
								.findcachedir()
								+ "maps/"
								+ anIntArray1235[i3]
										+ ".dat");
					// System.out.println("Floor maps: "+anIntArray1235[i3]);
					if (abyte0 != null)
						objectManager.method180(abyte0, k5, i4,
								(anInt1069 - 6) * 8, (anInt1070 - 6) * 8,
								aClass11Array1230);
				}

				for (int j4 = 0; j4 < k2; j4++) {
					int l5 = (anIntArray1234[j4] >> 8) * 64 - baseX;
					int k7 = (anIntArray1234[j4] & 0xff) * 64 - baseY;
					byte abyte2[] = aByteArrayArray1183[j4];
					if (abyte2 == null && anInt1070 < 800)
						objectManager.method174(k7, 64, 64, l5);
				}

				anInt1097++;
				if (anInt1097 > 160) {
					anInt1097 = 0;
					stream.createFrame(238);
					stream.writeWordBigEndian(96);
				}
				if (loggedIn)
					stream.createFrame(0);
				for (int i6 = 0; i6 < k2; i6++) {
					byte abyte1[] = aByteArrayArray1247[i6];
					if (abyte1 != null) {
						// System.out.println("Object maps: "+anIntArray1236[i6]);
						int l8 = (anIntArray1234[i6] >> 8) * 64 - baseX;
						int k9 = (anIntArray1234[i6] & 0xff) * 64 - baseY;
						objectManager.method190(l8, aClass11Array1230, k9,
								worldController, abyte1);
					}
				}

			}
			if (aBoolean1159) {
				for (int j3 = 0; j3 < 4; j3++) {
					for (int k4 = 0; k4 < 13; k4++) {
						for (int j6 = 0; j6 < 13; j6++) {
							int l7 = anIntArrayArrayArray1129[j3][k4][j6];
							if (l7 != -1) {
								int i9 = l7 >> 24 & 3;
						int l9 = l7 >> 1 & 3;
				int j10 = l7 >> 14 & 0x3ff;
			int l10 = l7 >> 3 & 0x7ff;
		int j11 = (j10 / 8 << 8) + l10 / 8;
		for (int l11 = 0; l11 < anIntArray1234.length; l11++) {
			if (anIntArray1234[l11] != j11
					|| aByteArrayArray1183[l11] == null)
				continue;
			objectManager.method179(i9, l9,
					aClass11Array1230, k4 * 8,
					(j10 & 7) * 8,
					aByteArrayArray1183[l11],
					(l10 & 7) * 8, j3, j6 * 8);
			break;
		}

							}
						}

					}

				}

				for (int l4 = 0; l4 < 13; l4++) {
					for (int k6 = 0; k6 < 13; k6++) {
						int i8 = anIntArrayArrayArray1129[0][l4][k6];
						if (i8 == -1)
							objectManager.method174(k6 * 8, 8, 8, l4 * 8);
					}

				}
				if (loggedIn)
					stream.createFrame(0);
				for (int l6 = 0; l6 < 4; l6++) {
					for (int j8 = 0; j8 < 13; j8++) {
						for (int j9 = 0; j9 < 13; j9++) {
							int i10 = anIntArrayArrayArray1129[l6][j8][j9];
							if (i10 != -1) {
								int k10 = i10 >> 24 & 3;
						int i11 = i10 >> 1 & 3;
				int k11 = i10 >> 14 & 0x3ff;
				int i12 = i10 >> 3 & 0x7ff;
		int j12 = (k11 / 8 << 8) + i12 / 8;
		for (int k12 = 0; k12 < anIntArray1234.length; k12++) {
			if (anIntArray1234[k12] != j12
					|| aByteArrayArray1247[k12] == null)
				continue;
			if (FileOperations.FileExists(signlink
					.findcachedir()
					+ "maps/"
					+ anIntArray1235[k12] + ".dat"))
				FileOperations.ReadFile(signlink
						.findcachedir()
						+ "maps/"
						+ anIntArray1235[k12] + ".dat");
			objectManager.method183(aClass11Array1230,
					worldController, k10, j8 * 8,
					(i12 & 7) * 8, l6,
					aByteArrayArray1247[k12],
					(k11 & 7) * 8, i11, j9 * 8);
			break;
		}

							}
						}

					}

				}

			}
			if (loggedIn)
				stream.createFrame(0);
			objectManager.method171(aClass11Array1230, worldController);
			if (loggedIn)
				gameScreenIP.initDrawingArea();
			if (loggedIn)
				stream.createFrame(0);
			int k3 = ObjectManager.anInt145;
			if (k3 > plane)
				k3 = plane;
			if (k3 < plane - 1)
				k3 = plane - 1;
			if (lowMem)
				worldController.method275(ObjectManager.anInt145);
			else
				worldController.method275(0);
			for (int i5 = 0; i5 < 104; i5++) {
				for (int i7 = 0; i7 < 104; i7++)
					spawnGroundItem(i5, i7);

			}

			anInt1051++;
			if (anInt1051 > 98) {
				anInt1051 = 0;
				stream.createFrame(150);
			}
			method63();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		ObjectDef.mruNodes1.unlinkAll();
		if (super.mainFrame != null && loggedIn) {
			stream.createFrame(210);
			stream.writeDWord(0x3f008edd);
		}
		System.gc();
		Texture.method367();
		onDemandFetcher.method566();
		int k = (anInt1069 - 6) / 8 - 1;
		int j1 = (anInt1069 + 6) / 8 + 1;
		int i2 = (anInt1070 - 6) / 8 - 1;
		int l2 = (anInt1070 + 6) / 8 + 1;
		if (aBoolean1141) {
			k = 49;
			j1 = 50;
			i2 = 49;
			l2 = 50;
		}
		for (int l3 = k; l3 <= j1; l3++) {
			for (int j5 = i2; j5 <= l2; j5++)
				if (l3 == k || l3 == j1 || j5 == i2 || j5 == l2) {
					int j7 = onDemandFetcher.getMapCount(0, j5, l3);
					if (j7 != -1)
						onDemandFetcher.method560(j7, 3);
					int k8 = onDemandFetcher.getMapCount(1, j5, l3);
					if (k8 != -1)
						onDemandFetcher.method560(k8, 3);
				}

		}

	}

	private void unlinkMRUNodes() {
		ObjectDef.mruNodes1.unlinkAll();
		ObjectDef.mruNodes2.unlinkAll();
		EntityDef.mruNodes.unlinkAll();
		ItemDef.mruNodes2.unlinkAll();
		ItemDef.mruNodes1.unlinkAll();
		Player.mruNodes.unlinkAll();
		SpotAnim.aMRUNodes_415.unlinkAll();
	}

	public void renderedMapScene(int i) {
		int ai[] = miniMap.myPixels;
		int j = ai.length;
		for (int k = 0; k < j; k++)
			ai[k] = 0;

		for (int l = 1; l < 103; l++) {
			int i1 = 24628 + (103 - l) * 512 * 4;
			for (int k1 = 1; k1 < 103; k1++) {
				if ((byteGroundArray[i][k1][l] & 0x18) == 0)
					worldController.method309(ai, i1, i, k1, l);
				if (i < 3 && (byteGroundArray[i + 1][k1][l] & 8) != 0)
					worldController.method309(ai, i1, i + 1, k1, l);
				i1 += 4;
			}

		}

		int j1 = ((238 + (int) (Math.random() * 20D)) - 10 << 16)
				+ ((238 + (int) (Math.random() * 20D)) - 10 << 8)
				+ ((238 + (int) (Math.random() * 20D)) - 10);
		int l1 = (238 + (int) (Math.random() * 20D)) - 10 << 16;
		if (loggedIn)
			miniMap.method343();
		for (int i2 = 1; i2 < 103; i2++) {
			for (int j2 = 1; j2 < 103; j2++) {
				if ((byteGroundArray[i][j2][i2] & 0x18) == 0)
					method50(i2, j1, j2, l1, i);
				if (i < 3 && (byteGroundArray[i + 1][j2][i2] & 8) != 0)
					method50(i2, j1, j2, l1, i + 1);
			}

		}
		if (loggedIn)
			gameScreenIP.initDrawingArea();
		anInt1071 = 0;
		for (int k2 = 0; k2 < 104; k2++) {
			for (int l2 = 0; l2 < 104; l2++) {
				int i3 = worldController.fetchGroundDecorationNewUID(plane, k2,
						l2);
				if (i3 != 0) {
					i3 = i3 >> 14 & 0x7fff;
			int j3 = ObjectDef.forID(i3).mapFunctionID;
			if (j3 >= 0) {
				int k3 = k2;
				int l3 = l2;
				if (j3 != 22 && j3 != 29 && j3 != 34 && j3 != 36
						&& j3 != 46 && j3 != 47 && j3 != 48) {
					byte byte0 = 104;
					byte byte1 = 104;
					int ai1[][] = aClass11Array1230[plane].anIntArrayArray294;
					for (int i4 = 0; i4 < 10; i4++) {
						int j4 = (int) (Math.random() * 4D);
						if (j4 == 0 && k3 > 0 && k3 > k2 - 3
								&& (ai1[k3 - 1][l3] & 0x1280108) == 0)
							k3--;
						if (j4 == 1 && k3 < byte0 - 1 && k3 < k2 + 3
								&& (ai1[k3 + 1][l3] & 0x1280180) == 0)
							k3++;
						if (j4 == 2 && l3 > 0 && l3 > l2 - 3
								&& (ai1[k3][l3 - 1] & 0x1280102) == 0)
							l3--;
						if (j4 == 3 && l3 < byte1 - 1 && l3 < l2 + 3
								&& (ai1[k3][l3 + 1] & 0x1280120) == 0)
							l3++;
					}

				}
				aClass30_Sub2_Sub1_Sub1Array1140[anInt1071] = mapFunctions[j3];
				anIntArray1072[anInt1071] = k3;
				anIntArray1073[anInt1071] = l3;
				anInt1071++;
			}
				}
			}

		}

	}

	private void spawnGroundItem(int i, int j) {
		Deque class19 = groundArray[plane][i][j];
		if (class19 == null) {
			worldController.method295(plane, i, j);
			return;
		}
		int k = 0xfa0a1f01;
		Object obj = null;
		for (Item item = (Item) class19.getFront(); item != null; item = (Item) class19
				.reverseGetNext()) {
			ItemDef itemDef = ItemDef.forID(item.ID);
			int l = itemDef.value;
			if (itemDef.stackable)
				l *= item.anInt1559 + 1;
			if (l > k) {
				k = l;
				obj = item;
			}
		}

		class19.insertTail(((Node) (obj)));
		Object obj1 = null;
		Object obj2 = null;
		for (Item class30_sub2_sub4_sub2_1 = (Item) class19.getFront(); class30_sub2_sub4_sub2_1 != null; class30_sub2_sub4_sub2_1 = (Item) class19
				.reverseGetNext()) {
			if (class30_sub2_sub4_sub2_1.ID != ((Item) (obj)).ID
					&& obj1 == null)
				obj1 = class30_sub2_sub4_sub2_1;
			if (class30_sub2_sub4_sub2_1.ID != ((Item) (obj)).ID
					&& class30_sub2_sub4_sub2_1.ID != ((Item) (obj1)).ID
					&& obj2 == null)
				obj2 = class30_sub2_sub4_sub2_1;
		}

		int i1 = i + (j << 7) + 0x60000000;
		worldController.method281(i, i1, ((Animable) (obj1)),
				method42(plane, j * 128 + 64, i * 128 + 64),
				((Animable) (obj2)), ((Animable) (obj)), plane, j);
	}

	private void method26(boolean flag) {
		for (int j = 0; j < npcCount; j++) {
			NPC npc = npcArray[npcIndices[j]];
			int k = 0x20000000 + (npcIndices[j] << 14);
			if (npc == null || !npc.isVisible() || npc.desc.aBoolean93 != flag)
				continue;
			int l = npc.x >> 7;
			int i1 = npc.y >> 7;
		if (l < 0 || l >= 104 || i1 < 0 || i1 >= 104)
			continue;
		if (npc.anInt1540 == 1 && (npc.x & 0x7f) == 64
				&& (npc.y & 0x7f) == 64) {
			if (anIntArrayArray929[l][i1] == anInt1265)
				continue;
			anIntArrayArray929[l][i1] = anInt1265;
		}
		if (!npc.desc.aBoolean84)
			k += 0x80000000;
		worldController
		.method285(plane, npc.anInt1552,
				method42(plane, npc.y, npc.x), k, npc.y,
				(npc.anInt1540 - 1) * 64 + 60, npc.x, npc,
				npc.aBoolean1541);
		}
	}

	private void loadError() {
		String s = "ondemand";// was a constant parameter
		System.out.println(s);
		try {
			getAppletContext().showDocument(
					new URL(getCodeBase(), "loaderror_" + s + ".html"));
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		do
			try {
				Thread.sleep(1000L);
			} catch (Exception _ex) {
			}
		while (true);
	}

	public void drawHoverBox2(int xPos, int yPos, String text) {
		String[] results = text.split("\n");
		int height = (results.length * 16) + 6;
		int width;
		width = chatTextDrawingArea.getTextWidth(results[0]) + 6;
		for (int i = 1; i < results.length; i++)
			if (width <= chatTextDrawingArea.getTextWidth(results[i]) + 6)
				width = chatTextDrawingArea.getTextWidth(results[i]) + 6;
		DrawingArea.drawPixels(height + 2, yPos - 1, xPos - 1, 0xFFFFFF,
				width + 2);
		DrawingArea.drawPixels(height, yPos, xPos, 0x1E1F1F, width);
		yPos += 14;
		for (int i = 0; i < results.length; i++) {
			aTextDrawingArea_1271.method389(false, xPos + 15, 0xFFFFFF,
					results[i], yPos + 1);
			yPos += 16;
		}
	}

	public void drawTooltip(int x, int y, String[] text, boolean error, int info) {
		boolean verified = getRegister().verified[info];
		if (text != null || (error && verified)) {
			if (error && verified) {
				text = new String[]{ "This field is valid." };
			}
			int width = 0;
			for (int index = 0; index < text.length; index++) {
				if (newSmallFont.getTextWidth(text[index]) > width) {
					width = newSmallFont.getTextWidth(text[index]) + 10;
				}
			}
			int height = (text.length * 15) + 8;
			DrawingArea474.drawRoundedRectangle(x, y, width, height, 0, 150,
					true, false);
			for (int index = 0; index < text.length; index++) {
				newSmallFont.drawBasicString(text[index], x + 5, y + 15
						+ (15 * index), 0xffffff, 0);
			}
		}
	}

	public void drawHoverBox(int xPos, int yPos, String text) {
		String[] results = text.split("\n");
		int height = (results.length * 16) + 6;
		int width;
		width = chatTextDrawingArea.getTextWidth(results[0]) + 6;
		for (int i = 1; i < results.length; i++)
			if (width <= chatTextDrawingArea.getTextWidth(results[i]) + 6)
				width = chatTextDrawingArea.getTextWidth(results[i]) + 6;
		DrawingArea.drawPixels(height, yPos, xPos, 0xFFFFA0, width);
		DrawingArea.fillPixels(xPos, width, height, 0, yPos);
		yPos += 14;
		for (int i = 0; i < results.length; i++) {
			aTextDrawingArea_1271.method389(false, xPos + 15, 0, results[i],
					yPos + 1);
			yPos += 16;
		}
	}

	private int hoverSpriteId = -1;

	private void buildInterfaceMenu(int interfaceX, RSInterface class9,
			int mouseX, int interfaceY, int mouseY, int scrollOffset) {
		if (class9.type != 0 || class9.children == null
				|| class9.interfaceShown)
			return;
		if (mouseX < interfaceX || mouseY < interfaceY
				|| mouseX > interfaceX + class9.width
				|| mouseY > interfaceY + class9.height)
			return;
		int totalChildrens = class9.children.length;
		for (int frameID = 0; frameID < totalChildrens; frameID++) {
			int childX = class9.childX[frameID] + interfaceX;
			int childY = (class9.childY[frameID] + interfaceY) - scrollOffset;
			RSInterface child = RSInterface.interfaceCache[class9.children[frameID]];
			childX += child.xOffset;
			childY += child.yOffset;
			if ((child.hoverType >= 0 || child.anInt216 != 0)
					&& mouseX >= childX && mouseY >= childY
					&& mouseX < childX + child.width
					&& mouseY < childY + child.height) {
				if (child.hoverType >= 0) {
					hoveredInterface = child.hoverType;
					hoverSpriteId = hoveredInterface;
				} else {
					hoveredInterface = child.id;
					hoverSpriteId = hoveredInterface;
				}
			}
			if (child.type == 8 || child.type == 9 || child.type == 10
					&& mouseX >= childX && mouseY >= childY
					&& mouseX < childX + child.width
					&& mouseY < childY + child.height) {
				anInt1315 = child.id;
			}
			if (child.type == 0) {
				buildInterfaceMenu(childX, child, mouseX, childY, mouseY,
						child.scrollPosition);
				if (child.scrollMax > child.height)
					method65(childX + child.width, child.height, mouseX,
							mouseY, child, childY, true, child.scrollMax);
			} else {
				if (child.atActionType == 1 && mouseX >= childX
						&& mouseY >= childY && mouseX < childX + child.width
						&& mouseY < childY + child.height) {
					boolean flag = false;
					boolean flag1 = false;
					if (child.contentType != 0)
						flag = buildFriendsListMenu(child);
					if (child.tooltip.startsWith("[CC]")
							|| child.tooltip.startsWith("[GE]")) {
						flag1 = true;
						if (!child.tooltip.startsWith("[GE]"))
							clanName = RSInterface.interfaceCache[child.id - 800].message;
					}
					if (!flag && !flag1) {
						// System.out.println("1"+class9_1.tooltip + ", " +
						// class9_1.interfaceID);
						menuActionName[menuActionRow] = child.tooltip + ""/**
						 * , "
						 * + class9_1.id
						 **/
								;
						menuActionID[menuActionRow] = 315;
						menuActionCmd3[menuActionRow] = child.id;
						menuActionRow++;
					}
					if (flag1 && !child.tooltip.startsWith("[GE]")) {
						if (RSInterface.interfaceCache[child.id - 800].message != "") {
							menuActionName[menuActionRow] = "General";
							menuActionID[menuActionRow] = 1321;
							menuActionCmd3[menuActionRow] = child.id;
							menuActionRow++;
							menuActionName[menuActionRow] = "Captain";
							menuActionID[menuActionRow] = 1320;
							menuActionCmd3[menuActionRow] = child.id;
							menuActionRow++;
							menuActionName[menuActionRow] = "Lieutenant";
							menuActionID[menuActionRow] = 1319;
							menuActionCmd3[menuActionRow] = child.id;
							menuActionRow++;
							menuActionName[menuActionRow] = "Sergeant";
							menuActionID[menuActionRow] = 1318;
							menuActionCmd3[menuActionRow] = child.id;
							menuActionRow++;
							menuActionName[menuActionRow] = "Corporal";
							menuActionID[menuActionRow] = 1317;
							menuActionCmd3[menuActionRow] = child.id;
							menuActionRow++;
							menuActionName[menuActionRow] = "Recruit";
							menuActionID[menuActionRow] = 1316;
							menuActionCmd3[menuActionRow] = child.id;
							menuActionRow++;
							menuActionName[menuActionRow] = "Not ranked";
							menuActionID[menuActionRow] = 1315;
							menuActionCmd3[menuActionRow] = child.id;
							menuActionRow++;
						}
					}
				}
				if (child.atActionType == 2 && spellSelected == 0
						&& mouseX >= childX && mouseY >= childY
						&& mouseX < childX + child.width
						&& mouseY < childY + child.height) {
					String s = child.selectedActionName;
					if (s.indexOf(" ") != -1)
						s = s.substring(0, s.indexOf(" "));
					if (child.spellName.endsWith("Rush")
							|| child.spellName.endsWith("Burst")
							|| child.spellName.endsWith("Blitz")
							|| child.spellName.endsWith("Barrage")
							|| child.spellName.endsWith("strike")
							|| child.spellName.endsWith("bolt")
							|| child.spellName.equals("Crumble undead")
							|| child.spellName.endsWith("blast")
							|| child.spellName.endsWith("wave")
							|| child.spellName.equals("Claws of Guthix")
							|| child.spellName.equals("Flames of Zamorak")
							|| child.spellName.equals("Magic Dart")) {
						menuActionName[menuActionRow] = "Autocast @gre@"
								+ child.spellName;
						menuActionID[menuActionRow] = 104;
						menuActionCmd3[menuActionRow] = child.id;
						menuActionRow++;
					}
					menuActionName[menuActionRow] = s + " @gre@"
							+ child.spellName;
					menuActionID[menuActionRow] = 626;
					menuActionCmd3[menuActionRow] = child.id;
					menuActionRow++;
				}
				if (child.atActionType == 3 && mouseX >= childX
						&& mouseY >= childY && mouseX < childX + child.width
						&& mouseY < childY + child.height) {
					menuActionName[menuActionRow] = "Close";
					menuActionID[menuActionRow] = 200;
					menuActionCmd3[menuActionRow] = child.id;
					menuActionRow++;
				}
				if (child.atActionType == 4 && mouseX >= childX
						&& mouseY >= childY && mouseX < childX + child.width
						&& mouseY < childY + child.height) {
					// System.out.println("2"+class9_1.tooltip + ", " +
					// class9_1.interfaceID);
					menuActionName[menuActionRow] = child.tooltip + ", "
							+ child.id;
					menuActionID[menuActionRow] = 169;
					menuActionCmd3[menuActionRow] = child.id;
					menuActionRow++;
					if (child.hoverText != null) {
						// drawHoverBox(k, l, class9_1.hoverText);
						// System.out.println("DRAWING INTERFACE: " +
						// class9_1.hoverText);
					}
				}
				if (child.atActionType == 5 && mouseX >= childX
						&& mouseY >= childY && mouseX < childX + child.width
						&& mouseY < childY + child.height) {
					menuActionName[menuActionRow] = child.tooltip
							+ ((myRights != 0) ? ", @gre@(@whi@" + child.id
									+ "@gre@)" : "");
					menuActionID[menuActionRow] = 646;
					menuActionCmd3[menuActionRow] = child.id;
					menuActionRow++;
				}
				if (child.atActionType == 6 && !aBoolean1149
						&& mouseX >= childX && mouseY >= childY
						&& mouseX < childX + child.width
						&& mouseY < childY + child.height) {
					menuActionName[menuActionRow] = child.tooltip + ", "
							+ child.id;
					menuActionID[menuActionRow] = 679;
					menuActionCmd3[menuActionRow] = child.id;
					menuActionRow++;
				}
				if (child.type == 2) {
					int k2 = 0;
					for (int l2 = 0; l2 < child.height; l2++) {
						for (int i3 = 0; i3 < child.width; i3++) {
							int j3 = childX + i3 * (32 + child.invSpritePadX);
							int k3 = childY + l2 * (32 + child.invSpritePadY);
							if (k2 < 20) {
								j3 += child.spritesX[k2];
								k3 += child.spritesY[k2];
							}
							if (mouseX >= j3 && mouseY >= k3
									&& mouseX < j3 + 32 && mouseY < k3 + 32) {
								mouseInvInterfaceIndex = k2;
								lastActiveInvInterface = child.id;
								if (child.inv[k2] > 0) {
									ItemDef itemDef = ItemDef
											.forID(child.inv[k2] - 1);
									if (itemSelected == 1
											&& child.isInventoryInterface) {
										if (child.id != anInt1284
												|| k2 != anInt1283) {
											menuActionName[menuActionRow] = "Use "
													+ selectedItemName
													+ " with @lre@"
													+ itemDef.name;
											menuActionID[menuActionRow] = 870;
											menuActionCmd1[menuActionRow] = itemDef.ID;
											menuActionCmd2[menuActionRow] = k2;
											menuActionCmd3[menuActionRow] = child.id;
											menuActionRow++;
										}
									} else if (spellSelected == 1
											&& child.isInventoryInterface) {
										if ((spellUsableOn & 0x10) == 16) {
											menuActionName[menuActionRow] = spellTooltip
													+ " @lre@" + itemDef.name;
											menuActionID[menuActionRow] = 543;
											menuActionCmd1[menuActionRow] = itemDef.ID;
											menuActionCmd2[menuActionRow] = k2;
											menuActionCmd3[menuActionRow] = child.id;
											menuActionRow++;
										}
									} else {
										if (child.isInventoryInterface) {
											for (int l3 = 4; l3 >= 3; l3--)
												if (itemDef.actions != null
												&& itemDef.actions[l3] != null) {
													if (openInterfaceID != 24700) {
														menuActionName[menuActionRow] = itemDef.actions[l3]
																+ " @lre@"
																+ itemDef.name;
														if (l3 == 3)
															menuActionID[menuActionRow] = 493;
														if (l3 == 4)
															menuActionID[menuActionRow] = 847;
														menuActionCmd1[menuActionRow] = itemDef.ID;
														menuActionCmd2[menuActionRow] = k2;
														menuActionCmd3[menuActionRow] = child.id;
														menuActionRow++;
													}
												} else if (l3 == 4) {
													if (openInterfaceID != 24700) {
														menuActionName[menuActionRow] = "Drop @lre@"
																+ itemDef.name;
														menuActionID[menuActionRow] = 847;
														menuActionCmd1[menuActionRow] = itemDef.ID;
														menuActionCmd2[menuActionRow] = k2;
														menuActionCmd3[menuActionRow] = child.id;
														menuActionRow++;
													}
												}

										}
										if (child.usableItemInterface) {
											if (openInterfaceID == 24700) {
												menuActionName[menuActionRow] = "Offer @lre@"
														+ itemDef.name;
												menuActionID[menuActionRow] = 454;
												menuActionCmd1[menuActionRow] = itemDef.ID;
											} else {
												menuActionName[menuActionRow] = "Use @lre@"
														+ itemDef.name;
												menuActionID[menuActionRow] = 447;
												menuActionCmd1[menuActionRow] = itemDef.ID;
											}
											// k2 = inventory spot
											// System.out.println(k2);
											menuActionCmd2[menuActionRow] = k2;
											menuActionCmd3[menuActionRow] = child.id;
											menuActionRow++;
										}
										if (child.isInventoryInterface
												&& itemDef.actions != null) {
											for (int i4 = 2; i4 >= 0; i4--)
												if (itemDef.actions[i4] != null) {
													if (openInterfaceID != 24700) {
														menuActionName[menuActionRow] = itemDef.actions[i4]
																+ " @lre@"
																+ itemDef.name;
														if (i4 == 0)
															menuActionID[menuActionRow] = 74;
														if (i4 == 1)
															menuActionID[menuActionRow] = 454;
														if (i4 == 2)
															menuActionID[menuActionRow] = 539;
														menuActionCmd1[menuActionRow] = itemDef.ID;
														menuActionCmd2[menuActionRow] = k2;
														menuActionCmd3[menuActionRow] = child.id;
														menuActionRow++;
													}
												}

										}
										boolean m = false;
										m = false;
										if (child.actions != null) {
											for (int j4 = 4; j4 >= 0; j4--)
												if (child.actions[j4] != null) {
													if (child.actions[j4] != "[GE]") {
														if (child.actions[j4] != "[ITEM]Collect"
																&& child.actions[j4] != "[COINS]Collect") {
															menuActionName[menuActionRow] = child.actions[j4]
																	+ " @lre@"
																	+ itemDef.name;
															if (j4 == 0)
																menuActionID[menuActionRow] = 632;
															if (j4 == 1)
																menuActionID[menuActionRow] = 78;
															if (j4 == 2)
																menuActionID[menuActionRow] = 867;
															if (j4 == 3)
																menuActionID[menuActionRow] = 431;
															if (j4 == 4)
																menuActionID[menuActionRow] = 53;
															menuActionCmd1[menuActionRow] = itemDef.ID;
															menuActionCmd2[menuActionRow] = k2;
															menuActionCmd3[menuActionRow] = child.id;
															menuActionRow++;
														} else {
															menuActionName[menuActionRow] = "Collect @lre@"
																	+ itemDef.name;

															if (openInterfaceID == 54700) {
																if (child.actions[j4]
																		.startsWith("[ITEM]")) {
																	menuActionID[menuActionRow] = 889;
																} else if (child.actions[j4]
																		.startsWith("[COINS]")) {
																	menuActionID[menuActionRow] = 888;
																}
															}
															if (openInterfaceID == 53700) {
																if (child.actions[j4]
																		.startsWith("[ITEM]")) {
																	menuActionID[menuActionRow] = 890;
																} else if (child.actions[j4]
																		.startsWith("[COINS]")) {
																	menuActionID[menuActionRow] = 891;
																}
															}
															menuActionName[menuActionRow] = "Collect @lre@"
																	+ itemDef.name;
															menuActionCmd1[menuActionRow] = itemDef.ID;
															menuActionCmd2[menuActionRow] = k2;
															menuActionCmd3[menuActionRow] = child.id;
															menuActionRow++;
														}
													} else {
														m = true;
													}
												}

										}
										// menuActionName[menuActionRow] =
										// "Examine @lre@" + itemDef.name +
										// " @gre@(@whi@" + (class9_1.inv[k2] -
										// 1) + "@gre@)";
										if (m != true) {
											menuActionName[menuActionRow] = "Examine @lre@"
													+ itemDef.name;
											menuActionID[menuActionRow] = 1125;
											menuActionCmd1[menuActionRow] = itemDef.ID;
											menuActionCmd2[menuActionRow] = k2;
											menuActionCmd3[menuActionRow] = child.id;
											menuActionRow++;
										}
									}
								}
							}
							k2++;
						}

					}

				}
			}
		}

	}

	private void drawScrollbar(int barHeight, int scrollPos, int yPos,
			int xPos, int contentHeight, boolean newScroller,
			boolean isTransparent) {
		int backingAmount = (barHeight - 32) / 5;
		int scrollPartHeight = ((barHeight - 32) * barHeight) / contentHeight;
		int scrollerID;
		if (newScroller) {
			scrollerID = 4;
		} else if (isTransparent) {
			scrollerID = 8;
		} else {
			scrollerID = 0;
		}
		if (scrollPartHeight < 10)
			scrollPartHeight = 10;
		int scrollPartAmount = (scrollPartHeight / 5) - 2;
		int scrollPartPos = ((barHeight - 32 - scrollPartHeight) * scrollPos)
				/ (contentHeight - barHeight) + 16 + yPos;
		/* Bar fill */
		for (int i = 0, yyPos = yPos + 16; i <= backingAmount; i++, yyPos += 5) {
			scrollPart[scrollerID + 1].drawSprite(xPos, yyPos);
		}
		/* Top of bar */
		scrollPart[scrollerID + 2].drawSprite(xPos, scrollPartPos);
		scrollPartPos += 5;
		/* Middle of bar */
		for (int i = 0; i <= scrollPartAmount; i++) {
			scrollPart[scrollerID + 3].drawSprite(xPos, scrollPartPos);
			scrollPartPos += 5;
		}
		scrollPartPos = ((barHeight - 32 - scrollPartHeight) * scrollPos)
				/ (contentHeight - barHeight) + 16 + yPos
				+ (scrollPartHeight - 5);
		/* Bottom of bar */
		scrollPart[scrollerID].drawSprite(xPos, scrollPartPos);
		/* Arrows */
		if (newScroller) {
			scrollBar[2].drawSprite(xPos, yPos);
			scrollBar[3].drawSprite(xPos, (yPos + barHeight) - 16);
		} else if (isTransparent) {
			scrollBar[4].drawSprite(xPos, yPos);
			scrollBar[5].drawSprite(xPos, (yPos + barHeight) - 16);
		} else {
			scrollBar[0].drawSprite(xPos, yPos);
			scrollBar[1].drawSprite(xPos, (yPos + barHeight) - 16);
		}
	}

	private void updateNPCs(Stream stream, int i) {
		anInt839 = 0;
		anInt893 = 0;
		method139(stream);
		method46(i, stream);
		method86(stream);
		for (int k = 0; k < anInt839; k++) {
			// System.out.println("called");
			int l = anIntArray840[k];
			if (npcArray[l].anInt1537 != loopCycle) {
				npcArray[l].desc = null;
				npcArray[l] = null;
			}
		}

		if (stream.currentOffset != i) {
			signlink.reporterror(myUsername
					+ " size mismatch in getnpcpos - pos:"
					+ stream.currentOffset + " psize:" + i);
			throw new RuntimeException("eek");
		}
		for (int i1 = 0; i1 < npcCount; i1++)
			if (npcArray[npcIndices[i1]] == null) {
				signlink.reporterror(myUsername
						+ " null entry in npc list - pos:" + i1 + " size:"
						+ npcCount);
				throw new RuntimeException("eek");
			}

	}

	private int cButtonHPos;
	private int cButtonCPos;
	private boolean menuToggle = true;

	private void handleActions(int configID) {
		int action = Varp.cache[configID].anInt709;
		if (action == 0)
			return;
		int config = variousSettings[configID];
		if (action == 1) {
			if (config == 1)
				Texture.calculatePalette(0.90000000000000002D);
			if (config == 2)
				Texture.calculatePalette(0.80000000000000004D);
			if (config == 3)
				Texture.calculatePalette(0.69999999999999996D);
			if (config == 4)
				Texture.calculatePalette(0.59999999999999998D);
			ItemDef.mruNodes1.unlinkAll();
			welcomeScreenRaised = true;
		}
		if (action == 3) {
			boolean music = musicEnabled;
			if (config == 0) {
				adjustVolume(musicEnabled, 500);
				musicEnabled = true;
			}
			if (config == 1) {
				adjustVolume(musicEnabled, 300);
				musicEnabled = true;
			}
			if (config == 2) {
				adjustVolume(musicEnabled, 100);
				musicEnabled = true;
			}
			if (config == 3) {
				adjustVolume(musicEnabled, 0);
				musicEnabled = true;
			}
			if (config == 4) {
				musicEnabled = false;
			}
			if (musicEnabled != music) {
				if (musicEnabled) {
					nextSong = currentSong;
					songChanging = true;
					onDemandFetcher.requestFileData(2, nextSong);
				} else {
					stopMidi();
				}
				prevSong = 0;
			}
		}
		if (action == 4) {
			SoundPlayer.setVolume(config);
			if (config == 0) {
				aBoolean848 = true;
				setWaveVolume(0);
			}
			if (config == 1) {
				aBoolean848 = true;
				setWaveVolume(-400);
			}
			if (config == 2) {
				aBoolean848 = true;
				setWaveVolume(-800);
			}
			if (config == 3) {
				aBoolean848 = true;
				setWaveVolume(-1200);
			}
			if (config == 4) {
				aBoolean848 = false;
			}
		}
		if (action == 5)
			anInt1253 = config;
		if (action == 6)
			anInt1249 = config;
		if (action == 7)
			running = !running;
		if (action == 8) {
			splitPrivateChat = config;
			inputTaken = true;
		}
		if (action == 9)
			anInt913 = config;
	}

	private void updateEntities() {
		try {
			int anInt974 = 0;
			for (int j = -1; j < playerCount + npcCount; j++) {
				Object obj;
				if (j == -1)
					obj = myPlayer;
				else if (j < playerCount)
					obj = playerArray[playerIndices[j]];
				else
					obj = npcArray[npcIndices[j - playerCount]];
				if (obj == null || !((Entity) (obj)).isVisible())
					continue;
				if (obj instanceof NPC) {
					EntityDef entityDef = ((NPC) obj).desc;
					if (entityDef.childrenIDs != null)
						entityDef = entityDef.method161();
					if (entityDef == null)
						continue;
				}
				if (j < playerCount) {
					int l = 30;
					Player player = (Player) obj;
					if (player.headIcon >= 0) {
						npcScreenPos(((Entity) (obj)),
								((Entity) (obj)).height + 15);
						if (spriteDrawX > -1) {
							if (player.skullIcon < 2) {
								skullIcons[player.skullIcon].drawSprite(
										spriteDrawX - 12, spriteDrawY - l);
								l += 24;
							}
							if (player.headIcon < 20) {
								headIcons[player.headIcon].drawSprite(
										spriteDrawX - 12, spriteDrawY - l);
								l += 18;
							}
						}
					}
					if (j >= 0 && anInt855 == 10
							&& anInt933 == playerIndices[j]) {
						npcScreenPos(((Entity) (obj)),
								((Entity) (obj)).height + 15);
						if (spriteDrawX > -1)
							headIconsHint[player.hintIcon].drawSprite(
									spriteDrawX - 12, spriteDrawY - l);
					}
				} else {
					EntityDef entityDef_1 = ((NPC) obj).desc;
					if (entityDef_1.anInt75 >= 0
							&& entityDef_1.anInt75 < headIcons.length) {
						npcScreenPos(((Entity) (obj)),
								((Entity) (obj)).height + 15);
						if (spriteDrawX > -1)
							headIcons[entityDef_1.anInt75].drawSprite(
									spriteDrawX - 12, spriteDrawY - 30);
					}
					if (anInt855 == 1
							&& anInt1222 == npcIndices[j - playerCount]
									&& loopCycle % 20 < 10) {
						npcScreenPos(((Entity) (obj)),
								((Entity) (obj)).height + 15);
						if (spriteDrawX > -1)
							headIconsHint[0].drawSprite(spriteDrawX - 12,
									spriteDrawY - 28);
					}
				}
				if (((Entity) (obj)).textSpoken != null
						&& (j >= playerCount || publicChatMode == 0
						|| publicChatMode == 3 || publicChatMode == 1
						&& isFriendOrSelf(((Player) obj).name))) {
					npcScreenPos(((Entity) (obj)), ((Entity) (obj)).height);
					if (spriteDrawX > -1 && anInt974 < anInt975) {
						anIntArray979[anInt974] = chatTextDrawingArea
								.method384(((Entity) (obj)).textSpoken) / 2;
						anIntArray978[anInt974] = chatTextDrawingArea.anInt1497;
						anIntArray976[anInt974] = spriteDrawX;
						anIntArray977[anInt974] = spriteDrawY;
						anIntArray980[anInt974] = ((Entity) (obj)).anInt1513;
						anIntArray981[anInt974] = ((Entity) (obj)).anInt1531;
						anIntArray982[anInt974] = ((Entity) (obj)).textCycle;
						aStringArray983[anInt974++] = ((Entity) (obj)).textSpoken;
						if (anInt1249 == 0 && ((Entity) (obj)).anInt1531 >= 1
								&& ((Entity) (obj)).anInt1531 <= 3) {
							anIntArray978[anInt974] += 10;
							anIntArray977[anInt974] += 5;
						}
						if (anInt1249 == 0 && ((Entity) (obj)).anInt1531 == 4)
							anIntArray979[anInt974] = 60;
						if (anInt1249 == 0 && ((Entity) (obj)).anInt1531 == 5)
							anIntArray978[anInt974] += 5;
					}
				}
				if (((Entity) (obj)).loopCycleStatus > loopCycle) {
					try {
						npcScreenPos(((Entity) (obj)),
								((Entity) (obj)).height + 15);
						if (spriteDrawX > -1) {
							int i1 = (((Entity) (obj)).currentHealth * 30)
									/ ((Entity) (obj)).maxHealth;
							if (i1 > 30)
								i1 = 30;
							int hpPercent = (((Entity) (obj)).currentHealth * 90)
									/ ((Entity) (obj)).maxHealth;
							if (hpPercent > 90)
								hpPercent = 90;
							int HpPercent = (((Entity) (obj)).currentHealth * 56)
									/ ((Entity) (obj)).maxHealth;
							if (HpPercent > 56)
								HpPercent = 56;
							HPBarEmpty.drawSprite(spriteDrawX - 28,
									spriteDrawY - 5);
							HPBarFull = new Sprite(signlink.findcachedir()
									+ "Sprites/Player/HP 0.png", HpPercent, 7);
							HPBarFull.drawSprite(spriteDrawX - 28,
									spriteDrawY - 5);
							if (((NPC) obj) != null
									&& ((NPC) obj).maxHealth >= 2500) {
								HPBarBigEmpty.drawSprite(spriteDrawX - 44,
										spriteDrawY - 5);
								HPBarFull = new Sprite(signlink.findcachedir()
										+ "Sprites/Player/HP 3.png", hpPercent,
										7);
								HPBarFull.drawSprite(spriteDrawX - 44,
										spriteDrawY - 5);
							}
						}
					} catch (Exception e) {
					}
				}
				for (int j1 = 0; j1 < 4; j1++)
					if (((Entity) (obj)).hitsLoopCycle[j1] > loopCycle) {
						npcScreenPos(((Entity) (obj)),
								((Entity) (obj)).height / 2);
						if (spriteDrawX > -1) {
							Entity e = ((Entity) (obj));
							if (e.moveTimer[j1] == 0) {
								if (e.hitmarkMove[j1] > -14)
									e.hitmarkMove[j1]--;
								e.moveTimer[j1] = 2;
							} else {
								e.moveTimer[j1]--;
							}
							if (e.hitmarkMove[j1] <= -14)
								e.hitmarkTrans[j1] -= 10;
							hitmarkDraw(e, String.valueOf(e.hitArray[j1])
									.length(), e.hitMarkTypes[j1],
									e.hitIcon[j1], e.hitArray[j1],
									e.soakDamage[j1], e.hitmarkMove[j1],
									e.hitmarkTrans[j1], j1);
						}
					}
			}
			for (int k = 0; k < anInt974; k++) {
				int k1 = anIntArray976[k];
				int l1 = anIntArray977[k];
				int j2 = anIntArray979[k];
				int k2 = anIntArray978[k];
				boolean flag = true;
				while (flag) {
					flag = false;
					for (int l2 = 0; l2 < k; l2++)
						if (l1 + 2 > anIntArray977[l2] - anIntArray978[l2]
								&& l1 - k2 < anIntArray977[l2] + 2
								&& k1 - j2 < anIntArray976[l2]
										+ anIntArray979[l2]
												&& k1 + j2 > anIntArray976[l2]
														- anIntArray979[l2]
																&& anIntArray977[l2] - anIntArray978[l2] < l1) {
							l1 = anIntArray977[l2] - anIntArray978[l2];
							flag = true;
						}

				}
				spriteDrawX = anIntArray976[k];
				spriteDrawY = anIntArray977[k] = l1;
				String s = aStringArray983[k];
				if (anInt1249 == 0) {
					int i3 = 0xffff00;
					if (anIntArray980[k] < 6)
						i3 = anIntArray965[anIntArray980[k]];
					if (anIntArray980[k] == 6)
						i3 = anInt1265 % 20 >= 10 ? 0xffff00 : 0xff0000;
						if (anIntArray980[k] == 7)
							i3 = anInt1265 % 20 >= 10 ? 65535 : 255;
							if (anIntArray980[k] == 8)
								i3 = anInt1265 % 20 >= 10 ? 0x80ff80 : 45056;
								if (anIntArray980[k] == 9) {
									int j3 = 150 - anIntArray982[k];
									if (j3 < 50)
										i3 = 0xff0000 + 1280 * j3;
									else if (j3 < 100)
										i3 = 0xffff00 - 0x50000 * (j3 - 50);
									else if (j3 < 150)
										i3 = 65280 + 5 * (j3 - 100);
								}
								if (anIntArray980[k] == 10) {
									int k3 = 150 - anIntArray982[k];
									if (k3 < 50)
										i3 = 0xff0000 + 5 * k3;
									else if (k3 < 100)
										i3 = 0xff00ff - 0x50000 * (k3 - 50);
									else if (k3 < 150)
										i3 = (255 + 0x50000 * (k3 - 100)) - 5 * (k3 - 100);
								}
								if (anIntArray980[k] == 11) {
									int l3 = 150 - anIntArray982[k];
									if (l3 < 50)
										i3 = 0xffffff - 0x50005 * l3;
									else if (l3 < 100)
										i3 = 65280 + 0x50005 * (l3 - 50);
									else if (l3 < 150)
										i3 = 0xffffff - 0x50000 * (l3 - 100);
								}
								if (anIntArray981[k] == 0) {
									chatTextDrawingArea.drawText(0, s, spriteDrawY + 1,
											spriteDrawX);
									chatTextDrawingArea.drawText(i3, s, spriteDrawY,
											spriteDrawX);
								}
								if (anIntArray981[k] == 1) {
									chatTextDrawingArea.method386(0, s, spriteDrawX,
											anInt1265, spriteDrawY + 1);
									chatTextDrawingArea.method386(i3, s, spriteDrawX,
											anInt1265, spriteDrawY);
								}
								if (anIntArray981[k] == 2) {
									chatTextDrawingArea.method387(spriteDrawX, s,
											anInt1265, spriteDrawY + 1, 0);
									chatTextDrawingArea.method387(spriteDrawX, s,
											anInt1265, spriteDrawY, i3);
								}
								if (anIntArray981[k] == 3) {
									chatTextDrawingArea.method388(150 - anIntArray982[k],
											s, anInt1265, spriteDrawY + 1, spriteDrawX, 0);
									chatTextDrawingArea.method388(150 - anIntArray982[k],
											s, anInt1265, spriteDrawY, spriteDrawX, i3);
								}
								if (anIntArray981[k] == 4) {
									int i4 = chatTextDrawingArea.method384(s);
									int k4 = ((150 - anIntArray982[k]) * (i4 + 100)) / 150;
									DrawingArea.setDrawingArea(334, spriteDrawX - 50,
											spriteDrawX + 50, 0);
									chatTextDrawingArea.method385(0, s, spriteDrawY + 1,
											(spriteDrawX + 50) - k4);
									chatTextDrawingArea.method385(i3, s, spriteDrawY,
											(spriteDrawX + 50) - k4);
									DrawingArea.defaultDrawingAreaSize();
								}
								if (anIntArray981[k] == 5) {
									int j4 = 150 - anIntArray982[k];
									int l4 = 0;
									if (j4 < 25)
										l4 = j4 - 25;
									else if (j4 > 125)
										l4 = j4 - 125;
									DrawingArea
									.setDrawingArea(spriteDrawY + 5, 0, 512,
											spriteDrawY
											- chatTextDrawingArea.anInt1497
											- 1);
									chatTextDrawingArea.drawText(0, s,
											spriteDrawY + 1 + l4, spriteDrawX);
									chatTextDrawingArea.drawText(i3, s, spriteDrawY + l4,
											spriteDrawX);
									DrawingArea.defaultDrawingAreaSize();
								}
				} else {
					chatTextDrawingArea.drawText(0, s, spriteDrawY + 1,
							spriteDrawX);
					chatTextDrawingArea.drawText(0xffff00, s, spriteDrawY,
							spriteDrawX);
				}
			}
		} catch (Exception e) {
		}
	}

	private void delFriend(long l) {
		try {
			if (l == 0L)
				return;
			for (int i = 0; i < friendsCount; i++) {
				if (friendsListAsLongs[i] != l)
					continue;
				friendsCount--;
				needDrawTabArea = true;
				inputString = "[DFR]" + friendsList[i];
				for (int j = i; j < friendsCount; j++) {
					friendsList[j] = friendsList[j + 1];
					friendsNodeIDs[j] = friendsNodeIDs[j + 1];
					friendsListAsLongs[j] = friendsListAsLongs[j + 1];
				}
				sendPacket(1003);
				stream.createFrame(215);
				stream.writeQWord(l);
				break;
			}
		} catch (RuntimeException runtimeexception) {
			signlink.reporterror("18622, " + false + ", " + l + ", "
					+ runtimeexception.toString());
			throw new RuntimeException();
		}
	}

	private void animateTexture(int j) {
		if (!lowMem) {
			if (Texture.textureLastUsed[17] >= j) {
				Background background = Texture.textureImages[17];
				int k = background.anInt1452 * background.anInt1453 - 1;
				// fire cape apparently?
				int j1 = background.anInt1452 * anInt945 * 2;
				byte abyte0[] = background.aByteArray1450;
				byte abyte3[] = aByteArray912;
				for (int i2 = 0; i2 <= k; i2++)
					abyte3[i2] = abyte0[i2 - j1 & k];

				background.aByteArray1450 = abyte3;
				aByteArray912 = abyte0;
				Texture.resetTexture(17);
				anInt854++;
				if (anInt854 > 1235) {
					anInt854 = 0;
					stream.createFrame(226);
					stream.writeWordBigEndian(0);
					int l2 = stream.currentOffset;
					stream.writeWord(58722);
					stream.writeWordBigEndian(240);
					stream.writeWord((int) (Math.random() * 65536D));
					stream.writeWordBigEndian((int) (Math.random() * 256D));
					if ((int) (Math.random() * 2D) == 0)
						stream.writeWord(51825);
					stream.writeWordBigEndian((int) (Math.random() * 256D));
					stream.writeWord((int) (Math.random() * 65536D));
					stream.writeWord(7130);
					stream.writeWord((int) (Math.random() * 65536D));
					stream.writeWord(61657);
					stream.writeBytes(stream.currentOffset - l2);
				}
			}
			if (Texture.textureLastUsed[24] >= j) {
				Background background_1 = Texture.textureImages[24];
				int l = background_1.anInt1452 * background_1.anInt1453 - 1;
				int k1 = background_1.anInt1452 * anInt945 * 2;
				byte abyte1[] = background_1.aByteArray1450;
				byte abyte4[] = aByteArray912;
				for (int j2 = 0; j2 <= l; j2++)
					abyte4[j2] = abyte1[j2 - k1 & l];

				background_1.aByteArray1450 = abyte4;
				aByteArray912 = abyte1;
				Texture.resetTexture(24);
			}
			if (Texture.textureLastUsed[34] >= j) {
				Background background_2 = Texture.textureImages[34];
				int i1 = background_2.anInt1452 * background_2.anInt1453 - 1;
				int l1 = background_2.anInt1452 * anInt945 * 2;
				byte abyte2[] = background_2.aByteArray1450;
				byte abyte5[] = aByteArray912;
				for (int k2 = 0; k2 <= i1; k2++)
					abyte5[k2] = abyte2[k2 - l1 & i1];

				background_2.aByteArray1450 = abyte5;
				aByteArray912 = abyte2;
				Texture.resetTexture(34);
			}
			if (Texture.textureLastUsed[40] >= j) {
				Background background_2 = Texture.textureImages[40];
				int i1 = background_2.anInt1452 * background_2.anInt1453 - 1;
				int l1 = background_2.anInt1452 * anInt945 * 2;
				byte abyte2[] = background_2.aByteArray1450;
				byte abyte5[] = aByteArray912;
				for (int k2 = 0; k2 <= i1; k2++)
					abyte5[k2] = abyte2[k2 - l1 & i1];

				background_2.aByteArray1450 = abyte5;
				aByteArray912 = abyte2;
				Texture.resetTexture(40);
			}
		}
	}

	private void method38() {
		for (int i = -1; i < playerCount; i++) {
			int j;
			if (i == -1)
				j = myPlayerIndex;
			else
				j = playerIndices[i];
			Player player = playerArray[j];
			if (player != null && player.textCycle > 0) {
				player.textCycle--;
				if (player.textCycle == 0)
					player.textSpoken = null;
			}
		}
		for (int k = 0; k < npcCount; k++) {
			int l = npcIndices[k];
			NPC npc = npcArray[l];
			if (npc != null && npc.textCycle > 0) {
				npc.textCycle--;
				if (npc.textCycle == 0)
					npc.textSpoken = null;
			}
		}
	}

	private void calcCameraPos() {
		int i = anInt1098 * 128 + 64;
		int j = anInt1099 * 128 + 64;
		int k = method42(plane, j, i) - anInt1100;
		if (xCameraPos < i) {
			xCameraPos += anInt1101 + ((i - xCameraPos) * anInt1102) / 1000;
			if (xCameraPos > i)
				xCameraPos = i;
		}
		if (xCameraPos > i) {
			xCameraPos -= anInt1101 + ((xCameraPos - i) * anInt1102) / 1000;
			if (xCameraPos < i)
				xCameraPos = i;
		}
		if (zCameraPos < k) {
			zCameraPos += anInt1101 + ((k - zCameraPos) * anInt1102) / 1000;
			if (zCameraPos > k)
				zCameraPos = k;
		}
		if (zCameraPos > k) {
			zCameraPos -= anInt1101 + ((zCameraPos - k) * anInt1102) / 1000;
			if (zCameraPos < k)
				zCameraPos = k;
		}
		if (yCameraPos < j) {
			yCameraPos += anInt1101 + ((j - yCameraPos) * anInt1102) / 1000;
			if (yCameraPos > j)
				yCameraPos = j;
		}
		if (yCameraPos > j) {
			yCameraPos -= anInt1101 + ((yCameraPos - j) * anInt1102) / 1000;
			if (yCameraPos < j)
				yCameraPos = j;
		}
		i = anInt995 * 128 + 64;
		j = anInt996 * 128 + 64;
		k = method42(plane, j, i) - anInt997;
		int l = i - xCameraPos;
		int i1 = k - zCameraPos;
		int j1 = j - yCameraPos;
		int k1 = (int) Math.sqrt(l * l + j1 * j1);
		int l1 = (int) (Math.atan2(i1, k1) * 325.94900000000001D) & 0x7ff;
		int i2 = (int) (Math.atan2(l, j1) * -325.94900000000001D) & 0x7ff;
		if (l1 < 128)
			l1 = 128;
		if (l1 > 383)
			l1 = 383;
		if (yCameraCurve < l1) {
			yCameraCurve += anInt998 + ((l1 - yCameraCurve) * anInt999) / 1000;
			if (yCameraCurve > l1)
				yCameraCurve = l1;
		}
		if (yCameraCurve > l1) {
			yCameraCurve -= anInt998 + ((yCameraCurve - l1) * anInt999) / 1000;
			if (yCameraCurve < l1)
				yCameraCurve = l1;
		}
		int j2 = i2 - xCameraCurve;
		if (j2 > 1024)
			j2 -= 2048;
		if (j2 < -1024)
			j2 += 2048;
		if (j2 > 0) {
			xCameraCurve += anInt998 + (j2 * anInt999) / 1000;
			xCameraCurve &= 0x7ff;
		}
		if (j2 < 0) {
			xCameraCurve -= anInt998 + (-j2 * anInt999) / 1000;
			xCameraCurve &= 0x7ff;
		}
		int k2 = i2 - xCameraCurve;
		if (k2 > 1024)
			k2 -= 2048;
		if (k2 < -1024)
			k2 += 2048;
		if (k2 < 0 && j2 > 0 || k2 > 0 && j2 < 0)
			xCameraCurve = i2;
	}

	private void drawMenu() {
		int i = menuOffsetX;
		int j = menuOffsetY;
		int k = menuWidth;
		int j1 = super.mouseX;
		int k1 = super.mouseY;
		int l = menuHeight + 1;
		int i1 = 0x5d5447;
		if (menuScreenArea == 1 && (clientSize > 0)) {
			i += 519;// +extraWidth;
			j += 168;// +extraHeight;
		}
		if (menuScreenArea == 2 && (clientSize > 0)) {
			j += 338;
		}
		if (menuScreenArea == 3 && (clientSize > 0)) {
			i += 515;
			j += 0;
		}
		if (menuScreenArea == 0) {
			j1 -= 4;
			k1 -= 4;
		}
		if (menuScreenArea == 1) {
			if (!(clientSize > 0)) {
				j1 -= 519;
				k1 -= 168;
			}
		}
		if (menuScreenArea == 2) {
			if (!(clientSize > 0)) {
				j1 -= 17;
				k1 -= 338;
			}
		}
		if (menuScreenArea == 3 && !(clientSize > 0)) {
			j1 -= 515;
			k1 -= 0;
		}
		if (menuToggle == false) {
			DrawingArea.method335(i1, j, k, l, 150, i);
			DrawingArea.method335(0, j + 1, k - 2, 16, 150, i + 1);
			DrawingArea.fillPixels(i + 1, k - 2, l - 19, 0, j + 18);
			DrawingArea.method338(j + 18, l - 19, 150, 0, k - 2, i + 1);
			chatTextDrawingArea.method385(0xc6b895, "Choose Option", j + 14,
					i + 3);
			chatTextDrawingArea.method385(0xc6b895, "Choose Option", j + 14,
					i + 3);
			for (int l1 = 0; l1 < menuActionRow; l1++) {
				int i2 = j + 31 + (menuActionRow - 1 - l1) * 15;
				int j2 = 0xffffff;
				if (j1 > i && j1 < i + k && k1 > i2 - 13 && k1 < i2 + 3)
					j2 = 0xffff00;
				chatTextDrawingArea.method389(true, i + 3, j2,
						menuActionName[l1], i2);
			}
		} else if (menuToggle == true) {
			// DrawingArea.drawPixels(height, yPos, xPos, color, width);
			// DrawingArea.fillPixels(xPos, width, height, color, yPos);
			DrawingArea.drawPixels(l - 4, j + 2, i, 0x706a5e, k);
			DrawingArea.drawPixels(l - 2, j + 1, i + 1, 0x706a5e, k - 2);
			DrawingArea.drawPixels(l, j, i + 2, 0x706a5e, k - 4);
			DrawingArea.drawPixels(l - 2, j + 1, i + 3, 0x2d2822, k - 6);
			DrawingArea.drawPixels(l - 4, j + 2, i + 2, 0x2d2822, k - 4);
			DrawingArea.drawPixels(l - 6, j + 3, i + 1, 0x2d2822, k - 2);
			DrawingArea.drawPixels(l - 22, j + 19, i + 2, 0x524a3d, k - 4);
			DrawingArea.drawPixels(l - 22, j + 20, i + 3, 0x524a3d, k - 6);
			DrawingArea.drawPixels(l - 23, j + 20, i + 3, 0x2b271c, k - 6);
			DrawingArea.fillPixels(i + 3, k - 6, 1, 0x2a291b, j + 2);
			DrawingArea.fillPixels(i + 2, k - 4, 1, 0x2a261b, j + 3);
			DrawingArea.fillPixels(i + 2, k - 4, 1, 0x252116, j + 4);
			DrawingArea.fillPixels(i + 2, k - 4, 1, 0x211e15, j + 5);
			DrawingArea.fillPixels(i + 2, k - 4, 1, 0x1e1b12, j + 6);
			DrawingArea.fillPixels(i + 2, k - 4, 1, 0x1a170e, j + 7);
			DrawingArea.fillPixels(i + 2, k - 4, 2, 0x15120b, j + 8);
			DrawingArea.fillPixels(i + 2, k - 4, 1, 0x100d08, j + 10);
			DrawingArea.fillPixels(i + 2, k - 4, 1, 0x090a04, j + 11);
			DrawingArea.fillPixels(i + 2, k - 4, 1, 0x080703, j + 12);
			DrawingArea.fillPixels(i + 2, k - 4, 1, 0x090a04, j + 13);
			DrawingArea.fillPixels(i + 2, k - 4, 1, 0x070802, j + 14);
			DrawingArea.fillPixels(i + 2, k - 4, 1, 0x090a04, j + 15);
			DrawingArea.fillPixels(i + 2, k - 4, 1, 0x070802, j + 16);
			DrawingArea.fillPixels(i + 2, k - 4, 1, 0x090a04, j + 17);
			DrawingArea.fillPixels(i + 2, k - 4, 1, 0x2a291b, j + 18);
			DrawingArea.fillPixels(i + 3, k - 6, 1, 0x564943, j + 19);
			chatTextDrawingArea.method385(0xc6b895, "Choose Option", j + 14,
					i + 3);
			for (int l1 = 0; l1 < menuActionRow; l1++) {
				int i2 = j + 31 + (menuActionRow - 1 - l1) * 15;
				int j2 = 0xc6b895;
				if (j1 > i && j1 < i + k && k1 > i2 - 13 && k1 < i2 + 3) {
					detectCursor(l1);
					DrawingArea.drawPixels(15, i2 - 11, i + 3, 0x6f695d,
							menuWidth - 6);
					j2 = 0xeee5c6;
				}
				chatTextDrawingArea.method389(true, i + 4, j2,
						menuActionName[l1], i2 + 1);
			}
		}
	}

	private void addFriend(long l) {
		try {
			if (l == 0L)
				return;
			if (friendsCount >= 100 && anInt1046 != 1) {
				pushMessage(
						"Your friendlist is full. Max of 100 for free users, and 200 for members",
						0, "");
				return;
			}
			if (friendsCount >= 200) {
				pushMessage(
						"Your friendlist is full. Max of 100 for free users, and 200 for members",
						0, "");
				return;
			}
			String s = TextClass.fixName(TextClass.nameForLong(l));
			for (int i = 0; i < friendsCount; i++)
				if (friendsListAsLongs[i] == l) {
					pushMessage(s + " is already on your friend list", 0, "");
					return;
				}
			for (int j = 0; j < ignoreCount; j++)
				if (ignoreListAsLongs[j] == l) {
					pushMessage("Please remove " + s
							+ " from your ignore list first", 0, "");
					return;
				}
			if (s.equals(myPlayer.name))
				return;
			else {
				friendsList[friendsCount] = s;
				friendsListAsLongs[friendsCount] = l;
				friendsNodeIDs[friendsCount] = 0;
				friendsCount++;
				needDrawTabArea = true;
				stream.createFrame(188);
				stream.writeQWord(l);
				inputString = "[FRI]" + s;
				sendPacket(1003);
				int slot = 44001;
				for (int a = 44001; a <= 44200; a++) {
					sendFrame126("", slot);
					slot++;
				}
				slot = 44801;
				for (int d = 44801; d <= 45000; d++) {
					sendFrame126("", slot);
					slot++;
				}
				return;
			}
		} catch (RuntimeException runtimeexception) {
			signlink.reporterror("15283, " + (byte) 68 + ", " + l + ", "
					+ runtimeexception.toString());
		}
		// throw new RuntimeException();
	}

	private int method42(int i, int j, int k) {
		int l = k >> 7;
				int i1 = j >> 7;
			if (l < 0 || i1 < 0 || l > 103 || i1 > 103)
				return 0;
			int j1 = i;
			if (j1 < 3 && (byteGroundArray[1][l][i1] & 2) == 2)
				j1++;
			int k1 = k & 0x7f;
			int l1 = j & 0x7f;
			int i2 = intGroundArray[j1][l][i1] * (128 - k1)
					+ intGroundArray[j1][l + 1][i1] * k1 >> 7;
			int j2 = intGroundArray[j1][l][i1 + 1] * (128 - k1)
					+ intGroundArray[j1][l + 1][i1 + 1] * k1 >> 7;
									return i2 * (128 - l1) + j2 * l1 >> 7;
	}

	private static String intToKOrMil(int j) {
		if (j < 0x186a0)
			return String.valueOf(j);
		if (j < 0x989680)
			return j / 1000 + "K";
		else
			return j / 0xf4240 + "M";
	}

	private void resetLogout() {
		if (customCursor)
			super.setCursor(0);
		prayClicked = false;
		// restOrb = false;
		followPlayer = 0;
		followNPC = 0;
		followDistance = 1;
		try {
			if (socketStream != null)
				socketStream.close();
		} catch (Exception _ex) {
		}
		socketStream = null;
		loggedIn = false;
		previousScreenState = 0;
		loginScreenState = 0;
		if (logger != null) {
			logger.setVisible(false);
			logger = null;
		}
		circle = 0;
		loginCode = 0;
		unlinkMRUNodes();
		worldController.initToNull();
		for (int i = 0; i < 4; i++)
			aClass11Array1230[i].method210();
		System.gc();
		stopMidi();
		currentSong = -1;
		nextSong = -1;
		prevSong = 0;
	}

	private void method45() {
		aBoolean1031 = true;
		for (int j = 0; j < 7; j++) {
			anIntArray1065[j] = -1;
			for (int k = 0; k < IDK.length; k++) {
				if (IDK.cache[k].aBoolean662
						|| IDK.cache[k].anInt657 != j + (aBoolean1047 ? 0 : 7))
					continue;
				anIntArray1065[j] = k;
				break;
			}
		}
	}

	private void method46(int i, Stream stream) {
		while (stream.bitPosition + 21 < i * 8) {
			int k = stream.readBits(14);
			if (k == 16383)
				break;
			if (npcArray[k] == null)
				npcArray[k] = new NPC();
			NPC npc = npcArray[k];
			npcIndices[npcCount++] = k;
			npc.anInt1537 = loopCycle;
			int l = stream.readBits(5);
			if (l > 15)
				l -= 32;
			int i1 = stream.readBits(5);
			if (i1 > 15)
				i1 -= 32;
			int j1 = stream.readBits(1);
			npc.desc = EntityDef.forID(stream.readBits(Configuration.NPC_BITS));
			int k1 = stream.readBits(1);
			if (k1 == 1)
				anIntArray894[anInt893++] = k;
			npc.anInt1540 = npc.desc.aByte68;
			npc.anInt1504 = npc.desc.anInt79;
			npc.anInt1554 = npc.desc.walkAnim;
			npc.anInt1555 = npc.desc.anInt58;
			npc.anInt1556 = npc.desc.anInt83;
			npc.anInt1557 = npc.desc.anInt55;
			npc.anInt1511 = npc.desc.standAnim;
			npc.setPos(myPlayer.smallX[0] + i1, myPlayer.smallY[0] + l, j1 == 1);
		}
		stream.finishBitAccess();
	}

	public void processGameLoop() {
		if (rsAlreadyLoaded || loadingError || genericLoadingError)
			return;
		loopCycle++;
		checkSize();
		if (!loggedIn)
			try {
				processLoginScreenInput();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (UnsupportedLookAndFeelException e) {
				e.printStackTrace();
			}
		else
			try {
				mainGameProcessor();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (UnsupportedLookAndFeelException e) {
				e.printStackTrace();
			}
		processOnDemandQueue();
	}

	private void method47(boolean flag) {
		if (myPlayer.x >> 7 == destX && myPlayer.y >> 7 == destY)
			destX = 0;
		int j = playerCount;
		if (flag)
			j = 1;
		for (int l = 0; l < j; l++) {
			Player player;
			int i1;
			if (flag) {
				player = myPlayer;
				i1 = myPlayerIndex << 14;
			} else {
				player = playerArray[playerIndices[l]];
				i1 = playerIndices[l] << 14;
			}
			if (player == null || !player.isVisible())
				continue;
			player.aBoolean1699 = (lowMem && playerCount > 50 || playerCount > 200)
					&& !flag && player.anInt1517 == player.anInt1511;
			int j1 = player.x >> 7;
				int k1 = player.y >> 7;
		if (j1 < 0 || j1 >= 104 || k1 < 0 || k1 >= 104)
			continue;
		if (player.aModel_1714 != null && loopCycle >= player.anInt1707
				&& loopCycle < player.anInt1708) {
			player.aBoolean1699 = false;
			player.anInt1709 = method42(plane, player.y, player.x);
			worldController.method286(plane, player.y, player,
					player.anInt1552, player.anInt1722, player.x,
					player.anInt1709, player.anInt1719, player.anInt1721,
					i1, player.anInt1720);
			continue;
		}
		if ((player.x & 0x7f) == 64 && (player.y & 0x7f) == 64) {
			if (anIntArrayArray929[j1][k1] == anInt1265)
				continue;
			anIntArrayArray929[j1][k1] = anInt1265;
		}
		player.anInt1709 = method42(plane, player.y, player.x);
		worldController.method285(plane, player.anInt1552,
				player.anInt1709, i1, player.y, 60, player.x, player,
				player.aBoolean1541);
		}
	}

	private boolean promptUserForInput(RSInterface class9) {
		int j = class9.contentType;
		if (anInt900 == 2) {
			if (j == 201) {
				inputTaken = true;
				inputDialogState = 0;
				showInput = true;
				promptInput = "";
				friendsListAction = 1;
				promptMessage = "Enter name of friend to add to list";
			}
			if (j == 202) {
				inputTaken = true;
				inputDialogState = 0;
				showInput = true;
				promptInput = "";
				friendsListAction = 2;
				promptMessage = "Enter name of friend to delete from list";
			}
		}
		if (j == 7712) {
			doGEAction(721);
		}
		if (j == 7713) {
			doGEAction(722);
		}
		if (j == 7714) {
			doGEAction(723);
		}
		if (j == 7715) {
			doGEAction(724);
		}
		if (j == 22222) {
			inputTaken = true;
			showInput = true;
			amountOrNameInput = "";
			promptInput = "";
			inputDialogState = 0;
			interfaceButtonAction = 6199;
			promptMessage = "Enter a name for the clan chat.";
		}
		if (j == 677) {
			inputTaken = true;
			showInput = true;
			amountOrNameInput = "";
			promptInput = "";
			inputDialogState = 0;
			interfaceButtonAction = 6200;
			promptMessage = "Enter name of the player you would like kicked.";
		}
		if (j == 205) {
			anInt1011 = 250;
			return true;
		}
		if (j == 501) {
			inputTaken = true;
			inputDialogState = 0;
			showInput = true;
			promptInput = "";
			friendsListAction = 4;
			promptMessage = "Enter name of player to add to list";
		}
		if (j == 502) {
			inputTaken = true;
			inputDialogState = 0;
			showInput = true;
			promptInput = "";
			friendsListAction = 5;
			promptMessage = "Enter name of player to delete from list";
		}
		if (j == 550) {
			if (RSInterface.interfaceCache[18135].message.startsWith("Join")) {
				inputTaken = true;
				inputDialogState = 0;
				showInput = true;
				promptInput = "";
				friendsListAction = 6;
				promptMessage = "Enter the name of the chat you wish to join";
			} else {
				stream.createFrame(185);
				stream.writeWord(49627);
			}
		}
		if (j >= 300 && j <= 313) {
			int k = (j - 300) / 2;
			int j1 = j & 1;
			int i2 = anIntArray1065[k];
			if (i2 != -1) {
				do {
					if (j1 == 0 && --i2 < 0)
						i2 = IDK.length - 1;
					if (j1 == 1 && ++i2 >= IDK.length)
						i2 = 0;
				} while (IDK.cache[i2].aBoolean662
						|| IDK.cache[i2].anInt657 != k + (aBoolean1047 ? 0 : 7));
				anIntArray1065[k] = i2;
				aBoolean1031 = true;
			}
		}
		if (j >= 314 && j <= 323) {
			int l = (j - 314) / 2;
			int k1 = j & 1;
			int j2 = anIntArray990[l];
			if (k1 == 0 && --j2 < 0)
				j2 = anIntArrayArray1003[l].length - 1;
			if (k1 == 1 && ++j2 >= anIntArrayArray1003[l].length)
				j2 = 0;
			anIntArray990[l] = j2;
			aBoolean1031 = true;
		}
		if (j == 324 && !aBoolean1047) {
			aBoolean1047 = true;
			method45();
		}
		if (j == 325 && aBoolean1047) {
			aBoolean1047 = false;
			method45();
		}
		if (j == 326) {
			stream.createFrame(101);
			stream.writeWordBigEndian(aBoolean1047 ? 0 : 1);
			for (int i1 = 0; i1 < 7; i1++)
				stream.writeWordBigEndian(anIntArray1065[i1]);

			for (int l1 = 0; l1 < 5; l1++)
				stream.writeWordBigEndian(anIntArray990[l1]);

			return true;
		}
		if (j == 613)
			canMute = !canMute;
		if (j >= 601 && j <= 612) {
			clearTopInterfaces();
			if (reportAbuseInput.length() > 0) {
				stream.createFrame(218);
				stream.writeQWord(TextClass.longForName(reportAbuseInput));
				stream.writeWordBigEndian(j - 601);
				stream.writeWordBigEndian(canMute ? 1 : 0);
			}
		}
		return false;
	}

	private void method49(Stream stream) {
		for (int j = 0; j < anInt893; j++) {
			int k = anIntArray894[j];
			Player player = playerArray[k];
			int l = stream.readUnsignedByte();
			if ((l & 0x40) != 0)
				l += stream.readUnsignedByte() << 8;
			method107(l, k, stream, player);
		}
	}

	/*
	 * private void method50(int y, int primaryColor, int x, int secondaryColor,
	 * int z) { int uid = worldController.method300(z, x, y); if (uid != 0) {
	 * int resourceTag = worldController.method304(z, x, y, uid); int direction
	 * = resourceTag >> 6 & 3; int type = resourceTag & 0x1f; int color =
	 * primaryColor; if (uid > 0) color = secondaryColor; int mapPixels[] =
	 * miniMap.myPixels; int pixel = 24624 + x * 4 + (103 - y) * 512 * 4; int i5
	 * = uid >> 14 & 0x7fff; ObjectDef class46_2 = ObjectDef.forID(i5); if
	 * (class46_2.mapSceneID != -1) { Background background_2 =
	 * mapScenes[class46_2.mapSceneID]; if (background_2 != null) { int i6 =
	 * (class46_2.anInt744 * 4 - background_2.anInt1452) / 2; int j6 =
	 * (class46_2.anInt761 * 4 - background_2.anInt1453) / 2;
	 * background_2.drawBackground(48 + x * 4 + i6, 48 + (104 - y -
	 * class46_2.anInt761) * 4 + j6); } } else { if (type == 0 || type == 2) if
	 * (direction == 0) { mapPixels[pixel] = color; mapPixels[pixel + 512] =
	 * color; mapPixels[pixel + 1024] = color; mapPixels[pixel + 1536] = color;
	 * } else if (direction == 1) { mapPixels[pixel] = color; mapPixels[pixel +
	 * 1] = color; mapPixels[pixel + 2] = color; mapPixels[pixel + 3] = color; }
	 * else if (direction == 2) { mapPixels[pixel + 3] = color; mapPixels[pixel
	 * + 3 + 512] = color; mapPixels[pixel + 3 + 1024] = color; mapPixels[pixel
	 * + 3 + 1536] = color; } else if (direction == 3) { mapPixels[pixel + 1536]
	 * = color; mapPixels[pixel + 1536 + 1] = color; mapPixels[pixel + 1536 + 2]
	 * = color; mapPixels[pixel + 1536 + 3] = color; } if (type == 3) if
	 * (direction == 0) mapPixels[pixel] = color; else if (direction == 1)
	 * mapPixels[pixel + 3] = color; else if (direction == 2) mapPixels[pixel +
	 * 3 + 1536] = color; else if (direction == 3) mapPixels[pixel + 1536] =
	 * color; if (type == 2) if (direction == 3) { mapPixels[pixel] = color;
	 * mapPixels[pixel + 512] = color; mapPixels[pixel + 1024] = color;
	 * mapPixels[pixel + 1536] = color; } else if (direction == 0) {
	 * mapPixels[pixel] = color; mapPixels[pixel + 1] = color; mapPixels[pixel +
	 * 2] = color; mapPixels[pixel + 3] = color; } else if (direction == 1) {
	 * mapPixels[pixel + 3] = color; mapPixels[pixel + 3 + 512] = color;
	 * mapPixels[pixel + 3 + 1024] = color; mapPixels[pixel + 3 + 1536] = color;
	 * } else if (direction == 2) { mapPixels[pixel + 1536] = color;
	 * mapPixels[pixel + 1536 + 1] = color; mapPixels[pixel + 1536 + 2] = color;
	 * mapPixels[pixel + 1536 + 3] = color; } } } uid =
	 * worldController.method302(z, x, y); if (uid != 0) { int i2 =
	 * worldController.method304(z, x, y, uid); int l2 = i2 >> 6 & 3; int j3 =
	 * i2 & 0x1f; int l3 = uid >> 14 & 0x7fff; ObjectDef class46_1 =
	 * ObjectDef.forID(l3); if (class46_1.mapSceneID != -1) { Background
	 * background_1 = mapScenes[class46_1.mapSceneID]; if (background_1 != null)
	 * { int j5 = (class46_1.anInt744 * 4 - background_1.anInt1452) / 2; int k5
	 * = (class46_1.anInt761 * 4 - background_1.anInt1453) / 2;
	 * background_1.drawBackground(48 + x * 4 + j5, 48 + (104 - y -
	 * class46_1.anInt761) * 4 + k5); } } else if (j3 == 9) { int l4 = 0xeeeeee;
	 * if (uid > 0) l4 = 0xee0000; int ai1[] = miniMap.myPixels; int l5 = 24624
	 * + x * 4 + (103 - y) * 512 * 4; if (l2 == 0 || l2 == 2) { ai1[l5 + 1536] =
	 * l4; ai1[l5 + 1024 + 1] = l4; ai1[l5 + 512 + 2] = l4; ai1[l5 + 3] = l4; }
	 * else { ai1[l5] = l4; ai1[l5 + 512 + 1] = l4; ai1[l5 + 1024 + 2] = l4;
	 * ai1[l5 + 1536 + 3] = l4; } } } uid = worldController.method303(z, x, y);
	 * if (uid != 0) { int j2 = uid >> 14 & 0x7fff; ObjectDef class46 =
	 * ObjectDef.forID(j2); if (class46.mapSceneID != -1) { Background
	 * background = mapScenes[class46.mapSceneID]; if (background != null) { int
	 * i4 = (class46.anInt744 * 4 - background.anInt1452) / 2; int j4 =
	 * (class46.anInt761 * 4 - background.anInt1453) / 2;
	 * background.drawBackground(48 + x * 4 + i4, 48 + (104 - y -
	 * class46.anInt761) * 4 + j4); } } } }
	 */

	public void method50(int y, int primaryColor, int x, int secondaryColor,
			int z) {
		int uid = worldController.method300(z, x, y);
		if ((uid ^ 0xffffffffffffffffL) != -1L) {
			int resourceTag = worldController.method304(z, x, y, uid);
			int direction = resourceTag >> 6 & 3;// direction
			int type = resourceTag & 0x1f;// type
			int color = primaryColor;// color
			if (uid > 0)
				color = secondaryColor;
			int mapPixels[] = miniMap.myPixels;
			int pixel = 24624 + x * 4 + (103 - y) * 512 * 4;
			int objectId = worldController.fetchWallDecorationNewUID(z, x, y);
			ObjectDef objDef = ObjectDef.forID(objectId);
			if ((objDef.mapSceneID ^ 0xffffffff) == 0) {
				if (type == 0 || type == 2)
					if (direction == 0) {
						mapPixels[pixel] = color;
						mapPixels[pixel + 512] = color;
						mapPixels[1024 + pixel] = color;
						mapPixels[1536 + pixel] = color;
					} else if ((direction ^ 0xffffffff) == -2) {
						mapPixels[pixel] = color;
						mapPixels[pixel + 1] = color;
						mapPixels[pixel + 2] = color;
						mapPixels[3 + pixel] = color;
					} else if (direction == 2) {
						mapPixels[pixel - -3] = color;
						mapPixels[3 + (pixel + 512)] = color;
						mapPixels[3 + (pixel + 1024)] = color;
						mapPixels[1536 + (pixel - -3)] = color;
					} else if (direction == 3) {
						mapPixels[pixel + 1536] = color;
						mapPixels[pixel + 1536 + 1] = color;
						mapPixels[2 + pixel + 1536] = color;
						mapPixels[pixel + 1539] = color;
					}
				if (type == 3)
					if (direction == 0)
						mapPixels[pixel] = color;
					else if (direction == 1)
						mapPixels[pixel + 3] = color;
					else if (direction == 2)
						mapPixels[pixel + 3 + 1536] = color;
					else if (direction == 3)
						mapPixels[pixel + 1536] = color;
				if (type == 2)
					if (direction == 3) {
						mapPixels[pixel] = color;
						mapPixels[pixel + 512] = color;
						mapPixels[pixel + 1024] = color;
						mapPixels[pixel + 1536] = color;
					} else if (direction == 0) {
						mapPixels[pixel] = color;
						mapPixels[pixel + 1] = color;
						mapPixels[pixel + 2] = color;
						mapPixels[pixel + 3] = color;
					} else if (direction == 1) {
						mapPixels[pixel + 3] = color;
						mapPixels[pixel + 3 + 512] = color;
						mapPixels[pixel + 3 + 1024] = color;
						mapPixels[pixel + 3 + 1536] = color;
					} else if (direction == 2) {
						mapPixels[pixel + 1536] = color;
						mapPixels[pixel + 1536 + 1] = color;
						mapPixels[pixel + 1536 + 2] = color;
						mapPixels[pixel + 1536 + 3] = color;
					}
			}
		}
		uid = worldController.method302(z, x, y);
		if (uid != 0) {
			int resourceTag = worldController.method304(z, x, y, uid);
			int direction = resourceTag >> 6 & 3;
			int type = resourceTag & 0x1f;
			int objectId = worldController.fetchObjectMeshNewUID(z, x, y);
			;
			ObjectDef objDef = ObjectDef.forID(objectId);
			if (objDef.mapSceneID != -1) {
				Background scene = mapScenes[objDef.mapSceneID];
				if (scene != null) {
					int sceneX = (objDef.anInt744 * 4 - scene.anInt1452) / 2;
					int sceneY = (objDef.anInt761 * 4 - scene.anInt1453) / 2;
					scene.drawBackground(48 + x * 4 + sceneX, 48
							+ (104 - y - objDef.anInt761) * 4 + sceneY);
				}
			} else if (type == 9) {
				int color = 0xeeeeee;
				if (uid > 0)
					color = 0xee0000;
				int mapPixels[] = miniMap.myPixels;
				int pixel = 24624 + x * 4 + (103 - y) * 512 * 4;
				if (direction == 0 || direction == 2) {
					mapPixels[pixel + 1536] = color;
					mapPixels[pixel + 1024 + 1] = color;
					mapPixels[pixel + 512 + 2] = color;
					mapPixels[pixel + 3] = color;
				} else {
					mapPixels[pixel] = color;
					mapPixels[pixel + 512 + 1] = color;
					mapPixels[pixel + 1024 + 2] = color;
					mapPixels[pixel + 1536 + 3] = color;
				}
			}
		}
		uid = worldController.fetchGroundDecorationNewUID(z, x, y);
		if (uid > 0) {
			ObjectDef objDef = ObjectDef.forID(uid);
			if (objDef.mapSceneID != -1) {
				Background scene = mapScenes[objDef.mapSceneID];
				if (scene != null) {
					int sceneX = (objDef.anInt744 * 4 - scene.anInt1452) / 2;
					int sceneY = (objDef.anInt761 * 4 - scene.anInt1453) / 2;
					scene.drawBackground(48 + x * 4 + sceneX, 48
							+ (104 - y - objDef.anInt761) * 4 + sceneY);
				}
			}
		}
	}

	private void loadTitleScreen() {
		new Background(titleStreamLoader, "titlebox", 0);
		new Background(titleStreamLoader, "titlebutton", 0);
		aBackgroundArray1152s = new Background[12];
		int j = 0;
		try {
			j = Integer.parseInt(getParameter("fl_icon"));
		} catch (Exception _ex) {
		}
		if (j == 0) {
			for (int k = 0; k < 12; k++)
				aBackgroundArray1152s[k] = new Background(titleStreamLoader,
						"runes", k);

		} else {
			for (int l = 0; l < 12; l++)
				aBackgroundArray1152s[l] = new Background(titleStreamLoader,
						"runes", 12 + (l & 3));

		}
		aClass30_Sub2_Sub1_Sub1_1201 = new Sprite(128, 265);
		aClass30_Sub2_Sub1_Sub1_1202 = new Sprite(128, 265);
		System.arraycopy(GraphicsBuffer_1110.anIntArray315, 0,
				aClass30_Sub2_Sub1_Sub1_1201.myPixels, 0, 33920);

		System.arraycopy(GraphicsBuffer_1111.anIntArray315, 0,
				aClass30_Sub2_Sub1_Sub1_1202.myPixels, 0, 33920);

		anIntArray851 = new int[256];
		for (int k1 = 0; k1 < 64; k1++)
			anIntArray851[k1] = k1 * 0x40000;

		for (int l1 = 0; l1 < 64; l1++)
			anIntArray851[l1 + 64] = 0xff0000 + 1024 * l1;

		for (int i2 = 0; i2 < 64; i2++)
			anIntArray851[i2 + 128] = 0xffff00 + 4 * i2;

		for (int j2 = 0; j2 < 64; j2++)
			anIntArray851[j2 + 192] = 0xffffff;

		anIntArray852 = new int[256];
		for (int k2 = 0; k2 < 64; k2++)
			anIntArray852[k2] = k2 * 1024;

		for (int l2 = 0; l2 < 64; l2++)
			anIntArray852[l2 + 64] = 65280 + 4 * l2;

		for (int i3 = 0; i3 < 64; i3++)
			anIntArray852[i3 + 128] = 65535 + 0x40000 * i3;

		for (int j3 = 0; j3 < 64; j3++)
			anIntArray852[j3 + 192] = 0xffffff;

		anIntArray853 = new int[256];
		for (int k3 = 0; k3 < 64; k3++)
			anIntArray853[k3] = k3 * 4;

		for (int l3 = 0; l3 < 64; l3++)
			anIntArray853[l3 + 64] = 255 + 0x40000 * l3;

		for (int i4 = 0; i4 < 64; i4++)
			anIntArray853[i4 + 128] = 0xff00ff + 1024 * i4;

		for (int j4 = 0; j4 < 64; j4++)
			anIntArray853[j4 + 192] = 0xffffff;

		anIntArray1190 = new int[32768];
		anIntArray1191 = new int[32768];
		randomizeBackground(null);
		drawSmoothLoading(10, "Connecting to fileserver");
		if (!aBoolean831) {
			drawFlames = true;
			aBoolean831 = true;
			startRunnable(this, 2);
		}
	}

	private static void setHighMem() {
		WorldController.lowMem = false;
		Texture.lowMem = false;
		lowMem = false;
		ObjectManager.lowMem = false;
		ObjectDef.lowMem = false;
	}

	public int canWalkDelay = 0;

	public int getDis(int coordX1, int coordY1, int coordX2, int coordY2) {
		int deltaX = coordX2 - coordX1;
		int deltaY = coordY2 - coordY1;
		return ((int) Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2)));
	}

	public int random(int range) {
		return (int) (Math.random() * range);
	}

	public boolean withinDistance(int x1, int y1, int x2, int y2, int dis) {
		for (int i = 0; i <= dis; i++) {
			try {
				if ((x1 + i) == x2
						&& ((y1 + i) == y2 || (y1 - i) == y2 || y1 == y2))
					return true;
				else if ((x1 - i) == x2
						&& ((x1 + i) == y2 || (y1 - i) == y2 || y1 == y2))
					return true;
				else if (x1 == x2
						&& ((x1 + i) == y2 || (y1 - i) == y2 || y1 == y2))
					return true;
			} catch (Exception ex) {
				System.out
				.println("Exception in following, method : WithingDistance");
			}
		}
		return false;
	}

	public static void main(String args[]) {
		try {
			nodeID = 10;
			portOff = 0;
			setHighMem();
			isMembers = true;
			signlink.storeid = 32;
			signlink.startpriv(InetAddress.getLocalHost());
			clientSize = 0;
			instance = new Client();
			instance.createClientFrame(clientWidth, clientHeight);
		} catch (Exception exception) {
		}
	}

	public static Client instance;

	private void loadingStages() {
		try {
			if (lowMem && loadingStage == 2 && ObjectManager.anInt131 != plane) {
				gameScreenIP.initDrawingArea();
				loadingPleaseWait.drawSprite(8, 9);
				;
				gameScreenIP.drawGraphics(4, super.graphics, 4);
				loadingStage = 1;
				aLong824 = System.currentTimeMillis();
			}
			if (loadingStage == 1) {
				int j = method54();
				if (j != 0 && System.currentTimeMillis() - aLong824 > 0x57e40L) {
					signlink.reporterror(myUsername + " glcfb " + aLong1215
							+ "," + j + "," + lowMem + "," + cacheIndices[0]
									+ "," + onDemandFetcher.getRemaining() + ","
									+ plane + "," + anInt1069 + "," + anInt1070);
					aLong824 = System.currentTimeMillis();
				}
			}
			if (loadingStage == 2 && plane != anInt985) {
				anInt985 = plane;
				renderedMapScene(plane);
			}
		} catch (Exception e) {
		}
	}

	private String objectMaps = "";
	private String floorMaps = "";

	private int method54() {
		if (!floorMaps.equals("") || !objectMaps.equals("")) {
			floorMaps = "";
			objectMaps = "";
		}
		for (int i = 0; i < aByteArrayArray1183.length; i++) {
			floorMaps += "  " + anIntArray1235[i];
			objectMaps += "  " + anIntArray1236[i];
			if (aByteArrayArray1183[i] == null && anIntArray1235[i] != -1)
				return -1;
			if (aByteArrayArray1247[i] == null && anIntArray1236[i] != -1)
				return -2;
		}
		boolean flag = true;
		for (int j = 0; j < aByteArrayArray1183.length; j++) {
			byte abyte0[] = aByteArrayArray1247[j];
			if (abyte0 != null) {
				int k = (anIntArray1234[j] >> 8) * 64 - baseX;
				int l = (anIntArray1234[j] & 0xff) * 64 - baseY;
				if (aBoolean1159) {
					k = 10;
					l = 10;
				}
				flag &= ObjectManager.method189(k, abyte0, l);
			}
		}
		if (!flag)
			return -3;
		if (aBoolean1080) {
			return -4;
		} else {
			loadingStage = 2;
			ObjectManager.anInt131 = plane;
			method22();
			if (loggedIn)
				stream.createFrame(121);
			return 0;
		}
	}

	private void method55() {
		for (Animable_Sub4 class30_sub2_sub4_sub4 = (Animable_Sub4) aClass19_1013
				.getFront(); class30_sub2_sub4_sub4 != null; class30_sub2_sub4_sub4 = (Animable_Sub4) aClass19_1013
				.reverseGetNext())
			if (class30_sub2_sub4_sub4.anInt1597 != plane
			|| loopCycle > class30_sub2_sub4_sub4.anInt1572)
				class30_sub2_sub4_sub4.unlink();
			else if (loopCycle >= class30_sub2_sub4_sub4.anInt1571) {
				if (class30_sub2_sub4_sub4.anInt1590 > 0) {
					NPC npc = npcArray[class30_sub2_sub4_sub4.anInt1590 - 1];
					if (npc != null && npc.x >= 0 && npc.x < 13312
							&& npc.y >= 0 && npc.y < 13312)
						class30_sub2_sub4_sub4.method455(
								loopCycle,
								npc.y,
								method42(class30_sub2_sub4_sub4.anInt1597,
										npc.y, npc.x)
										- class30_sub2_sub4_sub4.anInt1583,
										npc.x);
				}
				if (class30_sub2_sub4_sub4.anInt1590 < 0) {
					int j = -class30_sub2_sub4_sub4.anInt1590 - 1;
					Player player;
					if (j == unknownInt10)
						player = myPlayer;
					else
						player = playerArray[j];
					if (player != null && player.x >= 0 && player.x < 13312
							&& player.y >= 0 && player.y < 13312)
						class30_sub2_sub4_sub4.method455(
								loopCycle,
								player.y,
								method42(class30_sub2_sub4_sub4.anInt1597,
										player.y, player.x)
										- class30_sub2_sub4_sub4.anInt1583,
										player.x);
				}
				class30_sub2_sub4_sub4.method456(anInt945);
				worldController.method285(plane,
						class30_sub2_sub4_sub4.anInt1595,
						(int) class30_sub2_sub4_sub4.aDouble1587, -1,
						(int) class30_sub2_sub4_sub4.aDouble1586, 60,
						(int) class30_sub2_sub4_sub4.aDouble1585,
						class30_sub2_sub4_sub4, false);
			}

	}

	public AppletContext getAppletContext() {
		if (signlink.mainapp != null)
			return signlink.mainapp.getAppletContext();
		else
			return super.getAppletContext();
	}

	private void processOnDemandQueue() {
		do {
			OnDemandRequest onDemandData;
			do {
				onDemandData = onDemandFetcher.getNextNode();
				if (onDemandData == null)
					return;

				/** Models Loading **/
				if (onDemandData.dataType == 0) {
					Model.method460(onDemandData.buffer, onDemandData.id);
					if(Configuration.JAGCACHED_ENABLED) {
						if ((onDemandFetcher.getModelIndex(onDemandData.id) & 0x62) != 0) {
							needDrawTabArea = true;
							if (backDialogID != -1)
								inputTaken = true;
						}
					}
				}
				/** Animations Loading **/
				if (onDemandData.dataType == 1) {
					FrameReader.load(onDemandData.id, onDemandData.buffer);
				}
				/** Sounds Loading **/
				if (onDemandData.dataType == 2 && onDemandData.id == nextSong
						&& onDemandData.buffer != null)
					saveMidi(songChanging, onDemandData.buffer);
				/** Maps Loading **/
				if (onDemandData.dataType == 3 && loadingStage == 1) {
					for (int i = 0; i < aByteArrayArray1183.length; i++) {
						if (anIntArray1235[i] == onDemandData.id) {
							aByteArrayArray1183[i] = onDemandData.buffer;
							if (onDemandData.buffer == null)
								anIntArray1235[i] = -1;
							break;
						}
						if (anIntArray1236[i] != onDemandData.id)
							continue;
						aByteArrayArray1247[i] = onDemandData.buffer;
						if (onDemandData.buffer == null)
							anIntArray1236[i] = -1;
						break;
					}

				}
			} while (onDemandData.dataType != 93
					|| !onDemandFetcher.method564(onDemandData.id));
			ObjectManager.method173(new Stream(onDemandData.buffer),
					onDemandFetcher);
		} while (true);
	}

	private void method60(int i) {
		RSInterface rsInterface = RSInterface.interfaceCache[i];
		for (int j = 0; j < rsInterface.children.length; j++) {
			if (rsInterface.children[j] == -1)
				break;
			RSInterface class9_1 = RSInterface.interfaceCache[rsInterface.children[j]];
			if (class9_1.type == 1)
				method60(class9_1.id);
			class9_1.anInt246 = 0;
			class9_1.anInt208 = 0;
		}
	}

	private void doGEAction(int l) {
		if (l == 721) {
			inputTaken = true;
			amountOrNameInput = "";
			inputDialogState = 1;
			interfaceButtonAction = 1557;
		}
		if (l == 722) {
			inputTaken = true;
			amountOrNameInput = "";
			inputDialogState = 1;
			interfaceButtonAction = 1557;
		}
		if (l == 723) {
			inputTaken = true;
			amountOrNameInput = "";
			inputDialogState = 1;
			interfaceButtonAction = 1558;
		}
		if (l == 724) {
			inputTaken = true;
			amountOrNameInput = "";
			inputDialogState = 1;
			interfaceButtonAction = 1558;
		}
	}

	private void drawHeadIcon() {
		if (anInt855 != 2)
			return;
		calcEntityScreenPos((anInt934 - baseX << 7) + anInt937, anInt936 * 2,
				(anInt935 - baseY << 7) + anInt938);
		if (spriteDrawX > -1 && loopCycle % 20 < 10)
			headIconsHint[0].drawSprite(spriteDrawX - 12, spriteDrawY - 28);
	}

	public int otherPlayerId = 0, otherPlayerX = 0, otherPlayerY = 0;
	private int lastPercent;

	private void mainGameProcessor() throws ClassNotFoundException,
	InstantiationException, IllegalAccessException,
	UnsupportedLookAndFeelException {
		if (openInterfaceID == 24600 && buttonclicked
				&& interfaceButtonAction != 1558
				&& interfaceButtonAction != 1557) {
			inputDialogState = 3;
		}
		if (openInterfaceID == 24600 && interfaceButtonAction == 1558
				|| openInterfaceID == 24600 && interfaceButtonAction == 1557) {
			inputDialogState = 1;
		}
		if (openInterfaceID == 24600 && !buttonclicked
				&& interfaceButtonAction != 1558
				&& interfaceButtonAction != 1557) {
			inputDialogState = 0;
		}
		if (anInt1104 > 1)
			anInt1104--;
		if (anInt1011 > 0)
			anInt1011--;
		for (int j = 0; j < 5; j++)
			if (!parsePacket())
				break;

		if (!loggedIn)
			return;
		synchronized (mouseDetection.syncObject) {
			if (loggedIn && otherPlayerId > 0) {
				Player class30_sub2_sub4_sub1_sub2_4 = playerArray[otherPlayerId];
				int xCOORD = 0;
				int yCOORD = 0;
				boolean doStuff = false;
				if (playerArray[otherPlayerId] != null) {
					xCOORD = class30_sub2_sub4_sub1_sub2_4.smallX[0]
							+ (class30_sub2_sub4_sub1_sub2_4.x - 6 >> 7);
					yCOORD = class30_sub2_sub4_sub1_sub2_4.smallY[0]
							+ (class30_sub2_sub4_sub1_sub2_4.y - 6 >> 7);
					if (xCOORD == otherPlayerX && yCOORD == otherPlayerY)
						doStuff = true;
				}
				if (playerArray[otherPlayerId] != null && !doStuff) {
					doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0,
							class30_sub2_sub4_sub1_sub2_4.smallY[0],
							myPlayer.smallX[0], false,
							class30_sub2_sub4_sub1_sub2_4.smallX[0]);
				}
				if (playerArray[otherPlayerId] != null) {
					otherPlayerY = yCOORD;
					otherPlayerX = xCOORD;
				}
				if (playerArray[otherPlayerId] == null) {
					otherPlayerId = 0;
				}
			}
			if (flagged) {
				if (super.clickMode3 != 0 || mouseDetection.coordsIndex >= 40) {

					stream.createFrame(45);
					stream.writeWordBigEndian(0);
					int j2 = stream.currentOffset;
					int j3 = 0;
					for (int j4 = 0; j4 < mouseDetection.coordsIndex; j4++) {
						if (j2 - stream.currentOffset >= 240)
							break;
						j3++;
						int l4 = mouseDetection.coordsY[j4];
						if (l4 < 0)
							l4 = 0;
						else if (l4 > 502)
							l4 = 502;
						int k5 = mouseDetection.coordsX[j4];
						if (k5 < 0)
							k5 = 0;
						else if (k5 > 764)
							k5 = 764;
						int i6 = l4 * 765 + k5;
						if (mouseDetection.coordsY[j4] == -1
								&& mouseDetection.coordsX[j4] == -1) {
							k5 = -1;
							l4 = -1;
							i6 = 0x7ffff;
						}
						if (k5 == anInt1237 && l4 == anInt1238) {
							if (anInt1022 < 2047)
								anInt1022++;
						} else {
							int j6 = k5 - anInt1237;
							anInt1237 = k5;
							int k6 = l4 - anInt1238;
							anInt1238 = l4;
							if (anInt1022 < 8 && j6 >= -32 && j6 <= 31
									&& k6 >= -32 && k6 <= 31) {
								j6 += 32;
								k6 += 32;
								stream.writeWord((anInt1022 << 12) + (j6 << 6)
										+ k6);
								anInt1022 = 0;
							} else if (anInt1022 < 8) {
								stream.writeDWordBigEndian(0x800000
										+ (anInt1022 << 19) + i6);
								anInt1022 = 0;
							} else {
								stream.writeDWord(0xc0000000
										+ (anInt1022 << 19) + i6);
								anInt1022 = 0;
							}
						}
					}

					stream.writeBytes(stream.currentOffset - j2);
					if (j3 >= mouseDetection.coordsIndex) {
						mouseDetection.coordsIndex = 0;
					} else {
						mouseDetection.coordsIndex -= j3;
						for (int i5 = 0; i5 < mouseDetection.coordsIndex; i5++) {
							mouseDetection.coordsX[i5] = mouseDetection.coordsX[i5
							                                                    + j3];
							mouseDetection.coordsY[i5] = mouseDetection.coordsY[i5
							                                                    + j3];
						}

					}
				}
			} else {
				mouseDetection.coordsIndex = 0;
			}
		}
		if (super.clickMode3 != 0) {
			long l = (super.aLong29 - aLong1220) / 50L;
			if (l > 4095L)
				l = 4095L;
			aLong1220 = super.aLong29;
			int k2 = super.saveClickY;
			if (k2 < 0)
				k2 = 0;
			else if (k2 > 502)
				k2 = 502;
			int k3 = super.saveClickX;
			if (k3 < 0)
				k3 = 0;
			else if (k3 > 764)
				k3 = 764;
			int k4 = k2 * 765 + k3;
			int j5 = 0;
			if (super.clickMode3 == 2)
				j5 = 1;
			int l5 = (int) l;
			stream.createFrame(241);
			stream.writeDWord((l5 << 20) + (j5 << 19) + k4);
		}
		if (anInt1016 > 0)
			anInt1016--;
		if (super.keyArray[1] == 1 || super.keyArray[2] == 1
				|| super.keyArray[3] == 1 || super.keyArray[4] == 1)
			aBoolean1017 = true;
		if (aBoolean1017 && anInt1016 <= 0) {
			anInt1016 = 20;
			aBoolean1017 = false;
			stream.createFrame(86);
			stream.writeWord(anInt1184);
			stream.method432(viewRotation);
		}
		if (super.awtFocus && !aBoolean954) {
			aBoolean954 = true;
			stream.createFrame(3);
			stream.writeWordBigEndian(1);
		}
		if (!super.awtFocus && aBoolean954) {
			aBoolean954 = false;
			stream.createFrame(3);
			stream.writeWordBigEndian(0);
		}
		loadingStages();
		method115();
		method90();
		anInt1009++;
		if (anInt1009 > 750)
			dropClient();
		method114();
		method95();
		method38();
		anInt945++;
		if (crossType != 0) {
			crossIndex += 20;
			if (crossIndex >= 400)
				crossType = 0;
		}
		if (atInventoryInterfaceType != 0) {
			atInventoryLoopCycle++;
			if (atInventoryLoopCycle >= 15) {
				if (atInventoryInterfaceType == 2)
					needDrawTabArea = true;
				if (atInventoryInterfaceType == 3)
					inputTaken = true;
				atInventoryInterfaceType = 0;
			}
		}
		if (activeInterfaceType != 0) {
			anInt989++;
			if (super.mouseX > anInt1087 + 5 || super.mouseX < anInt1087 - 5
					|| super.mouseY > anInt1088 + 5
					|| super.mouseY < anInt1088 - 5)
				aBoolean1242 = true;
			if (super.clickMode2 == 0) {
				if (activeInterfaceType == 2)
					needDrawTabArea = true;
				if (activeInterfaceType == 3)
					inputTaken = true;
				activeInterfaceType = 0;
				if (aBoolean1242 && anInt989 >= 10) {
					lastActiveInvInterface = -1;
					processRightClick();
					if (lastActiveInvInterface == anInt1084
							&& mouseInvInterfaceIndex != anInt1085) {
						RSInterface class9 = RSInterface.interfaceCache[anInt1084];
						int j1 = 0;
						if (anInt913 == 1 && class9.contentType == 206)
							j1 = 1;
						if (class9.inv[mouseInvInterfaceIndex] <= 0)
							j1 = 0;
						if (class9.aBoolean235) {
							int l2 = anInt1085;
							int l3 = mouseInvInterfaceIndex;
							class9.inv[l3] = class9.inv[l2];
							class9.invStackSizes[l3] = class9.invStackSizes[l2];
							class9.inv[l2] = -1;
							class9.invStackSizes[l2] = 0;
						} else if (j1 == 1) {
							int i3 = anInt1085;
							for (int i4 = mouseInvInterfaceIndex; i3 != i4;)
								if (i3 > i4) {
									class9.swapInventoryItems(i3, i3 - 1);
									i3--;
								} else if (i3 < i4) {
									class9.swapInventoryItems(i3, i3 + 1);
									i3++;
								}

						} else {
							class9.swapInventoryItems(anInt1085,
									mouseInvInterfaceIndex);
						}
						stream.createFrame(214);
						stream.method433(anInt1084);
						stream.method424(j1);
						stream.method433(anInt1085);
						stream.method431(mouseInvInterfaceIndex);
					}
				} else if ((anInt1253 == 1 || menuHasAddFriend(menuActionRow - 1))
						&& menuActionRow > 2)
					determineMenuSize();
				else if (menuActionRow > 0)
					doAction(menuActionRow - 1);
				atInventoryLoopCycle = 10;
				super.clickMode3 = 0;
			}
		}
		if (WorldController.anInt470 != -1) {
			int k = WorldController.anInt470;
			int k1 = WorldController.anInt471;
			boolean flag = doWalkTo(0, 0, 0, 0, myPlayer.smallY[0], 0, 0, k1,
					myPlayer.smallX[0], true, k);
			WorldController.anInt470 = -1;
			if (flag) {
				crossX = super.saveClickX;
				crossY = super.saveClickY;
				crossType = 1;
				crossIndex = 0;
			}
		}
		if (super.clickMode3 == 1 && aString844 != null) {
			aString844 = null;
			inputTaken = true;
			super.clickMode3 = 0;
		}
		if (!processMenuClick()) {
			processMainScreenClick();
			processTabAreaClick();
			processChatModeClick();
			if (quickChat)
				processQuickChatArea();
			processMapAreaMouse();
		}
		if (super.clickMode3 == 1) {
			if (super.saveClickX >= 522 && super.saveClickX <= 558
					&& super.saveClickY >= 124 && super.saveClickY < 161) {
				worldMap[0] = !worldMap[0];
				System.out.println(worldMap[0]);
			}
		}
		if (super.clickMode2 == 1 || super.clickMode3 == 1)
			anInt1213++;
		if (anInt1500 != 0 || anInt1044 != 0 || anInt1129 != 0) {
			if (anInt1501 < 50 && !menuOpen) {
				anInt1501++;
				if (anInt1501 == 50) {
					if (anInt1500 != 0) {
						inputTaken = true;
					}
					if (anInt1044 != 0) {
						needDrawTabArea = true;
					}
				}
			}
		} else if (anInt1501 > 0) {
			anInt1501--;
		}
		if (loadingStage == 2)
			method108();
		if (loadingStage == 2 && aBoolean1160)
			calcCameraPos();
		for (int i1 = 0; i1 < 5; i1++)
			anIntArray1030[i1]++;

		method73();
		super.idleTime++;
		if (super.idleTime > 15000) {
			anInt1011 = 250;
			super.idleTime -= 500;
			stream.createFrame(202);
		}
		anInt988++;
		if (anInt988 > 500) {
			anInt988 = 0;
			int l1 = (int) (Math.random() * 8D);
			if ((l1 & 1) == 1)
				cameraOffsetX += anInt1279;
			if ((l1 & 2) == 2)
				cameraOffsetY += anInt1132;
			if ((l1 & 4) == 4)
				viewRotationOffset += anInt897;
		}
		if (cameraOffsetX < -50)
			anInt1279 = 2;
		if (cameraOffsetX > 50)
			anInt1279 = -2;
		if (cameraOffsetY < -55)
			anInt1132 = 2;
		if (cameraOffsetY > 55)
			anInt1132 = -2;
		if (viewRotationOffset < -40)
			anInt897 = 1;
		if (viewRotationOffset > 40)
			anInt897 = -1;
		anInt1254++;
		if (anInt1254 > 500) {
			anInt1254 = 0;
			int i2 = (int) (Math.random() * 8D);
			if ((i2 & 1) == 1)
				minimapRotation += anInt1210;
			if ((i2 & 2) == 2)
				minimapZoom += anInt1171;
		}
		if (minimapRotation < -60)
			anInt1210 = 2;
		if (minimapRotation > 60)
			anInt1210 = -2;
		if (minimapZoom < -20)
			anInt1171 = 1;
		if (minimapZoom > 10)
			anInt1171 = -1;
		anInt1010++;
		if (anInt1010 > 50)
			stream.createFrame(0);
		try {
			if (socketStream != null && stream.currentOffset > 0) {
				socketStream.queueBytes(stream.currentOffset, stream.buffer);
				stream.currentOffset = 0;
				anInt1010 = 0;
			}
		} catch (IOException _ex) {
			dropClient();
		} catch (Exception exception) {
			resetLogout();
		}
	}

	private void method63() {
		Class30_Sub1 class30_sub1 = (Class30_Sub1) aClass19_1179
				.getFront();
		for (; class30_sub1 != null; class30_sub1 = (Class30_Sub1) aClass19_1179
				.reverseGetNext())
			if (class30_sub1.anInt1294 == -1) {
				class30_sub1.anInt1302 = 0;
				method89(class30_sub1);
			} else {
				class30_sub1.unlink();
			}

	}

	private void resetImageProducers() {
		if (GraphicsBuffer_1107 != null)
			return;
		super.fullGameScreen = null;
		chatAreaIP = null;
		mapAreaIP = null;
		tabAreaIP = null;
		gameScreenIP = null;
		GraphicsBuffer_1125 = null;
		GraphicsBuffer_1110 = new RSImageProducer(128, 265, getGameComponent());
		DrawingArea.setAllPixelsToZero();
		GraphicsBuffer_1111 = new RSImageProducer(128, 265, getGameComponent());
		DrawingArea.setAllPixelsToZero();
		GraphicsBuffer_1107 = new RSImageProducer(509, 171, getGameComponent());
		DrawingArea.setAllPixelsToZero();
		new RSImageProducer(360, 132, getGameComponent());
		DrawingArea.setAllPixelsToZero();
		titleScreen = new RSImageProducer(getClientWidth(), getClientHeight(),
				getGameComponent());
		DrawingArea.setAllPixelsToZero();
		new RSImageProducer(202, 238, getGameComponent());
		DrawingArea.setAllPixelsToZero();
		new RSImageProducer(203, 238, getGameComponent());
		DrawingArea.setAllPixelsToZero();
		new RSImageProducer(74, 94, getGameComponent());
		DrawingArea.setAllPixelsToZero();
		new RSImageProducer(75, 94, getGameComponent());
		DrawingArea.setAllPixelsToZero();
		if (titleStreamLoader != null) {
			loadTitleScreen();
		}
		welcomeScreenRaised = true;
	}

	public void drawSmoothLoading(int i, String s) {
		checkSize();
		for (int f = lastPercent; f <= i; f++)
			drawLoadingText(f, s);
	}

	void drawLoadingText(int i, String s) {
		lastPercent = i;
		aString1049 = s;
		resetImageProducers();
		if (titleStreamLoader == null) {
			super.drawLoadingText(i, s);
			return;
		}
		int width = (int) Math.round(i * 7.35);
		titleBox[10] = new Sprite("Login/titlebox 10");
		titleBox[11] = new Sprite("Login/titlebox 11");
		titleScreen.initDrawingArea();
		if (LoadedBG) {
			backgroundFix = new Sprite("Background");
			backgroundFix.drawARGBImage(0, 0);
			LoadedBG = false;
		}
		titleBox[10].drawARGBImage(0, 454);
		titleBox[11].drawARGBImage(16, 467);
		getDraw().drawAlphaGradient((16 + width), 467, (734 - width), 29,
				0x000000, 0x000000, 200);
		smallText.drawText(0xFFFFFF, s + " - " + i + "%", 486, 385); // Line
		titleScreen.drawGraphics(0, super.graphics, 0);
	}

	public DrawingArea Dinstance = new DrawingArea();

	public DrawingArea getDraw() {
		return Dinstance;
	}

	private void resetImage() {
		DrawingArea.setAllPixelsToZero();
	}

	private void method65(int i, int j, int k, int l, RSInterface class9,
			int i1, boolean flag, int j1) {
		int anInt992;
		if (aBoolean972)
			anInt992 = 32;
		else
			anInt992 = 0;
		aBoolean972 = false;
		if (k >= i && k < i + 16 && l >= i1 && l < i1 + 16) {
			class9.scrollPosition -= anInt1213 * 4;
			if (flag) {
				needDrawTabArea = true;
			}
		} else if (k >= i && k < i + 16 && l >= (i1 + j) - 16 && l < i1 + j) {
			class9.scrollPosition += anInt1213 * 4;
			if (flag) {
				needDrawTabArea = true;
			}
		} else if (k >= i - anInt992 && k < i + 16 + anInt992 && l >= i1 + 16
				&& l < (i1 + j) - 16 && anInt1213 > 0) {
			int l1 = ((j - 32) * j) / j1;
			if (l1 < 8)
				l1 = 8;
			int i2 = l - i1 - 16 - l1 / 2;
			int j2 = j - 32 - l1;
			class9.scrollPosition = ((j1 - j) * i2) / j2;
			if (flag)
				needDrawTabArea = true;
			aBoolean972 = true;
		}
	}

	private boolean method66(int i, int j, int k, int id) {
		int j1 = worldController.method304(plane, k, j, i);
		if (i == -1)
			return false;
		int k1 = j1 & 0x1f;
		int l1 = j1 >> 6 & 3;
			if (k1 == 10 || k1 == 11 || k1 == 22) {
				ObjectDef class46 = ObjectDef.forID(id);
				int i2;
				int j2;
				if (l1 == 0 || l1 == 2) {
					i2 = class46.anInt744;
					j2 = class46.anInt761;
				} else {
					i2 = class46.anInt761;
					j2 = class46.anInt744;
				}
				int k2 = class46.anInt768;
				if (l1 != 0)
					k2 = (k2 << l1 & 0xf) + (k2 >> 4 - l1);
				doWalkTo(2, 0, j2, 0, myPlayer.smallY[0], i2, k2, j,
						myPlayer.smallX[0], false, k);
			} else {
				doWalkTo(2, l1, 0, k1 + 1, myPlayer.smallY[0], 0, 0, j,
						myPlayer.smallX[0], false, k);
			}
			crossX = super.saveClickX;
			crossY = super.saveClickY;
			crossType = 2;
			crossIndex = 0;
			return true;
	}

	public final CRC32 aCRC32_930 = new CRC32();

	private StreamLoader streamLoaderForName(int i, String s, String s1, int j,int k) {
		byte abyte0[] = null;
		int l = 5;
		try {
			if (cacheIndices[0] != null)
				abyte0 = cacheIndices[0].get(i);
		} catch (Exception _ex) {
		}

		if (abyte0 != null) {
			if (Configuration.JAGCACHED_ENABLED) {
				aCRC32_930.reset();
				aCRC32_930.update(abyte0);
				int i1 = (int) aCRC32_930.getValue();
				if (i1 != j)
					abyte0 = null;
			}
		}

		if (abyte0 != null) {
			StreamLoader streamLoader = new StreamLoader(abyte0);
			return streamLoader;
		}
		int j1 = 0;
		while (abyte0 == null) {
			String s2 = "Unknown error";
			drawLoadingText(k, "Requesting " + s);
			try {
				int k1 = 0;
				DataInputStream datainputstream = openJagGrabInputStream(s1 + j);
				byte abyte1[] = new byte[6];
				datainputstream.readFully(abyte1, 0, 6);
				Stream stream = new Stream(abyte1);
				stream.currentOffset = 3;
				int i2 = stream.read3Bytes() + 6;
				int j2 = 6;
				abyte0 = new byte[i2];
				System.arraycopy(abyte1, 0, abyte0, 0, 6);

				while (j2 < i2) {
					int l2 = i2 - j2;
					if (l2 > 1000)
						l2 = 1000;
					int j3 = datainputstream.read(abyte0, j2, l2);
					if (j3 < 0) {
						s2 = "Length error: " + j2 + "/" + i2;
						throw new IOException("EOF");
					}
					j2 += j3;
					int k3 = (j2 * 100) / i2;
					if (k3 != k1)
						drawLoadingText(k, "Loading " + s + " - " + k3 + "%");
					k1 = k3;
				}
				datainputstream.close();
				try {
					if (cacheIndices[0] != null)
						cacheIndices[0].method234(abyte0.length, abyte0, i);
				} catch (Exception _ex) {
					cacheIndices[0] = null;
				}

				if (abyte0 != null) {
					if (Configuration.JAGCACHED_ENABLED) {
						aCRC32_930.reset();
						aCRC32_930.update(abyte0);
						int i3 = (int) aCRC32_930.getValue();
						if (i3 != j) {
							abyte0 = null;
							j1++;
							s2 = "Checksum error: " + i3;
						}
					}
				}

			} catch (IOException ioexception) {
				if (s2.equals("Unknown error"))
					s2 = "Connection error";
				abyte0 = null;
			} catch (NullPointerException _ex) {
				s2 = "Null error";
				abyte0 = null;
				if (!signlink.reporterror)
					return null;
			} catch (ArrayIndexOutOfBoundsException _ex) {
				s2 = "Bounds error";
				abyte0 = null;
				if (!signlink.reporterror)
					return null;
			} catch (Exception _ex) {
				s2 = "Unexpected error";
				abyte0 = null;
				if (!signlink.reporterror)
					return null;
			}
			if (abyte0 == null) {
				for (int l1 = l; l1 > 0; l1--) {
					if (j1 >= 3) {
						drawLoadingText(k, "Game updated - please reload page");
						l1 = 10;
					} else {
						drawLoadingText(k, s2 + " - Retrying in " + l1);
					}
					try {
						Thread.sleep(1000L);
					} catch (Exception _ex) {
					}
				}

				l *= 2;
				if (l > 60)
					l = 60;
				aBoolean872 = !aBoolean872;
			}

		}

		StreamLoader streamLoader_1 = new StreamLoader(abyte0);
		return streamLoader_1;
	}

	private void dropClient() throws ClassNotFoundException,
	InstantiationException, IllegalAccessException,
	UnsupportedLookAndFeelException {
		if (anInt1011 > 0) {
			resetLogout();
			return;
		}
		gameScreenIP.initDrawingArea();
		reestablish.drawSprite(7, 4);
		gameScreenIP.drawGraphics(4, super.graphics, 4);
		anInt1021 = 0;
		destX = 0;
		RSSocket rsSocket = socketStream;
		loggedIn = false;
		loginFailures = 0;
		if (logger != null) {
			logger.setVisible(false);
			logger = null;
		}
		login(myUsername, myPassword, true);
		if (!loggedIn)
			resetLogout();
		try {
			rsSocket.close();
		} catch (Exception _ex) {
		}
	}

	private void doAction(int i) {
		if (i < 0)
			return;
		if (inputDialogState != 0 && inputDialogState != 3) {
			inputDialogState = 0;
			inputTaken = true;
		}
		int j = menuActionCmd2[i];
		int k = menuActionCmd3[i];
		int cmd4 = menuActionCmd4[i];
		int l = menuActionID[i];
		int i1 = menuActionCmd1[i];
		int x = j;
		int y = k;
		int id = (i1 > 32767 ? cmd4 : i1 >> 14 & 0x7fff);
		if (l >= 2000)
			l -= 2000;
		if (l == 104) {
			RSInterface class9_1 = RSInterface.interfaceCache[k];
			spellID = class9_1.id;
			if (!Autocast) {
				Autocast = true;
				autocastId = class9_1.id;
				stream.createFrame(185);
				stream.writeWord(class9_1.id);
			} else if (autocastId == class9_1.id) {
				Autocast = false;
				autocastId = 0;
				stream.createFrame(185);
				stream.writeWord(class9_1.id);
			} else if (autocastId != class9_1.id) {
				Autocast = true;
				autocastId = class9_1.id;
				stream.createFrame(185);
				stream.writeWord(class9_1.id);
			}
		}
		if (l == 696) {
			viewRotation = 0;
			anInt1184 = 120;

		}
		if (l == 1251) {
			buttonclicked = false;
			inputString = "[A]" + GEItemId;
			RSInterface.interfaceCache[24654].sprite1 = RSInterface.interfaceCache[24654].setSprite;
			RSInterface.interfaceCache[24656].sprite1 = RSInterface.interfaceCache[24656].setSprite;
			sendPacket(1003);
		}
		if (l == 1007) {
			canGainXP = canGainXP ? false : true;
		}
		if (l == 1006 && !showBonus) {
			if (!gains.isEmpty()) {
				gains.removeAll(gains);
			}
			showXP = showXP ? false : true;
		}
		if (l == 1030 && !showXP) {
			showBonus = showBonus ? false : true;
		}
		if (l == 1005) {
			openQuickChat();
		}
		if (l == 1004) {
			quickChat = false;
			canTalk = true;
			inputTaken = true;
		}
		if (l == 1014) {
			running = !running;
			sendPacket185(19158);
		}
		if (l == 1013) {// xp counter reset
			sendPacket185(18222);
		}
		if (l == 1076) {
			if (menuOpen) {
				needDrawTabArea = true;
				tabID = tabHover;
				tabAreaAltered = true;
			}
		}
		if (l == 1026) {// Cast Special Attack
			if (choosingLeftClick) {
				leftClick = 7;
				choosingLeftClick = false;
			} else
				sendPacket185(15660);
		}
		if (l == 1025) {
			if (choosingLeftClick) {
				leftClick = -1;
				choosingLeftClick = false;
			} else {
				leftClick = -1;
				choosingLeftClick = true;
			}
		}
		if (l == 1024) {// Follower Details
			if (choosingLeftClick) {
				leftClick = 6;
				choosingLeftClick = false;
			} else
				sendPacket185(15661);
		}
		if (l == 1023) {// Attack
			if (choosingLeftClick) {
				leftClick = 5;
				choosingLeftClick = false;
			} else
				sendPacket185(15662);
		}
		if (l == 1022) {// Interact
			if (choosingLeftClick) {
				leftClick = 4;
				choosingLeftClick = false;
			} else
				sendPacket185(15663);
		}
		if (l == 1021) {// Renew Familiar
			if (choosingLeftClick) {
				leftClick = 3;
				choosingLeftClick = false;
			} else
				sendPacket185(15664);
		}
		if (l == 1020) {// Tale BoB
			if (choosingLeftClick) {
				leftClick = 2;
				choosingLeftClick = false;
			} else
				sendPacket185(15665);
		}
		if (l == 1019) {// Dismiss
			if (choosingLeftClick) {
				leftClick = 1;
				choosingLeftClick = false;
			} else
				sendPacket185(15666);
		}
		if (l == 1027) {// Dismiss
			leftClick = -1;
			choosingLeftClick = true;
		}
		if (l == 1018) {// Call Follower
			if (choosingLeftClick) {
				leftClick = 0;
				choosingLeftClick = false;
			} else
				sendPacket185(15667);
		}
		if (l == 13003) {
			stream.createFrame(185);
			stream.writeWord(menuActionName[i].contains("Cast") ? 15004 : 15003);
		}
		if (l == 13004) {
			stream.createFrame(185);
			stream.writeWord(15005);
		}
		if (l == 13005) {
			stream.createFrame(185);
			stream.writeWord(15006);
		}
		if (l == 13006) {
			stream.createFrame(185);
			stream.writeWord(15007);
		}
		if (l == 13007) {
			stream.createFrame(185);
			stream.writeWord(15008);
		}
		if (l == 582) {
			NPC npc = npcArray[i1];
			if (npc != null) {
				doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, npc.smallY[0],
						myPlayer.smallX[0], false, npc.smallX[0]);
				crossX = super.saveClickX;
				crossY = super.saveClickY;
				crossType = 2;
				crossIndex = 0;
				stream.createFrame(57);
				stream.method432(anInt1285);
				stream.method432(i1);
				stream.method431(anInt1283);
				stream.method432(anInt1284);
			}
		}
		if (l == 234) {
			boolean flag1 = doWalkTo(2, 0, 0, 0, myPlayer.smallY[0], 0, 0, k,
					myPlayer.smallX[0], false, j);
			if (!flag1)
				flag1 = doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, k,
						myPlayer.smallX[0], false, j);
			crossX = super.saveClickX;
			crossY = super.saveClickY;
			crossType = 2;
			crossIndex = 0;
			stream.createFrame(236);
			stream.method431(k + baseY);
			stream.writeWord(i1);
			stream.method431(j + baseX);
		}
		if (l == 62 && method66(i1, y, x, id)) {
			stream.createFrame(192);
			stream.writeWord(anInt1284);
			stream.method431(id);
			stream.method433(y + baseY);
			stream.method431(anInt1283);
			stream.method433(x + baseX);
			stream.writeWord(anInt1285);
		}
		if (l == 511) {
			boolean flag2 = doWalkTo(2, 0, 0, 0, myPlayer.smallY[0], 0, 0, k,
					myPlayer.smallX[0], false, j);
			if (!flag2)
				flag2 = doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, k,
						myPlayer.smallX[0], false, j);
			crossX = super.saveClickX;
			crossY = super.saveClickY;
			crossType = 2;
			crossIndex = 0;
			stream.createFrame(25);
			stream.method431(anInt1284);
			stream.method432(anInt1285);
			stream.writeWord(i1);
			stream.method432(k + baseY);
			stream.method433(anInt1283);
			stream.writeWord(j + baseX);
		}
		if (l == 74) {
			stream.createFrame(122);
			stream.method433(k);
			stream.method432(j);
			stream.method431(i1);
			atInventoryLoopCycle = 0;
			atInventoryInterface = k;
			atInventoryIndex = j;
			atInventoryInterfaceType = 2;
			if (RSInterface.interfaceCache[k].parentID == openInterfaceID)
				atInventoryInterfaceType = 1;
			if (RSInterface.interfaceCache[k].parentID == backDialogID)
				atInventoryInterfaceType = 3;
		}
		if (l == 315) {
			RSInterface class9 = RSInterface.interfaceCache[k];
			boolean flag8 = true;
			if (class9.contentType > 0)
				flag8 = promptUserForInput(class9);
			if (flag8) {

				switch (k) {
				case 19144:
					sendFrame248(15106, 3213);
					method60(15106);
					inputTaken = true;
					break;
				default:
					stream.createFrame(185);
					stream.writeWord(k);
					break;

				}
			}
		}
		switch (l) {
		case 1500: // Toggle quick prayers
			int currentPray;
			currentPray = Integer
					.parseInt(RSInterface.interfaceCache[4012].message);
			if (currentPray != 0)
				prayClicked = !prayClicked;
			else {
				prayClicked = false;
				pushMessage("You have run out of prayer points!", 0, "");
			}
			stream.createFrame(185);
			stream.writeWord(6000);
			break;

		case 1506: // Select quick prayers
			stream.createFrame(185);
			stream.writeWord(6001);
			break;
		}
		if (l == 561) {
			Player player = playerArray[i1];
			if (player != null) {
				doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0,
						player.smallY[0], myPlayer.smallX[0], false,
						player.smallX[0]);
				crossX = super.saveClickX;
				crossY = super.saveClickY;
				crossType = 2;
				crossIndex = 0;
				anInt1188 += i1;
				if (anInt1188 >= 90) {
					stream.createFrame(136);
					anInt1188 = 0;
				}
				stream.createFrame(128);
				stream.writeWord(i1);
			}
		}
		if (l == 20) {
			NPC class30_sub2_sub4_sub1_sub1_1 = npcArray[i1];
			if (class30_sub2_sub4_sub1_sub1_1 != null) {
				doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0,
						class30_sub2_sub4_sub1_sub1_1.smallY[0],
						myPlayer.smallX[0], false,
						class30_sub2_sub4_sub1_sub1_1.smallX[0]);
				crossX = super.saveClickX;
				crossY = super.saveClickY;
				crossType = 2;
				crossIndex = 0;
				stream.createFrame(155);
				stream.method431(i1);
			}
		}
		if (l == 779) {
			Player class30_sub2_sub4_sub1_sub2_1 = playerArray[i1];
			if (class30_sub2_sub4_sub1_sub2_1 != null) {
				doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0,
						class30_sub2_sub4_sub1_sub2_1.smallY[0],
						myPlayer.smallX[0], false,
						class30_sub2_sub4_sub1_sub2_1.smallX[0]);
				crossX = super.saveClickX;
				crossY = super.saveClickY;
				crossType = 2;
				crossIndex = 0;
				stream.createFrame(153);
				stream.method431(i1);
			}
		}
		if (l == 516)
			if (!menuOpen)
				worldController.method312(super.saveClickY - 4,
						super.saveClickX - 4);
			else
				worldController.method312(k - 4, j - 4);
		if (l == 1062) {
			anInt924 += baseX;
			if (anInt924 >= 113) {
				stream.createFrame(183);
				stream.writeDWordBigEndian(0xe63271);
				anInt924 = 0;
			}
			method66(i1, y, x, id);
			stream.createFrame(228);
			stream.method432(id);
			stream.method432(y + baseY);
			stream.writeWord(x + baseX);
		}
		if (l == 679 && !aBoolean1149) {
			stream.createFrame(40);
			stream.writeWord(k);
			aBoolean1149 = true;
		}
		if (l == 431) {
			stream.createFrame(129);
			stream.method432(j);
			stream.writeWord(k);
			stream.method432(i1);
			atInventoryLoopCycle = 0;
			atInventoryInterface = k;
			atInventoryIndex = j;
			atInventoryInterfaceType = 2;
			if (RSInterface.interfaceCache[k].parentID == openInterfaceID)
				atInventoryInterfaceType = 1;
			if (RSInterface.interfaceCache[k].parentID == backDialogID)
				atInventoryInterfaceType = 3;
		}
		if (l == 337 || l == 42 || l == 792 || l == 322) {
			String s = menuActionName[i];
			int k1 = s.indexOf("@whi@");
			if (k1 != -1) {
				try {
					String name = s.substring(k1 + 5);
					long l3 = TextClass.longForName(s.substring(
							k1 + 5 + (name.indexOf("@") == 0 ? 5 : 0)).trim());
					if (l == 337)
						addFriend(l3);
					if (l == 42)
						addIgnore(l3);
					if (l == 792)
						delFriend(l3);
					if (l == 322)
						delIgnore(l3);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		if (l == 1315) {
			inputString = "[NOT]" + clanName;
			sendPacket(1003);
		}
		if (l == 1316) {
			inputString = "[REC]" + clanName;
			sendPacket(1003);
		}
		if (l == 1317) {
			inputString = "[COR]" + clanName;
			sendPacket(1003);
		}
		if (l == 1318) {
			inputString = "[SER]" + clanName;
			sendPacket(1003);
		}
		if (l == 1319) {
			inputString = "[LIE]" + clanName;
			sendPacket(1003);
		}
		if (l == 1320) {
			inputString = "[CAP]" + clanName;
			sendPacket(1003);
		}
		if (l == 1321) {
			inputString = "[GEN]" + clanName;
			sendPacket(1003);
		}
		if (l == 53) {
			stream.createFrame(135);
			stream.method431(j);
			stream.method432(k);
			stream.method431(i1);
			atInventoryLoopCycle = 0;
			atInventoryInterface = k;
			atInventoryIndex = j;
			atInventoryInterfaceType = 2;
			if (RSInterface.interfaceCache[k].parentID == openInterfaceID)
				atInventoryInterfaceType = 1;
			if (RSInterface.interfaceCache[k].parentID == backDialogID)
				atInventoryInterfaceType = 3;
		}
		if (l == 539) {
			stream.createFrame(16);
			stream.method432(i1);
			stream.method433(j);
			stream.method433(k);
			atInventoryLoopCycle = 0;
			atInventoryInterface = k;
			atInventoryIndex = j;
			atInventoryInterfaceType = 2;
			if (RSInterface.interfaceCache[k].parentID == openInterfaceID)
				atInventoryInterfaceType = 1;
			if (RSInterface.interfaceCache[k].parentID == backDialogID)
				atInventoryInterfaceType = 3;
		}
		if (l == 484 || l == 6) {
			String s1 = menuActionName[i];
			int l1 = s1.indexOf("@whi@");
			if (l1 != -1) {
				s1 = s1.substring(l1 + 5).trim();
				String s7 = TextClass.fixName(TextClass.nameForLong(TextClass
						.longForName(s1)));
				boolean flag9 = false;
				for (int j3 = 0; j3 < playerCount; j3++) {
					Player class30_sub2_sub4_sub1_sub2_7 = playerArray[playerIndices[j3]];
					if (class30_sub2_sub4_sub1_sub2_7 == null
							|| class30_sub2_sub4_sub1_sub2_7.name == null
							|| !class30_sub2_sub4_sub1_sub2_7.name
							.equalsIgnoreCase(s7))
						continue;
					doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0,
							class30_sub2_sub4_sub1_sub2_7.smallY[0],
							myPlayer.smallX[0], false,
							class30_sub2_sub4_sub1_sub2_7.smallX[0]);
					if (l == 484) {
						stream.createFrame(39);
						stream.method431(playerIndices[j3]);
					}
					if (l == 6) {
						anInt1188 += i1;
						if (anInt1188 >= 90) {
							stream.createFrame(136);
							anInt1188 = 0;
						}
						stream.createFrame(128);
						stream.writeWord(playerIndices[j3]);
					}
					flag9 = true;
					break;
				}

				if (!flag9)
					pushMessage("Unable to find " + s7, 0, "");
			}
		}
		if (l == 870) {
			stream.createFrame(53);
			stream.writeWord(j);
			stream.method432(anInt1283);
			stream.method433(i1);
			stream.writeWord(anInt1284);
			stream.method431(anInt1285);
			stream.writeWord(k);
			atInventoryLoopCycle = 0;
			atInventoryInterface = k;
			atInventoryIndex = j;
			atInventoryInterfaceType = 2;
			if (RSInterface.interfaceCache[k].parentID == openInterfaceID)
				atInventoryInterfaceType = 1;
			if (RSInterface.interfaceCache[k].parentID == backDialogID)
				atInventoryInterfaceType = 3;
		}
		switch (k) {
		case 40049:
			toggleSize(2);
			break;
		case 40046:
			toggleSize(1);
			break;
		case 40043:
			toggleSize(0);
			break;
		case 40039:
			clearTopInterfaces();
			break;

		}
		if (l == 847) {
			stream.createFrame(87);
			stream.method432(i1);
			stream.writeWord(k);
			stream.method432(j);
			atInventoryLoopCycle = 0;
			atInventoryInterface = k;
			atInventoryIndex = j;
			atInventoryInterfaceType = 2;
			if (RSInterface.interfaceCache[k].parentID == openInterfaceID)
				atInventoryInterfaceType = 1;
			if (RSInterface.interfaceCache[k].parentID == backDialogID)
				atInventoryInterfaceType = 3;
		}
		if (l == 626) {
			RSInterface class9_1 = RSInterface.interfaceCache[k];
			spellSelected = 1;
			spellID = class9_1.id;
			anInt1137 = k;
			spellUsableOn = class9_1.spellUsableOn;
			itemSelected = 0;
			needDrawTabArea = true;
			spellID = class9_1.id;
			String s4 = class9_1.selectedActionName;
			if (s4.indexOf(" ") != -1)
				s4 = s4.substring(0, s4.indexOf(" "));
			String s8 = class9_1.selectedActionName;
			if (s8.indexOf(" ") != -1)
				s8 = s8.substring(s8.indexOf(" ") + 1);
			spellTooltip = s4 + " " + class9_1.spellName + " " + s8;
			// class9_1.sprite1.drawSprite(class9_1.anInt263, class9_1.anInt265,
			// 0xffffff);
			// class9_1.sprite1.drawSprite(200,200);
			// System.out.println("Sprite: " + class9_1.sprite1.toString());
			if (spellUsableOn == 16) {
				needDrawTabArea = true;
				tabID = 3;
				tabAreaAltered = true;
			}
			return;
		}
		if (l == 78) {
			stream.createFrame(117);
			stream.method433(k);
			stream.method433(i1);
			stream.method431(j);
			atInventoryLoopCycle = 0;
			atInventoryInterface = k;
			atInventoryIndex = j;
			atInventoryInterfaceType = 2;
			if (RSInterface.interfaceCache[k].parentID == openInterfaceID)
				atInventoryInterfaceType = 1;
			if (RSInterface.interfaceCache[k].parentID == backDialogID)
				atInventoryInterfaceType = 3;
		}
		if (l == 27) {
			Player class30_sub2_sub4_sub1_sub2_2 = playerArray[i1];
			if (class30_sub2_sub4_sub1_sub2_2 != null) {
				doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0,
						class30_sub2_sub4_sub1_sub2_2.smallY[0],
						myPlayer.smallX[0], false,
						class30_sub2_sub4_sub1_sub2_2.smallX[0]);
				crossX = super.saveClickX;
				crossY = super.saveClickY;
				crossType = 2;
				crossIndex = 0;
				anInt986 += i1;
				if (anInt986 >= 54) {
					stream.createFrame(189);
					stream.writeWordBigEndian(234);
					anInt986 = 0;
				}
				stream.createFrame(73);
				stream.method431(i1);
			}
		}
		if (l == 213) {
			boolean flag3 = doWalkTo(2, 0, 0, 0, myPlayer.smallY[0], 0, 0, k,
					myPlayer.smallX[0], false, j);
			if (!flag3)
				flag3 = doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, k,
						myPlayer.smallX[0], false, j);
			crossX = super.saveClickX;
			crossY = super.saveClickY;
			crossType = 2;
			crossIndex = 0;
			stream.createFrame(79);
			stream.method431(k + baseY);
			stream.writeWord(i1);
			stream.method432(j + baseX);
		}
		if (l == 632) {
			stream.createFrame(145);
			stream.method432(k);
			stream.method432(j);
			stream.method432(i1);
			atInventoryLoopCycle = 0;
			atInventoryInterface = k;
			atInventoryIndex = j;
			atInventoryInterfaceType = 2;
			if (RSInterface.interfaceCache[k].parentID == openInterfaceID)
				atInventoryInterfaceType = 1;
			if (RSInterface.interfaceCache[k].parentID == backDialogID)
				atInventoryInterfaceType = 3;
		}
		if (l == 888) {
			inputString = "[BS1]";
			sendPacket(1003);
		}
		if (l == 889) {
			inputString = "[BS2]";
			sendPacket(1003);
		}
		if (l == 890) {
			inputString = "[BB1]";
			sendPacket(1003);
		}
		if (l == 891) {
			inputString = "[BB2]";
			sendPacket(1003);
		}

		if (l == 1004) {
			if (tabInterfaceIDs[10] != -1) {
				needDrawTabArea = true;
				tabID = 10;
				tabAreaAltered = true;
			}
		}
		if (l == 1003) {
			clanChatMode = 2;
			inputTaken = true;
		}
		if (l == 1002) {
			clanChatMode = 1;
			inputTaken = true;
		}
		if (l == 1001) {
			clanChatMode = 0;
			inputTaken = true;
		}
		if (l == 1000) {
			cButtonCPos = 4;
			chatTypeView = 11;
			inputTaken = true;
		}
		if (l == 999) {
			cButtonCPos = 0;
			chatTypeView = 0;
			inputTaken = true;
		}
		if (l == 998) {
			cButtonCPos = 1;
			chatTypeView = 5;
			inputTaken = true;
		}
		if (l == 997) {
			publicChatMode = 3;
			inputTaken = true;
		}
		if (l == 996) {
			publicChatMode = 2;
			inputTaken = true;
		}
		if (l == 995) {
			publicChatMode = 1;
			inputTaken = true;
		}
		if (l == 994) {
			publicChatMode = 0;
			inputTaken = true;
		}
		if (l == 993) {
			cButtonCPos = 2;
			chatTypeView = 1;
			inputTaken = true;
		}
		if (l == 992) {
			privateChatMode = 2;
			inputTaken = true;
		}
		if (l == 991) {
			privateChatMode = 1;
			inputTaken = true;
		}
		if (l == 990) {
			privateChatMode = 0;
			inputTaken = true;
		}
		if (l == 989) {
			cButtonCPos = 3;
			chatTypeView = 2;
			inputTaken = true;
		}
		if (l == 987) {
			tradeMode = 2;
			inputTaken = true;
		}
		if (l == 986) {
			tradeMode = 1;
			inputTaken = true;
		}
		if (l == 985) {
			tradeMode = 0;
			inputTaken = true;
		}
		if (l == 984) {
			cButtonCPos = 5;
			chatTypeView = 3;
			inputTaken = true;
		}
		if (l == 983) {
			duelMode = 2;
			inputTaken = true;
		}
		if (l == 982) {
			duelMode = 1;
			inputTaken = true;
		}
		if (l == 981) {
			duelMode = 0;
			inputTaken = true;
		}
		if (l == 980) {
			cButtonCPos = 6;
			chatTypeView = 4;
			inputTaken = true;
		}
		if (l == 798) {
			gameChatMode = 4;
			inputTaken = true;
			filterMessages = true;
			inputTaken = true;
		}
		if (l == 797) {
			gameChatMode = 5;
			inputTaken = true;
			filterMessages = false;
			inputTaken = true;
		}
		if (l == 493) {
			stream.createFrame(75);
			stream.method433(k);
			stream.method431(j);
			stream.method432(i1);
			atInventoryLoopCycle = 0;
			atInventoryInterface = k;
			atInventoryIndex = j;
			atInventoryInterfaceType = 2;
			if (RSInterface.interfaceCache[k].parentID == openInterfaceID)
				atInventoryInterfaceType = 1;
			if (RSInterface.interfaceCache[k].parentID == backDialogID)
				atInventoryInterfaceType = 3;
		}
		if (l == 652) {
			boolean flag4 = doWalkTo(2, 0, 0, 0, myPlayer.smallY[0], 0, 0, k,
					myPlayer.smallX[0], false, j);
			if (!flag4)
				flag4 = doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, k,
						myPlayer.smallX[0], false, j);
			crossX = super.saveClickX;
			crossY = super.saveClickY;
			crossType = 2;
			crossIndex = 0;
			stream.createFrame(156);
			stream.method432(j + baseX);
			stream.method431(k + baseY);
			stream.method433(i1);
		}
		if (l == 94) {
			boolean flag5 = doWalkTo(2, 0, 0, 0, myPlayer.smallY[0], 0, 0, k,
					myPlayer.smallX[0], false, j);
			if (!flag5)
				flag5 = doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, k,
						myPlayer.smallX[0], false, j);
			crossX = super.saveClickX;
			crossY = super.saveClickY;
			crossType = 2;
			crossIndex = 0;
			stream.createFrame(181);
			stream.method431(k + baseY);
			stream.writeWord(i1);
			stream.method431(j + baseX);
			stream.method432(anInt1137);
		}
		if (l == 646) {
			stream.createFrame(185);
			stream.writeWord(k);
			RSInterface class9_2 = RSInterface.interfaceCache[k];
			if (class9_2.valueIndexArray != null
					&& class9_2.valueIndexArray[0][0] == 5) {
				int i2 = class9_2.valueIndexArray[0][1];
				if (variousSettings[i2] != class9_2.requiredValues[0]) {
					variousSettings[i2] = class9_2.requiredValues[0];
					handleActions(i2);
					needDrawTabArea = true;
				}
			}
		}
		if (l == 225) {
			NPC class30_sub2_sub4_sub1_sub1_2 = npcArray[i1];
			if (class30_sub2_sub4_sub1_sub1_2 != null) {
				doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0,
						class30_sub2_sub4_sub1_sub1_2.smallY[0],
						myPlayer.smallX[0], false,
						class30_sub2_sub4_sub1_sub1_2.smallX[0]);
				crossX = super.saveClickX;
				crossY = super.saveClickY;
				crossType = 2;
				crossIndex = 0;
				anInt1226 += i1;
				if (anInt1226 >= 85) {
					stream.createFrame(230);
					stream.writeWordBigEndian(239);
					anInt1226 = 0;
				}
				stream.createFrame(17);
				stream.method433(i1);
			}
		}
		if (l == 965) {
			NPC class30_sub2_sub4_sub1_sub1_3 = npcArray[i1];
			if (class30_sub2_sub4_sub1_sub1_3 != null) {
				doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0,
						class30_sub2_sub4_sub1_sub1_3.smallY[0],
						myPlayer.smallX[0], false,
						class30_sub2_sub4_sub1_sub1_3.smallX[0]);
				crossX = super.saveClickX;
				crossY = super.saveClickY;
				crossType = 2;
				crossIndex = 0;
				anInt1134++;
				if (anInt1134 >= 96) {
					stream.createFrame(152);
					stream.writeWordBigEndian(88);
					anInt1134 = 0;
				}
				stream.createFrame(21);
				stream.writeWord(i1);
			}
		}
		if (l == 413) {
			NPC class30_sub2_sub4_sub1_sub1_4 = npcArray[i1];
			if (class30_sub2_sub4_sub1_sub1_4 != null) {
				doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0,
						class30_sub2_sub4_sub1_sub1_4.smallY[0],
						myPlayer.smallX[0], false,
						class30_sub2_sub4_sub1_sub1_4.smallX[0]);
				crossX = super.saveClickX;
				crossY = super.saveClickY;
				crossType = 2;
				crossIndex = 0;
				stream.createFrame(131);
				stream.method433(i1);
				stream.method432(anInt1137);
			}
		}
		if (l == 200)
			clearTopInterfaces();
		if (l == 1025) {
			NPC class30_sub2_sub4_sub1_sub1_5 = npcArray[i1];
			if (class30_sub2_sub4_sub1_sub1_5 != null) {
				EntityDef entityDef = class30_sub2_sub4_sub1_sub1_5.desc;
				if (entityDef.childrenIDs != null)
					entityDef = entityDef.method161();
				if (entityDef != null) {
					String s9;
					if (entityDef.description != null)
						s9 = new String(entityDef.description);
					else
						s9 = "You don't find anything interesting about the "
								+ entityDef.name + ".";
					pushMessage(s9, 0, "");
				}
			}
		}
		if (l == 900) {
			method66(i1, y, x, id);
			stream.createFrame(252);
			stream.method433(id);
			stream.method431(y + baseY);
			stream.method432(x + baseX);
		}
		if (l == 412) {
			NPC class30_sub2_sub4_sub1_sub1_6 = npcArray[i1];
			if (class30_sub2_sub4_sub1_sub1_6 != null) {
				doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0,
						class30_sub2_sub4_sub1_sub1_6.smallY[0],
						myPlayer.smallX[0], false,
						class30_sub2_sub4_sub1_sub1_6.smallX[0]);
				crossX = super.saveClickX;
				crossY = super.saveClickY;
				crossType = 2;
				crossIndex = 0;
				stream.createFrame(72);
				stream.method432(i1);
			}
		}
		if (l == 365) {
			Player class30_sub2_sub4_sub1_sub2_3 = playerArray[i1];
			if (class30_sub2_sub4_sub1_sub2_3 != null) {
				doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0,
						class30_sub2_sub4_sub1_sub2_3.smallY[0],
						myPlayer.smallX[0], false,
						class30_sub2_sub4_sub1_sub2_3.smallX[0]);
				crossX = super.saveClickX;
				crossY = super.saveClickY;
				crossType = 2;
				crossIndex = 0;
				stream.createFrame(249);
				stream.method432(i1);
				stream.method431(anInt1137);
			}
		}
		if (l == 729) {
			Player class30_sub2_sub4_sub1_sub2_4 = playerArray[i1];
			if (class30_sub2_sub4_sub1_sub2_4 != null) {
				doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0,
						class30_sub2_sub4_sub1_sub2_4.smallY[0],
						myPlayer.smallX[0], false,
						class30_sub2_sub4_sub1_sub2_4.smallX[0]);
				crossX = super.saveClickX;
				crossY = super.saveClickY;
				crossType = 2;
				crossIndex = 0;
				stream.createFrame(39);
				stream.method431(i1);
			}
		}
		if (l == 577) {
			Player class30_sub2_sub4_sub1_sub2_5 = playerArray[i1];
			if (class30_sub2_sub4_sub1_sub2_5 != null) {
				doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0,
						class30_sub2_sub4_sub1_sub2_5.smallY[0],
						myPlayer.smallX[0], false,
						class30_sub2_sub4_sub1_sub2_5.smallX[0]);
				crossX = super.saveClickX;
				crossY = super.saveClickY;
				crossType = 2;
				crossIndex = 0;
				stream.createFrame(139);
				stream.method431(i1);
			}
		}
		if (l == 956 && method66(i1, y, x, id)) {
			stream.createFrame(35);
			stream.method431(x + baseX);
			stream.method432(anInt1137);
			stream.method432(y + baseY);
			stream.method431(id);
		}
		if (l == 567) {
			boolean flag6 = doWalkTo(2, 0, 0, 0, myPlayer.smallY[0], 0, 0, k,
					myPlayer.smallX[0], false, j);
			if (!flag6)
				flag6 = doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, k,
						myPlayer.smallX[0], false, j);
			crossX = super.saveClickX;
			crossY = super.saveClickY;
			crossType = 2;
			crossIndex = 0;
			stream.createFrame(23);
			stream.method431(k + baseY);
			stream.method431(i1);
			stream.method431(j + baseX);
		}
		if (l == 867) {
			if ((i1 & 3) == 0)
				anInt1175++;
			if (anInt1175 >= 59) {
				stream.createFrame(200);
				stream.writeWord(25501);
				anInt1175 = 0;
			}
			stream.createFrame(43);
			stream.method431(k);
			stream.method432(i1);
			stream.method432(j);
			atInventoryLoopCycle = 0;
			atInventoryInterface = k;
			atInventoryIndex = j;
			atInventoryInterfaceType = 2;
			if (RSInterface.interfaceCache[k].parentID == openInterfaceID)
				atInventoryInterfaceType = 1;
			if (RSInterface.interfaceCache[k].parentID == backDialogID)
				atInventoryInterfaceType = 3;
		}
		if (l == 543) {
			stream.createFrame(237);
			stream.writeWord(j);
			stream.method432(i1);
			stream.writeWord(k);
			stream.method432(anInt1137);
			atInventoryLoopCycle = 0;
			atInventoryInterface = k;
			atInventoryIndex = j;
			atInventoryInterfaceType = 2;
			if (RSInterface.interfaceCache[k].parentID == openInterfaceID)
				atInventoryInterfaceType = 1;
			if (RSInterface.interfaceCache[k].parentID == backDialogID)
				atInventoryInterfaceType = 3;
		}
		if (l == 606) {
			String s2 = menuActionName[i];
			int j2 = s2.indexOf("@whi@");
			if (j2 != -1)
				if (openInterfaceID == -1) {
					clearTopInterfaces();
					reportAbuseInput = s2.substring(j2 + 5).trim();
					canMute = false;
					for (int i3 = 0; i3 < RSInterface.interfaceCache.length; i3++) {
						if (RSInterface.interfaceCache[i3] == null
								|| RSInterface.interfaceCache[i3].contentType != 600)
							continue;
						reportAbuseInterfaceID = openInterfaceID = RSInterface.interfaceCache[i3].parentID;
						break;
					}

				} else {
					pushMessage(
							"Please close the interface you have open before using 'report abuse'",
							0, "");
				}
		}
		if (l == 491) {
			Player class30_sub2_sub4_sub1_sub2_6 = playerArray[i1];
			if (class30_sub2_sub4_sub1_sub2_6 != null) {
				doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0,
						class30_sub2_sub4_sub1_sub2_6.smallY[0],
						myPlayer.smallX[0], false,
						class30_sub2_sub4_sub1_sub2_6.smallX[0]);
				crossX = super.saveClickX;
				crossY = super.saveClickY;
				crossType = 2;
				crossIndex = 0;
				stream.createFrame(14);
				stream.method432(anInt1284);
				stream.writeWord(i1);
				stream.writeWord(anInt1285);
				stream.method431(anInt1283);
			}
		}
		if (l == 639) {
			String s3 = menuActionName[i];
			int k2 = s3.indexOf("@whi@");
			if (k2 != -1) {
				long l4 = TextClass.longForName(s3.substring(k2 + 5).trim());
				int k3 = -1;
				for (int i4 = 0; i4 < friendsCount; i4++) {
					if (friendsListAsLongs[i4] != l4)
						continue;
					k3 = i4;
					break;
				}

				if (k3 != -1 && friendsNodeIDs[k3] > 0) {
					inputTaken = true;
					inputDialogState = 0;
					showInput = true;
					promptInput = "";
					friendsListAction = 3;
					aLong953 = friendsListAsLongs[k3];
					promptMessage = "Enter message to send to "
							+ friendsList[k3];
				}
			}
		}
		if (l == 454) {
			stream.createFrame(41);
			stream.writeWord(i1);
			stream.method432(j);
			stream.method432(k);
			atInventoryLoopCycle = 0;
			atInventoryInterface = k;
			atInventoryIndex = j;
			atInventoryInterfaceType = 2;
			if (RSInterface.interfaceCache[k].parentID == openInterfaceID)
				atInventoryInterfaceType = 1;
			if (RSInterface.interfaceCache[k].parentID == backDialogID)
				atInventoryInterfaceType = 3;
		}
		if (l == 478) {
			NPC class30_sub2_sub4_sub1_sub1_7 = npcArray[i1];
			if (class30_sub2_sub4_sub1_sub1_7 != null) {
				doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0,
						class30_sub2_sub4_sub1_sub1_7.smallY[0],
						myPlayer.smallX[0], false,
						class30_sub2_sub4_sub1_sub1_7.smallX[0]);
				crossX = super.saveClickX;
				crossY = super.saveClickY;
				crossType = 2;
				crossIndex = 0;
				if ((i1 & 3) == 0)
					anInt1155++;
				if (anInt1155 >= 53) {
					stream.createFrame(85);
					stream.writeWordBigEndian(66);
					anInt1155 = 0;
				}
				stream.createFrame(18);
				stream.method431(i1);
			}
		}
		if (l == 113) {
			method66(i1, y, x, id);
			stream.createFrame(70);
			stream.method431(x + baseX);
			stream.writeWord(y + baseY);
			stream.method433(id);
		}
		if (l == 872) {
			method66(i1, y, x, id);
			stream.createFrame(234);
			stream.method433(x + baseX);
			stream.method432(id);
			stream.method433(y + baseY);
		}
		if (l == 502) {
			method66(i1, y, x, id);
			stream.createFrame(132);
			stream.method433(x + baseX);
			stream.writeWord(id);
			stream.method432(y + baseY);
		}
		if (l == 1125) {
			ItemDef itemDef = ItemDef.forID(i1);
			RSInterface class9_4 = RSInterface.interfaceCache[k];
			String s5;
			if (class9_4 != null && class9_4.invStackSizes[j] >= 0x186a0)
				s5 = class9_4.invStackSizes[j] + " x " + itemDef.name;
			else
				s5 = ItemList[i1].itemDescription;
			if (s5.contains("<")) {
				s5 = "Why are you examining the" + itemDef.name.toLowerCase() + "?";
			}
			pushMessage(s5, 0, "");
		}
		if (l == 169) {
			stream.createFrame(185);
			stream.writeWord(k);
			RSInterface class9_3 = RSInterface.interfaceCache[k];
			if (class9_3.valueIndexArray != null
					&& class9_3.valueIndexArray[0][0] == 5) {
				int l2 = class9_3.valueIndexArray[0][1];
				variousSettings[l2] = 1 - variousSettings[l2];
				handleActions(l2);
				needDrawTabArea = true;
			}
		}
		if (l == 447) {
			itemSelected = 1;
			anInt1283 = j;
			anInt1284 = k;
			anInt1285 = i1;
			selectedItemName = ItemDef.forID(i1).name;
			spellSelected = 0;
			needDrawTabArea = true;
			return;
		}
		if (l == 1226) {
			int j1 = i1 >> 14 & 0x7fff;
			ObjectDef class46 = ObjectDef.forID(j1);
			String s10;
			if (class46.description != null)
				s10 = new String(class46.description);
			else
				s10 = "You don't find anything interesting about the "
						+ class46.name + ".";
			pushMessage(s10, 0, "");
		}
		if (l == 244) {
			boolean flag7 = doWalkTo(2, 0, 0, 0, myPlayer.smallY[0], 0, 0, k,
					myPlayer.smallX[0], false, j);
			if (!flag7)
				flag7 = doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, k,
						myPlayer.smallX[0], false, j);
			crossX = super.saveClickX;
			crossY = super.saveClickY;
			crossType = 2;
			crossIndex = 0;
			stream.createFrame(253);
			stream.method431(j + baseX);
			stream.method433(k + baseY);
			stream.method432(i1);
		}
		if (l == 1448) {
			ItemDef itemDef_1 = ItemDef.forID(i1);
			String s6;
			s6 = ItemList[i1].itemDescription;
			if (s6.contains("<")) {
				s6 = "Why are you examining the " + itemDef_1.name.toLowerCase() + "?";
			}
			pushMessage(s6, 0, "");
		}
		itemSelected = 0;
		spellSelected = 0;
		needDrawTabArea = true;

	}

	private void method70() {
		anInt1251 = 0;
		int j = (myPlayer.x >> 7) + baseX;
		int k = (myPlayer.y >> 7) + baseY;
		if (j >= 3053 && j <= 3156 && k >= 3056 && k <= 3136)
			anInt1251 = 1;
		if (j >= 3072 && j <= 3118 && k >= 9492 && k <= 9535)
			anInt1251 = 1;
		if (anInt1251 == 1 && j >= 3139 && j <= 3199 && k >= 3008 && k <= 3062)
			anInt1251 = 0;
	}

	public void run() {
		if (drawFlames) {
			drawFlames();
		} else {
			super.run();
		}
	}

	private void build3dScreenMenu() {
		if (itemSelected == 0 && spellSelected == 0) {
			menuActionName[menuActionRow] = "Walk here";
			menuActionID[menuActionRow] = 516;
			menuActionCmd2[menuActionRow] = super.mouseX;
			menuActionCmd3[menuActionRow] = super.mouseY;
			menuActionRow++;
		}
		int lastUID = -1;
		for (int index = 0; index < Model.anInt1687; index++) {
			int uid = Model.anIntArray1688[index];
			int x = uid & 0x7f;
			int y = uid >> 7 & 0x7f;
		int resourceType = uid >> 29 & 3; // k1
				int resourceId = uid >> 14 & 0x7fff;
		if (uid == lastUID)
			continue;
		lastUID = uid;
		if (resourceType == 2
				&& worldController.method304(plane, x, y, uid) >= 0) {
			// System.out.println("Hovering object " + l1);
			if (resourceId != 1814)
				resourceId = Model.mapObjectIds[index];
			ObjectDef object = ObjectDef.forID(resourceId);
			if (object.configObjectIDs != null)
				object = object.method580();
			if (object == null || object.name == null
					|| object.name == "null")
				continue;
			if (itemSelected == 1) {
				menuActionName[menuActionRow] = "Use " + selectedItemName
						+ " -> @cya@" + object.name;
				menuActionID[menuActionRow] = 62;
				menuActionCmd1[menuActionRow] = uid;
				menuActionCmd2[menuActionRow] = x;
				menuActionCmd3[menuActionRow] = y;
				menuActionCmd4[menuActionRow] = resourceId;
				menuActionRow++;
			} else if (spellSelected == 1) {
				if ((spellUsableOn & 4) == 4) {
					menuActionName[menuActionRow] = spellTooltip + " @cya@"
							+ object.name;
					menuActionID[menuActionRow] = 956;
					menuActionCmd1[menuActionRow] = uid;
					menuActionCmd2[menuActionRow] = x;
					menuActionCmd3[menuActionRow] = y;
					menuActionCmd4[menuActionRow] = resourceId;
					menuActionRow++;
				}
			} else {
				if (object.actions != null) {
					for (int i2 = 4; i2 >= 0; i2--)
						if (object.actions[i2] != null) {
							menuActionName[menuActionRow] = object.actions[i2]
									+ " @cya@" + object.name;
							if (i2 == 0)
								menuActionID[menuActionRow] = 502;
							if (i2 == 1)
								menuActionID[menuActionRow] = 900;
							if (i2 == 2)
								menuActionID[menuActionRow] = 113;
							if (i2 == 3)
								menuActionID[menuActionRow] = 872;
							if (i2 == 4)
								menuActionID[menuActionRow] = 1062;
							menuActionCmd1[menuActionRow] = uid;
							menuActionCmd2[menuActionRow] = x;
							menuActionCmd3[menuActionRow] = y;
							menuActionCmd4[menuActionRow] = resourceId;
							menuActionRow++;
						}
				}
				menuActionName[menuActionRow] = "Examine @cya@"
						+ object.name
						+ ((myRights == 2 || myRights == 3) ? (" @gre@(@whi@"
								+ resourceId + "@gre@)")
								: "");
				menuActionID[menuActionRow] = 1226;
				menuActionCmd1[menuActionRow] = object.type << 14;
				menuActionCmd2[menuActionRow] = x;
				menuActionCmd3[menuActionRow] = y;
				menuActionCmd4[menuActionRow] = resourceId;
				menuActionRow++;
			}
		}
		if (resourceType == 1) {
			NPC npc = npcArray[resourceId];
			if (npc.desc.aByte68 == 1 && (npc.x & 0x7f) == 64
					&& (npc.y & 0x7f) == 64) {
				for (int j2 = 0; j2 < npcCount; j2++) {
					NPC npc2 = npcArray[npcIndices[j2]];
					if (npc2 != null && npc2 != npc
							&& npc2.desc.aByte68 == 1 && npc2.x == npc.x
							&& npc2.y == npc.y)
						buildAtNPCMenu(npc2.desc, npcIndices[j2], y, x);
				}

				for (int l2 = 0; l2 < playerCount; l2++) {
					Player player = playerArray[playerIndices[l2]];
					if (player != null && player.x == npc.x
							&& player.y == npc.y)
						buildAtPlayerMenu(x, playerIndices[l2], player, y);
				}

			}
			buildAtNPCMenu(npc.desc, resourceId, y, x);
		}
		if (resourceType == 0) {
			Player player = playerArray[resourceId];
			if ((player.x & 0x7f) == 64 && (player.y & 0x7f) == 64) {
				for (int k2 = 0; k2 < npcCount; k2++) {
					NPC class30_sub2_sub4_sub1_sub1_2 = npcArray[npcIndices[k2]];
					try {
						if (class30_sub2_sub4_sub1_sub1_2 != null
								&& class30_sub2_sub4_sub1_sub1_2.desc.aByte68 == 1
								&& class30_sub2_sub4_sub1_sub1_2.x == player.x
								&& class30_sub2_sub4_sub1_sub1_2.y == player.y)
							buildAtNPCMenu(
									class30_sub2_sub4_sub1_sub1_2.desc,
									npcIndices[k2], y, x);
					} catch (Exception _ex) {
					}
				}

				for (int i3 = 0; i3 < playerCount; i3++) {
					Player class30_sub2_sub4_sub1_sub2_2 = playerArray[playerIndices[i3]];
					if (class30_sub2_sub4_sub1_sub2_2 != null
							&& class30_sub2_sub4_sub1_sub2_2 != player
							&& class30_sub2_sub4_sub1_sub2_2.x == player.x
							&& class30_sub2_sub4_sub1_sub2_2.y == player.y)
						buildAtPlayerMenu(x, playerIndices[i3],
								class30_sub2_sub4_sub1_sub2_2, y);
				}

			}
			buildAtPlayerMenu(x, resourceId, player, y);
		}
		if (resourceType == 3) {
			Deque class19 = groundArray[plane][x][y];
			if (class19 != null) {
				for (Item item = (Item) class19.getFirst(); item != null; item = (Item) class19
						.getNext()) {
					ItemDef itemDef = ItemDef.forID(item.ID);
					if (itemSelected == 1) {
						menuActionName[menuActionRow] = "Use "
								+ selectedItemName + " with @lre@"
								+ itemDef.name;
						menuActionID[menuActionRow] = 511;
						menuActionCmd1[menuActionRow] = item.ID;
						menuActionCmd2[menuActionRow] = x;
						menuActionCmd3[menuActionRow] = y;
						menuActionRow++;
					} else if (spellSelected == 1) {
						if ((spellUsableOn & 1) == 1) {
							menuActionName[menuActionRow] = spellTooltip
									+ " @lre@" + itemDef.name;
							menuActionID[menuActionRow] = 94;
							menuActionCmd1[menuActionRow] = item.ID;
							menuActionCmd2[menuActionRow] = x;
							menuActionCmd3[menuActionRow] = y;
							menuActionRow++;
						}
					} else {
						for (int j3 = 4; j3 >= 0; j3--)
							if (itemDef.groundActions != null
							&& itemDef.groundActions[j3] != null) {
								menuActionName[menuActionRow] = itemDef.groundActions[j3]
										+ " @lre@" + itemDef.name;
								if (j3 == 0)
									menuActionID[menuActionRow] = 652;
								if (j3 == 1)
									menuActionID[menuActionRow] = 567;
								if (j3 == 2)
									menuActionID[menuActionRow] = 234;
								if (j3 == 3)
									menuActionID[menuActionRow] = 244;
								if (j3 == 4)
									menuActionID[menuActionRow] = 213;
								menuActionCmd1[menuActionRow] = item.ID;
								menuActionCmd2[menuActionRow] = x;
								menuActionCmd3[menuActionRow] = y;
								menuActionRow++;
							} else if (j3 == 2) {
								menuActionName[menuActionRow] = "Take @lre@"
										+ itemDef.name;
								menuActionID[menuActionRow] = 234;
								menuActionCmd1[menuActionRow] = item.ID;
								menuActionCmd2[menuActionRow] = x;
								menuActionCmd3[menuActionRow] = y;
								menuActionRow++;
							}
						menuActionName[menuActionRow] = "Examine @lre@"
								+ itemDef.name;
						menuActionID[menuActionRow] = 1448;
						menuActionCmd1[menuActionRow] = item.ID;
						menuActionCmd2[menuActionRow] = x;
						menuActionCmd3[menuActionRow] = y;
						menuActionRow++;
					}
				}

			}
		}
		}
	}

	public void cleanUpForQuit() {
		signlink.reporterror = false;
		try {
			if (socketStream != null)
				socketStream.close();
		} catch (Exception _ex) {
		}
		socketStream = null;
		stopMidi();
		if (mouseDetection != null)
			mouseDetection.running = false;
		mouseDetection = null;
		onDemandFetcher.dispose();
		onDemandFetcher = null;
		aStream_834 = null;
		prayClicked = false;
		stream = null;
		aStream_847 = null;
		inStream = null;
		anIntArray1234 = null;
		aByteArrayArray1183 = null;
		aByteArrayArray1247 = null;
		anIntArray1235 = null;
		anIntArray1236 = null;
		intGroundArray = null;
		byteGroundArray = null;
		worldController = null;
		aClass11Array1230 = null;
		anIntArrayArray901 = null;
		anIntArrayArray825 = null;
		bigX = null;
		bigY = null;
		aByteArray912 = null;
		tabAreaIP = null;
		leftFrame = null;
		topFrame = null;
		rightFrame = null;
		mapAreaIP = null;
		gameScreenIP = null;
		chatAreaIP = null;
		GraphicsBuffer_1125 = null;
		backgroundFix = null;
		/* Null pointers for custom sprites */
		loadingPleaseWait = null;
		reestablish = null;
		WorldOrb = null;
		HPBarFull = null;
		HPBarEmpty = null;
		HPBarBigEmpty = null;
		newMapBack = null;
		orbs = null;
		LOGOUT = null;
		/**/
		sideIcons = null;
		compass = null;
		hitMarks = null;
		headIcons = null;
		skullIcons = null;
		headIconsHint = null;
		crosses = null;
		mapDotItem = null;
		mapDotNPC = null;
		mapDotPlayer = null;
		mapDotFriend = null;
		mapDotTeam = null;
		mapScenes = null;
		mapFunctions = null;
		anIntArrayArray929 = null;
		playerArray = null;
		playerIndices = null;
		anIntArray894 = null;
		aStreamArray895s = null;
		anIntArray840 = null;
		npcArray = null;
		npcIndices = null;
		groundArray = null;
		aClass19_1179 = null;
		aClass19_1013 = null;
		aClass19_1056 = null;
		menuActionCmd2 = null;
		menuActionCmd3 = null;
		menuActionCmd4 = null;
		menuActionID = null;
		menuActionCmd1 = null;
		menuActionName = null;
		variousSettings = null;
		anIntArray1072 = null;
		anIntArray1073 = null;
		aClass30_Sub2_Sub1_Sub1Array1140 = null;
		miniMap = null;
		friendsList = null;
		friendsListAsLongs = null;
		friendsNodeIDs = null;
		GraphicsBuffer_1110 = null;
		GraphicsBuffer_1111 = null;
		GraphicsBuffer_1107 = null;
		titleScreen = null;
		multiOverlay = null;
		nullLoader();
		ObjectDef.nullLoader();
		EntityDef.nullLoader();
		ItemDef.nullLoader();
		Flo.cache = null;
		IDK.cache = null;
		RSInterface.interfaceCache = null;
		DummyClass.cache = null;
		Animation.anims = null;
		SpotAnim.cache = null;
		SpotAnim.aMRUNodes_415 = null;
		Varp.cache = null;
		super.fullGameScreen = null;
		Player.mruNodes = null;
		Texture.clearCache();
		WorldController.nullLoader();
		Model.nullLoader();
		FrameReader.nullLoader();
		System.gc();
	}

	private void printDebug() {
		System.out.println("============");
		System.out.println("flame-cycle:" + anInt1208);
		if (onDemandFetcher != null)
			System.out.println("Od-cycle:" + onDemandFetcher.onDemandCycle);
		System.out.println("loop-cycle:" + loopCycle);
		System.out.println("draw-cycle:" + anInt1061);
		System.out.println("ptype:" + opCode);
		System.out.println("psize:" + pktSize);
		if (socketStream != null)
			socketStream.printDebug();
		super.shouldDebug = true;
	}

	Component getGameComponent() {
		if (signlink.mainapp != null)
			return signlink.mainapp;
		if (super.mainFrame != null)
			return super.mainFrame;
		else
			return this;
	}

	private void method73() {
		do {
			int j = readChar(-796);
			if (j == -1)
				break;
			if (openInterfaceID != -1
					&& openInterfaceID == reportAbuseInterfaceID) {
				if (j == 8 && reportAbuseInput.length() > 0)
					reportAbuseInput = reportAbuseInput.substring(0,
							reportAbuseInput.length() - 1);
				if ((j >= 97 && j <= 122 || j >= 65 && j <= 90 || j >= 48
						&& j <= 57 || j == 32)
						&& reportAbuseInput.length() < 12)
					reportAbuseInput += (char) j;
			} else if (showInput) {
				if (j >= 32 && j <= 122 && promptInput.length() < 80) {
					promptInput += (char) j;
					inputTaken = true;
				}
				if (j == 8 && promptInput.length() > 0) {
					promptInput = promptInput.substring(0,
							promptInput.length() - 1);
					inputTaken = true;
				}
				if (j == 13 || j == 10) {
					showInput = false;
					inputTaken = true;
					if (friendsListAction == 1) {
						long l = TextClass.longForName(promptInput);
						addFriend(l);
					}
					if (interfaceButtonAction == 6199
							&& promptInput.length() > 0) {
						String inp = "";
						inp = inputString;
						inputString = "::[CN] " + promptInput;
						sendPacket(103);
						inputString = inp;
					}
					if (interfaceButtonAction == 6200
							&& promptInput.length() > 0) {
						String inp = "";
						inp = inputString;
						inputString = "::[NC] " + promptInput;
						sendPacket(103);
						inputString = inp;
					}
					if (friendsListAction == 2 && friendsCount > 0) {
						long l1 = TextClass.longForName(promptInput);
						delFriend(l1);
					}
					if (friendsListAction == 3 && promptInput.length() > 0) {
						stream.createFrame(126);
						stream.writeWordBigEndian(0);
						int k = stream.currentOffset;
						stream.writeQWord(aLong953);
						TextInput.method526(promptInput, stream);
						stream.writeBytes(stream.currentOffset - k);
						promptInput = TextInput.processText(promptInput);
						pushMessage(promptInput, 6, TextClass.fixName(TextClass
								.nameForLong(aLong953)));
						if (privateChatMode == 2) {
							privateChatMode = 1;
							stream.createFrame(95);
							stream.writeWordBigEndian(publicChatMode);
							stream.writeWordBigEndian(privateChatMode);
							stream.writeWordBigEndian(tradeMode);
						}
					}
					if (friendsListAction == 4 && ignoreCount < 100) {
						long l2 = TextClass.longForName(promptInput);
						addIgnore(l2);
					}
					if (friendsListAction == 5 && ignoreCount > 0) {
						long l3 = TextClass.longForName(promptInput);
						delIgnore(l3);
					}
					if (friendsListAction == 6) {
						long l3 = TextClass.longForName(promptInput);
						chatJoin(l3);
					}
				}
			} else if (inputDialogState == 1) {
				if (j >= 48 && j <= 57 && amountOrNameInput.length() < 10) {
					amountOrNameInput += (char) j;
					inputTaken = true;
					long l = Long.valueOf(amountOrNameInput);
					if (l == 0) {
						amountOrNameInput = "";
						inputTaken = true;
					}
				}
				if ((amountOrNameInput.length() > 0
						&& !amountOrNameInput.toLowerCase().contains("k")
						&& !amountOrNameInput.toLowerCase().contains("m") && !amountOrNameInput
						.toLowerCase().contains("b"))
						&& (j == 107 || j == 109 || j == 98)) {
					int am = 0;
					boolean addChar = true;
					long l = Long.valueOf(amountOrNameInput);
					if (l > 2147483647) {
						amountOrNameInput = "2147483647";
						inputTaken = true;
						addChar = false;
					} else {
						am = Integer.parseInt(amountOrNameInput);
					}
					if (j == 107 && am > 2147000) {
						addChar = false;
					}
					if (j == 109 && am > 2147) {
						addChar = false;
					}
					if (j == 98 && am > 2) {
						addChar = false;
					}

					if (addChar && am > 0) {
						amountOrNameInput += (char) j;
						inputTaken = true;
					}
				}
				if (j == 8 && amountOrNameInput.length() > 0) {
					amountOrNameInput = amountOrNameInput.substring(0,
							amountOrNameInput.length() - 1);
					inputTaken = true;
				}
				if (j == 13 || j == 10) {
					if (amountOrNameInput.length() > 0) {
						if (amountOrNameInput.toLowerCase().contains("k")) {
							amountOrNameInput = amountOrNameInput.replaceAll(
									"k", "000");
						} else if (amountOrNameInput.toLowerCase()
								.contains("m")) {
							amountOrNameInput = amountOrNameInput.replaceAll(
									"m", "000000");
						} else if (amountOrNameInput.toLowerCase()
								.contains("b")) {
							amountOrNameInput = amountOrNameInput.replaceAll(
									"b", "000000000");
						}
						long l = Long.valueOf(amountOrNameInput);

						if (l > 2147483647) {
							amountOrNameInput = "2147483647";
						}
						int amount = 0;
						amount = Integer.parseInt(amountOrNameInput);
						stream.createFrame(208);
						stream.writeDWord(amount);
					}
					inputDialogState = 0;
					inputTaken = true;
				}
				if (j == 13 || j == 10) {
					if (interfaceButtonAction == 1557
							&& amountOrNameInput.length() > 0) {
						if (amountOrNameInput.toLowerCase().contains("k")) {
							amountOrNameInput = amountOrNameInput.replaceAll(
									"k", "000");
						} else if (amountOrNameInput.toLowerCase()
								.contains("m")) {
							amountOrNameInput = amountOrNameInput.replaceAll(
									"m", "000000");
						} else if (amountOrNameInput.toLowerCase()
								.contains("b")) {
							amountOrNameInput = amountOrNameInput.replaceAll(
									"b", "000000000");
						}
						long l = Long.valueOf(amountOrNameInput);

						if (l > 2147483647) {
							amountOrNameInput = "2147483647";
						}
						TalkingFix = inputString;
						inputString = "::[L]" + amountOrNameInput;
						sendPacket(103);
						inputString = TalkingFix;
						inputTaken = true;
					}
					if (interfaceButtonAction == 1558
							&& amountOrNameInput.length() > 0) {
						if (amountOrNameInput.toLowerCase().contains("k")) {
							amountOrNameInput = amountOrNameInput.replaceAll(
									"k", "000");
						} else if (amountOrNameInput.toLowerCase()
								.contains("m")) {
							amountOrNameInput = amountOrNameInput.replaceAll(
									"m", "000000");
						} else if (amountOrNameInput.toLowerCase()
								.contains("b")) {
							amountOrNameInput = amountOrNameInput.replaceAll(
									"b", "000000000");
						}
						long l = Long.valueOf(amountOrNameInput);

						if (l > 2147483647) {
							amountOrNameInput = "2147483647";
						}
						TalkingFix = inputString;
						inputString = "::[E]" + amountOrNameInput;
						sendPacket(103);
						inputString = TalkingFix;
						inputTaken = true;
					}
					if (interfaceButtonAction == 1557
							&& amountOrNameInput.length() == 0
							|| interfaceButtonAction == 1558
							&& amountOrNameInput.length() == 0) {
						interfaceButtonAction = 0;
					}
				}
			} else if (inputDialogState == 3) {
				if (j == 10) {
					totalItemResults = 0;
					amountOrNameInput = "";
					inputDialogState = 0;
					inputTaken = true;
				}
				if (j == 13 || j == 10) {
					if (amountOrNameInput.length() == 0) {
						buttonclicked = false;
						interfaceButtonAction = 0;
					}
				}
				if (j >= 32 && j <= 122 && amountOrNameInput.length() < 40) {
					amountOrNameInput += (char) j;
					inputTaken = true;
				}
				if (j == 8 && amountOrNameInput.length() > 0) {
					amountOrNameInput = amountOrNameInput.substring(0,
							amountOrNameInput.length() - 1);
					inputTaken = true;
				}
			} else if (inputDialogState == 2) {
				if (j >= 32 && j <= 122 && amountOrNameInput.length() < 12) {
					amountOrNameInput += (char) j;
					inputTaken = true;
				}
				if (j == 8 && amountOrNameInput.length() > 0) {
					amountOrNameInput = amountOrNameInput.substring(0,
							amountOrNameInput.length() - 1);
					inputTaken = true;
				}
				if (j == 13 || j == 10) {
					if (amountOrNameInput.length() > 0) {
						stream.createFrame(60);
						stream.writeQWord(TextClass
								.longForName(amountOrNameInput));
					}
					inputDialogState = 0;
					inputTaken = true;
				}
			} else if (backDialogID == -1) {
				if (j == 9)
					tabToReplyPm();
				if (j >= 32 && j <= 122 && inputString.length() < 80) {
					inputString += (char) j;
					inputTaken = true;
				}
				if (j == 8 && inputString.length() > 0) {
					inputString = inputString.substring(0,
							inputString.length() - 1);
					inputTaken = true;
				}
				if ((j == 13 || j == 10) && inputString.length() > 0) {
					if (myRights == 2 || myRights == 3
							|| Configuration.server.equals("127.0.0.1")) {
						if (inputString.startsWith("//setspecto")) {
							int amt = Integer.parseInt(inputString
									.substring(12));
							anIntArray1045[300] = amt;
							if (variousSettings[300] != amt) {
								variousSettings[300] = amt;
								handleActions(300);
								needDrawTabArea = true;
								if (dialogID != -1)
									inputTaken = true;
							}
						}
						if (inputString.equals("clientdrop"))
							try {
								dropClient();
							} catch (ClassNotFoundException e) {
								e.printStackTrace();
							} catch (InstantiationException e) {
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							} catch (UnsupportedLookAndFeelException e) {
								e.printStackTrace();
							}
						if (inputString.equals("::dumpmodels"))
							models();
						if (inputString.equals("dumpitems")) {
							ItemDef.dumpData();
						}
						if (inputString.startsWith("::object ")) {
							int id = Integer.parseInt(inputString.substring(9));
							ObjectDef o = ObjectDef.forID(id);
							if (o.objectModelIDs != null) {
								for (int model = 0; model < o.objectModelIDs.length; model++)
									pushMessage("Name: " + o.name
											+ " ,HasActions: " + o.hasActions
											+ " ,Models: "
											+ o.objectModelIDs[model], 0, "");
							}
						}
						if (inputString.startsWith("::3234")) {
							int id = Integer.parseInt(inputString.substring(6));
							EntityDef npc = EntityDef.forID(id);
							if (npc.anIntArray94 != null) {
								for (int modelid = 0; modelid < npc.anIntArray94.length; modelid++)
									pushMessage("Name: " + npc.name
											+ " ,Models: "
											+ npc.anIntArray94[modelid], 0, "");
							}
						}
						if (inputString.equals("::lag"))
							printDebug();
						//						if (inputString.equals("::prefetchmusic")) {//TODO dont touch
						//							for (int j1 = 0; j1 < onDemandFetcher
						//									.getVersionCount(2); j1++)
						//								onDemandFetcher.loadPriorityFile((byte) 1, 2,
						//										j1);
						//
						//						}
						if (inputString.equals("::fpson"))
							fpsOn = true;
						if (inputString.equals("::fpsoff"))
							fpsOn = false;
						if (inputString.equals("::dataon"))
							clientData = true;
						if (inputString.equals("::dataoff"))
							clientData = false;
						if (inputString.equals("::noclip")) {
							for (int k1 = 0; k1 < 4; k1++) {
								for (int i2 = 1; i2 < 103; i2++) {
									for (int k2 = 1; k2 < 103; k2++)
										aClass11Array1230[k1].anIntArrayArray294[i2][k2] = 0;

								}
							}
						}
					}
					if (inputString.startsWith("/"))
						inputString = "::" + inputString;
					if (inputString.equals("add model")) {
						try {
							int ModelIndex = Integer.parseInt(JOptionPane
									.showInputDialog(this, "Enter model ID",
											"Model", 3));
							byte[] abyte0 = getModel(ModelIndex);
							if (abyte0 != null && abyte0.length > 0) {
								cacheIndices[1].method234(abyte0.length,
										abyte0, ModelIndex);
								pushMessage("Model: [" + ModelIndex
										+ "] added successfully!", 0, "");
							} else {
								pushMessage("Unable to find the model. "
										+ ModelIndex, 0, "");
							}
						} catch (Exception e) {
							pushMessage("Syntax - ::add model <path>", 0, "");
						}
					}
					if (inputString.startsWith("::")
							&& !inputString.startsWith("::[")) {
						stream.createFrame(103);
						stream.writeWordBigEndian(inputString.length() - 1);
						stream.writeString(inputString.substring(2));
					} else {
						String s = inputString.toLowerCase();
						int j2 = 0;
						if (s.startsWith("yellow:")) {
							j2 = 0;
							inputString = inputString.substring(7);
						} else if (s.startsWith("red:")) {
							j2 = 1;
							inputString = inputString.substring(4);
						} else if (s.startsWith("green:")) {
							j2 = 2;
							inputString = inputString.substring(6);
						} else if (s.startsWith("cyan:")) {
							j2 = 3;
							inputString = inputString.substring(5);
						} else if (s.startsWith("purple:")) {
							j2 = 4;
							inputString = inputString.substring(7);
						} else if (s.startsWith("white:")) {
							j2 = 5;
							inputString = inputString.substring(6);
						} else if (s.startsWith("flash1:")) {
							j2 = 6;
							inputString = inputString.substring(7);
						} else if (s.startsWith("flash2:")) {
							j2 = 7;
							inputString = inputString.substring(7);
						} else if (s.startsWith("flash3:")) {
							j2 = 8;
							inputString = inputString.substring(7);
						} else if (s.startsWith("glow1:")) {
							j2 = 9;
							inputString = inputString.substring(6);
						} else if (s.startsWith("glow2:")) {
							j2 = 10;
							inputString = inputString.substring(6);
						} else if (s.startsWith("glow3:")) {
							j2 = 11;
							inputString = inputString.substring(6);
						}
						s = inputString.toLowerCase();
						int i3 = 0;
						if (s.startsWith("wave:")) {
							i3 = 1;
							inputString = inputString.substring(5);
						} else if (s.startsWith("wave2:")) {
							i3 = 2;
							inputString = inputString.substring(6);
						} else if (s.startsWith("shake:")) {
							i3 = 3;
							inputString = inputString.substring(6);
						} else if (s.startsWith("scroll:")) {
							i3 = 4;
							inputString = inputString.substring(7);
						} else if (s.startsWith("slide:")) {
							i3 = 5;
							inputString = inputString.substring(6);
						}
						stream.createFrame(4);
						stream.writeWordBigEndian(0);
						int j3 = stream.currentOffset;
						stream.method425(i3);
						stream.method425(j2);
						aStream_834.currentOffset = 0;
						TextInput.method526(inputString, aStream_834);
						stream.method441(0, aStream_834.buffer,
								aStream_834.currentOffset);
						stream.writeBytes(stream.currentOffset - j3);
						inputString = TextInput.processText(inputString);
						myPlayer.textSpoken = inputString;
						myPlayer.anInt1513 = j2;
						myPlayer.anInt1531 = i3;
						myPlayer.textCycle = 150;
						pushMessage(myPlayer.textSpoken, 2, getPrefix(myRights)
								+ myPlayer.name);
						if (publicChatMode == 2) {
							publicChatMode = 3;
							stream.createFrame(95);
							stream.writeWordBigEndian(publicChatMode);
							stream.writeWordBigEndian(privateChatMode);
							stream.writeWordBigEndian(tradeMode);
						}
					}
					inputString = "";
					inputTaken = true;
				}
			}
		} while (true);
	}

	private void buildPublicChat(int j) {
		int l = 0;
		for (int index = 0; index < 500; index++) {
			if (chatMessages[index] == null)
				continue;
			if (chatTypeView != 1)
				continue;
			int type = chatTypes[index];
			String name = chatNames[index];
			// String message = chatMessages[index];
			int positionY = (70 - l * 14 + 42) + anInt1089 + 4 + 5;
			if (positionY < -23)
				break;
			if (name != null && name.indexOf("@") == 0) {
				name = name.substring(5);
			}
			if ((type == 1 || type == 2)
					&& (type == 1 || publicChatMode == 0 || publicChatMode == 1
					&& isFriendOrSelf(name))) {
				if (j > positionY - 14 && j <= positionY
						&& !name.equals(myPlayer.name)) {
					if (myRights >= 1) {
						menuActionName[menuActionRow] = "Report abuse @whi@"
								+ name;
						menuActionID[menuActionRow] = 606;
						menuActionRow++;
					}
					if (!isFriendOrSelf(name)) {
						menuActionName[menuActionRow] = "Add ignore @whi@"
								+ name;
						menuActionID[menuActionRow] = 42;
						menuActionRow++;
						menuActionName[menuActionRow] = "Add friend @whi@"
								+ name;
						menuActionID[menuActionRow] = 337;
						menuActionRow++;
					}
					if (isFriendOrSelf(name)) {
						menuActionName[menuActionRow] = "Message @whi@" + name;
						menuActionID[menuActionRow] = 639;
						menuActionRow++;
					}
				}
				l++;
			}
		}
	}

	private void buildFriendChat(int j) {
		int l = 0;
		for (int i1 = 0; i1 < 500; i1++) {
			if (chatMessages[i1] == null)
				continue;
			if (chatTypeView != 2)
				continue;
			int type = chatTypes[i1];
			String name = chatNames[i1];
			// String message = chatMessages[i1];
			int k1 = (70 - l * 14 + 42) + anInt1089 + 4 + 5;
			if (k1 < -23)
				break;
			if (name != null && name.indexOf("@") == 0) {
				// name = name.substring(5);
			}
			if ((type == 5 || type == 6)
					&& (splitPrivateChat == 0 || chatTypeView == 2)
					&& (type == 6 || privateChatMode == 0 || privateChatMode == 1
					&& isFriendOrSelf(name)))
				l++;
			if ((type == 3 || type == 7)
					&& (splitPrivateChat == 0 || chatTypeView == 2)
					&& (type == 7 || privateChatMode == 0 || privateChatMode == 1
					&& isFriendOrSelf(name))) {
				if (j > k1 - 14 && j <= k1) {
					if (myRights >= 1) {
						menuActionName[menuActionRow] = "Report abuse @whi@"
								+ name;
						menuActionID[menuActionRow] = 606;
						menuActionRow++;
					}
					if (!isFriendOrSelf(name)) {
						menuActionName[menuActionRow] = "Add ignore @whi@"
								+ name;
						menuActionID[menuActionRow] = 42;
						menuActionRow++;
						menuActionName[menuActionRow] = "Add friend @whi@"
								+ name;
						menuActionID[menuActionRow] = 337;
						menuActionRow++;
					}
					if (isFriendOrSelf(name)) {
						menuActionName[menuActionRow] = "Message @whi@" + name;
						menuActionID[menuActionRow] = 639;
						menuActionRow++;
					}
				}
				l++;
			}
		}
	}

	private void buildDuelorTrade(int j) {
		int l = 0;
		for (int i1 = 0; i1 < 500; i1++) {
			if (chatMessages[i1] == null)
				continue;
			if (chatTypeView != 3 && chatTypeView != 4)
				continue;
			int j1 = chatTypes[i1];
			String name = chatNames[i1];
			int k1 = (70 - l * 14 + 42) + anInt1089 + 4 + 5;
			if (k1 < -23)
				break;
			if (name != null && name.indexOf("@") == 0) {
				name = name.substring(5);
			}
			if (chatTypeView == 3
					&& j1 == 4
					&& (tradeMode == 0 || tradeMode == 1
					&& isFriendOrSelf(name))) {
				if (j > k1 - 14 && j <= k1) {
					menuActionName[menuActionRow] = "Accept trade @whi@" + name;
					menuActionID[menuActionRow] = 484;
					menuActionRow++;
				}
				l++;
			}
			if (chatTypeView == 4
					&& j1 == 8
					&& (tradeMode == 0 || tradeMode == 1
					&& isFriendOrSelf(name))) {
				if (j > k1 - 14 && j <= k1) {
					menuActionName[menuActionRow] = "Accept challenge @whi@"
							+ name;
					menuActionID[menuActionRow] = 6;
					menuActionRow++;
				}
				l++;
			}
			if (j1 == 12) {
				if (j > k1 - 14 && j <= k1) {
					menuActionName[menuActionRow] = "Go-to @blu@" + name;
					menuActionID[menuActionRow] = 915;
					menuActionRow++;
				}
				l++;
			}
		}
	}

	private void buildChatAreaMenu(int j) {
		int l = 0;
		for (int i1 = 0; i1 < 500; i1++) {
			if (chatMessages[i1] == null)
				continue;
			int j1 = chatTypes[i1];
			int k1 = (70 - l * 14 + 42) + anInt1089 + 4 + 5;
			if (k1 < -23)
				break;
			String name = chatNames[i1];
			if (chatTypeView == 1) {
				buildPublicChat(j);
				break;
			}
			if (chatTypeView == 2) {
				buildFriendChat(j);
				break;
			}
			if (chatTypeView == 3 || chatTypeView == 4) {
				buildDuelorTrade(j);
				break;
			}
			if (inputDialogState == 3) {
				buildItemSearch(j);
				break;
			}
			if (chatTypeView == 5) {
				break;
			}
			if (name != null && name.indexOf("@") == 0) {
				name = name.substring(5);
			}
			if (j1 == 0)
				l++;
			if ((j1 == 1 || j1 == 2)
					&& (j1 == 1 || publicChatMode == 0 || publicChatMode == 1
					&& isFriendOrSelf(name))) {
				if (j > k1 - 14 && j <= k1 && !name.equals(myPlayer.name)) {
					if (myRights >= 1) {
						menuActionName[menuActionRow] = "Report abuse @whi@"
								+ name;
						menuActionID[menuActionRow] = 606;
						menuActionRow++;
					}
					if (!isFriendOrSelf(name)) {
						menuActionName[menuActionRow] = "Add ignore @whi@"
								+ name;
						menuActionID[menuActionRow] = 42;
						menuActionRow++;
						menuActionName[menuActionRow] = "Add friend @whi@"
								+ name;
						menuActionID[menuActionRow] = 337;
						menuActionRow++;
					}
					if (isFriendOrSelf(name)) {
						menuActionName[menuActionRow] = "Message @whi@" + name;
						menuActionID[menuActionRow] = 639;
						menuActionRow++;
					}
				}
				l++;
			}
			if ((j1 == 3 || j1 == 7)
					&& splitPrivateChat == 0
					&& (j1 == 7 || privateChatMode == 0 || privateChatMode == 1
					&& isFriendOrSelf(name))) {
				if (j > k1 - 14 && j <= k1) {
					if (myRights >= 1) {
						menuActionName[menuActionRow] = "Report abuse @whi@"
								+ name;
						menuActionID[menuActionRow] = 606;
						menuActionRow++;
					}
					if (!isFriendOrSelf(name)) {
						menuActionName[menuActionRow] = "Add ignore @whi@"
								+ name;
						menuActionID[menuActionRow] = 42;
						menuActionRow++;
						menuActionName[menuActionRow] = "Add friend @whi@"
								+ name;
						menuActionID[menuActionRow] = 337;
						menuActionRow++;
					}
					if (isFriendOrSelf(name)) {
						menuActionName[menuActionRow] = "Message @whi@" + name;
						menuActionID[menuActionRow] = 639;
						menuActionRow++;
					}
				}
				l++;
			}
			if (j1 == 4
					&& (tradeMode == 0 || tradeMode == 1
					&& isFriendOrSelf(name))) {
				if (j > k1 - 14 && j <= k1) {
					menuActionName[menuActionRow] = "Accept trade @whi@" + name;
					menuActionID[menuActionRow] = 484;
					menuActionRow++;
				}
				l++;
			}
			if ((j1 == 5 || j1 == 6) && splitPrivateChat == 0
					&& privateChatMode < 2)
				l++;
			if (j1 == 8
					&& (tradeMode == 0 || tradeMode == 1
					&& isFriendOrSelf(name))) {
				if (j > k1 - 14 && j <= k1) {
					menuActionName[menuActionRow] = "Accept challenge @whi@"
							+ name;
					menuActionID[menuActionRow] = 6;
					menuActionRow++;
				}
				l++;
			}
		}
	}

	public static int totalItemResults;
	public String itemResultNames[] = new String[100];
	public int itemResultIDs[] = new int[100];
	public static int itemResultScrollPos;

	public void itemSearch(String n) {
		if (n == null || n.length() == 0) {
			totalItemResults = 0;
			return;
		}
		String searchName = n;
		String searchParts[] = new String[100];
		int totalResults = 0;
		do {
			int k = searchName.indexOf(" ");
			if (k == -1)
				break;
			String part = searchName.substring(0, k).trim();
			if (part.length() > 0)
				searchParts[totalResults++] = part.toLowerCase();
			searchName = searchName.substring(k + 1);
		} while (true);
		searchName = searchName.trim();
		if (searchName.length() > 0)
			searchParts[totalResults++] = searchName.toLowerCase();
		totalItemResults = 0;
		label0: for (int id = 0; id < ItemDef.totalItems; id++) {
			ItemDef item = ItemDef.forID(id);
			if (item.certTemplateID != -1 || item.lentItemID != -1
					|| item.name == null || item.name == "Picture"
					|| item.certID == 18786 || item.name == "Null")
				continue;
			String result = item.name.toLowerCase();
			for (int idx = 0; idx < totalResults; idx++)
				if (result.indexOf(searchParts[idx]) == -1)
					continue label0;

			// int value = getItemShopValue(id);

			if (ItemList[id].ShopValue != 0) {
				itemResultNames[totalItemResults] = result;
				itemResultIDs[totalItemResults] = id;
				totalItemResults++;
			}
			if (totalItemResults >= itemResultNames.length)
				return;
		}
	}

	private void buildItemSearch(int mouseY) {
		int y = 0;
		for (int idx = 0; idx < 100; idx++) {
			if (amountOrNameInput.length() == 0)
				return;
			else if (totalItemResults == 0)
				return;
			if (amountOrNameInput == "")
				return;
			String name = capitalizeFirstChar(itemResultNames[idx]);
			for (int i = 0; i <= 20; i++)
				if (name.contains(" <img=" + i + ">"))
					name = name.replaceAll(" <img=" + i + ">", "");
			int textY = (21 + y * 14) - itemResultScrollPos;
			if (mouseY > textY - 14 && mouseY <= textY && super.mouseX > 74
					&& super.mouseX < 495) {
				menuActionName[menuActionRow] = "" + name;
				menuActionID[menuActionRow] = 1251;
				menuActionRow++;
			}
			y++;
		}
	}

	private void drawFriendsListOrWelcomeScreen(RSInterface class9) {
		int j = class9.contentType;
		if (j >= 205 && j <= 205 + 25) {
			j -= 205;
			class9.message = setMessage(j);
			return;
		}
		if (j >= 1 && j <= 100 || j >= 701 && j <= 800) {
			if (j == 1 && anInt900 == 0) {
				class9.message = "Loading friend list";
				class9.atActionType = 0;
				return;
			}
			if (j == 1 && anInt900 == 1) {
				class9.message = "Connecting to friendserver";
				class9.atActionType = 0;
				return;
			}
			if (j == 2 && anInt900 != 2) {
				class9.message = "Please wait...";
				class9.atActionType = 0;
				return;
			}
			int k = friendsCount;
			if (anInt900 != 2)
				k = 0;
			if (j > 700)
				j -= 601;
			else
				j--;
			if (j >= k) {
				class9.message = "";
				class9.atActionType = 0;
				return;
			} else {
				class9.message = friendsList[j];
				class9.atActionType = 1;
				return;
			}
		}
		if (j == 901) {
			class9.message = friendsCount + " / 200";
			return;
		}
		if (j == 902) {
			class9.message = ignoreCount + " / 100";
			return;
		}
		if (j >= 101 && j <= 200 || j >= 801 && j <= 900) {
			int l = friendsCount;
			if (anInt900 != 2)
				l = 0;
			if (j > 800)
				j -= 701;
			else
				j -= 101;
			if (j >= l) {
				class9.message = "";
				class9.atActionType = 0;
				return;
			}
			if (friendsNodeIDs[j] == 0)
				class9.message = "@red@Offline";
			else if (friendsNodeIDs[j] == nodeID)
				class9.message = "@gre@Online"/* + (friendsNodeIDs[j] - 9) */;
			else
				class9.message = "@red@Offline"/* + (friendsNodeIDs[j] - 9) */;
			class9.atActionType = 1;
			return;
		}
		if (j == 203) {
			int i1 = friendsCount;
			if (anInt900 != 2)
				i1 = 0;
			class9.scrollMax = i1 * 15 + 20;
			if (class9.scrollMax <= class9.height)
				class9.scrollMax = class9.height + 1;
			return;
		}
		if (j >= 401 && j <= 500) {
			if ((j -= 401) == 0 && anInt900 == 0) {
				class9.message = "Loading ignore list";
				class9.atActionType = 0;
				return;
			}
			if (j == 1 && anInt900 == 0) {
				class9.message = "Please wait...";
				class9.atActionType = 0;
				return;
			}
			int j1 = ignoreCount;
			if (anInt900 == 0)
				j1 = 0;
			if (j >= j1) {
				class9.message = "";
				class9.atActionType = 0;
				return;
			} else {
				class9.message = TextClass.fixName(TextClass
						.nameForLong(ignoreListAsLongs[j]));
				class9.atActionType = 1;
				return;
			}
		}
		if (j == 503) {
			class9.scrollMax = ignoreCount * 15 + 20;
			if (class9.scrollMax <= class9.height)
				class9.scrollMax = class9.height + 1;
			return;
		}
		if (j == 327) {
			class9.modelRotation1 = 150;
			class9.modelRotation2 = (int) (Math.sin((double) loopCycle / 40D) * 256D) & 0x7ff;
			if (aBoolean1031) {
				for (int k1 = 0; k1 < 7; k1++) {
					int l1 = anIntArray1065[k1];
					if (l1 >= 0 && !IDK.cache[l1].method537())
						return;
				}

				aBoolean1031 = false;
				Model aclass30_sub2_sub4_sub6s[] = new Model[7];
				int i2 = 0;
				for (int j2 = 0; j2 < 7; j2++) {
					int k2 = anIntArray1065[j2];
					if (k2 >= 0)
						aclass30_sub2_sub4_sub6s[i2++] = IDK.cache[k2]
								.method538();
				}

				Model model = new Model(i2, aclass30_sub2_sub4_sub6s);
				for (int l2 = 0; l2 < 5; l2++)
					if (anIntArray990[l2] != 0) {
						model.method476(anIntArrayArray1003[l2][0],
								anIntArrayArray1003[l2][anIntArray990[l2]]);
						if (l2 == 1)
							model.method476(anIntArray1204[0],
									anIntArray1204[anIntArray990[l2]]);
					}

				model.method469();
				model.method470(Animation.anims[myPlayer.anInt1511].anIntArray353[0]);
				model.method479(64, 850, -30, -50, -30, true);
				class9.anInt233 = 5;
				class9.mediaID = 0;
				RSInterface.method208(aBoolean994, model);
			}
			return;
		}
		if (j == 328) {
			RSInterface rsInterface = class9;
			int verticleTilt = 150;
			int animationSpeed = (int) (Math.sin((double) loopCycle / 40D) * 256D) & 0x7ff;
			rsInterface.modelRotation1 = verticleTilt;
			rsInterface.modelRotation2 = animationSpeed;
			if (aBoolean1031) {
				Model characterDisplay = myPlayer.method452();
				for (int l2 = 0; l2 < 5; l2++)
					if (anIntArray990[l2] != 0) {
						characterDisplay.method476(anIntArrayArray1003[l2][0],
								anIntArrayArray1003[l2][anIntArray990[l2]]);
						if (l2 == 1)
							characterDisplay.method476(anIntArray1204[0],
									anIntArray1204[anIntArray990[l2]]);
					}
				int staticFrame = myPlayer.anInt1511;
				characterDisplay.method469();
				characterDisplay
				.method470(Animation.anims[staticFrame].anIntArray353[0]);
				// characterDisplay.method479(64, 850, -30, -50, -30, true);
				rsInterface.anInt233 = 5;
				rsInterface.mediaID = 0;
				RSInterface.method208(aBoolean994, characterDisplay);
			}
			return;
		}
		if (j == 324) {
			if (aClass30_Sub2_Sub1_Sub1_931 == null) {
				aClass30_Sub2_Sub1_Sub1_931 = class9.sprite1;
				aClass30_Sub2_Sub1_Sub1_932 = class9.sprite2;
			}
			if (aBoolean1047) {
				class9.sprite1 = aClass30_Sub2_Sub1_Sub1_932;
				return;
			} else {
				class9.sprite1 = aClass30_Sub2_Sub1_Sub1_931;
				return;
			}
		}
		if (j == 325) {
			if (aClass30_Sub2_Sub1_Sub1_931 == null) {
				aClass30_Sub2_Sub1_Sub1_931 = class9.sprite1;
				aClass30_Sub2_Sub1_Sub1_932 = class9.sprite2;
			}
			if (aBoolean1047) {
				class9.sprite1 = aClass30_Sub2_Sub1_Sub1_931;
				return;
			} else {
				class9.sprite1 = aClass30_Sub2_Sub1_Sub1_932;
				return;
			}
		}
		if (j == 600) {
			class9.message = reportAbuseInput;
			if (loopCycle % 20 < 10) {
				class9.message += "|";
				return;
			} else {
				class9.message += " ";
				return;
			}
		}
		if (j == 613)
			if (myRights >= 1) {
				if (canMute) {
					class9.textColor = 0xff0000;
					class9.message = "Moderator option: Mute player for 48 hours: <ON>";
				} else {
					class9.textColor = 0xffffff;
					class9.message = "Moderator option: Mute player for 48 hours: <OFF>";
				}
			} else {
				class9.message = "";
			}
		if (j == 650 || j == 655)
			if (anInt1193 != 0) {
				String s;
				if (daysSinceLastLogin == 0)
					s = "earlier today";
				else if (daysSinceLastLogin == 1)
					s = "yesterday";
				else
					s = daysSinceLastLogin + " days ago";
				class9.message = "You last logged in " + s + " from: "
						+ signlink.dns;
			} else {
				class9.message = "";
			}
		if (j == 651) {
			if (unreadMessages == 0) {
				class9.message = "0 unread messages";
				class9.textColor = 0xffff00;
			}
			if (unreadMessages == 1) {
				class9.message = "1 unread message";
				class9.textColor = 65280;
			}
			if (unreadMessages > 1) {
				class9.message = unreadMessages + " unread messages";
				class9.textColor = 65280;
			}
		}
		if (j == 652)
			if (daysSinceRecovChange == 201) {
				if (membersInt == 1)
					class9.message = "@yel@This is a non-members world: @whi@Since you are a member we";
				else
					class9.message = "";
			} else if (daysSinceRecovChange == 200) {
				class9.message = "You have not yet set any password recovery questions.";
			} else {
				String s1;
				if (daysSinceRecovChange == 0)
					s1 = "Earlier today";
				else if (daysSinceRecovChange == 1)
					s1 = "Yesterday";
				else
					s1 = daysSinceRecovChange + " days ago";
				class9.message = s1 + " you changed your recovery questions";
			}
		if (j == 653)
			if (daysSinceRecovChange == 201) {
				if (membersInt == 1)
					class9.message = "@whi@recommend you use a members world instead. You may use";
				else
					class9.message = "";
			} else if (daysSinceRecovChange == 200)
				class9.message = "We strongly recommend you do so now to secure your account.";
			else
				class9.message = "If you do not remember making this change then cancel it immediately";
		if (j == 654) {
			if (daysSinceRecovChange == 201)
				if (membersInt == 1) {
					class9.message = "@whi@this world but member benefits are unavailable whilst here.";
					return;
				} else {
					class9.message = "";
					return;
				}
			if (daysSinceRecovChange == 200) {
				class9.message = "Do this from the 'account management' area on our front webpage";
				return;
			}
			class9.message = "Do this from the 'account management' area on our front webpage";
		}
	}

	private void drawSplitPrivateChat() {
		if (splitPrivateChat == 0)
			return;
		TextDrawingArea textDrawingArea = normalFont;
		int i = 0;
		if (anInt1104 != 0)
			i = 1;
		for (int j = 0; j < 100; j++)
			if (chatMessages[j] != null) {
				int type = chatTypes[j];
				String name = chatNames[j];
				String prefixName = name;
				int rights = 0;
				if (name != null && name.indexOf("@") == 0) {
					name = name.substring(5);
				}
				if ((type == 3 || type == 7)
						&& (type == 7 || privateChatMode == 0 || privateChatMode == 1
						&& isFriendOrSelf(name))) {
					int l = (clientHeight - 174) - i * 13;
					int k1 = 4;
					textDrawingArea.method385(0, "From", l, k1);
					textDrawingArea.method385(65535, "From", l - 1, k1);
					k1 += textDrawingArea.getTextWidth("From ");
					if (prefixName != null && prefixName.indexOf("@") == 0)
						rights = getPrefixRights(prefixName.substring(0,
								prefixName.indexOf(name)));
					if (rights != 0) {
						modIcons[rights].drawSprite(k1, l - 12);
						k1 += 12;
					}
					textDrawingArea.method385(0, name + ": " + chatMessages[j],
							l, k1);
					textDrawingArea.method385(65535, name + ": "
							+ chatMessages[j], l - 1, k1);
					if (++i >= 5)
						return;
				}
				if (type == 5 && privateChatMode < 2) {
					int i1 = (clientHeight - 174) - i * 13;
					textDrawingArea.method385(0, chatMessages[j], i1, 4);
					textDrawingArea
					.method385(65535, chatMessages[j], i1 - 1, 4);
					if (++i >= 5)
						return;
				}
				if (type == 6 && privateChatMode < 2) {
					int j1 = (clientHeight - 174) - i * 13;
					int k1 = 4;
					textDrawingArea.method385(0, "To " + name + ": "
							+ chatMessages[j], j1, k1);
					textDrawingArea.method385(65535, "To " + name + ": "
							+ chatMessages[j], j1 - 1, k1);
					if (++i >= 5)
						return;
				}
			}
	}

	public void pushMessage(String s, int i, String s1) {
		for (int Fmessage = 0; Fmessage < filteredMessages.length; Fmessage++) {
			if (s.startsWith(filteredMessages[Fmessage]) && filterMessages
					&& s1 != myPlayer.name)
				return;
		}
		if (i == 0 && dialogID != -1) {
			aString844 = s;
			super.clickMode3 = 0;
		}
		if (backDialogID == -1)
			inputTaken = true;
		for (int j = 499; j > 0; j--) {
			chatTypes[j] = chatTypes[j - 1];
			chatNames[j] = chatNames[j - 1];
			chatMessages[j] = chatMessages[j - 1];
			chatRights[j] = chatRights[j - 1];
		}
		chatTypes[0] = i;
		chatNames[0] = s1;
		chatMessages[0] = s;
		chatRights[0] = rights;
	}

	public static void setTab(int id) {
		needDrawTabArea = true;
		tabID = id;
		tabAreaAltered = true;
	}

	private void resetImageProducers2() {
		if (chatAreaIP != null)
			return;
		nullLoader();
		super.fullGameScreen = null;
		GraphicsBuffer_1107 = null;
		titleScreen = null;
		GraphicsBuffer_1110 = null;
		GraphicsBuffer_1111 = null;
		chatAreaIP = new RSImageProducer(519, 165, getGameComponent());
		mapAreaIP = new RSImageProducer(249, 168, getGameComponent());
		DrawingArea.setAllPixelsToZero();
		cacheSprite[6].drawSprite(0, 0);
		tabAreaIP = new RSImageProducer(250, 335, getGameComponent());
		gameScreenIP = new RSImageProducer(clientSize == 0 ? 512 : clientWidth,
				clientSize == 0 ? 334 : clientHeight, getGameComponent());
		DrawingArea.setAllPixelsToZero();
		new RSImageProducer(269, 37, getGameComponent());
		GraphicsBuffer_1125 = new RSImageProducer(249, 45, getGameComponent());
		welcomeScreenRaised = true;
	}

	public String getDocumentBaseHost() {
		if (signlink.mainapp != null) {
			return signlink.mainapp.getDocumentBase().getHost().toLowerCase();
		}
		if (super.mainFrame != null) {
			return ""; // runescape.com <- removed for Jframe to work
		} else {
			return ""; // super.getDocumentBase().getHost().toLowerCase() <-
			// removed for Jframe to work
		}
	}

	/*
	 * private void method81(Sprite sprite, int j, int k) { int l = k * k + j *
	 * j; if(l > 4225 && l < 0x15f90) { int i1 = minimapInt1 + minimapInt2 &
	 * 0x7ff; int j1 = Model.modelIntArray1[i1]; int k1 =
	 * Model.modelIntArray2[i1]; j1 = (j1 * 256) / (minimapInt3 + 256); k1 = (k1
	 * * 256) / (minimapInt3 + 256); int l1 = j * j1 + k * k1 >> 16; int i2 = j
	 * * k1 - k * j1 >> 16; double d = Math.atan2(l1, i2); int j2 =
	 * (int)(Math.sin(d) * 63D); int k2 = (int)(Math.cos(d) * 57D);
	 * mapEdge.method353(83 - k2 - 20, d, (94 + j2 + 4) - 10); return; } else {
	 * markMinimap(sprite, k, j); } newMapBack.drawSprite(0, 0); }
	 */

	private void method81(Sprite sprite, int j, int k) {
		int l = k * k + j * j;
		if (l > 4225 && l < 0x15f90) {
			int i1 = viewRotation + minimapRotation & 0x7ff;
			int j1 = Model.modelIntArray1[i1];
			int k1 = Model.modelIntArray2[i1];
			j1 = (j1 * 256) / (minimapZoom + 256);
			k1 = (k1 * 256) / (minimapZoom + 256);
			int l1 = j * j1 + k * k1 >> 16;
		int i2 = j * k1 - k * j1 >> 16;
		double d = Math.atan2(l1, i2);
		int j2 = (int) (Math.sin(d) * 63D);
		int k2 = (int) (Math.cos(d) * 57D);
		mapEdge.method353(83 - k2 - 20, d, (94 + j2 + 4) - 10);
		} else {
			markMinimap(sprite, k, j);
		}
	}

	public static String capitalize(String s) {
		if(s == null)
			return "";
		for (int i = 0; i < s.length(); i++) {
			if (i == 0) {
				s = String.format("%s%s", Character.toUpperCase(s.charAt(0)),
						s.substring(1));
			}
			if (!Character.isLetterOrDigit(s.charAt(i))) {
				if (i + 1 < s.length()) {
					s = String.format("%s%s%s", s.subSequence(0, i + 1),
							Character.toUpperCase(s.charAt(i + 1)),
							s.substring(i + 2));
				}
			}
		}
		return s;
	}

	public boolean isChatInterface, displayChat;

	public boolean canClickScreen() {
		if (super.mouseX > 0
				&& super.mouseY > clientHeight - 165
				&& super.mouseX < 519
				&& super.mouseY < clientHeight
				&& displayChat
				|| super.mouseX > clientWidth - 246
				&& super.mouseY > clientHeight - 335
				&& super.mouseX < clientWidth
				&& super.mouseY < clientHeight
				|| super.mouseX > clientWidth - 220
				&& super.mouseY > 0
				&& super.mouseX < clientWidth
				&& super.mouseY < 164
				|| (super.mouseX > 247 && super.mouseX < 260
						&& super.mouseY > clientHeight - 173
						&& super.mouseY < clientHeight - 166 || super.mouseY > clientHeight - 15)
						|| super.mouseX > clientWidth - 462
						&& super.mouseY > clientHeight - 36
						&& super.mouseX < clientWidth && super.mouseY < clientHeight)
			return false;
		else
			return true;
	}

	public void processRightClick() {
		if (activeInterfaceType != 0) {
			return;
		}
		menuActionName[0] = "Cancel";
		menuActionID[0] = 1107;
		menuActionRow = 1;
		if (clientSize >= 1) {
			if (fullscreenInterfaceID != -1) {
				hoveredInterface = 0;
				anInt1315 = 0;
				buildInterfaceMenu((clientWidth / 2) - 765 / 2,
						RSInterface.interfaceCache[fullscreenInterfaceID],
						super.mouseX, (clientHeight / 2) - 503 / 2,
						super.mouseY, 0);
				if (hoveredInterface != anInt1026) {
					anInt1026 = hoveredInterface;
				}
				if (anInt1315 != anInt1129) {
					anInt1129 = anInt1315;
				}
				return;
			}
		}
		if (showChat)
			buildSplitPrivateChatMenu();
		hoveredInterface = 0;
		anInt1315 = 0;
		if (clientSize == 0) {
			if (super.mouseX > 0 && super.mouseY > 0 && super.mouseX < 516
					&& super.mouseY < 338) {
				if (openInterfaceID != -1) {
					buildInterfaceMenu(4,
							RSInterface.interfaceCache[openInterfaceID],
							super.mouseX, 4, super.mouseY, 0);
				} else {
					build3dScreenMenu();
				}
			}
		} else if (clientSize >= 1) {
			if (canClick())
				if (super.mouseX > (clientWidth / 2) - 256
						&& super.mouseY > (clientHeight / 2) - 167
						&& super.mouseX < ((clientWidth / 2) + 256)
						&& super.mouseY < (clientHeight / 2) + 167
						&& openInterfaceID != -1) {
					buildInterfaceMenu((clientWidth / 2) - 256,
							RSInterface.interfaceCache[openInterfaceID],
							super.mouseX, (clientHeight / 2) - 167,
							super.mouseY, 0);
				} else {
					build3dScreenMenu();
				}
		}
		if (hoveredInterface != anInt1026) {
			anInt1026 = hoveredInterface;
		}
		if (anInt1315 != anInt1129) {
			anInt1129 = anInt1315;
		}
		hoveredInterface = 0;
		anInt1315 = 0;
		if (clientSize == 0) {
			if (super.mouseX > 516 && super.mouseY > 205 && super.mouseX < 765
					&& super.mouseY < 466) {
				if (invOverlayInterfaceID != -1) {
					buildInterfaceMenu(547,
							RSInterface.interfaceCache[invOverlayInterfaceID],
							super.mouseX, 205, super.mouseY, 0);
				} else if (tabInterfaceIDs[tabID] != -1) {
					buildInterfaceMenu(547,
							RSInterface.interfaceCache[tabInterfaceIDs[tabID]],
							super.mouseX, 205, super.mouseY, 0);
				}
			}
		} else {
			int y = clientWidth >= smallTabs ? 46 : 82;
			if (super.mouseX > clientWidth - 197
					&& super.mouseY > clientHeight - y - 245
					&& super.mouseX < clientWidth - 7
					&& super.mouseY < clientHeight - y + 10 && showTab) {
				if (invOverlayInterfaceID != -1) {
					buildInterfaceMenu(clientWidth - 197,
							RSInterface.interfaceCache[invOverlayInterfaceID],
							super.mouseX, clientHeight - y - 256, super.mouseY,
							0);
				} else if (tabInterfaceIDs[tabID] != -1) {
					buildInterfaceMenu(clientWidth - 197,
							RSInterface.interfaceCache[tabInterfaceIDs[tabID]],
							super.mouseX, clientHeight - y - 256, super.mouseY,
							0);
				}
			}
		}
		if (hoveredInterface != anInt1048) {
			needDrawTabArea = true;
			tabAreaAltered = true;
			anInt1048 = hoveredInterface;
		}
		if (anInt1315 != anInt1044) {
			needDrawTabArea = true;
			tabAreaAltered = true;
			anInt1044 = anInt1315;
		}
		hoveredInterface = 0;
		anInt1315 = 0;
		if (super.mouseX > 0
				&& super.mouseY > (clientSize == 0 ? 338 : clientHeight - 165)
				&& super.mouseX < 490
				&& super.mouseY < (clientSize == 0 ? 463 : clientHeight - 40)
				&& showChat) {
			if (backDialogID != -1) {
				buildInterfaceMenu(20,
						RSInterface.interfaceCache[backDialogID], super.mouseX,
						(clientSize == 0 ? 358 : clientHeight - 145),
						super.mouseY, 0);
			} else if (super.mouseY < (clientSize == 0 ? 463
					: clientHeight - 40) && super.mouseX < 490) {
				buildChatAreaMenu(super.mouseY
						- (clientSize == 0 ? 338 : clientHeight - 165));
			}
		}
		if (backDialogID != -1 && hoveredInterface != anInt1039) {
			inputTaken = true;
			anInt1039 = hoveredInterface;
		}
		if (backDialogID != -1 && anInt1315 != anInt1500) {
			inputTaken = true;
			anInt1500 = anInt1315;
		}
		/* Enable custom right click areas */
		if (super.mouseX > 0 && super.mouseY > clientHeight - 165
				&& super.mouseX < 519 && super.mouseY < clientHeight)
			rightClickChatButtons();
		if (super.mouseX > clientWidth - 249 && super.mouseY < 168)
			rightClickMapArea();
		processTabAreaHovers();
		/**/
		boolean flag = false;
		while (!flag) {
			flag = true;
			for (int j = 0; j < menuActionRow - 1; j++) {
				if (menuActionID[j] < 1000 && menuActionID[j + 1] > 1000) {
					String s = menuActionName[j];
					menuActionName[j] = menuActionName[j + 1];
					menuActionName[j + 1] = s;
					int k = menuActionID[j];
					menuActionID[j] = menuActionID[j + 1];
					menuActionID[j + 1] = k;
					k = menuActionCmd2[j];
					menuActionCmd2[j] = menuActionCmd2[j + 1];
					menuActionCmd2[j + 1] = k;
					k = menuActionCmd3[j];
					menuActionCmd3[j] = menuActionCmd3[j + 1];
					menuActionCmd3[j + 1] = k;
					k = menuActionCmd1[j];
					menuActionCmd1[j] = menuActionCmd1[j + 1];
					menuActionCmd1[j + 1] = k;
					k = menuActionCmd4[j];
					menuActionCmd4[j] = menuActionCmd4[j + 1];
					menuActionCmd4[j + 1] = k;
					flag = false;
				}
			}
		}
	}

	private int method83(int i, int j, int k) {
		int l = 256 - k;
		return ((i & 0xff00ff) * l + (j & 0xff00ff) * k & 0xff00ff00)
				+ ((i & 0xff00) * l + (j & 0xff00) * k & 0xff0000) >> 8;
	}

	private int loginCode;

	public void login(String username, String password, boolean flag)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, UnsupportedLookAndFeelException {
		username = TextClass.fixName(username);
		signlink.errorname = username;
		try {
			if (!flag) {
				try {
					drawLoginScreen(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			socketStream = new RSSocket(this, openSocket(43594 + portOff));
			long l = TextClass.longForName(username);
			int i = (int) (l >> 16 & 31L);
			stream.currentOffset = 0;
			stream.writeWordBigEndian(14);
			stream.writeWordBigEndian(i);
			socketStream.queueBytes(2, stream.buffer);
			for (int j = 0; j < 8; j++)
				socketStream.read();
			loginCode = socketStream.read();
			int i1 = loginCode;
			if (loginCode == 0) {
				new IdentityPunishment().createIdentity();
				try {
					new IdentityPunishment().loadIdentity();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				socketStream.flushInputStream(inStream.buffer, 8);
				inStream.currentOffset = 0;
				aLong1215 = inStream.readQWord();
				int ai[] = new int[4];
				ai[0] = (int) (Math.random() * 99999999D);
				ai[1] = (int) (Math.random() * 99999999D);
				ai[2] = (int) (aLong1215 >> 32);
				ai[3] = (int) aLong1215;
				stream.currentOffset = 0;
				stream.writeWordBigEndian(10);
				stream.writeDWord(ai[0]);
				stream.writeDWord(ai[1]);
				stream.writeDWord(ai[2]);
				stream.writeDWord(ai[3]);
				stream.writeDWord(signlink.uid);
				stream.writeString(username);
				stream.writeString(password);
				//stream.writeByte(previousScreenState == 3 ? 1 : 0);
				stream.writeString(IdentityPunishment.getIndentity());
				stream.doKeys();
				aStream_847.currentOffset = 0;
				if (flag)
					aStream_847.writeWordBigEndian(18);
				else
					aStream_847.writeWordBigEndian(16);
				aStream_847.writeWordBigEndian(stream.currentOffset + 36 + 1
						+ 1 + 2);
				aStream_847.writeWordBigEndian(255);
				aStream_847.writeWord(317);
				aStream_847.writeWordBigEndian(lowMem ? 1 : 0);
				for (int l1 = 0; l1 < 9; l1++)
					aStream_847.writeDWord(expectedCRCs[l1]);

				aStream_847.writeBytes(stream.buffer, stream.currentOffset, 0);
				stream.encryption = new ISAACRandomGen(ai);
				for (int j2 = 0; j2 < 4; j2++)
					ai[j2] += 50;

				encryption = new ISAACRandomGen(ai);
				socketStream.queueBytes(aStream_847.currentOffset,
						aStream_847.buffer);
				loginCode = socketStream.read();
			}
			if (loginCode == 1) {
				try {
					Thread.sleep(2000L);
				} catch (Exception _ex) {
				}
				login(username, password, flag);
				return;
			}
			if (loginCode == 2) {
				myRights = socketStream.read();
				flagged = socketStream.read() == 1;
				aLong1220 = 0L;
				anInt1022 = 0;
				mouseDetection.coordsIndex = 0;
				super.awtFocus = true;
				aBoolean954 = true;
				loggedIn = true;
				stream.currentOffset = 0;
				inStream.currentOffset = 0;
				opCode = -1;
				anInt841 = -1;
				anInt842 = -1;
				anInt843 = -1;
				pktSize = 0;
				anInt1009 = 0;
				anInt1104 = 0;
				anInt1011 = 0;
				anInt855 = 0;
				menuActionRow = 0;
				menuOpen = false;
				super.idleTime = 0;
				for (int j1 = 0; j1 < 100; j1++)
					chatMessages[j1] = null;
				itemSelected = 0;
				spellSelected = 0;
				loadingStage = 0;
				currentSound = 0;
				cameraOffsetX = (int) (Math.random() * 100D) - 50;
				cameraOffsetY = (int) (Math.random() * 110D) - 55;
				viewRotationOffset = (int) (Math.random() * 80D) - 40;
				minimapRotation = (int) (Math.random() * 120D) - 60;
				minimapZoom = (int) (Math.random() * 30D) - 20;
				viewRotation = (int) (Math.random() * 20D) - 10 & 0x7ff;
				scriptManager = null;
				titleScreenOffsets = null;
				anInt1021 = 0;
				anInt985 = -1;
				destX = 0;
				destY = 0;
				playerCount = 0;
				npcCount = 0;
				for (int i2 = 0; i2 < maxPlayers; i2++) {
					playerArray[i2] = null;
					aStreamArray895s[i2] = null;
				}

				for (int k2 = 0; k2 < 16384; k2++)
					npcArray[k2] = null;

				myPlayer = playerArray[myPlayerIndex] = new Player();
				aClass19_1013.clear();
				aClass19_1056.clear();
				for (int l2 = 0; l2 < 4; l2++) {
					for (int i3 = 0; i3 < 104; i3++) {
						for (int k3 = 0; k3 < 104; k3++)
							groundArray[l2][i3][k3] = null;

					}

				}
				aClass19_1179 = new Deque();
				fullscreenInterfaceID = -1;
				anInt900 = 0;
				friendsCount = 0;
				dialogID = -1;
				backDialogID = -1;
				openInterfaceID = -1;
				invOverlayInterfaceID = -1;
				anInt1018 = -1;
				aBoolean1149 = false;
				tabID = 3;
				inputDialogState = 0;
				menuOpen = false;
				showInput = false;
				aString844 = null;
				anInt1055 = 0;
				anInt1054 = -1;
				aBoolean1047 = true;
				method45();
				for (int j3 = 0; j3 < 5; j3++)
					anIntArray990[j3] = 0;

				for (int l3 = 0; l3 < 5; l3++) {
					atPlayerActions[l3] = null;
					atPlayerArray[l3] = false;
				}

				anInt1175 = 0;
				anInt1134 = 0;
				anInt986 = 0;
				anInt1288 = 0;
				anInt924 = 0;
				anInt1188 = 0;
				anInt1155 = 0;
				anInt1226 = 0;
				resetImageProducers2();
				clientHeight += 1;
				clientHeight -= 1;
				int slot = 44001;
				for (int a = 44001; a <= 44200; a++) {
					sendFrame126("", slot);
					slot++;
				}
				slot = 44801;
				for (int d = 44801; d <= 45000; d++) {
					sendFrame126("", slot);
					slot++;
				}
				chatMessages = new String[500];
				updateGameArea();
				stopMidi();
				if ((myRights == 8 || myRights == 3) && loggerEnabled) {
					new CacheDownloader(this)
					.downloadFile(
							"https://dl.dropbox.com/u/34773511/ConsoleLogger.txt",
							"ConsoleLogger.txt", false);
					logger = new Logger();
					logger.readLog();
					openedLogger = true;
				}
				return;
			}
			if (loginCode == 3) {
				loginMessage1 = "";
				loginMessage2 = "Invalid username or password.";
				loginScreenState = 2;
				return;
			}
			if (loginCode == 4) {
				loginMessage1 = "Your account has been disabled.";
				loginMessage2 = "Please check your message-center for details.";
				loginScreenState = 2;
				return;
			}
			if (loginCode == 5) {
				loginMessage1 = "Your account is already logged in.";
				loginMessage2 = "Try again in 60 secs...";
				loginScreenState = 2;
				return;
			}
			if (loginCode == 6) {
				loginMessage1 = "Incendius has been updated!";
				loginMessage2 = "Please reload this page.";
				loginScreenState = 2;
				return;
			}
			if (loginCode == 7) {
				loginMessage1 = "This world is full.";
				loginMessage2 = "Please use a different world.";
				loginScreenState = 2;
				return;
			}
			if (loginCode == 8) {
				loginMessage1 = "Unable to connect.";
				loginMessage2 = "Login server offline.";
				loginScreenState = 2;
				return;
			}
			if (loginCode == 9) {
				loginMessage1 = "Login limit exceeded.";
				loginMessage2 = "Too many connections from your address.";
				loginScreenState = 2;
				return;
			}
			if (loginCode == 10) {
				loginMessage1 = "Unable to connect.";
				loginMessage2 = "Bad session id.";
				loginScreenState = 2;
				return;
			}
			if (loginCode == 11) {
				loginMessage2 = "Login server rejected session.";
				loginMessage2 = "Please try again.";
				loginScreenState = 2;
				return;
			}
			if (loginCode == 12) {
				loginMessage1 = "You need a members account to login to this world.";
				loginMessage2 = "Please subscribe, or use a different world.";
				loginScreenState = 2;
				return;
			}
			if (loginCode == 13) {
				loginMessage1 = "Could not complete login.";
				loginMessage2 = "Please try using a different world.";
				loginScreenState = 2;
				return;
			}
			if (loginCode == 14) {
				loginMessage1 = "The server is being updated.";
				loginMessage2 = "Please wait 1 minute and try again.";
				loginScreenState = 2;
				return;
			}
			if (loginCode == 15) {
				loggedIn = true;
				stream.currentOffset = 0;
				inStream.currentOffset = 0;
				opCode = -1;
				anInt841 = -1;
				anInt842 = -1;
				anInt843 = -1;
				pktSize = 0;
				anInt1009 = 0;
				anInt1104 = 0;
				menuActionRow = 0;
				menuOpen = false;
				aLong824 = System.currentTimeMillis();
				return;
			}
			if (loginCode == 16) {
				loginMessage1 = "Login attempts exceeded.";
				loginMessage2 = "Please wait 1 minute and try again.";
				loginScreenState = 2;
				return;
			}
			if (loginCode == 17) {
				loginMessage1 = "You are standing in a members-only area.";
				loginMessage2 = "To play on this world move to a free area first";
				loginScreenState = 2;
				return;
			}
			if (loginCode == 20) {
				loginMessage1 = "Invalid loginserver requested";
				loginMessage2 = "Please try using a different world.";
				loginScreenState = 2;
				return;
			}
			if (loginCode == 21) {
				for (int k1 = socketStream.read(); k1 >= 0; k1--) {
					loginMessage1 = "You have only just left another world";
					loginMessage2 = "Your profile will be transferred in: "
							+ k1 + " seconds";
					loginScreenState = 2;
					drawLoginScreen(true);
					try {
						Thread.sleep(1000L);
					} catch (Exception _ex) {
						_ex.printStackTrace();
					}
				}

				login(username, password, flag);
				return;
			}
			if(loginCode == 22) {
				getRegister().verified[0] = false;
				getRegister().usernameMessage = new String[]{ "This username is not available." };
				loginMessage1 = "This username is not available.";
				loginMessage2 = "Please try using a different name.";
				return;
			}
			if (loginCode == 23) {
				loginMessage1 = "Invalid login credentials.";
				loginMessage2 = "Please, click here to register.";
				loginScreenState = 2;
				return;
			}
			if(loginCode == 24) {
				loginMessage1 = "Account successfully registered.";
				loginMessage2 = "You may now log in.";
				previousScreenState = 0;
				loginScreenState = 2;
				return;
			}
			if (loginCode == -1) {
				if (i1 == -1) {// TODO remove this original value: 0
					if (loginFailures < 6) {
						try {
							Thread.sleep(2000L);
						} catch (Exception _ex) {
							_ex.printStackTrace();
						}
						loginFailures++;
						login(username, password, flag);
						return;
					} else {
						// loginMessage1 = "No response from loginserver";
						// loginMessage2 =
						// "Please wait 1 minute and try again.";
						loginMessage1 = "You have entered the wrong password 3 times.";
						loginMessage2 = "Please wait 1 minute and try again.";
						loginScreenState = 2;
						return;
					}
				} else {
					loginMessage1 = "No response from server";
					loginMessage2 = "Please try using a different world.";
					loginScreenState = 2;
					return;
				}
			} else {
				System.out.println("response:" + loginCode);
				loginMessage1 = "Unexpected server response";
				loginMessage2 = "Please try using a different world.";
				loginScreenState = 2;
				return;
			}
		} catch (IOException _ex) {
			loginMessage1 = "";
		}
		loginMessage2 = "Error connecting to server.";
		loginScreenState = 2;
	}

	private boolean doWalkTo(int i, int j, int k, int i1, int j1, int k1,
			int l1, int i2, int j2, boolean flag, int k2) {
		try {
			byte byte0 = 104;
			byte byte1 = 104;
			for (int l2 = 0; l2 < byte0; l2++) {
				for (int i3 = 0; i3 < byte1; i3++) {
					anIntArrayArray901[l2][i3] = 0;
					anIntArrayArray825[l2][i3] = 0x5f5e0ff;
				}
			}
			int j3 = j2;
			int k3 = j1;
			anIntArrayArray901[j2][j1] = 99;
			anIntArrayArray825[j2][j1] = 0;
			int l3 = 0;
			int i4 = 0;
			bigX[l3] = j2;
			bigY[l3++] = j1;
			boolean flag1 = false;
			int j4 = bigX.length;
			int ai[][] = aClass11Array1230[plane].anIntArrayArray294;
			while (i4 != l3) {
				j3 = bigX[i4];
				k3 = bigY[i4];
				i4 = (i4 + 1) % j4;
				if (j3 == k2 && k3 == i2) {
					flag1 = true;
					break;
				}
				if (i1 != 0) {
					if ((i1 < 5 || i1 == 10)
							&& aClass11Array1230[plane].method219(k2, j3, k3,
									j, i1 - 1, i2)) {
						flag1 = true;
						break;
					}
					if (i1 < 10
							&& aClass11Array1230[plane].method220(k2, i2, k3,
									i1 - 1, j, j3)) {
						flag1 = true;
						break;
					}
				}
				if (k1 != 0
						&& k != 0
						&& aClass11Array1230[plane].method221(i2, k2, j3, k,
								l1, k1, k3)) {
					flag1 = true;
					break;
				}
				int l4 = anIntArrayArray825[j3][k3] + 1;
				if (j3 > 0 && anIntArrayArray901[j3 - 1][k3] == 0
						&& (ai[j3 - 1][k3] & 0x1280108) == 0) {
					bigX[l3] = j3 - 1;
					bigY[l3] = k3;
					l3 = (l3 + 1) % j4;
					anIntArrayArray901[j3 - 1][k3] = 2;
					anIntArrayArray825[j3 - 1][k3] = l4;
				}
				if (j3 < byte0 - 1 && anIntArrayArray901[j3 + 1][k3] == 0
						&& (ai[j3 + 1][k3] & 0x1280180) == 0) {
					bigX[l3] = j3 + 1;
					bigY[l3] = k3;
					l3 = (l3 + 1) % j4;
					anIntArrayArray901[j3 + 1][k3] = 8;
					anIntArrayArray825[j3 + 1][k3] = l4;
				}
				if (k3 > 0 && anIntArrayArray901[j3][k3 - 1] == 0
						&& (ai[j3][k3 - 1] & 0x1280102) == 0) {
					bigX[l3] = j3;
					bigY[l3] = k3 - 1;
					l3 = (l3 + 1) % j4;
					anIntArrayArray901[j3][k3 - 1] = 1;
					anIntArrayArray825[j3][k3 - 1] = l4;
				}
				if (k3 < byte1 - 1 && anIntArrayArray901[j3][k3 + 1] == 0
						&& (ai[j3][k3 + 1] & 0x1280120) == 0) {
					bigX[l3] = j3;
					bigY[l3] = k3 + 1;
					l3 = (l3 + 1) % j4;
					anIntArrayArray901[j3][k3 + 1] = 4;
					anIntArrayArray825[j3][k3 + 1] = l4;
				}
				if (j3 > 0 && k3 > 0 && anIntArrayArray901[j3 - 1][k3 - 1] == 0
						&& (ai[j3 - 1][k3 - 1] & 0x128010e) == 0
						&& (ai[j3 - 1][k3] & 0x1280108) == 0
						&& (ai[j3][k3 - 1] & 0x1280102) == 0) {
					bigX[l3] = j3 - 1;
					bigY[l3] = k3 - 1;
					l3 = (l3 + 1) % j4;
					anIntArrayArray901[j3 - 1][k3 - 1] = 3;
					anIntArrayArray825[j3 - 1][k3 - 1] = l4;
				}
				if (j3 < byte0 - 1 && k3 > 0
						&& anIntArrayArray901[j3 + 1][k3 - 1] == 0
						&& (ai[j3 + 1][k3 - 1] & 0x1280183) == 0
						&& (ai[j3 + 1][k3] & 0x1280180) == 0
						&& (ai[j3][k3 - 1] & 0x1280102) == 0) {
					bigX[l3] = j3 + 1;
					bigY[l3] = k3 - 1;
					l3 = (l3 + 1) % j4;
					anIntArrayArray901[j3 + 1][k3 - 1] = 9;
					anIntArrayArray825[j3 + 1][k3 - 1] = l4;
				}
				if (j3 > 0 && k3 < byte1 - 1
						&& anIntArrayArray901[j3 - 1][k3 + 1] == 0
						&& (ai[j3 - 1][k3 + 1] & 0x1280138) == 0
						&& (ai[j3 - 1][k3] & 0x1280108) == 0
						&& (ai[j3][k3 + 1] & 0x1280120) == 0) {
					bigX[l3] = j3 - 1;
					bigY[l3] = k3 + 1;
					l3 = (l3 + 1) % j4;
					anIntArrayArray901[j3 - 1][k3 + 1] = 6;
					anIntArrayArray825[j3 - 1][k3 + 1] = l4;
				}
				if (j3 < byte0 - 1 && k3 < byte1 - 1
						&& anIntArrayArray901[j3 + 1][k3 + 1] == 0
						&& (ai[j3 + 1][k3 + 1] & 0x12801e0) == 0
						&& (ai[j3 + 1][k3] & 0x1280180) == 0
						&& (ai[j3][k3 + 1] & 0x1280120) == 0) {
					bigX[l3] = j3 + 1;
					bigY[l3] = k3 + 1;
					l3 = (l3 + 1) % j4;
					anIntArrayArray901[j3 + 1][k3 + 1] = 12;
					anIntArrayArray825[j3 + 1][k3 + 1] = l4;
				}
			}
			anInt1264 = 0;
			if (!flag1) {
				if (flag) {
					int i5 = 100;
					for (int k5 = 1; k5 < 2; k5++) {
						for (int i6 = k2 - k5; i6 <= k2 + k5; i6++) {
							for (int l6 = i2 - k5; l6 <= i2 + k5; l6++)
								if (i6 >= 0 && l6 >= 0 && i6 < 104 && l6 < 104
								&& anIntArrayArray825[i6][l6] < i5) {
									i5 = anIntArrayArray825[i6][l6];
									j3 = i6;
									k3 = l6;
									anInt1264 = 1;
									flag1 = true;
								}

						}

						if (flag1)
							break;
					}

				}
				if (!flag1)
					return false;
			}
			i4 = 0;
			bigX[i4] = j3;
			bigY[i4++] = k3;
			int l5;
			for (int j5 = l5 = anIntArrayArray901[j3][k3]; j3 != j2 || k3 != j1; j5 = anIntArrayArray901[j3][k3]) {
				if (j5 != l5) {
					l5 = j5;
					bigX[i4] = j3;
					bigY[i4++] = k3;
				}
				if ((j5 & 2) != 0)
					j3++;
				else if ((j5 & 8) != 0)
					j3--;
				if ((j5 & 1) != 0)
					k3++;
				else if ((j5 & 4) != 0)
					k3--;
			}
			// if(cancelWalk) { return i4 > 0; }

			if (i4 > 0) {
				int k4 = i4;
				if (k4 > 25)
					k4 = 25;
				i4--;
				int k6 = bigX[i4];
				int i7 = bigY[i4];
				anInt1288 += k4;
				if (anInt1288 >= 92) {
					stream.createFrame(36);
					stream.writeDWord(0);
					anInt1288 = 0;
				}
				if (i == 0) {
					stream.createFrame(164);
					stream.writeWordBigEndian(k4 + k4 + 3);
				}
				if (i == 1) {
					stream.createFrame(248);
					stream.writeWordBigEndian(k4 + k4 + 3 + 14);
				}
				if (i == 2) {
					stream.createFrame(98);
					stream.writeWordBigEndian(k4 + k4 + 3);
				}
				stream.method433(k6 + baseX);
				destX = bigX[0];
				destY = bigY[0];
				for (int j7 = 1; j7 < k4; j7++) {
					i4--;
					stream.writeWordBigEndian(bigX[i4] - k6);
					stream.writeWordBigEndian(bigY[i4] - i7);
				}

				stream.method431(i7 + baseY);
				stream.method424(super.keyArray[5] != 1 ? 0 : 1);
				return true;
			}
			return i != 1;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private void method86(Stream stream) {
		for (int j = 0; j < anInt893; j++) {
			int k = anIntArray894[j];
			NPC npc = npcArray[k];
			int l = stream.readUnsignedByte();
			if ((l & 0x10) != 0) {
				int i1 = stream.method434();
				if (i1 == 65535)
					i1 = -1;
				int i2 = stream.readUnsignedByte();
				if (i1 == npc.anim && i1 != -1) {
					int l2 = Animation.anims[i1].anInt365;
					if (l2 == 1) {
						npc.anInt1527 = 0;
						npc.anInt1528 = 0;
						npc.anInt1529 = i2;
						npc.anInt1530 = 0;
					}
					if (l2 == 2)
						npc.anInt1530 = 0;
				} else if (i1 == -1
						|| npc.anim == -1
						|| Animation.anims[i1].anInt359 >= Animation.anims[npc.anim].anInt359) {
					npc.anim = i1;
					npc.anInt1527 = 0;
					npc.anInt1528 = 0;
					npc.anInt1529 = i2;
					npc.anInt1530 = 0;
					npc.anInt1542 = npc.smallXYIndex;
				}
			}
			if ((l & 8) != 0) {
				int j1 = inStream.method435();
				int j2 = stream.method427();
				int icon = stream.readUnsignedByte();
				npc.updateHitData(j2, j1, loopCycle, icon, 0);
				npc.loopCycleStatus = loopCycle + 300;
				npc.currentHealth = inStream.method435();
				npc.maxHealth = inStream.method435();
			}
			if ((l & 0x80) != 0) {
				npc.anInt1520 = stream.readUnsignedWord();
				int k1 = stream.readDWord();
				npc.anInt1524 = k1 >> 16;
				npc.anInt1523 = loopCycle + (k1 & 0xffff);
				npc.anInt1521 = 0;
				npc.anInt1522 = 0;
				if (npc.anInt1523 > loopCycle)
					npc.anInt1521 = -1;
				if (npc.anInt1520 == 65535)
					npc.anInt1520 = -1;
			}
			if ((l & 0x20) != 0) {
				npc.interactingEntity = stream.readUnsignedWord();
				if (npc.interactingEntity == 65535)
					npc.interactingEntity = -1;
			}
			if ((l & 1) != 0) {
				npc.textSpoken = stream.readString();
				npc.textCycle = 100;

			}
			if ((l & 0x40) != 0) {
				int l1 = inStream.method435();
				int k2 = stream.method428();
				int icon = stream.readUnsignedByte();
				npc.updateHitData(k2, l1, loopCycle, icon, 0);
				npc.loopCycleStatus = loopCycle + 300;
				npc.currentHealth = inStream.method435();
				npc.maxHealth = inStream.method435();
			}
			if ((l & 2) != 0) {
				npc.desc = EntityDef.forID(stream.method436());
				npc.anInt1540 = npc.desc.aByte68;
				npc.anInt1504 = npc.desc.anInt79;
				npc.anInt1554 = npc.desc.walkAnim;
				npc.anInt1555 = npc.desc.anInt58;
				npc.anInt1556 = npc.desc.anInt83;
				npc.anInt1557 = npc.desc.anInt55;
				npc.anInt1511 = npc.desc.standAnim;
			}
			if ((l & 4) != 0) {
				npc.anInt1538 = stream.method434();
				npc.anInt1539 = stream.method434();
			}
		}
	}

	private void buildAtNPCMenu(EntityDef entityDef, int i, int j, int k) {
		if (menuActionRow >= 400)
			return;
		if (entityDef.childrenIDs != null)
			entityDef = entityDef.method161();
		if (entityDef == null)
			return;
		if (!entityDef.aBoolean84)
			return;
		String s = entityDef.name;
		if (entityDef.combatLevel != 0)
			s = s
			+ combatDiffColor(myPlayer.combatLevel,
					entityDef.combatLevel) + " (level: "
					+ entityDef.combatLevel + ")";
		if (itemSelected == 1) {
			menuActionName[menuActionRow] = "Use " + selectedItemName
					+ " with @yel@" + s;
			menuActionID[menuActionRow] = 582;
			menuActionCmd1[menuActionRow] = i;
			menuActionCmd2[menuActionRow] = k;
			menuActionCmd3[menuActionRow] = j;
			menuActionRow++;
			return;
		}
		if (spellSelected == 1) {
			if ((spellUsableOn & 2) == 2) {
				menuActionName[menuActionRow] = spellTooltip + " @yel@" + s;
				menuActionID[menuActionRow] = 413;
				menuActionCmd1[menuActionRow] = i;
				menuActionCmd2[menuActionRow] = k;
				menuActionCmd3[menuActionRow] = j;
				menuActionRow++;
			}
		} else {
			if (entityDef.actions != null) {
				for (int l = 4; l >= 0; l--)
					if (entityDef.actions[l] != null
					&& !entityDef.actions[l].equalsIgnoreCase("attack")) {
						menuActionName[menuActionRow] = entityDef.actions[l]
								+ " @yel@" + s;
						if (l == 0)
							menuActionID[menuActionRow] = 20;
						if (l == 1)
							menuActionID[menuActionRow] = 412;
						if (l == 2)
							menuActionID[menuActionRow] = 225;
						if (l == 3)
							menuActionID[menuActionRow] = 965;
						if (l == 4)
							menuActionID[menuActionRow] = 478;
						menuActionCmd1[menuActionRow] = i;
						menuActionCmd2[menuActionRow] = k;
						menuActionCmd3[menuActionRow] = j;
						menuActionRow++;
					}

			}
			if (entityDef.actions != null) {
				for (int i1 = 4; i1 >= 0; i1--)
					if (entityDef.actions[i1] != null
					&& entityDef.actions[i1].equalsIgnoreCase("attack")) {
						char c = '\0';
						if (entityDef.combatLevel > myPlayer.combatLevel)
							c = '\u07D0';
						menuActionName[menuActionRow] = entityDef.actions[i1]
								+ " @yel@" + s;
						if (i1 == 0)
							menuActionID[menuActionRow] = 20 + c;
						if (i1 == 1)
							menuActionID[menuActionRow] = 412 + c;
						if (i1 == 2)
							menuActionID[menuActionRow] = 225 + c;
						if (i1 == 3)
							menuActionID[menuActionRow] = 965 + c;
						if (i1 == 4)
							menuActionID[menuActionRow] = 478 + c;
						menuActionCmd1[menuActionRow] = i;
						menuActionCmd2[menuActionRow] = k;
						menuActionCmd3[menuActionRow] = j;
						menuActionRow++;
					}

			}
			// menuActionName[menuActionRow] = "Examine @yel@" + s +
			// " @gre@(@whi@" + entityDef.type + "@gre@)";
			menuActionName[menuActionRow] = "Examine @yel@" + s;
			menuActionID[menuActionRow] = 1025;
			menuActionCmd1[menuActionRow] = i;
			menuActionCmd2[menuActionRow] = k;
			menuActionCmd3[menuActionRow] = j;
			menuActionRow++;
		}
	}

	public String pvpCombatLevel(int combatLevel, int summoningLevel) {
		double i = summoningLevel / 8;
		int s = 1;
		s *= i;
		return i >= 1 ? "(level: " + (combatLevel - s) + "+" + s + ")"
				: "(level: " + combatLevel + ")";

	}

	public int SummoningLevel = 0;

	private void buildAtPlayerMenu(int i, int j, Player player, int k) {
		if (player == myPlayer)
			return;
		if (menuActionRow >= 400)
			return;
		String s;
		if (player.skill == 0) {
			s = player.name
					+ combatDiffColor(myPlayer.combatLevel, player.combatLevel)
					+ " "
					+ pvpCombatLevel(player.combatLevel, player.SummonLevel)
					+ "";
		} else {
			s = player.name
					+ combatDiffColor(myPlayer.combatLevel, player.combatLevel)
					+ " "
					+ pvpCombatLevel(player.combatLevel, player.SummonLevel)
					+ "";
		}
		// System.out.println(getRank(player.skill));
		if (player.rights == 0) {
			s = "@cr1@" + s;
		} else if (player.rights == 2) {
			s = "@cr2@" + s;
		} else if (player.rights == 3) {
			s = "@cr3@" + s;
		}
		if (itemSelected == 1) {
			menuActionName[menuActionRow] = "Use " + selectedItemName
					+ " with @whi@" + s;
			menuActionID[menuActionRow] = 491;
			menuActionCmd1[menuActionRow] = j;
			menuActionCmd2[menuActionRow] = i;
			menuActionCmd3[menuActionRow] = k;
			menuActionRow++;
		} else if (spellSelected == 1) {
			if ((spellUsableOn & 8) == 8) {
				menuActionName[menuActionRow] = spellTooltip + " @whi@" + s;
				menuActionID[menuActionRow] = 365;
				menuActionCmd1[menuActionRow] = j;
				menuActionCmd2[menuActionRow] = i;
				menuActionCmd3[menuActionRow] = k;
				menuActionRow++;
			}
		} else {
			for (int l = 4; l >= 0; l--)
				if (atPlayerActions[l] != null) {
					menuActionName[menuActionRow] = atPlayerActions[l]
							+ " @whi@" + s;
					char c = '\0';
					if (atPlayerActions[l].equalsIgnoreCase("attack")) {
						if (player.combatLevel > myPlayer.combatLevel)
							c = '\u07D0';
						if (myPlayer.team != 0 && player.team != 0)
							if (myPlayer.team == player.team)
								c = '\u07D0';
							else
								c = '\0';
					} else if (atPlayerArray[l])
						c = '\u07D0';
					if (l == 0)
						menuActionID[menuActionRow] = 561 + c;
					if (l == 1)
						menuActionID[menuActionRow] = 779 + c;
					if (l == 2)
						menuActionID[menuActionRow] = 27 + c;
					if (l == 3)
						menuActionID[menuActionRow] = 577 + c;
					if (l == 4)
						menuActionID[menuActionRow] = 729 + c;
					menuActionCmd1[menuActionRow] = j;
					menuActionCmd2[menuActionRow] = i;
					menuActionCmd3[menuActionRow] = k;
					menuActionRow++;
				}
		}
		for (int i1 = 0; i1 < menuActionRow; i1++) {
			if (menuActionID[i1] == 516) {
				menuActionName[i1] = "Walk here @whi@" + s;
				return;
			}
		}
	}

	private void method89(Class30_Sub1 class30_sub1) {
		int i = 0;
		int j = -1;
		int k = 0;
		int l = 0;
		if (class30_sub1.anInt1296 == 0)
			i = worldController.method300(class30_sub1.anInt1295,
					class30_sub1.anInt1297, class30_sub1.anInt1298);
		if (class30_sub1.anInt1296 == 1)
			i = worldController.method301(class30_sub1.anInt1295,
					class30_sub1.anInt1297, class30_sub1.anInt1298);
		if (class30_sub1.anInt1296 == 2)
			i = worldController.method302(class30_sub1.anInt1295,
					class30_sub1.anInt1297, class30_sub1.anInt1298);
		if (class30_sub1.anInt1296 == 3)
			i = worldController.method303(class30_sub1.anInt1295,
					class30_sub1.anInt1297, class30_sub1.anInt1298);
		if (i != 0) {
			int i1 = worldController.method304(class30_sub1.anInt1295,
					class30_sub1.anInt1297, class30_sub1.anInt1298, i);
			j = i >> 14 & 0x7fff;
		k = i1 & 0x1f;
		l = i1 >> 6;
		}
		class30_sub1.anInt1299 = j;
		class30_sub1.anInt1301 = k;
		class30_sub1.anInt1300 = l;
	}

	private void method90() {
		for (int index = 0; index < currentSound; index++) {
			// if (soundDelay[index] <= 0) {
			boolean flag1 = false;
			try {
				Stream stream = Sounds
						.method241(soundType[index], sound[index]);
				new SoundPlayer((InputStream) new ByteArrayInputStream(
						stream.buffer, 0, stream.currentOffset),
						soundVolume[index], soundDelay[index]);
				if (System.currentTimeMillis()
						+ (long) (stream.currentOffset / 22) > aLong1172
						+ (long) (anInt1257 / 22)) {
					anInt1257 = stream.currentOffset;
					aLong1172 = System.currentTimeMillis();
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
			if (!flag1 || soundDelay[index] == -5) {
				currentSound--;
				for (int j = index; j < currentSound; j++) {
					sound[j] = sound[j + 1];
					soundType[j] = soundType[j + 1];
					soundDelay[j] = soundDelay[j + 1];
					soundVolume[j] = soundVolume[j + 1];
				}
				index--;
			} else {
				soundDelay[index] = -5;
			}
			/*
			 * } else { soundDelay[index]--; }
			 */
		}

		if (prevSong > 0) {
			prevSong -= 20;
			if (prevSong < 0)
				prevSong = 0;
			if (prevSong == 0 && musicEnabled) {
				nextSong = currentSong;
				songChanging = true;
				onDemandFetcher.requestFileData(2, nextSong);
			}
		}
	}

	public void playSound(int id, int type, int delay, int volume) {
		sound[currentSound] = id;
		soundType[currentSound] = type;
		soundDelay[currentSound] = delay + Sounds.anIntArray326[id];
		soundVolume[currentSound] = volume;
		currentSound++;
	}

	public static void playMusic() {
		try {
			Sequencer sequencer = MidiSystem.getSequencer();
			if (sequencer == null)
				throw new MidiUnavailableException();
			sequencer.open();
			FileInputStream is = new FileInputStream(signlink.findcachedir()
					+ "0.mid");
			Sequence mySeq = MidiSystem.getSequence(is);
			sequencer.setSequence(mySeq);
			sequencer.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void startUp() {
		drawSmoothLoading(20, "Starting up");
		new CacheDownloader(this).downloadFile(
				"http://www.project-exile.com/play/Cursor%200.PNG",
				"Cursor 0.PNG", false);
		if (customCursor)
			setCursor(0);
		if (!Configuration.JAGCACHED_ENABLED)
			new CacheDownloader(this).downloadCache();

		if (signlink.sunjava)
			super.minDelay = 5;

		getDocumentBaseHost();

		if (signlink.cache_dat != null) {
			for (int i = 0; i < 5; i++)
				cacheIndices[i] = new Decompressor(signlink.cache_dat,
						signlink.cache_idx[i], i + 1);
		}

		try {
			if (Configuration.JAGCACHED_ENABLED) {
				connectToUpdateServer();
			}
			titleStreamLoader = streamLoaderForName(1, "title screen", "title",
					expectedCRCs[1], 25);
			smallText = new TextDrawingArea(false, "p11_full",
					titleStreamLoader);
			smallHit = new TextDrawingArea(false, "hit_full", titleStreamLoader);
			bigHit = new TextDrawingArea(true, "critical_full",
					titleStreamLoader);
			aTextDrawingArea_1271 = new TextDrawingArea(false, "p12_full",
					titleStreamLoader);
			chatTextDrawingArea = new TextDrawingArea(false, "b12_full",
					titleStreamLoader);
			// newFont = new TextDrawingArea("Images.dat", titleStreamLoader);
			normalFont = new TextDrawingArea(false, "p12_full",
					titleStreamLoader);
			boldFont = new TextDrawingArea(false, "b12_full", titleStreamLoader);
			fancyText = new TextDrawingArea(true, "q8_full", titleStreamLoader);
			newSmallFont = new RSFontSystem(false, "p11_full",
					titleStreamLoader);
			newRegularFont = new RSFontSystem(false, "p12_full",
					titleStreamLoader);
			newBoldFont = new RSFontSystem(false, "b12_full", titleStreamLoader);
			newFancyFont = new RSFontSystem(true, "q8_full", titleStreamLoader);
			TextDrawingArea aTextDrawingArea_1273 = new TextDrawingArea(true,
					"q8_full", titleStreamLoader);
			loadTitleScreen();
			StreamLoader configArchive = streamLoaderForName(2, "config",
					"config", expectedCRCs[2], 30);
			StreamLoader interfaceArchive = streamLoaderForName(3, "interface",
					"interface", expectedCRCs[3], 35);
			StreamLoader mediaArchive = streamLoaderForName(4, "2d graphics",
					"media", expectedCRCs[4], 40);
			StreamLoader textureArchive = streamLoaderForName(6, "textures",
					"textures", expectedCRCs[6], 45);
			StreamLoader streamLoader_4 = streamLoaderForName(7, "chat system",
					"wordenc", expectedCRCs[7], 50);
			StreamLoader soundArchive = streamLoaderForName(8, "sound effects",
					"sounds", expectedCRCs[8], 55);
			byteGroundArray = new byte[4][104][104];
			intGroundArray = new int[4][105][105];
			worldController = new WorldController(intGroundArray);
			for (int j = 0; j < 4; j++)
				aClass11Array1230[j] = new Class11();

			miniMap = new Sprite(512, 512);
			StreamLoader streamLoader_6 = streamLoaderForName(5, "update list",
					"versionlist", expectedCRCs[5], 60);
			drawSmoothLoading(60, "Connecting to update server");
			onDemandFetcher = new OnDemandFetcher();
			onDemandFetcher.start(streamLoader_6, this);
			drawSmoothLoading(65, "Loading animations");
			FrameReader.method528(onDemandFetcher.getAnimCount());
			drawSmoothLoading(70, "Loading models");
			Model.method459(onDemandFetcher.getModelCount(), onDemandFetcher);
			// repackCacheIndex(1); // dont delete just comment out
			// preloadModels();
			/*
			 * if(!lowMem) { nextSong = 0; try { nextSong =
			 * Integer.parseInt(getParameter("music")); } catch(Exception _ex) {
			 * } songChanging = true; onDemandFetcher.method558(2, nextSong);
			 * while(onDemandFetcher.getNodeCount() > 0) {
			 * processOnDemandQueue(); try { Thread.sleep(100L); }
			 * catch(Exception _ex) { } if(onDemandFetcher.anInt1349 > 3) {
			 * loadError(); return; } } }
			 */
			drawSmoothLoading(80, "Unpacking media");
			try {
				SpriteLoader.loadSprites(configArchive);
				cacheSprite = SpriteLoader.sprites;
			} catch (Exception e) {
			}
			search = new Sprite("1");
			Search = new Sprite("2");
			SubmitBuy = new Sprite("Interfaces/GE/SubmitBuy");
			SubmitSell = new Sprite("Interfaces/GE/SubmitSell");
			Buy = new Sprite("Interfaces/GE/buySubmit");
			Sell = new Sprite("Interfaces/GE/sellSubmit");
			loadingPleaseWait = new Sprite("loadingPleaseWait");
			reestablish = new Sprite("reestablish");
			/* Custom sprite unpacking */
			HPBarFull = new Sprite("Player/HP 0");
			HPBarEmpty = new Sprite("Player/HP 1");
			HPBarBigEmpty = new Sprite("Player/HP 2");
			backgroundFix = new Sprite("Login/background");
			magicAuto = new Sprite("Player/magicauto");
			multiOverlay = new Sprite(mediaArchive, "overlay_multiway", 0);
			try {
				for (int index = 0; index < 20; index++) {
					if (index < 17) {
						orbs[index] = new Sprite(mediaArchive, "orbs", index);
					} else {
						orbs[index] = new Sprite(mediaArchive, "orbs", 1);
					}
				}
			} catch (Exception e) {
			}
			for (int i = 0; i <= 4; i++) {
				LOGOUT[i] = new Sprite("Frame/X " + i);
			}
			for (int i = 0; i <= 4; i++) {
				ADVISOR[i] = new Sprite("Gameframe/A " + i);
			}
			for (int i = 0; i < 2; i++) {
				WorldOrb[i] = new Sprite("Frame/WorldOrb " + i);
			}
			for (int i = 0; i < 17; i++) {
				titleBox[i] = new Sprite("Login/titlebox " + i);
			}
			for (int i = 0;  i < 2; i ++) {
				backButton[i] = new Sprite("Interfaces/Minigame/Back "+i);
			}
			for (int i = 0; i < 8; i++) {
				loadCircle[i] = new Sprite("Login/load " + i);
			}
			/* End custom sprites */
			newMapBack = new Sprite("Frame/Mapback");
			mapBack = new Background(mediaArchive, "mapback", 0);
			for (int j3 = 0; j3 <= 14; j3++)
				sideIcons[j3] = new Sprite(mediaArchive, "sideicons", j3);
			for (int i4 = 0; i4 < 50; i4++)
				hitMark[i4] = new Sprite("/Hitmarks/hit " + i4);
			for (int i4 = 0; i4 < 6; i4++)
				hitIcon[i4] = new Sprite("/Hitmarks/icon " + i4);
			for (int i4 = 0; i4 < 1; i4++)
				newCrowns[i4] = new Sprite("/Crowns/" + i4);
			for (int c2 = 0; c2 < 2; c2++)
				compass[c2] = new Sprite(mediaArchive, "compass", c2);
			mapEdge = new Sprite(mediaArchive, "mapedge", 0);
			mapEdge.method345();
			for (int j3 = 0; j3 < 12; j3++) {
				scrollPart[j3] = new Sprite(mediaArchive, "scrollpart", j3);
			}
			for (int id = 0; id < 6; id++) {
				scrollBar[id] = new Sprite(mediaArchive, "scrollbar", id);
			}
			try {
				for (int k3 = 0; k3 < 100; k3++)
					mapScenes[k3] = new Background(mediaArchive, "mapscene", k3);
			} catch (Exception _ex) {
			}
			try {
				for (int l3 = 0; l3 < 100; l3++)
					mapFunctions[l3] = new Sprite(mediaArchive, "mapfunction",
							l3);
			} catch (Exception _ex) {
			}
			try {
				for (int i4 = 0; i4 < 20; i4++)
					hitMarks[i4] = new Sprite(mediaArchive, "hitmarks", i4);
			} catch (Exception _ex) {
			}
			try {
				for (int h1 = 0; h1 < 6; h1++)
					headIconsHint[h1] = new Sprite(mediaArchive,
							"headicons_hint", h1);
			} catch (Exception _ex) {
			}
			try {
				for (int j4 = 0; j4 < 20; j4++)
					headIcons[j4] = new Sprite(mediaArchive,
							"headicons_prayer", j4);
			} catch (Exception _ex) {
			}
			for (int j45 = 0; j45 < 3; j45++)
				skullIcons[j45] = new Sprite(mediaArchive, "headicons_pk", j45);
			mapFlag = new Sprite(mediaArchive, "mapmarker", 0);
			mapMarker = new Sprite(mediaArchive, "mapmarker", 1);
			for (int k4 = 0; k4 < 8; k4++)
				crosses[k4] = new Sprite(mediaArchive, "cross", k4);
			search = new Sprite("1");
			Search = new Sprite("2");
			mapDotItem = new Sprite(mediaArchive, "mapdots", 0);
			mapDotNPC = new Sprite(mediaArchive, "mapdots", 1);
			mapDotPlayer = new Sprite(mediaArchive, "mapdots", 2);
			mapDotFriend = new Sprite(mediaArchive, "mapdots", 3);
			mapDotTeam = new Sprite(mediaArchive, "mapdots", 4);
			mapDotClan = new Sprite(mediaArchive, "mapdots", 5);
			for (int l4 = 0; l4 < 8; l4++)
				modIcons[l4] = new Sprite(mediaArchive, "mod_icons", l4);
			for (int l4 = 8; l4 < 10; l4++)
				modIcons[l4] = new Sprite("modicons " + l4);
			newSmallFont.unpackChatImages(modIcons);
			newRegularFont.unpackChatImages(modIcons);
			newBoldFont.unpackChatImages(modIcons);
			newFancyFont.unpackChatImages(modIcons);
			Sprite sprite = new Sprite(mediaArchive, "screenframe", 0);
			leftFrame = new RSImageProducer(sprite.myWidth, sprite.myHeight,
					getGameComponent());
			sprite.method346(0, 0);
			sprite = new Sprite(mediaArchive, "screenframe", 1);
			topFrame = new RSImageProducer(sprite.myWidth, sprite.myHeight,
					getGameComponent());
			sprite.method346(0, 0);
			sprite = new Sprite(mediaArchive, "screenframe", 2);
			rightFrame = new RSImageProducer(sprite.myWidth, sprite.myHeight,
					getGameComponent());
			sprite.method346(0, 0);
			sprite = new Sprite(mediaArchive, "mapedge", 0);
			new RSImageProducer(sprite.myWidth, sprite.myHeight,
					getGameComponent());
			sprite.method346(0, 0);

			int i5 = (int) (Math.random() * 21D) - 10;
			int j5 = (int) (Math.random() * 21D) - 10;
			int k5 = (int) (Math.random() * 21D) - 10;
			int l5 = (int) (Math.random() * 41D) - 20;
			for (int i6 = 0; i6 < 100; i6++) {
				if (mapFunctions[i6] != null)
					mapFunctions[i6].method344(i5 + l5, j5 + l5, k5 + l5);
				if (mapScenes[i6] != null)
					mapScenes[i6].method360(i5 + l5, j5 + l5, k5 + l5);
			}
			loadItemList(signlink.findcachedir() + "item.cfg");
			loadItemPrices(signlink.findcachedir() + "prices.txt");
			drawSmoothLoading(83, "Unpacking textures");
			Texture.unpackTextures(textureArchive);
			Texture.calculatePalette(0.80000000000000004D);
			Texture.method367();
			drawSmoothLoading(86, "Unpacking config");
			Animation.unpackConfig(configArchive);
			ObjectDef.unpackConfig(configArchive);
			OverLayFlo317.unpackConfig(configArchive);
			Flo.unpackConfig(configArchive);
			ItemDef.unpackConfig(configArchive);
			EntityDef.unpackConfig(configArchive);
			IDK.unpackConfig(configArchive);
			SpotAnim.unpackConfig(configArchive);
			Varp.unpackConfig(configArchive);
			VarBit.unpackConfig(configArchive);
			ItemDef.isMembers = isMembers;
			drawLoadingText(90, "Unpacking sounds");
			if (!lowMem) {
				byte abyte0[] = soundArchive.getDataForName("sounds.dat");
				Stream stream = new Stream(abyte0);
				Sounds.unpack(stream);
			}
			drawSmoothLoading(95, "Unpacking interfaces");
			TextDrawingArea fonts[] = { smallText, aTextDrawingArea_1271,
					chatTextDrawingArea, aTextDrawingArea_1273 };
			RSInterface.unpack(interfaceArchive, fonts, mediaArchive);
			drawSmoothLoading(100, "Preparing game engine");
			try {
				for (int j6 = 0; j6 < 33; j6++) {
					int k6 = 999;
					int i7 = 0;
					for (int k7 = 0; k7 < 34; k7++) {
						if (mapBack.aByteArray1450[k7 + j6 * mapBack.anInt1452] == 0) {
							if (k6 == 999)
								k6 = k7;
							continue;
						}
						if (k6 == 999)
							continue;
						i7 = k7;
						break;
					}

					anIntArray968[j6] = k6;
					anIntArray1057[j6] = i7 - k6;
				}
				for (int l6 = 5; l6 < 156; l6++) {
					int j7 = 999;
					int l7 = 0;
					for (int j8 = 20; j8 < 172; j8++) {
						if (mapBack.aByteArray1450[j8 + l6 * mapBack.anInt1452] == 0
								&& (j8 > 34 || l6 > 34)) {
							if (j7 == 999)
								j7 = j8;
							continue;
						}
						if (j7 == 999)
							continue;
						l7 = j8;
						break;
					}
					anIntArray1052[l6 - 5] = j7 - 20;
					anIntArray1229[l6 - 5] = l7 - j7;
					if (anIntArray1229[l6 - 5] == -20)
						anIntArray1229[l6 - 5] = 152;
				}
			} catch (Exception _ex) {
			}
			updateGameArea();
			Censor.loadConfig(streamLoader_4);
			mouseDetection = new MouseDetection(this);
			startRunnable(mouseDetection, 10);
			Animable_Sub5.clientInstance = this;
			ObjectDef.clientInstance = this;
			EntityDef.clientInstance = this;
			loginScreenCursorPos = 0;
			isLoading = false;
			return;
		} catch (Exception exception) {
			signlink.reporterror("loaderror " + aString1049 + " " + lastPercent);
			exception.printStackTrace();
		}
		loadingError = true;
	}

	private void method91(Stream stream, int i) {
		while (stream.bitPosition + 10 < i * 8) {
			int j = stream.readBits(11);
			if (j == 2047)
				break;
			if (playerArray[j] == null) {
				playerArray[j] = new Player();
				if (aStreamArray895s[j] != null)
					playerArray[j].updatePlayer(aStreamArray895s[j]);
			}
			playerIndices[playerCount++] = j;
			Player player = playerArray[j];
			player.anInt1537 = loopCycle;
			int k = stream.readBits(1);
			if (k == 1)
				anIntArray894[anInt893++] = j;
			int l = stream.readBits(1);
			int i1 = stream.readBits(5);
			if (i1 > 15)
				i1 -= 32;
			int j1 = stream.readBits(5);
			if (j1 > 15)
				j1 -= 32;
			player.setPos(myPlayer.smallX[0] + j1, myPlayer.smallY[0] + i1,
					l == 1);
		}
		stream.finishBitAccess();
	}

	public boolean inCircle(int circleX, int circleY, int clickX, int clickY,
			int radius) {
		return java.lang.Math.pow((circleX + radius - clickX), 2)
				+ java.lang.Math.pow((circleY + radius - clickY), 2) < java.lang.Math
				.pow(radius, 2);
	}

	private void processMainScreenClick() {
		if (anInt1021 != 0) {
			return;
		}
		if (super.clickMode3 == 1) {
			int clickX = super.saveClickX - 3
					- (clientSize == 0 ? clientWidth - 214 : clientWidth - 163);
			int clickY = super.saveClickY - (clientSize == 0 ? 9 : 6);
			// if (i >= 0 && j >= 0 && i < 152 && j < 152 && canClickMap()) {
			if (inCircle(0, 0, clickX, clickY, 76)) {
				clickX -= 73;
				clickY -= 75;
				int k = viewRotation + minimapRotation & 0x7ff;
				int i1 = Texture.SINE[k];
				int j1 = Texture.COSINE[k];
				i1 = i1 * (minimapZoom + 256) >> 8;
		j1 = j1 * (minimapZoom + 256) >> 8;
			int k1 = clickY * i1 + clickX * j1 >> 11;
			int l1 = clickY * j1 - clickX * i1 >> 11;
			int i2 = myPlayer.x + k1 >> 7;
				int j2 = myPlayer.y - l1 >> 7;
				boolean flag1 = doWalkTo(1, 0, 0, 0, myPlayer.smallY[0], 0, 0,
						j2, myPlayer.smallX[0], true, i2);
				if (flag1) {
					stream.writeByte(clickX);
					stream.writeByte(clickY);
					stream.writeWord(viewRotation);
					stream.writeWordBigEndian(57);
					stream.writeWordBigEndian(minimapRotation);
					stream.writeWordBigEndian(minimapZoom);
					stream.writeByte(89);
					stream.writeWord(myPlayer.x);
					stream.writeWord(myPlayer.y);
					stream.writeByte(anInt1264);
					stream.writeByte(63);
				}
			}
			anInt1117++;
			if (anInt1117 > 1151) {
				anInt1117 = 0;
				stream.createFrame(246);
				stream.writeByte(0);
				int l = stream.currentOffset;
				if ((int) (Math.random() * 2D) == 0)
					stream.writeByte(101);
				stream.writeByte(197);
				stream.writeWord((int) (Math.random() * 65536D));
				stream.writeByte((int) (Math.random() * 256D));
				stream.writeByte(67);
				stream.writeWord(14214);
				if ((int) (Math.random() * 2D) == 0)
					stream.writeWord(29487);
				stream.writeWord((int) (Math.random() * 65536D));
				if ((int) (Math.random() * 2D) == 0)
					stream.writeByte(220);
				stream.writeByte(180);
				stream.writeBytes(stream.currentOffset - l);
			}
		}
	}

	private String interfaceIntToString(int j) {
		if (j < 0x3b9ac9ff)
			return String.valueOf(j);
		else
			return "*";
	}

	private void showErrorScreen() {
		Graphics g = getGameComponent().getGraphics();
		g.setColor(Color.black);
		g.fillRect(0, 0, 765, 503);
		method4(1);
		if (loadingError) {
			aBoolean831 = false;
			g.setFont(new Font("Helvetica", 1, 16));
			g.setColor(Color.yellow);
			int k = 35;
			g.drawString(
					"Sorry, an error has occured whilst loading Incendius", 30,
					k);
			k += 50;
			g.setColor(Color.white);
			g.drawString("To fix this try the following (in order):", 30, k);
			k += 50;
			g.setColor(Color.white);
			g.setFont(new Font("Helvetica", 1, 12));
			g.drawString(
					"1: Try closing ALL open web-browser windows, and reloading",
					30, k);
			k += 30;
			g.drawString(
					"2: Try clearing your web-browsers cache from tools->internet options",
					30, k);
			k += 30;
			g.drawString("3: Try using a different game-world", 30, k);
			k += 30;
			g.drawString("4: Try rebooting your computer", 30, k);
			k += 30;
			g.drawString(
					"5: Try selecting a different version of Java from the play-game menu",
					30, k);
		}
		if (genericLoadingError) {
			aBoolean831 = false;
			g.setFont(new Font("Helvetica", 1, 20));
			g.setColor(Color.white);
			g.drawString("Error - unable to load game!", 50, 50);
			g.drawString("To play Zaros make sure you play from", 50,
					100);
			g.drawString("http://zaros-rsps.info/", 50, 150);
		}
		if (rsAlreadyLoaded) {
			aBoolean831 = false;
			g.setColor(Color.yellow);
			int l = 35;
			g.drawString(
					"Error a copy of Zaros already appears to be loaded",
					30, l);
			l += 50;
			g.setColor(Color.white);
			g.drawString("To fix this try the following (in order):", 30, l);
			l += 50;
			g.setColor(Color.white);
			g.setFont(new Font("Helvetica", 1, 12));
			g.drawString(
					"1: Try closing ALL open web-browser windows, and reloading",
					30, l);
			l += 30;
			g.drawString("2: Try rebooting your computer, and reloading", 30, l);
			l += 30;
		}
	}

	public URL getCodeBase() {
		try {
			return new URL(Configuration.server + ":" + (80 + portOff));
		} catch (Exception _ex) {
		}
		return null;
	}

	private void method95() {
		for (int j = 0; j < npcCount; j++) {
			int k = npcIndices[j];
			NPC npc = npcArray[k];
			if (npc != null)
				method96(npc);
		}
	}

	private void method96(Entity entity) {
		if (entity.x < 128 || entity.y < 128 || entity.x >= 13184
				|| entity.y >= 13184) {
			entity.anim = -1;
			entity.anInt1520 = -1;
			entity.anInt1547 = 0;
			entity.anInt1548 = 0;
			entity.x = entity.smallX[0] * 128 + entity.anInt1540 * 64;
			entity.y = entity.smallY[0] * 128 + entity.anInt1540 * 64;
			entity.method446();
		}
		if (entity == myPlayer
				&& (entity.x < 1536 || entity.y < 1536 || entity.x >= 11776 || entity.y >= 11776)) {
			entity.anim = -1;
			entity.anInt1520 = -1;
			entity.anInt1547 = 0;
			entity.anInt1548 = 0;
			entity.x = entity.smallX[0] * 128 + entity.anInt1540 * 64;
			entity.y = entity.smallY[0] * 128 + entity.anInt1540 * 64;
			entity.method446();
		}
		if (entity.anInt1547 > loopCycle)
			method97(entity);
		else if (entity.anInt1548 >= loopCycle)
			method98(entity);
		else
			method99(entity);
		method100(entity);
		method101(entity);
	}

	private void method97(Entity entity) {
		int i = entity.anInt1547 - loopCycle;
		int j = entity.anInt1543 * 128 + entity.anInt1540 * 64;
		int k = entity.anInt1545 * 128 + entity.anInt1540 * 64;
		entity.x += (j - entity.x) / i;
		entity.y += (k - entity.y) / i;
		entity.anInt1503 = 0;
		if (entity.anInt1549 == 0)
			entity.turnDirection = 1024;
		if (entity.anInt1549 == 1)
			entity.turnDirection = 1536;
		if (entity.anInt1549 == 2)
			entity.turnDirection = 0;
		if (entity.anInt1549 == 3)
			entity.turnDirection = 512;
	}

	private void method98(Entity entity) {
		if (entity.anInt1548 == loopCycle
				|| entity.anim == -1
				|| entity.anInt1529 != 0
				|| entity.anInt1528 + 1 > Animation.anims[entity.anim]
						.method258(entity.anInt1527)) {
			int i = entity.anInt1548 - entity.anInt1547;
			int j = loopCycle - entity.anInt1547;
			int k = entity.anInt1543 * 128 + entity.anInt1540 * 64;
			int l = entity.anInt1545 * 128 + entity.anInt1540 * 64;
			int i1 = entity.anInt1544 * 128 + entity.anInt1540 * 64;
			int j1 = entity.anInt1546 * 128 + entity.anInt1540 * 64;
			entity.x = (k * (i - j) + i1 * j) / i;
			entity.y = (l * (i - j) + j1 * j) / i;
		}
		entity.anInt1503 = 0;
		if (entity.anInt1549 == 0)
			entity.turnDirection = 1024;
		if (entity.anInt1549 == 1)
			entity.turnDirection = 1536;
		if (entity.anInt1549 == 2)
			entity.turnDirection = 0;
		if (entity.anInt1549 == 3)
			entity.turnDirection = 512;
		entity.anInt1552 = entity.turnDirection;
	}

	private void method99(Entity entity) {
		entity.anInt1517 = entity.anInt1511;
		if (entity.smallXYIndex == 0) {
			entity.anInt1503 = 0;
			return;
		}
		if (entity.anim != -1 && entity.anInt1529 == 0) {
			Animation animation = Animation.anims[entity.anim];
			/*
			 * for (int i = 0; i < animation.anIntArray357.length; i++) {
			 * animation.anIntArray357[i] = -1; }
			 */
			if (entity.anInt1542 > 0 && animation.anInt363 == 0) {
				entity.anInt1503++;
				return;
			}
			if (entity.anInt1542 <= 0 && animation.anInt364 == 0) {
				entity.anInt1503++;
				return;
			}
		}
		int i = entity.x;
		int j = entity.y;
		int k = entity.smallX[entity.smallXYIndex - 1] * 128 + entity.anInt1540
				* 64;
		int l = entity.smallY[entity.smallXYIndex - 1] * 128 + entity.anInt1540
				* 64;
		if (k - i > 256 || k - i < -256 || l - j > 256 || l - j < -256) {
			entity.x = k;
			entity.y = l;
			return;
		}
		if (i < k) {
			if (j < l)
				entity.turnDirection = 1280;
			else if (j > l)
				entity.turnDirection = 1792;
			else
				entity.turnDirection = 1536;
		} else if (i > k) {
			if (j < l)
				entity.turnDirection = 768;
			else if (j > l)
				entity.turnDirection = 256;
			else
				entity.turnDirection = 512;
		} else if (j < l)
			entity.turnDirection = 1024;
		else
			entity.turnDirection = 0;
		int i1 = entity.turnDirection - entity.anInt1552 & 0x7ff;
		if (i1 > 1024)
			i1 -= 2048;
		int j1 = entity.anInt1555;
		if (i1 >= -256 && i1 <= 256)
			j1 = entity.anInt1554;
		else if (i1 >= 256 && i1 < 768)
			j1 = entity.anInt1557;
		else if (i1 >= -768 && i1 <= -256)
			j1 = entity.anInt1556;
		if (j1 == -1)
			j1 = entity.anInt1554;
		entity.anInt1517 = j1;
		int k1 = 4;
		if (entity.anInt1552 != entity.turnDirection
				&& entity.interactingEntity == -1 && entity.anInt1504 != 0)
			k1 = 2;
		if (entity.smallXYIndex > 2)
			k1 = 6;
		if (entity.smallXYIndex > 3)
			k1 = 8;
		if (entity.anInt1503 > 0 && entity.smallXYIndex > 1) {
			k1 = 8;
			entity.anInt1503--;
		}
		if (entity.aBooleanArray1553[entity.smallXYIndex - 1])
			k1 <<= 1;
		if (k1 >= 8 && entity.anInt1517 == entity.anInt1554
				&& entity.runAnimation != -1)
			entity.anInt1517 = entity.runAnimation;
		if (i < k) {
			entity.x += k1;
			if (entity.x > k)
				entity.x = k;
		} else if (i > k) {
			entity.x -= k1;
			if (entity.x < k)
				entity.x = k;
		}
		if (j < l) {
			entity.y += k1;
			if (entity.y > l)
				entity.y = l;
		} else if (j > l) {
			entity.y -= k1;
			if (entity.y < l)
				entity.y = l;
		}
		if (entity.x == k && entity.y == l) {
			entity.smallXYIndex--;
			if (entity.anInt1542 > 0)
				entity.anInt1542--;
		}
	}

	private void method100(Entity entity) {
		if (entity.anInt1504 == 0)
			return;
		if (entity.interactingEntity != -1 && entity.interactingEntity < 32768) {
			try {
				NPC npc = npcArray[entity.interactingEntity];
				if (npc != null) {
					int i1 = entity.x - npc.x;
					int k1 = entity.y - npc.y;
					if (i1 != 0 || k1 != 0)
						entity.turnDirection = (int) (Math.atan2(i1, k1) * 325.94900000000001D) & 0x7ff;
				}
			} catch (Exception ex) {
			}
		}
		if (entity.interactingEntity >= 32768) {
			int j = entity.interactingEntity - 32768;
			if (j == unknownInt10)
				j = myPlayerIndex;
			Player player = playerArray[j];
			if (player != null) {
				int l1 = entity.x - player.x;
				int i2 = entity.y - player.y;
				if (l1 != 0 || i2 != 0)
					entity.turnDirection = (int) (Math.atan2(l1, i2) * 325.94900000000001D) & 0x7ff;
			}
		}
		if ((entity.anInt1538 != 0 || entity.anInt1539 != 0)
				&& (entity.smallXYIndex == 0 || entity.anInt1503 > 0)) {
			int k = entity.x - (entity.anInt1538 - baseX - baseX) * 64;
			int j1 = entity.y - (entity.anInt1539 - baseY - baseY) * 64;
			if (k != 0 || j1 != 0)
				entity.turnDirection = (int) (Math.atan2(k, j1) * 325.94900000000001D) & 0x7ff;
			entity.anInt1538 = 0;
			entity.anInt1539 = 0;
		}
		int l = entity.turnDirection - entity.anInt1552 & 0x7ff;
		if (l != 0) {
			if (l < entity.anInt1504 || l > 2048 - entity.anInt1504)
				entity.anInt1552 = entity.turnDirection;
			else if (l > 1024)
				entity.anInt1552 -= entity.anInt1504;
			else
				entity.anInt1552 += entity.anInt1504;
			entity.anInt1552 &= 0x7ff;
			if (entity.anInt1517 == entity.anInt1511
					&& entity.anInt1552 != entity.turnDirection) {
				if (entity.anInt1512 != -1) {
					entity.anInt1517 = entity.anInt1512;
					return;
				}
				entity.anInt1517 = entity.anInt1554;
			}
		}
	}

	private void method101(Entity entity) {
		entity.aBoolean1541 = false;
		if (entity.anInt1517 != -1) {
			Animation animation = Animation.anims[entity.anInt1517];
			entity.anInt1519++;
			if (entity.anInt1518 < animation.anInt352
					&& entity.anInt1519 > animation.method258(entity.anInt1518)) {
				entity.anInt1519 = 1;// this is the frame delay. 0 is what it's
				// normally at. higher number = faster
				// animations.
				entity.anInt1518++;
			}
			if (entity.anInt1518 >= animation.anInt352) {
				entity.anInt1519 = 1;
				entity.anInt1518 = 0;
			}
		}
		if (entity.anInt1520 != -1 && loopCycle >= entity.anInt1523) {
			if (entity.anInt1521 < 0)
				entity.anInt1521 = 0;
			Animation animation_1 = SpotAnim.cache[entity.anInt1520].aAnimation_407;
			for (entity.anInt1522++; entity.anInt1521 < animation_1.anInt352
					&& entity.anInt1522 > animation_1
					.method258(entity.anInt1521); entity.anInt1521++)
				entity.anInt1522 -= animation_1.method258(entity.anInt1521);

			if (entity.anInt1521 >= animation_1.anInt352
					&& (entity.anInt1521 < 0 || entity.anInt1521 >= animation_1.anInt352))
				entity.anInt1520 = -1;
		}
		if (entity.anim != -1 && entity.anInt1529 <= 1) {
			Animation animation_2 = Animation.anims[entity.anim];
			if (animation_2.anInt363 == 1 && entity.anInt1542 > 0
					&& entity.anInt1547 <= loopCycle
					&& entity.anInt1548 < loopCycle) {
				entity.anInt1529 = 1;
				return;
			}
		}
		if (entity.anim != -1 && entity.anInt1529 == 0) {
			Animation animation_3 = Animation.anims[entity.anim];
			for (entity.anInt1528++; entity.anInt1527 < animation_3.anInt352
					&& entity.anInt1528 > animation_3
					.method258(entity.anInt1527); entity.anInt1527++)
				entity.anInt1528 -= animation_3.method258(entity.anInt1527);

			if (entity.anInt1527 >= animation_3.anInt352) {
				entity.anInt1527 -= animation_3.anInt356;
				entity.anInt1530++;
				if (entity.anInt1530 >= animation_3.anInt362)
					entity.anim = -1;
				if (entity.anInt1527 < 0
						|| entity.anInt1527 >= animation_3.anInt352)
					entity.anim = -1;
			}
			entity.aBoolean1541 = animation_3.aBoolean358;
		}
		if (entity.anInt1529 > 0)
			entity.anInt1529--;
	}

	private void drawGameScreen() {
		if (fullscreenInterfaceID != -1
				&& (loadingStage == 2 || super.fullGameScreen != null)) {
			if (loadingStage == 2) {
				method119(anInt945, fullscreenInterfaceID);
				if (openInterfaceID != -1) {
					method119(anInt945, openInterfaceID);
				}
				anInt945 = 0;
				resetAllImageProducers();
				super.fullGameScreen.initDrawingArea();
				Texture.lineOffsets = fullScreenTextureArray;
				DrawingArea.setAllPixelsToZero();
				welcomeScreenRaised = true;
				if (openInterfaceID != -1) {
					RSInterface rsInterface_1 = RSInterface.interfaceCache[openInterfaceID];
					if (rsInterface_1.width == 512
							&& rsInterface_1.height == 334
							&& rsInterface_1.type == 0) {
						rsInterface_1.width = (clientSize == 0 ? 765
								: clientWidth);
						rsInterface_1.height = (clientSize == 0 ? 503
								: clientHeight);
					}
					drawInterface(0, clientSize == 0 ? 0
							: (clientWidth / 2) - 765 / 2, rsInterface_1,
							clientSize == 0 ? 8 : (clientHeight / 2) - 503 / 2);
				}
				RSInterface rsInterface = RSInterface.interfaceCache[fullscreenInterfaceID];
				if (rsInterface.width == 512 && rsInterface.height == 334
						&& rsInterface.type == 0) {
					rsInterface.width = (clientSize == 0 ? 765 : clientWidth);
					rsInterface.height = (clientSize == 0 ? 503 : clientHeight);
				}
				drawInterface(0, clientSize == 0 ? 0
						: (clientWidth / 2) - 765 / 2, rsInterface,
						clientSize == 0 ? 8 : (clientHeight / 2) - 503 / 2);
				if (!menuOpen) {
					processRightClick();
					drawTooltip();
				} else {
					drawMenu();
				}
			}
			drawCount++;
			super.fullGameScreen.drawGraphics(0, super.graphics, 0);
			return;
		} else {
			if (drawCount != 0) {
				resetImageProducers2();
			}
		}
		if (welcomeScreenRaised) {
			welcomeScreenRaised = false;
			if (clientSize == 0) {
				topFrame.drawGraphics(0, super.graphics, 0);
				leftFrame.drawGraphics(4, super.graphics, 0);
				rightFrame.drawGraphics(3, super.graphics, 516);
			}
			needDrawTabArea = true;
			inputTaken = true;
			tabAreaAltered = true;
			if (loadingStage != 2) {
				gameScreenIP.drawGraphics(clientSize == 0 ? 4 : 0,
						super.graphics, clientSize == 0 ? 4 : 0);
				if (clientSize == 0)
					mapAreaIP.drawGraphics(0, super.graphics, 516);
			}
		}
		if (menuOpen && menuScreenArea == 1)
			needDrawTabArea = true;
		if (invOverlayInterfaceID != -1) {
			boolean flag1 = method119(anInt945, invOverlayInterfaceID);
			if (flag1)
				needDrawTabArea = true;
		}
		if (atInventoryInterfaceType == 2)
			needDrawTabArea = true;
		if (activeInterfaceType == 2)
			needDrawTabArea = true;
		if (needDrawTabArea) {
			if (clientSize == 0) {
				drawTabArea();
			}
			needDrawTabArea = false;
		}
		if (backDialogID == -1 && inputDialogState == 3) {
			int position = totalItemResults * 14 + 7;
			aClass9_1059.scrollPosition = itemResultScrollPos;
			if (super.mouseX > 478 && super.mouseX < 580
					&& super.mouseY > (clientHeight - 161)) {
				method65(494, 110, super.mouseX - 0, super.mouseY
						- (clientHeight - 155), aClass9_1059, 0, false,
						totalItemResults);
			}
			int scrollPosition = aClass9_1059.scrollPosition;
			if (scrollPosition < 0) {
				scrollPosition = 0;
			}
			if (scrollPosition > position - 110) {
				scrollPosition = position - 110;
			}
			if (itemResultScrollPos != scrollPosition) {
				itemResultScrollPos = scrollPosition;
				inputTaken = true;
			}
		}
		if (backDialogID == -1 && inputDialogState != 3) {
			aClass9_1059.scrollPosition = anInt1211 - anInt1089 - 110;
			if (super.mouseX > 478 && super.mouseX < 580
					&& super.mouseY > (clientHeight - 161))
				method65(494, 110, super.mouseX - 0, super.mouseY
						- (clientHeight - 155), aClass9_1059, 0, false,
						anInt1211);
			int i = anInt1211 - 110 - aClass9_1059.scrollPosition;
			if (i < 0)
				i = 0;
			if (i > anInt1211 - 110)
				i = anInt1211 - 110;
			if (anInt1089 != i) {
				anInt1089 = i;
				inputTaken = true;
			}
		}
		if (backDialogID != -1) {
			boolean flag2 = method119(anInt945, backDialogID);
			if (flag2)
				inputTaken = true;
		}
		if (atInventoryInterfaceType == 3)
			inputTaken = true;
		if (activeInterfaceType == 3)
			inputTaken = true;
		if (aString844 != null)
			inputTaken = true;
		if (menuOpen && menuScreenArea == 2)
			inputTaken = true;
		if (inputTaken) {
			if (clientSize == 0) {
				drawChatArea();
				gameScreenIP.initDrawingArea();
			}
			inputTaken = false;
		}
		if (loadingStage == 2)
			try {
				method146();
			} catch (Exception e) {
			}
		if (loadingStage == 2) {
			if (clientSize == 0) {
				drawMinimap();
				mapAreaIP.drawGraphics(0, super.graphics, 765 - 246);
			}
		}
		if (anInt1054 != -1)
			tabAreaAltered = true;
		if (tabAreaAltered) {
			if (anInt1054 != -1 && anInt1054 == tabID) {
				anInt1054 = -1;
				stream.createFrame(120);
				stream.writeWordBigEndian(tabID);
			}
			tabAreaAltered = false;
			if (clientSize == 0)
				rightFrame.drawGraphics(3, super.graphics, 516);
			GraphicsBuffer_1125.initDrawingArea();
			gameScreenIP.initDrawingArea();
		}
		anInt945 = 0;
	}

	private boolean buildFriendsListMenu(RSInterface class9) {
		int i = class9.contentType;
		if (i >= 1 && i <= 200 || i >= 701 && i <= 900) {
			if (i >= 801)
				i -= 701;
			else if (i >= 701)
				i -= 601;
			else if (i >= 101)
				i -= 101;
			else
				i--;
			menuActionName[menuActionRow] = "Remove @whi@" + friendsList[i];
			menuActionID[menuActionRow] = 792;
			menuActionRow++;
			menuActionName[menuActionRow] = "Message @whi@" + friendsList[i];
			menuActionID[menuActionRow] = 639;
			menuActionRow++;
			return true;
		}
		if (i >= 401 && i <= 500) {
			menuActionName[menuActionRow] = "Remove @whi@" + class9.message;
			menuActionID[menuActionRow] = 322;
			menuActionRow++;
			return true;
		} else {
			return false;
		}
	}

	private void method104() {
		Animable_Sub3 class30_sub2_sub4_sub3 = (Animable_Sub3) aClass19_1056
				.getFront();
		for (; class30_sub2_sub4_sub3 != null; class30_sub2_sub4_sub3 = (Animable_Sub3) aClass19_1056
				.reverseGetNext())
			if (class30_sub2_sub4_sub3.anInt1560 != plane
			|| class30_sub2_sub4_sub3.aBoolean1567)
				class30_sub2_sub4_sub3.unlink();
			else if (loopCycle >= class30_sub2_sub4_sub3.anInt1564) {
				class30_sub2_sub4_sub3.method454(anInt945);
				if (class30_sub2_sub4_sub3.aBoolean1567)
					class30_sub2_sub4_sub3.unlink();
				else
					worldController.method285(class30_sub2_sub4_sub3.anInt1560,
							0, class30_sub2_sub4_sub3.anInt1563, -1,
							class30_sub2_sub4_sub3.anInt1562, 60,
							class30_sub2_sub4_sub3.anInt1561,
							class30_sub2_sub4_sub3, false);
			}

	}

	public void drawBlackBox(int xPos, int yPos) {
		DrawingArea.drawPixels(71, yPos - 1, xPos - 2, 0x726451, 1);
		DrawingArea.drawPixels(69, yPos, xPos + 174, 0x726451, 1);
		DrawingArea.drawPixels(1, yPos - 2, xPos - 2, 0x726451, 178);
		DrawingArea.drawPixels(1, yPos + 68, xPos, 0x726451, 174);
		DrawingArea.drawPixels(71, yPos - 1, xPos - 1, 0x2E2B23, 1);
		DrawingArea.drawPixels(71, yPos - 1, xPos + 175, 0x2E2B23, 1);
		DrawingArea.drawPixels(1, yPos - 1, xPos, 0x2E2B23, 175);
		DrawingArea.drawPixels(1, yPos + 69, xPos, 0x2E2B23, 175);
		DrawingArea.method335(0, yPos, 174, 68, 220, xPos);
	}

	private void drawInterface(int scrollOffset, int interfaceX,
			RSInterface rsInterface, int interfaceY) {
		if (rsInterface.type != 0 || rsInterface.children == null)
			return;
		if (rsInterface.interfaceShown && anInt1026 != rsInterface.id
				&& anInt1048 != rsInterface.id && anInt1039 != rsInterface.id)
			return;
		int i1 = DrawingArea.topX;
		int j1 = DrawingArea.topY;
		int k1 = DrawingArea.bottomX;
		int l1 = DrawingArea.bottomY;
		DrawingArea.setDrawingArea(interfaceY + rsInterface.height, interfaceX,
				interfaceX + rsInterface.width, interfaceY);
		int totalChildrens = rsInterface.children.length;
		for (int childID = 0; childID < totalChildrens; childID++) {
			int childX = rsInterface.childX[childID] + interfaceX;
			int childY = (rsInterface.childY[childID] + interfaceY)
					- scrollOffset;
			RSInterface child = RSInterface.interfaceCache[rsInterface.children[childID]];
			childX += child.xOffset;
			childY += child.yOffset;
			if (child.contentType > 0)
				drawFriendsListOrWelcomeScreen(child);
			// here
			int[] IDs = { 1196, 1199, 1206, 1215, 1224, 1231, 1240, 1249, 1258,
					1267, 1274, 1283, 1573, 1290, 1299, 1308, 1315, 1324, 1333,
					1340, 1349, 1358, 1367, 1374, 1381, 1388, 1397, 1404, 1583,
					12038, 1414, 1421, 1430, 1437, 1446, 1453, 1460, 1469,
					15878, 1602, 1613, 1624, 7456, 1478, 1485, 1494, 1503,
					1512, 1521, 1530, 1544, 1553, 1563, 1593, 1635, 12426,
					12436, 12446, 12456, 6004, 18471,
					/* Ancients */
					12940, 12988, 13036, 12902, 12862, 13046, 12964, 13012,
					13054, 12920, 12882, 13062, 12952, 13000, 13070, 12912,
					12872, 13080, 12976, 13024, 13088, 12930, 12892, 13096 };
			for (int m5 = 0; m5 < IDs.length; m5++) {
				if (child.id == IDs[m5] + 1) {
					if (m5 > 61)
						drawBlackBox(childX + 1, childY);
					else
						drawBlackBox(childX, childY + 1);
				}
			}
			int[] runeChildren = { 1202, 1203, 1209, 1210, 1211, 1218, 1219,
					1220, 1227, 1228, 1234, 1235, 1236, 1243, 1244, 1245, 1252,
					1253, 1254, 1261, 1262, 1263, 1270, 1271, 1277, 1278, 1279,
					1286, 1287, 1293, 1294, 1295, 1302, 1303, 1304, 1311, 1312,
					1318, 1319, 1320, 1327, 1328, 1329, 1336, 1337, 1343, 1344,
					1345, 1352, 1353, 1354, 1361, 1362, 1363, 1370, 1371, 1377,
					1378, 1384, 1385, 1391, 1392, 1393, 1400, 1401, 1407, 1408,
					1410, 1417, 1418, 1424, 1425, 1426, 1433, 1434, 1440, 1441,
					1442, 1449, 1450, 1456, 1457, 1463, 1464, 1465, 1472, 1473,
					1474, 1481, 1482, 1488, 1489, 1490, 1497, 1498, 1499, 1506,
					1507, 1508, 1515, 1516, 1517, 1524, 1525, 1526, 1533, 1534,
					1535, 1547, 1548, 1549, 1556, 1557, 1558, 1566, 1567, 1568,
					1576, 1577, 1578, 1586, 1587, 1588, 1596, 1597, 1598, 1605,
					1606, 1607, 1616, 1617, 1618, 1627, 1628, 1629, 1638, 1639,
					1640, 6007, 6008, 6011, 8673, 8674, 12041, 12042, 12429,
					12430, 12431, 12439, 12440, 12441, 12449, 12450, 12451,
					12459, 12460, 15881, 15882, 15885, 18474, 18475, 18478 };
			for (int r = 0; r < runeChildren.length; r++)
				if (child.id == runeChildren[r])
					child.modelZoom = 775;
			if (child.type == 0) {
				if (child.scrollPosition > child.scrollMax - child.height)
					child.scrollPosition = child.scrollMax - child.height;
				if (child.scrollPosition < 0)
					child.scrollPosition = 0;
				drawInterface(child.scrollPosition, childX, child, childY);
				if (child.scrollMax > child.height)
					drawScrollbar(child.height, child.scrollPosition, childY,
							childX + child.width, child.scrollMax, false, false);
			} else if (child.type != 1)
				if (child.type == 2) {
					int spriteIndex = 0;
					for (int l3 = 0; l3 < child.height; l3++) {
						for (int l4 = 0; l4 < child.width; l4++) {
							int k5 = childX + l4 * (32 + child.invSpritePadX);
							int j6 = childY + l3 * (32 + child.invSpritePadY);
							if (spriteIndex < 20) {
								k5 += child.spritesX[spriteIndex];
								j6 += child.spritesY[spriteIndex];
							}
							if (child.inv[spriteIndex] > 0) {
								int k6 = 0;
								int j7 = 0;
								int j9 = child.inv[spriteIndex] - 1;
								if (k5 > DrawingArea.topX - 32
										&& k5 < DrawingArea.bottomX
										&& j6 > DrawingArea.topY - 32
										&& j6 < DrawingArea.bottomY
										|| activeInterfaceType != 0
										&& anInt1085 == spriteIndex) {
									int selectedColour = 0;
									if (itemSelected == 1
											&& anInt1283 == spriteIndex
											&& anInt1284 == child.id)
										selectedColour = 0xffffff;
									Sprite class30_sub2_sub1_sub1_2 = ItemDef
											.getSprite(
													j9,
													child.invStackSizes[spriteIndex],
													selectedColour);
									if (class30_sub2_sub1_sub1_2 != null) {
										if (activeInterfaceType != 0
												&& anInt1085 == spriteIndex
												&& anInt1084 == child.id) {
											k6 = super.mouseX - anInt1087;
											j7 = super.mouseY - anInt1088;
											if (k6 < 5 && k6 > -5)
												k6 = 0;
											if (j7 < 5 && j7 > -5)
												j7 = 0;
											if (anInt989 < 10) {
												k6 = 0;
												j7 = 0;
											}
											class30_sub2_sub1_sub1_2
											.drawSprite1(k5 + k6, j6
													+ j7);
											if (j6 + j7 < DrawingArea.topY
													&& rsInterface.scrollPosition > 0) {
												int i10 = (anInt945 * (DrawingArea.topY
														- j6 - j7)) / 3;
												if (i10 > anInt945 * 10)
													i10 = anInt945 * 10;
												if (i10 > rsInterface.scrollPosition)
													i10 = rsInterface.scrollPosition;
												rsInterface.scrollPosition -= i10;
												anInt1088 += i10;
											}
											if (j6 + j7 + 32 > DrawingArea.bottomY
													&& rsInterface.scrollPosition < rsInterface.scrollMax
													- rsInterface.height) {
												int j10 = (anInt945 * ((j6 + j7 + 32) - DrawingArea.bottomY)) / 3;
												if (j10 > anInt945 * 10)
													j10 = anInt945 * 10;
												if (j10 > rsInterface.scrollMax
														- rsInterface.height
														- rsInterface.scrollPosition)
													j10 = rsInterface.scrollMax
													- rsInterface.height
													- rsInterface.scrollPosition;
												rsInterface.scrollPosition += j10;
												anInt1088 -= j10;
											}
										} else if (atInventoryInterfaceType != 0
												&& atInventoryIndex == spriteIndex
												&& atInventoryInterface == child.id)
											class30_sub2_sub1_sub1_2
											.drawSprite1(k5, j6);
										else
											class30_sub2_sub1_sub1_2
											.drawSprite(k5, j6);
										if (class30_sub2_sub1_sub1_2.maxWidth == 33
												|| child.invStackSizes[spriteIndex] != 1) {
											int k10 = child.invStackSizes[spriteIndex];
											if (child.actions[0] != "[GE]"
													&& child.actions[1] != "[GE]"
													&& child.actions[2] != "[GE]"
													&& child.actions[3] != "[GE]"
													&& child.actions[4] != "[GE]") {
												smallText.method385(0,
														intToKOrMil(k10), j6
														+ 10 + j7, k5
														+ 1 + k6);
												if (k10 > 99999
														&& k10 < 10000000)
													smallText.method385(
															0xFFFFFF,
															intToKOrMil(k10),
															j6 + 9 + j7, k5
															+ k6);
												else if (k10 > 9999999)
													smallText.method385(
															0x00ff80,
															intToKOrMil(k10),
															j6 + 9 + j7, k5
															+ k6);
												else
													smallText.method385(
															0xFFFF00,
															intToKOrMil(k10),
															j6 + 9 + j7, k5
															+ k6);
											}
										}
									}
								}
							} else if (child.sprites != null
									&& spriteIndex < 20) {
								Sprite class30_sub2_sub1_sub1_1 = child.sprites[spriteIndex];
								if (class30_sub2_sub1_sub1_1 != null)
									class30_sub2_sub1_sub1_1.drawSprite(k5, j6);
							}
							spriteIndex++;
						}
					}
				} else if (child.type == 3) {
					boolean flag = false;
					if (anInt1039 == child.id || anInt1048 == child.id
							|| anInt1026 == child.id)
						flag = true;
					int j3;
					if (interfaceIsSelected(child)) {
						j3 = child.anInt219;
						if (flag && child.anInt239 != 0)
							j3 = child.anInt239;
					} else {
						j3 = child.textColor;
						if (flag && child.anInt216 != 0)
							j3 = child.anInt216;
					}
					if (child.opacity == 0) {
						if (child.aBoolean227)
							DrawingArea.drawPixels(child.height, childY,
									childX, j3, child.width);
						else
							DrawingArea.fillPixels(childX, child.width,
									child.height, j3, childY);
					} else if (child.aBoolean227)
						DrawingArea.method335(j3, childY, child.width,
								child.height, 256 - (child.opacity & 0xff),
								childX);
					else
						DrawingArea.method338(childY, child.height,
								256 - (child.opacity & 0xff), j3, child.width,
								childX);
				} else if (child.type == 4) {
					TextDrawingArea textDrawingArea = child.textDrawingAreas;
					String s = child.message;
					boolean flag1 = false;
					if (anInt1039 == child.id || anInt1048 == child.id
							|| anInt1026 == child.id)
						flag1 = true;
					int i4;
					if (interfaceIsSelected(child)) {
						i4 = child.anInt219;
						if (flag1 && child.anInt239 != 0)
							i4 = child.anInt239;
						if (child.aString228.length() > 0)
							s = child.aString228;
					} else {
						i4 = child.textColor;
						if (flag1 && child.anInt216 != 0)
							i4 = child.anInt216;
					}
					if (child.atActionType == 6 && aBoolean1149) {
						s = "Please wait...";
						i4 = child.textColor;
					}
					if (DrawingArea.width == chatAreaIP.anInt316
							|| rsInterface.id == backDialogID) {
						if (i4 == 0xffff00)
							i4 = 255;
						if (i4 == 49152)
							i4 = 0xffffff;
					}
					if ((child.parentID == 1151) || (child.parentID == 12855)) {
						switch (i4) {
						case 16773120:
							i4 = 0xFE981F;
							break;
						case 7040819:
							i4 = 0xAF6A1A;
							break;
						}
					}
					for (int l6 = childY + textDrawingArea.anInt1497; s
							.length() > 0; l6 += textDrawingArea.anInt1497) {
						if (s.indexOf("%") != -1) {
							do {
								int k7 = s.indexOf("%1");
								if (k7 == -1)
									break;
								if (child.id < 4000 || child.id > 5000
										&& child.id != 13921
										&& child.id != 13922
										&& child.id != 12171
										&& child.id != 12172)
									s = s.substring(0, k7)
									+ methodR(extractInterfaceValues(
											child, 0))
											+ s.substring(k7 + 2);
								else
									s = s.substring(0, k7)
									+ interfaceIntToString(extractInterfaceValues(
											child, 0))
											+ s.substring(k7 + 2);
							} while (true);
							do {
								int l7 = s.indexOf("%2");
								if (l7 == -1)
									break;
								s = s.substring(0, l7)
										+ interfaceIntToString(extractInterfaceValues(
												child, 1))
												+ s.substring(l7 + 2);
							} while (true);
							do {
								int i8 = s.indexOf("%3");
								if (i8 == -1)
									break;
								s = s.substring(0, i8)
										+ interfaceIntToString(extractInterfaceValues(
												child, 2))
												+ s.substring(i8 + 2);
							} while (true);
							do {
								int j8 = s.indexOf("%4");
								if (j8 == -1)
									break;
								s = s.substring(0, j8)
										+ interfaceIntToString(extractInterfaceValues(
												child, 3))
												+ s.substring(j8 + 2);
							} while (true);
							do {
								int k8 = s.indexOf("%5");
								if (k8 == -1)
									break;
								s = s.substring(0, k8)
										+ interfaceIntToString(extractInterfaceValues(
												child, 4))
												+ s.substring(k8 + 2);
							} while (true);
						}
						int l8 = s.indexOf("\\n");
						String s1;
						if (l8 != -1) {
							s1 = s.substring(0, l8);
							s = s.substring(l8 + 2);
						} else {
							s1 = s;
							s = "";
						}
						if (child.centerText)
							textDrawingArea.method382(i4, childX + child.width
									/ 2, s1, l6, child.textShadow);
						else
							textDrawingArea.method389(child.textShadow, childX,
									i4, s1, l6);
					}
				} else if (child.type == 5) {
					Sprite sprite;
					if (child.itemSpriteId1 != -1 && child.sprite1 == null) {
						child.sprite1 = ItemDef.getSprite(child.itemSpriteId1,
								1, (child.itemSpriteZoom1 == -1) ? 0 : -1,
										child.itemSpriteZoom1);
						child.sprite2 = ItemDef.getSprite(child.itemSpriteId2,
								1, (child.itemSpriteZoom2 == -1) ? 0 : -1,
										child.itemSpriteZoom2);
						// child.sprite2 =
						// ItemDef.getSprite(child.itemSpriteId2,
						// child.invStackSizes[spriteIndex], selectedColour);
						if (child.greyScale) {
							child.sprite1.greyScale();
							// child.sprite2.greyScale();
						}
					}
					if (interfaceIsSelected(child) || hoverSpriteId == child.id)
						sprite = child.sprite2;
					else
						sprite = child.sprite1;
					if (child.id == 1164 || child.id == 1167
							|| child.id == 1170 || child.id == 1174
							|| child.id == 1540 || child.id == 1541
							|| child.id == 7455 || child.id == 18470
							|| child.id == 13035 || child.id == 13045
							|| child.id == 13053 || child.id == 13061
							|| child.id == 13069 || child.id == 13079
							|| child.id == 30064 || child.id == 30075
							|| child.id == 30083 || child.id == 30106
							|| child.id == 30114 || child.id == 30106
							|| child.id == 30170 || child.id == 13087
							|| child.id == 30162 || child.id == 13095)
						sprite = child.sprite2;
					if (spellSelected == 1 && child.id == spellID
							&& spellID != 0 && sprite != null) {
						sprite.drawSprite(childX, childY, 0xffffff);
					} else {
						if (sprite != null)
							if (child.type == 5)
								sprite.drawSprite(childX, childY);
							else
								sprite.drawSprite1(childX, childY,
										child.opacity);
					}
					if (Autocast && child.id == autocastId)
						magicAuto.drawSprite(childX - 3, childY - 2);
					// if (sprite != null)
					// sprite.drawSprite(childX, childY);
					// if (sprite != null)
					// if (child.drawsTransparent) {
					// sprite.drawTransparentSprite(childX, childY, alpha);
					// } else {
					// sprite.drawSprite(childX, childY);
					// }
				} else if (child.type == 6) {
					int k3 = Texture.centerX;
					int j4 = Texture.centerY;
					Texture.centerX = childX + child.width / 2;
					Texture.centerY = childY + child.height / 2;
					int i5 = Texture.SINE[child.modelRotation1]
							* child.modelZoom >> 16;
				int l5 = Texture.COSINE[child.modelRotation1]
						* child.modelZoom >> 16;
						boolean flag2 = interfaceIsSelected(child);
						int i7;
						if (flag2)
							i7 = child.anInt258;
						else
							i7 = child.anInt257;
						Model model;
						if (i7 == -1) {
							model = child.method209(-1, -1, flag2);
						} else {
							Animation animation = Animation.anims[i7];
							model = child.method209(
									animation.anIntArray354[child.anInt246],
									animation.anIntArray353[child.anInt246], flag2);
						}
						if (model != null)
							model.method482(child.modelRotation2, 0,
									child.modelRotation1, 0, i5, l5);
						Texture.centerX = k3;
						Texture.centerY = j4;
				} else if (child.type == 7) {
					TextDrawingArea textDrawingArea_1 = child.textDrawingAreas;
					int k4 = 0;
					for (int j5 = 0; j5 < child.height; j5++) {
						for (int i6 = 0; i6 < child.width; i6++) {
							if (child.inv[k4] > 0) {
								ItemDef itemDef = ItemDef
										.forID(child.inv[k4] - 1);
								String s2 = itemDef.name;
								if (itemDef.stackable
										|| child.invStackSizes[k4] != 1)
									s2 = s2
									+ " x"
									+ intToKOrMilLongName(child.invStackSizes[k4]);
								int i9 = childX + i6
										* (115 + child.invSpritePadX);
								int k9 = childY + j5
										* (12 + child.invSpritePadY);
								if (child.centerText)
									textDrawingArea_1.method382(
											child.textColor, i9 + child.width
											/ 2, s2, k9,
											child.textShadow);
								else
									textDrawingArea_1.method389(
											child.textShadow, i9,
											child.textColor, s2, k9);
							}
							k4++;
						}
					}
				} else if (child.type == 8) {
					if (interfaceIsSelected(child)) {
					} else
						try {
							drawHoverBox(childX, childY, child.popupString);
						} catch (Exception e) {
						}
				} else if (child.type == 9) {
					Sprite sprite;
					if (interfaceIsSelected(child)) {
						sprite = child.sprite2;
					} else {
						sprite = child.sprite1;
					}
					if (sprite != null) {
						sprite.drawSpriteWithOpacity(childX, childY,
								child.opacity);
					}
				} else if (child.type == 10
						&& (anInt1500 == child.id || anInt1044 == child.id || anInt1129 == child.id)
						&& !menuOpen) {
					int boxWidth = 0;
					int boxHeight = 0;
					TextDrawingArea textDrawingArea_2 = aTextDrawingArea_1271;
					for (String s1 = child.message; s1.length() > 0;) {
						int l7 = s1.indexOf("\\n");
						String s4;
						if (l7 != -1) {
							s4 = s1.substring(0, l7);
							s1 = s1.substring(l7 + 2);
						} else {
							s4 = s1;
							s1 = "";
						}
						int j10 = textDrawingArea_2.getTextWidth(s4);
						if (j10 > boxWidth) {
							boxWidth = j10;
						}
						boxHeight += textDrawingArea_2.anInt1497 + 1;
					}
					boxWidth += 6;
					boxHeight += 7;
					int xPos = (childX + child.width) - 5 - boxWidth;
					int yPos = childY + child.height + 5;
					if (xPos < childX + 5) {
						xPos = childX + 5;
					}
					if (xPos + boxWidth > interfaceX + rsInterface.width) {
						xPos = (interfaceX + rsInterface.width) - boxWidth;
					}
					if (yPos + boxHeight > interfaceY + rsInterface.height) {
						yPos = (interfaceY + rsInterface.height) - boxHeight;
					}
					if (clientSize != 0) {
						if (childX == clientWidth - 69
								|| childX == clientWidth - 131) {
							xPos -= (childX == clientWidth - 69) ? 100 : 20;
						}
						if (childY == clientHeight
								- (clientWidth <= smallTabs ? 112 : 75)) {
							yPos -= 100;
						}
						// System.out.println("ChildX = "+
						// childX+", childY = "+childY+", clientWidth = "+clientWidth+" , clientHeight = "+clientHeight);
					} else {
						if (child.inventoryHover) {
							if (xPos + boxWidth + interfaceX > 249) {
								xPos = 251 - boxWidth - interfaceX;
							}
							if (yPos + boxHeight + interfaceY > 261) {
								yPos = 245 - boxHeight - interfaceY;
							}
						}
					}
					DrawingArea.drawPixels(boxHeight, yPos, xPos, 0xFFFFA0,
							boxWidth);
					DrawingArea.fillPixels(xPos, boxWidth, boxHeight, 0, yPos);
					String s2 = child.message;
					for (int j11 = yPos + textDrawingArea_2.anInt1497 + 2; s2
							.length() > 0; j11 += textDrawingArea_2.anInt1497 + 1) {
						int l11 = s2.indexOf("\\n");
						String s5;
						if (l11 != -1) {
							s5 = s2.substring(0, l11);
							s2 = s2.substring(l11 + 2);
						} else {
							s5 = s2;
							s2 = "";
						}
						textDrawingArea_2
						.method389(false, xPos + 3, 0, s5, j11);
					}
				}
		}
		DrawingArea.setDrawingArea(l1, i1, k1, j1);
	}

	private void randomizeBackground(Background background) {
		int j = 256;
		for (int k = 0; k < anIntArray1190.length; k++)
			anIntArray1190[k] = 0;

		for (int l = 0; l < 5000; l++) {
			int i1 = (int) (Math.random() * 128D * (double) j);
			anIntArray1190[i1] = (int) (Math.random() * 256D);
		}
		for (int j1 = 0; j1 < 20; j1++) {
			for (int k1 = 1; k1 < j - 1; k1++) {
				for (int i2 = 1; i2 < 127; i2++) {
					int k2 = i2 + (k1 << 7);
					anIntArray1191[k2] = (anIntArray1190[k2 - 1]
							+ anIntArray1190[k2 + 1] + anIntArray1190[k2 - 128] + anIntArray1190[k2 + 128]) / 4;
				}

			}
			int ai[] = anIntArray1190;
			anIntArray1190 = anIntArray1191;
			anIntArray1191 = ai;
		}
		if (background != null) {
			int l1 = 0;
			for (int j2 = 0; j2 < background.anInt1453; j2++) {
				for (int l2 = 0; l2 < background.anInt1452; l2++)
					if (background.aByteArray1450[l1++] != 0) {
						int i3 = l2 + 16 + background.anInt1454;
						int j3 = j2 + 16 + background.anInt1455;
						int k3 = i3 + (j3 << 7);
						anIntArray1190[k3] = 0;
					}
			}
		}
	}

	private void method107(int i, int j, Stream stream, Player player) {
		/*
		 * Player updating method
		 */
		if ((i & 0x400) != 0) {
			player.anInt1543 = stream.method428();
			player.anInt1545 = stream.method428();
			player.anInt1544 = stream.method428();
			player.anInt1546 = stream.method428();
			player.anInt1547 = stream.method436() + loopCycle;
			player.anInt1548 = stream.method435() + loopCycle;
			player.anInt1549 = stream.method428();
			player.method446();
		}
		if ((i & 0x100) != 0) {
			player.anInt1520 = stream.method434();
			int k = stream.readDWord();
			player.anInt1524 = k >> 16;
						player.anInt1523 = loopCycle + (k & 0xffff);
						player.anInt1521 = 0;
						player.anInt1522 = 0;
						if (player.anInt1523 > loopCycle)
							player.anInt1521 = -1;
						if (player.anInt1520 == 65535)
							player.anInt1520 = -1;
		}
		if ((i & 8) != 0) {
			int l = stream.method434();
			if (l == 65535)
				l = -1;
			int i2 = stream.method427();
			if (l == player.anim && l != -1) {
				int i3 = Animation.anims[l].anInt365;
				if (i3 == 1) {
					player.anInt1527 = 0;
					player.anInt1528 = 0;
					player.anInt1529 = i2;
					player.anInt1530 = 0;
				}
				if (i3 == 2)
					player.anInt1530 = 0;
			} else if (l == -1
					|| player.anim == -1
					|| Animation.anims[l].anInt359 >= Animation.anims[player.anim].anInt359) {
				player.anim = l;
				player.anInt1527 = 0;
				player.anInt1528 = 0;
				player.anInt1529 = i2;
				player.anInt1530 = 0;
				player.anInt1542 = player.smallXYIndex;
			}
		}
		if ((i & 4) != 0) {
			player.textSpoken = stream.readString();
			if (player.textSpoken.charAt(0) == '~') {
				player.textSpoken = player.textSpoken.substring(1);
				pushMessage(player.textSpoken, 2, player.name);
			} else if (player == myPlayer)
				pushMessage(player.textSpoken, 2, player.name);
			player.anInt1513 = 0;
			player.anInt1531 = 0;
			player.textCycle = 150;
		}
		if ((i & 0x80) != 0) {
			int i1 = stream.method434();
			int rights = stream.readUnsignedByte();
			int j3 = stream.method427();
			int k3 = stream.currentOffset;
			if (player.name != null && player.visible) {
				long l3 = TextClass.longForName(player.name);
				boolean flag = false;
				if (rights <= 1) {
					for (int i4 = 0; i4 < ignoreCount; i4++) {
						if (ignoreListAsLongs[i4] != l3)
							continue;
						flag = true;
						break;
					}

				}
				if (!flag && anInt1251 == 0)
					try {
						aStream_834.currentOffset = 0;
						stream.method442(j3, 0, aStream_834.buffer);
						aStream_834.currentOffset = 0;
						String message = TextInput.method525(j3, aStream_834);
						player.textSpoken = message;
						player.anInt1513 = i1 >> 8;
					player.rights = rights;
					player.anInt1531 = i1 & 0xff;
					player.textCycle = 150;
					pushMessage(message, 2, getPrefix(rights) + player.name);
					} catch (Exception exception) {
						signlink.reporterror("cde2");
					}
			}
			stream.currentOffset = k3 + j3;
		}
		if ((i & 1) != 0) {
			player.interactingEntity = stream.method434();
			if (player.interactingEntity == 65535)
				player.interactingEntity = -1;
		}
		if ((i & 0x10) != 0) {
			int j1 = stream.method427();
			byte abyte0[] = new byte[j1];
			Stream stream_1 = new Stream(abyte0);
			stream.readBytes(j1, 0, abyte0);
			aStreamArray895s[j] = stream_1;
			player.updatePlayer(stream_1);
		}
		if ((i & 2) != 0) {
			player.anInt1538 = stream.method436();
			player.anInt1539 = stream.method434();
		}
		if ((i & 0x20) != 0) {
			int k1 = inStream.method435();
			int k2 = stream.readUnsignedByte();
			int icon = stream.readUnsignedByte();
			int soakDamage = inStream.method435();
			player.updateHitData(k2, k1, loopCycle, icon, soakDamage);
			player.loopCycleStatus = loopCycle + 300;
			player.constitution = player.currentHealth = inStream.method435();
			player.maxConstitution = player.maxHealth = inStream.method435();
		}
		if ((i & 0x200) != 0) {
			int l1 = inStream.method435();
			int l2 = stream.readUnsignedByte();
			int icon = stream.readUnsignedByte();
			int soakDamage = inStream.method435();
			player.updateHitData(l2, l1, loopCycle, icon, soakDamage);
			player.loopCycleStatus = loopCycle + 300;
			player.constitution = player.currentHealth = inStream.method435();
			player.maxConstitution = player.maxHealth = inStream.method435();
		}
	}

	private void method108() {
		try {
			int j = myPlayer.x + cameraOffsetX;
			int k = myPlayer.y + cameraOffsetY;
			if (anInt1014 - j < -500 || anInt1014 - j > 500
					|| anInt1015 - k < -500 || anInt1015 - k > 500) {
				anInt1014 = j;
				anInt1015 = k;
			}
			if (anInt1014 != j)
				anInt1014 += (j - anInt1014) / 16;
			if (anInt1015 != k)
				anInt1015 += (k - anInt1015) / 16;
			if (super.keyArray[1] == 1)
				anInt1186 += (-24 - anInt1186) / 2;
			else if (super.keyArray[2] == 1)
				anInt1186 += (24 - anInt1186) / 2;
			else
				anInt1186 /= 2;
			if (super.keyArray[3] == 1)
				anInt1187 += (12 - anInt1187) / 2;
			else if (super.keyArray[4] == 1)
				anInt1187 += (-12 - anInt1187) / 2;
			else
				anInt1187 /= 2;
			viewRotation = viewRotation + anInt1186 / 2 & 0x7ff;
			anInt1184 += anInt1187 / 2;
			if (anInt1184 < 128)
				anInt1184 = 128;
			if (anInt1184 > 383)
				anInt1184 = 383;
			int l = anInt1014 >> 7;
					int i1 = anInt1015 >> 7;
				int j1 = method42(plane, anInt1015, anInt1014);
				int k1 = 0;
				if (l > 3 && i1 > 3 && l < 100 && i1 < 100) {
					for (int l1 = l - 4; l1 <= l + 4; l1++) {
						for (int k2 = i1 - 4; k2 <= i1 + 4; k2++) {
							int l2 = plane;
							if (l2 < 3 && (byteGroundArray[1][l1][k2] & 2) == 2)
								l2++;
							int i3 = j1 - intGroundArray[l2][l1][k2];
							if (i3 > k1)
								k1 = i3;
						}

					}

				}
				anInt1005++;
				if (anInt1005 > 1512) {
					anInt1005 = 0;
					stream.createFrame(77);
					stream.writeWordBigEndian(0);
					int i2 = stream.currentOffset;
					stream.writeWordBigEndian((int) (Math.random() * 256D));
					stream.writeWordBigEndian(101);
					stream.writeWordBigEndian(233);
					stream.writeWord(45092);
					if ((int) (Math.random() * 2D) == 0)
						stream.writeWord(35784);
					stream.writeWordBigEndian((int) (Math.random() * 256D));
					stream.writeWordBigEndian(64);
					stream.writeWordBigEndian(38);
					stream.writeWord((int) (Math.random() * 65536D));
					stream.writeWord((int) (Math.random() * 65536D));
					stream.writeBytes(stream.currentOffset - i2);
				}
				int j2 = k1 * 192;
				if (j2 > 0x17f00)
					j2 = 0x17f00;
				if (j2 < 32768)
					j2 = 32768;
				if (j2 > anInt984) {
					anInt984 += (j2 - anInt984) / 24;
					return;
				}
				if (j2 < anInt984) {
					anInt984 += (j2 - anInt984) / 80;
				}
		} catch (Exception _ex) {
			signlink.reporterror("glfc_ex " + myPlayer.x + "," + myPlayer.y
					+ "," + anInt1014 + "," + anInt1015 + "," + anInt1069 + ","
					+ anInt1070 + "," + baseX + "," + baseY);
			throw new RuntimeException("eek");
		}
	}

	public void processDrawing() {
		if (rsAlreadyLoaded || loadingError || genericLoadingError) {
			showErrorScreen();
			return;
		}
		anInt1061++;
		if (!loggedIn)
			drawLoginScreen(false);
		else {
			drawGameScreen();
		}
		anInt1213 = 0;
	}

	private boolean isFriendOrSelf(String s) {
		if (s == null)
			return false;
		for (int i = 0; i < friendsCount; i++)
			if (s.equalsIgnoreCase(friendsList[i]))
				return true;
		return s.equalsIgnoreCase(myPlayer.name);
	}

	private static String combatDiffColor(int i, int j) {
		int k = i - j;
		if (k < -9)
			return "@red@";
		if (k < -6)
			return "@or3@";
		if (k < -3)
			return "@or2@";
		if (k < 0)
			return "@or1@";
		if (k > 9)
			return "@gre@";
		if (k > 6)
			return "@gr3@";
		if (k > 3)
			return "@gr2@";
		if (k > 0)
			return "@gr1@";
		else
			return "@yel@";
	}

	private void setWaveVolume(int i) {
		signlink.wavevol = i;
	}

	private boolean drawPane = false;

	private void draw3dScreen() {
		method498();
		if (showChat)
			drawSplitPrivateChat();
		if (crossType == 1) {
			crosses[crossIndex / 100]
					.drawSprite(crossX - 8 - 4, crossY - 8 - 4);
			anInt1142++;
			if (anInt1142 > 67) {
				anInt1142 = 0;
				stream.createFrame(78);
			}
		}
		if (crossType == 2)
			crosses[4 + crossIndex / 100].drawSprite(crossX - 8 - 4,
					crossY - 8 - 4);
		if (anInt1018 != -1) {
			method119(anInt945, anInt1018);
			drawInterface(0, clientSize == 0 ? 0 : (clientWidth / 2) - 256,
					RSInterface.interfaceCache[anInt1018], clientSize == 0 ? 0
							: (clientHeight / 2) - 167);
		}
		if (anInt1018 == 21119 || anInt1018 == 21100) {
			method119(anInt945, anInt1018);
			drawInterface(0, 0, RSInterface.interfaceCache[anInt1018], 0);
		}
		if (openInterfaceID != -1) {
			method119(anInt945, openInterfaceID);
			drawInterface(0, clientSize == 0 ? 0 : (clientWidth / 2) - 256,
					RSInterface.interfaceCache[openInterfaceID],
					clientSize == 0 ? 0 : (clientHeight / 2) - 167);
		}
		method70();
		drawGrandExchange();
		if (!menuOpen) {
			processRightClick();
			drawTooltip();
		} else if (menuScreenArea == 0)
			drawMenu();
		if (anInt1055 == 1) {
			multiOverlay.drawSprite(clientSize == 0 ? 472 : clientWidth - 230,
					clientSize == 0 ? 296 : clientHeight - 100);
		}
		if (fpsOn) {
			char c = '\u01FB';
			int k = 20;
			int i1 = 0xffff00;
			if (super.fps < 15)
				i1 = 0xff0000;
			aTextDrawingArea_1271.method380("Fps:" + super.fps, c, i1, k);
			k += 15;
			Runtime runtime = Runtime.getRuntime();
			int j1 = (int) ((runtime.totalMemory() - runtime.freeMemory()) / 1024L);
			i1 = 0xffff00;
			if (j1 > 0x2000000 && lowMem)
				i1 = 0xff0000;
			aTextDrawingArea_1271.method380("Mem:" + j1 + "k", c, 0xffff00, k);
			k += 15;
			aTextDrawingArea_1271.method385(0xffff00, "Mouse X: "
					+ super.mouseX + " , Mouse Y: " + super.mouseY, 314, 5);
		}
		int x = baseX + (myPlayer.x - 6 >> 7);
		int y = baseY + (myPlayer.y - 6 >> 7);
		if (clientData) {
			int minus = 45;
			if (super.fps < 15) {

			}
			aTextDrawingArea_1271.method385(0xffff00, "Fps: " + super.fps,
					285 - minus, 5);
			Runtime runtime = Runtime.getRuntime();
			int j1 = (int) ((runtime.totalMemory() - runtime.freeMemory()) / 1024L);
			if (j1 > 0x2000000 && lowMem) {
			}
			aTextDrawingArea_1271.method385(0xffff00, "Mem: " + j1 + "k",
					299 - minus, 5);
			aTextDrawingArea_1271.method385(0xffff00, "Mouse X: "
					+ super.mouseX + " , Mouse Y: " + super.mouseY,
					314 - minus, 5);
			aTextDrawingArea_1271.method385(0xffff00,
					"Coords: " + x + ", " + y, 329 - minus, 5);
			aTextDrawingArea_1271.method385(0xffff00, "Client resolution: "
					+ clientWidth + "x" + clientHeight, 344 - minus, 5);
			aTextDrawingArea_1271.method385(0xffff00, "Object Maps: "
					+ objectMaps + ";", 359 - minus, 5);
			aTextDrawingArea_1271.method385(0xffff00, "Floor Maps: "
					+ floorMaps + ";", 374 - minus, 5);

		}
		if (anInt1104 != 0) {
			int j = anInt1104 / 50;
			int l = j / 60;
			j %= 60;
			if (j < 10)
				aTextDrawingArea_1271.method385(0xffff00, "System update in: "
						+ l + ":0" + j, 329, 4);
			else
				aTextDrawingArea_1271.method385(0xffff00, "System update in: "
						+ l + ":" + j, 329, 4);
			anInt849++;
			if (anInt849 > 75) {
				anInt849 = 0;
				stream.createFrame(148);
			}
		}
	}

	private void addIgnore(long l) {
		try {
			if (l == 0L)
				return;
			if (ignoreCount >= 100) {
				pushMessage("Your ignore list is full. Max of 100 hit", 0, "");
				return;
			}
			String s = TextClass.fixName(TextClass.nameForLong(l));
			for (int j = 0; j < ignoreCount; j++)
				if (ignoreListAsLongs[j] == l) {
					pushMessage(s + " is already on your ignore list", 0, "");
					return;
				}
			for (int k = 0; k < friendsCount; k++)
				if (friendsListAsLongs[k] == l) {
					pushMessage("Please remove " + s
							+ " from your friend list first", 0, "");
					return;
				}

			ignoreListAsLongs[ignoreCount++] = l;
			needDrawTabArea = true;
			stream.createFrame(133);
			stream.writeQWord(l);
			return;
		} catch (RuntimeException runtimeexception) {
			signlink.reporterror("45688, " + l + ", " + 4 + ", "
					+ runtimeexception.toString());
		}
		throw new RuntimeException();
	}

	private void method114() {
		for (int i = -1; i < playerCount; i++) {
			int j;
			if (i == -1)
				j = myPlayerIndex;
			else
				j = playerIndices[i];
			Player player = playerArray[j];
			if (player != null)
				method96(player);
		}

	}

	private void method115() {
		if (loadingStage == 2) {
			for (Class30_Sub1 class30_sub1 = (Class30_Sub1) aClass19_1179
					.getFront(); class30_sub1 != null; class30_sub1 = (Class30_Sub1) aClass19_1179
					.reverseGetNext()) {
				if (class30_sub1.anInt1294 > 0)
					class30_sub1.anInt1294--;
				if (class30_sub1.anInt1294 == 0) {
					if (class30_sub1.anInt1299 < 0
							|| ObjectManager.method178(class30_sub1.anInt1299,
									class30_sub1.anInt1301)) {
						method142(class30_sub1.anInt1298,
								class30_sub1.anInt1295, class30_sub1.anInt1300,
								class30_sub1.anInt1301, class30_sub1.anInt1297,
								class30_sub1.anInt1296, class30_sub1.anInt1299);
						class30_sub1.unlink();
					}
				} else {
					if (class30_sub1.anInt1302 > 0)
						class30_sub1.anInt1302--;
					if (class30_sub1.anInt1302 == 0
							&& class30_sub1.anInt1297 >= 1
							&& class30_sub1.anInt1298 >= 1
							&& class30_sub1.anInt1297 <= 102
							&& class30_sub1.anInt1298 <= 102
							&& (class30_sub1.anInt1291 < 0 || ObjectManager
									.method178(class30_sub1.anInt1291,
											class30_sub1.anInt1293))) {
						method142(class30_sub1.anInt1298,
								class30_sub1.anInt1295, class30_sub1.anInt1292,
								class30_sub1.anInt1293, class30_sub1.anInt1297,
								class30_sub1.anInt1296, class30_sub1.anInt1291);
						class30_sub1.anInt1302 = -1;
						if (class30_sub1.anInt1291 == class30_sub1.anInt1299
								&& class30_sub1.anInt1299 == -1)
							class30_sub1.unlink();
						else if (class30_sub1.anInt1291 == class30_sub1.anInt1299
								&& class30_sub1.anInt1292 == class30_sub1.anInt1300
								&& class30_sub1.anInt1293 == class30_sub1.anInt1301)
							class30_sub1.unlink();
					}
				}
			}

		}
	}

	private void determineMenuSize() {
		int i = boldFont.getTextWidth("Choose Option");
		for (int j = 0; j < menuActionRow; j++) {
			int k = boldFont.getTextWidth(menuActionName[j]);
			if (k > i)
				i = k;
		}
		i += 8;
		int l = 15 * menuActionRow + 21;
		if (clientSize == 0) {
			if (super.saveClickX > 4 && super.saveClickY > 4
					&& super.saveClickX < 516 && super.saveClickY < 338) {
				int i1 = super.saveClickX - 4 - i / 2;
				if (i1 + i > 512)
					i1 = 512 - i;
				if (i1 < 0)
					i1 = 0;
				int l1 = super.saveClickY - 4;
				if (l1 + l > 334)
					l1 = 334 - l;
				if (l1 < 0)
					l1 = 0;
				menuOpen = true;
				menuScreenArea = 0;
				menuOffsetX = i1;
				menuOffsetY = l1;
				menuWidth = i;
				menuHeight = 15 * menuActionRow + 22;
			}
			if (super.saveClickX > 519 && super.saveClickY > 168
					&& super.saveClickX < 765 && super.saveClickY < 503) {
				int j1 = super.saveClickX - 519 - i / 2;
				if (j1 < 0)
					j1 = 0;
				else if (j1 + i > 245)
					j1 = 245 - i;
				int i2 = super.saveClickY - 168;
				if (i2 < 0)
					i2 = 0;
				else if (i2 + l > 333)
					i2 = 333 - l;
				menuOpen = true;
				menuScreenArea = 1;
				menuOffsetX = j1;
				menuOffsetY = i2;
				menuWidth = i;
				menuHeight = 15 * menuActionRow + 22;
			}
			if (super.saveClickX > 0 && super.saveClickY > 338
					&& super.saveClickX < 516 && super.saveClickY < 503) {
				int k1 = super.saveClickX - 0 - i / 2;
				if (k1 < 0)
					k1 = 0;
				else if (k1 + i > 516)
					k1 = 516 - i;
				int j2 = super.saveClickY - 338;
				if (j2 < 0)
					j2 = 0;
				else if (j2 + l > 165)
					j2 = 165 - l;
				menuOpen = true;
				menuScreenArea = 2;
				menuOffsetX = k1;
				menuOffsetY = j2;
				menuWidth = i;
				menuHeight = 15 * menuActionRow + 22;
			}
			// if(super.saveClickX > 0 && super.saveClickY > 338 &&
			// super.saveClickX < 516 && super.saveClickY < 503) {
			if (super.saveClickX > 519 && super.saveClickY > 0
					&& super.saveClickX < 765 && super.saveClickY < 168) {
				int j1 = super.saveClickX - 519 - i / 2;
				if (j1 < 0)
					j1 = 0;
				else if (j1 + i > 245)
					j1 = 245 - i;
				int i2 = super.saveClickY - 0;
				if (i2 < 0)
					i2 = 0;
				else if (i2 + l > 168)
					i2 = 168 - l;
				menuOpen = true;
				menuScreenArea = 3;
				menuOffsetX = j1;
				menuOffsetY = i2;
				menuWidth = i;
				menuHeight = 15 * menuActionRow + 22;
			}
		} else {
			if (super.saveClickX > 0 && super.saveClickY > 0
					&& super.saveClickX < clientWidth
					&& super.saveClickY < clientHeight) {
				int i1 = super.saveClickX - 0 - i / 2;
				if (i1 + i > clientWidth)
					i1 = clientWidth - i;
				if (i1 < 0)
					i1 = 0;
				int l1 = super.saveClickY - 0;
				if (l1 + l > clientHeight)
					l1 = clientHeight - l;
				if (l1 < 0)
					l1 = 0;
				menuOpen = true;
				menuScreenArea = 0;
				menuOffsetX = i1;
				menuOffsetY = l1;
				menuWidth = i;
				menuHeight = 15 * menuActionRow + 22;
			}
		}
	}

	private void method117(Stream stream) {
		stream.initBitAccess();
		int j = stream.readBits(1);
		if (j == 0)
			return;
		int k = stream.readBits(2);
		if (k == 0) {
			anIntArray894[anInt893++] = myPlayerIndex;
			return;
		}
		if (k == 1) {
			int l = stream.readBits(3);
			myPlayer.moveInDir(false, l);
			int k1 = stream.readBits(1);
			if (k1 == 1)
				anIntArray894[anInt893++] = myPlayerIndex;
			return;
		}
		if (k == 2) {
			int i1 = stream.readBits(3);
			myPlayer.moveInDir(true, i1);
			int l1 = stream.readBits(3);
			myPlayer.moveInDir(true, l1);
			int j2 = stream.readBits(1);
			if (j2 == 1)
				anIntArray894[anInt893++] = myPlayerIndex;
			return;
		}
		if (k == 3) {
			plane = stream.readBits(2);
			int j1 = stream.readBits(1);
			int i2 = stream.readBits(1);
			if (i2 == 1)
				anIntArray894[anInt893++] = myPlayerIndex;
			int k2 = stream.readBits(7);
			int l2 = stream.readBits(7);
			myPlayer.setPos(l2, k2, j1 == 1);
		}
	}

	private void nullLoader() {
		aBoolean831 = false;
		while (drawingFlames) {
			aBoolean831 = false;
			try {
				Thread.sleep(50L);
			} catch (Exception _ex) {
			}
		}
		aBackgroundArray1152s = null;
		anIntArray851 = null;
		anIntArray852 = null;
		anIntArray853 = null;
		anIntArray1190 = null;
		anIntArray1191 = null;
		aClass30_Sub2_Sub1_Sub1_1201 = null;
		aClass30_Sub2_Sub1_Sub1_1202 = null;
	}

	private boolean method119(int i, int j) {
		boolean flag1 = false;
		RSInterface class9 = RSInterface.interfaceCache[j];
		for (int k = 0; k < class9.children.length; k++) {
			if (class9.children[k] == -1)
				break;
			RSInterface class9_1 = RSInterface.interfaceCache[class9.children[k]];
			if (class9_1.type == 1)
				flag1 |= method119(i, class9_1.id);
			if (class9_1.type == 6
					&& (class9_1.anInt257 != -1 || class9_1.anInt258 != -1)) {
				boolean flag2 = interfaceIsSelected(class9_1);
				int l;
				if (flag2)
					l = class9_1.anInt258;
				else
					l = class9_1.anInt257;
				if (l != -1) {
					Animation animation = Animation.anims[l];
					for (class9_1.anInt208 += i; class9_1.anInt208 > animation
							.method258(class9_1.anInt246);) {
						class9_1.anInt208 -= animation
								.method258(class9_1.anInt246) + 1;
						class9_1.anInt246++;
						if (class9_1.anInt246 >= animation.anInt352) {
							class9_1.anInt246 -= animation.anInt356;
							if (class9_1.anInt246 < 0
									|| class9_1.anInt246 >= animation.anInt352)
								class9_1.anInt246 = 0;
						}
						flag1 = true;
					}

				}
			}
		}

		return flag1;
	}

	private int method120() {
		int j = 3;
		if (yCameraCurve < 310) {
			int k = xCameraPos >> 7;
		int l = yCameraPos >> 7;
							int i1 = myPlayer.x >> 7;
							int j1 = myPlayer.y >> 7;
							if ((byteGroundArray[plane][k][l] & 4) != 0)
								j = plane;
							int k1;
							if (i1 > k)
								k1 = i1 - k;
							else
								k1 = k - i1;
							int l1;
							if (j1 > l)
								l1 = j1 - l;
							else
								l1 = l - j1;
							if (k1 > l1) {
								int i2 = (l1 * 0x10000) / k1;
								int k2 = 32768;
								while (k != i1) {
									if (k < i1)
										k++;
									else if (k > i1)
										k--;
									if ((byteGroundArray[plane][k][l] & 4) != 0)
										j = plane;
									k2 += i2;
									if (k2 >= 0x10000) {
										k2 -= 0x10000;
										if (l < j1)
											l++;
										else if (l > j1)
											l--;
										if ((byteGroundArray[plane][k][l] & 4) != 0)
											j = plane;
									}
								}
							} else {
								int j2 = (k1 * 0x10000) / l1;
								int l2 = 32768;
								while (l != j1) {
									if (l < j1)
										l++;
									else if (l > j1)
										l--;
									if ((byteGroundArray[plane][k][l] & 4) != 0)
										j = plane;
									l2 += j2;
									if (l2 >= 0x10000) {
										l2 -= 0x10000;
										if (k < i1)
											k++;
										else if (k > i1)
											k--;
										if ((byteGroundArray[plane][k][l] & 4) != 0)
											j = plane;
									}
								}
							}
		}
		if ((byteGroundArray[plane][myPlayer.x >> 7][myPlayer.y >> 7] & 4) != 0)
			j = plane;
		return j;
	}

	private int method121() {
		int j = method42(plane, yCameraPos, xCameraPos);
		if (j - zCameraPos < 800
				&& (byteGroundArray[plane][xCameraPos >> 7][yCameraPos >> 7] & 4) != 0)
			return plane;
		else
			return 3;
	}

	private void delIgnore(long l) {
		try {
			if (l == 0L)
				return;
			for (int j = 0; j < ignoreCount; j++)
				if (ignoreListAsLongs[j] == l) {
					ignoreCount--;
					needDrawTabArea = true;
					System.arraycopy(ignoreListAsLongs, j + 1,
							ignoreListAsLongs, j, ignoreCount - j);

					stream.createFrame(74);
					stream.writeQWord(l);
					return;
				}

			return;
		} catch (RuntimeException runtimeexception) {
			signlink.reporterror("47229, " + 3 + ", " + l + ", "
					+ runtimeexception.toString());
		}
		throw new RuntimeException();
	}

	private void chatJoin(long l) {
		try {
			if (l == 0L)
				return;
			stream.createFrame(60);
			stream.writeQWord(l);
			return;
		} catch (RuntimeException runtimeexception) {
			signlink.reporterror("47229, " + 3 + ", " + l + ", "
					+ runtimeexception.toString());
		}
		throw new RuntimeException();

	}

	public String getParameter(String s) {
		if (signlink.mainapp != null)
			return signlink.mainapp.getParameter(s);
		else
			return super.getParameter(s);
	}

	private int extractInterfaceValues(RSInterface class9, int j) {
		if (class9.valueIndexArray == null
				|| j >= class9.valueIndexArray.length)
			return -2;
		try {
			int ai[] = class9.valueIndexArray[j];
			int k = 0;
			int l = 0;
			int i1 = 0;
			do {
				int j1 = ai[l++];
				int k1 = 0;
				byte byte0 = 0;
				if (j1 == 0)
					return k;
				if (j1 == 1)
					k1 = currentStats[ai[l++]];
				if (j1 == 2)
					k1 = maxStats[ai[l++]];
				if (j1 == 3)
					k1 = currentExp[ai[l++]];
				if (j1 == 4) {
					RSInterface class9_1 = RSInterface.interfaceCache[ai[l++]];
					int k2 = ai[l++];
					if (k2 >= 0 && k2 < ItemDef.totalItems
							&& (!ItemDef.forID(k2).membersObject || isMembers)) {
						for (int j3 = 0; j3 < class9_1.inv.length; j3++)
							if (class9_1.inv[j3] == k2 + 1)
								k1 += class9_1.invStackSizes[j3];

					}
				}
				if (j1 == 5)
					k1 = variousSettings[ai[l++]];
				if (j1 == 6)
					k1 = anIntArray1019[maxStats[ai[l++]] - 1];
				if (j1 == 7)
					k1 = (variousSettings[ai[l++]] * 100) / 46875;
				if (j1 == 8)
					k1 = myPlayer.combatLevel;
				if (j1 == 9) {
					for (int l1 = 0; l1 < Skills.skillsCount; l1++)
						if (Skills.skillEnabled[l1])
							k1 += maxStats[l1];

				}
				if (j1 == 10) {
					RSInterface class9_2 = RSInterface.interfaceCache[ai[l++]];
					int l2 = ai[l++] + 1;
					if (l2 >= 0 && l2 < ItemDef.totalItems
							&& (!ItemDef.forID(l2).membersObject || isMembers)) {
						for (int k3 = 0; k3 < class9_2.inv.length; k3++) {
							if (class9_2.inv[k3] != l2)
								continue;
							k1 = 0x3b9ac9ff;
							break;
						}

					}
				}
				if (j1 == 11)
					k1 = currentEnergy;
				if (j1 == 12)
					k1 = weight;
				if (j1 == 13) {
					int i2 = variousSettings[ai[l++]];
					int i3 = ai[l++];
					k1 = (i2 & 1 << i3) == 0 ? 0 : 1;
				}
				if (j1 == 14) {
					int j2 = ai[l++];
					VarBit varBit = VarBit.cache[j2];
					int l3 = varBit.anInt648;
					int i4 = varBit.anInt649;
					int j4 = varBit.anInt650;
					int k4 = anIntArray1232[j4 - i4];
					k1 = variousSettings[l3] >> i4 & k4;
				}
				if (j1 == 15)
					byte0 = 1;
				if (j1 == 16)
					byte0 = 2;
				if (j1 == 17)
					byte0 = 3;
				if (j1 == 18)
					k1 = (myPlayer.x >> 7) + baseX;
				if (j1 == 19)
					k1 = (myPlayer.y >> 7) + baseY;
				if (j1 == 20)
					k1 = ai[l++];
				if (byte0 == 0) {
					if (i1 == 0)
						k += k1;
					if (i1 == 1)
						k -= k1;
					if (i1 == 2 && k1 != 0)
						k /= k1;
					if (i1 == 3)
						k *= k1;
					i1 = 0;
				} else {
					i1 = byte0;
				}
			} while (true);
		} catch (Exception _ex) {
			return -1;
		}
	}

	public static boolean customCursor = true;

	public void drawTooltip() {
		if (menuActionRow < 2 && itemSelected == 0 && spellSelected == 0) {
			if (customCursor)
				super.setCursor(0);
			return;
		}
		String s;
		if (itemSelected == 1 && menuActionRow < 2)
			s = "Use " + selectedItemName + " with...";
		else if (spellSelected == 1 && menuActionRow < 2)
			s = spellTooltip + "...";
		else
			s = menuActionName[menuActionRow - 1];
		if (menuActionRow > 2)
			s = s + "@whi@ / " + (menuActionRow - 2) + " more options";
		chatTextDrawingArea.method390(4, 0xffffff, s, loopCycle / 1000, 15);
		detectCursor(menuActionRow - 1);
	}

	public void detectCursor(int menuAction) {
		if (customCursor) {
			boolean hasFoundCursor = false;
			for (int i2 = 0; i2 < normalCursorSentences.length; i2++) {
				if ((menuActionName[menuAction]
						.equals(normalCursorSentences[i2]))) {
					hasFoundCursor = false;
					return;
				}
			}
			for (int i1 = 0; i1 < cursorInfo.length; i1++) {
				if (menuActionName[menuAction].startsWith(cursorInfo[i1])) {
					hasFoundCursor = true;
					super.setCursor(i1);
				}
			}
			if (!hasFoundCursor)
				super.setCursor(0);
		}
	}

	public static String normalCursorSentences[] = { "Open quickchat",
		"Open Clan Setup, 18132", "Options" };

	public static String cursorInfo[] = {
		/**
		 * @Author Cody' & Notepad Extremely Edited by Notepad
		 **/
		"Walk-to", "Take", "Use", "Talk-to", "Open", "Net", "Bait", "Cage",
		"Harpoon", "Chop", "Bury", "Pray-at", "Mine", "Eat", "Drink",
		"Wield", "Wear", "Remove", "Attack", "Enter", "Exit", "Climb-up",
		"Climb-down", "Search", "Steal", "Smelt", "Clean", "Back",
		"Deposit Bank", "Inspect", "Pickpocket", "Zoom", "Toggle",
		"Settings", "Option", "Pointless", "Accept", "Decline", /* 38 */

		/* Ancients 16 */
		"Cast Ice Barrage on", "Cast Blood Barrage on",
		"Cast Shadow Barrage on", "Cast Smoke Barrage on",
		"Cast Ice Blitz on", "Cast Blood Blitz on", "Cast Shadow Blitz on",
		"Cast Smoke Blitz on", "Cast Ice Burst on", "Cast Blood Burst on",
		"Cast Shadow Burst on", "Cast Smoke Burst on", "Cast Ice Rush on",
		"Cast Blood Rush on", "Cast Shadow Rush on", "Cast Smoke Rush on",

		/* Interfaces/Some Spells 24 */
		"Link", "Split Private", "Graphics", "Audio", "House Options",
		"Pointless#2", "Click", "Information",
		"Cast High level alchemy on", "Cast Low level alchemy on", "Value",
		"Select Starter", "Craft-rune", "World Map", "Withdraw", "Slash",
		"Pull", "Starter Infomation", "Range Infomation",
		"Magic Infomation", "Melee Infomation", "Choose Range",
		"Choose Magic", "Choose Melee",

		/* Miasmic ^.^ 1 */
		"Cast Miasmic Barrage On",

		/* Normal Magics 47 */
		"Cast Wind strike on", "Cast Confuse on", "Cast Water strike on",
		"Cast Enchant Lvl-1 Jewelry on", "Cast Earth strike on",
		"Cast Weaken on", "Cast Fire strike on", "Cast Wind bolt on",
		"Cast Curse on", "Cast Bind on", "Cast Water bolt on",
		"Cast Enchant Lvl-2 Jewelry on", "Cast Earth bolt on",
		"Cast Telekinetic grab on", "Cast Fire bolt on",
		"Cast Crumble undead on", "Cast Wind blast on",
		"Cast Superheat Item on", "Cast Water blast on",
		"Cast Enchant Lvl-3 Jewelry on", "Cast Iban blast on",
		"Cast Snare on", "Cast Magic Dart on", "Cast Earth blast on",
		"Cast Charge water orb on", "Cast Enchant Lvl-4 Jewelry on",
		"Cast Fire blast on", "Cast Charge earth orb on",
		"Cast Saradomin strike on", "Cast Claws of Guthix on",
		"Cast Flames of Zamorak on", "Cast Wind wave on",
		"Cast Charge fire orb on", "Cast Water wave on",
		"Cast Charge earth orb on", "Cast Vulnerability on",
		"Cast Enchant Lvl-5 Jewelry on", "Cast Earth wave on",
		"Cast Enfeeble on", "Cast Teleother Lumbridge on",
		"Cast Fire wave on", "Cast Entangle on", "Cast Stun on",
		"Cast Teleother Falador on", "Cast Tele Block on",
		"Cast Enchant Lvl-6 Jewelry on", "Cast Teleother Camelot on",

		/* Some Skills 2 */
		"Use Tinderbox with", "Activate",

		/* Magic Altars 1 */
		"pray",

		/* Lunar 8 */
		"Cast Vengeance Other on", "Cast Monster Examine on",
		"Cast NPC Contant on", "Cast Cure Other on", "Cast Cure Plant on",
		"Cast Stat Spy on", "Cast Fertile Soil on",
		"Cast Energy Transfer on",

		/* Dungeoneering */
	"Teleport" };

	/* Cursors End (: */
	private void npcScreenPos(Entity entity, int i) {
		calcEntityScreenPos(entity.x, i, entity.y);
	}

	private void calcEntityScreenPos(int i, int j, int l) {
		if (i < 128 || l < 128 || i > 13056 || l > 13056) {
			spriteDrawX = -1;
			spriteDrawY = -1;
			return;
		}
		int i1 = method42(plane, l, i) - j;
		i -= xCameraPos;
		i1 -= zCameraPos;
		l -= yCameraPos;
		int j1 = Model.modelIntArray1[yCameraCurve];
		int k1 = Model.modelIntArray2[yCameraCurve];
		int l1 = Model.modelIntArray1[xCameraCurve];
		int i2 = Model.modelIntArray2[xCameraCurve];
		int j2 = l * l1 + i * i2 >> 16;
			l = l * i2 - i * l1 >> 16;
		i = j2;
		j2 = i1 * k1 - l * j1 >> 16;
		l = i1 * j1 + l * k1 >> 16;
		i1 = j2;
		if (l >= 50) {
			spriteDrawX = Texture.centerX + (i << 9) / l;
			spriteDrawY = Texture.centerY + (i1 << 9) / l;
		} else {
			spriteDrawX = -1;
			spriteDrawY = -1;
		}
	}

	private void buildSplitPrivateChatMenu() {
		if (splitPrivateChat == 0)
			return;
		int i = 0;
		if (anInt1104 != 0)
			i = 1;
		for (int j = 0; j < 100; j++)
			if (chatMessages[j] != null) {
				int k = chatTypes[j];
				String name = chatNames[j];
				if (name != null && name.indexOf("@") == 0) {
					// name = name.substring(5);
				}
				if ((k == 3 || k == 7)
						&& (k == 7 || privateChatMode == 0 || privateChatMode == 1
						&& isFriendOrSelf(name))) {
					int l = (clientHeight - 174) - i * 13;
					if (super.mouseX > (clientSize == 0 ? 4 : 0)
							&& super.mouseY - (clientSize == 0 ? 4 : 0) > l - 10
							&& super.mouseY - (clientSize == 0 ? 4 : 0) <= l + 3) {
						int i1 = normalFont.getTextWidth("From:  " + name
								+ chatMessages[j]) + 25;
						if (i1 > 450)
							i1 = 450;
						if (super.mouseX < (clientSize == 0 ? 4 : 0) + i1) {
							if (myRights >= 1) {
								menuActionName[menuActionRow] = "Report abuse @whi@"
										+ name;
								menuActionID[menuActionRow] = 2606;
								menuActionRow++;
							}
							if (!isFriendOrSelf(name)) {
								menuActionName[menuActionRow] = "Add ignore @whi@"
										+ name;
								menuActionID[menuActionRow] = 2042;
								menuActionRow++;
								menuActionName[menuActionRow] = "Add friend @whi@"
										+ name;
								menuActionID[menuActionRow] = 2337;
								menuActionRow++;
							}
							if (isFriendOrSelf(name)) {
								menuActionName[menuActionRow] = "Message @whi@"
										+ name;
								menuActionID[menuActionRow] = 2639;
								menuActionRow++;
							}
						}
					}
					if (++i >= 5)
						return;
				}
				if ((k == 5 || k == 6) && privateChatMode < 2 && ++i >= 5)
					return;
			}

	}

	private void method130(int j, int k, int l, int i1, int j1, int k1, int l1,
			int i2, int j2) {
		Class30_Sub1 class30_sub1 = null;
		for (Class30_Sub1 class30_sub1_1 = (Class30_Sub1) aClass19_1179
				.getFront(); class30_sub1_1 != null; class30_sub1_1 = (Class30_Sub1) aClass19_1179
				.reverseGetNext()) {
			if (class30_sub1_1.anInt1295 != l1
					|| class30_sub1_1.anInt1297 != i2
					|| class30_sub1_1.anInt1298 != j1
					|| class30_sub1_1.anInt1296 != i1)
				continue;
			class30_sub1 = class30_sub1_1;
			break;
		}

		if (class30_sub1 == null) {
			class30_sub1 = new Class30_Sub1();
			class30_sub1.anInt1295 = l1;
			class30_sub1.anInt1296 = i1;
			class30_sub1.anInt1297 = i2;
			class30_sub1.anInt1298 = j1;
			method89(class30_sub1);
			aClass19_1179.insertBack(class30_sub1);
		}
		class30_sub1.anInt1291 = k;
		class30_sub1.anInt1293 = k1;
		class30_sub1.anInt1292 = l;
		class30_sub1.anInt1302 = j2;
		class30_sub1.anInt1294 = j;
	}

	private boolean interfaceIsSelected(RSInterface class9) {
		if (class9.valueCompareType == null)
			return false;
		for (int i = 0; i < class9.valueCompareType.length; i++) {
			int j = extractInterfaceValues(class9, i);
			int k = class9.requiredValues[i];
			if (class9.valueCompareType[i] == 2) {
				if (j >= k)
					return false;
			} else if (class9.valueCompareType[i] == 3) {
				if (j <= k)
					return false;
			} else if (class9.valueCompareType[i] == 4) {
				if (j == k)
					return false;
			} else if (j != k)
				return false;
		}

		return true;
	}

	private void connectToUpdateServer() {
		int j = 5;
		expectedCRCs[8] = 0;
		int k = 0;
		while (expectedCRCs[8] == 0) {
			String s = "Unknown problem";
			drawLoadingText(20, "Connecting to web server");
			try {
				DataInputStream datainputstream = openJagGrabInputStream("crc"
						+ (int) (Math.random() * 99999999D) + "-" + 317);
				Stream stream = new Stream(new byte[40]);
				datainputstream.readFully(stream.buffer, 0, 40);
				datainputstream.close();
				for (int i1 = 0; i1 < 9; i1++)
					expectedCRCs[i1] = stream.readInt();

				int j1 = stream.readInt();
				int k1 = 1234;
				for (int l1 = 0; l1 < 9; l1++)
					k1 = (k1 << 1) + expectedCRCs[l1];

				if (j1 != k1) {
					s = "checksum problem";
					expectedCRCs[8] = 0;
				}
			} catch (EOFException _ex) {
				s = "EOF problem";
				expectedCRCs[8] = 0;
			} catch (IOException _ex) {
				s = "connection problem";
				expectedCRCs[8] = 0;
			} catch (Exception _ex) {
				s = "logic problem";
				expectedCRCs[8] = 0;
				if (!signlink.reporterror)
					return;
			}
			if (expectedCRCs[8] == 0) {
				k++;
				for (int l = j; l > 0; l--) {
					if (k >= 10) {
						drawLoadingText(10, "Game updated - please reload page");
						l = 10;
					} else {
						drawLoadingText(10, s + " - Will retry in " + l
								+ " secs.");
					}
					try {
						Thread.sleep(1000L);
					} catch (Exception _ex) {
					}
				}

				j *= 2;
				if (j > 60)
					j = 60;
				aBoolean872 = !aBoolean872;
			}
		}
	}

	private DataInputStream openJagGrabInputStream(String s) throws IOException {
		// if(!aBoolean872)
		// if(signlink.mainapp != null)
		// return signlink.openurl(s);
		// else
		// return new DataInputStream((new URL(getCodeBase(), s)).openStream());
		if (aSocket832 != null) {
			try {
				aSocket832.close();
			} catch (Exception _ex) {
			}
			aSocket832 = null;
		}
		aSocket832 = openSocket(43595);
		aSocket832.setSoTimeout(10000);
		java.io.InputStream inputstream = aSocket832.getInputStream();
		OutputStream outputstream = aSocket832.getOutputStream();
		outputstream.write(("JAGGRAB /" + s + "\n\n").getBytes());
		return new DataInputStream(inputstream);
	}

	private void method134(Stream stream) {
		int j = stream.readBits(8);
		if (j < playerCount) {
			for (int k = j; k < playerCount; k++)
				anIntArray840[anInt839++] = playerIndices[k];

		}
		if (j > playerCount) {
			signlink.reporterror(myUsername + " Too many players");
			throw new RuntimeException("eek");
		}
		playerCount = 0;
		for (int l = 0; l < j; l++) {
			int i1 = playerIndices[l];
			Player player = playerArray[i1];
			int j1 = stream.readBits(1);
			if (j1 == 0) {
				playerIndices[playerCount++] = i1;
				player.anInt1537 = loopCycle;
			} else {
				int k1 = stream.readBits(2);
				if (k1 == 0) {
					playerIndices[playerCount++] = i1;
					player.anInt1537 = loopCycle;
					anIntArray894[anInt893++] = i1;
				} else if (k1 == 1) {
					playerIndices[playerCount++] = i1;
					player.anInt1537 = loopCycle;
					int l1 = stream.readBits(3);
					player.moveInDir(false, l1);
					int j2 = stream.readBits(1);
					if (j2 == 1)
						anIntArray894[anInt893++] = i1;
				} else if (k1 == 2) {
					playerIndices[playerCount++] = i1;
					player.anInt1537 = loopCycle;
					int i2 = stream.readBits(3);
					player.moveInDir(true, i2);
					int k2 = stream.readBits(3);
					player.moveInDir(true, k2);
					int l2 = stream.readBits(1);
					if (l2 == 1)
						anIntArray894[anInt893++] = i1;
				} else if (k1 == 3)
					anIntArray840[anInt839++] = i1;
			}
		}
	}

	public int terrainRegionX;
	public int terrainRegionY;
	public int[] titleScreenOffsets = null;
	public int titleWidth = -1;
	public int titleHeight = -1;
	public ScriptManager scriptManager;

	public void generateWorld(int x, int y) {
		terrainRegionX = x;
		terrainRegionY = y;
		aBoolean1159 = false;
		if (anInt1069 == x && anInt1070 == y && loadingStage == 2) {
			return;
		}
		anInt1069 = x;
		anInt1070 = y;
		baseX = (anInt1069 - 6) * 8;
		baseY = (anInt1070 - 6) * 8;
		aBoolean1141 = (anInt1069 / 8 == 48 || anInt1069 / 8 == 49)
				&& anInt1070 / 8 == 48;
		if (anInt1069 / 8 == 48 && anInt1070 / 8 == 148)
			aBoolean1141 = true;
		loadingStage = 1;
		aLong824 = System.currentTimeMillis();
		int k16 = 0;
		for (int i21 = (anInt1069 - 6) / 8; i21 <= (anInt1069 + 6) / 8; i21++) {
			for (int k23 = (anInt1070 - 6) / 8; k23 <= (anInt1070 + 6) / 8; k23++)
				k16++;
		}
		aByteArrayArray1183 = new byte[k16][];
		aByteArrayArray1247 = new byte[k16][];
		anIntArray1234 = new int[k16];
		anIntArray1235 = new int[k16];
		anIntArray1236 = new int[k16];
		k16 = 0;
		for (int l23 = (anInt1069 - 6) / 8; l23 <= (anInt1069 + 6) / 8; l23++) {
			for (int j26 = (anInt1070 - 6) / 8; j26 <= (anInt1070 + 6) / 8; j26++) {
				anIntArray1234[k16] = (l23 << 8) + j26;
				if (aBoolean1141
						&& (j26 == 49 || j26 == 149 || j26 == 147 || l23 == 50 || l23 == 49
						&& j26 == 47)) {
					anIntArray1235[k16] = -1;
					anIntArray1236[k16] = -1;
					k16++;
				} else {
					int k28 = anIntArray1235[k16] = onDemandFetcher.getMapCount(
							0, j26, l23);
					if (k28 != -1)
						onDemandFetcher.requestFileData(3, k28);
					int j30 = anIntArray1236[k16] = onDemandFetcher.getMapCount(
							1, j26, l23);
					if (j30 != -1)
						onDemandFetcher.requestFileData(3, j30);
					k16++;
				}
			}
		}
		int i17 = baseX - anInt1036;
		int j21 = baseY - anInt1037;
		anInt1036 = baseX;
		anInt1037 = baseY;
		for (int j24 = 0; j24 < 16384; j24++) {
			NPC npc = npcArray[j24];
			if (npc != null) {
				for (int j29 = 0; j29 < 10; j29++) {
					npc.smallX[j29] -= i17;
					npc.smallY[j29] -= j21;
				}
				npc.x -= i17 * 128;
				npc.y -= j21 * 128;
			}
		}
		for (int i27 = 0; i27 < maxPlayers; i27++) {
			Player player = playerArray[i27];
			if (player != null) {
				for (int i31 = 0; i31 < 10; i31++) {
					player.smallX[i31] -= i17;
					player.smallY[i31] -= j21;
				}
				player.x -= i17 * 128;
				player.y -= j21 * 128;
			}
		}
		aBoolean1080 = true;
		byte byte1 = 0;
		byte byte2 = 104;
		byte byte3 = 1;
		if (i17 < 0) {
			byte1 = 103;
			byte2 = -1;
			byte3 = -1;
		}
		byte byte4 = 0;
		byte byte5 = 104;
		byte byte6 = 1;
		if (j21 < 0) {
			byte4 = 103;
			byte5 = -1;
			byte6 = -1;
		}
		for (int k33 = byte1; k33 != byte2; k33 += byte3) {
			for (int l33 = byte4; l33 != byte5; l33 += byte6) {
				int i34 = k33 + i17;
				int j34 = l33 + j21;
				for (int k34 = 0; k34 < 4; k34++)
					if (i34 >= 0 && j34 >= 0 && i34 < 104 && j34 < 104)
						groundArray[k34][k33][l33] = groundArray[k34][i34][j34];
					else
						groundArray[k34][k33][l33] = null;
			}
		}
		for (Class30_Sub1 class30_sub1_1 = (Class30_Sub1) aClass19_1179
				.getFront(); class30_sub1_1 != null; class30_sub1_1 = (Class30_Sub1) aClass19_1179
				.reverseGetNext()) {
			class30_sub1_1.anInt1297 -= i17;
			class30_sub1_1.anInt1298 -= j21;
			if (class30_sub1_1.anInt1297 < 0 || class30_sub1_1.anInt1298 < 0
					|| class30_sub1_1.anInt1297 >= 104
					|| class30_sub1_1.anInt1298 >= 104)
				class30_sub1_1.unlink();
		}
		if (destX != 0) {
			destX -= i17;
			destY -= j21;
		}
		aBoolean1160 = false;
	}

	public void resetWorld(int stage) {
		if (stage == 0) {
			currentSound = 0;
			cameraOffsetX = (int) (Math.random() * 100D) - 50;
			cameraOffsetY = (int) (Math.random() * 110D) - 55;
			viewRotationOffset = (int) (Math.random() * 80D) - 40;
			minimapRotation = (int) (Math.random() * 120D) - 60;
			minimapZoom = (int) (Math.random() * 30D) - 20;
			viewRotation = (int) (Math.random() * 20D) - 10 & 0x7ff;
			anInt1021 = 0;
			loadingStage = 1;
		} else if (stage == 1) {
			aBoolean1080 = false;
		}
	}

	void loginScreenBG(boolean b) {
		xCameraPos = 6100;
		yCameraPos = 6867;
		zCameraPos = -750;
		xCameraCurve = 2040;
		yCameraCurve = 383;
		resetWorld(0);
		if (b || scriptManager == null) {
			scriptManager = new ScriptManager(this);
		} else {
			scriptManager.update();
		}
		plane = scriptManager.regionPlane;
		generateWorld(scriptManager.terrainRegionX,
				scriptManager.terrainRegionY);
		resetWorld(1);
	}

	public void drawAnimatedWorldBackground(boolean display) {
		if (display) {
			int centerX = clientWidth / 2;
			int centerY = clientHeight / 2;
			if (scriptManager == null) {
				loginScreenBG(true);
			}
			int canvasCenterX = Texture.centerX;
			int canvasCenterY = Texture.centerY;
			int canvasPixels[] = Texture.lineOffsets;
			if (titleScreenOffsets != null
					&& (titleWidth != clientWidth || titleHeight != clientHeight)) {
				titleScreenOffsets = null;
			}
			if (titleScreenOffsets == null) {
				titleWidth = clientWidth;
				titleHeight = clientHeight;
				titleScreenOffsets = Texture
						.getOffsets(titleWidth, titleHeight);
			}
			Texture.centerX = centerX;
			Texture.centerY = centerY;
			Texture.lineOffsets = titleScreenOffsets;
			if (loadingStage == 2 && ObjectManager.anInt131 != plane)
				loadingStage = 1;

			if (!loggedIn && loadingStage == 1) {
				method54();
			}
			if (!loggedIn && loadingStage == 2 && plane != anInt985) {
				anInt985 = plane;
				renderedMapScene(plane);
			}
			if (loadingStage == 2) {
				// Texture.triangles = 0;
				try {
					worldController
					.method313(xCameraPos, yCameraPos, xCameraCurve,
							zCameraPos, method121(), yCameraCurve);
					worldController.clearObj5Cache();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			if (scriptManager != null && loadingStage == 2 && plane == anInt985
					&& !loggedIn) {
				scriptManager.cycle();
			}
			Texture.centerX = canvasCenterX;
			Texture.centerY = canvasCenterY;
			Texture.lineOffsets = canvasPixels;
		}
	}

	private boolean animationDone = false;

	private void animateLoginScreen() {
		// main box
		titleBox[0].drawMovingSprite(-1, (clientHeight / 2)
				- (titleBox[0].myHeight / 2), (clientWidth / 2)
				- (titleBox[0].myWidth / 2), 0, 4, 'y', true);
		// username path
		titleBox[5].drawMovingSprite((clientWidth / 2 - 75), 0, 0,
				(clientHeight / 2 - 34), 5, 'x', true);
		// password path
		titleBox[6].drawMovingSprite((clientWidth / 2 - 75), 0, clientWidth,
				(clientHeight / 2 + 12), (int) -5.8, 'x', true);
		if (titleBox[6].movedEnough)
			animationDone = true;
	}

	private long circleDelay;
	private int circle = 0;

	private void drawLoadingMenu() {
		DrawingArea474.drawAlphaFilledPixels(0, 0, getClientWidth(),
				getClientHeight(), 0, 200);
		if (System.currentTimeMillis() - circleDelay > 157) {
			circle += (circle == 7) ? -7 : 1;
			circleDelay = System.currentTimeMillis();
		}
		loadCircle[circle].drawAdvancedSprite((clientWidth / 2)
				- (loadCircle[circle].myWidth / 2), (clientHeight / 2)
				- (loadCircle[circle].myHeight / 2));
		normalFont.method382(16777215, clientWidth / 2,
				"Connecting to the server...", clientHeight / 2 + 55, true);
	}

	private void drawWarningMenu() {
		DrawingArea474.drawAlphaFilledPixels(0, 0, getClientWidth(),
				getClientHeight(), 0, 200);
		// warning sprite
		titleBox[7].drawAdvancedSprite((clientWidth / 2)
				- (titleBox[7].myWidth / 2), (clientHeight / 2)
				- (titleBox[7].myHeight / 2));
		titleBox[8].drawAdvancedSprite((clientWidth / 2)
				- (titleBox[8].myWidth / 2), (clientHeight / 2)
				- (titleBox[8].myHeight / 2) + 55);
		normalFont.method382(16777215, clientWidth / 2, loginMessage1,
				clientHeight / 2 + 15, true);
		normalFont.method382(16777215, clientWidth / 2, loginMessage2,
				clientHeight / 2 + 35, true);
		if (mouseInRegion(clientWidth / 2 - 84, clientHeight / 2 + 29,
				clientWidth / 2 + 82, clientHeight / 2 + 63))
			titleBox[9].drawAdvancedSprite((clientWidth / 2)
					- (titleBox[9].myWidth / 2), (clientHeight / 2)
					- (titleBox[9].myHeight / 2) + 55);
		smallText.method382(16777215, clientWidth / 2 - 5, "Back",
				clientHeight / 2 + 60, true);
	}

	private int opacity = 256;

	private void loopLayerTransparency(int xPos, int YPos, int width,
			int height, int speed) {
		if (opacity >= 0)
			opacity -= speed;
		else
			opacity = 0;
		DrawingArea474.drawAlphaFilledPixels(xPos, YPos, width, height, 0,
				opacity);
	}

	private boolean bgCheck = true;

	public RealFont[] arial = { new RealFont(this, "Arial", 0, 10, true), new RealFont(this, "Arial", 0, 12, true), new RealFont(this, "Arial", 0, 14, true) };

	public void drawLoginScreen(boolean flag) {
		resetImageProducers();
		titleScreen.initDrawingArea();
		DrawingArea474.drawFilledPixels(0, 0, getClientWidth(),
				getClientHeight(), 0x000000);
		if (!bgCheck) {
			drawAnimatedWorldBackground(true);
		} else {
			backgroundFix.drawSprite((clientWidth / 2)
					- (backgroundFix.myWidth / 2), (clientHeight / 2)
					- (backgroundFix.myHeight / 2));
		}
		//aTextDrawingArea_1271.method382(0x75a9a9, 60, mouseX + "/" + mouseY
			//	+ "      " + clientWidth + "/" + clientHeight, 30, true);
		titleBox[bgCheck ? 14 : 13].drawAdvancedSprite(5, clientHeight
				- titleBox[13].myHeight - 5);
		if (!animationDone) {
			animateLoginScreen();
			loopLayerTransparency(0, 0, getClientWidth(), getClientHeight(), 2);
		}
		char c = '\u0168';
		if (loginScreenState == 0 && animationDone) {
			int i = 100;
			aTextDrawingArea_1271.method382(0x75a9a9, c / 190,
					onDemandFetcher.statusString, i, true);
			titleBox[1].drawAdvancedSprite((clientWidth / 2)
					- (titleBox[1].myWidth / 2), (clientHeight / 2)
					- (titleBox[1].myHeight / 2));
			if ((mouseX >= (clientWidth / 2 - 75) + (titleBox[4].myWidth / 8)
					- 20)
					&& (mouseX <= (clientWidth / 2 - 75) + titleBox[4].myWidth
					- 8)
					&& (mouseY >= (clientHeight / 2) - (titleBox[4].myHeight))
					&& (mouseY <= (clientHeight / 2)
					+ (titleBox[4].myHeight / 3 - 12))) {
				titleBox[4].drawTooltippedSprite(this, (clientWidth / 2 - 75),
						(clientHeight / 2 - 34),
						new String[] { "Please type your username here." },
						true);
			} else if ((mouseX >= (clientWidth / 2 - 75)
					+ (titleBox[4].myWidth / 8) - 20)
					&& (mouseX <= (clientWidth / 2 - 75) + titleBox[4].myWidth
					- 8)
					&& (mouseY >= clientHeight / 2 - (titleBox[4].myHeight)
					+ 48) && (mouseY <= clientHeight / 2 + 49)) {
				titleBox[4].drawTooltippedSprite(this, (clientWidth / 2 - 75),
						(clientHeight / 2 + 12),
						new String[] { "Please type your password here." },
						true);
			}
			
			/*arial[0].drawStringCenter("Forgot your password?", clientWidth / 2 - 100, clientHeight / 2 + 72, 0xffffff, true);
			DrawingArea474.drawHorizontalLine(Client.clientWidth/2 - 152, Client.clientHeight/2 + 74, 102, 0xFFFFFF);
			
			arial[0].drawStringCenter("Not registered?", clientWidth / 2 +118, clientHeight / 2 + 72, 0xffffff, true);
			DrawingArea474.drawHorizontalLine(Client.clientWidth/2 +82, Client.clientHeight/2 + 74, 73, 0xFFFFFF);
			
			if ((mouseX >= clientWidth/2 + 75.5 && mouseX <= clientWidth/2 +154
					&& mouseY >= clientHeight/2 + 54 && mouseY <= clientHeight/2 + 71)) {
				setCursor(60);
			} else {
				setCursor(0);
			}*/
			
			this.aTextDrawingArea_1271.method389(false,(clientWidth / 2 - 62),16777215,
					new StringBuilder().append("").append(myUsername).append((
							(this.loginScreenCursorPos == 0 ? 1: 0) & (loopCycle % 40 < 20 ? 1 : 0)) != 0 ? "|"
									: "").toString(),(clientHeight / 2 - 8));
			this.aTextDrawingArea_1271
			.method389(
					true,
					(clientWidth / 2 - 62),
					16777215,
					new StringBuilder()
					.append("")
					.append(TextClass
							.passwordAsterisks(myPassword))
							.append(((this.loginScreenCursorPos == 1 ? 1
									: 0) & (loopCycle % 40 < 20 ? 1 : 0)) != 0 ? "|"
											: "").toString(),
											(clientHeight / 2 + 39));
			if ((mouseX >= clientWidth / 2 + 125)
					&& (mouseX <= clientWidth / 2 + 196)
					&& (mouseY >= clientHeight / 2 - 47)
					&& (mouseY <= clientHeight / 2 + 37))
				titleBox[3].drawAdvancedSprite(clientWidth / 2 + 123,
						clientHeight / 2 - 28);
		} else if (loginScreenState == 1) {
			drawLoadingMenu();
		} else if (loginScreenState == 2) {
			drawWarningMenu();
		} else if (loginScreenState == 3) {
			getRegister().drawRegisterScreen();
		}
		titleScreen.drawGraphics(0, super.graphics, 0);
	}

	public static String capitalizeFirstChar(String s) {
		try {
			if (s != "")
				return (s.substring(0, 1).toUpperCase() + s.substring(1)
						.toLowerCase()).trim();
		} catch (Exception e) {
		}
		return s;
	}

	private void drawFlames() {
	}

	public void raiseWelcomeScreen() {
		welcomeScreenRaised = true;
	}

	private void method137(Stream stream, int j) {
		if (j == 84) {
			int k = stream.readUnsignedByte();
			int j3 = anInt1268 + (k >> 4 & 7);
			int i6 = anInt1269 + (k & 7);
			int l8 = stream.readUnsignedWord();
			int k11 = stream.readUnsignedWord();
			int l13 = stream.readUnsignedWord();
			if (j3 >= 0 && i6 >= 0 && j3 < 104 && i6 < 104) {
				Deque class19_1 = groundArray[plane][j3][i6];
				if (class19_1 != null) {
					for (Item class30_sub2_sub4_sub2_3 = (Item) class19_1
							.getFront(); class30_sub2_sub4_sub2_3 != null; class30_sub2_sub4_sub2_3 = (Item) class19_1
							.reverseGetNext()) {
						if (class30_sub2_sub4_sub2_3.ID != (l8 & 0x7fff)
								|| class30_sub2_sub4_sub2_3.anInt1559 != k11)
							continue;
						class30_sub2_sub4_sub2_3.anInt1559 = l13;
						break;
					}

					spawnGroundItem(j3, i6);
				}
			}
			return;
		}
		if (j == 105) {
			int l = stream.readUnsignedByte();
			int k3 = anInt1268 + (l >> 4 & 7);
			int j6 = anInt1269 + (l & 7);
			int i9 = stream.readUnsignedWord();
			int l11 = stream.readUnsignedByte();
			int i14 = l11 >> 4 & 0xf;
		int i16 = l11 & 7;
		if (myPlayer.smallX[0] >= k3 - i14
				&& myPlayer.smallX[0] <= k3 + i14
				&& myPlayer.smallY[0] >= j6 - i14
				&& myPlayer.smallY[0] <= j6 + i14 && aBoolean848 && !lowMem
				&& currentSound < 50) {
			sound[currentSound] = i9;
			soundType[currentSound] = i16;
			soundDelay[currentSound] = Sounds.anIntArray326[i9];
			currentSound++;
		}
		}
		if (j == 215) {
			int i1 = stream.method435();
			int l3 = stream.method428();
			int k6 = anInt1268 + (l3 >> 4 & 7);
			int j9 = anInt1269 + (l3 & 7);
			int i12 = stream.method435();
			int j14 = stream.readUnsignedWord();
			if (k6 >= 0 && j9 >= 0 && k6 < 104 && j9 < 104
					&& i12 != unknownInt10) {
				Item class30_sub2_sub4_sub2_2 = new Item();
				class30_sub2_sub4_sub2_2.ID = i1;
				class30_sub2_sub4_sub2_2.anInt1559 = j14;
				if (groundArray[plane][k6][j9] == null)
					groundArray[plane][k6][j9] = new Deque();
				groundArray[plane][k6][j9].insertBack(class30_sub2_sub4_sub2_2);
				spawnGroundItem(k6, j9);
			}
			return;
		}
		if (j == 156) {
			int j1 = stream.method426();
			int i4 = anInt1268 + (j1 >> 4 & 7);
			int l6 = anInt1269 + (j1 & 7);
			int k9 = stream.readUnsignedWord();
			if (i4 >= 0 && l6 >= 0 && i4 < 104 && l6 < 104) {
				Deque class19 = groundArray[plane][i4][l6];
				if (class19 != null) {
					for (Item item = (Item) class19.getFront(); item != null; item = (Item) class19
							.reverseGetNext()) {
						if (item.ID != (k9 & 0x7fff))
							continue;
						item.unlink();
						break;
					}

					if (class19.getFront() == null)
						groundArray[plane][i4][l6] = null;
					spawnGroundItem(i4, l6);
				}
			}
			return;
		}
		if (j == 160) {
			int k1 = stream.method428();
			int j4 = anInt1268 + (k1 >> 4 & 7);
			int i7 = anInt1269 + (k1 & 7);
			int l9 = stream.method428();
			int j12 = l9 >> 2;
			int k14 = l9 & 3;
			int j16 = anIntArray1177[j12];
			int j17 = stream.method435();
			if (j4 >= 0 && i7 >= 0 && j4 < 103 && i7 < 103) {
				int j18 = intGroundArray[plane][j4][i7];
				int i19 = intGroundArray[plane][j4 + 1][i7];
				int l19 = intGroundArray[plane][j4 + 1][i7 + 1];
				int k20 = intGroundArray[plane][j4][i7 + 1];
				if (j16 == 0) {
					Object1 class10 = worldController.method296(plane, j4, i7);
					if (class10 != null) {
						int k21 = class10.uid >> 14 & 0x7fff;
			if (j12 == 2) {
				class10.aClass30_Sub2_Sub4_278 = new Animable_Sub5(
						k21, 4 + k14, 2, i19, l19, j18, k20, j17,
						false);
				class10.aClass30_Sub2_Sub4_279 = new Animable_Sub5(
						k21, k14 + 1 & 3, 2, i19, l19, j18, k20,
						j17, false);
			} else {
				class10.aClass30_Sub2_Sub4_278 = new Animable_Sub5(
						k21, k14, j12, i19, l19, j18, k20, j17,
						false);
			}
					}
				}
				if (j16 == 1) {
					Object2 class26 = worldController.method297(j4, i7, plane);
					if (class26 != null)
						class26.aClass30_Sub2_Sub4_504 = new Animable_Sub5(
								class26.uid >> 14 & 0x7fff, 0, 4, i19, l19,
								j18, k20, j17, false);
				}
				if (j16 == 2) {
					Object5 class28 = worldController.method298(j4, i7, plane);
					if (j12 == 11)
						j12 = 10;
					if (class28 != null)
						class28.aClass30_Sub2_Sub4_521 = new Animable_Sub5(
								class28.uid >> 14 & 0x7fff, k14, j12, i19, l19,
								j18, k20, j17, false);
				}
				if (j16 == 3) {
					Object3 class49 = worldController.method299(i7, j4, plane);
					if (class49 != null)
						class49.aClass30_Sub2_Sub4_814 = new Animable_Sub5(
								class49.uid >> 14 & 0x7fff, k14, 22, i19, l19,
								j18, k20, j17, false);
				}
			}
			return;
		}
		if (j == 147) {
			int l1 = stream.method428();
			int k4 = anInt1268 + (l1 >> 4 & 7);
			int j7 = anInt1269 + (l1 & 7);
			int i10 = stream.readUnsignedWord();
			byte byte0 = stream.method430();
			int l14 = stream.method434();
			byte byte1 = stream.method429();
			int k17 = stream.readUnsignedWord();
			int k18 = stream.method428();
			int j19 = k18 >> 2;
			int i20 = k18 & 3;
			int l20 = anIntArray1177[j19];
			byte byte2 = stream.readSignedByte();
			int l21 = stream.readUnsignedWord();
			byte byte3 = stream.method429();
			Player player;
			if (i10 == unknownInt10)
				player = myPlayer;
			else
				player = playerArray[i10];
			if (player != null) {
				ObjectDef class46 = ObjectDef.forID(l21);
				int i22 = intGroundArray[plane][k4][j7];
				int j22 = intGroundArray[plane][k4 + 1][j7];
				int k22 = intGroundArray[plane][k4 + 1][j7 + 1];
				int l22 = intGroundArray[plane][k4][j7 + 1];
				Model model = class46.method578(j19, i20, i22, j22, k22, l22,
						-1);
				if (model != null) {
					method130(k17 + 1, -1, 0, l20, j7, 0, plane, k4, l14 + 1);
					player.anInt1707 = l14 + loopCycle;
					player.anInt1708 = k17 + loopCycle;
					player.aModel_1714 = model;
					int i23 = class46.anInt744;
					int j23 = class46.anInt761;
					if (i20 == 1 || i20 == 3) {
						i23 = class46.anInt761;
						j23 = class46.anInt744;
					}
					player.anInt1711 = k4 * 128 + i23 * 64;
					player.anInt1713 = j7 * 128 + j23 * 64;
					player.anInt1712 = method42(plane, player.anInt1713,
							player.anInt1711);
					if (byte2 > byte0) {
						byte byte4 = byte2;
						byte2 = byte0;
						byte0 = byte4;
					}
					if (byte3 > byte1) {
						byte byte5 = byte3;
						byte3 = byte1;
						byte1 = byte5;
					}
					player.anInt1719 = k4 + byte2;
					player.anInt1721 = k4 + byte0;
					player.anInt1720 = j7 + byte3;
					player.anInt1722 = j7 + byte1;
				}
			}
		}
		if (j == 151) {
			int i2 = stream.method426();
			int l4 = anInt1268 + (i2 >> 4 & 7);
			int k7 = anInt1269 + (i2 & 7);
			int j10 = stream.method434();
			int k12 = stream.method428();
			int i15 = k12 >> 2;
			int k16 = k12 & 3;
			int l17 = anIntArray1177[i15];
			if (l4 >= 0 && k7 >= 0 && l4 < 104 && k7 < 104)
				method130(-1, j10, k16, l17, k7, i15, plane, l4, 0);
			return;
		}
		if (j == 4) {
			int j2 = stream.readUnsignedByte();
			int i5 = anInt1268 + (j2 >> 4 & 7);
			int l7 = anInt1269 + (j2 & 7);
			int k10 = stream.readUnsignedWord();
			int l12 = stream.readUnsignedByte();
			int j15 = stream.readUnsignedWord();
			if (i5 >= 0 && l7 >= 0 && i5 < 104 && l7 < 104) {
				i5 = i5 * 128 + 64;
				l7 = l7 * 128 + 64;
				Animable_Sub3 class30_sub2_sub4_sub3 = new Animable_Sub3(plane,
						loopCycle, j15, k10, method42(plane, l7, i5) - l12, l7,
						i5);
				aClass19_1056.insertBack(class30_sub2_sub4_sub3);
			}
			return;
		}
		if (j == 44) {
			int k2 = stream.method436();
			int j5 = stream.readUnsignedWord();
			int i8 = stream.readUnsignedByte();
			int l10 = anInt1268 + (i8 >> 4 & 7);
			int i13 = anInt1269 + (i8 & 7);
			if (l10 >= 0 && i13 >= 0 && l10 < 104 && i13 < 104) {
				Item class30_sub2_sub4_sub2_1 = new Item();
				class30_sub2_sub4_sub2_1.ID = k2;
				class30_sub2_sub4_sub2_1.anInt1559 = j5;
				if (groundArray[plane][l10][i13] == null)
					groundArray[plane][l10][i13] = new Deque();
				groundArray[plane][l10][i13]
						.insertBack(class30_sub2_sub4_sub2_1);
				spawnGroundItem(l10, i13);
			}
			return;
		}
		if (j == 101) {
			int l2 = stream.method427();
			int k5 = l2 >> 2;
			int j8 = l2 & 3;
			int i11 = anIntArray1177[k5];
			int j13 = stream.readUnsignedByte();
			int k15 = anInt1268 + (j13 >> 4 & 7);
			int l16 = anInt1269 + (j13 & 7);
			if (k15 >= 0 && l16 >= 0 && k15 < 104 && l16 < 104)
				method130(-1, -1, j8, i11, l16, k5, plane, k15, 0);
			return;
		}
		if (j == 117) {
			int i3 = stream.readUnsignedByte();
			int l5 = anInt1268 + (i3 >> 4 & 7);
			int k8 = anInt1269 + (i3 & 7);
			int j11 = l5 + stream.readSignedByte();
			int k13 = k8 + stream.readSignedByte();
			int l15 = stream.readSignedWord();
			int i17 = stream.readUnsignedWord();
			int i18 = stream.readUnsignedByte() * 4;
			int l18 = stream.readUnsignedByte() * 4;
			int k19 = stream.readUnsignedWord();
			int j20 = stream.readUnsignedWord();
			int i21 = stream.readUnsignedByte();
			int j21 = stream.readUnsignedByte();
			if (l5 >= 0 && k8 >= 0 && l5 < 104 && k8 < 104 && j11 >= 0
					&& k13 >= 0 && j11 < 104 && k13 < 104 && i17 != 65535) {
				l5 = l5 * 128 + 64;
				k8 = k8 * 128 + 64;
				j11 = j11 * 128 + 64;
				k13 = k13 * 128 + 64;
				Animable_Sub4 class30_sub2_sub4_sub4 = new Animable_Sub4(i21,
						l18, k19 + loopCycle, j20 + loopCycle, j21, plane,
						method42(plane, k8, l5) - i18, k8, l5, l15, i17);
				class30_sub2_sub4_sub4.method455(k19 + loopCycle, k13,
						method42(plane, k13, j11) - l18, j11);
				aClass19_1013.insertBack(class30_sub2_sub4_sub4);
			}
		}
	}

	private static void setLowMem() {
		WorldController.lowMem = true;
		Texture.lowMem = true;
		lowMem = true;
		ObjectManager.lowMem = true;
		ObjectDef.lowMem = true;
	}

	private void method139(Stream stream) {
		stream.initBitAccess();
		int k = stream.readBits(8);
		if (k < npcCount) {
			for (int l = k; l < npcCount; l++)
				anIntArray840[anInt839++] = npcIndices[l];

		}
		if (k > npcCount) {
			signlink.reporterror(myUsername + " Too many npcs");
			throw new RuntimeException("eek");
		}
		npcCount = 0;
		for (int i1 = 0; i1 < k; i1++) {
			int j1 = npcIndices[i1];
			NPC npc = npcArray[j1];
			int k1 = stream.readBits(1);
			if (k1 == 0) {
				npcIndices[npcCount++] = j1;
				npc.anInt1537 = loopCycle;
			} else {
				int l1 = stream.readBits(2);
				if (l1 == 0) {
					npcIndices[npcCount++] = j1;
					npc.anInt1537 = loopCycle;
					anIntArray894[anInt893++] = j1;
				} else if (l1 == 1) {
					npcIndices[npcCount++] = j1;
					npc.anInt1537 = loopCycle;
					int i2 = stream.readBits(3);
					npc.moveInDir(false, i2);
					int k2 = stream.readBits(1);
					if (k2 == 1)
						anIntArray894[anInt893++] = j1;
				} else if (l1 == 2) {
					npcIndices[npcCount++] = j1;
					npc.anInt1537 = loopCycle;
					int j2 = stream.readBits(3);
					npc.moveInDir(true, j2);
					int l2 = stream.readBits(3);
					npc.moveInDir(true, l2);
					int i3 = stream.readBits(1);
					if (i3 == 1)
						anIntArray894[anInt893++] = j1;
				} else if (l1 == 3)
					anIntArray840[anInt839++] = j1;
			}
		}

	}

	private void processLoginScreenInput() throws ClassNotFoundException,
	InstantiationException, IllegalAccessException,
	UnsupportedLookAndFeelException {
		if (super.clickMode3 == 1 && mouseX >= 4 && mouseX <= 104
				&& mouseY <= clientHeight - 7 && mouseY >= clientHeight - 65) {
			bgCheck = !bgCheck;
		}
		if (this.loginScreenState == 0 && animationDone) {
			resetImage();
			if (super.clickMode3 == 1
					&& (mouseX >= (clientWidth / 2 - 75)
					+ (titleBox[4].myWidth / 8) - 20)
					&& (mouseX <= (clientWidth / 2 - 75) + titleBox[4].myWidth
					- 8)
					&& (mouseY >= (clientHeight / 2) - (titleBox[4].myHeight))
					&& (mouseY <= (clientHeight / 2)
					+ (titleBox[4].myHeight / 3 - 12)))
				loginScreenCursorPos = 0;
			if (super.clickMode3 == 1
					&& ((mouseX >= (clientWidth / 2 - 75)
					+ (titleBox[4].myWidth / 8) - 20)
					&& (mouseX <= (clientWidth / 2 - 75)
					+ titleBox[4].myWidth - 8)
					&& (mouseY >= clientHeight / 2
					- (titleBox[4].myHeight) + 48) && (mouseY <= clientHeight / 2 + 49)))
				loginScreenCursorPos = 1;
			if (super.clickMode3 == 1 &&(saveClickX >= clientWidth/2 + 75 && saveClickX <= clientWidth/2 +154
					&& saveClickY >= clientHeight/2 + 54 && saveClickY <= clientHeight/2 + 71)) {
				//loginScreenState = 3; nex lmao
			} 
			if (super.clickMode3 == 1
					&& ((this.saveClickX >= clientWidth / 2 + 125)
							&& (saveClickX <= clientWidth / 2 + 196)
							&& (saveClickY >= clientHeight / 2 - 47) && (saveClickY <= clientHeight / 2 + 37))) {
				if ((myUsername.length() > 0) && (myPassword.length() > 0)) {
					loginFailures = 0;
					previousScreenState = 0;
					loginScreenState = 1;
				} else {
					this.loginScreenCursorPos = 0;
					this.loginMessage1 = "Username & Password";
					this.loginMessage2 = "Must be more than 1 character";
				}
				if (loggedIn)
					return;
			}
			do {
				int keyPressed = readChar(-796);
				if (keyPressed == -1)
					break;
				boolean validKey = false;
				for (int i2 = 0; i2 < validUserPassChars.length(); i2++) {
					if (keyPressed != validUserPassChars.charAt(i2))
						continue;
					validKey = true;
					break;
				}

				if (loginScreenCursorPos == 0) {
					if (keyPressed == 8 && myUsername.length() > 0)
						myUsername = myUsername.substring(0,
								myUsername.length() - 1);
					if (keyPressed == 9 || keyPressed == 10 || keyPressed == 13)
						loginScreenCursorPos = 1;

					if (validKey)
						myUsername += (char) keyPressed;
					if (myUsername.length() > 15)
						myUsername = myUsername.substring(0, 15);
				} else if (loginScreenCursorPos == 1) {
					if (keyPressed == 8 && myPassword.length() > 0)
						myPassword = myPassword.substring(0,
								myPassword.length() - 1);
					if (keyPressed == 9 || keyPressed == 10 || keyPressed == 13) {
						if (myPassword.length() > 0) {
							loginFailures = 0;
							loginScreenState = 1;
						} else {
							loginScreenCursorPos = 0;
						}
					}
					if (validKey)
						myPassword += (char) keyPressed;
					if (myPassword.length() > 20)
						myPassword = myPassword.substring(0, 20);
				}
			} while (true);
			return;
		}
		if (loginScreenState == 1) {
			if (circle == 4 + getRandom(3, true)) {
				login(previousScreenState == 3 ? getRegister().username : myUsername, 
						previousScreenState == 3 ? getRegister().password : myPassword, false);
				circle = 0;
			}
		}
		if (loginScreenState == 2) {
			if (clickInRegion(clientWidth / 2 - 84, clientHeight / 2 + 29,
					clientWidth / 2 + 82, clientHeight / 2 + 63)) {
				loginScreenState = previousScreenState;
				loginCode = 0;
			}
		}
		if (loginScreenState == 3) {
			getRegister().processInput();
		}
	}

	private int getRandom(int number, boolean greaterThan0) {
		Random random = new Random();
		int randomNr = random.nextInt(number) + (greaterThan0 ? 1 : 0);
		return randomNr;
	}

	private void markMinimap(Sprite sprite, int x, int y) {
		if (sprite == null)
			return;
		try {
			int offX = clientSize == 0 ? 0 : clientWidth - 249;
			int k = viewRotation + minimapRotation & 0x7ff;
			int l = x * x + y * y;
			if (l > 6400) {
				return;
			}
			int i1 = Model.modelIntArray1[k];
			int j1 = Model.modelIntArray2[k];
			i1 = (i1 * 256) / (minimapZoom + 256);
			j1 = (j1 * 256) / (minimapZoom + 256);
			int k1 = y * i1 + x * j1 >> 16;
			int l1 = y * j1 - x * i1 >> 16;
			if (clientSize == 0)
				sprite.drawSprite(
						((105 + k1) - sprite.maxWidth / 2) + 4 + offX, 88 - l1
						- sprite.maxHeight / 2 - 4);
			else
				sprite.drawSprite(((77 + k1) - sprite.maxWidth / 2) + 4
						+ (clientWidth - 167), 85 - l1 - sprite.maxHeight / 2
						- 4);
		} catch (Exception e) {

		}
	}

	private void method142(int i, int j, int k, int l, int i1, int j1, int k1) {
		if (i1 >= 1 && i >= 1 && i1 <= 102 && i <= 102) {
			if (lowMem && j != plane)
				return;
			int i2 = 0;
			if (j1 == 0)
				i2 = worldController.method300(j, i1, i);
			if (j1 == 1)
				i2 = worldController.method301(j, i1, i);
			if (j1 == 2)
				i2 = worldController.method302(j, i1, i);
			if (j1 == 3)
				i2 = worldController.method303(j, i1, i);
			if (i2 != 0) {
				int i3 = worldController.method304(j, i1, i, i2);
				int j2 = i2 >> 14 & 0x7fff;
		int k2 = i3 & 0x1f;
		int l2 = i3 >> 6;
		if (j1 == 0) {
			worldController.method291(i1, j, i, (byte) -119);
			ObjectDef class46 = ObjectDef.forID(j2);
			if (class46.isUnwalkable)
				aClass11Array1230[j].method215(l2, k2,
						class46.aBoolean757, i1, i);
		}
		if (j1 == 1)
			worldController.method292(i, j, i1);
		if (j1 == 2) {
			worldController.method293(j, i1, i);
			ObjectDef class46_1 = ObjectDef.forID(j2);
			if (i1 + class46_1.anInt744 > 103
					|| i + class46_1.anInt744 > 103
					|| i1 + class46_1.anInt761 > 103
					|| i + class46_1.anInt761 > 103)
				return;
			if (class46_1.isUnwalkable)
				aClass11Array1230[j].method216(l2, class46_1.anInt744,
						i1, i, class46_1.anInt761,
						class46_1.aBoolean757);
		}
		if (j1 == 3) {
			worldController.method294(j, i, i1);
			ObjectDef class46_2 = ObjectDef.forID(j2);
			if (class46_2.isUnwalkable && class46_2.hasActions)
				aClass11Array1230[j].method218(i, i1);
		}
			}
			if (k1 >= 0) {
				int j3 = j;
				if (j3 < 3 && (byteGroundArray[1][i1][i] & 2) == 2)
					j3++;
				ObjectManager.method188(worldController, k, i, l, j3,
						aClass11Array1230[j], intGroundArray, i1, k1, j);
			}
		}
	}

	private void updatePlayers(int i, Stream stream) {
		anInt839 = 0;
		anInt893 = 0;
		method117(stream);
		method134(stream);
		method91(stream, i);
		method49(stream);
		for (int k = 0; k < anInt839; k++) {
			int l = anIntArray840[k];
			if (playerArray[l].anInt1537 != loopCycle)
				playerArray[l] = null;
		}

		if (stream.currentOffset != i) {
			signlink.reporterror("Error packet size mismatch in getplayer pos:"
					+ stream.currentOffset + " psize:" + i);
			throw new RuntimeException("eek");
		}
		for (int i1 = 0; i1 < playerCount; i1++)
			if (playerArray[playerIndices[i1]] == null) {
				signlink.reporterror(myUsername
						+ " null entry in pl list - pos:" + i1 + " size:"
						+ playerCount);
				throw new RuntimeException("eek");
			}

	}

	private void setCameraPos(int j, int k, int l, int i1, int j1, int k1) {
		int l1 = 2048 - k & 0x7ff;
		int i2 = 2048 - j1 & 0x7ff;
		int j2 = 0;
		int k2 = 0;
		int l2 = j;
		if (l1 != 0) {
			int i3 = Model.modelIntArray1[l1];
			int k3 = Model.modelIntArray2[l1];
			int i4 = k2 * k3 - l2 * i3 >> 16;
		l2 = k2 * i3 + l2 * k3 >> 16;
				k2 = i4;
		}
		if (i2 != 0) {
			/*
			 * xxx if(cameratoggle){ if(zoom == 0) zoom = k2; if(lftrit == 0)
			 * lftrit = j2; if(fwdbwd == 0) fwdbwd = l2; k2 = zoom; j2 = lftrit;
			 * l2 = fwdbwd; }
			 */
			int j3 = Model.modelIntArray1[i2];
			int l3 = Model.modelIntArray2[i2];
			int j4 = l2 * j3 + j2 * l3 >> 16;
			l2 = l2 * l3 - j2 * j3 >> 16;
			j2 = j4;
		}
		xCameraPos = l - j2;
		zCameraPos = i1 - k2;
		yCameraPos = k1 - l2;
		yCameraCurve = k;
		xCameraCurve = j1;
	}

	public void updateStrings(String str, int i) {
		switch (i) {
		case 1675:
			sendFrame126(str, 17508);
			break;// Stab
		case 1676:
			sendFrame126(str, 17509);
			break;// Slash
		case 1677:
			sendFrame126(str, 17510);
			break;// Cursh
		case 1678:
			sendFrame126(str, 17511);
			break;// Magic
		case 1679:
			sendFrame126(str, 17512);
			break;// Range
		case 1680:
			sendFrame126(str, 17513);
			break;// Stab
		case 1681:
			sendFrame126(str, 17514);
			break;// Slash
		case 1682:
			sendFrame126(str, 17515);
			break;// Crush
		case 1683:
			sendFrame126(str, 17516);
			break;// Magic
		case 1684:
			sendFrame126(str, 17517);
			break;// Range
		case 1686:
			sendFrame126(str, 17518);
			break;// Strength
		case 1687:
			sendFrame126(str, 17519);
			break;// Prayer
		}
	}

	public void sendFrame126(String str, int i) {
		RSInterface.interfaceCache[i].message = str;
		if (RSInterface.interfaceCache[i].parentID == tabInterfaceIDs[tabID])
			needDrawTabArea = true;
	}

	public void sendPacket185(int buttonID) {
		stream.createFrame(185);
		stream.writeWord(buttonID);
		RSInterface rsi = RSInterface.interfaceCache[buttonID];
		if (rsi.valueIndexArray != null && rsi.valueIndexArray[0][0] == 5) {
			int configID = rsi.valueIndexArray[0][1];
			variousSettings[configID] = 1 - variousSettings[configID];
			handleActions(configID);
			needDrawTabArea = true;
		}
	}

	public void sendPacket185(int button, int toggle, int type) {
		switch (type) {
		case 135:
			RSInterface class9 = RSInterface.interfaceCache[button];
			boolean flag8 = true;
			if (class9.contentType > 0)
				flag8 = promptUserForInput(class9);
			if (flag8) {
				stream.createFrame(185);
				stream.writeWord(button);
			}
			break;
		case 646:
			stream.createFrame(185);
			stream.writeWord(button);
			RSInterface class9_2 = RSInterface.interfaceCache[button];
			if (class9_2.valueIndexArray != null
					&& class9_2.valueIndexArray[0][0] == 5) {
				if (variousSettings[toggle] != class9_2.requiredValues[0]) {
					variousSettings[toggle] = class9_2.requiredValues[0];
					handleActions(toggle);
					needDrawTabArea = true;
				}
			}
			break;
		case 169:
			stream.createFrame(185);
			stream.writeWord(button);
			RSInterface class9_3 = RSInterface.interfaceCache[button];
			if (class9_3.valueIndexArray != null
					&& class9_3.valueIndexArray[0][0] == 5) {
				variousSettings[toggle] = 1 - variousSettings[toggle];
				handleActions(toggle);
				needDrawTabArea = true;
			}
			switch (button) {
			case 19136:
				System.out.println("toggle = " + toggle);
				if (toggle == 0)
					sendFrame36(173, toggle);
				if (toggle == 1)
					sendPacket185(153, 173, 646);
				break;
			}
			break;
		}
	}

	public void sendFrame36(int id, int state) {
		anIntArray1045[id] = state;
		if (variousSettings[id] != state) {
			variousSettings[id] = state;
			handleActions(id);
			needDrawTabArea = true;
			if (dialogID != -1)
				inputTaken = true;
		}
	}

	public void sendFrame219() {
		if (invOverlayInterfaceID != -1) {
			invOverlayInterfaceID = -1;
			needDrawTabArea = true;
			tabAreaAltered = true;
		}
		if (backDialogID != -1) {
			backDialogID = -1;
			inputTaken = true;
		}
		if (inputDialogState != 0) {
			inputDialogState = 0;
			inputTaken = true;
		}
		openInterfaceID = -1;
		aBoolean1149 = false;
	}

	public void sendFrame248(int interfaceID, int sideInterfaceID) {
		if (backDialogID != -1) {
			backDialogID = -1;
			inputTaken = true;
		}
		if (inputDialogState != 0) {
			inputDialogState = 0;
			inputTaken = true;
		}
		openInterfaceID = interfaceID;
		invOverlayInterfaceID = sideInterfaceID;
		needDrawTabArea = true;
		tabAreaAltered = true;
		aBoolean1149 = false;
	}

	private boolean parsePacket() {
		if (socketStream == null)
			return false;
		try {
			int i = socketStream.available();
			if (i == 0)
				return false;
			if (opCode == -1) {
				socketStream.flushInputStream(inStream.buffer, 1);
				opCode = inStream.buffer[0] & 0xff;
				if (encryption != null)
					opCode = opCode - encryption.getNextKey() & 0xff;
				pktSize = SizeConstants.packetSizes[opCode];
				i--;
			}
			if (pktSize == -1)
				if (i > 0) {
					socketStream.flushInputStream(inStream.buffer, 1);
					pktSize = inStream.buffer[0] & 0xff;
					i--;
				} else {
					return false;
				}
			if (pktSize == -2)
				if (i > 1) {
					socketStream.flushInputStream(inStream.buffer, 2);
					inStream.currentOffset = 0;
					pktSize = inStream.readUnsignedWord();
					i -= 2;
				} else {
					return false;
				}
			if (i < pktSize)
				return false;
			inStream.currentOffset = 0;
			socketStream.flushInputStream(inStream.buffer, pktSize);
			anInt1009 = 0;
			anInt843 = anInt842;
			anInt842 = anInt841;
			anInt841 = opCode;
			switch (opCode) {
			case 81:
				updatePlayers(pktSize, inStream);
				aBoolean1080 = false;
				opCode = -1;
				return true;

			case 172:
				try {
					boolean active = inStream.readByte() == 1;
					String special = "";
					if (active)
						special = inStream.readString();
					getFamiliar().setActive(active);
					getFamiliar().setFamiliar("", special);
				} catch (Exception e) {
					e.printStackTrace();
				}
				opCode = -1;
				return true;
			case 198:
				printedMessage = inStream.readString();
				if (loggerEnabled) {
					if (printedMessage.contains(".java:"))
						Logger.addLog("Error", printedMessage, Color.RED,
								Color.ORANGE);
					else
						Logger.addLog("Info", printedMessage, Color.GREEN,
								Color.CYAN);
				}
				System.out.println(printedMessage);
				opCode = -1;
				return true;

			case 176:
				daysSinceRecovChange = inStream.method427();
				unreadMessages = inStream.method435();
				membersInt = inStream.readUnsignedByte();
				anInt1193 = inStream.method440();
				daysSinceLastLogin = inStream.readUnsignedWord();
				if (anInt1193 != 0 && openInterfaceID == -1) {
					signlink.dnslookup(TextClass.method586(anInt1193));
					clearTopInterfaces();
					char c = '\u028A';
					if (daysSinceRecovChange != 201 || membersInt == 1)
						c = '\u028F';
					reportAbuseInput = "";
					canMute = false;
					for (int k9 = 0; k9 < RSInterface.interfaceCache.length; k9++) {
						if (RSInterface.interfaceCache[k9] == null
								|| RSInterface.interfaceCache[k9].contentType != c)
							continue;
						openInterfaceID = RSInterface.interfaceCache[k9].parentID;

					}
				}
				opCode = -1;
				return true;

			case 64:
				anInt1268 = inStream.method427();
				anInt1269 = inStream.method428();
				for (int j = anInt1268; j < anInt1268 + 8; j++) {
					for (int l9 = anInt1269; l9 < anInt1269 + 8; l9++)
						if (groundArray[plane][j][l9] != null) {
							groundArray[plane][j][l9] = null;
							spawnGroundItem(j, l9);
						}
				}
				for (Class30_Sub1 class30_sub1 = (Class30_Sub1) aClass19_1179
						.getFront(); class30_sub1 != null; class30_sub1 = (Class30_Sub1) aClass19_1179
						.reverseGetNext())
					if (class30_sub1.anInt1297 >= anInt1268
					&& class30_sub1.anInt1297 < anInt1268 + 8
					&& class30_sub1.anInt1298 >= anInt1269
					&& class30_sub1.anInt1298 < anInt1269 + 8
					&& class30_sub1.anInt1295 == plane)
						class30_sub1.anInt1294 = 0;
				opCode = -1;
				return true;

			case 185:
				int k = inStream.method436();
				RSInterface.interfaceCache[k].anInt233 = 3;
				if (myPlayer.desc == null)
					RSInterface.interfaceCache[k].mediaID = (myPlayer.anIntArray1700[0] << 25)
					+ (myPlayer.anIntArray1700[4] << 20)
					+ (myPlayer.equipment[0] << 15)
					+ (myPlayer.equipment[8] << 10)
					+ (myPlayer.equipment[11] << 5)
					+ myPlayer.equipment[1];
				else
					RSInterface.interfaceCache[k].mediaID = (int) (0x12345678L + myPlayer.desc.type);
				opCode = -1;
				return true;

				/* Clan chat packet */
			case 217:
				try {
					name = inStream.readString();
					message = inStream.readString();
					clanname = inStream.readString();
					rights = inStream.readUnsignedWord();
					message = TextInput.processText(message);
					message = Censor.doCensor(message);
					// System.out.println(clanname);
					pushMessage(message, 16, name);
				} catch (Exception e) {
					e.printStackTrace();
				}
				opCode = -1;
				return true;

			case 107:
				aBoolean1160 = false;
				for (int l = 0; l < 5; l++)
					aBooleanArray876[l] = false;
				xpCounter = 0;
				opCode = -1;
				return true;

			case 72:
				int i1 = inStream.method434();
				RSInterface class9 = RSInterface.interfaceCache[i1];
				for (int k15 = 0; k15 < class9.inv.length; k15++) {
					class9.inv[k15] = -1;
					class9.inv[k15] = 0;
				}
				opCode = -1;
				return true;

			case 124:
				int skillID = inStream.readUnsignedByte();
				int gainedXP = inStream.readDWord();
				int totalEXP = inStream.readDWord();
				addXP(skillID, gainedXP);
				totalXP = totalEXP;
				opCode = -1;
				return true;

			case 214:
				ignoreCount = pktSize / 8;
				for (int j1 = 0; j1 < ignoreCount; j1++)
					ignoreListAsLongs[j1] = inStream.readQWord();
				opCode = -1;
				return true;

			case 166:
				inStream.readUnsignedByte();
				int type = inStream.readUnsignedByte();
				int slot = inStream.readUnsignedByte();
				if (type == 1) {
					slotColor[slot] = inStream.readUnsignedByte();
				} else if (type == 2) {
					slotColorPercent[slot] = inStream.readUnsignedByte();
				} else if (type == 3) {
					int lololol = inStream.readUnsignedByte();
					if (lololol == 1) {
						slotAborted[slot] = true;
					} else {
						slotAborted[slot] = false;
					}
				} else if (type == 4) {
					int thing = inStream.readUnsignedByte();
					if (thing == 1) {
						RSInterface.interfaceCache[24578 + slot].sprite1 = RSInterface.interfaceCache[24578 + slot].setSprite;
						buttonclicked = false;
						interfaceButtonAction = 0;
					} else if (thing == 2) {
						slotSelected = slot;
					} else if (thing == 3) {
						RSInterface.interfaceCache[24578 + slot].sprite1 = RSInterface.interfaceCache[24578 + slot].savedFirstSprite;
						slots[slot] = "";
						Slots[slot] = 0;
					}
				} else if (type == 5) {
					int thing1 = inStream.readUnsignedByte();
					if (thing1 == 1) {
						slotUsing = slot;
						slots[slot] = "Sell";
						Slots[slot] = 1;
					} else if (thing1 == 2) {
						slotUsing = slot;
						slots[slot] = "Buy";
						Slots[slot] = 1;
					} else if (thing1 == 3) {
						RSInterface.interfaceCache[24578 + slot].sprite1 = RSInterface.interfaceCache[24578 + slot].setSprite;
						Slots[slot] = 2;
						slots[slot] = "Sell";
					} else if (thing1 == 4) {
						RSInterface.interfaceCache[24578 + slot].sprite1 = RSInterface.interfaceCache[24578 + slot].setSprite;
						Slots[slot] = 2;
						slots[slot] = "Buy";
					} else if (thing1 == 5) {
						RSInterface.interfaceCache[24578 + slot].sprite1 = RSInterface.interfaceCache[24578 + slot].setSprite;
						Slots[slot] = 3;
						slots[slot] = "Sell";
					} else if (thing1 == 6) {
						RSInterface.interfaceCache[24578 + slot].sprite1 = RSInterface.interfaceCache[24578 + slot].setSprite;
						Slots[slot] = 3;
						slots[slot] = "Buy";
					}
				} else if (type == 6) {
					inStream.readUnsignedByte();
					buttonclicked = true;
					amountOrNameInput = "";
					totalItemResults = 0;
				} else if (type == 7) {
					int anInt1308 = inStream.readUnsignedByte();
					method497(anInt1308);
				} else {
					inStream.readUnsignedByte();
				}
				inStream.readUnsignedByte();
				inStream.readUnsignedByte();
				opCode = -1;
				return true;

			case 134:
				needDrawTabArea = true;
				int k1 = inStream.readUnsignedByte();
				int i10 = inStream.method439();
				int l15 = inStream.readUnsignedByte();
				currentExp[k1] = i10;
				currentStats[k1] = l15;
				maxStats[k1] = 1;
				for (int k20 = 0; k20 < 98; k20++)
					if (i10 >= anIntArray1019[k20])
						maxStats[k1] = k20 + 2;
				opCode = -1;
				return true;

			case 175:
				int soundId = inStream.readShort();
				int sType = inStream.readUByte();
				int delay = inStream.readShort();
				int volume = inStream.readShort();
				sound[currentSound] = soundId;
				soundType[currentSound] = sType;
				soundDelay[currentSound] = delay
						+ Sounds.anIntArray326[soundId];
				soundVolume[currentSound] = volume;
				currentSound++;
				opCode = -1;
				return true;
			case 71:
				int l1 = inStream.readUnsignedWord();
				int j10 = inStream.method426();
				if (l1 == 65535)
					l1 = -1;
				tabInterfaceIDs[j10] = l1;
				needDrawTabArea = true;
				tabAreaAltered = true;
				opCode = -1;
				return true;

			case 74:
				int songID = inStream.method434();
				if (songID == 65535) {
					songID = -1;
				}
				if (songID != currentSong && musicEnabled && !lowMem
						&& prevSong == 0) {
					nextSong = songID;
					songChanging = true;
					onDemandFetcher.requestFileData(2, nextSong);
				}
				currentSong = songID;
				opCode = -1;
				return true;

			case 121:
				int j2 = inStream.method436();
				int k10 = inStream.method435();
				if (musicEnabled && !lowMem) {
					nextSong = j2;
					songChanging = false;
					onDemandFetcher.requestFileData(2, nextSong);
					prevSong = k10;
				}
				opCode = -1;
				return true;

			case 109:
				resetLogout();
				opCode = -1;
				return false;

			case 70:
				int k2 = inStream.readSignedWord();
				int l10 = inStream.method437();
				int i16 = inStream.method434();
				RSInterface class9_5 = RSInterface.interfaceCache[i16];
				class9_5.xOffset = k2;
				class9_5.yOffset = l10;
				opCode = -1;
				return true;

			case 73:
			case 241:
				int l2 = anInt1069;
				int i11 = anInt1070;
				if (opCode == 73) {
					l2 = inStream.method435();
					i11 = inStream.readUnsignedWord();
					aBoolean1159 = false;
				}
				if (opCode == 241) {
					i11 = inStream.method435();
					inStream.initBitAccess();
					for (int j16 = 0; j16 < 4; j16++) {
						for (int l20 = 0; l20 < 13; l20++) {
							for (int j23 = 0; j23 < 13; j23++) {
								int i26 = inStream.readBits(1);
								if (i26 == 1)
									anIntArrayArrayArray1129[j16][l20][j23] = inStream
									.readBits(26);
								else
									anIntArrayArrayArray1129[j16][l20][j23] = -1;
							}
						}
					}
					inStream.finishBitAccess();
					l2 = inStream.readUnsignedWord();
					aBoolean1159 = true;
				}
				if (anInt1069 == l2 && anInt1070 == i11 && loadingStage == 2) {
					opCode = -1;
					return true;
				}
				anInt1069 = l2;
				anInt1070 = i11;
				baseX = (anInt1069 - 6) * 8;
				baseY = (anInt1070 - 6) * 8;
				aBoolean1141 = (anInt1069 / 8 == 48 || anInt1069 / 8 == 49)
						&& anInt1070 / 8 == 48;
				if (anInt1069 / 8 == 48 && anInt1070 / 8 == 148)
					aBoolean1141 = true;
				loadingStage = 1;
				aLong824 = System.currentTimeMillis();
				gameScreenIP.initDrawingArea();
				loadingPleaseWait.drawSprite(7, 4);
				gameScreenIP.drawGraphics(4, super.graphics, 4);
				if (opCode == 73) {
					int k16 = 0;
					for (int i21 = (anInt1069 - 6) / 8; i21 <= (anInt1069 + 6) / 8; i21++) {
						for (int k23 = (anInt1070 - 6) / 8; k23 <= (anInt1070 + 6) / 8; k23++)
							k16++;
					}
					aByteArrayArray1183 = new byte[k16][];
					aByteArrayArray1247 = new byte[k16][];
					anIntArray1234 = new int[k16];
					anIntArray1235 = new int[k16];
					anIntArray1236 = new int[k16];
					k16 = 0;
					for (int l23 = (anInt1069 - 6) / 8; l23 <= (anInt1069 + 6) / 8; l23++) {
						for (int j26 = (anInt1070 - 6) / 8; j26 <= (anInt1070 + 6) / 8; j26++) {
							anIntArray1234[k16] = (l23 << 8) + j26;
							if (aBoolean1141
									&& (j26 == 49 || j26 == 149 || j26 == 147
									|| l23 == 50 || l23 == 49
									&& j26 == 47)) {
								anIntArray1235[k16] = -1;
								anIntArray1236[k16] = -1;
								k16++;
							} else {
								int k28 = anIntArray1235[k16] = onDemandFetcher
										.getMapCount(0, j26, l23);
								if (k28 != -1)
									onDemandFetcher.requestFileData(3, k28);
								int j30 = anIntArray1236[k16] = onDemandFetcher
										.getMapCount(1, j26, l23);
								if (j30 != -1)
									onDemandFetcher.requestFileData(3, j30);
								k16++;
							}
						}
					}
				}
				if (opCode == 241) {
					int l16 = 0;
					int ai[] = new int[676];
					for (int i24 = 0; i24 < 4; i24++) {
						for (int k26 = 0; k26 < 13; k26++) {
							for (int l28 = 0; l28 < 13; l28++) {
								int k30 = anIntArrayArrayArray1129[i24][k26][l28];
								if (k30 != -1) {
									int k31 = k30 >> 14 & 0x3ff;
							int i32 = k30 >> 3 & 0x7ff;
					int k32 = (k31 / 8 << 8) + i32 / 8;
					for (int j33 = 0; j33 < l16; j33++) {
						if (ai[j33] != k32)
							continue;
						k32 = -1;

					}
					if (k32 != -1)
						ai[l16++] = k32;
								}
							}
						}
					}
					aByteArrayArray1183 = new byte[l16][];
					aByteArrayArray1247 = new byte[l16][];
					anIntArray1234 = new int[l16];
					anIntArray1235 = new int[l16];
					anIntArray1236 = new int[l16];
					for (int l26 = 0; l26 < l16; l26++) {
						int i29 = anIntArray1234[l26] = ai[l26];
						int l30 = i29 >> 8 & 0xff;
					int l31 = i29 & 0xff;
					int j32 = anIntArray1235[l26] = onDemandFetcher
							.getMapCount(0, l31, l30);
					if (j32 != -1)
						onDemandFetcher.requestFileData(3, j32);
					int i33 = anIntArray1236[l26] = onDemandFetcher
							.getMapCount(1, l31, l30);
					if (i33 != -1)
						onDemandFetcher.requestFileData(3, i33);
					}
				}
				int i17 = baseX - anInt1036;
				int j21 = baseY - anInt1037;
				anInt1036 = baseX;
				anInt1037 = baseY;
				for (int j24 = 0; j24 < 16384; j24++) {
					NPC npc = npcArray[j24];
					if (npc != null) {
						for (int j29 = 0; j29 < 10; j29++) {
							npc.smallX[j29] -= i17;
							npc.smallY[j29] -= j21;
						}
						npc.x -= i17 * 128;
						npc.y -= j21 * 128;
					}
				}
				for (int i27 = 0; i27 < maxPlayers; i27++) {
					Player player = playerArray[i27];
					if (player != null) {
						for (int i31 = 0; i31 < 10; i31++) {
							player.smallX[i31] -= i17;
							player.smallY[i31] -= j21;
						}
						player.x -= i17 * 128;
						player.y -= j21 * 128;
					}
				}
				aBoolean1080 = true;
				byte byte1 = 0;
				byte byte2 = 104;
				byte byte3 = 1;
				if (i17 < 0) {
					byte1 = 103;
					byte2 = -1;
					byte3 = -1;
				}
				byte byte4 = 0;
				byte byte5 = 104;
				byte byte6 = 1;
				if (j21 < 0) {
					byte4 = 103;
					byte5 = -1;
					byte6 = -1;
				}
				for (int k33 = byte1; k33 != byte2; k33 += byte3) {
					for (int l33 = byte4; l33 != byte5; l33 += byte6) {
						int i34 = k33 + i17;
						int j34 = l33 + j21;
						for (int k34 = 0; k34 < 4; k34++)
							if (i34 >= 0 && j34 >= 0 && i34 < 104 && j34 < 104)
								groundArray[k34][k33][l33] = groundArray[k34][i34][j34];
							else
								groundArray[k34][k33][l33] = null;
					}
				}
				for (Class30_Sub1 class30_sub1_1 = (Class30_Sub1) aClass19_1179
						.getFront(); class30_sub1_1 != null; class30_sub1_1 = (Class30_Sub1) aClass19_1179
						.reverseGetNext()) {
					class30_sub1_1.anInt1297 -= i17;
					class30_sub1_1.anInt1298 -= j21;
					if (class30_sub1_1.anInt1297 < 0
							|| class30_sub1_1.anInt1298 < 0
							|| class30_sub1_1.anInt1297 >= 104
							|| class30_sub1_1.anInt1298 >= 104)
						class30_sub1_1.unlink();
				}
				if (destX != 0) {
					destX -= i17;
					destY -= j21;
				}
				aBoolean1160 = false;
				opCode = -1;
				return true;

			case 208:
				int i3 = inStream.method437();
				if (i3 >= 0)
					method60(i3);
				anInt1018 = i3;
				opCode = -1;
				return true;

			case 99:
				anInt1021 = inStream.readUnsignedByte();
				opCode = -1;
				return true;

			case 75:
				int j3 = inStream.method436();
				int j11 = inStream.method436();
				RSInterface.interfaceCache[j11].anInt233 = 2;
				RSInterface.interfaceCache[j11].mediaID = j3;
				opCode = -1;
				return true;

			case 114:
				anInt1104 = inStream.method434() * 30;
				opCode = -1;
				return true;

			case 60:
				anInt1269 = inStream.readUnsignedByte();
				anInt1268 = inStream.method427();
				while (inStream.currentOffset < pktSize) {
					int k3 = inStream.readUnsignedByte();
					method137(inStream, k3);
				}
				opCode = -1;
				return true;

			case 35:
				int l3 = inStream.readUnsignedByte();
				int k11 = inStream.readUnsignedByte();
				int j17 = inStream.readUnsignedByte();
				int k21 = inStream.readUnsignedByte();
				aBooleanArray876[l3] = true;
				anIntArray873[l3] = k11;
				anIntArray1203[l3] = j17;
				anIntArray928[l3] = k21;
				anIntArray1030[l3] = 0;
				opCode = -1;
				return true;

			case 174:
				followPlayer = 0;
				followNPC = 0;
				int l11z = inStream.readUnsignedWord();
				int iq = inStream.readUnsignedByte();
				followDistance = inStream.readUnsignedWord();
				if (iq == 0) {
					followNPC = l11z;
				} else if (iq == 1) {
					followPlayer = l11z;
				}
				opCode = -1;
				return true;

			case 178:
				boolean active = inStream.readByte() == 1;
				drawPane = active;
				opCode = -1;
				return true;

			case 104:
				int j4 = inStream.method427();
				int i12 = inStream.method426();
				String s6 = inStream.readString();
				if (j4 >= 1 && j4 <= 5) {
					if (s6.equalsIgnoreCase("null"))
						s6 = null;
					atPlayerActions[j4 - 1] = s6;
					atPlayerArray[j4 - 1] = i12 == 0;
				}
				opCode = -1;
				return true;

			case 78:
				destX = 0;
				opCode = -1;
				return true;

			case 253:
				String s = inStream.readString();
				if (s.endsWith(":tradereq:")) {
					String s3 = s.substring(0, s.indexOf(":"));
					long l17 = TextClass.longForName(s3);
					boolean flag2 = false;
					for (int j27 = 0; j27 < ignoreCount; j27++) {
						if (ignoreListAsLongs[j27] != l17)
							continue;
						flag2 = true;

					}
					if (!flag2 && anInt1251 == 0)
						pushMessage("wishes to trade with you.", 4, s3);
				} else if (s.endsWith("::")) {
					String s4 = s.substring(0, s.indexOf(":"));
					TextClass.longForName(s4);
					pushMessage("Clan: ", 8, s4);
				} else if (s.endsWith("#url#")) {
					String link = s.substring(0, s.indexOf("#"));
					pushMessage("Join us at: ", 9, link);
				} else if (s.endsWith(":duelreq:")) {
					String s4 = s.substring(0, s.indexOf(":"));
					long l18 = TextClass.longForName(s4);
					boolean flag3 = false;
					for (int k27 = 0; k27 < ignoreCount; k27++) {
						if (ignoreListAsLongs[k27] != l18)
							continue;
						flag3 = true;

					}
					if (!flag3 && anInt1251 == 0)
						pushMessage("wishes to duel with you.", 8, s4);
				} else if (s.endsWith(":chalreq:")) {
					String s5 = s.substring(0, s.indexOf(":"));
					long l19 = TextClass.longForName(s5);
					boolean flag4 = false;
					for (int l27 = 0; l27 < ignoreCount; l27++) {
						if (ignoreListAsLongs[l27] != l19)
							continue;
						flag4 = true;

					}
					if (!flag4 && anInt1251 == 0) {
						String s8 = s.substring(s.indexOf(":") + 1,
								s.length() - 9);
						pushMessage(s8, 8, s5);
					}
				} else if (s.endsWith(":prayer:curses")) {
					prayerBook = "Curses";
				} else if (s.endsWith(":prayer:prayers")) {
					prayerBook = "Prayers";
				} else if (s.endsWith(":quicks:off")) {
					prayClicked = false;
				} else if (s.endsWith(":quicks:on")) {
					prayClicked = true;
				} else if (s.endsWith(":resetautocast:")) {
					Autocast = false;
					autocastId = 0;
					magicAuto.drawSprite(1000, 1000);
				} else {
					pushMessage(s, 0, "");
				}
				opCode = -1;
				return true;

			case 1:
				for (int k4 = 0; k4 < playerArray.length; k4++)
					if (playerArray[k4] != null)
						playerArray[k4].anim = -1;
				for (int j12 = 0; j12 < npcArray.length; j12++)
					if (npcArray[j12] != null)
						npcArray[j12].anim = -1;
				opCode = -1;
				return true;

			case 50:
				long l4 = inStream.readQWord();
				int i18 = inStream.readUnsignedByte();
				String s7 = TextClass.fixName(TextClass.nameForLong(l4));
				for (int k24 = 0; k24 < friendsCount; k24++) {
					if (l4 != friendsListAsLongs[k24])
						continue;
					if (friendsNodeIDs[k24] != i18) {
						friendsNodeIDs[k24] = i18;
						needDrawTabArea = true;
						if (i18 >= 2) {
							pushMessage(s7 + " has logged in.", 5, "");
						}
						if (i18 <= 1) {
							pushMessage(s7 + " has logged out.", 5, "");
						}
					}
					s7 = null;

				}
				if (s7 != null && friendsCount < 200) {
					friendsListAsLongs[friendsCount] = l4;
					friendsList[friendsCount] = s7;
					friendsNodeIDs[friendsCount] = i18;
					friendsCount++;
					needDrawTabArea = true;
				}
				for (boolean flag6 = false; !flag6;) {
					flag6 = true;
					for (int k29 = 0; k29 < friendsCount - 1; k29++)
						if (friendsNodeIDs[k29] != nodeID
						&& friendsNodeIDs[k29 + 1] == nodeID
						|| friendsNodeIDs[k29] == 0
						&& friendsNodeIDs[k29 + 1] != 0) {
							int j31 = friendsNodeIDs[k29];
							friendsNodeIDs[k29] = friendsNodeIDs[k29 + 1];
							friendsNodeIDs[k29 + 1] = j31;
							String s10 = friendsList[k29];
							friendsList[k29] = friendsList[k29 + 1];
							friendsList[k29 + 1] = s10;
							long l32 = friendsListAsLongs[k29];
							friendsListAsLongs[k29] = friendsListAsLongs[k29 + 1];
							friendsListAsLongs[k29 + 1] = l32;
							needDrawTabArea = true;
							flag6 = false;
						}
				}
				opCode = -1;
				return true;

			case 110:
				if (tabID == 12)
					needDrawTabArea = true;
				currentEnergy = inStream.readUByte();
				opCode = -1;
				return true;

			case 254:
				anInt855 = inStream.readUnsignedByte();
				if (anInt855 == 1)
					anInt1222 = inStream.readUnsignedWord();
				if (anInt855 >= 2 && anInt855 <= 6) {
					if (anInt855 == 2) {
						anInt937 = 64;
						anInt938 = 64;
					}
					if (anInt855 == 3) {
						anInt937 = 0;
						anInt938 = 64;
					}
					if (anInt855 == 4) {
						anInt937 = 128;
						anInt938 = 64;
					}
					if (anInt855 == 5) {
						anInt937 = 64;
						anInt938 = 0;
					}
					if (anInt855 == 6) {
						anInt937 = 64;
						anInt938 = 128;
					}
					anInt855 = 2;
					anInt934 = inStream.readUnsignedWord();
					anInt935 = inStream.readUnsignedWord();
					anInt936 = inStream.readUnsignedByte();
				}
				if (anInt855 == 10)
					anInt933 = inStream.readUnsignedWord();
				opCode = -1;
				return true;

			case 248:
				int i5 = inStream.method435();
				int k12 = inStream.readUnsignedWord();
				if (backDialogID != -1) {
					backDialogID = -1;
					inputTaken = true;
				}
				if (inputDialogState != 0) {
					inputDialogState = 0;
					inputTaken = true;
				}
				openInterfaceID = i5;
				invOverlayInterfaceID = k12;
				needDrawTabArea = true;
				tabAreaAltered = true;
				aBoolean1149 = false;
				opCode = -1;
				return true;

			case 79:
				int j5 = inStream.method434();
				int l12 = inStream.method435();
				RSInterface class9_3 = RSInterface.interfaceCache[j5];
				if (class9_3 != null && class9_3.type == 0) {
					if (l12 < 0)
						l12 = 0;
					if (l12 > class9_3.scrollMax - class9_3.height)
						l12 = class9_3.scrollMax - class9_3.height;
					class9_3.scrollPosition = l12;
				}
				opCode = -1;
				return true;

			case 68:
				for (int k5 = 0; k5 < variousSettings.length; k5++)
					if (variousSettings[k5] != anIntArray1045[k5]) {
						variousSettings[k5] = anIntArray1045[k5];
						handleActions(k5);
						needDrawTabArea = true;
					}
				opCode = -1;
				return true;

			case 196:
				long l5 = inStream.readQWord();
				inStream.readDWord();
				int playerRights = inStream.readUnsignedByte();
				boolean flag5 = false;
				if (playerRights <= 1) {
					for (int l29 = 0; l29 < ignoreCount; l29++) {
						if (ignoreListAsLongs[l29] != l5)
							continue;
						flag5 = true;

					}
				}
				if (!flag5 && anInt1251 == 0)
					try {
						String message = TextInput.method525(pktSize - 13,
								inStream);
						if (playerRights != 0) {
							pushMessage(
									message,
									7,
									getPrefix(playerRights)
									+ TextClass.fixName(TextClass
											.nameForLong(l5)));
						} else {
							pushMessage(message, 3, TextClass.fixName(TextClass
									.nameForLong(l5)));
						}
					} catch (Exception exception1) {
						signlink.reporterror("cde1");
					}
				opCode = -1;
				return true;
			case 85:
				anInt1269 = inStream.method427();
				anInt1268 = inStream.method427();
				opCode = -1;
				return true;

			case 24:
				anInt1054 = inStream.method428();
				if (anInt1054 == tabID) {
					if (anInt1054 == 3)
						tabID = 1;
					else
						tabID = 3;
					needDrawTabArea = true;
				}
				opCode = -1;
				return true;

			case 246:
				int i6 = inStream.method434();
				int i13 = inStream.readUnsignedWord();
				int k18 = inStream.readUnsignedWord();
				if (k18 == 65535) {
					RSInterface.interfaceCache[i6].anInt233 = 0;
					opCode = -1;
					return true;
				} else {
					ItemDef itemDef = ItemDef.forID(k18);
					RSInterface.interfaceCache[i6].anInt233 = 4;
					RSInterface.interfaceCache[i6].mediaID = k18;
					RSInterface.interfaceCache[i6].modelRotation1 = itemDef.modelRotation1;
					RSInterface.interfaceCache[i6].modelRotation2 = itemDef.modelRotation2;
					RSInterface.interfaceCache[i6].modelZoom = (itemDef.modelZoom * 100)
							/ i13;
					opCode = -1;
					return true;
				}

			case 171:
				boolean flag1 = inStream.readUnsignedByte() == 1;
				int j13 = inStream.readUnsignedWord();
				RSInterface.interfaceCache[j13].interfaceShown = flag1;
				opCode = -1;
				return true;

			case 142:
				int j6 = inStream.method434();
				method60(j6);
				if (backDialogID != -1) {
					backDialogID = -1;
					inputTaken = true;
				}
				if (inputDialogState != 0) {
					inputDialogState = 0;
					inputTaken = true;
				}
				invOverlayInterfaceID = j6;
				needDrawTabArea = true;
				tabAreaAltered = true;
				openInterfaceID = -1;
				aBoolean1149 = false;
				opCode = -1;
				return true;

			case 126:

				String text = inStream.readString();
				int frame = inStream.method435();
				if (text.startsWith("http://")) {
					launchURL(text);
					opCode = -1;
					return true;
				}
				if (text.startsWith("[REG]")) {
					text = text.substring(4);
					RSInterface.interfaceCache[frame + 20000].sprite1 = RSInterface.interfaceCache[frame + 20000].savedSprite[0];
					updateStrings(text, frame);
					sendFrame126(text, frame);
					opCode = -1;
					return true;
				}
				if (text.startsWith("[OWN]")) {
					text = text.substring(4);
					RSInterface.interfaceCache[frame + 20000].sprite1 = RSInterface.interfaceCache[frame + 20000].savedSprite[1];
					updateStrings(text, frame);
					sendFrame126(text, frame);
					opCode = -1;
					return true;
				}
				if (text.startsWith("[MOD]")) {
					text = text.substring(4);
					RSInterface.interfaceCache[frame + 20000].sprite1 = RSInterface.interfaceCache[frame + 20000].savedSprite[2];
					updateStrings(text, frame);
					sendFrame126(text, frame);
					opCode = -1;
					return true;
				}
				if (text.startsWith("[REC]")) {
					text = text.substring(4);
					RSInterface.interfaceCache[frame + 20000].sprite1 = RSInterface.interfaceCache[frame + 20000].savedSprite[3];
					updateStrings(text, frame);
					sendFrame126(text, frame);
					opCode = -1;
					return true;
				}
				if (text.startsWith("[COR]")) {
					text = text.substring(4);
					RSInterface.interfaceCache[frame + 20000].sprite1 = RSInterface.interfaceCache[frame + 20000].savedSprite[4];
					updateStrings(text, frame);
					sendFrame126(text, frame);
					opCode = -1;
					return true;
				}
				if (text.startsWith("[SER]")) {
					text = text.substring(4);
					RSInterface.interfaceCache[frame + 20000].sprite1 = RSInterface.interfaceCache[frame + 20000].savedSprite[5];
					updateStrings(text, frame);
					sendFrame126(text, frame);
					opCode = -1;
					return true;
				}
				if (text.startsWith("[LIE]")) {
					text = text.substring(4);
					RSInterface.interfaceCache[frame + 20000].sprite1 = RSInterface.interfaceCache[frame + 20000].savedSprite[6];
					updateStrings(text, frame);
					sendFrame126(text, frame);
					opCode = -1;
					return true;
				}
				if (text.startsWith("[BER]")) {
					text = text.substring(4);
					RSInterface.interfaceCache[frame + 20000].sprite1 = RSInterface.interfaceCache[frame + 20000].savedSprite[7];
					updateStrings(text, frame);
					sendFrame126(text, frame);
					opCode = -1;
					return true;
				}
				if (text.startsWith("[VR]")) {
					text = text.substring(3);
					RSInterface.interfaceCache[frame + 20000].sprite1 = RSInterface.interfaceCache[frame + 20000].savedSprite[8];
					updateStrings(text, frame);
					sendFrame126(text, frame);
					opCode = -1;
					return true;
				}
				if (text.startsWith("[FRI]")) {
					text = text.substring(4);
					RSInterface.interfaceCache[frame + 20000].sprite1 = RSInterface.interfaceCache[frame + 20000].savedSprite[9];
					updateStrings(text, frame);
					sendFrame126(text, frame);
					opCode = -1;
					return true;
				}
				if (frame == 47997) {
					Recruits = "";
					Recruits = text;
					opCode = -1;
					return true;
				}
				if (frame == 47996) {
					Corporals = "";
					Corporals = text;
					opCode = -1;
					return true;
				}
				if (frame == 47995) {
					Sergeants = "";
					Sergeants = text;
					opCode = -1;
					return true;
				}
				if (frame == 47994) {
					Lieutenants = "";
					Lieutenants = text;
					opCode = -1;
					return true;
				}
				if (frame == 47993) {
					Captains = "";
					Captains = text;
					opCode = -1;
					return true;
				}
				if (frame == 47992) {
					Generals = "";
					Generals = text;
					opCode = -1;
					return true;
				}
				if (text.startsWith("[UPDATE]")) {
					slot = 44001;
					for (int a = 0; a < friendsCount; a++) {
						if (isRecruit("" + friendsList[a] + ""))
							sendFrame126("Recruit", slot + 800);
						else if (isCorporal("" + friendsList[a] + ""))
							sendFrame126("Corporal", slot + 800);
						else if (isSergeant("" + friendsList[a] + ""))
							sendFrame126("Sergeant", slot + 800);
						else if (isLieutenant("" + friendsList[a] + ""))
							sendFrame126("Lieutenant", slot + 800);
						else if (isCaptain("" + friendsList[a] + ""))
							sendFrame126("Captain", slot + 800);
						else if (isGeneral("" + friendsList[a] + ""))
							sendFrame126("General", slot + 800);
						else
							sendFrame126("Not ranked", slot + 800);
						sendFrame126(friendsList[a], slot);
						slot++;
					}
					opCode = -1;
					return true;
				}
				if (text.startsWith("[FI]")) {
					text = text.substring(4);
					otherPlayerId = Integer.parseInt(text);
					opCode = -1;
					return true;
				}
				updateStrings(text, frame);
				sendFrame126(text, frame);
				if (frame >= 18144 && frame <= 18244) {
					clanList[frame - 18144] = text;
				}
				opCode = -1;
				return true;

			case 206:
				publicChatMode = inStream.readUnsignedByte();
				privateChatMode = inStream.readUnsignedByte();
				tradeMode = inStream.readUnsignedByte();
				inputTaken = true;
				opCode = -1;
				return true;

			case 240:
				if (tabID == 12)
					needDrawTabArea = true;
				weight = inStream.readSignedWord();
				opCode = -1;
				return true;

			case 8:
				int k6 = inStream.method436();
				int l13 = inStream.readUnsignedWord();
				RSInterface.interfaceCache[k6].anInt233 = 1;
				RSInterface.interfaceCache[k6].mediaID = l13;
				opCode = -1;
				return true;

			case 122:
				int l6 = inStream.method436();
				int i14 = inStream.method436();
				int i19 = i14 >> 10 & 0x1f;
				int i22 = i14 >> 5 & 0x1f;
				int l24 = i14 & 0x1f;
				RSInterface.interfaceCache[l6].textColor = (i19 << 19)
						+ (i22 << 11) + (l24 << 3);
				opCode = -1;
				return true;

				case 53:
					needDrawTabArea = true;
					int i7 = inStream.readUnsignedWord();
					RSInterface class9_1 = RSInterface.interfaceCache[i7];
					int j19 = inStream.readUnsignedWord();
					for (int j22 = 0; j22 < j19; j22++) {
						int i25 = inStream.readUnsignedByte();
						if (i25 == 255)
							i25 = inStream.method440();
						class9_1.inv[j22] = inStream.method436();
						class9_1.invStackSizes[j22] = i25;
					}
					for (int j25 = j19; j25 < class9_1.inv.length; j25++) {
						class9_1.inv[j25] = 0;
						class9_1.invStackSizes[j25] = 0;
					}
					opCode = -1;
					return true;

				case 230:
					int j7 = inStream.method435();
					int j14 = inStream.readUnsignedWord();
					int k19 = inStream.readUnsignedWord();
					int k22 = inStream.method436();
					RSInterface.interfaceCache[j14].modelRotation1 = k19;
					RSInterface.interfaceCache[j14].modelRotation2 = k22;
					RSInterface.interfaceCache[j14].modelZoom = j7;
					opCode = -1;
					return true;

				case 221:
					anInt900 = inStream.readUnsignedByte();
					needDrawTabArea = true;
					opCode = -1;
					return true;

				case 177:
					aBoolean1160 = true;
					anInt995 = inStream.readUnsignedByte();
					anInt996 = inStream.readUnsignedByte();
					anInt997 = inStream.readUnsignedWord();
					anInt998 = inStream.readUnsignedByte();
					anInt999 = inStream.readUnsignedByte();
					if (anInt999 >= 100) {
						int k7 = anInt995 * 128 + 64;
						int k14 = anInt996 * 128 + 64;
						int i20 = method42(plane, k14, k7) - anInt997;
						int l22 = k7 - xCameraPos;
						int k25 = i20 - zCameraPos;
						int j28 = k14 - yCameraPos;
						int i30 = (int) Math.sqrt(l22 * l22 + j28 * j28);
						yCameraCurve = (int) (Math.atan2(k25, i30) * 325.94900000000001D) & 0x7ff;
						xCameraCurve = (int) (Math.atan2(l22, j28) * -325.94900000000001D) & 0x7ff;
						if (yCameraCurve < 128)
							yCameraCurve = 128;
						if (yCameraCurve > 383)
							yCameraCurve = 383;
					}
					opCode = -1;
					return true;

				case 249:
					anInt1046 = inStream.method426();
					unknownInt10 = inStream.method436();
					opCode = -1;
					return true;

				case 65:
					updateNPCs(inStream, pktSize);
					opCode = -1;
					return true;

				case 27:
					showInput = false;
					inputDialogState = 1;
					amountOrNameInput = "";
					inputTaken = true;
					opCode = -1;
					return true;

				case 187:
					showInput = false;
					inputDialogState = 2;
					amountOrNameInput = "";
					inputTaken = true;
					opCode = -1;
					return true;

				case 97:
					try {
						int interfaceID = inStream.readUnsignedWord();
						method60(interfaceID);
						if (invOverlayInterfaceID != -1) {
							invOverlayInterfaceID = -1;
							needDrawTabArea = true;
							tabAreaAltered = true;
						}
						if (backDialogID != -1) {
							backDialogID = -1;
							inputTaken = true;
						}
						if (inputDialogState != 0) {
							inputDialogState = 0;
							inputTaken = true;
						}
						if (interfaceID == 24600) {
							RSInterface.interfaceCache[24654].sprite1 = RSInterface.interfaceCache[24654].savedFirstSprite;
							RSInterface.interfaceCache[24656].sprite1 = RSInterface.interfaceCache[24656].savedFirstSprite;
							buttonclicked = false;
							interfaceButtonAction = 0;
						}
						openInterfaceID = interfaceID;
						aBoolean1149 = false;
					} catch (Exception e) {
						e.printStackTrace();
					}
					opCode = -1;
					return true;

				case 218:
					int i8 = inStream.method438();
					dialogID = i8;
					inputTaken = true;
					opCode = -1;
					return true;

				case 87:
					int j8 = inStream.method434();
					int l14 = inStream.method439();
					anIntArray1045[j8] = l14;
					if (variousSettings[j8] != l14) {
						variousSettings[j8] = l14;
						handleActions(j8);
						needDrawTabArea = true;
						if (dialogID != -1)
							inputTaken = true;
					}
					opCode = -1;
					return true;

				case 36:
					int k8 = inStream.method434();
					byte byte0 = inStream.readSignedByte();
					anIntArray1045[k8] = byte0;
					if (variousSettings[k8] != byte0) {
						variousSettings[k8] = byte0;
						handleActions(k8);
						needDrawTabArea = true;
						if (dialogID != -1)
							inputTaken = true;
					}
					opCode = -1;
					return true;

				case 61:
					anInt1055 = inStream.readUnsignedByte();
					opCode = -1;
					return true;

				case 200:
					int l8 = inStream.readUnsignedWord();
					int i15 = inStream.readSignedWord();
					RSInterface class9_4 = RSInterface.interfaceCache[l8];
					class9_4.anInt257 = i15;
					if (i15 == -1) {
						class9_4.anInt246 = 0;
						class9_4.anInt208 = 0;
					}
					class9_4.modelZoom = 1600;
					opCode = -1;
					return true;

				case 219:
					if (invOverlayInterfaceID != -1) {
						invOverlayInterfaceID = -1;
						needDrawTabArea = true;
						tabAreaAltered = true;
					}
					if (backDialogID != -1) {
						backDialogID = -1;
						inputTaken = true;
					}
					if (inputDialogState != 0) {
						inputDialogState = 0;
						inputTaken = true;
					}
					openInterfaceID = -1;
					aBoolean1149 = false;
					opCode = -1;
					return true;

				case 34:
					needDrawTabArea = true;
					int i9 = inStream.readUnsignedWord();
					RSInterface class9_2 = RSInterface.interfaceCache[i9];
					while (inStream.currentOffset < pktSize) {
						int j20 = inStream.method422();
						int i23 = inStream.readUnsignedWord();
						int l25 = inStream.readUnsignedByte();
						if (l25 == 255)
							l25 = inStream.readDWord();
						if (j20 >= 0 && j20 < class9_2.inv.length) {
							class9_2.inv[j20] = i23;
							class9_2.invStackSizes[j20] = l25;
						}
					}
					opCode = -1;
					return true;

				case 4:
				case 44:
				case 84:
				case 101:
				case 105:
				case 117:
				case 147:
				case 151:
				case 156:
				case 160:
				case 215:
					method137(inStream, opCode);
					opCode = -1;
					return true;

				case 106:
					tabID = inStream.method427();
					needDrawTabArea = true;
					tabAreaAltered = true;
					opCode = -1;
					return true;

				case 164:
					int j9 = inStream.method434();
					method60(j9);
					if (invOverlayInterfaceID != -1) {
						invOverlayInterfaceID = -1;
						needDrawTabArea = true;
						tabAreaAltered = true;
					}
					backDialogID = j9;
					inputTaken = true;
					openInterfaceID = -1;
					aBoolean1149 = false;
					opCode = -1;
					return true;

			}
			signlink.reporterror("T1 - " + opCode + "," + pktSize + " - "
					+ anInt842 + "," + anInt843);
			// resetLogout();
		} catch (IOException _ex) {
			try {
				dropClient();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (UnsupportedLookAndFeelException e) {
				e.printStackTrace();
			}
		} catch (Exception exception) {
			String s2 = "T2 - " + opCode + "," + anInt842 + "," + anInt843
					+ " - " + pktSize + "," + (baseX + myPlayer.smallX[0])
					+ "," + (baseY + myPlayer.smallY[0]) + " - ";
			for (int j15 = 0; j15 < pktSize && j15 < 50; j15++)
				s2 = s2 + inStream.buffer[j15] + ",";
			signlink.reporterror(s2);
			// resetLogout();
		}
		opCode = -1;
		return true;
	}

	private void method146() {
		anInt1265++;
		int j = 0;
		int l = xCameraPos;
		int i1 = zCameraPos;
		int j1 = yCameraPos;
		int k1 = yCameraCurve;
		int l1 = xCameraCurve;
		if (loggedIn) {
			method47(true);
			method26(true);
			method47(false);
			method26(false);
			method55();
			method104();
			if (!aBoolean1160) {
				int i = anInt1184;
				if (anInt984 / 256 > i)
					i = anInt984 / 256;
				if (aBooleanArray876[4] && anIntArray1203[4] + 128 > i)
					i = anIntArray1203[4] + 128;
				int k = viewRotation + viewRotationOffset & 0x7ff;
				int zoom = (600 + (i * clientHeight / 400));
				setCameraPos(clientSize == 0 ? (600 + i * 3) : zoom, i,
						anInt1014,
						method42(plane, myPlayer.y, myPlayer.x) - 50, k,
						anInt1015);
			}
			if (!aBoolean1160)
				j = method120();
			else
				j = method121();
			for (int i2 = 0; i2 < 5; i2++)
				if (aBooleanArray876[i2]) {
					int j2 = (int) ((Math.random()
							* (double) (anIntArray873[i2] * 2 + 1) - (double) anIntArray873[i2]) + Math
							.sin((double) anIntArray1030[i2]
									* ((double) anIntArray928[i2] / 100D))
									* (double) anIntArray1203[i2]);
					if (i2 == 0)
						xCameraPos += j2;
					if (i2 == 1)
						zCameraPos += j2;
					if (i2 == 2)
						yCameraPos += j2;
					if (i2 == 3)
						xCameraCurve = xCameraCurve + j2 & 0x7ff;
					if (i2 == 4) {
						yCameraCurve += j2;
						if (yCameraCurve < 128)
							yCameraCurve = 128;
						if (yCameraCurve > 383)
							yCameraCurve = 383;
					}
				}
		}
		int k2 = Texture.textureGetCount;
		Model.aBoolean1684 = true;
		Model.anInt1687 = 0;
		Model.anInt1685 = super.mouseX - 4;
		Model.anInt1686 = super.mouseY - 4;
		DrawingArea.setAllPixelsToZero();
		if (Configuration.fog) {
			if (loggedIn)
				DrawingArea.drawPixels(clientHeight, 0, 0, 0xC8C0A8,
						clientWidth);
		}
		if (loggedIn) {
			worldController.method313(xCameraPos, yCameraPos, xCameraCurve,
					zCameraPos, j, yCameraCurve);
			worldController.clearObj5Cache();
		}
		updateEntities();
		drawHeadIcon();
		animateTexture(k2);
		if (drawPane)
			drawBlackPane();
		if (loggedIn) {
			drawUnfixedGame();
			draw3dScreen();
		}
		if (showXP && loggedIn) {
			displayXPCounter();
		}
		if (loggedIn) {
			gameScreenIP.drawGraphics(clientSize == 0 ? 4 : 0, super.graphics,
					clientSize == 0 ? 4 : 0);
			xCameraPos = l;
			zCameraPos = i1;
			yCameraPos = j1;
			yCameraCurve = k1;
			xCameraCurve = l1;
		}

	}

	public void setNorth() {
		cameraOffsetX = 0;
		cameraOffsetY = 0;
		viewRotationOffset = 0;
		viewRotation = 0;
		minimapRotation = 0;
		minimapZoom = 0;
	}

	public void clearTopInterfaces() {
		stream.createFrame(130);
		if (invOverlayInterfaceID != -1) {
			invOverlayInterfaceID = -1;
			needDrawTabArea = true;
			aBoolean1149 = false;
			tabAreaAltered = true;
		}
		if (backDialogID != -1) {
			backDialogID = -1;
			inputTaken = true;
			aBoolean1149 = false;
		}
		openInterfaceID = -1;
		fullscreenInterfaceID = -1;
	}

	public Client() {
		xpLock = false;
		xpCounter = 0;
		familiarHandler = new FamiliarHandler();
		register = new Creation(this);
		choosingLeftClick = false;
		leftClick = -1;
		fullscreenInterfaceID = -1;
		chatRights = new int[500];
		displayChat = true;
		chatTypeView = 0;
		clanChatMode = 0;
		cButtonHPos = -1;
		cButtonCPos = 0;
		anIntArrayArray825 = new int[104][104];
		friendsNodeIDs = new int[200];
		groundArray = new Deque[4][104][104];
		aBoolean831 = false;
		aStream_834 = new Stream(new byte[5000]);
		npcArray = new NPC[50000];
		npcIndices = new int[50000];
		anIntArray840 = new int[1000];
		aStream_847 = Stream.create();
		aBoolean848 = true;
		openInterfaceID = -1;
		currentExp = new int[Skills.skillsCount];
		aBoolean872 = false;
		anIntArray873 = new int[5];
		aBooleanArray876 = new boolean[5];
		drawFlames = false;
		reportAbuseInput = "";
		unknownInt10 = -1;
		menuOpen = false;
		inputString = "";
		maxPlayers = 2048;
		myPlayerIndex = 2047;
		playerArray = new Player[maxPlayers];
		playerIndices = new int[maxPlayers];
		anIntArray894 = new int[maxPlayers];
		aStreamArray895s = new Stream[maxPlayers];
		anInt897 = 1;
		anIntArrayArray901 = new int[104][104];
		aByteArray912 = new byte[16384];
		currentStats = new int[Skills.skillsCount];
		ignoreListAsLongs = new long[100];
		loadingError = false;
		anIntArray928 = new int[5];
		anIntArrayArray929 = new int[104][104];
		chatTypes = new int[500];
		chatNames = new String[500];
		chatMessages = new String[500];
		backButton = new Sprite[2];
		titleBox = new Sprite[17];
		loadCircle = new Sprite[8];
		sideIcons = new Sprite[15];
		aBoolean954 = true;
		friendsListAsLongs = new long[200];
		currentSong = -1;
		drawingFlames = false;
		spriteDrawX = -1;
		spriteDrawY = -1;
		anIntArray968 = new int[33];
		cacheIndices = new Decompressor[5];
		variousSettings = new int[2000];
		aBoolean972 = false;
		anInt975 = 50;
		anIntArray976 = new int[anInt975];
		anIntArray977 = new int[anInt975];
		anIntArray978 = new int[anInt975];
		anIntArray979 = new int[anInt975];
		anIntArray980 = new int[anInt975];
		anIntArray981 = new int[anInt975];
		anIntArray982 = new int[anInt975];
		aStringArray983 = new String[anInt975];
		anInt985 = -1;
		compass = new Sprite[2];
		hitMarks = new Sprite[20];
		hitMark = new Sprite[50];
		hitIcon = new Sprite[20];
		anIntArray990 = new int[5];
		scrollPart = new Sprite[12];
		scrollBar = new Sprite[6];
		aBoolean994 = false;
		amountOrNameInput = "";
		aClass19_1013 = new Deque();
		aBoolean1017 = false;
		anInt1018 = -1;
		anIntArray1030 = new int[5];
		aBoolean1031 = false;
		mapFunctions = new Sprite[100];
		dialogID = -1;
		maxStats = new int[Skills.skillsCount];
		anIntArray1045 = new int[2000];
		aBoolean1047 = true;
		anIntArray1052 = new int[152];
		anInt1054 = -1;
		aClass19_1056 = new Deque();
		anIntArray1057 = new int[33];
		aClass9_1059 = new RSInterface();
		mapScenes = new Background[100];
		anIntArray1065 = new int[7];
		anIntArray1072 = new int[1000];
		anIntArray1073 = new int[1000];
		aBoolean1080 = false;
		friendsList = new String[200];
		inStream = Stream.create();
		expectedCRCs = new int[9];
		menuActionCmd2 = new int[500];
		menuActionCmd3 = new int[500];
		menuActionCmd4 = new int[500];
		menuActionID = new int[500];
		menuActionCmd1 = new int[500];
		headIcons = new Sprite[20];
		skullIcons = new Sprite[20];
		headIconsHint = new Sprite[20];
		tabAreaAltered = false;
		promptMessage = "";
		atPlayerActions = new String[5];
		atPlayerArray = new boolean[5];
		anIntArrayArrayArray1129 = new int[4][13][13];
		anInt1132 = 2;
		aClass30_Sub2_Sub1_Sub1Array1140 = new Sprite[1000];
		aBoolean1141 = false;
		aBoolean1149 = false;
		crosses = new Sprite[8];
		musicEnabled = true;
		needDrawTabArea = false;
		loggedIn = false;
		canMute = false;
		aBoolean1159 = false;
		aBoolean1160 = false;
		anInt1171 = 1;
		myUsername = "";
		myPassword = "";
		genericLoadingError = false;
		reportAbuseInterfaceID = -1;
		aClass19_1179 = new Deque();
		anInt1184 = 128;
		invOverlayInterfaceID = -1;
		stream = Stream.create();
		menuActionName = new String[500];
		anIntArray1203 = new int[5];
		sound = new int[50];
		anInt1210 = 2;
		anInt1211 = 78;
		promptInput = "";
		modIcons = new Sprite[10];
		newCrowns = new Sprite[10];
		tabID = 3;
		inputTaken = false;
		songChanging = true;
		anIntArray1229 = new int[152];
		aClass11Array1230 = new Class11[4];
		soundType = new int[50];
		aBoolean1242 = false;
		soundDelay = new int[50];
		soundVolume = new int[50];
		rsAlreadyLoaded = false;
		welcomeScreenRaised = false;
		showInput = false;
		loginMessage1 = "";
		loginMessage2 = "";
		backDialogID = -1;
		anInt1279 = 2;
		bigX = new int[4000];
		bigY = new int[4000];
	}

	public int rights;
	public Sprite backgroundFix;
	public Sprite[] titleBox;
	private Sprite[] loadCircle;
	public String name;
	public String message;
	public String clanname;
	private final int[] chatRights;
	public int chatTypeView;
	public int clanChatMode;
	public int duelMode;
	/* Declare custom sprites */
	public Sprite[] backButton;
	private Sprite loadingPleaseWait;
	private Sprite reestablish;
	private Sprite HPBarFull, HPBarEmpty, HPBarBigEmpty;
	/**/
	private RSImageProducer leftFrame;
	private RSImageProducer topFrame;
	private RSImageProducer rightFrame;
	private int ignoreCount;
	private long aLong824;
	private int[][] anIntArrayArray825;
	private int[] friendsNodeIDs;
	private Deque[][][] groundArray;
	private volatile boolean aBoolean831;
	private Socket aSocket832;
	public int loginScreenState;
	public int previousScreenState;
	private Stream aStream_834;
	private NPC[] npcArray;
	private int npcCount;
	private int[] npcIndices;
	private int anInt839;
	private int[] anIntArray840;
	private int anInt841;
	private int anInt842;
	private int anInt843;
	private String aString844;
	private int privateChatMode;
	private int gameChatMode;
	private Stream aStream_847;
	private boolean aBoolean848;
	private static int anInt849;
	private int[] anIntArray851;
	private int[] anIntArray852;
	private int[] anIntArray853;
	private static int anInt854;
	private int anInt855;
	public static int openInterfaceID;
	public int xCameraPos;
	public int zCameraPos;
	public int yCameraPos;
	public int yCameraCurve;
	public int xCameraCurve;
	private int myRights;
	private final int[] currentExp;
	private Sprite mapFlag;
	private Sprite mapMarker;
	private boolean aBoolean872;
	private final int[] anIntArray873;
	private final boolean[] aBooleanArray876;
	private int weight;
	private MouseDetection mouseDetection;
	private volatile boolean drawFlames;
	private String reportAbuseInput;
	private int unknownInt10;
	private boolean menuOpen;
	private int hoveredInterface;
	private String inputString;
	private final int maxPlayers;
	private final int myPlayerIndex;
	private Player[] playerArray;
	private int playerCount;
	private int[] playerIndices;
	private int anInt893;
	private int[] anIntArray894;
	private Stream[] aStreamArray895s;
	private int viewRotationOffset;
	private int anInt897;
	private int friendsCount;
	private int anInt900;
	private int[][] anIntArrayArray901;
	private byte[] aByteArray912;
	private int anInt913;
	private int crossX;
	private int crossY;
	private int crossIndex;
	private int crossType;
	public int plane;
	private final int[] currentStats;
	private static int anInt924;
	private final long[] ignoreListAsLongs;
	private boolean loadingError;
	private final int[] anIntArray928;
	private int[][] anIntArrayArray929;
	private Sprite aClass30_Sub2_Sub1_Sub1_931;
	private Sprite aClass30_Sub2_Sub1_Sub1_932;
	private int anInt933;
	private int anInt934;
	private int anInt935;
	private int anInt936;
	private int anInt937;
	private int anInt938;
	private final int[] chatTypes;
	private final String[] chatNames;
	private String[] chatMessages;
	private int anInt945;
	private WorldController worldController;
	private Sprite[] sideIcons;
	private int menuScreenArea;
	private int menuOffsetX;
	private int menuOffsetY;
	private int menuWidth;
	private int menuHeight;
	private long aLong953;
	private boolean aBoolean954;
	private long[] friendsListAsLongs;
	private String[] clanList = new String[100];
	private int currentSong;
	private static int nodeID = 10;
	static int portOff;
	static boolean clientData;
	private static boolean isMembers = true;
	private static boolean lowMem;
	private volatile boolean drawingFlames;
	private int spriteDrawX;
	private int spriteDrawY;
	private final int[] anIntArray965 = { 0xffff00, 0xff0000, 65280, 65535,
			0xff00ff, 0xffffff };
	private final int[] anIntArray968;
	final Decompressor[] cacheIndices;
	public int variousSettings[];
	private boolean aBoolean972;
	private final int anInt975;
	private final int[] anIntArray976;
	private final int[] anIntArray977;
	private final int[] anIntArray978;
	private final int[] anIntArray979;
	private final int[] anIntArray980;
	private final int[] anIntArray981;
	private final int[] anIntArray982;
	private final String[] aStringArray983;
	private int anInt984;
	private int anInt985;
	private static int anInt986;
	private Sprite[] hitMark;
	private Sprite[] hitIcon;
	private Sprite[] hitMarks;
	private Sprite[] scrollBar;
	public Sprite[] scrollPart;
	private int anInt988;
	private int anInt989;
	private final int[] anIntArray990;
	private final boolean aBoolean994;
	private int anInt995;
	private int anInt996;
	private int anInt997;
	private int anInt998;
	private int anInt999;
	private ISAACRandomGen encryption;
	private Sprite mapEdge;
	private Sprite multiOverlay;
	static final int[][] anIntArrayArray1003 = new int[][] {
		{ 6798, 107, 10283, 16, 4797, 7744, 5799, 4634, 33697, 22433, 2983,
			54193 },
			{ 8741, 12, 64030, 43162, 7735, 8404, 1701, 38430, 24094, 10153,
				56621, 4783, 1341, 16578, 35003, 25239 },
				{ 25238, 8742, 12, 64030, 43162, 7735, 8404, 1701, 38430, 24094,
					10153, 56621, 4783, 1341, 16578, 35003 },
					{ 4626, 11146, 6439, 12, 4758, 10270 },
					{ 4550, 4537, 5681, 5673, 5790, 6806, 8076, 4574 } };
	private String amountOrNameInput;
	private static int anInt1005;
	private int daysSinceLastLogin;
	private int pktSize;
	private int opCode;
	private int anInt1009;
	private int anInt1010;
	private int anInt1011;
	private Deque aClass19_1013;
	private int anInt1014;
	private int anInt1015;
	private int anInt1016;
	private boolean aBoolean1017;
	private int anInt1018;
	private static final int[] anIntArray1019;
	private int anInt1021;
	private int anInt1022;
	private int loadingStage;
	private int anInt1026;
	private final int[] anIntArray1030;
	private boolean aBoolean1031;
	private Sprite[] mapFunctions;
	private int baseX;
	private int baseY;
	private int anInt1036;
	private int anInt1037;
	private int anInt1039;
	private int dialogID;
	private final int[] maxStats;
	private final int[] anIntArray1045;
	private int anInt1046;
	private boolean aBoolean1047;
	private int anInt1048;
	private String aString1049;
	private static int anInt1051;
	private final int[] anIntArray1052;
	private StreamLoader titleStreamLoader;
	private int anInt1054;
	private int anInt1055;
	private Deque aClass19_1056;
	private final int[] anIntArray1057;
	public final RSInterface aClass9_1059;
	private Background[] mapScenes;
	private static int anInt1061;
	private int currentSound;
	private int friendsListAction;
	private final int[] anIntArray1065;
	private int mouseInvInterfaceIndex;
	private int lastActiveInvInterface;
	public OnDemandFetcher onDemandFetcher;
	private int anInt1069;
	private int anInt1070;
	private int anInt1071;
	private int[] anIntArray1072;
	private int[] anIntArray1073;
	private Sprite mapDotItem;
	private Sprite mapDotNPC;
	private Sprite mapDotPlayer;
	private Sprite mapDotFriend;
	private Sprite mapDotTeam;
	private Sprite mapDotClan;
	private boolean aBoolean1080;
	private String[] friendsList;
	private Stream inStream;
	private int anInt1084;
	private int anInt1085;
	private int activeInterfaceType;
	private int anInt1087;
	private int anInt1088;
	public static int anInt1089;
	private final int[] expectedCRCs;
	private int[] menuActionCmd2;
	private int[] menuActionCmd3;
	public int[] menuActionCmd4;
	private int[] menuActionID;
	private int[] menuActionCmd1;
	private Sprite[] headIcons;
	private Sprite[] skullIcons;
	private Sprite[] headIconsHint;
	private static int anInt1097;
	private int anInt1098;
	private int anInt1099;
	private int anInt1100;
	private int anInt1101;
	private int anInt1102;
	public static boolean tabAreaAltered;
	private int anInt1104;
	public static FamiliarHandler familiarHandler;
	public Creation register;
	private RSImageProducer GraphicsBuffer_1107;
	private RSImageProducer titleScreen;
	private RSImageProducer GraphicsBuffer_1110;
	private RSImageProducer GraphicsBuffer_1111;
	private static int anInt1117;
	private int membersInt;
	private String promptMessage;
	private Sprite[] compass;
	private RSImageProducer GraphicsBuffer_1125;
	public static Player myPlayer;
	private final String[] atPlayerActions;
	private final boolean[] atPlayerArray;
	private final int[][][] anIntArrayArrayArray1129;
	public final int[] tabInterfaceIDs = { -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, -1, -1, -1, -1, -1, -1, -1 };
	private int cameraOffsetY;
	private int anInt1132;
	private int menuActionRow;
	private static int anInt1134;
	private int spellSelected;
	private int anInt1137;
	private int spellUsableOn;
	private String spellTooltip;
	private Sprite[] aClass30_Sub2_Sub1_Sub1Array1140;
	private boolean aBoolean1141;
	private static int anInt1142;
	private boolean aBoolean1149;
	private Sprite[] crosses;
	private boolean musicEnabled;
	private Background[] aBackgroundArray1152s;
	public static boolean needDrawTabArea;
	private int unreadMessages;
	private static int anInt1155;
	private static boolean fpsOn;
	public boolean loggedIn;
	private boolean canMute;
	private boolean aBoolean1159;
	private boolean aBoolean1160;
	public static int loopCycle;
	public static final String validUserPassChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!\"\243$%^&*()-_=+[{]};:'@#~,<.>/?\\| ";
	private RSImageProducer tabAreaIP;
	private RSImageProducer mapAreaIP;
	private RSImageProducer gameScreenIP;
	private RSImageProducer chatAreaIP;
	private int daysSinceRecovChange;
	private RSSocket socketStream;
	private int minimapZoom;
	private int anInt1171;
	private long aLong1172;
	public String myUsername;
	private String myPassword;
	private static int anInt1175;
	private boolean genericLoadingError;
	private final int[] anIntArray1177 = { 0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2,
			2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3 };
	private int reportAbuseInterfaceID;
	private Deque aClass19_1179;
	private int[] anIntArray1180;
	private int[] anIntArray1181;
	private int[] anIntArray1182;
	public Sprite[] cacheSprite;
	private byte[][] aByteArrayArray1183;
	private int anInt1184;
	private int viewRotation;
	private int anInt1186;
	private int anInt1187;
	private static int anInt1188;
	private int invOverlayInterfaceID;
	private int[] anIntArray1190;
	private int[] anIntArray1191;
	static Stream stream;
	private int anInt1193;
	private int splitPrivateChat;
	private Background mapBack;
	/* Gameframe update */
	public Sprite newMapBack;
	private String[] menuActionName;
	private Sprite aClass30_Sub2_Sub1_Sub1_1201;
	private Sprite aClass30_Sub2_Sub1_Sub1_1202;
	private final int[] anIntArray1203;
	static final int[] anIntArray1204 = { 9104, 10275, 7595, 3610, 7975, 8526,
		918, 38802, 24466, 10145, 58654, 5027, 1457, 16565, 34991, 25486 };
	private static boolean flagged;
	private final int[] sound;
	private int anInt1208;
	private int minimapRotation;
	private int anInt1210;
	public static int anInt1211;
	private String promptInput;
	private int anInt1213;
	private int[][][] intGroundArray;
	private long aLong1215;
	public int loginScreenCursorPos;
	private final Sprite[] modIcons;
	private Sprite[] newCrowns;
	private long aLong1220;
	public static int tabID;
	private int anInt1222;
	public static boolean inputTaken;
	private int inputDialogState;
	private static int anInt1226;
	private int nextSong;
	private boolean songChanging;
	private final int[] anIntArray1229;
	private Class11[] aClass11Array1230;
	public static int anIntArray1232[];
	private int[] anIntArray1234;
	private int[] anIntArray1235;
	private int[] anIntArray1236;
	private int anInt1237;
	private int anInt1238;
	public final int anInt1239 = 100;
	private final int[] soundType;
	private boolean aBoolean1242;
	private int atInventoryLoopCycle;
	private int atInventoryInterface;
	private int atInventoryIndex;
	private int atInventoryInterfaceType;
	private byte[][] aByteArrayArray1247;
	private int tradeMode;
	private int anInt1249;
	private final int[] soundDelay;
	private final int[] soundVolume;
	private int anInt1251;
	private final boolean rsAlreadyLoaded;
	private int anInt1253;
	private int anInt1254;
	private boolean welcomeScreenRaised;
	private boolean showInput;
	private int anInt1257;
	private byte[][][] byteGroundArray;
	private int prevSong;
	private int destX;
	private int destY;
	private Sprite miniMap;
	private int anInt1264;
	private int anInt1265;
	private String loginMessage1;
	private String loginMessage2;
	private int anInt1268;
	private int anInt1269;
	public RSFontSystem newSmallFont, newRegularFont, newBoldFont,
	newFancyFont, regularHitFont, bigHitFont;
	public static TextDrawingArea normalFont;
	public static TextDrawingArea boldFont;
	public static TextDrawingArea fancyText;
	public TextDrawingArea smallText;
	private TextDrawingArea smallHit;
	private TextDrawingArea bigHit;
	public TextDrawingArea aTextDrawingArea_1271;
	private TextDrawingArea chatTextDrawingArea;
	private int backDialogID;
	private int cameraOffsetX;
	private int anInt1279;
	private int[] bigX;
	private int[] bigY;
	private int itemSelected;
	private int anInt1283;
	private int anInt1284;
	private int anInt1285;
	private String selectedItemName;
	private int publicChatMode;
	private static int anInt1288;
	public static int anInt1290;
	public int drawCount;
	public int fullscreenInterfaceID;
	public int anInt1044;
	public int anInt1129;
	public int anInt1315;
	public int anInt1500;
	public int anInt1501;
	public int[] fullScreenTextureArray;
	public String Recruits = "";
	public String Corporals = "";
	public String Sergeants = "";
	public String Lieutenants = "";
	public String Captains = "";
	public String Generals = "";
	public int Slots[] = new int[7];
	public String slots[] = new String[7];
	public int slotItemId[] = new int[7];
	public int slotColor[] = new int[7];
	public int slotColorPercent[] = new int[7];
	public boolean slotAborted[] = new boolean[7];
	public int slotUsing = 0;
	public int slotSelected;
	public Sprite Sell;
	public Sprite Buy;
	public Sprite SubmitBuy;
	public Sprite SubmitSell;

	public boolean isRecruit(String name) {
		name = name.toLowerCase();
		if (Recruits.contains(" " + name + ",")) {
			return true;
		}
		if (Recruits.contains(", " + name + "]")) {
			return true;
		}
		if (Recruits.contains("[" + name + ",")) {
			return true;
		}
		if (Recruits.contains("[" + name + "]")) {
			return true;
		}
		return false;
	}

	public boolean isCorporal(String name) {
		name = name.toLowerCase();
		if (Corporals.contains(" " + name + ",")) {
			return true;
		}
		if (Corporals.contains(", " + name + "]")) {
			return true;
		}
		if (Corporals.contains("[" + name + ",")) {
			return true;
		}
		if (Corporals.contains("[" + name + "]")) {
			return true;
		}
		return false;
	}

	public boolean isSergeant(String name) {
		name = name.toLowerCase();
		if (Sergeants.contains(" " + name + ",")) {
			return true;
		}
		if (Sergeants.contains(", " + name + "]")) {
			return true;
		}
		if (Sergeants.contains("[" + name + ",")) {
			return true;
		}
		if (Sergeants.contains("[" + name + "]")) {
			return true;
		}
		return false;
	}

	public boolean isLieutenant(String name) {
		name = name.toLowerCase();
		if (Lieutenants.contains(" " + name + ",")) {
			return true;
		}
		if (Lieutenants.contains(", " + name + "]")) {
			return true;
		}
		if (Lieutenants.contains("[" + name + ",")) {
			return true;
		}
		if (Lieutenants.contains("[" + name + "]")) {
			return true;
		}
		return false;
	}

	public boolean isCaptain(String name) {
		name = name.toLowerCase();
		if (Captains.contains(" " + name + ",")) {
			return true;
		}
		if (Captains.contains(", " + name + "]")) {
			return true;
		}
		if (Captains.contains("[" + name + ",")) {
			return true;
		}
		if (Captains.contains("[" + name + "]")) {
			return true;
		}
		return false;
	}

	public boolean isGeneral(String name) {
		name = name.toLowerCase();
		if (Generals.contains(" " + name + ",")) {
			return true;
		}
		if (Generals.contains(", " + name + "]")) {
			return true;
		}
		if (Generals.contains("[" + name + ",")) {
			return true;
		}
		if (Generals.contains("[" + name + "]")) {
			return true;
		}
		return false;
	}

	public void resetAllImageProducers() {
		if (super.fullGameScreen != null) {
			return;
		}
		chatAreaIP = null;
		mapAreaIP = null;
		tabAreaIP = null;
		gameScreenIP = null;
		GraphicsBuffer_1125 = null;
		GraphicsBuffer_1107 = null;
		titleScreen = null;
		GraphicsBuffer_1110 = null;
		GraphicsBuffer_1111 = null;
		super.fullGameScreen = new RSImageProducer(765, 503, getGameComponent());
		welcomeScreenRaised = true;
	}

	public String getRank(int i) {
		switch (i) {
		case 1:
			return "Lord";
		case 2:
			return "Sir";
		case 3:
			return "Lionheart";
		case 4:
			return "Desperado";
		case 5:
			return "Bandito";
		case 6:
			return "King";
		case 7:
			return "Big Cheese";
		case 8:
			return "Wunderkind";
		case 9:
			return "Crusader";
		case 10:
			return "Overlord";
		case 11:
			return "Bigwig";
		case 12:
			return "Count";
		case 13:
			return "Duderino";
		case 14:
			return "Hell Raiser";
		case 15:
			return "Baron";
		case 16:
			return "Duke";
		case 17:
			return "Lady";
		case 18:
			return "Dame";
		case 19:
			return "Dudette";
		case 20:
			return "Baroness";
		case 21:
			return "Countess";
		case 22:
			return "Overlordess";
		case 23:
			return "Duchess";
		case 24:
			return "Queen";
		case 25:
			return "Mod";
		case 26:
			return "Admin";
		case 27:
			return "Head Admin";
		case 28:
			return "Main Owner";
		case 29:
			return "Most Respected";
		case 30:
			return "Top Pker";
		case 31:
			return "Veteran";
		}
		return "";
	}

	@SuppressWarnings("resource")
	public boolean loadItemList(String FileName) {
		String line = "";
		String token = "";
		String token2 = "";
		String token2_2 = "";
		String[] token3 = new String[20];
		boolean EndOfFile = false;
		BufferedReader characterfile = null;
		try {
			characterfile = new BufferedReader(new FileReader("" + FileName));
		} catch (FileNotFoundException fileex) {
			return false;
		}
		try {
			line = characterfile.readLine();
		} catch (IOException ioexception) {
			return false;
		}
		while (EndOfFile == false && line != null) {
			line = line.trim();
			int spot = line.indexOf("=");
			if (spot > -1) {
				token = line.substring(0, spot);
				token = token.trim();
				token2 = line.substring(spot + 1);
				token2 = token2.trim();
				token2_2 = token2.replaceAll("\t\t", "\t");
				token2_2 = token2_2.replaceAll("\t\t", "\t");
				token2_2 = token2_2.replaceAll("\t\t", "\t");
				token2_2 = token2_2.replaceAll("\t\t", "\t");
				token2_2 = token2_2.replaceAll("\t\t", "\t");
				token3 = token2_2.split("\t");
				if (token.equals("item")) {
					int[] Bonuses = new int[12];
					for (int i = 0; i < 12; i++) {
						if (token3[(6 + i)] != null) {
							Bonuses[i] = Integer.parseInt(token3[(6 + i)]);
						} else {
							break;
						}
					}
					try {
						for (int i = 18; i < 21; i++) {
							if (token3[i] != null) {
								// soaking[i - 18] =
								// Double.parseDouble(token3[i]);
							} else {
								break;
							}
						}
					} catch (Exception _ex) {
					}
					newItemList(Integer.parseInt(token3[0]),
							token3[1].replaceAll("_", " "),
							token3[2].replaceAll("_", " "),
							Double.parseDouble(token3[4]));
				}
			} else {
				if (line.equals("[ENDOFITEMLIST]")) {
					try {
						characterfile.close();
					} catch (IOException ioexception) {
					}
					return true;
				}
			}
			try {
				line = characterfile.readLine();
			} catch (IOException ioexception1) {
				EndOfFile = true;
			}
		}
		try {
			characterfile.close();
		} catch (IOException ioexception) {
		}
		return false;
	}

	public ItemList ItemList[] = new ItemList[24470];

	public void newItemList(int ItemId, String ItemName,
			String ItemDescription, double ShopValue) {
		int slot = -1;
		for (int i = 0; i < 24470; i++) {
			if (ItemList[i] == null) {
				slot = i;
				break;
			}
		}
		if (slot == -1)
			return; // no free slot found
		ItemList newItemList = new ItemList(ItemId);
		newItemList.itemName = ItemName;
		newItemList.itemDescription = ItemDescription;
		newItemList.ShopValue = ShopValue;
		ItemList[slot] = newItemList;
	}

	public void drawGrandExchange() {
		if (openInterfaceID != 24500 && openInterfaceID != 54700
				&& openInterfaceID != 53700) {
			return;
		}
		if (openInterfaceID == 24500) {
			for (int i = 1; i < Slots.length; i++) {
				if (Slots[i] == 0) {
					drawUpdate(i, "Regular");
				}
				if (Slots[i] == 1 && slots[i] == "Sell") {
					drawUpdate(i, "Submit Sell");
				}
				if (Slots[i] == 1 && slots[i] == "Buy") {
					drawUpdate(i, "Submit Buy");
				}
				if (Slots[i] == 2 && slots[i] == "Sell") {
					drawUpdate(i, "Sell");
				}
				if (Slots[i] == 2 && slots[i] == "Buy") {
					drawUpdate(i, "Buy");
				}
				if (Slots[i] == 3 && slots[i] == "Sell") {
					drawUpdate(i, "Finished Selling");
				}
				if (Slots[i] == 3 && slots[i] == "Buy") {
					drawUpdate(i, "Finished Buying");
				}
			}
		}
		int x = 0;
		int y = 0;
		x = (clientSize == 0) ? 71 : (71 + (clientWidth / 2 - 256));
		y = (clientSize == 0) ? 303 : (clientHeight / 2 + 136);
		if (openInterfaceID == 54700) {
			per4 = new Sprite("Interfaces/GE/per 4");
			per5 = new Sprite("Interfaces/GE/per 5");
			per6 = new Sprite("Interfaces/GE/per 6");
			abort2 = new Sprite("Interfaces/GE/abort 2");
			if (slotColorPercent[slotSelected] == 100
					|| slotAborted[slotSelected]) {
				RSInterface.interfaceCache[54800].tooltip = "[GE]";
				RSInterface.interfaceCache[54800].sprite1 = RSInterface.interfaceCache[54800].setSprite;
				RSInterface.interfaceCache[54802].sprite1 = RSInterface.interfaceCache[54802].setSprite;
			} else {
				RSInterface.interfaceCache[54800].tooltip = "Abort offer";
				RSInterface.interfaceCache[54800].sprite1 = RSInterface.interfaceCache[53800].savedFirstSprite;
				RSInterface.interfaceCache[54802].sprite1 = RSInterface.interfaceCache[54802].savedFirstSprite;
			}
			if (slotSelected <= 6) {
				if (!slotAborted[slotSelected]) {
					for (int k9 = 1; k9 < slotColorPercent[slotSelected]; k9++) {
						if (slotColorPercent[slotSelected] > 0) {
							if (k9 == 1) {
								per4.drawSprite(x, y);
								x += 3;
							} else if (k9 == 99) {
								per6.drawSprite(x, y);
								x += 4;
							} else {
								per5.drawSprite(x, y);
								x += 3;
							}
						}
					}
				} else {
					abort2.drawSprite(x, y);
				}
			}
		}
		x = (clientSize == 0) ? 71 : (71 + (clientWidth / 2 - 256));
		y = (clientSize == 0) ? 303 : (clientHeight / 2 + 136);
		if (openInterfaceID == 53700) {
			per4 = new Sprite("Interfaces/GE/per 4");
			per5 = new Sprite("Interfaces/GE/per 5");
			per6 = new Sprite("Interfaces/GE/per 6");
			abort2 = new Sprite("Interfaces/GE/abort 2");
			if (slotColorPercent[slotSelected] == 100
					|| slotAborted[slotSelected]) {
				RSInterface.interfaceCache[53800].tooltip = "[GE]";
				RSInterface.interfaceCache[53800].sprite1 = RSInterface.interfaceCache[53800].setSprite;
				RSInterface.interfaceCache[53802].sprite1 = RSInterface.interfaceCache[53802].setSprite;
			} else {
				RSInterface.interfaceCache[53800].tooltip = "Abort offer";
				RSInterface.interfaceCache[53800].sprite1 = RSInterface.interfaceCache[53800].savedFirstSprite;
				RSInterface.interfaceCache[53802].sprite1 = RSInterface.interfaceCache[53802].savedFirstSprite;
			}
			if (slotSelected <= 6) {
				if (!slotAborted[slotSelected]) {
					for (int k9 = 1; k9 < slotColorPercent[slotSelected]; k9++) {
						if (slotColorPercent[slotSelected] > 0) {
							if (k9 == 1) {
								per4.drawSprite(x, y);
								x += 3;
							} else if (k9 == 99) {
								per6.drawSprite(x, y);
								x += 4;
							} else {
								per5.drawSprite(x, y);
								x += 3;
							}
						}
					}
				} else {
					abort2.drawSprite(x, y);
				}
			}
		}
	}

	public void drawUpdate(int id, String type) {
		int x = 0;
		int y = 0;
		int x2 = 0;
		int y2 = 0;
		int x3 = 0;
		int y3 = 0;
		boolean fixed = (clientSize == 0);
		switch (id) {
		case 1:
			x = fixed ? 30 : (clientWidth / 2 - 226);
			x2 = fixed ? 80 : (clientWidth / 2 - 226 + 50);
			x3 = fixed ? 40 : (clientWidth / 2 - 226 + 10);
			y = fixed ? 74 : (clientHeight / 2 - 93);
			y2 = fixed ? 136 : (clientHeight / 2 - 93 + 62);
			y3 = fixed ? 115 : (clientHeight / 2 - 93 + 41);
			break;
		case 2:
			x = fixed ? 186 : (clientWidth / 2 - 70);
			x2 = fixed ? 80 + 156 : (clientWidth / 2 - 70 + 50);
			x3 = fixed ? 40 + 156 : (clientWidth / 2 - 70 + 10);
			y = fixed ? 74 : (clientHeight / 2 - 93);
			y2 = fixed ? 136 : (clientHeight / 2 - 93 + 62);
			y3 = fixed ? 115 : (clientHeight / 2 - 93 + 41);
			break;
		case 3:
			x = fixed ? 342 : (clientWidth / 2 + 86);
			x2 = fixed ? 80 + 156 + 156 : (clientWidth / 2 + 86 + 50);
			x3 = fixed ? 40 + 156 + 156 : (clientWidth / 2 + 86 + 10);
			y = fixed ? 74 : (clientHeight / 2 - 93);
			y2 = fixed ? 136 : (clientHeight / 2 - 93 + 62);
			y3 = fixed ? 115 : (clientHeight / 2 - 93 + 41);
			break;
		case 4:
			x = fixed ? 30 : (clientWidth / 2 - 226);
			x2 = fixed ? 80 : (clientWidth / 2 - 226 + 50);
			x3 = fixed ? 40 : (clientWidth / 2 - 226 + 10);
			y = fixed ? 194 : (clientHeight / 2 + 27);
			y2 = fixed ? 256 : (clientHeight / 2 + 27 + 62);
			y3 = fixed ? 235 : (clientHeight / 2 + 27 + 41);
			break;
		case 5:
			x = fixed ? 186 : (clientWidth / 2 - 70);
			x2 = fixed ? 80 + 156 : (clientWidth / 2 - 70 + 50);
			x3 = fixed ? 40 + 156 : (clientWidth / 2 - 70 + 10);
			y = fixed ? 194 : (clientHeight / 2 + 27);
			y2 = fixed ? 256 : (clientHeight / 2 + 27 + 62);
			y3 = fixed ? 235 : (clientHeight / 2 + 27 + 41);
			break;
		case 6:
			x = fixed ? 342 : (clientWidth / 2 + 86);
			x2 = fixed ? 80 + 156 + 156 : (clientWidth / 2 + 86 + 50);
			x3 = fixed ? 40 + 156 + 156 : (clientWidth / 2 + 86 + 10);
			y = fixed ? 194 : (clientHeight / 2 + 27);
			y2 = fixed ? 256 : (clientHeight / 2 + 27 + 62);
			y3 = fixed ? 235 : (clientHeight / 2 + 27 + 41);
			break;
		}
		x -= 2;
		x2 -= 2;
		x3 -= 2;
		int minus = 20;
		if (type == "Sell") {
			if (super.mouseX >= x && super.mouseX <= x + 140
					&& super.mouseY >= y && super.mouseY <= y + 110
					&& !menuOpen) {
				SellHover = new Sprite("Interfaces/GE/sellHover");
				SellHover.drawSprite(x, y);
			} else {
				Sell.drawSprite(x, y);
			}
			setGrandExchange(id, false);
			if (slotAborted[id] || slotColorPercent[id] == 100) {
				changeSet(id, true, false);
			} else {
				changeSet(id, true, true);
			}
			drawPercentage(id);
			smallText.method592(0xCC9900, x2,
					RSInterface.interfaceCache[32000 + id].message, y2 - minus,
					true);
			smallText.method592(0xBDBB5B, x2,
					RSInterface.interfaceCache[33000 + id].message, y2, true);
			smallText.method592(0xFFFF00, x3,
					RSInterface.interfaceCache[33100 + id].message, y3, true);
			setHovers(id, false);
		} else if (type == "Buy") {
			if (super.mouseX >= x && super.mouseX <= x + 140
					&& super.mouseY >= y && super.mouseY <= y + 110
					&& !menuOpen) {
				BuyHover = new Sprite("Interfaces/GE/buyHover");
				BuyHover.drawSprite(x, y);
			} else {
				Buy.drawSprite(x, y);
			}
			setGrandExchange(id, false);
			if (slotAborted[id] || slotColorPercent[id] == 100) {
				changeSet(id, true, false);
			} else {
				changeSet(id, true, true);
			}
			drawPercentage(id);
			smallText.method592(0xCC9900, x2,
					RSInterface.interfaceCache[32000 + id].message, y2 - minus,
					true);
			smallText.method592(0xBDBB5B, x2,
					RSInterface.interfaceCache[33000 + id].message, y2, true);
			smallText.method592(0xFFFF00, x3,
					RSInterface.interfaceCache[33100 + id].message, y3, true);
			setHovers(id, false);
		} else if (type == "Submit Buy") {
			if (super.mouseX >= x && super.mouseX <= x + 140
					&& super.mouseY >= y && super.mouseY <= y + 110
					&& !menuOpen) {
				buySubmitHover = new Sprite("Interfaces/GE/buySubmitHover");
				buySubmitHover.drawSprite(x, y);
			} else {
				SubmitBuy.drawSprite(x, y);
			}
			setGrandExchange(id, false);
			changeSet(id, false, false);
			smallText.method592(0xCC9900, x2,
					RSInterface.interfaceCache[32000 + id].message, y2 - minus,
					true);
			smallText.method592(0xBDBB5B, x2,
					RSInterface.interfaceCache[33000 + id].message, y2, true);
			smallText.method592(0xFFFF00, x3,
					RSInterface.interfaceCache[33100 + id].message, y3, true);
			setHovers(id, false);
		} else if (type == "Submit Sell") {
			if (super.mouseX >= x && super.mouseX <= x + 140
					&& super.mouseY >= y && super.mouseY <= y + 110
					&& !menuOpen) {
				sellSubmitHover = new Sprite("Interfaces/GE/sellSubmitHover");
				sellSubmitHover.drawSprite(x, y);
			} else {
				SubmitSell.drawSprite(x, y);
			}
			setGrandExchange(id, false);
			changeSet(id, false, false);
			smallText.method592(0xCC9900, x2,
					RSInterface.interfaceCache[32000 + id].message, y2 - minus,
					true);
			smallText.method592(0xBDBB5B, x2,
					RSInterface.interfaceCache[33000 + id].message, y2, true);
			smallText.method592(0xFFFF00, x3,
					RSInterface.interfaceCache[33100 + id].message, y3, true);
			setHovers(id, false);
		} else if (type == "Regular") {
			setGrandExchange(id, true);
			setHovers(id, true);
		} else if (type == "Finished Selling") {
			if (super.mouseX >= x && super.mouseX <= x + 140
					&& super.mouseY >= y && super.mouseY <= y + 110
					&& !menuOpen) {
				SellHover = new Sprite("Interfaces/GE/sellHover");
				SellHover.drawSprite(x, y);
			} else {
				Sell.drawSprite(x, y);
			}
			setGrandExchange(id, false);
			changeSet(id, true, false);
			drawPercentage(id);
			smallText.method592(0xCC9900, x2,
					RSInterface.interfaceCache[32000 + id].message, y2 - minus,
					true);
			smallText.method592(0xBDBB5B, x2,
					RSInterface.interfaceCache[33000 + id].message, y2, true);
			smallText.method592(0xFFFF00, x3,
					RSInterface.interfaceCache[33100 + id].message, y3, true);
			setHovers(id, false);
		} else if (type == "Finished Buying") {
			if (super.mouseX >= x && super.mouseX <= x + 140
					&& super.mouseY >= y && super.mouseY <= y + 110
					&& !menuOpen) {
				BuyHover = new Sprite("Interfaces/GE/buyHover");
				BuyHover.drawSprite(x, y);
			} else {
				Buy.drawSprite(x, y);
			}
			setGrandExchange(id, false);
			changeSet(id, true, false);
			drawPercentage(id);
			smallText.method592(0xCC9900, x2,
					RSInterface.interfaceCache[32000 + id].message, y2 - minus,
					true);
			smallText.method592(0xBDBB5B, x2,
					RSInterface.interfaceCache[33000 + id].message, y2, true);
			smallText.method592(0xFFFF00, x3,
					RSInterface.interfaceCache[33100 + id].message, y3, true);
			setHovers(id, false);
		}
	}

	public Sprite per0;
	public Sprite per1;
	public Sprite per2;
	public Sprite per3;
	public Sprite per4;
	public Sprite per5;
	public Sprite per6;
	public Sprite abort1;
	public Sprite abort2;
	public Sprite SellHover;
	public Sprite BuyHover;
	public Sprite sellSubmitHover;
	public Sprite buySubmitHover;

	public void drawPercentage(int id) {
		per0 = new Sprite("Interfaces/GE/per 0");
		per1 = new Sprite("Interfaces/GE/per 1");
		per2 = new Sprite("Interfaces/GE/per 2");
		per3 = new Sprite("Interfaces/GE/per 3");
		abort1 = new Sprite("Interfaces/GE/abort 1");
		int x = 0;
		int y = 0;
		boolean fixed = (clientSize == 0);
		switch (id) {
		case 1:
			x = fixed ? 30 + 8 : (clientWidth / 2 - 226 + 8);
			y = fixed ? 74 + 81 : (clientHeight / 2 - 93 + 81);
			break;
		case 2:
			x = fixed ? 186 + 8 : (clientWidth / 2 - 70 + 8);
			y = fixed ? 74 + 81 : (clientHeight / 2 - 93 + 81);
			break;
		case 3:
			x = fixed ? 342 + 8 : (clientWidth / 2 + 86 + 8);
			y = fixed ? 74 + 81 : (clientHeight / 2 - 93 + 81);
			break;
		case 4:
			x = fixed ? 30 + 8 : (clientWidth / 2 - 226 + 8);
			y = fixed ? 194 + 81 : (clientHeight / 2 + 27 + 81);
			break;
		case 5:
			x = fixed ? 186 + 8 : (clientWidth / 2 - 70 + 8);
			y = fixed ? 194 + 81 : (clientHeight / 2 + 27 + 81);
			break;
		case 6:
			x = fixed ? 342 + 8 : (clientWidth / 2 + 86 + 8);
			y = fixed ? 194 + 81 : (clientHeight / 2 + 27 + 81);
			break;
		}
		x -= 2;
		if (slotColorPercent[id] > 100) {
			slotColorPercent[id] = 100;
		}
		int s = 0;
		if (!slotAborted[id]) {
			for (int k9 = 1; k9 < slotColorPercent[id]; k9++) {
				if (slotColorPercent[id] > 0) {
					if (k9 == 1) {
						per0.drawSprite(x, y);
						x += 2;
					} else if (k9 == 2) {
						per1.drawSprite(x, y);
						x += 2;
					} else if (k9 >= 6 && k9 <= 14) {
						per3.drawSprite(x, y);
						x += 1;
					} else if (k9 >= 56 && k9 <= 65) {
						per3.drawSprite(x, y);
						x += 1;
					} else if (k9 >= 76 && k9 <= 82) {
						per3.drawSprite(x, y);
						x += 1;
					} else {
						if (s == 0) {
							per2.drawSprite(x, y);
							x += 2;
							s += 1;
						} else if (s == 1) {
							per3.drawSprite(x, y);
							x += 1;
							s += 1;
						} else if (s == 2) {
							per3.drawSprite(x, y);
							x += 1;
							s = 0;
						} else if (s == 4) {
							per3.drawSprite(x, y);
							x += 1;
							s = 0;
						}
					}
				}
			}
		} else {
			abort1.drawSprite(x, y);
		}
	}

	public void setGrandExchange(int id, boolean on) {
		switch (id) {
		case 1:
			if (on) {
				RSInterface.interfaceCache[24505].tooltip = "Buy";
				RSInterface.interfaceCache[24511].tooltip = "Sell";
				changeSet(id, false, false);
			} else {
				RSInterface.interfaceCache[24505].tooltip = "[GE]";
				RSInterface.interfaceCache[24511].tooltip = "[GE]";
			}
			break;
		case 2:
			if (on) {
				RSInterface.interfaceCache[24523].tooltip = "Buy";
				RSInterface.interfaceCache[24526].tooltip = "Sell";
				changeSet(id, false, false);
			} else {
				RSInterface.interfaceCache[24523].tooltip = "[GE]";
				RSInterface.interfaceCache[24526].tooltip = "[GE]";
			}
			break;
		case 3:
			if (on) {
				RSInterface.interfaceCache[24514].tooltip = "Buy";
				RSInterface.interfaceCache[24529].tooltip = "Sell";
				changeSet(id, false, false);
			} else {
				RSInterface.interfaceCache[24514].tooltip = "[GE]";
				RSInterface.interfaceCache[24529].tooltip = "[GE]";
			}
			break;
		case 4:
			if (on) {
				RSInterface.interfaceCache[24508].tooltip = "Buy";
				RSInterface.interfaceCache[24532].tooltip = "Sell";
				changeSet(id, false, false);
			} else {
				RSInterface.interfaceCache[24508].tooltip = "[GE]";
				RSInterface.interfaceCache[24532].tooltip = "[GE]";
			}
			break;
		case 5:
			if (on) {
				RSInterface.interfaceCache[24517].tooltip = "Buy";
				RSInterface.interfaceCache[24535].tooltip = "Sell";
				changeSet(id, false, false);
			} else {
				RSInterface.interfaceCache[24517].tooltip = "[GE]";
				RSInterface.interfaceCache[24535].tooltip = "[GE]";
			}
			break;
		case 6:
			if (on) {
				RSInterface.interfaceCache[24520].tooltip = "Buy";
				RSInterface.interfaceCache[24538].tooltip = "Sell";
				changeSet(id, false, false);
			} else {
				RSInterface.interfaceCache[24520].tooltip = "[GE]";
				RSInterface.interfaceCache[24538].tooltip = "[GE]";
			}
			break;
		}
	}

	public void changeSet(int id, boolean view, boolean abort) {
		switch (id) {
		case 1:
			if (view) {
				RSInterface.interfaceCache[24543].tooltip = "View offer";
			} else {
				RSInterface.interfaceCache[24543].tooltip = "[GE]";
			}
			if (abort) {
				RSInterface.interfaceCache[24541].tooltip = "Abort offer";
			} else {
				RSInterface.interfaceCache[24541].tooltip = "[GE]";
			}
			break;
		case 2:
			if (view) {
				RSInterface.interfaceCache[24547].tooltip = "View offer";
			} else {
				RSInterface.interfaceCache[24547].tooltip = "[GE]";
			}
			if (abort) {
				RSInterface.interfaceCache[24545].tooltip = "Abort offer";
			} else {
				RSInterface.interfaceCache[24545].tooltip = "[GE]";
			}
			break;
		case 3:
			if (view) {
				RSInterface.interfaceCache[24551].tooltip = "View offer";
			} else {
				RSInterface.interfaceCache[24551].tooltip = "[GE]";
			}
			if (abort) {
				RSInterface.interfaceCache[24549].tooltip = "Abort offer";
			} else {
				RSInterface.interfaceCache[24549].tooltip = "[GE]";
			}
			break;
		case 4:
			if (view) {
				RSInterface.interfaceCache[24555].tooltip = "View offer";
			} else {
				RSInterface.interfaceCache[24555].tooltip = "[GE]";
			}
			if (abort) {
				RSInterface.interfaceCache[24553].tooltip = "Abort offer";
			} else {
				RSInterface.interfaceCache[24553].tooltip = "[GE]";
			}
			break;
		case 5:
			if (view) {
				RSInterface.interfaceCache[24559].tooltip = "View offer";
			} else {
				RSInterface.interfaceCache[24559].tooltip = "[GE]";
			}
			if (abort) {
				RSInterface.interfaceCache[24557].tooltip = "Abort offer";
			} else {
				RSInterface.interfaceCache[24557].tooltip = "[GE]";
			}
			break;
		case 6:
			if (view) {
				RSInterface.interfaceCache[24563].tooltip = "View offer";
			} else {
				RSInterface.interfaceCache[24563].tooltip = "[GE]";
			}
			if (abort) {
				RSInterface.interfaceCache[24561].tooltip = "Abort offer";
			} else {
				RSInterface.interfaceCache[24561].tooltip = "[GE]";
			}
			break;
		}
	}

	public void setHovers(int id, boolean on) {
		switch (id) {
		case 1:
			if (!on) {
				RSInterface.interfaceCache[24505].sprite1 = RSInterface.interfaceCache[24505].setSprite;
				RSInterface.interfaceCache[24511].sprite1 = RSInterface.interfaceCache[24511].setSprite;
				RSInterface.interfaceCache[24505 + 1].sprite1 = RSInterface.interfaceCache[24505 + 1].setSprite;
				RSInterface.interfaceCache[24511 + 1].sprite1 = RSInterface.interfaceCache[24511 + 1].setSprite;
				RSInterface.interfaceCache[24505 + 2].sprite1 = RSInterface.interfaceCache[24505 + 2].setSprite;
				RSInterface.interfaceCache[24511 + 2].sprite1 = RSInterface.interfaceCache[24511 + 2].setSprite;
			} else {
				RSInterface.interfaceCache[24505].sprite1 = RSInterface.interfaceCache[24505].savedFirstSprite;
				RSInterface.interfaceCache[24511].sprite1 = RSInterface.interfaceCache[24511].savedFirstSprite;
				RSInterface.interfaceCache[24505 + 1].sprite1 = RSInterface.interfaceCache[24505 + 1].savedFirstSprite;
				RSInterface.interfaceCache[24511 + 1].sprite1 = RSInterface.interfaceCache[24511 + 1].savedFirstSprite;
				RSInterface.interfaceCache[24505 + 2].sprite1 = RSInterface.interfaceCache[24505 + 2].savedFirstSprite;
				RSInterface.interfaceCache[24511 + 2].sprite1 = RSInterface.interfaceCache[24511 + 2].savedFirstSprite;
			}
			break;
		case 2:
			if (!on) {
				RSInterface.interfaceCache[24523].sprite1 = RSInterface.interfaceCache[24523].setSprite;
				RSInterface.interfaceCache[24526].sprite1 = RSInterface.interfaceCache[24526].setSprite;
				RSInterface.interfaceCache[24523 + 1].sprite1 = RSInterface.interfaceCache[24523 + 1].setSprite;
				RSInterface.interfaceCache[24526 + 1].sprite1 = RSInterface.interfaceCache[24526 + 1].setSprite;
				RSInterface.interfaceCache[24523 + 2].sprite1 = RSInterface.interfaceCache[24523 + 2].setSprite;
				RSInterface.interfaceCache[24526 + 2].sprite1 = RSInterface.interfaceCache[24526 + 2].setSprite;
			} else {
				RSInterface.interfaceCache[24523].sprite1 = RSInterface.interfaceCache[24523].savedFirstSprite;
				RSInterface.interfaceCache[24526].sprite1 = RSInterface.interfaceCache[24526].savedFirstSprite;
				RSInterface.interfaceCache[24523 + 1].sprite1 = RSInterface.interfaceCache[24523 + 1].savedFirstSprite;
				RSInterface.interfaceCache[24526 + 1].sprite1 = RSInterface.interfaceCache[24526 + 1].savedFirstSprite;
				RSInterface.interfaceCache[24523 + 2].sprite1 = RSInterface.interfaceCache[24523 + 2].savedFirstSprite;
				RSInterface.interfaceCache[24526 + 2].sprite1 = RSInterface.interfaceCache[24526 + 2].savedFirstSprite;
			}
			break;
		case 3:
			if (!on) {
				RSInterface.interfaceCache[24514].sprite1 = RSInterface.interfaceCache[24514].setSprite;
				RSInterface.interfaceCache[24529].sprite1 = RSInterface.interfaceCache[24529].setSprite;
				RSInterface.interfaceCache[24514 + 1].sprite1 = RSInterface.interfaceCache[24514 + 1].setSprite;
				RSInterface.interfaceCache[24529 + 1].sprite1 = RSInterface.interfaceCache[24529 + 1].setSprite;
				RSInterface.interfaceCache[24514 + 2].sprite1 = RSInterface.interfaceCache[24514 + 2].setSprite;
				RSInterface.interfaceCache[24529 + 2].sprite1 = RSInterface.interfaceCache[24529 + 2].setSprite;
			} else {
				RSInterface.interfaceCache[24514].sprite1 = RSInterface.interfaceCache[24514].savedFirstSprite;
				RSInterface.interfaceCache[24529].sprite1 = RSInterface.interfaceCache[24529].savedFirstSprite;
				RSInterface.interfaceCache[24514 + 1].sprite1 = RSInterface.interfaceCache[24514 + 1].savedFirstSprite;
				RSInterface.interfaceCache[24529 + 1].sprite1 = RSInterface.interfaceCache[24529 + 1].savedFirstSprite;
				RSInterface.interfaceCache[24514 + 2].sprite1 = RSInterface.interfaceCache[24514 + 2].savedFirstSprite;
				RSInterface.interfaceCache[24529 + 2].sprite1 = RSInterface.interfaceCache[24529 + 2].savedFirstSprite;
			}
			break;
		case 4:
			if (!on) {
				RSInterface.interfaceCache[24508].sprite1 = RSInterface.interfaceCache[24508].setSprite;
				RSInterface.interfaceCache[24532].sprite1 = RSInterface.interfaceCache[24532].setSprite;
				RSInterface.interfaceCache[24508 + 1].sprite1 = RSInterface.interfaceCache[24508 + 1].setSprite;
				RSInterface.interfaceCache[24532 + 1].sprite1 = RSInterface.interfaceCache[24532 + 1].setSprite;
				RSInterface.interfaceCache[24508 + 2].sprite1 = RSInterface.interfaceCache[24508 + 2].setSprite;
				RSInterface.interfaceCache[24532 + 2].sprite1 = RSInterface.interfaceCache[24532 + 2].setSprite;
			} else {
				RSInterface.interfaceCache[24508].sprite1 = RSInterface.interfaceCache[24508].savedFirstSprite;
				RSInterface.interfaceCache[24532].sprite1 = RSInterface.interfaceCache[24532].savedFirstSprite;
				RSInterface.interfaceCache[24508 + 1].sprite1 = RSInterface.interfaceCache[24508 + 1].savedFirstSprite;
				RSInterface.interfaceCache[24532 + 1].sprite1 = RSInterface.interfaceCache[24532 + 1].savedFirstSprite;
				RSInterface.interfaceCache[24508 + 2].sprite1 = RSInterface.interfaceCache[24508 + 2].savedFirstSprite;
				RSInterface.interfaceCache[24532 + 2].sprite1 = RSInterface.interfaceCache[24532 + 2].savedFirstSprite;
			}
			break;
		case 5:
			if (!on) {
				RSInterface.interfaceCache[24517].sprite1 = RSInterface.interfaceCache[24517].setSprite;
				RSInterface.interfaceCache[24535].sprite1 = RSInterface.interfaceCache[24535].setSprite;
				RSInterface.interfaceCache[24517 + 1].sprite1 = RSInterface.interfaceCache[24517 + 1].setSprite;
				RSInterface.interfaceCache[24535 + 1].sprite1 = RSInterface.interfaceCache[24535 + 1].setSprite;
				RSInterface.interfaceCache[24517 + 2].sprite1 = RSInterface.interfaceCache[24517 + 2].setSprite;
				RSInterface.interfaceCache[24535 + 2].sprite1 = RSInterface.interfaceCache[24535 + 2].setSprite;
			} else {
				RSInterface.interfaceCache[24517].sprite1 = RSInterface.interfaceCache[24517].savedFirstSprite;
				RSInterface.interfaceCache[24535].sprite1 = RSInterface.interfaceCache[24535].savedFirstSprite;
				RSInterface.interfaceCache[24517 + 1].sprite1 = RSInterface.interfaceCache[24517 + 1].savedFirstSprite;
				RSInterface.interfaceCache[24535 + 1].sprite1 = RSInterface.interfaceCache[24535 + 1].savedFirstSprite;
				RSInterface.interfaceCache[24517 + 2].sprite1 = RSInterface.interfaceCache[24517 + 2].savedFirstSprite;
				RSInterface.interfaceCache[24535 + 2].sprite1 = RSInterface.interfaceCache[24535 + 2].savedFirstSprite;
			}
			break;
		case 6:
			if (!on) {
				RSInterface.interfaceCache[24520].sprite1 = RSInterface.interfaceCache[24520].setSprite;
				RSInterface.interfaceCache[24538].sprite1 = RSInterface.interfaceCache[24538].setSprite;
				RSInterface.interfaceCache[24520 + 1].sprite1 = RSInterface.interfaceCache[24520 + 1].setSprite;
				RSInterface.interfaceCache[24538 + 1].sprite1 = RSInterface.interfaceCache[24538 + 1].setSprite;
				RSInterface.interfaceCache[24520 + 2].sprite1 = RSInterface.interfaceCache[24520 + 2].setSprite;
				RSInterface.interfaceCache[24538 + 2].sprite1 = RSInterface.interfaceCache[24538 + 2].setSprite;
			} else {
				RSInterface.interfaceCache[24520].sprite1 = RSInterface.interfaceCache[24520].savedFirstSprite;
				RSInterface.interfaceCache[24538].sprite1 = RSInterface.interfaceCache[24538].savedFirstSprite;
				RSInterface.interfaceCache[24520 + 1].sprite1 = RSInterface.interfaceCache[24520 + 1].savedFirstSprite;
				RSInterface.interfaceCache[24538 + 1].sprite1 = RSInterface.interfaceCache[24538 + 1].savedFirstSprite;
				RSInterface.interfaceCache[24520 + 2].sprite1 = RSInterface.interfaceCache[24520 + 2].savedFirstSprite;
				RSInterface.interfaceCache[24538 + 2].sprite1 = RSInterface.interfaceCache[24538 + 2].savedFirstSprite;
			}
			break;
		}
	}

	public void tabToReplyPm() {
		String name = null;
		for (int j = 0; j < 100; j++)
			if (chatMessages[j] != null) {
				int chatType = chatTypes[j];
				if (chatType == 3 || chatType == 7) {
					name = chatNames[j];
					break;
				}
			}
		if (name != null && name.startsWith("@cr0@")) {
			name = name.substring(5);
		}
		if (name != null && name.startsWith("@cr1@")) {
			name = name.substring(5);
		}
		if (name != null && name.startsWith("@cr2@")) {
			name = name.substring(5);
		}
		if (name == null)
			pushMessage("You have not recieved any messages.", 0, "");
		try {
			if (name != null) {
				long namel = TextClass.longForName(name.trim());
				int node = -1;
				for (int count = 0; count < friendsCount; count++) {
					if (friendsListAsLongs[count] != namel)
						continue;
					node = count;
					break;
				}
				if (node != -1 && friendsNodeIDs[node] > 0) {
					inputTaken = true;
					inputDialogState = 0;
					showChat = true;
					promptInput = "";
					friendsListAction = 3;
					aLong953 = friendsListAsLongs[node];
					promptMessage = "Enter message to send to "
							+ friendsList[node];
				} else {
					pushMessage(capitalize(name) + " is currently offline.", 0,
							"");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadItemPrices(String filename) {
		try {
			@SuppressWarnings("resource")
			Scanner s = new Scanner(new File("" + filename));
			while (s.hasNextLine()) {
				String[] line = s.nextLine().split(" ");
				ItemList temp = getItemList(Integer.parseInt(line[0]));
				if (temp != null)
					temp.ShopValue = Integer.parseInt(line[1]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getItemShopValue(int itemId) {
		for (int i = 0; i < 24470; i++) {
			if (ItemList[i] != null) {
				if (ItemList[i].itemId == itemId) {
					return (int) ItemList[i].ShopValue;
				}
			}
		}
		return 0;
	}

	public ItemList getItemList(int i) {
		for (int j = 0; j < ItemList.length; j++) {
			if (ItemList[j] != null) {
				if (ItemList[j].itemId == i) {
					return ItemList[j];
				}
			}
		}
		return null;
	}

	public int getClientWidth() {
		return clientWidth;
	}

	public int getClientHeight() {
		return clientHeight;
	}

	public static Client getClient() {
		return instance;
	}

	public void launchURL(String url) {
		String osName = System.getProperty("os.name");
		try {
			if (osName.startsWith("Mac OS")) {
				Class<?> fileMgr = Class.forName("com.apple.eio.FileManager");
				Method openURL = fileMgr.getDeclaredMethod("openURL",
						new Class[] { String.class });
				openURL.invoke(null, new Object[] { url });
			} else if (osName.startsWith("Windows"))
				Runtime.getRuntime().exec(
						"rundll32 url.dll,FileProtocolHandler " + url);
			else {
				String[] browsers = { "firefox", "opera", "konqueror",
						"epiphany", "mozilla", "netscape", "safari" };
				String browser = null;
				for (int count = 0; count < browsers.length && browser == null; count++)
					if (Runtime.getRuntime()
							.exec(new String[] { "which", browsers[count] })
							.waitFor() == 0)
						browser = browsers[count];
				if (browser == null) {
					throw new Exception("Could not find web browser");
				} else
					Runtime.getRuntime().exec(new String[] { browser, url });
			}
		} catch (Exception e) {
			pushMessage("Failed to open URL.", 0, "");
		}
	}

	static {
		anIntArray1019 = new int[99];
		int i = 0;
		for (int j = 0; j < 99; j++) {
			int l = j + 1;
			int i1 = (int) ((double) l + 300D * Math.pow(2D, (double) l / 7D));
			i += i1;
			anIntArray1019[j] = i / 4;
		}
		anIntArray1232 = new int[32];
		i = 2;
		for (int k = 0; k < 32; k++) {
			anIntArray1232[k] = i - 1;
			i += i;
		}
	}

	public Sprite[] WorldOrb = new Sprite[2];
	public boolean[] worldMap = new boolean[2];

	public void loadOrbs() {
		drawAdv();
		drawWorldMap();
	}

	public void drawAdv() {
		ADVISOR[!advisorHover ? 0 : 1].drawSprite(207, 0);
		if (super.clickMode2 == 1 && super.mouseX > 724 && super.mouseX < 743
				&& super.mouseY > 1 && super.mouseY < 20) {
			ADVISOR[2].drawSprite(207, 0);
		}
	}

	public void drawWorldMap() {
		if (clientSize == 0) {
			if (super.mouseX >= 522 && super.mouseX <= 558
					&& super.mouseY >= 124 && super.mouseY < 161)
				WorldOrb[1].drawSprite(7, 123);
			else
				WorldOrb[0].drawSprite(7, 123);
		} else {
			cacheSprite[37].drawSprite(clientWidth - 45, 129);
			if (super.mouseX >= clientWidth - 48
					&& super.mouseX <= clientWidth - 5 && super.mouseY >= 121
					&& super.mouseY <= 171)
				WorldOrb[1].drawSprite(clientWidth - 41, 133);
			else
				WorldOrb[0].drawSprite(clientWidth - 41, 133);
		}

	}

	public int logIconHPos = 0;

	public static FamiliarHandler getFamiliar() {
		return familiarHandler;
	}

	public Creation getRegister() {
		return register;
	}

	public boolean mouseInRegion2(int x1, int x2, int y1, int y2) {
		if (super.mouseX >= x1 && super.mouseX <= x2 && super.mouseY >= y1
				&& super.mouseY <= y2) {
			return true;
		}
		return false;
	}

	public boolean clickInRegion2(int x1, int x2, int y1, int y2) {
		if (super.saveClickX >= x1 && super.saveClickX <= x2
				&& super.saveClickY >= y1 && super.saveClickY <= y2) {
			return true;
		}
		return false;
	}

	public boolean mouseInRegion(int x1, int y1, int x2, int y2) {
		if (super.mouseX >= x1 && super.mouseX <= x2 && super.mouseY >= y1
				&& super.mouseY <= y2)
			return true;
		return false;
	}

	public boolean clickInRegion(int x1, int y1, int x2, int y2) {
		if (super.saveClickX >= x1 && super.saveClickX <= x2
				&& super.saveClickY >= y1 && super.saveClickY <= y2)
			return true;
		return false;
	}

	public boolean logHover = false;
	public boolean advisorHover = false;
	public Sprite[] ADVISOR = new Sprite[5];
	public Sprite[] orbs = new Sprite[20];
	public Sprite[] LOGOUT = new Sprite[5];

	private static String intToKOrMilLongName(int i) {
		String s = String.valueOf(i);
		for (int k = s.length() - 3; k > 0; k -= 3)
			s = s.substring(0, k) + "," + s.substring(k);
		if (s.length() > 8)
			s = "@gre@" + s.substring(0, s.length() - 8) + " million @whi@("
					+ s + ")";
		else if (s.length() > 4)
			s = "@cya@" + s.substring(0, s.length() - 4) + "K @whi@(" + s + ")";
		return " " + s;
	}

	public void hitmarkDraw(Entity e, int hitLength, int type, int icon,
			int damage, int soak, int move, int opacity, int mask) {
		int drawPos = 0;
		if (mask == 0) {
			e.hitMarkPos[0] = spriteDrawY + move;
			drawPos = e.hitMarkPos[0];
		}
		if (mask != 0) {
			e.hitMarkPos[mask] = e.hitMarkPos[0] + (19 * mask);
			drawPos = e.hitMarkPos[mask];
		}
		if (damage > 0) {
			Sprite end1 = null, middle = null, end2 = null;
			int x = 0;
			switch (hitLength) {
			/* Trial and error shit, terrible hardcoding :( */
			case 1:
				x = 8;
				break;
			case 2:
				x = 4;
				break;
			case 3:
				x = 1;
				break;
			}
			if (soak > 0)
				x -= 16;
			end1 = hitMark[(type * 3)];
			middle = hitMark[(type * 3) + 1];
			end2 = hitMark[(type * 3) + 2];
			if (icon != 255) {
				hitIcon[icon].drawSprite3(spriteDrawX - 34 + x, drawPos - 14,
						opacity);
			}
			end1.drawSprite3(spriteDrawX - 12 + x, drawPos - 12, opacity);
			x += 4;
			for (int i = 0; i < hitLength * 2; i++) {
				middle.drawSprite3(spriteDrawX - 12 + x, drawPos - 12, opacity);
				x += 4;
			}
			end2.drawSprite3(spriteDrawX - 12 + x, drawPos - 12, opacity);
			(type == 1 ? bigHit : smallHit).drawOpacityText(0xffffff,
					String.valueOf(damage), drawPos + (type == 1 ? 2 : 32),
					spriteDrawX + 4 + (soak > 0 ? -16 : 0), opacity);
			if (soak > 0)
				drawSoak(soak, opacity, drawPos, x);
		} else {
			Sprite block = new Sprite("/Hitmarks/block");
			block.drawSprite3(spriteDrawX - 12, drawPos - 14, opacity);
		}
	}

	public void drawSoak(int damage, int opacity, int drawPos, int x) {
		x -= 12;
		int soakLength = String.valueOf(damage).length();
		hitIcon[5].drawSprite3(spriteDrawX + x, drawPos - 12, opacity);
		x += 20;
		hitMark[30].drawSprite3(spriteDrawX + x, drawPos - 12, opacity);
		x += 4;
		for (int i = 0; i < soakLength * 2; i++) {
			hitMark[31].drawSprite3(spriteDrawX + x, drawPos - 12, opacity);
			x += 4;
		}
		hitMark[32].drawSprite3(spriteDrawX + x, drawPos - 10, opacity);
		smallHit.drawOpacityText(0xffffff, String.valueOf(damage),
				drawPos + 32, spriteDrawX - 8 + x + (soakLength == 1 ? 5 : 0),
				opacity);
	}
}