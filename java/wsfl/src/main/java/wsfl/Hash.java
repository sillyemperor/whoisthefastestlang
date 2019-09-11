package wsfl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.stream.IntStream;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;

public class Hash {
	
	private byte[] data;
	private int count;
	
	Hash() throws FileNotFoundException, IOException {
		String s = IOUtils.toString(new FileInputStream("../../data/xyj.txt"));
		data = s.getBytes();
		count = 1000;
	}
	
	public void synchronize(Timeit ti) {
		IntStream.range(0, count).forEach(i -> {
			byte[] unuse = DigestUtils.sha256(data);
		});
		
		ti.set("顺序", count);
	}
	
	public void multipleThread(Timeit ti) {
		IntStream.range(0, count).parallel().forEach(i -> {
			byte[] unuse = DigestUtils.sha256(data);
		});
		ti.set("多线程", count);
	}

	public static void main(String[] args) throws Exception {
		Hash h = new Hash();
		
		Timeit.timeit(h::synchronize);
		Timeit.timeit(h::multipleThread);
	}

}
