package simulador;

import java.util.Random;

public class Leitor {
	
	//Array que guarda o valor do eixo X do gráfico
	static double[] xdata = new double[]{100, 200, 300, 400, 500, 600, 700, 800, 900, 1000};
	//Arrays de 10 posições que serao os dados do estimador para os graficos 
	//Arrays do Lower Bound
	static double[] vaziosLB = new double[10]; 
	static double[] slotsLB = new double[10];
	static double[] colisoesLB = new double[10];
	static double[] comandosLB = new double[10];
	static double[] tempoLB = new double[10];
	//Arrays do Schoute
	static double[] vaziosSC = new double[10]; 
	static double[] slotsSC = new double[10];
	static double[] colisoesSC = new double[10];
	static double[] comandosSC = new double[10];
	static double[] tempoSC = new double[10];
	//Arrays do ILCM-SbS
	static double[] vaziosIL = new double[10]; 
	static double[] slotsIL = new double[10];
	static double[] colisoesIL = new double[10];
	static double[] comandosIL = new double[10];
	static double[] tempoIL = new double[10];
	//Arrays do Eom-Lee
	static double[] vaziosEL = new double[10]; 
	static double[] slotsEL = new double[10];
	static double[] colisoesEL = new double[10];
	static double[] comandosEL = new double[10];
	static double[] tempoEL = new double[10];
	
	public Leitor(){
		
	}
	
