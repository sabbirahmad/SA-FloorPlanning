import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;
import java.util.Stack;

public class CostTree implements FinalVariable{
	
	private Node root;
	private String postfixExpression = "";
	String blockOrientation = "";
	private Stack<Node> stack = new Stack<>();
//	ArrayList<String> innerNodeSymbol;
//	ArrayList<String> nonTerminal;
//	ArrayList<String> terminal;
	HashMap<String, BlockDimension> blocks;
	
	HashMap<String, BlockDimension> finalOrientation = new HashMap<>();
	
	//public CostTree(String postfixExpression, HashMap<String, Dimension> blocks, ArrayList<String> innerNodeSymbol, ArrayList<String> nonTerminal, ArrayList<String> terminal){
	public CostTree(String postfixExpression, HashMap<String, BlockDimension> blocks){
		this.postfixExpression = postfixExpression;
		//this.innerNodeSymbol = innerNodeSymbol;
		//this.nonTerminal = nonTerminal;
		//this.terminal = terminal;
		this.blocks = blocks;
		generateTree(this.postfixExpression);
	}
	
	private void generateTree(String expression){
		//System.out.println(expression);
		String exp = new String(expression);
		while(exp.length() > 0){
			String start = exp.substring(0, 1);
			exp = exp.substring(1);
			
			if(blocks.containsKey(start)){ //means this is a terminal
				BlockDimension d = blocks.get(start);
								
				Node n = new Node(); //create Node
				n.setValue(start);
				n.setType(TERMINAL);
				n.dimension.add(d);
				
				if(d.getWidth() != d.getHeight()){ //not a square block
					BlockDimension d_opp = new BlockDimension(d.getHeight(), d.getWidth());
					n.dimension.add(d_opp);
				}
				
				stack.push(n);
			}
			else{ //non-terminal
				Node n = new Node(); //create Node
				n.setValue(start);
				n.setType(NON_TERMINAL);
				
				Node r = stack.pop();
				Node l = stack.pop();
				
				n.setChildren(l, r);
				l.setParent(n);
				r.setParent(n);
				
				//calculate cost
				int n_l = l.dimension.size();
				int n_r =  r.dimension.size();
				
				if(n.getValue().equals("H")){ //check if horizontal slice
					for(int i = 0; i < n_l; i++){
						BlockDimension di = l.dimension.get(i);
						for(int j = 0; j < n_r; j++){
							BlockDimension dj = r.dimension.get(j);
							BlockDimension dij = new BlockDimension(di.getWidth() > dj.getWidth() ? di.getWidth() : dj.getWidth(), di.getHeight() + dj.getHeight());
							n.dimension.add(dij);
							n.dimension_marker.add(new IndexPair(i, j));
							//check domination
							for(int k = 0; k < n.dimension.size() - 1; k++){
								if(n.dimension.get(k).dominates(dij) == 1){
									n.dimension.remove(n.dimension.size() - 1);
									n.dimension_marker.remove(n.dimension_marker.size() - 1);
									break;
								}
								else if(n.dimension.get(k).dominates(dij) == -1){
									n.dimension.remove(k);
									n.dimension_marker.remove(k);
								}
							}
						}
					}
				}
				else{ //vertical slice
					for(int i = 0; i < n_l; i++){
						BlockDimension di = l.dimension.get(i);
						for(int j = 0; j < n_r; j++){
							BlockDimension dj = r.dimension.get(j);
							BlockDimension dij = new BlockDimension(di.getWidth() + dj.getWidth(), di.getHeight() > dj.getHeight() ? di.getHeight() : dj.getHeight());
							n.dimension.add(dij);
							n.dimension_marker.add(new IndexPair(i, j));
							//check domination
							for(int k = 0; k < n.dimension.size() - 1; k++){
								if(n.dimension.get(k).dominates(dij) == 1){
									n.dimension.remove(n.dimension.size() - 1);
									n.dimension_marker.remove(n.dimension_marker.size() - 1);
									break;
								}
								else if(n.dimension.get(k).dominates(dij) == -1){
									n.dimension.remove(k);
									n.dimension_marker.remove(k);
								}
							}
						}
					}					
				}
				
				stack.push(n);
				root = n;
			}
			
		}
	}
	
	public String trackBlocks(){
		trackBlocksN(root);
		return blockOrientation;
	}
	
	private void trackBlocksN(Node n){
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
				BlockDimension d = current_node.dimension.get(current_min_index);
				blockOrientation += current_node.getValue() + ": " + d.toString() + "\n";
				finalOrientation.put(current_node.getValue(), current_node.dimension.get(current_min_index));
			}
			else{
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
	
	private int calculateNodeCost(Node n){
		int size = n.dimension.size();
		int min = n.dimension.get(0).getArea();
		for(int i = 1; i < size; i++){
			if(min > n.dimension.get(i).getArea()){
				min = n.dimension.get(i).getArea();
			}
		}
		return min;
	}
	
	public Node getRoot(){
		return root;
	}
	
	public int calculateCost(){
		return calculateNodeCost(root);
	}
}
