CREATE DATABASE Subus8;
USE Subus8;

--'Subus8 elimina el filtro estricto de las versiones anteriores y parea las validaciones con las expediciones solo usando la patente y la fechahora';

--set hivevar:servicio=216
--set hivevar:sentido='I'
--set hivevar:FH=7

----------Crear tablas de parametros----------
SELECT 'Creando tabla parametros...';
DROP TABLE parametros;
create table parametros (
name string,
value int)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY '\;'
LOCATION '/user/lloyola/Subus8/ParametrosTable/';

INSERT INTO TABLE parametros select 'expedicion_porcentaje',10;
INSERT INTO TABLE parametros select 'expedicion_cota_minima',15;

select * from parametros;
SELECT 'Tabla parametros [CREADA]\n####################################\n\n';


----------Crear tablas----------
--DEPRECATED, solo funciona para v7 o anteriores.
--SELECT 'Creando tabla expediciones...';
--DROP TABLE expediciones;
--CREATE EXTERNAL TABLE expediciones (
--fecha string,
--id_deposito_conductor string,
--nombre_deposito_conductor string,
--id_conductor string,
--nombre_conductor string,
--rut string,
--id_bus string,
--patente string,
--servicio string,
--servicio_org string,
--sentido string,
--nro_expedicion string,
--inicio_exp string,
--fin_exp string,
--inicio_val string,
--fin_val string,
--expedicion_parcial string,
--nro_validaciones int)
--ROW FORMAT DELIMITED 
--FIELDS TERMINATED BY '\;' 
--LOCATION '/user/lloyola/ExpedicionTable/'

--EXPEDICIONES
SELECT 'Creando tabla expediciones...';
DROP TABLE expediciones;
CREATE EXTERNAL TABLE expediciones (
fecha string,
id_deposito_conductor string,
nombre_deposito_conductor string,
id_conductor string,
nombre_conductor string,
rut string,
id_bus string,
patente string,
descripcion string,
servicio_org string,
sentido_org string,
nro_expedicion int,
inicio_exp string,
fin_exp string,
inicio_val string,
fin_val string,
expedicionParcial string,
nro_validaciones int)
ROW FORMAT DELIMITED 
FIELDS TERMINATED BY '\;' 
LOCATION '/user/lloyola/Subus8/ExpedicionTable/'
tblproperties("skip.header.line.count"="1");
SELECT 'Creando tabla expediciones [CREADA]\n####################################\n\n';


--PARADEROS
SELECT 'Creando tabla paraderos...';
DROP TABLE paraderos;
CREATE EXTERNAL TABLE paraderos (
orden int,
servicio_ts string,
servicio string,
sentido string,
variante string,
un string,
codigo_paradero_ts string,
codigo_paradero_usuario string,
comuna string,
eje string,
desde string,
hacia string,
x string,
y string,
nombre_paradero string,
zona_paga int,
paradas_con_excepciones string)
ROW FORMAT DELIMITED 
FIELDS TERMINATED BY '\;' 
LOCATION '/user/lloyola/ParaderoTable/';
SELECT 'Creando tabla paraderos [CREADA]\n####################################\n\n';

--VALIDACIONES
SELECT 'Creando tabla validaciones...';
DROP TABLE validaciones;
CREATE EXTERNAL TABLE validaciones(
patente string,
fecha string,
chip string,
tarjeta string,
NTT int,
servicio string,
sentido string,
nombre_paradero1 string,
distancia_paradero_1 int,
nombre_paradero2 string,
distancia_paradero_2 int,
indicador_paradero_valido string,
zona_paga string)
ROW FORMAT DELIMITED 
FIELDS TERMINATED BY '\,' 
LOCATION '/user/lloyola/Subus8/ValidacionTable/'
tblproperties("skip.header.line.count"="1");
SELECT 'Creando tabla validaciones [CREADA]\n####################################\n\n';

--FRANJAS HORARIAS
SELECT 'Creando tabla franja_horaria...';
DROP TABLE franja_horaria;
CREATE EXTERNAL TABLE franja_horaria (
id int,
dia int,
inicio string,
fin string)
ROW FORMAT DELIMITED 
FIELDS TERMINATED BY '\,'
LOCATION '/user/lloyola/Subus8/FranjaHorariaTable/';
SELECT 'Creando tabla franja_horaria [CREADA]\n####################################\n\n';

----------Crear tabla fh_id----------
SELECT 'Creando tabla fh_id...';
DROP TABLE fh_id;
CREATE EXTERNAL TABLE fh_id (
id int)
ROW FORMAT DELIMITED 
FIELDS TERMINATED BY '\,'
LOCATION '/user/lloyola/Subus8/FH_ID_Table/';
INSERT INTO TABLE fh_id select distinct id from franja_horaria;
SELECT 'Creando tabla fh_id [CREADA]\n####################################\n\n';


----------Crear tabla servicios----------
SELECT 'Creando tabla servicios...';
DROP TABLE servicios;
CREATE TABLE servicios (
servicio string)
ROW FORMAT DELIMITED 
FIELDS TERMINATED BY '\;' 
LOCATION '/user/lloyola/ServiciosTable/';
INSERT INTO TABLE servicios select distinct substr(servicio_org,2,100) from expediciones;
SELECT 'Creando tabla servicios [CREADA]\n####################################\n\n';


----------Crear vistas----------

--Crear vista params_1_2_view
---exp_porcen, exp_cota_min 
SELECT 'Creando view params_1_2_view...';
DROP VIEW params_1_2_view;
create view params_1_2_view as
with expedicion_porcentaje as (
select value as exp_porcen from parametros where name='expedicion_porcentaje'
),
expedicion_cota_minima as (
select value as exp_cota_min from parametros where name='expedicion_cota_minima'
)
select * from expedicion_porcentaje join expedicion_cota_minima;
SELECT 'Creando view params_1_2_view [CREADA]\n####################################\n\n';


