package br.com.cviana.serialization.converters;

import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class DateAdapter extends XmlAdapter<String, Date> {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public String marshal(Date date) throws Exception {
        return date != null ? dateFormat.format(date) : null;
    }

    @Override
    public Date unmarshal(String string) throws Exception {
        return string != null ? dateFormat.parse(string) : null;
    }
}