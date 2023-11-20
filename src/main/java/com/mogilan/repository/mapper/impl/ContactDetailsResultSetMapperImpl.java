package com.mogilan.repository.mapper.impl;

import com.mogilan.exception.DaoException;
import com.mogilan.model.ContactDetails;
import com.mogilan.repository.mapper.ContactDetailsResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ContactDetailsResultSetMapperImpl implements ContactDetailsResultSetMapper {

    private static final ContactDetailsResultSetMapperImpl INSTANCE = new ContactDetailsResultSetMapperImpl();

    private ContactDetailsResultSetMapperImpl() {
    }

    @Override
    public ContactDetails map(ResultSet resultSet) {
        try {
            return new ContactDetails(
                    resultSet.getLong("id"),
                    resultSet.getObject("address", String.class),
                    resultSet.getObject("tel_number", String.class),
                    resultSet.getObject("mob_number", String.class),
                    resultSet.getObject("fax_number", String.class),
                    resultSet.getObject("email", String.class));
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public static ContactDetailsResultSetMapperImpl getInstance() {
        return INSTANCE;
    }

}