--Crear vista exp_filtradas_ARG
--Los argumentos son seteados desde java
--ARGS: ${servicio}
SELECT 'Creando view exp_filtradas...';
DROP view exp_filtradas_ARG;
CREATE view exp_filtradas_ARG as
SELECT
concat(
split(split(inicio_exp,' ')[0],'/')[2],
'-',
substr(concat('0',split(split(inicio_exp,' ')[0],'/')[0]),-2,2),
'-',
substr(concat('0',split(split(inicio_exp,' ')[0],'/')[1]),-2,2)) as fecha,

pmod(datediff(
concat(
split(split(inicio_exp,' ')[0],'/')[2],
'-',
substr(concat('0',split(split(inicio_exp,' ')[0],'/')[0]),-2,2),
'-',
substr(concat('0',split(split(inicio_exp,' ')[0],'/')[1]),-2,2)
)
,'1900-01-08'),7)+1 as dia,

substr(concat('0',split(split(inicio_exp,' ')[0],'/')[0]),-2,2)  as mes,
patente, substr(servicio_org,2,100) as servicio, sentido_org as sentido, nro_expedicion,

concat(
split(split(inicio_exp,' ')[0],'/')[2],
'-',
substr(concat('0',split(split(inicio_exp,' ')[0],'/')[0]),-2,2),
'-',
substr(concat('0',split(split(inicio_exp,' ')[0],'/')[1]),-2,2),
' ',
substr(concat('0',split(split(inicio_exp,' ')[1],':')[0]),-2,2),
':',
substr(concat('0',split(split(inicio_exp,' ')[1],':')[1]),-2,2),':00') as inicio_exp,

concat(
split(split(fin_exp,' ')[0],'/')[2],
'-',
substr(concat('0',split(split(fin_exp,' ')[0],'/')[0]),-2,2),
'-',
substr(concat('0',split(split(fin_exp,' ')[0],'/')[1]),-2,2),
' ',
substr(concat('0',split(split(fin_exp,' ')[1],':')[0]),-2,2),
':',
substr(concat('0',split(split(fin_exp,' ')[1],':')[1]),-2,2),':00') as fin_exp,
concat(
split(split(inicio_val,' ')[0],'/')[2],
'-',
substr(concat('0',split(split(inicio_val,' ')[0],'/')[0]),-2,2),
'-',
substr(concat('0',split(split(inicio_val,' ')[0],'/')[1]),-2,2),
' ',
substr(concat('0',split(split(inicio_val,' ')[1],':')[0]),-2,2),
':',
substr(concat('0',split(split(inicio_val,' ')[1],':')[1]),-2,2),':00') as inicio_val,

concat(
split(split(fin_val,' ')[0],'/')[2],
'-',
substr(concat('0',split(split(fin_val,' ')[0],'/')[0]),-2,2),
'-',
substr(concat('0',split(split(fin_val,' ')[0],'/')[1]),-2,2),
' ',
substr(concat('0',split(split(fin_val,' ')[1],':')[0]),-2,2),
':',
substr(concat('0',split(split(fin_val,' ')[1],':')[1]),-2,2),':00') as fin_val,

rut,
nro_validaciones
from expediciones
where substr(servicio_org,2,100) = '201';
SELECT 'creando view exp_filtradas [CREADA]\n####################################\n\n';


--Crear vista val_filtradas_ARG
--Los argumentos son seteados desde java
--ARGS: ${servicio}
SELECT 'Creando view val_filtradas...';
DROP view val_filtradas_ARG;
create view val_filtradas_ARG as
select
substr(fecha,1,10) as fecha,
pmod(datediff(fecha,'1900-01-08'),7)+1 as dia,
substr(fecha,6,2) as mes,
substr(fecha,1,19) as fecha_hora, servicio, sentido, patente, nombre_paradero1 as paradero,
tarjeta,ntt
from validaciones;
SELECT 'Creando view val_filtradas [CREADA]\n####################################\n\n';


--Crear vista de expediciones
--NOTA: Se utiliza como rango de validación los rangos de expedición, porque los rangos de validacion están con problemas y se superponen dejando validaciones que corresponden a 2 expediciones.
--cuando se corrija el error, se debe modificar esta vista.
SELECT 'Creando view exp_view...';
DROP view exp_view;
CREATE view exp_view as
SELECT
exp_filtradas_ARG.fecha,
exp_filtradas_ARG.dia,
exp_filtradas_ARG.mes,
exp_filtradas_ARG.patente,
exp_filtradas_ARG.servicio,
exp_filtradas_ARG.sentido,
exp_filtradas_ARG.nro_expedicion,
exp_filtradas_ARG.inicio_exp,
exp_filtradas_ARG.fin_exp,
--exp_filtradas_arg.inicio_val,
--exp_filtradas_arg.fin_val,
exp_filtradas_ARG.inicio_exp as inicio_val,
exp_filtradas_ARG.fin_exp as fin_val,
exp_filtradas_ARG.rut,
exp_filtradas_ARG.nro_validaciones as nro_validaciones_exp,
sum(if(exp_filtradas_arg.inicio_exp <= val_filtradas_ARG.fecha_hora and val_filtradas_ARG.fecha_hora < exp_filtradas_ARG.fin_exp
,1,0)) as nro_validaciones_val
from exp_filtradas_ARG left join val_filtradas_ARG
on (
val_filtradas_ARG.patente = exp_filtradas_ARG.patente
)
group by 
exp_filtradas_ARG.fecha,
exp_filtradas_ARG.dia,
exp_filtradas_ARG.mes,
exp_filtradas_ARG.patente,
exp_filtradas_ARG.servicio,
exp_filtradas_ARG.sentido,
exp_filtradas_ARG.nro_expedicion,
exp_filtradas_ARG.inicio_exp,
exp_filtradas_ARG.fin_exp,
exp_filtradas_ARG.inicio_val,
exp_filtradas_ARG.fin_val,
exp_filtradas_ARG.rut,
exp_filtradas_ARG.nro_validaciones
--having
--nro_validaciones_exp != 0 and nro_validaciones_val=0
;
--fin de crear vista de expediciones
SELECT 'Creando view exp_view [CREADA]\n####################################\n\n';


