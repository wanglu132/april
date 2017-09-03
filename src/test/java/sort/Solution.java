package sort;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.*;

public class Solution {
	private static final Scanner scan = new Scanner(System.in);

	public static void main(String args[]) throws Exception {
        // read the string filename
        String filename;
        filename = scan.nextLine();
        
        Map<String, Integer> stat = new HashMap<String, Integer>();
        
        BufferedReader br = null;
        try{
            br = new BufferedReader(new FileReader(filename));
            for(String line = null; (line = br.readLine()) != null; ) {
                String host = line.substring(0, line.indexOf(" "));
                if(stat.containsKey(host)) {
                    stat.put(host, stat.get(host) + 1);
                }else{
                    stat.put(host, 1);
                }
            }
            }finally {
                if(br != null) {
                    br.close();
                }
            }
            
            BufferedWriter bw = null;
            try{
                bw = new BufferedWriter(new FileWriter("E:\\records_a.txt"));
                for(Iterator<String> its = stat.keySet().iterator(); its.hasNext(); ) {
                    String key = its.next();
                    bw.write(key + stat.get(key));
                    bw.newLine();
                }
            }finally {
                if(bw != null) {
                    bw.close();
                }
            }
            
            
        }
        
    
}
