package clocks;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Random;

public class KeyGen 
{
	private String seedValue;
	private MessageDigest digest;
	
	public KeyGen()
	{
		seedValue = generateSeed();
		try
		{
			digest = MessageDigest.getInstance("SHA-256");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public KeyGen(String seedValue)
	{
		if(seedValue != null)
		{
			this.seedValue = seedValue;
		}
		else
		{
			this.seedValue = generateSeed();
		}
		try
		{
			digest = MessageDigest.getInstance("SHA-256");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public byte[] getHash(String clockValue)
	{
		if(seedValue != null) {
			return digest.digest((seedValue + clockValue).getBytes((StandardCharsets.UTF_8)));
		}
		return null;
	}
	
	public boolean correctKey(byte[] key1, byte[] key2)
	{
		return Arrays.equals(key1, key2);
	}
	
	public String generateSeed()
	{
		String allowedCharacters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 ?!@#$%^&*()-=+_[]{}|/";
		StringBuilder seed = new StringBuilder();
		Random rand = new Random();
		
		for(int i = 0; i < 256; i++)
		{
			seed.append(allowedCharacters.charAt(rand.nextInt(allowedCharacters.length())));
		}
		return seed.toString();
	}
	
	public String getSeed() {
		return seedValue;
	}

}
