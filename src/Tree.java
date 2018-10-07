import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Tree implements FinalVariable{
	
	private int no_of_leafNode;
	private int count_treeNode;
	private int no_of_treeNode;
	private Node root;
	private Random random;
	private ArrayList<Node> nodes = new ArrayList<>();
	private String postfixExpression = "";
	private String treeBlock = "";
	HashMap<String, BlockDimension> blocks = new HashMap<>();
	
	public Tree(int n, HashMap<String, BlockDimension> blockInfo) throws FileNotFoundException{
		random = new Random();
		blocks = blockInfo;
		//System.out.println(blocks);
		root = new Node();
		no_of_leafNode = n;
		no_of_treeNode = 2*n-1;
		generateRandomTree();
		assignNodeValues();
	}
	
	private void generateRandomTree(){
		int randomLeaf;
		nodes.add(root);
		count_treeNode = 1;
//		System.out.println("no_of_treeNode: " + no_of_treeNode);
//		System.out.println("size: " + nodes.size());
		while(count_treeNode < no_of_treeNode){
			randomLeaf = random.nextInt(nodes.size());
//			System.out.println("randomLeaf: " + randomLeaf);
			Node currentNode = nodes.get(randomLeaf);
			nodes.remove(randomLeaf);
			
			Node t1 = new Node(currentNode);
			Node t2 = new Node(currentNode);
			currentNode.setChildren(t1, t2);
			currentNode.setType(NON_TERMINAL);//System.out.println("Set Non-Term");
			nodes.add(t1);//System.out.println("t1");
			nodes.add(t2);//System.out.println("t1");
			count_treeNode += 2;
			
//			System.out.println("count_treeNode: " + count_treeNode);
//			System.out.println("size: " + nodes.size());
		}
		
//		System.out.println("others:");
//		for(int i=0;i<nodes.size();i++){
//			System.out.println(nodes.get(i).getType());
//		}
	}
	
	private void assignNodeValues() throws FileNotFoundException{
		RuleSet R = new RuleSet(COMMON_PATH + "rules.txt", blocks);
		ArrayList<String> innerNodeSymbol = new ArrayList<>(R.getInnerNodeSymbolList());
		ArrayList<String> terminal = new ArrayList<>(R.getTerminalList());
		
		//System.out.println(innerNodeSymbol);
		//System.out.println(terminal);
		
		int randomVal;
		nodes.clear();
		nodes.add(root);
		count_treeNode = 0;
		while(count_treeNode < no_of_treeNode){
			Node currentNode = nodes.get(0);
			nodes.remove(0);
			count_treeNode++;
			if(currentNode.getType() == TERMINAL){
				//System.out.println(terminal);
				randomVal = random.nextInt(terminal.size());
				currentNode.setValue(terminal.get(randomVal));
				BlockDimension d = blocks.get(terminal.get(randomVal));
				currentNode.dimension.add(d);
				terminal.remove(randomVal);
			}
			else{ //node is NON_TERMINAL
				randomVal = random.nextInt(innerNodeSymbol.size());
				String symbol = innerNodeSymbol.get(randomVal);
				if(currentNode.getParent() != null && currentNode.getParent().getRightChild() == currentNode){
					String pSymbol = currentNode.getParent().getValue();
					while(pSymbol.equals(symbol)){
						randomVal = random.nextInt(innerNodeSymbol.size());
						symbol = innerNodeSymbol.get(randomVal);
					}
					currentNode.setValue(symbol);
				}
				else{
					currentNode.setValue(symbol);
				}
				Node lChild = currentNode.getLeftChild();
				Node rChild = currentNode.getRightChild();
				
				/*if(rChild.getType() == NON_TERMINAL){
					ArrayList<String> tempInnerSym = new ArrayList<>(innerNodeSymbol);
					tempInnerSym.remove(randomVal);
					randomVal = random.nextInt(tempInnerSym.size());
					symbol = tempInnerSym.get(randomVal);
					rChild.setValue(symbol);
				}
				
				if(lChild.getType() == NON_TERMINAL){
					randomVal = random.nextInt(innerNodeSymbol.size());
					symbol = innerNodeSymbol.get(randomVal);
					lChild.setValue(symbol);
				}*/
				
				nodes.add(lChild);
				nodes.add(rChild);
			}
		}
		//System.out.println("terminal:" + terminal);

	}
	
	public void generatePostfixExpression(Node rNode){
		if(rNode.getType() == TERMINAL){
			//System.out.print(rNode.getValue()+"");
			postfixExpression += rNode.getValue();
			treeBlock += (rNode.getValue() + ": " + rNode.dimension + "\n");
		}
		else{
			generatePostfixExpression(rNode.getLeftChild());
			generatePostfixExpression(rNode.getRightChild());
			//System.out.print(rNode.getValue()+"");
			postfixExpression += rNode.getValue();
		}
	}
	
	public String getPostfixExpression(){
		generatePostfixExpression(root);
		return postfixExpression;
	}
	public String getTreeBlock(){
		return treeBlock;
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		//Tree T = new Tree(26);
		//System.out.println(T.getPostfixExpression());
	}

}

