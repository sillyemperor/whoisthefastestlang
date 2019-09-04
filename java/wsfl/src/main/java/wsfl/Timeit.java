package wsfl;

import java.util.concurrent.Callable;

public class Timeit {

	public static void timeit(Callable<Integer> f) throws Exception {
		long t = System.currentTimeMillis();
		Integer n = f.call();
		t = System.currentTimeMillis() - t;
		float mean = (n != null && n > 0) ? t / n : 0;
		System.out.println(f.getClass().getName() + " " + t + " " + mean);
	}
}
