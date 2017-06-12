import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class DeprecatedMain {
	
	public static void main(String[] args) throws SQLException {
		System.out.println("Iniciando programa...");		
		
		HiveJdbcClient dbClient = new HiveJdbcClient("subus");
		ResultSet res = dbClient.getResExp("nropardetenido", "216", "I", 8,dbClient.NOV);
		double[] values = dbClient.res2Double(res);
		
		System.out.println("TEsting Chart");
		//TODO TEST HISTOGRAM
		/*HistogramDataset dataset = new HistogramDataset();
		double[] values = {1.0,1.0,1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0};
		dataset.addSeries("H1", values, 10, 0.0, 10.0);
		HistogramChart histChart =  new HistogramChart("MiTitulo", dataset);
		
		JFreeChart chart = histChart.buildHistogram(values, 4, "PlotTitulo", "EJE X", "EJE Y",true);
		*/
        
		//double[] values = {1.0,1.0,1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0};
		
		//Esta clase está en desuso, para mostrar histograma descomentar la siguiente línea
		//HistogramChart.buildHistogram("PlotTitulo","Subtitulo","EJE X", "EJE Y",values, 20, "leyenda",HistogramChart.FREQUENCY);
		System.out.println("FIN DEL PROGRAMA");
		
		
		/*
		HistogramDataset dataset = new HistogramDataset();
		double[] values = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0};
		dataset.addSeries("H1", values, 10, 0.0, 10.0);
		
		JFreeChart chart = createChart(dataset);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);
		*/
		
		
		
		//TODO Test JFREECHART
    	/*System.out.println("INICIANDO JFreeChartDemo");
        final XYSeriesDemo3 demo = new XYSeriesDemo3("XY Series Demo 3");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
		*/
        //TODO hasta aca todo bien.
        
        // CREATE TABLE 
		//String tableName = "testHiveDriverTable";
		//stmt.execute("drop table if exists " + tableName);
		//stmt.execute("create table " + tableName + " (key int, value string)");
		
		
		//String tableName = "testHiveDriverTable";
		
		// describe table
		//sql = "describe " + tableName;
		//System.out.println("Running: " + sql);
		//res = stmt.executeQuery(sql);	//execute sql query in hive
		//while (res.next()) {
		//	String tmpStr = "";    
		//	for (int i = 1; i< res.getMetaData().getColumnCount()+1; i++){
		//	    tmpStr += res.getString(i);
		//	}
		//	System.out.println(tmpStr);
		//	System.out.println(res.getString(1) + "\t" + res.getString(2));
		//}

		// load data into table
		// NOTE: filepath has to be local to the hive server
		// NOTE: /tmp/a.txt is a ctrl-A separated file with two fields per line
		//String filepath = "/tmp/a.txt";
		//sql = "load data local inpath '" + filepath + "' into table " + tableName;
		//System.out.println("Running: " + sql);
		//stmt.execute(sql);

		// select * query
		//sql = "select * from " + tableName;
		//System.out.println("Running: " + sql);
		//res = stmt.executeQuery(sql);
		//while (res.next()) {
		//	System.out.println(String.valueOf(res.getInt(1)) + "\t" + res.getString(2));
		//}

		// regular hive query
		//sql = "select count(1) from " + tableName;
		//System.out.println("Running: " + sql);
		//res = stmt.executeQuery(sql);
		//while (res.next()) {
		//	System.out.println(res.getString(1));
		//}
	}
}
