package com.resilience.auditapi.persistence.common;

import org.springframework.data.mongodb.core.mapping.Field;

public class OriginDocument {

    @Field(value = "database")
    private String db;

    @Field(value = "table")
    private String table;

    @Field(value = "segment")
    private String file;

    @Field(value = "operation")
    private String op;

    public OriginDocument() {}

    public OriginDocument(final String db, final String table, final String file, final String op) {
        this.db = db;
        this.table = table;
        this.file = file;
        this.op = op;
    }

    public String getDb() {
        return db;
    }

    public void setDb(final String db) {
        this.db = db;
    }

    public String getTable() {
        return table;
    }

    public void setTable(final String table) {
        this.table = table;
    }

    public String getFile() {
        return file;
    }

    public void setFile(final String file) {
        this.file = file;
    }

    public String getOp() {
        return op;
    }

    public void setOp(final String op) {
        this.op = op;
    }

}
