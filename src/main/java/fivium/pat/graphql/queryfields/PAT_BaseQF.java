package fivium.pat.graphql.queryfields;

import static graphql.Scalars.GraphQLString;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fivium.pat.utils.PAT_DAO;
import fivium.pat.utils.RnsUtils;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLObjectType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

public abstract class PAT_BaseQF implements DataFetcher {

	protected GraphQLObjectType field;

	protected List<GraphQLArgument> arguments;

	protected abstract GraphQLObjectType defineField();

	protected abstract List<GraphQLArgument> defineArguments();

	protected abstract Object fetchData(DataFetchingEnvironment environment);

	protected PAT_BaseQF() {
		field = defineField();

		arguments = new ArrayList<GraphQLArgument>();
		arguments.addAll(defineArguments());
		arguments.add(new GraphQLArgument("jwt_token", GraphQLString));
	}

	public GraphQLObjectType getField() {
		return field;
	}
	
	public boolean verifySuperUser(String sql, Object[] userVerifyArgs, Object[] passwordVerifyArgs, int passwordIndex) throws ClassNotFoundException, SQLException, IOException {
		Collection<Map<String, String>> resultVerifyUser;
		final String VERIFY_SUPER_USER = "SELECT Password, Salt from internaluser WHERE User = ?";
		resultVerifyUser = PAT_DAO.executeStatement(VERIFY_SUPER_USER, userVerifyArgs);
        Map<String, String> aMap = new HashMap<String, String>();
        aMap = resultVerifyUser.iterator().next();
        byte[] encoded = aMap.get("Salt").getBytes("ISO-8859-1");
        if(!resultVerifyUser.isEmpty() && RnsUtils.isExpectedPassword(passwordVerifyArgs[passwordIndex].toString().toCharArray(), encoded,aMap.get("Password").getBytes("ISO-8859-1"))){
        	return true;
        }
        else
        	return false;
	}

	public List<GraphQLArgument> getArguments() {
		return arguments;
	}

	public Object get(DataFetchingEnvironment environment) {
			return fetchData(environment);
	}
}