--Crear vista de expediciones
--DEPRECATED: no se filtrarán debido a que no hay un criterio claro para ello. Se pierden demasiadas expediciones. Los outlayers serán tratados en otro proceso.
--Selecciona las expediciones que seran utilizadas en el análisis de datos
--SELECT 'Creando view exp_view...';
--DROP view exp_view;
--CREATE view exp_view as
--SELECT
--exp_filtradas_ARG.fecha,
--exp_filtradas_ARG.dia,
--exp_filtradas_ARG.mes,
--exp_filtradas_ARG.patente,
--exp_filtradas_ARG.servicio,
--exp_filtradas_ARG.sentido,
--exp_filtradas_ARG.nro_expedicion,
--exp_filtradas_ARG.inicio_exp,
--exp_filtradas_ARG.fin_exp,
--exp_filtradas_ARG.inicio_val,
--exp_filtradas_ARG.fin_val,
--exp_filtradas_ARG.rut,
--exp_filtradas_ARG.nro_validaciones as nro_validaciones_exp,
--count(*) as nro_validaciones_val,
--params_1_2_view.exp_porcen,
--params_1_2_view.exp_cota_min
--from exp_filtradas_ARG join val_filtradas_ARG
--on (
--val_filtradas_ARG.patente = exp_filtradas_ARG.patente
--)
--join params_1_2_view
--where(
--val_filtradas_ARG.fecha_hora >= exp_filtradas_ARG.inicio_val
--and val_filtradas_ARG.fecha_hora <= exp_filtradas_ARG.fin_val
--)
--group by 
--exp_filtradas_ARG.fecha,
--exp_filtradas_ARG.dia,
--exp_filtradas_ARG.mes,
--exp_filtradas_ARG.patente,
--exp_filtradas_ARG.servicio,
--exp_filtradas_ARG.sentido,
--exp_filtradas_ARG.nro_expedicion,
--exp_filtradas_ARG.inicio_exp,
--exp_filtradas_ARG.fin_exp,
--exp_filtradas_ARG.inicio_val,
--exp_filtradas_ARG.fin_val,
--exp_filtradas_ARG.rut,
--exp_filtradas_ARG.nro_validaciones,
--params_1_2_view.exp_porcen,
--params_1_2_view.exp_cota_min
--Having(
--count(*) < exp_cota_min
--or (count(*) >= nro_validaciones*((100 - exp_porcen)/100)
--and count(*) <= nro_validaciones*((100 + exp_porcen)/100))
--);
--fin de crear vista de expediciones

--DEPRECATED: porque exp_view anterior fue deprecado
--SELECT 'Creando view exp_view_CONTROL...';
--DROP view exp_view_CONTROL;
--create view exp_view_CONTROL as
--select
--exp_filtradas_ARG.fecha,
--exp_filtradas_ARG.dia,
--exp_filtradas_ARG.mes,
--exp_filtradas_ARG.patente,
--exp_filtradas_ARG.servicio,
--exp_filtradas_ARG.sentido,
--exp_filtradas_ARG.nro_expedicion,
--exp_filtradas_ARG.inicio_exp,
--exp_filtradas_ARG.fin_exp,
--exp_filtradas_ARG.inicio_val,
--exp_filtradas_ARG.fin_val,
--exp_filtradas_ARG.rut,
--exp_filtradas_ARG.nro_validaciones as nro_validaciones_exp,
--count(*) as nro_validaciones_val,
--params_1_2_view.exp_porcen,
--params_1_2_view.exp_cota_min,

--CASE when (
--count(*) <= exp_cota_min
--or (count(*) >= nro_validaciones*((100 - exp_porcen)/100)
--and count(*) <= nro_validaciones*((100 + exp_porcen)/100)))
--then 1
--when (
--count(*) > exp_cota_min
--and (count(*) < nro_validaciones*((100 - exp_porcen)/100)
--or count(*) > nro_validaciones* ((100 + exp_porcen)/100))
--) then 0
--else 2
--END as cumple_criterio

--from exp_filtradas_ARG join val_filtradas_ARG
--on (
--val_filtradas_ARG.patente = exp_filtradas_ARG.patente
--)
--join params_1_2_view
--where(
--val_filtradas_ARG.fecha_hora >= exp_filtradas_ARG.inicio_val
--and val_filtradas_ARG.fecha_hora <= exp_filtradas_ARG.fin_val
--)
--group by 
--exp_filtradas_ARG.fecha,
--exp_filtradas_ARG.dia,
--exp_filtradas_ARG.mes,
--exp_filtradas_ARG.patente,
--exp_filtradas_ARG.servicio,
--exp_filtradas_ARG.sentido,
--exp_filtradas_ARG.nro_expedicion,
--exp_filtradas_ARG.inicio_exp,
--exp_filtradas_ARG.fin_exp,
--exp_filtradas_ARG.inicio_val,
--exp_filtradas_ARG.fin_val,
--exp_filtradas_ARG.rut,
--exp_filtradas_ARG.nro_validaciones,
--params_1_2_view.exp_porcen,
--params_1_2_view.exp_cota_min;


