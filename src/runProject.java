import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class runProject implements FinalVariable{

	String blockInfoFile = COMMON_PATH + "blockInfo.txt";
	HashMap<String, BlockDimension> blocks = new HashMap<>();
	
	int max_block_dim = 0;
	
	public runProject() throws FileNotFoundException{
		readBlockInfo();
		
		int best_cost = INFINITY;
		String best_floorplanning = "";
		String best_orientation = "";
		
		int initial_cost = INFINITY;
		String initial_floorplanning = "";
		String initial_orientation = "";
		
		SA bestSA = null;
		
//		System.out.println("Run\tInitial \tCost \t || \tBest \tCost");
//		System.out.println("Initial \tCost \t || \tBest \tCost");

		for(int i = 0; i<RUN; i++){
			if((i + 1) % 10 == 0){
				System.out.print("... ");
				if((i + 1) % 200 == 0){
					System.out.println("");
				}
			}
			//System.out.print((i+1) + "\t");
			SA sa = new SA(INITIAL_TEMP, SCHEDULE_RATE, blocks);
			//System.out.print(sa.getInitialFloorplan() + "\t" + sa.getInitialCost() + "\t || \t" + sa.getBestFloorplan() + "\t" + sa.getBestCost());
			
			if(sa.getBestCost() < best_cost){
				bestSA = sa;
				
				best_cost = sa.getBestCost();
				best_floorplanning = sa.getBestFloorplan();
				best_orientation = sa.getBestOrientation();
				
				initial_cost = sa.getInitialCost();
				initial_floorplanning = sa.getInitialFloorplan();
				initial_orientation = sa.getBestOrientation();
			}
			//System.out.println();
		}
		
		System.out.println();
//		System.out.println("--------------------------------------------------------------------------");
//		System.out.println("Best Result: ");
		System.out.println("Initial \tCost \t || \tBest \tCost");
		System.out.println(initial_floorplanning + "\t" + initial_cost + "\t || \t" + best_floorplanning + "\t" + best_cost);
		System.out.println("--------------------------------------------------------------------------");
//		System.out.println("Block Orientations:");
//		System.out.println("Initial:\n" + initial_orientation);
//		System.out.println("Best:\n" + best_orientation);
//		
		new DrawPlan(bestSA.initialCtReturn, "Initial Floorplan", max_block_dim);
		new DrawPlan(bestSA.ctReturn, "Final Floorplan", max_block_dim);
		
//		System.out.println("Blocks: "+blocks);
		
//		RuleSet R = new RuleSet("//Users//ahmadsabbir//Documents//workspace//SA-FloorPlanning//src//rules.txt");
//		ArrayList<String> innerNodeSymbol = new ArrayList<>(R.getInnerNodeSymbolList());
//		ArrayList<String> nonTerminal = new ArrayList<>(R.getNonTerminalList());
//		ArrayList<String> terminal = new ArrayList<>(R.getTerminalList());
//		
//		Tree t = new Tree(blocks.size(), blocks);
//		System.out.println(t.getPostfixExpression());
//		//System.out.println(t.getTreeBlock());
		
		//test CostTree.java
//		CostTree ct = new CostTree("12H4H35VH", blocks);
//		System.out.println("Final Cost: " + ct.calculateCost());
		
	}
	
	
	public static void main(String[] args) throws FileNotFoundException {
		new runProject();
	}
	
	public void readBlockInfo() throws FileNotFoundException{
		Scanner in = new Scanner(new FileReader(new File(blockInfoFile)));
		
		max_block_dim = 0;
		String str;
		while(in.hasNext()){
			str = in.nextLine();
			String[] info = str.split(" ");
			int w = Integer.parseInt(info[1]);
			int h = Integer.parseInt(info[2]);
			blocks.put(info[0], new BlockDimension(w, h));
			if(max_block_dim < w){
				max_block_dim = w;
			}
			if(max_block_dim < h){
				max_block_dim = h;
			}
		}
		in.close();
	}

}
