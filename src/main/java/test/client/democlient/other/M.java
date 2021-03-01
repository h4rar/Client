package test.client.democlient.other;

import java.io.*;
import java.net.*;

public class M {
	public static void main(String[] args) throws InterruptedException, IOException, URISyntaxException {
		String PATH = "C:\\Work\\SDF\\ServerFiles\\testResponse1.mp4";

		String url = "http://localhost:8080/video/streamtest/mp4/toystory";
		String url2 = "http://localhost:8080/video/streamtest/mp4";

		WebClient webClient=new WebClient();
		byte[] bytes = webClient.download(
			url2,
			262_144154);
		try (FileOutputStream stream = new FileOutputStream(PATH)) {
			stream.write(bytes);
		}
		System.out.println("end");
	}
}