--OLD version of control.
--SELECT 'Creando view exp_view_CONTROL...';
--DROP view exp_view_CONTROL_old;
--create view exp_view_CONTROL_old as
--select
--exp_filtradas_ARG.fecha,
--exp_filtradas_ARG.dia,
--exp_filtradas_ARG.mes,
--exp_filtradas_ARG.patente,
--exp_filtradas_ARG.servicio,
--exp_filtradas_ARG.sentido,
--exp_filtradas_ARG.nro_expedicion,
--exp_filtradas_ARG.inicio_exp,
--exp_filtradas_ARG.fin_exp,
--exp_filtradas_ARG.inicio_val,
--exp_filtradas_ARG.fin_val,
--exp_filtradas_ARG.rut,
--exp_filtradas_ARG.nro_validaciones as nro_validaciones_exp,
--count(*) as nro_validaciones_val,
--params_1_2_view.exp_porcen,
--params_1_2_view.exp_cota_min
--from exp_filtradas_ARG join val_filtradas_ARG
--on (
--val_filtradas_ARG.patente = exp_filtradas_ARG.patente
--)
--join params_1_2_view
--where(
--val_filtradas_ARG.fecha_hora >= exp_filtradas_ARG.inicio_val
--and val_filtradas_ARG.fecha_hora <= exp_filtradas_ARG.fin_val
--)
--group by 
--exp_filtradas_ARG.fecha,
--exp_filtradas_ARG.dia,
--exp_filtradas_ARG.mes,
--exp_filtradas_ARG.patente,
--exp_filtradas_ARG.servicio,
--exp_filtradas_ARG.sentido,
--exp_filtradas_ARG.nro_expedicion,
--exp_filtradas_ARG.inicio_exp,
--exp_filtradas_ARG.fin_exp,
--exp_filtradas_ARG.inicio_val,
--exp_filtradas_ARG.fin_val,
--exp_filtradas_ARG.rut,
--exp_filtradas_ARG.nro_validaciones,
--params_1_2_view.exp_porcen,
--params_1_2_view.exp_cota_min
--Having(
--count(*) >= exp_cota_min
--and (count(*) < nro_validaciones*((100 - exp_porcen)/100)
--or count(*) > nro_validaciones*((100 + exp_porcen)/100))
--);
-- Para negar el having
-- A or (B y C)
-- (A or B) y (A or C)
-- not(A or B) or not(A or C)
--(notA y notB) or (notA y notC)
-- notA y (notB or notC)


--Crear vista de validaciones
---Da formato a la vista de validaciones
---Las validaciones correspondientes a expediciones invalidas son descartadas 
--NOTA: Se utiliza como rango de validación los rangos de expedición, porque los rangos de validacion están con problemas y se superponen dejando validaciones que corresponden a 2 expediciones.
SELECT 'Creando view val_view...';
DROP VIEW val_view;
CREATE view val_view as
SELECT
val_filtradas_ARG.fecha,
val_filtradas_ARG.dia,
val_filtradas_ARG.mes,
val_filtradas_ARG.fecha_hora,
val_filtradas_ARG.servicio,
val_filtradas_ARG.sentido,
val_filtradas_ARG.patente,
val_filtradas_ARG.paradero,
val_filtradas_arg.tarjeta,
val_filtradas_arg.ntt
from exp_view join val_filtradas_ARG
on (
val_filtradas_ARG.patente = exp_view.patente
)
where(
exp_view.inicio_exp <= val_filtradas_ARG.fecha_hora and
val_filtradas_ARG.fecha_hora < exp_view.fin_exp
);
--//fin de crear vista de validaciones
SELECT 'Creando view val_view [CREADA]\n####################################\n\n';



--megajoin_list_view, para cada validacion tiene a que expedición corresponde y a que franja horaria corresponde esa expedición.
SELECT 'Creando view megajoin_list_view...';
DROP view megajoin_list_view;
create view megajoin_list_view as
select 
franja_horaria.id as fh_id, franja_horaria.dia, franja_horaria.inicio, franja_horaria.fin, 
exp_view.fecha, exp_view.mes, exp_view.patente, exp_view.inicio_exp, exp_view.fin_exp, exp_view.inicio_val, exp_view.fin_val, exp_view.servicio, exp_view.sentido,
val_view.fecha_hora, val_view.paradero
from exp_view join franja_horaria
on (exp_view.dia = franja_horaria.dia)
join val_view on (
val_view.patente = exp_view.patente)
where substr(exp_view.inicio_exp,12,8) >= franja_horaria.inicio 
and substr(exp_view.inicio_exp,12,8) <= franja_horaria.fin 
and val_view.fecha_hora >= exp_view.inicio_val
and val_view.fecha_hora < exp_view.fin_val;
SELECT 'Creando view megajoin_list_view [CREADA]\n####################################\n\n';

----------Obtener peso de los paraderos----------

--OBTENER PESO de cada paradero, según servicio, sentido y FH.
--pesos_paraderos_temp1, para cada validacion tiene a que expedición corresponde y a que franja horaria corresponde esa expedición.
---Luego grupa las validaciones por: mes, FH, servicio, sentido y paradero, y los cuenta.
---nro_validaciones por paradero, por servicio-sentido, por franja_horaria, por mes.
SELECT 'Creando view pesos_paraderos_temp1...';
DROP VIEW pesos_paraderos_temp1;
create view pesos_paraderos_temp1 as
select mes, fh_id, servicio, sentido, paradero, Count(*) as nro_validaciones
from megajoin_list_view
group by mes, fh_id, servicio, sentido, paradero;
SELECT 'Creando view pesos_paraderos_temp1 [CREADA]\n####################################\n\n';

--pesos_paraderos_temp2, toma pesos_paraderos_temp1 y cuenta el TOTAL de validaciones del servicio-sentido en cada FH, mes.
SELECT 'Creando view pesos_paraderos_temp2...';
DROP VIEW pesos_paraderos_temp2;
create view pesos_paraderos_temp2 as
select mes, fh_id, servicio, sentido, sum(nro_validaciones) as total_validaciones
from pesos_paraderos_temp1
group by mes, fh_id, servicio, sentido;
SELECT 'Creando view pesos_paraderos_temp2 [CREADA]\n####################################\n\n';

