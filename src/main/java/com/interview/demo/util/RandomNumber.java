package com.interview.demo.util;

public class RandomNumber {
	public static String randomGenerator() {
		long min= 100000;
		long max= 200000;
		
		long randNum = (long)(Math.random()*(max-min+1)+min); 
		
		return Long.toString(randNum);
	}
}
