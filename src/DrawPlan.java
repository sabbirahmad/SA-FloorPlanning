import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.sql.Time;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class DrawPlan extends JComponent implements FinalVariable{
	
	private static final long serialVersionUID = 1L;
	//private CostTree t;
	private int panelX = 512; //defualt screen size 512x512
	private int panelY = 512;
	private String frameName = "Floorplan";
	
	private int times_limit;
	private int TIMES = 1;

	private static class Line{
	    final int x1;
	    final int y1;
	    final int x2;
	    final int y2;
	    final Color color;

	    public Line(int x1, int y1, int x2, int y2, Color color) {
	        this.x1 = x1;
	        this.y1 = y1;
	        this.x2 = x2;
	        this.y2 = y2;
	        this.color = color;
	    }
	}

	private final LinkedList<Line> lines = new LinkedList<Line>();
	private final ArrayList<String> terminal_name = new ArrayList<>();
	private final ArrayList<Point> terminal_position = new ArrayList<>();
	
	public DrawPlan(CostTree t, String frameName, int times_max){
		//this.t = t;
		Dimension screen_size = Toolkit.getDefaultToolkit().getScreenSize();
		times_limit = (int)screen_size.getHeight();
		TIMES = (int)(times_limit / ((t.blocks.size()/2) * times_max));
		//System.out.println("Times: " + TIMES);
		
		
		
		this.frameName = frameName;
		blockToLine(t.getRoot());
		draw();
		repaint();
	}
	
	public void draw(){
		JFrame testFrame = new JFrame();
		testFrame.setTitle(frameName);
	    testFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    //final LinesComponent comp = new LinesComponent();
	    setPreferredSize(new Dimension(OFFSET + panelX * TIMES + OFFSET, OFFSET + panelY * TIMES + OFFSET));
	    testFrame.getContentPane().add(this, BorderLayout.CENTER);
//	    JPanel buttonsPanel = new JPanel();
//	    JButton newLineButton = new JButton("New Line");
//	    JButton clearButton = new JButton("Clear");
//	    buttonsPanel.add(newLineButton);
//	    buttonsPanel.add(clearButton);
//	    testFrame.getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
	    //repaint();
	    testFrame.pack();
	    testFrame.setVisible(true);
	}
	
	private void blockToLine(Node n){
		int size = n.dimension.size();
		int min_index = 0;
		int min = n.dimension.get(0).getArea();
		for(int i = 1; i < size; i++){
			if(min > n.dimension.get(i).getArea()){
				min = n.dimension.get(i).getArea();
				min_index = i;
			}
		}
		
		Node current_node;
		int current_min_index;
		ArrayList<Node> queue = new ArrayList<>();
		ArrayList<Integer> marker = new ArrayList<>();
		
		queue.add(n);
		marker.add(min_index);
		
		while(!queue.isEmpty()){
			current_node = queue.get(0);
			current_min_index = marker.get(0);
			queue.remove(0);
			marker.remove(0);
			
			if(current_node.getType() == TERMINAL){
//				Dimension d = current_node.dimension.get(current_min_index);
//				blockOrientation += current_node.getValue() + ": " + d.toString() + "\n";
//				finalOrientation.put(current_node.getValue(), current_node.dimension.get(current_min_index));

				BlockDimension dc = current_node.dimension.get(current_min_index);
				int w = dc.getWidth();
				int h = dc.getHeight();
				
				ArrayList<Point> pt = current_node.getParent().points;
				
				if(current_node.getParent().getValue().equals("H")){ //parent horijontal cut
					if(current_node.getParent().getLeftChild() == current_node){ // left child //below box
						addLine(pt.get(P4).getX(), pt.get(P4).getY() - h, pt.get(P4).getX() + w, pt.get(P4).getY() - h, Color.red); //upper line
						addLine(pt.get(P4).getX() + w, pt.get(P4).getY() - h, pt.get(P4).getX() + w, pt.get(P4).getY(), Color.red); //right line
						addLine(pt.get(P4).getX() + w, pt.get(P4).getY(), pt.get(P4).getX(), pt.get(P4).getY(), Color.red); //lower line
						addLine(pt.get(P4).getX(), pt.get(P4).getY(), pt.get(P4).getX(), pt.get(P4).getY() - h, Color.red); //left line
						
						current_node.points.add(new Point(pt.get(P4).getX(), pt.get(P4).getY() - h)); //store points anti-clock wise
						current_node.points.add(new Point(pt.get(P4).getX() + w, pt.get(P4).getY() - h));
						current_node.points.add(new Point(pt.get(P4).getX() + w, pt.get(P4).getY()));
						current_node.points.add(new Point(pt.get(P4).getX(), pt.get(P4).getY()));
						
						terminal_name.add(current_node.getValue());
						int px = (pt.get(P4).getX() + pt.get(P4).getX() + w) / 2;
						int py = (pt.get(P4).getY() - h + pt.get(P4).getY()) / 2;
						terminal_position.add(new Point(px, py));
					}
					else{
						addLine(pt.get(P1).getX(), pt.get(P1).getY(), pt.get(P1).getX() + w, pt.get(P1).getY(), Color.red); //upper line
						addLine(pt.get(P1).getX() + w, pt.get(P1).getY(), pt.get(P1).getX() + w, pt.get(P1).getY() + h, Color.red); //right line
						addLine(pt.get(P1).getX() + w, pt.get(P1).getY() + h, pt.get(P1).getX(), pt.get(P1).getY() + h, Color.red); //lower line
						addLine(pt.get(P1).getX(), pt.get(P1).getY() + h, pt.get(P1).getX(), pt.get(P1).getY(), Color.red); //left line
						
						current_node.points.add(new Point(pt.get(P1).getX(), pt.get(P1).getY())); //store points anti-clock wise
						current_node.points.add(new Point(pt.get(P1).getX() + w, pt.get(P1).getY()));
						current_node.points.add(new Point(pt.get(P1).getX() + w, pt.get(P1).getY() + h));
						current_node.points.add(new Point(pt.get(P1).getX(), pt.get(P1).getY() + h));
						
						terminal_name.add(current_node.getValue());
						int px = (pt.get(P1).getX() + pt.get(P1).getX() + w) / 2;
						int py = (pt.get(P1).getY() + pt.get(P1).getY() + h) / 2;
						terminal_position.add(new Point(px, py));
					}
				}
				else{ //parent vertical cut
					if(current_node.getParent().getLeftChild() == current_node){ // left child
						addLine(pt.get(P1).getX(), pt.get(P1).getY(), pt.get(P1).getX() + w, pt.get(P1).getY(), Color.red); //upper line
						addLine(pt.get(P1).getX() + w, pt.get(P1).getY(), pt.get(P1).getX() + w, pt.get(P1).getY() + h, Color.red); //right line
						addLine(pt.get(P1).getX() + w, pt.get(P1).getY() + h, pt.get(P1).getX(), pt.get(P1).getY() + h, Color.red); //lower line
						addLine(pt.get(P1).getX(), pt.get(P1).getY() + h, pt.get(P1).getX(), pt.get(P1).getY(), Color.red); //left line
						
						current_node.points.add(new Point(pt.get(P1).getX(), pt.get(P1).getY())); //store points anti-clock wise
						current_node.points.add(new Point(pt.get(P1).getX() + w, pt.get(P1).getY()));
						current_node.points.add(new Point(pt.get(P1).getX() + w, pt.get(P1).getY() + h));
						current_node.points.add(new Point(pt.get(P1).getX(), pt.get(P1).getY() + h));
						
						terminal_name.add(current_node.getValue());
						int px = (pt.get(P1).getX() + pt.get(P1).getX() + w) / 2;
						int py = (pt.get(P1).getY() + pt.get(P1).getY() + h) / 2;
						terminal_position.add(new Point(px, py));
					}
					else{
						addLine(pt.get(P2).getX() - w, pt.get(P2).getY(), pt.get(P2).getX(), pt.get(P2).getY(), Color.red); //upper line
						addLine(pt.get(P2).getX(), pt.get(P2).getY(), pt.get(P2).getX(), pt.get(P2).getY() + h, Color.red); //right line
						addLine(pt.get(P2).getX(), pt.get(P2).getY() + h, pt.get(P2).getX() - w, pt.get(P2).getY() + h, Color.red); //lower line
						addLine(pt.get(P2).getX() - w, pt.get(P2).getY() + h, pt.get(P2).getX() - w, pt.get(P2).getY(), Color.red); //left line
						
						current_node.points.add(new Point(pt.get(P2).getX() - w, pt.get(P2).getY())); //store points anti-clock wise
						current_node.points.add(new Point(pt.get(P2).getX(), pt.get(P2).getY()));
						current_node.points.add(new Point(pt.get(P2).getX(), pt.get(P2).getY() + h));
						current_node.points.add(new Point(pt.get(P2).getX() - w, pt.get(P2).getY() + h));
						
						terminal_name.add(current_node.getValue());
						int px = (pt.get(P2).getX() - w + pt.get(P2).getX()) / 2;
						int py = (pt.get(P2).getY() + pt.get(P2).getY() + h) / 2;
						terminal_position.add(new Point(px, py));
					}
					
				}
				
				
				
			}
			else{
				if(current_node.getParent() == null){ //root node
					BlockDimension dc = current_node.dimension.get(current_min_index);
					int w = dc.getWidth();
					int h = dc.getHeight();
					addLine(0, 0, w, 0); //upper line
					addLine(w, 0, w, h); //right line
					addLine(w, h, 0, h); //lower line
					addLine(0, h, 0, 0); //left line
					
					current_node.points.add(new Point(0, 0)); //store points anti-clock wise
					current_node.points.add(new Point(w, 0));
					current_node.points.add(new Point(w, h));
					current_node.points.add(new Point(0, h));
					
					panelX = w;
					panelY = h;
				}
				else{
					BlockDimension dc = current_node.dimension.get(current_min_index);
					int w = dc.getWidth();
					int h = dc.getHeight();
					
					ArrayList<Point> pt = current_node.getParent().points;
					
					if(current_node.getParent().getValue().equals("H")){ //parent horijontal cut
						if(current_node.getParent().getLeftChild() == current_node){ // left child
							addLine(pt.get(P4).getX(), pt.get(P4).getY() - h, pt.get(P3).getX(), pt.get(P3).getY() - h); //upper line
							addLine(pt.get(P3).getX(), pt.get(P3).getY() - h, pt.get(P3).getX(), pt.get(P3).getY()); //right line
							addLine(pt.get(P3).getX(), pt.get(P3).getY(), pt.get(P4).getX(), pt.get(P4).getY()); //lower line
							addLine(pt.get(P4).getX(), pt.get(P4).getY(), pt.get(P4).getX(), pt.get(P4).getY() - h); //left line
							
							current_node.points.add(new Point(pt.get(P4).getX(), pt.get(P4).getY() - h)); //store points anti-clock wise
							current_node.points.add(new Point(pt.get(P3).getX(), pt.get(P3).getY() - h));
							current_node.points.add(new Point(pt.get(P3).getX(), pt.get(P3).getY()));
							current_node.points.add(new Point(pt.get(P4).getX(), pt.get(P4).getY()));
						}
						else{
							addLine(pt.get(P1).getX(), pt.get(P1).getY(), pt.get(P2).getX(), pt.get(P2).getY()); //upper line
							addLine(pt.get(P2).getX(), pt.get(P2).getY(), pt.get(P2).getX(), pt.get(P2).getY() + h); //right line
							addLine(pt.get(P2).getX(), pt.get(P2).getY() + h, pt.get(P1).getX(), pt.get(P1).getY() + h); //lower line
							addLine(pt.get(P1).getX(), pt.get(P1).getY() + h, pt.get(P1).getX(), pt.get(P1).getY()); //left line
							
							current_node.points.add(new Point(pt.get(P1).getX(), pt.get(P1).getY())); //store points anti-clock wise
							current_node.points.add(new Point(pt.get(P2).getX(), pt.get(P2).getY()));
							current_node.points.add(new Point(pt.get(P2).getX(), pt.get(P2).getY() + h));
							current_node.points.add(new Point(pt.get(P1).getX(), pt.get(P1).getY() + h));
						}
					}
					else{ //parent vertical cut
						if(current_node.getParent().getLeftChild() == current_node){ // left child
							addLine(pt.get(P1).getX(), pt.get(P1).getY(), pt.get(P1).getX() + w, pt.get(P1).getY()); //upper line
							addLine(pt.get(P1).getX() + w, pt.get(P1).getY(), pt.get(P4).getX() + w, pt.get(P4).getY()); //right line
							addLine(pt.get(P4).getX() + w, pt.get(P4).getY(), pt.get(P4).getX(), pt.get(P4).getY()); //lower line
							addLine(pt.get(P4).getX(), pt.get(P4).getY(), pt.get(P1).getX(), pt.get(P1).getY()); //left line
							
							current_node.points.add(new Point(pt.get(P1).getX(), pt.get(P1).getY())); //store points anti-clock wise
							current_node.points.add(new Point(pt.get(P1).getX() + w, pt.get(P1).getY()));
							current_node.points.add(new Point(pt.get(P4).getX() + w, pt.get(P4).getY()));
							current_node.points.add(new Point(pt.get(P4).getX(), pt.get(P4).getY()));
						}
						else{
							addLine(pt.get(P2).getX() - w, pt.get(P2).getY(), pt.get(P2).getX(), pt.get(P2).getY()); //upper line
							addLine(pt.get(P2).getX(), pt.get(P2).getY(), pt.get(P3).getX(), pt.get(P3).getY()); //right line
							addLine(pt.get(P3).getX(), pt.get(P3).getY(), pt.get(P3).getX() - w, pt.get(P3).getY()); //lower line
							addLine(pt.get(P3).getX() - w, pt.get(P3).getY(), pt.get(P2).getX() - w, pt.get(P2).getY()); //left line
							
							current_node.points.add(new Point(pt.get(P2).getX() - w, pt.get(P2).getY())); //store points anti-clock wise
							current_node.points.add(new Point(pt.get(P2).getX(), pt.get(P2).getY()));
							current_node.points.add(new Point(pt.get(P3).getX(), pt.get(P3).getY()));
							current_node.points.add(new Point(pt.get(P3).getX() - w, pt.get(P3).getY()));
						}
						
					}
				}
				
				IndexPair lr = current_node.dimension_marker.get(current_min_index);
				Node lChild = current_node.getLeftChild();
				Node rChild = current_node.getRightChild();
				
				queue.add(lChild);
				queue.add(rChild);
				marker.add(lr.getL());
				marker.add(lr.getR());
			}
		}
		
		//System.out.println("Blocks Orientation: " + blockOrientation);
		
	}
	
	

	public void addLine(int x1, int x2, int x3, int x4) {
	    addLine(x1, x2, x3, x4, Color.black);
	}

	public void addLine(int x1, int x2, int x3, int x4, Color color) {
	    lines.add(new Line(OFFSET + x1 * TIMES, OFFSET + x2 * TIMES, OFFSET + x3 * TIMES, OFFSET + x4 * TIMES, color));
	    //repaint();
	}

	public void clearLines() {
	    lines.clear();
	    repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
	    super.paintComponent(g);
	    for (Line line : lines) {
	        g.setColor(line.color);
	        g.drawLine(line.x1, line.y1, line.x2, line.y2);
	    }
	    
	    for (int i = 0; i < terminal_name.size(); i++) {
	    	g.drawString(terminal_name.get(i), OFFSET + terminal_position.get(i).getX() * TIMES, OFFSET + terminal_position.get(i).getY() * TIMES);
	    }
	}
}
