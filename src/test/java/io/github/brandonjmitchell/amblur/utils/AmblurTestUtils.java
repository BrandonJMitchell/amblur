package io.github.brandonjmitchell.amblur.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.core.io.ClassPathResource;

public class AmblurTestUtils {

	private AmblurTestUtils() {}
	
	public static String collectLines(List<String> list) {
		StringBuilder builder = new StringBuilder();
		if (list != null && !list.isEmpty()) {
			list.forEach(builder::append);
		}
		return builder.toString();
	}
	
	public static String retrieveContent(String path, String fileName) {
		String content = null;
		try {
			ClassPathResource pathResource = new ClassPathResource (path + File.separator + fileName);
			List<String> lines = Files.readAllLines(Paths.get(pathResource.getPath()));
			content = collectLines(lines);
		} catch (IOException e){
			
		}
		return content;
	}
}
