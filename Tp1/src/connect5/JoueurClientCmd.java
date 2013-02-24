/*
 * IFT615 - TP1
 * Session d'hiver 2009
 *
 * Université de Sherbrooke
 * Département d'informatique
 */

package connect5;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.StringTokenizer;



/**
 *
 * @author Eric Beaudry
 */
public class JoueurClientCmd implements Joueur {

    public JoueurClientCmd(String command) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(command);
        process = pb.start();
        
        fromProcess = new BufferedReader(new InputStreamReader(process.getInputStream()));
        new Thread(new Runnable(){
            public void run() {
                BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                try{
                    String ligne=null;
                    do{
                        ligne=err.readLine();
                        if(debug)
                            System.err.println(" STDERR: " + ligne);
                    }while(ligne!=null);
                    process.getErrorStream().close();
                }catch(Exception e){e.printStackTrace();}
            }
        }).start();
        
        toProcess = new PrintWriter(process.getOutputStream());
        
        System.out.println("Initialisation du joueur artificiel");
        String ligne;
        do{
            ligne = fromProcess.readLine();
            System.out.println(" STDOUT: " + ligne);
        }while(!ligne.equalsIgnoreCase("READY"));
        
        System.out.println("Joueur artificiel initialisé.");
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        toProcess.println("0");
        for(int i=0;i<20;i++){
            try{
                process.exitValue();
                return;
            }catch(IllegalThreadStateException e)
            {
                try{
                    Thread.sleep(1000);
                }catch(InterruptedException ie){}
            }
        }
        process.destroy();
    }
    
    public int[] getProchainCoup(Grille g, int delais){
        int[] coup = new int[2];
        if(debug){
            System.out.println("----- Texte envoye au STDIN -------");
            System.out.print(g.toString());
            System.out.println("" + delais);
            System.out.println("-----------------------------------");
        }
        toProcess.print(g.toString());
        toProcess.println("" + delais);
        toProcess.flush();
        
        try{
            String ligne = fromProcess.readLine();
            if(debug)
                System.out.println(" Ligne recu du STDOUT: \"" + ligne + "\"");
            StringTokenizer tokens = new StringTokenizer(ligne);
            coup[0] = Integer.parseInt(tokens.nextToken());
            coup[1] = Integer.parseInt(tokens.nextToken());
        }catch(Exception e){
            System.out.println("Joueur fail!");
            e.printStackTrace();
        }
        return coup;
    }
    
    public void terminate(){
        toProcess.println("0");
    }
    
    public void forceTerminate(){
        toProcess.println("0");
        for(int i=0;i<5;i++){
            try{
                process.exitValue();
                return;
            }catch(IllegalThreadStateException e)
            {
                try{
                    Thread.sleep(1000);
                }catch(InterruptedException ie){}
            }
        }
        process.destroy();        
    }
    
    public void setDebug(boolean d){
        debug = d;
    }
    
    protected Process        process;
    protected PrintWriter    toProcess;
    protected BufferedReader fromProcess;
    protected BufferedReader fromProcessErr;
    protected boolean        debug=true;
    
    
    public static void main(String args[]) throws Exception{
        JoueurClientCmd j = new JoueurClientCmd("/Users/eric/Eric/ift615/tp1/JoueurAleatoire/a.out");
        Grille g = new Grille(10, 10);
        GrilleVerificateur verif = new GrilleVerificateur();
        int next=0;
        while(true){
            if(g.nbLibre()==0){
                System.out.println("Partie nulle");
                break;
            }
            int[] coup = j.getProchainCoup(g, 2000);
            if(g.get(coup[0], coup[1])!=0){
                System.out.println("Placement illégal!");
                break;
            }
            g.set(coup[0], coup[1], next+1);
            int winner = verif.determineGagnant(g);
            if(winner>0){
                System.out.println(g);
                System.out.println("Gagant: " + winner);
                break;
            }
            next = (next+1)%2;
        }
        j = null;
      
    }
    
}
