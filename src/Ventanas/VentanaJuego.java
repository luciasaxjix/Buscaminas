package Ventanas;

import Listas.Pistas.Nodo;
import Listas.Pistas.Pila;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;

public class VentanaJuego extends javax.swing.JFrame {

    //Constantes que utiliza el juego
    private int SIZE = 8;
    private int CANTIDAD_MINAS = 1;

    private int segundos = 0, minutos = 0, horas = 0;
    private long inicio = 0;
    private Thread hilo;

    private JButton[][] botonesJuego;

    private int matrizMinas[][];
    private int matrizMinasRevisadas[][];

    private int minasEncontradas = 0;

    private boolean gameover = false;
    private Comunicador comunicador = new Comunicador();

    private int fila_control = 0;
    private int colum_control = 0;

    private int turnos_pista = 0;
    private Pila listaPistas = new Pila();

    /**
     * Crea nuevo form de VentanaJuego, ademas inicia el tablero
     */
    public VentanaJuego() {
        initComponents();
        crearTablero(SIZE, CANTIDAD_MINAS);
    }

    /**
     * *
     * Muestra la primera pista disponible, cambiando el color del boton a azul
     */
    private void mostrarPista() {

        Nodo temp = listaPistas.head;

        while (temp != null) {
            if (botonesJuego[temp.getFila()][temp.getColumna()].isEnabled()) {
                botonesJuego[temp.getFila()][temp.getColumna()].setBackground(Color.blue);
                break;
            }
            temp = temp.next;

        }

    }

    /**
     * *
     * Verifica que hayan pasado 5 turnos del jugador para agregar una pista a
     * la lista
     */
    private void cargarPista() {
        if (turnos_pista >= 5) {
            turnos_pista = 0;
            //busca aleatoriamente casillas que no contengan minas
            while (true) {
                int fil, col;
                Random ran = new Random();
                fil = ran.nextInt(SIZE);
                col = ran.nextInt(SIZE);

                if (botonesJuego[fil][col].isEnabled() && !checkCasilla(fil, col)) {
                    if (!listaPistas.existePista(fil, col)) {
                        listaPistas.insertarPista(fil, col);
                        break;
                    }
                }
            }

        }
    }

