package br.com.cviana.file.exporter.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import br.com.cviana.exceptions.BadRequestException;
import br.com.cviana.file.exporter.MediaTypes;
import br.com.cviana.file.exporter.contract.PersonExporter;
import br.com.cviana.file.exporter.implementation.CsvExporter;
import br.com.cviana.file.exporter.implementation.PdfExporter;
import br.com.cviana.file.exporter.implementation.XlsxExporter;

@Component
public class FileExporterFactory implements MediaTypes {
    private Logger logger = LoggerFactory.getLogger(FileExporterFactory.class);

    @Autowired
    private ApplicationContext context;

    public PersonExporter getExporter(String acceptHeader) throws Exception {
        if( acceptHeader.equalsIgnoreCase(APPLICATION_XLSX_VALUE) ) {
            logger.info("Processando arquivo XLSX");
            return context.getBean(XlsxExporter.class);
        }
        else if( acceptHeader.equalsIgnoreCase(APPLICATION_CSV_VALUE) ) {
            logger.info("Processando arquivo CSV");
            return context.getBean(CsvExporter.class);
        }
        else if( acceptHeader.equalsIgnoreCase(APPLICATION_PDF_VALUE) ) {
            logger.info("Processando arquivo PDF");
            return context.getBean(PdfExporter.class);
        }
        else {
            logger.error("Formato ["+acceptHeader.split(".")[1]+"] não é suportado para essa operação");
            throw new BadRequestException();
        }
    }
}
