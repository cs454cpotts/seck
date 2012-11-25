package com.pcwerk.seck.crawler;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

import org.apache.tika.sax.Link;

import com.pcwerk.seck.extractor.Extractor;
import com.pcwerk.seck.extractor.ExtractorFactory;
import com.pcwerk.seck.store.WebDocument;

public class FileManager {
	public static File directory;
	public static File masterFile;
	public static Queue<Queue<String>> queueMaster = new LinkedList<Queue<String>>();
	public static File htmlFile;
	public static ArrayList<String> readFile(String fileName) 
	throws IOException {
		ArrayList<String> arrayOfStrings = new ArrayList<String>();
		try {		

			File fin = new File(fileName);

			Scanner scanner = new Scanner(fin);
			
			while (scanner.hasNext()) {
				arrayOfStrings.add(scanner.nextLine());
			}
			
			scanner.close();
			
		} catch (Exception e) {
			String msg = "File read error: filename = " + fileName;
			throw new IOException(msg);
		}

		return arrayOfStrings;
	}
	
	public static ArrayList<Integer> readMasterFile(String fileName) {
		ArrayList<Integer> hashes = new ArrayList<Integer>();
		try {		
			File fin = new File(fileName);

			Scanner scanner = new Scanner(fin);
			
			while (scanner.hasNext()) {
				hashes.add(Integer.parseInt(scanner.nextLine()));
			}
			
			scanner.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return hashes;
	}
	
	public static void writeFile(String fileName, String[] array) {
		File fout = new File(fileName);

		try {
			String websiteName = array[array.length - 1];
			
			PrintWriter out = new PrintWriter(fout);
			out.println(websiteName);
			
			for(int i = 0; i < array.length - 2; i++){
				out.println(array[i]);
			}
			
//			for (String line : array) {
//				out.println(line);
//			}
//			
			out.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void writeFile(String fileName, int hashCode) {
		System.out.println("Place to write to: " + fileName);
		try {

			ArrayList<String> oldList = readFile(fileName);
			System.out.println("New Update to MasterFile:");
			for(String line : oldList){
				System.out.println(line);
			}
			File fout = new File(fileName);
			PrintWriter out = new PrintWriter(fout);
			oldList.add(hashCode + "");
			for(String line : oldList){
				out.println(line);
			}

			
			out.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String[]	getInfo(){
		return null;
	}

	public static synchronized boolean isCrawled(String fileName, String masterFileLocation){
		
		ArrayList<Integer> hashes = new ArrayList<Integer>();
		hashes = FileManager.readMasterFile(masterFileLocation);
		
		if(hashes.contains(fileName.hashCode())) return true;
		else return false;

	}
	
	public static synchronized void saveHash(int root){
		//System.out.println(masterFile + " this is the masterFile toString");
		String stringFile = masterFile.toString().replace("\\", "/");
		//System.out.println(stringFile + " this is the stringFile");
		FileManager.writeFile(stringFile, root);
	}
	
	public static synchronized void saveData(String fileName, HashSet<String> data)	{
		//System.out.println(directory.getPath());
		String localName = directory.getPath().concat("/" + fileName.hashCode() + ".txt");
		//System.out.println(localName);

		String[] array = new String[data.size() + 1];
		data.toArray(array).toString();
		array[array.length - 1] = fileName;
		FileManager.writeFile(localName, array);
	}
	
	public static HashSet<String> extractData(String root, File file){
		ExtractorFactory eFactory = new ExtractorFactory();
		Extractor ex = eFactory.getExtractor(file);
		WebDocument webDoc = new WebDocument();
		try {
			webDoc = ex.extract(new URL(root));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		HashSet<String> temp = new HashSet<String>();
		if(webDoc.isHTML()){
			List<Link> links = webDoc.getLinks();
			for(Link link : links){
				//System.out.println(link.getUri());
				temp.add(link.getUri());
			}
		}
		return temp;
	}
	
	public static void setDirectory(String directoryIn){
		String path = directoryIn;
		//System.out.println("In set directory " + path);
		String[] pathArray = path.split("/");
		String newDirectory = new String();
		for(int i = 0; i < pathArray.length - 1; i++){
			newDirectory = newDirectory.concat(pathArray[i] + "/");
		}
		//System.out.println(newDirectory);
		directory = new File(newDirectory);
		masterFile = new File(directoryIn);
		if(!directory.exists())
			directory.mkdirs();
		
		if(!masterFile.exists())
			try {
				//System.out.println(masterFile + " is the masterFile");
				masterFile.createNewFile();
			} catch (IOException e) {

				e.printStackTrace();
			}
	}
	
	public static void printCrawlInfo(String masterFilePath){
		ArrayList<Integer> urlFileNames = readMasterFile(masterFilePath);
		
		System.out.println("The number of files currently crawled is: " + urlFileNames.size());
		System.out.println("The websites that have been crawled are: ");
		
		
		String[] directoryArray = masterFilePath.split("/");
		String directory = new String();
		
		for(int i = 0; i < directoryArray.length - 1; i++){
			directory = directory.concat(directoryArray[i] + "/");
		}
		
//		ArrayList<String> data;
//		try {
//			data = readFile(directory.concat("-102178447" + ".txt"));
//			System.out.println(data.get(0));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		for(Integer fileName : urlFileNames){
			try {
				ArrayList<String> data = readFile(directory.concat(fileName + ".txt"));
				Thread.sleep(10);
				//System.out.println(data.get(0));
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {

			}
		}
		
//		for(Integer fileName : urlFileNames){
//			try {
//				ArrayList<String> data = readFile(directory.concat(fileName + ".txt"));
//				for(int i = 0; i < data.size(); i++){
//					if(i == 0){
//						System.out.println(FileManager.tag("Data for website: " + data.get(i)));
//						System.out.println(FileManager.tag("Links And Anchors:"));
//					}
//					else if(data.get(i).equals("**body starts**")){
//						System.out.println(FileManager.tag("Body Information"));
//						
//					}
//					else
//						System.out.println(data.get(i));
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
	}
	
	public static String tag(String tag){
		int tagLength = tag.length();
		String stars = new String();
		for(int i = 0; i < tagLength; i++){
			stars = stars.concat("*");
		}
		stars = stars.concat("**");
		return new String(stars + "\n" + tag + " *" + "\n" + stars + "\n");
	}
	
	public static synchronized void updateQueueMaster(HashSet<String> set, int tc){
		Queue<String> queue = new LinkedList<String>();
		String[] array = new String[set.size()];
		set.toArray(array).toString();

			//System.out.println(set.toArray());
			for(int i = 0; i < array.length; i++){
				queue.add(array[i]);
			}
		//set.addAll(queue);
		int length = set.size();
		
		int mod = length % tc;

		for(int i = 0; i < tc; i++){
			Queue<String> newQueue = new LinkedList<String>();
			for(int j = (i) * (length / tc); j < (i + 1) * (length / tc) ; j++){
				//System.out.println(queue.peek());
				newQueue.add(queue.poll());
				
			}
			if(mod > 0){
				newQueue.add(queue.poll());
				mod--;
			}
			queueMaster.add(newQueue);
			System.out.println("new Queue size = " + newQueue.size());
		}
		//queueMaster.remove();
		System.out.println("Size of master is = " + queueMaster.size());
		for(Queue<String> n : queueMaster){
			//System.out.println("Queue begins here");
			for(String line : n){
				//System.out.println(line);
			}
		}
	}
	
	public static synchronized Queue<String> getQueue(){
		return queueMaster.poll();
	}
	
	public static void removeQueueHead(){
		queueMaster.remove();
	}
	
	public static void populate(String root, int tc){
		boolean invalidURL = false;
		URL website;
		String localName = directory.getPath().concat("/" + root.hashCode() + ".txt");
		try {
			website = new URL(root);
			InputStream inStr = website.openConnection().getInputStream();
		    BufferedInputStream bins = new BufferedInputStream(inStr);
		    FileOutputStream fos = new FileOutputStream(localName);
		    int c;
		    while((c = bins.read()) != -1){
		    	fos.write(c);
		    }
		    fos.close();
		    bins.close();
		    inStr.close();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Unable to connect to: " + root);
			invalidURL = true;
			
		} 
		if(!invalidURL){
		    File file = new File(localName);
			
			//FileManager.saveHash(root.hashCode());
		    
		    HashSet<String> linksSet = FileManager.extractData(root, file);
		    //System.out.println(root);
			FileManager.saveData(root, linksSet);
		    FileManager.updateQueueMaster(linksSet, tc);
		}
		else{
			//FileManager.saveHash(root.hashCode());
			HashSet<String> linksSet = new HashSet<String>();
			linksSet.add(root);
			FileManager.updateQueueMaster(linksSet, tc);
		}
	}
	
}
