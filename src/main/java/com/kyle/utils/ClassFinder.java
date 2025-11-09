/**
 * Kyle-Soft All rights reserved.
 * @author Kyle.Zhang
 */
package com.kyle.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * To find and locate the jar files which contains the specified java class.
 *
 */
public class ClassFinder {

	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("""
				Usage: 
					java -jar build/libs/Kyle-ClassFinder-1.0.jar [class full name] [jars' folder path]
					java -jar build/libs/Kyle-ClassFinder-1.0.jar [Filename.class] [jars' folder path]
				""");
			System.exit(0);
		}
		
		String classFullname = args[0];
		String classFilename = classFullname;
		if (classFilename.endsWith(".class")) {
			System.out.println("Searching class file " + classFilename + " ...");
			
			Collection<File> jarFiles = FileUtils.listFiles(new File(args[1]), new String[]{"jar"}, true);
			List<File> tldFiles = new ArrayList<File>();
			
			for (File jarFile:jarFiles) {
				try (ZipFile zipFile = new ZipFile(jarFile)) {
					Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
					while (zipEntries.hasMoreElements()) {
						ZipEntry zipEntry = zipEntries.nextElement();
						if (!zipEntry.isDirectory() && zipEntry.getName().endsWith(classFilename)) {								
							System.out.println("[" + zipEntry.getName() + "]");
							System.out.println("Jar found: " + jarFile.getAbsolutePath() + "[" + FileUtils.byteCountToDisplaySize(jarFile.length()) + "]");
							tldFiles.add(jarFile);
							break;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}	
			}
			System.out.println("Class name " + classFilename + " found in " + tldFiles.size() + " jars.");
		} else {		
			String classPathname = RegExUtils.replaceAll(classFullname, "\\.", "/");
			System.out.println("Searching class " + classFullname + " ...");
			Collection<File> jarFiles = FileUtils.listFiles(new File(args[1]), new String[]{"jar"}, true);
			List<File> tldFiles = new ArrayList<File>();
			
			for (File jarFile:jarFiles) {
				try (ZipFile zipFile = new ZipFile(jarFile)) {
					Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
					while (zipEntries.hasMoreElements()) {
						ZipEntry zipEntry = zipEntries.nextElement();
						if (!zipEntry.isDirectory() && zipEntry.getName().endsWith(".class")
							&& StringUtils.equals(zipEntry.getName(), classPathname)) {
								
							System.out.println("Jar found: " + jarFile.getAbsolutePath() + "[" + FileUtils.byteCountToDisplaySize(jarFile.length()) + "]");
							tldFiles.add(jarFile);
							break;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}	
			}

			/*System.out.println("jars contains tlds: ");
			for (File jarFile : tldFiles) {
				System.out.println(jarFile.getName());
			}*/
			System.out.println("Class " + classFullname + " found in " + tldFiles.size() + " jars.");
		}
	}
}
