/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package connect5;

import ift615tp1.JoueurArtificiel;

/**
 *
 * @author Éric Beaudry
 */
public class GamePlay implements Runnable {

    public GamePlay(Joueur j1, Joueur j2, int nblignes, int nbcols){
        grille = new Grille(nblignes, nbcols);
        verif = new GrilleVerificateur();
        joueurs = new Joueur[2];
        joueurs[0] = j1;
        joueurs[1] = j2;
    }
    
    public void setJoueur(Joueur j, int n){
        joueurs[n-1] = j;
    }

    public void setJoueurs(Joueur[] joueurs){
        this.joueurs = joueurs;
        assert joueurs.length == 2;
    }
    
    public void setSize(int nblignes, int nbcols){
        grille = new Grille(nblignes, nbcols);
    }
    
    public Grille getGrille(){
        return grille;
    }
    
    public void run() {
        erreur = false;
        fireMessage("Début de la partie...");
        gagnant = 0;
        grille.reset();
        if(grilleObserver!=null) grilleObserver.grilleChanged(grille);
        int nextjoueur=0;
        int nbtours=0;
        forceStop = false;
        depassement[0] = depassement[1] = 0;
        while(gagnant==0 && grille.nbLibre()>0 && !forceStop){
            fireMessage("Tour #" + (nbtours/2));
            int tempsallouetour = tempsAlloue - depassement[nextjoueur];
            fireMessage("  Joueur #" + (nextjoueur + 1) + "  [temps alloué = " + tempsallouetour + " ms] ...");

            long start = System.currentTimeMillis();
            int[] coup = joueurs[nextjoueur].getProchainCoup(grille, tempsallouetour);
            long stop = System.currentTimeMillis();
            int duree = (int) (stop - start);
            fireMessage("    Réponse reçue du Joueur #" + (nextjoueur + 1) + " [" + duree + " ms]");
            duree -= 50; // periode de grace

            depassement[nextjoueur] = duree - tempsallouetour;
            if(depassement[nextjoueur]<0)
                depassement[nextjoueur] = 0;

            if(depassement[nextjoueur]>0){
                nbRetards[nextjoueur]++;
                fireMessage("     Dépassement: " + depassement[nextjoueur] + " ms");
            }

            int depassementPermis = tolere50pDepassement ? tempsAlloue / 2 : 0;

            if(depassement[nextjoueur] > depassementPermis)
            {
                fireMessage("  Le joueur #" + (nextjoueur+1) + " a mis trop de temps (" + depassement[nextjoueur] + " ms)!");
                if(!ignorerRetard && !(joueurs[nextjoueur] instanceof Connect5.JoueurGUI)){
                    gagnant = (nextjoueur+1)%joueurs.length + 1;
                    return;
                }
            }
            if(coup[0]<0 || coup[1]<0 || grille.get(coup[0], coup[1])!=0){
                fireMessage("  Coup illégal pour le joueur #" + (nextjoueur+1));
                gagnant = (nextjoueur+1)%joueurs.length + 1;
                erreur = true;
                return;
            }
            grille.set(coup[0], coup[1], nextjoueur+1);
            if(grilleObserver!=null) grilleObserver.grilleChanged(grille);
            gagnant = verif.determineGagnant(grille);
            nextjoueur=(nextjoueur+1)%joueurs.length;
            try{
                Thread.sleep(20);
            }catch(InterruptedException ie){ie.printStackTrace();}
            nbtours++;
        }
        fireMessage("********");
        fireMessage("Partie terminée.   Gagnant=" + gagnant);
    }
    
    public void setGrilleObserver(GameObserver go){
        grilleObserver = go;
    }
    
    private void fireMessage(String msg){
        System.out.println(msg);
        if(grilleObserver!=null)
            grilleObserver.message(msg);
    }
    
    public boolean getErreur(){
        return erreur;
    }
    
    public int getGagnant(){
        return gagnant;
    }

    public void forceStop(){
        forceStop = true;
    }

    public void setTempsAlloue(int d){
        this.tempsAlloue = d;
    }

    public void setTolerer50pDepassement(boolean tolere50pdepassement){
        this.tolere50pDepassement = tolere50pdepassement;
    }

    public void setIgnorerRetard(boolean ignoreretard){
        this.ignorerRetard = ignoreretard;
    }

    private GrilleVerificateur verif;
    private int      gagnant;
    private Grille   grille;
    private Joueur[] joueurs;
    private int      tempsAlloue = 5000;
    private int[]    depassement = new int[] {0,0};
    public  int[]    nbRetards = new int[] {0,0};
    public  boolean  forceStop = false;

    GameObserver   grilleObserver;
    private boolean  erreur = false;
    private boolean  tolere50pDepassement = true;
    private boolean  ignorerRetard = false;
    
    public static void main(String args[]) throws Exception {
        JoueurArtificiel j1 = new JoueurArtificiel();
        JoueurArtificiel j2 = new JoueurArtificiel();
        GamePlay gp = new GamePlay(j1, j2, 14, 14);
        gp.run();
        System.out.println("Gagnant:" + gp.gagnant);
    }
}
