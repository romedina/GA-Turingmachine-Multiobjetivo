/*	ESTE PROGRAMA TRATA DE ENCONTRAR LA MAQUINA DE TURING QUE ESCRIBA EN UNA
 *	CINTA ORIGINALMENTE LLENA DE CEROS UNA CADENA IGUAL A LA QUE SE LE PROPORCIONA
 *	EN UN ARCHIVO BINARIO-ASCII
 */
import java.io.*;
import java.util.Random;
import java.lang.Math.*;
import java.lang.*;

class Kolmogorov {

  static int LG=1024;					// Longitud del genoma
  /*
		NN -> Numero de Transiciones
		N -> Numero de Individuos
		N_2 -> Mitad de Individuos
		L -> Longitud de la cinta
		L_2 -> Mitad de la cinta
		G -> Generaciones
		B2M -> Bits to Mutate
		Nx2 -> Doble de Individuos
		iTmp -> input desde terminal [ ver Modify() ]
  */
  static int P,N,NN,N_2,L,L_2,FN=1,G,B2M,Nx2,iTmp;

  static String	Resp,TT;
  static double	Pc,	Pm;
  static double	fTmp,W;					// WW --> Ponderacion para Solos,Pares,Triadas,Cuartetas
  static double	BestSingleMatches=-1;
  static double BestFitness=-1;
  static double lenTape;
  static String BestTapeMatch="";
  static int root;
  static Random RandN;
//
  static int maxN=250,minN=1;			// Numero de individuos
  static int maxNN=5000000,	minNN=1;	// Numero de transiciones
  static int maxL=50000, minL=1;		// Longitud de la cinta
  static double	maxPc=1f, minPc=.01f;	// Probabilidad de cruza
  static double	maxPm=1f, minPm=.001f;	// Probabilidad de mutacion
  static int maxG=10000, minG=1;		// Numero de generaciones
  static double	maxW=1,	minW=0;			// Valores de W

  //static double worstofA = 1, worstofB = 1, worstofC = 1; //los peores para el multiobjetivo
//
  public static	String genoma [];
  public static	double fitness[];
  public static	BufferedReader Fbr,Kbr,Tbr;
  



/*
 *	LEE UNA CADENA HASTA EL PRIMER <TAB>
 *		A la entrada:	Buffered Reader
 *		A la salida:	La cadena hasta (excluyendo) el 1er <TAB>
 */
   public static String	LHT(BufferedReader BR) throws Exception {
   	String Dato=BR.readLine();
   	for	(int i=0;i<Dato.length();i++){
   		if (Dato.substring(i,i+1).equals("\t"))
   			return Dato.substring(0,i);
   		//endIf
   	}//endFor
   	System.out.println("No se encontro el tabulador");
   	return "";
   }//endLHT




   /*
		=========================

						FUNCIONES PARAMETRIZACION [ AGE ]

		=========================
  */

   /*	
   		CreaParams() -----------------------
		Crea los parametros iniciales que salen en el programa
   */

   public static void CreaParams() throws Exception {
	  try {
		Fbr=new BufferedReader(new InputStreamReader(new FileInputStream(new File("AGParams.txt"))));
	  }//endTry
	  catch (Exception e){
	    PrintStream Fps=new PrintStream(new FileOutputStream(new File("AGParams.txt")));
		Fps.println("200\t\t1) Individuos");
		Fps.println("50000\t\t2) Numero de transiciones");
		Fps.println("1000\t\t3) Longitud de la cinta");
		Fps.println("0.900\t\t4) Pc");
		Fps.println("0.010\t\t5) Pm");
		Fps.println("1000\t\t6) Generaciones");
		Fps.println("0.5\t\t7) Ponderacion");
	  }//endCatch
  }//endCreaParams

  /*
		GetParams()
		Obtiene los valores de la parametrizacion del archivo AGParams.txt usando LTH.
  */

