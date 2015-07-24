package src;
public final class ObjectDef {
	public static ObjectDef forID(int i) {
		if (i > streamIndices.length)
			i = streamIndices.length - 1;
		for (int j = 0; j < 20; j++)
			if (cache[j].type == i) {
				return cache[j];
			}
		cacheIndex = (cacheIndex + 1) % 20;
		ObjectDef objectDef = cache[cacheIndex];
		try {
			stream.currentOffset = streamIndices[i];
		} catch (Exception e) {
			e.printStackTrace();
		}
		objectDef.type = i;
		objectDef.setDefaults();
		objectDef.readValues(stream);
		if(i == 10284) {
			objectDef.name = "Chest";
			objectDef.hasActions = true;
			objectDef.actions = new String[5];
			objectDef.actions[0] = "Open";
		}
		if(i == 22721) {
			objectDef.hasActions = true;
			objectDef.actions = new String[5];
			objectDef.actions[0] = "Smelt";
		}
		if(i == 7837) {
			objectDef.hasActions = true;
			objectDef.actions = new String[5];
		}
		if (objectDef.type == i && objectDef.originalModelColors == null) {
			objectDef.originalModelColors = new int[1];
			objectDef.modifiedModelColors = new int[1];
			objectDef.originalModelColors[0] = 0;
			objectDef.modifiedModelColors[0] = 1;
		}
		//if (objectDef.animationID == -1)
			//objectDef.animationID = 0;
		return objectDef;
	}
	
	private void setDefaults() {
		objectModelIDs = null;
		anIntArray776 = null;
		name = null;
		description = null;
		modifiedModelColors = null;
		originalModelColors = null;
		anInt744 = 1;
		anInt761 = 1;
		isUnwalkable = true;
		aBoolean757 = true;
		hasActions = false;
		adjustToTerrain = false;
		aBoolean769 = false;
		aBoolean764 = false;
		animationID = -1;
		anInt775 = 16;
		brightness = 0;
		contrast = 0;
		actions = null;
		mapFunctionID = -1;
		mapSceneID = -1;
		aBoolean751 = false;
		aBoolean779 = true;
		modelSizeX = 128;
		modelSizeH = 128;
		modelSizeY = 128;
		anInt768 = 0;
		offsetX = 0;
		offsetH = 0;
		offsetY = 0;
		aBoolean736 = false;
		isSolidObject = false;
		anInt760 = -1;
		anInt774 = -1;
		configID = -1;
		configObjectIDs = null;
	}

	public void method574(OnDemandFetcher class42_sub1) {
		if (objectModelIDs == null)
			return;
		for (int j = 0; j < objectModelIDs.length; j++)
			class42_sub1.method560(objectModelIDs[j] & 0xffff, 0);
	}

	public static void nullLoader() {
		mruNodes1 = null;
		mruNodes2 = null;
		streamIndices = null;
		cache = null;
		stream = null;
	}

	public static void unpackConfig(StreamLoader streamLoader) {
		stream = new Stream(streamLoader.getDataForName("loc.dat"));
		Stream stream = new Stream(streamLoader.getDataForName("loc.idx"));
		int totalObjects = stream.readUnsignedWord();
		streamIndices = new int[totalObjects + 40000];
		int i = 2;
		for (int j = 0; j < totalObjects; j++) {
			streamIndices[j] = i;
			i += stream.readUnsignedWord();
		}
		cache = new ObjectDef[20];
		for (int k = 0; k < 20; k++)
			cache[k] = new ObjectDef();
	}

	public boolean method577(int i) {
		if (anIntArray776 == null) {
			if (objectModelIDs == null)
				return true;
			if (i != 10)
				return true;
			boolean flag1 = true;
			for (int k = 0; k < objectModelIDs.length; k++)
				flag1 &= Model.method463(objectModelIDs[k] & 0xffff);
			return flag1;
		}
		for (int j = 0; j < anIntArray776.length; j++)
			if (anIntArray776[j] == i)
				return Model.method463(objectModelIDs[j] & 0xffff);

		return true;
	}

	public Model method578(int i, int j, int k, int l, int i1, int j1, int k1) {
		Model model = method581(i, k1, j);
		if (model == null)
			return null;
		if (adjustToTerrain || aBoolean769)
			model = new Model(adjustToTerrain, aBoolean769, model);
		if (adjustToTerrain) {
			int l1 = (k + l + i1 + j1) / 4;
			for (int i2 = 0; i2 < model.anInt1626; i2++) {
				int j2 = model.anIntArray1627[i2];
				int k2 = model.anIntArray1629[i2];
				int l2 = k + ((l - k) * (j2 + 64)) / 128;
				int i3 = j1 + ((i1 - j1) * (j2 + 64)) / 128;
				int j3 = l2 + ((i3 - l2) * (k2 + 64)) / 128;
				model.anIntArray1628[i2] += j3 - l1;
			}
			model.method467();
		}
		return model;
	}

	public boolean method579() {
		if (objectModelIDs == null)
			return true;
		boolean flag1 = true;
		for (int i = 0; i < objectModelIDs.length; i++)
			flag1 &= Model.method463(objectModelIDs[i] & 0xffff);
		return flag1;
	}

