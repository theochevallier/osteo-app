CREATE TABLE IF NOT EXISTS patient_import (
    adresse text,
    CodePostalVille text,
    DateNaissance text,
    email text,
    ID_Patient text PRIMARY KEY,
    Nom text,
    "Prénom" text,
    profession text,
    téléphone text
);

TRUNCATE TABLE patient_import;


CREATE TABLE IF NOT EXISTS medical_history_import (
    "Examens complémentaires" text,
    Histoire_Médicale text,
    ID_DossierMedical text,
    TTTMedic text
);

TRUNCATE TABLE medical_history_import;

create table if not exists treatment_import (
    ID_DossierMedical text,
    ID_TTT text,
    Mode_Paiement text,
    Montant text,
    "Motif consultation" text,
    Note_remarque text,
    traitement text
);

truncate table treatment_import;