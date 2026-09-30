#!/bin/bash

PG_HOST=localhost
PG_PORT=5432
PG_USER=ostheo_user
DB_NAME=ostheo_db

DIRECTORY_FILES=$1

if [ -z "$DIRECTORY_FILES" ]; then
  echo "Argument avec répertoire des fichiers d'importation manquant, usage: $0 <directory_path>"
  exit 1
fi

echo "Creating import tables if they doesn't exist..."

result=$(psql -h $PG_HOST -p $PG_PORT -U $PG_USER -d $DB_NAME -a -f 'SQL/create_table_import.sql')
echo "$result"

echo "Importing CSV files from directory: $DIRECTORY_FILES"

## Insert patient csv file into patient_import table
psql -h $PG_HOST -p $PG_PORT -U $PG_USER -d $DB_NAME <<EOF
\copy patient_import FROM '$DIRECTORY_FILES/infopatient.csv' DELIMITER ';' CSV HEADER;
EOF

## Insert medical history csv file
psql -h $PG_HOST -p $PG_PORT -U $PG_USER -d $DB_NAME <<EOF
\copy medical_history_import FROM '$DIRECTORY_FILES/infomedicale.csv' DELIMITER ';' CSV HEADER;
EOF

## Insert medical csv file
psql -h $PG_HOST -p $PG_PORT -U $PG_USER -d $DB_NAME <<EOF
\copy treatment_import FROM '$DIRECTORY_FILES/info-traitements.csv' DELIMITER ';' CSV HEADER;
EOF

echo "Data imported in temp tables from csv files."

result=$(psql -h $PG_HOST -p $PG_PORT -U $PG_USER -d $DB_NAME -a -f 'SQL/insert_table_import.sql')
echo "Retour insert script : " "$result"

echo "Data inserted in main tables from temp tables."

#/Users/Theo_1/Desktop/script_backup.sh