	public ObjectDef method580() {
		int i = -1;
		if (anInt774 != -1) {
			VarBit varBit = VarBit.cache[anInt774];
			int j = varBit.anInt648;
			int k = varBit.anInt649;
			int l = varBit.anInt650;
			int i1 = Client.anIntArray1232[l - k];
			i = clientInstance.variousSettings[j] >> k & i1;
		} else if (configID != -1)
			i = clientInstance.variousSettings[configID];
		if (i < 0 || i >= configObjectIDs.length || configObjectIDs[i] == -1)
			return null;
		else
			return forID(configObjectIDs[i]);
	}

	private Model method581(int j, int k, int l) {
		Model model = null;
		long l1;
		if (anIntArray776 == null) {
			if (j != 10)
				return null;
			l1 = (long) ((type << 8) + l) + ((long) (k + 1) << 32);
			Model model_1 = (Model) mruNodes2.insertFromCache(l1);
			if (model_1 != null)
				return model_1;
			if (objectModelIDs == null)
				return null;
			boolean flag1 = aBoolean751 ^ (l > 3);
			int k1 = objectModelIDs.length;
			for (int i2 = 0; i2 < k1; i2++) {
				int l2 = objectModelIDs[i2];
				if (flag1)
					l2 += 0x10000;
				model = (Model) mruNodes1.insertFromCache(l2);
				if (model == null) {
					model = Model.method462(l2 & 0xffff);
					if (model == null)
						return null;
					if (flag1)
						model.method477();
					mruNodes1.removeFromCache(model, l2);
				}
				if (k1 > 1)
					aModelArray741s[i2] = model;
			}
			if (k1 > 1)
				model = new Model(k1, aModelArray741s);
		} else {
			int i1 = -1;
			for (int j1 = 0; j1 < anIntArray776.length; j1++) {
				if (anIntArray776[j1] != j)
					continue;
				i1 = j1;
				break;
			}
			if (i1 == -1)
				return null;
			l1 = (long) ((type << 8) + (i1 << 3) + l) + ((long) (k + 1) << 32);
			Model model_2 = (Model) mruNodes2.insertFromCache(l1);
			if (model_2 != null)
				return model_2;
			int j2 = objectModelIDs[i1];
			boolean flag3 = aBoolean751 ^ (l > 3);
			if (flag3)
				j2 += 0x10000;
			model = (Model) mruNodes1.insertFromCache(j2);
			if (model == null) {
				model = Model.method462(j2 & 0xffff);
				if (model == null)
					return null;
				if (flag3)
					model.method477();
				mruNodes1.removeFromCache(model, j2);
			}
		}
		boolean flag;
		flag = modelSizeX != 128 || modelSizeH != 128 || modelSizeY != 128;
		boolean flag2;
		flag2 = offsetX != 0 || offsetH != 0 || offsetY != 0;
		Model model_3 = new Model(modifiedModelColors == null,
				FrameReader.method532(k), l == 0 && k == -1 && !flag && !flag2,
				model);
		if (k != -1) {
			model_3.method469();
			model_3.method470(k);
			model_3.anIntArrayArray1658 = null;
			model_3.anIntArrayArray1657 = null;
		}
		while (l-- > 0)
			model_3.method473();
		if (modifiedModelColors != null) {
			for (int k2 = 0; k2 < modifiedModelColors.length; k2++)
				model_3.method476(modifiedModelColors[k2],
						originalModelColors[k2]);
		}
		if (flag)
			model_3.method478(modelSizeX, modelSizeY, modelSizeH);
		if (flag2)
			model_3.method475(offsetX, offsetH, offsetY);
		model_3.method479(74, 1000, -90, -580, -90, !aBoolean769);
		if (anInt760 == 1)
			model_3.anInt1654 = model_3.modelHeight;
		mruNodes2.removeFromCache(model_3, l1);
		return model_3;
	}

