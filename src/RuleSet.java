import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class RuleSet implements FinalVariable{
	
	public HashMap<String, ArrayList<String>> Rules = new HashMap<>();;
	
	private ArrayList<String> nonTerminal = new ArrayList<>();
	private ArrayList<String> innerNodeSymbol = new ArrayList<>();
	private ArrayList<String> terminal = new ArrayList<>();
	
	HashMap<String, BlockDimension> blocks;
	
	private String fileName;
	
	public RuleSet(String rules, HashMap<String, BlockDimension> blockInfo) throws FileNotFoundException{
		blocks = blockInfo;
		fileName = rules;
		populateRules();
	}
	
	private void populateRules() throws FileNotFoundException{
		
		Scanner in = new Scanner(new FileReader(new File(fileName)));
		
		int i;
    	String str = in.nextLine();
    	String[] splitStr = str.split(",");
    	for(i=0; i<splitStr.length; i++){
    		nonTerminal.add(splitStr[i].trim());
        }
    	
    	str = in.nextLine();
    	splitStr = str.split(",");
    	for(i=0; i<splitStr.length; i++){
    		innerNodeSymbol.add(splitStr[i].trim());
        }
    	
//    	str = in.nextLine();
//    	splitStr = str.split(",");
//    	for(i=0; i<splitStr.length; i++){
//    		terminal.add(splitStr[i].trim());
//        }

    	for (String b : blocks.keySet()) {
			terminal.add(b);
		}
    	//System.out.println(terminal);
    	
    	while(in.hasNext()){
    		str = in.nextLine();
    		//System.out.println(str);
    		splitStr = str.split(":=");
    		//System.out.println(splitStr[1]);
    		ArrayList<String> rule = new ArrayList<>();
    		String[] splitRules = splitStr[1].trim().split(",");
    		for(i=0; i<splitRules.length; i++){
    			//System.out.println(splitRules[i].trim());
    			rule.add(splitRules[i].trim());
    		}
    		Rules.put(splitStr[0].trim(), rule);
    	}
    	
        in.close();	
		
	}
	
	public ArrayList<String> getNonTerminalList(){
		return nonTerminal;
	}
	public ArrayList<String> getInnerNodeSymbolList(){
		return innerNodeSymbol;
	}
	public ArrayList<String> getTerminalList(){
		return terminal;
	}
	
//	public static void main(String[] args) {
//		try {
//			RuleSet T = new RuleSet(COMMON_PATH + "rules.txt", blocks);
//			System.out.println(T.Rules);
//		} catch (FileNotFoundException e) {
//			System.err.println("rules.txt file not found!");
//		}
//
//	}

}
