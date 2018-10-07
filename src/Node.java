import java.util.ArrayList;

public class Node implements FinalVariable{
	private Node parent = null;
	private Node leftChild = null;
	private Node rightChild = null;
	private int type;
	private String value;
	
	public ArrayList<BlockDimension> dimension = new ArrayList<>();
	public ArrayList<IndexPair> dimension_marker = new ArrayList<>();
	
	public ArrayList<Point> points = new ArrayList<>();
	
	public Node(){
		parent = null;
		leftChild = null;
		rightChild = null;
		type = TERMINAL;
	}
	
	public Node(Node p){
		parent = p;
		leftChild = null;
		rightChild = null;
		type = TERMINAL;
	}
	
	public void setChildren(Node lChild, Node rChild){
		leftChild = lChild;
		rightChild = rChild;
	}
	
	public void setType(int t){
		type = t;
	}
	
	public int getType(){
		return type;
	}
	
	public void setValue(String v){
		value = v;
	}
	
	public String getValue(){
		return value;
	}
	
	public Node getLeftChild(){
		return leftChild;
	}
	
	public Node getRightChild(){
		return rightChild;
	}
	
	public void setParent(Node p){
		parent = p;
	}
	
	public Node getParent(){
		return parent;
	}
}
