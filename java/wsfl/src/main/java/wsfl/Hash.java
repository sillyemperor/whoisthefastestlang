package wsfl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.stream.IntStream;

import javax.crypto.NoSuchPaddingException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;

public class Hash {

	public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException, FileNotFoundException, IOException, InvalidKeyException {
		String s = IOUtils.toString(new FileInputStream("../../data/xyj.txt"));
		final byte[] data = s.getBytes();
		int n = 1000;
		
		{
			long t = System.currentTimeMillis();
			System.out.println("Synchronize");
			IntStream.range(0, n).forEach(i -> {
				byte[] unuse = DigestUtils.sha256(data);
			});
			System.out.println(System.currentTimeMillis() - t);
		}
		
		{	
			long t = System.currentTimeMillis();
			System.out.println("Multiple Thread");
			IntStream.range(0, n).parallel().forEach(i -> {
				byte[] unuse = DigestUtils.sha256(data);
			});
			System.out.println(System.currentTimeMillis() - t);
		}
	}

}
