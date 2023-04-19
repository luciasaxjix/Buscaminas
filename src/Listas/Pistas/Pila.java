package Listas.Pistas;
/***
 * pila utilizada para almacenar las pistas
 */
public class Pila {
    public Nodo head;

    /**
     * metodo constructor
     */
    public Pila() {
        this.head = null;
    }
    /***
     * inserta una pista en la pila
     * @param col
     * @param fil 
     */
    public void insertarPista(int col, int fil){
        Nodo nodo = new Nodo(col,fil, this.head);
        this.head =  nodo;
    }
    
    /***
     * verifica si la pista ya existe dentro de la lista
     * @param fil
     * @param col
     * @return true si existe, false si no
     */
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
