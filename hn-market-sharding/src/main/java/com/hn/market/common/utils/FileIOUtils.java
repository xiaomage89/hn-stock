package com.hn.market.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.channels.FileChannel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class FileIOUtils {

	/**
	 * 创建文件
	 * 
	 * @throws IOException
	 */
	public  boolean creatTxtFile(String pathfile) throws IOException {
		boolean flag = false;
		String filenameTemp = pathfile + ".txt";
		File filename = new File(filenameTemp);
		File parentFile = filename.getParentFile();
		if(!parentFile.exists()) parentFile.mkdirs();
		if (!filename.exists()) {
			filename.createNewFile();
			flag = true;
		}
		return flag;
	}

	/**
	 * 创建文件
	 *
	 * @throws IOException
	 */
	public  String  creatTxtFile(String filepath,String filename) throws IOException {
		filename = filename + ".txt";
		File file = new File(filepath,filename);
		File parentFile = file.getParentFile();
		if(!parentFile.exists()) parentFile.mkdirs();
		if(!file.exists()){
			file.createNewFile();
		}
		return file.getAbsolutePath();
	}

	/**
	 * 读取文件夹下的文件
	 * @param path
	 * @return
	 */
	public List<String> readFileName(String path){
		List<String> fileNames= new ArrayList<String>();
		File file = new File(path);
		if(!file.exists()) return null;

		File[] files = file.listFiles();
		for(File fileV : files){
			if(fileV.isDirectory()){
				readFileName(fileV.getAbsolutePath());
			}else{
				String fileName = fileV.getName();
				if(fileName.endsWith(".txt")){
					fileNames.add(fileV.getAbsolutePath());
					if(fileNames.size()==10) break;
				}
			}
		}
		return fileNames;
	}

	/**
	 * 文件内容读取
	 * @param fileName
	 * @return
	 */
	public  List<String> readFileData(String fileName){
		List<String> fileValues= new ArrayList<String>();

		if(StringUtils.isBlank(fileName)) return null;

		try {
			FileReader fileReader = new FileReader(fileName);

			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String lineValue = "";
			while((lineValue = bufferedReader.readLine()) != null){
				fileValues.add(lineValue);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fileValues;
	}



	/**
	 * 文件重命名
	 * @param fileNameS
	 */
	public void reNameto(List<String> fileNameS) {
		for (String fileName : fileNameS) {
			File oldfile = new File(fileName);
			File newfile = new File(fileName + "--已处理");
			try {
				copyFileUsingFileChannels(oldfile, newfile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.gc();
		for (String fileName : fileNameS) {
			File oldfile = new File(fileName);
			oldfile.delete();
		}
	}

	/**
	 * 文件copy
	 * @param source
	 * @param dest
	 * @throws IOException
	 */
	public  void copyFileUsingFileChannels(File source, File dest) throws IOException {
		FileChannel inputChannel = null;
		FileChannel outputChannel = null;
		if(dest.exists()){
			dest.delete();
		}
		try {
			inputChannel = new FileInputStream(source).getChannel();
			outputChannel = new FileOutputStream(dest).getChannel();
			outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
		} finally {
			inputChannel.close();
			outputChannel.close();
		}
	}
}
