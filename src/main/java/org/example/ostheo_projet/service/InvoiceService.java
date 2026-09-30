package org.example.ostheo_projet.service;


import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.ostheo_projet.Interface.Service;
import org.example.ostheo_projet.RootApplication;
import org.example.ostheo_projet.dao.InvoiceDAO;
import org.example.ostheo_projet.model.*;
import org.example.ostheo_projet.utility.ApplicationContext;
import org.example.ostheo_projet.utility.Utils;
import org.slf4j.Logger;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvoiceService implements Service<Invoice> {
    private final TemplateEngine templateEngine;
    private final InvoiceDAO invoiceDAO;
    private final ConfigurationService configurationService;
    private final ApplicationContext applicationContext;
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(InvoiceService.class);

    // ConfigurationService and ApplicationContext are passed to the constructor by params because ServiceLocator
    // Is instanciating when InvoiceService is called
    public InvoiceService(InvoiceDAO invoiceDAO, ConfigurationService configurationService, ApplicationContext applicationContext) {
        this.invoiceDAO = invoiceDAO;
        this.configurationService = configurationService;
        this.applicationContext = applicationContext;

        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding("UTF-8");
        resolver.setCacheable(false);

        this.templateEngine = new TemplateEngine();
        this.templateEngine.setTemplateResolver(resolver);
    }

    @Override
    public void save(Invoice invoice) throws Exception {
        try {
            if(invoice.getId() != 0){
                invoiceDAO.update(invoice);
                System.out.println("Invoice " + invoice.getId() + " has been saved");
            } else {
                invoiceDAO.insert(invoice);
                System.out.println("Invoice " + invoice.getId() + " has been created");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Invoice getById(int id) {
        try {
            return invoiceDAO.findById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Invoice> getAll() {
        try {
            return invoiceDAO.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Invoice> getAll(int limit) {
        try {
            return invoiceDAO.findAll(limit);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Invoice> getByPredicates(int month, int year, PaymentType paymentType, String chequeDepositId) {
        try {
            return invoiceDAO.findByPredicates(month, year, paymentType, chequeDepositId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Invoice> getByConsultation(Consultation consultation) {
        try {
            return invoiceDAO.findByConsultation(consultation);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void delete(Invoice invoice) {
        try {
            invoiceDAO.delete(invoice);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<ExportCompta> getAllExportData() {
        try {
            return invoiceDAO.findAllExportData();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<ExportCompta> convertInvoicesToExportCompta(List<Invoice> invoices) {
        return invoices.stream().map(invoice -> {
            Patient patient = invoice.getConsultation().getPatient();
            return new ExportCompta(
                    patient.getLastname(),
                    patient.getFirstname(),
                    invoice.getInvoiceNumber(),
                    invoice.getConsultation().getConsultationDate(),
                    invoice.getAmount(),
                    invoice.getPaymentType().getName()
            );
        }).toList();
    }

    public void exportDataToExcel(List<ExportCompta> comptaData) throws IOException {
        // Create export compta directory if not exists
        File defaultAppDir = new File(Utils.DEFAULT_EXPORT_COMPTA_DIRECTORY);
        if(!defaultAppDir.exists()){
            if(!defaultAppDir.mkdirs()){
                logger.error("Impossible de créer le répertoire par défaut : {}", Utils.DEFAULT_EXPORT_COMPTA_DIRECTORY);
                return;
            }
        }

        Sheet sheet1;
        try (Workbook workbook = new XSSFWorkbook()){
            sheet1 = workbook.createSheet("Export compta");

            if(comptaData.isEmpty()){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Export compta");
                alert.setHeaderText(null);
                alert.setContentText("Aucune donnée à exporter");
                alert.showAndWait();
                return;
            }

            // Insert columns titles
            sheet1.createRow(comptaData.size() + 2).createCell(0).setCellValue("Total");
            sheet1.createRow(0).createCell(0).setCellValue("N° facture");
            sheet1.getRow(0).createCell(1).setCellValue("Date de la consultation");
            sheet1.getRow(0).createCell(2).setCellValue("Nom du patient");
            sheet1.getRow(0).createCell(3).setCellValue("Prénom");
            sheet1.getRow(0).createCell(4).setCellValue("Montant de la facture");
            sheet1.getRow(0).createCell(5).setCellValue("Moyen de paiement");

            // TODO : apply style to the excel file
            // https://medium.com/@gavinklfong/java-programming-how-to-generate-data-report-in-excel-spreadsheet-173d24e4eb74

            // Insert for each tuple in compta a row in the sheet
            for(int i = 1; i < comptaData.size() + 1; i++){
                ExportCompta compta = comptaData.get(i-1);
                sheet1.createRow(i).createCell(0).setCellValue(compta.getInvoiceNumber());
                sheet1.getRow(i).createCell(1).setCellValue(compta.getConsultationDate());
                sheet1.getRow(i).createCell(2).setCellValue(compta.getPatientLastname());
                sheet1.getRow(i).createCell(3).setCellValue(compta.getPatientFirstname());
                sheet1.getRow(i).createCell(4).setCellValue(String.format("%.2f", compta.getAmount()) + "€");
                sheet1.getRow(i).createCell(5).setCellValue(compta.getPaymentType());
            }

            sheet1.getRow(comptaData.size() + 2).createCell(4).setCellValue(String.format("%.2f", comptaData.stream().mapToDouble(ExportCompta::getAmount).sum()) + "€");

            // Save to excel file
            String exportFileName = "export_compta_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd_MM_yyyy_HH_mm")) + ".xlsx";
            String outputPath = Utils.DEFAULT_EXPORT_COMPTA_DIRECTORY + exportFileName;
            try (OutputStream outputStream = new FileOutputStream(outputPath)) {
                workbook.write(outputStream);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Export compta");
                alert.setHeaderText(null);
                alert.setContentText("Export compta terminé, le fichier " + exportFileName + " a été créé avec succès.\n" +
                        "Souhaitez-vous ouvrir :");
                alert.getDialogPane().getStylesheets().add(RootApplication.class.getResource("/style.css").toExternalForm());
                //Set les boutons personnalisés juste avant le listener (évite bug de récupération de la réponse)
                ButtonType btnOpenDir = new ButtonType("Dossier d'export", ButtonBar.ButtonData.YES);
                ButtonType btnOpenFile = new ButtonType("Fichier", ButtonBar.ButtonData.YES);
                ButtonType btnCancel = new ButtonType("Annuler", ButtonBar.ButtonData.NO);
                alert.getButtonTypes().setAll(btnOpenDir, btnOpenFile, btnCancel);

                // Apply style class to buttons : Open file
                Button btn = (Button) alert.getDialogPane().lookupButton(btnOpenFile);
                Utils.insertIconInButton(btn, "/data/generate.png", Color.WHITE, ContentDisplay.RIGHT);
                btn.getStyleClass().add("green-button");
                // Open dir
                btn = (Button) alert.getDialogPane().lookupButton(btnOpenDir);
                Utils.insertIconInButton(btn, "/data/folder.png", Color.WHITE, ContentDisplay.RIGHT);
                btn.getStyleClass().add("blue-button");
                // Fermer
                btn = (Button) alert.getDialogPane().lookupButton(btnCancel);
                btn.getStyleClass().add("cancel-button");

                alert.showAndWait().ifPresent(response -> {
                    if (response == btnOpenDir) {
                        Utils.openFile(new File(Utils.DEFAULT_EXPORT_COMPTA_DIRECTORY));
                    } else if (response == btnOpenFile) {
                        Utils.openFile(new File(outputPath));
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Export compta");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors de la création du fichier Excel");
            alert.showAndWait();
        }

    }


    public File generateInvoicePDF(Invoice invoice, String outputPath) throws IOException {
        if(outputPath == null || outputPath.isEmpty()){
            logger.error("Le chemin de génération de la facture en PDF est vide ou null.");
            return null;
        }

        Map<String, Object> data = new HashMap<>();
        Patient patientInvoice = invoice.getConsultation().getPatient();
        PaymentType paymentType = invoice.getPaymentType();
        City userCity = patientInvoice.getCity();

        // Cabinet infos
        Configuration practitionerConfig = this.configurationService.getByPractitioner(this.applicationContext.getCurrentPractitioner());
        data.put("cabinetName", practitionerConfig.getCabinetName());
        data.put("cabinetAddress", practitionerConfig.getCabinetAddress());
        data.put("cabinetPostalCode", practitionerConfig.getCabinetPostalCode());
        data.put("cabinetCity", practitionerConfig.getCabinetCity());
        data.put("cabinetCountry", practitionerConfig.getCabinetCountry());

        // Practicien infos
        Practitioner practitioner = this.applicationContext.getCurrentPractitioner();
        data.put("practitionerLabel", practitioner.getInvoicePractitionerLabel());
        data.put("practitionerFirstname", practitioner.getFirstname());
        data.put("practitionerLastname", practitioner.getLastname());
        data.put("practitionerPhone", practitioner.getPhone());
        data.put("practitionerEmail", practitioner.getEmail());
        data.put("practitionerRPPS", practitioner.getRppsId());

        // Invoice infos
        data.put("invoiceNumber", invoice.getInvoiceNumber());
        data.put("amount", String.format("%.2f", invoice.getAmount()));
        data.put("paymentMethod", paymentType.getName());
        data.put("currency", "€");

        // Patient infos
        data.put("patientFirstname", patientInvoice.getFirstname());
        data.put("patientLastname", patientInvoice.getLastname());
        data.put("patientAddress", patientInvoice.getAddress());
        data.put("patientCity", userCity.getName());
        data.put("patientPostalCode", userCity.getPostalCode());

        data.put("invoiceConsultLabel", practitionerConfig.getInvoiceConsultationLabel());
        data.put("endText", practitionerConfig.getInvoiceEndText());

        Context context = new Context();
        context.setVariables(data);
        String htmlContent = templateEngine.process("invoice.html", context);

        generatePDF(htmlContent, outputPath);
        return new File(outputPath);
    }

    // Create the pdf document
    private static void generatePDF(String content, String outputPath) throws IOException {
        try (FileOutputStream file = new FileOutputStream(outputPath)) {
            ConverterProperties converterProperties = new ConverterProperties();
//            BasicFontProvider basicFontProvider = new BasicFontProvider(true,true,true);
//            basicFontProvider.addSystemFonts();
//            converterProperties.setFontProvider(basicFontProvider);
            HtmlConverter.convertToPdf(content, file, converterProperties);
        }
    }
}