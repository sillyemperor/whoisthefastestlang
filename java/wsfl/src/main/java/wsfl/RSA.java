package wsfl;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.stream.IntStream;

import javax.crypto.Cipher;

public class RSA {
	
	private byte[] data;
	private int count;
	
	RSA() throws Exception {
		count = 1000;
		data = "看别人不顺眼，是自己修养不够。".getBytes("UTF-8");
	}
	
	public void synchronize(Timeit ti) throws Exception {
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		kpg.initialize(2048);
		KeyPair kp = kpg.genKeyPair();
		Key publicKey = kp.getPublic();
		Key privateKey = kp.getPrivate();
		
		IntStream.range(0, count).forEach(i -> {
			try {
				Cipher enCipher = Cipher.getInstance("RSA");
				enCipher.init(Cipher.ENCRYPT_MODE, publicKey);
				Cipher deCipher = Cipher.getInstance("RSA");
				deCipher.init(Cipher.DECRYPT_MODE, privateKey);
				
				byte[] encoded = enCipher.doFinal(data);
				byte[] unuse = deCipher.doFinal(encoded);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		});
		
		ti.set("顺序", count);
	}
	
	public void multipleThread(Timeit ti) throws Exception {
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		kpg.initialize(2048);
		KeyPair kp = kpg.genKeyPair();
		Key publicKey = kp.getPublic();
		Key privateKey = kp.getPrivate();
		
		
		IntStream.range(0, count).parallel().forEach(i -> {
			try {
				Cipher enCipher = Cipher.getInstance("RSA");
				enCipher.init(Cipher.ENCRYPT_MODE, publicKey);
				Cipher deCipher = Cipher.getInstance("RSA");
				deCipher.init(Cipher.DECRYPT_MODE, privateKey);
				
				byte[] encoded = enCipher.doFinal(data);
				byte[] unuse = deCipher.doFinal(encoded);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		});
		
		ti.set("多线程", count);
	}

	public static void main(String[] args) throws Exception {
		RSA h = new RSA();
		
		Timeit.timeit(h::synchronize);
		Timeit.timeit(h::multipleThread);
	}

}
