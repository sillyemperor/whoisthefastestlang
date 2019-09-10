package wsfl;

public class Timeit {

	private String name;
	private int number;
	
	public String getName() {
		return name;
	}

	public int getNumber() {
		return number;
	}

	public void set(String name, int number) {
		this.name = name;
		this.number = number;
	}
	
	
	public static void timeit(TimeitCallable f) throws Exception {
		Timeit timeit = new Timeit();
		long t = System.currentTimeMillis();		
		f.call(timeit);
		t = System.currentTimeMillis() - t;
		int n = timeit.number;
		double mean = n > 0 ? (double)t / n : 0;
		System.out.println(timeit.name + " " + t + " " + mean);
	}
}
