package ks.teaching.res;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

/**
	 * Answer class, see constructor for examples
	 * @author kwss
	 *
	 */
	public class Answer {
		private ArrayList<String> answers;
		private BufferedImage img;
		
		/**
		 * Constructor for an answer
		 * An answer can consist of a set of strings 
		 * (instructions, pattern completions)
		 * E.G: For a question:
		 * "Q1: Complete the following sequence of numbers: 2, 4, 6, 8, 10"
		 * ["12", "14"], null
		 * @param answers
		 * @param img
		 */
		public Answer(ArrayList<String> answers, BufferedImage img) {
			this.answers = answers;
			this.img = img;
		}
		
		/**
		 * Returns an HTML string which represents the given answer
		 * @param file the filename to use for the image
		 * @return the HTML string representation
		 */
		public String outputHtml(File file) {
			String s = "";
			// Make a paragraph and list
			s+="<p><ul>";
			// Iterate through answers
			for (String answer : answers) {
				// Add a list item
				s+="<li>"+answer+"</li>";
			}
			// If there is an image...
			if (img != null) {
				// Save it to a file
				exportImage(file);
				// Add an image link to the HTML string
				s+="</ul><img src='"+file.getName()+"' /></p>";
			}
			// Otherwise
			else {
				// Just close our list and paragraph
				s+="</ul></p>";
			}
			// Return the html representation of the answer
			return s;
		}
		
		/**
		 * Export a PNG of the answers' associated image.
		 * @param file the filename to save as
		 */
		private void exportImage(File file) {
			try {
				ImageIO.write(img, "png", file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}