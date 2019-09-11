package wsfl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Json {
	
	private String big;
	private ObjectMapper mapper = new ObjectMapper();
	private int count = 100000;
	
	Json() throws FileNotFoundException, IOException {
		big = IOUtils.toString(new FileInputStream("../../data/big.json"));
	}

	public void synchronize(Timeit ti) {
		IntStream.range(0, count).forEach(i -> {
			try {
				List<Object> obj = mapper.readValue(big, List.class);
				String unuse = mapper.writeValueAsString(obj);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		ti.set("顺序", count);
	}
	
	public void multipleThread(Timeit ti) {
		IntStream.range(0, count).parallel().forEach(i -> {
			try {
				List<Object> obj = mapper.readValue(big, List.class);
				String unuse = mapper.writeValueAsString(obj);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		ti.set("多线程", count);
	}
	
	public static void main(String[] args) throws Exception {
		Json h = new Json();
		
		Timeit.timeit(h::synchronize);
		Timeit.timeit(h::multipleThread);
	}

}
