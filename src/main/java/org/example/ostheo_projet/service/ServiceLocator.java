package org.example.ostheo_projet.service;

import org.example.ostheo_projet.dao.*;
import org.example.ostheo_projet.utility.ApplicationContext;

public enum ServiceLocator {
    INSTANCE;

    private final PractitionerService practitionerService;
    private final PatientService patientService;
    private final CityService cityService;
    private final InvoiceService invoiceService;
    private final PaymentTypeService paymentTypeService;
    private final ConsultationService consultationService;
    private final JobService jobService;
    private final ConfigurationService configurationService;
    private final ApplicationContext applicationContext;

    ServiceLocator() {
        this.applicationContext = new ApplicationContext();

        this.practitionerService = new PractitionerService(new PractitionerDAO());
        this.patientService = new PatientService(new PatientDAO(), new JobDAO());
        this.configurationService = new ConfigurationService(new ConfigurationDAO());
        this.cityService = new CityService(new CityDAO());
        this.invoiceService = new InvoiceService(new InvoiceDAO(), this.configurationService, this.applicationContext);
        this.paymentTypeService = new PaymentTypeService(new PaymentTypeDAO());
        this.consultationService = new ConsultationService(new ConsultationDAO());
        this.jobService = new JobService(new JobDAO());
    }

    public PatientService getPatientService() {
        return patientService;
    }

    public CityService getCityService() {
        return cityService;
    }

    public InvoiceService getInvoiceService() {
        return invoiceService;
    }

    public PractitionerService getCredentialService() {
        return practitionerService;
    }

    public PaymentTypeService getPaymentTypeService() {
        return paymentTypeService;
    }

    public ConsultationService getConsultationService() {
        return consultationService;
    }

    public JobService getJobService() {
        return jobService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
