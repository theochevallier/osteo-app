echo "Compiling the project..."
mvn clean package

# Delete .app file if it exists
echo "Removing existing Ostheo.app..."
rm -rf Ostheo.app

# Generate .app :
echo "Generating the .app file..."
jpackage \
--type app-image \
--name Ostheo \
--input target \
--main-jar Ostheo_projet-1.0-SNAPSHOT.jar \
--main-class org.example.ostheo_projet.Launcher \
--java-options "-Xmx1024m" \
--mac-package-identifier com.theo.ostheo

echo "Ostheo.app generated successfully."

# Copy .app and scripts/SQL files to the folder ready to be exported
rm -rf /Users/Theo/Documents/Ostheo/Installation
mkdir /Users/Theo/Documents/Ostheo/Installation
cp -R scripts /Users/Theo/Documents/Ostheo/Installation
cp -R SQL /Users/Theo/Documents/Ostheo/Installation
cp -R Ostheo.app /Users/Theo/Documents/Ostheo/Installation

echo ".app file and scripts copied to /Users/Theo/Documents/Ostheo/Installation folder."