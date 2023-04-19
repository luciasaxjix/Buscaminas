package Listas.Computadora;

/***
 * lista donde se almacenan las casillas
 */
public class Lista {
    
    public Nodo head;

    /**
     * metodo constructor
     */
    public Lista() {
        this.head = null;
    }
    
    /**
     * inserta una nueva casilla
     * @param nueva 
     */
    public void insertarCasilla(Casilla nueva){
        Nodo newNodo = new Nodo(nueva);
        if(this.head == null){
            this.head = newNodo;
        }
        else{
            this.head.prev = newNodo;
            newNodo.next = this.head;
            this.head = newNodo;
            
        }
    }
    /**
     * 
     * @param fil
     * @param col 
     */
    public void eliminarCasilla(int fil, int col){
        Nodo temp = this.head;
        while(temp!=null){
            if(temp.getData().getFila() == fil && temp.getData().getFila() == col){
                if(temp.prev!=null){
                    temp.prev.next = temp.next;
                }
                if(temp.next!=null){
                    temp.next.prev = temp.prev;
                }
                if(temp.next == null && temp.prev == null){
                    this.head = null;
                }
            }
            temp = temp.next;
        }
    }
    
        
    
}