--pesos_paradero_view, toma el número de validaciones por paradero en pesos_paraderos_temp1 y lo divide por el total de validaciones de pesos_paraderos_temp2
--fh_id,servicio,sentido,paradero,Peso
SELECT 'Creando view pesos_paradero...';
DROP VIEW pesos_paradero_view;
create view pesos_paradero_view as
select pesos_paraderos_temp1.mes, pesos_paraderos_temp1.fh_id, pesos_paraderos_temp1.servicio, pesos_paraderos_temp1.sentido, pesos_paraderos_temp1.paradero, nro_validaciones/total_validaciones as peso
from pesos_paraderos_temp1 join pesos_paraderos_temp2
on (pesos_paraderos_temp1.mes = pesos_paraderos_temp2.mes
and pesos_paraderos_temp1.fh_id = pesos_paraderos_temp2.fh_id
and pesos_paraderos_temp1.servicio = pesos_paraderos_temp2.servicio
and pesos_paraderos_temp1.sentido = pesos_paraderos_temp2.sentido
);
SELECT 'Creando view pesos_paradero [CREADA]\n####################################\n\n';

--SELECT 'Creando view pesos_paradero_view...';
--DROP TABLE pesos_paradero_view;
--CREATE TABLE pesos_paradero_view(
--mes string,
--fh_id int,
--servicio string,
--sentido string,
--paradero string,
--peso double)
--ROW FORMAT DELIMITED 
--FIELDS TERMINATED BY '\;'
--LOCATION '/user/cloudera/Subus8/PesosParaderoTable/';
--INSERT INTO TABLE pesos_paradero_view select * from pesos_paradero_view;


----------Validaciones Ponderadas----------

--NroValxparaderoxExp
SELECT 'Creando view nro_val_par_view...';
DROP VIEW nro_val_par_view;
create view nro_val_par_view as
select mes, fh_id, fecha,patente,inicio_exp, servicio, sentido, paradero, Count(*) as nro_val_paradero
from megajoin_list_view
group by mes, fh_id, fecha, patente, inicio_exp, servicio, sentido, paradero;
SELECT 'Creando view nro_val_par_view [CREADA]\n####################################\n\n';

--val_pond_par_view: Validaciones Ponderadas (nro_val_par_view*PesoPar) por paradero.
SELECT 'Creando view val_pond_par_view...';
DROP VIEW val_pond_par_view;
create view val_pond_par_view as
select nro_val_par_view.mes, nro_val_par_view.fh_id, nro_val_par_view.fecha, 
nro_val_par_view.patente, nro_val_par_view.inicio_exp, nro_val_par_view.servicio, nro_val_par_view.sentido, 
nro_val_par_view.paradero, nro_val_paradero*Peso as val_ponderada_paradero
from pesos_paradero_view join nro_val_par_view 
on (pesos_paradero_view.mes = nro_val_par_view.mes
and pesos_paradero_view.fh_id = nro_val_par_view.fh_id 
and pesos_paradero_view.servicio = nro_val_par_view.servicio
and pesos_paradero_view.sentido = nro_val_par_view.sentido
and pesos_paradero_view.paradero = nro_val_par_view.paradero);
SELECT 'Creando view val_pond_par_view [CREADA]\n####################################\n\n';



--val_pond_exp_view: Validaciones Ponderadas por Expedición
SELECT 'Creando view val_pond_exp_view...';
DROP VIEW val_pond_exp_view;
create view val_pond_exp_view as
select mes, fh_id, fecha,  patente, inicio_exp, servicio, sentido, sum(val_ponderada_paradero) as validaciones_ponderadas
from val_pond_par_view
group by mes, fh_id, fecha, patente, inicio_exp, servicio,sentido;
SELECT 'Creando view val_pond_exp_view [CREADA]\n####################################\n\n';


----------Número de paraderos detenidos----------
--nro_paradero_detenidos
SELECT 'Creando view nro_par_detenido_view...';
DROP VIEW nro_par_detenido_view;
create view nro_par_detenido_view as
select mes, fh_id, fecha, patente, inicio_exp, servicio, sentido, count(DISTINCT paradero) as nro_paradero_detenido
from megajoin_list_view
group by mes, fh_id, fecha, patente, inicio_exp,servicio, sentido;
SELECT 'Creando view nro_par_detenido_view [CREADA]\n####################################\n\n';


----------Duración de expedición----------
--Calcular duracion_exp_view
SELECT 'Creando view duracion_exp_view...';
DROP VIEW duracion_exp_view;
create view duracion_exp_view as 
Select 
franja_horaria.id as fh_id,
exp_view.fecha,
exp_view.dia,
exp_view.mes,
exp_view.patente,
exp_view.servicio,
exp_view.sentido,
exp_view.nro_expedicion,
exp_view.inicio_exp,
exp_view.fin_exp,
exp_view.inicio_val,
exp_view.fin_val,
exp_view.rut,
exp_view.nro_validaciones_exp,
exp_view.nro_validaciones_val,
(unix_timestamp(exp_view.fin_exp)-unix_timestamp(exp_view.inicio_exp))/60 as duracion_exp
from exp_view join franja_horaria
on (exp_view.dia = franja_horaria.dia)
where substr(exp_view.inicio_exp,12,8) >= franja_horaria.inicio 
and substr(exp_view.inicio_exp,12,8) <= franja_horaria.fin;
SELECT 'Creando view duracion_exp_view [CREADA]\n####################################\n\n';

----------Metricas de expediciones----------