    public void finJuego() {
        boolean gano = true;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if ((botonesJuego[i][j].isEnabled() && matrizMinas[i][j]==0)) {
                    gano = false;
                }
            }
        }

        if (gano) {
            JOptionPane.showMessageDialog(null, "El juego ha acabado.");
        }

    }

    /**
     * *
     * Crea el tablero y sus elementos
     *
     * @param tam
     * @param cantidadMinas
     */
    private void crearTablero(int tam, int cantidadMinas) {
        matrizMinas = new int[tam][tam];
        matrizMinasRevisadas = new int[tam][tam];

        //Ciclo que agrega minas de manera aleatoria
        for (int i = 0; i < cantidadMinas; i++) {
            boolean agregado = false;
            //verifica que la mina no exista 
            do {
                int f, c;
                f = (int) (Math.random() * 8);
                c = (int) (Math.random() * 8);
                if (matrizMinas[f][c] == 0) {
                    matrizMinas[f][c] = 1;
                    agregado = true;
                }
            } while (!agregado);
        }
        //matriz donde se almacenan los botones
        botonesJuego = new JButton[tam][tam];

        //ciclo que recorre la matriz de botones y le agrega sus propiedades y funciones
        for (int i = 0; i < tam; i++) {
            for (int j = 0; j < tam; j++) {
                botonesJuego[i][j] = new JButton();
                botonesJuego[i][j].setBackground(Color.GREEN);
                botonesJuego[i][j].setBorder(BorderFactory.createBevelBorder(0));

                //actionLister para la pulsacion del boton
                botonesJuego[i][j].addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (!gameover) {
                            for (int i = 0; i < 8; i++) {
                                for (int j = 0; j < 8; j++) {
                                    if (botonesJuego[i][j] == e.getSource() && botonesJuego[i][j].getBackground() != Color.RED) {
                                        matrizMinasRevisadas[i][j] = 1;
                                        if (checkCasilla(i, j)) {
                                            comunicador.escribir(2);
                                            JOptionPane.showMessageDialog(null, "Has perdido.");

                                            gameover = true;
                                        } else {
                                            verificarCasilla(i, j);
                                            comunicador.escribir(1);
                                            turnos_pista++;
                                            cargarPista();
                                            finJuego();
                                            //inicia turno de la computadora
                                            turnoComputadora();
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
                );
                //mouse listener para detectar los clicks
                botonesJuego[i][j].addMouseListener(new MouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        //verifica que el boton pulsado sea el derecho
                        if (e.getButton() == 3 && !gameover) {
                            //ciclo para encontrar el boton pulsado
                            for (int i = 0; i < 8; i++) {
                                for (int j = 0; j < 8; j++) {
                                    if (botonesJuego[i][j] == e.getSource()) {
                                        //condicion para cambiar el color del boton
                                        if (botonesJuego[i][j].getBackground() != Color.RED) {
                                            botonesJuego[i][j].setBackground(Color.RED);
                                            comunicador.escribir(3);
                                            minasEncontradas += 1;
                                            finJuego();

                                            jTextField1.setText(String.valueOf(minasEncontradas));
                                        } else {
                                            botonesJuego[i][j].setBackground(Color.GREEN);
                                            minasEncontradas -= 1;
                                            finJuego();
                                            jTextField1.setText(String.valueOf(minasEncontradas));
                                        }
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                    }

                });

                //agrega el boton al tablero
                tableroPanel.add(botonesJuego[i][j]);

            }
        }

        //conecta el arduino, si falla el programa sigue
        conectarArduino();

        //inicia el cronometro
        iniciarCronometro();
    }

    /**
     * *
     * Trata de conectar el arduino, si falla no se inicia el hilo del
     * comunicador
     */
    private void conectarArduino() {
        comunicador.conectar(comunicador.obtenerPuerto());
        comunicador.iniciarIO();
        comunicador.initListener();
        //si se conecta inicia el hilo
        if (comunicador.getConectado()) {
            iniciarControles();
            hilo.start();
        }
    }

    /**
     * *
     * inicia el receptor de senales enviadas por arduino por medio del puerto
     * serial
     */
    void iniciarControles() {

        //pone borde diferente para indicar donde se encuentra el jugador
        botonesJuego[fila_control][colum_control].setBorder(BorderFactory.createBevelBorder(0, Color.green, Color.orange, Color.red, Color.blue));

        //hilo que se encarga de leer las peticiones
        hilo = new Thread() {
            @Override
            public void run() {
                while (!gameover) {
                    try {

                        // si existe un nuevo evento , lo lee y realiza la accion correspondiente
                        if (comunicador.isNuevoEvento()) {
                            String[] dato = comunicador.getDato();
                            if (dato != null && dato.length == 1) {
                                switch (dato[0]) {
                                    // cada numero representa una direcion
                                    //0 = izquierda, 1= arriba, 2 = derecha, 3 = abajo
                                    case "0":
                                        comunicador.escribir(0);
                                        if (colum_control > 0) {
                                            botonesJuego[fila_control][colum_control].setBorder(BorderFactory.createBevelBorder(0));
                                            colum_control--;
                                            botonesJuego[fila_control][colum_control].setBorder(BorderFactory.createBevelBorder(0, Color.green, Color.orange, Color.red, Color.blue));
                                        }

                                        break;
                                    case "1":
                                        comunicador.escribir(0);
                                        if (fila_control > 0) {
                                            botonesJuego[fila_control][colum_control].setBorder(BorderFactory.createBevelBorder(0));
                                            fila_control--;
                                            botonesJuego[fila_control][colum_control].setBorder(BorderFactory.createBevelBorder(0, Color.green, Color.orange, Color.red, Color.blue));
                                        }
                                        break;
                                    case "2":
                                        comunicador.escribir(0);
                                        if (colum_control < 7) {
                                            botonesJuego[fila_control][colum_control].setBorder(BorderFactory.createBevelBorder(0));
                                            colum_control++;
                                            botonesJuego[fila_control][colum_control].setBorder(BorderFactory.createBevelBorder(0, Color.green, Color.orange, Color.red, Color.blue));
                                        }
                                        break;
                                    case "3":
                                        comunicador.escribir(0);
                                        if (fila_control < 7) {
                                            botonesJuego[fila_control][colum_control].setBorder(BorderFactory.createBevelBorder(0));
                                            fila_control++;
                                            botonesJuego[fila_control][colum_control].setBorder(BorderFactory.createBevelBorder(0, Color.green, Color.orange, Color.red, Color.blue));
                                        }
                                        break;

                                    //seleciona una casilla y verifica su estado
                                    case "4":

                                        if (botonesJuego[fila_control][colum_control].getBackground() != Color.RED) {
                                            matrizMinasRevisadas[fila_control][colum_control] = 1;
                                            if (checkCasilla(fila_control, colum_control)) {
                                                comunicador.escribir(2);
                                                JOptionPane.showMessageDialog(null, "Has perdido.");
                                                gameover = true;

                                            } else {
                                                verificarCasilla(fila_control, colum_control);
                                                comunicador.escribir(1);
                                                finJuego();
                                                turnoComputadora();
                                            }
                                        }

                                        break;
                                    //marca la casilla
                                    case "5":

                                        if (botonesJuego[fila_control][colum_control].getBackground() != Color.RED) {
                                            botonesJuego[fila_control][colum_control].setBackground(Color.RED);
                                            minasEncontradas += 1;
                                            comunicador.escribir(3);
                                            finJuego();
                                            jTextField1.setText(String.valueOf(minasEncontradas));
                                        } else {
                                            botonesJuego[fila_control][colum_control].setBackground(Color.GREEN);
                                            minasEncontradas -= 1;
                                            jTextField1.setText(String.valueOf(minasEncontradas));
                                        }

                                        break;
                                }
                                comunicador.setNuevoEvento(false);
                            }
                        }
                        //si el juego termino, envia senal a arduino
                        Thread.sleep(200);
                    } catch (InterruptedException ex) {
                    }
                }
            }

        };

    }

    /**
     * *
     * inicia el cronometro y lo actualiza en pantalla
     */
    void iniciarCronometro() {
        Runnable runna;
        runna = new Runnable() {
            public void run() {
                // guarda cuando inicio el cronometro
                inicio = System.currentTimeMillis();
                while (!gameover) {
                    //calcula cuanto tiempo ha pasado
                    long actual = System.currentTimeMillis();
                    segundos = (int) ((actual - inicio) / 1000);
                    minutos = segundos / 60;
                    horas = minutos / 60;

                    minutos = minutos % 60;
                    segundos = segundos % 60;

                    //actualiza el label
                    labelCronometro.setText(horas + ":" + minutos + ":" + segundos);
                }
            }

        };
        //inicia el hilo
        hilo = new Thread(runna);
        hilo.start();
    }

    /**
     * verifica y muestra las casillas vacias a su alrededor
     *
     * @param fil
     * @param col
     */
    public void verificarCasilla(int fil, int col) {

        int contador = 0;
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                //verifica que este dentro de la matriz
                if (fil + i >= 0 && col + j >= 0 && fil + i <= 7 && col + j <= 7) {
                    if (checkCasilla(fil + i, col + j)) {
                        contador++;
                    }
                }
            }
        }
        //desactiva boton y lo vuelve gris

        botonesJuego[fil][col].setBackground(Color.LIGHT_GRAY);
        botonesJuego[fil][col].setEnabled(false);
        if (contador == 0) {
            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    if (!(i == 0 && j == 0)) {
                        //verifica que este dentro de la matriz
                        if (fil + i >= 0 && col + j >= 0 && fil + i <= 7 && col + j <= 7) {
                            //verifica que la mina no haya sido revisada
                            if (matrizMinasRevisadas[fil + i][col + j] != 1) {
                                matrizMinasRevisadas[fil + i][col + j] = 1;
                                //verifica para mostrar la casilla
                                verificarCasilla(fil + i, col + j);

                            }
                        }
                    }
                }
            }
        } else {
            botonesJuego[fil][col].setText(String.valueOf(contador));
        }

    }

    /**
     * *
     * Inicia el turno de la computadora
     */
    public void turnoComputadora() {
        boolean agregado = false;
        do {
            int f, c;
            f = (int) (Math.random() * 8);
            c = (int) (Math.random() * 8);
            //verifica que la casilla se pueda usar
            if (botonesJuego[f][c].isEnabled()) {
                matrizMinasRevisadas[f][c] = 1;
                //checkea si hay mina o no
                if (checkCasilla(f, c)) {
                    comunicador.escribir(2);
                    JOptionPane.showMessageDialog(null, "La computadora ha perdido.");
                    gameover = true;
                } else {
                    comunicador.escribir(1);
                    verificarCasilla(f, c);
                }
                //detiene el ciclo
                agregado = true;
            }
        } while (!agregado);
        finJuego();

    }

    /**
     * *
     * verifica si la casilla es mina
     *
     * @param fil
     * @param col
     * @return true si hay mina, false de lo contrario
     */
    public boolean checkCasilla(int fil, int col) {
        return matrizMinas[fil][col] == 1;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        tableroPanel = new javax.swing.JPanel();
        labelCronometro = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(364, 300));

        jLabel1.setText("Juego");

        jTextField1.setEditable(false);
        jTextField1.setText("0");
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        tableroPanel.setLayout(new java.awt.GridLayout(8, 8));

        labelCronometro.setText("00:00");

        jLabel2.setText("Minas encontradas:");

        jButton1.setText("Reiniciar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Pista");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel3.setText("Dificultad");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Facil", "Dificil" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tableroPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(labelCronometro))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tableroPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelCronometro)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        comunicador.desconectar();
        VentanaJuego v = new VentanaJuego();
        v.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        mostrarPista();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(VentanaJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VentanaJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VentanaJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VentanaJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VentanaJuego().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JLabel labelCronometro;
    private javax.swing.JPanel tableroPanel;
    // End of variables declaration//GEN-END:variables
}
