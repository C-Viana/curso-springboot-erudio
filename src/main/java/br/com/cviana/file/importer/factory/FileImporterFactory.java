package br.com.cviana.file.importer.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import br.com.cviana.exceptions.BadRequestException;
import br.com.cviana.file.importer.contract.FileImporter;
import br.com.cviana.file.importer.implementation.CsvImporter;
import br.com.cviana.file.importer.implementation.XlsxImporter;

@Component
public class FileImporterFactory {
    private Logger logger = LoggerFactory.getLogger(FileImporterFactory.class);

    @Autowired
    private ApplicationContext context;

    public FileImporter getImporter(String fileName) throws Exception {
        if(fileName.endsWith(".xlsx")) {
            logger.info("Processando arquivo XLSX");
            return context.getBean(XlsxImporter.class);
            //return new XlsxImporter();
        }
        else if( fileName.endsWith(".csv") ) {
            logger.info("Processando arquivo CSV");
            return context.getBean(CsvImporter.class);
            //return new CsvImporter();
        }
        else {
            logger.error("Formato ["+fileName.split(".")[1]+"] não é suportado para essa operação");
            throw new BadRequestException();
        }
    }
}