--DEPRECADO: antiguo resumen de expedicion res_exp_view
--Variables para analizar las expediciones
-- fh_id, fecha, servicio, sentido, patente, inicio_exp, fin_exp, inicio_val, fin_val, rut, nro_validaciones, validaciones_ponderadas, --nro_par_detenido_view, duracion_exp_view
--SELECT 'Creando view exp_metrica_view...';
--DROP VIEW exp_metrica_view;
--create view exp_metrica_view as
--select
--val_pond_exp_view.fh_id,
--val_pond_exp_view.fecha,
--substr(val_pond_exp_view.fecha,6,2) as mes,
--val_pond_exp_view.servicio,
--val_pond_exp_view.sentido,
--val_pond_exp_view.patente,
--duracion_exp_view.inicio_exp,
--duracion_exp_view.fin_exp,
--duracion_exp_view.inicio_val,
--duracion_exp_view.fin_val,
--duracion_exp_view.rut,
--duracion_exp_view.nro_validaciones_exp,
--duracion_exp_view.nro_validaciones_val,
--val_pond_exp_view.validaciones_ponderadas,
--nro_par_detenido_view.nro_paradero_detenido,
--duracion_exp_view.duracion_exp
--from val_pond_exp_view join nro_par_detenido_view
--on (val_pond_exp_view.fh_id = nro_par_detenido_view.fh_id
--and val_pond_exp_view.fecha = nro_par_detenido_view.fecha
--and val_pond_exp_view.servicio = nro_par_detenido_view.servicio
--and val_pond_exp_view.sentido = nro_par_detenido_view.sentido
--and val_pond_exp_view.patente = nro_par_detenido_view.patente)
--join duracion_exp_view
--on( val_pond_exp_view.fh_id = duracion_exp_view.fh_id
--and val_pond_exp_view.fecha = duracion_exp_view.fecha
--and val_pond_exp_view.servicio = duracion_exp_view.servicio
--and val_pond_exp_view.sentido = duracion_exp_view.sentido
--and val_pond_exp_view.patente = duracion_exp_view.patente);
--SELECT 'Creando view exp_metrica_view [CREADA]\n####################################\n\n';


----------Crear tabla de la vista exp_metrica_view----------
SELECT 'Creando tabla exp_metrica...';
DROP TABLE exp_metrica;
CREATE TABLE exp_metrica (
fh_id int,
fecha string,
Mes string,
servicio string,
sentido string,
patente string,
inicio_exp string,
fin_exp string,
inicio_val string,
fin_val string,
rut string,
nro_validaciones_exp int,
nro_validaciones_val bigint,
validaciones_ponderadas double,
nro_par_detenido bigint,
duracion_exp double)
ROW FORMAT DELIMITED 
FIELDS TERMINATED BY '\;' 
LOCATION '/user/lloyola/Subus8/ExpedicionMetricaTable/';
SELECT 'Creando tabla exp_metrica [CREADA]\n####################################\n\n';

--Ahora esto se debe hacer desde java con el boton generar resumen de expedicion
--INSERT INTO TABLE exp_metrica select * from exp_metrica_view;

SELECT 'Creando view exp_metrica_view...';
DROP VIEW exp_metrica_view;
create view exp_metrica_view as
select
duracion_exp_view.fh_id,
duracion_exp_view.fecha,
substr(duracion_exp_view.fecha,6,2) as mes,
duracion_exp_view.servicio,
duracion_exp_view.sentido,
duracion_exp_view.patente,
duracion_exp_view.inicio_exp,
duracion_exp_view.fin_exp,
duracion_exp_view.inicio_val,
duracion_exp_view.fin_val,
duracion_exp_view.rut,
duracion_exp_view.nro_validaciones_exp,
duracion_exp_view.nro_validaciones_val,
if (val_pond_exp_view.patente is null, 0, val_pond_exp_view.validaciones_ponderadas) as validaciones_ponderadas,
if (nro_par_detenido_view.patente is null, 0, nro_par_detenido_view.nro_paradero_detenido) as nro_paradero_detenido,
duracion_exp_view.duracion_exp
from duracion_exp_view left join val_pond_exp_view on
(duracion_exp_view.patente = val_pond_exp_view.patente
and duracion_exp_view.inicio_exp = val_pond_exp_view.inicio_exp)
left join nro_par_detenido_view on
(duracion_exp_view.patente = nro_par_detenido_view.patente
and duracion_exp_view.inicio_exp = nro_par_detenido_view.inicio_exp);

SELECT 'Creando view exp_metrica_view [CREADA]\n####################################\n\n';



----------Crear tabla puntos de corte percentil----------
SELECT 'Creando tabla puntos_corte_percentil...';
DROP TABLE puntos_corte_percentil;
CREATE TABLE puntos_corte_percentil (
servicio string,
sentido string,
franja_horaria int,
nro_validaciones_exp_min double,
nro_validaciones_exp_max double,
nro_validaciones_val_min double,
nro_validaciones_val_max double,
validaciones_ponderadas_min double,
validaciones_ponderadas_max double,
nro_par_detenido_min double,
nro_par_detenido_max double,
distancia_tiempo_promedio_min double,
distancia_tiempo_promedio_max double,
duracion_exp_promedio double)
ROW FORMAT DELIMITED 
FIELDS TERMINATED BY '\;'
LOCATION '/user/lloyola/Subus8/PuntosCortePercentilTable/';
SELECT 'Creando tabla puntos_corte_percentil [CREADA]\n####################################\n\n';

--Crear vista clasificacion_exp_view
SELECT 'Creando view clasificacion_exp_view...';
DROP view clasificacion_exp_view;
CREATE view clasificacion_exp_view as
select
exp_metrica.servicio,
exp_metrica.sentido,
exp_metrica.fecha,
exp_metrica.Mes,
exp_metrica.fh_id,
exp_metrica.patente,
exp_metrica.inicio_exp,
exp_metrica.fin_exp,
exp_metrica.inicio_val,
exp_metrica.fin_val,
exp_metrica.rut,
exp_metrica.nro_validaciones_exp,
exp_metrica.nro_validaciones_val,
exp_metrica.validaciones_ponderadas,
exp_metrica.nro_par_detenido,
exp_metrica.duracion_exp,
exp_metrica.duracion_exp - duracion_exp_promedio as dist_tiempo_promedio,

