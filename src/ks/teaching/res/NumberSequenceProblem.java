package ks.teaching.res;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class NumberSequenceProblem extends Problem {

	/**
	 * Generated ID for serialising this object
	 */
	private static final long serialVersionUID = -4588958353915428647L;
	// List of answers
	private int[] answers;
	// First correct answer
	private int answer1;
	// Second correct answer
	private int answer2;
	// User chosen answers
	private int given_answer1 = -1, given_answer2= -1;
	// Dragged number (-1 for none)
	private int dragged = -1;
	// List of possible answers to choose from
	private ArrayList<Integer> possible_answers;
	// generated hit zone for dragging answers
	private ArrayList<Ellipse2D> dragZones;
	// Mouse location tracker for dragging
	private Point mouseLoc;
	// Answer drop areas
	private Rectangle2D answer1_area = new Rectangle2D.Double(30, 70, 50, 50);
	private Rectangle2D answer2_area = new Rectangle2D.Double(100, 70, 50, 50);
	// The question
	private String question;
	// Submit button
	private JButton submit;

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// Confirm and submit answer when user clicks submit button
		if (arg0.getSource() == submit) {
			if (isAnswered()) {
				if(JOptionPane.showConfirmDialog(null, "Are you sure you want to submit your solution?") != 0) {
					return;
				}
				GlobalData.recordAnswer(getQuestion(), getAnswers(), getImage());
				qf.nextQuestion();
			}
			else {
				// Reject attempt if the question is not answered
				JOptionPane.showMessageDialog(null,"You need to drag the missing numbers into the boxes before submitting");
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {}

	@Override
	public void mouseEntered(MouseEvent arg0) {}

	@Override
	public void mouseExited(MouseEvent arg0) {}

	@Override
	public void mousePressed(MouseEvent arg0) {
		
		// If there is a component underneath the mouse
		// Set it to be the dragged component
		mouseLoc = arg0.getPoint();
		for(Ellipse2D e : dragZones) {
			if(e.contains(arg0.getPoint())) {
				dragged = dragZones.indexOf(e);
			}
		}
		repaint();
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// If there is a component being dragged
		// And the drag has been released in an answer square
		// fix the component as the answer
		if(answer1_area.contains(arg0.getPoint())) {
			given_answer1 = dragged;
		}
		if(answer2_area.contains(arg0.getPoint())) {
			given_answer2 = dragged;
		}
		dragged = -1;
		repaint();
	}

	@Override
	protected void init(int questionNumber, int level) {
		// Choose a number sequence
		Sequence seq = random();
		
		answers = new int[10];
		// Calculate ten terms of sequence
		for (int i = 0; i < 10; i++) {
			answers[i] = calculate_term(seq, i); 
		}
		// Randomly choose two missing terms
		Random r = new Random();
		answer1 = r.nextInt(10);
		answer2 = answer1;
		while(answer2 == answer1) {
			answer2 = r.nextInt(10);
		}
		
		// make sure the answers are in order
		if (answer1 > answer2) {
			int temp = answer1;
			answer1 = answer2;
			answer2 = temp;
		}
		
		// Generate 9 answers including the two missing terms
		possible_answers = new ArrayList<Integer>();
		possible_answers.add(answers[answer1]);
		possible_answers.add(answers[answer2]);
		for (int x = 0; x < 7; x++) {
			int num = r.nextInt(answers[9]);
			while(possible_answers.contains(num)) {
				num = r.nextInt(answers[9]);
			}
			possible_answers.add(num);
		}
		Collections.shuffle(possible_answers);
		
		// Set up a list to detect if an answer is clicked
		dragZones = new ArrayList<Ellipse2D>();
		
		// Add a submit button
		answerPane.addMouseListener(this);
		answerPane.addMouseMotionListener(this);
		submit = new JButton("Submit");
		submit.addActionListener(this);
		answerPane.setLayout(new BorderLayout());
		answerPane.add(submit, BorderLayout.SOUTH);
		
	}
	
	
	/**
	 * Calculate the 'i'th term of the sequence seq
	 * @param seq the sequence to calculate
	 * @param i the term to calculate
	 * @return the value of the calculated term
	 */
	private int calculate_term(Sequence seq, int i) {
		i++;
		if (seq == Sequence.MULTIPLE2) {
			return i*2;
		}
		if (seq == Sequence.MULTIPLE3) {
			return i*3;
		}
		if (seq == Sequence.MULTIPLE4) {
			return i*4;
		}
		if (seq == Sequence.MULTIPLE5) {
			return i*5;
		}
		if (seq == Sequence.MULTIPLE6) {
			return i*6;
		}
		if (seq == Sequence.MULTIPLE7) {
			return i*7;
		}
		if (seq == Sequence.MULTIPLE8) {
			return i*8;
		}
		if (seq == Sequence.MULTIPLE9) {
			return i*9;
		}
		if (seq == Sequence.SQUARE) {
			return i*i;
		}
		if (seq == Sequence.TRIANGULAR) {
			return (i*(i+1))/2;
		}
		return 0;
	}

	/**
	 * Return a sequence based on the difficulty level
	 * @return the random sequence
	 */
	public static Sequence random()  {
		return Sequence.getLevelValues(GlobalData.currentLevel)[new Random().nextInt(Sequence.getLevelValues(GlobalData.currentLevel).length)];
	}

	@Override
	protected void drawQuestion(JPanel jp, Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		// Start with grey for background
		g2.setColor(Color.GRAY);
		// Add some anti aliasing so it's pretty :)
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		// Draw a nice rounded rectangle background
		g2.fillRoundRect(0, 0, jp.getWidth()-1, jp.getHeight()-1, 50, 50);
		// Set a nice font for children
		g2.setFont(new Font("Comic Sans MS", 1, 25));
		// Text is going to be white
		g2.setColor(Color.WHITE);
		// Draw question number
		g2.drawString("Q"+GlobalData.currentQuestion, 30, 30);
		// Draw question
		g2.drawString("Complete the following number sequence:", 30, 60);

	}

	@Override
	protected void drawInfo(JPanel jp, Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		// Set background colour
		g2.setColor(Color.WHITE);
		// Anti-aliasing
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		// Draw a rounded rectangle for a background
		g2.fillRoundRect(0, 0, jp.getWidth()-1, jp.getHeight()-1, 50, 50);
		// Build string with underscores for missing numbers
		String info = "";
		for(int i = 0; i < answers.length; i++) {
			if(i != answer1 && i != answer2) {
				info+=answers[i]+" ";
			}
			else {
				info+="_ ";
			}
				
		}
		// Set the question to the built string for output purposes
		question = info;
		// Black text
		g2.setColor(Color.BLACK);
		// Set a nice font for children
		g2.setFont(new Font("Comic Sans MS", 1, 25));
		// Draw the string
		g2.drawString(info, 50, 250);

	}

	@Override
	protected void drawAnswers(JPanel jp, Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		// Set background colour
		g2.setColor(Color.GRAY);
		// Anti-aliasing
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		// Draw a rounded rectangle for a background
		g2.fillRoundRect(0, 0, jp.getWidth()-1, jp.getHeight()-1, 50, 50);
		g2.setColor(Color.WHITE);
		// Set a nice font for children
		g2.setFont(new Font("Comic Sans MS", 1, 15));
		// Draw instructions
		g2.drawString("Drag the correct numbers", 30, 30);
		g2.drawString("to the answer spaces below", 30, 50);
		// Draw answer squares
		g2.setColor(Color.BLACK);
		g2.draw(answer1_area);
		g2.draw(answer2_area);
		
		// Increase font size 
		g2.setFont(new Font("Comic Sans MS", 1, 25));
		// Draw in answer 1 and 2 if user has chosen them
		if(given_answer1 != -1) {
			int x_pos = (int) answer1_area.getX()+9;
			int y_pos = (int) answer1_area.getY()+35;
			g2.setColor(Color.BLUE);
			g2.fill(new Ellipse2D.Double(x_pos-5, y_pos-30, 40.0, 40.0));
			g2.setColor(Color.WHITE);
			g2.drawString(""+possible_answers.get(given_answer1), x_pos, y_pos);
			g2.setColor(Color.BLACK);
			dragZones.add(new Ellipse2D.Double(x_pos-5, y_pos-30, 40.0, 40.0));
			g2.draw(new Ellipse2D.Double(x_pos-5, y_pos-30, 40.0, 40.0));
		}
		if(given_answer2 != -1) {
			int x_pos = (int) answer2_area.getX()+9;
			int y_pos = (int) answer2_area.getY()+35;
			g2.setColor(Color.BLUE);
			g2.fill(new Ellipse2D.Double(x_pos-5, y_pos-30, 40.0, 40.0));
			g2.setColor(Color.WHITE);
			g2.drawString(""+possible_answers.get(given_answer2), x_pos, y_pos);
			g2.setColor(Color.BLACK);
			dragZones.add(new Ellipse2D.Double(x_pos-5, y_pos-30, 40.0, 40.0));
			g2.draw(new Ellipse2D.Double(x_pos-5, y_pos-30, 40.0, 40.0));
		}
		// Draw possible answers (excluding any that are in the answer boxes above)
		for(int x : possible_answers) {
			int pos = possible_answers.indexOf(x);
			if (pos == dragged || given_answer1 == pos || given_answer2 == pos) {
				continue;
			}
			int x_pos = ((pos/3)*50)+50;
			int y_pos = ((pos%3)*50)+250;
			g2.setColor(Color.BLUE);
			g2.fill(new Ellipse2D.Double(x_pos-5, y_pos-30, 40.0, 40.0));
			g2.setColor(Color.WHITE);
			g2.drawString(""+x, x_pos, y_pos);
			g2.setColor(Color.BLACK);
			dragZones.add(new Ellipse2D.Double(x_pos-5, y_pos-30, 40.0, 40.0));
			g2.draw(new Ellipse2D.Double(x_pos-5, y_pos-30, 40.0, 40.0));
			
		}
		
		// If an answer is being dragged then draw it by the mouse pointer
		if (dragged != -1) {
			
			int x_pos = mouseLoc.x;
			int y_pos = mouseLoc.y;
			g2.setColor(Color.BLUE);
			g2.fill(new Ellipse2D.Double(x_pos-5, y_pos-30, 40.0, 40.0));
			g2.setColor(Color.WHITE);
			g2.drawString(""+possible_answers.get(dragged), x_pos, y_pos);
			g2.setColor(Color.BLACK);
			dragZones.add(new Ellipse2D.Double(x_pos-5, y_pos-30, 40.0, 40.0));
			g2.draw(new Ellipse2D.Double(x_pos-5, y_pos-30, 40.0, 40.0));
		}

	}

	@Override
	protected ArrayList<String> getAnswers() {
		// return list containing given answers
		ArrayList<String> ans = new ArrayList<String>();
		ans.add(possible_answers.get(given_answer1)+"");
		ans.add(possible_answers.get(given_answer2)+"");
		return ans;
	}

	@Override
	protected String getQuestion() {
		// Return formatted question
		return "Q"+GlobalData.currentQuestion+": Complete the following sequence:\n"+question;
	}

	@Override
	protected BufferedImage getImage() {
		// No image for this problem type
		return null;
	}

	@Override
	protected boolean isAnswered() {	
		return given_answer1 != -1 && given_answer2 != -1;
	}
	
	/**
	 * Enum for holding sequence names and levels
	 * @author kwss
	 *
	 */
	private enum Sequence {
		
		MULTIPLE2(1),
		MULTIPLE3(1),
		MULTIPLE4(1),
		MULTIPLE5(1),
		MULTIPLE6(2),
		MULTIPLE7(2),
		SQUARE(2),
		TRIANGULAR(3),
		MULTIPLE8(3),
		MULTIPLE9(3);
		
		// The level of this sequence
		private int level;
		
		/**
		 * Constructor
		 * @param level the level of this sequence
		 */
		Sequence(int level) {
			this.level = level;
		}
		
		/**
		 * Return a list of sequences filtered by level
		 * @param level the max level of sequence to return
		 * @return the list of sequences
		 */
		public static Sequence[] getLevelValues(int level) {
			ArrayList<Sequence> seqs = new ArrayList<Sequence>();
			for(Sequence s : values()) {
				if (s.level <= level) {
					seqs.add(s);
				}
			}
			Sequence[] seq = new Sequence[seqs.size()];
			seqs.toArray(seq);
			return seq;
		}
	
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// update the mouse location for dragged object
		if (dragged != -1) {
			mouseLoc = e.getPoint();
			repaint();
		}
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {}
}
