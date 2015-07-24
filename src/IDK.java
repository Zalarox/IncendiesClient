package src;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import src.sign.signlink;

// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 


public final class IDK {

    public static void unpackConfig(StreamLoader streamLoader) {
    	Stream stream = new Stream(streamLoader.getDataForName("idk.dat"));
        length = stream.readUnsignedWord();
        if (cache == null)
            cache = new IDK[length+newIndex];
        for (int j = 0; j < length; j++) {
            if (cache[j] == null)
                cache[j] = new IDK();
            cache[j].readValues(stream);
			anIntArray659[0] = 55232;
			anIntArray660[0] = 6798; 
        }
        for (int j = 0; j < newIndex; j++){
            if (cache[j+length] == null)
                cache[j+length] = new IDK();
            cache[j+length].readValues(j);
        }
        writeValues(streamLoader);
    }
    
	private void readValues(Stream stream)
	{
		do
		{
			int i = stream.readUnsignedByte();
			if(i == 0)
				return;
			if(i == 1)
				anInt657 = stream.readUnsignedByte();
			else
			if(i == 2)
			{
				int j = stream.readUnsignedByte();
				anIntArray658 = new int[j];
				for(int k = 0; k < j; k++)
					anIntArray658[k] = stream.readUnsignedWord();

			} else
			if(i == 3)
				aBoolean662 = true;
			else
			if(i >= 40 && i < 50)
				anIntArray659[i - 40] = stream.readUnsignedWord();
			else
			if(i >= 50 && i < 60)
				anIntArray660[i - 50] = stream.readUnsignedWord();
			else
			if(i >= 60 && i < 70)
				anIntArray661[i - 60] = stream.readUnsignedWord();
			else
				System.out.println("Error unrecognised config code: " + i);
		} while(true);
	}
	
    private static void writeValues(StreamLoader streamLoader) {
    	Stream stream = new Stream(streamLoader.getDataForName("idk.dat"));
        try {
			stream.writeWord(cache.length);
			for (int i = 0; i < cache.length; i++) {
				IDK IdentityKit = cache[i];
				stream.writeBytes(IdentityKit.randomVal1);
				if (IdentityKit.randomVal1 == 0) {
					return;
				}
				if (IdentityKit.randomVal1 == 1) {
					stream.writeBytes(IdentityKit.anInt657);
				} else if (IdentityKit.randomVal1 == 2) {
					stream.writeBytes(IdentityKit.randomVal2);
					for (int k = 0; k < IdentityKit.randomVal2; k++) {
						stream.writeWord(IdentityKit.anIntArray658[k]);
					}
				} else if (IdentityKit.randomVal1 >= 40 && IdentityKit.randomVal1 < 50) {
					stream.writeWord(IdentityKit.anIntArray659[IdentityKit.randomVal1-40]);
				} else if (IdentityKit.randomVal1 >= 50 && IdentityKit.randomVal1 < 60) {
					stream.writeWord(IdentityKit.anIntArray660[IdentityKit.randomVal1-50]);
				} else if (IdentityKit.randomVal1 >= 60 && IdentityKit.randomVal1 < 70) {
					stream.writeWord(anIntArray661[IdentityKit.randomVal1-60]);
				}
			}
        } catch (Exception e) {
        }
    }
	
	public int 
		MALE_HEAD = 0,
		MALE_JAW = 1,
		MALE_TORSO = 2,
		MALE_ARMS = 3,
		MALE_HANDS = 4,
		MALE_LEGS = 5,
		MALE_FEET = 6,
		FEMALE_HEAD = 7,
		FEMALE_JAW = 8,
		FEMALE_TORSO = 9,
		FEMALE_ARMS = 10,
		FEMALE_HANFS = 11,
		FEMALE_LEGS = 12,
		FEMALE_FEET = 13;
		
	public void readValues(int i) {
        switch (i) {
            
         }
    }
	
    public boolean method537() {
        if (anIntArray658 == null)
            return true;
        boolean flag = true;
        for (int j = 0; j < anIntArray658.length; j++)
            if (!Model.method463(anIntArray658[j]))
                flag = false;
        return flag;
    }

    public Model method538() {
        if (anIntArray658 == null)
            return null;
        Model aclass30_sub2_sub4_sub6s[] = new Model[anIntArray658.length];
        for (int i = 0; i < anIntArray658.length; i++)
            aclass30_sub2_sub4_sub6s[i] = Model.method462(anIntArray658[i]);
        Model model;
        if (aclass30_sub2_sub4_sub6s.length == 1)
            model = aclass30_sub2_sub4_sub6s[0];
        else
            model = new Model(aclass30_sub2_sub4_sub6s.length, aclass30_sub2_sub4_sub6s);
        for (int j = 0; j < 6; j++) {
            if (anIntArray659[j] == 0)
                break;
            model.method476(anIntArray659[j], anIntArray660[j]);
        }
        return model;
    }

    public boolean method539() {
        boolean flag1 = true;
        for (int i = 0; i < 5; i++)
            if (anIntArray661[i] != -1 && !Model.method463(anIntArray661[i]))
                flag1 = false;
        return flag1;
    }

    public Model method540() {
        Model aclass30_sub2_sub4_sub6s[] = new Model[5];
        int j = 0;
        for (int k = 0; k < 5; k++)
            if (anIntArray661[k] != -1)
                aclass30_sub2_sub4_sub6s[j++] = Model.method462(anIntArray661[k]);
        Model model = new Model(j, aclass30_sub2_sub4_sub6s);
        for (int l = 0; l < 6; l++) {
            if (anIntArray659[l] == 0)
                break;
            model.method476(anIntArray659[l], anIntArray660[l]);
        }
        return model;
    }
	
	private IDK() {
		anInt657 = -1;
		anIntArray659 = new int[6];
		anIntArray660 = new int[6];
		aBoolean662 = false;
	}
	
	public static int newIndex = 0;
    public int randomVal1;
    public int randomVal2;
	public static int length;
	public static IDK cache[];
	public int anInt657;//partId
	private int[] anIntArray658;//models
	private static int[] anIntArray659;//origMdlCol
	private static int[] anIntArray660;//newMdlCol
	private static final int[] anIntArray661 = {//headMdl
		-1, -1, -1, -1, -1
	};
	public boolean aBoolean662;//noInterface
	
}