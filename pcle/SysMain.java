package hexl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class SysMain {

	/**
	 * 原始文件名、新文件名分隔符
	 */
	private static String FILENAME_SPLITER = " >>> ";

	private static String FILES_INFO_DATA = "./READ.DAT";

	private static String FILENAME_STATE = "";
	// 状态常量
	private static final String FILENAME_OPEN_STATE = "OPEN";
	private static final String FILENAME_CLOSE_STATE = "CLOSE";

	private static ArrayList<String> fileNameListInFile = new ArrayList<String>();

	/**
	 * 程序入口
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		File dataFile = new File(FILES_INFO_DATA);
		if (dataFile.exists()) {
			loadFilesInfo(dataFile);
			dataFile.delete();
		} else {
			FILENAME_STATE = FILENAME_OPEN_STATE;
		}

		// 加密
		if (FILENAME_OPEN_STATE.equals(FILENAME_STATE)) {
			// if ("".length() != 0) {
			System.out.println("开始加密");
			List<File> files = listFiles("./");
			doRenTask(files);
			saveFileNames(FILES_INFO_DATA);
			// }
		}
		// 解密
		else if (FILENAME_CLOSE_STATE.equals(FILENAME_STATE)) {
			System.out.println("开始解密");
			restoreFiles();
			saveFileNames(FILES_INFO_DATA);
		} else {
			System.out.println("文件名状态不正常：" + FILENAME_STATE);
			System.exit(1);
		}
	}

	/**
	 * read已保存文件名信息
	 * 
	 * @param dir
	 */
	private static void loadFilesInfo(File dataFile) {
		BufferedReader reader = null;
		InputStreamReader isr = null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(dataFile);
			isr = new InputStreamReader(fis);
			reader = new BufferedReader(isr);
			String line = null;
			int i = 0;
			while ((line = reader.readLine()) != null) {
				if (0 == i) {
					i++;
					FILENAME_STATE = line.trim();
					continue;
				}
				fileNameListInFile.add(line.trim());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
				if (isr != null) {
					isr.close();
				}
				if (fis != null) {
					fis.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private static void restoreFiles() {
		try {
			for (String nameInfo : fileNameListInFile) {
				String nameArr[] = nameInfo.split(FILENAME_SPLITER);
				File newFile = new File("./" + nameArr[1]);
				if (newFile.exists()) {
					newFile.renameTo(new File("./" + nameArr[0]));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 生成文件名并完成重命名
	 * 
	 * @param files
	 */
	private static void doRenTask(List<File> files) {
		try {
			fileNameListInFile.clear();

			String suffix = Utils.randomLetterString(2);
			for (File f : files) {
				String srcFileName = f.getName();
				String newFileName = Utils.randomNumAndLetterString(10) + "."
						+ suffix;
				File newFile = new File(f.getAbsolutePath().substring(0,
						f.getAbsolutePath().lastIndexOf("\\") - 1)
						+ newFileName);
				System.out.println(f.getAbsolutePath().substring(0,
						f.getAbsolutePath().lastIndexOf("\\") - 1)
						+ newFileName);
				fileNameListInFile.add(srcFileName + FILENAME_SPLITER
						+ newFileName);

				f.renameTo(newFile);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将加密信息保存到文件
	 * 
	 * @param saveFileName
	 */
	private static void saveFileNames(String saveFileName) {
		BufferedWriter writer = null;
		OutputStreamWriter osw = null;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(saveFileName);
			osw = new OutputStreamWriter(fos);
			writer = new BufferedWriter(osw);
			writer.write(FILENAME_STATE.equals(FILENAME_CLOSE_STATE) ? FILENAME_OPEN_STATE
					: FILENAME_CLOSE_STATE);
			writer.newLine();

			for (String nameInfo : fileNameListInFile) {
				writer.write(nameInfo);
				writer.newLine();
			}
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
				if (osw != null) {
					osw.close();
				}
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static List<File> listFiles(String dir) {
		String exceptFileNames = "READ.DAT,asd.jar"; // 不作处理的文件
		List<File> results = new ArrayList<File>();
		File baseDir = new File(dir);
		if (baseDir.exists() && baseDir.isDirectory()) {
			File[] files = baseDir.listFiles();
			for (File file : files) {
				if (file.isFile() && file.length() > 0 && !file.isHidden()
						&& exceptFileNames.indexOf(file.getName()) == -1) {
					results.add(file);
				}
			}
		}
		return results;
	}

}
