#!/bin/bash
#Este script agrega los archivos de expediciones, validaciones, paraderos y franjas horarias al HDFS.
#Configure las rutas de origen de los archivos.
#/home/cloudera/Desktop/CarpetaInstalacion/
hdfs dfs -mkdir -p /user/lloyola/ParaderoTable/
hdfs dfs -mkdir -p /user/lloyola/Subus8/
hdfs dfs -mkdir -p /user/lloyola/Subus8/ExpedicionTable/
hdfs dfs -mkdir -p /user/lloyola/Subus8/ValidacionTable/
hdfs dfs -mkdir -p /user/lloyola/Subus8/FranjaHorariaTable/

echo 'Cargando Paraderos'
hdfs dfs -put /home/cloudera/Desktop/CarpetaInstalacion/Archivos/Paraderos.csv /user/lloyola/ParaderoTable/Paraderos.csv

echo 'Cargando Expediciones'
hdfs dfs -put /home/cloudera/Desktop/CarpetaInstalacion/Archivos/Expediciones/Expediciones_Anonimas_Agosto_2016.csv /user/lloyola/Subus8/ExpedicionTable/Expediciones_Anonimas_Agosto_2016.csv

echo 'Cargando Validaciones'
hdfs dfs -put /home/cloudera/Desktop/CarpetaInstalacion/Archivos/Validaciones/Validaciones_Agosto_2016.csv  /user/lloyola/Subus8/ValidacionTable/Validaciones_Agosto_2016.csv

echo 'Cargando FranjaHoraria'
hdfs dfs -put /home/cloudera/Desktop/CarpetaInstalacion/Archivos/FranjaHoraria.csv /user/lloyola/Subus8/FranjaHorariaTable/FranjaHoraria.csv

hdfs dfs -ls /user/lloyola/Subus8/ExpedicionTable/
hdfs dfs -ls /user/lloyola/ParaderoTable/
hdfs dfs -ls /user/lloyola/Subus8/ValidacionTable/
hdfs dfs -ls /user/lloyola/Subus8/FranjaHorariaTable/