  public static	void GetParams() throws	Exception {
	  Fbr=new BufferedReader(new InputStreamReader(new FileInputStream(new File("AGParams.txt"))));
	  N =Integer.parseInt(LHT(Fbr));			// 1) Individuos
	  NN=Integer.parseInt(LHT(Fbr));			// 2) Transiciones
	  L =Integer.parseInt(LHT(Fbr));			// 3) Long. de la cinta
	  Tbr=new BufferedReader(new InputStreamReader(new FileInputStream(new File("Tape.txt"))));
	  TT=Tbr.readLine();						// Lee cinta destino (Target)
	  Pc=Double.valueOf(LHT(Fbr)).floatValue();	// 4) Probabilidad cruce
	  Pm=Double.valueOf(LHT(Fbr)).floatValue();	// 5) Probabilidad mutacion
	  G =Integer.parseInt(LHT(Fbr));			// 6) Generaciones
	  W=Double.valueOf(LHT(Fbr)).floatValue();	// 7) Ponderacion
  }//endGetParams

  /*
		UpdateParams()
		Actualiza los valores de la parametrizacion
  */

 public static	void UpdateParams()	throws Exception {
	PrintStream Fps=new PrintStream(new FileOutputStream(new File("AGParams.txt")));
	Fps.println(N+"\t\t1) Individuos");
	Fps.println(NN+"\t\t2) Numero de transiciones");
	Fps.println(L+"\t\t3) Longitud de la Cinta");
	Fps.printf("%8.6f\t\t4)Pc",Pc);
		Fps.println();
	Fps.printf("%8.6f\t\t5)Pm",Pm);
		Fps.println();
	Fps.println(G+"\t\t6) Generaciones");
	Fps.printf("%8.6f\t\t7) W",W);
		Fps.println();
  }//endUpdateParams

  /*
		DispParams()
		Imprime los valores que se asignaron en GetParams().
  */
  
  public static	void DispParams() throws Exception {
	System.out.println();
	System.out.println("1) Numero de individuos:    "+ N);
	System.out.println("2) Numero de transiciones:  "+ NN);
	System.out.println("3) Long. de la cinta	    "+ L);
	System.out.printf ("4) Prob. de cruzamiento:    %8.6f\n",Pc);
	System.out.printf ("5) Prob. de mutacion:       %8.6f\n",Pm);
	System.out.println("6) Numero de generaciones:  "+ G);
	System.out.printf ("7) Factor de Ponderacion:   %8.6f\n",W);
  }//endDispParams

  /*
		CalcParamas()
		Se calculan algunos valores globales a partir de la parametrizacion.
  */

  public static	void CalcParams() {
	N_2=N/2;
	Nx2=N*2;
	genoma = new String [Nx2];
	fitness= new double [Nx2];
	L_2=LG/2; 
	B2M=(int)((double)N*(double)LG*Pm);		//Bits to Mutate
  }//endCalcParams

  /*
		Modify()
		Sirve para modificar los valores de la parametrizacion desde la terminal.
		Agarra el input de terminal y lo asigna en el elemento seleccionado.
  */

