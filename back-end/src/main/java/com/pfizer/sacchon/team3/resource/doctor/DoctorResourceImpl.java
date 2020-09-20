package com.pfizer.sacchon.team3.resource.doctor;

import com.pfizer.sacchon.team3.exception.BadEntityException;
import com.pfizer.sacchon.team3.exception.NotFoundException;
import com.pfizer.sacchon.team3.model.Doctors;
import com.pfizer.sacchon.team3.repository.DoctorRepository;
import com.pfizer.sacchon.team3.repository.util.JpaUtil;
import com.pfizer.sacchon.team3.representation.DoctorRepresentation;
import com.pfizer.sacchon.team3.resource.util.ResourceValidator;
import com.pfizer.sacchon.team3.security.ResourceUtils;
import com.pfizer.sacchon.team3.security.Shield;
import org.restlet.engine.Engine;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DoctorResourceImpl extends ServerResource implements DoctorResource {
    public static final Logger LOGGER = Engine.getLogger(DoctorResourceImpl.class);
    private long id;
    private DoctorRepository doctorRepository;

    @Override
    protected void doInit() {
        LOGGER.info("Initialising doctor resource starts");
        try {
            doctorRepository = new DoctorRepository(JpaUtil.getEntityManager());
            id = Long.parseLong(getAttribute("id"));
        } catch (Exception e) {
            id = -1;
        }
        LOGGER.info("Initialising doctor resource ends");
    }

    // GET a doctor
    @Override
    public DoctorRepresentation getDoctor() throws NotFoundException {
        LOGGER.info("Retrieve a doctor");

        // Check authorization
        ResourceUtils.checkRole(this, Shield.ROLE_DOCTOR);

        // Initialize the persistence layer.
        DoctorRepository doctorRepository = new DoctorRepository(JpaUtil.getEntityManager());
        Doctors doctor;
        try {
            Optional<Doctors> opDoctor = doctorRepository.findById(id);
            setExisting(opDoctor.isPresent());
            if (!isExisting()) {
                LOGGER.config("doctor id does not exist:" + id);
                throw new NotFoundException("No doctor with  : " + id);
            } else {
                doctor = opDoctor.get();
                LOGGER.finer("User allowed to retrieve a doctor.");
                DoctorRepresentation result = new DoctorRepresentation(doctor);
                LOGGER.finer("Doctor successfully retrieved");

                return result;
            }
        } catch (Exception ex) {
            throw new ResourceException(ex);
        }
    }

    // DELETE Doctor
    @Override
    public void remove() throws NotFoundException {
        LOGGER.finer("Removal of doctor");
        ResourceUtils.checkRole(this, Shield.ROLE_DOCTOR);
        LOGGER.finer("User allowed to remove a doctor.");

        try {
            Boolean isDeleted = doctorRepository.remove(id);
            if (!isDeleted) {
                LOGGER.config("Doctor id does not exist");
                throw new NotFoundException("Doctor with the following identifier does not exist:" + id);
            }
            LOGGER.finer("Doctor successfully removed.");
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "Error when removing a doctor", ex);
            throw new ResourceException(ex);
        }
    }

    // UPDATE Doctor
    @Override
    public DoctorRepresentation store(DoctorRepresentation doctorReprIn) throws NotFoundException, BadEntityException {
        LOGGER.finer("Update a doctor.");
        ResourceUtils.checkRole(this, Shield.ROLE_DOCTOR);
        LOGGER.finer("User allowed to update a doctor.");
        // Check given entity
        ResourceValidator.notNull(doctorReprIn);
        ResourceValidator.validateDoctor(doctorReprIn);
        LOGGER.finer("Doctor checked");

        try {
            // Convert DoctorRepresentation to Doctor
            Doctors doctorIn = doctorReprIn.createDoctor();
            doctorIn.setId(id);
            Optional<Doctors> doctorOut;
            Optional<Doctors> oDoctor = doctorRepository.findById(id);
            setExisting(oDoctor.isPresent());
            // If doctor exists, we update it.
            if (isExisting()) {
                LOGGER.finer("Update doctor.");
                // Update doctor in DB and retrieve the new one.
                doctorOut = doctorRepository.update(doctorIn);
                // Check if retrieved doctor is not null : if it is null it
                // means that the id is wrong.
                if (!doctorOut.isPresent()) {
                    LOGGER.finer("Doctor does not exist.");
                    throw new NotFoundException("Doctor with the following id does not exist: " + id);
                }
            } else {
                LOGGER.finer("Resource does not exist.");
                throw new NotFoundException("Doctor with the following id does not exist: " + id);
            }
            LOGGER.finer("Doctor successfully updated.");
            return new DoctorRepresentation(doctorOut.get());
        } catch (Exception ex) {
            throw new ResourceException(ex);
        }
    }
}