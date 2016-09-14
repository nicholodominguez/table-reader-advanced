import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.io.IOException;
import java.lang.NumberFormatException;

class TableGenerator{
    private Table table;
    private Path path;
    private String filename;
    private int maxColumn;
    private Scanner sc = new Scanner(System.in);

    public TableGenerator(String filename) throws IOException{
        this.filename = filename;
        this.path = Paths.get(filename);
        
        if(!Files.exists(this.path)){
            this.initTable();
        }
        else if(!Files.isReadable(this.path) || !Files.isWritable(this.path)){
            System.out.println("Unable to read or write file");
            System.exit(0);
        }
        else    
            this.loadTable();
    }
    
    public void saveTable() throws IOException{        
        if(!Files.exists(this.path)){
            if(this.path.getParent() != null){
                System.out.println("hi");
                Files.createDirectory(this.path.getParent());
            }
            Files.createFile(this.path);
        }    
        
        Files.write(this.path, Integer.toString(this.table.getRowCount()).concat("\n").getBytes());
        Files.write(this.path, Integer.toString(this.table.getMaxColumn()).concat("\n").getBytes(), StandardOpenOption.APPEND);
        this.table.forEach((k,v) ->  {
            v.forEach((i,j) -> {
                String input = String.join("", String.join("",k.toString(),",",i.toString()), ":", j, "\n");
                try{ 
                    Files.write(this.path, input.getBytes(), StandardOpenOption.APPEND);
                }catch(IOException e){
                    System.out.println("File not found");
                }
            });
        });
                
    }
    
    public void loadTable() throws IOException{
        List<String> load, temp;
        int rowCount, columnCount;
        int row, column, lineNumber = 3;
        Pattern linePattern = Pattern.compile("\\d+,\\d+:.{6}");                        //regex for the whole line
        Matcher matcher;
        
        load = Files.readAllLines(this.path);
        
        rowCount = Integer.parseInt(load.get(0));
        columnCount = Integer.parseInt(load.get(1));
        
        this.table = new Table(columnCount);
        this.table.setLastUpdated(Files.getLastModifiedTime(this.path).toMillis());
        
        for(int i = 2, a = 0; i < load.size(); i += columnCount, a++){                  //loop that divides that list into rows
            temp = load.subList(i, i+columnCount);
            String index, data;
            String[]  indices;
            ArrayList<String> dataList = new ArrayList<String>();
            
            for(int b = 0; b < columnCount; b++){
                matcher = linePattern.matcher(temp.get(b));                                   
                boolean hasError = false;
                
                if(matcher.matches()){
                    Pattern indexPattern = Pattern.compile("\\d+,\\d+:");               //regex for the index
                    Matcher m = indexPattern.matcher(temp.get(b));
                    
                    while(m.find()){
                        index = m.group();
                        data = m.replaceFirst("");
                        indices = index.split("[,:]");
                        
                        row = Integer.parseInt(indices[0]);
                        column = Integer.parseInt(indices[1]);
                        if(row != a || column != b){                                     //if index row doesnt match with group
                            hasError = true;
                            break;
                        }
                        dataList.add(data);
                    }
                }
                
                else{
                    hasError = true;    
                }
                
                if(hasError){
                    lineNumber += (a * columnCount) + b;
                    System.out.println("Invalid line in file on line "+lineNumber);
                    System.exit(0);
                }
            }
            
            this.addRow(a, columnCount, dataList);
        }           
    }
    
    
    public void initTable() throws IOException{
        int row, column;
        HashMap<Integer, String> m;

        System.out.println("Define table dimensions");
        row = this.getInputInt("Enter table row: ", true);        
        column = this.getInputInt("Enter table column: ", true);
        
        this.table = new Table(column);
        
        for(int i = 0; i < row; i++){
            this.addRow(i, column);
        }

        this.saveTable();
        this.table.setLastUpdated(Files.getLastModifiedTime(this.path).toMillis());
    }
    
