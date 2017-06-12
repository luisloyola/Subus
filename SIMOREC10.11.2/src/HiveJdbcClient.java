import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.statistics.*;

public class HiveJdbcClient {
	private String driverName = "org.apache.hive.jdbc.HiveDriver";
	private  String DBName;
	
	public static final int ENE = 1;
	public static final int FEB = 2;
	public static final int MAR = 3;
	public static final int ABR = 4;
	public static final int MAY = 5;
	public static final int JUN = 6;
	public static final int JUL = 7;
	public static final int AGO = 8;
	public static final int SEP = 9;
	public static final int OCT = 10;
	public static final int NOV = 11;
	public static final int DIC = 12;
	
	public static final String ResExpParam1 = "Número de validaciones";
	public static final String ResExpParam2 = "Número de validaciones posicionadas";
	public static final String ResExpParam3 = "Validaciones ponderadas";
	public static final String ResExpParam4 = "Número de paraderos con validaciones";
	public static final String ResExpParam5 = "Duración de la expedición (en minutos)";	

	private Map<String,String> columnDicctionary = new HashMap<String,String>();
		
	private Connection con;
	private Statement stmt;
	
       
	/*
	 * Constructor
	 */
	public HiveJdbcClient(String DBName) {
		this.DBName = DBName;
		initColumnMap();
		try {
			Class.forName(driverName);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
		this.createConnection();
		this.useDB();
	}
	
	private void initColumnMap(){
		columnDicctionary.put(ResExpParam1, "nro_validaciones_exp");
		columnDicctionary.put(ResExpParam2, "nro_validaciones_val");
		columnDicctionary.put(ResExpParam3, "validaciones_ponderadas");
		columnDicctionary.put(ResExpParam4, "nro_par_detenido");
		columnDicctionary.put(ResExpParam5, "duracion_exp");
	}
	
	public String dicctionaryGet(String key){
		return columnDicctionary.get(key);
	}
		
	//Create connection, statement, sql and resultSet
	//replace "hive" here with the name of the user the queries should run as
	private void createConnection(){
		try {
			//con = DriverManager.getConnection("jdbc:hive2://158.170.35.9:10000/subus7", "hive", ""); //Cluster
			con = DriverManager.getConnection("jdbc:hive2://localhost:10000/default", "hive", ""); //Local
			//con = DriverManager.getConnection("beeline -u jdbc:hive2://[Manager Node IP Address]:10000/default -n admin -d org.apache.hive.jdbc.HiveDriver");
			stmt = con.createStatement();
			System.out.println("Hive connection: [OK]");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("Hive connection : [FAIL]");
		}
	}
	
	private void useDB(){
		// Use DBName
		if (!DBName.equals("")){
			try{
				ResultSet res = stmt.executeQuery("use " + DBName);		
                //System.out.println(res.toString());
			}
			catch (SQLException e) {
				this.SQLExceptionManager(e);
			}
		}
	}
	
	//TODO Test this
	public boolean dbExist(String DB){
		try {
			ResultSet res = stmt.executeQuery("show databases");
			while(res.next()){
				if (res.getString(1).compareTo(DB) == 0){
					return true;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	
	//TODO Finish and Test this
	public void createDB(){
		/*
		if (!this.dbExist(DBName)){
			try {
				stmt.execute("create database "+ this.DBName);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		useDB();
		try {
			stmt.executeQuery("DROP TABLE parametros");
			stmt.execute("create table parametros ("
					+ "name string,"
					+ "value int)"
					+ "ROW FORMAT DELIMITED"
					+ "FIELDS TERMINATED BY '\\;'" //   \\ is for printing \
					+ "LOCATION '/user/cloudera/Subus5/ParametrosTable/'");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			stmt.executeQuery("INSERT INTO TABLE parametros select 'expedicion_porcentaje',10");
			stmt.executeQuery("INSERT INTO TABLE parametros select 'expedicion_cota_minima',15");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}
	
	
	public ResultSet executeQuery(String sql) throws SQLException{
		System.out.println("Ejecutando:" + sql);
		return this.stmt.executeQuery(sql);
	}
	
	public void printResultSet(ResultSet res){
		try {
			while (res.next()) {
				String tmpStr = "";    
				for (int i = 1; i< res.getMetaData().getColumnCount()+1; i++){
				    tmpStr += res.getString(i) + "\t";
				}
				System.out.println(tmpStr);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ResultSet getResExp(String selCol, String servicio, String sentido, int FranjaHorariaID, int mes){
		String sql = "select " + selCol
				+ " from exp_metrica "
				+ " where servicio='" + servicio + "'"
				+ " and sentido='" + sentido + "'"
				+ " and fh_id=" + String.valueOf(FranjaHorariaID)
				+ " and mes=" + mes;
		try {
			ResultSet res = this.executeQuery(sql);
			return res;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("Warning: no se encontraron resultados para la consulta.");
		}
		return null;
	}
	
	public ResultSet getResExp(String selCol, String servicio, String sentido, int FranjaHorariaID){
		String sql = "select " + selCol
				+ " from exp_metrica "
				+ " where servicio='" + servicio + "'"
				+ " and sentido='" + sentido + "'"
				+ " and fh_id=" + String.valueOf(FranjaHorariaID);
		try {
			ResultSet res = this.executeQuery(sql);
			return res;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("Warning: no se encontraron resultados para la consulta.");
		}
		return null;
	}
	
	public ResultSet getServicios(){
		String sql = "select * from servicios";
		try {
			return this.executeQuery(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public ResultSet getFranjasHorariasID(){
		String sql = "select * from fh_id";
		try {
			return this.executeQuery(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	/*
	 * Maneja las SQLException
	 */
	public void SQLExceptionManager(SQLException e){
		switch (e.getErrorCode()) {
		case 0: //Query no genera un resultado
            System.out.println("Usando: " + DBName);
			break;
		case 10072: //DB no existe
			System.out.println("Nombre de Exception: " + e.getClass().getName());
			System.exit(1);
			break;
		default:
			System.out.println("Mensaje: " + e.getMessage());
			System.out.println("ErrorCode: " + e.getErrorCode());
			e.printStackTrace();
			System.exit(1);		
		}
	}
	
	public static double[] res2Double(ResultSet res){
		ArrayList<Double> lista = new ArrayList();
		try {
			while (res.next()) {		//skipped when 0 results
				Double d = res.getDouble(1);
			    lista.add((d));
			    //System.out.println(d);
			}
			if (lista.size() == 0){
				return null;
			}
			//lista.size() is not 0
			double[] target = new double[lista.size()];
			for (int i = 0; i < target.length; i++) {
				target[i] = lista.get(i);                // java 1.5+ style (outboxing)
			    //System.out.println(target[i]);
			}
			return target;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("returned null");
			return null;
		}
	}
	
	
	
	/*
	 * Retorna una matriz de String a partir de un ResultSet
	 * retorna null si es que ocurre una exception
	 */
	public static String[][] res2StringMatrix(ResultSet res){
		ArrayList<String[]> matrix = new ArrayList();
		try {
			int nro_filas; //reservado para cantidad de filas
			int nro_columnas =res.getMetaData().getColumnCount();
			System.out.println("nro_columnas: " + nro_columnas);
			
			while (res.next()) { //recorre las filas	
				String strRow[] = new String[nro_columnas];
				for (int j = 0; j< nro_columnas; j++){
				    strRow[j] = res.getString(j+1);
				    System.out.print(strRow[j] + " | ");
				}
				matrix.add(strRow);
				System.out.println("");
			}
			
			System.out.println("CONTROL 1");
			nro_filas = matrix.size();
			System.out.println("nro_filas: " + nro_filas);
			String[][] strMatrix = new String[nro_filas][nro_columnas];
			for(int i=0; i<nro_filas; i++){
				for(int j=0; j<nro_columnas; j++){
					strMatrix[i][j] = matrix.get(i)[j];
					System.out.print(strMatrix[i][j] + " | ");
				}
				System.out.println("");
			}
			System.out.println("CONTROL 2");
			return strMatrix;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("returned null");
			return null;
		}
	}
	
	public void setServicio(String servicio){
		try {
			this.executeQuery("set hivevar:servicio=\"" + servicio + "\""); // set hivevar:servicio="servicio"
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("ERROR: Falla al setear variable hivevar:servicio con valor:" + servicio);
			e.printStackTrace();
		} 
	}
	
	public void insertResExp(){
		try {
			this.executeQuery("INSERT INTO TABLE exp_metrica select * from exp_metrica_view");
			
		} catch (SQLException e) {
			if (e.getErrorCode() != 0){ //error: 0 = The query did not generate a result set!
				e.printStackTrace();
				System.out.println("INSERT error: " + e.getErrorCode());
				System.out.println("INSERT msg: " + e.getMessage());
				System.err.println("ERROR: Falla al insertar exp_metrica_view en exp_metrica");
			}
		}
	}

	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}
	
	/**
	 * Busca la lista de servicios en la tabla servicios y procesa cada uno creando y borrando las vistas "exp_filtradas_ARG" y "val_filtradas_ARG"
	 */
	public void createTablaResExp(){
		
		//Crear vistas de argumento
		/*String sql_exp_filtradas_ARG = 
				"create view exp_filtradas_ARG as "
				+ " select "
				+ " concat(substr(fecha,7,4),'-',substr(fecha,4,2),'-',substr(fecha,1,2)) as fecha,"
				+ " pmod(datediff(concat(substr(fecha,7,4),'-',substr(fecha,4,2),'-',substr(fecha,1,2)),'1900-01-08'),7)+1 as dia,"
				+ " substr(fecha,4,2) as mes,"
				+ " patente, servicio, sentido, nro_expedicion,"
				+ " concat(substr(inicio_exp,7,4),'-',substr(inicio_exp,4,2),'-',substr(inicio_exp,1,2),' ',substr(inicio_exp,12,8),':00') as inicio_exp,"
				+ " concat(substr(fin_exp,7,4),'-',substr(fin_exp,4,2),'-',substr(fin_exp,1,2),' ',substr(fin_exp,12,8),':00') as fin_exp,"
				+ " concat(substr(inicio_val,7,4),'-',substr(inicio_val,4,2),'-',substr(inicio_val,1,2),' ',substr(inicio_val,12,8),':00') as inicio_val,"
				+ " concat(substr(fin_val,7,4),'-',substr(fin_val,4,2),'-',substr(fin_val,1,2),' ',substr(fin_val,12,8),':00') as fin_val,"
				+ " rut,"
				+ " nro_validaciones"
				+ " from expediciones"
				+ " where substr(servicio_org,2,100)= "; 
		*/
		
		String sql_exp_filtradas_ARG =
				"CREATE view exp_filtradas_ARG as"
				+" SELECT"
				+" concat("
				+" split(split(inicio_exp,' ')[0],'/')[2],"
				+" '-',"
				+" substr(concat('0',split(split(inicio_exp,' ')[0],'/')[0]),-2,2),"
				+" '-',"
				+" substr(concat('0',split(split(inicio_exp,' ')[0],'/')[1]),-2,2)) as fecha,"
				+" "
				+" pmod(datediff("
				+" concat("
				+" split(split(inicio_exp,' ')[0],'/')[2],"
				+" '-',"
				+" substr(concat('0',split(split(inicio_exp,' ')[0],'/')[0]),-2,2),"
				+" '-',"
				+" substr(concat('0',split(split(inicio_exp,' ')[0],'/')[1]),-2,2)"
				+" )"
				+" ,'1900-01-08'),7)+1 as dia,"
				+" "
				+" substr(concat('0',split(split(inicio_exp,' ')[0],'/')[0]),-2,2)  as mes,"
				+" patente, substr(servicio_org,2,100) as servicio, sentido_org as sentido, nro_expedicion,"
				+" "
				+" concat("
				+" split(split(inicio_exp,' ')[0],'/')[2],"
				+" '-',"
				+" substr(concat('0',split(split(inicio_exp,' ')[0],'/')[0]),-2,2),"
				+" '-',"
				+" substr(concat('0',split(split(inicio_exp,' ')[0],'/')[1]),-2,2),"
				+" ' ',"
				+" substr(concat('0',split(split(inicio_exp,' ')[1],':')[0]),-2,2),"
				+" ':',"
				+" substr(concat('0',split(split(inicio_exp,' ')[1],':')[1]),-2,2),':00') as inicio_exp,"
				+" "
				+" concat("
				+" split(split(fin_exp,' ')[0],'/')[2],"
				+" '-',"
				+" substr(concat('0',split(split(fin_exp,' ')[0],'/')[0]),-2,2),"
				+" '-',"
				+" substr(concat('0',split(split(fin_exp,' ')[0],'/')[1]),-2,2),"
				+" ' ',"
				+" substr(concat('0',split(split(fin_exp,' ')[1],':')[0]),-2,2),"
				+" ':',"
				+" substr(concat('0',split(split(fin_exp,' ')[1],':')[1]),-2,2),':00') as fin_exp,"
				+" concat("
				+" split(split(inicio_val,' ')[0],'/')[2],"
				+" '-',"
				+" substr(concat('0',split(split(inicio_val,' ')[0],'/')[0]),-2,2),"
				+" '-',"
				+" substr(concat('0',split(split(inicio_val,' ')[0],'/')[1]),-2,2),"
				+" ' ',"
				+" substr(concat('0',split(split(inicio_val,' ')[1],':')[0]),-2,2),"
				+" ':',"
				+" substr(concat('0',split(split(inicio_val,' ')[1],':')[1]),-2,2),':00') as inicio_val,"
				+" "
				+" concat("
				+" split(split(fin_val,' ')[0],'/')[2],"
				+" '-',"
				+" substr(concat('0',split(split(fin_val,' ')[0],'/')[0]),-2,2),"
				+" '-',"
				+" substr(concat('0',split(split(fin_val,' ')[0],'/')[1]),-2,2),"
				+" ' ',"
				+" substr(concat('0',split(split(fin_val,' ')[1],':')[0]),-2,2),"
				+" ':',"
				+" substr(concat('0',split(split(fin_val,' ')[1],':')[1]),-2,2),':00') as fin_val,"
				+" "
				+" rut,"
				+" nro_validaciones"
				+" from expediciones"
				+" where substr(servicio_org,2,100) = ";
		
		//sql_val_filtradas_ARG is DEPRECATED
		/*String sql_val_filtradas_ARG = "create view val_filtradas_ARG as "
				+" select "
				+" substr(fecha,1,10) as fecha,"
				+" pmod(datediff(fecha,'1900-01-08'),7)+1 as dia,"
				+" substr(fecha,6,2) as mes,"
				+" substr(fecha,1,19) as fecha_hora, servicio, sentido, patente, nombre_paradero1 as paradero" 	
				+" from validaciones"
				+" where "
				+" servicio =";
		*/
		
		//Get Servicios
		ResultSet resServicios = this.getServicios();
		ArrayList<String>ServicioArray = new ArrayList<String>();
		try {
			while (resServicios.next()){
				ServicioArray.add(resServicios.getString(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//TESTING
		//ServicioArray.clear();
		//ServicioArray.add("201");
		//ServicioArray.add("202");
		//END TESTING
		
		int count = 0;
		for(String servicio:ServicioArray){
			count++;
			System.out.println("[JOB]:Trabajando con servicio:" + servicio + "(" + count + "/" + ServicioArray.size() + ")");
			//DROP exp_filtradas_ARG
			try {
				this.executeQuery("DROP view exp_filtradas_ARG");
			} catch (SQLException e) {
				if(e.getErrorCode() != 0){
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("DROP exp_filtradas_ARG error: " + e.getErrorCode());
					System.out.println("DROP exp_filtradas_ARG msg: " + e.getMessage());
				}
			}
			
			//NEW exp_filtradas_ARG
			try {
				this.executeQuery(sql_exp_filtradas_ARG + "'" + servicio + "'");
			} catch (SQLException e) {
				if(e.getErrorCode() != 0){
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("CREATE exp_filtradas_ARG error: " + e.getErrorCode());
					System.out.println("CREATE exp_filtradas_ARG msg: " + e.getMessage());
				}
			}
			
			//DROP val_filtradas_ARG
			/*
			try {
				this.executeQuery("DROP view val_filtradas_ARG");
			} catch (SQLException e) {
				if(e.getErrorCode() != 0){
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("DROP val_filtradas_ARG error: " + e.getErrorCode());
					System.out.println("DROP val_filtradas_ARG msg: " + e.getMessage());			
				}
			}
			
			//NEW val_filtradas_ARG
			try {
				this.executeQuery(sql_val_filtradas_ARG + "'" + servicio + "'");
			} catch (SQLException e) {
				if(e.getErrorCode() != 0){
					// TODO Auto-generated catch block
					//e.printStackTrace();
					System.out.println("CREATE val_filtradas_ARG error: " + e.getErrorCode());
					System.out.println("CREATE val_filtradas_ARG msg: " + e.getMessage());
				}
			}
			*/
			
			//insertar en ResExp
			this.insertResExp();
			System.out.println("[END]: Servicio:" + servicio + " terminado.");
		}
	}
	
	public void createTablaPuntosDeCorte(double nroValMinPercent, double nroValMaxPercent,
			double nroValPosMinPercent, double nroValPosMaxPercent, 
			double valPondMinPercent, double valPondMaxPercent,
			double nroParMinPercent, double nroParMaxPercent,
			double distTiempoPromedioMinPercent, double distTiempoPromedioMaxPercent ){
		//round numero de decimales
		int decimal_numbers = 2;
		
		//Get Servicios
		ResultSet resServicios = this.getServicios();
		ArrayList<String>ServicioArray = new ArrayList<String>();
		try {
			while (resServicios.next()){
				ServicioArray.add(resServicios.getString(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Get FHs
		ResultSet resFH = this.getFranjasHorariasID();
		ArrayList<Integer> FHArray = new ArrayList<Integer>();
		try {
			while(resFH.next()){
				FHArray.add(resFH.getInt(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayList<String> SentidoArray = new ArrayList<String>();
		SentidoArray.add("I");
		SentidoArray.add("R");
		
		//TODO incluir mes
		
		String query = "INSERT INTO TABLE puntos_corte_percentil values  ";
		for (String servicio:ServicioArray){
			for(String sentido:SentidoArray){
				for (int fh:FHArray){
				//TODO implementar mes
					/*
					ResultSet resParam1 = this.getResExp(columnDicctionary.get(this.ResExpParam1), servicio, sentido, fh, this.AGO);
					double[] valuesParam1 = this.res2Double(resParam1);
					ResultSet resParam2 = this.getResExp(columnDicctionary.get(this.ResExpParam2), servicio, sentido, fh, this.AGO);
					double[] valuesParam2 = this.res2Double(resParam2);
					ResultSet resParam3 = this.getResExp(columnDicctionary.get(this.ResExpParam3), servicio, sentido, fh, this.AGO);
					double[] valuesParam3 = this.res2Double(resParam3);
					ResultSet resParam4 = this.getResExp(columnDicctionary.get(this.ResExpParam4), servicio, sentido, fh, this.AGO);
					double[] valuesParam4 = this.res2Double(resParam4);
					ResultSet resParam5 = this.getResExp(columnDicctionary.get(this.ResExpParam5), servicio, sentido, fh, this.AGO);
					double[] valuesParam5 = this.res2Double(resParam5);
					*/
					
					ResultSet resParam1 = this.getResExp(columnDicctionary.get(this.ResExpParam1), servicio, sentido, fh);
					double[] valuesParam1 = this.res2Double(resParam1);
					ResultSet resParam2 = this.getResExp(columnDicctionary.get(this.ResExpParam2), servicio, sentido, fh);
					double[] valuesParam2 = this.res2Double(resParam2);
					ResultSet resParam3 = this.getResExp(columnDicctionary.get(this.ResExpParam3), servicio, sentido, fh);
					double[] valuesParam3 = this.res2Double(resParam3);
					ResultSet resParam4 = this.getResExp(columnDicctionary.get(this.ResExpParam4), servicio, sentido, fh);
					double[] valuesParam4 = this.res2Double(resParam4);
					ResultSet resParam5 = this.getResExp(columnDicctionary.get(this.ResExpParam5), servicio, sentido, fh);
					double[] valuesParam5 = this.res2Double(resParam5);
					
					
					if (valuesParam1 != null &&
							valuesParam2 != null &&
							valuesParam3 != null &&
							valuesParam4 != null &&
							valuesParam5 != null
							){
						
						//Para ResExpParam5 que es 'duracion_exp' se reemplazará por un nuevo índice, que es la distancia hasta el promedio
						double Param5prom = 0;
						for (int i=0; i<valuesParam5.length; i++){
							Param5prom += valuesParam5[i];	//sumatoria
						}
						Param5prom = Math.round(Param5prom/valuesParam5.length);	//error despreciable al aproximar ya que la escala es en minutos (+-1minuto)
						
						double[] valuesParam5_2 = new double[valuesParam5.length];
						for(int i=0; i<valuesParam5.length; i++){
							valuesParam5_2[i] = Math.abs( Param5prom - valuesParam5[i] );
						}
						
						
						//Calcular puntos de corte
						double minParam1 = Math.round(HistogramChart.getQuantile(nroValMinPercent, valuesParam1));
						double maxParam1 = Math.round(HistogramChart.getQuantile(nroValMaxPercent, valuesParam1));
						double minParam2 = Math.round(HistogramChart.getQuantile(nroValPosMinPercent, valuesParam2));
						double maxParam2 = Math.round(HistogramChart.getQuantile(nroValPosMaxPercent, valuesParam2));
						double minParam3 = round(HistogramChart.getQuantile(valPondMinPercent, valuesParam3),decimal_numbers);
						double maxParam3 = round(HistogramChart.getQuantile(valPondMaxPercent, valuesParam3),decimal_numbers);
						double minParam4 = Math.round(HistogramChart.getQuantile(nroParMinPercent, valuesParam4));
						double maxParam4 = Math.round(HistogramChart.getQuantile(nroParMaxPercent, valuesParam4));
						double minParam5 = Math.round(HistogramChart.getQuantile(distTiempoPromedioMinPercent, valuesParam5_2));
						double maxParam5 = Math.round(HistogramChart.getQuantile(distTiempoPromedioMaxPercent, valuesParam5_2));
						
						//fila con los valores que será usada en un INSERT
						query += "('" + servicio + "', "
								+ "'" + sentido + "', "
								+ fh + ", "
								+ minParam1 +", "
								+ maxParam1 +", "
								+ minParam2 +", "
								+ maxParam2 +", "
								+ minParam3 +", "
								+ maxParam3 +", "
								+ minParam4 +", "
								+ maxParam4 +", "
								+ minParam5 +", "
								+ maxParam5 +", "
								+ Param5prom + ")";
					}//if
				}//for fh
			}//for sentido 
		}//for servicio
		//System.out.println(query);
		
		query = query.replace(")(", "),(");
		System.out.println("query: " + query);
		try {
			this.executeQuery(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			int error_code = e.getErrorCode();
			if (error_code != 0){
				System.out.println("ERROR: fallo en INSERT into TABLE puntos_corte_percentil");	
				if(error_code == 40000){
					System.out.println("ERROR " + error_code + ": la tabla donde desea insertar no existe.");
				}
			}
		}//catch
	}
	
	public ResultSet getRankingMAB(String fechaIni, String fechaFin){
		//DROP Ranking_MAB_view
				try {
					this.executeQuery("DROP VIEW Ranking_MAB_view");
				} catch (SQLException e) {
					if(e.getErrorCode() != 0){
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println("DROP VIEW  Ranking_MAB_view error: " + e.getErrorCode());
						System.out.println("DROP VIEW  Ranking_MAB_view msg: " + e.getMessage());
					}
				}

		
		//SQLRankingMAB
		String sqlRankingMAB = "CREATE VIEW Ranking_MAB_view as"
				+ " select RUT,"
				+ " count(*) as cant_exp,"
				+ " count (CASE clasif_nro_validaciones_exp when 'M' then 1 else null END) as M_nro_val,"
				+ " count (CASE clasif_nro_validaciones_exp when 'A' then 1 else null END) as A_nro_val,"
				+ " count (CASE clasif_nro_validaciones_exp when 'B' then 1 else null END) as B_nro_val,"
				+ " round((count (CASE clasif_nro_validaciones_exp when 'M' then 1 else null END) / count(*) )*100 ,2) as M_nro_val_porc,"
				+ " round((count (CASE clasif_nro_validaciones_exp when 'A' then 1 else null END) / count(*) )*100 ,2) as A_nro_val_porc,"
				+ " round((count (CASE clasif_nro_validaciones_exp when 'B' then 1 else null END) / count(*) )*100 ,2) as B_nro_val_porc,"
				+ " count (CASE clasif_dist_tiempo_promedio when 'M' then 1 else null END) as M_dist_tiempo_promedio,"
				+ " count (CASE clasif_dist_tiempo_promedio when 'A' then 1 else null END) as A_dist_tiempo_promedio,"
				+ " count (CASE clasif_dist_tiempo_promedio when 'B' then 1 else null END) as B_dist_tiempo_promedio,"
				+ " round((count (CASE clasif_dist_tiempo_promedio when 'M' then 1 else null END) / count(*) )*100 ,2) as M_dist_tiempo_promedio_porc,"
				+ " round((count (CASE clasif_dist_tiempo_promedio when 'A' then 1 else null END) / count(*) )*100 ,2) as A_dist_tiempo_promedio_porc,"
				+ " round((count (CASE clasif_dist_tiempo_promedio when 'B' then 1 else null END) / count(*) )*100 ,2) as B_dist_tiempo_promedio_porc"
				+ " from clasificacion_exp"
				+ " where servicio!='(Indeterminado)'"
				+ " and fecha >= "
				+ "'" + fechaIni + "'"
				+ " and fecha <= "
				+ "'" + fechaFin + "'"
				+ " group by rut";
		
		
		//CREATE Ranking_MAB_view
		try {
			this.executeQuery(sqlRankingMAB);
		} catch (SQLException e) {
			if(e.getErrorCode() != 0){
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("sql_Ranking_MAB error: " + e.getErrorCode());
				System.out.println("sql_Ranking_MAB msg: " + e.getMessage());
			}
		}
	
		//GET RANKING
		String sqlGetRanking = "select * from Ranking_MAB_view order by M_nro_val_porc DESC ";
		try {
			return this.executeQuery(sqlGetRanking);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public ResultSet getRankingCalificacion(String fechaIni, String fechaFin){
		//DROP Ranking_getRankingCalificacion
		/*
			try {
					this.executeQuery("DROP VIEW Ranking_MAB_view");
				} catch (SQLException e) {
					if(e.getErrorCode() != 0){
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println("DROP VIEW  Ranking_MAB_view error: " + e.getErrorCode());
						System.out.println("DROP VIEW  Ranking_MAB_view msg: " + e.getMessage());
					}
				}

		*/
		//SQLRankingCalificacion
		String sqlRankingCalificacion = "with MAB as (select RUT,"
				+" count(*) as cant_exp,"
				+" round((count (CASE clasif_nro_validaciones_exp when 'M' then 1 else null END) / count(*) )*100 ,2) as Mv,"
				+" round((count (CASE clasif_nro_validaciones_exp when 'A' then 1 else null END) / count(*) )*100 ,2) as Av,"
				+" round((count (CASE clasif_nro_validaciones_exp when 'B' then 1 else null END) / count(*) )*100 ,2) as Bv,"
				+" round((count (CASE clasif_dist_tiempo_promedio when 'M' then 1 else null END) / count(*) )*100 ,2) as MD,"
				+" round((count (CASE clasif_dist_tiempo_promedio when 'A' then 1 else null END) / count(*) )*100 ,2) as AD,"
				+" round((count (CASE clasif_dist_tiempo_promedio when 'B' then 1 else null END) / count(*) )*100 ,2) as BD"
				+" from clasificacion_exp"
				//+" --where servicio!='(Indeterminado)'"
				+" where"
				+" fecha >= '" + fechaIni + "'"
				+" and fecha <= '" + fechaFin + "'"
				+" group by RUT)"
				+" select RUT,"
				+" if (Mv >=40.0,"
				+"    if(MD >=0.4,"
				+"        1,"
				//+"    --else"
				+"        if(BD >=30.0,"
				+"            3.0,"
				//+"        --else"
				+"            2.0"
				+"        )"
				+"     ),"
				//+" --else"
				+"    if(Bv >= 30.0,"
				+"        if(BD >= 30.0,"
				+"            7.0,"
				//+"        --else"
				+"            if(MD >= 40.0,"
				+"                4.5,"
				//+"            --else"
				+"                6.0"
				+"            )"
				+"        ),"
				//+"    --else"
				+"        if(MD >= 40.0,"
				+"            3.0,"
				//+"        --else"
				+"            if(BD >=30.0,"
				+"                5.0,"
				//+"            --else"
				+"                4.0"
				+"            )"
				+"        )"
				+"    )"
				+" )"
				+" as calificacion,"
				+" cant_exp,"
				+" Mv,Av,Bv,MD,AD,BD"
				+" from MAB"
				+" order by calificacion";
	
		//GET RANKING CALIFICACION
		try {
			return this.executeQuery(sqlRankingCalificacion);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	
	public ResultSet getExpedicionesByRut(String rut, String fechaIni, String fechaFin){
		String sql = "with promedioMAB as ("
				+ " SELECT Servicio, Sentido, fh_id,"
				+ " round(SUM (CASE clasif_nro_validaciones_exp WHEN 'M' then nro_validaciones_exp ELSE null END) /"
				+ " COUNT (CASE clasif_nro_validaciones_exp WHEN 'M' then 1 ELSE null END) ,2) as M_prom_nro_validaciones_exp,"
				+ " "
				+ " round(SUM (CASE clasif_nro_validaciones_exp WHEN 'A' then nro_validaciones_exp ELSE null END) /"
				+ " COUNT (CASE clasif_nro_validaciones_exp WHEN 'A' then 1 ELSE null END) ,2) as A_prom_nro_validaciones_exp,"
				+ " "
				+ " round(SUM (CASE clasif_nro_validaciones_exp WHEN 'B' then nro_validaciones_exp ELSE null END) /"
				+ " COUNT (CASE clasif_nro_validaciones_exp WHEN 'B' then 1 ELSE null END) ,2) as B_prom_nro_validaciones_exp,"
				+ " "
				+ " round(SUM (CASE clasif_dist_tiempo_promedio WHEN 'M' then dist_tiempo_promedio ELSE null END) /"
				+ " COUNT (CASE clasif_dist_tiempo_promedio WHEN 'M' then 1 ELSE null END) ,2) as M_prom_dist_tiempo_prom,"
				+ " "
				+ " round(SUM (CASE clasif_dist_tiempo_promedio WHEN 'A' then dist_tiempo_promedio ELSE null END) /"
				+ " COUNT (CASE clasif_dist_tiempo_promedio WHEN 'A' then 1 ELSE null END) ,2) as A_prom_dist_tiempo_prom,"
				+ " "
				+ " round(SUM (CASE clasif_dist_tiempo_promedio WHEN 'B' then dist_tiempo_promedio ELSE null END) /"
				+ " COUNT (CASE clasif_dist_tiempo_promedio WHEN 'B' then 1 ELSE null END) ,2) as B_prom_dist_tiempo_prom"
				+ " "
				+ " from clasificacion_exp"
				+ " where servicio!='(Indeterminado)' and fecha >= '" + fechaIni + "' and fecha <= '" + fechaFin + "'"
				+ " group by Servicio, Sentido, fh_id"
				+ " )"
				+ " "
				+ " select"
				+ " fecha,"
				+ " clasificacion_exp.servicio,"
				+ " clasificacion_exp.sentido,"
				+ " inicio_exp,"
				+ " nro_validaciones_exp,"
				+ " clasif_nro_validaciones_exp,"
				+ " M_prom_nro_validaciones_exp,"
				+ " A_prom_nro_validaciones_exp,"
				+ " B_prom_nro_validaciones_exp,"
				+ " "
				+ " dist_tiempo_promedio,"
				+ " clasif_dist_tiempo_promedio,"
				+ " M_prom_dist_tiempo_prom,"
				+ " A_prom_dist_tiempo_prom,"
				+ " B_prom_dist_tiempo_prom"
				+ " from clasificacion_exp join promedioMAB on"
				+ " (clasificacion_exp.servicio = promedioMAB.servicio"
				+ " and clasificacion_exp.sentido = promedioMAB.sentido"
				+ " and clasificacion_exp.fh_id = promedioMAB.fh_id)"
				+ " where clasificacion_exp.servicio!='(Indeterminado)'"
				+ " and fecha >= '2015-11-01' and fecha >= '" + fechaIni + "' and fecha <= '" + fechaFin + "'"
				+ " and rut='"+ rut + "'";
		try {
			return this.executeQuery(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 
	 * @param rut
	 * @param inicio_exp
	 * @return nombre_paradero, fechahora, nroVal
	 */
	public ResultSet getValParTiempo(String rut, String inicio_exp){
		//Con el rut e inicio_exp se pueden obtener patente, inicio_val y fin_val
		//Mientras SUBUS no corrija error en inicio_val y fin_val se usará inicio_exp, y fin_exp
		
		//Convertir formato de inicio_exp 
		String fecha = inicio_exp.split(" ")[0];
		String tiempo = inicio_exp.split(" ")[1];
		String anno = fecha.split("-")[0];
		String mes = fecha.split("-")[1];
		String dia = fecha.split("-")[2];
		String hora = tiempo.split(":")[0];
		String minutos = tiempo.split(":")[1];
		
		//String inicio_exp_Formated = dia + "/" + mes + "/" + anno + " " + hora + ":" + minutos; //formato especial para tabla Expediciones dd/MM/yyyy hh:mm:ss
		String inicio_exp_Formated = anno + "-" + mes + "-" + dia + " " + hora + ":" + minutos + ":00"; //formato exp_formated yyyy-MM-dd hh:mm:ss
		System.out.println("inicio_exp_Formated:" + inicio_exp_Formated);
		//SQL1: obtener patente, inicio_val y fin_val     (se usará inicio_exp y fin_exp)
		String sql1 = " select patente, inicio_exp, fin_exp"
				+ " from exp_metrica"
				+ " where inicio_exp= '" + inicio_exp + "' and rut= '" + rut + "'";
		try {
			String patente = "";
			String inicio_val = "";
			String fin_val = "";
			ResultSet res = this.executeQuery(sql1);
			while(res.next()){
				patente = res.getString(1);
				inicio_val = res.getString(2);
				fin_val = res.getString(3);
			}
			System.out.println("Patente:" + patente + " |inicio_val:" + inicio_val + " |fin_val:" + fin_val);
		
			//formatear inicio_val y fin_val  desde dd/mm/yyyy hh:mm a yyyy-mm-dd hh:mm:ss
			/*String inicio_val_fecha = inicio_val.split(" ")[0];
			String inicio_val_dia = inicio_val_fecha.split("/")[0];
			String inicio_val_mes = inicio_val_fecha.split("/")[1];
			String inicio_val_anno = inicio_val_fecha.split("/")[2];
			String inicio_val_tiempo = inicio_val.split(" ")[1];
			String inicio_val_hora = inicio_val_tiempo.split(":")[0];
			String inicio_val_minutos = inicio_val_tiempo.split(":")[1];
			
			String inicio_val_formated = inicio_val_anno + "-" + inicio_val_mes + "-" + inicio_val_dia
					+ " " + inicio_val_hora + ":" + inicio_val_minutos + ":00.000";
			
			String fin_val_fecha = fin_val.split(" ")[0];
			String fin_val_dia = fin_val_fecha.split("/")[0];
			String fin_val_mes = fin_val_fecha.split("/")[1];
			String fin_val_anno = fin_val_fecha.split("/")[2];
			String fin_val_tiempo = fin_val.split(" ")[1];
			String fin_val_hora = fin_val_tiempo.split(":")[0];
			String fin_val_minutos = fin_val_tiempo.split(":")[1];
			
			String fin_val_formated = fin_val_anno + "-" + fin_val_mes + "-" + fin_val_dia
					+ " " + fin_val_hora + ":" + fin_val_minutos + ":00.000";
			
			*/
			//SQL2: obtener las validaciones por paradero para la expedicion
			String sql2 = "with ValPorPar as ("
					+ " Select nombre_paradero1, from_unixtime(floor(avg(unix_timestamp(fecha)))) as fechahora, count (*) as nroVal"
					+ " From validaciones"
					+ " Where fecha >= '" + inicio_val + "' and"
					+ " Fecha < '" + fin_val + "' and"
					+ " Patente = '" + patente + "' and "
					+ " nombre_paradero1!=''"
					+ " Group by nombre_paradero1"
					+ ")"
					+ " select * from ValPorPar order by fechahora";
			System.out.println("sql2:" + sql2);
			return this.executeQuery(sql2);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 *
	 * @param servicio
	 * @param sentido
	 * @return Orden,codigo_paradero_usuario
	 */
	public ResultSet getParaderos(String servicio, String sentido) {
		String sql = "select orden, codigo_paradero_usuario from paraderos"
				+ " where (servicio='" + servicio + "' or servicio_ts='" + servicio +"') and "
				+ " codigo_paradero_usuario!='' and "
				+ " sentido='" + sentido +"' order by orden";
		try {
			return this.executeQuery(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	

	
	/*
	public static void main(String[] args) throws SQLException {
		System.out.println("--- o ---\nHiveJdbcClient.main: Iniciando programa...");		
		try {
			Class.forName(driverName);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
		HiveJdbcClient dbClient = new HiveJdbcClient();
		
		
		
		// Use DBName
		if (!DBName.equals("")){
			try{
				res = stmt.executeQuery("use " + DBName);		
                //System.out.println(res.toString());
			}
			catch (SQLException e) {
				dbClient.SQLExceptionManager(e);
			}
		}
        
		// Query example
		try{
			sql = "select * from resexp where Servicio='216' and Sentido='I' and FranjaHorariaID=8";
			System.out.println("Running: " + sql+"\n");
			res = stmt.executeQuery(sql);
			while (res.next()) {
				String tmpStr = "";    
				for (int i = 1; i< res.getMetaData().getColumnCount()+1; i++){
				    tmpStr += res.getString(i) + "\t";
				}
				System.out.println(tmpStr);
			}

	        System.out.println("##############################################################");
		}
		catch (SQLException e) {
			dbClient.SQLExceptionManager(e);
		}		
		
		System.out.println("TEsting Chart");
		//TODO TEST HISTOGRAM
		/*HistogramDataset dataset = new HistogramDataset();
		double[] values = {1.0,1.0,1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0};
		dataset.addSeries("H1", values, 10, 0.0, 10.0);
		HistogramChart histChart =  new HistogramChart("MiTitulo", dataset);
		
		JFreeChart chart = histChart.buildHistogram(values, 4, "PlotTitulo", "EJE X", "EJE Y",true);
		*/
        
		/*
		double[] values = {1.0,1.0,1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0};
		HistogramChart.buildHistogram(values, 6, "PlotTitulo", "EJE X", "EJE Y",true);
		System.out.println("FIN DEL PROGRAMA");
		*/
		
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
	//}
	
}
