#!/bin/bash

PG_HOST=localhost
PG_PORT=5432
PG_USER=ostheo_user
DB_NAME=ostheo_db

DB_FILE=$1

if [ -z "$DB_FILE" ]; then
  echo "Argument avec fichier de restauration manquant, usage: $0 <backup_file.sql>"
  exit 1
fi

# Perform a backup of the database
echo "Backup of current database before restoration..."

/Users/Theo_1/Desktop/script_backup.sh

echo "Restoring database from $DB_FILE..."

psql -h $PG_HOST -p $PG_PORT -U $PG_USER -d $DB_NAME -f "$DB_FILE"

echo "Database restoration complete from $DB_FILE"
