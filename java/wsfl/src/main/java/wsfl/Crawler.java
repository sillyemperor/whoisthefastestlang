package wsfl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import io.reactivex.Observable;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.client.WebClient;

@SuppressWarnings("unused")
public class Crawler {
	
	private List<String> links;
	private File linksFile;
	
	@SuppressWarnings("unchecked")
	Crawler() throws FileNotFoundException, IOException {
		linksFile = new File("../../data/links.txt");
		links = IOUtils.readLines(new FileInputStream(linksFile));
//		links.clear();
	}
	
	public void synchronize(Timeit ti) {
		links.forEach(i -> {
			try {
				URL url = new URL(i);
//				System.out.println(url);
				try (InputStream io = url.openStream()) {
					byte[] unused = IOUtils.toByteArray(io);
//					System.out.println(data[0]);
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		});
		ti.set("顺序", links.size());
	}
	
	public void multipleThread(Timeit ti) {
		links.parallelStream().forEach(i -> {
			try {
				URL url = new URL(i);
//				System.out.println(url);
				try (InputStream io = url.openStream()) {
					byte[] unused = IOUtils.toByteArray(io);
//					System.out.println(unused.length);
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		});
		ti.set("多线程", links.size());
	}
	
	public void httpAsyncClients1(Timeit ti) throws IOException {
		try (CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault()) {
			httpclient.start();

			links.stream().map(url -> httpclient.execute(new HttpGet(url), null)).forEach(f -> {
				try {
					org.apache.http.HttpResponse resp = f.get();
					if (resp.getStatusLine().getStatusCode() > 300) {
						throw new Exception();
					}
					byte[] unused = IOUtils.toByteArray(resp.getEntity().getContent());
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		}
		ti.set("Apache异步Client1", links.size());
	}
	
	public void httpAsyncClients2(Timeit ti) throws Exception {
		try (CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault()) {
			httpclient.start();

			final CountDownLatch latch = new CountDownLatch(links.size());

			links.forEach(url -> {
				httpclient.execute(new HttpGet(url), new FutureCallback<HttpResponse>() {

					@Override
					public void failed(Exception ex) {
						// TODO Auto-generated method stub

					}

					@Override
					public void completed(HttpResponse result) {

						try {
							if (result.getStatusLine().getStatusCode() > 300) {
								throw new Exception();
							}
							byte[] unused = IOUtils.toByteArray(result.getEntity().getContent());
						} catch (Exception e) {
							e.printStackTrace();
						}
						latch.countDown();
					}

					@Override
					public void cancelled() {
						// TODO Auto-generated method stub

					}
				});
			});

			latch.await();

		}
		ti.set("Apache异步Client2", links.size());
	}
	
	public void httpAsyncClients3(Timeit ti) throws Exception {
		try (CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault()) {
			httpclient.start();

			links.stream().map(url -> httpclient.execute(new HttpGet(url), null)).collect(Collectors.toList())
					.forEach(f -> {
						try {
							org.apache.http.HttpResponse resp = f.get();
							if (resp.getStatusLine().getStatusCode() > 300) {
								throw new Exception();
							}
							byte[] unused = IOUtils.toByteArray(resp.getEntity().getContent());
						} catch (Exception e) {
							e.printStackTrace();
						}
					});
		}
		
		ti.set("Apache异步Client3", links.size());
	}
	
	public void vertx1(Timeit ti) throws InterruptedException {

		Vertx vertx = Vertx.vertx();
		WebClient client = WebClient.create(vertx);
		final CountDownLatch latch = new CountDownLatch(links.size());
		links.forEach(url -> {
			client.getAbs(url).send(ar -> {
				if (ar.succeeded()) {
					io.vertx.reactivex.ext.web.client.HttpResponse<io.vertx.reactivex.core.buffer.Buffer> response = ar
							.result();
					byte[] unused = response.body().getBytes();
					latch.countDown();
				} else {
					System.out.println("Something went wrong " + ar.cause().getMessage());
				}

			});
		});

		latch.await();
		vertx.close();
		ti.set("Vertx1", links.size());
	}
	
	public void vertx2(Timeit ti) throws InterruptedException {

		Vertx vertx = Vertx.vertx();
		WebClient client = WebClient.create(vertx);
		final CountDownLatch latch = new CountDownLatch(links.size());
		links.forEach(url -> {
			client.getAbs(url).rxSend()
				.map(io.vertx.reactivex.ext.web.client.HttpResponse::bodyAsBuffer)
				.subscribe(buff -> {
					byte[] unused = buff.getBytes();
					latch.countDown();
				}, error -> {
					System.out.println("Something went wrong " + error.getMessage());
				});
		});

		latch.await();
		vertx.close();
		ti.set("Vertx2", links.size());
	}
	
	public void vertx3(Timeit ti) throws InterruptedException {

		Vertx vertx = Vertx.vertx();
		WebClient client = WebClient.create(vertx);
		final CountDownLatch latch = new CountDownLatch(links.size());
		Observable.fromIterable(links)
			.map(url -> client.getAbs(url).rxSend().map(io.vertx.reactivex.ext.web.client.HttpResponse::bodyAsBuffer))
			.subscribe(ar -> {
				ar.subscribe(buff -> {
					
					byte[] unused = buff.getBytes();
					latch.countDown();
				});
			});
		latch.await();
		vertx.close();
		ti.set("Vertx3", links.size());
	}
	
	public void vertx4(Timeit ti) throws InterruptedException {

		Vertx vertx = Vertx.vertx();
		WebClient client = WebClient.create(vertx);
		final CountDownLatch latch = new CountDownLatch(links.size());
		Observable.<String>fromPublisher(s -> {
			try(BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(linksFile)))) {
				r.lines().forEach(s::onNext);
			} catch(Exception e) {
				s.onError(e);
			}
		})
			.map(url -> client.getAbs(url).rxSend().map(io.vertx.reactivex.ext.web.client.HttpResponse::bodyAsBuffer))
			.subscribe(ar -> {
				ar.subscribe(buff -> {
					byte[] unused = buff.getBytes();
					latch.countDown();
				});
			});
		latch.await();
		
		vertx.close();
		ti.set("Vertx4", links.size());
	}

	public static void main(String[] args) throws Exception {
		Crawler crawler = new Crawler();
		
		Timeit.timeit(crawler::synchronize);
		Timeit.timeit(crawler::multipleThread);
		Timeit.timeit(crawler::httpAsyncClients1);
		Timeit.timeit(crawler::httpAsyncClients2);
		Timeit.timeit(crawler::httpAsyncClients3);
		Timeit.timeit(crawler::vertx1);
		Timeit.timeit(crawler::vertx2);
		Timeit.timeit(crawler::vertx3);
		Timeit.timeit(crawler::vertx4);

	}
}