    public void addRow(int rowCount, int column){
        HashMap<Integer, String> m = new HashMap<Integer, String>();
        String input;
        int a;
        Random r = new Random();
                
        for(int j = 0; j < column; j++){  
            input = new String();
            for(int k = 0; k < this.table.getStringLen()*2; k++){
                a = r.nextInt(this.table.getAsciiMax() - this.table.getAsciiMin()) + this.table.getAsciiMin();
                input = String.join("", input, String.valueOf((char) a));
            }
            m.put(j, input.toString());                            
        }
        
        this.table.put(rowCount, m);
        this.table.incrementRowCount();   
    }
    
    public void addRow(int rowCount, int column, ArrayList<String> data){
        HashMap<Integer, String> m = new HashMap<Integer, String>();
        int a;
                
        for(int j = 0; j < column; j++){ 
            //System.out.println(data.get(j)+" "+data.get(j).length());
            m.put(j, data.get(j));                            
        }
        
        this.table.put(rowCount, m);
        this.table.incrementRowCount();
    }
    
    public void addNewRow() throws IOException{
        if(this.hasUpdate()){
            this.loadTable();
            System.out.println();
            System.out.println("Changes in the file detected. Table updated");
            System.out.println();
        }
        
        this.addRow(this.table.getRowCount(), this.table.getMaxColumn());
        System.out.println("New row added.");
        this.saveTable();
    }
    
    public void printTable() throws IOException{
        if(this.hasUpdate()){
            this.loadTable();
            System.out.println();
            System.out.println("Changes in the file detected. Table updated");
            System.out.println();
        }
        
        int maxColumn = this.table.getMaxColumn();
        int stringLen = this.table.getStringLen();
        
        System.out.println();
        this.table.forEach((k,v) -> {
            System.out.print("|  ");
            v.forEach((i,j) -> {
               System.out.print(j.toString().substring(0, stringLen) + " : " + j.toString().substring(stringLen) + "  |  ");
            });
            System.out.println();
        });  
        
        System.out.println();
    }
    
    public int printMenu() throws IOException{
        int input = 0;

        this.printTable();
        System.out.println("------------");
        System.out.println("[1] Search");
        System.out.println("[2] Edit");
        System.out.println("[3] Print");
        System.out.println("[4] Reset");
        System.out.println("[5] Sort");
        System.out.println("[6] Add row");
        System.out.println("[7] Exit");
        System.out.println("------------");
        do{
            input = this.getInputInt("Option: ", true);
            if(input < 1 || input > 7) System.out.println("Input too high or too low.");
        }while(input < 1 || input > 7);

        return input;
    }

    public void searchTable() throws IOException{
        if(this.hasUpdate()){
            this.loadTable();
            System.out.println();
            System.out.println("Changes in the file detected. Table updated");
            System.out.println();
        }
        
        String search, content;
        int searchLen;

        search = this.getInputStr("Search keyword: ");
        searchLen = search.length();

        this.table.forEach((i,j) -> {
            j.forEach((k,l) -> {
                boolean hasMatch = false;
                int counter = 0;
                for(int a = 0; a <= table.getStringLen()*2 - searchLen; a++){
                    int b = a + searchLen;
                    if(search.compareToIgnoreCase(l.substring(a, b)) == 0){
                        hasMatch = true;                    
                        counter += 1;                    
                    }
                }
                if(hasMatch) System.out.println("(" + k + "," + i + ") - " + counter);
                hasMatch = false;
                counter = 0;
            });
        });
    }

    public void editTable() throws IOException{
        if(this.hasUpdate()){
            this.loadTable();
            System.out.println();
            System.out.println("Changes in the file detected. Table updated");
            System.out.println();
            this.printTable();
        }
        
        int row, column, size, input, stringLen = table.getStringLen();
        String replacement, data;

        System.out.println("Enter cell dimension to be edited");
        
        row = this.getInputInt("Enter table row: ", false);        
        column = this.getInputInt("Enter table column: ", false);
        data = table.get(row).get(column);
         
        System.out.println();
        System.out.println("|  " + data.substring(0, stringLen) + " : " + data.substring(stringLen) + "  |");
        
        System.out.println();
        System.out.println("------------");
        System.out.println("[1] Edit key");
        System.out.println("[2] Edit value");
        System.out.println("[3] Edit both (Enter value as <key><value>. No spaces.)");
        System.out.println("------------");
        
        do{
            input = this.getInputInt("Option: ", true);
            if(input < 1 || input > 3) System.out.println("Input too high or too low.");
        }while(input < 1 || input > 3); 
        
        size = input>2?stringLen*2:stringLen;
          
        replacement = this.getInputStr("Replace cell data with: ", size);
        
        switch(input){
            case 1:
                replacement = replacement.concat(data.substring(stringLen));
                break;
            case 2:
                replacement = data.substring(0, stringLen).concat(replacement);
                break;
        }

        table.get(row).replace(column, replacement);   
        this.saveTable();         
    }
    