	public static void Simulacao(String estimador, int passoIncremento, int maxTags, int numRepeticoes, int frameSize, boolean potDois){
		int Q = frameSize;
		int tagsR = 0;
		Random rdmGenerator = new Random();
		int vazios = 0; //guarda a quantidade de vazios num quadro
		int sucessos = 0; //guarda a quantidade de sucessos num quadro
		int colisoes = 0; //guarda a quantidade de colisoes num quadro
		//Variaveis que armazenam o total para uma simulação, depois de todas as repeticoes
		int vaziosT = 0; //Numero total de slots vazios ate identificar todas as tags
		int colisoesT = 0; //Numero total de slots em colisao ate identificar todas as tags
		int numSlots = 0; //Numero total de slots utilizados até identificar as tags
		int comandosT = 0; //Numero total de comandos dados pelo leitor ate identificar todas as tags
		//Variaveis para controlar o tempo de execucao
		long start;
		long end;
		double tempoParcial = 0.0;

		start = System.currentTimeMillis();
		for(int a = 0; a < 10; a++){
			
			for(int b = 0; b < numRepeticoes; b++){
				
				Q = frameSize;
				tagsR = (a + 1) * passoIncremento; //Controla a quantidade de tags por passo de incremento
				while(tagsR > 0){ //Loop que de fato, faz a simulacao de identificacao
					int[] frame = new int[Q]; //Crio um novo quadro com o tamanho Q
										
					numSlots = numSlots + Q; //Atualizando a quantidade total de Slots utilizados
					comandosT++; //Comando para informar o tamanho do quadr
					//Esse for é o nosso Broadcast(Q)
					for(int i = 0; i < tagsR; i++){ //Tento ler nos slots do quadro
						frame[rdmGenerator.nextInt(Q)]++;
					}
					//Agora investigo o num de vazios, sucessos e colisoes
					
					
					if(estimador != "ILCM-SbS"){
						for(int i = 0; i < Q; i++){
							comandosT++; //simulando um comando pra informar por slot "Tente se identificar"
							if(frame[i] == 0){
								vazios++;
							}else if(frame[i] == 1){
								sucessos++;
								tagsR--; //se houve sucesso, decremento o numero de tags restantes
							} else colisoes++;
						}
					}else{ //Codigo do ILCM
						int i = 0;
						int Qn = -1;
						double l = 0.0;
						double k = 0.0;
						double n = 0.0;
						double r = 0.0;
						double l1 = 0.0;
						double l2 = 0.0;
						int qTemp  = 0;
						double ps1 = 0.0;
						double ps2 = 0.0;
						double zxx = Math.pow(2, Q);
						double rAnt = 0.0;
						
						while((Qn == -1)&&(i < zxx)&&(i < Q)){
							if(frame[i] == 0){
								vazios++;
							}else if(frame[i] == 1){
								sucessos++;
								tagsR--; //se houve sucesso, decremento o numero de tags restantes
							} else colisoes++;
							i++;
							//k = colisoes / ((4.344 * i - 16.28) + (i / (-2.282 - 0.273 * i) * colisoes) + 0.2407 * Math.log(i + 42.56));
							k = colisoes / (((4.344 * i - 16.28) + ((i / (-2.282 - 0.273 * i))) * colisoes) + (0.2407 * Math.log(i + 42.56)));
							//l = (1.2592 + 1.513 * i) * Math.tan(Math.pow(1.234 * i, -0.9907)) * colisoes;
							l = (1.2592 + (1.513 * i)) * (Math.tan((Math.pow((1.234 * i), -0.9907)) * colisoes));
							if (k < 0) k = 0;
							n = k * sucessos + l;
							r = (n * Math.pow(2, Q)) / i;
							if (colisoes == 0) r = (sucessos * Math.pow(2, Q)) / i;
							if ((i > 1) && ((r - rAnt) < 1)) { //
								l1 = Math.pow(2, Q);
								qTemp = (int) Math.round(Math.log(r) / Math.log(2));
								l2 = Math.pow(2, qTemp);
								ps1 = (r / l1) *  Math.pow((1 - (1 / l1)), (r - 1));
								ps2 = (r / l2) *  Math.pow((1 - (1 / l2)), (r - 1));
								if((l1 * ps1 - sucessos) < l2 * ps2) Qn = qTemp;
							}
							rAnt = r;
						}//fim do while do ILCM
						if(Qn != -1){
							Q = Qn;
						}
						System.out.println("Tamanho do Quadro = "+Q+", tags a identificar: "+tagsR);
					}
					//Agora tenho que redimensionar o quadro segundo os estimadores
					switch (estimador) {
					case "Lower Bound":
						if(potDois){
							Q = checaPotDois(2 * colisoes);
						}else Q = 2 * colisoes;
						break;
						
					case "Schoute":
						if(potDois){
							Q = checaPotDois((int) Math.ceil(2.39 * colisoes));
						}else Q = (int) Math.ceil(2.39 * colisoes); 
						break;
						
					case "ILCM-SbS":
						//Devido ao if antes de processar a leitura, já temos o valor atualizado
						//de Q, pois recebeu ou não Qn do pseudo-código do ILCM, logo não precisamos
						//mais alterá-lo.
						Q = Q;
						
						break;
						
					case "Eom-Lee":
						double gamaKanterior = 0.0;
						double gamaK = 2.0;
						double Qatual = Q;
						double betaK = 0.0;
						double sS = sucessos;
						double sC = colisoes;
						double f = 0.0;
						double ax = 0.0;
						double nTags = 0.0;
						
						do{
							gamaKanterior = gamaK;

							betaK = (Qatual/(gamaKanterior*sC+sS));
							
							gamaK = (1.0-Math.exp((-1.0)/betaK))/(betaK*(1.0-(1.0+(1.0/betaK))*Math.exp((-1.0)/betaK)));
							
							ax = Math.abs(gamaKanterior - gamaK);
						}while(ax >= 0.001);
						f = gamaK * sC;
						nTags = f / betaK;
						if(potDois){
							Q = checaPotDois((int) Math.ceil(nTags));
						}else Q = (int) Math.ceil(f);
						break;
		
					default:
						System.out.println("Não foi informado um estimador válido.");
						break;
					}
					//Vamos agora salvar os valores da simulacao
					vaziosT = vaziosT + vazios;
					colisoesT = colisoesT + colisoes;
					//Agora zeramos os contadores para quadros
					vazios = 0;
					sucessos = 0;
					colisoes = 0;
				}//fim do While	
			}//fim do segundo For(b)
			end = System.currentTimeMillis();
			tempoParcial = tempoParcial + (end - start);
			//Nesse momento acabaram as 2000 simulações, preciso tirar a media dos valores para
			//plotar no gráfico
			switch (estimador) {
			case "Lower Bound":
				vaziosLB[a] = vaziosT / numRepeticoes; 
				slotsLB[a] = numSlots / numRepeticoes;
				colisoesLB[a] = colisoesT / numRepeticoes;
				comandosLB[a] = comandosT / numRepeticoes;
				tempoLB[a] = tempoParcial / numRepeticoes;
				break;
				
			case "Schoute":
				vaziosSC[a] = vaziosT / numRepeticoes; 
				slotsSC[a] = numSlots / numRepeticoes;
				colisoesSC[a] = colisoesT / numRepeticoes;
				comandosSC[a] = comandosT / numRepeticoes;
				tempoSC[a] = tempoParcial / numRepeticoes;
				break;
				
			case "ILCM-SbS":
				vaziosIL[a] = vaziosT / numRepeticoes; 
				slotsIL[a] = numSlots / numRepeticoes;
				colisoesIL[a] = colisoesT / numRepeticoes;
				comandosIL[a] = comandosT / numRepeticoes;
				tempoIL[a] = tempoParcial / numRepeticoes;
				break;
				
			case "Eom-Lee":
				vaziosEL[a] = vaziosT / numRepeticoes; 
				slotsEL[a] = numSlots / numRepeticoes;
				colisoesEL[a] = colisoesT / numRepeticoes;
				comandosEL[a] = comandosT / numRepeticoes;
				tempoEL[a] = tempoParcial / numRepeticoes;
				break;
				
			default:
				break;
			}
			//Agora vamos zerar os acumuladores para começar nova sério de simulações com novo
			//numero de tags
			vaziosT = 0;
			numSlots = 0;
			colisoesT = 0;
			comandosT = 0;
			tempoParcial = 0.0;
		}//fim primeiro For(a)
	}
	
