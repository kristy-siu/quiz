package ks.teaching.res;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D.Double;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ShapeDecompositionProblem extends Problem {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2575602988971446522L;
	
	// Submit button
	private JButton submit;
	
	// Drawing pointer (shows direction we are drawing in)
	private BufferedImage pointer;
	
	// The available instruction commands (L1 uses instructions, L2, L3 use instructions2)
	protected String[] instructions;
	private String[] instructions2;
	
	// Array of instructions entered by user
	protected ArrayList<Instruction> instruction_set = new ArrayList<Instruction>();
	
	// drop down to allow selection of instruction command
	private JComboBox<String> instruction;
	
	// Textfield for entering an instruction value
	protected JTextField value;
	
	// Add instruction button
	protected JButton add;
	
	// Test instructions button
	protected JButton test;
	
	// Lines to draw on info pane
	protected ArrayList<Line2D> linesToDraw; 
	
	// This is set with a partial length line while we are in the middle of drawing one
	protected Line2D currentLine;
	
	// All the locations of delete buttons mapped to the index of the instruction
	// they correspond to
	protected HashMap<Integer, Rectangle2D.Double> deleteButtons;
	
	// Image for a delete button
	private BufferedImage delete;
	
	// Current direction we are facing
	private int heading;
	
	// Current pointer location
	private Point2D current_pos;
	
	// The question
	private Question question;

	// The instruction limit
	private int maxIns;


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
				JOptionPane.showMessageDialog(null,"You don't have many instructions, add some more and test");
			}
		}
		// Add an instruction when add is clicked
		if(arg0.getSource() == add) {
			int val = 0;
			// Check value is an integer
			try {
				val = Math.abs(Integer.parseInt(value.getText()));
			}
			catch(NumberFormatException e) {
				JOptionPane.showMessageDialog(null,"You can only use numbers in the text field");
				return;
			}
			// Ignore an end on its own
			if(((String) instruction.getSelectedItem()).contains("End")) {
				val = 0;
			}
			// Check that the given values aren't stupid...
			if(instruction.getSelectedItem().equals("Move") && val > 200) {
				JOptionPane.showMessageDialog(null,"200 pixels is big enough for an edge :)");
				return;
			}
			else if(((String) instruction.getSelectedItem()).contains("Turn") && val > 360) {
				JOptionPane.showMessageDialog(null,"Do you really want to turn it that much? :)");
				return;
			}
			else if(((String) instruction.getSelectedItem()).contains("Repeat") && val > 10) {
				JOptionPane.showMessageDialog(null,"I think ten iterations is enough! :)");
				return;
			}
			// Check we have more instructions left to use
			if(instruction_set.size() == maxIns ) {
				JOptionPane.showMessageDialog(null,"Your instruction set is full, delete some and try again :)");
				return;
			}
			// Add it
			instruction_set.add(new Instruction((String) instruction.getSelectedItem(), val));
			repaint();
		}
		// Test the instructions by drawing the shape on the infopane
		if(arg0.getSource() == test) {
			heading = 0;
			current_pos = new Point(infoPane.getWidth()/2-10, infoPane.getHeight()/2-10);
			linesToDraw.clear();
			new Thread() {
				public void run() {
					test.setEnabled(false);
			animDraw(instruction_set);
			test.setEnabled(true);
				}
			}.start();
			repaint();
			
		}
	}
	/**
	 * Draws the image in an animated way (slowed down for visibility)
	 * @param instructions the instruction set to draw (passed in for recursive purposes)
	 */
	private void animDraw(ArrayList<Instruction> instructions) {
		final ArrayList<Instruction> instruction_set = instructions;
		
				for(int i = 0; i < instruction_set.size(); i++) {
					Instruction ins = instruction_set.get(i);
					if(ins.getType().equals("Turn Clockwise")) {
						final int val = ins.value;
						final int old_heading = heading;
						
							
								while(heading != old_heading+val){
									heading+=1;
									try {
										Thread.sleep(3);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
									repaint();
								}
								heading%=360;
							}
					else if (ins.getType().equals("Turn Anti-Clockwise")) {
						final int val = ins.value;
						final int old_heading = heading;
						
							
								while(heading != old_heading-val){
									heading-=1;
									try {
										Thread.sleep(3);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
									repaint();
								}
								heading%=360;
							}	
					else if (ins.getType().equals("Repeat")) {
						int x = i+1;
						ArrayList<Instruction> looped_ins_set = new ArrayList<Instruction>();
						Instruction end = instruction_set.get(x);
						while(!end.getType().equals("End repeat") && x < instruction_set.size()-1) {
							looped_ins_set.add(end);
							x++;
							end = instruction_set.get(x);
						}
						for(int y = 0; y < ins.value; y++) {
							// Use recursion to handle loops
							animDraw(looped_ins_set);
						}
						if(x < instruction_set.size()) {
							i = x;
						}
						else {
							break;
						}
					}
					else {
						Point2D old_pos = current_pos;
						int length = 0;
						while (length != ins.value) {
							current_pos = calculateNewPos(old_pos, length);
							
							currentLine = new Line2D.Double(old_pos.getX(), old_pos.getY(), current_pos.getX(), current_pos.getY());
							
							length++;
							try {
								Thread.sleep(5);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
								repaint();	
						}
						if(currentLine != null)
							linesToDraw.add(currentLine);
						currentLine = null;
						
						}
					}
					repaint();	
	}
	
	/**
	 * Calculate new position of a line according to the current position, 
	 * and the distance to travel. The instance variable heading is used
	 * 
	 * @param current_pos the current position
	 * @param length the distance to travel
	 * @return the new position
	 */
	private Point2D calculateNewPos(Point2D current_pos, int length) {
		// new X
		double new_x = length * Math.cos(Math.toRadians(heading)) + current_pos.getX();
		// new Y
		double new_y = length * Math.sin(Math.toRadians(heading)) + current_pos.getY();
		// return new point(x, y)
		return new Point2D.Double(new_x, new_y);
	}
	
	/**
	 * Calculate new position of a line according to the current position, 
	 * and the distance to travel. The parameter  heading is used
	 * 
	 * @param current_pos current position
	 * @param length distance to travel
	 * @param heading the heading to base calculation on
	 * @return the new position
	 */
	private Point2D calculateNewPos(Point2D current_pos, int length, int heading) {
		// new X
		double new_x = length * Math.cos(Math.toRadians(heading)) + current_pos.getX();
		// new Y
		double new_y = length * Math.sin(Math.toRadians(heading)) + current_pos.getY();
		// return new point(x, y)
		return new Point2D.Double(new_x, new_y);
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// Check if the user clicked a delete button and
		// delete the corresponding instruction
		for (Integer i : deleteButtons.keySet()) {
			if(deleteButtons.get(i).contains(arg0.getPoint())) {
				instruction_set.remove(instruction_set.get(i));
			}
		}
		repaint();
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {}

	@Override
	public void mouseExited(MouseEvent arg0) {}

	@Override
	public void mousePressed(MouseEvent arg0) {}

	@Override
	public void mouseReleased(MouseEvent arg0) {}

	@Override
	public void mouseDragged(MouseEvent arg0) {}

	@Override
	public void mouseMoved(MouseEvent arg0) {}

	@Override
	protected void init(int questionNumber, int level) {
		// Start heading at 0 (facing right)
		heading = 0;
		// Centre initial position
		current_pos = new Point(infoPane.getWidth()/2-10, infoPane.getHeight()/2-10);
		// Give it a border (NECSW) layout
		answerPane.setLayout(new BorderLayout());
		// Initialise submit button
		submit = new JButton("Submit");
		// Add the submit button to the answer pane
		answerPane.add(submit, BorderLayout.SOUTH);
		// Make this the submit button action listener so that events
		// on the submit button route to this class's actionPerformed function
		submit.addActionListener(this);
		// Attempt to load images catching IOExceptions
		try {
			this.pointer = ImageIO.read(getClass().getResource("/pointer2.png"));
			this.delete = ImageIO.read(getClass().getResource("/delete.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Initialise toolbar
		JPanel toolbar = new JPanel();
		// Populate instruction command lists
		instructions = new String[]{"Move", "Turn Clockwise", "Turn Anti-Clockwise"};
		instructions2 = new String[]{"Move", "Turn Clockwise", "Turn Anti-Clockwise", "Repeat", "End repeat"};
		// populate the drop down according to the difficulty level
		if(GlobalData.currentLevel == 1) {
			instruction = new JComboBox<String>(instructions);
		}
		else {
			instruction = new JComboBox<String>(instructions2);
		}	
		// Add the instructions to the tool bar
		toolbar.add(instruction);
		// Initialise value field, style and start at 0
	    value = new JTextField();
		value.setText("0");
		value.setPreferredSize(new Dimension(28,28));
		// Add a mouse listener to clear this text field when clicked
		value.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                value.setText("");
            }
        });
		// Add the value field to the tool bar
		toolbar.add(value);
		// Create the add button
		add = new JButton("Add");
		// Add the add button to the tool bar
		toolbar.add(add);
		// Make this the add button action listener so that events
		// on the add button route to this class's actionPerformed function
		add.addActionListener(this);
		// Initialise lines to draw array
		linesToDraw = new ArrayList<Line2D>();
		
		// Add the tool bar to the answer pane
		answerPane.add(toolbar, BorderLayout.NORTH);
		// Make this the mouse listener for the answer pane
		answerPane.addMouseListener(this);
		// Initialise the test button
		test = new JButton("Test it!");
		// Make this the add button action listener so that events
		// on the add button route to this class's actionPerformed function
		test.addActionListener(this);
		// Give the info pane a border (NECSW) layout
		infoPane.setLayout(new BorderLayout());
		// Add the test button to info pane
		infoPane.add(test, BorderLayout.SOUTH);
		// Generate a question
		generateQuestion();
	}

	/**
	 * Generate and store a new question for this problem
	 */
	private void generateQuestion() {
		Random r = new Random();
		int sides = r.nextInt(GlobalData.currentLevel+1)+3;
		if (GlobalData.currentLevel == 3) {
			maxIns = sides+1;
		}
		else {
			maxIns = 20;
		}
		repaint();
		int size = r.nextInt(50)+50;
		this.question = new Question(sides, "edges", size);
		
		
		
	}
	@Override
	protected void drawQuestion(JPanel questionPane, Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		// Start with grey for background
		g2.setColor(Color.GRAY);
		// Add some anti aliasing so it's pretty :)
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		// Draw a nice rounded rectangle background
		g2.fillRoundRect(0, 0, questionPane.getWidth()-1, questionPane.getHeight()-1, 50, 50);
		// Set a nice font for children
		g2.setFont(new Font("Comic Sans MS", 1, 25));
		// Text is going to be white
		g2.setColor(Color.WHITE);
		// Draw question number
		g2.drawString("Q"+GlobalData.currentQuestion, 30, 30);
		
		g2.setFont(new Font("Comic Sans MS", 1, 15));
		// Draw question
		g2.drawString(question.toString(), 30, 60);
		g2.drawString("Instructions remaining:"+(maxIns-instruction_set.size()), 30, 90);
	}

	@Override
	protected void drawInfo(JPanel infoPane, Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		// Start with white for background
		g2.setColor(Color.WHITE);
		// Add some anti aliasing so it's pretty :)
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		// Draw a nice rounded rectangle background
		g2.fillRoundRect(0, 0, infoPane.getWidth()-1, infoPane.getHeight()-1, 50, 50);
		// Text is going to be black		
		g2.setColor(Color.BLACK);
		// Draw any lines that need drawing
		for(Line2D l : linesToDraw) {
			g2.draw(l);
		}
		// If the line isn't null draw it
		if(currentLine != null) {
			g2.draw(currentLine);
		}
		// Work out the rotation in radians
		double rotationRequired = Math.toRadians(heading);
		// Work out the rotation point
		double locationX = pointer.getWidth() / 2;
		double locationY = pointer.getHeight() / 2;
		// Create an affine transform for the rotation
		AffineTransform tx = AffineTransform.getRotateInstance(rotationRequired, locationX, locationY);
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);

		// Drawing the rotated image at the required drawing locations
		g2.drawImage(op.filter(pointer, null), (int)current_pos.getX()-10, (int)current_pos.getY()-10, null);
	}

	@Override
	protected void drawAnswers(JPanel answersPanel, Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		// Start with grey for background
		g2.setColor(Color.GRAY);
		// Add some anti aliasing so it's pretty :)
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		// Draw a nice rounded rectangle background
		Double rect = new RoundRectangle2D.Double(0.0,0.0,getWidth()-1,getHeight()-1, 50.0, 50.0);
		g2.fill(rect);
		// Text is going to be white
		g2.setColor(Color.WHITE);
		// Set font
		g2.setFont(new Font("Comic Sans MS", 1, 32));
		// Draw some text
		g2.drawString("Instruction Set", 30, 70);
		int i = 70;
		g2.setFont(new Font("Comic Sans MS", 1, 15));
		// Draw the delete buttons, clearing the list first
		deleteButtons = new HashMap<Integer, Rectangle2D.Double>();
		deleteButtons.clear();
		for (Instruction instruction : instruction_set){
			i=i+20;
			g2.drawString(instruction.toString(), 30, i);
			g2.drawImage(delete, 10, i-15, null);
			Rectangle2D.Double r = new Rectangle2D.Double(10, i-15, 15, 15);
			deleteButtons.put(new Integer(instruction_set.indexOf(instruction)), r);
		}
	}

	@Override
	protected ArrayList<String> getAnswers() {
		// Add the instructions to a string arraylist
		ArrayList<String> answers = new ArrayList<String>();
		for(Instruction i : instruction_set) {
			answers.add(i.toString());
		}
		return answers;
	}

	@Override
	protected String getQuestion() {
		// Format question
		return "Q"+GlobalData.currentQuestion+": "+question.toString();
	}

	@Override
	protected BufferedImage getImage() {
		// Draw the image according to the instructions into a new buffered img
		BufferedImage img = new BufferedImage(125,125, BufferedImage.TYPE_INT_ARGB);
		Graphics g = img.getGraphics();
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		int heading_2 = 0;
		Point2D current_pos_2 = new Point(img.getWidth()/2-10, img.getHeight()/2-10);
		ArrayList<Line2D> lines = new ArrayList<Line2D>();
		
		drawImage(heading_2, current_pos_2, lines, instruction_set);
		g2.setColor(Color.BLACK);
		g2.scale(0.5,0.5);
		g2.setBackground(Color.WHITE);
		for (Line2D l : lines) {
			g2.draw(l);
		}
		return img;
			
	}
	
	// Draw image without animation
	private Tuple<Integer, Point2D> drawImage(int heading_2, Point2D current_pos_2,
			ArrayList<Line2D> lines, ArrayList<Instruction> instruction_set) {
		for(int i = 0; i < instruction_set.size();i++) {
			Instruction ins = instruction_set.get(i);
			if(ins.getType().equals("Turn Clockwise")) {	
				heading_2+=ins.value;
				heading_2%=360;
			}
			else if (ins.getType().equals("Turn Anti-Clockwise")) {
				heading_2-=ins.value;
				heading_2%=360;
			}
			else if (ins.getType().equals("Repeat")) {
				int x = i+1;
				ArrayList<Instruction> looped_ins_set = new ArrayList<Instruction>();
				Instruction end = instruction_set.get(x);
				while(!end.getType().equals("End repeat") && x < instruction_set.size()-1) {
					looped_ins_set.add(end);
					x++;
					end = instruction_set.get(x);
				}
				for(int y = 0; y < ins.value; y++) {
					Tuple<Integer, Point2D> newVals = drawImage(heading_2,current_pos_2, lines, looped_ins_set);
					heading_2 = newVals.getFirst();
					current_pos_2 = newVals.getSecond();
				}
				if(x < instruction_set.size()) {
					i = x;
				}
				else {
					break;
				}
			}
			else if (ins.getType().equals("End Repeat")) {
			}
			
			else {
				Point2D old_pos = current_pos_2;
				current_pos_2 = calculateNewPos(old_pos, ins.value, heading_2);	
				lines.add(new Line2D.Double(old_pos, current_pos_2));
			}
		}
		Tuple<Integer, Point2D> newVals = new Tuple<Integer, Point2D>(heading_2, current_pos_2);
		return newVals;
	}
	

	@Override
	protected boolean isAnswered() {
		return instruction_set.size() >= 3;
	}
	
	/**
	 * Class to represent an instruction, which returns a nicely
	 * formatted string when toString is called
	 * 
	 * @author kwss
	 *
	 */
	public class Instruction {
		private String type;
		private int value;
		
		public Instruction(String type, int value) {
			this.type = type;
			this.value = value;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public int getValue() {
			return value;
		}
		public void setValue(int value) {
			this.value = value;
		}
		
		@Override
		public String toString() {
			String unit = type.contains("Move") ? "pixels" : type.contains("Repeat") ? "times" : type.contains("End") ? "" : "degrees";
			return type+" "+(type.contains("End")?"":value+" ")+unit;
		}
		
	}
	
	/**
	 * Class to represent a question, which returns a nicely
	 * formatted string when toString is called
	 * 
	 * @author kwss
	 *
	 */
	private class Question {
		private int sides;
		private String type;
		private int size;
		private String[] shapes = {"null", "null","triangle","square","pentagon", "hexagon"};
		public Question(int sides, String type, int size) {
			this.sides = sides;
			this.type = type;
			this.size = size;
		}
		
		public String getShape() {
			return shapes[sides-1];
		}
			
		@Override
		public String toString() {
			return "Write a set of instructions to draw a "+getShape()+" with "+type+ " " +(type.equals("edges") ? "of length " : " of size ")+size+(type.equals("edges") ? " pixels" : " squared pixels");
		}
	}
}
