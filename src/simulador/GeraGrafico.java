package simulador;

import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

public class GeraGrafico {

	String tipoY;
	// = "Slots Vazios";
	private double[] xData;
	//= new double[10];
	private double[] lower;
	//= new double[10];
	private double[] schoute;
	//= new double[10];
	private double[] ilcm;
	//= new double[10];
	private double[] eomlee;
	double[][] yData = new double[][] {lower, schoute, ilcm, eomlee};
	
	//metodo Render();
	public GeraGrafico (double[] lower, double [] schoute, double[] ilcm, double[] eomlee, double[] xData, String tipoY) {
		this.lower = lower;
		this.schoute = schoute;
		this.ilcm = ilcm;
		this.eomlee = eomlee;
		this.xData = xData;
		this.tipoY = tipoY;
		
	}

	public void Render(){
		double[][] yData = new double[][] {lower, schoute, ilcm, eomlee}; //Ajustar a posição dessa declaração
		
		String[] names = new String[] {"Lower","Schoute","ILCM-SbS","Eom-Lee"};
		// Create Chart	
		XYChart chart = QuickChart.getChart("Desempenho dos Estimadores", "Número de Tags", tipoY, names, xData, yData);
		chart.setWidth(700);
		chart.setHeight(400);
		// Show it
		new SwingWrapper(chart).displayChart();
	}

}
