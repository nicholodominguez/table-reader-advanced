import java.util.ArrayList;

public class MergeSorter{
    private ArrayList<String> row;
    private ArrayList<String> temp;
    private boolean ascending = true;
    
    public MergeSorter(ArrayList<String> row, boolean ascending){
        this.row = row;
        this.ascending = ascending;
    }
    
    public ArrayList<String> sort(){
        this.mergeSort(0, row.size()-1);
        return row;
    }
    
    private void mergeSort(int startIndex, int lastIndex){
        if(startIndex < lastIndex){
            int middle = startIndex + (lastIndex - startIndex) / 2;
            mergeSort(startIndex, middle);
            mergeSort(middle + 1, lastIndex);
            merge(startIndex, middle, lastIndex);
        }
    }
    
    private void merge(int startIndex, int middle, int lastIndex){
        this.temp = new ArrayList<String>(row);
        
        int i = startIndex, j = middle + 1, k = startIndex;
        int compareValue;
        while(i <= middle && j <= lastIndex){
            compareValue = this.ascending?temp.get(i).compareTo(temp.get(j)):temp.get(j).compareTo(temp.get(i));
            
            if(compareValue <= 0){
                row.set(k, temp.get(i++));
            }
            else{
                row.set(k, temp.get(j++));
            }
            k++;
        }
        
        while(i <= middle){
            row.set(k++, temp.get(i++));
        }
    }
}