CASE
when (exp_metrica.nro_validaciones_exp <= nro_validaciones_exp_min) then "M"
when (exp_metrica.nro_validaciones_exp >= nro_validaciones_exp_max) then "B"
else "A"
END as clasif_nro_validaciones_exp,

CASE
when (exp_metrica.nro_validaciones_val <= nro_validaciones_val_min) then "M"
when (exp_metrica.nro_validaciones_val >= nro_validaciones_val_max) then "B"
else "A"
END as clasif_nro_validaciones_val,

CASE
when (exp_metrica.validaciones_ponderadas <= validaciones_ponderadas_min) then "M"
when (exp_metrica.validaciones_ponderadas >= validaciones_ponderadas_max) then "B"
else "A"
END as clasif_validaciones_ponderadas,

CASE
when (exp_metrica.nro_par_detenido <= nro_par_detenido_min) then "M"
when (exp_metrica.nro_par_detenido >= nro_par_detenido_max) then "B"
else "A"
END as clasif_nro_par_detenido,

CASE
when (abs(exp_metrica.duracion_exp - duracion_exp_promedio) <= distancia_tiempo_promedio_min) then "B"
when (abs(exp_metrica.duracion_exp - duracion_exp_promedio) >= distancia_tiempo_promedio_max) then "M"
else "A"
END as clasif_dist_tiempo_promedio
from exp_metrica join puntos_corte_percentil
on (exp_metrica.servicio = puntos_corte_percentil.servicio
and exp_metrica.sentido = puntos_corte_percentil.sentido
and exp_metrica.fh_id = puntos_corte_percentil.franja_horaria);
SELECT 'Creando view clasificacion_exp_view [CREADA]\n####################################\n\n';


--Crear tabla clasificacion_exp
SELECT 'Creando tabla clasificacion_exp...';
DROP TABLE clasificacion_exp;
CREATE TABLE clasificacion_exp (
servicio string,
sentido string,
fecha string,
Mes string,
fh_id int,
patente string,
inicio_exp string,
fin_exp string,
inicio_val string,
fin_val string,
rut string,
nro_validaciones_exp int,
nro_validaciones_val bigint,
validaciones_ponderadas double,
nro_par_detenido bigint,
duracion_exp double,
dist_tiempo_promedio double,
clasif_nro_validaciones_exp string,
clasif_nro_validaciones_val string,
clasif_validaciones_ponderadas string,
clasif_nro_par_detenido string,
clasif_dist_tiempo_promedio string)
ROW FORMAT DELIMITED 
FIELDS TERMINATED BY '\;' 
LOCATION '/user/lloyola/Subus8/ClasificacionExpedicionesTable/';
SELECT 'Creando tabla clasificacion_exp [CREADA]\n####################################\n\n';

--INSERT INTO TABLE clasificacion_exp select * from clasificacion_exp_view;


--Vista con menos columnas de la tabla "clasificacion_exp"
SELECT 'Creando vista clasificacion_exp_corta_view...';
DROP view clasificacion_exp_corta_view;
CREATE view clasificacion_exp_corta_view as
select servicio, sentido, fh_id, patente, rut, nro_Validaciones_exp, clasif_nro_validaciones_exp, validaciones_ponderadas, clasif_validaciones_ponderadas,nro_par_detenido,clasif_nro_par_detenido, duracion_exp, clasif_dist_tiempo_promedio from clasificacion_exp;
SELECT 'Creando vista clasificacion_exp_corta_view [CREADA]\n####################################\n\n';


--Vista con números para la clasificación de la tabla "clasificacion_exp"
SELECT 'Creando vista clasificacion_exp_num_view...';
DROP view clasificacion_exp_num_view;
CREATE view clasificacion_exp_num_view as
select servicio, sentido, fecha, Mes, fh_id, patente, inicio_exp, fin_exp, inicio_val, fin_val, rut, nro_validaciones_exp, nro_validaciones_val, validaciones_ponderadas, nro_par_detenido, duracion_exp, dist_tiempo_promedio,
CASE
when clasif_nro_validaciones_exp="M" then 1
when clasif_nro_validaciones_exp="A" then 2
WHEN clasif_nro_validaciones_exp="B" then 3
else 0
END as clasif_nro_validaciones_exp,

CASE
when clasif_nro_validaciones_val="M" then 1
when clasif_nro_validaciones_val="A" then 2
WHEN clasif_nro_validaciones_val="B" then 3
else 0
END as clasif_nro_validaciones_val, 

CASE
when clasif_validaciones_ponderadas="M" then 1
when clasif_validaciones_ponderadas="A" then 2
WHEN clasif_validaciones_ponderadas="B" then 3
else 0
END as clasif_validaciones_ponderadas, 

CASE
when clasif_nro_par_detenido="M" then 1
when clasif_nro_par_detenido="A" then 2
WHEN clasif_nro_par_detenido="B" then 3
else 0
END as clasif_nro_par_detenido, 

CASE
when clasif_dist_tiempo_promedio="M" then 1
when clasif_dist_tiempo_promedio="A" then 2
WHEN clasif_dist_tiempo_promedio="B" then 3
else 0
END as clasif_dist_tiempo_promedio
from clasificacion_exp;
SELECT 'Creando vista clasificacion_exp_num_view [CREADA]\n####################################\n\n';

--Vista clasif_exp_conteo_view. Cuenta cuantas M,A y B tiene una expedición.
-- Variables usadas: nro_val y dist_tiempo_prom
SELECT 'Creando vista clasif_exp_conteo_view...';
DROP view clasif_exp_conteo_view;
CREATE view clasif_exp_conteo_view as
select servicio, sentido, fecha, Mes, fh_id, patente, inicio_exp, fin_exp, inicio_val, fin_val, rut, nro_validaciones_exp, nro_validaciones_val, validaciones_ponderadas, nro_par_detenido, duracion_exp,
dist_tiempo_promedio,
clasif_nro_validaciones_exp,
clasif_nro_validaciones_val, 
clasif_validaciones_ponderadas, 
clasif_nro_par_detenido, 
clasif_dist_tiempo_promedio,

