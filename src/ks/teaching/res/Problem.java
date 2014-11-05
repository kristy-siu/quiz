package ks.teaching.res;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;

/**
 * Superclass to define required methods for a quiz problem
 * @author kwss
 *
 */
public abstract class Problem extends JPanel 
	implements ActionListener, MouseListener, MouseMotionListener {

	/**
	 * Generated ID for serialising this object
	 */
	private static final long serialVersionUID = -5143568253880452513L;
	protected JPanel questionPane;
	protected JPanel infoPane;
	protected JPanel answerPane;
	protected QuizFrame qf;
	
	@SuppressWarnings("serial")
	/**
	 * Constructor for a problem.
	 * Creates a question pane (800x100)
	 * Creates an info pane (500x500)
	 * Creates an answers pane (800x100)
	 * calls the init method which subclasses 
	 * should use to populate any required data
	 */
	public Problem() {
		// Create three panels for question, info and answering and style them
		// Additionally override the paint component method of each to call the
		// appropriate function with the panels graphics object as a parameter
		questionPane = new JPanel() {
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				drawQuestion(this, g);
			}
		};
		
		questionPane.setPreferredSize(new Dimension(800, 100));
		questionPane.setBackground(Color.BLACK);
		infoPane = new JPanel() {
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				drawInfo(this,g);
			}
		};
		
		infoPane.setPreferredSize(new Dimension(500, 500));
		infoPane.setBackground(Color.BLACK);
		
		answerPane = new JPanel() {
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				drawAnswers(this,g);
			}
		};
		
		answerPane.setPreferredSize(new Dimension(300, 500));
		answerPane.setBackground(Color.BLACK);
		
		setLayout(new BorderLayout());
		this.add(questionPane, BorderLayout.NORTH);
		this.add(answerPane, BorderLayout.EAST);
		this.add(infoPane, BorderLayout.CENTER);
		
		init(GlobalData.currentQuestion, GlobalData.currentLevel);
		
		setBackground(Color.BLACK);
		
	}
	
	/**
	 * Populate any data structures required for your problem
	 * and set the question string
	 * @param questionNumber current question
	 */
	protected abstract void init(int questionNumber, int level);
	
	/**
	 * Draw the question pane according to the problem
	 * @param g Graphics object to draw with
	 * @param questionPane the question pane component
	 */
	protected abstract void drawQuestion(JPanel questionPane, Graphics g);
	
	/**
	 * Draw the info pane according to the problem
	 * @param g Graphics object to draw with
	 * @param infoPane the info pane component
	 */
	protected abstract void drawInfo(JPanel infoPane, Graphics g);
	
	/**
	 * Draw the answers pane according to the problem
	 * @param answersPanel the answers pane component
	 * @param g Graphics object to draw with
	 */
	protected abstract void drawAnswers(JPanel answersPanel, Graphics g);
	
	/**
	 * Get the answer(s) given by the participant in an ArrayList
	 * @return an ArrayList containing the answer strings given by the participant
	 */
	protected abstract ArrayList<String> getAnswers();
	
	/**
	 * Get a question with the format: Q<questionNumber>: <question string>
	 * E.G. "Q1: What numbers are missing from the following sequence?\n2,4,_,8,_,12"
	 * 
	 * *NOTES* 
	 * \n will be replaced by <br /> in the outputted HTML
	 * <questionNumber> will be used to order the final results when exporting
	 * 
	 * @return the question details
	 */
	protected abstract String getQuestion();
	
	/**
	 * If the results of the problem produce an image, return it, else return null
	 * @return an image or null
	 */
	protected abstract BufferedImage getImage();
	
	/**
	 * @return true if the participant has given a sufficient answer, otherwise false
	 */
	protected abstract boolean isAnswered();

	public void setQuizFrame(QuizFrame quizFrame) {
		this.qf = quizFrame;
		
	}

}
