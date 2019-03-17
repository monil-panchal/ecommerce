package com.ecommerce.util;

import java.security.SecureRandom;

import org.springframework.stereotype.Service;

@Service
public class IdGenerator {

	private static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	private static SecureRandom rnd = new SecureRandom();

	public String randomString(String item) {
		StringBuilder sb = new StringBuilder(12);
		sb.append(item + "-");
		for (int i = 0; i < 12; i++)
			sb.append(AB.charAt(rnd.nextInt(AB.length())));
		return sb.toString();
	}

}
