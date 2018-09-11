package controllers.singlePlayer.cristobal;

import core.game.StateObservation;
import core.game.Observation;
import java.util.ArrayList;
public class ExtraccionCaracteristicas{
  /**
  *Obtenemos un array de celdas con el número
  **/

public static boolean comprobarNovedad(StateObservation so , Boolean[][] nov){
   // System.out.println( ExtraccionCaracteristicas.comprobarNovedad(stCopy,novelty));
  boolean novedad = false;
  ArrayList<Observation> grid[][];
  grid = so.getObservationGrid();
  for(int j = 0; j < grid[0].length; ++j)
  {
    for(int i = 0; i < grid.length; ++i)
    {
       if(grid[i][j].size() > 0)
       {
           for (int z=0; z < grid[i][j].size(); z++){
             int type =  grid[i][j].get(z).category;
             
              // if (  true != null)
              //   System.out.println("Java no es una mierda");
             if (nov[i*j][type] == null || nov[i*j][type] == false ){
               novedad = true;
             }
             nov[i*j][type] = true;
           }


       }
    }
  }
  return novedad;
 }




  // public static void convertir(StateObservation so){
  //   System.out.println("conviritiendo estado");
  //   System.out.println(so.getObservationGrid().toString());
  //   ArrayList<Observation>[] npcPositions = so.getNPCPositions();
  //   ArrayList<Observation>[] fixedPositions = so.getImmovablePositions();
  //   ArrayList<Observation>[] movingPositions = so.getMovablePositions();
  //   ArrayList<Observation>[] resourcesPositions = so.getResourcesPositions();
  //   ArrayList<Observation>[] portalPositions = so.getPortalsPositions();
  //   // grid = so.getObservationGrid();
  //
  //   // printDebug(npcPositions,"npc");
  //   // printDebug(fixedPositions,"fix");
  //   // printDebug(movingPositions,"mov");
  //   // printDebug(resourcesPositions,"res");
  //   // printDebug(portalPositions,"por");
  //   // System.out.println();
  //   ArrayList<Observation> grid[][];
  //
  //   grid = so.getObservationGrid();
  //
  //   int[] counters = new int[50];
  //   for(int j = 0; j < grid[0].length; ++j)
  //   {
  //     System.out.println("");
  //       for(int i = 0; i < grid.length; ++i)
  //       {
  //           if(grid[i][j].size() > 0)
  //           {
  //               Observation firstObs = grid[i][j].get(0); //grid[i][j].size()-1
  //               //Three interesting options:
  //               //System.out.print("-> " + i + " , " + j + " : " );
  //               int z= 0;
  //               for (z=0; z < grid[i][j].size(); z++){
  //                 int type =  grid[i][j].get(z).itype;
  //                 counters[type]++;
  //               }
  //
  //
  //           }
  //       }
  //   }
  //   for (int i=0; i< counters.length; i++)
  //     if (counters[i]> 0)
  //       System.out.println("elementos de tipo "+ i + "   :  " + counters[i]);
  //
  //
  //   // ArrayList<Observation>[] npcPositions = so.getNPCPositions();
  //   // printDebug(npcPositions,"npc");
  // }

              // public static void printDebug(ArrayList<Observation>[] positions, String str)
              //   {
              //       if(positions != null){
              //           System.out.print(str + ":" + positions.length + "(");
              //           for (int i = 0; i < positions.length; i++)
              //           {
              //               System.out.print(" nuevo ->");
              //               // /ArrayList<Observation>[] p = new ArrayList<Observation>[3];
              //               //System.out.print(p.get(0).toString());
              //               //Observation obs = positions[i];
              //
              //               // /System.out.print(obs.position + ",");
              //
              //               //System.out.print(((Observation)(positions[i])).position[1] + ", ");
              //           }
              //           System.out.print("); ");
              //       }else System.out.print(str + ": 0; ");
              //     }

  // public static void printDebug(ArrayList<Observation>[] positions, String str)
  // {
  //     if(positions != null){
  //         System.out.print(str + ":" + positions.length + "(");
  //         for (int i = 0; i < positions.length; i++) {
  //             //System.out.print(positions[i].size() + "   ,");
  //             if ( positions[i].size() >0){
  //               System.out.print(positions[i].get(0).position.x + "   ,");
  //               System.out.print(positions[i].get(0).position.y + ",   ");
  //             }
  //         }
  //         System.out.print("); ");
  //     }else System.out.print(str + ": 0; ");
  // }
}
