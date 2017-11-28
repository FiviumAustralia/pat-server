package fivium.pat.graphql.queryfields.clinician;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fivium.pat.graphql.PAT_BaseQF;
import fivium.pat.utils.PAT_DAO;
import graphql.GraphQLException;
import graphql.Scalars;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLObjectType;

public class GenerateCSV_QF extends PAT_BaseQF {

	public static final String BASE_STEPS_FETCH_QUERY = "SELECT x.date, x.study_id, p.mrn, p.first_name, p.last_name, p.dob, x.steps FROM stepdata x INNER JOIN patient p ON x.study_id = p.study_id ";
	public static final String BASE_WEIGHT_FETCH_QUERY = "SELECT x.date, x.study_id, p.mrn, p.first_name, p.last_name, p.dob, x.weight FROM weightdata x INNER JOIN patient p ON x.study_id = p.study_id ";
	public static final String BASE_SURVEY_FETCH_QUERY = "SELECT x.date, x.study_id, p.mrn, p.first_name, p.last_name, p.dob, x.survey_data FROM surveydata x INNER JOIN patient p ON x.study_id = p.study_id ";
	public static final String BASE_NOTIFICATION_FETCH_QUERY = "SELECT x.date, x.study_id, p.mrn, p.first_name, p.last_name, p.dob, x.notification_type FROM notifications x INNER JOIN patient p ON x.study_id = p.study_id ";
	public static final String BASE_SLEEP_FETCH_QUERY = "SELECT x.date, x.study_id, p.mrn, p.first_name, p.last_name, p.dob, x.duration, x.efficiency FROM sleepdata x INNER JOIN patient p ON x.study_id = p.study_id ";


	public static final String CSV_INSERT_QUERY = "INSERT INTO appdata (creation_ts, csv_filename, csv_content) VALUES (NOW(),?,?)";

	public static final String WHERE_CLAUSE_STARTER = " WHERE (1 = 1)";

	public static final String DATE_RANGE_QUERY_FILTER = " AND (x.date between ? and ?)";
	public static final String EXACT_DATE_QUERY_FILTER = " AND (x.date = ?)";
	public static final String STUDY_ID_QUERY_FILTER = " AND (x.study_id = ?)";
	public static final String MRN_QUERY_FILTER = " AND (p.mrn = ?)";

	public static final String ORDER_BY_CLAUSE = " ORDER BY date DESC";

	public static final String STEPS_CSV_HEADER = "DATE, STUDY_ID, MRN, FIRST_NAME, SURNAME, DOB, STEPS";
	public static final String SLEEP_CSV_HEADER = "DATE, STUDY_ID, MRN, FIRST_NAME, SURNAME, DOB, DURATION, EFFICIENCY";
	public static final String WEIGHT_CSV_HEADER = "DATE, STUDY_ID, MRN, FIRST_NAME, SURNAME, DOB, WEIGHT";
	public static final String SURVEY_CSV_HEADER = "DATE, STUDY_ID, MRN, FIRST_NAME, SURNAME, DOB, SURVEY_DATA";
	public static final String NOTIFICATION_CSV_HEADER = "DATE, STUDY_ID, MRN, FIRST_NAME, SURNAME, DOB, NOTIFICATION_TYPE";

	private static Log logger = LogFactory.getLog(GenerateCSV_QF.class);

	@Override
	protected GraphQLObjectType defineField() {
		return newObject()
			    .name("GenerateCSV")
			    .description("Fetches data then generates a CSV file and pushes it to the database")
			    .field(newFieldDefinition()
			    		.name("csv_filename")
			            .type(GraphQLString))
			    .field(newFieldDefinition()
			    		.name("csv_content")
			            .type(GraphQLString))
			   .build();
	}

	@Override
	protected List<GraphQLArgument> defineArguments() {
		return Arrays.asList(
				new GraphQLArgument("report_type", Scalars.GraphQLString),
				new GraphQLArgument("date_from", Scalars.GraphQLString),
				new GraphQLArgument("date_to", Scalars.GraphQLString),
				new GraphQLArgument("date_exact", Scalars.GraphQLString),
				new GraphQLArgument("study_id", Scalars.GraphQLString),
				new GraphQLArgument("mrn", Scalars.GraphQLString)
				);
	}

