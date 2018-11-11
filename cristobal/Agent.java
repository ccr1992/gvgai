package controllers.singlePlayer.cristobal;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Random;
import java.util.LinkedList;
import core.game.Observation;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;
import controllers.singlePlayer.cristobal.ExtraccionCaracteristicas;
import java.util.HashMap;
import tools.Vector2d;

/**
 * Created with IntelliJ IDEA.
 * User: ssamot
 * Date: 14/11/13
 * Time: 21:45
 * This is a Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
public class Agent extends AbstractPlayer {
    int CONST_REWARD =5;
    /**
     * Random generator for the agent.
     */
    protected Random randomGenerator;

    /**
     * Observation grid.
     */
    protected ArrayList<Observation> grid[][];
    protected Boolean novelty[][];
    protected int orden[];

    /**
     * block size
     */
    protected int block_size;
    private boolean debug;
    private boolean win;

    private double bestScore;
    Types.ACTIONS bestMove;

    //rewardShaping
    HashMap<Integer, Boolean>  initObservationsIds =new HashMap<Integer, Boolean>();
    int numInitObservationsIds;
    int lastTipo;

    /**
     * Public constructor with state observation and time due.
     * @param so state observation of the current game.
     * @param elapsedTimer Timer for the controller creation.
     */
    public Agent(StateObservation so, ElapsedCpuTimer elapsedTimer)
    {
        debug = false;
        randomGenerator = new Random();
        grid = so.getObservationGrid();
        //Añadimos una matriz de nº casillas * nº de categorías de personajes (Averiguar si están limitadas)
        novelty = new Boolean[grid.length*grid[0].length][20];
        block_size = so.getBlockSize();
        orden = new int[so.getAvailableActions().size()];
        initializeOrden(orden);
        System.gc();
        win =false;

        initObservationsIds = initMovablePositions(so);
        numInitObservationsIds = initObservationsIds.size();
        ArrayList<Observation> obs [][] = so.getObservationGrid();
        Vector2d pos = getAvatarGridPosition(so);
        lastTipo = getAvatarItype(obs, pos);
        //System.out.println("mitipo "+myType);
    }


    /**
     * Picks an action. This function is called every game step to request an
     * action from the player.
     * @param stateObs Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
     * @return An action for the current state
     */
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        if (debug){
          System.out.println("actuando");
        }

        Types.ACTIONS action = null;
        StateObservation stCopy = stateObs.copy();
        Tuple stCopySim;
        double avgTimeTaken = 0;
        double acumTimeTaken = 0;
        long remaining = elapsedTimer.remainingTimeMillis();
        int numIters = 0;

        int remainingLimit = 5;
        int index;
        int nodos =0;

        LinkedList<Tuple> queueState = new LinkedList<Tuple>();
        queueState.add(new Tuple(stCopy,action,0,-1,0));
        ArrayList<Types.ACTIONS> actions;
        //getResourcesPositions(stCopy);
        //getMyAvatarResources(stCopy);




        int currentMove;
        int currentLevel;
        double currentScore;
        bestScore = Double.NEGATIVE_INFINITY;
        bestMove= stateObs.getAvailableActions().get(0);
        double scoreActualWithDescount;
        double incrementoScore;
        //ExtraccionCaracteristicas.pintarTodo(stCopy);
        //System.out.println("#################### " + queueState.size());
        novelty = new Boolean[grid.length*grid[0].length][20];
        // System.out.println("============"+stateObs.getAvailableActions().size());
        win =false;
        while(remaining > 2*avgTimeTaken && remaining > remainingLimit && queueState.size() != 0 && !win)
        //while(bestMove<100)
        {

            ElapsedCpuTimer elapsedTimerIteration = new ElapsedCpuTimer();
            actions = stateObs.getAvailableActions();  // ¿El número de acciones puede cambiar?
            shuffleArray(orden);
            if (numIters == 0){
              primerVistazo(queueState, actions);
            }
            // System.out.println(queueState.size());
            index = randomGenerator.nextInt(actions.size());
            //System.out.println("----------------------"+actions.size());
            //if (!debug)

            Tuple tupla = queueState.poll();
            // stCopy= queueState.poll();
            //stCopy = tupla.getSo();
            if (debug)
              imprimirMiPosicion(stCopy);
            //currentMove = tupla.getMove() ; // queueMoves.poll();
            //currentLevel = tupla.getLevel(); //queueNivel.poll();
            //Sacar a una clase search
            for (int i: orden){
              // action = actions.get(i);
              // stCopySim = stCopy.copy();
              // stCopySim.advance(action);
              stCopySim = new Tuple(tupla, actions.get(i));
              if (debug){
                System.out.println("       Ejecutando acción " + i);
                System.out.print("       ");
                imprimirMiPosicion(stCopySim.getSo());
              }
              nodos++;
              //System.out.println(currentMove == -1 ? i : currentMove);
              // if (stCopySim.isGameOver())
              //   System.out.println("SE HA PERDIDO EL JUEGO");
              Boolean novedad = ExtraccionCaracteristicas.comprobarNovedad(stCopySim.getSo(),novelty);
              // if (!novedad){
              //   System.out.println("     No supera el criterio de novedad----");
              // }
              if(!stCopySim.getSo().isGameOver() && novedad)
              {

                  // System.out.println("Acción encolada ");

                  queueState.add(stCopySim);
      // System.out.println(stCopySim.getSo().getGameScore()+" nuestro calculo -> "+stCopySim.getScore());
                  //scoreActualWithDescount  = stCopySim.getSo().getGameScore()*(1/Math.pow(1.01,currentLevel));
                  scoreActualWithDescount  = stCopySim.getScore();
                  int rewardShaping =  objetosEliminadosCambioTipo(stCopySim.getSo());
                  //System.out.println(rewardShaping+" reward" );
                  if (scoreActualWithDescount +rewardShaping*CONST_REWARD > bestScore){
                    bestScore = scoreActualWithDescount;
                    // bestMove = stCopySim.getMove() == -1 ? i : currentMove;
                    bestMove = stCopySim.getMove();
                    if (debug){
                      // System.out.println("Actualizando bestMove  " +bestMove);
                      System.out.println("Actualizando bestScore  " +bestScore);
                    }


                  }
                  if(debug){
                    System.out.println(stCopySim.getSo().getGameScore());
                  }

              }
              else if (stCopySim.getSo().getGameWinner() == Types.WINNER.PLAYER_WINS){
                // System.out.println("hemos encontrado acción para ganar la partida " +stCopySim.getSo().getGameScore());
                bestMove = stCopySim.getMove();
                win = true;
              }

            }
            action = bestMove;

            /*
            if(stCopy.isGameOver())
            {
                stCopy = stateObs.copy();
            }
            */

            numIters++;
            acumTimeTaken += (elapsedTimerIteration.elapsedMillis()) ;
            //System.out.println(elapsedTimerIteration.elapsedMillis() + " --> " + acumTimeTaken + " (" + remaining + ")");
            avgTimeTaken  = acumTimeTaken/numIters;
            remaining = elapsedTimer.remainingTimeMillis();
        }
        //System.out.println("------------número nodos  " +nodos);
        //System.out.println( ExtraccionCaracteristicas.comprobarNovedad(stCopy,novelty));

       //if (true){
       if (debug){
          try{
            Thread.sleep(200);
          }
          catch(InterruptedException e){
              System.out.println("thread 2 interrupted");
          }
        }

        return action;
    }
    private void imprimirMiPosicion(StateObservation s){
      System.out.println(s.getAvatarPosition().x/block_size + "   " +s.getAvatarPosition().y/block_size );
      if (s.getAvatarPosition().x/block_size < 0){
          debugPosicionExtranya(s);
      }
    }
    private void debugPosicionExtranya(StateObservation s){
      System.out.println("########################################3");
      System.out.println("########################################3");
      System.out.println(s.getAvatarPosition().x+ " <- x  " +s.getAvatarPosition().y + " <- y "+ block_size + "<- b_size" );
      System.out.println("score " +s.getGameScore());

      System.out.println("winer " +s.getGameWinner());
      System.out.println("GameOver" +s.isGameOver());
      System.out.println(" avatarResources" +s.getAvatarResources());
      System.out.println("########################################3");
      System.out.println("########################################3");
    }

    // private void primerVistazo(LinkedList<T<> queueState , LinkedList<Integer> queueMoves,
    //  LinkedList<Integer> queueNivel, ArrayList<Types.ACTIONS> actions){
    private void primerVistazo(LinkedList<Tuple> queueState, ArrayList<Types.ACTIONS> actions){

      int minContMuertes = 10;
      int contMuertes;

      Tuple stCopy = queueState.poll();

      Types.ACTIONS action;
      // queueMoves.poll();
      // queueNivel.poll();
      for (int i: orden){
        Tuple stCopySim = null;
        contMuertes = 0;
        action = actions.get(i);
        for (int cont=0 ; cont < 5 ; cont++){
          // stCopySim = stCopy.copy();
          // stCopySim.advance(action);
          // if(stCopySim.isGameOver())
          stCopySim = new Tuple (stCopy, action);
          stCopySim.setMove(action);
          if (stCopySim.getSo().isGameOver()){
            contMuertes++;
          }
          if (stCopySim.getSo().getGameWinner() == Types.WINNER.PLAYER_WINS)
          {

            bestMove = action;
            queueState.add(stCopySim);
            win = true;
            return;
          }
        }

        // System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" +contMuertes);
        if (contMuertes == minContMuertes){
          stCopySim.setMove(action);
          queueState.add(stCopySim);
  // System.out.println(stCopySim.getSo().getGameScore()+" nuestro calculo -> "+stCopySim.getScore());
          if (stCopySim.getScore() > bestScore){
            bestScore = stCopySim.getScore();
            // bestMove = stCopySim.getMove() == -1 ? i : currentMove;
            bestMove = stCopySim.getMove();
            if (debug){
              // System.out.println("Actualizando bestMove  " +bestMove);
              // System.out.println("Actualizando bestScore  " +bestScore + "con accion "+ stCopySim.getMove());
            }
          }

        }
        if (contMuertes < minContMuertes){
          // if (debug){
          //   System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx")
          // }
          queueState.clear();

          minContMuertes = contMuertes;
          stCopySim.setMove(action);
          queueState.add(stCopySim);
// System.out.println(stCopySim.getSo().getGameScore()+" nuestro calculo -> "+stCopySim.getScore());

          bestScore = stCopySim.getScore();
            // bestMove = stCopySim.getMove() == -1 ? i : currentMove;
          bestMove = stCopySim.getMove();
          if (debug){
              // System.out.println("Actualizando bestMove  " +bestMove);
            // System.out.println("Actualizando bestScore  " +bestScore + "con accion "+ stCopySim.getMove());
          }

        }


      }
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
                System.out.print(positions[i].size() + "   ,");
                //System.out.print(positions[0].get(0).position.x + "   ,");
                //System.out.print(positions[0].get(0).position.y + ",   ");
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

    private static void shuffleArray(int[] array)
    {
        int index;
        Random random = new Random();
        for (int i = array.length - 1; i > 0; i--)
        {
            index = random.nextInt(i + 1);
            if (index != i)
            {
                array[index] ^= array[i];
                array[i] ^= array[index];
                array[index] ^= array[i];
            }
        }
    }
    private static void initializeOrden(int[] array){
        for (int i = array.length - 1; i > 0; i--){
          array[i]=i;
        }

    }

    //print method
    protected int objetosEliminadosCambioTipo(StateObservation so) {

    int auxNum=0;
    int numResourcesDesaparecidos = 0;
    ArrayList<Observation>[] resources = so.getMovablePositions();
    if (resources == null){
      return 0;
    }
    for (int i = 0; i < resources.length; ++i){
      for (Observation o : resources[i]){

        if (initObservationsIds.get(o.obsID) != null && initObservationsIds.get(o.obsID) == true)
          auxNum++;
      }
    }
    if (auxNum < numInitObservationsIds){
      numInitObservationsIds = auxNum;
      numResourcesDesaparecidos = numInitObservationsIds- auxNum;
    }

    ArrayList<Observation> obs [][] = so.getObservationGrid();
    Vector2d pos = getAvatarGridPosition(so);
    int myTipo = getAvatarItype(obs, pos);
    if (lastTipo != myTipo){
      lastTipo = myTipo;
      numResourcesDesaparecidos +=5;
    }
    return numResourcesDesaparecidos;
  }
  protected static HashMap<Integer, Boolean> initMovablePositions(StateObservation so) {
  HashMap<Integer, Boolean> result = new HashMap<Integer, Boolean>();
  ArrayList<Observation>[] resources = so.getMovablePositions();

  if (resources == null){
    return result;
  }
  for (int i = 0; i < resources.length; ++i){
    for (Observation o : resources[i]){
      result.put(o.obsID, true);
    }
  }


  return result ;

}
  //print method
  public static void getMyAvatarResources(StateObservation so) {
    System.out.println("resources");
    HashMap<Integer, Integer>  resources =so.getAvatarResources();
    // System.out.println(so.getAvatarPosition().getSpriteCategory());
    //movable llaves y cajas
    //portals objetivos teletransportes y de donde salen los fantasmas del pacman(algo malo)
    if (resources == null){
      System.out.println("No hay resources");
      return;
    }
    for (Integer key : resources.keySet()){
      System.out.print("key "+ key);
      System.out.print("value "+resources.get(key)+"\n");
    }
  }
  //obtener el tipo del avatar
  static int getAvatarItype(ArrayList<Observation>[][] grid, Vector2d pos) {
    int posX = (int) pos.x;
    int posY = (int) pos.y;

    if (posX < 0 || posY < 0 || posX >= grid.length || posY >= grid[0].length) return 0;
    ArrayList<Observation> obsPosAvatar = grid[posX][posY];
    Observation auxObs;
    int avatarCat = 0;

    for (int k = 0; k < obsPosAvatar.size(); k++) {
      auxObs = obsPosAvatar.get(k);
      if (auxObs.category == avatarCat) return auxObs.itype;
    }
    return 0;
  }
  static Vector2d getAvatarGridPosition(StateObservation so) {
    Vector2d v = new Vector2d();
    int factor = so.getBlockSize();
    v.x = ((int)so.getAvatarPosition().x) / factor;
    v.y = ((int)so.getAvatarPosition().y) / factor;
    return v;
  }
}
//fin de obtener tipo avatar
class Tuple {
    private StateObservation state;
    private double score;
    private int level;
    private double incScore;
    private Types.ACTIONS moveInicial;
    public Tuple (StateObservation so, Types.ACTIONS m, double s1, int l, double s2) {
      this.state = so;
      this.moveInicial = m;
      this.score = s1;
      this.level = l;
      this.incScore  = s2;
    }
    // Constructor que recibe el estado anterior, y la accion a realizar.
    // Copia y avanza el estado, calcula los nuevos parámetros
    public Tuple (Tuple t, Types.ACTIONS a ) {
      this.state = t.getSo().copy();
      this.state.advance(a);
      this.moveInicial= t.getMove();
      this.level = t.getLevel() + 1;
      //incremento de score

      this.score = this.calculateScoreWithResources(t.getScore());
      if (this.score < 0){
        this.score= this.state.getGameScore()  + this.score*10; // penalizamos el incremento negativo
      }
      else {
        this.score = this.state.getGameScore()*(1/Math.pow(1.01,this.level)); // factor de descuento en funcion de la profundidad
      }
      //this.score = this.state.getGameScore();

      this.incScore  = this.score - t.getScore();
    }
    public double getScore(){
      return this.score;
    }
    public StateObservation getSo(){
      return this.state;
    }

    public int getLevel(){
      return this.level;
    }
    public Types.ACTIONS getMove(){
      return this.moveInicial;
    }
    public void setMove(Types.ACTIONS a){
      this.moveInicial = a;
    }

    public double calculateScoreWithResources(double scoreAnterior){
      return this.state.getGameScore() - scoreAnterior;
    }



}
