import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.jdatepicker.JDateComponentFactory;
import org.jdatepicker.JDatePicker;
import org.jfree.data.xy.XYDataset;


public class RankingPanel extends JPanel{
	//JDBC Client
	HiveJdbcClient dbClient;
	
	//JFrame
	JFrame frame;
	
	//RankingPanel variables
	private int selectedRow = -1;
	private String fechaIni;
	private String fechaFin;
	
	private int selectedRow2 = -1;
	
	//Declaration of PanelRanking's sub panels
	private JPanel panelRanking1;
	private JPanel panelRanking2;
	private JDateComponentFactory datePickerFactory;
	
	//Panel1's items
	private JTable tableRanking;
	private JLabel lblInicio;
	private JLabel lblFin;
	private JDatePicker datePickerInicio;
	private JDatePicker datePickerFin;
	private JLabel filler1;
	private JButton btnVerRanking;
	private JButton btnRankingDetalle;
	
	//Panel2's items
	private JScrollPane jscrollPane;
	private JTable tableRanking2;
	private JLabel lblRut;
	private JLabel lblConductor;
	private JLabel lblDesde;
	private JLabel lblHasta;
	private JLabel lblFechaIni;
	private JLabel lblFechaFin;
	private JLabel Filler2;
	private JButton btnRanking2Atras;
	private JButton btnRanking2GraficarExpedicion;
	
	
	
	
	//Constructor
	/**
	 * 
	 */
	public RankingPanel(HiveJdbcClient dbClient, JFrame frame) {
		super();
		
		this.dbClient = dbClient;
		this.frame = frame;		
		panelRanking1 = new JPanel();	
		panelRanking2 = new JPanel();
		panelRanking2.setVisible(false);


		this.add(panelRanking1);
		this.add(panelRanking2);
		
		datePickerFactory = new JDateComponentFactory();
		initialize();
	}

	private HiveJdbcClient getDBClient(){
		return this.dbClient;
	}
	
	
	/*
	 * Retorna la fecha en un JDatePicker con el formato YYYY/MM/DD
	 * TODO maybe move this to a new class
	 */
	private static String datePickerGetDate(JDatePicker dp) {
		// Toma los valores de los datePicker
		String anno = String.format("%04d", dp.getModel().getYear());
		String mes = String.format("%02d", dp.getModel().getMonth() + 1);
		// Por alguna razón los meses parten de 0


		String dia = String.format("%02d", dp.getModel().getDay());

		String date = anno + "-" + mes + "-" + dia;
		return date;
	}

	private String getSelectedRut(){
		if (tableRanking != null && selectedRow>-1){
			try{
				return (String) tableRanking.getValueAt(selectedRow, 0);
			} catch (ArrayIndexOutOfBoundsException e){
				System.out.println("ERROR: Could'nt get value at" + selectedRow + ", " + 0);
				return null;
			}
		} else {
			return null;
		}
	}
	
	/*
	 * Retorna rut, inicio_exp (yyyy-mm-dd hh:mm:ss)?
	 */
	private String[] getSelectedExp(){
		/*
		 * Header:
		 {"Fecha", " Servicio", " Sentido", 
		  " Inicio de expedición", " Número de validaciones", " Clasificación de validaciones", 
		  " Promedio M para validaciones", " Promedio A para validaciones", " Promedio B para validaciones", 
		  " Distancia al tiempo promedio", " Clasificación distancia tiempo promedio", 
		  " Promedio M para distancia tiempo promedio", "Promedio A para distancia tiempo promedio", 
		  "Promedio B para distancia tiempo promedio"};
		*/
		
		if (tableRanking2 != null && selectedRow2>-1){
			try{
				String servicio = (String) tableRanking2.getValueAt(selectedRow2, 1);
				String sentido = (String) tableRanking2.getValueAt(selectedRow2, 2);
				String inicio_exp = (String) tableRanking2.getValueAt(selectedRow2, 3);
				String[] out = {servicio, sentido, getSelectedRut(),inicio_exp};
				return out;
			} catch (ArrayIndexOutOfBoundsException e){
				System.out.println("ERROR: Could'nt get value at" + selectedRow2 + ", " + 3);
				return null;
			}
		} else {
			return null;
		}
	}
	
