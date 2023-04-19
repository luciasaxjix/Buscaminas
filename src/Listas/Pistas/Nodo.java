package Listas.Pistas;
/***
 * Nodo que se utiliza para almacenar los datos de la pista
 */
public class Nodo {
    
    private int columna;
    private int fila;
    public Nodo next;

    //metodo constrructor
    public Nodo(int fila, int columna, Nodo next) {
        this.columna = columna;
        this.fila = fila;
        this.next = next;
    }

    //getters y setters 
    public int getColumna() {
        return columna;
    }

    public void setColumna(int columna) {
        this.columna = columna;
    }

    public int getFila() {
        return fila;
    }

    public void setFila(int fila) {
        this.fila = fila;
    }
}
