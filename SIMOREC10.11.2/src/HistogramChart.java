/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2004, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * ------------------
 * XYSeriesDemo3.java
 * ------------------
 * (C) Copyright 2004, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: XYSeriesDemo3.java,v 1.6 2004/05/05 16:28:55 mungady Exp $
 *
 * Changes
 * -------
 * 03-Feb-2004 : Version 1 (DG);
 *
 */


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

import org.apache.avro.generic.GenericData.Array;
import org.apache.commons.math.stat.Frequency;
import org.apache.hadoop.hive.metastore.partition.spec.PartitionSpecWithSharedSDProxy.Iterator;
import org.datanucleus.store.types.backed.Collection;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartTheme;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.title.Title;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.TextAnchor;
import org.jfree.ui.VerticalAlignment;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.statistics.*;

import com.codahale.metrics.Histogram;
/**
 * This demo shows a simple bar cgetQuantilehart created using the {@link XYSeriesCollection} dataset.
 *
 */
public class HistogramChart{
	public static final int FREQUENCY = 1;
	public static final int RELATIVE_FREQUENCY = 2;
	public static final int SCALE_AREA_TO_1 = 3;
	public static final int CUMMULATIVE_FREQUENCY = 4;
	public static final int CUMMULATIVE_FREQUENCY_PERCENT = 5;
	

