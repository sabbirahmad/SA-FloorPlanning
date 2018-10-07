import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class SA implements FinalVariable{
	private double initial_T;
	private double rate;
	HashMap<String, BlockDimension> blocks;
	private String initial_expression = "";
	private String initial_orientation = "";
	private int initial_cost;
	private Random random;
	
	private String best_floorplan = "";
	private String best_orientation = "";
	private int best_cost;
	
	public CostTree ctReturn;
	public CostTree initialCtReturn;
	
	public SA(double t_not, double r, HashMap<String, BlockDimension> blockInfo) throws FileNotFoundException{
		initial_T = t_not;
		rate = r;
		blocks = blockInfo;
		
		random = new Random();
		
		//System.out.println("NEW RUN");
		
		initializeSA();
		runSA(initial_expression);
		//runSA("deVabcHVHfgVhiVHV");
		//runSA("fhgHVkijVHVbaHcVedHVH");
		
		//runSA("12H4H35VH");
	}
	
	public String getBestFloorplan(){
		return best_floorplan;
	}
	public String getBestOrientation(){
		return best_orientation;
	}
	public int getBestCost(){
		return best_cost;
	}
	
	public String getInitialFloorplan(){
		return initial_expression;
	}
	public String getInitialOrientation(){
		return initial_orientation;
	}
	public int getInitialCost(){
		return initial_cost;
	}
	
	private void initializeSA() throws FileNotFoundException{
		//generate tree randomly for a string
		Tree t = new Tree(blocks.size(), blocks); //reads rules file, so file exception
		//System.out.println(t.getPostfixExpression());
		initial_expression = t.getPostfixExpression();
	}
	
	private void runSA(String initialState){
		boolean done = false;
		int randomIndex;
		String new_state = "";
		String current_state = new String(initialState);
		CostTree ct = new CostTree(current_state, blocks);
		best_floorplan = current_state;
		best_cost = ct.calculateCost();
		best_orientation = ct.trackBlocks();
		
		initial_orientation = best_orientation;//set the initial orientation
		initial_cost = best_cost;//set initial cost
		initialCtReturn = new CostTree(current_state, blocks);
		initialCtReturn.trackBlocks();
		
		//System.out.print(best_floorplan + "\t" + best_cost + "\t || \t");
		//System.out.println("initial_cost: " + best_cost);
		
		//calculate number of non-terminals
		ArrayList<Integer> cumulative_sum_temp = new ArrayList<>();
		ArrayList<Integer> cumulative_sum_op = new ArrayList<>();
		int op = 0;
		for(int i = 0; i < current_state.length() - 1; i++){
			if(!blocks.containsKey(current_state.substring(i, i + 1))){
				op++;
			}
			cumulative_sum_op.add(op);
		}
		op++;
		cumulative_sum_op.add(op);
		
//		System.out.println(current_state);
		//System.out.println("ini_cumu: " + cumulative_sum_op);
		
		double temp = initial_T;
		
		while(temp > SCHEDULE_LIMIT){
			done = false;
			int selectOperation = 1 + random.nextInt(TYPES_OF_OPERATION);
			
			cumulative_sum_temp = new ArrayList<>(cumulative_sum_op);
			//System.out.println("loop starts:\n\t" + current_state);
			
			
			//check which operation
			if(selectOperation == TYPE_ONE_OP){ //select two adjacent operands
				while(true){
					randomIndex = random.nextInt(current_state.length() - 2); // -2 because last index always NON-TERMINAL
					
					if(blocks.containsKey(current_state.substring(randomIndex, randomIndex + 1)) && blocks.containsKey(current_state.substring(randomIndex + 1, randomIndex + 2))){
						if(randomIndex == 0){
							new_state = current_state.substring(randomIndex + 1, randomIndex + 2) + current_state.substring(randomIndex, randomIndex + 1) + current_state.substring(randomIndex + 2);
						}
						else{
							new_state = current_state.substring(0, randomIndex) + current_state.substring(randomIndex + 1, randomIndex + 2) + current_state.substring(randomIndex, randomIndex + 1) + current_state.substring(randomIndex + 2);
						}
						//System.out.println("OP1");
//						System.out.println("\t" + current_state);
//						System.out.println("\t" + new_state);
						break;
					}
				}
			}
			else if(selectOperation == TYPE_TWO_OP){ //select a non-zero chain
				int i, j;
				randomIndex = random.nextInt(current_state.length());
				String chain = "";
				while(true){
					if(current_state.substring(randomIndex, randomIndex + 1).equals("H")){
						chain += "V";
						break;
					}
					else if(current_state.substring(randomIndex, randomIndex + 1).equals("V")){
						chain += "H";
						break;
					}
					randomIndex = random.nextInt(current_state.length());
				}
				//System.out.println("randomIndex: " + randomIndex);
				for(i = randomIndex + 1; i < current_state.length(); i++){ //randomIndex to end
					if(current_state.substring(i, i + 1).equals("H")){
						chain += "V";
					}
					else if(current_state.substring(i, i + 1).equals("V")){
						chain += "H";
					}
					else{
						break;
					}
				}
				for(j = randomIndex - 1; j > -1; j--){ //randomIndex to start
					if(current_state.substring(j, j + 1).equals("H")){
						chain = "V" + chain;
					}
					else if(current_state.substring(j, j + 1).equals("V")){
						chain = "H" + chain;
					}
					else{
						break;
					}
				}
				
				new_state = current_state.substring(0, j + 1) + chain + current_state.substring(i);
				
				//System.out.println("OP2");
//				System.out.println("\t" + current_state);
//				System.out.println("\t" + new_state);
			}
			else{ //TYPE_THREE_OP: select two adjacent operator and operand
//				System.out.println("OP3");
				randomIndex = 2; //just a initialization. no
				done = false;
				cumulative_sum_temp = new ArrayList<>(cumulative_sum_op);
				//System.out.println("cum_actu: " + cumulative_sum_op);
				//System.out.println("cum_temp: " + cumulative_sum_temp);
				
				int count_loop = 0;
				while(!done){
					randomIndex = random.nextInt(current_state.length() - 2); //end is always non-terminal
//					System.out.println("cs: " + current_state);
//					System.out.println("randomIndex: " + randomIndex);
					if(blocks.containsKey(current_state.substring(randomIndex, randomIndex + 1)) && !blocks.containsKey(current_state.substring(randomIndex + 1, randomIndex + 2))){
						//System.out.println("N_k+1: " + cumulative_sum_temp + "randomIndex: " + randomIndex);
						if(!(current_state.substring(randomIndex - 1, randomIndex).equals(current_state.substring(randomIndex + 1, randomIndex + 2))) && ((2*cumulative_sum_temp.get(randomIndex + 1)) < (randomIndex + 1))){
							cumulative_sum_temp.add(randomIndex, cumulative_sum_temp.get(randomIndex) + 1);
							cumulative_sum_temp.remove(randomIndex + 1);
							//System.out.println("change + : " + cumulative_sum_temp + "randomIndex: " + randomIndex);
							//System.out.println("OP3-1");
							done = true;
						}
					}
					else if(!blocks.containsKey(current_state.substring(randomIndex, randomIndex + 1)) && blocks.containsKey(current_state.substring(randomIndex + 1, randomIndex + 2))){
						//System.out.println("N_k+1: " + cumulative_sum_temp + "randomIndex: " + randomIndex);
						if(!(current_state.substring(randomIndex, randomIndex + 1).equals(current_state.substring(randomIndex + 2, randomIndex + 3)))){
							cumulative_sum_temp.add(randomIndex, cumulative_sum_temp.get(randomIndex) - 1);
							cumulative_sum_temp.remove(randomIndex + 1);
							//System.out.println("change - : " + cumulative_sum_temp + "randomIndex: " + randomIndex);
							//System.out.println("OP3-2");
							done = true;
						}
					}
					count_loop++;
					if(count_loop > 3 * current_state.length()){
						break;
					}
				}
				if(done == true){
					new_state = current_state.substring(0, randomIndex) + current_state.substring(randomIndex + 1, randomIndex + 2) + current_state.substring(randomIndex, randomIndex + 1) + current_state.substring(randomIndex + 2);
				}
				else{
					new_state = new String(current_state);
				}
			}
			
//			System.out.println("\tc0: " + current_state);
//			current_state = new_state;
//			System.out.println("loop ends: ");
//			System.out.println("\tn: " + new_state);
//			System.out.println("\tc1: " + current_state);
//			System.out.println("\t" + cumulative_operator);
			
			//System.out.println("c_s: " + current_state);
			//System.out.println("n_s: " + new_state);
			//System.out.println("");
			
			//System.out.println("P: " + cumulative_sum_op);
			//System.out.println("N: " + cumulative_sum_temp);
			//System.out.println("");
			
			//cumulative_sum_op = new ArrayList<>(calculateCumulativeSum(current_state));
			//System.out.println(current_state);
			//System.out.println(cumulative_sum_op);
			
			
			CostTree cT_current = new CostTree(current_state, blocks);
			CostTree cT_new = new CostTree(new_state, blocks);
			
			//System.out.println("");
			
			int current_cost = cT_current.calculateCost();
			int new_cost = cT_new.calculateCost();
			
			int del_cost = new_cost - current_cost;
			
			if(del_cost < 0 || random.nextDouble() < Math.exp(-del_cost/temp)){
				current_state = new_state;
				current_cost = new_cost;
				cT_current = cT_new;
				
				
				//cumulative_sum_op = new ArrayList<>(calculateCumulativeSum(current_state));
				//cumulative_sum_op = new ArrayList<>(cumulative_sum_temp);
				
				if(done == true){
					cumulative_sum_op = new ArrayList<>(cumulative_sum_temp);
				}
				if(current_cost < best_cost){
					best_cost = current_cost;
					best_floorplan = new String(current_state);
					best_orientation = cT_current.trackBlocks();
					ctReturn = cT_current;
				}
			}
			
			temp = rate * temp;
		}
		
//		System.out.println("best_floorplan: " + best_floorplan);
//		System.out.println("best_cost: " + best_cost);
//		System.out.println("best_orientation:\n" + best_orientation);
//		System.out.print(best_floorplan + "\t" + best_cost);
	}
	
	private ArrayList<Integer> calculateCumulativeSum(String cs){
		ArrayList<Integer> c_sum = new ArrayList<>();
		int op = 0;
		for(int i = 0; i < cs.length() - 1; i++){
			if(!blocks.containsKey(cs.substring(i, i + 1))){
				op++;
			}
			c_sum.add(op);
		}
		op++;
		c_sum.add(op);
		
		return c_sum;
	}
	
}

