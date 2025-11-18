package br.com.cviana.integrationtests.dto.wrapper;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BookWrapperJson implements Serializable {

	private static final long serialVersionUID = 1L;

    @JsonProperty("_embedded")
    private BookEmbeddedDto embedded;

    public BookWrapperJson() {}

    public BookWrapperJson(BookEmbeddedDto embedded) {
        this.embedded = embedded;
    }

    public BookEmbeddedDto getEmbedded() {
        return embedded;
    }

    public void setEmbedded(BookEmbeddedDto embedded) {
        this.embedded = embedded;
    }
    
}
