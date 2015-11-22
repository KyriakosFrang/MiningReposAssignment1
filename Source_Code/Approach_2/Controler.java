package main.tool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Controler {
	public static void createFile() {
		String line = "";
		BufferedReader reader = null;
		BufferedWriter writer = null;
		String tmp = "";
		int counter = 1;
		try {
			reader = new BufferedReader(new FileReader(
					"Data/FindBugs4SpecificRelease.txt"));
			writer = new BufferedWriter(new FileWriter(
					"Data/FindBugs4SpecificReleaseCmmitPerLine.csv"));
			while ((line = reader.readLine()) != null) {
				System.out.println(counter);

				tmp = tmp + "," + line;
				// System.out.println(line.length());
				if (line.length() <= 0) {

					System.out.println("newline");
					// tmp.setLength(tmp.length() - 1);

					writer.write(tmp + "\n");

					tmp = "";
					// tmp = new StringBuilder();
					writer.flush();
				}
				counter++;

			}
			reader.close();
			writer.close();
		} catch (IOException e) {
			System.out.print(e);
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	// createFile();
		
		Tool tool = new Tool();
		calculateCotripution(tool);
		
		tool.calculateOwnershipTable();
		tool.saveContrTable();
		tool.findIndependentAttr();
		tool.findOnlyBugs();
		// tool.saveFiles_Conr();
	}

	private static void calculateCotripution(Tool tool) {
		// TODO Auto-generated method stub
		BufferedReader reader = null;
		String line = "";
		try {
			reader = new BufferedReader(new FileReader(
					"Data/BeforeReleaseCmmitPerLine.csv"));
			while((line = reader.readLine())!=null){
//				String file= "";
//				String contr = "";
				String split [] = line.split(",");
				//contr = split[1];
				int contr_ind = tool.getContrNameIndex(split[1]);
				for (int i = 3; i < split.length; i++) {
					String[] spl_fi = split[i].split("/");
					for (String str : spl_fi) {
						if (str.contains(".java")) {
							int fileIndex = tool.getFileNameIndex(str);
							//System.out.println(str);
							tool.file_contr[fileIndex][ contr_ind]++; 
						}
					}
				}
			}
		} catch (IOException e) {
			System.out.println(e);
		}

	}
}
