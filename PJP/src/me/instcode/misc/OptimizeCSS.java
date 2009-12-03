package me.instcode.misc;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OptimizeCSS {
	private static final Pattern CLASS_ATTRIBUTE_PATTERN = Pattern.compile("class=\"([^\"]*)\"");
	private static final Pattern CSS_PATTERN = Pattern.compile("([^\\{]*)(\\{[^\\}]*\\})");
	
	Map<String, String> classes = new ConcurrentHashMap<String, String>();
	Map<String, String> styles = new ConcurrentHashMap<String, String>();
	Map<String, StringBuilder> map = new ConcurrentHashMap<String, StringBuilder>();
	
	void optimize(File htmlFile, File cssFile) throws IOException {
		readCSS(cssFile);
		readHTML(htmlFile);
		findCSS(htmlFile);
	}

	private void readCSS(File cssFile) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(cssFile));
		String line = null;
		while ((line = reader.readLine()) != null) {
			Matcher matcher = CSS_PATTERN.matcher(line);
			while (matcher.find()) {
				String selectors = matcher.group(1);
				String[] selector = selectors.split(",");
				for (String css: selector) {
					styles.put(css, matcher.group(2));
				}
			}
		}
	}
	
	private void readHTML(File htmlFile) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(htmlFile));
		String line;
		while ((line = reader.readLine()) != null) {
			Matcher matcher = CLASS_ATTRIBUTE_PATTERN.matcher(line);
			while (matcher.find()) {
				String[] clazzes = matcher.group(1).split(" ");
				for (String clazz : clazzes) {
					classes.put(clazz, clazz);
				}
			}
		}
	}
	
	private void findCSS(File htmlFile) throws IOException {
		Map<String, List<String>> groups = new HashMap<String, List<String>>();
		Iterator<String> iterator = styles.keySet().iterator();
		while (iterator.hasNext()) {
			String css = iterator.next();
			String[] clazzes = css.split("[. ]");
			boolean all = true;
			for (String clazz : clazzes) {
				if (clazz.length() > 0 && !classes.containsKey(clazz)) {
					all = false;
					break;
				}
			}
			if (all) {
				String value = styles.get(css);
				List<String> group = groups.get(value);
				if (group == null) {
					group = new ArrayList<String>();
					groups.put(value, group);
				}
				group.add(css);
			}
		}
		
		SortedSet<String> set = new TreeSet<String>();
		for (List<String> group : groups.values()) {
			String rep = group.get(0);
			StringBuilder builder = new StringBuilder(rep);
			for (int i = 1; i < group.size(); i++) {
				builder.append(", " + group.get(i));
			}
			builder.append(" " + styles.get(rep));
			set.add(builder.toString());
		}
		for (Object css : set.toArray()) {
			System.out.println(css);
		}
	}

	public static void main(String[] args) throws IOException {
		new OptimizeCSS().optimize(
				new File("input.html"),
				new File("style.css"));
	}
}