    public void sortRow() throws IOException{
        if(this.hasUpdate()){
            this.loadTable();
            System.out.println();
            System.out.println("Changes in the file detected. Table updated");
            System.out.println();
        }
        int row;
        int asc;
        
        row = this.getInputInt("Enter table row: ", false);
        
        System.out.println();
        System.out.println("------------");
        System.out.println("[1] Ascending");
        System.out.println("[2] Descending");
        System.out.println("------------");
        System.out.println();
        
        do{
            asc = this.getInputInt("Option: ", true);
            if(asc < 1 || asc > 2) System.out.println("Input too high or too low.");
        }while(asc < 1 || asc > 2); 
        
        
        this.sort(row, asc);
        this.saveTable();         
    }
    
    public void sort(int row, int asc) throws IOException{
        ArrayList<String> rowValues = new ArrayList<String>();        
        HashMap<Integer, String> map = new HashMap<>();
        for(int i = 0; i < this.table.getMaxColumn(); i++){
            rowValues.add(this.table.get(row).get(i));
        }
        
        MergeSorter ms = new MergeSorter(rowValues, asc==1?true:false);
        rowValues = ms.sort();
        
        for(int i = 0; i < this.table.getMaxColumn(); i++){
            map.put(i, rowValues.get(i));
        }
        
        this.table.replace(row, map);
        this.saveTable();
    }

    public int getInputInt(String msg, boolean nonZero){
        boolean isAlpha = true;
        int floor = nonZero?1:0;
        String limit = nonZero?"one":"zero";
        int row = 0;
        
        while(isAlpha){
            try{
                System.out.print(msg);
                row = Integer.parseInt(sc.nextLine());
                if(row < floor){
                    System.out.println("Integer too low, it should be greater than "+limit);    
                }
                else isAlpha = false;
            }catch(NumberFormatException e){
                isAlpha = true;
                System.out.println("Input not an integer");
            }
        }    
        

        return row;    
    }

    public String getInputStr(String msg){
        int maxLen = table.getStringLen();
        boolean isValid = false;
        String input = "";

        while(!isValid){
            System.out.print(msg);
            if(sc.hasNextLine()){
                input = sc.nextLine();
                if(input.length() > maxLen*2){
                    System.out.println("Input too long. Max char of " + maxLen);               
                }
                else{
                    Character c = containsInvalidChar(input);
                    if(c != null){
                        System.out.println("Invalid character " + c);                            
                    }
                    else isValid = true;
                }
            }           
        }

        return input;     
    }

    public String getInputStr(String msg, int size){
        int maxLen = table.getStringLen();
        boolean isValid = false;
        String input = "";

        while(!isValid){
            System.out.print(msg);
            if(sc.hasNextLine()){
                input = sc.nextLine();
                if(input.length() > size){
                    System.out.println("Input too long. No. of char should be " + size);               
                }
                else if(input.length() < size){
                    System.out.println("Input too short. No. of char should be " + size);                 
                }
                else{
                    Character c = containsInvalidChar(input);
                    if(c != null){
                        System.out.println("Invalid character " + c);                            
                    }
                    else isValid = true;
                }
            }           
        }

        return input;     
    }

    public Character containsInvalidChar(String input){
        int max = table.getAsciiMax();
        int min = table.getAsciiMin();
        int inputLen = input.length();

        for(int i = 0; i < inputLen; i++){
            int ascii = (int)input.charAt(i);             
            if(ascii < min || ascii > max){
                return input.charAt(i);
            }
        }

        return null;
    }
    
    
    public boolean hasUpdate() throws IOException{
        long fileUpdateTime = Files.getLastModifiedTime(this.path).toMillis();
        long tableUpdateTime = this.table.getLastUpdated();
        
        return fileUpdateTime!=tableUpdateTime?true:false;
    }
}


