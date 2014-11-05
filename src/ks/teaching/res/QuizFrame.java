package ks.teaching.res;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;


public class QuizFrame extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// Exit and export button
	static JButton exit = new JButton("Exit");
	static JButton export = new JButton("Export");

	private static QuizFrame quiz;

	private MainMenu menu;

	private JFrame frame;

	private JPanel menubar;

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// Kill program on exit but get confirmation from user first
		if(arg0.getSource() == exit) {
			if(JOptionPane.showConfirmDialog(this, "If you exit without exporting you will lose your progress, Are you sure you want to quit?")== 0){
				System.exit(0);
			}
		}
		// Export to an HTML file
		if(arg0.getSource() == export) {
			if (GlobalData.results.size() == 0) {
				JOptionPane.showMessageDialog(null, "You can't export until you have answered at least one question!");
			}
			else {
				if(JOptionPane.showConfirmDialog(null, "You have answered "+GlobalData.results.size()+" question(s), Are you sure you want to export your results?") == 0) {
					GlobalData.exportResults();
				}
			}
		}
	}
	
	/**
	 * Entry point for the quiz program
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		quiz = new QuizFrame();
		quiz.init();
	}

	private void init() {
		// Create Frame
		frame = new JFrame("Quiz!");
		// Style Frame
		frame.setSize(800, 650);
		// Remove nasty java frame
		frame.setUndecorated(true);
		// Give it a border layout so we can position using NECSW
		frame.getContentPane().setLayout(new BorderLayout());
		// Create menu bar
		menubar = new JPanel();
		menubar.setPreferredSize(new Dimension(800,30));
		menubar.setLayout(new BorderLayout());
		menubar.add(export, BorderLayout.WEST);
		menubar.add(exit, BorderLayout.EAST);
		menubar.setBackground(Color.BLACK);
		// Set this as the action handler for the xit and export buttons
		exit.addActionListener(this);
		export.addActionListener(this);
		
		// Make a panel to draw the background on
		JPanel bg = new JPanel();
		try {
			bg = new JPanel() {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;
				BufferedImage picture = ImageIO.read(getClass().getResource("/picture.png"));
				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					g.drawImage(picture,0,0, null);
				}
			};
		} catch (IOException e) {
			e.printStackTrace();
		}
		bg.setBackground(Color.BLACK);
		frame.setContentPane(bg);
		frame.getContentPane().add(menubar, BorderLayout.NORTH);
		// Initialise Menu Panel
		menu = new MainMenu(this);
		frame.getContentPane().add(menu, BorderLayout.CENTER);
		// Set content pane to Menu
		Container pane = frame.getContentPane();
		LineBorder lb = new LineBorder(Color.BLACK, 5);
		((JComponent) pane).setBorder(lb);
		// Center on screen
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
	}

	/**
	 * Start the quiz by generating the first question and updating the interface
	 */
	public void startQuiz() {
		frame.getContentPane().removeAll();		
		frame.getContentPane().add(menubar, BorderLayout.NORTH);
		frame.getContentPane().add(generateQuestion(), BorderLayout.CENTER);		
		frame.validate();
		frame.repaint();
	}
	
	/**
	 * End the quiz by resetting question number and interface
	 */
	public void endQuiz() {
		frame.getContentPane().removeAll();
		frame.getContentPane().add(menubar, BorderLayout.NORTH);
		frame.getContentPane().add(menu, BorderLayout.CENTER);
		GlobalData.currentQuestion = 1;
		GlobalData.results.clear();
		frame.validate();
		frame.repaint();
	}
	
	/**
	 * Generate and return a new question from the available classes
	 * @return a question 
	 */
	private JPanel generateQuestion() {
		
		Class<? extends Problem> c;
		Random r = new Random();
		c = GlobalData.availableProblems.get(r.nextInt(GlobalData.availableProblems.size()));
		try {
			Problem p =  c.newInstance();
			p.setQuizFrame(this);
			return p;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	/**
	 * Move onto the next question checking if the quiz has ended, update interface
	 */
	public void nextQuestion() {
		
		GlobalData.currentQuestion++;
		if(GlobalData.currentQuestion > GlobalData.QUESTION_LIMIT) {
			if(JOptionPane.showConfirmDialog(null, "You have finished the quiz, press Yes to export your results") == 0) {
				GlobalData.exportResults();
				endQuiz();
			}
			return;
		}
		frame.getContentPane().removeAll();
		
		frame.getContentPane().add(menubar, BorderLayout.NORTH);
		frame.getContentPane().add(generateQuestion(), BorderLayout.CENTER);
		
		frame.validate();
		frame.repaint();
		
	}

}
