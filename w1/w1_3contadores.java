    import java.util.*;


	public class w1_3contadores{
		public static void main(String[] args) {

			int  nuevoNodo[], nodoInicial[]= new int[3];
			int cont[]= nodoInicial;
			int nodoFinal[] = {(int)(Math.random() * 10),(int)(Math.random() * 10),(int)(Math.random() * 10)};
			System.out.println("Obejtivo  :" + nodoFinal[0]+" "+ nodoFinal[1]+" "+ nodoFinal[2] );

			String acciones[] = {"incrementar1", "incrementar2", "incrementar3" };

			// /ArrayList<(Integer, Integer)> novelties = new ArrayList<(Integer, Integer)>();
			ArrayList <Tuple> novelties=new ArrayList<Tuple>();
			Queue<int[]> cola=new LinkedList<int[]>();
			cola.add(nodoInicial);
			nodoContainsNovelty(nodoInicial, novelties);

      boolean encontrado = false;
			while (!cola.isEmpty() && !encontrado){
				cont=cola.poll();
        if (nodoEsObjetivo(cont, nodoFinal)){
          encontrado =  true;
        }
				for (int i=0 ; i< acciones.length; i++){
					nuevoNodo = actuar(acciones[i], cont);

					if (nodoContainsNovelty(nuevoNodo, novelties))
					{

							//agregamos al arbol
							cola.add(nuevoNodo);
					}


				}
				System.out.println("Actual  :" + cont[0]+" "+ cont[1]+" "+ cont[2] );
				//System.out.println(novelties );
			}

			/*desarrollo de ramas según funciones

			array con los elementos desplegados (recorrido en anchura)



			modo libreria*/
		}

		public static int[] actuar(String act, int[]estado){

			int e[]= estado.clone();
			switch(act){
			  case "incrementar1":
				e[0]+=1;
			  break;
			  case "incrementar2":
				e[1]+=1;
			  break;
			  case "incrementar3":
				e[2]+=1;
			  break;
			}
			return e;
		}

		public static boolean nodoContainsNovelty(int[] estado, ArrayList <Tuple> novelties){
			Tuple t;
			boolean containsNovelty = false;
			for (int i = 0; i< estado.length; i++)
			{
				t= new Tuple(i, estado[i]);
				if (!novelties.contains(t)){
					containsNovelty = true;
					novelties.add(t);

				}

			}
			return containsNovelty;
		}

    public static boolean nodoEsObjetivo(int[] estado, int[] estadoObjetivo){
      for (int i = 0; i< estado.length ; i++ ){
        if (estado[i] == estadoObjetivo[i]) return true;
      }
      return false;
    }

	}
	class Tuple{
		private int cont;
		private int val;

		public Tuple(int cont, int val){
		  this.cont = cont;
		  this.val = val;

		}

		public String toString() {
			return cont + ": " + val;
		}

		@Override
		public boolean equals(Object obj) {
			Tuple t = (Tuple)obj;
			return this.cont == t.cont  && this.val == t.val;
		}
	}
