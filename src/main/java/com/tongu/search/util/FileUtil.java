package com.tongu.search.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.FileCopyUtils;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

/**
 * 文件工具类
 * @author wangjf
 *
 */
@Slf4j
public class FileUtil {

	/**
	 * 检查目录是否存在，不存在则创建
	 * @param path
	 */
	public static void checkAndMakeDir(String path) {
		File file = new File(path);
		if(!file.exists()) {
			file.mkdirs();
		}
	}

	/**
	 * 获取扩展名(不含.)
	 *
	 * @author wangjf
	 * @date 2019年4月12日 下午6:07:28
	 * @param filename
	 * @return
	 */
	public static String getExtensionName(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length() - 1))) {
				return filename.substring(dot + 1);
			}
		}
		return "";
	}

	/**
	 * 遍历目录下所有文件
	 *
	 * @author wangjf
	 * @date 2019年4月26日 下午5:13:02
	 * @param path
	 * @param fileList
	 */
	public static void traverseFolder(String path, List<File> fileList) {
		File file = new File(path);
		if (file.exists()) {
			File[] files = file.listFiles();
			if (null == files || files.length == 0) {
				return;
			} else {
				for (File file2 : files) {
					if (file2.isDirectory()) {
						traverseFolder(file2.getAbsolutePath(), fileList);
					} else {
						fileList.add(file2);
					}
				}
			}
		}
	}

	/**
	 *   清空目录
	 *
	 * @author  wangjf
	 * @date    2019年5月10日 上午9:44:17
	 * @param path
	 */
	public static void clearFolder(String path) {
		File file = new File(path);
		if (file.exists()) {
			if(file.isDirectory()) {
				File[] files = file.listFiles();
				Stream.of(files).forEach(f->{
					clearFolder(f.getAbsolutePath());
				});
			}
			file.delete();
		}
	}

	/**
	 * 文件拷贝
	 * @param source
	 * @param dest
	 * @throws IOException
	 */
	public static void copyFile(File source, File dest){
		try {
			FileChannel inputChannel = null;
			FileChannel outputChannel = null;
			try {
				inputChannel = new FileInputStream(source).getChannel();
				outputChannel = new FileOutputStream(dest).getChannel();
				outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
			} finally {
				inputChannel.close();
				outputChannel.close();
			}
		} catch (IOException e) {
			log.error("拷贝文件失败", e);
		}
	}

	/**
	 * 拷贝文件
	 * @param source
	 * @param dest
	 */
	public static void copyFile(InputStream source, File dest){
		try {
			FileCopyUtils.copy(source, new FileOutputStream(dest));
		}  catch (IOException e) {
			log.error("拷贝文件失败", e);
		}
	}

	/**
	 * 生成文件名称
	 * @param fileName 原来文件名称
	 * @param url 存储路径
	 * @param createDate 文件创建时间
	 * @return
	 */
	public static String generateFileName(String fileName, String url, Date createDate) {
		// 如果文件名称中含有扩展名，则删除扩展名，使用存储路径中的扩展名
		int dot = fileName.lastIndexOf('.');
		if(dot != -1) {
			fileName = fileName.substring(0, dot);
		}
		return fileName + "_" + String.valueOf(createDate.getTime()) + "." + FileUtil.getExtensionName(url);
	}
}
