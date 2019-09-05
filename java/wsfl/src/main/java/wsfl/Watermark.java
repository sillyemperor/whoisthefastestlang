package wsfl;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.stream.IntStream;

import javax.imageio.ImageIO;

public class Watermark {
	
	private int count = 1000;
	
	private void doWatermark() throws IOException {
		
		BufferedImage front = ImageIO.read(new File("../../data/lena512color.jpg"));
		BufferedImage stamp = ImageIO.read(new File("../../data/stamp.png"));
		
		BufferedImage stampResized = new BufferedImage(front.getWidth(), front.getHeight(), stamp.getType());
		Graphics2D gResized = stampResized.createGraphics();
		gResized.drawImage(stamp, 0, 0, front.getWidth(), front.getHeight(), null);
		gResized.dispose();
		
//		ImageIO.write(stampResized, "PNG", new File("tmp.png"));
		
		Graphics2D g = front.createGraphics();
		g.drawImage(stampResized, 0, 0, null);
		g.dispose();
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		ImageIO.write(front, "JPEG", out);
		
		byte[] unuse = out.toByteArray();
	}
	
	public void synchronize(Timeit ti) {
		IntStream.range(0, count).forEach(i -> { 
			try {
				this.doWatermark();
			} catch (IOException e) {
				e.printStackTrace();
			} 
		} );
		ti.set("顺序", count);
	}
	
	public void multipleThread(Timeit ti) {
		IntStream.range(0, count).parallel().forEach(i -> { 
			try {
				this.doWatermark();
			} catch (IOException e) {
				e.printStackTrace();
			} 
		} );
		ti.set("多线程", count);
	}

	public static void main(String[] args) throws Exception {
		
		Watermark h = new Watermark();
		
		Timeit.timeit(h::synchronize);
		Timeit.timeit(h::multipleThread);
		
	}

}
