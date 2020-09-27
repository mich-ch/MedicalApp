package com.pfizer.sacchon.team3.resource.userAuth.login;

import com.pfizer.sacchon.team3.exception.NotFoundException;
import com.pfizer.sacchon.team3.exception.WrongCredentials;
import com.pfizer.sacchon.team3.model.Doctors;
import com.pfizer.sacchon.team3.repository.DoctorRepository;
import com.pfizer.sacchon.team3.repository.util.JpaUtil;
import com.pfizer.sacchon.team3.representation.DoctorRepresentation;
import com.pfizer.sacchon.team3.representation.LoginRepresentation;
import com.pfizer.sacchon.team3.security.ResourceUtils;
import com.pfizer.sacchon.team3.security.Shield;
import org.restlet.engine.Engine;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import java.util.Optional;
import java.util.logging.Logger;

public class LoginDoctorImpl extends ServerResource implements LoginDoctor {
    public static final Logger LOGGER = Engine.getLogger(LoginDoctorImpl.class);
    private DoctorRepository doctorRepository;

    @Override
    protected void doInit() {
        LOGGER.info("Initialising doc resource starts");
        try {
            doctorRepository = new DoctorRepository(JpaUtil.getEntityManager());
        } catch (Exception e) {
            e.printStackTrace();
        }
        LOGGER.info("Initialising doc resource ends");
    }

    @Override
    public DoctorRepresentation loginDoctor(LoginRepresentation loginRepresentation) throws NotFoundException, WrongCredentials {
        LOGGER.info("Login doctor");
        // Check authorization
        ResourceUtils.checkRole(this, loginRepresentation.getRole().getRoleName());
        // Initialize the persistence layer
        Doctors doctor;
        try {
            Optional<Doctors> opDoctor = doctorRepository.findByEmailAndPass(loginRepresentation.getEmail(), loginRepresentation.getPassword());
            setExisting(opDoctor.isPresent());
            if (!isExisting()) {
                LOGGER.config("email does not exist:" + loginRepresentation.getEmail());
                throw new NotFoundException("No doctor with that email : " + loginRepresentation.getPassword());
            } else {
                doctor = opDoctor.get();
                DoctorRepresentation result = new DoctorRepresentation(doctor);
                LOGGER.finer("Doctor successfully logged in");

                return result;
            }
        } catch (Exception ex) {
            throw new ResourceException(ex);
        }
    }
}