  public static	void Modify() throws Exception {
	Kbr	= new BufferedReader(new InputStreamReader(System.in));
  	String Resp;
	while (true){
		CalcParams();
		DispParams();
		System.out.print("\nModificar (S/N)? ");
		Resp=Kbr.readLine().toUpperCase();
		if (!Resp.equals("S")&!Resp.equals("N")) continue;
		if (Resp.equals("N")) return;
		if (Resp.equals("S")){
			while (true){
				System.out.print("Opcion No:       ");
				int Opt;
				try{Opt=Integer.parseInt(Kbr.readLine());}
				catch (Exception e){continue;}
				if (Opt<1|Opt>7) continue;
				System.out.print("Nuevo valor:     ");
				iTmp=1; fTmp=1;
				try{if (Opt==4|Opt==5|Opt==7) fTmp=Double.valueOf(Kbr.readLine()).floatValue();
					else	   				  iTmp=Integer.parseInt(Kbr.readLine());}
				catch (Exception e){continue;}
				boolean OK=true;
				switch(Opt) {
					case 1: {N =iTmp; if (N<minN|N>maxN) 	OK=false; break;}
					case 2: {NN=iTmp; if (NN<minNN|NN>maxNN)OK=false; break;}
					case 3: {L =iTmp; if (L<minL|L>maxL)	OK=false; break;}
					case 4: {Pc=fTmp; if (Pc<minPc|Pc>maxPc)OK=false; break;}
					case 5: {Pm=fTmp; if (Pm<minPm|Pm>maxPm)OK=false; break;}
					case 6: {G =iTmp; if (G<minG|G>maxG)    OK=false; break;}
					case 7:	{W =fTmp; if (W<minW|W>maxW)	OK=false; break;}
				}//endSwitch
				if (OK) break;
				System.out.println("Error en la opcion # "+Opt);
			}//endWhile
		}//endIf
	}//endWhile
  }//endModify

  /*
		==================== FIN FUNCIONES PARAMETRIZACION
  */

  /*
		=========================

					FUNCIONES ALGORITMO GENETICO ECLECTICO [ AGE ]

		=========================
  */



  /*
		PoblacionInicial()
		Crea la primer poblacion con tamano N de individuos.
  */

  public static	void PoblacionInicial(double fitness[],	String genoma[]) throws	Exception{
	/*
	 *Genera N individuos aleatoriamente
	 */
  	for (int i=0;i<N;i++){
  		genoma[i]="";
		for (int j=0;j<LG;j++){
			if (RandN.nextFloat()<0.5)
				genoma[i]=genoma[i].concat("0");
			else
		  		genoma[i]=genoma[i].concat("1");
		  	//endIf
		}//endFor
  	}//endFor
  }//endPoblacionInicial

  /*
		Duplica()
		al arreglo genoma y fitness los duplica con el mismo contenido que tenian antes
		ejemplo:
		genoma [0,1,1,0]  -->
		genoma [0,1,1,0 , 0,1,1,0]
  */

  public static	void Duplica(double	fitness[],String genoma[]){
	for (int i=0;i<N;i++){
		genoma [N+i]=genoma [i];
		fitness[N+i]=fitness[i];
	}//endFor
  }//endCopia

  /*
		Cruza()
		fuciona entre el primero del arreglo con el ultimo del arreglo
  */
  
  public static	void Cruza(String genoma[]){
  	int N_i,P;
	String LI,MI,RI,LN,MN,RN;
	for (int i=0;i<N_2;i++){
		if (RandN.nextFloat()>Pc) continue;
		N_i=N-i-1;
		P=0; while (!(1<=P&P<=L_2-1)) P=(int)(RandN.nextFloat()*L_2);
		LI=genoma[i  ].substring(0,P);
		MI=genoma[i  ].substring(P,P+L_2);
		RI=genoma[i  ].substring(P+L_2);
		LN=genoma[N_i].substring(0,P);
		MN=genoma[N_i].substring(P,P+L_2);
		RN=genoma[N_i].substring(P+L_2);
		genoma[i  ]=LI.concat(MN).concat(RI);
		genoma[N_i]=LN.concat(MI).concat(RN);
	}//endFor
  }//endCruza

  /*
		Muta()
		algun bit de forma aleatoria es mutado.

  */

