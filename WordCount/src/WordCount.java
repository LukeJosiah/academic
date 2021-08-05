
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 *
 * @author Luke Johnson
 */
public class WordCount {
    
    public static String IgnoreSym(String search){
        search = search.toLowerCase().trim();
        char wordLength = search.charAt(search.length()-1);
        if(wordLength == ',' || wordLength == '?' || wordLength == '!' || wordLength == '.' || wordLength == '-'){
        return search.substring(0, search.length()-1);        
        }
        return search;
    }
    
    public static void main(String[] args) {
        if(args.length<2){
            System.err.println("Nessasary formatting as follows: java checkFile fileName keyword1 keyword2 and so on");
            System.exit(1);
        }
        
        try {
            File n = new File(args[0]);
            Scanner readNew = new Scanner(n);
            Dictionary<String, Integer> d = new OpenAddressingHashDictionary <>();
            for(int i =1; i <args.length; i++){
                d.put(IgnoreSym(args[i]),0);                
            }
            while(readNew.hasNext()){
                String search = readNew.next();
                search = IgnoreSym(search);
                if(d.contains(search)){
                    d.put(search, d.get(search)+1);
                }
            }
            for(int i =1; i<args.length; i++){
                System.out.println(IgnoreSym(args[i])+" "+d.get(IgnoreSym(args[i])));                
            }
            readNew.close();
        }
        catch (FileNotFoundException ex) {
            System.err.println("No File Found");
            System.exit(2);
        }
    }  
    
}
