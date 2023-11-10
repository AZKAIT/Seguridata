package com.seguridata.tools.dbmigrator.data.entity;

import com.seguridata.segurilib.cipher.Cipher;
import com.seguridata.tools.dbmigrator.data.constant.DatabaseType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.io.Serializable;
import java.util.List;

@Document(collection = "connections")
@Getter @Setter
public class ConnectionEntity implements Serializable {
	private static final long serialVersionUID = 7867256518876301190L;

	@Id
    private String id;
    private String name;
    private String description;
    private String host;
    private Integer port;
    private String database;
    private String username;
    private String password;
    private DatabaseType type;
    private Boolean locked;
    private String objectService;

    @ReadOnlyProperty
    @DocumentReference(lookup="{'connection':?#{#self._id} }")
    private List<TableEntity> tables;

    public String getPassword() {
        try {
            return Cipher.decipher(this.password);
        } catch (Exception e) {
            return null;
        }
    }

    public void setPassword(String password) {
        try {
            this.password = Cipher.cipher(password);
        } catch (Exception e) {
            this.password = null;
        }
    }
}
