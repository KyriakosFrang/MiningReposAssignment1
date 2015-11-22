
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.nio.file.ReadOnlyFileSystemException;

import javax.xml.stream.events.Namespace;

import org.omg.CORBA.TRANSACTION_MODE;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class OwnerScanner {
	//I run the code to estimate this already, not a good way though.
	static int nameNum = 45;
	static int fileNum = 6360;
	//initialize name[], file[], commits[][]
	static String[] names = new String[nameNum];
	static String[] files = new String[fileNum];
	
	
	static int[][] commits = new int[nameNum][fileNum];		//[names][files], number of commits
	static int nameIndex = 0;
	static int fileIndex = 0;
	static int curName = 0;
	static int curFile = 0;
	
	/***
	 * scan the log file line by line
	 * @throws FileNotFoundException
	 */
	private static void scanFile() throws FileNotFoundException
	{
		/* read file */
		String pathname = "C:\\Users\\Angia\\Desktop\\beforeRelease.txt"; //directory
		File filename = new File(pathname); 
		InputStreamReader reader = null;
		String line = "";
		BufferedReader br = null;
		
		try {
			reader = new InputStreamReader(new FileInputStream(filename));
			br = new BufferedReader(reader); 
			line = br.readLine();
			String nameLine = "";
			

			while (line != null) 
			{
				
				if (line.contains(","))
				{
					nameLine = line;
				}
				if (line.contains(".java"))
				{
					String name = nameLine.substring(0,nameLine.indexOf(",")); 
					String file = line.substring(line.lastIndexOf("/")+1, line.indexOf(".java")+5);
					
					if (!isInArray(name, "name"))	//store names in string[] names
					{
						names[nameIndex] = name;
						curName = nameIndex;
						nameIndex++;
						
					}
					if (!isInArray(file, "file"))	//store file names in string[] files
					{
						files[fileIndex] = file;
						curFile = fileIndex;
						fileIndex++;
						
					}
					
					commits[curName][curFile] = commits[curName][curFile]+1;
					
					//System.out.print(name + " ");
					//System.out.print(file + " ");
					//System.out.println(commits[curName][curFile]);
					
					
				}
				
				line = br.readLine(); // read one line
			}
			System.out.println("nameIndex: "+nameIndex);
			System.out.println("fileIndex: "+fileIndex);
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("File read failed: e");
		} // 	
	}
	
	/***
	 * 
	 * @param term the term that needs to be checked in an array
	 * @param type "name"/"file"
	 * @return	true/false, if a term already exists in the array
	 */
	private static boolean isInArray(String term, String type)
	{
		if (type == "name")
		{
			for (int i = 0; i<nameIndex; i++)
			{
				if (names[i] != null && names[i].equals(term))
				{
					curName = i;
					return true;		//already exists
				}
			}
			return false;
		}
		
		if (type == "file")
		{
			for (int i = 0; i<fileIndex; i++)
			{
				if (files[i] != null && files[i].equals(term))
				{
					curFile = i;
					return true;		//already exists
				}
			}
			return false;
		}
		
		System.out.println("something wrong happened in isInArray()!!!!");
		return false;		//careful!
		
		
	}
	public static void writeResult()
	{
		//write files!
		
		try {
			File writename = new File("C:\\Users\\Angia\\Desktop\\result.csv"); 
			writename.createNewFile();
			BufferedWriter out = new BufferedWriter(new FileWriter(writename));
			out.write("file_name, minor, major, total, ownership \r\n"); // write the head
			
			/***** to print out commits[][] ***/
			String line = "";
			for (int j=0; j<fileNum; j++)
			{
				int minor = 0;	//to count minors
				int major = 0; //to count majors
				double max = 0.0; //to store the ownership
				double sumCommits = 0.0;
				for (int i=0; i<nameNum; i++)
				{
					sumCommits += commits[i][j];		//just to count the total commits for each file
				}
				
				int total = 0;
				for (int i=0; i<nameNum; i++)
				{
					double percent = ((double)commits[i][j])/sumCommits;
					if (percent > max)
					{
						max = percent;	//the max is the ownership
					}
					if (percent < 0.05)	//minor
					{
						minor++;
					}
					else
					{
						major++;
					}
					total++;
				}
				out.write(files[j] + ", "+ minor+", "+major+", "+total+", "+max+ "\r\n"); // \r\n is change line
			}

			out.flush(); // compress into file
			out.close(); // close file
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // create new file
				
				

	}
	public static void main(String args[]) {
		try {
			scanFile();
			//String a = {"a","b","c"};
		} 
		
		catch (FileNotFoundException e1) {
			System.out.println("unexpected things happened: "+e1);
			e1.printStackTrace();
		}
		
		writeResult();

		
	}
}