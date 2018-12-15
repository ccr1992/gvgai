package controllers.singlePlayer.cristobal;

  import java.awt.Graphics2D;
  import java.util.ArrayList;
  import java.util.HashMap;
  
  import core.game.Observation;
  import core.game.StateObservation;
  import ontology.Types;
  import tools.Vector2d;
  
  import java.util.Arrays;
  import java.util.stream.IntStream;
  import java.util.*;
  
public class Explorar{
      /**
       * Observation grid.
       */
      protected ArrayList<Observation> grid[][];

      /**
       * block size
       */
      protected int block_size;
      HashMap<Integer, Boolean> tiposPlayer;
      // protected int[] tiposPlayer;
      protected Boolean perseguidores;
      
      protected Set rewards; //si hay distintos numeros de rewards
      protected boolean muere;
      protected HashMap<Integer, Observation> portales;
      protected HashMap<Integer, Observation>  resources;
      protected HashMap<Integer, Observation>  NPC;
      protected HashMap<Integer, Observation>  mobiles;
      protected HashMap<Integer, Observation> mobiblesIniciales; //lista objeto mobiles iniciales
      protected HashMap<Integer, Observation>  inmobiles;
      protected HashMap<Integer, Observation>  fromAvatar;
      protected HashMap<Integer, Observation> rewardsSubjetivos;
      protected int numTiposSprites; 
      protected Boolean variosSpritesMismoTipo; //No inamobibles
      protected Boolean filizaExploracion;
      protected int numeroNodosMaximo;
      
      
      protected Boolean firstMove;
      
      protected Boolean verbose;

          
          // grid = so.getObservationGrid();
          // block_size = so.getBlockSize();
          // firstMove = true;
          
      public Explorar(){
        firstMove = true;
        tiposPlayer = new HashMap<Integer, Boolean>();
        
        rewardsSubjetivos = new HashMap<Integer, Observation>(); //objetos móviles que desaparecen
        portales = new HashMap<Integer, Observation>();
        resources = new HashMap<Integer, Observation>();
        NPC = new HashMap<Integer, Observation>();
        mobiles = new HashMap<Integer, Observation>();
        inmobiles = new HashMap<Integer, Observation>();
        fromAvatar = new HashMap<Integer, Observation>();
        //new HashMap<Integer, Observation>();
        rewards = new HashSet();
        
        
        verbose = false;
      }

      public void comprobarPropiedades(StateObservation stateObs) {
          if (firstMove){
            mobiblesIniciales = initMovablePositions(stateObs);
          }
          
          // ArrayList<Observation>[] npcPositions = stateObs.getNPCPositions();
          // ArrayList<Observation>[] fixedPositions = stateObs.getImmovablePositions();
          // ArrayList<Observation>[] movingPositions = stateObs.getMovablePositions();
          // ArrayList<Observation>[] resourcesPositions = stateObs.getResourcesPositions();
          // ArrayList<Observation>[] portalPositions = stateObs.getPortalsPositions();
          // grid = stateObs.getObservationGrid();

          explorarTiposPlayer(stateObs);
          explorarPerseguidores();
          explorarRewardsSubjetivos(stateObs);
          explorarMuere(stateObs);
          explorarPortales(stateObs);
          explorarResources(stateObs);
          explorarNPC(stateObs);
          explorarMobiles(stateObs);
          explorarInmobiles(stateObs);
          explorarFromAvatar(stateObs);
          explorarRewards(stateObs);
          explorarNumTiposSprites();
          explorarVariosSpritesMismoTipo();
          
          if (firstMove){
            System.out.println("Resultados en la primera iteración");
            this.printResults();
            System.out.println("-----------------------------------");
            firstMove = false;
          }
          
      }

      private void explorarTiposPlayer(StateObservation so){
        ArrayList<Observation> obs [][] = so.getObservationGrid();
        Vector2d pos = getAvatarGridPosition(so);
        int lastTipo = getAvatarItype(obs, pos);
        
        if (!tiposPlayer.containsKey(lastTipo)){
          tiposPlayer.put(lastTipo, true);
        }
        
      }
      private void explorarPerseguidores(){
        
        return;
      }
      //devuelve true, si se ha eliminado un objeto mobible de los elementons iniciales
      private Boolean explorarRewardsSubjetivos(StateObservation so){
        Boolean rew = false;
        ArrayList<Observation>[] posiblesResources = so.getMovablePositions();
        if (posiblesResources == null){
          return false;
        }
        int[] resourcesIds =  getIds(posiblesResources);
        // System.out.println (Arrays.toString(resourcesIds));
        // System.out.println ("INI");
        
        
        for (Integer o : mobiblesIniciales.keySet()){
          //System.out.println (o);
          boolean contains = IntStream.of(resourcesIds).anyMatch(x -> x == o);// o  está incluido en resourcesId

          //System.out.println(contains);
          
          if (!contains && mobiblesIniciales.get(o) != null ){
            rewardsSubjetivos.put(o, mobiblesIniciales.get(o));
            mobiblesIniciales.remove(o);
            rew = true;
            //System.out.println("encontrado");
          }
        }
        
        
        return rew;
      }
      private Observation findById(ArrayList<Observation>[] observationsArray, int id){
        System.out.println("find");
        System.out.println(id);
        for (int i = 0; i < observationsArray.length; ++i){
          for (Observation o : observationsArray[i]){
            System.out.println(o.obsID);
            
            if (id == o.obsID)
              return o;
          }
        }
        System.out.println("NO ENCONTRADO");
        return null;
      }
      
