import java.io.IOException;

class Program{
    public static void main(String[] args)throws IOException{
        String filename;
        
        if(args.length < 1){
            System.out.println("Please specify a file. Run program as: java Program [filename/filepath]");
            System.exit(0);           
        }
        else if(args.length > 1){
            System.out.println("Please specify a file");
        }
        
        filename = args[0];
        TableGenerator tg = new TableGenerator(filename);
	    int choice = 3;

	    do{                
            switch(choice){
                case 1:
                    tg.searchTable();
                    break;
                case 2:
                    tg.editTable();
                    break;
                case 3:
                    break;
                case 4:
                    tg.initTable();
                    break;
                case 5:
                    tg.sortRow();
                    break;
                case 6:
                    tg.addNewRow();
                    break;
                case 7:
                    break;
                default:
                    System.out.println("Invalid choice");
            }
            tg.saveTable();            
            choice = tg.printMenu();	
        }while(choice != 7);
    } 
}
