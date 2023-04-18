package Listas.Pistas;

public class Pila {
    public Nodo head;

    public Pila() {
        this.head = null;
    }
    
    public void insertarPista(int col, int fil){
        Nodo nodo = new Nodo(col,fil, this.head);
        this.head =  nodo;
    }
    
    public boolean existePista(int fil, int col){
        
        Nodo temp = head;
        
        while(temp!=null){
            
            if(temp.getFila() == fil && temp.getColumna() == col){
                return true;
            }
            temp = temp.next;
            
        }
        
        return false;
        
    }
    
    
}
