/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ihm;

import exploration.Case;
import exploration.Carte;
import java.util.EventListener;

/**
 * Interface que doivent implémenter toutes les classes qui souhaite écouter les
 * changements survenus sur la {@link Carte}. Implémente l'interface
 * {@link EventListener}
 *
 * @author riviere
 */
public interface CarteListener extends EventListener {

    /**
     * Méthode appelée par la {@link Carte}.Prévient d'une mise à jour des
    {@link Case} cases dans la matrice de taille x*y.
     *
     * @param cases les {@link Case} dont l'état a été modifié
     * @param x la largeur de la matrice de cases
     * @param y la hauteur de la matrice de cases
     * @param xstart 
     * @param ystart
     */
    public void update(Case cases[][], int x, int y, int xstart, int ystart);

    /**
     * Méthode appelée par la {@link Carte} lorsque toutes les {@link Case} ont
     * été découvertes
     * @param cout calculée par la méthode {@link Carte#calculCout()  }
     * @param efficacite calculée par la méthode {@link Carte#calculEfficacite() }
     */
    public void success(int cout, Double efficacite);

}
