package main.tool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.kyriakos.json.JSONObject;

public class Tool {
	public String contrNames_index[];
	public String file_index[];
	public float file_contr[][];
	public short bugs_index[];
	public float final_table [][];
	public int attributes = 4;

	public Tool() {

		String line = "";
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(
					"Data/BeforeReleaseCmmitPerLine.csv"));
			Set<String> contriputors = new HashSet<String>();
			Set<String> files = new HashSet<String>();
			while ((line = reader.readLine()) != null) {

				String split[] = line.split(",");
				contriputors.add(split[1]);

				for (int i = 3; i < split.length; i++) {
					String[] spl_fi = split[i].split("/");
					for (String str : spl_fi) {
						if (str.contains(".java")) {
							files.add(str);
						}
					}
				}

			}
			contrNames_index = new String[contriputors.size()];
			file_index = new String[files.size()];
			bugs_index = new short[files.size()];
			file_contr = new float[files.size()][contriputors.size()];
			final_table=new float[files.size()][5];
			contriputors.toArray(contrNames_index);
			contriputors = null;
			files.toArray(file_index);
			files = null;

			// System.out.println(contrNames_index.length + " \n" +
			// file_index.length);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void saveFiles_Conr() {
		BufferedWriter writer = null;
		BufferedWriter writer2 = null;
		try {
			writer = new BufferedWriter(new FileWriter("Data/Files.txt"));
			writer2 = new BufferedWriter(
					new FileWriter("Data/Contriputors.txt"));
			for (int i = 0; i < file_index.length; i++) {
				writer.write(file_index[i] + "\n");

			}

			for (int i = 0; i < contrNames_index.length; i++) {
				writer2.write(contrNames_index[i] + "\n");

			}
			writer.close();
			writer2.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public int getContrNameIndex(String contriputor) {
		for (int i = 0; i < this.contrNames_index.length; i++) {
			if (this.contrNames_index[i].equalsIgnoreCase(contriputor)) {
				return i;
			}
		}
		return -1;
	}

	public int getFileNameIndex(String file) {
		for (int i = 0; i < this.file_index.length; i++) {
			if (this.file_index[i].equalsIgnoreCase(file)) {
				return i;
			}
		}
		return -1;
	}

	public void saveContrTable() {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter("Data/contripution.csv"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for (int i = 0; i < this.file_index.length; i++) {
			try {
				writer.write(file_index[i] + ",");
				StringBuilder str = new StringBuilder();
				for (int j = 0; j < this.contrNames_index.length; j++) {

					str.append(this.file_contr[i][j] + ",");

				}
				str.setLength(str.length() - 1);
				writer.write(str.toString() + "\n");

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		try {
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void calculateOwnershipTable() {
		// TODO Auto-generated method stub
		int totals[] = new int[file_index.length];
		for (int i = 0; i < file_index.length; i++) {
			for (int j = 0; j < contrNames_index.length; j++) {
				totals[i] += file_contr[i][j];
			}
		}
		for (int i = 0; i < file_index.length; i++) {
			for (int j = 0; j < contrNames_index.length; j++) {
				file_contr[i][j] = file_contr[i][j] / totals[i];
			}
		}
	}

	public void findIndependentAttr() {
		// TODO Auto-generated method stub
		//float[][] attr = new float[this.file_index.length][this.attributes];
		int major = 0;
		int minor = 0;

		float ownership = 0;
		for (int i = 0; i < file_index.length; i++) {
			ownership = findMaxOwn(file_contr[i]);
			for (int j = 0; j < contrNames_index.length; j++) {
				if (file_contr[i][j] >= 0.5) {
					major++;
				} else  if(file_contr[i][j]< 0.5 && file_contr[i][j] > 0){
					minor++;
				}
			}
		this.final_table[i][0] = minor;
		this.final_table[i][1] = major;
		this.final_table[i][2] = minor + major;
			this.final_table[i][3] = ownership;
			minor = 0;
			major = 0;
		}
		saveIndependentAttr();
	}

	private void saveIndependentAttr() {
		// TODO Auto-generated method stub
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(
					"Data/IdepententVriables.csv"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for (int i = 0; i < this.final_table.length; i++) {
			try {
				writer.write(file_index[i] + ",");
				StringBuilder str = new StringBuilder();
				for (int j = 0; j < 4; j++) {

					str.append(this.final_table[i][j] + ",");

				}
				str.setLength(str.length() - 1);
				writer.write(str.toString() + "\n");

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		try {
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private float findMaxOwn(float[] fs) {
		// TODO Auto-generated method stub
		float max = 0;
		for (int i = 0; i < fs.length; i++) {
			if (fs[i] > max) {
				max = fs[i];
			}
		}
		return max;
	}

	public void findOnlyBugs() {
		Set<String> bugs = new HashSet<String>();
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter("Data/BugsCodes.csv"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		BufferedReader reader = null;
		File folder = new File("Data/issue_LUCENE");
		File[] listOfFiles = folder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				try {
					String line = "";
					reader = new BufferedReader(new FileReader(listOfFiles[i]));
					while ((line = reader.readLine()) != null) {
						JSONObject root = new JSONObject(line);
						String issue_type = root.getJSONObject("fields")
								.getJSONObject("issuetype").getString("name");
						if (issue_type.equalsIgnoreCase("Bug")) {
							bugs.add(root.getString("key"));
							writer.write(root.getString("key") + "\n");
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		try {
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		findBugsForEachFile(bugs);
	}

	private void findBugsForEachFile(Set<String> bugs) {
		// TODO Auto-generated method stub
		BufferedReader reader = null;
		try {
			int num_bugs = 1;
			reader = new BufferedReader(new FileReader(
					"Data/FindBugs4SpecificReleaseCmmitPerLine.csv"));
			String line = "";
			while ((line = reader.readLine()) != null) {
				String sp[] = line.split(",");
				String issue = sp[1];
				for (String str : bugs) {
					//System.out.println(str+" , " + issue );
					//System.exit(1);
					String sp_issue [] = issue.split("-");
					String bugCode = "";
					try{
						 bugCode = sp_issue[0] +"-" + sp_issue[1];	
					}catch(ArrayIndexOutOfBoundsException e){
						continue;
					}
					if (bugCode.equalsIgnoreCase(str)) {
						//System.out.println(num_bugs++);
						
						for (int i = 3; i < sp.length; i++) {
							String[] spl_fi = sp[i].split("/");
							for (String file : spl_fi) {
								if (file.contains(".java")) {
									int fileIndex = getFileNameIndex(file);
									if (fileIndex == -1) {
										continue;
									}
									this.bugs_index[fileIndex]++;
									// System.out.println(str);
								}
							}
						}
					}
				}

			}
			reader.close();
			for(int i =0; i < file_index.length; i++){
				this.final_table[i][4] = bugs_index[i];
			}
			saveFinalTable();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private void saveFinalTable() {
		// TODO Auto-generated method stub
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(
					"Data/FinalTable.csv"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			writer.write( "File,Minor,Major,Total,Ownership,Bugs\n");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for (int i = 0; i < this.final_table.length; i++) {
			try {
				writer.write(file_index[i] + ",");
				StringBuilder str = new StringBuilder();
				for (int j = 0; j < 5; j++) {

					str.append(this.final_table[i][j] + ",");

				}
				str.setLength(str.length() - 1);
				writer.write(str.toString() + "\n");

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		try {
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
