package com.pfizer.sacchon.team3.resource.DoctorRes;

import com.pfizer.sacchon.team3.exception.NotFoundException;
import com.pfizer.sacchon.team3.representation.PatientRepresentation;
import org.restlet.resource.Get;

import java.util.List;

public interface AvailablePatients {
    @Get("json")
    public List<PatientRepresentation> availablePatients() throws NotFoundException;
}