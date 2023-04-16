/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Ventanas;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;

public class VentanaJuego extends javax.swing.JFrame {

    private int SIZE = 8;
    private int CANTIDAD_MINAS = 10;

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
    

    /**
     * Creates new form VentanaJuego
     */
    public VentanaJuego() {
        initComponents();
        crearTablero(SIZE, CANTIDAD_MINAS);
    }

    private void crearTablero(int tam, int cantidadMinas) {
        matrizMinas = new int[tam][tam];
        matrizMinasRevisadas = new int[tam][tam];

        for (int i = 0; i < cantidadMinas; i++) {
            boolean agregado = false;
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

        botonesJuego = new JButton[tam][tam];
        for (int i = 0; i < tam; i++) {
            for (int j = 0; j < tam; j++) {
                botonesJuego[i][j] = new JButton();
                botonesJuego[i][j].setBackground(Color.GREEN);
                botonesJuego[i][j].setBorder(BorderFactory.createBevelBorder(0));

                botonesJuego[i][j].addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (!gameover) {
                            for (int i = 0; i < 8; i++) {
                                for (int j = 0; j < 8; j++) {
                                    if (botonesJuego[i][j] == e.getSource() && botonesJuego[i][j].getBackground() != Color.RED) {
                                        matrizMinasRevisadas[i][j] = 1;
                                        if (checkCasilla(i, j)) {
                                            JOptionPane.showMessageDialog(null, "Has perdido.");
                                            gameover = true;
                                        } else {
                                            verificarCasilla(i, j);
                                            turnoComputadora();
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
                );
                botonesJuego[i][j].addMouseListener(new MouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getButton() == 3 && !gameover) {
                            for (int i = 0; i < 8; i++) {
                                for (int j = 0; j < 8; j++) {
                                    if (botonesJuego[i][j] == e.getSource()) {
                                        if (botonesJuego[i][j].getBackground() != Color.RED) {
                                            botonesJuego[i][j].setBackground(Color.RED);
                                            minasEncontradas += 1;
                                            jTextField1.setText(String.valueOf(minasEncontradas));
                                        } else {
                                            botonesJuego[i][j].setBackground(Color.GREEN);
                                            minasEncontradas -= 1;
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

                tableroPanel.add(botonesJuego[i][j]);

            }
        }

        conectarArduino();

        iniciarCronometro();
    }

    private void conectarArduino() {
        comunicador.conectar(comunicador.obtenerPuerto());
        comunicador.iniciarIO();
        comunicador.initListener();
        if (comunicador.getConectado()) {
            iniciarControles();
            hilo.start();
        }
    }

    void iniciarControles() {

        botonesJuego[fila_control][colum_control].setBorder(BorderFactory.createBevelBorder(0, Color.green, Color.orange, Color.red, Color.blue));

        hilo = new Thread() {
            @Override
            public void run() {
                while (!gameover) {
                    try {
                        
                        if (comunicador.isNuevoEvento()) {
                            String[] dato = comunicador.getDato();
                            if (dato != null && dato.length == 1) {
                                switch (dato[0]) {
                                    case "0":
                                        comunicador.escribir(1);
                                        if (colum_control > 0) {
                                            botonesJuego[fila_control][colum_control].setBorder(BorderFactory.createBevelBorder(0));
                                            colum_control--;
                                            botonesJuego[fila_control][colum_control].setBorder(BorderFactory.createBevelBorder(0, Color.green, Color.orange, Color.red, Color.blue));
                                        }

                                        break;
                                    case "1":
                                        comunicador.escribir(1);
                                        if (fila_control > 0) {
                                            botonesJuego[fila_control][colum_control].setBorder(BorderFactory.createBevelBorder(0));
                                            fila_control--;
                                            botonesJuego[fila_control][colum_control].setBorder(BorderFactory.createBevelBorder(0, Color.green, Color.orange, Color.red, Color.blue));
                                        }
                                        break;
                                    case "2":
                                        comunicador.escribir(3);
                                        if (colum_control < 7) {
                                            botonesJuego[fila_control][colum_control].setBorder(BorderFactory.createBevelBorder(0));
                                            colum_control++;
                                            botonesJuego[fila_control][colum_control].setBorder(BorderFactory.createBevelBorder(0, Color.green, Color.orange, Color.red, Color.blue));
                                        }
                                        break;
                                    case "3":
                                        comunicador.escribir(4);
                                        if (fila_control < 7) {
                                            botonesJuego[fila_control][colum_control].setBorder(BorderFactory.createBevelBorder(0));
                                            fila_control++;
                                            botonesJuego[fila_control][colum_control].setBorder(BorderFactory.createBevelBorder(0, Color.green, Color.orange, Color.red, Color.blue));
                                        }
                                        break;
                                    case "4":

                                        if (botonesJuego[fila_control][colum_control].getBackground() != Color.RED) {
                                            matrizMinasRevisadas[fila_control][colum_control] = 1;
                                            if (checkCasilla(fila_control, colum_control)) {
                                                comunicador.escribir(2);
                                                JOptionPane.showMessageDialog(null, "Has perdido.");
                                                gameover = true;
                                                
                                            } else {
                                                verificarCasilla(fila_control, colum_control);
                                                turnoComputadora();
                                            }
                                        }

                                        break;
                                    case "5":

                                        if (botonesJuego[fila_control][colum_control].getBackground() != Color.RED) {
                                            botonesJuego[fila_control][colum_control].setBackground(Color.RED);
                                            minasEncontradas += 1;
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
                        if(gameover){
                            comunicador.escribir(2);
                        }
                        Thread.sleep(200);
                    } catch (InterruptedException ex) {
                    }
                }
            }

        };

    }

    void iniciarCronometro() {
        Runnable runna;
        runna = new Runnable() {
            public void run() {
                inicio = System.currentTimeMillis();
                while (true) {
                    long actual = System.currentTimeMillis();
                    segundos = (int) ((actual - inicio) / 1000);
                    minutos = segundos / 60;
                    horas = minutos / 60;

                    minutos = minutos % 60;
                    segundos = segundos % 60;

                    labelCronometro.setText(horas + ":" + minutos + ":" + segundos);
                }
            }

        };
        hilo = new Thread(runna);
        hilo.start();
    }

    public void verificarCasilla(int fil, int col) {

        int contador = 0;
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (fil + i >= 0 && col + j >= 0 && fil + i <= 7 && col + j <= 7) {
                    if (checkCasilla(fil + i, col + j)) {
                        contador++;
                    }
                }
            }
        }
        botonesJuego[fil][col].setBackground(Color.LIGHT_GRAY);
        botonesJuego[fil][col].setEnabled(false);
        if (contador == 0) {
            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    if (!(i == 0 && j == 0)) {
                        if (fil + i >= 0 && col + j >= 0 && fil + i <= 7 && col + j <= 7) {
                            if (matrizMinasRevisadas[fil + i][col + j] != 1) {
                                matrizMinasRevisadas[fil + i][col + j] = 1;
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

    public void turnoComputadora() {
        boolean agregado = false;
        do {
            int f, c;
            f = (int) (Math.random() * 8);
            c = (int) (Math.random() * 8);
            if (botonesJuego[f][c].isEnabled()) {
                matrizMinasRevisadas[f][c] = 1;
                if (checkCasilla(f, c)) {
                    JOptionPane.showMessageDialog(null, "La computadora ha perdido.");
                    gameover = true;
                } else {
                    verificarCasilla(f, c);
                }
                agregado = true;
            }
        } while (!agregado);

    }

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tableroPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(labelCronometro))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton1))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 76, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tableroPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(labelCronometro)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        VentanaJuego v = new VentanaJuego();
        v.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

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
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JLabel labelCronometro;
    private javax.swing.JPanel tableroPanel;
    // End of variables declaration//GEN-END:variables
}
