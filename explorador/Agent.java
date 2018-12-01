package controllers.singlePlayer.explorador;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Random;

import core.game.Observation;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;

/**
 * Created with IntelliJ IDEA.
 * User: ssamot
 * Date: 14/11/13
 * Time: 21:45
 * This is a Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
public class Agent extends AbstractPlayer {

    /**
     * Random generator for the agent.
     */
    protected Random randomGenerator;

    /**
     * Observation grid.
     */
    protected ArrayList<Observation> grid[][];

    /**
     * block size
     */
    protected int block_size;
    protected int[] tiposPlayer;
    protected boolean perseguidores;
    protected int[] rewards;
    protected boolean muere;
    protected int[] portales;
    protected int[] resources;
    protected int[] NPC;
    protected int[] mobiles;
    protected int[] inmobiles;
    protected int[] rewardsSubjetivos;
    protected int numTiposSprites;
    protected boolean variosSpritesMismoTipo; //No inamobibles


    protected boolean fistMove;


    /**
     * Public constructor with state observation and time due.
     * @param so state observation of the current game.
     * @param elapsedTimer Timer for the controller creation.
     */
    public Agent(StateObservation so, ElapsedCpuTimer elapsedTimer)
    {
        randomGenerator = new Random();
        grid = so.getObservationGrid();
        block_size = so.getBlockSize();
        fistMove = true;
        // try{
        //   Thread.sleep(60000);
        // }
        // catch(InterruptedException e){
        //     System.out.println("thread 2 interrupted");
        // }
    }


    /**
     * Picks an action. This function is called every game step to request an
     * action from the player.
     * @param stateObs Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
     * @return An action for the current state
     */
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {

        ArrayList<Observation>[] npcPositions = stateObs.getNPCPositions();
        ArrayList<Observation>[] fixedPositions = stateObs.getImmovablePositions();
        ArrayList<Observation>[] movingPositions = stateObs.getMovablePositions();
        ArrayList<Observation>[] resourcesPositions = stateObs.getResourcesPositions();
        ArrayList<Observation>[] portalPositions = stateObs.getPortalsPositions();
        grid = stateObs.getObservationGrid();

        /*printDebug(npcPositions,"npc");
        printDebug(fixedPositions,"fix");
        printDebug(movingPositions,"mov");
        printDebug(resourcesPositions,"res");
        printDebug(portalPositions,"por");
        System.out.println();               */

        Types.ACTIONS action = null;
        StateObservation stCopy = stateObs.copy();

        double avgTimeTaken = 0;
        double acumTimeTaken = 0;
        long remaining = elapsedTimer.remainingTimeMillis();
        int numIters = 0;

        int remainingLimit = 5;
        while(remaining > 2*avgTimeTaken && remaining > remainingLimit)
        {
            ElapsedCpuTimer elapsedTimerIteration = new ElapsedCpuTimer();
            ArrayList<Types.ACTIONS> actions = stateObs.getAvailableActions();
            int index = randomGenerator.nextInt(actions.size());
            action = actions.get(index);

            stCopy.advance(action);
            if(stCopy.isGameOver())
            {
                stCopy = stateObs.copy();
            }

            explorarTiposPlayer();
            explorarPerseguidores();
            explorarRewards();
            explorarMuere();
            explorarPortales();
            explorarResources();
            explorarNPC();
            explorarMobiles();
            explorarInmobiles();
            private void explorarRewardsSubjetivos(){

            }

            private void explorarNumTiposSprites(){

            }
            private void explorarVariosSpritesMismoTipo(){

            }



            numIters++;
            acumTimeTaken += (elapsedTimerIteration.elapsedMillis()) ;
            //System.out.println(elapsedTimerIteration.elapsedMillis() + " --> " + acumTimeTaken + " (" + remaining + ")");
            avgTimeTaken  = acumTimeTaken/numIters;
            remaining = elapsedTimer.remainingTimeMillis();
        }
        firstMove = false;
        return action;
    }

    private void explorarTiposPlayer(){

    }
    private void explorarPerseguidores(){

    }
    private void explorarRewards(){

    }

    private void explorarMuere(){

    }
    private void explorarPortales(){

    }
    private void explorarResources(){

    }
    private void explorarNPC(){

    }
    private void explorarMobiles(){

    }

    private void explorarInmobiles(){

    }

    private void explorarRewardsSubjetivos(){

    }

    private void explorarNumTiposSprites(){

    }
    private void explorarVariosSpritesMismoTipo(){

    }


    /**
     * Prints the number of different types of sprites available in the "positions" array.
     * Between brackets, the number of observations of each type.
     * @param positions array with observations.
     * @param str identifier to print
     */
    private void printDebug(ArrayList<Observation>[] positions, String str)
    {
        if(positions != null){
            System.out.print(str + ":" + positions.length + "(");
            for (int i = 0; i < positions.length; i++) {
                System.out.print(positions[i].size() + ",");
            }
            System.out.print("); ");
        }else System.out.print(str + ": 0; ");
    }

    /**
     * Gets the player the control to draw something on the screen.
     * It can be used for debug purposes.
     * @param g Graphics device to draw to.
     */
     public void draw(Graphics2D g)
     {
         int half_block = (int) (block_size*0.5);
         for(int j = 0; j < grid[0].length; ++j)
         {
           // /System.out.println("");
             for(int i = 0; i < grid.length; ++i)
             {
                 if(grid[i][j].size() > 0)
                 {
                     Observation firstObs = grid[i][j].get(0); //grid[i][j].size()-1
                     //Three interesting options:
                     //System.out.print("-> " + i + " , " + j + " : " );
                     int z= 0;
                     for (z=0; z < grid[i][j].size(); z++){
                       //System.out.print(grid[i][j].get(z).itype +"");
                     }

                     int print = firstObs.category; //firstObs.itype; //firstObs.obsID;
                     g.drawString("" + "", i*block_size+half_block,j*block_size+half_block);
                 }
             }
         }
     }
}
