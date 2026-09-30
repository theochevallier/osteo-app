## Pour faire pointer la bonne version de Java dans variable d'environnement JAVA_HOME
export JAVA_HOME=$(/usr/libexec/java_home -v 21)

## Build et lancer le projet : 
mvn clean javafx:run

## Clean le projet (css)
mvn clean compile
mvn clean package

## Générer un exécutable .jar :
mvn clean package

## Générer le .app :
jpackage \
--type app-image \
--name Ostheo \
--input target \
--main-jar Ostheo_projet-1.0-SNAPSHOT.jar \
--main-class org.example.ostheo_projet.Launcher \
--java-options "-Xmx1024m" \
--mac-package-identifier com.theo.ostheo
--runtime-image "$(dirname $(dirname $(/usr/libexec/java_home -v 21)))" (à vérifier que ça fonctionne, pour que Julie ait pas forcément à installer Java 21)

## Générer le .dmg :
jpackage \
--type dmg \
--name Ostheo \
--input target \
--main-jar Ostheo_projet-1.0-SNAPSHOT.jar \
--main-class org.example.ostheo_projet.Launcher \
--class-path "lib/*" \
--mac-package-identifier com.theo.ostheo \
--runtime-image "$(dirname $(dirname $(/usr/libexec/java_home -v 21)))"

## Configurer la connexion à la base de données
modifier le fichier ostheo.app/Contents/App/Classes/hibernate.cfg.xml

## Sauvegarde SQL de toute la BDD (schéma + BDD)
pg_dump -h localhost -p 5432 -U ostheo_user ostheo_db > /Users/Theo/Desktop/database.sql

## Créer les schémas d'une BDD vide à partir du fichier de sauvegarde et insérer les données
psql test_db < database.sql

## Sauvegarde SQL des données uniquement
pg_dump \
-U ostheo_user \
-d ostheo_db \
--data-only \
--column-inserts \
> data.sql

## Insérer les données dans une BDD avec les schémas créés
psql \
-U ostheo_user \
-d ostheo_db_test \
-f data.sql

## Donner tous les privilèges à un user et accès aux tables et séquences (après commande psql test_db)
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO my_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO my_user;
GRANT ALL PRIVILEGES ON DATABASE my_database TO my_user;

## Supprimer des doublons
WITH CTE AS (
SELECT ID, Name, ROW_NUMBER() OVER (PARTITION BY Name ORDER BY ID ASC) AS RowNum
FROM customers
)
-- Select only the unique records where RowNum = 1
SELECT ID, Name
FROM CTE
WHERE RowNum = 1;

## Pour Hibernate
Si il y a un type non natif dans un modele, comme une enum, il faut rajouter : @Enumerated(EnumType.STRING) pour que hibernate sache que c'est un string qui est inséré

## Raccourcis Intellij
Cmd Option L : reformater tout le fichier

ctrl^ I : implémenter méthodes interface

System out println : sout + tab
soutm : Prints current class and method names to System.out
soutp : Prints method parameter names and values to System.out
soutv : Prints a value to System.out


## Astuces CSS
Les VBox vont aligner les éléments verticalement, HBox horizontalement.
Pour mettre du padding, on peut mettre juste apres l'ouverture de la balise VBox ou HBox :
<padding><Insets top="20" left="10" right="10" bottom="20"/></padding>

Si on veut pousser un élément en bas, on peut faire :
<Region VBox.vgrow="ALWAYS"/>

Pour le pousser à droite : 
<Region HBox.hgrow="ALWAYS"/>

Pour ajouter x pixels en bas d'un bouton par exemple (au sein des deux balises <Button> et </Button>) :
<VBox.margin>
    <Insets bottom="20"/>
</VBox.margin>

Pour aligner les éléments d'un grid pane, utiliser les propriétés halignment et valignment :
<Label text="Label 1" GridPane.rowIndex="0" GridPane.columnIndex="0" GridPane.halignment="CENTER" GridPane.valignment="CENTER"/>
Ou sur la row constraint directement :
<RowConstraints vgrow="ALWAYS" halignment="CENTER" valignment="CENTER"/>

AnchorPane : pour faire du positionnement absolu, on peut utiliser les propriétés topAnchor, leftAnchor, rightAnchor et bottomAnchor :
Si je met un HBox dans un AnchorPane avec propriétés topAnchor="10" leftAnchor="10", alors le HBox sera positionné à 10 pixels du haut et 10 pixels de la gauche de l'AnchorPane.
Si je précise aussi rightAnchor="10", alors le HBox s'étirera pour occuper tout l'espace entre la gauche et la droite, tout en restant à 10 pixels de chaque côté.

Le pane Flowpane ne donne pas par défaut la largeur max à ses enfants, on peut donc binder sa prefWidth avec celle de son conteneur parent :
fieldInFlowPane.prefWidthProperty().bind(parentVBox.widthProperty());

## Sauvegarde automatique BDD
Lien expressions cron : https://www.uptimia.com/fr/cron/every-friday

Commandes :
Donner les droits d'exécution au script :
chmod +x /Users/Theo_1/Desktop/script_backup.sh
Ouvrir le crontab pour l'édition :
crontab -e
Touche i pour passer en mode insertion
Ajouter la ligne suivante pour faire une sauvegarde tous les jours à 2h du matin :
* 2 * * * /Users/Theo_1/Desktop/script_backup.sh >> /tmp/backup.log 2>&1
Touche esc pour sortir du mode insertion, puis :x pour enregistrer et quitter

Si message de log "Operation not permitted" :
Aller dans les préférences système, Sécurité et confidentialité, Accès complet au disque, cliquer sur la croix
Puis touches cmd + shift + G, entrer "/usr/sbin", puis double cliquer fichier cron