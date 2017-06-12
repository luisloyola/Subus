import groovy.model.DefaultTableModel;
import groovy.model.ValueModel;

import java.awt.EventQueue;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import java.awt.BorderLayout;

import javax.swing.JLabel;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;

import org.jdatepicker.JDateComponentFactory;
import org.jdatepicker.JDatePicker;

import antlr.collections.impl.Vector;

public class Ventana {
	HiveJdbcClient dbClient;
	private JFrame frame;
	private JTable tableRanking;
	private int selectedRow;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					HiveJdbcClient dbClient = new HiveJdbcClient("subus8");
					Ventana window = new Ventana(dbClient);
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Ventana(HiveJdbcClient dbClient) {
		this.dbClient = dbClient;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		System.out.println("Initializing GUI...");
		int gridwidth = 5;
		frame = new JFrame();
		frame.setBounds(100, 100, 1500, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		frame.getContentPane().add(tabbedPane);

		
		//TESTING NEW DATE-CATEGORY-SCATTER
		//DateCategoryScatterChart.testing();
		
		// PANEL RANKING
		RankingPanel panelRanking = new RankingPanel(dbClient, frame);
		tabbedPane.addTab("Ranking", null, panelRanking, null);

		//Panel ProcesarDatos
		JPanel panelProcesarDatos = new JPanel();
		tabbedPane.addTab("Procesar Datos", null, panelProcesarDatos, null);

		// Generar Métricas de Expediciones
				JButton btnResumenExp = new JButton("Generar métricas de expediciones");
				GridBagConstraints gbc_btnResumenExp = new GridBagConstraints();
				gbc_btnResumenExp.insets = new Insets(0, 0, 5, 0);
				gbc_btnResumenExp.gridx = 1;
				gbc_btnResumenExp.gridy = 10;
				gbc_btnResumenExp.gridwidth = gridwidth;
				panelProcesarDatos.add(btnResumenExp, gbc_btnResumenExp);
				btnResumenExp.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						// Toma servicio del JComboBox
						// String servicio =
						// String.valueOf(comboBoxServicio.getSelectedItem());

						// dbClient.insertResExp();
						dbClient.createTablaResExp();
						// TODO mensaje solo para testing
						JOptionPane.showMessageDialog(frame,
								"Hive: su consulta a finalizado");
					}
				});
		
		// Graficar panel & layout
		JPanel panelGraficar = new JPanel();
		tabbedPane.add(panelGraficar, "Graficar");

		GridBagLayout gridBagLayout_Graficar = new GridBagLayout();
		gridBagLayout_Graficar.columnWidths = new int[] { 0, 0, 0 };
		gridBagLayout_Graficar.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout_Graficar.columnWeights = new double[] { 0.0, 1.0,
				Double.MIN_VALUE };
		gridBagLayout_Graficar.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0, 0.0, Double.MIN_VALUE };
		panelGraficar.setLayout(gridBagLayout_Graficar);

		JLabel filler1 = new JLabel("      ");
		GridBagConstraints gbc_filler1 = new GridBagConstraints();
		gbc_filler1.anchor = GridBagConstraints.EAST;
		gbc_filler1.insets = new Insets(0, 0, 5, 5);
		gbc_filler1.gridx = 3;
		gbc_filler1.gridy = 0;
		panelGraficar.add(filler1, gbc_filler1);

		JLabel lblServicio = new JLabel("Servicio");
		GridBagConstraints gbc_lblServicio = new GridBagConstraints();
		gbc_lblServicio.anchor = GridBagConstraints.EAST;
		gbc_lblServicio.insets = new Insets(0, 0, 5, 5);
		gbc_lblServicio.gridx = 0;
		gbc_lblServicio.gridy = 2;
		panelGraficar.add(lblServicio, gbc_lblServicio);

		final JComboBox comboBoxServicio = new JComboBox();
		this.addItems(comboBoxServicio, dbClient.getServicios());
		GridBagConstraints gbc_comboBoxServicio = new GridBagConstraints();
		gbc_comboBoxServicio.insets = new Insets(0, 0, 5, 0);
		gbc_comboBoxServicio.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBoxServicio.gridx = 1;
		gbc_comboBoxServicio.gridy = 2;
		gbc_comboBoxServicio.gridwidth = gridwidth;
		panelGraficar.add(comboBoxServicio, gbc_comboBoxServicio);

		JLabel lblSentido = new JLabel("Sentido");
		GridBagConstraints gbc_lblSentido = new GridBagConstraints();
		gbc_lblSentido.anchor = GridBagConstraints.EAST;
		gbc_lblSentido.insets = new Insets(0, 0, 5, 5);
		gbc_lblSentido.gridx = 0;
		gbc_lblSentido.gridy = 3;
		panelGraficar.add(lblSentido, gbc_lblSentido);

		final JComboBox comboBoxSentido = new JComboBox();
		comboBoxSentido.addItem("I");
		comboBoxSentido.addItem("R");
		GridBagConstraints gbc_comboBoxSentido = new GridBagConstraints();
		gbc_comboBoxSentido.insets = new Insets(0, 0, 5, 0);
		gbc_comboBoxSentido.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBoxSentido.gridx = 1;
		gbc_comboBoxSentido.gridy = 3;
		gbc_comboBoxSentido.gridwidth = gridwidth;
		panelGraficar.add(comboBoxSentido, gbc_comboBoxSentido);

		JLabel lblFranjahoraria = new JLabel("FranjaHoraria");
		GridBagConstraints gbc_lblFranjahoraria = new GridBagConstraints();
		gbc_lblFranjahoraria.anchor = GridBagConstraints.EAST;
		gbc_lblFranjahoraria.insets = new Insets(0, 0, 0, 5);
		gbc_lblFranjahoraria.gridx = 0;
		gbc_lblFranjahoraria.gridy = 4;
		panelGraficar.add(lblFranjahoraria, gbc_lblFranjahoraria);

		final JComboBox comboBoxFranjahoraria = new JComboBox();
		this.addItems(comboBoxFranjahoraria, dbClient.getFranjasHorariasID());
		GridBagConstraints gbc_comboBoxFranjaHoraria = new GridBagConstraints();
		gbc_comboBoxFranjaHoraria.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBoxFranjaHoraria.gridx = 1;
		gbc_comboBoxFranjaHoraria.gridy = 4;
		gbc_comboBoxFranjaHoraria.gridwidth = gridwidth;
		panelGraficar.add(comboBoxFranjahoraria, gbc_comboBoxFranjaHoraria);

		JLabel lblMes = new JLabel("Mes");
		GridBagConstraints gbc_lblMes = new GridBagConstraints();
		gbc_lblMes.anchor = GridBagConstraints.EAST;
		gbc_lblMes.insets = new Insets(0, 0, 5, 5);
		gbc_lblMes.gridx = 0;
		gbc_lblMes.gridy = 5;
		panelGraficar.add(lblMes, gbc_lblMes);

		final JComboBox comboBoxMes = new JComboBox();
		comboBoxMes.addItem("01 ENE");
		comboBoxMes.addItem("02 FEB");
		comboBoxMes.addItem("03 MAR");
		comboBoxMes.addItem("04 ABR");
		comboBoxMes.addItem("05 MAY");
		comboBoxMes.addItem("06 JUN");
		comboBoxMes.addItem("07 JUL");
		comboBoxMes.addItem("08 AGO");
		comboBoxMes.addItem("09 SEP");
		comboBoxMes.addItem("10 OCT");
		comboBoxMes.addItem("11 NOV");
		comboBoxMes.addItem("12 DIC");
		comboBoxMes.setSelectedIndex(7); // TODO just for testing
		comboBoxMes.setEnabled(true);
		GridBagConstraints gbc_comboBoxMes = new GridBagConstraints();
		gbc_comboBoxMes.insets = new Insets(0, 0, 5, 0);
		gbc_comboBoxMes.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBoxMes.gridx = 1;
		gbc_comboBoxMes.gridy = 5;
		gbc_comboBoxMes.gridwidth = gridwidth;
		panelGraficar.add(comboBoxMes, gbc_comboBoxMes);

		JLabel lblIndicador = new JLabel("Indicador");
		GridBagConstraints gbc_lblIndicador = new GridBagConstraints();
		gbc_lblIndicador.anchor = GridBagConstraints.EAST;
		gbc_lblIndicador.insets = new Insets(0, 0, 0, 5);
		gbc_lblIndicador.gridx = 0;
		gbc_lblIndicador.gridy = 6;
		panelGraficar.add(lblIndicador, gbc_lblIndicador);

		final JComboBox comboBoxIndicador = new JComboBox();
		comboBoxIndicador.addItem(dbClient.ResExpParam1);
		comboBoxIndicador.addItem(dbClient.ResExpParam2);
		comboBoxIndicador.addItem(dbClient.ResExpParam3);
		comboBoxIndicador.addItem(dbClient.ResExpParam4);
		comboBoxIndicador.addItem(dbClient.ResExpParam5);
		GridBagConstraints gbc_comboBoxIndicador = new GridBagConstraints();
		gbc_comboBoxIndicador.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBoxIndicador.gridx = 1;
		gbc_comboBoxIndicador.gridy = 6;
		gbc_comboBoxIndicador.gridwidth = gridwidth;
		panelGraficar.add(comboBoxIndicador, gbc_comboBoxIndicador);

		JButton btnGraficar = new JButton("Graficar");
		// btnGraficar.setEnabled(false);
		GridBagConstraints gbc_btnGraficar = new GridBagConstraints();
		gbc_btnGraficar.insets = new Insets(0, 0, 5, 0);
		gbc_btnGraficar.gridx = 1;
		gbc_btnGraficar.gridy = 9;
		gbc_btnGraficar.gridwidth = gridwidth;
		panelGraficar.add(btnGraficar, gbc_btnGraficar);
		btnGraficar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// Toma los valores de los JComboBox
				String servicio = String.valueOf(comboBoxServicio
						.getSelectedItem());
				String sentido = String.valueOf(comboBoxSentido
						.getSelectedItem());
				int franjahoraria = Integer.valueOf(String
						.valueOf(comboBoxFranjahoraria.getSelectedItem()));

				String indicadorUser = String.valueOf(comboBoxIndicador
						.getSelectedItem());
				String indicador = dbClient.dicctionaryGet(indicadorUser);

				// TODO cambiar "String min" y "String max" (que por ahora estan
				// por defectos) para que obtengan sus valores a partir de los
				// ya definidos en hive utilizando el panel de parametros.
				String min = "0.15";
				String max = "0.80";
				Double minPercent = 0.15;
				Double maxPercent = 0.8;
				// try double convertion
				try {
					minPercent = Double.valueOf(min);
					maxPercent = Double.valueOf(max);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(frame,
							"Debe ingresar un decimal entre 0 y 1.0");
				}

				// TODO Falta implementar mes
				int mes = comboBoxMes.getSelectedIndex()+1;

				// Ejecuta la query e imprime un gráfico.
				ResultSet res = dbClient.getResExp(indicador, servicio,
						sentido, franjahoraria, mes);
				double[] values = dbClient.res2Double(res);

				if (values == null) {
					// System.out.println("There is no data to show");
					// TODO Agregar mes en el mensaje
					JOptionPane.showMessageDialog(frame,
							"No hay valores que mostrar para '" + indicadorUser
							+ "' con:\n" + "servicio: " + servicio
							+ "\n" + "sentido: " + sentido + "\n"
							+ "franjahoraria: " + franjahoraria + "\n"
							+ "en mes: " + comboBoxMes.getSelectedItem()
							);
				} else {
					String plotTitle = "Histrograma de " + indicadorUser + "\n Servicio: " + servicio + " " + sentido + ", FH: " + franjahoraria + " en mes: " + comboBoxMes.getSelectedItem();
					String xAxisLabel = indicadorUser;
					String yAxisLabel = "Cantidad de expediciones";
					String legend = "" + indicadorUser + " para"
							+ " servicio: " + servicio + " sentido: " + sentido
							+ "\n" + " franjahoraria: " + franjahoraria
							+ "\n en mes: " + comboBoxMes.getSelectedItem();

					HistogramChart.buildHistogram(plotTitle, xAxisLabel,
							yAxisLabel, values, legend,
							HistogramChart.FREQUENCY, minPercent, maxPercent);
				}
			}
		});

		// Generar Resumen de Expediciones
		/*JButton btnResumenExp = new JButton("Generar métricas de expediciones");
		GridBagConstraints gbc_btnResumenExp = new GridBagConstraints();
		gbc_btnResumenExp.insets = new Insets(0, 0, 5, 0);
		gbc_btnResumenExp.gridx = 1;
		gbc_btnResumenExp.gridy = 10;
		gbc_btnResumenExp.gridwidth = gridwidth;
		panelGraficar.add(btnResumenExp, gbc_btnResumenExp);
		btnResumenExp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// Toma servicio del JComboBox
				// String servicio =
				// String.valueOf(comboBoxServicio.getSelectedItem());

				// dbClient.insertResExp();
				dbClient.createTablaResExp();
				// TODO mensaje solo para testing
				JOptionPane.showMessageDialog(frame,
						"Hive: su consulta a finalizado");
			}
		});
		*/
		
		JButton btnCrearDB = new JButton("Crear DB");
		btnCrearDB.setEnabled(false);
		GridBagConstraints gbc_btnCrearDB = new GridBagConstraints();
		gbc_btnCrearDB.insets = new Insets(0, 0, 5, 0);
		gbc_btnCrearDB.gridx = 1;
		gbc_btnCrearDB.gridy = 11;
		gbc_btnCrearDB.gridwidth = gridwidth;
		panelGraficar.add(btnCrearDB, gbc_btnCrearDB);
		btnCrearDB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("btnCrearDB pressed");
			}
		});

		// *****************************************
		// *** Panel de Parámetros ***
		// *****************************************

		// Parámetros panel & layout
		JPanel panelParametros = new JPanel();
		tabbedPane.add(panelParametros, "Parámetros");

		GridBagLayout gridBagLayout_Parametros = new GridBagLayout();
		gridBagLayout_Parametros.columnWidths = new int[] { 0, 0, 0 };
		gridBagLayout_Parametros.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0,
				0 };
		gridBagLayout_Parametros.columnWeights = new double[] { 0.0, 1.0,
				Double.MIN_VALUE };
		gridBagLayout_Parametros.rowWeights = new double[] { 0.0, 0.0, 0.0,
				0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panelParametros.setLayout(gridBagLayout_Parametros);

		/* Quantiles */
		// NroVal
		JLabel lblQuantilNroValMin = new JLabel("V(e) Min:");
		GridBagConstraints gbc_lblQuantilNroValMin = new GridBagConstraints();
		gbc_lblQuantilNroValMin.anchor = GridBagConstraints.EAST;
		gbc_lblQuantilNroValMin.insets = new Insets(0, 0, 5, 5);
		gbc_lblQuantilNroValMin.gridx = 0;
		gbc_lblQuantilNroValMin.gridy = 0;
		panelParametros.add(lblQuantilNroValMin, gbc_lblQuantilNroValMin);

		final JTextField txtFldNroValMin = new JTextField();
		txtFldNroValMin.setText(String.valueOf(0.15));
		txtFldNroValMin.setPreferredSize(new Dimension(40, 20));
		GridBagConstraints gbc_txtFldNroValMin = new GridBagConstraints();
		gbc_txtFldNroValMin.anchor = GridBagConstraints.WEST;
		gbc_txtFldNroValMin.insets = new Insets(0, 0, 5, 5);
		gbc_txtFldNroValMin.gridx = 1;
		gbc_txtFldNroValMin.gridy = 0;
		panelParametros.add(txtFldNroValMin, gbc_txtFldNroValMin);

		JLabel lblQuantilNroValMax = new JLabel("V(e) Max:");
		GridBagConstraints gbc_lblQuantilNroValMax = new GridBagConstraints();
		gbc_lblQuantilNroValMax.anchor = GridBagConstraints.EAST;
		gbc_lblQuantilNroValMax.insets = new Insets(0, 0, 5, 5);
		gbc_lblQuantilNroValMax.gridx = 0;
		gbc_lblQuantilNroValMax.gridy = 1;
		panelParametros.add(lblQuantilNroValMax, gbc_lblQuantilNroValMax);

		final JTextField txtFldNroValMax = new JTextField();
		txtFldNroValMax.setText(String.valueOf(0.80));
		txtFldNroValMax.setPreferredSize(new Dimension(40, 20));
		GridBagConstraints gbc_txtFldNroValMax = new GridBagConstraints();
		gbc_txtFldNroValMax.anchor = GridBagConstraints.WEST;
		gbc_txtFldNroValMax.insets = new Insets(0, 0, 5, 5);
		gbc_txtFldNroValMax.gridx = 1;
		gbc_txtFldNroValMax.gridy = 1;
		panelParametros.add(txtFldNroValMax, gbc_txtFldNroValMax);

		// NroValPos
		JLabel lblQuantilNroValPosMin = new JLabel("Vpos(e) Min:");
		GridBagConstraints gbc_lblQuantilNroValPosMin = new GridBagConstraints();
		gbc_lblQuantilNroValPosMin.anchor = GridBagConstraints.EAST;
		gbc_lblQuantilNroValPosMin.insets = new Insets(0, 0, 5, 5);
		gbc_lblQuantilNroValPosMin.gridx = 2;
		gbc_lblQuantilNroValPosMin.gridy = 0;
		panelParametros.add(lblQuantilNroValPosMin, gbc_lblQuantilNroValPosMin);

		final JTextField txtFldNroValPosMin = new JTextField();
		txtFldNroValPosMin.setText(String.valueOf(0.15));
		txtFldNroValPosMin.setPreferredSize(new Dimension(40, 20));
		GridBagConstraints gbc_txtFldNroValPosMin = new GridBagConstraints();
		gbc_txtFldNroValPosMin.anchor = GridBagConstraints.WEST;
		gbc_txtFldNroValPosMin.insets = new Insets(0, 0, 5, 5);
		gbc_txtFldNroValPosMin.gridx = 3;
		gbc_txtFldNroValPosMin.gridy = 0;
		panelParametros.add(txtFldNroValPosMin, gbc_txtFldNroValPosMin);

		JLabel lblQuantilNroValPosMax = new JLabel("Vpos(e) Max:");
		GridBagConstraints gbc_lblQuantilNroValPosMax = new GridBagConstraints();
		gbc_lblQuantilNroValPosMax.anchor = GridBagConstraints.EAST;
		gbc_lblQuantilNroValPosMax.insets = new Insets(0, 0, 5, 5);
		gbc_lblQuantilNroValPosMax.gridx = 2;
		gbc_lblQuantilNroValPosMax.gridy = 1;
		panelParametros.add(lblQuantilNroValPosMax, gbc_lblQuantilNroValPosMax);

		final JTextField txtFldNroValPosMax = new JTextField();
		txtFldNroValPosMax.setText(String.valueOf(0.80));
		txtFldNroValPosMax.setPreferredSize(new Dimension(40, 20));
		GridBagConstraints gbc_txtFldNroValPosMax = new GridBagConstraints();
		gbc_txtFldNroValPosMax.anchor = GridBagConstraints.WEST;
		gbc_txtFldNroValPosMax.insets = new Insets(0, 0, 5, 5);
		gbc_txtFldNroValPosMax.gridx = 3;
		gbc_txtFldNroValPosMax.gridy = 1;
		panelParametros.add(txtFldNroValPosMax, gbc_txtFldNroValPosMax);

		// ValPond
		JLabel lblQuantilValPondMin = new JLabel("Vpond(e) Min:");
		GridBagConstraints gbc_lblQuantilValPondMin = new GridBagConstraints();
		gbc_lblQuantilValPondMin.anchor = GridBagConstraints.EAST;
		gbc_lblQuantilValPondMin.insets = new Insets(0, 0, 5, 5);
		gbc_lblQuantilValPondMin.gridx = 4;
		gbc_lblQuantilValPondMin.gridy = 0;
		panelParametros.add(lblQuantilValPondMin, gbc_lblQuantilValPondMin);

		final JTextField txtFldValPondMin = new JTextField();
		txtFldValPondMin.setText(String.valueOf(0.15));
		txtFldValPondMin.setPreferredSize(new Dimension(40, 20));
		GridBagConstraints gbc_txtFldValPondMin = new GridBagConstraints();
		gbc_txtFldValPondMin.anchor = GridBagConstraints.WEST;
		gbc_txtFldValPondMin.insets = new Insets(0, 0, 5, 5);
		gbc_txtFldValPondMin.gridx = 5;
		gbc_txtFldValPondMin.gridy = 0;
		panelParametros.add(txtFldValPondMin, gbc_txtFldValPondMin);

		JLabel lblQuantilValPondMax = new JLabel("Vpond(e) Max:");
		GridBagConstraints gbc_lblQuantilValPondMax = new GridBagConstraints();
		gbc_lblQuantilValPondMax.anchor = GridBagConstraints.EAST;
		gbc_lblQuantilValPondMax.insets = new Insets(0, 0, 5, 5);
		gbc_lblQuantilValPondMax.gridx = 4;
		gbc_lblQuantilValPondMax.gridy = 1;
		panelParametros.add(lblQuantilValPondMax, gbc_lblQuantilValPondMax);

		final JTextField txtFldValPondMax = new JTextField();
		txtFldValPondMax.setText(String.valueOf(0.80));
		txtFldValPondMax.setPreferredSize(new Dimension(40, 20));
		GridBagConstraints gbc_txtFldValPondMax = new GridBagConstraints();
		gbc_txtFldValPondMax.anchor = GridBagConstraints.WEST;
		gbc_txtFldValPondMax.insets = new Insets(0, 0, 5, 5);
		gbc_txtFldValPondMax.gridx = 5;
		gbc_txtFldValPondMax.gridy = 1;
		panelParametros.add(txtFldValPondMax, gbc_txtFldValPondMax);

		// NroPar
		JLabel lblQuantilNroParMin = new JLabel("P(e) Min:");
		GridBagConstraints gbc_lblQuantilNroParMin = new GridBagConstraints();
		gbc_lblQuantilNroParMin.anchor = GridBagConstraints.EAST;
		gbc_lblQuantilNroParMin.insets = new Insets(0, 0, 5, 5);
		gbc_lblQuantilNroParMin.gridx = 6;
		gbc_lblQuantilNroParMin.gridy = 0;
		panelParametros.add(lblQuantilNroParMin, gbc_lblQuantilNroParMin);

		final JTextField txtFldNroParMin = new JTextField();
		txtFldNroParMin.setText(String.valueOf(0.15));
		txtFldNroParMin.setPreferredSize(new Dimension(40, 20));
		GridBagConstraints gbc_txtFldNroParMin = new GridBagConstraints();
		gbc_txtFldNroParMin.anchor = GridBagConstraints.WEST;
		gbc_txtFldNroParMin.insets = new Insets(0, 0, 5, 5);
		gbc_txtFldNroParMin.gridx = 7;
		gbc_txtFldNroParMin.gridy = 0;
		panelParametros.add(txtFldNroParMin, gbc_txtFldNroParMin);

		JLabel lblQuantilNroParMax = new JLabel("P(e) Max:");
		GridBagConstraints gbc_lblQuantilNroParMax = new GridBagConstraints();
		gbc_lblQuantilNroParMax.anchor = GridBagConstraints.EAST;
		gbc_lblQuantilNroParMax.insets = new Insets(0, 0, 5, 5);
		gbc_lblQuantilNroParMax.gridx = 6;
		gbc_lblQuantilNroParMax.gridy = 1;
		panelParametros.add(lblQuantilNroParMax, gbc_lblQuantilNroParMax);

		final JTextField txtFldNroParMax = new JTextField();
		txtFldNroParMax.setText(String.valueOf(0.80));
		txtFldNroParMax.setPreferredSize(new Dimension(40, 20));
		GridBagConstraints gbc_txtFldNroParMax = new GridBagConstraints();
		gbc_txtFldNroParMax.anchor = GridBagConstraints.WEST;
		gbc_txtFldNroParMax.insets = new Insets(0, 0, 5, 5);
		gbc_txtFldNroParMax.gridx = 7;
		gbc_txtFldNroParMax.gridy = 1;
		panelParametros.add(txtFldNroParMax, gbc_txtFldNroParMax);

		// DistTiempoProm
		JLabel lblQuantilDistTiempoPromedioMin = new JLabel(
				"D(e) Min:");
		GridBagConstraints gbc_lblQuantilDistTiempoPromedioMin = new GridBagConstraints();
		gbc_lblQuantilDistTiempoPromedioMin.anchor = GridBagConstraints.EAST;
		gbc_lblQuantilDistTiempoPromedioMin.insets = new Insets(0, 0, 5, 5);
		gbc_lblQuantilDistTiempoPromedioMin.gridx = 8;
		gbc_lblQuantilDistTiempoPromedioMin.gridy = 0;
		panelParametros.add(lblQuantilDistTiempoPromedioMin,
				gbc_lblQuantilDistTiempoPromedioMin);

		final JTextField txtFldDistTiempoPromedioMin = new JTextField();
		txtFldDistTiempoPromedioMin.setText(String.valueOf(0.15));
		txtFldDistTiempoPromedioMin.setPreferredSize(new Dimension(40, 20));
		GridBagConstraints gbc_txtFldDistTiempoPromedioMin = new GridBagConstraints();
		gbc_txtFldDistTiempoPromedioMin.anchor = GridBagConstraints.WEST;
		gbc_txtFldDistTiempoPromedioMin.insets = new Insets(0, 0, 5, 5);
		gbc_txtFldDistTiempoPromedioMin.gridx = 9;
		gbc_txtFldDistTiempoPromedioMin.gridy = 0;
		panelParametros.add(txtFldDistTiempoPromedioMin,
				gbc_txtFldDistTiempoPromedioMin);

		JLabel lblQuantilDistTiempoPromedioMax = new JLabel(
				"D(e) Max:");
		GridBagConstraints gbc_lblQuantilDistTiempoPromedioMax = new GridBagConstraints();
		gbc_lblQuantilDistTiempoPromedioMax.anchor = GridBagConstraints.EAST;
		gbc_lblQuantilDistTiempoPromedioMax.insets = new Insets(0, 0, 5, 5);
		gbc_lblQuantilDistTiempoPromedioMax.gridx = 8;
		gbc_lblQuantilDistTiempoPromedioMax.gridy = 1;
		panelParametros.add(lblQuantilDistTiempoPromedioMax,
				gbc_lblQuantilDistTiempoPromedioMax);

		final JTextField txtFldDistTiempoPromedioMax = new JTextField();
		txtFldDistTiempoPromedioMax.setText(String.valueOf(0.80));
		txtFldDistTiempoPromedioMax.setPreferredSize(new Dimension(40, 20));
		GridBagConstraints gbc_txtFldDistTiempoPromedioMax = new GridBagConstraints();
		gbc_txtFldDistTiempoPromedioMax.anchor = GridBagConstraints.WEST;
		gbc_txtFldDistTiempoPromedioMax.insets = new Insets(0, 0, 5, 5);
		gbc_txtFldDistTiempoPromedioMax.gridx = 9;
		gbc_txtFldDistTiempoPromedioMax.gridy = 1;
		panelParametros.add(txtFldDistTiempoPromedioMax,
				gbc_txtFldDistTiempoPromedioMax);

		// Generar puntos de corte
		JButton btnPuntosCorte = new JButton("Calcular puntos de corte");
		GridBagConstraints gbc_btnPuntosCorte = new GridBagConstraints();
		gbc_btnPuntosCorte.insets = new Insets(0, 0, 5, 0);
		gbc_btnPuntosCorte.gridx = 1;
		gbc_btnPuntosCorte.gridy = 3;
		gbc_btnPuntosCorte.gridwidth = gridwidth;
		panelParametros.add(btnPuntosCorte, gbc_btnPuntosCorte);
		btnPuntosCorte.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				// Get minPercent y maxPercent strings
				String nroValMin = txtFldNroValMin.getText();
				String nroValMax = txtFldNroValMax.getText();
				String nroValPosMin = txtFldNroValPosMin.getText();
				String nroValPosMax = txtFldNroValPosMax.getText();
				String valPondMin = txtFldValPondMin.getText();
				String valPondMax = txtFldValPondMax.getText();
				String nroParMin = txtFldNroParMin.getText();
				String nroParMax = txtFldNroParMax.getText();
				String distTiempoPromedioMin = txtFldDistTiempoPromedioMin
						.getText();
				String distTiempoPromedioMax = txtFldDistTiempoPromedioMax
						.getText();

				// Set default values
				Double nroValMinPercent = 0.15;
				Double nroValMaxPercent = 0.8;
				Double nroValPosMinPercent = 0.15;
				Double nroValPosMaxPercent = 0.8;
				Double valPondMinPercent = 0.15;
				Double valPondMaxPercent = 0.8;
				Double nroParMinPercent = 0.15;
				Double nroParMaxPercent = 0.8;
				Double distTiempoPromedioMinPercent = 0.15;
				Double distTiempoPromedioMaxPercent = 0.8;

				// try double convertion
				boolean flagDoubleConvertion = true;
				try {
					nroValMinPercent = Double.valueOf(nroValMin);
					nroValMaxPercent = Double.valueOf(nroValMax);
					nroValPosMinPercent = Double.valueOf(nroValPosMin);
					nroValPosMaxPercent = Double.valueOf(nroValPosMax);
					valPondMinPercent = Double.valueOf(valPondMin);
					valPondMaxPercent = Double.valueOf(valPondMax);
					nroParMinPercent = Double.valueOf(nroParMin);
					nroParMaxPercent = Double.valueOf(nroParMax);
					distTiempoPromedioMinPercent = Double
							.valueOf(distTiempoPromedioMin);
					distTiempoPromedioMaxPercent = Double
							.valueOf(distTiempoPromedioMax);

					// Comprobar que esten entre 0 y 1
					if ((nroValMinPercent < 0 || nroValMinPercent > 1)
							|| (nroValMaxPercent < 0 || nroValMaxPercent > 1)
							|| (nroValPosMinPercent < 0 || nroValPosMinPercent > 1)
							|| (nroValPosMaxPercent < 0 || nroValPosMaxPercent > 1)
							|| (valPondMinPercent < 0 || valPondMinPercent > 1)
							|| (valPondMaxPercent < 0 || valPondMaxPercent > 1)
							|| (nroParMinPercent < 0 || nroParMinPercent > 1)
							|| (nroParMaxPercent < 0 || nroParMaxPercent > 1)
							|| (distTiempoPromedioMinPercent < 0 || distTiempoPromedioMinPercent > 1)
							|| (distTiempoPromedioMaxPercent < 0 || distTiempoPromedioMaxPercent > 1)) {
						flagDoubleConvertion = false;
					}

				} catch (Exception e) {
					flagDoubleConvertion = false;
				} finally {
					if (!flagDoubleConvertion) {
						JOptionPane.showMessageDialog(frame,
								"Debe ingresar un decimal entre 0 y 1.0");
					}
				}

				// Double convertion was OK.
				if (flagDoubleConvertion) {
					// Crear tabla de puntos de corte para "todos los servicios"
					// (solo 216 durante testing), todos los sentidos, en todas
					// las franjas horarias para todos indicadores.
					dbClient.createTablaPuntosDeCorte(nroValMinPercent,
							nroValMaxPercent, nroValPosMinPercent,
							nroValMaxPercent, valPondMinPercent,
							valPondMaxPercent, nroParMinPercent,
							nroParMaxPercent, distTiempoPromedioMinPercent,
							distTiempoPromedioMaxPercent);
					JOptionPane.showMessageDialog(frame,
							"Tabla de puntos de corte poblada");
				}
			}
		});

	}

	private static void addItems(JComboBox box, ResultSet res) {
		try {
			while (res.next()) {
				box.addItem(res.getString(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