  public static	void Muta(String genoma[]) throws Exception {
	int nInd, nBit;
	for (int i=1;i<=B2M;i++){
		nInd=-1; while (nInd<0|nInd>=N)  nInd=(int)(RandN.nextFloat()*N);
		nBit=-1; while (nBit<0|nBit>=LG) nBit=(int)(RandN.nextFloat()*LG);
/*
 *		** Mutation **
 */
		String mBit="0";
		String G=genoma[nInd];
		if (nBit!=0&nBit!=LG-1){
		 if (G.substring(nBit,nBit+1).equals("0")) mBit="1";
		 genoma[nInd]=G.substring(0,nBit).concat(mBit).concat(G.substring(nBit+1));
		 continue;
		}//endif
		if (nBit==0){
			if (G.substring(0,1).equals("0")) mBit="1";
			genoma[nInd]=mBit.concat(G.substring(1));
			continue;
		}//endif
		//if (nBit==LG-1){
			if (G.substring(LG-1).equals("0")) mBit="1";
			genoma[nInd]=G.substring(0,LG-1).concat(mBit);
		//}//endIf
	}//endFor
  }//endMuta
		 
  public static	void Evalua(double fitness[],String	genoma[]){
	String Tape,NewTape;
	for (int i=0;i<N;i++){
		int PP=L/2; // UBICA LA CABEZA A LA MITAD DE LA CINTA
		Tape="0";for (int j=1;j<L;j++) Tape=Tape+"0"; //Cinta llena de ceros
		NewTape=UTM_AG.NewTape(genoma[i],Tape,NN,PP); // Cinta de la Maquina de Turing
		double Solos=0,Pares=0,Triadas=0,Cuartetas=0; 
		boolean FF2=true, FF3=true, FF4=true;
		double WF=1+W; // 1 + Factor de ponderacion
		int TgtLen=TT.length();
		double objA = 0, objB = 0,objC = 0;
		double objAStan = 0, objBStan = 0, objCStan = 0;
		
		
		if (NewTape.length()!=0){

			// Objetivo A
			int k=PP-1;
			for (int j=0;j<TgtLen;j++){
				k++;
				if (NewTape.substring(k,k+1).equals(TT.substring(j,j+1))){
					Solos++;
					if (Solos>BestSingleMatches) BestSingleMatches=Solos;
				}//endIf
				if (FF2){
					try{if (NewTape.substring(k,k+2).equals(TT.substring(j,j+2))) Pares++;}
					catch (Exception e) {FF2=false;}
				}//endif
				if (FF3){
					try{if (NewTape.substring(k,k+3).equals(TT.substring(j,j+3))) Triadas++;}
					catch (Exception e) {FF3=false;}
				}//endIf
				if (FF4){
					try{if (NewTape.substring(k,k+4).equals(TT.substring(j,j+4))) Cuartetas++;}
					catch (Exception e) {FF4=false;}
				}//endIf
			}//endFor

			//fitness[i]=Solos+WF*(Pares+WF*(Triadas+WF*Cuartetas));


			// OBJETIVO A

			// asignamos el fitnes como el valor del objetivo A
			objA = Solos+WF*(Pares+WF*(Triadas+WF*Cuartetas));
			// estandarizamos el valor para tener un numero entre 0 - 1
			// dividimos el valor obtenido de objA entre el maximo que podriamos obtener
			objAStan = objA / (TgtLen+WF*((TgtLen/2) + WF * ((TgtLen/3) + WF * (TgtLen/4))) );
			//System.out.println("objAStan: " + objAStan);

			/*
			// escogemos el peor valor y lo guardamos en WorstofA
			if(objAStan < worstofA){
				worstofA = objAStan;
			}
			System.out.println("worstofA: " + worstofA);
			*/
			

			// OBJETIVO B
			try{
				objB= UTM_AG.Complejidad(genoma[i],Tape,NN,(int)TgtLen/2);	
			}catch (Exception e){};
			
			objBStan = 16/ (double)objB;

			/*
			if(objBStan < worstofB){
				worstofB = objBStan;
			}
			System.out.println("worstofB: " + worstofB);
			*/

			double worst = Math.min(objA,objB);
			


			//fitness[i] = Math.max(worstofA,worstofB);
			fitness[i] = worst;

			if (fitness[i]>BestFitness){
				BestFitness=fitness[i];
				BestTapeMatch=NewTape.substring(PP,PP+TgtLen);
			}//endIf
		}else{
			fitness[i]=0d;
		}//endIf
	}//endFor
	return;
  }//endEvalua

/*		Selecciona los mejores N individuos
 *
 */
  public static	void Selecciona(double fitness[],String	genoma[]) {
  	double fitnessOfBest,fTmp;
  	String sTmp;
	int indexOfBest;
  	for (int i=0;i<N;i++){
	  	fitnessOfBest=fitness[i];
		indexOfBest  =i;
  		for (int j=i+1;j<Nx2;j++){
			if (fitness[j]>fitnessOfBest){
				fitnessOfBest=fitness[j];
				indexOfBest  =j;
			}//endIf
  		}//endFor
  		if (indexOfBest!=i){
  			sTmp=genoma[i];
  			genoma[i]=genoma[indexOfBest];
  			genoma[indexOfBest]=sTmp;
 			fTmp=fitness[i];
 			fitness[i]=fitness[indexOfBest];
 			fitness[indexOfBest]=fTmp;
  		}//endIf
  	}//endFor
	return;
  }//endSelecciona

