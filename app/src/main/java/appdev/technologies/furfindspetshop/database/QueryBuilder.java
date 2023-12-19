package appdev.technologies.furfindspetshop.database;

import java.util.ArrayList;

public class QueryBuilder
{
    private StringBuilder query;
    private ArrayList<String> parameters;

    public QueryBuilder() {
        parameters = new ArrayList<>();
    }

    public QueryBuilder selectCount(String column) {
        query = new StringBuilder("SELECT COUNT(" + column + ") ");
        return this;
    }

    public QueryBuilder select(String... fields) {
        query = new StringBuilder("SELECT ");
        for (int i = 0; i < fields.length; i++) {
            query.append(fields[i]);
            if (i < fields.length - 1) {
                query.append(", ");
            }
        }
        query.append(" ");
        return this;
    }

    public QueryBuilder from(String table) {
        query.append("FROM " + table + " ");
        return this;
    }

    public QueryBuilder leftJoin(String table, String onField1, String onField2) {
        query.append("LEFT JOIN " + table + " ON " + onField1 + " = " + onField2 + " ");
        return this;
    }

    public QueryBuilder where(String[][] conditions) {
        query.append("WHERE ");
        for (int i = 0; i < conditions.length; i++) {
            query.append(conditions[i][0] + " " + conditions[i][1] + " ?");
            parameters.add(conditions[i][2]);
            if (i < conditions.length - 1) {
                query.append(" AND ");
            }
        }
        query.append(" ");
        return this;
    }

    public QueryBuilder orWhere(String[][] conditions) {
        query.append("OR ");
        for (int i = 0; i < conditions.length; i++) {
            query.append(conditions[i][0] + " " + conditions[i][1] + " ?");
            parameters.add(conditions[i][2]);
            if (i < conditions.length - 1) {
                query.append(" AND ");
            }
        }
        query.append(" ");
        return this;
    }

    public QueryBuilder orderby(String column, String order) {
        query.append("ORDER BY " + column + " " + order + " ");
        return this;
    }

    public QueryBuilder limit(int limit) {
        query.append("LIMIT " + limit);
        return this;
    }

    public QueryBuilder insertInto(String table, String... cols) {
        query = new StringBuilder("INSERT INTO " + table + " (");
        for (int i = 0; i < cols.length; i++) {
            query.append(cols[i]);
            if (i < cols.length - 1) {
                query.append(", ");
            }
        }
        query.append(") ");
        return this;
    }

    /**
     * Used to chain in insert()
     * @param values
     * @return
     */
    public QueryBuilder values(String... values) {
        query.append("VALUES (");
        for (int i = 0; i < values.length; i++) {
            query.append("?");
            parameters.add(values[i]);
            if (i < values.length - 1) {
                query.append(", ");
            }
        }
        query.append(") ");
        return this;
    }

    public QueryBuilder update(String table) {
        query = new StringBuilder("UPDATE " + table + " ");
        return this;
    }

    public QueryBuilder deleteFrom(String table) {
        query = new StringBuilder("DELETE FROM " + table + " ");
        return this;
    }

    /**
     * Used to chain in update()
     * @param fieldsAndValues
     * @return
     */
    public QueryBuilder set(String[][] fieldsAndValues) {
        query.append("SET ");
        for (int i = 0; i < fieldsAndValues.length; i++) {
            query.append(fieldsAndValues[i][0] + " " + fieldsAndValues[i][1] + " ?");
            parameters.add(fieldsAndValues[i][2]);
            if (i < fieldsAndValues.length - 1) {
                query.append(", ");
            }
        }
        query.append(" ");
        return this;
    }

    public String build() {
        return query.toString();
    }

    public ArrayList<String> getParameters() {
        return parameters;
    }

    public String[] getParameterBindings()
    {
        String[] parametersArray = new String[parameters.size()];
        return parameters.toArray(parametersArray);
    }

}

/**SAMPLE USAGE**/
/*
// Build the query
QueryBuilder db = new QueryBuilder();
String query = db.select("field1", "field2")
                 .from("table1 as t1")
                 .leftJoin("table2 as t2", "t2.id", "t1.id")
                 .where(new String[][] {{"field1", "=", "x"}, {"field3", ">", "5"}})
                 .orderby("desc")
                 .build();

// Get the parameters
ArrayList<String> parameters = db.getParameters();

// Convert ArrayList to array
String[] parametersArray = new String[parameters.size()];
parameters.toArray(parametersArray);

// Get a reference to the database
SQLiteDatabase db = getReadableDatabase();

// Execute the query
Cursor cursor = db.rawQuery(query, parametersArray);

// Use the Cursor to read the results
while (cursor.moveToNext()) {
    // Do something with the results
}
db.close();
// Don't forget to close the Cursor when you're done with it
cursor.close();

 */