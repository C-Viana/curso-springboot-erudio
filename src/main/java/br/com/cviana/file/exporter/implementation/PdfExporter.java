package br.com.cviana.file.exporter.implementation;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import br.com.cviana.data.dto.PersonDTO;
import br.com.cviana.file.exporter.contract.PersonExporter;
import br.com.cviana.services.QrCodeService;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Component
@SuppressWarnings("null")
public class PdfExporter implements PersonExporter {

    @Autowired
    private QrCodeService qrcodeService;

    @Override
    public Resource exportPeople(List<PersonDTO> people) throws Exception {
        InputStream is = getClass().getResourceAsStream("/templates/People.jrxml");
        if(is == null) throw new RuntimeException("TEMPLATE FILE NOT FOUND! FILE EXPECTED AT [/templates/People.jrxml]");

        JasperReport jasperReport = JasperCompileManager.compileReport(is);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(people);
        Map<String, Object> parameters = new HashMap<>();
        JasperPrint printer = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            JasperExportManager.exportReportToPdfStream(printer, output);
            return new ByteArrayResource(output.toByteArray());
        }
    }

    public String getResource() {
        //String value = new File(System.getProperty("user.dir") + "/templates/Books.jasper").getPath();
        String value = getClass().getResource("/templates/Books.jasper").getPath();
        return value;
    }

    @Override
    public Resource exportPerson(PersonDTO person) throws Exception {
        String subreportFileName = "Books";
        InputStream mainTemplateStream = getClass().getResourceAsStream("/templates/person.jrxml");
        if (mainTemplateStream == null) {
            throw new RuntimeException("Template file not found: /templates/person.jrxml");
        }

        InputStream subReportStream = getClass().getResourceAsStream("/templates/"+subreportFileName+".jrxml");
        if (subReportStream == null) {
            throw new RuntimeException("Template file not found: /templates/"+subreportFileName+".jrxml");
        }

        JasperReport mainReport = JasperCompileManager.compileReport(mainTemplateStream);
        JasperReport subReport = JasperCompileManager.compileReport(subReportStream);

        InputStream qrCodeStream = qrcodeService.generateQrCode(person.getProfileUrl(), 200, 200);

        JRBeanCollectionDataSource subReportDataSource = new JRBeanCollectionDataSource(person.getBooks());
        
        String path = getClass().getResource("/templates/"+subreportFileName+".jasper").getPath();
        //String path = new File("target/classes/templates/"+subreportFileName+".jasper").getAbsolutePath().replace("\\", "/");  // Windows fix
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("SUB_REPORT_DATA_SOURCE", subReportDataSource);
        parameters.put("BOOK_SUB_REPORT", subReport);
        parameters.put("SUB_REPORT_DIR", "file:" +path);
        parameters.put("QR_CODEIMAGE", qrCodeStream);

        JRBeanCollectionDataSource mainDataSource = new JRBeanCollectionDataSource(Collections.singletonList(person));

        JasperPrint jasperPrint = JasperFillManager.fillReport(mainReport, parameters, mainDataSource);
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()){
            JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
            return new ByteArrayResource(outputStream.toByteArray());
        }
    }

}
