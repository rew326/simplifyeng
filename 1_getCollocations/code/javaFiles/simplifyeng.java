package mutualInformation;

import java.util.*;
import java.io.*;
import java.nio.charset.*;
//import edu.stanford.nlp.simple.*;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import org.json.*;

/**
 *
 * @author VanMinh
 */
public class simplifyeng {
    // main
    public static void main(String[] args)
        throws FileNotFoundException, IOException, JSONException, ClassNotFoundException
    {          
        System.out.println("Copyright @ 2016 by Van Minh Nguyen");
        // Initialize the tagger
        MaxentTagger tagger = new MaxentTagger("." + File.separator + "lib" + File.separator + "english-left3words-distsim.tagger");
        
        // <editor-fold defaultstate="collapsed" desc=" Parse global variables ">
        String folderCorpusPath = "";
        String output = "";
        //String inputFolderPath = "";
        
        /*
        try
        {
            for (String arg : args)
            {
                if (arg.contains("corpus"))
                {
                    String[] corpusArray = arg.split(";");
                    folderCorpusPath = corpusArray[1];
                }
                if (arg.contains("output"))
                {
                    String[] outputArray = arg.split(";");
                    output = outputArray[1];
                }
                if (arg.contains("input"))
                {
                    String[] inputArray = arg.split(";");
                    inputFolderPath = inputArray[1];
                }
            }
        }
        catch (Exception e)
        {
            System.out.println(e);
        } 
        // </editor-fold>
        */
        folderCorpusPath = "." + File.separator + ".." + File.separator + ".." + File.separator + "corpus" + File.separator +"inputCorpus";
        output = "." + File.separator + ".." + File.separator + "outputCollocations" + File.separator + "collocations.csv";
        
        System.out.println("Corpus input: " + folderCorpusPath);
        
        // <editor-fold defaultstate="collapsed" desc=" Get corpus documents ">
        File folder = new File(folderCorpusPath);
        // files in the folder
        File[] listOfFiles = folder.listFiles();/*(new FileFilter()
                             {
                                 @Override
                                 public boolean accept(File pathname)
                                 {
                                     String name = pathname.getName().toLowerCase();
                                     return name.endsWith(".txt") && pathname.isFile() && name.contains(".0.") && name.contains(".en.");
                                 }
                             });*/
        if (listOfFiles.length == 0)
        {
            folderCorpusPath = "." + File.separator + ".." + File.separator + ".." + File.separator + "corpus" + File.separator +"exampleCorpus";
            System.out.println("Corpus example: " + folderCorpusPath);
            folder = new File(folderCorpusPath);
            listOfFiles = folder.listFiles();
        }
        
        System.out.println("List of files retrieved");
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc=" Get corpus data ">
        String wholeCorpus = "";
        for (int i = 0; i < listOfFiles.length; i++)
        {
            wholeCorpus += addContent(listOfFiles, i);
        }
        
        System.out.println("Corpus data retrieved.");
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc=" Get unique words ">
        HashMap<String, Integer> uniqueCollocations = new HashMap<String, Integer>();
        HashMap<String, Integer> uniqueWords = new HashMap<String, Integer>();
        
        wholeCorpus = wholeCorpus.replace(".", " . ");
        wholeCorpus = wholeCorpus.replace(",", " , ");
        wholeCorpus = wholeCorpus.replace(":", " : ");
        wholeCorpus = wholeCorpus.replace(";", " ; ");
        wholeCorpus = wholeCorpus.replace("!", " ! ");
        wholeCorpus = wholeCorpus.replace("?", " ? ");
        wholeCorpus = wholeCorpus.replace("\"", " \" ");
        wholeCorpus = wholeCorpus.replace(")", " ) ");
        wholeCorpus = wholeCorpus.replace("(", " ( ");
        String[] wholeCorpusArray = wholeCorpus.split("\\s+");
        
        for (int i = 0; i < wholeCorpusArray.length - 1; i++)
        {
            if (wholeCorpusArray[i].charAt(0) != ' ' )
            {
                if (i == 0)
                {
                    uniqueWords.put(wholeCorpusArray[i], 1);
                }

                if (uniqueWords.containsKey(wholeCorpusArray[i + 1]))
                {
                    int value = uniqueWords.get(wholeCorpusArray[i + 1]);
                    uniqueWords.put(wholeCorpusArray[i + 1], value + 1);
                }
                else
                {
                    uniqueWords.put(wholeCorpusArray[i + 1], 1);
                }
                        
                if (tagger.tagString(wholeCorpusArray[i]).contains("NN") || tagger.tagString(wholeCorpusArray[i]).contains("JJ"))
                {
                    if (tagger.tagString(wholeCorpusArray[i + 1]).contains("NN") || tagger.tagString(wholeCorpusArray[i + 1]).contains("JJ"))
                    {
                        String potentialCollocation = wholeCorpusArray[i] + " " + wholeCorpusArray[i + 1];
                
                        if (uniqueCollocations.containsKey(potentialCollocation))
                        {
                            int value = uniqueCollocations.get(potentialCollocation) + 1;
                            uniqueCollocations.put(potentialCollocation, value);
                        }
                        else
                        {
                            uniqueCollocations.put(potentialCollocation, 1);
                        }
                    }
                }
            }
        }
        // </editor-fold>
                
        
        // <editor-fold defaultstate="collapsed" desc=" Get unique words ">
        List<Collocation> collocations = new ArrayList<Collocation>();
        for (String key : uniqueCollocations.keySet())
        {
            String[] parts = key.split("\\s+");
            double p1, p2 = 0;
            
            p1 = uniqueWords.get(parts[0]);
            p2 = uniqueWords.get(parts[1]);
            
            Collocation newCollocation = new Collocation();
            newCollocation.collocation = key;
            newCollocation.score =  Math.log((uniqueCollocations.get(key) * wholeCorpusArray.length) / (p1 * p2));
            
            collocations.add(newCollocation);
        }
        System.out.println("Unique words list generated");
        // </editor-fold>
        
        Collections.sort(collocations, new Comparator<Collocation>()
        {
            public int compare(Collocation collocation1, Collocation collocation2)
            {
                if (collocation1.score == collocation2.score)
                {
                    return 0;
                }
                return collocation1.score < collocation2.score ? -1 : 1;
            }
        });
        
        System.out.println("Unique words sorted by score");
        
        // <editor-fold defaultstate="collapsed" desc=" Create wordsList file ">
        // init test output
        String data = "";
        int i = 0;
        for (Collocation uniqueCollocation : collocations)
        {
            i++;
            System.out.print(i + " / " + collocations.size() + "\n");
            if (!uniqueCollocation.collocation.matches(".*[^a-zA-Z -].*"))
            {
                String line = uniqueCollocation.collocation + "," + uniqueCollocation.score + "\n";
                data += line;
            }
        }
        
        //File file = new File(output + "collocations.csv");
        File file = new File(output);
        if (!file.exists())
        {
            file.createNewFile();
        }
        else
        {
            file.delete();
            file.createNewFile();
        }
        
        // write to test output
        FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write(data);
        bufferedWriter.close();
        // </editor-fold>
    }
    
    // fileToTokens
    public static String addContent(File[] corpusList, int i) 
            throws FileNotFoundException
    {
        File file = corpusList[i];
        System.out.println((i + 1) + ": " + file.getName());
        FileInputStream fis = new FileInputStream(file.getAbsoluteFile());
        InputStreamReader isr = new InputStreamReader(fis, Charset.forName("windows-1252"));
        String fileContentString = "";
        try
        {
            // get content of the file
            while (isr.ready())
            {
                fileContentString += (char) isr.read();
            }
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        
        return fileContentString;
    } // fileToTokens
}