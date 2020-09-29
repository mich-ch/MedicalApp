package com.pfizer.sacchon.team3.resource.patient;

import com.pfizer.sacchon.team3.exception.BadEntityException;
import com.pfizer.sacchon.team3.model.PatientRecords;
import com.pfizer.sacchon.team3.model.Patients;
import com.pfizer.sacchon.team3.repository.PatientRecordRepository;
import com.pfizer.sacchon.team3.repository.PatientRepository;
import com.pfizer.sacchon.team3.repository.util.JpaUtil;
import com.pfizer.sacchon.team3.representation.PatientRecordRepresentation;
import com.pfizer.sacchon.team3.representation.PatientRepresentation;
import com.pfizer.sacchon.team3.representation.ResponseRepresentation;
import com.pfizer.sacchon.team3.resource.util.ResourceValidator;
import org.restlet.data.Status;
import org.restlet.engine.Engine;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class PatientRecordsListImpl extends ServerResource implements PatientRecordsList {
    public static final Logger LOGGER = Engine.getLogger(PatientRecordsListImpl.class);
    private PatientRecordRepository patientRecordRepository;
    private PatientRepository patientRepository;
    long id;

    @Override
    protected void doInit() {
        LOGGER.info("Initialising patient record resource L starts");
        try {
            patientRecordRepository = new PatientRecordRepository(JpaUtil.getEntityManager());
            patientRepository = new PatientRepository(JpaUtil.getEntityManager());
            id = Long.parseLong(getAttribute("id"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        LOGGER.info("Initialising patient record resource L ends");
    }

    @Override
    public ResponseRepresentation<List<PatientRecordRepresentation>> getAllPatientRecords() {
        LOGGER.finer("Select all records.");
        try {
            List<PatientRecords> patientRecords = patientRecordRepository.findPatientRecordsByPatient(id);
            List<PatientRecordRepresentation> result = new ArrayList<>();

            for (PatientRecords p : patientRecords)
                result.add(new PatientRecordRepresentation(p));

            return new ResponseRepresentation<>(200, "Records retrieved", result);
        } catch (Exception e) {
            return new ResponseRepresentation<>(404, "Records not found", null);
        }
    }

    @Override
    public ResponseRepresentation<PatientRecordRepresentation> storeData(PatientRecordRepresentation patientRecordRepresentation) {
        LOGGER.finer("Add a new record.");
        if (patientRecordRepresentation.getCarbs() == 0 || patientRecordRepresentation.getGlycose() == 0)
            return new ResponseRepresentation<>(422, "Bad Entity", null);
        // get patient
        Optional<Patients> opatient = patientRepository.findById(id);
        if (opatient.isPresent()) {
            // Check entity
            // Convert to PatientRepr so a validation can procceed
            Patients patient = opatient.get();
            PatientRepresentation pr = new PatientRepresentation();
            pr.setLastName(patient.getLastName());
            pr.setFirstName(patient.getFirstName());
            try {
                ResourceValidator.notNull(patientRecordRepresentation);
                ResourceValidator.validatePatient(pr);
            } catch (BadEntityException ex) {
                return new ResponseRepresentation<>(422, "Bad Entity", null);
            }

            LOGGER.finer("Patient checked");

            try {
                // Convert PatientRecordRepr to PatientRecord
                PatientRecords patientRecordsIn = new PatientRecords();
                patientRecordsIn.setGlycose(patientRecordRepresentation.getGlycose());
                patientRecordsIn.setCarbs(patientRecordRepresentation.getCarbs());
                patientRecordsIn.setTimeCreated(patientRecordRepresentation.getTimeCreated());
                patientRecordsIn.setPatient(patient);

                // Check if patient can add PatientRecord
                boolean lastConsultationLessThanAMonthAgo = patientRepository.checkLastConsultation(patientRecordsIn, patient.getConsultations());
                boolean recordTimeMoreRecentThanPatientsCreationTime = patientRepository.checkPatientsCreationTime(patientRecordsIn, patient.getTimeCreated());

                if (lastConsultationLessThanAMonthAgo && recordTimeMoreRecentThanPatientsCreationTime) {
                    Optional<PatientRecords> patientRecordsOut = patientRecordRepository.save(patientRecordsIn);
                    PatientRecords patientRecords = null;
                    if (patientRecordsOut.isPresent()) {
                        patientRecords = patientRecordsOut.get();
                        // change patients last activity field
                        patient.setLastActive(new Date());
                        patientRepository.update(patient);
                    } else
                        return new ResponseRepresentation<>(404, "Record not found", null);

                    //Convert PatientRecord to PatientRecordRepr
                    PatientRecordRepresentation result = new PatientRecordRepresentation();
                    result.setGlycose(patientRecords.getGlycose());
                    result.setCarbs(patientRecords.getCarbs());
                    result.setTimeCreated(patientRecords.getTimeCreated());
                    result.setId(patientRecords.getId());

                    getResponse().setLocationRef("http://localhost:9000/v1/patient/" + patient.getId() + "/storeData/patientRecord/" + patientRecords.getId());
                    getResponse().setStatus(Status.SUCCESS_CREATED);

                    LOGGER.finer("Record successfully added.");

                    return new ResponseRepresentation<>(404, "Record created", result);
                } else {
                    return new ResponseRepresentation<>(422, "Bad Record Date", null);
                }

            } catch (Exception ex) {
                throw new ResourceException(ex);
            }
        } else {
            return new ResponseRepresentation<>(404, "Not found", null);
        }
    }
}
