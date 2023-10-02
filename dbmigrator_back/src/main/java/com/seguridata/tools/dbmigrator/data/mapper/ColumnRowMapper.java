package com.seguridata.tools.dbmigrator.data.mapper;

import com.seguridata.tools.dbmigrator.data.constant.ColumnDataType;
import com.seguridata.tools.dbmigrator.data.entity.ColumnEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ColumnRowMapper implements RowMapper<ColumnEntity> {
    @Override
    public ColumnEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        ColumnEntity column = new ColumnEntity();
        column.setName(rs.getString("name"));
        column.setDataType(this.resolveDataType(rs.getString("dataType")));
        column.setDataLength(rs.getLong("dataLength"));

        return column;
    }

    private ColumnDataType resolveDataType(String dataType) {
        ColumnDataType columnDataType;
        switch (dataType) {
            case "CHAR":
            case "char":
                columnDataType = ColumnDataType.CHAR;
                break;
            case "NUMBER":
            case "int":
                columnDataType = ColumnDataType.NUMBER;
                break;
            case "DATE":
            case "datetime":
                columnDataType = ColumnDataType.DATE;
                break;
            case "VARCHAR2":
            case "varchar":
                columnDataType = ColumnDataType.STRING;
                break;
            case "BLOB":
            case "image":
                columnDataType = ColumnDataType.BINARY;
                break;
            default:
                columnDataType = null;
        }

        return columnDataType;
    }
}
