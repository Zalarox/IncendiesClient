package src; 

import src.item.ItemDef;
import src.sprite.Sprite;

public final class RSInterface {

	public void swapInventoryItems(int i, int j) {
		int k = inv[i];
		inv[i] = inv[j];
		inv[j] = k;
		k = invStackSizes[i];
		invStackSizes[i] = invStackSizes[j];
		invStackSizes[j] = k;
	}

	public static void unpack(StreamLoader streamLoader,
			TextDrawingArea textDrawingAreas[], StreamLoader streamLoader_1) {
		fonts = textDrawingAreas;
		aMRUNodes_238 = new MRUNodes(50000);
		Stream stream = new Stream(streamLoader.getDataForName("data"));
		int i = -1;
		int j = stream.readUnsignedWord();
		interfaceCache = new RSInterface[j + 50000];
		while (stream.currentOffset < stream.buffer.length) {
			int k = stream.readUnsignedWord();
			if (k == 65535) {
				i = stream.readUnsignedWord();
				k = stream.readUnsignedWord();
			}
			RSInterface rsInterface = interfaceCache[k] = new RSInterface();
			rsInterface.id = k;
			rsInterface.parentID = i;
			rsInterface.type = stream.readUnsignedByte();
			rsInterface.atActionType = stream.readUnsignedByte();
			rsInterface.contentType = stream.readUnsignedWord();
			rsInterface.width = stream.readUnsignedWord();
			rsInterface.height = stream.readUnsignedWord();
			rsInterface.opacity = (byte) stream.readUnsignedByte();
			rsInterface.hoverType = stream.readUnsignedByte();
			if (rsInterface.hoverType != 0)
				rsInterface.hoverType = (rsInterface.hoverType - 1 << 8)
				+ stream.readUnsignedByte();
			else
				rsInterface.hoverType = -1;
			int i1 = stream.readUnsignedByte();
			if (i1 > 0) {
				rsInterface.valueCompareType = new int[i1];
				rsInterface.requiredValues = new int[i1];
				for (int j1 = 0; j1 < i1; j1++) {
					rsInterface.valueCompareType[j1] = stream
							.readUnsignedByte();
					rsInterface.requiredValues[j1] = stream.readUnsignedWord();
				}

			}
			int k1 = stream.readUnsignedByte();
			if (k1 > 0) {
				rsInterface.valueIndexArray = new int[k1][];
				for (int l1 = 0; l1 < k1; l1++) {
					int i3 = stream.readUnsignedWord();
					rsInterface.valueIndexArray[l1] = new int[i3];
					for (int l4 = 0; l4 < i3; l4++)
						rsInterface.valueIndexArray[l1][l4] = stream
						.readUnsignedWord();

				}

			}
			if (rsInterface.type == 0) {
				rsInterface.drawsTransparent = false;
				rsInterface.scrollMax = stream.readUnsignedWord();
				rsInterface.interfaceShown = stream.readUnsignedByte() == 1;
				int i2 = stream.readUnsignedWord();
				rsInterface.children = new int[i2];
				rsInterface.childX = new int[i2];
				rsInterface.childY = new int[i2];
				for (int j3 = 0; j3 < i2; j3++) {
					rsInterface.children[j3] = stream.readUnsignedWord();
					rsInterface.childX[j3] = stream.readSignedWord();
					rsInterface.childY[j3] = stream.readSignedWord();
				}
			}
			if (rsInterface.type == 1) {
				stream.readUnsignedWord();
				stream.readUnsignedByte();
			}
			if (rsInterface.type == 2) {
				rsInterface.inv = new int[rsInterface.width
				                          * rsInterface.height];
				rsInterface.invStackSizes = new int[rsInterface.width
				                                    * rsInterface.height];
				rsInterface.aBoolean259 = stream.readUnsignedByte() == 1;
				rsInterface.isInventoryInterface = stream.readUnsignedByte() == 1;
				rsInterface.usableItemInterface = stream.readUnsignedByte() == 1;
				rsInterface.aBoolean235 = stream.readUnsignedByte() == 1;
				rsInterface.invSpritePadX = stream.readUnsignedByte();
				rsInterface.invSpritePadY = stream.readUnsignedByte();
				rsInterface.spritesX = new int[20];
				rsInterface.spritesY = new int[20];
				rsInterface.sprites = new Sprite[20];
				for (int j2 = 0; j2 < 20; j2++) {
					int k3 = stream.readUnsignedByte();
					if (k3 == 1) {
						rsInterface.spritesX[j2] = stream.readSignedWord();
						rsInterface.spritesY[j2] = stream.readSignedWord();
						String s1 = stream.readString();
						if (streamLoader_1 != null && s1.length() > 0) {
							int i5 = s1.lastIndexOf(",");
							rsInterface.sprites[j2] = method207(
									Integer.parseInt(s1.substring(i5 + 1)),
									streamLoader_1, s1.substring(0, i5));
						}
					}
				}
				rsInterface.actions = new String[5];
				for (int l3 = 0; l3 < 5; l3++) {
					rsInterface.actions[l3] = stream.readString();
					if (rsInterface.actions[l3].length() == 0)
						rsInterface.actions[l3] = null;
					if (rsInterface.parentID == 3824)
						rsInterface.actions[4] = "Buy 100";
					if (rsInterface.parentID == 1644)
						rsInterface.actions[2] = "Operate";
				}
			}
			if (rsInterface.type == 3)
				rsInterface.aBoolean227 = stream.readUnsignedByte() == 1;
			if (rsInterface.type == 4 || rsInterface.type == 1) {
				rsInterface.centerText = stream.readUnsignedByte() == 1;
				int k2 = stream.readUnsignedByte();
				if (textDrawingAreas != null)
					rsInterface.textDrawingAreas = textDrawingAreas[k2];
				rsInterface.textShadow = stream.readUnsignedByte() == 1;
			}
			if (rsInterface.type == 4) {
				rsInterface.message = stream.readString().replaceAll(
						"RuneScape", "Incendius");
				rsInterface.aString228 = stream.readString();
			}
			if (rsInterface.type == 1 || rsInterface.type == 3
					|| rsInterface.type == 4)
				rsInterface.textColor = stream.readDWord();
			if (rsInterface.type == 3 || rsInterface.type == 4) {
				rsInterface.anInt219 = stream.readDWord();
				rsInterface.anInt216 = stream.readDWord();
				rsInterface.anInt239 = stream.readDWord();
			}
			if (rsInterface.type == 5) {
				rsInterface.drawsTransparent = false;
				String s = stream.readString();
				if (streamLoader_1 != null && s.length() > 0) {
					int i4 = s.lastIndexOf(",");
					rsInterface.sprite1 = method207(
							Integer.parseInt(s.substring(i4 + 1)),
							streamLoader_1, s.substring(0, i4));
				}
				s = stream.readString();
				if (streamLoader_1 != null && s.length() > 0) {
					int j4 = s.lastIndexOf(",");
					rsInterface.sprite2 = method207(
							Integer.parseInt(s.substring(j4 + 1)),
							streamLoader_1, s.substring(0, j4));
				}
			}
			if (rsInterface.type == 6) {
				int l = stream.readUnsignedByte();
				if (l != 0) {
					rsInterface.anInt233 = 1;
					rsInterface.mediaID = (l - 1 << 8)
							+ stream.readUnsignedByte();
				}
				l = stream.readUnsignedByte();
				if (l != 0) {
					rsInterface.anInt255 = 1;
					rsInterface.anInt256 = (l - 1 << 8)
							+ stream.readUnsignedByte();
				}
				l = stream.readUnsignedByte();
				if (l != 0) {
					rsInterface.anInt257 = (l - 1 << 8)
							+ stream.readUnsignedByte();
				} else
					rsInterface.anInt257 = -1;
				l = stream.readUnsignedByte();
				if (l != 0)
					rsInterface.anInt258 = (l - 1 << 8)
					+ stream.readUnsignedByte();
				else
					rsInterface.anInt258 = -1;
				rsInterface.modelZoom = stream.readUnsignedWord();
				rsInterface.modelRotation1 = stream.readUnsignedWord();
				rsInterface.modelRotation2 = stream.readUnsignedWord();
			}
			if (rsInterface.type == 7) {
				rsInterface.inv = new int[rsInterface.width
				                          * rsInterface.height];
				rsInterface.invStackSizes = new int[rsInterface.width
				                                    * rsInterface.height];
				rsInterface.centerText = stream.readUnsignedByte() == 1;
				int l2 = stream.readUnsignedByte();
				if (textDrawingAreas != null)
					rsInterface.textDrawingAreas = textDrawingAreas[l2];
				rsInterface.textShadow = stream.readUnsignedByte() == 1;
				rsInterface.textColor = stream.readDWord();
				rsInterface.invSpritePadX = stream.readSignedWord();
				rsInterface.invSpritePadY = stream.readSignedWord();
				rsInterface.isInventoryInterface = stream.readUnsignedByte() == 1;
				rsInterface.actions = new String[5];
				for (int k4 = 0; k4 < 5; k4++) {
					rsInterface.actions[k4] = stream.readString();
					if (rsInterface.actions[k4].length() == 0)
						rsInterface.actions[k4] = null;
				}

			}
			if (rsInterface.atActionType == 2 || rsInterface.type == 2) {
				rsInterface.selectedActionName = stream.readString();
				rsInterface.spellName = stream.readString();
				rsInterface.spellUsableOn = stream.readUnsignedWord();
			}

			if (rsInterface.type == 8)
				rsInterface.message = stream.readString();

			if (rsInterface.atActionType == 1 || rsInterface.atActionType == 4
					|| rsInterface.atActionType == 5
					|| rsInterface.atActionType == 6) {
				rsInterface.tooltip = stream.readString();
				if (rsInterface.tooltip.length() == 0) {
					if (rsInterface.atActionType == 1)
						rsInterface.tooltip = "Ok";
					if (rsInterface.atActionType == 4)
						rsInterface.tooltip = "Select";
					if (rsInterface.atActionType == 5)
						rsInterface.tooltip = "Select";
					if (rsInterface.atActionType == 6)
						rsInterface.tooltip = "Continue";
				}
			}
		}
		aClass44 = streamLoader;
		note(textDrawingAreas);
		skilllevel(textDrawingAreas);
		prayerTab(textDrawingAreas);
		emoteTab();
		newBank();
		quickPrayers(textDrawingAreas);
		quickCurses(textDrawingAreas);
		taskInterface(textDrawingAreas);
		taskInterface2(textDrawingAreas);
		EquipmentTab(textDrawingAreas);
		itemsOnDeath(textDrawingAreas);
		itemsOnDeathDATA(textDrawingAreas);
		equipmentScreen(textDrawingAreas);
		formParty(textDrawingAreas);
		dungParty(textDrawingAreas);
		floorMenus(textDrawingAreas);
		InvtoParty(textDrawingAreas);
		SummonTab(textDrawingAreas);
		boss(textDrawingAreas);
		wilderness(textDrawingAreas);
		city(textDrawingAreas);
		training(textDrawingAreas);
		questTab(textDrawingAreas);
		minigame(textDrawingAreas);
		skill(textDrawingAreas);
		Trade(textDrawingAreas);
		Shop(textDrawingAreas);
		optionTab(textDrawingAreas);
		clanChatTab(textDrawingAreas);
		editClan(textDrawingAreas);
		Sidebar0(textDrawingAreas);
		friendsTab(textDrawingAreas);
		ignoreTab(textDrawingAreas);
		magicTab(textDrawingAreas);
		beastOfBurden(textDrawingAreas);
		beastOfBurden2(textDrawingAreas);
		configureLunar(textDrawingAreas);
		pouches(textDrawingAreas);
		skillTab602(textDrawingAreas);
		Pestpanel(textDrawingAreas);
		Pestpanel2(textDrawingAreas);
		ancientMagicTab(textDrawingAreas);
		curseTab(textDrawingAreas);
		HotZoneInterface(textDrawingAreas);
		NonHotZoneInterface(textDrawingAreas);
		godwars(textDrawingAreas);
		summoningTab(textDrawingAreas);
		petSummoningTab(textDrawingAreas);
		pouchCreation(textDrawingAreas);
		scrollCreation(textDrawingAreas);
		Buy(textDrawingAreas);
		Sell(textDrawingAreas);
		BuyandSell(textDrawingAreas);
		collectSell(textDrawingAreas);
		collectBuy(textDrawingAreas);
		vidOptions(textDrawingAreas);
		aMRUNodes_238 = null;
	}

	public static void vidOptions(TextDrawingArea tda[]) {
		RSInterface tab = addTabInterface(40030);
		// RSInterface rsinterface = addTabInterface(40030);
		int i = 0;
		byte byte0 = 2;
		addSprite(40042, 24, "/Options/OPTION");
		addHoverButton(40039, "/Options/close", 20, 16, 16, "Close Window", 0,
				40040, 1);
		addHoveredButton(40040, "/Options/close", 21, 16, 16, 40027);
		// addHovered(40040, 2, "/Options/close", 16, 16, 40027);
		addText(40041, "Graphics Options", tda, 2, 0xff9b00, true, true);
		addConfigButton(906, 904, 10, 14, "/Options/SPRITE", 32, 16, "Dark", 1,
				5, 166);
		addConfigButton(908, 904, 11, 15, "/Options/SPRITE", 32, 16, "Normal",
				2, 5, 166);
		addConfigButton(910, 904, 12, 16, "/Options/SPRITE", 32, 16, "Bright",
				3, 5, 166);
		addConfigButton(912, 904, 13, 17, "/Options/SPRITE", 32, 16,
				"Very Bright", 4, 5, 166);

		addConfigButton(941, 904, 19, 24, "/Options/SPRITE", 26, 16,
				"Regular Zoom", 4, 5, 169);
		addConfigButton(942, 904, 20, 25, "/Options/SPRITE", 26, 16, "Zoom +1",
				3, 5, 169);
		addConfigButton(943, 904, 21, 26, "/Options/SPRITE", 26, 16, "Zoom +2",
				2, 5, 169);
		addConfigButton(944, 904, 22, 27, "/Options/SPRITE", 26, 16, "Zoom +3",
				1, 5, 169);
		addConfigButton(945, 904, 23, 28, "/Options/SPRITE", 24, 16, "Zoom +4",
				0, 5, 169);

		addSprite(40036, 9, "/Options/SPRITE");
		addSprite(40037, 29, "/Options/SPRITE");

		addHoverButton(40043, "/Options/SPRITE", 37, 50, 39, "Fixed", 0, 40044,
				1);
		addHoveredButton(40044, "/Options/SPRITE", 38, 50, 39, 40045);
		addHoverButton(40046, "/Options/SPRITE", 39, 50, 39, "Resizable", 0,
				40047, 1);
		addHoveredButton(40047, "/Options/SPRITE", 40, 50, 39, 40048);
		addHoverButton(40049, "/Options/SPRITE", 41, 50, 39, "Fullscreen", 0,
				40050, 1);
		addHoveredButton(40050, "/Options/SPRITE", 42, 50, 39, 40051);

		tab.totalChildren(21);
		tab.child(0, 40042, 25, 75 + byte0);
		tab.child(1, 906, 125, 140 + byte0);
		tab.child(2, 908, 149, 140 + byte0);
		tab.child(3, 910, 174, 140 + byte0);
		tab.child(4, 912, 200, 140 + byte0);

		tab.child(5, 941, 245, 140 + byte0);
		tab.child(6, 942, 269, 140 + byte0);
		tab.child(7, 943, 294, 140 + byte0);
		tab.child(8, 944, 320, 140 + byte0);
		tab.child(9, 945, 345, 140 + byte0);

		tab.child(10, 40036, 160, 105);
		tab.child(11, 40037, 290, 105);

		tab.child(12, 40039, 450, 80);
		tab.child(13, 40040, 450, 80);
		tab.child(14, 40041, 250, 80);

		tab.child(15, 40043, 145, 180);
		tab.child(16, 40044, 145, 180);
		tab.child(17, 40046, 220, 180);
		tab.child(18, 40047, 220, 180);
		tab.child(19, 40049, 295, 180);
		tab.child(20, 40050, 295, 180);
	}

	public static void setChildren(int total, RSInterface rsinterface) {
		rsinterface.children = new int[total];
		rsinterface.childX = new int[total];
		rsinterface.childY = new int[total];
	}

	public static void drawTooltip(int id, String text) {
		RSInterface rsinterface = addTabInterface(id);
		rsinterface.parentID = id;
		rsinterface.type = 0;
		rsinterface.interfaceShown = true;
		rsinterface.hoverType = -1;
		addTooltipBox(id + 1, text);
		rsinterface.totalChildren(1);
		rsinterface.child(0, id + 1, 0, 0);
	}

	public static void taskInterface(TextDrawingArea[] TDA) {
		RSInterface Interface = addTabInterface(23800);
		setChildren(43, Interface);
		addSprite(23801, 1, "Interfaces/Task/TaskSystemInterface");
		addHoverButton(23802, "Interfaces/Task/taskButton", 1, 18, 18,
				"Close Window", 0, 23803, 1);
		addHoveredButton(23803, "Interfaces/Task/taskButton", 2, 16, 16, 23804);
		// addHover(23802, 3, 0, 23803, 1, "Interfaces/Task/taskButton", 18, 18,
		// "Close");
		// addHovered(23803, 2, "Interfaces/Task/taskButton", 16, 16, 23804);
		addHoverButton(23805, "Interfaces/Task/Task", 3, 20, 20, "Filter-sets",
				0, 23806, 1);
		addHoveredButton(23806, "Interfaces/Task/Task", 5, 20, 20, 23807);
		addHoverButton(23808, "Interfaces/Task/Task", 4, 20, 20, "Filter-done",
				0, 23809, 1);
		addHoveredButton(23809, "Interfaces/Task/Task", 6, 20, 20, 23810);
		addHoverButton(23811, "Interfaces/Task/Task", 23, 68, 54,
				"Summary for Skills", 0, 23812, 1);
		addHoveredButton(23812, "Interfaces/Task/Task", 22, 68, 54, 23813);
		addHoverButton(23814, "Interfaces/Task/Task", 32, 68, 54,
				"Summary for Quarter Centurion", 0, 23815, 1);
		addHoveredButton(23815, "Interfaces/Task/Task", 33, 68, 54, 23816);
		addHoverButton(23817, "Interfaces/Task/Task", 36, 68, 54,
				"Summary for Hi Ho, Silver", 0, 23818, 1);
		addHoveredButton(23818, "Interfaces/Task/Task", 37, 68, 54, 23819);
		addHoverButton(23820, "Interfaces/Task/Task", 38, 68, 54,
				"Summary for Grab The Cash", 0, 23821, 1);
		addHoveredButton(23821, "Interfaces/Task/Task", 39, 68, 54, 23822);
		addHoverButton(23823, "Interfaces/Task/Task", 40, 68, 54,
				"Summary for Intervention", 0, 23824, 1);
		addHoveredButton(23824, "Interfaces/Task/Task", 41, 68, 54, 23825);
		addHoverButton(23826, "Interfaces/Task/Task", 7, 14, 14,
				"Turn-on popups", 0, 23827, 1);
		addHoveredButton(23827, "Interfaces/Task/Task", 8, 14, 14, 23828);
		addHoverButton(23829, "Interfaces/Task/Task", 1, 68, 54,
				"Summary for Undefeatable", 0, 23830, 1);
		addHoveredButton(23830, "Interfaces/Task/Task", 2, 68, 54, 23831);
		addHoverButton(23832, "Interfaces/Task/Task", 24, 68, 54,
				"Summary for You Doity Rat", 0, 23833, 1);
		addHoveredButton(23833, "Interfaces/Task/Task", 25, 68, 54, 23834);
		addHoverButton(23835, "Interfaces/Task/Task", 26, 68, 54,
				"Summary for Can't Touch This", 0, 23836, 1);
		addHoveredButton(23836, "Interfaces/Task/Task", 27, 68, 54, 23837);
		addHoverButton(23838, "Interfaces/Task/Task", 28, 68, 54,
				"Summary for Ease of Access", 0, 23839, 1);
		addHoveredButton(23839, "Interfaces/Task/Task", 29, 68, 54, 23840);
		addHoverButton(23841, "Interfaces/Task/Task", 42, 68, 54,
				"Summary for Bovine Intervention", 0, 23842, 1);
		addHoveredButton(23842, "Interfaces/Task/Task", 43, 68, 54, 23843);
		addHoverButton(23844, "Interfaces/Task/Task", 44, 68, 54,
				"Summary for Armed and Dangerous", 0, 23845, 1);
		addHoveredButton(23845, "Interfaces/Task/Task", 45, 68, 54, 23846);
		addHoverButton(23847, "Interfaces/Task/Task", 46, 68, 54,
				"Summary for You Can Bank On Us", 0, 23848, 1);
		addHoveredButton(23848, "Interfaces/Task/Task", 47, 68, 54, 23849);
		addHoverButton(23850, "Interfaces/Task/Task", 48, 68, 54,
				"Summary for Air Craft", 0, 23851, 1);
		addHoveredButton(23851, "Interfaces/Task/Task", 49, 68, 54, 23852);
		addHoverButton(23853, "Interfaces/Task/Task", 50, 68, 54,
				"Summary for I Wonder If It'll Sprout", 0, 23854, 1);
		addHoveredButton(23854, "Interfaces/Task/Task", 51, 68, 54, 23855);
		addHoverButton(23856, "Interfaces/Task/Task", 30, 68, 54,
				"Summary for Always Be Prepared", 0, 23857, 1);
		addHoveredButton(23857, "Interfaces/Task/Task", 31, 68, 54, 23858);
		addText(23859, "Click on a Task to view its details", TDA, 0, 0xFFFFFF,
				false, true);
		addHoverButton(23860, "Interfaces/Task/Progress", 1, 0, 0, "", 0,
				23861, 1);
		addHoveredButton(23861, "Interfaces/Task/Progress", 1, 0, 0, 23862);
		addText(23863, "Progress:  0 / 15", TDA, 0, 0xFFFFFF, false, true);
		setBounds(23801, 5, 4, 0, Interface); // Background
		setBounds(23802, 482, 7, 1, Interface); // Close
		setBounds(23803, 482, 7, 2, Interface);
		setBounds(23805, 458, 32, 3, Interface); // Task option 1
		setBounds(23806, 458, 32, 4, Interface);
		setBounds(23808, 481, 32, 5, Interface); // Task option 2
		setBounds(23809, 481, 32, 6, Interface);
		// Row one
		setBounds(23811, 20, 60, 7, Interface);
		setBounds(23812, 20, 60, 8, Interface);
		setBounds(23814, 120, 60, 9, Interface);
		setBounds(23815, 120, 60, 10, Interface);
		setBounds(23817, 220, 60, 11, Interface);
		setBounds(23818, 220, 60, 12, Interface);
		setBounds(23820, 320, 60, 13, Interface);
		setBounds(23821, 320, 60, 14, Interface);
		setBounds(23823, 420, 60, 15, Interface);
		setBounds(23824, 420, 60, 16, Interface);
		setBounds(23826, 9, 298, 17, Interface);
		setBounds(23827, 9, 298, 18, Interface);
		// Row 2
		setBounds(23829, 20, 145, 19, Interface);
		setBounds(23830, 20, 145, 20, Interface);
		setBounds(23832, 120, 145, 21, Interface);
		setBounds(23833, 120, 145, 22, Interface);
		setBounds(23835, 220, 145, 23, Interface);
		setBounds(23836, 220, 145, 24, Interface);
		setBounds(23838, 320, 145, 25, Interface);
		setBounds(23839, 320, 145, 26, Interface);
		setBounds(23841, 420, 145, 27, Interface);
		setBounds(23842, 420, 145, 28, Interface);
		// Row 3
		setBounds(23844, 120, 230, 29, Interface);
		setBounds(23845, 120, 230, 30, Interface);
		setBounds(23847, 220, 230, 31, Interface);
		setBounds(23848, 220, 230, 32, Interface);
		setBounds(23850, 320, 230, 33, Interface);
		setBounds(23851, 320, 230, 34, Interface);
		setBounds(23853, 420, 230, 35, Interface);
		setBounds(23854, 420, 230, 36, Interface);
		setBounds(23856, 20, 230, 37, Interface);
		setBounds(23857, 20, 230, 38, Interface);
		/** TEXT **/
		setBounds(23859, 340, 300, 39, Interface);
		/** INFO **/
		setBounds(23860, 10, 33, 40, Interface);
		setBounds(23861, 10, 33, 41, Interface);
		setBounds(23863, 27, 37, 42, Interface);
	}

	public static void taskInterface2(TextDrawingArea[] TDA) {
		/** Interface by Cody' **/
		RSInterface rsinterface = addTabInterface(23900);
		setChildren(17, rsinterface);
		addSprite(23901, 2, "Interfaces/Task/TaskSystemInterface");
		addHoverButton(23907, "Interfaces/Task/taskOpen", 1, 181, 17, "Open",
				0, 23908, 1);
		addHoveredButton(23908, "Interfaces/Task/taskOpen", 2, 181, 17, 23909);
		addHoverButton(23910, "Interfaces/Task/sideTask", 1, 68, 54, "Summary",
				0, 23911, 1);
		addHoveredButton(23911, "Interfaces/Task/sideTask", 2, 68, 54, 23912);
		addHoverButton(23913, "Interfaces/Task/sideTask", 3, 68, 54, "Summary",
				0, 23914, 1);
		addHoveredButton(23914, "Interfaces/Task/sideTask", 4, 68, 54, 23915);
		addHoverButton(23916, "Interfaces/Task/sideTask", 5, 68, 54, "Summary",
				0, 23917, 1);
		addHoveredButton(23917, "Interfaces/Task/sideTask", 6, 68, 54, 23918);
		addHoverButton(23919, "Interfaces/Task/sideTask", 7, 68, 54, "Summary",
				0, 23920, 1);
		addHoveredButton(23920, "Interfaces/Task/sideTask", 8, 68, 54, 23921);
		addHoverButton(23922, "Interfaces/Task/sideTask", 9, 68, 54, "Summary",
				0, 23923, 1);
		addHoveredButton(23923, "Interfaces/Task/sideTask", 10, 68, 54, 23924);
		addHoverButton(23926, "Interfaces/Task/sideTask", 11, 68, 54,
				"Summary", 0, 23927, 1);
		addHoveredButton(23927, "Interfaces/Task/sideTask", 12, 68, 54, 23928);
		addText(23929, "Open Task List", TDA, 0, 0xFFFFFF, false, true);
		addText(23930, "Progress:  0 / 15", TDA, 0, 0xFFFFFF, false, true);
		rsinterface.child(0, 23901, 0, 0);
		rsinterface.child(1, 23907, 3, 240); // Task List Button
		rsinterface.child(2, 23908, 3, 240); // Task List Button
		rsinterface.child(3, 23910, 15, 50); // Task 1
		rsinterface.child(4, 23911, 15, 50);
		rsinterface.child(5, 23913, 105, 50); // Task 2
		rsinterface.child(6, 23914, 105, 50);
		rsinterface.child(7, 23916, 15, 110); // Task 3
		rsinterface.child(8, 23917, 15, 110);
		rsinterface.child(9, 23919, 105, 110); // Task 4
		rsinterface.child(10, 23920, 105, 110);
		rsinterface.child(11, 23922, 15, 170); // Task 5
		rsinterface.child(12, 23923, 15, 170);
		rsinterface.child(13, 23926, 105, 170); // Task 6
		rsinterface.child(14, 23927, 105, 170);
		rsinterface.child(15, 23929, 58, 243); // Open Text
		rsinterface.child(16, 23930, 47, 24); // Open Text
	}

	public static void collectSell(TextDrawingArea[] TDA) {
		RSInterface rsinterface = addTabInterface(54700);
		int x = 9;
		addSprite(54701, 1, "Interfaces/GE/sellCollect");

		addHoverButton(54702, "Interfaces/GrandExchange/close", 1, 16, 16,
				"Close", 0, 54703, 1);
		addHoveredButton(54703, "Interfaces/GrandExchange/close", 2, 16, 16,
				54704);
		addHoverButton(54758, "Interfaces/GrandExchange/sprite", 25, 29, 23,
				"Back", 0, 54759, 1);
		addHoveredButton(54759, "Interfaces/GrandExchange/sprite", 26, 29, 23,
				54760);
		addText(54769, "Choose an item to exchange", TDA, 0, 0x96731A, false,
				true);
		addText(54770, "Select an item from your invertory to sell.", TDA, 0,
				0x958E60, false, true);
		addText(54771, "0", TDA, 0, 0xB58338, true, true);
		addText(54772, "1 gp", TDA, 0, 0xB58338, true, true);
		addText(54773, "0 gp", TDA, 0, 0xB58338, true, true);
		addHoverButton(54793, "Interfaces/GE/collectNoHover", 1, 40, 36,
				"[GE]", 0, 54794, 1);
		addHoveredButton(54794, "Interfaces/GE/collectHover", 1, 40, 36, 54795);
		addHoverButton(54796, "Interfaces/GE/collectNoHover", 1, 40, 36,
				"[GE]", 0, 54797, 1);
		addHoveredButton(54797, "Interfaces/GE/collectHover", 1, 40, 36, 54798);
		RSInterface add = addInterface(54780);
		addToItemGroup(add, 1, 1, 24, 24, true, "[GE]", "[GE]", "[GE]");
		add = addInterface(54781);
		addToItemGroup(add, 1, 1, 24, 24, true, "[COINS]Collect", "[GE]",
				"[GE]");
		add = addInterface(54782);
		addToItemGroup(add, 1, 1, 24, 24, true, "[ITEM]Collect", "[GE]", "[GE]");
		addText(54784, "", TDA, 0, 0xFFFF00, false, true);
		addText(54785, "", TDA, 0, 0xFFFF00, false, true);
		addText(54787, "N/A", TDA, 0, 0xB58338, false, true);
		addText(54788, "", TDA, 0, 0xFFFF00, true, true);
		addText(54789, "", TDA, 0, 0xFFFF00, true, true);
		addHoverButton(54800, "Interfaces/GE/clickAbort", 1, 20, 20,
				"Abort offer", 0, 54801, 1);
		addHoveredButton(54801, "Interfaces/GE/clickAbort", 2, 20, 20, 54802);
		rsinterface.totalChildren(24);
		rsinterface.child(0, 54701, 4 + x, 23);// 385, 260
		rsinterface.child(1, 54702, 464 + x, 33);// 435, 260
		rsinterface.child(2, 54703, 464 + x, 33);
		rsinterface.child(3, 54758, 19 + x, 284);
		rsinterface.child(4, 54759, 19 + x, 284);
		rsinterface.child(5, 54769, 202 + x, 71);
		rsinterface.child(6, 54770, 202 + x, 98);
		rsinterface.child(7, 54771, 142 + x, 185);
		rsinterface.child(8, 54772, 354 + x, 185);
		rsinterface.child(9, 54773, 252 + x, 246);
		rsinterface.child(10, 54793, 386 + x, 256 + 23);
		rsinterface.child(11, 54794, 386 + x, 256 + 23);
		rsinterface.child(12, 54796, 435 + x, 256 + 23);
		rsinterface.child(13, 54797, 435 + x, 256 + 23);
		rsinterface.child(14, 54780, 97 + x, 97);
		rsinterface.child(15, 54781, 385 + 4 + x, 260 + 23);
		rsinterface.child(16, 54782, 435 + 4 + x, 260 + 23);
		rsinterface.child(17, 54784, 385 + 4 + x, 260 + 23);
		rsinterface.child(18, 54785, 435 + 4 + x, 260 + 23);
		rsinterface.child(19, 54787, 108, 136);
		rsinterface.child(20, 54788, 214 + x, 249 + 23);
		rsinterface.child(21, 54789, 214 + x, 263 + 23);
		rsinterface.child(22, 54800, 345 + x, 250 + 23);
		rsinterface.child(23, 54801, 345 + x, 250 + 23);
	}

	public static void collectBuy(TextDrawingArea[] TDA) {
		RSInterface rsinterface = addTabInterface(53700);
		int x = 9;
		addSprite(53701, 1, "Interfaces/GE/buyCollect");
		addHoverButton(53702, "Interfaces/GrandExchange/close", 1, 16, 16,
				"Close", 0, 53703, 1);
		addHoveredButton(53703, "Interfaces/GrandExchange/close", 2, 16, 16,
				53704);
		addHoverButton(53758, "Interfaces/GrandExchange/sprite", 25, 29, 23,
				"Back", 0, 53759, 1);
		addHoveredButton(53759, "Interfaces/GrandExchange/sprite", 26, 29, 23,
				53760);
		addText(53769, "Choose an item to exchange", TDA, 0, 0x96731A, false,
				true);
		addText(53770, "Select an item from your invertory to sell.", TDA, 0,
				0x958E60, false, true);
		addText(53771, "0", TDA, 0, 0xB58338, true, true);
		addText(53772, "1 gp", TDA, 0, 0xB58338, true, true);
		addText(53773, "0 gp", TDA, 0, 0xB58338, true, true);
		addHoverButton(53793, "Interfaces/GE/collectNoHover", 1, 40, 36,
				"[GE]", 0, 53794, 1);
		addHoveredButton(53794, "Interfaces/GE/collectHover", 1, 40, 36, 53795);
		addHoverButton(53796, "Interfaces/GE/collectNoHover", 1, 40, 36,
				"[GE]", 0, 53797, 1);
		addHoveredButton(53797, "Interfaces/GE/collectHover", 1, 40, 36, 53798);
		RSInterface add = addInterface(53780);
		addToItemGroup(add, 1, 1, 24, 24, true, "[GE]", "[GE]", "[GE]");
		add = addInterface(53781);
		addToItemGroup(add, 1, 1, 24, 24, true, "[ITEM]Collect", "[GE]", "[GE]");
		add = addInterface(53782);
		addToItemGroup(add, 1, 1, 24, 24, true, "[COINS]Collect", "[GE]",
				"[GE]");
		addText(53784, "", TDA, 0, 0xFFFF00, false, true);
		addText(53785, "", TDA, 0, 0xFFFF00, false, true);
		addText(53787, "N/A", TDA, 0, 0xB58338, false, true);
		addText(53788, "", TDA, 0, 0xFFFF00, true, true);
		addText(53789, "", TDA, 0, 0xFFFF00, true, true);
		addHoverButton(53800, "Interfaces/GE/clickAbort", 1, 20, 20,
				"Abort offer", 0, 53801, 1);
		addHoveredButton(53801, "Interfaces/GE/clickAbort", 2, 20, 20, 53802);
		rsinterface.totalChildren(24);
		rsinterface.child(0, 53701, 4 + x, 23);// 385, 260
		rsinterface.child(1, 53702, 464 + x, 33);// 435, 260
		rsinterface.child(2, 53703, 464 + x, 33);
		rsinterface.child(3, 53758, 19 + x, 284);
		rsinterface.child(4, 53759, 19 + x, 284);
		rsinterface.child(5, 53769, 202 + x, 71);
		rsinterface.child(6, 53770, 202 + x, 98);
		rsinterface.child(7, 53771, 142 + x, 185);
		rsinterface.child(8, 53772, 354 + x, 185);
		rsinterface.child(9, 53773, 252 + x, 246);
		rsinterface.child(10, 53793, 386 + x, 256 + 23);
		rsinterface.child(11, 53794, 386 + x, 256 + 23);
		rsinterface.child(12, 53796, 435 + x, 256 + 23);
		rsinterface.child(13, 53797, 435 + x, 256 + 23);
		rsinterface.child(14, 53780, 97 + x, 97);
		rsinterface.child(15, 53781, 385 + 4 + x, 260 + 23);
		rsinterface.child(16, 53782, 435 + 4 + x, 260 + 23);
		rsinterface.child(17, 53784, 385 + 4 + x, 260 + 23);
		rsinterface.child(18, 53785, 435 + 4 + x, 260 + 23);
		rsinterface.child(19, 53787, 108, 136);
		rsinterface.child(20, 53788, 214 + x, 249 + 23);
		rsinterface.child(21, 53789, 214 + x, 263 + 23);
		rsinterface.child(22, 53800, 345 + x, 250 + 23);
		rsinterface.child(23, 53801, 345 + x, 250 + 23);
	}

	public static void quickPrayers(TextDrawingArea[] TDA) {
		int i = 0;
		RSInterface localRSInterface = addTabInterface(20000);
		addSprite(17201, 3, "/Interfaces/QuickPrayer/Sprite");
		addText(17240, "Select your quick prayers:", TDA, 0, 16750623, false,
				true);
		addTransparentSprite(17249, 0, "/Interfaces/QuickPrayer/Sprite", 50);
		int j = 17202;
		for (int k = 630; (j <= 17231) || (k <= 659); ++k) {
			addConfigButton(j, 17200, 2, 1, "/Interfaces/QuickPrayer/Sprite",
					14, 15, "Select", 0, 1, k);
			j++;
		}
		addHoverButton(17241, "/Interfaces/QuickPrayer/Sprite", 4, 190, 24,
				"Confirm Selection", -1, 17242, 1);
		addHoveredButton(17242, "/Interfaces/QuickPrayer/Sprite", 5, 190, 24,
				17243);
		setChildren(58, localRSInterface);
		setBounds(25001, 5, 28, i++, localRSInterface);
		setBounds(25003, 44, 28, i++, localRSInterface);
		setBounds(25005, 79, 31, i++, localRSInterface);
		setBounds(25007, 116, 30, i++, localRSInterface);
		setBounds(25009, 153, 29, i++, localRSInterface);
		setBounds(25011, 5, 68, i++, localRSInterface);
		setBounds(25013, 44, 67, i++, localRSInterface);
		setBounds(25015, 79, 69, i++, localRSInterface);
		setBounds(25017, 116, 70, i++, localRSInterface);
		setBounds(25019, 154, 70, i++, localRSInterface);
		setBounds(25021, 4, 104, i++, localRSInterface);
		setBounds(25023, 44, 107, i++, localRSInterface);
		setBounds(25025, 81, 105, i++, localRSInterface);
		setBounds(25027, 117, 105, i++, localRSInterface);
		setBounds(25029, 156, 107, i++, localRSInterface);
		setBounds(25031, 5, 145, i++, localRSInterface);
		setBounds(25033, 43, 144, i++, localRSInterface);
		setBounds(25035, 83, 144, i++, localRSInterface);
		setBounds(25037, 115, 141, i++, localRSInterface);
		setBounds(25039, 154, 144, i++, localRSInterface);
		setBounds(25041, 5, 180, i++, localRSInterface);
		setBounds(25043, 41, 178, i++, localRSInterface);
		setBounds(25045, 79, 183, i++, localRSInterface);
		setBounds(25047, 116, 178, i++, localRSInterface);
		setBounds(25049, 161, 180, i++, localRSInterface);
		// setBounds(18015, 4, 210, i++, localRSInterface);
		setBounds(25051, 5, 217, i++, localRSInterface);
		// setBounds(18061, 78, 212, i++, localRSInterface);
		// setBounds(18121, 116, 208, i++, localRSInterface);
		setBounds(17249, 0, 25, i++, localRSInterface);
		setBounds(17201, 0, 22, i++, localRSInterface);
		setBounds(17201, 0, 237, i++, localRSInterface);
		setBounds(17202, 2, 25, i++, localRSInterface);
		setBounds(17203, 41, 25, i++, localRSInterface);
		setBounds(17204, 76, 25, i++, localRSInterface);
		setBounds(17205, 113, 25, i++, localRSInterface);
		setBounds(17206, 150, 25, i++, localRSInterface);
		setBounds(17207, 2, 65, i++, localRSInterface);
		setBounds(17208, 41, 65, i++, localRSInterface);
		setBounds(17209, 76, 65, i++, localRSInterface);
		setBounds(17210, 113, 65, i++, localRSInterface);
		setBounds(17211, 150, 65, i++, localRSInterface);
		setBounds(17212, 2, 102, i++, localRSInterface);
		setBounds(17213, 41, 102, i++, localRSInterface);
		setBounds(17214, 76, 102, i++, localRSInterface);
		setBounds(17215, 113, 102, i++, localRSInterface);
		setBounds(17216, 150, 102, i++, localRSInterface);
		setBounds(17217, 2, 141, i++, localRSInterface);
		setBounds(17218, 41, 141, i++, localRSInterface);
		setBounds(17219, 76, 141, i++, localRSInterface);
		setBounds(17220, 113, 141, i++, localRSInterface);
		setBounds(17221, 150, 141, i++, localRSInterface);
		setBounds(17222, 2, 177, i++, localRSInterface);
		setBounds(17223, 41, 177, i++, localRSInterface);
		setBounds(17224, 76, 177, i++, localRSInterface);
		setBounds(17225, 113, 177, i++, localRSInterface);
		setBounds(17226, 150, 177, i++, localRSInterface);
		setBounds(17227, 1, 211, i++, localRSInterface);
		// setBounds(17228, 1, 211, i++, localRSInterface);
		// setBounds(17229, 75, 211, i++, localRSInterface);
		// setBounds(17230, 113, 211, i++, localRSInterface);
		setBounds(17240, 5, 5, i++, localRSInterface);
		setBounds(17241, 0, 237, i++, localRSInterface);
		setBounds(17242, 0, 237, i++, localRSInterface);
	}

	public static void quickCurses(TextDrawingArea[] TDA) {
		int frame = 0;
		RSInterface tab = addTabInterface(22000);
		addSprite(17201, 3, "/Interfaces/QuickPrayer/Sprite");
		addText(17235, "Select your quick curses:", TDA, 0, 16750623, false,
				true);
		addTransparentSprite(17249, 0, "/Interfaces/QuickPrayer/Sprite", 50);
		int j = 17202;
		for (int k = 630; (j <= 17222) || (k <= 656); k++) {
			addConfigButton(j, 17200, 2, 1, "/Interfaces/QuickPrayer/Sprite",
					14, 15, "Select", 0, 1, k);
			j++;
		}
		setChildren(46, tab);
		setBounds(22504, 5, 8 + 17, frame++, tab);
		setBounds(22506, 44, 8 + 20, frame++, tab);
		setBounds(22508, 79, 11 + 19, frame++, tab);
		setBounds(22510, 116, 10 + 18, frame++, tab);
		setBounds(22512, 153, 9 + 20, frame++, tab);
		setBounds(22514, 5, 48 + 18, frame++, tab);
		setBounds(22516, 44, 47 + 21, frame++, tab);
		setBounds(22518, 79, 49 + 20, frame++, tab);
		setBounds(22520, 116, 50 + 19, frame++, tab);
		setBounds(22522, 154, 50 + 20, frame++, tab);
		setBounds(22524, 4, 84 + 21, frame++, tab);
		setBounds(22526, 44, 87 + 19, frame++, tab);
		setBounds(22528, 81, 85 + 20, frame++, tab);
		setBounds(22530, 117, 85 + 20, frame++, tab);
		setBounds(22532, 156, 87 + 18, frame++, tab);
		setBounds(22534, 5, 125 + 19, frame++, tab);
		setBounds(22536, 43, 124 + 19, frame++, tab);
		setBounds(22538, 83, 124 + 20, frame++, tab);
		setBounds(22540, 115, 125 + 21, frame++, tab);
		setBounds(22542, 154, 126 + 22, frame++, tab);
		setBounds(17249, 0, 25, frame++, tab);
		setBounds(17201, 0, 22, frame++, tab);
		setBounds(17201, 0, 237, frame++, tab);
		setBounds(17202, 2, 25, frame++, tab);
		setBounds(17203, 41, 25, frame++, tab);
		setBounds(17204, 76, 25, frame++, tab);
		setBounds(17205, 113, 25, frame++, tab);
		setBounds(17206, 150, 25, frame++, tab);
		setBounds(17207, 2, 65, frame++, tab);
		setBounds(17208, 41, 65, frame++, tab);
		setBounds(17209, 76, 65, frame++, tab);
		setBounds(17210, 113, 65, frame++, tab);
		setBounds(17211, 150, 65, frame++, tab);
		setBounds(17212, 2, 102, frame++, tab);
		setBounds(17213, 41, 102, frame++, tab);
		setBounds(17214, 76, 102, frame++, tab);
		setBounds(17215, 113, 102, frame++, tab);
		setBounds(17216, 150, 102, frame++, tab);
		setBounds(17217, 2, 141, frame++, tab);
		setBounds(17218, 41, 141, frame++, tab);
		setBounds(17219, 76, 141, frame++, tab);
		setBounds(17220, 113, 141, frame++, tab);
		setBounds(17221, 150, 141, frame++, tab);
		setBounds(17235, 5, 5, frame++, tab);
		setBounds(17241, 0, 237, frame++, tab);
		setBounds(17242, 0, 237, frame++, tab);
	}

	public int transparency;

	public static void addTransparentSprite(int id, int spriteId, String spriteName, int opacity) {
		RSInterface tab = interfaceCache[id] = new RSInterface();
		tab.id = id;
		tab.parentID = id;
		tab.type = 9;
		tab.atActionType = 0;
		tab.contentType = 0;
		tab.opacity = (byte)opacity;
		tab.hoverType = 52;
		tab.sprite1 = imageLoader(spriteId, spriteName);
		tab.sprite2 = imageLoader(spriteId, spriteName);
		tab.width = 512;
		tab.height = 334;
		tab.drawsTransparent = true;
	}

	public static void magicTab(TextDrawingArea[] tda) {
		RSInterface tab = addTabInterface(1151);
		RSInterface homeHover = addTabInterface(1196);
		RSInterface spellButtons = interfaceCache[12424];
		int[] spellButton = { 1196, 1199, 1206, 1215, 1224, 1231, 1240, 1249,
				1258, 1267, 1274, 1283, 1573, 1290, 1299, 1308, 1315, 1324,
				1333, 1340, 1349, 1358, 1367, 1374, 1381, 1388, 1397, 1404,
				1583, 12038, 1414, 1421, 1430, 1437, 1446, 1453, 1460, 1469,
				15878, 1602, 1613, 1624, 7456, 1478, 1485, 1494, 1503, 1512,
				1521, 1530, 1544, 1553, 1563, 1593, 1635, 12426, 12436, 12446,
				12456, 6004, 18471 };
		tab.totalChildren(63);
		tab.child(0, 12424, 13, 24);
		for (int i1 = 0; i1 < spellButton.length; i1++) {
			int yPos = i1 > 34 ? 8 : 183;
			tab.child(1, 1195, 13, 24);
			tab.child(i1 + 2, spellButton[i1], 5, yPos);
			addButton(1195, 1, "Magic/Home", "Cast @gre@Home Teleport");
			RSInterface homeButton = interfaceCache[1195];
			homeButton.hoverType = 1196;
		}
		for (int i2 = 0; i2 < spellButton.length; i2++) {
			if (i2 < 60)
				spellButtons.childX[i2] = spellButtons.childX[i2] + 24;
			if (i2 == 6 || i2 == 12 || i2 == 19 || i2 == 35 || i2 == 41
					|| i2 == 44 || i2 == 49 || i2 == 51)
				spellButtons.childX[i2] = 0;
			spellButtons.childY[6] = 24;
			spellButtons.childY[12] = 48;
			spellButtons.childY[19] = 72;
			spellButtons.childY[49] = 96;
			spellButtons.childY[44] = 120;
			spellButtons.childY[51] = 144;
			spellButtons.childY[35] = 170;
			spellButtons.childY[41] = 192;
		}
		homeHover.interfaceShown = true;
		addText(1197, "Level 0: Home Teleport", tda, 1, 0xFE981F, true, true);
		RSInterface homeLevel = interfaceCache[1197];
		homeLevel.width = 174;
		homeLevel.height = 68;
		addText(1198, "A teleport which requires no", tda, 0, 0xAF6A1A, true,
				true);
		addText(18998, "runes and no required level that", tda, 0, 0xAF6A1A,
				true, true);
		addText(18999, "teleports you to the main land.", tda, 0, 0xAF6A1A,
				true, true);
		homeHover.totalChildren(4);
		homeHover.child(0, 1197, 3, 4);
		homeHover.child(1, 1198, 91, 23);
		homeHover.child(2, 18998, 91, 34);
		homeHover.child(3, 18999, 91, 45);
		spellButtons.scrollMax = 0;
		spellButtons.height = 260;
		spellButtons.width = 190;
	}

	private static final int WHITE_TEXT = 0xFFFFFF;
	private static final int GREY_TEXT = 0xB9B855;
	private static final int ORANGE_TEXT = 0xFF981F;

	private static void summoningTab(TextDrawingArea[] tda) {
		final String dir = "Interfaces/Summoning/Tab/SPRITE";
		RSInterface rsi = addTabInterface(18017);
		addButton(18018, 0, dir, 143, 13, "Cast special", 1);
		addText(18019, "S P E C I A L  M O V E", tda, 0, WHITE_TEXT, false,
				false);
		addSprite(18020, 1, dir);
		addFamiliarHead(18021, 75, 50, 875);
		addSprite(18022, 2, dir);
		addConfigButton(18023, 18017, 4, 3, dir, 30, 31, "Cast special", 0, 1,
				330);
		addText(18024, "0", tda, 0, ORANGE_TEXT, false, true);
		addSprite(18025, 5, dir);
		addConfigButton(18026, 18017, 7, 6, dir, 29, 39, "Attack", 0, 1, 333);
		addSprite(18027, 8, dir);
		addText(18028, "None", tda, 2, ORANGE_TEXT, true, false);
		addHoverButton(18029, dir, 9, 38, 38, "Withdraw BoB", -1, 18030, 1);
		addHoveredButton(18030, dir, 10, 38, 38, 18031);
		addHoverButton(18032, dir, 11, 38, 38, "Renew familiar", -1, 18033, 1);
		addHoveredButton(18033, dir, 12, 38, 38, 18034);
		addHoverButton(18035, dir, 13, 38, 38, "Call follower", -1, 18036, 1);
		addHoveredButton(18036, dir, 14, 38, 38, 18037);
		addHoverButton(18038, dir, 15, 38, 38, "Dismiss familiar", -1, 18039, 1);
		addHoveredButton(18039, dir, 16, 38, 38, 18040);
		addSprite(18041, 17, dir);
		addSprite(18042, 18, dir);
		addText(18043, "35.30", tda, 0, GREY_TEXT, false, true);
		addSprite(18044, 19, dir);
		addText(18045, "63/69", tda, 0, GREY_TEXT, false, true);
		setChildren(24, rsi);
		setBounds(18018, 23, 10, 0, rsi);
		setBounds(18019, 43, 12, 1, rsi);
		setBounds(18020, 10, 32, 2, rsi);
		setBounds(18021, 63, 60, 3, rsi);
		setBounds(18022, 11, 32, 4, rsi);
		setBounds(18023, 14, 35, 5, rsi);
		setBounds(18024, 25, 69, 6, rsi);
		setBounds(18025, 138, 32, 7, rsi);
		setBounds(18026, 143, 36, 8, rsi);
		setBounds(18027, 12, 144, 9, rsi);
		setBounds(18028, 93, 146, 10, rsi);
		setBounds(18029, 23, 168, 11, rsi);
		setBounds(18030, 23, 168, 12, rsi);
		setBounds(18032, 75, 168, 13, rsi);
		setBounds(18033, 75, 168, 14, rsi);
		setBounds(18035, 23, 213, 15, rsi);
		setBounds(18036, 23, 213, 16, rsi);
		setBounds(18038, 75, 213, 17, rsi);
		setBounds(18039, 75, 213, 18, rsi);
		setBounds(18041, 130, 168, 19, rsi);
		setBounds(18042, 153, 170, 20, rsi);
		setBounds(18043, 148, 198, 21, rsi);
		setBounds(18044, 149, 213, 22, rsi);
		setBounds(18045, 145, 241, 23, rsi);
	}

	private static void petSummoningTab(TextDrawingArea[] tda) {
		final String dir = "Interfaces/Summoning/Tab/SPRITE";
		RSInterface rsi = addTabInterface(19017);
		addSprite(19018, 1, dir);
		addFamiliarHead(19019, 75, 50, 900);
		addSprite(19020, 8, dir);
		addText(19021, "None", tda, 2, ORANGE_TEXT, true, false);
		addHoverButton(19022, dir, 13, 38, 38, "Call follower", -1, 19023, 1);
		addHoveredButton(19023, dir, 14, 38, 38, 19024);
		addHoverButton(19025, dir, 15, 38, 38, "Dismiss familiar", -1, 19026, 1);
		addHoveredButton(19026, dir, 16, 38, 38, 19027);
		addSprite(19028, 17, dir);
		addSprite(19029, 20, dir);
		addText(19030, "0%", tda, 0, GREY_TEXT, false, true);
		addSprite(19031, 21, dir);
		addText(19032, "0%", tda, 0, WHITE_TEXT, false, true);
		setChildren(13, rsi);
		setBounds(19018, 10, 32, 0, rsi);
		setBounds(19019, 65, 65, 1, rsi);
		setBounds(19020, 12, 145, 2, rsi);
		setBounds(19021, 93, 147, 3, rsi);
		setBounds(19022, 23, 213, 4, rsi);
		setBounds(19023, 23, 213, 5, rsi);
		setBounds(19025, 75, 213, 6, rsi);
		setBounds(19026, 75, 213, 7, rsi);
		setBounds(19028, 130, 168, 8, rsi);
		setBounds(19029, 148, 170, 9, rsi);
		setBounds(19030, 152, 198, 10, rsi);
		setBounds(19031, 149, 220, 11, rsi);
		setBounds(19032, 155, 241, 12, rsi);
	}

	public static void pouches(TextDrawingArea[] tda) {
		RSInterface tab = addTabInterface(22022);
		RSInterface scroll = addTabInterface(22023);
		addSprite(22024, 48, "Summon/SUMMON");
		addHoverButton(22025, "Misc/SPRITE", 9, 21, 21, "Close", 250, 22026, 3);
		addHoveredButton(22026, "Misc/SPRITE", 10, 21, 21, 22027);
		tab.totalChildren(4);
		tab.child(0, 22024, 16, 23);
		tab.child(1, 22025, 476, 33);
		tab.child(2, 22026, 476, 33);
		tab.child(3, 22023, 0, 63);
		int[] data = {// 12158 gold, 12159 green, 12160 crim, 16163 blue
				49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66,
				67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82,
				83, 84, 85 };
		int x = 67, y = 0, r = 0, s = 22028;
		scroll.height = 257;
		scroll.width = 477;
		scroll.scrollMax = 535;
		scroll.totalChildren(37);
		for (int i = 0; i < data.length; i++) {
			if (r == 4) {
				r = 0;
				y += 50;
				x = 67;
			}
			addButton(s, data[i], "Summon/SUMMON", "Make pouch");
			scroll.child(i, s, x, y);
			x += 110;
			s++;
			r++;
		}
	}

	public static void addText(int i, String s, int k, boolean l, boolean m,
			int a, int j) {
		try {
			RSInterface rsinterface = addTabInterface(i);
			rsinterface.parentID = i;
			rsinterface.id = i;
			rsinterface.type = 4;
			rsinterface.atActionType = 0;
			rsinterface.width = 0;
			rsinterface.height = 0;
			rsinterface.contentType = 0;
			rsinterface.aByte254 = 0;
			rsinterface.hoverType = a;
			rsinterface.centerText = l;
			rsinterface.textShadow = m;
			rsinterface.textDrawingAreas = RSInterface.fonts[j];
			rsinterface.message = s;
			rsinterface.textColor = k;
		} catch (Exception e) {
		}
	}

	private static Sprite loadSprite(int i, String s) {

		Sprite sprite;
		try {
			sprite = new Sprite(s + " " + i);
			if (sprite != null) {
				return sprite;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;

		}
		return sprite;
	}

	public Sprite loadSprite(String s, int i) {
		Sprite sprite;
		try {
			sprite = new Sprite(s + " " + i);
			if (sprite != null) {
				return sprite;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;

		}
		return null;
	}

	public static void skilllevel(TextDrawingArea[] tda) {
		RSInterface text = interfaceCache[7202];
		RSInterface attack = interfaceCache[6247];
		RSInterface defence = interfaceCache[6253];
		RSInterface str = interfaceCache[6206];
		RSInterface hits = interfaceCache[6216];
		RSInterface rng = interfaceCache[4443];
		RSInterface pray = interfaceCache[6242];
		RSInterface mage = interfaceCache[6211];
		RSInterface cook = interfaceCache[6226];
		RSInterface wood = interfaceCache[4272];
		RSInterface flet = interfaceCache[6231];
		RSInterface fish = interfaceCache[6258];
		RSInterface fire = interfaceCache[4282];
		RSInterface craf = interfaceCache[6263];
		RSInterface smit = interfaceCache[6221];
		RSInterface mine = interfaceCache[4416];
		RSInterface herb = interfaceCache[6237];
		RSInterface agil = interfaceCache[4277];
		RSInterface thie = interfaceCache[4261];
		RSInterface slay = interfaceCache[12122];
		RSInterface farm = interfaceCache[5267];
		RSInterface rune = interfaceCache[4267];
		RSInterface cons = interfaceCache[7267];
		RSInterface hunt = interfaceCache[8267];
		RSInterface summ = addInterface(9267);
		RSInterface dung = addInterface(10267);
		addSprite(17878, 0, "Interfaces/skillchat/skill");
		addSprite(17879, 1, "Interfaces/skillchat/skill");
		addSprite(17880, 2, "Interfaces/skillchat/skill");
		addSprite(17881, 3, "Interfaces/skillchat/skill");
		addSprite(17882, 4, "Interfaces/skillchat/skill");
		addSprite(17883, 5, "Interfaces/skillchat/skill");
		addSprite(17884, 6, "Interfaces/skillchat/skill");
		addSprite(17885, 7, "Interfaces/skillchat/skill");
		addSprite(17886, 8, "Interfaces/skillchat/skill");
		addSprite(17887, 9, "Interfaces/skillchat/skill");
		addSprite(17888, 10, "Interfaces/skillchat/skill");
		addSprite(17889, 11, "Interfaces/skillchat/skill");
		addSprite(17890, 12, "Interfaces/skillchat/skill");
		addSprite(17891, 13, "Interfaces/skillchat/skill");
		addSprite(17892, 14, "Interfaces/skillchat/skill");
		addSprite(17893, 15, "Interfaces/skillchat/skill");
		addSprite(17894, 16, "Interfaces/skillchat/skill");
		addSprite(17895, 17, "Interfaces/skillchat/skill");
		addSprite(17896, 18, "Interfaces/skillchat/skill");
		addSprite(11897, 19, "Interfaces/skillchat/skill");
		addSprite(17898, 20, "Interfaces/skillchat/skill");
		addSprite(17899, 21, "Interfaces/skillchat/skill");
		addSprite(17900, 22, "Interfaces/skillchat/skill");
		addSprite(17901, 23, "Interfaces/skillchat/skill");
		addSprite(17902, 24, "Interfaces/skillchat/skill");
		setChildren(4, attack);
		setBounds(17878, 20, 30, 0, attack);
		setBounds(4268, 80, 15, 1, attack);
		setBounds(4269, 80, 45, 2, attack);
		setBounds(358, 95, 75, 3, attack);
		setChildren(4, defence);
		setBounds(17879, 20, 30, 0, defence);
		setBounds(4268, 80, 15, 1, defence);
		setBounds(4269, 80, 45, 2, defence);
		setBounds(358, 95, 75, 3, defence);
		setChildren(4, str);
		setBounds(17880, 20, 30, 0, str);
		setBounds(4268, 80, 15, 1, str);
		setBounds(4269, 80, 45, 2, str);
		setBounds(358, 95, 75, 3, str);
		setChildren(4, hits);
		setBounds(17881, 20, 30, 0, hits);
		setBounds(4268, 80, 15, 1, hits);
		setBounds(4269, 80, 45, 2, hits);
		setBounds(358, 95, 75, 3, hits);
		setChildren(4, rng);
		setBounds(17882, 20, 30, 0, rng);
		setBounds(4268, 80, 15, 1, rng);
		setBounds(4269, 80, 45, 2, rng);
		setBounds(358, 95, 75, 3, rng);
		setChildren(4, pray);
		setBounds(17883, 20, 30, 0, pray);
		setBounds(4268, 80, 15, 1, pray);
		setBounds(4269, 80, 45, 2, pray);
		setBounds(358, 95, 75, 3, pray);
		setChildren(4, mage);
		setBounds(17884, 20, 30, 0, mage);
		setBounds(4268, 80, 15, 1, mage);
		setBounds(4269, 80, 45, 2, mage);
		setBounds(358, 95, 75, 3, mage);
		setChildren(4, cook);
		setBounds(17885, 20, 30, 0, cook);
		setBounds(4268, 80, 15, 1, cook);
		setBounds(4269, 80, 45, 2, cook);
		setBounds(358, 95, 75, 3, cook);
		setChildren(4, wood);
		setBounds(17886, 20, 30, 0, wood);
		setBounds(4268, 80, 15, 1, wood);
		setBounds(4269, 80, 45, 2, wood);
		setBounds(358, 95, 75, 3, wood);
		setChildren(4, flet);
		setBounds(17887, 20, 30, 0, flet);
		setBounds(4268, 80, 15, 1, flet);
		setBounds(4269, 80, 45, 2, flet);
		setBounds(358, 95, 75, 3, flet);
		setChildren(4, fish);
		setBounds(17888, 20, 30, 0, fish);
		setBounds(4268, 80, 15, 1, fish);
		setBounds(4269, 80, 45, 2, fish);
		setBounds(358, 95, 75, 3, fish);
		setChildren(4, fire);
		setBounds(17889, 20, 30, 0, fire);
		setBounds(4268, 80, 15, 1, fire);
		setBounds(4269, 80, 45, 2, fire);
		setBounds(358, 95, 75, 3, fire);
		setChildren(4, craf);
		setBounds(17890, 20, 30, 0, craf);
		setBounds(4268, 80, 15, 1, craf);
		setBounds(4269, 80, 45, 2, craf);
		setBounds(358, 95, 75, 3, craf);
		setChildren(4, smit);
		setBounds(17891, 20, 30, 0, smit);
		setBounds(4268, 80, 15, 1, smit);
		setBounds(4269, 80, 45, 2, smit);
		setBounds(358, 95, 75, 3, smit);
		setChildren(4, mine);
		setBounds(17892, 20, 30, 0, mine);
		setBounds(4268, 80, 15, 1, mine);
		setBounds(4269, 80, 45, 2, mine);
		setBounds(358, 95, 75, 3, mine);
		setChildren(4, herb);
		setBounds(17893, 20, 30, 0, herb);
		setBounds(4268, 80, 15, 1, herb);
		setBounds(4269, 80, 45, 2, herb);
		setBounds(358, 95, 75, 3, herb);
		setChildren(4, agil);
		setBounds(17894, 20, 30, 0, agil);
		setBounds(4268, 80, 15, 1, agil);
		setBounds(4269, 80, 45, 2, agil);
		setBounds(358, 95, 75, 3, agil);
		setChildren(4, thie);
		setBounds(17895, 20, 30, 0, thie);
		setBounds(4268, 80, 15, 1, thie);
		setBounds(4269, 80, 45, 2, thie);
		setBounds(358, 95, 75, 3, thie);
		setChildren(4, slay);
		setBounds(17896, 20, 30, 0, slay);
		setBounds(4268, 80, 15, 1, slay);
		setBounds(4269, 80, 45, 2, slay);
		setBounds(358, 95, 75, 3, slay);
		setChildren(3, farm);
		setBounds(4268, 80, 15, 0, farm);
		setBounds(4269, 80, 45, 1, farm);
		setBounds(358, 95, 75, 2, farm);
		setChildren(4, rune);
		setBounds(17898, 20, 30, 0, rune);
		setBounds(4268, 80, 15, 1, rune);
		setBounds(4269, 80, 45, 2, rune);
		setBounds(358, 95, 75, 3, rune);
		setChildren(3, cons);
		setBounds(4268, 80, 15, 0, cons);
		setBounds(4269, 80, 45, 1, cons);
		setBounds(358, 95, 75, 2, cons);
		setChildren(3, hunt);
		setBounds(4268, 80, 15, 0, hunt);
		setBounds(4269, 80, 45, 1, hunt);
		setBounds(358, 95, 75, 2, hunt);
		setChildren(4, summ);
		setBounds(17901, 20, 30, 0, summ);
		setBounds(4268, 80, 15, 1, summ);
		setBounds(4269, 80, 45, 2, summ);
		setBounds(358, 95, 75, 3, summ);
		setChildren(4, dung);
		setBounds(17902, 20, 30, 0, dung);
		setBounds(4268, 80, 15, 1, dung);
		setBounds(4269, 80, 45, 2, dung);
		setBounds(358, 95, 75, 3, dung);
	}

	private static void addFamiliarHead(int interfaceID, int width, int height,
			int zoom) {
		RSInterface rsi = addTabInterface(interfaceID);
		rsi.type = 6;
		rsi.anInt233 = 2;
		rsi.mediaID = 4000;
		rsi.modelZoom = zoom;
		rsi.modelRotation1 = 40;
		rsi.modelRotation2 = 1800;
		rsi.height = height;
		rsi.width = width;
	}

	public static void HotZoneInterface(TextDrawingArea[] TDA) {
		RSInterface RSinterface = addInterface(21300);
		addSprite(21301, 0, "PvP/HOTZONE");
		addText(21305, "1", 0xFF9933, true, true, 52, TDA, 1);
		addText(21306, "2", 0xFF9933, true, true, 52, TDA, 1);
		addText(21307, "3", 0xFF9933, true, true, 52, TDA, 1);
		int last = 4;
		RSinterface.children = new int[last];
		RSinterface.childX = new int[last];
		RSinterface.childY = new int[last];
		setBounds(21301, 402, 285, 0, RSinterface);
		setBounds(21305, 462, 318, 1, RSinterface);
		setBounds(21306, 462, 303, 2, RSinterface);
		setBounds(21307, 462, 288, 3, RSinterface);
	}

	public static void NonHotZoneInterface(TextDrawingArea[] TDA) {
		RSInterface RSinterface = addInterface(21310);
		addSprite(21311, 0, "PvP/NOTHOTZONE");
		addText(21315, "1", 0xFF9933, true, true, 52, TDA, 1);
		addText(21316, "2", 0xFF9933, true, true, 52, TDA, 1);
		addText(21317, "3", 0xFF9933, true, true, 52, TDA, 1);
		int last = 4;
		RSinterface.children = new int[last];
		RSinterface.childX = new int[last];
		RSinterface.childY = new int[last];
		setBounds(21311, 402, 285, 0, RSinterface);
		setBounds(21315, 462, 318, 1, RSinterface);
		setBounds(21316, 462, 303, 2, RSinterface);
		setBounds(21317, 462, 288, 3, RSinterface);
	}

	public static void Buy(TextDrawingArea[] TDA) {
		RSInterface rsinterface = addTabInterface(24600);
		int x = 9;
		addSprite(24601, 0, "Interfaces/GrandExchange/buy");
		addHoverButton(24602, "Interfaces/GrandExchange/close", 1, 16, 16,
				"Close", 0, 24603, 1);
		addHoveredButton(24603, "Interfaces/GrandExchange/close", 2, 16, 16,
				24604);
		addHoverButton(24606, "Interfaces/GrandExchange/sprite", 1, 13, 13,
				"Decrease Quantity", 0, 24607, 1);
		addHoveredButton(24607, "Interfaces/GrandExchange/sprite", 3, 13, 13,
				24608);
		addHoverButton(24610, "Interfaces/GrandExchange/sprite", 2, 13, 13,
				"Increase Quantity", 0, 24611, 1);
		addHoveredButton(24611, "Interfaces/GrandExchange/sprite", 4, 13, 13,
				24612);
		addHoverButton(24614, "Interfaces/GrandExchange/sprite", 5, 35, 25,
				"Add 1", 0, 24615, 1);
		addHoveredButton(24615, "Interfaces/GrandExchange/sprite", 6, 35, 25,
				24616);
		addHoverButton(24618, "Interfaces/GrandExchange/sprite", 7, 35, 25,
				"Add 10", 0, 24619, 1);
		addHoveredButton(24619, "Interfaces/GrandExchange/sprite", 8, 35, 25,
				24620);
		addHoverButton(24622, "Interfaces/GrandExchange/sprite", 9, 35, 25,
				"Add 100", 0, 24623, 1);
		addHoveredButton(24623, "Interfaces/GrandExchange/sprite", 10, 35, 25,
				24624);
		addHoverButton(24626, "Interfaces/GrandExchange/sprite", 11, 35, 25,
				"Add 1000", 0, 24627, 1);
		addHoveredButton(24627, "Interfaces/GrandExchange/sprite", 12, 35, 25,
				24628);
		addHoverButton(24630, "Interfaces/GrandExchange/sprite", 13, 35, 25,
				"Edit Quantity", 7712, 24631, 1);
		addHoveredButton(24631, "Interfaces/GrandExchange/sprite", 14, 35, 25,
				24632);
		addHoverButton(24634, "Interfaces/GrandExchange/sprite", 15, 35, 25,
				"Decrease Price", 0, 24635, 1);
		addHoveredButton(24635, "Interfaces/GrandExchange/sprite", 16, 35, 25,
				24636);
		addHoverButton(24638, "Interfaces/GrandExchange/sprite", 17, 35, 25,
				"Offer Guild Price", 0, 24639, 1);
		addHoveredButton(24639, "Interfaces/GrandExchange/sprite", 18, 35, 25,
				24640);
		addHoverButton(24642, "Interfaces/GrandExchange/sprite", 13, 35, 25,
				"Edit Price", 7714, 24643, 1);
		addHoveredButton(24643, "Interfaces/GrandExchange/sprite", 14, 35, 25,
				24644);
		addHoverButton(24646, "Interfaces/GrandExchange/sprite", 19, 35, 25,
				"Increase Price", 0, 24647, 1);
		addHoveredButton(24647, "Interfaces/GrandExchange/sprite", 20, 35, 25,
				24648);
		addHoverButton(24650, "Interfaces/GrandExchange/sprite", 21, 120, 43,
				"Confirm Offer", 0, 24651, 1);
		addHoveredButton(24651, "Interfaces/GrandExchange/sprite", 22, 120, 43,
				24652);
		addHoverButton(1, 24654, "Interfaces/GrandExchange/sprite", 23, 40, 36,
				"Choose Item", 0, 24655, 1);
		addHoveredButton(24655, "Interfaces/GrandExchange/sprite", 24, 40, 36,
				24656);
		addHoverButton(24658, "Interfaces/GrandExchange/sprite", 25, 29, 23,
				"Back", 0, 24659, 1);
		addHoveredButton(24659, "Interfaces/GrandExchange/sprite", 26, 29, 23,
				24660);
		addHoverButton(24662, "Interfaces/GrandExchange/sprite", 1, 13, 13,
				"Decrease Price", 0, 24663, 1);
		addHoveredButton(24663, "Interfaces/GrandExchange/sprite", 3, 13, 13,
				24664);
		addHoverButton(24665, "Interfaces/GrandExchange/sprite", 2, 13, 13,
				"Increase Price", 0, 24666, 1);
		addHoveredButton(24666, "Interfaces/GrandExchange/sprite", 4, 13, 13,
				24667);
		addText(24669, "Choose an item to exchange", TDA, 0, 0x96731A, false,
				true);
		addText(24670, "Click the icon to the left to search for items.", TDA,
				0, 0x958E60, false, true);
		addText(24671, "0", TDA, 0, 0xB58338, true, true);
		addText(24672, "1 gp", TDA, 0, 0xB58338, true, true);
		addText(24673, "0 gp", TDA, 0, 0xB58338, true, true);
		RSInterface add = addInterface(24680);
		addToItemGroup(add, 1, 1, 24, 24, true, "[GE]", "[GE]", "[GE]");
		addText(24682, "N/A", TDA, 0, 0xB58338, false, true);
		rsinterface.totalChildren(42);
		rsinterface.child(0, 24601, 4 + x, 23);
		rsinterface.child(1, 24602, 464 + x, 33);
		rsinterface.child(2, 24603, 464 + x, 33);
		rsinterface.child(3, 24606, 46 + x, 184);
		rsinterface.child(4, 24607, 46 + x, 184);
		rsinterface.child(5, 24610, 226 + x, 184);
		rsinterface.child(6, 24611, 226 + x, 184);
		rsinterface.child(7, 24614, 43 + x, 208);
		rsinterface.child(8, 24615, 43 + x, 208);
		rsinterface.child(9, 24618, 84 + x, 208);
		rsinterface.child(10, 24619, 84 + x, 208);
		rsinterface.child(11, 24622, 125 + x, 208);
		rsinterface.child(12, 24623, 125 + x, 208);
		rsinterface.child(13, 24626, 166 + x, 208);
		rsinterface.child(14, 24627, 166 + x, 208);
		rsinterface.child(15, 24630, 207 + x, 208);
		rsinterface.child(16, 24631, 207 + x, 208);
		rsinterface.child(17, 24634, 260 + x, 208);
		rsinterface.child(18, 24635, 260 + x, 208);
		rsinterface.child(19, 24638, 316 + x, 208);
		rsinterface.child(20, 24639, 316 + x, 208);
		rsinterface.child(21, 24642, 357 + x, 208);
		rsinterface.child(22, 24643, 357 + x, 208);
		rsinterface.child(23, 24646, 413 + x, 208);
		rsinterface.child(24, 24647, 413 + x, 208);
		rsinterface.child(25, 24650, 191 + x, 273);
		rsinterface.child(26, 24651, 191 + x, 273);
		rsinterface.child(27, 24654, 93 + x, 95);
		rsinterface.child(28, 24655, 93 + x, 95);
		rsinterface.child(29, 24658, 19 + x, 284);
		rsinterface.child(30, 24659, 19 + x, 284);
		rsinterface.child(31, 24662, 260 + x, 184);
		rsinterface.child(32, 24663, 260 + x, 184);
		rsinterface.child(33, 24665, 435 + x, 184);
		rsinterface.child(34, 24666, 435 + x, 184);
		rsinterface.child(35, 24669, 202 + x, 71);
		rsinterface.child(36, 24670, 202 + x, 98);
		rsinterface.child(37, 24671, 142 + x, 185);
		rsinterface.child(38, 24672, 354 + x, 185);
		rsinterface.child(39, 24673, 252 + x, 246);
		rsinterface.child(40, 24680, 97 + x, 97);
		rsinterface.child(41, 24682, 121, 136);
	}

	public static void addToItemGroup(RSInterface rsi, int w, int h, int x,
			int y, boolean actions, String action1, String action2,
			String action3) {
		rsi.width = w;
		rsi.height = h;
		rsi.inv = new int[w * h];
		rsi.invStackSizes = new int[w * h];
		rsi.usableItemInterface = false;
		rsi.isInventoryInterface = false;
		rsi.invSpritePadX = x;
		rsi.invSpritePadY = y;
		rsi.spritesX = new int[20];
		rsi.spritesY = new int[20];
		rsi.sprites = new Sprite[20];
		rsi.actions = new String[5];
		if (actions) {
			rsi.actions[0] = action1;
			rsi.actions[1] = action2;
			rsi.actions[2] = action3;
		}
		rsi.type = 2;
	}

	public static void Sell(TextDrawingArea[] TDA) {
		RSInterface rsinterface = addTabInterface(24700);
		int x = 9;
		addSprite(24701, 0, "Interfaces/GrandExchange/sell");
		addHoverButton(24702, "Interfaces/GrandExchange/close", 1, 16, 16,
				"Close", 0, 24703, 1);
		addHoveredButton(24703, "Interfaces/GrandExchange/close", 2, 16, 16,
				24704);
		addHoverButton(24706, "Interfaces/GrandExchange/sprite", 1, 13, 13,
				"Decrease Quantity", 0, 24707, 1);
		addHoveredButton(24707, "Interfaces/GrandExchange/sprite", 3, 13, 13,
				24708);
		addHoverButton(24710, "Interfaces/GrandExchange/sprite", 2, 13, 13,
				"Increase Quantity", 0, 24711, 1);
		addHoveredButton(24711, "Interfaces/GrandExchange/sprite", 4, 13, 13,
				24712);
		addHoverButton(24714, "Interfaces/GrandExchange/sprite", 5, 35, 25,
				"Sell 1", 0, 24715, 1);
		addHoveredButton(24715, "Interfaces/GrandExchange/sprite", 6, 35, 25,
				24716);
		addHoverButton(24718, "Interfaces/GrandExchange/sprite", 7, 35, 25,
				"Sell 10", 0, 24719, 1);
		addHoveredButton(24719, "Interfaces/GrandExchange/sprite", 8, 35, 25,
				24720);
		addHoverButton(24722, "Interfaces/GrandExchange/sprite", 9, 35, 25,
				"Sell 100", 0, 24723, 1);
		addHoveredButton(24723, "Interfaces/GrandExchange/sprite", 10, 35, 25,
				24724);
		addHoverButton(24726, "Interfaces/GrandExchange/sprite", 29, 35, 25,
				"Sell All", 0, 24727, 1);
		addHoveredButton(24727, "Interfaces/GrandExchange/sprite", 30, 35, 25,
				24728);
		addHoverButton(24730, "Interfaces/GrandExchange/sprite", 13, 35, 25,
				"Edit Quantity", 7713, 24731, 1);
		addHoveredButton(24731, "Interfaces/GrandExchange/sprite", 14, 35, 25,
				24732);
		addHoverButton(24734, "Interfaces/GrandExchange/sprite", 15, 35, 25,
				"Decrease Price", 0, 24735, 1);
		addHoveredButton(24735, "Interfaces/GrandExchange/sprite", 16, 35, 25,
				24736);
		addHoverButton(24738, "Interfaces/GrandExchange/sprite", 17, 35, 25,
				"Offer Guild Price", 0, 24739, 1);
		addHoveredButton(24739, "Interfaces/GrandExchange/sprite", 18, 35, 25,
				24740);
		addHoverButton(24742, "Interfaces/GrandExchange/sprite", 13, 35, 25,
				"Edit Price", 7715, 24743, 1);
		addHoveredButton(24743, "Interfaces/GrandExchange/sprite", 14, 35, 25,
				24744);
		addHoverButton(24746, "Interfaces/GrandExchange/sprite", 19, 35, 25,
				"Increase Price", 0, 24747, 1);
		addHoveredButton(24747, "Interfaces/GrandExchange/sprite", 20, 35, 25,
				24748);
		addHoverButton(24750, "Interfaces/GrandExchange/sprite", 21, 120, 43,
				"Confirm Offer", 0, 24751, 1);
		addHoveredButton(24751, "Interfaces/GrandExchange/sprite", 22, 120, 43,
				24752);
		addHoverButton(24758, "Interfaces/GrandExchange/sprite", 25, 29, 23,
				"Back", 0, 24759, 1);
		addHoveredButton(24759, "Interfaces/GrandExchange/sprite", 26, 29, 23,
				24760);
		addHoverButton(24762, "Interfaces/GrandExchange/sprite", 1, 13, 13,
				"Decrease Price", 0, 24763, 1);
		addHoveredButton(24763, "Interfaces/GrandExchange/sprite", 3, 13, 13,
				24764);
		addHoverButton(24765, "Interfaces/GrandExchange/sprite", 2, 13, 13,
				"Increase Price", 0, 24766, 1);
		addHoveredButton(24766, "Interfaces/GrandExchange/sprite", 4, 13, 13,
				24767);
		addText(24769, "Choose an item to exchange", TDA, 0, 0x96731A, false,
				true);
		addText(24770, "Select an item from your invertory to sell.", TDA, 0,
				0x958E60, false, true);
		addText(24771, "0", TDA, 0, 0xB58338, true, true);
		addText(24772, "1 gp", TDA, 0, 0xB58338, true, true);
		addText(24773, "0 gp", TDA, 0, 0xB58338, true, true);
		RSInterface add = addInterface(24780);
		addToItemGroup(add, 1, 1, 24, 24, true, "[GE]", "[GE]", "[GE]");
		addText(24782, "N/A", TDA, 0, 0xB58338, false, true);
		rsinterface.totalChildren(40);
		rsinterface.child(0, 24701, 4 + x, 23);
		rsinterface.child(1, 24702, 464 + x, 33);
		rsinterface.child(2, 24703, 464 + x, 33);
		rsinterface.child(3, 24706, 46 + x, 184);
		rsinterface.child(4, 24707, 46 + x, 184);
		rsinterface.child(5, 24710, 226 + x, 184);
		rsinterface.child(6, 24711, 226 + x, 184);
		rsinterface.child(7, 24714, 43 + x, 208);
		rsinterface.child(8, 24715, 43 + x, 208);
		rsinterface.child(9, 24718, 84 + x, 208);
		rsinterface.child(10, 24719, 84 + x, 208);
		rsinterface.child(11, 24722, 125 + x, 208);
		rsinterface.child(12, 24723, 125 + x, 208);
		rsinterface.child(13, 24726, 166 + x, 208);
		rsinterface.child(14, 24727, 166 + x, 208);
		rsinterface.child(15, 24730, 207 + x, 208);
		rsinterface.child(16, 24731, 207 + x, 208);
		rsinterface.child(17, 24734, 260 + x, 208);
		rsinterface.child(18, 24735, 260 + x, 208);
		rsinterface.child(19, 24738, 316 + x, 208);
		rsinterface.child(20, 24739, 316 + x, 208);
		rsinterface.child(21, 24742, 357 + x, 208);
		rsinterface.child(22, 24743, 357 + x, 208);
		rsinterface.child(23, 24746, 413 + x, 208);
		rsinterface.child(24, 24747, 413 + x, 208);
		rsinterface.child(25, 24750, 191 + x, 273);
		rsinterface.child(26, 24751, 191 + x, 273);
		rsinterface.child(27, 24758, 19 + x, 284);
		rsinterface.child(28, 24759, 19 + x, 284);
		rsinterface.child(29, 24762, 260 + x, 184);
		rsinterface.child(30, 24763, 260 + x, 184);
		rsinterface.child(31, 24765, 435 + x, 184);
		rsinterface.child(32, 24766, 435 + x, 184);
		rsinterface.child(33, 24769, 202 + x, 71);
		rsinterface.child(34, 24770, 202 + x, 98);
		rsinterface.child(35, 24771, 142 + x, 185);
		rsinterface.child(36, 24772, 354 + x, 185);
		rsinterface.child(37, 24773, 252 + x, 246);
		rsinterface.child(38, 24780, 97 + x, 97);
		rsinterface.child(39, 24782, 121, 136);
	}

	public static void BuyandSell(TextDrawingArea[] TDA) {
		RSInterface Interface = addTabInterface(24500);
		setChildren(51, Interface);
		addHoverButton(24541, "b", 3, 138, 108, "Abort offer", 0, 24542, 1);
		addHoverButton(24543, "b", 3, 138, 108, "View offer", 0, 24544, 1);
		addHoverButton(24545, "b", 3, 138, 108, "Abort offer", 0, 24546, 1);
		addHoverButton(24547, "b", 3, 138, 108, "View offer", 0, 24548, 1);
		addHoverButton(24549, "b", 3, 138, 108, "Abort offer", 0, 24550, 1);
		addHoverButton(24551, "b", 3, 138, 108, "View offer", 0, 24552, 1);
		addHoverButton(24553, "b", 3, 138, 108, "Abort offer", 0, 24554, 1);
		addHoverButton(24555, "b", 3, 138, 108, "View offer", 0, 24556, 1);
		addHoverButton(24557, "b", 3, 138, 108, "Abort offer", 0, 24558, 1);
		addHoverButton(24559, "b", 3, 138, 108, "View offer", 0, 24560, 1);
		addHoverButton(24561, "b", 3, 138, 108, "Abort offer", 0, 24562, 1);
		addHoverButton(24563, "b", 3, 138, 108, "View offer", 0, 24564, 1);
		addSprite(1, 24579, 1, "b", false);
		addSprite(1, 24580, 1, "b", false);
		addSprite(1, 24581, 1, "b", false);
		addSprite(1, 24582, 1, "b", false);
		addSprite(1, 24583, 1, "b", false);
		addSprite(1, 24584, 1, "b", false);
		addSprite(24501, 1, "Interfaces/GE/NEW");
		addHoverButton(24502, "Interfaces/GE/SPRITE", 1, 21, 21, "Close", 250,
				24503, 3);
		addHoveredButton(24503, "Interfaces/GE/SPRITE", 3, 21, 21, 24504);
		addHoverButton(24505, "Interfaces/GE/NEW", 2, 50, 50, "Buy", 0, 24506,
				1);
		addHoveredButton(24506, "Interfaces/GE/NEW", 4, 50, 50, 24507);
		addHoverButton(24508, "Interfaces/GE/NEW", 2, 50, 50, "Buy", 0, 24509,
				1);
		addHoveredButton(24509, "Interfaces/GE/NEW", 4, 50, 50, 24510);
		addHoverButton(24514, "Interfaces/GE/NEW", 2, 50, 50, "Buy", 0, 24515,
				1);
		addHoveredButton(24515, "Interfaces/GE/NEW", 4, 50, 50, 24516);
		addHoverButton(24517, "Interfaces/GE/NEW", 2, 50, 50, "Buy", 0, 24518,
				1);
		addHoveredButton(24518, "Interfaces/GE/NEW", 4, 50, 50, 24519);
		addHoverButton(24520, "Interfaces/GE/NEW", 2, 50, 50, "Buy", 0, 24521,
				1);
		addHoveredButton(24521, "Interfaces/GE/NEW", 4, 50, 50, 24522);
		addHoverButton(24523, "Interfaces/GE/NEW", 2, 50, 50, "Buy", 0, 24524,
				1);
		addHoveredButton(24524, "Interfaces/GE/NEW", 4, 50, 50, 24525);
		addHoverButton(24511, "Interfaces/GE/NEW", 3, 50, 50, "Sell", 0, 24512,
				1);
		addHoveredButton(24512, "Interfaces/GE/NEW", 5, 50, 50, 24513);
		addHoverButton(24526, "Interfaces/GE/NEW", 3, 50, 50, "Sell", 0, 24527,
				1);
		addHoveredButton(24527, "Interfaces/GE/NEW", 5, 50, 50, 24528);
		addHoverButton(24529, "Interfaces/GE/NEW", 3, 50, 50, "Sell", 0, 24530,
				1);
		addHoveredButton(24530, "Interfaces/GE/NEW", 5, 50, 50, 24531);
		addHoverButton(24532, "Interfaces/GE/NEW", 3, 50, 50, "Sell", 0, 24533,
				1);
		addHoveredButton(24533, "Interfaces/GE/NEW", 5, 50, 50, 24534);
		addHoverButton(24535, "Interfaces/GE/NEW", 3, 50, 50, "Sell", 0, 24536,
				1);
		addHoveredButton(24536, "Interfaces/GE/NEW", 5, 50, 50, 24537);
		addHoverButton(24538, "Interfaces/GE/NEW", 3, 50, 50, "Sell", 0, 24539,
				1);
		addHoveredButton(24539, "Interfaces/GE/NEW", 5, 50, 50, 24540);

		RSInterface add = addInterface(24567);
		addToItemGroup(add, 1, 1, 24, 24, true, "[GE]", "[GE]", "[GE]");
		add = addInterface(24569);
		addToItemGroup(add, 1, 1, 24, 24, true, "[GE]", "[GE]", "[GE]");
		add = addInterface(24571);
		addToItemGroup(add, 1, 1, 24, 24, true, "[GE]", "[GE]", "[GE]");
		add = addInterface(24573);
		addToItemGroup(add, 1, 1, 24, 24, true, "[GE]", "[GE]", "[GE]");
		add = addInterface(24575);
		addToItemGroup(add, 1, 1, 24, 24, true, "[GE]", "[GE]", "[GE]");
		add = addInterface(24577);
		addToItemGroup(add, 1, 1, 24, 24, true, "[GE]", "[GE]", "[GE]");

		setBounds(24541, 30, 74, 0, Interface);
		setBounds(24543, 30, 74, 1, Interface);
		setBounds(24545, 186, 74, 2, Interface);
		setBounds(24547, 186, 74, 3, Interface);
		setBounds(24549, 342, 74, 4, Interface);
		setBounds(24551, 342, 74, 5, Interface);
		setBounds(24553, 30, 194, 6, Interface);
		setBounds(24555, 30, 194, 7, Interface);
		setBounds(24557, 186, 194, 8, Interface);
		setBounds(24559, 186, 194, 9, Interface);
		setBounds(24561, 339, 194, 10, Interface);
		setBounds(24563, 339, 194, 11, Interface);
		setBounds(24501, 4, 23, 12, Interface);
		setBounds(24579, 30 + 6, 74 + 30, 13, Interface);
		setBounds(24580, 186 + 6, 74 + 30, 14, Interface);
		setBounds(24581, 342 + 6, 74 + 30, 15, Interface);
		setBounds(24582, 30 + 6, 194 + 30, 16, Interface);
		setBounds(24583, 186 + 6, 194 + 30, 17, Interface);
		setBounds(24584, 342 + 6, 194 + 30, 18, Interface);
		setBounds(24502, 480, 32, 19, Interface);
		setBounds(24503, 480, 32, 20, Interface);
		setBounds(24505, 45, 115, 21, Interface);
		setBounds(24506, 45, 106, 22, Interface);
		setBounds(24508, 45, 234, 23, Interface);
		setBounds(24509, 45, 225, 24, Interface);
		setBounds(24511, 105, 115, 25, Interface);
		setBounds(24512, 105, 106, 26, Interface);
		setBounds(24514, 358, 115, 27, Interface);
		setBounds(24515, 358, 106, 28, Interface);
		setBounds(24517, 202, 234, 29, Interface);
		setBounds(24518, 202, 225, 30, Interface);
		setBounds(24520, 358, 234, 31, Interface);
		setBounds(24521, 358, 225, 32, Interface);
		setBounds(24523, 202, 115, 33, Interface);
		setBounds(24524, 202, 106, 34, Interface);
		setBounds(24526, 261, 115, 35, Interface);
		setBounds(24527, 261, 106, 36, Interface);
		setBounds(24529, 417, 115, 37, Interface);
		setBounds(24530, 417, 106, 38, Interface);
		setBounds(24532, 105, 234, 39, Interface);
		setBounds(24533, 105, 225, 40, Interface);
		setBounds(24535, 261, 234, 41, Interface);
		setBounds(24536, 261, 225, 42, Interface);
		setBounds(24538, 417, 234, 43, Interface);
		setBounds(24539, 417, 225, 44, Interface);

		setBounds(24567, 39, 106, 45, Interface);
		setBounds(24569, 46 + 156 - 7, 114 - 7, 46, Interface);
		setBounds(24571, 46 + 156 + 156 - 7, 114 - 7, 47, Interface);
		setBounds(24573, 39, 234 - 7, 48, Interface);
		setBounds(24575, 46 + 156 - 7, 234 - 7, 49, Interface);
		setBounds(24577, 46 + 156 + 156 - 7, 234 - 7, 50, Interface);

	}

	public static void addHoverButton(int a, int i, String imageName, int j,
			int width, int height, String text, int contentType, int hoverOver,
			int aT) {// hoverable button
		RSInterface tab = addTabInterface(i);
		tab.id = i;
		tab.parentID = i;
		tab.type = 5;
		tab.atActionType = aT;
		tab.contentType = contentType;
		tab.opacity = 0;
		tab.hoverType = hoverOver;
		tab.sprite1 = imageLoader(j, imageName);
		tab.sprite2 = imageLoader(j, imageName);
		if (a == 1) {
			tab.setSprite = imageLoader(40, "Sprite");
			tab.savedFirstSprite = imageLoader(j, imageName);
		}
		tab.width = width;
		tab.height = height;
		tab.tooltip = text;
	}

	public static void addSprite(int a, int id, int spriteId,
			String spriteName, boolean l) {
		RSInterface tab = interfaceCache[id] = new RSInterface();
		tab.id = id;
		tab.parentID = id;
		tab.type = 5;
		tab.atActionType = 0;
		tab.contentType = 0;
		tab.opacity = (byte) 0;
		tab.hoverType = 52;
		tab.sprite1 = imageLoader(spriteId, spriteName);
		tab.sprite2 = imageLoader(spriteId, spriteName);
		if (a == 1) {
			tab.setSprite = imageLoader(40, "Interfaces/GE/img");
			tab.savedFirstSprite = imageLoader(spriteId, spriteName);
		}
		tab.width = 512;
		tab.height = 334;
	}

	public static void beastOfBurden(TextDrawingArea[] wid) {
		RSInterface familiarInventory = addTabInterface(24000)
				.totalChildrenReturn(4).childReturn(0, 24001, 104 - 25, 16);
		familiarInventory.addSprite(24001, 0, "inv");
		familiarInventory.childReturn(1, 24002, 95, 59).addInventoryItemGroup(
				24002, 5, 6);
		familiarInventory.childReturn(2, 24003, 216 - 25, 25).addText(24003,
				"Familiar's Inventory", wid, 2, 0xFF8C00, false, true);
		familiarInventory.childReturn(3, 24004, 445 - 25, 27).addButton(24004,
				1, "inv", "Close");
	}

	public static void beastOfBurden2(TextDrawingArea[] wid) {
		RSInterface familiarInventory = addTabInterface(24005)
				.totalChildrenReturn(1).childReturn(0, 24006, 0, 0);
		familiarInventory.addInventoryItemGroup2(24006, 7, 4);
	}

	public static void addInventoryItemGroup(int id, int h, int w) {
		RSInterface Tab = interfaceCache[id] = new RSInterface();
		Tab.inv = new int[w * h];
		Tab.invStackSizes = new int[w * h];
		for (int i1 = 0; i1 < w * h; i1++) {
			Tab.invStackSizes[i1] = 0; // inv item stack size
			Tab.inv[i1] = 0; // inv item ids
		}
		Tab.spritesY = new int[30];
		Tab.spritesX = new int[30];
		int[] rowX = { 0, 22, 44, 66, 88, 110, 0, 22, 44, 66, 88, 110, 0, 22,
				44, 66, 88, 110, 0, 22, 44, 66, 88, 110, 0, 22, 44, 66, 88, 110 };
		int[] rowY = { 0, 0, 0, 0, 0, 0, 22, 22, 22, 22, 22, 22, 44, 44, 44,
				44, 44, 44, 66, 66, 66, 66, 66, 66, 88, 88, 88, 88, 88, 88 };
		for (int i2 = 0; i2 < 30; i2++) {
			Tab.spritesY[i2] = rowY[i2];
			Tab.spritesX[i2] = rowX[i2];
		}
		Tab.actions = new String[] { "Withdraw 1", "Withdraw 5", "Withdraw 10",
				"Withdraw All", null };
		Tab.width = w;
		Tab.hoverType = -1;
		Tab.parentID = id;
		Tab.id = id;
		Tab.scrollMax = 0;
		Tab.type = 2;
		Tab.height = h;
	}

	public static void addInventoryItemGroup2(int id, int h, int w) {
		RSInterface Tab = interfaceCache[id] = new RSInterface();
		Tab.inv = new int[w * h];
		Tab.invStackSizes = new int[w * h];
		for (int i1 = 0; i1 < w * h; i1++) {
			Tab.invStackSizes[i1] = 0; // inv item stack size
			Tab.inv[i1] = 0; // inv item ids
		}
		Tab.spritesY = new int[28];
		Tab.spritesX = new int[28];
		for (int i2 = 0; i2 < 28; i2++) {
			Tab.spritesY[i2] = 8;
			Tab.spritesX[i2] = 16;
		}
		Tab.invSpritePadX = 10;
		Tab.invSpritePadY = 4;
		Tab.actions = new String[] { "Store 1", "Store 5", "Store 10",
				"Store All", null };
		Tab.width = w;
		Tab.hoverType = -1;
		Tab.parentID = id;
		Tab.id = id;
		Tab.scrollMax = 0;
		Tab.type = 2;
		Tab.height = h;
	}

	public RSInterface totalChildrenReturn(int total) {
		this.children = new int[total];
		this.childX = new int[total];
		this.childY = new int[total];
		return this;
	}

	public RSInterface childReturn(int index, int id, int x, int y) {
		this.children[index] = id;
		this.childX[index] = x;
		this.childY[index] = y;
		return this;
	}

	/*
	 * /* Start of 602 Skill Tab
	 */
	public static TextDrawingArea[] fonts;

	public static void skillTab602(TextDrawingArea[] TDA) {
		RSInterface skill = addInterface(3917);
		addText(27203, "99", 0xFFFF00, false, true, -1, TDA, 0);
		addText(27204, "99", 0xFFFF00, false, true, -1, TDA, 0);
		addText(27205, "99", 0xFFFF00, false, true, -1, TDA, 0);
		addText(27206, "99", 0xFFFF00, false, true, -1, TDA, 0);
		int[] logoutID = { 2450, 2451, 2452 };
		int[] logoutID2 = { 2458 };
		for (int i : logoutID) {
			RSInterface Logout = interfaceCache[i];
			Logout.textColor = 0xFF981F;
			Logout.contentType = 0;
		}
		for (int i : logoutID2) {
			RSInterface Logout = interfaceCache[i];
			Logout.contentType = 0;
		}
		skill.totalChildren(4);
		skill.child(0, 27203, 158, 175);
		skill.child(1, 27204, 171, 186);
		skill.child(2, 27205, 158, 203);
		skill.child(3, 27206, 171, 214);
		String[] spriteNames = { "Attack", "HP", "Mine", "Strength", "Agility",
				"Smith", "Defence", "Herblore", "Fish", "Range", "Thief",
				"Cook", "Prayer", "Craft", "Fire", "Mage", "Fletch", "Wood",
				"Rune", "Slay", "Farm", "Construction", "Hunter", "Summon",
		"Dungeon" };
		int[] buttons = { 8654, 8655, 8656, 8657, 8658, 8659, 8660, 8861, 8662,
				8663, 8664, 8665, 8666, 8667, 8668, 8669, 8670, 8671, 8672,
				12162, 13928, 27123, 27124, 27125, 27126 };
		int[] hovers = { 4040, 4076, 4112, 4046, 4082, 4118, 4052, 4088, 4124,
				4058, 4094, 4130, 4064, 4100, 4136, 4070, 4106, 4142, 4160,
				2832, 13917, 19005, 19006, 19007, 19008 };
		int[][] text = { { 4004, 4005 }, { 4016, 4017 }, { 4028, 4029 },
				{ 4006, 4007 }, { 4018, 4019 }, { 4030, 4031 }, { 4008, 4009 },
				{ 4020, 4021 }, { 4032, 4033 }, { 4010, 4011 }, { 4022, 4023 },
				{ 4034, 4035 }, { 4012, 4013 }, { 4024, 4025 }, { 4036, 4037 },
				{ 4014, 4015 }, { 4026, 4027 }, { 4038, 4039 }, { 4152, 4153 },
				{ 12166, 12167 }, { 13926, 13927 }, { 18165, 18169 },
				{ 18166, 18170 }, { 18167, 18171 }, { 18168, 18172 } };
		int[] icons = { 3965, 3966, 3967, 3968, 3969, 3970, 3971, 3972, 3973,
				3974, 3975, 3976, 3977, 3978, 3979, 3980, 3981, 3982, 4151,
				12165, 13925, 27127, 27128, 27129, 27130 };
		int[][] buttonCoords = { { 4, 4 }, { 66, 4 }, { 128, 4 }, { 4, 32 },
				{ 66, 32 }, { 128, 32 }, { 4, 60 }, { 66, 60 }, { 128, 60 },
				{ 4, 88 }, { 66, 88 }, { 128, 88 }, { 4, 116 }, { 66, 116 },
				{ 128, 116 }, { 4, 144 }, { 66, 144 }, { 128, 144 },
				{ 4, 172 }, { 66, 172 }, { 128, 172 }, { 4, 200 }, { 66, 200 },
				{ 128, 200 }, { 4, 229 } };
		int[][] iconCoords = { { 6, 6 }, { 69, 7 }, { 131, 6 }, { 9, 34 },
				{ 68, 33 }, { 131, 36 }, { 9, 64 }, { 67, 63 }, { 131, 61 },
				{ 7, 91 }, { 68, 94 }, { 133, 90 }, { 6, 118 }, { 70, 120 },
				{ 130, 118 }, { 6, 147 }, { 69, 146 }, { 132, 146 },
				{ 6, 173 }, { 69, 173 }, { 130, 174 }, { 6, 202 }, { 69, 201 },
				{ 131, 202 }, { 6, 230 } };
		int[][] textCoords = { { 31, 7, 44, 18 }, { 93, 7, 106, 18 },
				{ 155, 7, 168, 18 }, { 31, 35, 44, 46 }, { 93, 35, 106, 46 },
				{ 155, 35, 168, 46 }, { 31, 63, 44, 74 }, { 93, 63, 106, 74 },
				{ 155, 63, 168, 74 }, { 31, 91, 44, 102 },
				{ 93, 91, 106, 102 }, { 155, 91, 168, 102 },
				{ 31, 119, 44, 130 }, { 93, 119, 106, 130 },
				{ 155, 119, 168, 130 }, { 31, 149, 44, 158 },
				{ 93, 147, 106, 158 }, { 155, 147, 168, 158 },
				{ 31, 175, 44, 186 }, { 93, 175, 106, 186 },
				{ 155, 175, 168, 186 }, { 31, 203, 44, 214 },
				{ 93, 203, 106, 214 }, { 155, 203, 168, 214 },
				{ 31, 231, 44, 242 } };
		int[][] newText = { { 37165, 37166, 37167, 37168 },
				{ 37169, 37170, 37171, 37172 } };
		for (int i = 0; i < hovers.length; i++) {
			createSkillHover(hovers[i], 205 + i);
			addSkillButton(buttons[i]);
			addImage(icons[i], "Skill/" + spriteNames[i]);
		}
		for (int i = 0; i < 4; i++) {
			addSkillText(newText[0][i], false, i + 21);
			addSkillText(newText[1][i], true, i + 21);
		}
		skill.children(icons.length + (text.length * 2) + hovers.length
				+ buttons.length + 1);
		int frame = 0;
		RSInterface totalLevel = interfaceCache[3984];
		totalLevel.message = "@yel@Total Level: %1";
		totalLevel.textDrawingAreas = fonts[2];
		skill.child(frame, 3984, 74, 237);
		frame++;
		for (int i = 0; i < buttons.length; i++) {
			skill.child(frame, buttons[i], buttonCoords[i][0],
					buttonCoords[i][1]);
			frame++;
		}
		for (int i = 0; i < icons.length; i++) {
			skill.child(frame, icons[i], iconCoords[i][0], iconCoords[i][1]);
			frame++;
		}
		for (int i = 0; i < text.length; i++) {
			skill.child(frame, text[i][0], textCoords[i][0], textCoords[i][1]);
			frame++;
		}
		for (int i = 0; i < text.length; i++) {
			skill.child(frame, text[i][1], textCoords[i][2], textCoords[i][3]);
			frame++;
		}
		for (int i = 0; i < hovers.length; i++) {
			skill.child(frame, hovers[i], buttonCoords[i][0],
					buttonCoords[i][1]);
			frame++;
		}
	}

	public void children(int total) {
		children = new int[total];
		childX = new int[total];
		childY = new int[total];
	}

	public static void createSkillHover(int id, int x) {
		RSInterface hover = addInterface(id);
		hover.type = 10;
		hover.message = "TESTING!";
		hover.contentType = x;
		hover.width = 60;
		hover.height = 28;
		hover.inventoryHover = true;
	}

	public static void addImage(int id, String s) {
		RSInterface image = addInterface(id);
		image.type = 5;
		image.atActionType = 0;
		image.contentType = 0;
		image.width = 100;
		image.height = 100;
		image.sprite1 = getSprite(s);
	}

	public static void addSkillText(int id, boolean max, int skill) {
		RSInterface text = addInterface(id);
		text.id = id;
		text.parentID = id;
		text.type = 4;
		text.atActionType = 0;
		text.width = 15;
		text.height = 12;
		text.textDrawingAreas = fonts[0];
		text.textShadow = true;
		text.centerText = true;
		text.textColor = 16776960;
		if (!max) {
			text.valueIndexArray = new int[1][];
			text.valueIndexArray[0] = new int[3];
			text.valueIndexArray[0][0] = 1;
			text.valueIndexArray[0][1] = skill;
			text.valueIndexArray[0][2] = 0;
		} else {
			text.valueIndexArray = new int[2][];
			text.valueIndexArray[0] = new int[3];
			text.valueIndexArray[0][0] = 1;
			text.valueIndexArray[0][1] = skill;
			text.valueIndexArray[0][2] = 0;
			text.valueIndexArray[1] = new int[1];
			text.valueIndexArray[1][0] = 0;
		}
		text.message = "%1";
	}

	public static Sprite getSprite(String s) {
		Sprite image;
		try {
			image = new Sprite(s);
			if (image != null) {
				return image;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return image;
	}

	public static void addSkillButton(int id) {
		RSInterface button = addInterface(id);
		button.type = 5;
		button.atActionType = 5;
		button.contentType = 0;
		button.width = 60;
		button.height = 27;
		button.sprite1 = CustomSpriteLoader(33225, "");
		button.sprite1 = getSprite("Skill/Button");
		button.tooltip = "View";
	}

	/*
	 * End of 602 Skill Tab
	 */

	public static void godwars(TextDrawingArea[] tda) {
		RSInterface tab = addTabInterface(14500);
		addText(14501, "NPC Killcount", tda, 1, 0xff9040, false, true);
		addText(14502, "Armadyl kills", tda, 1, 0xff9040, false, true);
		addText(14503, "Bandos kills", tda, 1, 0xff9040, false, true);
		addText(14504, "Saradomin kills", tda, 1, 0xff9040, false, true);
		addText(14505, "Zamorak kills", tda, 1, 0xff9040, false, true);
		addText(14506, "", tda, 1, 0x66FFFF, true, true);
		addText(14507, "", tda, 1, 0x66FFFF, true, true);
		addText(14508, "", tda, 1, 0x66FFFF, true, true);
		addText(14509, "", tda, 1, 0x66FFFF, true, true);
		int[][] configs = { { 14501, 381, 9 }, { 14502, 381, 33 },
				{ 14503, 381, 47 }, { 14504, 381, 61 }, { 14505, 381, 75 },
				{ 14506, 482, 33 }, { 14507, 482, 47 }, { 14508, 482, 61 },
				{ 14509, 482, 75 } };
		tab.totalChildren(9);
		for (int i = 0; i < configs.length; i++) {
			tab.child(i, configs[i][0], configs[i][1], configs[i][2]);
		}
	}

	public static void ancientMagicTab(TextDrawingArea[] tda) {
		RSInterface tab = addInterface(12855);
		addButton(12856, 1, "Magic/Home", "Cast @gre@Home Teleport");
		RSInterface homeButton = interfaceCache[12856];
		homeButton.hoverType = 1196;
		int[] itfChildren = { 12856, 12939, 12987, 13035, 12901, 12861, 13045,
				12963, 13011, 13053, 12919, 12881, 13061, 12951, 12999, 13069,
				12911, 12871, 13079, 12975, 13023, 13087, 12929, 12891, 13095,
				1196, 12940, 12988, 13036, 12902, 12862, 13046, 12964, 13012,
				13054, 12920, 12882, 13062, 12952, 13000, 13070, 12912, 12872,
				13080, 12976, 13024, 13088, 12930, 12892, 13096 };
		tab.totalChildren(itfChildren.length);
		for (int i1 = 0, xPos = 18, yPos = 8; i1 < itfChildren.length; i1++, xPos += 45) {
			if (xPos > 175) {
				xPos = 18;
				yPos += 28;
			}
			if (i1 < 25)
				tab.child(i1, itfChildren[i1], xPos, yPos);
			if (i1 > 24) {
				yPos = i1 < 41 ? 181 : 1;
				tab.child(i1, itfChildren[i1], 4, yPos);
			}
		}
	}

	public void drawBlackBox(int xPos, int yPos) {
		DrawingArea.method335(0x000000, yPos - 2, 178, 72, 220, xPos - 2);// drawPixelsWithOpacity
		DrawingArea.fillPixels(xPos - 1, 177, 71, 0x2E2B23, yPos - 1);// drawUnfilledPixels/method335
		DrawingArea.fillPixels(xPos - 2, 177, 71, 0x726451, yPos - 2);// drawUnfilledPixels/method335
	}

	public static void addButton(int id, int sid, String spriteName,
			String tooltip) {
		RSInterface tab = interfaceCache[id] = new RSInterface();
		tab.id = id;
		tab.parentID = id;
		tab.type = 5;
		tab.atActionType = 1;
		tab.contentType = 0;
		tab.opacity = (byte) 0;
		tab.hoverType = 52;
		tab.sprite1 = imageLoader(sid, spriteName);
		tab.sprite2 = imageLoader(sid, spriteName);
		tab.width = tab.sprite1.myWidth;
		tab.height = tab.sprite2.myHeight;
		tab.tooltip = tooltip;
	}

	public static void prayerTab(TextDrawingArea[] tda) {
		RSInterface rsinterface = addInterface(5608);
		int i = 0;
		int j = 0;
		byte byte0 = 10;
		byte byte1 = 50;
		byte byte2 = 10;
		byte byte3 = 86;
		byte byte4 = 10;
		byte byte5 = 122;
		byte byte6 = 10;
		char c = '\237';
		byte byte7 = 10;
		byte byte8 = 86;
		int k = 1;
		byte byte9 = 52;
		addText(687, "", 0xff981f, false, true, -1, tda, 1);
		addSprite(25105, 0, "Interfaces/PrayerTab/PRAYERICON");
		addPrayerWithTooltip(25000, 0, 83, 0, j, 25052,
				"Activate @lre@Thick Skin");
		j++;
		addPrayerWithTooltip(25002, 0, 84, 3, j, 25054,
				"Activate @lre@Burst of Strength");
		j++;
		addPrayerWithTooltip(25004, 0, 85, 6, j, 25056,
				"Activate @lre@Clarity of Thought");
		j++;
		addPrayerWithTooltip(25006, 0, 601, 7, j, 25058,
				"Activate @lre@Sharp Eye");
		j++;
		addPrayerWithTooltip(25008, 0, 602, 8, j, 25060,
				"Activate @lre@Mystic Will");
		j++;
		addPrayerWithTooltip(25010, 0, 86, 9, j, 25062,
				"Activate @lre@Rock Skin");
		j++;
		addPrayerWithTooltip(25012, 0, 87, 12, j, 25064,
				"Activate @lre@Superhuman Strength");
		j++;
		addPrayerWithTooltip(25014, 0, 88, 15, j, 25066,
				"Activate @lre@Improved Reflexes");
		j++;
		addPrayerWithTooltip(25016, 0, 89, 18, j, 25068,
				"Activate @lre@Rapid Restore");
		j++;
		addPrayerWithTooltip(25018, 0, 90, 21, j, 25070,
				"Activate @lre@Rapid Heal");
		j++;
		addPrayerWithTooltip(25020, 0, 91, 24, j, 25072,
				"Activate @lre@Protect Item");
		j++;
		addPrayerWithTooltip(25022, 0, 603, 25, j, 25074,
				"Activate @lre@Hawk Eye");
		j++;
		addPrayerWithTooltip(25024, 0, 604, 26, j, 25076,
				"Activate @lre@Mystic Lore");
		j++;
		addPrayerWithTooltip(25026, 0, 92, 27, j, 25078,
				"Activate @lre@Steel Skin");
		j++;
		addPrayerWithTooltip(25028, 0, 93, 30, j, 25080,
				"Activate @lre@Ultimate Strength");
		j++;
		addPrayerWithTooltip(25030, 0, 94, 33, j, 25082,
				"Activate @lre@Incredible Reflexes");
		j++;
		addPrayerWithTooltip(25032, 0, 95, 36, j, 25084,
				"Activate @lre@Protect from Magic");
		j++;
		addPrayerWithTooltip(25034, 0, 96, 39, j, 25086,
				"Activate @lre@Protect from Missles");
		j++;
		addPrayerWithTooltip(25036, 0, 97, 42, j, 25088,
				"Activate @lre@Protect from Melee");
		j++;
		addPrayerWithTooltip(25038, 0, 605, 43, j, 25090,
				"Activate @lre@Eagle Eye");
		j++;
		addPrayerWithTooltip(25040, 0, 606, 44, j, 25092,
				"Activate @lre@Mystic Might");
		j++;
		addPrayerWithTooltip(25042, 0, 98, 45, j, 25094,
				"Activate @lre@Retribution");
		j++;
		addPrayerWithTooltip(25044, 0, 99, 48, j, 25096,
				"Activate @lre@Redemption");
		j++;
		addPrayerWithTooltip(25046, 0, 100, 51, j, 25098, "Activate @lre@Smite");
		j++;
		addPrayerWithTooltip(25048, 0, 607, 59, j, 25100,
				"Activate @lre@Chivalry");
		j++;
		addPrayerWithTooltip(25050, 0, 608, 69, j, 25102, "Activate @lre@Piety");
		j++;
		drawTooltip(25052, "Level 01\nThick Skin\nIncreases your Defence by 5%");
		drawTooltip(25054,
				"Level 04\nBurst of Strength\nIncreases your Strength by 5%");
		drawTooltip(25056,
				"Level 07\nClarity of Thought\nIncreases your Attack by 5%");
		drawTooltip(25058, "Level 08\nSharp Eye\nIncreases your Ranged by 5%");
		drawTooltip(25060, "Level 09\nMystic Will\nIncreases your Magic by 5%");
		drawTooltip(25062, "Level 10\nRock Skin\nIncreases your Defence by 10%");
		drawTooltip(25064,
				"Level 13\nSuperhuman Strength\nIncreases your Strength by 10%");
		drawTooltip(25066,
				"Level 16\nImproved Reflexes\nIncreases your Attack by 10%");
		drawTooltip(
				25068,
				"Level 19\nRapid Restore\n2x restore rate for all stats\nexcept Hitpoints, Summon"
						+ "ing\nand Prayer");
		drawTooltip(25070,
				"Level 22\nRapid Heal\n2x restore rate for the\nHitpoints stat");
		drawTooltip(25072,
				"Level 25\nProtect Item\nKeep 1 extra item if you die");
		drawTooltip(25074, "Level 26\nHawk Eye\nIncreases your Ranged by 10%");
		drawTooltip(25076, "Level 27\nMystic Lore\nIncreases your Magic by 10%");
		drawTooltip(25078,
				"Level 28\nSteel Skin\nIncreases your Defence by 15%");
		drawTooltip(25080,
				"Level 31\nUltimate Strength\nIncreases your Strength by 15%");
		drawTooltip(25082,
				"Level 34\nIncredible Reflexes\nIncreases your Attack by 15%");
		drawTooltip(25084,
				"Level 37\nProtect from Magic\nProtection from magical attacks");
		drawTooltip(25086,
				"Level 40\nProtect from Missles\nProtection from ranged attacks");
		drawTooltip(25088,
				"Level 43\nProtect from Melee\nProtection from melee attacks");
		drawTooltip(25090, "Level 44\nEagle Eye\nIncreases your Ranged by 15%");
		drawTooltip(25092,
				"Level 45\nMystic Might\nIncreases your Magic by 15%");
		drawTooltip(25094,
				"Level 46\nRetribution\nInflicts damage to nearby\ntargets if you die");
		drawTooltip(25096,
				"Level 49\nRedemption\nHeals you when damaged\nand Hitpoints falls\nbelow 10%");
		drawTooltip(25098,
				"Level 52\nSmite\n1/4 of damage dealt is\nalso removed from\nopponent's Prayer");
		drawTooltip(
				25100,
				"Level 60\nChivalry\nIncreases your Defence by 20%,\nStrength by 18%, and Attack "
						+ "by\n15%");
		drawTooltip(
				25102,
				"Level 70\nPiety\nIncreases your Defence by 25%,\nStrength by 23%, and Attack by\n"
						+ "20%");
		setChildren(80, rsinterface);
		setBounds(687, 85, 241, i, rsinterface);
		i++;
		setBounds(25105, 65, 241, i, rsinterface);
		i++;
		setBounds(25000, 2, 5, i, rsinterface);
		i++;
		setBounds(25001, 5, 8, i, rsinterface);
		i++;
		setBounds(25002, 40, 5, i, rsinterface);
		i++;
		setBounds(25003, 44, 8, i, rsinterface);
		i++;
		setBounds(25004, 76, 5, i, rsinterface);
		i++;
		setBounds(25005, 79, 11, i, rsinterface);
		i++;
		setBounds(25006, 113, 5, i, rsinterface);
		i++;
		setBounds(25007, 116, 10, i, rsinterface);
		i++;
		setBounds(25008, 150, 5, i, rsinterface);
		i++;
		setBounds(25009, 153, 9, i, rsinterface);
		i++;
		setBounds(25010, 2, 45, i, rsinterface);
		i++;
		setBounds(25011, 5, 48, i, rsinterface);
		i++;
		setBounds(25012, 39, 45, i, rsinterface);
		i++;
		setBounds(25013, 44, 47, i, rsinterface);
		i++;
		setBounds(25014, 76, 45, i, rsinterface);
		i++;
		setBounds(25015, 79, 49, i, rsinterface);
		i++;
		setBounds(25016, 113, 45, i, rsinterface);
		i++;
		setBounds(25017, 116, 50, i, rsinterface);
		i++;
		setBounds(25018, 151, 45, i, rsinterface);
		i++;
		setBounds(25019, 154, 50, i, rsinterface);
		i++;
		setBounds(25020, 2, 82, i, rsinterface);
		i++;
		setBounds(25021, 4, 84, i, rsinterface);
		i++;
		setBounds(25022, 40, 82, i, rsinterface);
		i++;
		setBounds(25023, 44, 87, i, rsinterface);
		i++;
		setBounds(25024, 77, 82, i, rsinterface);
		i++;
		setBounds(25025, 81, 85, i, rsinterface);
		i++;
		setBounds(25026, 114, 83, i, rsinterface);
		i++;
		setBounds(25027, 117, 85, i, rsinterface);
		i++;
		setBounds(25028, 153, 83, i, rsinterface);
		i++;
		setBounds(25029, 156, 87, i, rsinterface);
		i++;
		setBounds(25030, 2, 120, i, rsinterface);
		i++;
		setBounds(25031, 5, 125, i, rsinterface);
		i++;
		setBounds(25032, 40, 120, i, rsinterface);
		i++;
		setBounds(25033, 43, 124, i, rsinterface);
		i++;
		setBounds(25034, 78, 120, i, rsinterface);
		i++;
		setBounds(25035, 83, 124, i, rsinterface);
		i++;
		setBounds(25036, 114, 120, i, rsinterface);
		i++;
		setBounds(25037, 115, 121, i, rsinterface);
		i++;
		setBounds(25038, 151, 120, i, rsinterface);
		i++;
		setBounds(25039, 154, 124, i, rsinterface);
		i++;
		setBounds(25040, 2, 158, i, rsinterface);
		i++;
		setBounds(25041, 5, 160, i, rsinterface);
		i++;
		setBounds(25042, 39, 158, i, rsinterface);
		i++;
		setBounds(25043, 41, 158, i, rsinterface);
		i++;
		setBounds(25044, 76, 158, i, rsinterface);
		i++;
		setBounds(25045, 79, 163, i, rsinterface);
		i++;
		setBounds(25046, 114, 158, i, rsinterface);
		i++;
		setBounds(25047, 116, 158, i, rsinterface);
		i++;
		setBounds(25048, 153, 158, i, rsinterface);
		i++;
		setBounds(25049, 161, 160, i, rsinterface);
		i++;
		setBounds(25050, 2, 196, i, rsinterface);
		i++;
		setBounds(25051, 4, 207, i, rsinterface);
		setBoundry(++i, 25052, byte0 - 2, byte1, rsinterface);
		setBoundry(++i, 25054, byte0 - 5, byte1, rsinterface);
		setBoundry(++i, 25056, byte0, byte1, rsinterface);
		setBoundry(++i, 25058, byte0, byte1, rsinterface);
		setBoundry(++i, 25060, byte0, byte1, rsinterface);
		setBoundry(++i, 25062, byte2 - 9, byte3, rsinterface);
		setBoundry(++i, 25064, byte2 - 11, byte3, rsinterface);
		setBoundry(++i, 25066, byte2, byte3, rsinterface);
		setBoundry(++i, 25068, byte2, byte3, rsinterface);
		setBoundry(++i, 25070, byte2 + 25, byte3, rsinterface);
		setBoundry(++i, 25072, byte4, byte5, rsinterface);
		setBoundry(++i, 25074, byte4 - 2, byte5, rsinterface);
		setBoundry(++i, 25076, byte4, byte5, rsinterface);
		setBoundry(++i, 25078, byte4 - 7, byte5, rsinterface);
		setBoundry(++i, 25080, byte4 - 10, byte5, rsinterface);
		setBoundry(++i, 25082, byte6, c, rsinterface);
		setBoundry(++i, 25084, byte6 - 8, c, rsinterface);
		setBoundry(++i, 25086, byte6 - 7, c, rsinterface);
		setBoundry(++i, 25088, byte6 - 2, c, rsinterface);
		setBoundry(++i, 25090, byte6 - 2, c, rsinterface);
		setBoundry(++i, 25092, byte7, byte8, rsinterface);
		setBoundry(++i, 25094, byte7, byte8 - 20, rsinterface);
		setBoundry(++i, 25096, byte7, byte8 - 25, rsinterface);
		setBoundry(++i, 25098, byte7 + 15, byte8 - 25, rsinterface);
		setBoundry(++i, 25100, byte7 - 12, byte8 - 20, rsinterface);
		setBoundry(++i, 25102, k - 2, byte9, rsinterface);
		i++;
	}

	public static void addPrayerWithTooltip(int i, int configId,
			int configFrame, int requiredValues, int prayerSpriteID, int Hover,
			String tooltip) {
		RSInterface Interface = addTabInterface(i);
		Interface.id = i;
		Interface.parentID = 5608;
		Interface.type = 5;
		Interface.atActionType = 4;
		Interface.contentType = 0;
		Interface.opacity = 0;
		Interface.hoverType = Hover;
		Interface.sprite1 = imageLoader(0, "Interfaces/PrayerTab/PRAYERGLOW");
		Interface.sprite2 = imageLoader(1, "Interfaces/PrayerTab/PRAYERGLOW");
		Interface.width = 34;
		Interface.height = 34;
		Interface.valueCompareType = new int[1];
		Interface.requiredValues = new int[1];
		Interface.valueCompareType[0] = 1;
		Interface.requiredValues[0] = configId;
		Interface.valueIndexArray = new int[1][3];
		Interface.valueIndexArray[0][0] = 5;
		Interface.valueIndexArray[0][1] = configFrame;
		Interface.valueIndexArray[0][2] = 0;
		Interface.tooltip = tooltip;
		Interface = addTabInterface(i + 1);
		Interface.id = i + 1;
		Interface.parentID = 5608;
		Interface.type = 5;
		Interface.atActionType = 0;
		Interface.contentType = 0;
		Interface.opacity = 0;
		Interface.sprite1 = imageLoader(prayerSpriteID,
				"Interfaces/PrayerTab/PRAYERON");
		Interface.sprite2 = imageLoader(prayerSpriteID,
				"Interfaces/" +
				"/PRAYEROFF");
		Interface.width = 34;
		Interface.height = 34;
		Interface.valueCompareType = new int[1];
		Interface.requiredValues = new int[1];
		Interface.valueCompareType[0] = 2;
		Interface.requiredValues[0] = requiredValues + 1;
		Interface.valueIndexArray = new int[1][3];
		Interface.valueIndexArray[0][0] = 2;
		Interface.valueIndexArray[0][1] = 5;
		Interface.valueIndexArray[0][2] = 0;
	}

	public static void setBoundry(int frame, int ID, int X, int Y,
			RSInterface RSInterface) {
		RSInterface.children[frame] = ID;
		RSInterface.childX[frame] = X;
		RSInterface.childY[frame] = Y;
	}

	public String popupString;

	public static void addTooltipBox(int id, String text) {
		RSInterface rsi = addInterface(id);
		rsi.id = id;
		rsi.parentID = id;
		rsi.type = 8;
		rsi.popupString = text;
	}

	public static void addTooltip(int id, String text) {
		RSInterface rsi = addInterface(id);
		rsi.id = id;
		rsi.type = 0;
		rsi.interfaceShown = true;
		rsi.hoverType = -1;
		addTooltipBox(id + 1, text);
		rsi.totalChildren(1);
		rsi.child(0, id + 1, 0, 0);
	}

	public static void addPrayer(int i, int configId, int configFrame,
			int requiredValues, int spriteID, String prayerName) {
		RSInterface tab = addTabInterface(i);
		tab.id = i;
		tab.parentID = 5608;
		tab.type = 5;
		tab.atActionType = 4;
		tab.contentType = 0;
		tab.opacity = 0;
		tab.hoverType = -1;
		tab.sprite1 = imageLoader(0, "PRAYERGLOW");
		tab.sprite2 = imageLoader(1, "PRAYERGLOW");
		tab.width = 34;
		tab.height = 34;
		tab.valueCompareType = new int[1];
		tab.requiredValues = new int[1];
		tab.valueCompareType[0] = 1;
		tab.requiredValues[0] = configId;
		tab.valueIndexArray = new int[1][3];
		tab.valueIndexArray[0][0] = 5;
		tab.valueIndexArray[0][1] = configFrame;
		tab.valueIndexArray[0][2] = 0;
		tab.tooltip = "Activate@or2@ " + prayerName;
		// tab.tooltip = "Select";
		RSInterface tab2 = addTabInterface(i + 1);
		tab2.id = i + 1;
		tab2.parentID = 5608;
		tab2.type = 5;
		tab2.atActionType = 0;
		tab2.contentType = 0;
		tab2.opacity = 0;
		tab2.hoverType = -1;
		tab2.sprite1 = imageLoader(spriteID, "/PRAYER/PRAYON");
		tab2.sprite2 = imageLoader(spriteID, "/PRAYER/PRAYOFF");
		tab2.width = 34;
		tab2.height = 34;
		tab2.valueCompareType = new int[1];
		tab2.requiredValues = new int[1];
		tab2.valueCompareType[0] = 2;
		tab2.requiredValues[0] = requiredValues + 1;
		tab2.valueIndexArray = new int[1][3];
		tab2.valueIndexArray[0][0] = 2;
		tab2.valueIndexArray[0][1] = 5;
		tab2.valueIndexArray[0][2] = 0;
		// RSInterface tab3 = addTabInterface(i + 50);
	}

	public static void friendsTab(TextDrawingArea[] kam) {
		RSInterface tab = addTabInterface(5065);
		tab.width = 192;
		tab.height = 263;
		addText(5067, "Friends List:", kam, 1, 0xff9933, true, true);
		addText(5068, "Zaros", kam, 1, 0xff9933, true, true);
		addSprite(5069, 0, "Friends/SPRITE");
		addSprite(5070, 1, "Friends/SPRITE");
		addSprite(5071, 1, "Friends/SPRITE");
		addSprite(5072, 2, "Friends/SPRITE");
		addHoverButton(5073, "Friends/SPRITE", 3, 17, 17, "Add friend", 201,
				5074, 1);
		addHoveredButton(5074, "Friends/SPRITE", 4, 17, 17, 5075);
		addHoverButton(5076, "Friends/SPRITE", 5, 17, 17, "Delete friend", 202,
				5077, 1);
		addHoveredButton(5077, "Friends/SPRITE", 6, 17, 17, 5078);
		addText(5079, "0 / 200", kam, 0, 0xF5F5DC, false, true, 901, 0);

		RSInterface friendsList = interfaceCache[5066];
		friendsList.height = 197;
		friendsList.width = 174;
		for (int id = 5092; id <= 5191; id++) {
			int i = id - 5092;
			friendsList.children[i] = id;
			friendsList.childX[i] = 3;
			friendsList.childY[i] = friendsList.childY[i] - 3;
		}
		for (int id = 5192; id <= 5291; id++) {
			int i = id - 5092;
			friendsList.children[i] = id;
			friendsList.childX[i] = 113;
			friendsList.childY[i] = friendsList.childY[i] - 3;
		}

		tab.totalChildren(12);
		tab.child(0, 5067, 92, 2);
		tab.child(1, 5068, 92, 17);
		tab.child(2, 5069, 0, 39);
		tab.child(3, 5070, 0, 36);
		tab.child(4, 5071, 0, 237);
		tab.child(5, 5072, 107, 39);
		tab.child(6, 5073, 5, 242);
		tab.child(7, 5074, 5, 242);
		tab.child(8, 5076, 23, 242);
		tab.child(9, 5077, 23, 242);
		tab.child(10, 5079, 46, 245);
		tab.child(11, 5066, 0, 39);

	}

	public static void ignoreTab(TextDrawingArea[] kam) {
		RSInterface tab = addTabInterface(5715);
		tab.width = 192;
		tab.height = 263;
		addText(5717, "Ignore List:", kam, 1, 0xff9933, true, true);
		addText(5730, "Zaros", kam, 1, 0xff9933, true, true);
		addSprite(5718, 0, "Friends/SPRITE");
		addSprite(5719, 1, "Friends/SPRITE");
		addSprite(5720, 1, "Friends/SPRITE");
		addHoverButton(5721, "Friends/SPRITE", 11, 17, 17, "Add name", 501,
				5722, 1);
		addHoveredButton(5722, "Friends/SPRITE", 12, 17, 17, 5723);
		addHoverButton(5724, "Friends/SPRITE", 13, 17, 17, "Delete name", 502,
				5725, 1);
		addHoveredButton(5725, "Friends/SPRITE", 14, 17, 17, 5726);
		addText(5727, "0 / 100", kam, 0, 0xF5F5DC, false, true, 902, 0);

		RSInterface ignoresList = interfaceCache[5716];
		ignoresList.height = 197;
		ignoresList.width = 174;
		for (int id = 5742; id <= 5841; id++) {
			int i = id - 5742;
			ignoresList.children[i] = id;
			ignoresList.childX[i] = 3;
			ignoresList.childY[i] = ignoresList.childY[i] - 3;
		}

		tab.totalChildren(11);
		tab.child(0, 5717, 92, 2);
		tab.child(1, 5718, 0, 39);
		tab.child(2, 5719, 0, 36);
		tab.child(3, 5720, 0, 237);
		tab.child(4, 5721, 5, 242);
		tab.child(5, 5722, 5, 242);
		tab.child(6, 5724, 23, 242);
		tab.child(7, 5725, 23, 242);
		tab.child(8, 5727, 46, 245);
		tab.child(9, 5716, 0, 39);
		tab.child(10, 5730, 92, 17);

	}

	public static void addText(int id, String text, TextDrawingArea tda[],
			int idx, int color, boolean center, boolean shadow,
			int contentType, int actionType) {
		RSInterface tab = addTabInterface(id);
		tab.parentID = id;
		tab.id = id;
		tab.type = 4;
		tab.atActionType = actionType;
		tab.width = 0;
		tab.height = 11;
		tab.contentType = contentType;
		tab.centerText = center;
		tab.textShadow = shadow;
		tab.textDrawingAreas = tda[idx];
		tab.message = text;
		tab.aString228 = "";
		tab.textColor = color;
		tab.anInt219 = 0;
		tab.anInt216 = 0;
		tab.anInt239 = 0;
	}

	private static Sprite CustomSpriteLoader(int id, String s) {
		long l = (TextClass.method585(s) << 8) + (long) id;
		Sprite sprite = (Sprite) aMRUNodes_238.insertFromCache(l);
		if (sprite != null) {
			return sprite;
		}
		try {
			sprite = new Sprite("/Attack/" + id + s);
			aMRUNodes_238.removeFromCache(sprite, l);
		} catch (Exception exception) {
			return null;
		}
		return sprite;
	}

	public static RSInterface addInterface(int id) {
		RSInterface rsi = interfaceCache[id] = new RSInterface();
		rsi.id = id;
		rsi.parentID = id;
		rsi.width = 512;
		rsi.height = 334;
		return rsi;
	}

	public static void addText(int id, String text, TextDrawingArea tda[],
			int idx, int color, boolean centered) {
		RSInterface rsi = interfaceCache[id] = new RSInterface();
		if (centered)
			rsi.centerText = true;
		rsi.textShadow = true;
		rsi.textDrawingAreas = tda[idx];
		rsi.message = text;
		rsi.textColor = color;
		rsi.id = id;
		rsi.type = 4;
	}

	public static void textColor(int id, int color) {
		RSInterface rsi = interfaceCache[id];
		rsi.textColor = color;
	}

	public static void textSize(int id, TextDrawingArea tda[], int idx) {
		RSInterface rsi = interfaceCache[id];
		rsi.textDrawingAreas = tda[idx];
	}

	public static void addCacheSprite(int id, int sprite1, int sprite2,
			String sprites) {
		RSInterface rsi = interfaceCache[id] = new RSInterface();
		rsi.sprite1 = method207(sprite1, aClass44, sprites);
		rsi.sprite2 = method207(sprite2, aClass44, sprites);
		rsi.parentID = id;
		rsi.id = id;
		rsi.type = 5;
	}

	public static void sprite1(int id, int sprite) {
		RSInterface class9 = interfaceCache[id];
		class9.sprite1 = CustomSpriteLoader(sprite, "");
	}

	public static void addActionButton(int id, int sprite, int sprite2,
			int width, int height, String s) {
		RSInterface rsi = interfaceCache[id] = new RSInterface();
		rsi.sprite1 = CustomSpriteLoader(sprite, "");
		if (sprite2 == sprite)
			rsi.sprite2 = CustomSpriteLoader(sprite, "a");
		else
			rsi.sprite2 = CustomSpriteLoader(sprite2, "");
		rsi.tooltip = s;
		rsi.contentType = 0;
		rsi.atActionType = 1;
		rsi.width = width;
		rsi.hoverType = 52;
		rsi.parentID = id;
		rsi.id = id;
		rsi.type = 5;
		rsi.height = height;
	}

	public static void addToggleButton(int id, int sprite, int setconfig,
			int width, int height, String s) {
		RSInterface rsi = addInterface(id);
		rsi.sprite1 = CustomSpriteLoader(sprite, "");
		rsi.sprite2 = CustomSpriteLoader(sprite, "a");
		rsi.requiredValues = new int[1];
		rsi.requiredValues[0] = 1;
		rsi.valueCompareType = new int[1];
		rsi.valueCompareType[0] = 1;
		rsi.valueIndexArray = new int[1][3];
		rsi.valueIndexArray[0][0] = 5;
		rsi.valueIndexArray[0][1] = setconfig;
		rsi.valueIndexArray[0][2] = 0;
		rsi.atActionType = 4;
		rsi.width = width;
		rsi.hoverType = -1;
		rsi.parentID = id;
		rsi.id = id;
		rsi.type = 5;
		rsi.height = height;
		rsi.tooltip = s;
	}

	public void totalChildren(int id, int x, int y) {
		children = new int[id];
		childX = new int[x];
		childY = new int[y];
	}

	public static void removeSomething(int id) {
		RSInterface rsi = interfaceCache[id] = new RSInterface();
	}

	public void specialBar(int id) {
		/*
		 * addActionButton(ID, SpriteOFF, SpriteON, Width, Height,
		 * "SpriteText");
		 */
		addActionButton(id - 12, 7587, -1, 150, 26, "Use @gre@Special Attack");
		/* removeSomething(ID); */
		for (int i = id - 11; i < id; i++)
			removeSomething(i);

		RSInterface rsi = interfaceCache[id - 12];
		rsi.width = 150;
		rsi.height = 26;

		rsi = interfaceCache[id];
		rsi.width = 150;
		rsi.height = 26;

		rsi.child(0, id - 12, 0, 0);

		rsi.child(12, id + 1, 3, 7);

		rsi.child(23, id + 12, 16, 8);

		for (int i = 13; i < 23; i++) {
			rsi.childY[i] -= 1;
		}

		rsi = interfaceCache[id + 1];
		rsi.type = 5;
		rsi.sprite1 = CustomSpriteLoader(7600, "");

		for (int i = id + 2; i < id + 12; i++) {
			rsi = interfaceCache[i];
			rsi.type = 5;
		}

		sprite1(id + 2, 7601);
		sprite1(id + 3, 7602);
		sprite1(id + 4, 7603);
		sprite1(id + 5, 7604);
		sprite1(id + 6, 7605);
		sprite1(id + 7, 7606);
		sprite1(id + 8, 7607);
		sprite1(id + 9, 7608);
		sprite1(id + 10, 7609);
		sprite1(id + 11, 7610);
	}

	public static final void wilderness(TextDrawingArea[] tda)
	{
		RSInterface rsinterface = addTabInterface(45600);
		addText(45601, "P'King Teleport", 0xff9b00, false, true, 52, tda, 2);
		addHoverButton(45602, "Interfaces/Minigame/Hover", 0, 172, 24, "Mage Bank", -1, 45603, 1);
		addHoveredButton(45603, "Interfaces/Minigame/Hover", 2, 172, 24, 45604);
		addHoverButton(45618, "Interfaces/Minigame/Hover", 0, 172, 24, "Level 13 Wild", -1, 45619, 1);
		addHoveredButton(45619, "Interfaces/Minigame/Hover", 2, 172, 24, 45620);
		addHoverButton(45621, "Interfaces/Minigame/Hover", 0, 172, 24, "Level 18 Wild", -1, 45622, 1);
		addHoveredButton(45622, "Interfaces/Minigame/Hover", 2, 172, 24, 45623);
		addHoverButton(45624, "Interfaces/Minigame/Hover", 0, 172, 24, "Edgeville", -1, 45625, 1);
		addHoveredButton(45625, "Interfaces/Minigame/Hover", 2, 172, 24, 45626);
		addHoverButton(45627, "Interfaces/Minigame/Hover", 0, 172, 24, "Extreme PK", -1, 45628, 1);
		addHoveredButton(45628, "Interfaces/Minigame/Hover", 2, 172, 24, 45629);
		addHoverButton(45633, "Interfaces/Minigame/Back", 0, 16, 16, "Back", -1, 45634, 1);
		addHoveredButton(45634, "Interfaces/Minigame/Back", 1, 16, 16, 45635);
		addSprite(45605, 1, "Interfaces/Minigame/Pk");
		addSprite(45606, 1, "Interfaces/Minigame/Pk");
		addSprite(45607, 1, "Interfaces/Minigame/Pk");
		addSprite(45608, 1, "Interfaces/Minigame/Pk");
		addSprite(45609, 1, "Interfaces/Minigame/Pk");
		addSprite(45611, 1, "Interfaces/Minigame/Background");
		addText(45612, "Mage Bank", 0xd67b29, true, true, 52, tda, 2);
		addText(45613, "Level 13 Wild", 0xd67b29, true, true, 52, tda, 2);
		addText(45614, "Level 18 Wild", 0xd67b29, true, true, 52, tda, 2);
		addText(45615, "Edgeville", 0xd67b29, true, true, 52, tda, 2);
		addText(45616, "Extreme PK", 0xd67b29, true, true, 52, tda, 2);
		byte childAmount = 24;
		int indexChild = 0;
		setChildren(childAmount, rsinterface);
		setBounds(45611, -1, 26, indexChild, rsinterface);
		indexChild++;
		setBounds(45601, 33, 7, indexChild, rsinterface);
		indexChild++;
		setBounds(45602, 8, 35, indexChild, rsinterface);
		indexChild++;
		setBounds(45603, 8, 35, indexChild, rsinterface);
		indexChild++;
		setBounds(45612, 80, 39, indexChild, rsinterface);
		indexChild++;
		setBounds(45618, 8, 72, indexChild, rsinterface);
		indexChild++;
		setBounds(45619, 8, 72, indexChild, rsinterface);
		indexChild++;
		setBounds(45613, 80, 76, indexChild, rsinterface);
		indexChild++;
		setBounds(45621, 8, 109, indexChild, rsinterface);
		indexChild++;
		setBounds(45622, 8, 109, indexChild, rsinterface);
		indexChild++;
		setBounds(45614, 80, 113, indexChild, rsinterface);
		indexChild++;
		setBounds(45624, 8, 146, indexChild, rsinterface);
		indexChild++;
		setBounds(45625, 8, 146, indexChild, rsinterface);
		indexChild++;
		setBounds(45615, 80, 150, indexChild, rsinterface);
		indexChild++;
		setBounds(45627, 8, 183, indexChild, rsinterface);
		indexChild++;
		setBounds(45628, 8, 183, indexChild, rsinterface);
		indexChild++;
		setBounds(45616, 80, 187, indexChild, rsinterface);
		indexChild++;
		setBounds(45605, 148, 34, indexChild, rsinterface);
		indexChild++;
		setBounds(45606, 148, 71, indexChild, rsinterface);
		indexChild++;
		setBounds(45607, 148, 108, indexChild, rsinterface);
		indexChild++;
		setBounds(45608, 148, 146, indexChild, rsinterface);
		indexChild++;
		setBounds(45609, 148, 183, indexChild, rsinterface);
		indexChild++;
		setBounds(45633, 10, 6, indexChild, rsinterface);
		indexChild++;
		setBounds(45634, 10, 6, indexChild, rsinterface);
		indexChild++;
	}

	public static final void boss(TextDrawingArea[] tda)
	{
		RSInterface rsinterface = addTabInterface(45500);
		addText(45501, "Boss Teleport", 0xff9b00, false, true, 52, tda, 2);
		addHoverButton(45502, "Interfaces/Minigame/Hover", 0, 172, 24, "God Wars", -1, 45503, 1);
		addHoveredButton(45503, "Interfaces/Minigame/Hover", 3, 172, 24, 45504);
		addHoverButton(45518, "Interfaces/Minigame/Hover", 0, 172, 24, "King Black Dragon", -1, 45519, 1);
		addHoveredButton(45519, "Interfaces/Minigame/Hover", 3, 172, 24, 45520);
		addHoverButton(45521, "Interfaces/Minigame/Hover", 0, 172, 24, "Dagannoth Kings", -1, 45522, 1);
		addHoveredButton(45522, "Interfaces/Minigame/Hover", 3, 172, 24, 45523);
		addHoverButton(45524, "Interfaces/Minigame/Hover", 0, 172, 24, "Tormented Demons", -1, 45525, 1);
		addHoveredButton(45525, "Interfaces/Minigame/Hover", 3, 172, 24, 45526);
		addHoverButton(45527, "Interfaces/Minigame/Hover", 0, 172, 24, "Corporal Beast", -1, 45528, 1);
		addHoveredButton(45528, "Interfaces/Minigame/Hover", 3, 172, 24, 45529);
		addHoverButton(45533, "Interfaces/Minigame/Back", 0, 16, 16, "Back", -1, 45534, 1);
		addHoveredButton(45534, "Interfaces/Minigame/Back", 1, 16, 16, 45535);
		addSprite(45505, 1, "Interfaces/Minigame/Godwars");
		addSprite(45506, 1, "Interfaces/Minigame/Kbd");
		addSprite(45507, 1, "Interfaces/Minigame/Dagganoths");
		addSprite(45508, 1, "Interfaces/Minigame/Chaos");
		addSprite(45509, 1, "Interfaces/Minigame/Corporeal");
		addSprite(45511, 1, "Interfaces/Minigame/Background");
		addText(45512, "Godwars", 0xd67b29, true, true, 52, tda, 2);
		addText(45513, "King Black Dragon", 0xd67b29, true, true, 52, tda, 2);
		addText(45514, "Dagannoth Kings", 0xd67b29, true, true, 52, tda, 2);
		addText(45515, "Tormented Demons", 0xd67b29, true, true, 52, tda, 2);
		addText(45516, "Corporal Beast", 0xd67b29, true, true, 52, tda, 2);
		byte childAmount = 24;
		int indexChild = 0;
		setChildren(childAmount, rsinterface);
		setBounds(45511, -1, 26, indexChild, rsinterface);
		indexChild++;
		setBounds(45501, 33, 7, indexChild, rsinterface);
		indexChild++;
		setBounds(45502, 8, 35, indexChild, rsinterface);
		indexChild++;
		setBounds(45503, 8, 35, indexChild, rsinterface);
		indexChild++;
		setBounds(45512, 80, 39, indexChild, rsinterface);
		indexChild++;
		setBounds(45518, 8, 72, indexChild, rsinterface);
		indexChild++;
		setBounds(45519, 8, 72, indexChild, rsinterface);
		indexChild++;
		setBounds(45513, 80, 76, indexChild, rsinterface);
		indexChild++;
		setBounds(45521, 8, 109, indexChild, rsinterface);
		indexChild++;
		setBounds(45522, 8, 109, indexChild, rsinterface);
		indexChild++;
		setBounds(45514, 80, 113, indexChild, rsinterface);
		indexChild++;
		setBounds(45524, 8, 146, indexChild, rsinterface);
		indexChild++;
		setBounds(45525, 8, 146, indexChild, rsinterface);
		indexChild++;
		setBounds(45515, 80, 150, indexChild, rsinterface);
		indexChild++;
		setBounds(45527, 8, 183, indexChild, rsinterface);
		indexChild++;
		setBounds(45528, 8, 183, indexChild, rsinterface);
		indexChild++;
		setBounds(45516, 80, 187, indexChild, rsinterface);
		indexChild++;
		setBounds(45505, 148, 33, indexChild, rsinterface);
		indexChild++;
		setBounds(45506, 148, 70, indexChild, rsinterface);
		indexChild++;
		setBounds(45507, 148, 104, indexChild, rsinterface);
		indexChild++;
		setBounds(45508, 148, 144, indexChild, rsinterface);
		indexChild++;
		setBounds(45509, 148, 179, indexChild, rsinterface);
		indexChild++;
		setBounds(45533, 10, 6, indexChild, rsinterface);
		indexChild++;
		setBounds(45534, 10, 6, indexChild, rsinterface);
		indexChild++;
	}

	public static final void city(TextDrawingArea[] tda)
	{
		RSInterface rsinterface = addTabInterface(45900);
		addText(45901, "City Teleport", 0xff9b00, false, true, 52, tda, 2);
		addHoverButton(45902, "Interfaces/Minigame/Hover", 0, 172, 24, "Lumbridge", -1, 45903, 1);
		addHoveredButton(45903, "Interfaces/Minigame/Hover", 5, 172, 24, 45904);
		addHoverButton(45918, "Interfaces/Minigame/Hover", 0, 172, 24, "Varrock", -1, 45919, 1);
		addHoveredButton(45919, "Interfaces/Minigame/Hover", 5, 172, 24, 45920);
		addHoverButton(45921, "Interfaces/Minigame/Hover", 0, 172, 24, "Camelot", -1, 45922, 1);
		addHoveredButton(45922, "Interfaces/Minigame/Hover", 5, 172, 24, 45923);
		addHoverButton(45924, "Interfaces/Minigame/Hover", 0, 172, 24, "Falador", -1, 45925, 1);
		addHoveredButton(45925, "Interfaces/Minigame/Hover", 5, 172, 24, 45926);
		addHoverButton(45927, "Interfaces/Minigame/Hover", 0, 172, 24, "Sophanem", -1, 45928, 1);
		addHoveredButton(45928, "Interfaces/Minigame/Hover", 5, 172, 24, 45929);
		addHoverButton(45933, "Interfaces/Minigame/Back", 0, 16, 16, "Back", -1, 45934, 1);
		addHoveredButton(45934, "Interfaces/Minigame/Back", 1, 16, 16, 45935);
		addSprite(45905, 1, "Interfaces/Minigame/lumbridge");
		addSprite(45906, 1, "Interfaces/Minigame/varrock");
		addSprite(45907, 1, "Interfaces/Minigame/camelot");
		addSprite(45908, 1, "Interfaces/Minigame/falador");
		addSprite(45909, 1, "Interfaces/Minigame/pollnivneach");
		addSprite(45911, 1, "Interfaces/Minigame/Background");
		addText(45912, "Lumbridge", 0xd67b29, true, true, 52, tda, 2);
		addText(45913, "Varrock", 0xd67b29, true, true, 52, tda, 2);
		addText(45914, "Camelot", 0xd67b29, true, true, 52, tda, 2);
		addText(45915, "Falador", 0xd67b29, true, true, 52, tda, 2);
		addText(45916, "Sophanem", 0xd67b29, true, true, 52, tda, 2);
		byte childAmount = 24;
		int indexChild = 0;
		setChildren(childAmount, rsinterface);
		setBounds(45911, 0, 26, indexChild, rsinterface);
		indexChild++;
		setBounds(45901, 33, 7, indexChild, rsinterface);
		indexChild++;
		setBounds(45902, 8, 35, indexChild, rsinterface);
		indexChild++;
		setBounds(45903, 8, 35, indexChild, rsinterface);
		indexChild++;
		setBounds(45912, 80, 39, indexChild, rsinterface);
		indexChild++;
		setBounds(45918, 8, 72, indexChild, rsinterface);
		indexChild++;
		setBounds(45919, 8, 72, indexChild, rsinterface);
		indexChild++;
		setBounds(45913, 80, 76, indexChild, rsinterface);
		indexChild++;
		setBounds(45921, 8, 109, indexChild, rsinterface);
		indexChild++;
		setBounds(45922, 8, 109, indexChild, rsinterface);
		indexChild++;
		setBounds(45914, 80, 113, indexChild, rsinterface);
		indexChild++;
		setBounds(45924, 8, 146, indexChild, rsinterface);
		indexChild++;
		setBounds(45925, 8, 146, indexChild, rsinterface);
		indexChild++;
		setBounds(45915, 80, 150, indexChild, rsinterface);
		indexChild++;
		setBounds(45927, 8, 183, indexChild, rsinterface);
		indexChild++;
		setBounds(45928, 8, 183, indexChild, rsinterface);
		indexChild++;
		setBounds(45916, 80, 187, indexChild, rsinterface);
		indexChild++;
		setBounds(45905, 148, 33, indexChild, rsinterface);
		indexChild++;
		setBounds(45906, 148, 70, indexChild, rsinterface);
		indexChild++;
		setBounds(45907, 148, 104, indexChild, rsinterface);
		indexChild++;
		setBounds(45908, 148, 140, indexChild, rsinterface);
		indexChild++;
		setBounds(45909, 148, 179, indexChild, rsinterface);
		indexChild++;
		setBounds(45933, 10, 6, indexChild, rsinterface);
		indexChild++;
		setBounds(45934, 10, 6, indexChild, rsinterface);
		indexChild++;
	}
	public static final void skill(TextDrawingArea[] tda)
	{
		RSInterface rsinterface = addTabInterface(45100);
		addText(45101, "Skill Teleport", 0xff9b00, false, true, 52, tda, 2);
		addHoverButton(45102, "Interfaces/Minigame/Hover", 0, 172, 24, "Skill Zone", -1, 45103, 1);
		addHoveredButton(45103, "Interfaces/Minigame/Hover", 5, 172, 24, 45104);
		addHoverButton(45118, "Interfaces/Minigame/Hover", 0, 172, 24, "Hunter", -1, 45119, 1);
		addHoveredButton(45119, "Interfaces/Minigame/Hover", 5, 172, 24, 45120);
		addHoverButton(45121, "Interfaces/Minigame/Hover", 0, 172, 24, "Agility", -1, 45122, 1);
		addHoveredButton(45122, "Interfaces/Minigame/Hover", 5, 172, 24, 45123);
		addHoverButton(45124, "Interfaces/Minigame/Hover", 0, 172, 24, "Fishing", -1, 45125, 1);
		addHoveredButton(45125, "Interfaces/Minigame/Hover", 5, 172, 24, 45126);
		addHoverButton(45127, "Interfaces/Minigame/Hover", 0, 172, 24, "Farming", -1, 45128, 1);
		addHoveredButton(45128, "Interfaces/Minigame/Hover", 5, 172, 24, 45129);
		addHoverButton(45133, "Interfaces/Minigame/Back", 0, 16, 16, "Back", -1, 45134, 1);
		addHoveredButton(45134, "Interfaces/Minigame/Back", 1, 16, 16, 45135);
		addSprite(45105, 1, "Interfaces/Minigame/overall");
		addSprite(45106, 1, "Interfaces/Minigame/hunter");
		addSprite(45107, 1, "Interfaces/Minigame/agility");
		addSprite(45108, 1, "Interfaces/Minigame/fishing");
		addSprite(45109, 1, "Interfaces/Minigame/farming");
		addSprite(45111, 1, "Interfaces/Minigame/Background");
		addText(45112, "Skill Zone", 0xd67b29, true, true, 52, tda, 2);
		addText(45113, "Hunter", 0xd67b29, true, true, 52, tda, 2);
		addText(45114, "Agility", 0xd67b29, true, true, 52, tda, 2);
		addText(45115, "Fishing", 0xd67b29, true, true, 52, tda, 2);
		addText(45116, "Farming", 0xd67b29, true, true, 52, tda, 2);
		byte childAmount = 24;
		int indexChild = 0;
		setChildren(childAmount, rsinterface);
		setBounds(45111, 0, 26, indexChild, rsinterface);
		indexChild++;
		setBounds(45101, 33, 7, indexChild, rsinterface);
		indexChild++;
		setBounds(45102, 8, 35, indexChild, rsinterface);
		indexChild++;
		setBounds(45103, 8, 35, indexChild, rsinterface);
		indexChild++;
		setBounds(45112, 80, 39, indexChild, rsinterface);
		indexChild++;
		setBounds(45118, 8, 72, indexChild, rsinterface);
		indexChild++;
		setBounds(45119, 8, 72, indexChild, rsinterface);
		indexChild++;
		setBounds(45113, 80, 76, indexChild, rsinterface);
		indexChild++;
		setBounds(45121, 8, 109, indexChild, rsinterface);
		indexChild++;
		setBounds(45122, 8, 109, indexChild, rsinterface);
		indexChild++;
		setBounds(45114, 80, 113, indexChild, rsinterface);
		indexChild++;
		setBounds(45124, 8, 146, indexChild, rsinterface);
		indexChild++;
		setBounds(45125, 8, 146, indexChild, rsinterface);
		indexChild++;
		setBounds(45115, 80, 150, indexChild, rsinterface);
		indexChild++;
		setBounds(45127, 8, 183, indexChild, rsinterface);
		indexChild++;
		setBounds(45128, 8, 183, indexChild, rsinterface);
		indexChild++;
		setBounds(45116, 80, 187, indexChild, rsinterface);
		indexChild++;
		setBounds(45105, 148, 33, indexChild, rsinterface);
		indexChild++;
		setBounds(45106, 148, 70, indexChild, rsinterface);
		indexChild++;
		setBounds(45107, 148, 104, indexChild, rsinterface);
		indexChild++;
		setBounds(45108, 148, 140, indexChild, rsinterface);
		indexChild++;
		setBounds(45109, 148, 179, indexChild, rsinterface);
		indexChild++;
		setBounds(45133, 10, 6, indexChild, rsinterface);
		indexChild++;
		setBounds(45134, 10, 6, indexChild, rsinterface);
		indexChild++;
	}

	public static final void minigame(TextDrawingArea[] tda)
	{
		RSInterface rsinterface = addTabInterface(45200);
		addText(45201, "Minigames Teleport", 0xff9b00, false, true, 52, tda, 2);
		addHoverButton(45202, "Interfaces/Minigame/Hover", 0, 172, 24, "Duel Arena", -1, 45203, 1);
		addHoveredButton(45203, "Interfaces/Minigame/Hover", 4, 172, 24, 45204);
		addHoverButton(45218, "Interfaces/Minigame/Hover", 0, 172, 24, "Barrows", -1, 45219, 1);
		addHoveredButton(45219, "Interfaces/Minigame/Hover", 4, 172, 24, 45220);
		addHoverButton(45221, "Interfaces/Minigame/Hover", 0, 172, 24, "Pest Control", -1, 45222, 1);
		addHoveredButton(45222, "Interfaces/Minigame/Hover", 4, 172, 24, 45223);
		addHoverButton(45224, "Interfaces/Minigame/Hover", 0, 172, 24, "Tzhaar", -1, 45225, 1);
		addHoveredButton(45225, "Interfaces/Minigame/Hover", 4, 172, 24, 45226);
		addHoverButton(45227, "Interfaces/Minigame/Hover", 0, 172, 24, "Warriors Guild", -1, 45228, 1);
		addHoveredButton(45228, "Interfaces/Minigame/Hover", 4, 172, 24, 45229);
		addHoverButton(45233, "Interfaces/Minigame/Back", 0, 16, 16, "Back", -1, 45234, 1);
		addHoveredButton(45234, "Interfaces/Minigame/Back", 1, 16, 16, 45235);
		addSprite(45205, 1, "Interfaces/Minigame/DuelArena");
		addSprite(45206, 1, "Interfaces/Minigame/Barrows");
		addSprite(45207, 1, "Interfaces/Minigame/PestControl");
		addSprite(45208, 1, "Interfaces/Minigame/Tzhaar");
		addSprite(45209, 1, "Interfaces/Minigame/Warriors");
		addSprite(45211, 1, "Interfaces/Minigame/Background");
		addText(45212, "Duel Arena", 0xd67b29, true, true, 52, tda, 2);
		addText(45213, "Barrows", 0xd67b29, true, true, 52, tda, 2);
		addText(45214, "Pest Control", 0xd67b29, true, true, 52, tda, 2);
		addText(45215, "Tzhaar", 0xd67b29, true, true, 52, tda, 2);
		addText(45216, "Warriors Guild", 0xd67b29, true, true, 52, tda, 2);
		byte childAmount = 24;
		int indexChild = 0;
		setChildren(childAmount, rsinterface);
		setBounds(45211, 0, 26, indexChild, rsinterface);
		indexChild++;
		setBounds(45201, 33, 7, indexChild, rsinterface);
		indexChild++;
		setBounds(45202, 8, 35, indexChild, rsinterface);
		indexChild++;
		setBounds(45203, 8, 35, indexChild, rsinterface);
		indexChild++;
		setBounds(45212, 80, 39, indexChild, rsinterface);
		indexChild++;
		setBounds(45218, 8, 72, indexChild, rsinterface);
		indexChild++;
		setBounds(45219, 8, 72, indexChild, rsinterface);
		indexChild++;
		setBounds(45213, 80, 76, indexChild, rsinterface);
		indexChild++;
		setBounds(45221, 8, 109, indexChild, rsinterface);
		indexChild++;
		setBounds(45222, 8, 109, indexChild, rsinterface);
		indexChild++;
		setBounds(45214, 80, 113, indexChild, rsinterface);
		indexChild++;
		setBounds(45224, 8, 146, indexChild, rsinterface);
		indexChild++;
		setBounds(45225, 8, 146, indexChild, rsinterface);
		indexChild++;
		setBounds(45215, 80, 150, indexChild, rsinterface);
		indexChild++;
		setBounds(45227, 8, 183, indexChild, rsinterface);
		indexChild++;
		setBounds(45228, 8, 183, indexChild, rsinterface);
		indexChild++;
		setBounds(45216, 80, 187, indexChild, rsinterface);
		indexChild++;
		setBounds(45205, 148, 33, indexChild, rsinterface);
		indexChild++;
		setBounds(45206, 148, 70, indexChild, rsinterface);
		indexChild++;
		setBounds(45207, 148, 104, indexChild, rsinterface);
		indexChild++;
		setBounds(45208, 148, 140, indexChild, rsinterface);
		indexChild++;
		setBounds(45209, 148, 179, indexChild, rsinterface);
		indexChild++;
		setBounds(45233, 10, 6, indexChild, rsinterface);
		indexChild++;
		setBounds(45234, 10, 6, indexChild, rsinterface);
		indexChild++;
	}

	public static final void training(TextDrawingArea[] tda)
	{
		RSInterface rsinterface = addTabInterface(45300);
		addText(45301, "Training Teleport", 0xff9b00, false, true, 52, tda, 2);
		addHoverButton(45302, "Interfaces/Minigame/Hover", 0, 172, 24, "RockCrabs", -1, 45303, 1);
		addHoveredButton(45303, "Interfaces/Minigame/Hover", 5, 172, 24, 45304);
		addHoverButton(45318, "Interfaces/Minigame/Hover", 0, 172, 24, "Taverly Dung", -1, 45319, 1);
		addHoveredButton(45319, "Interfaces/Minigame/Hover", 5, 172, 24, 45320);
		addHoverButton(45321, "Interfaces/Minigame/Hover", 0, 172, 24, "Brimhaven Dung", -1, 45322, 1);
		addHoveredButton(45322, "Interfaces/Minigame/Hover", 5, 172, 24, 45323);
		addHoverButton(45324, "Interfaces/Minigame/Hover", 0, 172, 24, "Slayer Tower", -1, 45325, 1);
		addHoveredButton(45325, "Interfaces/Minigame/Hover", 5, 172, 24, 45326);
		addHoverButton(45327, "Interfaces/Minigame/Hover", 0, 172, 24, "Hill Giants", -1, 45328, 1);
		addHoveredButton(45328, "Interfaces/Minigame/Hover", 5, 172, 24, 45329);
		addHoverButton(45333, "Interfaces/Minigame/Back", 0, 16, 16, "Back", -1, 45334, 1);
		addHoveredButton(45334, "Interfaces/Minigame/Back", 1, 16, 16, 45335);
		addSprite(45305, 1, "Interfaces/Minigame/Crabs");
		addSprite(45306, 1, "Interfaces/Minigame/Taverly");
		addSprite(45307, 1, "Interfaces/Minigame/brimhaven");
		addSprite(45308, 1, "Interfaces/Minigame/Slayer");
		addSprite(45309, 1, "Interfaces/Minigame/HillGiants");
		addSprite(45311, 1, "Interfaces/Minigame/Background");
		addText(45312, "RockCrabs", 0xd67b29, true, true, 52, tda, 2);
		addText(45313, "Taverly Dung", 0xd67b29, true, true, 52, tda, 2);
		addText(45314, "Brimhaven Dung", 0xd67b29, true, true, 52, tda, 2);
		addText(45315, "Slayer Tower", 0xd67b29, true, true, 52, tda, 2);
		addText(45316, "Hill Giants", 0xd67b29, true, true, 52, tda, 2);
		byte childAmount = 24;
		int indexChild = 0;
		setChildren(childAmount, rsinterface);
		setBounds(45311, 0, 26, indexChild, rsinterface);
		indexChild++;
		setBounds(45301, 33, 7, indexChild, rsinterface);
		indexChild++;
		setBounds(45302, 8, 35, indexChild, rsinterface);
		indexChild++;
		setBounds(45303, 8, 35, indexChild, rsinterface);
		indexChild++;
		setBounds(45312, 80, 39, indexChild, rsinterface);
		indexChild++;
		setBounds(45318, 8, 72, indexChild, rsinterface);
		indexChild++;
		setBounds(45319, 8, 72, indexChild, rsinterface);
		indexChild++;
		setBounds(45313, 80, 76, indexChild, rsinterface);
		indexChild++;
		setBounds(45321, 8, 109, indexChild, rsinterface);
		indexChild++;
		setBounds(45322, 8, 109, indexChild, rsinterface);
		indexChild++;
		setBounds(45314, 80, 113, indexChild, rsinterface);
		indexChild++;
		setBounds(45324, 8, 146, indexChild, rsinterface);
		indexChild++;
		setBounds(45325, 8, 146, indexChild, rsinterface);
		indexChild++;
		setBounds(45315, 80, 150, indexChild, rsinterface);
		indexChild++;
		setBounds(45327, 8, 183, indexChild, rsinterface);
		indexChild++;
		setBounds(45328, 8, 183, indexChild, rsinterface);
		indexChild++;
		setBounds(45316, 80, 187, indexChild, rsinterface);
		indexChild++;
		setBounds(45305, 148, 33, indexChild, rsinterface);
		indexChild++;
		setBounds(45306, 148, 70, indexChild, rsinterface);
		indexChild++;
		setBounds(45307, 148, 104, indexChild, rsinterface);
		indexChild++;
		setBounds(45308, 148, 140, indexChild, rsinterface);
		indexChild++;
		setBounds(45309, 148, 179, indexChild, rsinterface);
		indexChild++;
		setBounds(45333, 10, 6, indexChild, rsinterface);
		indexChild++;
		setBounds(45334, 10, 6, indexChild, rsinterface);
		indexChild++;
	}

	public static void Sidebar0(TextDrawingArea[] tda) {
		/*
		 * Sidebar0a(id, id2, id3, "text1", "text2", "text3", "text4", str1x,
		 * str1y, str2x, str2y, str3x, str3y, str4x, str4y, img1x, img1y, img2x,
		 * img2y, img3x, img3y, img4x, img4y, tda);
		 */
		Sidebar0a(1698, 1701, 7499, "Chop", "Hack", "Smash", "Block", 42, 75,
				127, 75, 39, 128, 125, 128, 122, 103, 40, 50, 122, 50, 40, 103,
				tda);
		Sidebar0a(2276, 2279, 7574, "Stab", "Lunge", "Slash", "Block", 43, 75,
				124, 75, 41, 128, 125, 128, 122, 103, 40, 50, 122, 50, 40, 103,
				tda);
		Sidebar0a(2423, 2426, 7599, "Chop", "Slash", "Lunge", "Block", 42, 75,
				125, 75, 40, 128, 125, 128, 122, 103, 40, 50, 122, 50, 40, 103,
				tda);
		Sidebar0a(3796, 3799, 7624, "Pound", "Pummel", "Spike", "Block", 39,
				75, 121, 75, 41, 128, 125, 128, 122, 103, 40, 50, 122, 50, 40,
				103, tda);
		Sidebar0a(4679, 4682, 7674, "Lunge", "Swipe", "Pound", "Block", 40, 75,
				124, 75, 39, 128, 125, 128, 122, 103, 40, 50, 122, 50, 40, 103,
				tda);
		Sidebar0a(4705, 4708, 7699, "Chop", "Slash", "Smash", "Block", 42, 75,
				125, 75, 39, 128, 125, 128, 122, 103, 40, 50, 122, 50, 40, 103,
				tda);
		Sidebar0a(5570, 5573, 7724, "Spike", "Impale", "Smash", "Block", 41,
				75, 123, 75, 39, 128, 125, 128, 122, 103, 40, 50, 122, 50, 40,
				103, tda);
		Sidebar0a(7762, 7765, 7800, "Chop", "Slash", "Lunge", "Block", 42, 75,
				125, 75, 40, 128, 125, 128, 122, 103, 40, 50, 122, 50, 40, 103,
				tda);
		/*
		 * Sidebar0b(id, id2, "text1", "text2", "text3", "text4", str1x, str1y,
		 * str2x, str2y, str3x, str3y, str4x, str4y, img1x, img1y, img2x, img2y,
		 * img3x, img3y, img4x, img4y, tda);
		 */
		Sidebar0b(776, 779, "Reap", "Chop", "Jab", "Block", 42, 75, 126, 75,
				46, 128, 125, 128, 122, 103, 122, 50, 40, 103, 40, 50, tda);
		/*
		 * Sidebar0c(id, id2, id3, "text1", "text2", "text3", str1x, str1y,
		 * str2x, str2y, str3x, str3y, img1x, img1y, img2x, img2y, img3x, img3y,
		 * tda);
		 */
		Sidebar0c(425, 428, 7474, "Pound", "Pummel", "Block", 39, 75, 121, 75,
				42, 128, 40, 103, 40, 50, 122, 50, tda);
		Sidebar0c(1749, 1752, 7524, "Accurate", "Rapid", "Longrange", 33, 75,
				125, 75, 29, 128, 40, 103, 40, 50, 122, 50, tda);
		Sidebar0c(1764, 1767, 7549, "Accurate", "Rapid", "Longrange", 33, 75,
				125, 75, 29, 128, 40, 103, 40, 50, 122, 50, tda);
		Sidebar0c(4446, 4449, 7649, "Accurate", "Rapid", "Longrange", 33, 75,
				125, 75, 29, 128, 40, 103, 40, 50, 122, 50, tda);
		Sidebar0c(5855, 5857, 7749, "Punch", "Kick", "Block", 40, 75, 129, 75,
				42, 128, 40, 50, 122, 50, 40, 103, tda);
		Sidebar0c(6103, 6132, 6117, "Bash", "Pound", "Block", 43, 75, 124, 75,
				42, 128, 40, 103, 40, 50, 122, 50, tda);
		Sidebar0c(8460, 8463, 8493, "Jab", "Swipe", "Fend", 46, 75, 124, 75,
				43, 128, 40, 103, 40, 50, 122, 50, tda);
		Sidebar0c(12290, 12293, 12323, "Flick", "Lash", "Deflect", 44, 75, 127,
				75, 36, 128, 40, 50, 40, 103, 122, 50, tda);
		/*
		 * Sidebar0d(id, id2, "text1", "text2", "text3", str1x, str1y, str2x,
		 * str2y, str3x, str3y, img1x, img1y, img2x, img2y, img3x, img3y, tda);
		 */
		Sidebar0d(328, 331, "Bash", "Pound", "Focus", 42, 66, 39, 101, 41, 136,
				40, 120, 40, 50, 40, 85, tda);

		RSInterface rsi = addInterface(19300);
		/* addToggleButton(id, sprite, config, width, height, wid); */
		addToggleButton(150, 150, 172, 150, 44, "Auto Retaliate");
		rsi.totalChildren(22, 22, 22);
		rsi.child(0, 19000, 92, 26); // Combat level
		rsi.child(2, 19001, 500, 500); // Constitution updating
		rsi.child(1, 150, 21, 153); // auto retaliate
		rsi.child(3, 34555, 500, 500);
		rsi.child(4, 34556, 500, 500);
		rsi.child(4, 19001, 500, 500);
		rsi.child(5, 32002, 500, 500);
		rsi.child(6, 32003, 500, 500);
		rsi.child(7, 32004, 500, 500);
		rsi.child(8, 32005, 500, 500);
		rsi.child(9, 32006, 500, 500);
		rsi.child(10, 33001, 500, 500);
		rsi.child(11, 33002, 500, 500);
		rsi.child(12, 33003, 500, 500);
		rsi.child(13, 33004, 500, 500);
		rsi.child(14, 33005, 500, 500);
		rsi.child(15, 33006, 500, 500);
		rsi.child(16, 33101, 500, 500);
		rsi.child(17, 33102, 500, 500);
		rsi.child(18, 33103, 500, 500);
		rsi.child(19, 33104, 500, 500);
		rsi.child(20, 33105, 500, 500);
		rsi.child(21, 33106, 500, 500);

		addText(34555, "", tda, 0, 0xffffff, true, false);
		addText(34556, "", tda, 0, 0xffffff, true, false);
		addText(19000, "Combat level:", tda, 0, 0xff981f, true, false);
		addText(19001, "lolquoi", tda, 0, 0xff981f, true, false);
		addText(32001, "", tda, 0, 0xff981f, true, false);
		addText(32002, "", tda, 0, 0xff981f, true, false);
		addText(32003, "", tda, 0, 0xff981f, true, false);
		addText(32004, "", tda, 0, 0xff981f, true, false);
		addText(32005, "", tda, 0, 0xff981f, true, false);
		addText(32006, "", tda, 0, 0xff981f, true, false);
		addText(33001, "", tda, 0, 0xff981f, true, false);
		addText(33002, "", tda, 0, 0xff981f, true, false);
		addText(33003, "", tda, 0, 0xff981f, true, false);
		addText(33004, "", tda, 0, 0xff981f, true, false);
		addText(33005, "", tda, 0, 0xff981f, true, false);
		addText(33006, "", tda, 0, 0xff981f, true, false);
		addText(33101, "", tda, 0, 0xff981f, true, false);
		addText(33102, "", tda, 0, 0xff981f, true, false);
		addText(33103, "", tda, 0, 0xff981f, true, false);
		addText(33104, "", tda, 0, 0xff981f, true, false);
		addText(33105, "", tda, 0, 0xff981f, true, false);
		addText(33106, "", tda, 0, 0xff981f, true, false);
	}

	public static void Sidebar0a(int id, int id2, int id3, String text1,
			String text2, String text3, String text4, int str1x, int str1y,
			int str2x, int str2y, int str3x, int str3y, int str4x, int str4y,
			int img1x, int img1y, int img2x, int img2y, int img3x, int img3y,
			int img4x, int img4y, TextDrawingArea[] tda) // 4button spec
	{
		RSInterface rsi = addInterface(id); // 2423
		/* addText(ID, "Text", tda, Size, Colour, Centered); */
		addText(id2, "-2", tda, 3, 0xff981f, true); // 2426
		addText(id2 + 11, text1, tda, 0, 0xff981f, false);
		addText(id2 + 12, text2, tda, 0, 0xff981f, false);
		addText(id2 + 13, text3, tda, 0, 0xff981f, false);
		addText(id2 + 14, text4, tda, 0, 0xff981f, false);
		/* specialBar(ID); */
		rsi.specialBar(id3); // 7599

		rsi.width = 190;
		rsi.height = 261;

		int last = 15;
		int frame = 0;
		rsi.totalChildren(last, last, last);

		rsi.child(frame, id2 + 3, 21, 46);
		frame++; // 2429
		rsi.child(frame, id2 + 4, 104, 99);
		frame++; // 2430
		rsi.child(frame, id2 + 5, 21, 99);
		frame++; // 2431
		rsi.child(frame, id2 + 6, 105, 46);
		frame++; // 2432

		rsi.child(frame, id2 + 7, img1x, img1y);
		frame++; // bottomright 2433
		rsi.child(frame, id2 + 8, img2x, img2y);
		frame++; // topleft 2434
		rsi.child(frame, id2 + 9, img3x, img3y);
		frame++; // bottomleft 2435
		rsi.child(frame, id2 + 10, img4x, img4y);
		frame++; // topright 2436

		rsi.child(frame, id2 + 11, str1x, str1y);
		frame++; // chop 2437
		rsi.child(frame, id2 + 12, str2x, str2y);
		frame++; // slash 2438
		rsi.child(frame, id2 + 13, str3x, str3y);
		frame++; // lunge 2439
		rsi.child(frame, id2 + 14, str4x, str4y);
		frame++; // block 2440

		rsi.child(frame, 19300, 0, 0);
		frame++; // stuffs
		rsi.child(frame, id2, 94, 4);
		frame++; // weapon 2426
		rsi.child(frame, id3, 21, 205);
		frame++; // special attack 7599

		for (int i = id2 + 3; i < id2 + 7; i++) { // 2429 - 2433
			rsi = interfaceCache[i];
			rsi.sprite1 = CustomSpriteLoader(19301, "");
			rsi.sprite2 = CustomSpriteLoader(19301, "a");
			rsi.width = 68;
			rsi.height = 44;
		}
	}

	public static void Sidebar0b(int id, int id2, String text1, String text2,
			String text3, String text4, int str1x, int str1y, int str2x,
			int str2y, int str3x, int str3y, int str4x, int str4y, int img1x,
			int img1y, int img2x, int img2y, int img3x, int img3y, int img4x,
			int img4y, TextDrawingArea[] tda) // 4button nospec
	{
		RSInterface rsi = addInterface(id); // 2423
		/* addText(ID, "Text", tda, Size, Colour, Centered); */
		addText(id2, "-2", tda, 3, 0xff981f, true); // 2426
		addText(id2 + 11, text1, tda, 0, 0xff981f, false);
		addText(id2 + 12, text2, tda, 0, 0xff981f, false);
		addText(id2 + 13, text3, tda, 0, 0xff981f, false);
		addText(id2 + 14, text4, tda, 0, 0xff981f, false);

		rsi.width = 190;
		rsi.height = 261;

		int last = 14;
		int frame = 0;
		rsi.totalChildren(last, last, last);

		rsi.child(frame, id2 + 3, 21, 46);
		frame++; // 2429
		rsi.child(frame, id2 + 4, 104, 99);
		frame++; // 2430
		rsi.child(frame, id2 + 5, 21, 99);
		frame++; // 2431
		rsi.child(frame, id2 + 6, 105, 46);
		frame++; // 2432

		rsi.child(frame, id2 + 7, img1x, img1y);
		frame++; // bottomright 2433
		rsi.child(frame, id2 + 8, img2x, img2y);
		frame++; // topleft 2434
		rsi.child(frame, id2 + 9, img3x, img3y);
		frame++; // bottomleft 2435
		rsi.child(frame, id2 + 10, img4x, img4y);
		frame++; // topright 2436

		rsi.child(frame, id2 + 11, str1x, str1y);
		frame++; // chop 2437
		rsi.child(frame, id2 + 12, str2x, str2y);
		frame++; // slash 2438
		rsi.child(frame, id2 + 13, str3x, str3y);
		frame++; // lunge 2439
		rsi.child(frame, id2 + 14, str4x, str4y);
		frame++; // block 2440

		rsi.child(frame, 19300, 0, 0);
		frame++; // stuffs
		rsi.child(frame, id2, 94, 4);
		frame++; // weapon 2426

		for (int i = id2 + 3; i < id2 + 7; i++) { // 2429 - 2433
			rsi = interfaceCache[i];
			rsi.sprite1 = CustomSpriteLoader(19301, "");
			rsi.sprite2 = CustomSpriteLoader(19301, "a");
			rsi.width = 68;
			rsi.height = 44;
		}
	}

	public static void Sidebar0c(int id, int id2, int id3, String text1,
			String text2, String text3, int str1x, int str1y, int str2x,
			int str2y, int str3x, int str3y, int img1x, int img1y, int img2x,
			int img2y, int img3x, int img3y, TextDrawingArea[] tda) // 3button
	// spec
	{
		RSInterface rsi = addInterface(id); // 2423
		/* addText(ID, "Text", tda, Size, Colour, Centered); */
		addText(id2, "-2", tda, 3, 0xff981f, true); // 2426
		addText(id2 + 9, text1, tda, 0, 0xff981f, false);
		addText(id2 + 10, text2, tda, 0, 0xff981f, false);
		addText(id2 + 11, text3, tda, 0, 0xff981f, false);
		/* specialBar(ID); */
		rsi.specialBar(id3); // 7599

		rsi.width = 190;
		rsi.height = 261;

		int last = 12;
		int frame = 0;
		rsi.totalChildren(last, last, last);

		rsi.child(frame, id2 + 3, 21, 99);
		frame++;
		rsi.child(frame, id2 + 4, 105, 46);
		frame++;
		rsi.child(frame, id2 + 5, 21, 46);
		frame++;

		rsi.child(frame, id2 + 6, img1x, img1y);
		frame++; // topleft
		rsi.child(frame, id2 + 7, img2x, img2y);
		frame++; // bottomleft
		rsi.child(frame, id2 + 8, img3x, img3y);
		frame++; // topright

		rsi.child(frame, id2 + 9, str1x, str1y);
		frame++; // chop
		rsi.child(frame, id2 + 10, str2x, str2y);
		frame++; // slash
		rsi.child(frame, id2 + 11, str3x, str3y);
		frame++; // lunge

		rsi.child(frame, 19300, 0, 0);
		frame++; // stuffs
		rsi.child(frame, id2, 94, 4);
		frame++; // weapon
		rsi.child(frame, id3, 21, 205);
		frame++; // special attack 7599

		for (int i = id2 + 3; i < id2 + 6; i++) {
			rsi = interfaceCache[i];
			rsi.sprite1 = CustomSpriteLoader(19301, "");
			rsi.sprite2 = CustomSpriteLoader(19301, "a");
			rsi.width = 68;
			rsi.height = 44;
		}
	}

	public static void Sidebar0d(int id, int id2, String text1, String text2,
			String text3, int str1x, int str1y, int str2x, int str2y,
			int str3x, int str3y, int img1x, int img1y, int img2x, int img2y,
			int img3x, int img3y, TextDrawingArea[] tda) // 3button nospec
	// (magic intf)
	{
		RSInterface rsi = addInterface(id); // 2423
		/* addText(ID, "Text", tda, Size, Colour, Centered); */
		addText(id2, "-2", tda, 3, 0xff981f, true); // 2426
		addText(id2 + 9, text1, tda, 0, 0xff981f, false);
		addText(id2 + 10, text2, tda, 0, 0xff981f, false);
		addText(id2 + 11, text3, tda, 0, 0xff981f, false);

		// addText(353, "Spell", tda, 0, 0xff981f, false);
		removeSomething(353);
		addText(354, "Spell", tda, 0, 0xff981f, false);

		addCacheSprite(337, 19, 0, "combaticons");
		addCacheSprite(338, 13, 0, "combaticons2");
		addCacheSprite(339, 14, 0, "combaticons2");

		/* addToggleButton(id, sprite, config, width, height, tooltip); */
		// addToggleButton(349, 349, 108, 68, 44, "Select");
		removeSomething(349);
		addToggleButton(350, 350, 108, 68, 44, "Select");

		rsi.width = 190;
		rsi.height = 261;

		int last = 15;
		int frame = 0;
		rsi.totalChildren(last, last, last);

		rsi.child(frame, id2 + 3, 20, 115);
		frame++;
		rsi.child(frame, id2 + 4, 20, 80);
		frame++;
		rsi.child(frame, id2 + 5, 20, 45);
		frame++;

		rsi.child(frame, id2 + 6, img1x, img1y);
		frame++; // topleft
		rsi.child(frame, id2 + 7, img2x, img2y);
		frame++; // bottomleft
		rsi.child(frame, id2 + 8, img3x, img3y);
		frame++; // topright

		rsi.child(frame, id2 + 9, str1x, str1y);
		frame++; // bash
		rsi.child(frame, id2 + 10, str2x, str2y);
		frame++; // pound
		rsi.child(frame, id2 + 11, str3x, str3y);
		frame++; // focus

		rsi.child(frame, 349, 105, 46);
		frame++; // spell1
		rsi.child(frame, 350, 104, 106);
		frame++; // spell2

		rsi.child(frame, 353, 125, 74);
		frame++; // spell
		rsi.child(frame, 354, 125, 134);
		frame++; // spell

		rsi.child(frame, 19300, 0, 0);
		frame++; // stuffs
		rsi.child(frame, id2, 94, 4);
		frame++; // weapon
	}

	public static void EquipmentTab(TextDrawingArea[] tda) {
		RSInterface tab = interfaceCache[1644];
		addHoverButton(15201, "Interfaces/Equipment/CUSTOM", 1, 40, 40,
				"Show Equipment Screen", 0, 15202, 1);
		addHoveredButton(15202, "Interfaces/Equipment/CUSTOM", 5, 40, 40, 15203);
		addHoverButton(15204, "Interfaces/Equipment/CUSTOM", 2, 40, 40,
				"Items Kept on Death", 0, 15205, 1);
		addHoveredButton(15205, "Interfaces/Equipment/CUSTOM", 4, 40, 40, 15206);

		// removeSomething(15109);

		tab.child(23, 15201, 21, 210);
		// tab.child(1, 15226, 95, 250);
		tab.child(24, 15202, 21, 210);
		tab.child(25, 15204, 129, 210);
		tab.child(26, 15205, 129, 210);
		/* tab.child(3, 15109, 41+39+30, 212); */
	}

	public static void note(TextDrawingArea[] tda) {
		RSInterface tab = addTabInterface(173);
		addSprite(17351, 0, "Interfaces/Notes/NOTE");
		addHoverButton(17352, "Interfaces/Notes/NOTE", 1, 200, 30, "Add note", -1, 17353, 1);
		addHoveredButton(17353, "Interfaces/Notes/NOTE", 2, 200, 30, 17354);
		addHoverButton(17355, "Interfaces/Notes/NOTE", 3, 200, 30, "Delete all", -1, 17356, 1);
		addHoveredButton(17356, "Interfaces/Notes/NOTE", 4, 200, 30, 17357);
		addText(17800, "No notes", tda, 0, 0xffffff, false, true);
		addText(17801, "", tda, 0, 0xff981f, false, true);
		addText(17812, "", tda, 0, 0xff981f, false, true);
		addText(17813, "", tda, 0, 0xff981f, false, true);
		addText(17814, "", tda, 0, 0xff981f, false, true);
		tab.totalChildren(6);
		tab.child(0, 17351, 0, 0);
		tab.child(1, 17352, 8, 2);
		tab.child(2, 17353, 8, 2);
		tab.child(3, 17355, 165, 237);
		tab.child(4, 17356, 165, 237);
		tab.child(5, 17800, 68, 78);
		tab = addTabInterface(14000);
		tab.width = 474;
		tab.height = 213;
		tab.scrollMax = 305;
		for(int i = 14001; i <= 14030; i++){
			addText(i, "", tda, 1, 0xffffff, false, true);
		}
		tab.totalChildren(30);
		int Child = 0;
		int Y = 5;
		for(int i = 14001; i <= 14030; i++){
			tab.child(Child, i, 248, Y);
			Child++;
			Y += 13;
		}
	}

	public static void equipmentScreen(TextDrawingArea[] wid) {

		RSInterface tab = addTabInterface(15106);
		addSprite(15107, 1, "bg");
		addHoverButton(15210, "SPRITE", 1, 21, 21, "Close", 250, 15211, 3);
		addHoveredButton(15211, "SPRITE", 3, 21, 21, 15212);
		addText(15111, "", wid, 2, 0xe4a146, false, true);
		int rofl = 3;
		addText(15112, "Attack bonuses", wid, 2, 0xFF8900, false, true);
		addText(15113, "Defence bonuses", wid, 2, 0xFF8900, false, true);
		addText(15114, "Other bonuses", wid, 2, 0xFF8900, false, true);
		addText(19148, "Summoning: +0", wid, 1, 0xFF8900, false, true);
		addText(19149, "Absorb Melee: +0%", wid, 1, 0xFF9200, false, true);
		addText(19150, "Absorb Magic: +0%", wid, 1, 0xFF9200, false, true);
		addText(19151, "Absorb Ranged: +0%", wid, 1, 0xFF9200, false, true);
		addText(19152, "Ranged Strength: 0", wid, 1, 0xFF9200, false, true);
		addText(19153, "Magic Damage: +0%", wid, 1, 0xFF9200, false, true);
		for (int i = 1675; i <= 1684; i++) {
			textSize(i, wid, 1);
		}
		textSize(1686, wid, 1);
		textSize(1687, wid, 1);
		addChar(15125);
		tab.totalChildren(50);
		tab.child(0, 15107, 15, 5);
		tab.child(1, 15210, 476, 8);
		tab.child(2, 15211, 476, 8);
		tab.child(3, 15111, 14, 30);
		int Child = 4;
		int Y = 45;
		tab.child(16, 15112, 24, 30 - rofl);
		for (int i = 1675; i <= 1679; i++) {
			tab.child(Child, i, 29, Y - rofl);
			Child++;
			Y += 14;
		}
		int edit = 7 + rofl;
		tab.child(18, 15113, 24, 122 - edit); // 147
		tab.child(9, 1680, 29, 137 - edit - 2); // 161
		tab.child(10, 1681, 29, 153 - edit - 3);
		tab.child(11, 1682, 29, 168 - edit - 3);
		tab.child(12, 1683, 29, 183 - edit - 3);
		tab.child(13, 1684, 29, 197 - edit - 3);
		tab.child(44, 19148, 29, 211 - edit - 3);
		tab.child(45, 19149, 29, 225 - edit - 3);
		tab.child(46, 19150, 29, 239 - edit - 3);
		tab.child(47, 19151, 29, 253 - edit - 3);
		/* bottom */
		int edit2 = 33 - rofl, edit3 = 2;
		tab.child(19, 15114, 24, 223 + edit2);
		tab.child(14, 1686, 29, 262 - 24 + edit2 - edit3);
		tab.child(17, 19152, 29, 276 - 24 + edit2 - edit3);
		tab.child(48, 1687, 29, 290 - 24 + edit2 - edit3);
		tab.child(49, 19153, 29, 304 - 24 + edit2 - edit3);

		tab.child(15, 15125, 170, 200);
		tab.child(20, 1645, 104 + 295, 149 - 52);
		tab.child(21, 1646, 399, 163);
		tab.child(22, 1647, 399, 163);
		tab.child(23, 1648, 399, 58 + 146);
		tab.child(24, 1649, 26 + 22 + 297 - 2, 110 - 44 + 118 - 13 + 5);
		tab.child(25, 1650, 321 + 22, 58 + 154);
		tab.child(26, 1651, 321 + 134, 58 + 118);
		tab.child(27, 1652, 321 + 134, 58 + 154);
		tab.child(28, 1653, 321 + 48, 58 + 81);
		tab.child(29, 1654, 321 + 107, 58 + 81);
		tab.child(30, 1655, 321 + 58, 58 + 42);
		tab.child(31, 1656, 321 + 112, 58 + 41);
		tab.child(32, 1657, 321 + 78, 58 + 4);
		tab.child(33, 1658, 321 + 37, 58 + 43);
		tab.child(34, 1659, 321 + 78, 58 + 43);
		tab.child(35, 1660, 321 + 119, 58 + 43);
		tab.child(36, 1661, 321 + 22, 58 + 82);
		tab.child(37, 1662, 321 + 78, 58 + 82);
		tab.child(38, 1663, 321 + 134, 58 + 82);
		tab.child(39, 1664, 321 + 78, 58 + 122);
		tab.child(40, 1665, 321 + 78, 58 + 162);
		tab.child(41, 1666, 321 + 22, 58 + 162);
		tab.child(42, 1667, 321 + 134, 58 + 162);
		tab.child(43, 1688, 50 + 297 - 2, 110 - 13 + 5);
		for (int i = 1675; i <= 1684; i++) {
			RSInterface rsi = interfaceCache[i];
			rsi.textColor = 0xFF9200;
			rsi.centerText = false;
		}
		for (int i = 1686; i <= 1687; i++) {
			RSInterface rsi = interfaceCache[i];
			rsi.textColor = 0xFF9200;
			rsi.centerText = false;
		}
	}

	public static void formParty(TextDrawingArea[] tda) {
		RSInterface tab = addTabInterface(27224);
		addSprite(27225, 0, "Interfaces/Dungeoneering/DUNG");
		addHoverButton(27326, "Interfaces/Dungeoneering/DUNG", 1, 16, 16, "Exit", 250, 27227, 5);
		addHoveredButton(27227, "Interfaces/Dungeoneering/DUNG", 2, 16, 16, 27228);
		addHoverButton(27229, "Interfaces/Dungeoneering/DUNG", 3, 180, 32, "Form Party", 250, 27230, 5);
		addHoveredButton(27230, "Interfaces/Dungeoneering/DUNG", 4, 180, 32, 27231);
		addHoverButton(27132, "Interfaces/Dungeoneering/DUNG", 7, 52, 25, "Reset", 250, 27133, 5);
		addHoveredButton(27133, "Interfaces/Dungeoneering/DUNG", 8, 52, 25, 27134);
		addText(27235, "", tda, 1, 0xffffff, true, true);
		addText(27236, "", tda, 1, 0xffffff, true, true);
		addText(27237, "", tda, 1, 0xffffff, true, true);
		addText(27238, "", tda, 1, 0xffffff, true, true);
		addText(27239, "", tda, 1, 0xffffff, true, true);
		addText(27240, "", tda, 2, 0xffffff, false, true);
		addText(27241, "", tda, 2, 0xffffff, true, true);
		addText(27242, "-", tda, 1, 0xffffff, false, true);
		addText(27243, "-", tda, 1, 0xffffff, false, true);
		int[][] data = {
				{27225, 0, 0}, {27326, 171, 1}, {27227, 171, 1}, {27229, 5, 111}, {27230, 5, 111}, {27132, 132, 230}, {27133, 132, 230},
				{27235, 91, 29}, {27236, 91, 44}, {27237, 91, 59}, {27238, 91, 75}, {27239, 91, 90}, {27240, 99, 156}, {27241, 103, 183}, {27242, 112, 229},
				{27243, 112, 245}
		};
		tab.totalChildren(16);
		for(int i = 0; i < data.length; i++) {
			tab.child(i, data[i][0], data[i][1], data[i][2]);
		}
	}

	public static void dungParty(TextDrawingArea[] tda) {
		RSInterface tab = addTabInterface(26224);
		addSprite(26225, 0, "Interfaces/Dungeoneering/DUNG");
		addHoverButton(26226, "Interfaces/Dungeoneering/DUNG", 1, 16, 16, "Exit", 250, 26227, 5);
		addHoveredButton(26227, "Interfaces/Dungeoneering/DUNG", 2, 16, 16, 26228);
		addHoverButton(26229, "Interfaces/Dungeoneering/DUNG", 5, 90, 32, "Leave Party", 250, 26230, 5);
		addHoveredButton(26230, "Interfaces/Dungeoneering/DUNG", 6, 90, 32, 26231);
		addHoverButton(26232, "Interfaces/Dungeoneering/DUNG", 7, 52, 25, "Reset", 250, 26233, 5);
		addHoveredButton(26233, "Interfaces/Dungeoneering/DUNG", 8, 52, 25, 26234);
		addText(26235, "", tda, 1, 0xffffff, true, true);
		addText(26236, "", tda, 1, 0xffffff, true, true);
		addText(26237, "", tda, 1, 0xffffff, true, true);
		addText(26238, "", tda, 1, 0xffffff, true, true);
		addText(26239, "", tda, 1, 0xffffff, true, true);
		addText(26240, "0", tda, 2, 0xffffff, false, true);
		addText(26241, "0", tda, 2, 0xffffff, true, true);
		addText(26242, "0", tda, 1, 0xffffff, false, true);
		addText(26243, "0", tda, 1, 0xffffff, false, true);
		addHoverButton(26244, "Interfaces/Dungeoneering/DUNG", 26, 61, 20, "Change", 250, 26245, 5);
		addHoveredButton(26245, "Interfaces/Dungeoneering/DUNG", 18, 61, 20, 26246);
		addHoverButton(26247, "Interfaces/Dungeoneering/DUNG", 26, 61, 20, "Change", 250, 26248, 5);
		addHoveredButton(26248, "Interfaces/Dungeoneering/DUNG", 18, 61, 20, 26249);
		addHoverButton(26250, "Interfaces/Dungeoneering/DUNG", 16, 90, 32, "Invite player", 250, 26251, 5);
		addHoveredButton(26251, "Interfaces/Dungeoneering/DUNG", 17, 90, 32, 26252);
		int[][] data = {
				{26225, 0, 0}, {26226, 171, 1}, {26227, 171, 1}, {26229, 5, 111}, {26230, 5, 111}, {26232, 132, 230}, {26233, 132, 230},
				{26235, 91, 29}, {26236, 91, 44}, {26237, 91, 59}, {26238, 91, 75}, {26239, 91, 90}, {26240, 99, 156}, {26241, 103, 183}, {26242, 112, 229},
				{26243, 112, 245}, {26244, 121, 152}, {26245, 121, 152}, {26247, 121, 180}, {26248, 121, 180},
				{26250, 95, 111}, {26251, 95, 111}
		};
		tab.totalChildren(22);
		for(int i = 0; i < data.length; i++) {
			tab.child(i, data[i][0], data[i][1], data[i][2]);
		}
	}
	
	public static void handleFloorMenus(int number, int interfaceId) {
		RSInterface rsInterface = addInterface(interfaceId);
		addSprite(interfaceId+1, 18+number, "Interfaces/Dungeoneering/DUNG");
		
	    addHoverButton(interfaceId+2, "Interfaces/Dungeoneering/DUNG", 1, 16, 16, "Close", 250,interfaceId+3, 3);
	    addHoveredButton(interfaceId+3, "Interfaces/Dungeoneering/DUNG", 2, 16, 16, interfaceId+4);
	    addHoverButton(interfaceId+5, "", -1, 74, 30, "Confirm", 250, interfaceId+6, 5);
	    addHoveredButton(interfaceId+6, "Interfaces/Dungeoneering/DUNG", 25, 74, 30, interfaceId+7);
	    
	    addButton(interfaceId+8, -1, "", 21, 34, "Select", 1);
	    addButton(interfaceId+9, -1, "", 21, 34, "Select", 1);
	    addButton(interfaceId+10, -1, "", 21, 34, "Select", 1);
	    addButton(interfaceId+11, -1, "", 21, 34, "Select", 1);
	    addButton(interfaceId+12, -1, "", 21, 34, "Select", 1);
	    addButton(interfaceId+13, -1, "", 21, 34, "Select", 1);
	    
		rsInterface.totalChildren(11);
		int[][] childs = {
				{interfaceId + 1 , 0, 0}, {interfaceId + 2,  487, 4}, {interfaceId + 3, 487, 4}, 
				{interfaceId + 5, 169, 264}, {interfaceId + 6, 169, 264},{interfaceId + 8, 27, 37},
				{interfaceId + 9, 66, 37}, {interfaceId + 10, 105, 37}, {interfaceId + 11, 141, 37},
				{interfaceId + 12, 180, 37}, {interfaceId + 13, 219, 37}
		};
	    for (int i = 0; i < childs.length; i++)
		      rsInterface.child(i, childs[i][0], childs[i][1], childs[i][2]);
	}
	
	public static void floorMenus(TextDrawingArea[] TDA) {
		handleFloorMenus(1, 35233);
		handleFloorMenus(2, 35233 + 100);
		handleFloorMenus(3, 35233 + 200);
		handleFloorMenus(4, 35233 + 300);
		handleFloorMenus(5, 35233 + 400);
		handleFloorMenus(6, 35233 + 500);
	}

	  public static void InvtoParty(TextDrawingArea[] TDA) {
	    RSInterface rsInterface = addTabInterface(40224);
	    addSprite(40225, 10, "Interfaces/Dungeoneering/DUNG");
	    addHoverButton(40226, "Interfaces/Dungeoneering/DUNG", 1, 16, 16, "Close", 250, 40227, 3);
	    addHoveredButton(40227, "Interfaces/Dungeoneering/DUNG", 2, 16, 16, 26228);
	    addHoverButton(40229, "Interfaces/Dungeoneering/DUNG", 14, 72, 32, "Accept", 250, 40230, 5);
	    addHoveredButton(40230, "Interfaces/Dungeoneering/DUNG", 11, 72, 31, 40231);
	    addHoverButton(40232, "Interfaces/Dungeoneering/DUNG", 15, 72, 32, "Decline", 250, 40233, 5);
	    addHoveredButton(40233, "Interfaces/Dungeoneering/DUNG", 12, 72, 31, 40234);
	    addText(40235, "", TDA, 1, 16777215, true, true);
	    addText(40236, "", TDA, 1, 16777215, true, true);
	    addText(40237, "", TDA, 1, 16777215, true, true);
	    addText(40238, "", TDA, 1, 16777215, true, true);
	    addText(40239, "", TDA, 1, 16777215, true, true);
	    
	    addText(40240, "0", TDA, 2, 16777215, false, true);
	    addText(40241, "0", TDA, 2, 16777215, true, true);
	    
	    addText(40242, "", TDA, 1, 16777215, true, true);
	    addText(40243, "", TDA, 1, 16777215, true, true);
	    addText(40244, "", TDA, 1, 16777215, true, true);
	    addText(40245, "", TDA, 1, 16777215, true, true);
	    addText(40246, "", TDA, 1, 16777215, true, true);
	    
	    addText(40247, "", TDA, 1, 16777215, true, true);
	    addText(40248, "", TDA, 1, 16777215, true, true);
	    addText(40249, "", TDA, 1, 16777215, true, true);
	    addText(40250, "", TDA, 1, 16777215, true, true);
	    addText(40251, "", TDA, 1, 16777215, true, true);
	    
	    addText(40252, "", TDA, 1, 16777215, true, true);
	    addText(40253, "", TDA, 1, 16777215, true, true);
	    addText(40254, "", TDA, 1, 16777215, true, true);
	    addText(40255, "", TDA, 1, 16777215, true, true);
	    addText(40256, "", TDA, 1, 16777215, true, true);
	    
	    addText(40257, "", TDA, 1, 16777215, true, true);
	    addText(40258, "", TDA, 1, 16777215, true, true);
	    addText(40259, "", TDA, 1, 16777215, true, true);
	    addText(40260, "", TDA, 1, 16777215, true, true);
	    addText(40261, "", TDA, 1, 16777215, true, true);
	    int[][] arrayOfInt = { { 40225, 14, 20 }, { 40226, 468, 23 }, { 40227, 468, 23 }, { 40229, 128, 247 }, { 40230, 129, 248 }, { 40232, 218, 247 }, 
	    		{ 40233, 219, 248 }, { 40235, 93, 74 }, { 40236, 93, 93 }, { 40237, 93, 112 }, { 40238, 93, 131 }, { 40239, 93, 150}, 
	    		{ 40240, 287, 173 }, { 40241, 290, 198 }, { 40242, 220, 74 }, { 40243, 220, 93}, {40244, 220, 112},
	    		{ 40245, 220, 131}, {40246, 220 ,150}, {40247, 290 ,74}, {40248, 290 ,93}, {40249, 290 ,112}, {40250, 290 ,131},{40251, 290, 150},
	    		{40252, 360, 74}, {40253, 360, 93}, {40254, 360, 112}, {40255, 360, 131}, {40256, 360, 150},
	    		{40257, 440, 74}, {40258, 440, 93}, {40259, 440, 112}, {40260, 440, 131}, {40261, 440, 150}
	    };

	    rsInterface.totalChildren(34);
	    for (int i = 0; i < arrayOfInt.length; i++)
	      rsInterface.child(i, arrayOfInt[i][0], arrayOfInt[i][1], arrayOfInt[i][2]);
	  }


	
	public static void itemsOnDeath(TextDrawingArea[] tda) {
		RSInterface rsinterface = addInterface(17100);
		addSprite(17101, 2, "Interfaces/Equipment/SPRITE");
		addHoverButton(17102, "Interfaces/Equipment/SPRITE", 1, 17, 17, "Close Window", 250, 10601,
				3);
		addHoveredButton(10601, "Interfaces/Equipment/SPRITE", 3, 17, 17, 10602);

		addText(17103, "Items Kept On Death", 0xff981f, false, true, 0, tda, 2);
		addText(17104, "Items you will keep on death (if not skulled):",
				0xff981f, false, true, 0, tda, 2);
		addText(17105, "Items you will lose on death (if not skulled):",
				0xff981f, false, true, 0, tda, 2);
		addText(17106, "Information", 0xff981f, false, true, 0, tda, 1);
		addText(17107, "Max items kept on death:", 0xff981f, false, true, 0,
				tda, 1);
		addText(17108, "~ 3 ~", 0xffcc33, false, true, 0, tda, 1);
		rsinterface.scrollMax = 0;
		rsinterface.interfaceShown = false;
		rsinterface.children = new int[12];
		rsinterface.childX = new int[12];
		rsinterface.childY = new int[12];

		rsinterface.children[0] = 17101;
		rsinterface.childX[0] = 7;
		rsinterface.childY[0] = 8;
		rsinterface.children[1] = 17102;
		rsinterface.childX[1] = 480;
		rsinterface.childY[1] = 17;
		rsinterface.children[2] = 17103;
		rsinterface.childX[2] = 185;
		rsinterface.childY[2] = 18;
		rsinterface.children[3] = 17104;
		rsinterface.childX[3] = 22;
		rsinterface.childY[3] = 50;
		rsinterface.children[4] = 17105;
		rsinterface.childX[4] = 22;
		rsinterface.childY[4] = 110;
		rsinterface.children[5] = 17106;
		rsinterface.childX[5] = 347;
		rsinterface.childY[5] = 47;
		rsinterface.children[6] = 17107;
		rsinterface.childX[6] = 349;
		rsinterface.childY[6] = 270;
		rsinterface.children[7] = 17108;
		rsinterface.childX[7] = 398;
		rsinterface.childY[7] = 298;
		rsinterface.children[8] = 17115;
		rsinterface.childX[8] = 348;
		rsinterface.childY[8] = 64;
		rsinterface.children[9] = 10494;
		rsinterface.childX[9] = 26;
		rsinterface.childY[9] = 74;
		rsinterface.children[10] = 10600;
		rsinterface.childX[10] = 26;
		rsinterface.childY[10] = 133;
		rsinterface.children[11] = 10601;
		rsinterface.childX[11] = 480;
		rsinterface.childY[11] = 17;
	}

	public static void itemsOnDeathDATA(TextDrawingArea[] tda) {
		RSInterface RSinterface = addInterface(17115);
		addText(17109, "", 0xff981f, false, false, 0, tda, 0);
		addText(17110, "The normal amount of", 0xff981f, false, false, 0, tda,
				0);
		addText(17111, "items kept is three.", 0xff981f, false, false, 0, tda,
				0);
		addText(17112, "", 0xff981f, false, false, 0, tda, 0);
		addText(17113, "If you are skulled,", 0xff981f, false, false, 0, tda, 0);
		addText(17114, "you will lose all your", 0xff981f, false, false, 0,
				tda, 0);
		addText(17117, "items, unless an item", 0xff981f, false, false, 0, tda,
				0);
		addText(17118, "protecting prayer is", 0xff981f, false, false, 0, tda,
				0);
		addText(17119, "used.", 0xff981f, false, false, 0, tda, 0);
		addText(17120, "", 0xff981f, false, false, 0, tda, 0);
		addText(17121, "Item protecting prayers", 0xff981f, false, false, 0,
				tda, 0);
		addText(17122, "will allow you to keep", 0xff981f, false, false, 0,
				tda, 0);
		addText(17123, "one extra item.", 0xff981f, false, false, 0, tda, 0);
		addText(17124, "", 0xff981f, false, false, 0, tda, 0);
		addText(17125, "The items kept are", 0xff981f, false, false, 0, tda, 0);
		addText(17126, "selected by the server", 0xff981f, false, false, 0,
				tda, 0);
		addText(17127, "and include the most", 0xff981f, false, false, 0, tda,
				0);
		addText(17128, "expensive items you're", 0xff981f, false, false, 0,
				tda, 0);
		addText(17129, "carrying.", 0xff981f, false, false, 0, tda, 0);
		addText(17130, "", 0xff981f, false, false, 0, tda, 0);
		RSinterface.parentID = 17115;
		RSinterface.id = 17115;
		RSinterface.type = 0;
		RSinterface.atActionType = 0;
		RSinterface.contentType = 0;
		RSinterface.width = 130;
		RSinterface.height = 197;
		RSinterface.opacity = 0;
		RSinterface.hoverType = -1;
		RSinterface.scrollMax = 280;
		RSinterface.children = new int[20];
		RSinterface.childX = new int[20];
		RSinterface.childY = new int[20];
		RSinterface.children[0] = 17109;
		RSinterface.childX[0] = 0;
		RSinterface.childY[0] = 0;
		RSinterface.children[1] = 17110;
		RSinterface.childX[1] = 0;
		RSinterface.childY[1] = 12;
		RSinterface.children[2] = 17111;
		RSinterface.childX[2] = 0;
		RSinterface.childY[2] = 24;
		RSinterface.children[3] = 17112;
		RSinterface.childX[3] = 0;
		RSinterface.childY[3] = 36;
		RSinterface.children[4] = 17113;
		RSinterface.childX[4] = 0;
		RSinterface.childY[4] = 48;
		RSinterface.children[5] = 17114;
		RSinterface.childX[5] = 0;
		RSinterface.childY[5] = 60;
		RSinterface.children[6] = 17117;
		RSinterface.childX[6] = 0;
		RSinterface.childY[6] = 72;
		RSinterface.children[7] = 17118;
		RSinterface.childX[7] = 0;
		RSinterface.childY[7] = 84;
		RSinterface.children[8] = 17119;
		RSinterface.childX[8] = 0;
		RSinterface.childY[8] = 96;
		RSinterface.children[9] = 17120;
		RSinterface.childX[9] = 0;
		RSinterface.childY[9] = 108;
		RSinterface.children[10] = 17121;
		RSinterface.childX[10] = 0;
		RSinterface.childY[10] = 120;
		RSinterface.children[11] = 17122;
		RSinterface.childX[11] = 0;
		RSinterface.childY[11] = 132;
		RSinterface.children[12] = 17123;
		RSinterface.childX[12] = 0;
		RSinterface.childY[12] = 144;
		RSinterface.children[13] = 17124;
		RSinterface.childX[13] = 0;
		RSinterface.childY[13] = 156;
		RSinterface.children[14] = 17125;
		RSinterface.childX[14] = 0;
		RSinterface.childY[14] = 168;
		RSinterface.children[15] = 17126;
		RSinterface.childX[15] = 0;
		RSinterface.childY[15] = 180;
		RSinterface.children[16] = 17127;
		RSinterface.childX[16] = 0;
		RSinterface.childY[16] = 192;
		RSinterface.children[17] = 17128;
		RSinterface.childX[17] = 0;
		RSinterface.childY[17] = 204;
		RSinterface.children[18] = 17129;
		RSinterface.childX[18] = 0;
		RSinterface.childY[18] = 216;
		RSinterface.children[19] = 17130;
		RSinterface.childX[19] = 0;
		RSinterface.childY[19] = 228;
	}

	private static void newBank() {
		RSInterface Interface = addTabInterface(5292);
		setChildren(19, Interface);
		addSprite(5293, 0, "Bank/BANK");
		setBounds(5293, 13, 13, 0, Interface);
		addHoverButton(5384, "SPRITE", 1, 17, 17, "Close Window", 250, 5380, 3);
		addHoveredButton(5380, "SPRITE", 3, 17, 17, 5379);
		setBounds(5384, 476, 16, 3, Interface);
		setBounds(5380, 476, 16, 4, Interface);
		addHoverButton(5294, "Bank/BANK", 3, 114, 25, "Set A Bank PIN", 250,
				5295, 4);
		addHoveredButton(5295, "Bank/BANK", 4, 114, 25, 5296);
		setBounds(5294, 110, 285, 5, Interface);
		setBounds(5295, 110, 285, 6, Interface);
		addBankHover(21000, 4, 21001, 5, 8, "Bank/BANK", 35, 25, 304, 1,
				"Swap Withdraw Mode", 21002, 7, 6, "Bank/BANK", 21003,
				"Switch to insert items \nmode",
				"Switch to swap items \nmode.", 12, 20);
		setBounds(21000, 25, 285, 7, Interface);
		setBounds(21001, 10, 225, 8, Interface);
		addBankHover(21004, 4, 21005, 13, 15, "Bank/BANK", 35, 25, 0, 1,
				"Search", 21006, 14, 16, "Bank/BANK", 21007,
				"Click here to search your \nbank",
				"Click here to search your \nbank", 12, 20);
		setBounds(21004, 65, 285, 9, Interface);
		setBounds(21005, 50, 225, 10, Interface);
		addBankHover(21008, 4, 21009, 9, 11, "Bank/BANK", 35, 25, 115, 1,
				"Swap Withdrawal Mode", 21010, 10, 12, "Bank/BANK", 21011,
				"Switch to note withdrawal \nmode",
				"Switch to item withdrawal \nmode", 12, 20);
		setBounds(21008, 240, 285, 11, Interface);
		setBounds(21009, 225, 225, 12, Interface);
		addBankHover1(21012, 5, 21013, 17, "Bank/BANK", 35, 25,
				"Deposit carried items", 21014, 18, "Bank/BANK", 21015,
				"Empty your backpack into\nyour bank", 0, 20);
		setBounds(21012, 375, 285, 13, Interface);
		setBounds(21013, 360, 225, 14, Interface);
		addBankHover1(21016, 5, 21017, 19, "Bank/BANK", 35, 25,
				"Deposit worn items", 21018, 20, "Bank/BANK", 21019,
				"Empty the items your are\nwearing into your bank", 0, 20);
		setBounds(21016, 415, 285, 15, Interface);
		setBounds(21017, 400, 225, 16, Interface);
		addBankHover1(21020, 5, 21021, 21, "Bank/BANK", 35, 25,
				"Deposit beast of burden inventory.", 21022, 22, "Bank/BANK",
				21023, "Empty your BoB's inventory\ninto your bank", 0, 20);
		setBounds(21020, 455, 285, 17, Interface);
		setBounds(21021, 440, 225, 18, Interface);
		setBounds(5383, 170, 15, 1, Interface);
		setBounds(5385, -4, 34, 2, Interface);
		Interface = interfaceCache[5385];
		Interface.height = 247;
		Interface.width = 480;
		Interface = interfaceCache[5382];
		Interface.width = 10;
		Interface.invSpritePadX = 12;
		Interface.height = 35;
	}

	public static void addBankHover(int interfaceID, int actionType,
			int hoverid, int spriteId, int spriteId2, String NAME, int Width,
			int Height, int configFrame, int configId, String Tooltip,
			int hoverId2, int hoverSpriteId, int hoverSpriteId2,
			String hoverSpriteName, int hoverId3, String hoverDisabledText,
			String hoverEnabledText, int X, int Y) {
		RSInterface hover = addTabInterface(interfaceID);
		hover.id = interfaceID;
		hover.parentID = interfaceID;
		hover.type = 5;
		hover.atActionType = actionType;
		hover.contentType = 0;
		hover.opacity = 0;
		hover.hoverType = hoverid;
		hover.sprite1 = imageLoader(spriteId, NAME);
		hover.sprite2 = imageLoader(spriteId2, NAME);
		hover.width = Width;
		hover.tooltip = Tooltip;
		hover.height = Height;
		hover.valueCompareType = new int[1];
		hover.requiredValues = new int[1];
		hover.valueCompareType[0] = 1;
		hover.requiredValues[0] = configId;
		hover.valueIndexArray = new int[1][3];
		hover.valueIndexArray[0][0] = 5;
		hover.valueIndexArray[0][1] = configFrame;
		hover.valueIndexArray[0][2] = 0;
		hover = addTabInterface(hoverid);
		hover.parentID = hoverid;
		hover.id = hoverid;
		hover.type = 0;
		hover.atActionType = 0;
		hover.width = 550;
		hover.height = 334;
		hover.interfaceShown = true;
		hover.hoverType = -1;
		addSprite(hoverId2, hoverSpriteId, hoverSpriteId2, hoverSpriteName,
				configId, configFrame);
		addHoverBox(hoverId3, interfaceID, hoverDisabledText, hoverEnabledText,
				configId, configFrame);
		setChildren(2, hover);
		setBounds(hoverId2, 15, 60, 0, hover);
		setBounds(hoverId3, X, Y, 1, hover);
	}

	public static void addBankHover1(int interfaceID, int actionType,
			int hoverid, int spriteId, String NAME, int Width, int Height,
			String Tooltip, int hoverId2, int hoverSpriteId,
			String hoverSpriteName, int hoverId3, String hoverDisabledText,
			int X, int Y) {
		RSInterface hover = addTabInterface(interfaceID);
		hover.id = interfaceID;
		hover.parentID = interfaceID;
		hover.type = 5;
		hover.atActionType = actionType;
		hover.contentType = 0;
		hover.opacity = 0;
		hover.hoverType = hoverid;
		hover.sprite1 = imageLoader(spriteId, NAME);
		hover.width = Width;
		hover.tooltip = Tooltip;
		hover.height = Height;
		hover = addTabInterface(hoverid);
		hover.parentID = hoverid;
		hover.id = hoverid;
		hover.type = 0;
		hover.atActionType = 0;
		hover.width = 550;
		hover.height = 334;
		hover.interfaceShown = true;
		hover.hoverType = -1;
		addSprite(hoverId2, hoverSpriteId, hoverSpriteId, hoverSpriteName, 0, 0);
		addHoverBox(hoverId3, interfaceID, hoverDisabledText,
				hoverDisabledText, 0, 0);
		setChildren(2, hover);
		setBounds(hoverId2, 15, 60, 0, hover);
		setBounds(hoverId3, X, Y, 1, hover);
	}

	public static void addHoverBox(int id, int ParentID, String text,
			String text2, int configId, int configFrame) {
		RSInterface rsi = addTabInterface(id);
		rsi.id = id;
		rsi.parentID = ParentID;
		rsi.type = 8;
		rsi.disabledText = text;
		rsi.message = text2;
		rsi.valueCompareType = new int[1];
		rsi.requiredValues = new int[1];
		rsi.valueCompareType[0] = 1;
		rsi.requiredValues[0] = configId;
		rsi.valueIndexArray = new int[1][3];
		rsi.valueIndexArray[0][0] = 5;
		rsi.valueIndexArray[0][1] = configFrame;
		rsi.valueIndexArray[0][2] = 0;
	}

	public static void addSprite(int ID, int i, int i2, String name,
			int configId, int configFrame) {
		RSInterface Tab = addTabInterface(ID);
		Tab.id = ID;
		Tab.parentID = ID;
		Tab.type = 5;
		Tab.atActionType = 0;
		Tab.contentType = 0;
		Tab.width = 512;
		Tab.height = 334;
		Tab.opacity = 0;
		Tab.hoverType = -1;
		Tab.valueCompareType = new int[1];
		Tab.requiredValues = new int[1];
		Tab.valueCompareType[0] = 1;
		Tab.requiredValues[0] = configId;
		Tab.valueIndexArray = new int[1][3];
		Tab.valueIndexArray[0][0] = 5;
		Tab.valueIndexArray[0][1] = configFrame;
		Tab.valueIndexArray[0][2] = 0;
		if (name == null) {
			Tab.itemSpriteZoom1 = -1;
			Tab.itemSpriteId1 = i;
			Tab.itemSpriteZoom2 = 70;
			Tab.itemSpriteId2 = i2;
		} else {
			Tab.sprite1 = imageLoader(i, name);
			Tab.sprite2 = imageLoader(i2, name);
		}
	}

	public static void Shop(TextDrawingArea[] TDA) {
		RSInterface rsinterface = addTabInterface(3824);
		setChildren(8, rsinterface);
		addSprite(3825, 0, "Shop/SHOP");
		addHoverButton(3902, "SPRITE", 1, 17, 17, "Close Window", 250, 3826, 3);
		addHoveredButton(3826, "SPRITE", 3, 17, 17, 3827);
		addText(19679, "", 0xff981f, false, true, 52, TDA, 1);
		addText(19680, "", 0xbf751d, false, true, 52, TDA, 1);
		addButton(19681, 2, "Shop/SHOP", 0, 0, "", 1);
		addSprite(19687, 1, "Shop/ITEMBG");
		setBounds(3825, 6, 8, 0, rsinterface);
		setBounds(3902, 478, 10, 1, rsinterface);
		setBounds(3826, 478, 10, 2, rsinterface);
		setBounds(3900, 26, 44, 3, rsinterface);
		setBounds(3901, 240, 11, 4, rsinterface);
		setBounds(19679, 42, 54, 5, rsinterface);
		setBounds(19680, 150, 54, 6, rsinterface);
		setBounds(19681, 129, 50, 7, rsinterface);
		rsinterface = interfaceCache[3900];
		setChildren(1, rsinterface);
		setBounds(19687, 6, 15, 0, rsinterface);
		rsinterface.invSpritePadX = 15;
		rsinterface.width = 10;
		rsinterface.height = 4;
		rsinterface.invSpritePadY = 25;
		rsinterface = addTabInterface(19682);
		addSprite(19683, 1, "Shop/SHOP");
		addText(19684, "Main Stock", 0xbf751d, false, true, 52, TDA, 1);
		addText(19685, "Store Info", 0xff981f, false, true, 52, TDA, 1);
		addButton(19686, 2, "Shop/SHOP", 95, 19, "Main Stock", 1);
		setChildren(7, rsinterface);
		setBounds(19683, 12, 12, 0, rsinterface);
		setBounds(3901, 240, 21, 1, rsinterface);
		setBounds(19684, 42, 54, 2, rsinterface);
		setBounds(19685, 150, 54, 3, rsinterface);
		setBounds(19686, 23, 50, 4, rsinterface);
		setBounds(3902, 471, 22, 5, rsinterface);
		setBounds(3826, 60, 85, 6, rsinterface);
	}

	public static void Trade(TextDrawingArea[] TDA){
		RSInterface Interface = addInterface(3323);
		setChildren(19, Interface);
		addSprite(3324, 6, "Interfaces/TradeTab/TRADE");
		addHover(3442, 3, 0, 3325, 1, "Bank/BANK", 17, 17, "Close Window");
		addHovered(3325, 2, "Bank/BANK", 17, 17, 3326);
		addText(3417, "Trading With:", 0xFF981F, true, true, 52,TDA, 2);
		addText(3418, "Trader's Offer", 0xFF981F, false, true, 52,TDA, 1);
		addText(3419, "Your Offer", 0xFF981F, false, true, 52,TDA, 1);
		addText(3421, "Accept", 0x00C000, true, true, 52,TDA, 1);
		addText(3423, "Decline", 0xC00000, true, true, 52,TDA, 1);

		addText(3431, "Waiting For Other Player", 0xFFFFFF, true, true, 52,TDA, 1);
		addText(53504, "Wealth transfer: 2147,000,000 coins' worth to Zezimablud12", 0xB9B855, true, true, -1,TDA, 0);
		addText(53505, "1 has\\n 28 free\\n inventory slots.", 0xFF981F, true, true, -1,TDA, 0);

		addText(53506, "Wealth transfer: 2147,000,000 coins' worth to Zezimablud12", 0xB9B855, false, true, -1,TDA, 0);
		addText(53507, "Wealth transfer: 2147,000,000 coins' worth to me", 0xB9B855, false, true, -1,TDA, 0);

		addHover(3420, 1, 0, 3327, 5, "Interfaces/TradeTab/TRADE", 65, 32, "Accept");
		addHovered(3327, 2, "Interfaces/TradeTab/TRADE", 65, 32, 3328);
		addHover(3422, 3, 0, 3329, 5, "Interfaces/TradeTab/TRADE", 65, 32, "Decline");
		addHovered(3329, 2, "Interfaces/TradeTab/TRADE", 65, 32, 3330);
		setBounds(3324, 0, 16, 0, Interface);
		setBounds(3442, 485, 24, 1, Interface);
		setBounds(3325, 485, 24, 2, Interface);
		setBounds(3417, 258, 25, 3, Interface);
		setBounds(3418, 355, 51, 4, Interface);
		setBounds(3419, 68, 51, 5, Interface);
		setBounds(3420, 223, 120, 6, Interface);
		setBounds(3327, 223, 120, 7, Interface);
		setBounds(3422, 223, 160, 8, Interface);
		setBounds(3329, 223, 160, 9, Interface);
		setBounds(3421, 256, 127, 10, Interface);
		setBounds(3423, 256, 167, 11, Interface);
		setBounds(3431, 256, 272, 12, Interface);
		setBounds(3415, 12, 64, 13, Interface);
		setBounds(3416, 321, 67, 14, Interface);

		setBounds(53505, 256, 67, 16, Interface);

		setBounds(53504, 255, 310, 15, Interface);
		setBounds(53506, 20, 310, 17, Interface);
		setBounds(53507, 380, 310, 18, Interface);

		Interface = addInterface(3443);
		setChildren(15, Interface);
		addSprite(3444, 3, "Interfaces/TradeTab/TRADE");
		addButton(3546, 2, "Interfaces/ShopTab/SHOP", 63, 24, "Accept", 1);
		addButton(3548, 2, "Interfaces/ShopTab/SHOP", 63, 24, "Decline", 3);
		addText(3547, "Accept", 0x00C000, true, true, 52,TDA, 1);
		addText(3549, "Decline", 0xC00000, true, true, 52,TDA, 1);
		addText(3450, "Trading With:", 0x00FFFF, true, true, 52,TDA, 2);
		addText(3451, "Yourself", 0x00FFFF, true, true, 52,TDA, 2);
		setBounds(3444, 12, 20, 0, Interface);
		setBounds(3442, 470, 32, 1, Interface);
		setBounds(3325, 470, 32, 2, Interface);
		setBounds(3535, 130, 28, 3, Interface);
		setBounds(3536, 105, 47, 4, Interface);
		setBounds(3546, 189, 295, 5, Interface);
		setBounds(3548, 258, 295, 6, Interface);
		setBounds(3547, 220, 299, 7, Interface);
		setBounds(3549, 288, 299, 8, Interface);
		setBounds(3557, 71, 87, 9, Interface);
		setBounds(3558, 315, 87, 10, Interface);
		setBounds(3533, 64, 70, 11, Interface);
		setBounds(3534, 297, 70, 12, Interface);
		setBounds(3450, 95, 289, 13, Interface);
		setBounds(3451, 95, 304, 14, Interface);
	}

	private static void addHead2(int id, int w, int h, int zoom) {//tewst
		RSInterface rsinterface = interfaceCache[id] = new RSInterface();
		rsinterface.type = 6;
		rsinterface.anInt233 = 2;
		rsinterface.mediaID = 4000;//
		rsinterface.modelZoom = zoom;
		rsinterface.modelRotation1 = 40;
		rsinterface.modelRotation2 = 1900;
		rsinterface.height = h;
		rsinterface.width = w;
	}

	public static void SummonTab(TextDrawingArea[] wid) {
		RSInterface Tab = addTabInterface(17011);
		addSprite(17012, 6, "SummonTab/SUMMON");
		addButton(17013, 7, "/SummonTab/SUMMON", "Click");
		addSprite(17014, 6, "SummonTab/SUMMON");
		addConfigButton(17015, 17032, 14, 8, "/SummonTab/SUMMON", 20, 30, "Familiar Special", 1, 5, 300);
		addHoverButton(17018, "/SummonTab/SUMMON", 2, 38, 36, "Beast of burden Inventory", -1, 17028, 1);
		addHoveredButton(17028, "/SummonTab/SUMMON", 12, 38, 36, 17029);
		addHoverButton(17022, "/SummonTab/SUMMON", 1, 38, 36, "Call Familiar", -1, 17030, 1);//addHoverButton(int i, String imageName, int j, int width, int height, String text, int contentType, int hoverOver, int aT) {//hoverable button
		addHoveredButton(17030, "/SummonTab/SUMMON", 13, 38, 36, 17031);
		addHoverButton(17023, "/SummonTab/SUMMON", 3, 38, 36, "Dismiss Familiar", -1, 17033, 1);//addHoverButton(int i, String imageName, int j, int width, int height, String text, int contentType, int hoverOver, int aT) {//hoverable button
		addHoveredButton(17033, "/SummonTab/SUMMON", 15, 38, 36, 17034);
		addHoverButton(17038, "/SummonTab/SUMMON", 17, 38, 36, "Renew Summon", -1, 17039, 1);
		addHoveredButton(17039, "/SummonTab/SUMMON", 18, 38, 36, 17041);
		addSprite(17016, 5, "SummonTab/SUMMON");
		addText(17017, "No familiar", wid, 2, 0xDAA520, true, true);
		addSprite(17019, 9, "SummonTab/SUMMON");
		addText(17021, "0:00", wid, 0, 0xFFA500, true, true);
		addSprite(17020, 10, "SummonTab/SUMMON");
		addSprite(17024, 11, "SummonTab/SUMMON");
		addText(17025, "49/50", wid, 0, 0xFFA500, false, true);
		addText(17026, "0", wid, 0, 0xFFA500, false, true);
		addText(17040, " ", wid, 0, 0xFFA500, false, true);
		addHead2(17027, 75, 55, 800);
		Tab.totalChildren(22);
		Tab.child(0, 17012, 10, 25);
		Tab.child(1, 17013, 24, 7);
		Tab.child(2, 17014, 10, 25);
		Tab.child(3, 17015, 11, 25);
		Tab.child(4, 17016, 15, 140);
		Tab.child(5, 17017, 95, 143);
		Tab.child(6, 17018, 20, 170);
		Tab.child(7, 17019, 115, 167);
		Tab.child(8, 17020, 143, 170);
		Tab.child(9, 17021, 145, 197);
		Tab.child(10, 17022, 20, 213);
		Tab.child(11, 17023, 67, 170);
		Tab.child(12, 17024, 135, 214);
		Tab.child(13, 17025, 135, 240);
		Tab.child(14, 17026, 21, 59);
		Tab.child(15, 17028, 20, 170);
		Tab.child(16, 17030, 20, 213);
		Tab.child(17, 17033, 67, 170);
		Tab.child(18, 17038, 67, 213);
		Tab.child(19, 17039, 67, 213);
		Tab.child(20, 17040, 30, 8);
		Tab.child(21, 17027, 75, 55);
	}
	static int[] req = {1,2,3, 4};
	public static void pouchCreation(TextDrawingArea[] TDA) {
		int totalScrolls = pouchItems.length;
		int xPadding = 53;
		int yPadding = 57;
		int xPos = 13;
		int yPos = 20;
		RSInterface rsinterface = addTabInterface(23471);
		setChildren(7, rsinterface);
		addSprite(23472, 1, "interfaces/summoning/creation/summoning");
		addButton(23475, 0, "interfaces/summoning/creation/tab", "Transform Scolls");
		addSprite(23474, 1, "interfaces/summoning/creation/pouch");
		addSprite(23473, 1, "interfaces/summoning/creation/tab");
		addSprite(23476, 0, "interfaces/summoning/creation/scroll");
		addInAreaHover(23477, "interfaces/summoning/creation/close", 0, 1, 16, 16, "Close", 250, 3);
			//Scroll section
			RSInterface scroll = addTabInterface(23478);
			setChildren(3*totalScrolls, scroll);
			for(int i = 0; i < totalScrolls; i++) {
				addInAreaHover(23479 + (i*8), "interfaces/summoning/creation/box", 0, 1, 48, 52, "nothing", -1, 0);
				addPouch(23480 + (i*8), req, 1, pouchItems[i], summoningLevelRequirements[i], pouchNames[i], TDA, i,5);
				//addSprite(23485 + (i*8), pouchItems[i], null, 200, 200);
				setBounds(23479 + (i*8), 36+((i%8)*xPadding), 0+(i/8)*yPadding, 0+(i*2), scroll);
				setBounds(23480 + (i*8), 43+((i%8)*xPadding), 2+(i/8)*yPadding, 1+(i*2), scroll);
				//setBounds(23485 + (i*8), 43+((i%8)*xPadding), 2+(i/8)*yPadding, 1+(i*2), scroll);
			}
			for(int i = 0; i < totalScrolls; i++) {
				int drawX = 5+((i%8)*xPadding);
				if(drawX > 472-180)
					drawX -= 90;
				int drawY = 55+(i/8)*yPadding;
				if(drawY > 200-40)
					drawY -= 80;
				setBounds(23481 + (i*8), drawX, drawY, 2+((totalScrolls-1)*2)+i, scroll);
			}
			scroll.parentID = 23478;
			scroll.id = 23478;
			scroll.atActionType = 0;
			scroll.contentType = 0;
			scroll.width = 452 + 22;
			scroll.height = 257;
			scroll.scrollMax = 570;
			//
		setBounds(23472, xPos, yPos, 0, rsinterface);
		setBounds(23473, xPos + 9, yPos + 9, 1, rsinterface);
		setBounds(23474, xPos + 29, yPos + 10, 2, rsinterface);
		
		setBounds(23475, xPos + 79, yPos + 9, 3, rsinterface);
		setBounds(23476, xPos + 106, yPos + 10, 4, rsinterface);
		setBounds(23477, xPos + 461, yPos + 10, 5, rsinterface);
		setBounds(23478, 0, yPos + 39, 6, rsinterface);
	}
	
	
	public static void scrollCreation(TextDrawingArea[] TDA) {
		int totalScrolls = pouchItems.length;
		int xPadding = 53;
		int yPadding = 57;
		int xPos = 13;
		int yPos = 20;
		RSInterface rsinterface = addTabInterface(22760);
		setChildren(7, rsinterface);
		addSprite(22761, 0, "interfaces/summoning/creation/summoning");
		addButton(22762, 0, "interfaces/summoning/creation/tab", "Infuse Pouches");
		addSprite(22763, 0, "interfaces/summoning/creation/pouch");
		addSprite(22764, 1, "interfaces/summoning/creation/tab");
		addSprite(22765, 1, "interfaces/summoning/creation/scroll");
		addInAreaHover(22766, "interfaces/summoning/creation/close", 0, 1, 16, 16, "Close", 250, 3);
			//Scroll section
			RSInterface scroll = addTabInterface(22767);
			setChildren(4*totalScrolls, scroll);
			for(int i = 0; i < totalScrolls; i++) {
				addInAreaHover(22768 + (i*9), "interfaces/summoning/creation/box", 0, 1, 48, 52, "nothing", -1, 0);
				addScroll(22769 + (i*9), pouchItems[i], 1, scrollItems[i], summoningLevelRequirements[i], scrollNames[i], TDA, i,5);
				
				addSprite(22776 + (i*9), pouchItems[i], null, 50, 50);
				setBounds(22768 + (i*9), 36+((i%8)*xPadding), 0+(i/8)*yPadding, 0+(i*3), scroll);
				setBounds(22769 + (i*9), 43+((i%8)*xPadding), 2+(i/8)*yPadding, 1+(i*3), scroll);
				setBounds(22776 + (i*9), 28+((i%8)*xPadding), 28+(i/8)*yPadding, 2+(i*3), scroll);
			}
			for(int i = 0; i < totalScrolls; i++) {
				int drawX = 5+((i%8)*xPadding);
				if(drawX > 472-180)
					drawX -= 90;
				int drawY = 55+(i/8)*yPadding;
				if(drawY > 200-40)
					drawY -= 80;
				setBounds(22770 + (i*9), drawX, drawY, 3+((totalScrolls-1)*3)+i, scroll);
			}
			scroll.parentID = 22767;
			scroll.id = 22767;
			scroll.atActionType = 0;
			scroll.contentType = 0;
			scroll.width = 452 + 22;
			scroll.height = 257;
			scroll.scrollMax = 570;
			//
		setBounds(22761, xPos, yPos, 0, rsinterface);
		setBounds(22762, xPos + 9, yPos + 9, 1, rsinterface);
		setBounds(22763, xPos + 29, yPos + 10, 2, rsinterface);
		
		setBounds(22764, xPos + 79, yPos + 9, 3, rsinterface);
		setBounds(22765, xPos + 106, yPos + 10, 4, rsinterface);
		setBounds(22766, xPos + 461, yPos + 10, 5, rsinterface);
		setBounds(22767, 0, yPos + 39, 6, rsinterface);
	}

	public static void addSprite(int id, int spriteId, String spriteName, int zoom1, int zoom2) {
		RSInterface tab = interfaceCache[id] = new RSInterface();
		tab.id = id;
		tab.parentID = id;
		tab.type = 5;
		tab.atActionType = 0;
		tab.contentType = 0;
		tab.opacity = (byte)0;
		tab.hoverType = 52;
		if(spriteName == null) {
			tab.itemSpriteZoom1 = zoom1;
			tab.itemSpriteId1 = spriteId;
			tab.itemSpriteZoom2 = zoom2;
			tab.itemSpriteId2 = spriteId;
		} else {
			tab.sprite1 = imageLoader(spriteId, spriteName);
			tab.sprite2 = imageLoader(spriteId, spriteName);
		}
		tab.width = 512;
		tab.height = 334;
	}

	public static void addScroll(int ID, int r1, int ra1, int r2, int lvl, String name, TextDrawingArea[] TDA, int imageID, int type) {
		RSInterface rsInterface = addTabInterface(ID);
		rsInterface.id = ID;
		rsInterface.parentID = 1151;
		rsInterface.type = 5;
		rsInterface.atActionType = type;
		rsInterface.contentType = 0;
		rsInterface.hoverType = ID + 1;
		rsInterface.width = 32;
		rsInterface.height = 32;
		rsInterface.tooltip = "Transform @or1@" + name;//infuse for pouches
		rsInterface.spellName = name;
		rsInterface.valueCompareType = new int[2];
		rsInterface.requiredValues = new int[2];
		rsInterface.valueCompareType[0] = 3;
		rsInterface.requiredValues[0] = ra1;
		rsInterface.valueCompareType[1] = 3;
		rsInterface.requiredValues[1] = lvl-1;
		rsInterface.valueIndexArray = new int[3][];
		rsInterface.valueIndexArray[0] = new int[4];
		rsInterface.valueIndexArray[0][0] = 4;
		rsInterface.valueIndexArray[0][1] = 3214;
		rsInterface.valueIndexArray[0][2] = r1;
		rsInterface.valueIndexArray[0][3] = 0;
		rsInterface.valueIndexArray[1] = new int[3];
		rsInterface.valueIndexArray[1][0] = 1;
		rsInterface.valueIndexArray[1][1] = 6;
		rsInterface.valueIndexArray[1][2] = 0;
		//rsInterface.sprite1 = null;
		rsInterface.itemSpriteId1 = r2;
		rsInterface.itemSpriteId2 = r2;
		rsInterface.itemSpriteZoom1 = 150;
		rsInterface.itemSpriteZoom2 = 150;
		rsInterface.itemSpriteIndex = imageID;
		rsInterface.greyScale = true;
		RSInterface hover = addTabInterface(ID + 1);//Hover interface ID
		hover.hoverType = -1;
		hover.interfaceShown = true;
		setChildren(5, hover);
		addSprite(ID + 2,0,"Lunar/BOX");
		addText(ID + 3, "Level " + (lvl) + ": " + name, 0xFF981F, true, true, 52, 1);
		addText(ID + 4, "This item requires", 0xAF6A1A, true, true, 52, 0);
		addRuneText(ID + 5, ra1, r1, TDA);
		addSprite(ID + 6, r1, null);
		
		setBounds(ID + 2, 0, 0, 0, hover);
		setBounds(ID + 3, 90, 4, 1, hover);
		setBounds(ID + 4, 90, 19, 2, hover);
		setBounds(ID + 5, 87, 66, 3, hover);
		setBounds(ID + 6, 72, 33, 4, hover);// Rune
	}
	
	public static void addPouch(int ID, int[] r1, int ra1, int r2, int lvl, String name, TextDrawingArea[] TDA, int imageID, int type) {
		RSInterface rsInterface = addTabInterface(ID);
		rsInterface.id = ID;
		rsInterface.parentID = 1151;
		rsInterface.type = 5;
		rsInterface.atActionType = type;
		rsInterface.contentType = 0;
		rsInterface.hoverType = ID + 1;
		rsInterface.width = 32;
		rsInterface.height = 32;
		rsInterface.tooltip = "Infuse @or1@" + name;//infuse for pouches
		rsInterface.spellName = name;
		rsInterface.valueCompareType = new int[2];
		rsInterface.requiredValues = new int[2];
		rsInterface.valueCompareType[0] = 3;
		rsInterface.requiredValues[0] = ra1;
		rsInterface.valueCompareType[1] = 3;
		rsInterface.requiredValues[1] = lvl-1;
		rsInterface.valueIndexArray = new int[2 + r1.length][];
		for(int i = 0; i < r1.length; i++) {
			rsInterface.valueIndexArray[i] = new int[4];
			rsInterface.valueIndexArray[i][0] = 4;
			rsInterface.valueIndexArray[i][1] = 3214;
			rsInterface.valueIndexArray[i][2] = r1[i];
			rsInterface.valueIndexArray[i][3] = 0;
		}
		rsInterface.valueIndexArray[1] = new int[3];
		rsInterface.valueIndexArray[1][0] = 1;
		rsInterface.valueIndexArray[1][1] = 6;
		rsInterface.valueIndexArray[1][2] = 0;
//		rsInterface.sprite1 = null;
		rsInterface.itemSpriteId1 = r2;
		rsInterface.itemSpriteId2 = r2;
		rsInterface.itemSpriteZoom1 = 150;
		rsInterface.itemSpriteZoom2 = 150;
		rsInterface.itemSpriteIndex = imageID;
		rsInterface.greyScale = true;
		RSInterface hover = addTabInterface(ID + 1);//Hover interface ID
		hover.hoverType = -1;
		hover.interfaceShown = true;
		setChildren(8, hover);
		addSprite(ID + 2,0,"Lunar/BOX");
		addText(ID + 3, "Level " + (lvl) + ": " + name, 0xFF981F, true, true, 52, 1);
		addText(ID + 4, "This item requires", 0xAF6A1A, true, true, 52, 0);
		addRuneText(ID + 5, ra1, r1[0], TDA);
		addSprite(ID + 6, r1[0], null);
		addSprite(ID + 7, r1[1], null);
		addSprite(ID + 8, r1[2], null);
		addSprite(ID + 9, r1[3], null);
		
		setBounds(ID + 2, 0, 0, 0, hover);
		setBounds(ID + 3, 90, 4, 1, hover);
		setBounds(ID + 4, 90, 19, 2, hover);
		setBounds(ID + 5, 87, 66, 3, hover);
		
		setBounds(ID + 6, 7, 33, 4, hover);// Rune
		setBounds(ID + 7, 50, 33, 5, hover);// Rune
		setBounds(ID + 8, 96, 33, 6, hover);// Rune
		setBounds(ID + 9, 144, 33, 7, hover);// Rune
	}

	public static void addInAreaHover(int i, String imageName, int sId, int sId2, int w, int h, String text, int contentType, int actionType) {//hoverable button
		RSInterface tab = addTabInterface(i);
		tab.id = i;
		tab.parentID = i;
		tab.type = 5;//Sprite
		tab.atActionType = actionType;
		tab.contentType = contentType;
		tab.opacity = 0;
		tab.hoverType = i;
		if(sId != -1)
			tab.sprite1 = imageLoader(sId, imageName);
		tab.sprite2 = imageLoader(sId2, imageName);
		tab.width = w;
		tab.height = h;
		tab.tooltip = text;
	}


	public static void questTab(TextDrawingArea[] TDA) {
		RSInterface Interface = addTabInterface(638);
		setChildren(7, Interface);
		addText(19155, "Quest Diary", 0xFF981F, false, true, 52, TDA, 2);
		addButton(19156, 1, "Bank/QUEST", 18, 18, "Achievement Diary", 1);
		addSprite(19157, 0, "Bank/QUEST");
		addSprite(19158, 4, "Bank/QUEST");
		addText(19159, "Quest Points:", 0xFF981F, false, true, 52, TDA, 0);
		setBounds(19155, 4, 2, 0, Interface);
		setBounds(19156, 170, 2, 1, Interface);
		setBounds(19157, 0, 24, 2, Interface);
		setBounds(19158, 0, 22, 3, Interface);
		setBounds(19158, 0, 242, 4, Interface);
		setBounds(19159, 4, 246, 5, Interface);
		setBounds(19160, 0, 24, 6, Interface);
		Interface = addTabInterface(19160);
		Interface.scrollMax = 1700;
		Interface.height = 218;
		Interface.width = 174;
		setChildren(105, Interface);
		addText(19161, "Special Event", 0xFF981F, false, true, 52, TDA, 2);
		addText(19162, "None", 0xFF0000, false, true, -1, TDA, 1, 0xFFFFFF);
		addText(19163, "Special Quest", 0xFF981F, false, true, 52, TDA, 2);
		addText(19164, "None", 0xFF0000, false, true, -1, TDA, 1, 0xFFFFFF);
		setBounds(19161, 4, 4, 0, Interface);
		setBounds(19162, 8, 20, 1, Interface);
		setBounds(19163, 4, 35, 2, Interface);
		setBounds(19164, 8, 51, 3, Interface);
		setBounds(663, 4, 67, 4, Interface);
		int Y = 83;
		int frame = 5;
		for (int i = 16026; i <= 16125; i++) {
			String[] questName = {"On my way to Zaros.", "Cook's Assistant"

			};
			addText(i, "QuestID: " + i, 0xFF0000, false, true, -1, TDA, 0,
					0xFFFFFF);
			setBounds(i, 8, Y, frame, Interface);
			frame++;
			Y += 15;
			Y++;
		}
		Interface = addTabInterface(19265);
		try {
			setChildren(6, Interface);
			addText(19266, "Achievements", 0xFF981F, false, true, -1, TDA, 2);
			addButton(19267, 2, "Bank/QUEST", 18, 18, "Quest Diary", 1);
			addSprite(19269, 0, "Bank/QUEST");
			addSprite(19296, 4, "Bank/QUEST");
			setBounds(19266, 4, 2, 0, Interface);
			setBounds(19267, 170, 2, 1, Interface);
			setBounds(19269, 0, 24, 2, Interface);
			setBounds(19296, 0, 22, 3, Interface);
			setBounds(19296, 0, 242, 4, Interface);
			setBounds(19268, 0, 24, 5, Interface);
			Interface = addTabInterface(19268);
			Interface.height = 218;
			Interface.width = 174;
			Interface.scrollMax = 500;
			setChildren(27, Interface);
			addText(19300, "Free Play Tasks", 0xFF981F, false, true, -1, TDA, 2);
			addText(19270, "Lumbridge/Draynor", 0x00FF00, false, true, -1, TDA,
					0);
			addText(19271, "Easy", 0xFF0000, false, true, -1, TDA, 0, 0xFFFFFF);
			addText(19272, "Medium", 0xFF0000, false, true, -1, TDA, 0,
					0xFFFFFF);
			addText(19273, "Hard", 0xFF0000, false, true, -1, TDA, 0, 0xFFFFFF);
			setBounds(19300, 12, 4, 0, Interface);
			setBounds(19270, 12, 20, 1, Interface);
			setBounds(19271, 18, 34, 2, Interface);
			setBounds(19272, 18, 48, 3, Interface);
			setBounds(19273, 18, 62, 4, Interface);
			addText(19274, "Members' Tasks", 0xFF981F, false, true, -1, TDA, 2);
			addText(19275, "Falador", 0x00FF00, false, true, -1, TDA, 0);
			addText(19276, "Easy", 0xFF0000, false, true, -1, TDA, 0, 0xFFFFFF);
			addText(19277, "Medium", 0xFF0000, false, true, -1, TDA, 0,
					0xFFFFFF);
			addText(19278, "Hard", 0xFF0000, false, true, -1, TDA, 0, 0xFFFFFF);
			setBounds(19274, 12, 80, 5, Interface);
			setBounds(19275, 12, 96, 6, Interface);
			setBounds(19276, 18, 110, 7, Interface);
			setBounds(19277, 18, 124, 8, Interface);
			setBounds(19278, 18, 138, 9, Interface);
			addText(19279, "Fremennik", 0x00FF00, false, true, -1, TDA, 0);
			addText(19280, "Easy", 0xFF0000, false, true, -1, TDA, 0, 0xFFFFFF);
			addText(19281, "Medium", 0xFF0000, false, true, -1, TDA, 0,
					0xFFFFFF);
			addText(19282, "Hard", 0xFF0000, false, true, -1, TDA, 0, 0xFFFFFF);
			setBounds(19279, 12, 167, 10, Interface);
			setBounds(19280, 18, 183, 11, Interface);
			setBounds(19281, 18, 197, 12, Interface);
			setBounds(19282, 18, 211, 13, Interface);
			addText(19283, "Karamja", 0x00FF00, false, true, -1, TDA, 0);
			addText(19284, "Easy", 0xFF0000, false, true, -1, TDA, 0, 0xFFFFFF);
			addText(19285, "Medium", 0xFF0000, false, true, -1, TDA, 0,
					0xFFFFFF);
			addText(19286, "Hard", 0xFF0000, false, true, -1, TDA, 0, 0xFFFFFF);
			setBounds(19283, 12, 237, 14, Interface);
			setBounds(19284, 18, 253, 15, Interface);
			setBounds(19285, 18, 267, 16, Interface);
			setBounds(19286, 18, 281, 17, Interface);
			addText(19287, "Seers Village", 0x00FF00, false, true, -1, TDA, 0);
			addText(19288, "Easy", 0xFF0000, false, true, -1, TDA, 0, 0xFFFFFF);
			addText(19289, "Medium", 0xFF0000, false, true, -1, TDA, 0,
					0xFFFFFF);
			addText(19290, "Hard", 0xFF0000, false, true, -1, TDA, 0, 0xFFFFFF);
			setBounds(19287, 12, 306, 18, Interface);
			setBounds(19288, 18, 322, 19, Interface);
			setBounds(19289, 18, 336, 20, Interface);
			setBounds(19290, 18, 350, 21, Interface);
			addText(19291, "Varrock", 0x00FF00, false, true, -1, TDA, 0);
			addText(19292, "Easy", 0xFF0000, false, true, -1, TDA, 0, 0xFFFFFF);
			addText(19293, "Medium", 0xFF0000, false, true, -1, TDA, 0,
					0xFFFFFF);
			addText(19294, "Hard", 0xFF0000, false, true, -1, TDA, 0, 0xFFFFFF);
			setBounds(19291, 12, 375, 22, Interface);
			setBounds(19292, 18, 391, 23, Interface);
			setBounds(19293, 18, 405, 24, Interface);
			setBounds(19294, 18, 419, 25, Interface);
			addText(19295, "More coming soon...", 0xFF0000, false, true, -1,
					TDA, 0, 0xFFFFFF);
			setBounds(19295, 12, 444, 26, Interface);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void addText(int i, String s, int k, boolean l, boolean m,
			int a, TextDrawingArea[] TDA, int j, int dsc) {
		RSInterface rsinterface = addTabInterface(i);
		rsinterface.parentID = i;
		rsinterface.id = i;
		rsinterface.type = 4;
		rsinterface.atActionType = 1;
		rsinterface.width = 174;
		rsinterface.height = 11;
		rsinterface.contentType = 0;
		// rsinterface.aByte254 = 0;
		// rsinterface.hoverType = a;
		rsinterface.centerText = l;
		rsinterface.textShadow = m;
		rsinterface.textDrawingAreas = TDA[j];
		rsinterface.message = s;
		rsinterface.aString228 = "";
		rsinterface.anInt219 = 0;
		rsinterface.textColor = k;
		rsinterface.anInt216 = dsc;
		rsinterface.tooltip = s;
	}

	/*public static void addHovered(int i, int j, String imageName, int w, int h,
			int IMAGEID) {
		RSInterface hover = addTabInterface(i);
		hover.parentID = i;
		hover.id = i;
		hover.type = 0;
		hover.atActionType = 0;
		hover.width = w;
		hover.height = h;
		hover.interfaceShown = true;
		hover.hoverType = -1;
		addSprite(IMAGEID, j, imageName);
		setChildren(1, hover);
		setBounds(IMAGEID, 0, 0, 0, hover);
	}*/

	public static void addHovered(int i, int j, String imageName, int w, int h, int IMAGEID) {
		addHoveredButton(i,imageName,j,w,h,IMAGEID);
	}
	
	public static void addHover(int i, int aT, int contentType, int hoverOver,int sId, String imageName, int width, int height, String text){
		addHoverButton(i,imageName, sId,width,height,text,contentType,hoverOver,aT);
	}

	/*public static void addHover(int i, int aT, int cT, int hoverid, int sId,
			String NAME, int W, int H, String tip) {
		RSInterface hover = addTabInterface(i);
		hover.id = i;
		hover.parentID = i;
		hover.type = 5;
		hover.atActionType = aT;
		hover.contentType = cT;
		// hover.hoverType = hoverid;
		hover.sprite1 = imageLoader(sId, NAME);
		hover.sprite2 = imageLoader(sId, NAME);
		hover.width = W;
		hover.height = H;
		hover.tooltip = tip;
	}*/

	public static void addPrayer(int i, int configId, int configFrame,
			int requiredValues, int prayerSpriteID, String PrayerName, int Hover) {
		RSInterface Interface = addTabInterface(i);
		Interface.id = i;
		Interface.parentID = 22500;
		Interface.type = 5;
		Interface.atActionType = 4;
		Interface.contentType = 0;
		Interface.opacity = 0;
		Interface.hoverType = Hover;
		Interface.sprite1 = imageLoader(0, "Interfaces/CurseTab/GLOW");
		Interface.sprite2 = imageLoader(1, "Interfaces/CurseTab/GLOW");
		Interface.width = 34;
		Interface.height = 34;
		Interface.valueCompareType = new int[1];
		Interface.requiredValues = new int[1];
		Interface.valueCompareType[0] = 1;
		Interface.requiredValues[0] = configId;
		Interface.valueIndexArray = new int[1][3];
		Interface.valueIndexArray[0][0] = 5;
		Interface.valueIndexArray[0][1] = configFrame;
		Interface.valueIndexArray[0][2] = 0;
		Interface.tooltip = "Activate@lre@ " + PrayerName;
		Interface = addTabInterface(i + 1);
		Interface.id = i + 1;
		Interface.parentID = 22500;
		Interface.type = 5;
		Interface.atActionType = 0;
		Interface.contentType = 0;
		Interface.opacity = 0;
		Interface.sprite1 = imageLoader(prayerSpriteID,
				"Interfaces/CurseTab/PRAYON");
		Interface.sprite2 = imageLoader(prayerSpriteID,
				"Interfaces/CurseTab/PRAYOFF");
		Interface.width = 34;
		Interface.height = 34;
		Interface.valueCompareType = new int[1];
		Interface.requiredValues = new int[1];
		Interface.valueCompareType[0] = 2;
		Interface.requiredValues[0] = requiredValues + 1;
		Interface.valueIndexArray = new int[1][3];
		Interface.valueIndexArray[0][0] = 2;
		Interface.valueIndexArray[0][1] = 5;
		Interface.valueIndexArray[0][2] = 0;
	}

	public static void curseTab(TextDrawingArea[] TDA) {
		RSInterface Interface = addTabInterface(22500);
		int index = 0;
		addText(687, "99/99", 0xFF981F, false, false, -1, TDA, 1);
		addSprite(22502, 0, "Interfaces/CurseTab/ICON");
		addPrayer(22503, 0, 83, 49, 7, "Protect Item", 22582);
		addPrayer(22505, 0, 84, 49, 4, "Sap Warrior", 22544);
		addPrayer(22507, 0, 85, 51, 5, "Sap Ranger", 22546);
		addPrayer(22509, 0, 101, 53, 3, "Sap Mage", 22548);
		addPrayer(22511, 0, 102, 55, 2, "Sap Spirit", 22550);
		addPrayer(22513, 0, 86, 58, 18, "Berserker", 22552);
		addPrayer(22515, 0, 87, 61, 15, "Deflect Summoning", 22554);
		addPrayer(22517, 0, 88, 64, 17, "Deflect Magic", 22556);
		addPrayer(22519, 0, 89, 67, 16, "Deflect Missiles", 22558);
		addPrayer(22521, 0, 90, 70, 6, "Deflect Melee", 22560);
		addPrayer(22523, 0, 91, 73, 9, "Leech Attack", 22562);
		addPrayer(22525, 0, 103, 75, 10, "Leech Ranged", 22564);
		addPrayer(22527, 0, 104, 77, 11, "Leech Magic", 22566);
		addPrayer(22529, 0, 92, 79, 12, "Leech Defence", 22568);
		addPrayer(22531, 0, 93, 81, 13, "Leech Strength", 22570);
		addPrayer(22533, 0, 94, 83, 14, "Leech Energy", 22572);
		addPrayer(22535, 0, 95, 85, 19, "Leech Special Attack", 22574);
		addPrayer(22537, 0, 96, 88, 1, "Wrath", 22576);
		addPrayer(22539, 0, 97, 91, 8, "Soul Split", 22578);
		addPrayer(22541, 0, 105, 94, 20, "Turmoil", 22580);
		drawTooltip(22582,
				"Level 50\nProtect Item\nKeep 1 extra item if you die");
		drawTooltip(
				22544,
				"Level 50\nSap Warrior\nDrains 10% of enemy Attack,\nStrength and Defence,\nincreasing to 20% over time.");
		drawTooltip(
				22546,
				"Level 52\nSap Ranger\nDrains 10% of enemy Ranged\nand Defence, increasing to 20%\nover time.");
		drawTooltip(
				22548,
				"Level 54\nSap Mage\nDrains 10% of enemy Magic\nand Defence, increasing to 20%\nover time.");
		drawTooltip(22550,
				"Level 56\nSap Spirit\nDrains enenmy special attack\nenergy.");
		drawTooltip(22552,
				"Level 59\nBerserker\nBoosted stats last 15% longer.");
		drawTooltip(
				22554,
				"Level 62\nDeflect Summoning\nReduces damage dealt from\nSummoning scrolls, prevents the\nuse of a familiar's special\nattack, and can deflect some of\ndamage back to the attacker.");
		drawTooltip(
				22556,
				"Level 65\nDeflect Magic\nProtects against magical attacks\nand can deflect some of the\ndamage back to the attacker.");
		drawTooltip(
				22558,
				"Level 68\nDeflect Missiles\nProtects against ranged attacks\nand can deflect some of the\ndamage back to the attacker.");
		drawTooltip(
				22560,
				"Level 71\nDeflect Melee\nProtects against melee attacks\nand can deflect some of the\ndamage back to the attacker.");
		drawTooltip(
				22562,
				"Level 74\nLeech Attack\nBoosts Attack by 5%, increasing\nto 10% over time, while draining\nenemy Attack by 10%, increasing\nto 25% over time.");
		drawTooltip(
				22564,
				"Level 76\nLeech Ranged\nBoosts Ranged by 5%, increasing\nto 10% over time, while draining\nenemy Ranged by 10%,\nincreasing to 25% over\ntime.");
		drawTooltip(
				22566,
				"Level 78\nLeech Magic\nBoosts Magic by 5%, increasing\nto 10% over time, while draining\nenemy Magic by 10%, increasing\nto 25% over time.");
		drawTooltip(
				22568,
				"Level 80\nLeech Defence\nBoosts Defence by 5%, increasing\nto 10% over time, while draining\n enemy Defence by10%,\nincreasing to 25% over\ntime.");
		drawTooltip(
				22570,
				"Level 82\nLeech Strength\nBoosts Strength by 5%, increasing\nto 10% over time, while draining\nenemy Strength by 10%, increasing\n to 25% over time.");
		drawTooltip(22572,
				"Level 84\nLeech Energy\nDrains enemy run energy, while\nincreasing your own.");
		drawTooltip(
				22574,
				"Level 86\nLeech Special Attack\nDrains enemy special attack\nenergy, while increasing your\nown.");
		drawTooltip(22576,
				"Level 89\nWrath\nInflicts damage to nearby\ntargets if you die.");
		drawTooltip(
				22578,
				"Level 92\nSoul Split\n1/4 of damage dealt is also removed\nfrom opponent's Prayer and\nadded to your Hitpoints.");
		drawTooltip(
				22580,
				"Level 95\nTurmoil\nIncreases Attack and Defence\nby 15%, plus 15% of enemy's\nlevel, and Strength by 23% plus\n10% of enemy's level.");
		setChildren(62, Interface);

		setBounds(687, 85, 241, index, Interface);
		index++;
		setBounds(22502, 65, 241, index, Interface);
		index++;
		setBounds(22503, 2, 5, index, Interface);
		index++;
		setBounds(22504, 8, 8, index, Interface);
		index++;
		setBounds(22505, 40, 5, index, Interface);
		index++;
		setBounds(22506, 47, 12, index, Interface);
		index++;
		setBounds(22507, 76, 5, index, Interface);
		index++;
		setBounds(22508, 82, 11, index, Interface);
		index++;
		setBounds(22509, 113, 5, index, Interface);
		index++;
		setBounds(22510, 116, 8, index, Interface);
		index++;
		setBounds(22511, 150, 5, index, Interface);
		index++;
		setBounds(22512, 155, 10, index, Interface);
		index++;
		setBounds(22513, 2, 45, index, Interface);
		index++;
		setBounds(22514, 9, 48, index, Interface);
		index++;
		setBounds(22515, 39, 45, index, Interface);
		index++;
		setBounds(22516, 42, 47, index, Interface);
		index++;
		setBounds(22517, 76, 45, index, Interface);
		index++;
		setBounds(22518, 79, 48, index, Interface);
		index++;
		setBounds(22519, 113, 45, index, Interface);
		index++;
		setBounds(22520, 116, 48, index, Interface);
		index++;
		setBounds(22521, 151, 45, index, Interface);
		index++;
		setBounds(22522, 154, 48, index, Interface);
		index++;
		setBounds(22523, 2, 82, index, Interface);
		index++;
		setBounds(22524, 6, 86, index, Interface);
		index++;
		setBounds(22525, 40, 82, index, Interface);
		index++;
		setBounds(22526, 42, 86, index, Interface);
		index++;
		setBounds(22527, 77, 82, index, Interface);
		index++;
		setBounds(22528, 79, 86, index, Interface);
		index++;
		setBounds(22529, 114, 83, index, Interface);
		index++;
		setBounds(22530, 119, 87, index, Interface);
		index++;
		setBounds(22531, 153, 83, index, Interface);
		index++;
		setBounds(22532, 156, 86, index, Interface);
		index++;
		setBounds(22533, 2, 120, index, Interface);
		index++;
		setBounds(22534, 7, 125, index, Interface);
		index++;
		setBounds(22535, 40, 120, index, Interface);
		index++;
		setBounds(22536, 45, 124, index, Interface);
		index++;
		setBounds(22537, 78, 120, index, Interface);
		index++;
		setBounds(22538, 86, 124, index, Interface);
		index++;
		setBounds(22539, 114, 120, index, Interface);
		index++;
		setBounds(22540, 120, 125, index, Interface);
		index++;
		setBounds(22541, 151, 120, index, Interface);
		index++;
		setBounds(22542, 153, 127, index, Interface);
		index++;
		setBounds(22582, 10, 40, index, Interface);
		index++;
		setBounds(22544, 20, 40, index, Interface);
		index++;
		setBounds(22546, 20, 40, index, Interface);
		index++;
		setBounds(22548, 20, 40, index, Interface);
		index++;
		setBounds(22550, 20, 40, index, Interface);
		index++;
		setBounds(22552, 10, 80, index, Interface);
		index++;
		setBounds(22554, 10, 80, index, Interface);
		index++;
		setBounds(22556, 10, 80, index, Interface);
		index++;
		setBounds(22558, 10, 80, index, Interface);
		index++;
		setBounds(22560, 10, 80, index, Interface);
		index++;
		setBounds(22562, 10, 120, index, Interface);
		index++;
		setBounds(22564, 10, 120, index, Interface);
		index++;
		setBounds(22566, 10, 120, index, Interface);
		index++;
		setBounds(22568, 5, 120, index, Interface);
		index++;
		setBounds(22570, 5, 120, index, Interface);
		index++;
		setBounds(22572, 10, 160, index, Interface);
		index++;
		setBounds(22574, 10, 160, index, Interface);
		index++;
		setBounds(22576, 10, 160, index, Interface);
		index++;
		setBounds(22578, 10, 160, index, Interface);
		index++;
		setBounds(22580, 10, 160, index, Interface);
		index++;
	}

	public static void emoteTab() {
		RSInterface tab = addTabInterface(147);
		RSInterface scroll = addTabInterface(148);
		tab.totalChildren(1);
		tab.child(0, 148, 0, 1);
		addButton(168, 0, "/Emotes/EMOTE", "Yes", 41, 47);
		addButton(169, 1, "/Emotes/EMOTE", "No", 41, 47);
		addButton(164, 2, "/Emotes/EMOTE", "Bow", 41, 47);
		addButton(165, 3, "/Emotes/EMOTE", "Angry", 41, 47);
		addButton(162, 4, "/Emotes/EMOTE", "Think", 41, 47);
		addButton(163, 5, "/Emotes/EMOTE", "Wave", 41, 47);
		addButton(13370, 6, "/Emotes/EMOTE", "Shrug", 41, 47);
		addButton(171, 7, "/Emotes/EMOTE", "Cheer", 41, 47);
		addButton(167, 8, "/Emotes/EMOTE", "Beckon", 41, 47);
		addButton(170, 9, "/Emotes/EMOTE", "Laugh", 41, 47);
		addButton(13366, 10, "/Emotes/EMOTE", "Jump for Joy", 41, 47);
		addButton(13368, 11, "/Emotes/EMOTE", "Yawn", 41, 47);
		addButton(166, 12, "/Emotes/EMOTE", "Dance", 41, 47);
		addButton(13363, 13, "/Emotes/EMOTE", "Jig", 41, 47);
		addButton(13364, 14, "/Emotes/EMOTE", "Spin", 41, 47);
		addButton(13365, 15, "/Emotes/EMOTE", "Headbang", 41, 47);
		addButton(161, 16, "/Emotes/EMOTE", "Cry", 41, 47);
		addButton(11100, 17, "/Emotes/EMOTE", "Blow kiss", 41, 47);
		addButton(13362, 18, "/Emotes/EMOTE", "Panic", 41, 47);
		addButton(13367, 19, "/Emotes/EMOTE", "Raspberry", 41, 47);
		addButton(172, 20, "/Emotes/EMOTE", "Clap", 41, 47);
		addButton(13369, 21, "/Emotes/EMOTE", "Salute", 41, 47);
		addButton(13383, 22, "/Emotes/EMOTE", "Goblin Bow", 41, 47);
		addButton(13384, 23, "/Emotes/EMOTE", "Goblin Salute", 41, 47);
		addButton(667, 24, "/Emotes/EMOTE", "Glass Box", 41, 47);
		addButton(6503, 25, "/Emotes/EMOTE", "Climb Rope", 41, 47);
		addButton(6506, 26, "/Emotes/EMOTE", "Lean On Air", 41, 47);
		addButton(666, 27, "/Emotes/EMOTE", "Glass Wall", 41, 47);
		addButton(18464, 28, "/Emotes/EMOTE", "Zombie Walk", 41, 47);
		addButton(18465, 29, "/Emotes/EMOTE", "Zombie Dance", 41, 47);
		addButton(15166, 30, "/Emotes/EMOTE", "Scared", 41, 47);
		addButton(18686, 31, "/Emotes/EMOTE", "Rabbit Hop", 41, 47);
		addButton(154, 32, "/Emotes/EMOTE", "Skillcape Emote", 41, 47);
		scroll.totalChildren(33);
		scroll.child(0, 168, 10, 7);
		scroll.child(1, 169, 54, 7);
		scroll.child(2, 164, 98, 14);
		scroll.child(3, 165, 137, 7);
		scroll.child(4, 162, 9, 56);
		scroll.child(5, 163, 48, 56);
		scroll.child(6, 13370, 95, 56);
		scroll.child(7, 171, 137, 56);
		scroll.child(8, 167, 7, 105);
		scroll.child(9, 170, 51, 105);
		scroll.child(10, 13366, 95, 104);
		scroll.child(11, 13368, 139, 105);
		scroll.child(12, 166, 6, 154);
		scroll.child(13, 13363, 50, 154);
		scroll.child(14, 13364, 90, 154);
		scroll.child(15, 13365, 135, 154);
		scroll.child(16, 161, 8, 204);
		scroll.child(17, 11100, 51, 203);
		scroll.child(18, 13362, 99, 204);
		scroll.child(19, 13367, 137, 203);
		scroll.child(20, 172, 10, 253);
		scroll.child(21, 13369, 53, 253);
		scroll.child(22, 13383, 88, 258);
		scroll.child(23, 13384, 138, 252);
		scroll.child(24, 667, 2, 303);
		scroll.child(25, 6503, 49, 302);
		scroll.child(26, 6506, 93, 302);
		scroll.child(27, 666, 137, 302);
		scroll.child(28, 18464, 9, 352);
		scroll.child(29, 18465, 50, 352);
		scroll.child(30, 15166, 94, 356);
		scroll.child(31, 18686, 141, 353);
		scroll.child(32, 154, 5, 401);
		scroll.width = 173;
		scroll.height = 258;
		scroll.scrollMax = 460;
	}

	public static void optionTab(TextDrawingArea[] tda) {
		RSInterface tab = addTabInterface(904);
		RSInterface energy = interfaceCache[149];
		energy.textColor = 0xff9933;
		addSprite(905, 9, "/Options/SPRITE");
		addSprite(907, 18, "/Options/SPRITE");
		addSprite(909, 29, "/Options/SPRITE");
		addSprite(951, 32, "/Options/SPRITE");
		addSprite(953, 33, "/Options/SPRITE");
		addSprite(955, 34, "/Options/SPRITE");
		addSprite(947, 36, "/Options/SPRITE");
		addSprite(949, 35, "/Options/SPRITE");
		// run button here
		addButton(950, 31, "/Options/OPTION", "Graphic Options");
		addConfigButton(152, 904, 30, 31, "/Options/SPRITE", 40, 40,
				"Toggle-run", 1, 5, 173);
		addConfigButton(906, 904, 10, 14, "/Options/SPRITE", 32, 16, "Dark", 1,
				5, 166);
		addConfigButton(908, 904, 11, 15, "/Options/SPRITE", 32, 16, "Normal",
				2, 5, 166);
		addConfigButton(910, 904, 12, 16, "/Options/SPRITE", 32, 16, "Bright",
				3, 5, 166);
		addConfigButton(912, 904, 13, 17, "/Options/SPRITE", 32, 16,
				"Very Bright", 4, 5, 166);
		addConfigButton(930, 904, 19, 24, "/Options/SPRITE", 26, 16,
				"Music Off", 4, 5, 168);
		addConfigButton(931, 904, 20, 25, "/Options/SPRITE", 26, 16,
				"Music Level-1", 3, 5, 168);
		addConfigButton(932, 904, 21, 26, "/Options/SPRITE", 26, 16,
				"Music Level-2", 2, 5, 168);
		addConfigButton(933, 904, 22, 27, "/Options/SPRITE", 26, 16,
				"Music Level-3", 1, 5, 168);
		addConfigButton(934, 904, 23, 28, "/Options/SPRITE", 24, 16,
				"Music Level-4", 0, 5, 168);
		addConfigButton(941, 904, 19, 24, "/Options/SPRITE", 26, 16,
				"Sound Effects Off", 4, 5, 169);
		addConfigButton(942, 904, 20, 25, "/Options/SPRITE", 26, 16,
				"Sound Effects Level-1", 3, 5, 169);
		addConfigButton(943, 904, 21, 26, "/Options/SPRITE", 26, 16,
				"Sound Effects Level-2", 2, 5, 169);
		addConfigButton(944, 904, 22, 27, "/Options/SPRITE", 26, 16,
				"Sound Effects Level-3", 1, 5, 169);
		addConfigButton(945, 904, 23, 28, "/Options/SPRITE", 24, 16,
				"Sound Effects Level-4", 0, 5, 169);
		addConfigButton(913, 904, 30, 31, "/Options/SPRITE", 40, 40,
				"Toggle-Mouse Buttons", 0, 5, 170);
		addConfigButton(915, 904, 30, 31, "/Options/SPRITE", 40, 40,
				"Toggle-Chat Effects", 0, 5, 171);
		addConfigButton(957, 904, 30, 31, "/Options/SPRITE", 40, 40,
				"Toggle-Split Private Chat", 1, 5, 287);
		addConfigButton(12464, 904, 30, 31, "/Options/SPRITE", 40, 40,
				"Toggle-Accept Aid", 0, 5, 427);
		tab.totalChildren(29);
		int x = 0;
		int y = 2;
		tab.child(0, 905, 13 + x, 10 + y);
		tab.child(1, 906, 48 + x, 18 + y);
		tab.child(2, 908, 80 + x, 18 + y);
		tab.child(3, 910, 112 + x, 18 + y);
		tab.child(4, 912, 144 + x, 18 + y);
		tab.child(5, 907, 14 + x, 55 + y);
		tab.child(6, 930, 49 + x, 61 + y);
		tab.child(7, 931, 75 + x, 61 + y);
		tab.child(8, 932, 101 + x, 61 + y);
		tab.child(9, 933, 127 + x, 61 + y);
		tab.child(10, 934, 151 + x, 61 + y);
		tab.child(11, 909, 13 + x, 99 + y);
		tab.child(12, 941, 49 + x, 104 + y);
		tab.child(13, 942, 75 + x, 104 + y);
		tab.child(14, 943, 101 + x, 104 + y);
		tab.child(15, 944, 127 + x, 104 + y);
		tab.child(16, 945, 151 + x, 104 + y);
		tab.child(17, 913, 15, 153);
		tab.child(18, 955, 19, 159);
		tab.child(19, 915, 75, 153);
		tab.child(20, 953, 79, 160);
		tab.child(21, 957, 135, 153);
		tab.child(22, 951, 139, 159);
		tab.child(23, 12464, 15, 208);
		tab.child(24, 949, 20, 213);
		tab.child(25, 152, 75, 208);
		tab.child(26, 947, 87, 212);
		tab.child(27, 149, 80, 231);
		tab.child(28, 950, 135, 208);
	}

	public static void editClan(TextDrawingArea[] tda) {
		RSInterface tab = addTabInterface(40172);
		addSprite(47251, 1, "/Clan Chat/CLAN");
		addHoverButton(47252, "/Clan Chat/BUTTON", 1, 150, 35, "Set name",
				22222, 47253, 1);
		addHoveredButton(47253, "/Clan Chat/BUTTON", 2, 150, 35, 47254);
		addHoverButton(47255, "/Clan Chat/BUTTON", 1, 150, 35, "Anyone", -1,
				47256, 1);
		addHoveredButton(47256, "/Clan Chat/BUTTON", 2, 150, 35, 47257);

		addHoverButton(48000, "b", 1, 150, 35, "Only me", -1, 47999, 1);
		addHoverButton(48001, "b", 1, 150, 35, "General+", -1, 47999, 1);
		addHoverButton(48002, "b", 1, 150, 35, "Captain+", -1, 47999, 1);
		addHoverButton(48003, "b", 1, 150, 35, "Lieutenant+", -1, 47999, 1);
		addHoverButton(48004, "b", 1, 150, 35, "Sergeant+", -1, 47999, 1);
		addHoverButton(48005, "b", 1, 150, 35, "Corporal+", -1, 47999, 1);
		addHoverButton(48006, "b", 1, 150, 35, "Recruit+", -1, 47999, 1);
		addHoverButton(48007, "b", 1, 150, 35, "Any friends", -1, 47999, 1);

		addHoverButton(47258, "/Clan Chat/BUTTON", 1, 150, 35, "Anyone", -1,
				47259, 1);
		addHoveredButton(47259, "/Clan Chat/BUTTON", 2, 150, 35, 17260);

		addHoverButton(48010, "b", 1, 150, 35, "Only me", -1, 47999, 1);
		addHoverButton(48011, "b", 1, 150, 35, "General+", -1, 47999, 1);
		addHoverButton(48012, "b", 1, 150, 35, "Captain+", -1, 47999, 1);
		addHoverButton(48013, "b", 1, 150, 35, "Lieutenant+", -1, 47999, 1);
		addHoverButton(48014, "b", 1, 150, 35, "Sergeant+", -1, 47999, 1);
		addHoverButton(48015, "b", 1, 150, 35, "Corporal+", -1, 47999, 1);
		addHoverButton(48016, "b", 1, 150, 35, "Recruit+", -1, 47999, 1);
		addHoverButton(48017, "b", 1, 150, 35, "Any friends", -1, 47999, 1);

		addHoverButton(47261, "/Clan Chat/BUTTON", 1, 150, 35, "Only me", -1,
				47262, 1);
		addHoveredButton(47262, "/Clan Chat/BUTTON", 2, 150, 35, 47263);

		// addHoverButton(48020, "b", 1, 150, 35, "Only me", -1, 47999, 1);
		addHoverButton(48021, "b", 1, 150, 35, "General+", -1, 47999, 1);
		addHoverButton(48022, "b", 1, 150, 35, "Captain+", -1, 47999, 1);
		addHoverButton(48023, "b", 1, 150, 35, "Lieutenant+", -1, 47999, 1);
		addHoverButton(48024, "b", 1, 150, 35, "Sergeant+", -1, 47999, 1);
		addHoverButton(48025, "b", 1, 150, 35, "Corporal+", -1, 47999, 1);
		addHoverButton(48026, "b", 1, 150, 35, "Recruit+", -1, 47999, 1);

		addHoverButton(47267, "/Clan Chat/CLOSE", 3, 16, 16, "Close", -1,
				47268, 1);
		addHoveredButton(47268, "/Clan Chat/CLOSE", 4, 16, 16, 47269);

		addText(47800, "Clan name:", tda, 0, 0xff981f, false, true);
		addText(47801, "Who can enter chat?", tda, 0, 0xff981f, false, true);
		addText(47812, "Who can talk on chat?", tda, 0, 0xff981f, false, true);
		addText(47813, "Who can kick on chat?", tda, 0, 0xff981f, false, true);
		addText(47814, "Alex", tda, 0, 0xffffff, true, true);
		addText(47815, "Anyone", tda, 0, 0xffffff, true, true);
		addText(47816, "Anyone", tda, 0, 0xffffff, true, true);
		addText(47817, "Only me", tda, 0, 0xffffff, true, true);
		tab.totalChildren(42);
		tab.child(0, 47251, 15, 15);
		tab.child(1, 47252, 25, 47 + 20);
		tab.child(2, 47253, 25, 47 + 20);
		tab.child(3, 47267, 476, 23);
		tab.child(4, 47268, 476, 23);
		tab.child(5, 48000, 25, 87 + 25);
		tab.child(6, 48001, 25, 87 + 25);
		tab.child(7, 48002, 25, 87 + 25);
		tab.child(8, 48003, 25, 87 + 25);
		tab.child(9, 48004, 25, 87 + 25);
		tab.child(10, 48005, 25, 87 + 25);
		tab.child(11, 48006, 25, 87 + 25);
		tab.child(12, 48007, 25, 87 + 25);
		tab.child(13, 47255, 25, 87 + 25);
		tab.child(14, 47256, 25, 87 + 25);
		tab.child(15, 48010, 25, 128 + 30);
		tab.child(16, 48011, 25, 128 + 30);
		tab.child(17, 48012, 25, 128 + 30);
		tab.child(18, 48013, 25, 128 + 30);
		tab.child(19, 48014, 25, 128 + 30);
		tab.child(20, 48015, 25, 128 + 30);
		tab.child(21, 48016, 25, 128 + 30);
		tab.child(22, 48017, 25, 128 + 30);
		tab.child(23, 47258, 25, 128 + 30);
		tab.child(24, 47259, 25, 128 + 30);
		// tab.child(25, 48020, 25, 168+35);
		tab.child(25, 48021, 25, 168 + 35);
		tab.child(26, 48022, 25, 168 + 35);
		tab.child(27, 48023, 25, 168 + 35);
		tab.child(28, 48024, 25, 168 + 35);
		tab.child(29, 48025, 25, 168 + 35);
		tab.child(30, 48026, 25, 168 + 35);
		tab.child(31, 47261, 25, 168 + 35);
		tab.child(32, 47262, 25, 168 + 35);
		tab.child(33, 47800, 73, 54 + 20);
		tab.child(34, 47801, 53, 95 + 25);
		tab.child(35, 47812, 53, 136 + 30);
		tab.child(36, 47813, 53, 177 + 35);
		tab.child(37, 47814, 100, 54 + 20 + 12);
		tab.child(38, 47815, 100, 95 + 25 + 12);
		tab.child(39, 47816, 100, 136 + 30 + 12);
		tab.child(40, 47817, 100, 177 + 35 + 12);
		tab.child(41, 44000, 0, 94);

		tab = addTabInterface(44000);
		tab.width = 474;
		tab.height = 213;
		tab.scrollMax = 2030;
		for (int i = 44001; i <= 44200; i++) {
			addText(i, "", tda, 1, 0xffff64, false, true, true);
		}
		for (int i = 44801; i <= 45000; i++) {
			// addText(i, "Not in clan", tda, 1, 0xffffff, false, true);
			addHoverText(i, "Not ranked", "[CC]", tda, 1, 0xffffff, false,
					false, 150);
		}
		tab.totalChildren(400);
		int Child = 0;
		int Y = 3;
		for (int i = 44001; i <= 44200; i++) {
			tab.child(Child, i, 204, Y);
			Child++;
			Y += 13;
		}
		Y = 3;
		for (int i = 44801; i <= 45000; i++) {
			tab.child(Child, i, 343, Y);
			Child++;
			Y += 13;
		}
	}

	public static void addSprite(int id, int spriteId1, int spriteId2,
			String spriteName) {
		RSInterface tab = interfaceCache[id] = new RSInterface();
		tab.id = id;
		tab.parentID = id;
		tab.type = 5;
		tab.atActionType = 0;
		tab.contentType = 0;
		tab.opacity = (byte) 0;
		tab.hoverType = 52;
		tab.sprite1 = imageLoader(spriteId1, spriteName);
		tab.sprite2 = imageLoader(spriteId2, spriteName);
		for (int i = 0; i < 10; i++) {
			tab.savedSprite[i] = imageLoader(i, spriteName);
		}
		tab.width = 512;
		tab.height = 334;
	}

	public static void clanChatTab(TextDrawingArea[] tda) {
		RSInterface tab = addTabInterface(18128);
		addHoverButton(18129, "/Clan Chat/SPRITE", 6, 72, 32,
				"Join/Leave a Clan", 550, 18130, 1);
		addHoveredButton(18130, "/Clan Chat/SPRITE", 7, 72, 32, 18131);
		addHoverButton(18132, "/Clan Chat/SPRITE", 6, 72, 32,
				"Open Clan Setup", -1, 18133, 5);
		addHoveredButton(18133, "/Clan Chat/SPRITE", 7, 72, 32, 18134);
		addButton(18250, 0, "/Clan Chat/Lootshare", "Toggle lootshare");
		addText(18135, "Join Chat", tda, 0, 0xff9b00, true, true);
		addText(18136, "Clan Setup", tda, 0, 0xff9b00, true, true);
		addSprite(18137, 37, "/Clan Chat/SPRITE");
		addText(18138, "Clan Chat", tda, 1, 0xff9b00, true, true);
		addText(18139, "Talking in: Not in chat", tda, 0, 0xff9b00, false, true);
		addText(18140, "Owner: None", tda, 0, 0xff9b00, false, true);
		addHoverButton(18343, "/Clan Chat/kick", 0, 23, 23, "Kick", 677, 18344,
				1);
		addHoveredButton(18344, "/Clan Chat/kick", 1, 23, 23, 18345);
		tab.totalChildren(14);
		tab.child(0, 18137, 0, 62);
		tab.child(1, 18143, 0, 62);
		tab.child(2, 18129, 15, 226);
		tab.child(3, 18130, 15, 226);
		tab.child(4, 18132, 103, 226);
		tab.child(5, 18133, 103, 226);
		tab.child(6, 18135, 51, 237);
		tab.child(7, 18136, 139, 237);
		tab.child(8, 18138, 95, 1);
		tab.child(9, 18139, 10, 23);
		tab.child(10, 18140, 25, 38);
		tab.child(11, 18250, 145, 5);
		tab.child(12, 18343, 147, 35);
		tab.child(13, 18344, 147, 35);
		/* Text area */
		RSInterface list = addTabInterface(18143);
		list.totalChildren(200);

		for (int i = 38144; i <= 38244; i++) {
			addSprite(i, 0, 0, "/Clan Chat/CC");
		}
		for (int i = 18144; i <= 18244; i++) {
			addText(i, "", tda, 0, 0xffffff, false, true);
		}
		for (int id = 38144, i = 100; id <= 38243 && i <= 199; id++, i++) {
			list.children[i] = id;
			list.childX[i] = 5;
			for (int id2 = 38144, i2 = 101; id2 <= 38243 && i2 <= 199; id2++, i2++) {
				list.childY[0] = 2;
				list.childY[i2] = list.childY[i2 - 1] + 14;
			}
		}

		for (int id = 18144, i = 0; id <= 18243 && i <= 99; id++, i++) {
			list.children[i] = id;
			list.childX[i] = 12;
			for (int id2 = 18144, i2 = 1; id2 <= 18243 && i2 <= 99; id2++, i2++) {
				list.childY[0] = 2;
				list.childY[i2] = list.childY[i2 - 1] + 14;
			}
		}
		list.height = 158;
		list.width = 174;
		list.scrollMax = 1405;
	}

	public static void addHoverText(int id, String text, String tooltip,
			TextDrawingArea tda[], int idx, int color, boolean center,
			boolean textShadowed, int width) {
		RSInterface rsinterface = addInterface(id);
		rsinterface.id = id;
		rsinterface.parentID = id;
		rsinterface.type = 4;
		rsinterface.atActionType = 1;
		rsinterface.width = width;
		rsinterface.height = 11;
		rsinterface.contentType = 0;
		rsinterface.opacity = 0;
		rsinterface.hoverType = -1;
		rsinterface.centerText = center;
		rsinterface.textShadow = textShadowed;
		rsinterface.textDrawingAreas = tda[idx];
		rsinterface.message = text;
		rsinterface.aString228 = "";
		rsinterface.textColor = color;
		rsinterface.anInt219 = 0;
		rsinterface.anInt216 = 0xffffff;
		rsinterface.anInt239 = 0;
		rsinterface.tooltip = tooltip;
	}

	public static void addText(int id, String text, TextDrawingArea tda[],
			int idx, int color, boolean center, boolean shadow, boolean cc) {
		RSInterface tab = addTabInterface(id);
		tab.parentID = id;
		tab.id = id;
		tab.type = 4;
		tab.atActionType = 0;
		tab.width = 0;
		tab.height = 11;
		tab.contentType = 0;
		tab.opacity = 0;
		tab.hoverType = -1;
		tab.centerText = center;
		tab.textShadow = shadow;
		tab.textDrawingAreas = tda[idx];
		tab.message = text;
		tab.aString228 = "";
		tab.textColor = color;
		tab.anInt219 = 0;
		tab.anInt216 = 0;
		tab.anInt239 = 0;
	}

	public static void addText(int i, String s, int k, boolean l, boolean m,
			int a, TextDrawingArea[] TDA, int j) {
		RSInterface RSInterface = addInterface(i);
		RSInterface.parentID = i;
		RSInterface.id = i;
		RSInterface.type = 4;
		RSInterface.atActionType = 0;
		RSInterface.width = 0;
		RSInterface.height = 0;
		RSInterface.contentType = 0;
		RSInterface.opacity = 0;
		RSInterface.hoverType = a;
		RSInterface.centerText = l;
		RSInterface.textShadow = m;
		RSInterface.textDrawingAreas = TDA[j];
		RSInterface.message = s;
		RSInterface.aString228 = "";
		RSInterface.textColor = k;
	}

	public static void Pestpanel(TextDrawingArea[] tda) {
		RSInterface RSinterface = addInterface(21119);
		addText(21120, "What", 0x999999, false, true, 52, tda, 1);
		addText(21121, "What", 0x33cc00, false, true, 52, tda, 1);
		addText(21122, "(Need 5 to 25 players)", 0xFFcc33, false, true, 52,
				tda, 1);
		addText(21123, "Points", 0x33ccff, false, true, 52, tda, 1);
		int last = 4;
		RSinterface.children = new int[last];
		RSinterface.childX = new int[last];
		RSinterface.childY = new int[last];
		setBounds(21120, 15, 12, 0, RSinterface);
		setBounds(21121, 15, 30, 1, RSinterface);
		setBounds(21122, 15, 48, 2, RSinterface);
		setBounds(21123, 15, 66, 3, RSinterface);
	}

	public static void Pestpanel2(TextDrawingArea[] tda) {
		RSInterface RSinterface = addInterface(21100);
		addSprite(21101, 0, "Pest Control/PEST1");
		addSprite(21102, 1, "Pest Control/PEST1");
		addSprite(21103, 2, "Pest Control/PEST1");
		addSprite(21104, 3, "Pest Control/PEST1");
		addSprite(21105, 4, "Pest Control/PEST1");
		addSprite(21106, 5, "Pest Control/PEST1");
		addText(21107, "", 0xCC00CC, false, true, 52, tda, 1);
		addText(21108, "", 0x0000FF, false, true, 52, tda, 1);
		addText(21109, "", 0xFFFF44, false, true, 52, tda, 1);
		addText(21110, "", 0xCC0000, false, true, 52, tda, 1);
		addText(21111, "250", 0x99FF33, false, true, 52, tda, 1);// w purp
		addText(21112, "250", 0x99FF33, false, true, 52, tda, 1);// e blue
		addText(21113, "250", 0x99FF33, false, true, 52, tda, 1);// se yel
		addText(21114, "250", 0x99FF33, false, true, 52, tda, 1);// sw red
		addText(21115, "200", 0x99FF33, false, true, 52, tda, 1);// attacks
		addText(21116, "0", 0x99FF33, false, true, 52, tda, 1);// knights hp
		addText(21117, "Time Remaining:", 0xFFFFFF, false, true, 52, tda, 0);
		addText(21118, "", 0xFFFFFF, false, true, 52, tda, 0);
		int last = 18;
		RSinterface.children = new int[last];
		RSinterface.childX = new int[last];
		RSinterface.childY = new int[last];
		setBounds(21101, 361, 26, 0, RSinterface);
		setBounds(21102, 396, 26, 1, RSinterface);
		setBounds(21103, 436, 26, 2, RSinterface);
		setBounds(21104, 474, 26, 3, RSinterface);
		setBounds(21105, 3, 21, 4, RSinterface);
		setBounds(21106, 3, 50, 5, RSinterface);
		setBounds(21107, 371, 60, 6, RSinterface);
		setBounds(21108, 409, 60, 7, RSinterface);
		setBounds(21109, 443, 60, 8, RSinterface);
		setBounds(21110, 479, 60, 9, RSinterface);
		setBounds(21111, 362, 10, 10, RSinterface);
		setBounds(21112, 398, 10, 11, RSinterface);
		setBounds(21113, 436, 10, 12, RSinterface);
		setBounds(21114, 475, 10, 13, RSinterface);
		setBounds(21115, 32, 32, 14, RSinterface);
		setBounds(21116, 32, 62, 15, RSinterface);
		setBounds(21117, 8, 88, 16, RSinterface);
		setBounds(21118, 87, 88, 17, RSinterface);
	}

	public String hoverText;

	public static void addHoverBox(int id, String text) {
		RSInterface rsi = interfaceCache[id];// addTabInterface(id);
		rsi.id = id;
		rsi.parentID = id;
		rsi.interfaceShown = true;
		rsi.type = 8;
		rsi.hoverText = text;
	}

	public static void addText(int id, String text, TextDrawingArea tda[],
			int idx, int color, boolean center, boolean shadow) {
		RSInterface tab = addTabInterface(id);
		tab.parentID = id;
		tab.id = id;
		tab.type = 4;
		tab.atActionType = 0;
		tab.width = 0;
		tab.height = 11;
		tab.contentType = 0;
		tab.opacity = 0;
		tab.hoverType = -1;
		tab.centerText = center;
		tab.textShadow = shadow;
		tab.textDrawingAreas = tda[idx];
		tab.message = text;
		tab.aString228 = "";
		tab.textColor = color;
		tab.anInt219 = 0;
		tab.anInt216 = 0;
		tab.anInt239 = 0;
	}

	public static void addButton(int id, int sid, String spriteName,
			String tooltip, int w, int h) {
		RSInterface tab = interfaceCache[id] = new RSInterface();
		tab.id = id;
		tab.parentID = id;
		tab.type = 5;
		tab.atActionType = 1;
		tab.contentType = 0;
		tab.opacity = (byte) 0;
		tab.hoverType = 52;
		tab.sprite1 = imageLoader(sid, spriteName);
		tab.sprite2 = imageLoader(sid, spriteName);
		tab.width = w;
		tab.height = h;
		tab.tooltip = tooltip;
	}

	public static void addConfigButton(int ID, int pID, int bID, int bID2,
			String bName, int width, int height, String tT, int configID,
			int aT, int configFrame) {
		RSInterface Tab = addTabInterface(ID);
		Tab.parentID = pID;
		Tab.id = ID;
		Tab.type = 5;
		Tab.atActionType = aT;
		Tab.contentType = 0;
		Tab.width = width;
		Tab.height = height;
		Tab.opacity = 0;
		Tab.hoverType = -1;
		Tab.valueCompareType = new int[1];
		Tab.requiredValues = new int[1];
		Tab.valueCompareType[0] = 1;
		Tab.requiredValues[0] = configID;
		Tab.valueIndexArray = new int[1][3];
		Tab.valueIndexArray[0][0] = 5;
		Tab.valueIndexArray[0][1] = configFrame;
		Tab.valueIndexArray[0][2] = 0;
		Tab.sprite1 = imageLoader(bID, bName);
		Tab.sprite2 = imageLoader(bID2, bName);
		Tab.tooltip = tT;
	}

	/*public static void addSprite(int id, int spriteId, String spriteName) {
		RSInterface tab = interfaceCache[id] = new RSInterface();
		tab.id = id;
		tab.parentID = id;
		tab.type = 5;
		tab.atActionType = 0;
		tab.contentType = 0;
		tab.opacity = (byte) 0;
		tab.hoverType = 52;
		tab.sprite1 = imageLoader(spriteId, spriteName);
		tab.sprite2 = imageLoader(spriteId, spriteName);
		tab.width = 512;
		tab.height = 334;
	}*/

	public static void addSprite(int id, int spriteId, String spriteName) {
		addSprite(id,spriteId,spriteName,-1,-1);
	}

	public static void addHoverButton(int i, String imageName, int j, int width, int height, String text, int contentType, int hoverOver, int aT) {//hoverable button
		RSInterface tab = addTabInterface(i);
		tab.id = i;
		tab.parentID = i;
		tab.type = 5;
		tab.atActionType = aT;
		tab.contentType = contentType;
		tab.opacity = 0;
		tab.hoverType = hoverOver;
		tab.sprite1 = imageLoader(j, imageName);
		tab.sprite2 = imageLoader(j, imageName);
		tab.setSprite = imageLoader(0, "l");
		tab.savedFirstSprite = imageLoader(j, imageName);
		tab.width = width;
		tab.height = height;
		tab.tooltip = text;
	}

	public static void addHoveredButton(int i, String imageName, int j, int w,
			int h, int IMAGEID) {// hoverable button
		RSInterface tab = addTabInterface(i);
		tab.parentID = i;
		tab.id = i;
		tab.type = 0;
		tab.atActionType = 0;
		tab.width = w;
		tab.height = h;
		tab.interfaceShown = true;
		tab.opacity = 0;
		tab.hoverType = -1;
		tab.scrollMax = 0;
		if (i != 24655) {
			addHoverImage(IMAGEID, j, j, imageName);
		} else {
			addHoverImage(1, IMAGEID, j, j, imageName);
		}
		tab.totalChildren(1);
		tab.child(0, IMAGEID, 0, 0);
	}

	private static Sprite LoadLunarSprite(int i, String s) {
		Sprite sprite = imageLoader(i, "/Lunar/" + s);
		return sprite;
	}

	public static void addLunarSprite(int i, int j, String name) {
		RSInterface RSInterface = addTabInterface(i);
		RSInterface.id = i;
		RSInterface.parentID = i;
		RSInterface.type = 5;
		RSInterface.atActionType = 0;
		RSInterface.contentType = 0;
		RSInterface.opacity = 0;
		RSInterface.hoverType = 52;
		RSInterface.sprite1 = LoadLunarSprite(j, name);
		RSInterface.width = 500;
		RSInterface.height = 500;
		RSInterface.tooltip = "";
	}

	public static void drawRune(int i, int id, String runeName) {
		RSInterface RSInterface = addTabInterface(i);
		RSInterface.type = 5;
		RSInterface.atActionType = 0;
		RSInterface.contentType = 0;
		RSInterface.opacity = 0;
		RSInterface.hoverType = 52;
		RSInterface.sprite1 = LoadLunarSprite(id, "RUNE");
		RSInterface.width = 500;
		RSInterface.height = 500;
	}

	public static void addRuneText(int ID, int runeAmount, int RuneID,
			TextDrawingArea[] font) {
		RSInterface rsInterface = addTabInterface(ID);
		rsInterface.id = ID;
		rsInterface.parentID = 1151;
		rsInterface.type = 4;
		rsInterface.atActionType = 0;
		rsInterface.contentType = 0;
		rsInterface.width = 0;
		rsInterface.height = 14;
		rsInterface.opacity = 0;
		rsInterface.hoverType = -1;
		rsInterface.valueCompareType = new int[1];
		rsInterface.requiredValues = new int[1];
		rsInterface.valueCompareType[0] = 3;
		rsInterface.requiredValues[0] = runeAmount;
		rsInterface.valueIndexArray = new int[1][4];
		rsInterface.valueIndexArray[0][0] = 4;
		rsInterface.valueIndexArray[0][1] = 3214;
		rsInterface.valueIndexArray[0][2] = RuneID;
		rsInterface.valueIndexArray[0][3] = 0;
		rsInterface.centerText = true;
		rsInterface.textDrawingAreas = font[0];
		rsInterface.textShadow = true;
		rsInterface.message = "%1/" + runeAmount + "";
		rsInterface.aString228 = "";
		rsInterface.textColor = 12582912;
		rsInterface.anInt219 = 49152;
	}

	public static void homeTeleport() {
		RSInterface RSInterface = addTabInterface(30000);
		RSInterface.tooltip = "Cast @gre@Lunar Home Teleport";
		RSInterface.id = 30000;
		RSInterface.parentID = 30000;
		RSInterface.type = 5;
		RSInterface.atActionType = 5;
		RSInterface.contentType = 0;
		RSInterface.opacity = 0;
		RSInterface.hoverType = 30001;
		RSInterface.sprite1 = LoadLunarSprite(1, "SPRITE");
		RSInterface.width = 20;
		RSInterface.height = 20;
		RSInterface hover = addTabInterface(30001);
		hover.hoverType = -1;
		hover.interfaceShown = true;
		setChildren(1, hover);
		addLunarSprite(30002, 0, "SPRITE");
		setBounds(30002, 0, 0, 0, hover);
	}

	public static void addLunar2RunesSmallBox(int ID, int r1, int r2, int ra1,
			int ra2, int rune1, int lvl, String name, String descr,
			TextDrawingArea[] TDA, int sid, int suo, int type) {
		RSInterface rsInterface = addTabInterface(ID);
		rsInterface.id = ID;
		rsInterface.parentID = 1151;
		rsInterface.type = 5;
		rsInterface.atActionType = type;
		rsInterface.contentType = 0;
		rsInterface.hoverType = ID + 1;
		rsInterface.spellUsableOn = suo;
		rsInterface.selectedActionName = "Cast On";
		rsInterface.width = 20;
		rsInterface.height = 20;
		rsInterface.tooltip = "Cast @gre@" + name;
		rsInterface.spellName = name;
		rsInterface.valueCompareType = new int[3];
		rsInterface.requiredValues = new int[3];
		rsInterface.valueCompareType[0] = 3;
		rsInterface.requiredValues[0] = ra1;
		rsInterface.valueCompareType[1] = 3;
		rsInterface.requiredValues[1] = ra2;
		rsInterface.valueCompareType[2] = 3;
		rsInterface.requiredValues[2] = lvl;
		rsInterface.valueIndexArray = new int[3][];
		rsInterface.valueIndexArray[0] = new int[4];
		rsInterface.valueIndexArray[0][0] = 4;
		rsInterface.valueIndexArray[0][1] = 3214;
		rsInterface.valueIndexArray[0][2] = r1;
		rsInterface.valueIndexArray[0][3] = 0;
		rsInterface.valueIndexArray[1] = new int[4];
		rsInterface.valueIndexArray[1][0] = 4;
		rsInterface.valueIndexArray[1][1] = 3214;
		rsInterface.valueIndexArray[1][2] = r2;
		rsInterface.valueIndexArray[1][3] = 0;
		rsInterface.valueIndexArray[2] = new int[3];
		rsInterface.valueIndexArray[2][0] = 1;
		rsInterface.valueIndexArray[2][1] = 6;
		rsInterface.valueIndexArray[2][2] = 0;
		rsInterface.sprite2 = LoadLunarSprite(sid, "LUNARON");
		rsInterface.sprite1 = LoadLunarSprite(sid, "LUNAROFF");
		RSInterface hover = addTabInterface(ID + 1);
		hover.hoverType = -1;
		hover.interfaceShown = true;
		setChildren(7, hover);
		addLunarSprite(ID + 2, 0, "BOX");
		setBounds(ID + 2, 0, 0, 0, hover);
		addText(ID + 3, "Level " + (lvl + 1) + ": " + name, 0xFF981F, true,
				true, 52, 1);
		setBounds(ID + 3, 90, 4, 1, hover);
		addText(ID + 4, descr, 0xAF6A1A, true, true, 52, 0);
		setBounds(ID + 4, 90, 19, 2, hover);
		setBounds(30016, 37, 35, 3, hover);// Rune
		setBounds(rune1, 112, 35, 4, hover);// Rune
		addRuneText(ID + 5, ra1 + 1, r1, TDA);
		setBounds(ID + 5, 50, 66, 5, hover);
		addRuneText(ID + 6, ra2 + 1, r2, TDA);
		setBounds(ID + 6, 123, 66, 6, hover);

	}

	public static void addLunar3RunesSmallBox(int ID, int r1, int r2, int r3,
			int ra1, int ra2, int ra3, int rune1, int rune2, int lvl,
			String name, String descr, TextDrawingArea[] TDA, int sid, int suo,
			int type) {
		RSInterface rsInterface = addTabInterface(ID);
		rsInterface.id = ID;
		rsInterface.parentID = 1151;
		rsInterface.type = 5;
		rsInterface.atActionType = type;
		rsInterface.contentType = 0;
		rsInterface.hoverType = ID + 1;
		rsInterface.spellUsableOn = suo;
		rsInterface.selectedActionName = "Cast on";
		rsInterface.width = 20;
		rsInterface.height = 20;
		rsInterface.tooltip = "Cast @gre@" + name;
		rsInterface.spellName = name;
		rsInterface.valueCompareType = new int[4];
		rsInterface.requiredValues = new int[4];
		rsInterface.valueCompareType[0] = 3;
		rsInterface.requiredValues[0] = ra1;
		rsInterface.valueCompareType[1] = 3;
		rsInterface.requiredValues[1] = ra2;
		rsInterface.valueCompareType[2] = 3;
		rsInterface.requiredValues[2] = ra3;
		rsInterface.valueCompareType[3] = 3;
		rsInterface.requiredValues[3] = lvl;
		rsInterface.valueIndexArray = new int[4][];
		rsInterface.valueIndexArray[0] = new int[4];
		rsInterface.valueIndexArray[0][0] = 4;
		rsInterface.valueIndexArray[0][1] = 3214;
		rsInterface.valueIndexArray[0][2] = r1;
		rsInterface.valueIndexArray[0][3] = 0;
		rsInterface.valueIndexArray[1] = new int[4];
		rsInterface.valueIndexArray[1][0] = 4;
		rsInterface.valueIndexArray[1][1] = 3214;
		rsInterface.valueIndexArray[1][2] = r2;
		rsInterface.valueIndexArray[1][3] = 0;
		rsInterface.valueIndexArray[2] = new int[4];
		rsInterface.valueIndexArray[2][0] = 4;
		rsInterface.valueIndexArray[2][1] = 3214;
		rsInterface.valueIndexArray[2][2] = r3;
		rsInterface.valueIndexArray[2][3] = 0;
		rsInterface.valueIndexArray[3] = new int[3];
		rsInterface.valueIndexArray[3][0] = 1;
		rsInterface.valueIndexArray[3][1] = 6;
		rsInterface.valueIndexArray[3][2] = 0;
		rsInterface.sprite2 = LoadLunarSprite(sid, "LUNARON");
		rsInterface.sprite1 = LoadLunarSprite(sid, "LUNAROFF");
		RSInterface hover = addTabInterface(ID + 1);
		hover.hoverType = -1;
		hover.interfaceShown = true;
		setChildren(9, hover);
		addLunarSprite(ID + 2, 0, "BOX");
		setBounds(ID + 2, 0, 0, 0, hover);
		addText(ID + 3, "Level " + (lvl + 1) + ": " + name, 0xFF981F, true,
				true, 52, 1);
		setBounds(ID + 3, 90, 4, 1, hover);
		addText(ID + 4, descr, 0xAF6A1A, true, true, 52, 0);
		setBounds(ID + 4, 90, 19, 2, hover);
		setBounds(30016, 14, 35, 3, hover);
		setBounds(rune1, 74, 35, 4, hover);
		setBounds(rune2, 130, 35, 5, hover);
		addRuneText(ID + 5, ra1 + 1, r1, TDA);
		setBounds(ID + 5, 26, 66, 6, hover);
		addRuneText(ID + 6, ra2 + 1, r2, TDA);
		setBounds(ID + 6, 87, 66, 7, hover);
		addRuneText(ID + 7, ra3 + 1, r3, TDA);
		setBounds(ID + 7, 142, 66, 8, hover);
	}

	public static void addLunar3RunesBigBox(int ID, int r1, int r2, int r3,
			int ra1, int ra2, int ra3, int rune1, int rune2, int lvl,
			String name, String descr, TextDrawingArea[] TDA, int sid, int suo,
			int type) {
		RSInterface rsInterface = addTabInterface(ID);
		rsInterface.id = ID;
		rsInterface.parentID = 1151;
		rsInterface.type = 5;
		rsInterface.atActionType = type;
		rsInterface.contentType = 0;
		rsInterface.hoverType = ID + 1;
		rsInterface.spellUsableOn = suo;
		rsInterface.selectedActionName = "Cast on";
		rsInterface.width = 20;
		rsInterface.height = 20;
		rsInterface.tooltip = "Cast @gre@" + name;
		rsInterface.spellName = name;
		rsInterface.valueCompareType = new int[4];
		rsInterface.requiredValues = new int[4];
		rsInterface.valueCompareType[0] = 3;
		rsInterface.requiredValues[0] = ra1;
		rsInterface.valueCompareType[1] = 3;
		rsInterface.requiredValues[1] = ra2;
		rsInterface.valueCompareType[2] = 3;
		rsInterface.requiredValues[2] = ra3;
		rsInterface.valueCompareType[3] = 3;
		rsInterface.requiredValues[3] = lvl;
		rsInterface.valueIndexArray = new int[4][];
		rsInterface.valueIndexArray[0] = new int[4];
		rsInterface.valueIndexArray[0][0] = 4;
		rsInterface.valueIndexArray[0][1] = 3214;
		rsInterface.valueIndexArray[0][2] = r1;
		rsInterface.valueIndexArray[0][3] = 0;
		rsInterface.valueIndexArray[1] = new int[4];
		rsInterface.valueIndexArray[1][0] = 4;
		rsInterface.valueIndexArray[1][1] = 3214;
		rsInterface.valueIndexArray[1][2] = r2;
		rsInterface.valueIndexArray[1][3] = 0;
		rsInterface.valueIndexArray[2] = new int[4];
		rsInterface.valueIndexArray[2][0] = 4;
		rsInterface.valueIndexArray[2][1] = 3214;
		rsInterface.valueIndexArray[2][2] = r3;
		rsInterface.valueIndexArray[2][3] = 0;
		rsInterface.valueIndexArray[3] = new int[3];
		rsInterface.valueIndexArray[3][0] = 1;
		rsInterface.valueIndexArray[3][1] = 6;
		rsInterface.valueIndexArray[3][2] = 0;
		rsInterface.sprite2 = LoadLunarSprite(sid, "LUNARON");
		rsInterface.sprite1 = LoadLunarSprite(sid, "LUNAROFF");
		RSInterface hover = addTabInterface(ID + 1);
		hover.hoverType = -1;
		hover.interfaceShown = true;
		setChildren(9, hover);
		addLunarSprite(ID + 2, 4, "BOX");
		setBounds(ID + 2, 0, 0, 0, hover);
		addText(ID + 3, "Level " + (lvl + 1) + ": " + name, 0xFF981F, true,
				true, 52, 1);
		setBounds(ID + 3, 90, 4, 1, hover);
		addText(ID + 4, descr, 0xAF6A1A, true, true, 52, 0);
		setBounds(ID + 4, 90, 21, 2, hover);
		setBounds(30016, 14, 48, 3, hover);
		setBounds(rune1, 74, 48, 4, hover);
		setBounds(rune2, 130, 48, 5, hover);
		addRuneText(ID + 5, ra1 + 1, r1, TDA);
		setBounds(ID + 5, 26, 79, 6, hover);
		addRuneText(ID + 6, ra2 + 1, r2, TDA);
		setBounds(ID + 6, 87, 79, 7, hover);
		addRuneText(ID + 7, ra3 + 1, r3, TDA);
		setBounds(ID + 7, 142, 79, 8, hover);
	}

	public static void addLunar3RunesLargeBox(int ID, int r1, int r2, int r3,
			int ra1, int ra2, int ra3, int rune1, int rune2, int lvl,
			String name, String descr, TextDrawingArea[] TDA, int sid, int suo,
			int type) {
		RSInterface rsInterface = addTabInterface(ID);
		rsInterface.id = ID;
		rsInterface.parentID = 1151;
		rsInterface.type = 5;
		rsInterface.atActionType = type;
		rsInterface.contentType = 0;
		rsInterface.hoverType = ID + 1;
		rsInterface.spellUsableOn = suo;
		rsInterface.selectedActionName = "Cast on";
		rsInterface.width = 20;
		rsInterface.height = 20;
		rsInterface.tooltip = "Cast @gre@" + name;
		rsInterface.spellName = name;
		rsInterface.valueCompareType = new int[4];
		rsInterface.requiredValues = new int[4];
		rsInterface.valueCompareType[0] = 3;
		rsInterface.requiredValues[0] = ra1;
		rsInterface.valueCompareType[1] = 3;
		rsInterface.requiredValues[1] = ra2;
		rsInterface.valueCompareType[2] = 3;
		rsInterface.requiredValues[2] = ra3;
		rsInterface.valueCompareType[3] = 3;
		rsInterface.requiredValues[3] = lvl;
		rsInterface.valueIndexArray = new int[4][];
		rsInterface.valueIndexArray[0] = new int[4];
		rsInterface.valueIndexArray[0][0] = 4;
		rsInterface.valueIndexArray[0][1] = 3214;
		rsInterface.valueIndexArray[0][2] = r1;
		rsInterface.valueIndexArray[0][3] = 0;
		rsInterface.valueIndexArray[1] = new int[4];
		rsInterface.valueIndexArray[1][0] = 4;
		rsInterface.valueIndexArray[1][1] = 3214;
		rsInterface.valueIndexArray[1][2] = r2;
		rsInterface.valueIndexArray[1][3] = 0;
		rsInterface.valueIndexArray[2] = new int[4];
		rsInterface.valueIndexArray[2][0] = 4;
		rsInterface.valueIndexArray[2][1] = 3214;
		rsInterface.valueIndexArray[2][2] = r3;
		rsInterface.valueIndexArray[2][3] = 0;
		rsInterface.valueIndexArray[3] = new int[3];
		rsInterface.valueIndexArray[3][0] = 1;
		rsInterface.valueIndexArray[3][1] = 6;
		rsInterface.valueIndexArray[3][2] = 0;
		rsInterface.sprite2 = LoadLunarSprite(sid, "LUNARON");
		rsInterface.sprite1 = LoadLunarSprite(sid, "LUNAROFF");
		RSInterface hover = addTabInterface(ID + 1);
		hover.interfaceShown = true;
		hover.hoverType = -1;
		setChildren(9, hover);
		addLunarSprite(ID + 2, 2, "BOX");
		setBounds(ID + 2, 0, 0, 0, hover);
		addText(ID + 3, "Level " + (lvl + 1) + ": " + name, 0xFF981F, true,
				true, 52, 1);
		setBounds(ID + 3, 90, 4, 1, hover);
		addText(ID + 4, descr, 0xAF6A1A, true, true, 52, 0);
		setBounds(ID + 4, 90, 34, 2, hover);
		setBounds(30016, 14, 61, 3, hover);
		setBounds(rune1, 74, 61, 4, hover);
		setBounds(rune2, 130, 61, 5, hover);
		addRuneText(ID + 5, ra1 + 1, r1, TDA);
		setBounds(ID + 5, 26, 92, 6, hover);
		addRuneText(ID + 6, ra2 + 1, r2, TDA);
		setBounds(ID + 6, 87, 92, 7, hover);
		addRuneText(ID + 7, ra3 + 1, r3, TDA);
		setBounds(ID + 7, 142, 92, 8, hover);
	}

	public static void configureLunar(TextDrawingArea[] TDA) {
		constructLunar();
		homeTeleport();
		drawRune(30003, 1, "Fire");
		drawRune(30004, 2, "Water");
		drawRune(30005, 3, "Air");
		drawRune(30006, 4, "Earth");
		drawRune(30007, 5, "Mind");
		drawRune(30008, 6, "Body");
		drawRune(30009, 7, "Death");
		drawRune(30010, 8, "Nature");
		drawRune(30011, 9, "Chaos");
		drawRune(30012, 10, "Law");
		drawRune(30013, 11, "Cosmic");
		drawRune(30014, 12, "Blood");
		drawRune(30015, 13, "Soul");
		drawRune(30016, 14, "Astral");
		addLunar3RunesSmallBox(30017, 9075, 554, 555, 0, 4, 3, 30003, 30004,
				64, "Bake Pie", "Bake pies without a stove", TDA, 0, 16, 2);
		addLunar2RunesSmallBox(30025, 9075, 557, 0, 7, 30006, 65, "Cure Plant",
				"Cure disease on farming patch", TDA, 1, 4, 2);
		addLunar3RunesBigBox(30032, 9075, 564, 558, 0, 0, 0, 30013, 30007, 65,
				"Monster Examine",
				"Detect the combat statistics of a\\nmonster", TDA, 2, 2, 2);
		addLunar3RunesSmallBox(30040, 9075, 564, 556, 0, 0, 1, 30013, 30005,
				66, "NPC Contact", "Speak with varied NPCs", TDA, 3, 0, 5);
		addLunar3RunesSmallBox(30048, 9075, 563, 557, 0, 0, 9, 30012, 30006,
				67, "Cure Other", "Cure poisoned players", TDA, 4, 8, 2);
		addLunar3RunesSmallBox(30056, 9075, 555, 554, 0, 2, 0, 30004, 30003,
				67, "Humidify", "Fills certain vessels with water", TDA, 5, 0,
				5);
		addLunar3RunesSmallBox(30064, 9075, 563, 557, 1, 0, 1, 30012, 30006,
				68, "Monster Teleport", "Teleports you to Training Spots", TDA,
				6, 0, 5);
		addLunar3RunesBigBox(30075, 9075, 563, 557, 1, 0, 3, 30012, 30006, 69,
				"Minigame Teleports", "Teleports you to MiniGames", TDA, 7, 0,
				5);
		addLunar3RunesSmallBox(30083, 9075, 563, 557, 1, 0, 5, 30012, 30006,
				70, "Boss Teleports", "Teleports you to Bosses", TDA, 8, 0, 5);
		addLunar3RunesSmallBox(30091, 9075, 564, 563, 1, 1, 0, 30013, 30012,
				70, "Cure Me", "Cures Poison", TDA, 9, 0, 5);
		addLunar2RunesSmallBox(30099, 9075, 557, 1, 1, 30006, 70, "Hunter Kit",
				"Get a kit of hunting gear", TDA, 10, 0, 5);
		addLunar3RunesSmallBox(30106, 9075, 563, 555, 1, 0, 0, 30012, 30004,
				71, "PK Teleports", "Teleports you to Pking spots", TDA, 11, 0,
				5);
		addLunar3RunesBigBox(30114, 9075, 563, 555, 1, 0, 4, 30012, 30004, 72,
				"Skilling Teleport", "Teleports you to Skilling Spots", TDA,
				12, 0, 5);
		addLunar3RunesSmallBox(30122, 9075, 564, 563, 1, 1, 1, 30013, 30012,
				73, "Cure Group", "Cures Poison on players", TDA, 13, 0, 5);
		addLunar3RunesBigBox(30130, 9075, 564, 559, 1, 1, 4, 30013, 30008, 74,
				"Stat Spy",
				"Cast on another player to see\\ntheir skill levels", TDA, 14,
				8, 2);
		addLunar3RunesBigBox(30138, 9075, 563, 554, 1, 1, 2, 30012, 30003, 74,
				"Strykeworms Tele", "Teleports you to the Strykeworms", TDA,
				15, 0, 5);
		addLunar3RunesBigBox(30146, 9075, 563, 554, 1, 1, 5, 30012, 30003, 75,
				"Tele Group Barbarian",
				"Teleports players to the\\nBarbarian Outpost", TDA, 16, 0, 5);
		addLunar3RunesSmallBox(30154, 9075, 554, 556, 1, 5, 9, 30003, 30005,
				76, "Superglass Make", "Make glass without a furnace", TDA, 17,
				16, 2);
		addLunar3RunesSmallBox(30162, 9075, 563, 555, 1, 1, 3, 30012, 30004,
				77, "Donator Zone", "Donator only teleport", TDA,
				18, 0, 5);
		addLunar3RunesSmallBox(30170, 9075, 563, 555, 1, 1, 7, 30012, 30004,
				78, "City Teleports", "Teleports to various cities",
				TDA, 19, 0, 5);
		addLunar3RunesBigBox(30178, 9075, 564, 559, 1, 0, 4, 30013, 30008, 78,
				"Dream", "Take a rest and restore hitpoints 3\\n times faster",
				TDA, 20, 0, 5);
		addLunar3RunesSmallBox(30186, 9075, 557, 555, 1, 9, 4, 30006, 30004,
				79, "String Jewellery", "String amulets without wool", TDA, 21,
				0, 5);
		addLunar3RunesLargeBox(30194, 9075, 557, 555, 1, 9, 9, 30006, 30004,
				80, "Boost Other Stats",
				"Temporarily increases Atk, Str\\nand Def of other players",
				TDA, 22, 0, 5);
		addLunar3RunesSmallBox(30202, 9075, 554, 555, 1, 6, 6, 30003, 30004,
				81, "Magic Imbue", "Combine runes without a talisman", TDA, 23,
				0, 5);
		addLunar3RunesBigBox(30210, 9075, 561, 557, 2, 1, 14, 30010, 30006, 82,
				"Fertile Soil",
				"Fertilise a farming patch with\\nsuper compost", TDA, 24, 4, 2);
		addLunar3RunesBigBox(30218, 9075, 557, 555, 2, 11, 9, 30006, 30004, 83,
				"Boost Stats",
				"Temporarily increases Attack,\\nStrength and Defence", TDA,
				25, 0, 5);
		addLunar3RunesSmallBox(30226, 9075, 563, 555, 2, 2, 9, 30012, 30004,
				84, "Fishing Guild Teleport",
				"Teleports you to the fishing guild", TDA, 26, 0, 5);
		addLunar3RunesLargeBox(30234, 9075, 563, 555, 1, 2, 13, 30012, 30004,
				85, "Tele Group Fishing\\nGuild",
				"Teleports players to the Fishing\\nGuild", TDA, 27, 0, 5);
		addLunar3RunesSmallBox(30242, 9075, 557, 561, 2, 14, 0, 30006, 30010,
				85, "Plank Make", "Turn Logs into planks", TDA, 28, 16, 5);
		addLunar3RunesSmallBox(30250, 9075, 563, 555, 2, 2, 9, 30012, 30004,
				86, "Catherby Teleport", "Teleports you to Catherby", TDA, 29,
				0, 5);
		addLunar3RunesSmallBox(30258, 9075, 563, 555, 2, 2, 14, 30012, 30004,
				87, "Tele Group Catherby", "Teleports players to Catherby",
				TDA, 30, 0, 5);
		addLunar3RunesSmallBox(30266, 9075, 563, 555, 2, 2, 7, 30012, 30004,
				88, "Ice Plateau Teleport", "Teleports you to Ice Plateau",
				TDA, 31, 0, 5);
		addLunar3RunesBigBox(30274, 9075, 563, 555, 2, 2, 15, 30012, 30004, 89,
				"Tele Group Ice\\n Plateau",
				"\\nTeleports players to Ice Plateau", TDA, 32, 0, 5);
		addLunar3RunesBigBox(30282, 9075, 563, 561, 2, 1, 0, 30012, 30010, 90,
				"Energy Transfer",
				"Spend HP and REnergy to give\\naway to another player", TDA,
				33, 8, 2);
		addLunar3RunesBigBox(30290, 9075, 563, 565, 2, 2, 0, 30012, 30014, 91,
				"Heal Other", "Heal targeted player by\\nup to 40 HP", TDA, 34,
				8, 2);
		addLunar3RunesBigBox(30298, 9075, 560, 557, 2, 1, 9, 30009, 30006, 92,
				"Vengeance Other",
				"Allows another player to rebound\\ndamage to an opponent",
				TDA, 35, 8, 2);
		addLunar3RunesSmallBox(30306, 9075, 560, 557, 3, 1, 9, 30009, 30006,
				93, "Vengeance", "Rebound damage to an opponent", TDA, 36, 0, 5);
		addLunar3RunesBigBox(30314, 9075, 565, 563, 3, 2, 5, 30014, 30012, 94,
				"Heal Group", "Heal all players around you\\nby up to 40 HP",
				TDA, 37, 0, 5);
		addLunar3RunesBigBox(30322, 9075, 564, 563, 2, 1, 0, 30013, 30012, 95,
				"Spellbook Swap",
				"Change to another spellbook for 1\\nspell cast", TDA, 38, 0, 5);
	}

	public static void constructLunar() {
		RSInterface Interface = addTabInterface(29999);
		int[] LunarIDs = { 30000, 30017, 30025, 30032, 30040, 30048, 30056,
				30064, 30075, 30083, 30091, 30099, 30106, 30114, 30122, 30130,
				30138, 30146, 30154, 30162, 30170, 30178, 30186, 30194, 30202,
				30210, 30218, 30226, 30234, 30242, 30250, 30258, 30266, 30274,
				30282, 30290, 30298, 30306, 30314, 30322, 30001, 30018, 30026,
				30033, 30041, 30049, 30057, 30065, 30076, 30084, 30092, 30100,
				30107, 30115, 30123, 30131, 30139, 30147, 30155, 30163, 30171,
				30179, 30187, 30195, 30203, 30211, 30219, 30227, 30235, 30243,
				30251, 30259, 30267, 30275, 30283, 30291, 30299, 30307, 30323,
				30315 };
		int[] LunarX = { 11, 40, 71, 103, 133, 162, 8, 41, 71, 103, 134, 165,
				12, 42, 71, 103, 135, 165, 14, 42, 71, 101, 135, 168, 10, 42,
				74, 103, 135, 164, 10, 42, 71, 103, 138, 162, 13, 42, 69, 104,
				6, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
				5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5 };
		int[] LunarY = { 10, 9, 12, 10, 12, 10, 38, 39, 39, 39, 39, 37, 68, 68,
				66, 68, 68, 68, 97, 97, 97, 97, 98, 98, 126, 124, 125, 125,
				125, 126, 155, 155, 155, 155, 155, 155, 185, 185, 183, 184,
				184, 176, 176, 163, 176, 176, 176, 176, 163, 176, 176, 176,
				176, 163, 176, 163, 163, 163, 176, 176, 176, 163, 176, 149,
				176, 163, 163, 176, 149, 176, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5 };
		Interface.totalChildren(LunarIDs.length);
		for (int index = 0; index < LunarIDs.length; index++) {
			Interface.child(index, LunarIDs[index], LunarX[index],
					LunarY[index]);
		}
	}

	public static void addHoverImage(int i, int j, int k, String name) {
		RSInterface tab = addTabInterface(i);
		tab.id = i;
		tab.parentID = i;
		tab.type = 5;
		tab.atActionType = 0;
		tab.contentType = 0;
		tab.width = 512;
		tab.height = 334;
		tab.opacity = 0;
		tab.hoverType = 52;
		tab.sprite1 = imageLoader(j, name);
		tab.sprite2 = imageLoader(k, name);
		tab.setSprite = imageLoader(k, "1bsdc");
		tab.savedFirstSprite = imageLoader(j, name);
	}

	public static void addHoverImage(int a, int i, int j, int k, String name) {
		RSInterface tab = addTabInterface(i);
		tab.id = i;
		tab.parentID = i;
		tab.type = 5;
		tab.atActionType = 0;
		tab.contentType = 0;
		tab.width = 512;
		tab.height = 334;
		tab.opacity = 0;
		tab.hoverType = 52;
		tab.sprite1 = imageLoader(j, name);
		tab.sprite2 = imageLoader(k, name);
		tab.setSprite = imageLoader(41, "Interfaces/GrandExchange/sprite");
		tab.savedFirstSprite = imageLoader(j, name);
	}

	public static void addTransparentSprite(int id, int spriteId,
			String spriteName) {
		RSInterface tab = interfaceCache[id] = new RSInterface();
		tab.id = id;
		tab.parentID = id;
		tab.type = 5;
		tab.atActionType = 0;
		tab.contentType = 0;
		tab.opacity = (byte) 0;
		tab.hoverType = 52;
		tab.sprite1 = imageLoader(spriteId, spriteName);
		tab.sprite2 = imageLoader(spriteId, spriteName);
		tab.width = 512;
		tab.height = 334;
		tab.drawsTransparent = true;
	}

	public static RSInterface addScreenInterface(int id) {
		RSInterface tab = interfaceCache[id] = new RSInterface();
		tab.id = id;
		tab.parentID = id;
		tab.type = 0;
		tab.atActionType = 0;
		tab.contentType = 0;
		tab.width = 512;
		tab.height = 334;
		tab.opacity = (byte) 0;
		tab.hoverType = 0;
		return tab;
	}

	public static RSInterface addTabInterface(int id) {
		RSInterface tab = interfaceCache[id] = new RSInterface();
		tab.id = id;// 250
		tab.parentID = id;// 236
		tab.type = 0;// 262
		tab.atActionType = 0;// 217
		tab.contentType = 0;
		tab.width = 512;// 220
		tab.height = 700;// 267
		tab.opacity = (byte) 0;
		tab.hoverType = -1;// Int 230
		return tab;
	}

	private static Sprite imageLoader(int i, String s) {
		long l = (TextClass.method585(s) << 8) + (long) i;
		Sprite sprite = (Sprite) aMRUNodes_238.insertFromCache(l);
		if (sprite != null)
			return sprite;
		try {
			sprite = new Sprite(s + " " + i);
			aMRUNodes_238.removeFromCache(sprite, l);
		} catch (Exception exception) {
			return null;
		}
		return sprite;
	}

	public void child(int id, int interID, int x, int y) {
		children[id] = interID;
		childX[id] = x;
		childY[id] = y;
	}

	public void totalChildren(int t) {
		children = new int[t];
		childX = new int[t];
		childY = new int[t];
	}

	private Model method206(int i, int j) {
		Model model = (Model) aMRUNodes_264.insertFromCache((i << 16) + j);
		if (model != null)
			return model;
		if (i == 1)
			model = Model.method462(j);
		if (i == 2)
			model = EntityDef.forID(j).method160();
		if (i == 3)
			model = Client.myPlayer.method453();
		if (i == 4)
			model = ItemDef.forID(j).method202(50);
		if (i == 5)
			model = null;
		if (model != null)
			aMRUNodes_264.removeFromCache(model, (i << 16) + j);
		return model;
	}

	private static Sprite method207(int i, StreamLoader streamLoader, String s) {
		long l = (TextClass.method585(s) << 8) + (long) i;
		Sprite sprite = (Sprite) aMRUNodes_238.insertFromCache(l);
		if (sprite != null)
			return sprite;
		try {
			sprite = new Sprite(streamLoader, s, i);
			aMRUNodes_238.removeFromCache(sprite, l);
		} catch (Exception _ex) {
			return null;
		}
		return sprite;
	}

	public static void method208(boolean flag, Model model) {
		int i = 0;// was parameter
		int j = 5;// was parameter
		if (flag)
			return;
		aMRUNodes_264.unlinkAll();
		if (model != null && j != 4)
			aMRUNodes_264.removeFromCache(model, (j << 16) + i);
	}

	public Model method209(int j, int k, boolean flag) {
		Model model;
		if (flag)
			model = method206(anInt255, anInt256);
		else
			model = method206(anInt233, mediaID);
		if (model == null)
			return null;
		if (k == -1 && j == -1 && model.anIntArray1640 == null)
			return model;
		Model model_1 = new Model(true, FrameReader.method532(k)
				& FrameReader.method532(j), false, model);
		if (k != -1 || j != -1)
			model_1.method469();
		if (k != -1)
			model_1.method470(k);
		if (j != -1)
			model_1.method470(j);
		model_1.method479(64, 768, -50, -10, -50, true);
		return model_1;
	}

	public RSInterface() {
	}

	public Sprite setSprite;
	public Sprite savedFirstSprite;
	public static StreamLoader aClass44;
	public boolean drawsTransparent;
	public Sprite sprite1;
	public byte aByte254;
	public int anInt208;
	public int anIntArray212[];
	public int anIntArray245[];
	public Sprite sprites[];
	public static RSInterface interfaceCache[];
	public int requiredValues[];
	public int contentType;// anInt214
	public int spritesX[];
	public int anInt216;
	public int atActionType;
	public String spellName;
	public int anInt219;
	public int width;
	public String disabledText;
	public int hoverType;
	public int itemSpriteId1;
	public int itemSpriteId2;
	public int itemSpriteZoom1;
	public int itemSpriteZoom2;
	public int itemSpriteIndex;
	public String tooltip;
	public String selectedActionName;
	public boolean centerText;
	public int scrollPosition;
	public String actions[];
	public int valueIndexArray[][];
	public boolean aBoolean227;
	public String aString228;
	public int invSpritePadX;
	public int textColor;
	public int anInt233;
	public int mediaID;
	public boolean aBoolean235;
	public int parentID;
	public int spellUsableOn;
	private static MRUNodes aMRUNodes_238;
	public int anInt239;
	public Sprite savedSprite[] = new Sprite[10];
	public int children[];
	public int childX[];
	public boolean usableItemInterface;
	public TextDrawingArea textDrawingAreas;
	public int invSpritePadY;
	public int valueCompareType[];
	public int anInt246;
	public int spritesY[];
	public String message;
	public boolean isInventoryInterface;
	public int id;
	public int invStackSizes[];
	public int inv[];
	public byte opacity;
	private int anInt255;
	private int anInt256;
	public int anInt257;
	public int anInt258;
	public boolean aBoolean259;
	public Sprite sprite2;
	public int scrollMax;
	public int type;
	public int xOffset;
	private static final MRUNodes aMRUNodes_264 = new MRUNodes(30);
	public int yOffset;
	public boolean interfaceShown;
	public int height;
	public boolean textShadow;
	public int modelZoom;
	public int modelRotation1;
	public int modelRotation2;
	public int childY[];
	public boolean inventoryHover;
	private int transAmount;
	public boolean greyScale;

	public static void addChar(int ID) {
		RSInterface t = interfaceCache[ID] = new RSInterface();
		t.id = ID;
		t.parentID = ID;
		t.type = 6;
		t.atActionType = 0;
		t.contentType = 328;
		t.width = 180;
		t.height = 190;
		t.aByte254 = 0;
		t.hoverType = 0;
		t.modelZoom = 560;
		t.modelRotation1 = 30;
		t.modelRotation2 = 0;
		t.anInt257 = -1;
		t.anInt258 = -1;
	}

	public static void setBounds(int ID, int X, int Y, int frame,
			RSInterface RSinterface) {
		RSinterface.children[frame] = ID;
		RSinterface.childX[frame] = X;
		RSinterface.childY[frame] = Y;
	}

	public static void addButton(int i, int j, String name, int W, int H,
			String S, int AT) {
		RSInterface RSInterface = addInterface(i);
		RSInterface.id = i;
		RSInterface.parentID = i;
		RSInterface.type = 5;
		RSInterface.atActionType = AT;
		RSInterface.contentType = 0;
		RSInterface.opacity = 0;
		RSInterface.hoverType = 52;
		RSInterface.sprite1 = imageLoader(j, name);
		RSInterface.sprite2 = imageLoader(j, name);
		RSInterface.width = W;
		RSInterface.height = H;
		RSInterface.tooltip = S;
	}

	private static int[] summoningLevelRequirements = {
		1,4,10,13,16,17,18,19,22,23,25,28,29,31,32,33,34,34,34,34,36,40,41,42,43,43,43,43,43,43,43,46,46,47,49,52,54,55,56,56,57,
		57,57,58,61,62,63,64,66,66,67,68,69,70,71,72,73,74,75,76,76,77,78,79,79,79,80,83,83,85,86,88,89,92,93,95,96,99
	};
	
	private static int[] pouchItems = {
		12047,//Spirit_wolf_pouch
		12043,//Dreadfowl_pouch
		12059,//Spirit_spider_pouch
		12019,//Thorny_snail_pouch
		12009,//Granite_crab_pouch
		12778,//Spirit_mosquito_pouch
		12049,//Desert_wyrm_pouch
		12055,//Spirit_scorpion_pouch
		12808,//Spirit_tz-kih_pouch
		12067,//Albino_rat_pouch
		12063,//Spirit_kalphite_pouch
		12091,//Compost_mound_pouch
		12800,//Giant_chinchompa_pouch
		12053,//Vampire_bat_pouch
		12065,//Honey_badger_pouch
		12021,//Beaver_pouch
		12818,//Void_ravager_pouch
		12780,//Void_spinner_pouch
		12798,//Void_torcher_pouch
		12814,//Void_shifter_pouch
		12073,//Bronze_minotaur_pouch
		12087,//Bull_ant_pouch
		12071,//Macaw_pouch
		12051,//Evil_turnip_pouch
		12095,//Sp._cockatrice_pouch
		12097,//Sp._guthatrice_pouch
		12099,//Sp._saratrice_pouch
		12101,//Sp._zamatrice_pouch
		12103,//Sp._pengatrice_pouch
		12105,//Sp._coraxatrice_pouch
		12107,//Sp._vulatrice_pouch
		12075,//Iron_minotaur_pouch
		12816,//Pyrelord_pouch
		12041,//Magpie_pouch
		12061,//Bloated_leech_pouch
		12007,//Spirit_terrorbird_pouch
		12035,//Abyssal_parasite_pouch
		12027,//Spirit_jelly_pouch
		12077,//Steel_minotaur_pouch
		12531,//Ibis_pouch
		12810,//Spirit_graahk_pouch
		12812,//Spirit_kyatt_pouch
		12784,//Spirit_larupia_pouch
		12023,//Karam._overlord_pouch
		12085,//Smoke_devil_pouch
		12037,//Abyssal_lurker_pouch
		12015,//Spirit_cobra_pouch
		12045,//Stranger_plant_pouch
		12079,//Mithril_minotaur_pouch
		12123,//Barker_toad_pouch
		12031,//War_tortoise_pouch
		12029,//Bunyip_pouch
		12033,//Fruit_bat_pouch
		12820,//Ravenous_locust_pouch
		12057,//Arctic_bear_pouch
		14623,//Phoenix_pouch
		12792,//Obsidian_golem_pouch
		12069,//Granite_lobster_pouch
		12011,//Praying_mantis_pouch
		12081,//Adamant_minotaur_pouch
		12782,//Forge_regent_pouch
		12794,//Talon_beast_pouch
		12013,//Giant_ent_pouch
		12802,//Fire_titan_pouch
		12804,//Moss_titan_pouch
		12806,//Ice_titan_pouch
		12025,//Hydra_pouch
		12017,//Spirit_dagannoth_pouch
		12788,//Lava_titan_pouch
		12776,//Swamp_titan_pouch
		12083,//Rune_minotaur_pouch
		12039,//Unicorn_stallion_pouch
		12786,//Geyser_titan_pouch
		12089,//Wolpertinger_pouch
		12796,//Abyssal_titan_pouch
		12822,//Iron_titan_pouch
		12093,//Pack_yak_pouch
		12790,//Steel_titan_pouch
	};
	private static int[] scrollItems = {
		12425,//Howl_scroll
		12445,//Dreadfowl_strike_scroll
		12428,//Egg_spawn_scroll
		12459,//Slime_spray_scroll
		12533,//Stony_shell_scroll
		12838,//Pester_scroll
		12460,//Electric_lash_scroll
		12432,//Venom_shot_scroll
		12839,//Fireball_assault_scroll
		12430,//Cheese_feast_scroll
		12446,//Sandstorm_scroll
		12440,//Generate_compost_scroll
		12834,//Explode_scroll
		12447,//Vampire_touch_scroll
		12433,//Insane_ferocity_scroll
		12429,//Multichop_scroll
		12443,//Call_to_arms_scroll
		12443,//Call_to_arms_scroll
		12443,//Call_to_arms_scroll
		12443,//Call_to_arms_scroll
		12461,//Bronze_bull_rush_scroll
		12431,//Unburden_scroll
		12422,//Herbcall_scroll
		12448,//Evil_flames_scroll
		12458,//Petrifying_gaze_scroll
		12458,//Petrifying_gaze_scroll
		12458,//Petrifying_gaze_scroll
		12458,//Petrifying_gaze_scroll
		12458,//Petrifying_gaze_scroll
		12458,//Petrifying_gaze_scroll
		12458,//Petrifying_gaze_scroll
		12462,//Iron_bull_rush_scroll
		12829,//Immense_heat_scroll
		12426,//Thieving_fingers_scroll
		12444,//Blood_drain_scroll
		12441,//Tireless_run_scroll
		12454,//Abyssal_drain_scroll
		12453,//Dissolve_scroll,//,//
		12463,//Steel_bull_rush_scroll
		12424,//Fish_rain_scroll
		12835,//Goad_scroll
		12836,//Ambush_scroll
		12840,//Rending_scroll
		12455,//Doomsphere_scroll
		12468,//Dust_cloud_scroll
		12427,//Abyssal_stealth_scroll
		12436,//Oph._incubation_scroll
		12467,//Poisonous_blast_scroll
		12464,//Mith_bull_rush_scroll
		12452,//Toad_bark_scroll
		12439,//Testudo_scroll
		12438,//Swallow_whole_scroll
		12423,//Fruitfall_scroll
		12830,//Famine_scroll
		12451,//Arctic_blast_scroll
		14622,//Rise_from_the_ashes_scroll
		12826,//Volcanic_str._scroll
		12449,//Crushing_claw_scroll
		12450,//Mantis_strike_scroll
		12465,//Addy_bull_rush_scroll
		12841,//Inferno_scroll
		12831,//Deadly_claw_scroll
		12457,//Acorn_missile_scroll
		12824,//Titan's_con._scroll
		12824,//Titan's_con._scroll
		12824,//Titan's_con._scroll
		12442,//Regrowth_scroll
		12456,//Spike_shot_scroll
		12837,//Ebon_thunder_scroll
		12832,//Swamp_plague_scroll
		12466,//Rune_bull_rush_scroll
		12434,//Healing_aura_scroll
		12833,//Boil_scroll
		12437,//Magic_focus_scroll
		12827,//Essence_shipment_scroll
		12828,//Iron_within_scroll
		12435,//Winter_storage_scroll
		12825,//Steel_of_legends_scroll
	};
	private static String[] scrollNames = {
		"Howl",
		"Dreadfowl Strike",
		"Egg Spawn",
		"Slime Spray",
		"Stony Shell",
		"Pester",
		"Electric Lash",
		"Venom Shot",
		"Fireball Assault",
		"Cheese Feast",
		"Sandstorm",
		"Generate Compost",
		"Explode",
		"Vampire Touch",
		"Insane Ferocity",
		"Multichop",
		"Call of Arms",
		"Call of Arms",
		"Call of Arms",
		"Call of Arms",
		"Bronze Bull Rush",
		"Unburden",
		"Herbcall",
		"Evil Flames",
		"Petrifying gaze",
		"Petrifying gaze",
		"Petrifying gaze",
		"Petrifying gaze",
		"Petrifying gaze",
		"Petrifying gaze",
		"Petrifying gaze",
		"Iron Bull Rush",
		"Immense Heat",
		"Thieving Fingers",
		"Blood Drain",
		"Tireless Run",
		"Abyssal Drain",
		"Dissolve",
		"Steel Bull Rush",
		"Fish Rain",
		"Goad",
		"Ambush",
		"Rending",
		"Doomsphere Device",
		"Dust Cloud",
		"Abyssal Stealth",
		"Ophidian Incubation",
		"Poisonous Blast",
		"Mithril Bull Rush",
		"Toad Bark",
		"Testudo",
		"Swallow Whole",
		"Fruitfall",
		"Famine",
		"Arctic Blast",
		"Rise from the Ashes",
		"Volcanic Strength",
		"Crushing Claw",
		"Mantis Strike",
		"Adamant Bull Rush",
		"Inferno",
		"Deadly Claw",
		"Acorn Missile",
		"Titan's Consitution",
		"Titan's Consitution",
		"Titan's Consitution",
		"Regrowth",
		"Spike Shot",
		"Ebon Thunder",
		"Swamp Plague",
		"Rune Bull Rush",
		"Healing Aura",
		"Boil",
		"Magic Focus",
		"Essence Shipment",
		"Iron Within",
		"Winter Storage",
		"Steel of Legends",
	};
	private static String[] pouchNames = {
		"Spirit wolf",
		"Dreadfowl",
		"Spirit spider",
		"Thorny snail",
		"Granite crab",
		"Spirit mosquito",
		"Desert wyrm",
		"Spirit scorpion",
		"Spirit tz-kih",
		"Albino rat",
		"Spirit kalphite",
		"Compost mound",
		"Giant chinchompa",
		"Vampire bat",
		"Honey badger",
		"Beaver",
		"Void ravager",
		"Void spinner",
		"Void torcher",
		"Void shifter",
		"Bronze minotaur",
		"Bull ant",
		"Macaw",
		"Evil turnip",
		"Sp. cockatrice",
		"Sp. guthatrice",
		"Sp. saratrice",
		"Sp. zamatrice",
		"Sp. pengatrice",
		"Sp. coraxatrice",
		"Sp. vulatrice",
		"Iron minotaur",
		"Pyrelord",
		"Magpie",
		"Bloated leech",
		"Spirit terrorbird",
		"Abyssal parasite",
		"Spirit jelly",
		"Steel minotaur",
		"Ibis",
		"Spirit graahk",
		"Spirit kyatt",
		"Spirit larupia",
		"Karam. overlord",
		"Smoke devil",
		"Abyssal lurker",
		"Spirit cobra",
		"Stranger plant",
		"Mithril minotaur",
		"Barker toad",
		"War tortoise",
		"Bunyip",
		"Fruit bat",
		"Ravenous locust",
		"Arctic bear",
		"Phoenix",
		"Obsidian golem",
		"Granite lobster",
		"Praying mantis",
		"Adamant minotaur",
		"Forge regent",
		"Talon beast",
		"Giant ent",
		"Fire titan",
		"Moss titan",
		"Ice titan",
		"Hydra",
		"Spirit dagannoth",
		"Lava titan",
		"Swamp titan",
		"Rune minotaur",
		"Unicorn stallion",
		"Geyser titan",
		"Wolpertinger",
		"Abyssal titan",
		"Iron titan",
		"Pack yak",
		"Steel titan",
	};

}
