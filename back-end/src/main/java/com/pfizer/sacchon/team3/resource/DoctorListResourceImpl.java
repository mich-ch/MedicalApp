package com.pfizer.sacchon.team3.resource;

import com.pfizer.sacchon.team3.exception.NotFoundException;
import com.pfizer.sacchon.team3.model.Doctors;
import com.pfizer.sacchon.team3.repository.DoctorRepository;
import com.pfizer.sacchon.team3.repository.util.JpaUtil;
import com.pfizer.sacchon.team3.representation.DoctorRepresentation;
import com.pfizer.sacchon.team3.security.ResourceUtils;
import com.pfizer.sacchon.team3.security.Shield;
import org.hibernate.Hibernate;
import org.restlet.engine.Engine;
import org.restlet.resource.ServerResource;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class DoctorListResourceImpl
        extends ServerResource implements DoctorListResource {

    public static final Logger LOGGER = Engine.getLogger(DoctorResourceImpl.class);

    private DoctorRepository doctorRepository;

    @Override
    protected void doInit() {
        LOGGER.info("Initialising doctor resource starts");
        try {
            System.out.println("Doc Init");
            doctorRepository = new DoctorRepository(JpaUtil.getEntityManager()) ;

        }
        catch(Exception e)
        {

        }

        LOGGER.info("Initialising doctor resource ends");
    }

    @Override
    public List<DoctorRepresentation> getDoctors() throws NotFoundException{
        LOGGER.finer("Select all doctors.");
        System.out.println("Before role check");
        // Check authorization
        ResourceUtils.checkRole(this, Shield.ROLE_ADMIN);
        System.out.println("After role check");
        try{

            List<Doctors> doctors = doctorRepository.findAll();
            for (Doctors d: doctors) {
                Hibernate.initialize(d.getConsultations());
            }
            List<DoctorRepresentation> result = new ArrayList<>();

            doctors.forEach(doc -> result.add (new DoctorRepresentation(doc)));
            return result;
        }
        catch(Exception e)
        {
            throw new NotFoundException("doctors not found");
        }
    }




}