	private void initialize() {
		//PanelRanking1
		GridBagLayout gbl_panelRanking1 = new GridBagLayout();
		gbl_panelRanking1.columnWidths = new int[]{70, 65, 0, 150, 0, 0};
		gbl_panelRanking1.rowHeights = new int[]{32, 0};
		gbl_panelRanking1.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panelRanking1.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panelRanking1.setLayout(gbl_panelRanking1);

		//lblInicio
		lblInicio = new JLabel(" Inicio: ");
		GridBagConstraints gbc_lblInicio = new GridBagConstraints();
		gbc_lblInicio.insets = new Insets(5, 5, 5, 5);
		gbc_lblInicio.gridx = 0;
		gbc_lblInicio.gridy = 0;
		panelRanking1.add(lblInicio, gbc_lblInicio);

		//lblFin
		lblFin = new JLabel(" Fin: ");
		GridBagConstraints gbc_lblPp = new GridBagConstraints();
		gbc_lblPp.insets = new Insets(5, 5, 5, 5);
		gbc_lblPp.anchor = GridBagConstraints.WEST;
		gbc_lblPp.gridx = 2;
		gbc_lblPp.gridy = 0;
		panelRanking1.add(lblFin, gbc_lblPp);

		//datePickerInicio
		datePickerInicio = datePickerFactory.createJDatePicker();
		GridBagConstraints gbc_lblDatePickerInicio = new GridBagConstraints();
		gbc_lblDatePickerInicio.insets = new Insets(5, 5, 5, 5);
		gbc_lblDatePickerInicio.anchor = GridBagConstraints.WEST;
		gbc_lblDatePickerInicio.gridx = 1;
		gbc_lblDatePickerInicio.gridy = 0;
		panelRanking1.add((Component) datePickerInicio, gbc_lblDatePickerInicio);

		//datePickerFin
		datePickerFin = datePickerFactory.createJDatePicker();
		GridBagConstraints gbc_lblDatePickerFin = new GridBagConstraints();
		gbc_lblDatePickerFin.insets = new Insets(5, 5, 5, 5);
		gbc_lblDatePickerFin.anchor = GridBagConstraints.WEST;
		gbc_lblDatePickerFin.gridx = 3;
		gbc_lblDatePickerFin.gridy = 0;
		panelRanking1.add((Component) datePickerFin, gbc_lblDatePickerFin);

		//filler
		filler1 = new JLabel("                                                                                                                                                                                           ");
		GridBagConstraints gbc_filler1 = new GridBagConstraints();
		gbc_filler1.insets = new Insets(5, 5, 5, 5);
		gbc_filler1.gridx = 4;
		gbc_filler1.gridy = 0;
		gbc_filler1.gridwidth = 7;
		panelRanking1.add(filler1, gbc_filler1);

		//btnVerRanking
		btnVerRanking = new JButton("Ver Ranking");
		GridBagConstraints gbc_btnVerRanking = new GridBagConstraints();
		gbc_btnVerRanking.insets = new Insets(5, 5, 5, 5);
		gbc_btnVerRanking.gridx = 11;
		gbc_btnVerRanking.gridy = 0;
		panelRanking1.add(btnVerRanking, gbc_btnVerRanking);
		btnVerRanking.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//Toma los valores de los datePicker
				fechaIni = datePickerGetDate(datePickerInicio);
				fechaFin = datePickerGetDate(datePickerFin);

				//ResultSet res = dbClient.getRankingMAB(fechaIni, fechaFin);
				ResultSet res = dbClient.getRankingCalificacion(fechaIni, fechaFin);
				String[][] strMatrix = dbClient.res2StringMatrix(res);
				if (strMatrix != null){
					//Hay datos que mostrar en la tabla
					//System.out.println("CONTROL 3");
					btnRankingDetalle.setEnabled(true);
					selectedRow=0;
					
					/*String[] header = new String[] {"RUT", "Nro de expediciones", "M_nro_val", "A_nro_val", "B_nro_Val",
							"M_nro_val_%", "A_nro_val_%", "B_nro_Val_%",
							"M_dist_tiempo_prom", "A_dist_tiempo_prom", "B_dist_tiempo_prom",
							"M_dist_tiempo_prom_%", "A_dist_tiempo_prom_%", "B_dist_tiempo_prom_%"};
					*/
					/*String[] header = new String[] {"RUT", "Nro de exp.", "Mv", "Av", "Bv",
							"Mv%", "Av%", "Bv%",
							"MD", "AD", "BD",
							"MD%", "AD%", "BD%"};
					*/
					String[] header = new String[] {"RUT", "Calificación","Nro de exp.", "Mv%", "Av%", "Bv%",
							"MD%", "AD%", "BD%"};
					
					
					//Remover la tabla anterior, si es que hay una
					if (tableRanking != null){
						panelRanking1.remove(tableRanking);
					}
					tableRanking = new JTable(strMatrix,header){
						public boolean getScrollableTracksViewportWidth() {
							return getPreferredSize().width < getParent().getWidth();
							//Esto hace que se vea bonito y ocupe todo el espacio que debe ocupar
						}
						
						//Make cells non-editable
						@Override
						public boolean isCellEditable(int row, int column) {
							//all cells false
							return false;
						}
						
						//Show a tooltip when text is to large
						//public String getToolTipText( MouseEvent e ) {
						//	int row = rowAtPoint( e.getPoint() );
						//	int column = columnAtPoint( e.getPoint() );

						//	Object value = getValueAt(row, column);
						//	return value == null ? null : value.toString();
						//}

					};
					
					//auto-resize the widths of jtables columns dynamically
					/*tableRanking.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );

					for (int column = 0; column < tableRanking.getColumnCount(); column++)
					{
					    TableColumn tableColumn = tableRanking.getColumnModel().getColumn(column);
					    int preferredWidth = tableColumn.getMinWidth();
					    int maxWidth = tableColumn.getMaxWidth();

					    for (int row = 0; row < tableRanking.getRowCount(); row++)
					    {
					        TableCellRenderer cellRenderer = tableRanking.getCellRenderer(row, column);
					        Component c = tableRanking.prepareRenderer(cellRenderer, row, column);
					        int width = c.getPreferredSize().width + tableRanking.getIntercellSpacing().width;
					        preferredWidth = Math.max(preferredWidth, width);

					        //  We've exceeded the maximum width, no need to check other rows

					        if (preferredWidth >= maxWidth)
					        {
					            preferredWidth = maxWidth;
					            break;
					        }
					    }

					    tableColumn.setPreferredWidth( preferredWidth );
					}
					*/
					
					
					GridBagConstraints gbc_tableRankingScrollPane = new GridBagConstraints();
					gbc_tableRankingScrollPane.insets = new Insets(5, 5, 5, 5);
					gbc_tableRankingScrollPane.anchor = GridBagConstraints.CENTER;
					gbc_tableRankingScrollPane.gridx = 0;
					gbc_tableRankingScrollPane.gridy = 3;
					gbc_tableRankingScrollPane.gridwidth = 12;
					gbc_tableRankingScrollPane.fill = GridBagConstraints.BOTH;
					panelRanking1.add(new JScrollPane(tableRanking), gbc_tableRankingScrollPane);
					frame.repaint();

					final ListSelectionModel selectionModelRanking = tableRanking.getSelectionModel();
					selectionModelRanking.addListSelectionListener(new ListSelectionListener() {

						@Override
						public void valueChanged(ListSelectionEvent e) {
							if(! selectionModelRanking.isSelectionEmpty()) {
								//get selected row
								selectedRow = selectionModelRanking.getMinSelectionIndex();
								//JOptionPane.showMessageDialog(null, "Selected row:" + selectedRow +"\n RUT: " + getSelectedRut());
							}
						}
					});


				}
				else{//Problema?, no hay resultados que mostrar
					System.out.println("WHY?!, strMatrix is null" );
				}
				//JOptionPane.showMessageDialog(null, fechaIni + "\n" + fechaFin);

			}
		});
		
		/*
		 * Changes from "Panel Ranking 1" to "Panel Ranking 2"
		 */
		//btnRankingDetalle  
		btnRankingDetalle = new JButton("Ver detalle");
		btnRankingDetalle.setEnabled(false);
		GridBagConstraints gbc_btnRankingDetalle = new GridBagConstraints();
		gbc_btnRankingDetalle.insets = new Insets(5, 5, 5, 5);
		gbc_btnRankingDetalle.gridx = 11;
		gbc_btnRankingDetalle.gridy = 1;
		panelRanking1.add(btnRankingDetalle, gbc_btnRankingDetalle);
		btnRankingDetalle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				panelRanking1.setVisible(false);
				showRanking2();
			}
		});

		
		//*********************************
		//***		PANEL RANKING 2		***		
	    //*********************************

		//Panel Ranking 2
		GridBagLayout gbl_panelRanking2 = new GridBagLayout();
		gbl_panelRanking2.columnWidths = new int[]{65, 0};
		gbl_panelRanking2.rowHeights = new int[]{15, 0};
		gbl_panelRanking2.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_panelRanking2.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panelRanking2.setLayout(gbl_panelRanking2);

		lblConductor = new JLabel("Conductor: ");
		GridBagConstraints gbc_lblConductor = new GridBagConstraints();
		gbc_lblConductor.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblConductor.insets = new Insets(5, 5, 5, 5);
		gbc_lblConductor.gridx = 0;
		gbc_lblConductor.gridy = 0;
		panelRanking2.add(lblConductor, gbc_lblConductor);

		lblDesde = new JLabel("Desde:");
		GridBagConstraints gbc_lblDesde = new GridBagConstraints();
		gbc_lblDesde.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblDesde.insets = new Insets(5,5,5,5);
		gbc_lblDesde.gridx = 2;
		gbc_lblDesde.gridy = 0;
		panelRanking2.add(lblDesde, gbc_lblDesde);

		lblHasta = new JLabel(" hasta: ");
		GridBagConstraints gbc_lblHasta = new GridBagConstraints();
		gbc_lblHasta.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblHasta.insets = new Insets(5,5,5,5);
		gbc_lblHasta.gridx = 4;
		gbc_lblHasta.gridy = 0;
		panelRanking2.add(lblHasta, gbc_lblHasta);

		
		//lblRanking2Rut
		lblRut = new JLabel("---");
		GridBagConstraints gbc_lblRanking = new GridBagConstraints();
		gbc_lblRanking.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblRanking.insets = new Insets(5,5,5,5);
		gbc_lblRanking.gridx = 1;
		gbc_lblRanking.gridy = 0;
		panelRanking2.add(lblRut, gbc_lblRanking);

		//lblFechaIni
		lblFechaIni = new JLabel(fechaIni);
		GridBagConstraints gbc_lblFechaIni = new GridBagConstraints();
		gbc_lblFechaIni.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblFechaIni.insets = new Insets(5,5,5,5);
		gbc_lblFechaIni.gridx = 3;
		gbc_lblFechaIni.gridy = 0;
		panelRanking2.add(lblFechaIni, gbc_lblFechaIni);

		//lblRanking2Rut
		lblFechaFin = new JLabel(fechaFin);
		GridBagConstraints gbc_lblFechaFin = new GridBagConstraints();
		gbc_lblFechaFin.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblFechaFin.insets = new Insets(5,5,5,5);
		gbc_lblFechaFin.gridx = 5;
		gbc_lblFechaFin.gridy = 0;
		panelRanking2.add(lblFechaFin, gbc_lblFechaFin);
		
		//lblRanking2Rut
		JLabel Filler2 = new JLabel("                                                                                                                                                ");
		GridBagConstraints gbc_Filler2 = new GridBagConstraints();
		gbc_Filler2.anchor = GridBagConstraints.NORTHWEST;
		gbc_Filler2.insets = new Insets(5,5,5,5);
		gbc_Filler2.gridx = 6;
		gbc_Filler2.gridy = 0;
		panelRanking2.add(Filler2, gbc_Filler2);
		
		
		//btnRanking2Atras
		btnRanking2Atras = new JButton("Atrás");
		GridBagConstraints gbc_btnRanking2Atras= new GridBagConstraints();
		gbc_btnRanking2Atras.insets = new Insets(5, 5, 5, 5);
		gbc_btnRanking2Atras.gridx = 7;
		gbc_btnRanking2Atras.gridy = 0;
		panelRanking2.add(btnRanking2Atras, gbc_btnRanking2Atras);
		btnRanking2Atras.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				panelRanking2.setVisible(false);
				panelRanking1.setVisible(true);
			}
		});

		//btnRanking2GraficarExpedicion
		btnRanking2GraficarExpedicion = new JButton("Graficar expedición");
		GridBagConstraints gbc_btnRanking2GraficarExpedicion= new GridBagConstraints();
		gbc_btnRanking2GraficarExpedicion.insets = new Insets(5, 5, 5, 5);
		gbc_btnRanking2GraficarExpedicion.gridx = 7;
		gbc_btnRanking2GraficarExpedicion.gridy = 1;
		panelRanking2.add(btnRanking2GraficarExpedicion, gbc_btnRanking2GraficarExpedicion);
		btnRanking2GraficarExpedicion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("selectedRow2:" + selectedRow2);
				String[] exp = getSelectedExp();
				String servicio = exp[0];
				String sentido = exp[1];
				String rut = exp[2];
				String inicio_exp = exp[3];
				
				//get validaciones por paradero en el tiempo y paraderos del servicio,sentido
				System.out.println("GRAFICANDO1:COMIENZAN CONSULTAS");
				ResultSet valParTiempo_res = dbClient.getValParTiempo(rut, inicio_exp); //nombre_paradero, fechahora, nroVal
				System.out.println("GRAFICANDO2:getValParTiempo terminó");
				String[][] valParTiempo_str = dbClient.res2StringMatrix(valParTiempo_res);
				System.out.println("GRAFICANDO3:convertir a StringMatrix");
				ResultSet paraderos_res = dbClient.getParaderos(servicio, sentido); //Orden,codigo_paradero_usuario
				System.out.println("GRAFICANDO4:getParaderos terminó");
				String[][] paraderos_str = dbClient.res2StringMatrix(paraderos_res);
				System.out.println("GRAFICANDO5:convertir a StringMatrix");
				
				//Paraderos como etiquetas de categoria
				System.out.println("Paraderos como etiquetas de categoria");
				String[] paraderos_categorias = new String[paraderos_str.length];
				for(int i=0; i<paraderos_categorias.length; i++){
					paraderos_categorias[i] = paraderos_str[i][1];
					System.out.println(paraderos_categorias[i]);
				}
				
				XYDataset dataset = DateCategoryScatterChart.createDataset(valParTiempo_str, paraderos_str, "Serie1", "yyyy-MM-dd HH:mm:ss");
				
				DateCategoryScatterChart.buildChart(dataset, "HH:mm:ss", paraderos_categorias);
			}
		});


	}

	private void Ranking2ShowTable(){
		System.out.println("################################");
		String rut = this.getSelectedRut();
		ResultSet res = dbClient.getExpedicionesByRut(rut, fechaIni, fechaFin);
		//ResultSet res = dbClient.getRankingMAB(fechaIni, fechaFin);
		
		String[][] strMatrix = dbClient.res2StringMatrix(res);
		if (strMatrix != null){
			System.out.println("CONTROL 4");
			for(int i=0; i<strMatrix.length; i++){
				for(int j=0; j<strMatrix[i].length; j++){
					System.out.print(strMatrix[i][j] + ", ");
				}
				System.out.println("");
			}
			
			//TODO Agregar head correcto
			/*
			String[] header = new String[] {"Fecha", " Servicio", " Sentido", 
					" Inicio de expedición", " Número de validaciones", " Clasificación de validaciones", 
					" Promedio M para validaciones", " Promedio A para validaciones", " Promedio B para validaciones", 
					" Distancia al tiempo promedio", " Clasificación distancia tiempo promedio", 
					" Promedio M para distancia tiempo promedio", "Promedio A para distancia tiempo promedio", 
					"Promedio B para distancia tiempo promedio"};
			*/
			String[] header = new String[] {"Fecha", " Servicio", " Sentido", 
					" InicioExp", " V(E)", " Rv(E)", 
					" Promedio Mv", " Promedio Av", " Promedio Bv", 
					" D(E)", " RD(E)", 
					" Promedio MD", "Promedio AD", 
					"Promedio BD"};
			
			//Remover la tabla anterior, si es que hay una
			boolean flag1 = true;
			if (tableRanking2 != null){
				//TODO Fix this table that doesn't want to be removed and repainted
				panelRanking2.remove(jscrollPane);
				panelRanking2.revalidate();
				panelRanking2.repaint();
				System.out.println("jscrollPane removed");
				flag1 = false;
				flag1 = true;
				//frame.repaint();
			}
			if (flag1){
				if(tableRanking2 != null){
					System.out.println(tableRanking2.toString());	
				}
				tableRanking2 = new JTable(strMatrix,header){
					public boolean getScrollableTracksViewportWidth() {
						return getPreferredSize().width < getParent().getWidth();
						//Esto hace que se vea bonito y ocupe todo el espacio que debe ocupar
					}

					//Make cells non-editable
					@Override
					public boolean isCellEditable(int row, int column) {
						//all cells false
						return false;
					}
				};
				System.out.println(tableRanking2.toString());
				GridBagConstraints gbc_tableRanking2ScrollPane = new GridBagConstraints();
				gbc_tableRanking2ScrollPane.insets = new Insets(0, 0, 0, 5);
				gbc_tableRanking2ScrollPane.anchor = GridBagConstraints.CENTER;
				gbc_tableRanking2ScrollPane.gridx = 0;
				gbc_tableRanking2ScrollPane.gridy = 2;
				gbc_tableRanking2ScrollPane.gridwidth = 12;
				gbc_tableRanking2ScrollPane.fill = GridBagConstraints.BOTH;
				jscrollPane = new JScrollPane(tableRanking2);
				panelRanking2.add(jscrollPane, gbc_tableRanking2ScrollPane);
				frame.repaint();

				final ListSelectionModel selectionModelRanking = tableRanking2.getSelectionModel();
				selectionModelRanking.addListSelectionListener(new ListSelectionListener() {

					@Override
					public void valueChanged(ListSelectionEvent e) {
						if(! selectionModelRanking.isSelectionEmpty()) {
							//Code here what happend when a row is selected in tableRanking2
							//get selected row
							selectedRow2 = selectionModelRanking.getMinSelectionIndex();
							JOptionPane.showMessageDialog(null, "Selected row:" + selectedRow2);
						}
					}
				});

			}
		}
		else{//Problema?, no hay resultados que mostrar
			System.out.println("WHY?!, strMatrix is null" );
		}
		//JOptionPane.showMessageDialog(null, fechaIni + "\n" + fechaFin);

	}

	private void showRanking2(){
		this.lblRut.setText(this.getSelectedRut());
		this.lblFechaIni.setText(fechaIni);
		this.lblFechaFin.setText(fechaFin);
		Ranking2ShowTable();
		this.panelRanking2.setVisible(true);
	}
}
