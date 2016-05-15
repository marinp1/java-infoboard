package fi.patrikmarin.weather;

import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;

public class GetFMIIcons {
	static ArrayList<Integer> numlist = new ArrayList<Integer>();
	static String URI = "http://cdn.fmi.fi/symbol-images/weather/";
	static String EXT = ".svg";
	
	public static void main(String args[]) {

		numlist.add(1);
		numlist.add(2);
		numlist.add(21);
		numlist.add(22);
		numlist.add(23);
		numlist.add(3);
		numlist.add(31);
		numlist.add(32);
		numlist.add(33);
		numlist.add(41);
		numlist.add(42);
		numlist.add(43);
		numlist.add(51);
		numlist.add(52);
		numlist.add(53);
		numlist.add(61);
		numlist.add(62);
		numlist.add(63);   
		numlist.add(64);     
		numlist.add(71);
		numlist.add(72);
		numlist.add(73);
		numlist.add(81);
		numlist.add(82);
		numlist.add(83);
		numlist.add(91);
		numlist.add(92);
		
		try {
			for (Integer j = 1; j < 2; j++) {
				for (Integer i : numlist) {
					String loc = generateURI(j, i);
					saveFile(loc,j,i);
					Thread.sleep(100);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	static String generateURI(int dayNight, int code) {
		return URI + dayNight + "/" + code + EXT;
	}
	
	static void saveFile(String LOC, int dayNight, int code) throws Exception {
		URL website = new URL(LOC);
		ReadableByteChannel rbc = Channels.newChannel(website.openStream());
		FileOutputStream fos = new FileOutputStream("images/" + dayNight + "_" + code + EXT);
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		fos.flush();
		fos.close();
		
		System.out.println("Saved file images/" + dayNight + "_" + code + EXT);
	}
}