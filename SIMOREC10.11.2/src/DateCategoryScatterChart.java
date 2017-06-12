import java.awt.geom.Ellipse2D;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.cloudera.com.fasterxml.jackson.annotation.JsonFormat.Shape;


public class DateCategoryScatterChart{

	public static XYDataset createTestingDataset(){
		System.out.println("Creating testing dataset");
		XYSeriesCollection result = new XYSeriesCollection();
		
		XYSeries series1 = new XYSeries("Serie 1");
		XYSeries series2 = new XYSeries("Serie 2");
		try {
			series1.add(0,date2timestamp("2015-11-01 23:00:01", "yyyy-MM-dd HH:mm:ss"));
			series1.add(1,date2timestamp("2015-11-01 23:20:34","yyyy-MM-dd HH:mm:ss"));
			series1.add(2,date2timestamp("2015-11-01 23:40:21","yyyy-MM-dd HH:mm:ss"));
			series1.add(3,date2timestamp("2015-11-01 23:58:21","yyyy-MM-dd HH:mm:ss"));
			series2.add(0,date2timestamp("2015-11-01 23:15:21","yyyy-MM-dd HH:mm:ss"));
			series2.add(1,date2timestamp("2015-11-01 23:59:21","yyyy-MM-dd HH:mm:ss"));
			series2.add(2,date2timestamp("2015-11-02 00:02:21","yyyy-MM-dd HH:mm:ss"));
			series2.add(3,date2timestamp("2015-11-02 00:08:21","yyyy-MM-dd HH:mm:ss"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Add series to seriesCollection
		//result.addSeries(series);
		result.addSeries(series1);
		result.addSeries(series2);
		return (XYDataset)result;
	}
	
	
	public static XYDataset createDataset(String[][] data, String [][] categorias, String serie_nombre, String dateformat){
		XYSeriesCollection serieCollection = new XYSeriesCollection();
		System.out.println("DateCategory.createDataset: etapa 0 completada");
		int n = data.length;
		System.out.println("n:" +n);
		XYSeries serie = new XYSeries(serie_nombre);
		for (int i=0; i<n; i++){
			String x_str = data[i][0];
			int x = -1;
			String y_str = data[i][1];
			int size = Integer.valueOf(data[i][2]);
			
			//Obtener valor de categoria en X
			System.out.println("###############\nDEBUGING: ");
			for(int j=0; j<n;j++){
				String valor = categorias[j][0];
				String etiqueta = categorias[j][1];
				System.out.println("valor:" + valor + ", etiqueta:" + etiqueta + ", x_str:" + x_str);
				if( x_str.compareTo(etiqueta) == 0 ){
					x = Integer.valueOf(valor)-1;
				}
			}
			double y = -1;
			//convertir Y en timestamp
			try {
				y = date2timestamp(y_str, dateformat);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			
			//Agregar x,y a la serie
			serie.add(x,y);
			System.out.println("" + x + ", " + y);
		}
		serieCollection.addSeries(serie);
		System.out.println("DateCategory.createDataset: etapa 1 completada");
		return (XYDataset) serieCollection;
	}
	
	
	/**
	 * 
	 * @param data X(int),Y(date),size
	 * @param Categorias valor, etiqueta
	 * @return XYDataset
	 */
	/*public static XYDataset createDataset(String[][] data, String [][] categorias, String serie_nombre, String dateformat){
		XYSeriesCollection serieCollection = new XYSeriesCollection();
		
		System.out.println("createDataset called");
		int n = data.length;
		System.out.println("n:" + n);
		
		XYSeries serie = new XYSeries(serie_nombre);
		for (int i=0; i<n; i++){
			String x_str = data[i][0];
			System.out.println("x_str:" + x_str);
			int x = -1;
			String y_str = data[i][1];
			int size = Integer.valueOf(data[i][2]);
			
			//Obtener valor de categoria en X
			for(int j=0; j<n;j++){
				String valor = categorias[j][0];
				String etiqueta = categorias[j][1];
				if( y_str.compareTo(etiqueta) == 0 ){
					x = Integer.valueOf(valor);
					System.out.println("x:" + x);
				}
			}
			double y = -1;
			//convertir Y en timestamp
			try {
				System.out.println("y_str:" + y_str);
				y = date2timestamp(y_str, dateformat);
				System.out.println("y:" + y);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			
			//Agregar x,y a la serie
			serie.add(x,y);
		}
		serieCollection.addSeries(serie);
		return (XYDataset) serieCollection;
	}
	*/
	public static JFreeChart buildChart (XYDataset dataset, String dateFormat, String[] categorias) {

		JFreeChart chart = ChartFactory.createScatterPlot("Titulo", "Eje X", "Eje Y",dataset);
		
		ChartFrame frame = new ChartFrame("Chart", chart);
		frame.pack();
		frame.setVisible(true);
		
		XYDataset dataset2 = createTestingDataset();
		JFreeChart chart2 = ChartFactory.createTimeSeriesChart("Titulo2", "Eje X2", "Eje Y2",dataset2);
		
		final XYPlot plot = chart.getXYPlot();
		
		//Dot size, but the dot is displaced
		/*plot.setRenderer(new XYShapeRenderer(){
			@Override
			public java.awt.Shape getItemShape(int row, int column){
				System.out.println(row + "," + column);
				try{
					return new Ellipse2D.Double(-50,-50,50,50);
				} catch (Exception e){
					return new Ellipse2D.Double(-10,-10,10,10);				}
			}
		});
		*/
		
		SymbolAxis symbolAxis = new SymbolAxis("Paraderos",categorias);
		plot.setDomainAxis(symbolAxis);
		
		DateAxis dateAxis = new DateAxis("DateAxis");
		dateAxis.setDateFormatOverride(new SimpleDateFormat(dateFormat));
		plot.setRangeAxis(dateAxis);
		
		
		ChartFrame frame2 = new ChartFrame("Chart", chart);
		frame.pack();
		frame.setVisible(true);
		
		
		return chart; //TODO Maybe return frame
	}
	
	
	public static double date2timestamp(String strDate, String dateFormat) throws ParseException{
		DateFormat formatter = new SimpleDateFormat(dateFormat);
		java.util.Date date;
		date = formatter.parse(strDate);
		long output=date.getTime()/1000L;
		String str=Long.toString(output);
		long timestampLong = Long.parseLong(str) * 1000;
		double timestamp = Double.parseDouble(str + "000.0");
		//System.out.println(strDate + " -> " + timestamp);
		return timestamp;
	}

	public static void testing(){
		String[] categorias = {"Par1","Par2","Par3","Par4"};
		buildChart(createTestingDataset(),"HH:mm:ss", categorias);  //change for the actual dataset, or just forget it, it's just a testing
	}


}
