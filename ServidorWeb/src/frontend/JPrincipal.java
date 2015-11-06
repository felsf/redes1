/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frontend;

import javax.swing.*;

/**
 *
 * @author Administrador
 */
public class JPrincipal extends JFrame {

    public static final int WIDTH = 640;
    public static final int HEIGHT = 350;

    public JPrincipal() 
    {
        setSize(WIDTH, HEIGHT);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    public void setPanel(JPanel panel) {
    	setSize(WIDTH-1, HEIGHT);
    	setContentPane(panel);
    	setSize(WIDTH, HEIGHT);
    }
}
