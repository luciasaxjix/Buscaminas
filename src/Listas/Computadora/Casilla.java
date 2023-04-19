package Listas.Computadora;

/***
 * almacena las cordenadas de la casilla
 */
public class Casilla {
    
    private int fila;
    private int colunma;

    /**
     * metodo constructor
     * @param fila
     * @param colunma 
     */
    public Casilla(int fila, int colunma) {
        this.colunma = colunma;
        this.fila = fila;
    }

    // getters y setters
    public int getColunma() {
        return colunma;
    }

    public void setColunma(int colunma) {
        this.colunma = colunma;
    }

    public int getFila() {
        return fila;
    }

    public void setFila(int fila) {
        this.fila = fila;
    }
    
    
    
}
