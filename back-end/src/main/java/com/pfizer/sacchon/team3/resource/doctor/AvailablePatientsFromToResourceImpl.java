package com.pfizer.sacchon.team3.resource.doctor;

import com.pfizer.sacchon.team3.exception.NotFoundException;
import com.pfizer.sacchon.team3.model.Patients;
import com.pfizer.sacchon.team3.repository.DoctorRepository;
import com.pfizer.sacchon.team3.repository.util.JpaUtil;
import com.pfizer.sacchon.team3.representation.PatientRepresentation;
import com.pfizer.sacchon.team3.security.ResourceUtils;
import com.pfizer.sacchon.team3.security.Shield;
import org.restlet.engine.Engine;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class AvailablePatientsFromToResourceImpl extends ServerResource implements AvailablePatientsFromTo {

    public static final Logger LOGGER = Engine.getLogger(AvailablePatientsFromToResourceImpl.class);
    private DoctorRepository doctorRepository;
    private Date startDate;
    private Date endDate;

    @Override
    protected void doInit() {
        LOGGER.info("Available patients resource starts");
        try {
            doctorRepository = new DoctorRepository (JpaUtil.getEntityManager()) ;
            try {
                String startDateString = getQueryValue("from");
                String   endDateString = getQueryValue("to");
                String[] words = startDateString.split("-");

                startDate = new Date(Integer.parseInt(words[0])-1900,
                        Integer.parseInt(words[1]) - 1, Integer.parseInt(words[2])  );

                words = endDateString.split("-");
                endDate = new Date(Integer.parseInt(words[0])-1900,
                        Integer.parseInt(words[1]) - 1, Integer.parseInt(words[2])  );
            }
            catch(Exception e)
            {
                startDate =null; endDate =null;
            }
        }
        catch(Exception ex)
        {
            throw new ResourceException(ex);
        }
        LOGGER.info("Available patients resource ends");
    }

    @Override
    public List<PatientRepresentation> availablePatients() throws NotFoundException {
        LOGGER.finer("Select all available patients.");

        // Check authorization
        ResourceUtils.checkRole(this, Shield.ROLE_DOCTOR);
        try{
            List<Patients> patients = doctorRepository.availablePatientsFromTo(startDate, endDate);
            List<PatientRepresentation> result = new ArrayList<>();
            patients.forEach(patient -> result.add(new PatientRepresentation(patient)));
            return result;
        }
        catch(Exception e)
        {
            throw new NotFoundException("available patients not found");
        }
    }
}