      private int[] getIds(ArrayList<Observation>[] observationsArray){
        int obsSize = 0;
        for (int i = 0; i < observationsArray.length; ++i){
          obsSize+=observationsArray[i].size();
        }  
        int[] ids = new int[obsSize];
        int cont = 0;
        for (int i = 0; i < observationsArray.length; ++i){
          for (Observation o : observationsArray[i]){
            ids[cont] = o.obsID;
            cont++;
          }
        }
        
        return ids;
          
      }

      public void explorarMuere(StateObservation so){
        if(so.isGameOver() &&   so.getGameWinner() != Types.WINNER.PLAYER_WINS)
          muere = true;
      }
      private void explorarPortales(StateObservation so){
        explorarSprites(so.getPortalsPositions(), portales);
      }
      private void explorarResources(StateObservation so){
        explorarSprites(so.getResourcesPositions(), resources);
      }
      private void explorarNPC(StateObservation so){
        explorarSprites(so.getNPCPositions(), NPC);
      }
      private void explorarMobiles(StateObservation so){
        explorarSprites(so.getMovablePositions(), mobiles);
      }
      
      private void explorarInmobiles(StateObservation so){
        explorarSprites(so.getImmovablePositions(), inmobiles);
      }
      
      private void explorarFromAvatar(StateObservation so){
        explorarSprites(so.getFromAvatarSpritesPositions(), fromAvatar);
      }
      

      private void explorarRewards(StateObservation so){
        rewards.add(so.getGameScore());
      }
      
      private void explorarSprites(ArrayList<Observation>[] listSprites, HashMap<Integer, Observation> anotarSprites){
        
        if (listSprites == null){
          return;
        }
        for (int i = 0; i < listSprites.length; ++i){
          for (Observation o : listSprites[i]){
            if (!anotarSprites.containsKey(o.obsID)){
              anotarSprites.put(o.obsID, o);
            }
          }
        }
        
      
      }

      private void explorarNumTiposSprites(){
        return;
      }
      private void explorarVariosSpritesMismoTipo(){
        return;
      } 
      
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
      
      public void printResults(){
        System.out.println("Tipos del avatar principal");
        printHash(tiposPlayer);
        System.out.println("rewardsSubjetivos");
        if (rewards.size() > 0)
          printObservationHash(rewardsSubjetivos);
        else
          System.out.println("No se han encotrado rewardsSubjetivos en este juego");
        System.out.println("Juego con muerte "+ muere);
        
        System.out.println("portales");
        if (portales.size() > 0)
          printObservationHash(portales);
        else
          System.out.println("No se han encotrado portales en este juego");
          
        System.out.println("resources");
        if (resources.size() > 0)
          printObservationHash(resources);
        else
          System.out.println("No se han encotrado resources en este juego");
        
        System.out.println("NPC");
        if (NPC.size() > 0)
          printObservationHash(NPC);
        else
          System.out.println("No se han encotrado NPCs en este juego");
          
        System.out.println("mobiles");
        if (mobiles.size() > 0)
          printObservationHash(mobiles);
        else
          System.out.println("No se han encotrado objetos mobiles en este juego");
        
        System.out.println("inmobiles");
        if (inmobiles.size() > 0)
          printObservationHash(inmobiles);
        else
          System.out.println("No se han encotrado objetos inmobiles en este juego");
        
        System.out.println("fromAvatar");
        if (fromAvatar.size() > 0)
          printObservationHash(fromAvatar);
        else
          System.out.println("No se han encotrado objetos fromAvatar en este juego");
        
        System.out.println("número de rewards distintos en el juego");
        printSet(rewards);
      }
      
      
      
      private void printHash(HashMap<Integer, Boolean> hash){
        for (Integer key : hash.keySet()){
          System.out.println("\tkey "+ key);
          // System.out.print("value "+hash.get(key)+"\n");
        }
        
      }
      
      private void printObservationHash(HashMap<Integer, Observation> hash){
        for (Integer key : hash.keySet()){
          System.out.print("Id "+ key);
          System.out.print("\t type: "+ hash.get(key).itype);
          System.out.print("\t cat: "+ hash.get(key).category +"\n");
          // System.out.print("value "+hash.get(key)+"\n");
        }
        
      }
      
      private <E> void printSet(Set<E> set){
        if (verbose){
          for (Object o : set){
            
              if(o.getClass().equals(Double.class)){
                System.out.println((double)o);
              }
              else{
                System.out.println("Set con clase desconocida"+o.getClass());
              }
          }
        }
        else 
          System.out.println(set.size());
      }
      
    protected static HashMap<Integer, Observation> initMovablePositions(StateObservation so) {
      HashMap<Integer, Observation> result = new HashMap<Integer, Observation>();
      ArrayList<Observation>[] resources = so.getMovablePositions();

      if (resources == null){
        return result;
      }
      for (int i = 0; i < resources.length; ++i){
        for (Observation o : resources[i]){
          result.put(o.obsID, o);
        }
      }
      return result ;
    }
}