	private void readValues(Stream stream) {
		int i = -1;
		label0: do {
			int opcode;
			do {
				opcode = stream.readUnsignedByte();
				if (opcode == 0)
					break label0;
				if (opcode == 1) {
					int k = stream.readUnsignedByte();
					if (k > 0)
						if (objectModelIDs == null || lowMem) {
							anIntArray776 = new int[k];
							objectModelIDs = new int[k];
							for (int k1 = 0; k1 < k; k1++) {
								objectModelIDs[k1] = stream.readUnsignedWord();
								anIntArray776[k1] = stream.readUnsignedByte();
							}
						} else {
							stream.currentOffset += k * 3;
						}
				} else if (opcode == 2)
					name = stream.readString();
				else if (opcode == 3)
					description = stream.readBytes();
				else if (opcode == 5) {
					int l = stream.readUnsignedByte();
					if (l > 0)
						if (objectModelIDs == null || lowMem) {
							anIntArray776 = null;
							objectModelIDs = new int[l];
							for (int l1 = 0; l1 < l; l1++)
								objectModelIDs[l1] = stream.readUnsignedWord();
						} else {
							stream.currentOffset += l * 2;
						}
				} else if (opcode == 14)
					anInt744 = stream.readUnsignedByte();
				else if (opcode == 15)
					anInt761 = stream.readUnsignedByte();
				else if (opcode == 17)
					isUnwalkable = false;
				else if (opcode == 18)
					aBoolean757 = false;
				else if (opcode == 19) {
					i = stream.readUnsignedByte();
					if(i == 1)
						hasActions = true;
				} else if (opcode == 21)
					adjustToTerrain = true;
				else if (opcode == 22)
					aBoolean769 = false;
				else if (opcode == 23)
					aBoolean764 = true;
				else if (opcode == 24) {
					animationID = stream.readUnsignedWord();
					if (animationID == 65535)
						animationID = -1;
				} else if (opcode == 28)
					anInt775 = stream.readUnsignedByte();
				else if (opcode == 29)
					brightness = stream.readSignedByte();
				else if (opcode == 39)
					contrast = stream.readSignedByte();
				else if (opcode >= 30 && opcode < 39) {
					if (actions == null)
						actions = new String[5];
					actions[opcode - 30] = stream.readString();
					if (actions[opcode - 30].equalsIgnoreCase("hidden"))
						actions[opcode - 30] = null;
				} else if (opcode == 40) {
					int i1 = stream.readUnsignedByte();
					modifiedModelColors = new int[i1];
					originalModelColors = new int[i1];
					for (int i2 = 0; i2 < i1; i2++) {
						modifiedModelColors[i2] = stream.readUnsignedWord();
						originalModelColors[i2] = stream.readUnsignedWord();
					}
				} else if (opcode == 60)
					mapFunctionID = stream.readUnsignedWord();
				else if (opcode == 62)
					aBoolean751 = true;
				else if (opcode == 64)
					aBoolean779 = false;
				else if (opcode == 65)
					modelSizeX = stream.readUnsignedWord();
				else if (opcode == 66)
					modelSizeH = stream.readUnsignedWord();
				else if (opcode == 67)
					modelSizeY = stream.readUnsignedWord();
				else if (opcode == 68)
					mapSceneID = stream.readUnsignedWord();
				else if (opcode == 69)
					anInt768 = stream.readUnsignedByte();
				else if (opcode == 70)
					offsetX = stream.readSignedWord();
				else if (opcode == 71)
					offsetH = stream.readSignedWord();
				else if (opcode == 72)
					offsetY = stream.readSignedWord();
				else if (opcode == 73)
					aBoolean736 = true;
				else if (opcode == 74) {
					isSolidObject = true;
				} else {
					if (opcode != 75)
						continue;
					anInt760 = stream.readUnsignedByte();
				}
				continue label0;
			} while (opcode != 77);
			anInt774 = stream.readUnsignedWord();
			if (anInt774 == 65535)
				anInt774 = -1;
			configID = stream.readUnsignedWord();
			if (configID == 65535)
				configID = -1;
			int j1 = stream.readUnsignedByte();
			configObjectIDs = new int[j1 + 1];
			for (int j2 = 0; j2 <= j1; j2++) {
				configObjectIDs[j2] = stream.readUnsignedWord();
				if (configObjectIDs[j2] == 65535)
					configObjectIDs[j2] = -1;
			}

		} while (true);
		if (i == -1) {
			hasActions = objectModelIDs != null
					&& (anIntArray776 == null || anIntArray776[0] == 10);
			if (actions != null)
				hasActions = true;
		}
		if (isSolidObject) {
			isUnwalkable = false;
			aBoolean757 = false;
		}
		if (anInt760 == -1)
			anInt760 = isUnwalkable ? 1 : 0;
	}

	private ObjectDef() {
		type = -1;
	}

	public boolean aBoolean736;
	private byte brightness;
	private int offsetX;
	public String name;
	private int modelSizeY;
	private static final Model[] aModelArray741s = new Model[4];
	private byte contrast;
	public int anInt744;
	private int offsetH;
	public int mapFunctionID;
	private int[] originalModelColors;
	private int modelSizeX;
	public int configID;
	private boolean aBoolean751;
	public static boolean lowMem;
	private static Stream stream;
	public int type;
	private static int[] streamIndices;
	public boolean aBoolean757;
	public int mapSceneID;
	public int configObjectIDs[];
	private int anInt760;
	public int anInt761;
	public boolean adjustToTerrain;
	public boolean aBoolean764;
	public static Client clientInstance;
	private boolean isSolidObject;
	public boolean isUnwalkable;
	public int anInt768;
	private boolean aBoolean769;
	private static int cacheIndex;
	private int modelSizeH;
	public int[] objectModelIDs;
	public int anInt774;
	public int anInt775;
	public int[] anIntArray776;
	public byte description[];
	public boolean hasActions;
	public boolean aBoolean779;
	public static MRUNodes mruNodes2 = new MRUNodes(30);
	public int animationID;
	private static ObjectDef[] cache;
	private int offsetY;
	private int[] modifiedModelColors;
	public static MRUNodes mruNodes1 = new MRUNodes(500);
	public String actions[];

}