	public static void ShowTM(String STM) throws Exception {
		/* 
		 *	LEE TODOS LOS BYTES DE LA MAQUINA DE TURING
		 *
		 */
		int MTLen=STM.length();
//		System.out.println(MTLen+" bytes leidos del mapa de la MT");
		int iCar=0;
		int NumStates=MTLen/18;			//2 para el estado y 16 para el resto
		int ix18,x0_I,x1_I,Estado;
		String x0_M,x1_M;
		System.out.println("Hay "+NumStates+" estados en la Maquina de Turing");
		System.out.println(" EA | O | M | SE || O | M | SE |");
		System.out.println(" -------------------------------");
		for (int i=0;i<NumStates;i++){
			ix18=i*18;
			System.out.print("  "+STM.substring(ix18,ix18+2)+"|");
			x0_I=Integer.parseInt(STM.substring(ix18+2,ix18+3));
			x0_M=STM.substring(ix18+3,ix18+4);
			if (x0_M.equals("0")) x0_M=" R |"; else x0_M=" L |";
			System.out.printf("%3.0f|"+x0_M,(float)x0_I);
			Estado=0;
			for (int j=ix18+4;j<ix18+10;j++){
				Estado=Estado*2;
				if (STM.substring(j,j+1).equals("1"))
					Estado++;
				//endif
			}//endFor
			if (Estado==63)
				System.out.print("   H||");
			else
				System.out.printf("%4.0f||",(float)Estado);
			//endif
			x1_I=Integer.parseInt(STM.substring(ix18+8,ix18+9));
			x1_M=STM.substring(ix18+10,ix18+11);
			if (x1_M.equals("0")) x1_M=" R |"; else x1_M=" L |";
			System.out.printf("%3.0f|"+x1_M,(float)x1_I);
			Estado=0;
			for (int j=ix18+12;j<ix18+18;j++){
				Estado=Estado*2;
				if (STM.substring(j,j+1).equals("1"))
					Estado++;
				//endif
			}//endFor
			if (Estado==63)
				System.out.print("   H|\n");
			else
				System.out.printf("%4.0f|\n",(float)Estado);
			//endif
		}//endFor
//		System.out.println("<Enter> para continuar...");
//		String Resp=Kbr.readLine();
		return;
		}//endMethod

