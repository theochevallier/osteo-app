
-- FOR TEST, delete imported patient
-- TODO : remove when deploying
delete from patient where contraindication is null;
delete from consultation where consultation_date = '2026-08-27';

-- Insert patient massively
insert into patient(lastname, firstname, birthday, address, email, mobile_phone, is_english, bruxisme,
                    osteoporose, oil_allergy, canceled_appointment, genre)
select nom, "Prénom", coalesce(to_date(datenaissance, 'dd/MM/YYYY'), '1900-01-01'),
       adresse, email, left(téléphone, 30), false, false, 'AUCUN',
       false, id_patient, 'AUCUN'
from patient_import
where nom is not null OR "Prénom" is not null;


-- Insert medical history in medical imported history and histoire medicale in medical_history
update patient p
set medical_imported_history = m."Examens complémentaires",
                   medical_history = m.histoire_médicale || $$
                       $$ || m.tttmedic
from medical_history_import m
where p.canceled_appointment = m.id_dossiermedical;


-- Update the city id by tryind to find the entity with the codepostalville from patient_import
update patient p
set city_id = c.id
from patient_import pai
left join city c on c.postal_code = substring(codepostalville, 1, 5)
and c.name ilike '%' || substring(codepostalville, 7) || '%'
where p.canceled_appointment = pai.id_patient;

-- Insert the old patients treatments
insert into consultation(patient_id, reason, note, treatment, practitioner_id, is_ibclc_relocation,
                         is_podologue_relocation, is_orthoptiste_relocation, is_orthodontiste_relocation,
                         is_doctor_relocation, is_orthophoniste_relocation, is_dentiste_relocation)
select p.id, t."Motif consultation", t.note_remarque, t.traitement,
       (select id from practitioner limit 1), false, false, false,
       false, false, false, false
from treatment_import t
inner join patient p on p.canceled_appointment = t.id_dossiermedical
where t."Motif consultation" is not null or t.note_remarque is not null or t.traitement is not null


-- truncate canceled aptmt column