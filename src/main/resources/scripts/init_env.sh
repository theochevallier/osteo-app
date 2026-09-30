echo "Installing PostgreSQL 16..."
brew install postgresql@16

echo "Starting PostgreSQL service..."
brew services start postgresql@16

echo "Setting environment variables for PostgreSQL..."
echo 'export PATH="/opt/homebrew/opt/postgresql@16/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc

echo "Verify PostgreSQL installation :"
psql --version

echo "Création utilisateur SQL..."

psql postgres <<EOF
CREATE USER test_install WITH PASSWORD 'root';
CREATE DATABASE ostheo_db OWNER test_install;
GRANT ALL ON SCHEMA public TO test_install;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO test_install;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO test_install;
EOF

