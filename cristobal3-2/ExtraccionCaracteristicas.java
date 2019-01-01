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
  int ancho= grid.length;
  for(int j = 0; j < grid[0].length; j++)
  {
    for(int i = 0; i < ancho; i++)
    {
       if(grid[i][j].size() > 0)
       {
           for (int z=0; z < grid[i][j].size(); z++){
             int type =  grid[i][j].get(z).category; // category-type

              // if (  true != null)
              //   System.out.println("Java no es una mierda");
             if (nov[i+ancho*j][type] == null || nov[i+ancho*j][type] == false ){
               novedad = true;
             }
             nov[i+ancho*j][type] = true;
           }


       }
    }
  }
  return novedad;
 }
 public static void pintarTodo(StateObservation so){
   ArrayList<Observation> grid[][];
   grid = so.getObservationGrid();
   for(int j = 0; j < grid[0].length; j++)
   {
     for(int i = 0; i < grid.length; i++)
     {
        if(grid[i][j].size() > 0)
        {
            System.out.println("Casilla "+ i +"  "+ j +" :");
            for (int z=0; z < grid[i][j].size(); z++){
              int type =  grid[i][j].get(z).category;

              System.out.println("      "+ type);

            }


        }
     }
   }
 }
}
