/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ihm;

import exploration.Case;
import exploration.Carte;
import exploration.Obstacle;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

/**
 * Représente la vue principale (l'IHM) dans le modèle MVC. Cette classe
 * implémente l'interface {@link CarteListener} pour être tenue au courant des
 * changements survenus sur la {@link Carte} et pouvoir les afficher.
 *
 * @author riviere
 */
public class Visualiseur extends javax.swing.JPanel implements CarteListener {

    private final Controleur controller;

    // Attributs nécessaires à l'affichage de la carte
    private BufferedImage bmp = null;
    private Graphics graph = null;
    private final float rapportX;
    private final float rapportY;

    private final int largeur;
    private final int hauteur;

    /**
     *
     * Calcule le rapport entre la carte affichée et les dimensions réelles de
     * la {@link Carte}. Initialise les composants de la vue.
     *
     * @param c la référence au {@link Controleur}
     * @param largeur la largeur de la carte affichée
     * @param hauteur la hauteur de la carte affichée
     * @param carteX la largeur réelle de la {@link Carte}
     * @param carteY la hauteur réelle de la {@link Carte}
     */
    public Visualiseur(Controleur c, int largeur, int hauteur, int carteX, int carteY) {
        this.largeur = largeur;
        this.hauteur = hauteur;
        controller = c;
        rapportX = this.largeur / carteX;
        rapportY = this.hauteur / carteY;
        initComponents();
        map.setSize(new Dimension(this.largeur, this.hauteur));
        initialiserBmp();
    }

    /**
     * @see CarteListener#update(exploration.Case[][], int, int)
     * @param cases
     * @param x
     * @param y
     */
    @Override
    public void update(Case[][] cases, int x, int y, int xstart, int ystart) {
        dessiner(cases, x, y, xstart, ystart);
    }

    /**
     ** @see CarteListener#success(java.lang.Double)
     * @param cout
     * @param efficacite
     */
    @Override
    public void success(int cout, Double efficacite) {
        controller.success(cout, efficacite);
    }

    private void initialiserBmp() {
        bmp = new BufferedImage(largeur, hauteur, BufferedImage.TYPE_INT_ARGB);
        graph = bmp.getGraphics();
        graph.setColor(Color.WHITE);
        graph.fillRect(0, 0, largeur, hauteur);
    }

    /**
     * Dessiner à l'écran la {@link Carte} avec la position des véhicules (ronds
     * rouges) et l'état des différentes cases (gris - non découverte, blanche -
     * découverte)
     *
     * @param cases la matrice de {@link Case} à dessiner
     * @param x la largeur de la matrice de cases
     * @param y la hauteur de la matrice de cases
     */
    public synchronized void dessiner(Case[][] cases, int x, int y, int xstart, int ystart) {
        if (bmp == null) {
            initialiserBmp();
        }

        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                Case c = cases[i][j];
                if (c != null) {
                    Color color;
                    if (c.isOccupee()) {
                        switch (c.getVehicule()) {
                            case 0:
                                color = Color.RED;
                                break;
                            case 1:
                                color = Color.GREEN;
                                break;
                            case 2:
                                color = Color.BLUE;
                                break;
                            default:
                                color = Color.YELLOW;
                                break;
                        }

                        graph.setColor(color);
                        graph.fillOval((int) rapportX * (i + xstart), (int) (rapportY * (j + ystart)), (int) rapportX, (int) rapportY);
                    } else {
                        if (c.isDecouverte()) {
                            if (c instanceof Obstacle) {
                                color = Color.BLACK;
                            }
                            else if (c.hasPheromones()) {
                                color = Color.ORANGE;
                            } else {
                                color = Color.WHITE;
                            }
                        } else {
                            color = Color.LIGHT_GRAY;
                        }
                        graph.setColor(color);
                        graph.fillRect((int) rapportX * (i + xstart), (int) (rapportY * (j + ystart)), (int) rapportX, (int) rapportY);
                    }
                }
            }
        }

        map.setIcon(new ImageIcon(bmp));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        map = new javax.swing.JLabel();

        setLayout(null);

        map.setBackground(new java.awt.Color(255, 255, 255));
        map.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        map.setMaximumSize(new java.awt.Dimension(largeur, hauteur));
        map.setMinimumSize(new java.awt.Dimension(largeur,hauteur));
        map.setOpaque(true);
        add(map);
        map.setBounds(20, 10, 400, 400);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel map;
    // End of variables declaration//GEN-END:variables

}
