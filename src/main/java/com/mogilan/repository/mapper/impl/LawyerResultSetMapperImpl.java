package com.mogilan.repository.mapper.impl;

import com.mogilan.model.Task;
import com.mogilan.repository.TaskDao;
import com.mogilan.repository.impl.TaskDaoImpl;
import com.mogilan.servlet.dto.JobTitle;
import com.mogilan.exception.DaoException;
import com.mogilan.model.ContactDetails;
import com.mogilan.model.LawFirm;
import com.mogilan.model.Lawyer;
import com.mogilan.repository.ContactDetailsDao;
import com.mogilan.repository.impl.ContactDetailsDaoImpl;
import com.mogilan.repository.mapper.LawyerResultSetMapper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LawyerResultSetMapperImpl implements LawyerResultSetMapper {
    private static final LawyerResultSetMapperImpl INSTANCE = new LawyerResultSetMapperImpl();
    private final ContactDetailsDao contactDetailsDao = ContactDetailsDaoImpl.getInstance();
    private final TaskDao taskDao = TaskDaoImpl.getInstance();

    private LawyerResultSetMapperImpl() {
    }

    @Override
    public Lawyer map(ResultSet resultSet) {
        try {
            var connection = resultSet.getStatement().getConnection();

            Optional<ContactDetails> contactDetails = getContactDetails(resultSet, connection);

            var lawFirm = resultSet.getObject("law_firm_id", Long.class) == null ?
                    null :
                    new LawFirm(
                    resultSet.getObject("law_firm_id", Long.class),
                    resultSet.getString("name"),
                    resultSet.getDate("company_start_day").toLocalDate()
            );

            var id = resultSet.getLong("id");
            var taskList = taskDao.findAllByLawyerId(id);

            return new Lawyer(
                    id,
                    resultSet.getString("first_name"),
                    resultSet.getString("last_name"),
                    JobTitle.valueOf(resultSet.getString("job_title")),
                    resultSet.getDouble("hourly_rate"),
                    lawFirm,
                    contactDetails.orElse(null),
                    taskList
            );
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public List<Lawyer> toLawyerList(ResultSet resultSet) throws SQLException {
        List<Lawyer> lawyers = new ArrayList<>();
        while (resultSet.next()) {
            var lawyer = map(resultSet);
            lawyers.add(lawyer);
        }
        return lawyers;
    }

    public static LawyerResultSetMapperImpl getInstance() {
        return INSTANCE;
    }

    private Optional<ContactDetails> getContactDetails(ResultSet resultSet, Connection connection) throws SQLException {
        return Optional.ofNullable(resultSet.getObject("id", Long.class))
                .map(contactsId -> contactDetailsDao.findById(contactsId, connection).orElse(null
                ));
    }
}
