import java.util.HashMap;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.io.IOException;

public class Table extends HashMap<Integer, HashMap<Integer, String>> implements Comparable<Table>{
    private final int MAX_STRING_LENGTH = 3;
    private final int MAX_COLUMN; 
    private final int ASCII_RANGE_MAX = 126; 
    private final int ASCII_RANGE_MIN = 33;
    private int rowCount;
    private long lastUpdated;
     
    public Table(int column){
        this.MAX_COLUMN = column;
        this.rowCount = 0;
    } 
        
    public int getStringLen(){
        return this.MAX_STRING_LENGTH;
    }
    
    public long getLastUpdated(){
        return lastUpdated;
    } 
    
    public void setLastUpdated(long time){
        this.lastUpdated = time;
    } 
    
    public int getRowCount(){
        return this.rowCount;
    }

    public void incrementRowCount(){
        this.rowCount++;
    }

    public int getMaxColumn(){
        return this.MAX_COLUMN;
    }

    public int getAsciiMax(){
        return this.ASCII_RANGE_MAX;
    }

    public int getAsciiMin(){
        return this.ASCII_RANGE_MIN;
    }
}
