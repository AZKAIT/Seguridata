package com.seguridata.tools.dbmigrator.data.mapper;

import com.seguridata.tools.dbmigrator.data.entity.TableEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TableRowMapper implements RowMapper<TableEntity> {
    @Override
    public TableEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        TableEntity table = new TableEntity();
        table.setName(rs.getNString("name"));
        table.setSchema(rs.getString("schema"));

        return table;
    }
}