	public static int checaPotDois(int numTagsEstimado){
		int Q = 0;
		if(numTagsEstimado <= 5){
			Q = 4;
		}else if(numTagsEstimado <= 11){
			Q = 8;
		}else if(numTagsEstimado <= 22){
			Q = 16;
		}else if(numTagsEstimado <= 44){
			Q = 32;
		}else if(numTagsEstimado <= 89){
			Q = 64;
		}else if(numTagsEstimado <= 177){
			Q = 128;
		}else if(numTagsEstimado <= 355){
			Q = 256;
		}else if(numTagsEstimado <= 710){
			Q = 512;
		}else if(numTagsEstimado <= 1420){
			Q = 1024;
		}else if(numTagsEstimado <= 2840){
			Q = 2048;
		}else if(numTagsEstimado <= 5680){
			Q = 4096;
		}else Q = 8192;
		
		return Q;
	}
	
	public static void graphTotalSlots(){
		GeraGrafico graf = new GeraGrafico(slotsLB, slotsSC, slotsIL, slotsEL, xdata, "Número de Slots Utilizados");
		graf.Render();
	}
	
	public static void graphTotalVazios(){
		GeraGrafico graf = new GeraGrafico(vaziosLB, vaziosSC, vaziosIL, vaziosEL, xdata, "Número de Slots Vazios");
		graf.Render();
	}
	
	public static void graphTotalColisao(){
		GeraGrafico graf = new GeraGrafico(colisoesLB, colisoesSC, colisoesIL, colisoesEL, xdata, "Número de Colisões");
		graf.Render();
	}
	
	public static void graphTotalComandos(){
		GeraGrafico graf = new GeraGrafico(comandosLB, comandosSC, comandosIL, comandosEL, xdata, "Número de Comandos Enviados");
		graf.Render();
	}
	
	public static void graphTotalTempo(){
		GeraGrafico graf = new GeraGrafico(tempoLB, tempoSC, tempoIL, tempoEL, xdata, "Tempo Médio de Execução");
		graf.Render();
	}
	
	public static void main(String[] args) {
		//Parametros (estimador, passoIncremento, maxTags, numRepeticoes, frameSize, usaPotenciaDois)
//		Simulacao("Lower Bound", 100, 1000, 2000, 64, false);
	//	Simulacao("Schoute", 100, 1000, 2000, 64, false);
		Simulacao("ILCM-SbS", 100, 1000, 1, 64, false);
	//	Simulacao("Eom-Lee", 100, 1000, 2000, 64, false);
		graphTotalSlots();
		graphTotalVazios();
		graphTotalColisao();
		graphTotalComandos();
		graphTotalTempo();
	}
	/* CHECKLIST:
	 * ILCM
	 * Potencia de dois
	 */
	
	
}
