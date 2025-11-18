package br.com.cviana.integrationtests.dto.wrapper;

import java.io.Serializable;
import java.util.List;

import br.com.cviana.integrationtests.dto.BookDTO;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "PagedModel")
public class BookWrapperXml implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(name = "content")
    public List<BookDTO> content;

    public BookWrapperXml() {}

    public List<BookDTO> getContent() {
        return content;
    }

    public void setContent(List<BookDTO> content) {
        this.content = content;
    }
}
