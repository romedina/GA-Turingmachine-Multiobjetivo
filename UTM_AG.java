/*
 *  NewTape=UTM(TT,Tape,N,P)
 *              /\   |  | |
		Description of Turing's Machine in ASCII binary
					 |  | |
			Input tape in ASCII binary
					    | |
				Maximum number of transitions
					      |
					Position of the Head at offset
					   	  
		Maximum 64 states (000000 - 111111)
			State 111111 is HALT

	ON OUTPUT:
		1) The processed tape if HALT
		2) Idem if N is exceeded
		3) Nul tape if over/under flow occurs
*/

import java.io.*;

class UTM_AG {

  public static String NewTape(String TM,String Tape,int N,int P){
  int ReportNum=N/20;								// Report every 5%
  int Steps=0;
  int PtrTM;										// Pointer to TM
  int I,M;											// Input and Movement
  int LenTape=Tape.length();						// Size of tape
  String LeftTape=Tape.substring(0,P);				// Left tape minus last bit
  //                         0,1,..,P-1
  //                         \___P___/
  int LLT=LeftTape.length();						// Length of LeftTape
  String RighTape=Tape.substring(P+1);			 	// Right tape minus first bit
  //                         P+1,..,eot
  int LRT=RighTape.length();						// Length of RighTape
  String sO="";
  int Q=0;											// Start in state Q=0
  for (int i=1;i<=N;i++){
  	 I=Integer.parseInt(Tape.substring(P,P+1));		// Input symbol in Tape
  	 PtrTM=Q*16+I*8;								// Position in the TM
  	 sO=TM.substring(PtrTM,PtrTM+1);
//  	 try{sO=TM.substring(PtrTM,PtrTM+1);}
//  	 catch (Exception e) {return "";}				// Output symbol
  	 M=Integer.parseInt(TM.substring(PtrTM+1,PtrTM+2));	// Movement
  	 if (M==0){
  	 	P++;										// Move RIGHT
  	 	if (P==LenTape){
//	 		System.out.println("\nRight limit of tape exceeded");
  	 		return "";
  	 	}//endif
		LeftTape=LeftTape+sO;
		LLT++;
		Tape=LeftTape+RighTape;
		LRT--;
		RighTape=RighTape.substring(1);}
  	 else{	// M==1
  	 	P--;										// Move LEFT
	  	if (P<0){
// 			System.out.println("\nLeft limit of tape exceeded");
  	 		return "";
  	 	}//endif
  	 	RighTape=sO+RighTape;
  	 	LRT++;
		Tape=LeftTape+RighTape;
		LLT--;
  	 	LeftTape=LeftTape.substring(0,LLT);
  	 }//endif
	 Q=0;											// Next State
	 for (int j=PtrTM+2;j<PtrTM+8;j++){
	 	Q=Q*2;
	 	if (TM.substring(j,j+1).equals("1")) Q++;
//	 	try{if (TM.substring(j,j+1).equals("1")) Q++;}
//	 	catch (Exception e) {Q=63;break;}			// Fuerza fin de simulacion
	 }//endFor
  	 if (Q==63){
// 	 	System.out.println("\n\nHALT state was reached");
// 	 	System.out.printf("%10.0f transitions were performed\n",(float)i);
  	 	return Tape;								// *** Processed Tape
  	 }//endif
/*	 Steps++;
	 if (Steps==ReportNum){
	 	System.out.print("\b\b\b\b\b\b\b\b\b\b");
	 	System.out.print("\t"+i);
	 	Steps=0;
	 }//endIf
*/  }//endfor
//  System.out.println("\nMaximum number of transitions was reached");
  return Tape;
  }//endOutTape

  public static int Complejidad(String TM,String Tape,int N,int P) throws Exception {
  boolean Estado[] = new boolean[64];
  Estado[0]=true;
  boolean exit;
  int Steps=0;
  int PtrTM;										// Pointer to TM
  int I,M;											// Input and Movement
  int LenTape=Tape.length();						// Size of tape
  String LeftTape=Tape.substring(0,P);				// Left tape minus last bit
  //                         0,1,..,P-1
  //                         \___P___/
  int LLT=LeftTape.length();						// Length of LeftTape
  String RighTape=Tape.substring(P+1);			 	// Right tape minus first bit
  //                         P+1,..,eot
  int LRT=RighTape.length();						// Length of RighTape
  String sO="";
  int Q=0;											// Start in state Q=0
  for (int i=1;i<=N;i++){
  	 int EA=Q;
  	 I=Integer.parseInt(Tape.substring(P,P+1));		// Input symbol in Tape
  	 PtrTM=Q*16+I*8;								// Position in the TM
  	 sO=TM.substring(PtrTM,PtrTM+1);
  	 M=Integer.parseInt(TM.substring(PtrTM+1,PtrTM+2));	// Movement
  	 if (M==0){
  	 	P++;										// Move RIGHT
  	 	if (P==LenTape){
  	 		break;									// From outermost FOR
  	 	}//endif
		LeftTape=LeftTape+sO;
		LLT++;
		Tape=LeftTape+RighTape;
		LRT--;
		RighTape=RighTape.substring(1);}
  	 else{	// M==1
  	 	P--;										// Move LEFT
	  	if (P<0){
  	 		break;									// From outermost FOR
  	 	}//endif
  	 	RighTape=sO+RighTape;
  	 	LRT++;
		Tape=LeftTape+RighTape;
		LLT--;
  	 	LeftTape=LeftTape.substring(0,LLT);
  	 }//endif
	 Q=0; exit=false;								// Next State
	 for (int j=PtrTM+2;j<PtrTM+8;j++){
	 	Q=Q*2;
	 	if (TM.substring(j,j+1).equals("1")) Q++;
//	 	try{if (TM.substring(j,j+1).equals("1")) Q++;}
//	 	catch (Exception e) {Q=63;exit=true; break;}// Fuerza fin de simulacion
	 }//endFor
	 if (exit) break;								// From outermost FOR
//	System.out.println("EA: "+EA+"; I: "+I+"; O: "+sO+" SE: "+Q);
	 Estado[Q]=true;
  	 if (Q==63){
  	 	break;
  	 }//endif
  }//endfor
  int NumEstados=0;
  String D="0123456789";
  String PTM="",Ei;
  int ix16,ix16p16,d1,d2;
  for (int i=0;i<63;i++)
  	if (Estado[i]){
		if (i<10) Ei="0".concat(D.substring(i,i+1));
  		else{d1=i/10;d2=i%10;Ei=D.substring(d1,d1+1).concat(D.substring(d2,d2+1));}
  		ix16=i*16;
  		ix16p16=ix16+16;
  		NumEstados++;
  		PTM=PTM.concat(Ei).concat(TM.substring(ix16,ix16p16));
  	}//endIf
  //endFor
  PrintStream PkdTMps=new PrintStream(new FileOutputStream(new File("PackedTM.txt")));
  PkdTMps.println(PTM);
  return NumEstados*16;
  }//endComplejidad

} //endClass
