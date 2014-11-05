package ks.teaching.res;
import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Static class (only one per application, initialised at runtime) for holding
 * miscellaneous application wide data.
 * @author kwss
 *
 */
public abstract class GlobalData {
	// Stored user name
	public static String name = null;
	
	// Questions to answer for quiz completion
	public static int QUESTION_LIMIT = 5;
	
	// Current question number
	public static int currentQuestion = 1;
	
	// Completed questions and answers
	public static HashMap<String, Answer> results = new HashMap<String, Answer>();
    
	// Difficulty level
	public static int currentLevel;
    
	// Classes to include in the problem selection
	public static ArrayList<Class<? extends Problem>> availableProblems;
	
	
	// Statically load in classes to form weighted selection list
	static {
		availableProblems = new ArrayList<Class<? extends Problem>>();
		availableProblems.add(NumberSequenceProblem.class);
		availableProblems.add(ShapeDecompositionProblem.class);
		availableProblems.add(ShapeDecompositionProblem.class);
		availableProblems.add(ShapeDecompositionProblem.class);
	}
	
	/**
	 * Add an answer to the completed answer list
	 * @param question The question
	 * @param answers  The answer(s) in a list
	 * @param img Optionally an associated image
	 */
	public static void recordAnswer(String question,
			ArrayList<String> answers, BufferedImage img){
		results.put(question, new Answer(answers, img));
	}
	
	
	/**
	 * Save results to an HTML file
	 * @return true if success otherwise false
	 */
	public static boolean exportResults() {
		// Create a file chooser
		JFileChooser fc = new JFileChooser();
		// Define acceptable extensions
		String[] extensions = {"html"};
		
		// Set a filter for only HTML files
		fc.setFileFilter(new FileNameExtensionFilter("HTML file", extensions));
		
		// Get the user response
		int returnVal = fc.showSaveDialog(null);
		
		// If they cancelled then do nothing
		if(returnVal != JFileChooser.APPROVE_OPTION) {
			return false;
		}
		
		// Else get the file to save as
		File file = fc.getSelectedFile();
		
		// If the student forgot to specify the extension, add it.
		if(!file.getName().endsWith(".html")) {
			file = new File(file.getAbsolutePath()+".html");
		}
		
		// Try to write out the results
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			bw.write("<html>\n");
			bw.write("<head>\n");
			bw.write("<title>"+name+"</title>\n");
			bw.write("</head>\n");
			bw.write("<body>\n");
			bw.write("<h1>"+name+"</h1>\n");
			Set<String> questions = results.keySet();
			ArrayList<String> sorted = new ArrayList<String>();
			sorted.addAll(questions);
			Collections.sort(sorted);
			for (String q : sorted) {
				// Replace new lines with line break tags
				bw.write("<p>"+q.replaceAll("\n", "<br />")+"</p>\n");
				bw.write(results.get(q).outputHtml(new File(file.getParent()+"/"+currentLevel+q.split(":")[0]+".png")));
			}
			bw.write("</body>\n");
			bw.write("</html>\n");
			bw.close();
			
		} catch (IOException e) {
			// Cancel if a problem occurs
			return false;
		}
		
		// Try to open the file in the browser
		try {
			Desktop.getDesktop().browse(file.toURI());
		} catch (IOException e) {
		}
		return true;
		
	}
	
}


