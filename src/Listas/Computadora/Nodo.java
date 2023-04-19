package Listas.Computadora;

/***
 * almacena los datos de la casilla y sus conexiones con los demas nodos
 */
public class Nodo {
    
    private Casilla data;
    public Nodo next;
    public Nodo prev;

    /**
     * metodo constructor
     * @param data 
     */
    public Nodo(Casilla data) {
        this.data = data;
        this.next = null;
        this.prev = null;
    }

    //getters y setters
    
    public Casilla getData() {
        return data;
    }

    public void setData(Casilla data) {
        this.data = data;
    }
    
    
    
    
    
}
