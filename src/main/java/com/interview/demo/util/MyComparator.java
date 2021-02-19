package com.interview.demo.util;

import java.util.Comparator;

import com.interview.demo.entity.Vendor;

public class MyComparator implements Comparator<Vendor> {

	@Override
	public int compare(Vendor v1, Vendor v2) {		
		return -v1.getName().compareTo(v2.getName());
	}	
	

}