	@Override
	protected Object fetchData(DataFetchingEnvironment environment) {

		String reportType = environment.getArgument("report_type");
		Collection<Map<String, String>> sqlResult = null;

		if (reportType.equals("steps")) {
			sqlResult = fetchCSV_Data(BASE_STEPS_FETCH_QUERY, environment.getArguments());
		} else if (reportType.equals("weight")) {
			sqlResult = fetchCSV_Data(BASE_WEIGHT_FETCH_QUERY, environment.getArguments());
		} else if (reportType.equals("survey")) {
			sqlResult = fetchCSV_Data(BASE_SURVEY_FETCH_QUERY, environment.getArguments());
		} else if (reportType.equals("notifications")) {
			sqlResult = fetchCSV_Data(BASE_NOTIFICATION_FETCH_QUERY, environment.getArguments());
		}else if (reportType.equals("sleep")) {
			sqlResult = fetchCSV_Data(BASE_SLEEP_FETCH_QUERY, environment.getArguments());
		}else {
			throw new GraphQLException("Unexpected report_type parameter: " + reportType + ". report_type needs to be 'steps', 'weight', 'sleep', 'notifications' or 'survey'");
		}

		// build the CSV file
		String csvFileContent = buildCSV_file(reportType, sqlResult);
		// push the CSV file to the database
		String reportName = pushCSV_File(environment.getArguments(), csvFileContent);

		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put("csv_filename", reportName);
		resultMap.put("csv_content", csvFileContent);

		logger.debug("Generated CSV:\n\n" + csvFileContent);

		return resultMap;

	}

	private Collection<Map<String, String>> fetchCSV_Data (String baseQuery, Map<String, Object> params) {

		String finalQuery = baseQuery + WHERE_CLAUSE_STARTER;

		List<String> queryArgs = new ArrayList<String>();

		if (params.get("date_from") != null && params.get("date_to") != null ) {
			finalQuery += DATE_RANGE_QUERY_FILTER;
			queryArgs.add((String) params.get("date_from"));
			queryArgs.add((String) params.get("date_to"));
		} else if (params.get("date_exact") != null) {
			finalQuery += EXACT_DATE_QUERY_FILTER;
			queryArgs.add((String) params.get("date_exact"));
		}

		if (params.get("study_id") != null ) {
			finalQuery += STUDY_ID_QUERY_FILTER;
			queryArgs.add((String) params.get("study_id"));
		}

		if (params.get("mrn") != null ) {
			finalQuery += MRN_QUERY_FILTER;
			queryArgs.add((String) params.get("mrn"));
		}

		finalQuery += ORDER_BY_CLAUSE;

		logger.debug("executing CSV generation query: " + finalQuery);
		try {
			return PAT_DAO.executeStatement(finalQuery, queryArgs.toArray());
		} catch (Exception e) {
			logger.error("Unexpected error occured", e);
			throw new GraphQLException("Unexpected execution error", e);
		}

	}

	private String buildCSV_file (String reportType, Collection<Map<String, String>> data) {

		StringBuilder csvContent = new StringBuilder();

		if (reportType.equals("steps")) {
			csvContent.append(STEPS_CSV_HEADER);
		} else if (reportType.equals("weight")) {
			csvContent.append(WEIGHT_CSV_HEADER);
		} else if (reportType.equals("sleep")) {
			csvContent.append(SLEEP_CSV_HEADER);
		}else if (reportType.equals("survey")) {
			csvContent.append(SURVEY_CSV_HEADER);
		} else if (reportType.equals("notifications")) {
			csvContent.append(NOTIFICATION_CSV_HEADER);
		}

		csvContent.append(System.lineSeparator());

		for (Map<String, String> row : data) {
			// get a list of all the values in the current row
			Collection<String> values = row.values();
			for (String value : values) {
				csvContent.append(value);
				csvContent.append(',');
			}
			// delete last trailing comma on current line
			csvContent.deleteCharAt(csvContent.length() - 1);
			// add a new line for the next row
			csvContent.append(System.lineSeparator());
		}

		return csvContent.toString();
	}

	private String pushCSV_File(Map<String, Object> params, String csvContent) {

		String reportType = (String) params.get("report_type");

		StringBuilder reportName = new StringBuilder();

		if (reportType.equals("steps")) {
			reportName.append("steps");
		} else if (reportType.equals("weight")) {
			reportName.append("weight");
		} else if (reportType.equals("survey")) {
			reportName.append("survey");
		} else if (reportType.equals("notifications")) {
			reportName.append("notifications");
		} else if (reportType.equals("sleep")) {
			reportName.append("sleep");
		}


		if (params.get("date_from") != null && params.get("date_to") != null ) {
			reportName.append("__" + (String) params.get("date_from") + "_" + (String) params.get("date_to") );
		} else if (params.get("date_exact") != null) {
			reportName.append("__" + (String) params.get("date_exact"));
		}

		if (params.get("study_id") != null ) {
			reportName.append("__study_id=" + (String) params.get("study_id"));
		}

		if (params.get("mrn") != null ) {
			reportName.append("__mrn=" + (String) params.get("mrn"));
		}

		Object[] queryArgs = new Object[] {
				reportName.append(".csv").toString(),
				csvContent
		};

		try {
			PAT_DAO.executeStatement(CSV_INSERT_QUERY, queryArgs);
		} catch (Exception e) {
			logger.error("Unexpected error occured", e);
			throw new GraphQLException("Unexpected execution error", e);
		}

		return reportName.toString();

	}

}
