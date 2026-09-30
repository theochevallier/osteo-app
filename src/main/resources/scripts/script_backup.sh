#!/bin/bash

PG_HOST=localhost
PG_PORT=5432
PG_USER=ostheo_user
DB_NAME=ostheo_db

SAVEDATE=$(date +"%d-%m-%y")
BACKUPDIR=$1
FILENAME="$DB_NAME"_$(date +"%H_%M_%S")_backup.sql

#Create directory
mkdir -p "$BACKUPDIR""$SAVEDATE"


# delete old backups

NUMBACKUPS=$(ls -d "$BACKUPDIR"* 2>/dev/null | wc -l)

echo "$NUMBACKUPS existing backups"

if [ "$NUMBACKUPS" -gt 2 ]; then

echo " REMOVING OLD BACKUPS"

# sort -r : sort in reverse order, tail -n +3 : skip the first 2 lines (the 2 most recent backups)
ls -d $BACKUPDIR*/ 2>/dev/null | sort -r | tail -n +3 | while read REMOVEBACKUP; do

echo " REMOVING BACKUP $REMOVEBACKUP"

rm -rf "$REMOVEBACKUP"

done

fi

echo "Backing up database..."

pg_dump -h $PG_HOST -p $PG_PORT -U $PG_USER $DB_NAME > "$BACKUPDIR""$SAVEDATE"/"$FILENAME"

echo "Backup complete and saved to $BACKUPDIR$SAVEDATE/$FILENAME"