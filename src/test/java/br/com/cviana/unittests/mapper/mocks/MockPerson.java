package br.com.cviana.unittests.mapper.mocks;

import java.util.ArrayList;
import java.util.List;

import br.com.cviana.data.dto.PersonDTO;
import br.com.cviana.models.Person;

public class MockPerson {
    public Person mockEntity() {
    	return mockEntity(0);
    }
    
    public PersonDTO mockDTO() {
    	return mockDTO(0);
    }
    
    public List<Person> mockEntityList() {
        List<Person> persons = new ArrayList<Person>();
        for (int i = 1; i <= 14; i++) {
            persons.add(mockEntity(i));
        }
        return persons;
    }

    public List<PersonDTO> mockDTOList() {
        List<PersonDTO> persons = new ArrayList<>();
        for (int i = 1; i <= 14; i++) {
            persons.add(mockDTO(i));
        }
        return persons;
    }
    
    public Person mockEntity(Integer number) {
    	Person person = new Person();
        person.setId(number.longValue());
        person.setFirstName("First Name Test" + number);
        person.setLastName("Last Name Test" + number);
        person.setGender(((number % 2)==0) ? "Male" : "Female");
    	person.setAddress("Addres Test" + number);
        return person;
    }

    public PersonDTO mockDTO(Integer number) {
    	PersonDTO person = new PersonDTO();
        person.setId(number.longValue());
        person.setFirstName("First Name Test" + number);
        person.setLastName("Last Name Test" + number);
        person.setGender(((number % 2)==0) ? "Male" : "Female");
    	person.setAddress("Addres Test" + number);
        return person;
    }
}