   public static void ResultadosDeLaCorrida() throws Exception {
/*
 *		EL MEJOR AJUSTE
 */
		System.out.printf("\n\nAjuste maximo: %15.7f\n",fitness[0]);
/*
 *		LA MEJOR MAQUINA
 */
		PrintStream TgtTMps=new PrintStream(new FileOutputStream(new File("TargetTM.txt")));
		TgtTMps.println(genoma[0]);
		System.out.println("La mejor MT encontrada esta en \"TargetTM.txt\"\n");
/*
 *		LA MEJOR CINTA DESTINO
 */
		PrintStream TgtTAPEps=new PrintStream(new FileOutputStream(new File("TargetTape.txt")));
		TgtTAPEps.println(BestTapeMatch);
		System.out.println("La mejor cinta encontrada esta en \"TargetTape.txt\"\n");
/*
 *		COINCIDENCIAS
 */
		System.out.println("\na) Numero de coincidencias: "+BestSingleMatches);
		double Ratio=(double)BestSingleMatches/lenTape;
		System.out.println("b) Longitud de la cinta de datos: "+lenTape+"\n");
		System.out.printf("\t===> Tasa de coincidencias: %6.4f\n\n\n",Ratio);
/*
 *		COMPLEJIDAD
 */
		System.out.println("\t*****\t<ENTER> para ver los estados de la MT\t*****\n");
		String sResp=Kbr.readLine();
		String Tape=""; for (int j=0;j<L;j++) Tape=Tape+"0";
	 	int Informacion=UTM_AG.Complejidad(genoma[0],Tape,NN,(int)L/2);
	 	System.out.println("Estados en la Maquina de Turing: "+Informacion/16);
	 	System.out.println("Maquina de Turing compactada en PackedTM.txt");
	 	System.out.println("\n\t******************************************");
	 	System.out.printf("\t*  La complejidad de Kolmogorov: %7.0f *\n",(float)Informacion);
	 	System.out.println("\t******************************************\n");
	 	BufferedReader PTMbr=new BufferedReader(new InputStreamReader(new FileInputStream(new File("PackedTM.txt"))));
	 	ShowTM(PTMbr.readLine());
		return;
	}//endMethod

   public static void main(String[] args) throws Exception {
	BufferedReader Fbr,Kbr;
	while (true){
	 Kbr = new BufferedReader(new InputStreamReader(System.in));
	 while (true){
		System.out.println("Deme la raiz del generador de numeros aleatorios");
		try{root=Integer.parseInt(Kbr.readLine());break;}
		catch (Exception e) {System.out.println("Debe ser entero!\n");}
	 }//endWhile
	 RandN = new Random(root);
	 float RN = RandN.nextFloat();
	 LeeDatos.Cinta();						//Rutina externa
	 CreaParams();							//Crea archivo si no existe
	 GetParams();							//Lee parametros de archivo
	 Modify();								//Modifica valores
	 CalcParams();							//Calcula parametros
	 UpdateParams();						//Graba en archivo
/*
 *		EMPIEZA EL ALGORITMO GENETICO
*/
 	 PoblacionInicial(fitness, genoma);			//Genera la poblacion inicial
	 System.out.printf("GEN %8.0f\n",0f);
	 Evalua(fitness,genoma);
	 int First=1, Last=G;						//Evalua los primeros N
	 lenTape=TT.length(); 
	 while (true){
		for (int i=First;i<Last;i++){
			System.out.printf("GEN  %8.0f\tMatches %8.0f\n",(float)i,(float)BestSingleMatches);
	  		Duplica(fitness,genoma);				//Duplica los primeros N
			Cruza(genoma);							//Cruza los primeros N
			Muta(genoma);							//Muta los primeros N
			Evalua(fitness,genoma);					//Evalua los primeros N
			if (BestSingleMatches==lenTape) break;	//Termina si hay ajuste perfecto
			Selecciona(fitness,genoma);				//Selecciona los mejores N
		 }//endFor
		 ResultadosDeLaCorrida();
		 System.out.println("DESEA CONTINUAR LA BUSQUEDA? (S/*)");
		 if (!Kbr.readLine().toUpperCase().equals("S")) break;
		 First=First+G;
		 Last=Last+G;
	 }//endWhile
	 System.out.println("\n\nOtra corrida? (S/*)");
	 String Resp=Kbr.readLine().toUpperCase();
	 if (!Resp.equals("S")) break;
   }//endMain
   System.out.println("\n\n*****\t\t\tFIN DE PROGRAMA\t\t\t*****\n\n\n");
  }//endLoop
} //endClass
