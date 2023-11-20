package com.mogilan.repository.mapper.impl;

import com.mogilan.exception.DaoException;
import com.mogilan.model.LawFirm;
import com.mogilan.repository.LawyerDao;
import com.mogilan.repository.impl.LawyerDaoImpl;
import com.mogilan.repository.mapper.LawFirmResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LawFirmResultSetMapperImpl implements LawFirmResultSetMapper {
    private static final LawFirmResultSetMapperImpl INSTANCE = new LawFirmResultSetMapperImpl();
    private final LawyerDao lawyerDao = LawyerDaoImpl.getInstance();

    private LawFirmResultSetMapperImpl() {
    }

    @Override
    public LawFirm map(ResultSet resultSet) {
        try {
            var id = resultSet.getLong("id");
            var lawyers = lawyerDao.findAllByLawFirmId(id, resultSet.getStatement().getConnection());
            return new LawFirm(
                    id,
                    resultSet.getString("name"),
                    resultSet.getDate("company_start_day").toLocalDate(),
                    lawyers
            );
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public static LawFirmResultSetMapperImpl getInstance() {
        return INSTANCE;
    }
}