	/*
	 * Create and show an histogram
	 */
	public static JFreeChart buildHistogram(String plotTitle, String xAxisLabel, String yAxisLabel, double[] values, int steps, String legend, int type, Double minPercent, Double maxPercent) {
		
		if (	type == HistogramChart.FREQUENCY || 
				type == HistogramChart.RELATIVE_FREQUENCY||
				type == HistogramChart.SCALE_AREA_TO_1){
			HistogramDataset hds = new HistogramDataset();
			hds.setType(HistogramType. FREQUENCY);
			hds.addSeries(legend, values, steps);
			
			PlotOrientation orientation = PlotOrientation.VERTICAL;
			boolean showLegend = true;
			boolean toolTips = false;
			boolean urls = false;
			JFreeChart chart = ChartFactory.createHistogram(plotTitle, xAxisLabel,
					yAxisLabel, hds, orientation, showLegend, toolTips, urls);
			
			//TODO agregar subtitulos
			// Agregar subtitulos
			ArrayList<Title> subTitles = new ArrayList<Title>();
	        
			//Agregar promedio
			double prom = getMean(values);
			prom = Math.round(prom);
			subTitles.add(createSubtitle("Promedio: " + prom));
			
			//Agregar varianza
			double varianza = getVariance(values);
			varianza = Math.round(varianza);
			subTitles.add(createSubtitle("Varianza: " + varianza));
			
			//Agregar desviación estandar
			double desviacion = getStdDev(values);
			desviacion = Math.round(desviacion);
			subTitles.add(createSubtitle("Desviación estandar: " + desviacion));
			
			//Agregar min,max
			double min = getMin(values);
			double max = getMax(values);
			subTitles.add(createSubtitle("Min:" + min + "     Max:" + max));
			
	        // Agregar subtitulos con puntos de corte
			//TODO verificar puntos de corte en los subtitulos del gráfico
	        double corte_min = HiveJdbcClient.round( HistogramChart.getQuantile(minPercent, values), 2);
			double corte_max = HiveJdbcClient.round( HistogramChart.getQuantile(maxPercent, values), 2);
			subTitles.add(createSubtitle("Percentil: " + minPercent + " corta en " + corte_min));
			subTitles.add(createSubtitle("Percentil: " + maxPercent + " corta en " + corte_max));
			
	        // Insertar subtitulos en el gráfico
			Collections.reverse(subTitles);
	        chart.setSubtitles(subTitles);
			
			
			//configuration
			/*final CategoryPlot plot = chart.getCategoryPlot();
			plot.setBackgroundPaint(Color.lightGray);
	        plot.setDomainGridlinePaint(Color.white);
	        plot.setRangeGridlinePaint(Color.white);

	        // set the range axis to display integers only...
	        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
	        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

	        // disable bar outlines...
	        final BarRenderer renderer = (BarRenderer) plot.getRenderer();
	        renderer.setDrawBarOutline(true);
	        renderer.setBarPainter(new StandardBarPainter());
	        */
	        //
	        
	        
			
			final ChartPanel chartPanel = new ChartPanel(chart);
			//chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
			chartPanel.setPreferredSize(new java.awt.Dimension(1000, 750));
	       
			//chartPanel.setBackground(Color.BLUE);
			//Make jframe windows' title in one line (without \n)
	        JFrame FrameHistogram = new JFrame(plotTitle.replaceAll("(\r\n|\n)", " - "));
			FrameHistogram.setContentPane(chartPanel);
			
	        FrameHistogram.pack();
	        RefineryUtilities.centerFrameOnScreen(FrameHistogram);
	        FrameHistogram.setVisible(true);
	        FrameHistogram.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			
	        return chart;
		}
		if(		type == HistogramChart.CUMMULATIVE_FREQUENCY||
				type == HistogramChart.CUMMULATIVE_FREQUENCY_PERCENT){
			System.out.println("TESTING");
			//TESTING
			//data
			
			
			final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
	        //dataset.addValue(y_value, "Serie 1", X);
			dataset.addValue(1.0, "Serie 1", "Step1");
			dataset.addValue(3.0, "Serie 1", "Step2");
			dataset.addValue(2.0, "Serie 1", "Step3");
	        
			
	        // create the chart...

	        // create the chart...
	        final JFreeChart chart = ChartFactory.createBarChart(
	            "Bar Chart Demo",         // chart title
	            "Category",               // domain axis label
	            "Value",                  // range axis label
	            dataset,                  // data
	            PlotOrientation.VERTICAL, // orientation
	            true,                     // include legend
	            true,                     // tooltips?
	            false                     // URLs?
	        );

	        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

	        // set the background color for the chart...
	        chart.setBackgroundPaint(Color.white);

	        // get a reference to the plot for further customisation...
	        final CategoryPlot plot = chart.getCategoryPlot();
	        plot.setBackgroundPaint(Color.lightGray);
	        plot.setDomainGridlinePaint(Color.white);
	        plot.setRangeGridlinePaint(Color.white);

	        // set the range axis to display integers only...
	        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
	        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

	        // disable bar outlines...
	        final BarRenderer renderer = (BarRenderer) plot.getRenderer();
	        renderer.setDrawBarOutline(true);
	        renderer.setBarPainter(new StandardBarPainter());
	        // set up gradient paints for series...
	        /*final GradientPaint gp0 = new GradientPaint(
	            0.0f, 0.0f, Color.blue, 
	            0.0f, 0.0f, Color.lightGray
	        );
	        final GradientPaint gp1 = new GradientPaint(
	            0.0f, 0.0f, Color.green, 
	            0.0f, 0.0f, Color.lightGray
	        );
	        final GradientPaint gp2 = new GradientPaint(
	            0.0f, 0.0f, Color.red, 
	            0.0f, 0.0f, Color.lightGray
	        );
	        renderer.setSeriesPaint(0, gp0);
	        renderer.setSeriesPaint(1, gp1);
	        renderer.setSeriesPaint(2, gp2);
		*/
	        final CategoryAxis domainAxis = plot.getDomainAxis();
	        domainAxis.setCategoryLabelPositions(
	            CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0)
	        );
	        // OPTIONAL CUSTOMISATION COMPLETED.

	        JFrame FrameHistogram = new JFrame(plotTitle);
	        final ChartPanel chartPanel = new ChartPanel(chart);
	        chartPanel.setPreferredSize(new Dimension(1000, 750));
	        FrameHistogram.setContentPane(chartPanel);

			
	        FrameHistogram.pack();
	        RefineryUtilities.centerFrameOnScreen(FrameHistogram);
	        FrameHistogram.setVisible(true);
	        FrameHistogram.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			
			//END TESTING
	        return chart;
	        
		}
		//TODO finish this
		return null;
		
		
        /*
        Map<Integer,Integer> frequencyDicctionary = new HashMap<Integer,Integer>();
		frequencyDicctionary.put(5,25);
		int a = (int) frequencyDicctionary.keySet().toArray()[3];
		if (frequencyDicctionary.containsKey(5)){
			System.out.println("No");
		}
		else{
			int a = frequencyDicctionary.get(5);
			System.out.println("a:" + a);
			frequencyDicctionary.put(5, frequencyDicctionary.get(5) +1);
			System.out.println(frequencyDicctionary.get(5));
		}
	//map.put(key, map.get(key) + 1);
		*/
	}
	
	private static TextTitle createSubtitle(String subtitle){
		return new TextTitle(subtitle,
        	    new Font("Dialog", Font.PLAIN, 14), Color.black,
        	    RectangleEdge.BOTTOM, HorizontalAlignment.CENTER,
        	    VerticalAlignment.BOTTOM, RectangleInsets.ZERO_INSETS);
	}

	//automaticly set the steps
	public static JFreeChart buildHistogram(String plotTitle, String xAxisLabel, String yAxisLabel, double[] values, String legend, int type, Double minPercent, Double maxPercent) {
		//calcular el numero de steps
		int steps = 1;
		if (values.length != 0){
			double max = values[0];
			double min = values[0];
			for (int i=0; i< values.length; i++){
				if (values[i]>max){max = values[i];}
				if(values[i]<min){min = values[i];}
			}
			//steps = (int) (max-min+1);
			steps = (int) Math.ceil( max);
		}
		return buildHistogram(plotTitle, xAxisLabel, yAxisLabel, values, steps, legend, type, minPercent, maxPercent);
	}
	
	
	public static Map getFrequency(double[] values){
		Map<String,Integer> frequencyMap = new HashMap<String,Integer>();
		for (int i=0; i<values.length; i++){
			String valStr = String.valueOf( values[i]);
			if (frequencyMap.containsKey(valStr)){
				frequencyMap.put(valStr, frequencyMap.get(valStr)+1);
			}
			else{
				frequencyMap.put(valStr, 1);
			}
		}
		
		Object[] arr = frequencyMap.keySet().toArray();
		for( int i=0; i<arr.length; i++){
			//System.out.println(frequencyMap.get(arr[i]));
		}
		return frequencyMap;
	}
	
	/*
	public static void getCummulativeFrequency(double[] values){
		Map<String,Integer> frequencyMap = getFrequency(values);
		ArrayList arrlist = new ArrayList();
		for (int i=0; i<values.length; i++){
			arrlist.add(values[i]);
		}
		
		
		
		
		
		
		Collections.frequency(arrlist, arrlist.get(0));
		
		Map<String,Integer> cummulativeFrequencyMap = new HashMap<String,Integer>();
		String[] keys = (String[]) frequencyMap.keySet().toArray();
		ArrayList keyList= new ArrayList();
		//for every <key,frequency>
		
		for (int i=0; i<keys.length; i++){
			String key = keys[i];
			int freq = frequencyMap.get(key);
			
		}
	}
	*/
	
	/*
	 * Entrega el punto de corte desde donde se encuentra el % de la población
	 * percent from 0 to 1
	 */
	public static double getQuantile(double percent, double[] values) throws IllegalArgumentException{
		if (percent > 1.0 || percent <0){
			throw new IllegalArgumentException();
		}
		
		int n = values.length;
		//System.out.println("max_freq:" + n);
		//double step = 100/n;
		//double category = values[0];
		//double lastcategory = category;
		//double sum= 0;
		//double lastSum= sum;
		
		//get categories
		ArrayList<Double> categories = new ArrayList<Double>();
		for( int i=0; i<n; i++){
			if (!categories.contains(values[i])){
				categories.add(values[i]);
			}
		}
		
		//sort
		ArrayList<Double> sortedCategories = new ArrayList<Double>();
		while(!categories.isEmpty()){
			double min =  categories.get(0);
			int index = 0;
			for( int i=0; i<categories.size(); i++){
				if (categories.get(i)< min){
					index = i;
					min = categories.get(index);
				}
			}
			sortedCategories.add(min);
			categories.remove(index);
		}
		
		//values -> valuesList
		ArrayList<Double> valuesList = new ArrayList<Double>();
		for (int i=0; i<values.length; i++){
			valuesList.add(values[i]);
		}
		
		//frequencyArray  (Linked to sortedCategories)
		ArrayList<Integer> frequencyArray = new ArrayList<Integer>();
		for(int i=0; i<sortedCategories.size(); i++){
			int freq = Collections.frequency(valuesList, sortedCategories.get(i));
			frequencyArray.add(freq);
		}
		
		// cumulative frequency (Linked to sortedCategories)
		ArrayList<Integer> cumulativeFrequencyArray = new ArrayList<Integer>();
		for(int i=0; i<sortedCategories.size(); i++){ //for every category
			int sum = 0;
			for(int j=0; j<=i; j++){
				sum += frequencyArray.get(j);
			}
			cumulativeFrequencyArray.add(sum);
		}
		
		//DEBUG
		/*System.out.println("############################");
		for(int i=0; i<sortedCategories.size(); i++){
			double cat = sortedCategories.get(i);
			double fc = cumulativeFrequencyArray.get(i);
			System.out.println(cat + ";" + fc);
		}
		System.out.println("############################");
		*/
		//END DEBUG
		
		// cumulative frequency percent (Linked to sortedCategories)
		ArrayList<Double> cumulativeFrequencyPercentArray = new ArrayList<Double>();
		for (int i=0; i<cumulativeFrequencyArray.size(); i++){
			cumulativeFrequencyPercentArray.add(cumulativeFrequencyArray.get(i) / (double)n);
			//System.out.println("["+i+"] Cat:" + sortedCategories.get(i) + "| CumFreq:" + cumulativeFrequencyArray.get(i) / (double)n);
		}
		
		//get Quantiles
		for(int i=0; i<cumulativeFrequencyPercentArray.size(); i++){
			if (cumulativeFrequencyPercentArray.get(i) >=percent){
				//System.out.println("cumulativeFrequencyPercentArray[" + i + "]:" + cumulativeFrequencyPercentArray.get(i) + ">="+ percent);
				if (cumulativeFrequencyPercentArray.get(i) ==percent){
					//System.out.println("cumulativeFrequencyPercentArray[" + i + "]:" + cumulativeFrequencyPercentArray.get(i) + "="+ percent);
					//System.out.println("returning sortedCategories[" + i + "]:" + sortedCategories.get(i));
					return  sortedCategories.get(i);
				}
				else{ //is > 
					//System.out.println("cumulativeFrequencyPercentArray[" + i + "]:" + cumulativeFrequencyPercentArray.get(i) + ">"+ percent);
					//tratar de tomar la categoría anterior
					if( i>0){//existe una categoria antes que esta
						//System.out.println("i:" + i + "is >0");
						double y2_cumulativeFreq = cumulativeFrequencyPercentArray.get(i);
						double y1_lastCumulativeFreg= cumulativeFrequencyPercentArray.get(i-1);
						double x2_category = sortedCategories.get(i);
						double x1_lastCategory = sortedCategories.get(i-1);
						
						//rename for easy use
						double y2 = y2_cumulativeFreq;
						double y1 = y1_lastCumulativeFreg;
						double x2 = x2_category;
						double x1 = x1_lastCategory;
						
						//calculate Y= Xm + c   o  X= (Y-c)/m
						double m = (y2-y1) / (x2-x1);
						double c = y2 - (m*x2);
						double x = (percent -c) / m;
						return x;
					}
					else{//es la primera categoría
						//System.out.println("i:" + i + "is =0 [Primera cat]");
						//System.out.println("returning sortedCategories[" + i + "]:" + sortedCategories.get(i));
						return sortedCategories.get(i);
					}
				}
			}
		}
		throw new IllegalArgumentException();
	}
	
	
	/*
	 * Entrega el punto de corte desde donde se encuentra el % de la población
	 * percent from 0 to 1
	 */
	public static double getQuantileOld(double percent, double[] values) throws IllegalArgumentException{
		if (percent > 1.0 || percent <0){
			throw new IllegalArgumentException();
		}
		
		int n = values.length;
		//System.out.println("max_freq:" + n);
		//double step = 100/n;
		//double category = values[0];
		//double lastcategory = category;
		//double sum= 0;
		//double lastSum= sum;
		
		//get categories
		ArrayList<Double> categories = new ArrayList<Double>();
		for( int i=0; i<n; i++){
			if (!categories.contains(values[i])){
				categories.add(values[i]);
			}
		}
		
		//sort
		ArrayList<Double> sortedCategories = new ArrayList<Double>();
		while(!categories.isEmpty()){
			double min =  categories.get(0);
			int index = 0;
			for( int i=0; i<categories.size(); i++){
				if (categories.get(i)< min){
					index = i;
					min = categories.get(index);
				}
			}
			sortedCategories.add(min);
			categories.remove(index);
		}
		
		//values -> valuesList
		ArrayList<Double> valuesList = new ArrayList<Double>();
		for (int i=0; i<values.length; i++){
			valuesList.add(values[i]);
		}
		
		//frequencyArray  (Linked to sortedCategories)
		ArrayList<Integer> frequencyArray = new ArrayList<Integer>();
		for(int i=0; i<sortedCategories.size(); i++){
			int freq = Collections.frequency(valuesList, sortedCategories.get(i));
			frequencyArray.add(freq);
		}
		
		// cumulative frequency (Linked to sortedCategories)
		ArrayList<Integer> cumulativeFrequencyArray = new ArrayList<Integer>();
		for(int i=0; i<sortedCategories.size(); i++){ //for every category
			int sum = 0;
			for(int j=0; j<=i; j++){
				sum += frequencyArray.get(j);
			}
			cumulativeFrequencyArray.add(sum);
		}
		
		//DEBUG
		/*System.out.println("############################");
		for(int i=0; i<sortedCategories.size(); i++){
			double cat = sortedCategories.get(i);
			double fc = cumulativeFrequencyArray.get(i);
			System.out.println(cat + ";" + fc);
		}
		System.out.println("############################");
		*/
		//END DEBUG
		
		// cumulative frequency percent (Linked to sortedCategories)
		ArrayList<Double> cumulativeFrequencyPercentArray = new ArrayList<Double>();
		for (int i=0; i<cumulativeFrequencyArray.size(); i++){
			cumulativeFrequencyPercentArray.add(cumulativeFrequencyArray.get(i) / (double)n);
			//System.out.println("["+i+"] Cat:" + sortedCategories.get(i) + "| CumFreq:" + cumulativeFrequencyArray.get(i) / (double)n);
		}
		
		//get Quantiles
		for(int i=0; i<cumulativeFrequencyPercentArray.size(); i++){
			if (cumulativeFrequencyPercentArray.get(i) >=percent){
				//System.out.println("cumulativeFrequencyPercentArray[" + i + "]:" + cumulativeFrequencyPercentArray.get(i) + ">="+ percent);
				if (cumulativeFrequencyPercentArray.get(i) ==percent){
					//System.out.println("cumulativeFrequencyPercentArray[" + i + "]:" + cumulativeFrequencyPercentArray.get(i) + "="+ percent);
					//System.out.println("returning sortedCategories[" + i + "]:" + sortedCategories.get(i));
					return  sortedCategories.get(i);
				}
				else{ //is > 
					//System.out.println("cumulativeFrequencyPercentArray[" + i + "]:" + cumulativeFrequencyPercentArray.get(i) + ">"+ percent);
					//tratar de tomar la categoría anterior
					if( i>0){//existe una categoria antes que esta
						//System.out.println("i:" + i + "is >0");
						double cumulativeFreq = cumulativeFrequencyPercentArray.get(i);
						double lastCumulativeFreg= cumulativeFrequencyPercentArray.get(i-1);
						double diffUp = cumulativeFreq-percent;
						double diffDown = percent-lastCumulativeFreg;
						//System.out.println("cumulativeFreq: " + cumulativeFreq);
						//System.out.println("lastCumulativeFreg: " + lastCumulativeFreg);
						//System.out.println("diffUp: " + diffUp);
						//System.out.println("diffDown: " + diffDown);
						if (diffUp < diffDown){
							//System.out.println("diffUp < diffDown");
							//System.out.println("returning sortedCategories[" + i + "]:" + sortedCategories.get(i));
							return sortedCategories.get(i);
						}
						else{
							//System.out.println("diffUp >= diffDown");
							//System.out.println("returning sortedCategories[" + (i-1) + "]:" + sortedCategories.get(i-1));
							return sortedCategories.get(i-1);
							
						}
					}
					else{//es la primera categoría
						//System.out.println("i:" + i + "is =0 [Primera cat]");
						//System.out.println("returning sortedCategories[" + i + "]:" + sortedCategories.get(i));
						return sortedCategories.get(i);
					}
				}
			}
		}
		throw new IllegalArgumentException();
	}
	
	//Reference for stadistic indicators:
	//http://stackoverflow.com/questions/7988486/how-do-you-calculate-the-variance-median-and-standard-deviation-in-c-or-java
	public static double getMin(double[] values){
        double min = values[0];
        for(double a : values)
            if (a < min){
            	min = a;
            }
        return min;
    }

	public static double getMax(double[] values){
		  double max = values[0];
	        for(double a : values)
	            if (a > max){
	            	max = a;
	            }
	        return max;
    }

	public static double getMean(double[] values){
        double sum = 0.0;
        for(double a : values)
            sum += a;
        return sum/values.length;
    }
	
	public static double getVariance(double[] values){
        double mean = getMean(values);
        double temp = 0;
        for(double a :values)
            temp += (a-mean)*(a-mean);
        return temp/values.length;
    }
	
	public static double getStdDev(double[] values)
    {
        return Math.sqrt(getVariance(values));
    }
	
    /// NOT USED
	{
    /**
     * Creates a new demo instance.
     *
     * @param title  the frame title.
     */
	/*
    public HistogramChart(final String title, HistogramDataset dataset) {
        super(title);
        JFreeChart chart = createChart(dataset);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);
    }
    */

    /**
     * Creates a sample chart.
     * 
     * @param dataset  the dataset.
     * 
     * @return A sample chart.
     */
    /*private JFreeChart createChart(IntervalXYDataset dataset) {
        final JFreeChart chart = ChartFactory.createXYBarChart(
            "XY Series Demo",
            "X", 
            false,
            "Y", 
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        XYPlot plot = (XYPlot) chart.getPlot();
        final IntervalMarker target = new IntervalMarker(400.0, 700.0);
        target.setLabel("Target Range");
        target.setLabelFont(new Font("SansSerif", Font.ITALIC, 11));
        target.setLabelAnchor(RectangleAnchor.LEFT);
        target.setLabelTextAnchor(TextAnchor.CENTER_LEFT);
        target.setPaint(new Color(222, 222, 255, 128));
        plot.addRangeMarker(target, Layer.BACKGROUND);
        return chart;    
    }
    */
    
    // ****************************************************************************
    // * JFREECHART DEVELOPER GUIDE                                               *
    // * The JFreeChart Developer Guide, written by David Gilbert, is available   *
    // * to purchase from Object Refinery Limited:                                *
    // *                                                                          *
    // * http://www.object-refinery.com/jfreechart/guide.html                     *
    // *                                                                          *
    // * Sales are used to provide funding for the JFreeChart project - please    * 
    // * support us so that we can continue developing free software.             *
    // ****************************************************************************
    
    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    /*public static void main(final String[] args) {
    	System.out.println("INICIANDO!!");
        final XYSeriesDemo3 demo = new XYSeriesDemo3("XY Series Demo 3");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
     
    }
     */
}
}