package br.com.cviana.integrationtests.dto.wrapper;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PersonWrapperJson implements Serializable {

	private static final long serialVersionUID = 1L;

    @JsonProperty("_embedded")
    private PersonEmbeddedDto embedded;

    public PersonWrapperJson() {}

    public PersonEmbeddedDto getEmbedded() {
        return embedded;
    }

    public void setEmbedded(PersonEmbeddedDto embedded) {
        this.embedded = embedded;
    }

}