length(regexp_replace(concat(clasif_nro_validaciones_exp, clasif_dist_tiempo_promedio), "A|B","")) as cantidad_M,
length(regexp_replace(concat(clasif_nro_validaciones_exp, clasif_dist_tiempo_promedio), "M|B","")) as cantidad_A,
length(regexp_replace(concat(clasif_nro_validaciones_exp, clasif_dist_tiempo_promedio), "M|A","")) as cantidad_B

from clasificacion_exp;
SELECT 'Creando vista clasif_exp_conteo_view [CREADA]\n####################################\n\n';


--Vista clasif_exp_conteo_corto_view.
SELECT 'Creando vista clasif_exp_conteo_corto_view...';
DROP VIEW clasif_exp_conteo_corto_view;
CREATE view clasif_exp_conteo_corto_view as
select servicio, sentido, fecha, fh_id, patente, rut, nro_validaciones_exp, nro_validaciones_val, validaciones_ponderadas, nro_par_detenido, duracion_exp, dist_tiempo_promedio
clasif_nro_validaciones_exp,
clasif_nro_validaciones_val, 
clasif_validaciones_ponderadas, 
clasif_nro_par_detenido, 
clasif_dist_tiempo_promedio
cantidad_M,
cantidad_A,
cantidad_B
from clasif_exp_conteo_view;
SELECT 'Creando vista clasif_exp_conteo_corto_view [CREADA]\n####################################\n\n';


SELECT 'Creando vista conteo_MAB_conductor_view...';
DROP VIEW conteo_MAB_conductor_view;
CREATE VIEW conteo_MAB_conductor_view as
select RUT,
count(*) as cant_exp,
sum(cantidad_M) as M, 
sum(cantidad_A) as A, 
sum(cantidad_B) as B,
sum(cantidad_M) / (sum(cantidad_M) + sum(cantidad_A) + sum(cantidad_B) )*100 as M_porc, 
sum(cantidad_A) / (sum(cantidad_M) + sum(cantidad_A) + sum(cantidad_B) )*100 as A_porc,  
sum(cantidad_B) / (sum(cantidad_M) + sum(cantidad_A) + sum(cantidad_B) )*100 as B_porc
from clasif_exp_conteo_view
where servicio!='(Indeterminado)'
group by rut;
SELECT 'Creando vista conteo_MAB_conductor_view [CREADA]\n####################################\n\n';


--select count(case Position when 'something to count' then 1 else null end)
SELECT 'Creando vista Ranking_MAB_view...';
DROP VIEW Ranking_MAB_view;
CREATE VIEW Ranking_MAB_view as
select RUT,
count(*) as cant_exp,

count (CASE clasif_nro_validaciones_exp when 'M' then 1 else null END) as M_nro_val,
count (CASE clasif_nro_validaciones_exp when 'A' then 1 else null END) as A_nro_val,
count (CASE clasif_nro_validaciones_exp when 'B' then 1 else null END) as B_nro_val,
round((count (CASE clasif_nro_validaciones_exp when 'M' then 1 else null END) / count(*) )*100 ,2) as M_nro_val_porc,
round((count (CASE clasif_nro_validaciones_exp when 'A' then 1 else null END) / count(*) )*100 ,2) as A_nro_val_porc,
round((count (CASE clasif_nro_validaciones_exp when 'B' then 1 else null END) / count(*) )*100 ,2) as B_nro_val_porc,

count (CASE clasif_dist_tiempo_promedio when 'M' then 1 else null END) as M_dist_tiempo_promedio,
count (CASE clasif_dist_tiempo_promedio when 'A' then 1 else null END) as A_dist_tiempo_promedio,
count (CASE clasif_dist_tiempo_promedio when 'B' then 1 else null END) as B_dist_tiempo_promedio,
round((count (CASE clasif_dist_tiempo_promedio when 'M' then 1 else null END) / count(*) )*100 ,2) as M_dist_tiempo_promedio_porc,
round((count (CASE clasif_dist_tiempo_promedio when 'A' then 1 else null END) / count(*) )*100 ,2) as A_dist_tiempo_promedio_porc,
round((count (CASE clasif_dist_tiempo_promedio when 'B' then 1 else null END) / count(*) )*100 ,2) as B_dist_tiempo_promedio_porc

from clasificacion_exp
where servicio!='(Indeterminado)'
group by rut;
SELECT 'Creando vista Ranking_MAB_view [CREADA]\n####################################\n\n';


--Crear tabla de conteo_MAB_conductor
SELECT 'Creando vista conteo_MAB_conductor...';
DROP TABLE conteo_MAB_conductor;
CREATE table conteo_MAB_conductor (
rut string,
cant_exp int,
M double,
A double,
B double,
M_porc  double,
A_porc  double,
B_porc double)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY '\;'
LOCATION '/user/lloyola/Subus8/ConteoMABConductorTable/';
SELECT 'Creando vista conteo_MAB_conductor [CREADA]\n####################################\n\n';

--INSERT INTO TABLE conteo_MAB_conductor select * from conteo_MAB_conductor_view;


----------Other Functions----------

--GetValPorPar ($Patente, $InicioVal, $FinVal)
--//Solo para 1 expedición
--CREATE VIEW ValPorPar as 
--with ValPorPar as (
--Select nombre_paradero1, from_unixtime(floor(avg(unix_timestamp(fecha)))) as fechahora, count (*) as nroVal
--From validaciones
--Where fecha >= $InicioVal and
--Fecha <= $FinVal and
--Patente = $Patente
--Group by nombre_paradero1
--)
--select * from ValPorPar order by fechahora;




show tables;

-- insert overwrite local directory '/some/place/'
-- row format delimited
-- fields terminated by '\;'
-- query


